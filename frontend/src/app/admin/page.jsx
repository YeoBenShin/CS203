"use client";
import React, { useState } from "react";

export default function AdminPage() {
  const [form, setForm] = useState({
    tariffMappingID: "",
    rate: "",
    effectiveDate: "",
    expiryDate: "",
    reference: ""
  });
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    try {
      const response = await fetch("http://localhost:8080/tariff", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...form,
          tariffMappingID: form.tariffMappingID ? parseInt(form.tariffMappingID) : null,
          rate: form.rate ? parseFloat(form.rate) : null,
        })
      });
      if (response.ok) {
        setMessage("Tariff added successfully!");
        setForm({ tariffMappingID: "", rate: "", effectiveDate: "", expiryDate: "", reference: "" });
      } else {
        setMessage("Failed to add tariff.");
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
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="tariffMappingID">Tariff Mapping ID</label>
          <input name="tariffMappingID" type="number" min="1" value={form.tariffMappingID} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="rate">Rate</label>
          <input name="rate" type="number" min="0" step="0.01" value={form.rate} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
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
          <input name="reference" type="text" value={form.reference} onChange={handleChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
        </div>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Add Tariff</button>
        {message && <div className="mt-4 text-center text-black font-bold">{message}</div>}
      </form>
    </main>
  );
}
