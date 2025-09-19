"use client";
import React, { useState, useEffect } from "react";
import Select from "react-select";
//import countryList from "react-select-country-list";

export default function CreateTariffMappingPage() {
  const [form, setForm] = useState({
    exporter: null,
    importer: null,
    product: null,
  });
  const [message, setMessage] = useState("");
  const [countryOptions, setCountryOptions] = useState([]);
  const [productOptions, setProductOptions] = useState([]);

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/countries");
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
        const response = await fetch("http://localhost:8080/api/products");
        const products = await response.json();
        const options = products.map(product => ({
          label: `${product.hsCode}${product.description ? ` - ${product.description}` : ''}`,
          value: product.hsCode.toString()
        }));
        setProductOptions(options);
        console.log("Fetched products:", products);
      } catch (error) {
        console.error("Failed to fetch products:", error);
      }
    };

    fetchProducts();
  }, []);

  //const countryOptions = useMemo(() => countryList().getData(), []);

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
    try {
      const response = await fetch("http://localhost:8080/tariffmapping", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          exporter: form.exporter ? form.exporter.value : "",
          importer: form.importer ? form.importer.value : "",
          product: form.product ? form.product.value : ""
        })
      });
      if (response.ok) {
        setMessage("Tariff mapping added successfully!");
        setForm({ exporter: null, importer: null, product: null });
      } else {
        setMessage("Failed to add tariff mapping.");
      }
    } catch (err) {
      setMessage("Error: " + err.message);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Create Tariff Mapping</h1>
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
        
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Add Tariff Mapping</button>
        {message && <div className="mt-4 text-center text-black font-bold">{message}</div>}
      </form>
    </main>
  );
}
