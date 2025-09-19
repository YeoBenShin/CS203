"use client";
import React, { useState, useEffect } from "react";
import Select from "react-select";

export default function CreateTariffMappingPage() {
  const [form, setForm] = useState({
    exporter: null,
    importer: null,
    productId: null,
    description: ""
  });
  const [message, setMessage] = useState("");
  const [countries, setCountries] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch countries and products from the backend
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Fetch countries
        const countriesResponse = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/countries`);
        if (!countriesResponse.ok) {
          throw new Error(`Failed to fetch countries: ${countriesResponse.status} ${countriesResponse.statusText}`);
        }
        const countriesData = await countriesResponse.json();
        
        // Transform countries data to match react-select format
        const countryOptions = countriesData.map(country => ({
          value: country.isoCode,
          label: `${country.name} (${country.isoCode})`
        }));
        
        setCountries(countryOptions);
        
        // Fetch products
        const productsResponse = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/products`);
        if (!productsResponse.ok) {
          throw new Error(`Failed to fetch products: ${productsResponse.status} ${productsResponse.statusText}`);
        }
        const productsData = await productsResponse.json();
        
        // Transform products data to match react-select format
        const productOptions = productsData.map(product => ({
          value: product.hsCode,
          label: `${product.hsCode} - ${product.description}`
        }));
        
        setProducts(productOptions);
      } catch (err) {
        console.error("Error fetching data:", err);
        setError("Failed to load necessary data. Please refresh the page or contact support.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleExporterChange = (option) => {
    setForm({ ...form, exporter: option });
  };
  
  const handleImporterChange = (option) => {
    setForm({ ...form, importer: option });
  };
  
  const handleProductChange = (option) => {
    setForm({ ...form, productId: option });
  };

  const validateForm = () => {
    // Validate required fields
    if (!form.exporter) {
      setMessage("Error: Exporter country is required");
      return false;
    }
    
    if (!form.importer) {
      setMessage("Error: Importer country is required");
      return false;
    }
    
    if (!form.productId) {
      setMessage("Error: Product is required");
      return false;
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    
    // Use the validation function
    if (!validateForm()) {
      return;
    }
    
    console.log("Form data:", form);
    
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/tariffmappings`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          exporter: form.exporter ? form.exporter.value : "",
          importer: form.importer ? form.importer.value : "",
          productId: form.productId ? form.productId.value : null
        })
      });
      if (response.ok) {
        // Try to get the created mapping ID from the response if available
        let mappingId = null;
        try {
          const responseData = await response.json();
          console.log("Response data:", responseData);
          if (responseData && responseData.tariffMappingID) {
            mappingId = responseData.tariffMappingID;
          }
        } catch (err) {
          // If response is not JSON or doesn't have ID, just continue
          console.log("Could not extract mapping ID from response:", err);
        }
        
        if (mappingId) {
          setMessage(`Tariff mapping added successfully! The mapping ID is: ${mappingId}. Use this ID when creating tariffs.`);
        } else {
          setMessage("Tariff mapping added successfully! You can now use this mapping to create tariffs.");
        }
        
        setForm({ exporter: null, importer: null, productId: null, description: "" });
      } else {
        const errorText = await response.text();
        console.error("Server responded with error:", response.status, errorText);
        
        let errorMessage = `Failed to add tariff mapping: ${response.status} ${response.statusText}`;
        
        // Try to extract more specific error from the response
        try {
          const errorJson = JSON.parse(errorText);
          if (errorJson.message) {
            errorMessage = `Error: ${errorJson.message}`;
            
            // Special handling for foreign key errors
            if (errorMessage.includes("fk_exporter") || errorMessage.includes("foreign key constraint fails") && errorMessage.includes("exporter_iso_code")) {
              errorMessage = "Error: The selected exporter country does not exist in our database. Please select a different country.";
            } else if (errorMessage.includes("fk_importer") || errorMessage.includes("foreign key constraint fails") && errorMessage.includes("importer_iso_code")) {
              errorMessage = "Error: The selected importer country does not exist in our database. Please select a different country.";
            }
          }
        } catch (e) {
          // Not JSON or can't parse the message, try to detect foreign key issues in raw text
          if (errorText.includes("fk_exporter") || (errorText.includes("foreign key constraint fails") && errorText.includes("exporter_iso_code"))) {
            errorMessage = "Error: The selected exporter country does not exist in our database. Please select a different country.";
          } else if (errorText.includes("fk_importer") || (errorText.includes("foreign key constraint fails") && errorText.includes("importer_iso_code"))) {
            errorMessage = "Error: The selected importer country does not exist in our database. Please select a different country.";
          }
        }
        
        setMessage(errorMessage);
      }
    } catch (err) {
      console.error("Request failed:", err);
      setMessage("Error: " + err.message);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Create Tariff Mapping</h1>
      
      <div className="mb-4 w-full max-w-md flex justify-between">
        <a href="/admin" className="text-blue-600 hover:text-blue-800 font-medium">Admin Home</a>
        <a href="/admin/mapping" className="text-blue-600 hover:text-blue-800 font-medium">Create Mapping</a>
        <a href="/admin/tariff" className="text-blue-600 hover:text-blue-800 font-medium">Add Tariff</a>
        <a href="/admin/countries" className="text-blue-600 hover:text-blue-800 font-medium">Countries</a>
        <a href="/admin/products" className="text-blue-600 hover:text-blue-800 font-medium">Products</a>
      </div>
      
      {error && (
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6 w-full max-w-md" role="alert">
          <p className="font-bold">Error:</p>
          <p>{error}</p>
        </div>
      )}
      
      {loading ? (
        <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md">
          <p className="text-center">Loading countries...</p>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md">
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Exporter</label>
          <Select
            options={countries}
            value={form.exporter}
            onChange={handleExporterChange}
            className="text-black"
            placeholder="Select exporter country"
            isClearable
            isSearchable
            isDisabled={loading}
            noOptionsMessage={() => "No countries available. Please add countries to the database."}
          />
          {countries.length === 0 && !loading && !error && (
            <p className="text-xs text-red-500 mt-1">No countries available in the database. Please contact an administrator.</p>
          )}
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Importer</label>
          <Select
            options={countries}
            value={form.importer}
            onChange={handleImporterChange}
            className="text-black"
            placeholder="Select importer country"
            isClearable
            isSearchable
            isDisabled={loading}
            noOptionsMessage={() => "No countries available. Please add countries to the database."}
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Product</label>
          <Select
            options={products}
            value={form.productId}
            onChange={handleProductChange}
            className="text-black"
            placeholder="Select a product"
            isClearable
            isSearchable
            isDisabled={loading}
            noOptionsMessage={() => "No products available. Please add products first."}
          />
          {products.length === 0 && !loading && !error && (
            <p className="text-xs text-red-500 mt-1">No products available in the database. Please add products in the Products Management page.</p>
          )}
          {products.length > 0 && (
            <p className="text-xs text-gray-500 mt-1">Select a product by HS Code and description</p>
          )}
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="description">Description (Optional)</label>
          <input name="description" type="text" value={form.description} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Add Tariff Mapping</button>
        {message && <div className="mt-4 text-center text-black font-bold">{message}</div>}
      </form>
      )}
    </main>
  );
}
