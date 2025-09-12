"use client";
import React, { useState, useMemo } from "react";
import Select from "react-select";
import countryList from "react-select-country-list";

export default function CreateTariffMappingPage() {
  const [form, setForm] = useState({
    exporter: null,
    importer: null,
    product: "",
    description: ""
  });
  const [message, setMessage] = useState("");
  const countryOptions = useMemo(() => countryList().getData(), []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleExporterChange = (option) => {
    setForm({ ...form, exporter: option });
  };
  const handleImporterChange = (option) => {
    setForm({ ...form, importer: option });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    try {
      const response = await fetch("http://localhost:8080/tariff-mapping", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          exporter: form.exporter ? form.exporter.value : "",
          importer: form.importer ? form.importer.value : "",
          product: form.product,
          description: form.description
        })
      });
      if (response.ok) {
        setMessage("Tariff mapping added successfully!");
        setForm({ exporter: null, importer: null, product: "", description: "" });
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
            className="text-black"
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
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product">Product</label>
          <input name="product" type="text" value={form.product} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="description">Description</label>
          <input name="description" type="text" value={form.description} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Add Tariff Mapping</button>
        {message && <div className="mt-4 text-center text-black font-bold">{message}</div>}
      </form>
    </main>
  );
}
