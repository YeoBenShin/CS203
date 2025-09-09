  "use client";
  import { useMemo, useState } from "react";
  import Select from 'react-select';
  import countryList from 'react-select-country-list';
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

    const handleHsCode = (e) => setHsCode(e.target.value);
    const handleProdCost = (e) => setProdCost(e.target.value);
    const handleSalePrice = (e) => setSalePrice(e.target.value);
    const handlePricingDate = (e) => setPricingDate(e.target.value);
    const handleQuantity = (e) => setQuantity(e.target.value);
    const changeHandler = value => {
      setValue(value)
    }

    return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-50">
      <div className="flex w-full min-h-screen max-w-7xl mx-auto p-8">

        <div className="w-4/5 pr-6">
          <label className="text-xl text-black font-bold block mb-4">Tariff Calculator</label>
          <div className="flex items-center gap-8">
            <div className="flex flex-col">
              <label className="font-bold mb-1 text-black" htmlFor="hsCode">Enter HS Code:</label>
              <input
                id="hsCode"
                type="text"
                className="text-black border border-black rounded px-2.5 py-1.5 w-64"
                value={hsCode}
                onChange={handleHsCode}
              />
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="prodCost">Current Product Cost:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input 
                  id="prodCost"
                  type="number"min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7"
                  value={prodCost}
                  onChange={handleProdCost}
                />
                </div>
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="pricingDate">Pricing Date:</label>
              <input 
                id="pricingDate"
                type="date"
                className="text-black border border-black rounded px-2.5 py-1.5 w-64"
                value={pricingDate}
                onChange={handlePricingDate}
              />  
            </div>

            <div className="flex flex-col">
              <label className="font-bold mb-1 text-black" htmlFor="countryOrigin">Enter Country of Origin:</label>
              <Select 
                id="countryOrigin"
                className="w-64 text-black border border-black rounded"
                options={options}
                value={value}
                onChange={changeHandler}
              />
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="salePrice">Current Sale Price:</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                <input 
                  id="salePrice"
                  type="number" min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64 pl-7"
                  value={salePrice}
                  onChange={handleSalePrice}
                />
              </div>
              <div className="mb-8"></div>
              <label className="font-bold mb-1 text-black" htmlFor="quantity">Quantity:</label>
              <input 
                  id="quantity"
                  type="number" min={0}
                  className="text-black border border-black rounded px-2.5 py-1.5 w-64"
                  value={quantity}
                  onChange={handleQuantity}
                />
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
