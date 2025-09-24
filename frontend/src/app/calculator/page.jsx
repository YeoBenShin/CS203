// "use client";
// import React, { useMemo, useState } from "react";
// import Select, {StylesConfig} from 'react-select';
// import countryList from 'react-select-country-list';
// import hsCodeList from '../lib/tariff-list';
// import tariffs from '../lib/tariffs.json';
// // must install npm install react-select
// // npm install react-select-country-list --save

// export default function CalculatorPage() {
//   const [hsCode, setHsCode] = useState("");
//   const [prodCost, setProdCost] = useState("");
//   const [salePrice, setSalePrice] = useState('');
//   const [pricingDate, setPricingDate] = useState('');
//   const [quantity, setQuantity] = useState('');
//   const [value, setValue] = useState('');
//   const options = useMemo(() => countryList().getData(), []); 
//   const hsCodeOptions = useMemo(() => hsCodeList().getOptions(), []);

//   const handleProdCost = (e) => setProdCost(e.target.value);
//   const handleSalePrice = (e) => setSalePrice(e.target.value);
//   const handlePricingDate = (e) => setPricingDate(e.target.value);
//   const handleQuantity = (e) => setQuantity(e.target.value);
//   const [selectedOption, setSelectedOption] = useState(null);
//   const [selectedHsCode, setSelectedHsCode] = useState(null);

//   const changeHandler = (option) => {
//       setSelectedOption(option);
//   };
//   const handleHsCodeChange = (option) => {
//     setSelectedHsCode(option);
//     setHsCode(option ? option.value: "");
//   };



//   // State for calculation result
//   const [calcResult, setCalcResult] = useState(null);

//   // Function to send data to backend for calculation
//   const handleCalculate = async () => {
//     // For demo, use prodCost, quantity, and a dummy rate (e.g., 0.05)
//     const data = {
//       prodCost: parseFloat(prodCost),
//       quantity: parseInt(quantity),
//       rate: 0.05 // You can replace this with a real value if you have it
//     };
//     try {
//       const response = await fetch("http://localhost:8080/tariff/calculate", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/json"
//         },
//         body: JSON.stringify(data)
//       });
//       if (response.ok) {
//         const result = await response.json();
//         setCalcResult(result.totalCost);
//       } else {
//         setCalcResult(null);
//         alert("Failed to calculate tariff.");
//       }
//     } catch (error) {
//       setCalcResult(null);
//       alert("Error: " + error.message);
//     }
//   };

//   return (
//   <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
//     <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">

//       <div className="w-4/5 pr-6">
//         <label className="text-xl text-black font-bold block mb-4">Tariff Calculator</label>
//         <div className="flex items-start gap-8">
//           <div className="flex flex-col justify-start">
//             <label className="font-bold mb-1 text-black" htmlFor="hsCode">Enter HS Code:</label>
//             <Select 
//               instanceId="hsCodeSelect"
//               id="hsCodeSelect"
//               className="w-64 text-black border border-black rounded"
//               options={hsCodeOptions}
//               isSearchable={true}
//               isClearable={true}
//               value={selectedHsCode}
//               placeholder="Search.."
//               onChange={handleHsCodeChange}
//             />
//             <div className="mb-8"></div>
//             <label className="font-bold mb-1 text-black" htmlFor="prodCost">Current Product Cost:</label>
//             <div className="relative">
//               <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
//               <input 
//                 id="prodCost"
//                 type="number"min={0}
//                 className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7 bg-white"
//                 value={prodCost}
//                 onChange={handleProdCost}
//               />
//               </div>
//             <div className="mb-8"></div>
//             <label className="font-bold mb-1 text-black" htmlFor="pricingDate">Pricing Date:</label>
//             <input 
//               id="pricingDate"
//               type="date"
//               className="text-black border border-black rounded px-2.5 py-1.5 w-64 bg-white"
//               value={pricingDate}
//               onChange={handlePricingDate}
//             />  
//             <div className="mb-8"></div>
//             <div className="flex flex-row items-center gap-4">
//               <button 
//                 className="bg-blue-200 border border-black border-2 text-black font-bold px-8 text-xl rounded transition w-64"
//                 type="button"
//                 onClick={handleCalculate}
//                 >Calculate</button>
//             </div>
//             <div style={{ minHeight: '2.5rem' }}>
//               {calcResult !== null && (
//                 <div className="mt-4 text-black font-bold">Total Cost: ${calcResult.toFixed(2)}</div>
//               )}
//             </div>
//           </div>

//           <div className="flex flex-col justify-start">
//             <label className="font-bold mb-1 text-black" htmlFor="countryOrigin">Enter Country of Origin:</label>
//             <Select 
//               instanceId="countryOrigin"
//               id="countryOrigin"
//               className="w-64 text-black border border-black rounded"
//               options={options}
//               isSearchable={true}
//               isClearable={true}
//               value={selectedOption}
//               placeholder="Search.."
//               onChange={changeHandler}
//             />
//             <div className="mb-8"></div>
//             <label className="font-bold mb-1 text-black" htmlFor="salePrice">Current Sale Price:</label>
//             <div className="relative">
//               <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
//               <input 
//                 id="salePrice"
//                 type="number" min={0}
//                 className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7 bg-white"
//                 value={salePrice}
//                 onChange={handleSalePrice}
//               />
//             </div>
//             <div className="mb-8"></div>
//             <label className="font-bold mb-1 text-black" htmlFor="quantity">Quantity:</label>
//             <input 
//                 id="quantity"
//                 type="number" min={0}
//                 className="text-black border border-black rounded px-2.5 py-1.5 w-64 bg-white"
//                 value={quantity}
//                 onChange={handleQuantity}
//               />
//               <div className="mb-8"></div>
//               <button 
//                 className="bg-blue-200 border-black border-2 text-black font-bold px-8 text-xl rounded transition w-64"
//                 type="button"
//                 style={{ marginTop: 0 }}
//                 >Save</button>
//           </div>
//         </div>
//       </div>

//       <div className="w-px bg-black mx-4" />

//       <div className="w-1/5">
//         <h2 className="text-2xl font-bold mb-2 text-black">Saved Tariffs</h2>
//         <p className="text-black">Saved tariffs will appear here</p>
//       </div>

//     </div>
//   </main>
//   );
// }


"use client";
import React, { useState, useEffect } from "react";

export default function CalculatorPage() {
  // Product search states
  const [hsCodeOptions, setHsCodeOptions] = useState([]);
  const [filteredHsCodeOptions, setFilteredHsCodeOptions] = useState([]);
  const [selectedHsCode, setSelectedHsCode] = useState(null);
  const [selectedProductDesc, setSelectedProductDesc] = useState(null);
  const [hsCodeInput, setHsCodeInput] = useState('');
  const [showHsCodeDropdown, setShowHsCodeDropdown] = useState(false);

  // Country and trade direction states
  const [countryOptions, setCountryOptions] = useState([]);
  const [filteredCountryOptions, setFilteredCountryOptions] = useState([]);
  const [selectedCountry, setSelectedCountry] = useState(null);
  const [countryInput, setCountryInput] = useState('');
  const [showCountryDropdown, setShowCountryDropdown] = useState(false);
  const [tradeDirection, setTradeDirection] = useState(''); // 'import' or 'export'

  // Other form states
  const [shippingCost, setShippingCost] = useState('');
  const [tradeDate, setTradeDate] = useState('');

  // Calculation results
  const [calcResult, setCalcResult] = useState(null);
  const [tariffBreakdown, setTariffBreakdown] = useState([]);
  const [loading, setLoading] = useState(false);

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
          value: item.hsCode + '',
          label: `${item.hsCode} - ${item.description}`
        }));
        setHsCodeOptions(products);
        setFilteredHsCodeOptions(products);

      } catch (error) {
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
        setFilteredCountryOptions(countries);
      } catch (error) {
        console.error('Error fetching countries:', error);
      }
    };

    fetchHsCodes();
    fetchCountries();
  }, []);

  // Handle HS Code selection and filtering
  const handleHsCodeInputChange = (value) => {
    setHsCodeInput(value);
    if (!value) {
      setFilteredHsCodeOptions(hsCodeOptions);
      setSelectedHsCode(null);
      setSelectedProductDesc(null);
      setShowHsCodeDropdown(false);
      return;
    }

    const filtered = hsCodeOptions.filter(option =>
      option.label.toLowerCase().includes(value.toLowerCase()) ||
      option.value.includes(value)
    );
    setFilteredHsCodeOptions(filtered);
    setShowHsCodeDropdown(true);
  };

  const regexForProductExtraction = /.*-\s*(.*)/;
  const handleHsCodeSelection = (option) => {
    setSelectedHsCode(option.value);
    setSelectedProductDesc(regexForProductExtraction.exec(option.label)[1]); // Extract description from label
    setHsCodeInput(option.label);
    setShowHsCodeDropdown(false); // Hide dropdown after selection
  };

  // Handle Country selection and filtering
  const handleCountryInputChange = (value) => {
    setCountryInput(value);
    if (!value) {
      setFilteredCountryOptions(countryOptions);
      setSelectedCountry(null);
      setShowCountryDropdown(false);
      return;
    }

    const filtered = countryOptions.filter(option =>
      option.label.toLowerCase().includes(value.toLowerCase())
    );
    setFilteredCountryOptions(filtered);
    setShowCountryDropdown(true);
  };

  const handleCountrySelection = (option) => {
    setSelectedCountry(option.value);
    setCountryInput(option.label);
    setShowCountryDropdown(false); // Hide dropdown after selection

  };

  // Handle form inputs
  const handleShippingCost = (e) => setShippingCost(e.target.value);
  const handleTradeDate = (e) => setTradeDate(e.target.value);
  const handleTradeDirectionChange = (e) => setTradeDirection(e.target.value);

  // Tariff calculation function
  const handleCalculate = async () => {
    // Basic validation -> can upgrade to better UI validation later
    if (!selectedHsCode || !selectedCountry || !tradeDirection || !shippingCost || !tradeDate) {
      alert("Please fill in all required fields");
      return;
    }

    if (!/^\d+(\.\d{1,2})?$/.test(shippingCost)) {
      alert("Please enter a valid shipping cost");
      return;
    }

    setLoading(true);
    const data = {
      hsCode: selectedHsCode,
      country: selectedCountry,
      tradeDirection: tradeDirection,
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
        // {totalCost: 10, totalTariffRate: 0, totalTariffCost: 0, tariffs: []}
        setCalcResult({
          originalCost: parseFloat(shippingCost),
          totalCost: result.totalCost,
          totalTariffRate: result.totalTariffRate.toFixed(2),
          totalTariffCost: result.totalTariffCost,
        })

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
      alert("Error calculating tariff: " + error.message);
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
        hsCode: selectedHsCode,
        productDescription: selectedProductDesc,
        country: selectedCountry,
        tradeDirection: tradeDirection,
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
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">
        <div className="w-full">
          <h1 className="text-3xl text-black font-bold mb-8 text-center">Tariff Calculator</h1>

          {/* Product Selection Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Product Selection</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Search by HS Code / Description:</label>
                <div className="relative">
                  <input
                    type="text"
                    className="text-black border border-gray-300 rounded px-3 py-2 w-full bg-white"
                    value={hsCodeInput}
                    onChange={(e) => handleHsCodeInputChange(e.target.value)}
                    onFocus={() => {
                      if (hsCodeInput && filteredHsCodeOptions.length > 0) {
                        setShowHsCodeDropdown(true);
                      }
                    }}
                    onBlur={() => {
                      // Delay hiding to allow for clicks on dropdown items
                      setTimeout(() => setShowHsCodeDropdown(false), 150);
                    }}
                    placeholder="Enter HS Code or Product Description..."
                  />
                  {showHsCodeDropdown && filteredHsCodeOptions.length > 0 && (
                    <div className="absolute z-10 w-full bg-white border border-gray-300 rounded-b max-h-48 overflow-y-auto shadow-lg">
                      {filteredHsCodeOptions.map((option) => (
                        <div
                          key={option.value}
                          className="px-3 py-2 hover:bg-blue-100 cursor-pointer"
                          onMouseDown={(e) => e.preventDefault()} // Prevent blur on click
                          onClick={() => handleHsCodeSelection(option)}
                        >
                          {option.label}
                        </div>
                      ))}
                    </div>
                  )}
                  {hsCodeInput && (
                    <button
                      type="button"
                      className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                      onClick={() => {
                        setHsCodeInput('');
                        setSelectedHsCode(null);
                        setSelectedProductDesc(null);
                        // setProductDescInput('');
                        setShowHsCodeDropdown(false);
                      }}
                    >
                      ×
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Trade Details Section */}
          <div className="bg-white/20 rounded-lg p-6 mb-6">
            <h2 className="text-xl font-bold text-black mb-4">Trade Details</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <label className="font-bold mb-2 text-black block">Country:</label>
                <div className="relative">
                  <input
                    type="text"
                    className="text-black border border-gray-300 rounded px-3 py-2 w-full bg-white"
                    value={countryInput}
                    onChange={(e) => handleCountryInputChange(e.target.value)}
                    onFocus={() => {
                      if (countryInput && filteredCountryOptions.length > 0) {
                        setShowCountryDropdown(true);
                      }
                    }}
                    onBlur={() => {
                      // Delay hiding to allow for clicks on dropdown items
                      setTimeout(() => setShowCountryDropdown(false), 150);
                    }}
                    placeholder="Enter Country..."
                  />
                  {showCountryDropdown && filteredCountryOptions.length > 0 && (
                    <div className="absolute z-10 w-full bg-white border border-gray-300 rounded-b max-h-48 overflow-y-auto shadow-lg">
                      {filteredCountryOptions.map((option) => (
                        <div
                          key={option.value}
                          className="px-3 py-2 hover:bg-blue-100 cursor-pointer"
                          onMouseDown={(e) => e.preventDefault()} // Prevent blur on click
                          onClick={() => handleCountrySelection(option)}
                        >
                          {option.label}
                        </div>
                      ))}
                    </div>
                  )}
                  {countryInput && (
                    <button
                      type="button"
                      className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                      onClick={() => {
                        setCountryInput('');
                        setSelectedCountry(null);
                        setShowCountryDropdown(false);
                      }}
                    >
                      ×
                    </button>
                  )}
                </div>
              </div>
              <div>
                <label className="font-bold mb-2 text-black block">Trade Direction:</label>
                <div className="flex flex-col gap-2">
                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name="tradeDirection"                 // same name => mutually exclusive
                      value="import"
                      checked={tradeDirection === "import"}
                      onChange={handleTradeDirectionChange}
                      className="accent-blue-500"           // Tailwind colour for the radio
                    />
                    Import From USA
                  </label>

                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      name="tradeDirection"
                      value="export"
                      checked={tradeDirection === "export"}
                      onChange={handleTradeDirectionChange}
                      className="accent-blue-500"
                    />
                    Export To USA
                  </label>
                </div>
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
                  step="0.01"
                  className="text-black border border-gray-300 rounded px-3 py-2 w-full pl-8 bg-white"
                  value={shippingCost}
                  onChange={handleShippingCost}
                  placeholder="0.00"
                />
              </div>
            </div>
          </div>

          {/* Calculate Button */}
          <div className="flex gap-4 mb-8">
            <button
              className="bg-blue-500 hover:bg-blue-600 border-2 border-black text-white font-bold px-8 py-3 text-xl rounded transition disabled:opacity-50"
              onClick={handleCalculate}
              disabled={loading}
            >
              {loading ? 'Calculating...' : 'Calculate Tariffs'}
            </button>

            {calcResult && (
              <button
                className="bg-green-500 hover:bg-green-600 border-2 border-black text-white font-bold px-8 py-3 text-xl rounded transition"
                onClick={handleSave}
              >
                Save Calculation
              </button>
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
                    <span className="font-semibold text-gray-700">Product:</span>
                    <p className="text-black">{selectedProductDesc ? selectedProductDesc : 'N/A'}</p>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">HS Code:</span>
                    <p className="text-black">{selectedHsCode ? selectedHsCode : 'N/A'}</p>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">Trade Partners:</span>
                    <p className="text-black">
                      {tradeDirection === 'import'
                        ? `${selectedCountry ? selectedCountry : 'N/A'} → USA`
                        : `USA → ${selectedCountry ? selectedCountry : 'N/A'}`
                      }
                    </p>
                  </div>
                  <div>
                    <span className="font-semibold text-gray-700">Trade Date:</span>
                    <p className="text-black">{tradeDate || 'N/A'}</p>
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
                        <span className="text-gray-600 ml-2">({tariff.rate})</span>
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