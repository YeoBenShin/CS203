"use client";
import React, { useState } from "react";

export default function AddTariffPage() {
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

  const validateForm = () => {
    // Validate required fields
    if (!form.tariffMappingID) {
      setMessage("Error: Tariff Mapping ID is required and must reference an existing mapping. Please create a mapping first.");
      return false;
    }
    
    const mappingIdNum = parseInt(form.tariffMappingID);
    if (isNaN(mappingIdNum) || mappingIdNum <= 0) {
      setMessage("Error: Tariff Mapping ID must be a positive number");
      return false;
    }
    
    if (!form.rate) {
      setMessage("Error: Rate is required");
      return false;
    }
    
    const rateNum = parseFloat(form.rate);
    if (isNaN(rateNum) || rateNum < 0) {
      setMessage("Error: Rate must be a non-negative number");
      return false;
    }
    
    // If effective date and expiry date are both provided, check that expiry is after effective
    if (form.effectiveDate && form.expiryDate) {
      const effectiveDate = new Date(form.effectiveDate);
      const expiryDate = new Date(form.expiryDate);
      
      if (expiryDate < effectiveDate) {
        setMessage("Error: Expiry date must be after effective date");
        return false;
      }
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Use the validation function
    if (!validateForm()) {
      return;
    }
    
    setMessage("");
    
    // Format dates correctly for Java SQL Date compatibility
    const formatDateForJava = (dateStr) => {
      if (!dateStr) return null;
      // Format as yyyy-MM-dd to match java.sql.Date format
      return dateStr;
    };
    
    // Prepare the data to send
    const payload = {
      tariffMappingID: form.tariffMappingID ? Number(form.tariffMappingID) : null,
      rate: form.rate || null,
      effectiveDate: formatDateForJava(form.effectiveDate),
      expiryDate: formatDateForJava(form.expiryDate),
      reference: form.reference || ""
    };
    
    console.log("Sending payload:", payload);
    console.log("API URL:", `${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/tariffs`);
    
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/tariffs`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (response.ok) {
        const createdTariff = await response.json();
        setMessage(`Tariff added successfully! Tariff ID: ${createdTariff.tariffID}`);
        setForm({ tariffMappingID: "", rate: "", effectiveDate: "", expiryDate: "", reference: "" });
      } else {
        const errorText = await response.text();
        console.error("Server responded with error:", response.status, errorText);
        
        // Check for foreign key constraint error
        if (errorText.includes("foreign key constraint fails") || errorText.includes("fk_tariff_mapping")) {
          setMessage("Error: The Tariff Mapping ID you entered does not exist. Please create a mapping first using the 'Create Tariff Mapping' page.");
        } else {
          let errorMessage = `Failed to add tariff: ${response.status} ${response.statusText}`;
          
          // Try to extract more specific error from the response
          try {
            const errorJson = JSON.parse(errorText);
            if (errorJson.message) {
              errorMessage = `Error: ${errorJson.message}`;
            }
          } catch (e) {
            // Not JSON or can't parse the message, use the default error message
          }
          
          setMessage(errorMessage);
        }
      }
    } catch (err) {
      console.error("Request failed:", err);
      setMessage("Error: " + err.message);
    }
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Add Tariff</h1>
      
      <div className="mb-4 w-full max-w-md flex justify-between">
        <a href="/admin" className="text-blue-600 hover:text-blue-800 font-medium">Admin Home</a>
        <a href="/admin/mapping" className="text-blue-600 hover:text-blue-800 font-medium">Create Mapping</a>
        <a href="/admin/tariff" className="text-blue-600 hover:text-blue-800 font-medium">Add Tariff</a>
        <a href="/admin/countries" className="text-blue-600 hover:text-blue-800 font-medium">Countries</a>
        <a href="/admin/products" className="text-blue-600 hover:text-blue-800 font-medium">Products</a>
      </div>
      
      <div className="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 mb-6 w-full max-w-md" role="alert">
        <p className="font-bold">Important Note:</p>
        <p>You must create a Tariff Mapping first using the 'Create Tariff Mapping' page before adding a tariff. The Tariff Mapping ID you enter here must reference an existing mapping.</p>
      </div>
      
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