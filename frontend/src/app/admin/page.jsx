"use client";
import React from "react";
import Link from "next/link";

export default function AdminIndexPage() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
      <h1 className="text-3xl font-bold mb-6 text-black">Admin Dashboard</h1>
      
      <div className="mb-4 w-full max-w-md flex justify-between">
        <a href="/admin" className="text-blue-600 hover:text-blue-800 font-medium">Admin Home</a>
        <a href="/admin/mapping" className="text-blue-600 hover:text-blue-800 font-medium">Create Mapping</a>
        <a href="/admin/tariff" className="text-blue-600 hover:text-blue-800 font-medium">Add Tariff</a>
        <a href="/admin/countries" className="text-blue-600 hover:text-blue-800 font-medium">Countries</a>
        <a href="/admin/products" className="text-blue-600 hover:text-blue-800 font-medium">Products</a>
      </div>
      
      <div className="bg-white shadow-md rounded p-8 mb-4 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4 text-center">Tariff Management</h2>
        
        <div className="flex flex-col space-y-4">
          <Link href="/admin/countries" className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-3 px-4 rounded text-center">
            Manage Countries
          </Link>
          <p className="text-sm text-gray-600 mb-4">
            First, ensure the countries you need are in the database.
          </p>
          
          <Link href="/admin/products" className="bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded text-center">
            Manage Products
          </Link>
          <p className="text-sm text-gray-600 mb-4">
            Next, ensure the products you need are in the database.
          </p>
          
          <Link href="/admin/mapping" className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-4 rounded text-center">
            Create Tariff Mapping
          </Link>
          <p className="text-sm text-gray-600 mb-4">
            Then, create a mapping between exporter country, importer country, and product.
          </p>
          
          <Link href="/admin/tariff" className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded text-center">
            Add Tariff Rate
          </Link>
          <p className="text-sm text-gray-600">
            Finally, add tariff rates using the mapping ID from the previous step.
          </p>
        </div>
        
        <div className="mt-8 pt-6 border-t border-gray-200">
          <h3 className="text-lg font-semibold mb-2">How It Works</h3>
          <ol className="list-decimal list-inside space-y-2 text-gray-700">
            <li>Create a tariff mapping between countries and product</li>
            <li>Note the mapping ID that is generated</li>
            <li>Use that mapping ID to create a tariff with specific rate</li>
            <li>Tariffs can be queried through the calculator</li>
          </ol>
        </div>
      </div>
    </main>
  );
}
