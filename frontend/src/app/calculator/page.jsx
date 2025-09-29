"use client";
import React, { useState, useEffect } from "react";
import FieldSelector from "../components/FieldSelector";
import Button from '../components/Button';

export default function CalculatorPage() {
  // Product search states
  const [hsCodeOptions, setHsCodeOptions] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Country and trade direction states
  const [countryOptions, setCountryOptions] = useState([]);
  const [selectedCountry, setSelectedCountry] = useState(null);

  // Other form states
  const [shippingCost, setShippingCost] = useState('');
  const [tradeDate, setTradeDate] = useState('');

  // Calculation results
  const [calcResult, setCalcResult] = useState(null);
  const [tariffBreakdown, setTariffBreakdown] = useState([]);
  const [loading, setLoading] = useState(false);

  const [errorMessage, setErrorMessage] = useState(null);

  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  useEffect(() => {
    // Fetch HS codes from backend
    const fetchHsCodes = async () => {
      try {
        // Replace with actual API call
        const response = await fetch(`${baseUrl}/api/products`);
        const data = await response.json();
        // console.log(data);

        // const mockHsCodes = [
        //   { value: '010121', label: '010121 - Pure-bred breeding horses' },
        //   { value: '010129', label: '010129 - Other live horses' },
        //   { value: '020130', label: '020130 - Fresh or chilled bovine meat, boneless' },
        // ];

        const products = data.map(item => ({
          value: item.hsCode, // required field for react-selector to work
          description: item.description,
          label: `${item.hsCode} - ${item.description}`
        }));
        setHsCodeOptions(products);

      } catch (error) {
        setErrorMessage("Error fetching HS codes: " + error);
        console.error('Error fetching HS codes:', error);
      }
    };

    // Fetch countries from backend
    const fetchCountries = async () => {
      try {
        const response = await fetch(`${baseUrl}/api/countries`);
        const data = await response.json();
        // console.log(data);

        // const mockCountries = [
        //   { value: 'CN', label: 'China' },
        //   { value: 'CA', label: 'Canada' },
        //   { value: 'MX', label: 'Mexico' },
        //   { value: 'DE', label: 'Germany' },
        //   { value: 'JP', label: 'Japan' },
        // ].filter(country => country.value !== 'USA'); // Exclude USA

        const countries = data.filter(country => country.isoCode !== 'USA').map(country => ({
          value: country.isoCode,
          label: country.isoCode + " - " + country.name
        }));
        setCountryOptions(countries);
      } catch (error) {
        setErrorMessage("Error fetching countries: " + error);
        console.error('Error fetching countries:', error);
      }
    };

    fetchHsCodes();
    fetchCountries();
  }, []);

  const handleHsCodeSelection = (option) => {
    if (!option) {
      setSelectedProduct(null);
      return;
    } 
    setSelectedProduct(option);
  };

  // Handle Country selection and filtering
  // const handleCountryInputChange = (value) => {
  //   setCountryInput(value);
  //   if (!value) {
  //     setFilteredCountryOptions(countryOptions);
  //     setSelectedCountry(null);
  //     setShowCountryDropdown(false);
  //     return;
  //   }

  //   const filtered = countryOptions.filter(option =>
  //     option.label.toLowerCase().includes(value.toLowerCase())
  //   );
  //   setFilteredCountryOptions(filtered);
  //   setShowCountryDropdown(true);
  // };

  const handleCountrySelection = (option) => {
    setSelectedCountry(option);
    // setCountryInput(option.label);

  };

  // Handle form inputs
  const handleShippingCost = (e) => setShippingCost(e.target.value);
  const handleTradeDate = (e) => setTradeDate(e.target.value);

  // Tariff calculation function
  const handleCalculate = async () => {
    // Basic validation -> can upgrade to better UI validation later
    if (!selectedProduct) {
      setErrorMessage("Please select a valid HS Code");
      return;
    }
    if (!selectedCountry) {
      setErrorMessage("Please select a valid Country");
      return;
    }
    if (!shippingCost || !/^\d+(\.\d{1,2})?$/.test(shippingCost)) {
      setErrorMessage("Please enter a valid Shipping Cost");
      return;
    }
    if (!tradeDate) {
      setErrorMessage("Please select a valid Trade Date");
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
      // Replace with actual API call
      const response = await fetch(`${baseUrl}/api/calculations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        const result = await response.json();
        console.log(result);
        setTariffBreakdown(result.tariffs);
        setCalcResult({
          originalCost: parseFloat(shippingCost),
          totalCost: result.totalCost,
          totalTariffRate: result.totalTariffRate.toFixed(2),
          totalTariffCost: result.totalTariffCost,
        })
      } else {
        const errorData = await response.json();
        console.log(errorData)
        setErrorMessage(errorData.message || "Error calculating tariff");
      }

      // Mock response for demo
      // await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate API delay

      // const mockTariffBreakdown = [
      //   {
      //     id: 1,
      //     tariffType: 'Ad Valorem Duty',
      //     rate: '15%',
      //     appliedAmount: parseFloat(shippingCost) * 0.15,
      //     reference: 'USMCA Trade Agreement Article 4.2'
      //   },
      //   {
      //     id: 2,
      //     tariffType: 'Anti-Dumping Duty',
      //     rate: '8.5%',
      //     appliedAmount: parseFloat(shippingCost) * 0.085,
      //     reference: null
      //   },
      //   {
      //     id: 3,
      //     tariffType: 'Processing Fee',
      //     rate: '$25 flat',
      //     appliedAmount: 25,
      //     reference: 'CBP Processing Regulation 19.6'
      //   }
      // ];

      // const totalTariffCost = mockTariffBreakdown.reduce((sum, tariff) => sum + tariff.appliedAmount, 0);
      // const totalCost = parseFloat(shippingCost) + totalTariffCost;
      // const totalTariffRate = ((totalTariffCost / parseFloat(shippingCost)) * 100).toFixed(2);

      // setTariffBreakdown(mockTariffBreakdown);
      // setCalcResult({
      //   originalCost: parseFloat(shippingCost),
      //   totalTariffCost: totalTariffCost,
      //   totalCost: totalCost,
      //   totalTariffRate: totalTariffRate
      // });

    } catch (error) {
      setErrorMessage("Error calculating tariffs: " + error);
    } finally {
      setLoading(false);
    }
  };

  // Save tariff calculation
  const handleSave = async () => {
    if (!calcResult) {
      alert("Please calculate tariffs first before saving");
      return;
    }

    try {
      const saveData = {
        hsCode: selectedProduct.value,
        productDescription: selectedProduct.description,
        country: selectedCountry.value,
        shippingCost: parseFloat(shippingCost),
        tradeDate: tradeDate,
        tariffBreakdown: tariffBreakdown,
        totalCost: calcResult.totalCost,
        savedAt: new Date().toISOString()
      };

      // Replace with actual API call
      // const response = await fetch(`${baseUrl}/api/tariff/save`, {
      //   method: "POST",
      //   headers: { "Content-Type": "application/json" },
      //   body: JSON.stringify(saveData)
      // });

      alert("Tariff calculation saved successfully!");
    } catch (error) {
      alert("Error saving tariff: " + error.message);
    }
  };

  return (
    <main>
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">
        <div className="w-full">
          <h1 className="text-3xl text-black font-bold mb-8 text-center">Tariff Calculator</h1>

          {/* Product Selection Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Product Selection</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Search by HS Code / Description:</label>
                <FieldSelector
                  options={hsCodeOptions}
                  value={selectedProduct}
                  onChange={handleHsCodeSelection}
                  className="text-red"
                  placeholder="Select HS Code..."
                  isClearable
                />
              </div>
            </div>
          </div>

          {/* Trade Details Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Trade Details</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Exporting Country:</label>
                <FieldSelector
                  options={countryOptions}
                  value={selectedCountry}
                  onChange={handleCountrySelection}
                  className="text-red"
                  placeholder="Enter Country..."
                  isClearable
                />
              </div>
              <div>
                <label className="font-bold mb-2 text-black block">Date of Trade:</label>
                <input
                  type="date"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-full bg-white"
                  value={tradeDate}
                  onChange={handleTradeDate}
                />
              </div>
            </div>
          </div>

          {/* Shipping Cost Section */}
          <div className="bg-white/20 backdrop-blur-sm rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Cost Details</h2>
            <div className="w-full md:w-1/3">
              <label className="font-bold mb-2 text-black block">Total Shipping Cost:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input
                  type="number"
                  min={0}
                  step="0.5"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-full pl-8 bg-white"
                  value={shippingCost}
                  onChange={handleShippingCost}
                  placeholder="0.00"
                />
              </div>
            </div>
          </div>

          {errorMessage ? <div className="text-red-600 font-bold mb-4">{errorMessage}</div> : null}

          {/* Calculate Button */}
          <div className="flex gap-4 mb-8">
            <Button
              onClick={handleCalculate}
              isLoading={loading}
              isLoadingText="Calculating..."
              buttonText="Calculate Tariffs"
              width=''
              colorBg="bg-blue-500 hover:bg-blue-600 focus:ring-blue-500"/>

            {calcResult && (
              <Button
              onClick={handleSave}
              isLoading={loading}
              isLoadingText="Saving..."
              buttonText="Save Tariff"
              width=''
              colorBg="bg-green-500 hover:bg-green-600 focus:ring-green-500"/>
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
                    <span className="text-black">{`${selectedCountry.value ? selectedCountry.value : 'N/A'} → USA`} </span>
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
      </div>
    </main>
  );
}