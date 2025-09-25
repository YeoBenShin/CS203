"use client";
import React, { useState, useEffect } from "react";
import Select from "react-select";

export default function AdminPage() {
  const [form, setForm] = useState({
    exporter: null,
    importer: null,
    product: null,
    rate: "",
    effectiveDate: "",
    expiryDate: "",
    reference: ""
  });

  const [message, setMessage] = useState("");

  const [countryOptions, setCountryOptions] = useState([]);
    const [productOptions, setProductOptions] = useState([]);
  
    useEffect(() => {
      const fetchCountries = async () => {
        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/countries`);
          const countries = await response.json();
          const options = countries.map(country => ({
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
          const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/products`);
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
  };

   const handleExporterChange = (option) => {
    setForm({ ...form, exporter: option });
  };
  const handleImporterChange = (option) => {
    setForm({ ...form, importer: option });
  };
  const handleProductChange = (option) => {
    setForm({ ...form, product: option });
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    
    // Validation
    if (!form.exporter) {
      setMessage("Please select an exporter country");
      return;
    }
    if (!form.importer) {
      setMessage("Please select an importer country");
      return;
    }
    if (!form.product) {
      setMessage("Please select a product");
      return;
    }
    if (!form.rate) {
      setMessage("Please enter a rate");
      return;
    }
    if (!form.effectiveDate) {
      setMessage("Please enter an effective date");
      return;
    }
    
    try {
        const requestData = {
          exporter: form.exporter.value,
          importer: form.importer.value,
          HSCode: Number(form.product.value),
          rate: parseFloat(form.rate),
          effectiveDate: new Date(form.effectiveDate).toISOString(),
          expiryDate: form.expiryDate ? new Date(form.expiryDate).toISOString() : null,
          reference: form.reference || null
        };
        
        console.log("Sending request data:", requestData);
        
        const base_url = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080";
        const response = await fetch(`${base_url}/api/tariffs`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
      });
      if (response.ok) {
        setMessage("Tariff added successfully!");
        setForm({ 
          exporter: null, 
          importer: null, 
          product: null, 
          rate: "", 
          effectiveDate: "", 
          expiryDate: "", 
          reference: "" 
        });
      } else {
        const errorText = await response.text();
        setMessage(`Failed to add tariff: ${errorText}`);
      }
    } catch (err) {
      setMessage("Error: " + err.message);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Add Tariff</h1>
      <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md">
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Exporter</label>
          <Select
            options={countryOptions}
            value={form.exporter}
            onChange={handleExporterChange}
            className="text-blue"
            placeholder="Select exporter country"
            isClearable
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Importer</label>
          <Select
            options={countryOptions}
            value={form.importer}
            onChange={handleImporterChange}
            className="text-black"
            placeholder="Select importer country"
            isClearable
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">Product</label>
          <Select
            options={productOptions}
            value={form.product}
            onChange={handleProductChange}
            className="text-black"
            placeholder="Select Product HSCode"
            isClearable
          />
        </div>
        
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="rate">Rate</label>
          <input name="rate" type="number" min="0" step="0.01" value={form.rate} onChange={handleChange} placeholder="Enter a percentage for tariff" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="effectiveDate">Effective Date</label>
          <input name="effectiveDate" type="date" value={form.effectiveDate} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="expiryDate">Expiry Date</label>
          <input name="expiryDate" type="date" value={form.expiryDate} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="reference">Reference</label>
          <input name="reference" type="text" value={form.reference} onChange={handleChange} placeholder="Source URL" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Add Tariff</button>
        {message && <div className="mt-4 text-center text-black font-bold">{message}</div>}
      </form>
    </main>
  );
}
