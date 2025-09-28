"use client";
import React, { useState } from "react";
import { SuccessMessage, showSuccessPopupMessage } from "@/app/components/SuccessMessage";

export default function AddProductPage() {
  const [form, setForm] = useState({
    hsCode: "",
    description: ""
  });
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage("");

    // Validate form
    if (!form.hsCode || !form.description) {
      setMessage("Please fill in all fields.");
      setIsLoading(false);
      return;
    }

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/products`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          hsCode: form.hsCode,
          description: form.description
        }),
      });

      if (response.ok) {
        const data = await response.json();
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Product created successfully!\nHS Code: " + data.hsCode);
        setForm({ hsCode: "", description: "" }); // Reset form
      } else {
        const errorData = await response.json();
        setMessage(`Error: ${errorData.message || "Failed to create product"}`);
      }
    } catch (error) {
      setMessage(`Error: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Add New Product
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Enter product details to add to the system
          </p>
        </div>
        <form className="mt-8 space-y-6 bg-white p-6 rounded-md shadow-md" onSubmit={handleSubmit}>
          <div className="rounded-md -space-y-px">
            <div>
              <label htmlFor="hsCode" className="block text-sm font-medium text-gray-700 mb-1">
                HS Code
              </label>
              <input
                id="hsCode"
                name="hsCode"
                type="text"
                required
                className="appearance-none rounded-t-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                placeholder="e.g., 8471, 1006"
                value={form.hsCode}
                onChange={handleChange}
              />
            </div>
            <div className="mt-4">
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                id="description"
                name="description"
                required
                rows={4}
                className="appearance-none rounded-b-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                placeholder="Enter product description"
                value={form.description}
                onChange={handleChange}
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={isLoading}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Adding Product..." : "Add Product"}
            </button>
          </div>

          {showSuccessPopup && <SuccessMessage successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}

          {message && (
            <div className={"mt-4 p-4 rounded-md bg-red-100 text-red-700"}>
              {message}
            </div>
          )}
        </form>
      </div>
    </div>
  );
}