"use client";
import React, { useState, useEffect } from "react";
import FieldSelector from "../components/FieldSelector";
import Button from '../components/Button';
import LoadingSpinner from "../components/messages/LoadingSpinner";
import ErrorMessageDisplay from "../components/messages/ErrorMessageDisplay";
import LoadingPage from "../components/LoadingPage";
import { useUser } from '@clerk/nextjs';
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../components/messages/SuccessMessageDisplay";

const getUnitDetails = (unitCode) => {
  if (!unitCode) return null;

  switch (unitCode.toUpperCase()) {
    case 'AV' : return { unit: 'Percentage', abbreviation: 'av'}
    case 'BBL': return { unit: 'Barrels', abbreviation: 'bbl' };
    case 'CAR': return { unit: 'Carats', abbreviation: 'car' };
    case 'KG':
    case 'CKG':
    case 'CYK':
    case 'GKG': return { unit: 'Kilograms', abbreviation: 'kg' };
    case 'CM2':
    case 'SME':
    case 'SQM': return { unit: 'Square Meters', abbreviation: 'm²' };
    case 'CM3':
    case 'CC': return { unit: 'Cubic Centimeters', abbreviation: 'cm³' };
    case 'DOZ': return { unit: 'Dozens', abbreviation: 'doz' };
    case 'DPC': return { unit: 'Dozen Pieces', abbreviation: 'dz pcs' };
    case 'DPR': return { unit: 'Dozen Pairs', abbreviation: 'dz prs' };
    case 'G':
    case 'GM': return { unit: 'Grams', abbreviation: 'g' };
    case 'GR': return { unit: 'Gross', abbreviation: 'gr' };
    case 'L':
    case 'LTR':
    case 'PFL': return { unit: 'Liters', abbreviation: 'L' };
    case 'M':
    case 'LNM': return { unit: 'Meters', abbreviation: 'm' };
    case 'M2': return { unit: 'Square Meters', abbreviation: 'm²' };
    case 'M3': return { unit: 'Cubic Meters', abbreviation: 'm³' };
    case 'PCS': return { unit: 'Pieces', abbreviation: 'pcs' };
    case 'PK': return { unit: 'Pack', abbreviation: 'pk' };
    case 'PRS': return { unit: 'Pairs', abbreviation: 'prs' };
    case 'T':
    case 'TON': return { unit: 'Tons', abbreviation: 't' };
    case 'NO': return { unit: 'Number', abbreviation: 'no.' };
    case 'X':
    case 'NA':
    default:
      return null; // No specific unit required
  }
};

export default function CalculatorPage() {
  const [pageLoading, setPageLoading] = useState(false);
  // Product search states  
  const [hsCodeOptions, setHsCodeOptions] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Country and trade direction states
  const [exportCountryOptions, setExportCountryOptions] = useState([]);
  const [selectedExportCountry, setExportSelectedCountry] = useState(null);

  const [importCountryOptions, setImportCountryOptions] = useState([]);
  const [selectedImportCountry, setImportSelectedCountry] = useState(null);

  // Quantity of goods states
  const [productQuantity, setProductQuantity] = useState('');

  // Other form states
  const [shippingCost, setShippingCost] = useState('');
  const [tradeDate, setTradeDate] = useState("");

  // Calculation results
  const [calcResult, setCalcResult] = useState(null);
  const [tariffBreakdown, setTariffBreakdown] = useState([]);
  const [loading, setLoading] = useState(false);

  // Recent calculations with persistence
  const [recentCalculations, setRecentCalculations] = useState([]);

  const [errorMessage, setErrorMessage] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);

  const [tariffUnitInfo, setTariffUnitInfo] = useState(null);

  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  const { user } = useUser();
  const userUuid = user ? user.id : null;

  // Ensure dates are formatted consistently
  const formatDate = (date) => {
    return new Date(date).toISOString().split('T')[0];
  };

  useEffect(() => {
    // Set initial trade date only on client side
    const date = new Date();
    setTradeDate(formatDate(date));
  }, []);

  useEffect(() => {
    setRecentCalculations(loadRecentCalculations());
  }, []);

  useEffect(() => {
    const fetchTariffUnitInfo = async () => {
      if (selectedProduct && selectedImportCountry && selectedExportCountry) {
        // Clear previous state
        setTariffUnitInfo(null);
        setProductQuantity('');

        try {
          const params = new URLSearchParams({
            hsCode: selectedProduct.value,
            importCountry: selectedImportCountry.value,
            exportCountry: selectedExportCountry.value,
          });
          const response = await fetch(`${baseUrl}/api/tariffs/unit-info?${params}`);
          
          if (response.ok) {
            const data = await response.json(); // Expects { "unit": "KG" }
            console.log('API Response Data:', data); 
            
            const unitDetails = getUnitDetails(data.unit);
            console.log('Derived Unit Details:', unitDetails);
            
            setTariffUnitInfo(unitDetails);
          } else {
            // If response is not ok (e.g., 404), no specific unit is found.
            setTariffUnitInfo(null);
          }
        } catch (error) {
          console.error('Error fetching tariff unit info:', error);
          setTariffUnitInfo(null);
        }
      } else {
        // Clear info if any selection is missing
        setTariffUnitInfo(null);
      }
    };

    fetchTariffUnitInfo();
  }, [selectedProduct, selectedImportCountry, selectedExportCountry]);

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
        value: item.hscode,
        description: item.description,
        label: `${item.hscode} - ${item.description}`
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

      const countries = data.map(country => ({
        value: country.isoCode,
        label: country.isoCode + " - " + country.name
      }));
      setExportCountryOptions(countries);
      setImportCountryOptions(countries);
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

  const handleExportCountrySelection = (option) => {
    if (!option) {
      setExportSelectedCountry(null);
      return;
    }
    setCalcResult(null);
    setErrorMessage([]);
    setExportSelectedCountry(option);
  };

  const handleImportCountrySelection = (option) => {
    if (!option) {
      setImportSelectedCountry(null);
      return;
    }
    setCalcResult(null);
    setErrorMessage([]);
    setImportSelectedCountry(option);
  };

  // Removed search and tariff selection related functions

  // Handle form inputs
  const handleShippingCost = (e) => {
    setShippingCost(e.target.value);
    setCalcResult(null);
    setErrorMessage([]);
  }

  const handleProductQuantity = (e) => {
    setProductQuantity(e.target.value);
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
    } else if (!/^\d{1,10}$/.test(selectedProduct.value)) { // Check if it's a digit string up to 10 chars
      newErrorMsg.push("HS Code must be a valid number up to 10 digits");
    }

    if (!(selectedExportCountry && selectedImportCountry)) {
      newErrorMsg.push("Please select both Exporting and Importing Countries");
    } else if (selectedExportCountry.value === selectedImportCountry.value) {
      newErrorMsg.push("Please select different Exporting and Importing Countries");
    }

    if (!shippingCost || !/^\d+(\.\d{1,2})?$/.test(shippingCost) || parseFloat(shippingCost) <= 0) {
      newErrorMsg.push("Please enter a valid Shipping Cost greater than 0");
    }

    if (!tradeDate) {
      newErrorMsg.push("Please select a valid Trade Date");
    } else if (new Date(tradeDate) > new Date()) {
      // newErrorMsg.push("Trade date cannot be in the future");
    }

    if (tariffUnitInfo && (!productQuantity || parseFloat(productQuantity) <= 0)) {
      newErrorMsg.push(`Please enter a valid quantity for the product.`);
    }

    if (newErrorMsg.length > 0) {
      setErrorMessage(newErrorMsg);
      return;
    }

    setLoading(true);
    const data = {
      hsCode: selectedProduct.value,
      importer: selectedImportCountry.value,
      exporter: selectedExportCountry.value,
      shippingCost: parseFloat(shippingCost),
      productQuantity: productQuantity ? parseFloat(productQuantity) : null,
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
          totalTariffRate: (responseData.totalTariffRate ?? 0).toFixed(2),
          totalTariffCost: responseData.totalTariffCost,
        });

        // Automatically add to recent calculations
        const newCalculation = {
          id: crypto.randomUUID(),
          product: selectedProduct.description,
          hsCode: selectedProduct.value,
          country: selectedExportCountry.label.split(' - ')[1], // Get country name
          totalCost: responseData.totalCost,
          totalTariffCost: responseData.totalTariffCost,
          totalTariffRate: (responseData.totalTariffRate ?? 0).toFixed(2),
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
          newErrorMsg.push("An unknown error occurred during calculation.");
        }
        setErrorMessage(newErrorMsg);
      }

    } catch (error) {
      newErrorMsg.push("Could not connect to the server. Please try again later.");
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
        newErrorMsg.push("Error saving watchlist: " + (errorData.message || "Unknown error"));
        setErrorMessage(newErrorMsg);
      }
    } catch (error) {
      let newErrorMsg = [];
      newErrorMsg.push("Error saving tariff: " + error.message);
      setErrorMessage(newErrorMsg);
    } finally {
      setLoading(false);
    }
  };

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
            <h2 className="text-lg font-semibold text-blue-900 mb-2">Calculate Tariff For A Product!</h2>
            <p className="text-sm text-gray-600">Calculate import tariffs for your international trade.
              <br /> Enter your product details, shipping cost, and trade date below to get started. </p>
          </div>

          {/* Manual Input Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Enter Details</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Importing Country:</label>
                <FieldSelector
                  options={importCountryOptions}
                  value={selectedImportCountry}
                  onChange={handleImportCountrySelection}
                  placeholder="Enter Country..."
                  isClearable
                />
              </div>
              <div>
                <div>
                  <label className="font-bold mb-2 text-black block">Exporting Country:</label>
                  <FieldSelector
                    options={exportCountryOptions}
                    value={selectedExportCountry}
                    onChange={handleExportCountrySelection}
                    placeholder="Enter Country..."
                    isClearable
                  />
                </div>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
              <div className="w-full">
                <label className="font-bold mb-2 text-black block">Search by HS Code / Description:</label>
                <FieldSelector
                  options={hsCodeOptions}
                  value={selectedProduct}
                  onChange={handleHsCodeSelection}
                  placeholder="Select HS Code..."
                  isClearable
                />
              </div>
              <div>
                <label className="font-bold mb-2 text-black block">Date of Trade:</label>
                <input
                  type="date"
                  className="text-black border border-gray-300 rounded px-3 py-2 h-9.5 w-full bg-white"
                  value={tradeDate}
                  onChange={handleTradeDate}
                />
              </div>
            </div>
          </div>
          {tariffUnitInfo && (
            <div className="bg-white/20 rounded-lg p-6 mb-6 animate-fade-in">
              <h2 className="text-xl font-bold text-black mb-4">Unit of Measurement</h2>
              <div className="w-full md:w-1/2">
                <label className="font-bold mb-2 text-black block">Quantity in {tariffUnitInfo.unit} ({tariffUnitInfo.abbreviation}):</label>
                <input
                  type="number"
                  min="0"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-full bg-white"
                  value={productQuantity}
                  onChange={handleProductQuantity}
                  placeholder="0"
                />
              </div>
            </div>
          )}

          {/* Shipping Cost Section */}
          <div className="bg-white/20 backdrop-blur-sm rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Cost Details</h2>
            <div className="w-full md:w-1/2">
              <label className="font-bold mb-2 text-black block">Product Cost:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-full pl-8 bg-white"
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
                        <span className="text-black">{`${selectedExportCountry?.value} to ${selectedImportCountry?.value}`} </span>
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
                      <p className="text-2xl font-bold text-green-600">${(calcResult.totalCost ?? 0).toFixed(2)}</p>
                    </div>
                  </div>

                  {/* Detailed Breakdown */}
                  <div className="bg-white rounded-lg p-4">
                    <h3 className="text-lg font-bold text-black mb-4">Individual Tariff Details</h3>
                    <div className="space-y-3">
                      {tariffBreakdown.map((tariff, index) => (
                        <div
                          key={tariff.tariffID || index}
                          className="flex justify-between items-center p-3 border border-gray-300 rounded hover:bg-gray-50 cursor-help relative group"
                          title={tariff.reference || "not-updated"}
                        >
                          <div>
                            <span className="font-semibold text-black">Tariff {index + 1}</span>
                            <span className="text-gray-600 ml-2">({(tariff.rate ?? 0).toFixed(2)}%)</span>
                          </div>
                          <span className="font-bold text-black">${(tariff.amountApplied ?? 0).toFixed(2)}</span>

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
                        <div className="justify-between items-start mb-2">
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