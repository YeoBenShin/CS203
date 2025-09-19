"use client";
import React, { useState, useEffect } from "react";

export default function ProductManagementPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState("");
  const [form, setForm] = useState({
    hsCode: "",
    description: ""
  });

  // Fetch existing products from the database
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/products`);
        if (!response.ok) {
          throw new Error(`Failed to fetch products: ${response.status} ${response.statusText}`);
        }
        const data = await response.json();
        setProducts(data);
        setError(null);
      } catch (err) {
        console.error("Error fetching products:", err);
        setError("Failed to load products. Please refresh the page or contact support.");
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [message]); // Refresh when message changes (after adding a product)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.hsCode) {
      setMessage("Error: HS Code is required");
      return false;
    }

    const hsCodeNum = parseInt(form.hsCode);
    if (isNaN(hsCodeNum) || hsCodeNum <= 0) {
      setMessage("Error: HS Code must be a positive number");
      return false;
    }

    if (!form.description) {
      setMessage("Error: Description is required");
      return false;
    }

    // Check if product already exists
    if (products.some(p => p.hsCode === hsCodeNum)) {
      setMessage("Error: A product with this HS Code already exists");
      return false;
    }

    return true;
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/products`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          hsCode: parseInt(form.hsCode),
          description: form.description
        })
      });
      
      if (response.ok) {
        setMessage(`Product with HS Code ${form.hsCode} added successfully!`);
        setForm({ hsCode: "", description: "" });
      } else {
        const errorText = await response.text();
        console.error("Server responded with error:", response.status, errorText);
        
        let errorMessage = `Failed to add product: ${response.status} ${response.statusText}`;
        
        try {
          const errorJson = JSON.parse(errorText);
          if (errorJson.message) {
            errorMessage = `Error: ${errorJson.message}`;
          }
        } catch (e) {
          // Not JSON or can't parse the message
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
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Product Management</h1>
      
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
      
      <div className="bg-white shadow-md rounded p-6 mb-6 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">Add New Product</h2>
        
        {loading ? (
          <p className="text-center">Loading...</p>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="hsCode">HS Code</label>
              <input
                id="hsCode"
                name="hsCode"
                type="number"
                min="1"
                value={form.hsCode}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="e.g. 8471 (for computers)"
              />
              <p className="text-xs text-gray-500 mt-1">
                The Harmonized System (HS) code is a standardized numerical classification system for traded products.
              </p>
            </div>
            
            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="description">Description</label>
              <input
                id="description"
                name="description"
                type="text"
                value={form.description}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="e.g. Automatic data processing machines (computers)"
              />
            </div>
            
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full"
            >
              Add Product
            </button>
            
            {message && (
              <div className={`mt-4 text-center font-bold ${message.startsWith("Error") ? "text-red-600" : "text-green-600"}`}>
                {message}
              </div>
            )}
          </form>
        )}
      </div>
      
      <div className="bg-white shadow-md rounded p-6 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">Existing Products ({products.length})</h2>
        
        {loading ? (
          <p className="text-center">Loading products...</p>
        ) : products.length === 0 ? (
          <p className="text-center text-gray-500">No products in the database. Add some using the form above.</p>
        ) : (
          <div className="max-h-64 overflow-y-auto">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="py-2 px-4 text-left">HS Code</th>
                  <th className="py-2 px-4 text-left">Description</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.hsCode} className="border-b">
                    <td className="py-2 px-4">{product.hsCode}</td>
                    <td className="py-2 px-4">{product.description}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </main>
  );
}