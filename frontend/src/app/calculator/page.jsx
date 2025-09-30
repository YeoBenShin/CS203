"use client";
import React, { useState, useEffect } from "react";
import FieldSelector from "../components/FieldSelector";
import Button from '../components/Button';
import LoadingSpinner from "../components/messages/LoadingSpinner";
import ErrorMessageDisplay from "../components/messages/ErrorMessageDisplay";
import LoadingPage from "../components/LoadingPage";

export default function CalculatorPage() {
  const [pageLoading, setPageLoading] = useState(false);
  // Product search states  
  const [hsCodeOptions, setHsCodeOptions] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Country and trade direction states
  const [countryOptions, setCountryOptions] = useState([]);
  const [selectedCountry, setSelectedCountry] = useState(null);

  // Tariff search and selection
  const [tariffs, setTariffs] = useState([]);
  const [filteredTariffs, setFilteredTariffs] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedTariff, setSelectedTariff] = useState(null);

  // Other form states
  const [shippingCost, setShippingCost] = useState('');
  const [tradeDate, setTradeDate] = useState(new Date().toISOString().split('T')[0]); // Default to today

  // Calculation results
  const [calcResult, setCalcResult] = useState(null);
  const [tariffBreakdown, setTariffBreakdown] = useState([]);
  const [loading, setLoading] = useState(false);

  // Recent calculations
  const [recentCalculations, setRecentCalculations] = useState([]);

  const [errorMessage, setErrorMessage] = useState([]);

  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  useEffect(() => {
    const fetchAllData = async () => {
        setPageLoading(true);
        
        try {
            // Execute all fetch operations in parallel
            await Promise.all([
                fetchHsCodes(),
                fetchCountries(), 
                fetchTariffs()
            ]);
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            setPageLoading(false);
        }
    };

    fetchAllData();
}, []);

  const fetchHsCodes = async () => {
      try {
        const response = await fetch(`${baseUrl}/api/products`);
        const data = await response.json();

        const products = data.map(item => ({
          value: item.hsCode,
          description: item.description,
          label: `${item.hsCode} - ${item.description}`
        }));
        setHsCodeOptions(products);

      } catch (error) {
        errorMessage.push("Error fetching HS codes: " + error);
        console.error('Error fetching HS codes:', error);
      } 
    };

    // Fetch countries from backend
    const fetchCountries = async () => {
      try {
        const response = await fetch(`${baseUrl}/api/countries`);
        const data = await response.json();

        const countries = data.filter(country => country.isoCode !== 'USA').map(country => ({
          value: country.isoCode,
          label: country.isoCode + " - " + country.name
        }));
        setCountryOptions(countries);
      } catch (error) {
        errorMessage.push("Error fetching countries: " + error);
        console.error('Error fetching countries:', error);
      }
    };

    // Fetch tariffs from backend
    const fetchTariffs = async () => {
      try {
        const response = await fetch(`${baseUrl}/api/tariffs`);
        if (response.ok) {
          const data = await response.json();
          setTariffs(data);
          setFilteredTariffs(data.slice(0, 5)); // Show first 5 tariffs initially
        } else {
          setErrorMessage("Failed to fetch tariffs");
        }
      } catch (error) {
        setErrorMessage("Error fetching tariffs: " + error);
        console.error('Error fetching tariffs:', error);
      }
    };

  const handleHsCodeSelection = (option) => {
    if (!option) {
      setSelectedProduct(null);
      return;
    }
    setErrorMessage([]);
    setCalcResult(null);
    setSelectedProduct(option);
  };

  const handleCountrySelection = (option) => {
    if (!option) {
      setSelectedCountry(null);
      return;
    }
    setSelectedCountry(option);
  };

  // Search functionality for tariffs
  const handleSearchChange = (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    if (query === "") {
      setFilteredTariffs(tariffs.slice(0, 5));
    } else {
      const filtered = tariffs.filter(tariff =>
        tariff.exporterName?.toLowerCase().includes(query) ||
        tariff.importerName?.toLowerCase().includes(query) ||
        tariff.HSCode?.toString().toLowerCase().includes(query) ||
        tariff.productDescription?.toLowerCase().includes(query) ||
        (parseFloat(tariff.rate) * 100).toFixed(2).includes(query)
      ).slice(0, 5);
      setFilteredTariffs(filtered);
    }
  };

  const clearSearch = () => {
    setSearchQuery("");
    setFilteredTariffs(tariffs.slice(0, 5));
  };

  const handleTariffSelection = (tariff) => {
    setSelectedTariff(tariff);
    // Auto-populate form fields based on selected tariff
    const matchingProduct = hsCodeOptions.find(product => product.value === tariff.HSCode);
    const matchingCountry = countryOptions.find(country => country.value === tariff.exporterCode);

    if (matchingProduct) setSelectedProduct(matchingProduct);
    if (matchingCountry) setSelectedCountry(matchingCountry);
  };

  // Function to highlight matching text
  const highlightText = (text, query) => {
    if (!query || !text) return text;

    const regex = new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
    const parts = text.toString().split(regex);

    return parts.map((part, index) =>
      regex.test(part) ? (
        <span key={index} className="bg-yellow-200 font-semibold text-gray-900">
          {part}
        </span>
      ) : part
    );
  };

  // Handle form inputs
  const handleShippingCost = (e) => {
    setShippingCost(e.target.value);
    setCalcResult(null);
    setErrorMessage([]);
  }
  const handleTradeDate = (e) => {
    setTradeDate(e.target.value);
    setCalcResult(null);
    setErrorMessage([]);
  }

  // Tariff calculation function
  const handleCalculate = async () => {
    setErrorMessage([]); // Clear previous errors
    let newErrorMsg = [];
    
    // Basic validation
    if (!selectedProduct) {
      newErrorMsg.push("Please select a valid HS Code");
    } else if (selectedProduct.value < 0 || selectedProduct.value > 9999999999) {
      newErrorMsg.push("HS Code must be a 10 digit number");
    }
    
    if (!selectedCountry) {
      newErrorMsg.push("Please select a valid Country");
    }
    
    if (!shippingCost || !/^\d+(\.\d{1,2})?$/.test(shippingCost) || parseFloat(shippingCost) <= 0) {
      newErrorMsg.push("Please enter a valid Shipping Cost greater than 0");
    }
    
    if (!tradeDate) {
      newErrorMsg.push("Please select a valid Trade Date");
    } else if (new Date(tradeDate) > new Date()) {
      // newErrorMsg.push("Trade date cannot be in the future");
    }
    
    if (newErrorMsg.length > 0) {
      setErrorMessage(newErrorMsg);
      return;
    }

    setLoading(true);
    const data = {
      hsCode: selectedProduct.value,
      country: selectedCountry.value,
      shippingCost: parseFloat(shippingCost),
      tradeDate: tradeDate
    };

    try {
      const response = await fetch(`${baseUrl}/api/calculations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      });

      const responseData = await response.json();
      if (response.ok) {
        setTariffBreakdown(responseData.tariffs);
        setCalcResult({
          originalCost: parseFloat(shippingCost),
          totalCost: responseData.totalCost,
          totalTariffRate: responseData.totalTariffRate.toFixed(2),
          totalTariffCost: responseData.totalTariffCost,
        });

        // Automatically add to recent calculations
        const newCalculation = {
          id: Date.now(),
          product: selectedProduct.description,
          hsCode: selectedProduct.value,
          country: selectedCountry.label.split(' - ')[1], // Get country name
          totalCost: result.totalCost,
          totalTariffCost: result.totalTariffCost,
          totalTariffRate: result.totalTariffRate.toFixed(2),
          date: new Date().toLocaleDateString()
        };

        setRecentCalculations(prev => [newCalculation, ...prev.slice(0, 4)]); // Keep only 5 recent
      } else {
        const errorData = await response.json();
        console.log(errorData)
        newErrorMsg.push(errorData.message || "Error calculating tariff");
        setErrorMessage(newErrorMsg);
      }

    } catch (error) {
      errorMessage.push("Error calculating tariffs: " + error);
    } finally {
      setLoading(false);
    }
  };

  // Save tariff calculation - changed to placeholder 
  const handleSave = async () => {
    // Empty placeholder for future functionality
    console.log("Save button clicked - functionality not yet implemented");
    // You can add your desired functionality here in the future
  };

  if (pageLoading) {
    return <LoadingPage />;
  }

  return (
    <main>
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8 gap-8">
        {/* Left Side - Main Calculator */}
        <div className="w-2/3">
          <h1 className="text-3xl text-black font-bold mb-8 text-center">Tariff Calculator</h1>

          {/* Search Bar or Selected Tariff Display */}
          {selectedTariff ? (
            // Selected Tariff Display
            <div className="bg-blue-50 border-l-4 border-blue-500 rounded-lg p-4 mb-6">
              <div className="flex justify-between items-start">
                <div>
                  <div className="flex items-center gap-6 mb-2">
                    <h3 className="text-lg font-semibold text-blue-900">Selected Tariff</h3>
                    <span className={`px-3 py-1 text-xs font-medium rounded-full ${selectedTariff.expiryDate && new Date(selectedTariff.expiryDate) < new Date()
                      ? 'bg-red-100 text-red-800'
                      : 'bg-green-600 text-white'
                      }`}>
                      {selectedTariff.expiryDate && new Date(selectedTariff.expiryDate) < new Date()
                        ? 'Expired'
                        : 'In Effect'}
                    </span>
                  </div>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="font-medium text-blue-700">Route: </span>
                      <span className="text-blue-900">{selectedTariff.exporterName} → {selectedTariff.importerName}</span>
                    </div>
                    <div>
                      <span className="font-medium text-blue-700">HS Code: </span>
                      <span className="text-blue-900">{selectedTariff.HSCode}</span>
                    </div>
                    <div>
                      <span className="font-medium text-blue-700">Product: </span>
                      <span className="text-blue-900">{selectedTariff.productDescription || "N/A"}</span>
                    </div>
                    <div>
                      <span className="font-medium text-blue-700">Rate: </span>
                      <span className="text-blue-900 font-semibold">{(parseFloat(selectedTariff.rate) * 100).toFixed(2)}%</span>
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => setSelectedTariff(null)}
                  className="text-blue-400 hover:text-blue-600"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          ) : (
            // Search Bar
            <div className="bg-white/20 rounded-lg p-6 mb-6">
              <h2 className="text-xl font-bold text-black mb-4">Find Tariff</h2>
              <div className="relative max-w-md mb-4">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
                <input
                  type="text"
                  value={searchQuery}
                  onChange={handleSearchChange}
                  placeholder="Search tariffs by country, HS code, product, or rate..."
                  className="block w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
                />
                {searchQuery && (
                  <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
                    <button
                      onClick={clearSearch}
                      className="text-gray-400 hover:text-gray-600"
                    >
                      <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                )}
              </div>

              {/* Tariff List */}
                <div className="bg-white rounded-lg border border-gray-200 max-h-80 overflow-y-auto">
                  {filteredTariffs.length === 0 ? (
                    <div className="p-4 text-center text-gray-500">
                      {searchQuery ? `No tariffs found matching "${searchQuery}"` : "No tariffs available"}
                    </div>
                  ) : (
                    <div>
                      {filteredTariffs.map((tariff) => (
                        <div
                          key={tariff.tariffID}
                          onClick={() => handleTariffSelection(tariff)}
                          className="p-3 border-b border-gray-100 hover:bg-blue-50 cursor-pointer transition-colors"
                        >
                          <div className="flex justify-between items-start">
                            <div className="flex-1">
                              <div className="flex items-center space-x-2 mb-1">
                                <span className="text-sm font-medium text-gray-900">
                                  {highlightText(tariff.exporterName, searchQuery)} → {highlightText(tariff.importerName, searchQuery)}
                                </span>
                                <span className="text-sm font-semibold text-blue-600">
                                  {highlightText((parseFloat(tariff.rate) * 100).toFixed(2) + "%", searchQuery)}
                                </span>
                              </div>
                              <div className="text-xs text-gray-600 mb-1">
                                <span className="font-medium">HS: </span>
                                {highlightText(tariff.HSCode, searchQuery)}
                              </div>
                              <div className="text-xs text-gray-500 truncate">
                                {highlightText(tariff.productDescription || "N/A", searchQuery)}
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
            </div>
          )}

          {/* Manual Input Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Or Enter Details Manually</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Search by HS Code / Description:</label>
                <FieldSelector
                  options={hsCodeOptions}
                  value={selectedProduct}
                  onChange={handleHsCodeSelection}
                  placeholder="Select HS Code..."
                />
              </div>
              <div>
                <label className="font-bold mb-2 text-black block">Exporting Country:</label>
                <FieldSelector
                  options={countryOptions}
                  value={selectedCountry}
                  onChange={handleCountrySelection}
                  placeholder="Enter Country..."
                  isClearable
                />
              </div>
            </div>
            <div className="mt-4 w-full md:w-1/2">
              <label className="font-bold mb-2 text-black block">Date of Trade:</label>
              <input
                type="date"
                className="text-black border border-gray-300 rounded px-3 py-2 w-90 bg-white"
                value={tradeDate}
                onChange={handleTradeDate}
              />
            </div>
          </div>

          {/* Shipping Cost Section */}
          <div className="bg-white/20 backdrop-blur-sm rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Cost Details</h2>
            <div className="w-full md:w-1/2">
              <label className="font-bold mb-2 text-black block">Total Shipping Cost:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input
                  type="number"
                  min={0}
                  step="0.5"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-90 pl-8 bg-white"
                  value={shippingCost}
                  onChange={handleShippingCost}
                  placeholder="0.00"
                />
              </div>
            </div>
          </div>

          <ErrorMessageDisplay errors={errorMessage} />

          {/* Calculate Button */}
          <div className="flex gap-4 mb-8">
            <Button
              className="w-200"
              onClick={handleCalculate}
              isLoading={loading}
              width=''
              colorBg="bg-blue-500 hover:bg-blue-600 focus:ring-blue-500"
            >
              {loading && <LoadingSpinner />}
              {loading ? "Calculating..." : "Calculate Tariffs"}
            </Button>

            {calcResult && (
              <Button
               className="w-200"
                onClick={handleSave}
                isLoading={loading}
                width=''
                colorBg="bg-green-500 hover:bg-green-600 focus:ring-green-500"
              >
                {loading && <LoadingSpinner />}
                {loading ? "Saving..." : "Save Tariff"}
              </Button>
            )}
          </div>

          {/* Results Section */}
          {calcResult && (
            <div className="bg-white/30 backdrop-blur-sm rounded-lg p-6">
              {/* Trade Summary */}
              <div className="bg-blue-50 rounded-lg p-4 mb-6 border-l-4 border-blue-500">
                <h2 className="text-lg font-bold text-black mb-3">Trade Summary</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-semibold text-gray-700">Product: </span>
                    <span className="text-black">{selectedProduct ? selectedProduct.description : 'N/A'} </span>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">HS Code: </span>
                    <span className="text-black">{selectedProduct ? selectedProduct.value : 'N/A'} </span>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">Trade Partners: </span>
                    <span className="text-black">{`${selectedCountry?.value ? selectedCountry.value : 'N/A'} → USA`} </span>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">Trade Date: </span>
                    <span className="text-black">{tradeDate || 'N/A'}</span>
                  </div>
                  <div className="md:col-span-2">
                    <span className="font-semibold text-gray-700">Total Cumulative Tariff Rate:</span>
                    <p className="text-2xl font-bold text-red-600">{calcResult.totalTariffRate}%</p>
                  </div>
                </div>
              </div>

              <h2 className="text-2xl font-bold text-black mb-4">Tariff Breakdown</h2>

              {/* Summary */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-blue-100 p-4 rounded">
                  <h3 className="font-bold text-black">Original Shipping Cost</h3>
                  <p className="text-2xl font-bold text-blue-600">${calcResult.originalCost.toFixed(2)}</p>
                </div>
                <div className="bg-red-100 p-4 rounded">
                  <h3 className="font-bold text-black">Total Tariff Cost</h3>
                  <p className="text-2xl font-bold text-red-600">${calcResult.totalTariffCost.toFixed(2)}</p>
                </div>
                <div className="bg-green-100 p-4 rounded">
                  <h3 className="font-bold text-black">Total Cost</h3>
                  <p className="text-2xl font-bold text-green-600">${calcResult.totalCost.toFixed(2)}</p>
                </div>
              </div>

              {/* Detailed Breakdown */}
              <div className="bg-white rounded-lg p-4">
                <h3 className="text-lg font-bold text-black mb-4">Individual Tariff Details</h3>
                <div className="space-y-3">
                  {tariffBreakdown.map((tariff, index) => (
                    <div
                      key={tariff.tariffID}
                      className="flex justify-between items-center p-3 border border-gray-300 rounded hover:bg-gray-50 cursor-help relative group"
                      title={tariff.reference || "not-updated"}
                    >
                      <div>
                        <span className="font-semibold text-black">Tariff {index + 1}</span>
                        <span className="text-gray-600 ml-2">({tariff.rate.toFixed(2)}%)</span>
                      </div>
                      <span className="font-bold text-black">${tariff.amountApplied.toFixed(2)}</span>

                      {/* Tooltip */}
                      <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-black text-white text-sm rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10">
                        {tariff.reference || "not-updated"}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Right Side - Recent Calculations */}
        <div className="w-1/3 mt-17">
          <div className="bg-white/20 backdrop-blur-sm rounded-lg p-6">
            <h2 className="text-xl font-bold text-black mb-4">Recent Calculations</h2>

            {recentCalculations.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                <svg className="h-12 w-12 mx-auto mb-3 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
                <p className="text-sm">No calculations yet</p>
                <p className="text-xs text-gray-400 mt-1">Your saved calculations will appear here</p>
              </div>
            ) : (
              <div className="space-y-3">
                {recentCalculations.map((calc) => (
                  <div key={calc.id} className="bg-white/30 rounded-lg p-3 border border-white/20">
                    <div className= "justify-between items-start mb-2">
                        <h4 className="text-sm font-semibold text-black truncate">{calc.product}</h4>
                        <p className="text-xs text-gray-600">HS: {calc.hsCode}</p>
                        <p className="text-xs text-gray-600">{calc.country}</p>
                      </div>
                      <div>
                        <p className="text-sm font-bold text-green-600 ">Total Cost: ${calc.totalCost.toFixed(2)}</p>
                        <p className="text-xs text-red-600">Tariff Rate: {calc.totalTariffRate}%</p>
                        <div>
                        <p className="text-xs text-red-600">Tariff Cost: ${calc.totalTariffCost}</p>
                        </div>
                    </div>
                    <div>
                      <span className="text-xs text-gray-500">{calc.date}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}