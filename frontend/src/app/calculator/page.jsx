  "use client";
  import React, { useMemo, useState } from "react";
  import Select, {StylesConfig} from 'react-select';
  import countryList from 'react-select-country-list';
  import hsCodeList from '../lib/tariff-list';
  import tariffs from '../lib/tariffs.json';
  // must install npm install react-select
  // npm install react-select-country-list --save

  export default function CalculatorPage() {
    const [hsCode, setHsCode] = useState("");
    const [prodCost, setProdCost] = useState("");
    const [salePrice, setSalePrice] = useState('');
    const [pricingDate, setPricingDate] = useState('');
    const [quantity, setQuantity] = useState('');
    const [value, setValue] = useState('');
    const options = useMemo(() => countryList().getData(), []); 
    const hsCodeOptions = useMemo(() => hsCodeList().getOptions(), []);

    const handleProdCost = (e) => setProdCost(e.target.value);
    const handleSalePrice = (e) => setSalePrice(e.target.value);
    const handlePricingDate = (e) => setPricingDate(e.target.value);
    const handleQuantity = (e) => setQuantity(e.target.value);
    const [selectedOption, setSelectedOption] = useState(null);
    const [selectedHsCode, setSelectedHsCode] = useState(null);

    const changeHandler = (option) => {
        setSelectedOption(option);
    };
    const handleHsCodeChange = (option) => {
      setSelectedHsCode(option);
      setHsCode(option ? option.value: "");
    };



    // State for calculation result
    const [calcResult, setCalcResult] = useState(null);

    // Function to send data to backend for calculation
    const handleCalculate = async () => {
      // For demo, use prodCost, quantity, and a dummy rate (e.g., 0.05)
      const data = {
        prodCost: parseFloat(prodCost),
        quantity: parseInt(quantity),
        rate: 0.05 // You can replace this with a real value if you have it
      };
      try {
        const response = await fetch("http://localhost:8080/tariff/calculate", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(data)
        });
        if (response.ok) {
          const result = await response.json();
          setCalcResult(result.totalCost);
        } else {
          setCalcResult(null);
          alert("Failed to calculate tariff.");
        }
      } catch (error) {
        setCalcResult(null);
        alert("Error: " + error.message);
      }
    };

    return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">

        <div className="w-4/5 pr-6">
          <label className="text-xl text-black font-bold block mb-4">Tariff Calculator</label>
          <div className="flex items-start gap-8">
            <div className="flex flex-col justify-start">
              <label className="font-bold mb-1 text-black" htmlFor="hsCode">Enter HS Code:</label>
              <Select 
                instanceId="hsCodeSelect"
                id="hsCodeSelect"
                className="w-64 text-black border border-black rounded"
                options={hsCodeOptions}
                isSearchable={true}
                isClearable={true}
                value={selectedHsCode}
                placeholder="Search.."
                onChange={handleHsCodeChange}
              />
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="prodCost">Current Product Cost:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input 
                  id="prodCost"
                  type="number"min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7 bg-white"
                  value={prodCost}
                  onChange={handleProdCost}
                />
                </div>
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="pricingDate">Pricing Date:</label>
              <input 
                id="pricingDate"
                type="date"
                className="text-black border border-black rounded px-2.5 py-1.5 w-64 bg-white"
                value={pricingDate}
                onChange={handlePricingDate}
              />  
              <div className="mb-8"></div>
              <div className="flex flex-row items-center gap-4">
                <button 
                  className="bg-blue-200 border border-black border-2 text-black font-bold px-8 text-xl rounded transition w-64"
                  type="button"
                  onClick={handleCalculate}
                  >Calculate</button>
              </div>
              <div style={{ minHeight: '2.5rem' }}>
                {calcResult !== null && (
                  <div className="mt-4 text-black font-bold">Total Cost: ${calcResult.toFixed(2)}</div>
                )}
              </div>
            </div>

            <div className="flex flex-col justify-start">
              <label className="font-bold mb-1 text-black" htmlFor="countryOrigin">Enter Country of Origin:</label>
              <Select 
                instanceId="countryOrigin"
                id="countryOrigin"
                className="w-64 text-black border border-black rounded"
                options={options}
                isSearchable={true}
                isClearable={true}
                value={selectedOption}
                placeholder="Search.."
                onChange={changeHandler}
              />
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="salePrice">Current Sale Price:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input 
                  id="salePrice"
                  type="number" min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7 bg-white"
                  value={salePrice}
                  onChange={handleSalePrice}
                />
              </div>
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="quantity">Quantity:</label>
              <input 
                  id="quantity"
                  type="number" min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64 bg-white"
                  value={quantity}
                  onChange={handleQuantity}
                />
                <div className="mb-8"></div>
                <button 
                  className="bg-blue-200 border-black border-2 text-black font-bold px-8 text-xl rounded transition w-64"
                  type="button"
                  style={{ marginTop: 0 }}
                  >Save</button>
            </div>
          </div>
        </div>

        <div className="w-px bg-black mx-4" />

        <div className="w-1/5">
          <h2 className="text-2xl font-bold mb-2 text-black">Saved Tariffs</h2>
          <p className="text-black">Saved tariffs will appear here</p>
        </div>

      </div>
    </main>
    );
  }
