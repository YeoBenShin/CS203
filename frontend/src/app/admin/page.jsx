"use client";
import React, { useState, useEffect } from "react";
import ErrorDisplay from "../../components/messages/ErrorMessageDisplay";
import LoadingSpinner from "../../components/messages/LoadingSpinner";
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../../components/messages/SuccessMessageDisplay";
import FieldSelector from "../../components/FieldSelector";
import Button from "../../components/Button";
import fetchApi from "@/utils/fetchApi";
import { useAuth } from "@clerk/nextjs";

export default function AdminPage() {
  const { getToken } = useAuth();
  const [form, setForm] = useState({
    exporter: null,
    product: null,
    rate: "",
    effectiveDate: "",
    expiryDate: "",
    reference: ""
  });

  const [errors, setErrors] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);

  const [countryOptions, setCountryOptions] = useState([]);
  const [productOptions, setProductOptions] = useState([]);
  
    useEffect(() => {
      const fetchCountries = async () => {
        try {
          const token = await getToken();
          const response = await fetchApi(token, "/api/countries");
          const countries = await response.json();
          const options = countries.filter(country => country.isoCode !== 'USA').map(country => ({
            label: country.name,
            value: country.isoCode
          }));
          setCountryOptions(options);
        } catch (error) {
          console.error("Failed to fetch countries:", error);
        }
      };
      
      fetchCountries();
    }, []);
  
    useEffect(() => {
      const fetchProducts = async () => {
        try {
          const token = await getToken();
          const response = await fetchApi(token, "/api/products");
          const products = await response.json();
          const options = products.map(product => ({
            label: `${product.hsCode}${product.description ? ` - ${product.description}` : ''}`,
            value: product.hsCode
          }));
          setProductOptions(options);
          console.log("Fetched products:", products);
        } catch (error) {
          console.error("Failed to fetch products:", error);
        }
      };
  
      fetchProducts();
    }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    // Clear errors when user starts typing
    if (errors.length > 0) {
      setErrors([]);
    }
  };

   const handleExporterChange = (option) => {
    setForm({ ...form, exporter: option });
    if (errors.length > 0) {
      setErrors([]);
    }
  };
  const handleProductChange = (option) => {
    setForm({ ...form, product: option });
    if (errors.length > 0) {
      setErrors([]);
    }
  };

  // Client-side validation
  const validateForm = () => {
    const validationErrors = [];

    if (!form.exporter) {
      validationErrors.push("Please select an exporter country");
    }
    if (!form.product) {
      validationErrors.push("Please select a product");
    }
    if (!form.rate) {
      validationErrors.push("Please enter a tariff rate");
    } else {
      const rate = parseFloat(form.rate);
      if (rate < 0) {
        validationErrors.push("Tariff rate cannot be negative");
      }
    }
    if (!form.effectiveDate) {
      validationErrors.push("Please enter an effective date");
    }

    // Check if exporter and importer are the same
    if (form.exporter && form.exporter.value === "USA") {
      validationErrors.push("Exporter and importer cannot be the same country");
    }

    // Check if expiry date is before effective date
    if (form.effectiveDate && form.expiryDate) {
      const effectiveDate = new Date(form.effectiveDate);
      const expiryDate = new Date(form.expiryDate);
      if (expiryDate <= effectiveDate) {
        validationErrors.push("Expiry date must be after the effective date");
      }
    }

    return validationErrors;
  };

  // Parse backend error response
  const parseErrorResponse = (errorData) => {
    if (typeof errorData === 'string') {
      try {
        const parsed = JSON.parse(errorData);
        return parseErrorResponse(parsed);
      } catch {
        return [errorData];
      }
    }

    // Handle structured error response from backend
    if (errorData && errorData.errorCode) {
      return [getHelpfulErrorMessage(errorData.errorCode, errorData.message)];
    }

    // Handle validation errors array
    if (Array.isArray(errorData)) {
      return errorData;
    }

    // Handle generic error object
    if (errorData && errorData.message) {
      return [errorData.message];
    }

    return ["An unexpected error occurred. Please try again."];
  };

  // Convert error codes to user-friendly messages
  const getHelpfulErrorMessage = (errorCode, originalMessage) => {
    const errorMessages = {
      'SAME_COUNTRY': 'Please select different countries for exporter and importer.',
      'NEGATIVE_TARIFF_RATE': 'Tariff rate must be a positive number.',
      'INVALID_TARIFF_RATE': 'Please enter a valid positive tariff rate.',
      'PAST_EFFECTIVE_DATE': 'Please select an effective date that is today or in the future.',
      'EXPIRY_BEFORE_EFFECTIVE': 'Expiry date must be after the effective date.',
      'OVERLAPPING_TARIFF_PERIOD': 'A tariff already exists for this period. Please choose different dates.',
      'DUPLICATE_TARIFF_MAPPING': 'This tariff mapping already exists. Please check your selections.',
      'INVALID_DATE_RANGE': 'Please check your date inputs for any conflicts.',
      'RESOURCE_NOT_FOUND': 'One of the selected options is no longer available. Please refresh and try again.'
    };

    return errorMessages[errorCode] || originalMessage || 'An error occurred with your request.';
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);
    setSuccessMessage("");
    setIsLoading(true);
    
    // Client-side validation
    const validationErrors = validateForm();
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      setIsLoading(false);
      return;
    }
    
    try {
        const requestData = {
          exporter: form.exporter.value,
          HSCode: Number(form.product.value),
          rate: parseFloat(form.rate) / 100, // Convert percentage to decimal
          effectiveDate: new Date(form.effectiveDate).toISOString(),
          expiryDate: form.expiryDate ? new Date(form.expiryDate).toISOString() : null,
          reference: form.reference || null
        };
        
        console.log("Sending request data:", requestData);
  
        const token = await getToken();
        const response = await fetchApi(token, "/api/tariffs", "POST", requestData);
      
      if (response.ok) {
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup,"Tariff added successfully!");
        setForm({ 
          exporter: null, 
          product: null, 
          rate: "", 
          effectiveDate: "", 
          expiryDate: "", 
          reference: "" 
        });
      } else {
        const errorText = await response.text();
        let errorData;
        try {
          errorData = JSON.parse(errorText);
        } catch {
          errorData = errorText;
        }
        
        const errorMessages = parseErrorResponse(errorData);
        setErrors(errorMessages);
      }
    } catch (err) {
      console.error("Network error:", err);
      setErrors(["❌ Network error: Please check your connection and try again."]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Add Tariff</h1>
      <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md">
        <p className="text-sm text-gray-600 mb-4">
          Fields marked with <span className="text-red-500">*</span> are required
        </p>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Exporter <span className="text-red-500">*</span>
          </label>
          <FieldSelector
            options={countryOptions}
            value={form.exporter}
            onChange={handleExporterChange}
            placeholder="Select exporter country"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Product <span className="text-red-500">*</span>
          </label>
          <FieldSelector
            options={productOptions}
            value={form.product}
            onChange={handleProductChange}
            placeholder="Select Product HSCode"
          />
        </div>
        
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="rate">
            Rate (%) <span className="text-red-500">*</span>
          </label>
          <input 
            name="rate" 
            type="number" 
            min="0" 
            step="0.01"
            value={form.rate} 
            onChange={handleChange} 
            placeholder="Enter percentage (e.g., 1 for 1%, 25 for 25%)" 
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" 
          />
          <p className="text-xs text-gray-600 mt-1">Enter as percentage: 1 for 1%, 5 for 5%, 25 for 25%</p>
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="effectiveDate">
            Effective Date <span className="text-red-500">*</span>
          </label>
          <input 
            name="effectiveDate" 
            type="date" 
            value={form.effectiveDate} 
            onChange={handleChange} 
            className={`shadow appearance-none border rounded w-full py-2 px-3 leading-tight focus:outline-none focus:shadow-outline ${form.effectiveDate ? " text-gray-700" : "text-white"}`} 
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="expiryDate">Expiry Date</label>
          <input name="expiryDate" type="date" value={form.expiryDate} onChange={handleChange} className={`shadow appearance-none border rounded w-full py-2 px-3 leading-tight focus:outline-none focus:shadow-outline ${form.expiryDate ? " text-gray-700" : "text-white"}`} />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="reference">Reference</label>
          <input name="reference" type="text" value={form.reference} onChange={handleChange} placeholder="Source URL" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <Button 
          type='submit'
        >
          {isLoading && <LoadingSpinner />} 
          {isLoading ? "Adding Tariff..." : "Add Tariff"}
        </Button>
        {showSuccessPopup && <SuccessMessageDisplay successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}
        
        {/* Error Messages */}
        <ErrorDisplay errors={errors} />
       </form>
    </main>
  );
}
