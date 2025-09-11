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


    return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">

        <div className="w-4/5 pr-6">
          <label className="text-xl text-black font-bold block mb-4">Tariff Calculator</label>
          <div className="flex items-center gap-8">
            <div className="flex flex-col">
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
                <button 
                  className="bg-blue-200 border border-black border-2 text-black font-bold px-8 text-xl rounded transition w-64"
                  type="button"
                  >Calculate</button>
            </div>

            <div className="flex flex-col">
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
