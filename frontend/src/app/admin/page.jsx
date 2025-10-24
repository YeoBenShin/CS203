"use client";
import React, { useState, useEffect } from "react";
import ErrorDisplay from "../../components/messages/ErrorMessageDisplay";
import LoadingSpinner from "../../components/messages/LoadingSpinner";
import {
  SuccessMessageDisplay,
  showSuccessPopupMessage,
} from "../../components/messages/SuccessMessageDisplay";
import FieldSelector from "../../components/FieldSelector";
import Button from "../../components/Button";
import fetchApi from "@/utils/fetchApi";
import { useAuth } from "@clerk/nextjs";

export default function AdminPage() {
  const { getToken } = useAuth();

  const [form, setForm] = useState({
    exporter: null,
    importer: null,
    product: null,
    effectiveDate: "",
    expiryDate: "",
    reference: "",
  });

  const [rates, setRates] = useState([{ unit: "AV", rate: "" }]); // at least one (Ad Valorem)
  const [errors, setErrors] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [countryOptions, setCountryOptions] = useState([]);
  const [productOptions, setProductOptions] = useState([]);

  // Fetch countries
  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const token = await getToken();
        const response = await fetchApi(token, "api/countries");
        const countries = await response.json();
        const options = countries.map((country) => ({
          label: country.name,
          value: country.isoCode,
        }));
        setCountryOptions(options);
      } catch (error) {
        console.error("Failed to fetch countries:", error);
      }
    };
    fetchCountries();
  }, []);

  // Fetch products
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = await getToken();
        const response = await fetchApi(token, "api/products");
        const products = await response.json();
        const options = products.map((product) => ({
          label: `${product.hsCode}${product.description ? ` - ${product.description}` : ""}`,
          value: product.hsCode,
        }));
        setProductOptions(options);
      } catch (error) {
        console.error("Failed to fetch products:", error);
      }
    };
    fetchProducts();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRateChange = (index, field, value) => {
    const updatedRates = [...rates];
    updatedRates[index][field] = value;
    setRates(updatedRates);
  };

  const addRate = () => {
    setRates([...rates, { unit: "", rate: "" }]);
  };

  const removeRate = (index) => {
    if (rates.length > 1) {
      setRates(rates.filter((_, i) => i !== index));
    }
  };

  const validateForm = () => {
    const validationErrors = [];
    if (!form.exporter) validationErrors.push("Please select an exporter country.");
    if (!form.importer) validationErrors.push("Please select an importer country.");
    if (form.exporter && form.importer && form.exporter.value === form.importer.value)
      validationErrors.push("Exporter and importer cannot be the same country.");
    if (!form.product) validationErrors.push("Please select a product.");
    if (rates.length === 0 || !rates.some((r) => r.rate))
      validationErrors.push("Please enter at least one tariff rate.");
    if (!form.effectiveDate) validationErrors.push("Please enter an effective date.");
    if (form.effectiveDate && form.expiryDate) {
      const eff = new Date(form.effectiveDate);
      const exp = new Date(form.expiryDate);
      if (exp <= eff) validationErrors.push("Expiry date must be after effective date.");
    }
    return validationErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);
    setIsLoading(true);

    const validationErrors = validateForm();
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      setIsLoading(false);
      return;
    }

    // Convert rates array into map structure { "AV": 0.05, "KG": 0.02 }
    const tariffRates = {};
    rates.forEach((r) => {
      if (r.unit && r.rate) {
        tariffRates[r.unit.toUpperCase()] = parseFloat(r.rate) / 100;
      }
    });

    const requestData = {
      exporter: form.exporter.value,
      importer: form.importer.value,
      hSCode: form.product.value,
      effectiveDate: form.effectiveDate,
      expiryDate: form.expiryDate || null,
      reference: form.reference || null,
      tariffRates,
    };

    console.log("Sending request data:", requestData);

    try {
      const token = await getToken();
      const response = await fetchApi(token, "api/tariffs", "POST", requestData);
      if (response.ok) {
        showSuccessPopupMessage(
          setSuccessMessage,
          setShowSuccessPopup,
          "✅ Tariff added successfully!"
        );
        setForm({
          exporter: null,
          importer: null,
          product: null,
          effectiveDate: "",
          expiryDate: "",
          reference: "",
        });
        setRates([{ unit: "AV", rate: "" }]);
      } else {
        const errText = await response.text();
        setErrors([errText]);
      }
    } catch (err) {
      console.error(err);
      setErrors(["Network error: please try again."]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Add Tariff</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"
      >
        {/* Exporter */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Exporter Country *
          </label>
          <FieldSelector
            options={countryOptions}
            value={form.exporter}
            onChange={(opt) => setForm({ ...form, exporter: opt })}
            placeholder="Select exporter country"
          />
        </div>

        {/* Importer */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Importer Country *
          </label>
          <FieldSelector
            options={countryOptions}
            value={form.importer}
            onChange={(opt) => setForm({ ...form, importer: opt })}
            placeholder="Select importer country"
          />
        </div>

        {/* Product */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Product (HS Code) *
          </label>
          <FieldSelector
            options={productOptions}
            value={form.product}
            onChange={(opt) => setForm({ ...form, product: opt })}
            placeholder="Select product"
          />
        </div>

        {/* Tariff Rates */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Tariff Rates (%)
          </label>
          {rates.map((rate, index) => (
            <div key={index} className="flex space-x-2 mb-2">
              <input
                type="text"
                value={rate.unit}
                onChange={(e) => handleRateChange(index, "unit", e.target.value)}
                placeholder="Unit (e.g., AV, KG, M2)"
                className="border rounded w-1/2 py-2 px-3"
              />
              <input
                type="number"
                step="0.01"
                value={rate.rate}
                onChange={(e) => handleRateChange(index, "rate", e.target.value)}
                placeholder="Rate (%)"
                className="border rounded w-1/2 py-2 px-3"
              />
              {index > 0 && (
                <button
                  type="button"
                  onClick={() => removeRate(index)}
                  className="text-red-500 font-bold"
                >
                  ✕
                </button>
              )}
            </div>
          ))}
          <button
            type="button"
            onClick={addRate}
            className="text-blue-500 text-sm mt-2"
          >
            + Add another rate
          </button>
        </div>

        {/* Dates */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Effective Date *
          </label>
          <input
            type="date"
            name="effectiveDate"
            value={form.effectiveDate}
            onChange={handleChange}
            className="border rounded w-full py-2 px-3"
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Expiry Date
          </label>
          <input
            type="date"
            name="expiryDate"
            value={form.expiryDate}
            onChange={handleChange}
            className="border rounded w-full py-2 px-3"
          />
        </div>

        {/* Reference */}
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Reference
          </label>
          <input
            type="text"
            name="reference"
            value={form.reference}
            onChange={handleChange}
            placeholder="Optional reference"
            className="border rounded w-full py-2 px-3"
          />
        </div>

        {/* Submit */}
        <Button type="submit">
          {isLoading && <LoadingSpinner />}{" "}
          {isLoading ? "Adding Tariff..." : "Add Tariff"}
        </Button>

        {showSuccessPopup && (
          <SuccessMessageDisplay
            successMessage={successMessage}
            setShowSuccessPopup={setShowSuccessPopup}
          />
        )}
        <ErrorDisplay errors={errors} />
      </form>
    </main>
  );
}
