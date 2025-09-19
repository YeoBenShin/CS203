"use client";
import React, { useState, useEffect } from "react";
import Select from "react-select";
import countryList from "react-select-country-list";

export default function CountryManagementPage() {
  const [countries, setCountries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState("");
  const [selectedCountry, setSelectedCountry] = useState(null);
  const [region, setRegion] = useState("");
  
  // Get all available countries from react-select-country-list
  const availableCountries = React.useMemo(() => {
    return countryList().getData().map(country => ({
      value: country.value,
      label: `${country.label} (${country.value})`,
      isoCode: country.value,
      name: country.label
    }));
  }, []);
  
  // Fetch existing countries from the database
  useEffect(() => {
    const fetchCountries = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/countries`);
        if (!response.ok) {
          throw new Error(`Failed to fetch countries: ${response.status} ${response.statusText}`);
        }
        const data = await response.json();
        setCountries(data);
        setError(null);
      } catch (err) {
        console.error("Error fetching countries:", err);
        setError("Failed to load countries. Please refresh the page or contact support.");
      } finally {
        setLoading(false);
      }
    };

    fetchCountries();
  }, [message]); // Refresh when message changes (after adding a country)
  
  // Filter out countries that already exist in the database
  const countriesToAdd = React.useMemo(() => {
    if (!countries.length) return availableCountries;
    
    const existingIsoCodes = new Set(countries.map(c => c.isoCode));
    return availableCountries.filter(c => !existingIsoCodes.has(c.isoCode));
  }, [availableCountries, countries]);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!selectedCountry) {
      setMessage("Error: Please select a country");
      return;
    }
    
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/api/countries`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          isoCode: selectedCountry.isoCode,
          name: selectedCountry.name,
          region: region || null
        })
      });
      
      if (response.ok) {
        setMessage(`Country ${selectedCountry.name} (${selectedCountry.isoCode}) added successfully!`);
        setSelectedCountry(null);
        setRegion("");
      } else {
        const errorText = await response.text();
        console.error("Server responded with error:", response.status, errorText);
        
        let errorMessage = `Failed to add country: ${response.status} ${response.statusText}`;
        
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
      <h1 className="text-3xl font-bold mb-6 text-black">Admin: Country Management</h1>
      
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
        <h2 className="text-xl font-bold mb-4">Add New Country</h2>
        
        {loading ? (
          <p className="text-center">Loading...</p>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-bold mb-2">Country</label>
              <Select
                options={countriesToAdd}
                value={selectedCountry}
                onChange={setSelectedCountry}
                className="text-black"
                placeholder="Select a country to add"
                isClearable
                isSearchable
              />
              {countriesToAdd.length === 0 && (
                <p className="text-xs text-green-500 mt-1">All countries have been added to the database!</p>
              )}
            </div>
            
            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="region">Region (Optional)</label>
              <input
                id="region"
                type="text"
                value={region}
                onChange={(e) => setRegion(e.target.value)}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="e.g. Asia, Europe, North America"
              />
            </div>
            
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full"
              disabled={!selectedCountry || countriesToAdd.length === 0}
            >
              Add Country
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
        <h2 className="text-xl font-bold mb-4">Existing Countries ({countries.length})</h2>
        
        {loading ? (
          <p className="text-center">Loading countries...</p>
        ) : countries.length === 0 ? (
          <p className="text-center text-gray-500">No countries in the database. Add some using the form above.</p>
        ) : (
          <div className="max-h-64 overflow-y-auto">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="py-2 px-4 text-left">ISO Code</th>
                  <th className="py-2 px-4 text-left">Name</th>
                  <th className="py-2 px-4 text-left">Region</th>
                </tr>
              </thead>
              <tbody>
                {countries.map((country) => (
                  <tr key={country.isoCode} className="border-b">
                    <td className="py-2 px-4">{country.isoCode}</td>
                    <td className="py-2 px-4">{country.name}</td>
                    <td className="py-2 px-4">{country.region || "-"}</td>
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