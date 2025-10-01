"use client";
import React, { useState, useEffect } from "react";
import FieldSelector from "../components/FieldSelector";
import Button from '../components/Button';
import LoadingSpinner from "../components/messages/LoadingSpinner";
import ErrorMessageDisplay from "../components/messages/ErrorMessageDisplay";
import LoadingPage from "../components/LoadingPage";
import { useUser } from '@clerk/nextjs';
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../components/messages/SuccessMessageDisplay";

// Helper functions for recent calculations persistence
const loadRecentCalculations = () => {
  // Always return empty array during server-side rendering
  if (typeof window === 'undefined') {
    return [];
  }
  const saved = localStorage.getItem('recentCalculations');
  return saved ? JSON.parse(saved) : [];
};

const saveRecentCalculations = (calculations) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('recentCalculations', JSON.stringify(calculations));
  }
};

export default function CalculatorPage() {
  const [pageLoading, setPageLoading] = useState(false);
  // Product search states  
  const [hsCodeOptions, setHsCodeOptions] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Country and trade direction states
  const [countryOptions, setCountryOptions] = useState([]);
  const [selectedCountry, setSelectedCountry] = useState(null);

  // Ensure dates are formatted consistently
  const formatDate = (date) => {
    return new Date(date).toISOString().split('T')[0];
  };

  // Other form states
  const [shippingCost, setShippingCost] = useState('');
  const [tradeDate, setTradeDate] = useState("");
  
  useEffect(() => {
    // Set initial trade date only on client side
    const date = new Date();
    setTradeDate(formatDate(date));
  }, []);

  // Calculation results
  const [calcResult, setCalcResult] = useState(null);
  const [tariffBreakdown, setTariffBreakdown] = useState([]);
  const [loading, setLoading] = useState(false);

  // Recent calculations with persistence
  const [recentCalculations, setRecentCalculations] = useState([]);

  useEffect(() => {
    setRecentCalculations(loadRecentCalculations());
  }, []);

  const [errorMessage, setErrorMessage] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);

  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  const { user } = useUser();
  const userUuid = user ? user.id : null;

  useEffect(() => {
    const fetchAllData = async () => {
        setPageLoading(true);
        
        try {
            // Execute fetch operations in parallel
            await Promise.all([
                fetchHsCodes(),
                fetchCountries()
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

  // Removed search and tariff selection related functions

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
          totalCost: responseData.totalCost,
          totalTariffCost: responseData.totalTariffCost,
          totalTariffRate: responseData.totalTariffRate.toFixed(2),
          date: formatDate(new Date())
        };

        const updatedCalculations = [newCalculation, ...recentCalculations.slice(0, 4)]; // Keep only 5 recent
        setRecentCalculations(updatedCalculations);
        if (typeof window !== 'undefined') {
          saveRecentCalculations(updatedCalculations);
        }
      } else {
        // Handle error response
        if (responseData.message) {
          newErrorMsg.push(responseData.message);
        } else if (responseData.error) {
          newErrorMsg.push(responseData.error);
        } else {
          newErrorMsg.push("Error calculating tariff. No matching tariff found for the selected combination.");
        }
        setErrorMessage(newErrorMsg);
      }

    } catch (error) {
      newErrorMsg.push("Error calculating tariffs. Please try again later.");
      setErrorMessage(newErrorMsg);
      console.error('Calculation error:', error);
    } finally {
      setLoading(false);
    }
  };

  // Save tariff calculation - changed to placeholder 
  const handleSave = async () => {  
    setErrorMessage([]); // Clear previous errors
    let newErrorMsg = [];
    if (!tariffBreakdown || tariffBreakdown.length === 0) {
      newErrorMsg.push("No tariff information available to save.");
      setErrorMessage(newErrorMsg);
      return;
    }

    try {
      setLoading(true);
      const data = {
        uuid: userUuid,
        tariffs: tariffBreakdown,
      }

      const response = await fetch(`${baseUrl}/api/watchlists`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Tariff saved to watchlist successfully!");

      } else {
        const errorData = await response.json();
        newErrorMsg.push("Error saving watchlist: " + errorData.message);
        setErrorMessage(newErrorMsg);
      }
    } catch (error) {
      errorMessage.push("Error saving tariff: " + error);
    } finally {
      setLoading(false);
    }
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

          {/* Search Bar and Tariff Selection commented out
          {selectedTariff ? (
            // Selected Tariff Display section
          ) : (
            // Search Bar section
          )}
          */}
          
          {/* Descriptive Header */}
          <div className="bg-blue-50 border-l-4 border-blue-500 rounded-lg p-4 mb-6">
            <h2 className="text-lg font-semibold text-blue-900 mb-2">Tariff Calculator</h2>
            <p className="text-sm text-gray-600">Calculate import tariffs for your international trade. Enter your product details, shipping cost, and trade date below to get started.</p>
          </div>

          {/* Manual Input Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Enter Details</h2>
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
          {showSuccessPopup && <SuccessMessageDisplay successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}

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