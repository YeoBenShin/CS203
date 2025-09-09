"use client";
import { useState } from "react";

export default function CalculatorPage() {
  const [hsCode, setHsCode] = useState("");
  const [countryOrigin, setCountryOrigin] = useState("");
  const [countryDest, setCountryDest] = useState("");

  const handleChange1 = (e) => setHsCode(e.target.value);
  const handleChange2 = (e) => setCountryOrigin(e.target.value);
  const handleChange3 = (e) => setCountryDest(e.target.value);

  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-50">
      <div className="flex justify-center items-start gap-8 p-8 min-h-screen w-full max-w-7xl mx-auto">

        {/* Left side: inputs */}
        <div className="flex-1">
          <label className="text-xl text-black font-bold block mb-4">Tariff Calculator</label>
          <div className="flex items-center gap-8">
            <div className="flex flex-col">
              <label className="font-bold mb-1 text-black" htmlFor="hsCode">Enter HS Code:</label>
              <input
                id="hsCode"
                type="text"
                className="text-black border border-black rounded px-2 py-1 w-40"
                value={hsCode}
                onChange={handleChange1}
              />
            </div>

            <div className="flex flex-col">
              <label className="font-bold mb-1 text-black" htmlFor="countryOrigin">Enter Country of Origin:</label>
              <input
                id="countryOrigin"
                type="text"
                className="text-black border border-black rounded px-2 py-1 w-64"
                value={countryOrigin}
                onChange={handleChange2}
              />
            </div>

            <div className="flex flex-col">
              <label className="font-bold mb-1 text-black" htmlFor="countryDest">Enter Country of Destination:</label>
              <input
                id="countryDest"
                type="text"
                className="text-black border border-black rounded px-2 py-1 w-64"
                value={countryDest}
                onChange={handleChange3}
              />
            </div>
          </div>
        </div>

        {/* Vertical black line */}
        <div className="w-px bg-black mx-6" style={{ height: '300px' }}></div>

        {/* Right side: saved tariffs */}
        <div className="flex flex-col items-end flex-1 max-w-sm">
          <h2 className="text-2xl font-bold mb-2 text-black">Saved Tariffs</h2>
          <p className="text-black">Saved tariffs will appear here  </p>
        </div>

      </div>
    </main>
  );
}
