"use client";
import ClientHeatMap from "@/components/ClientHeatMap";

export default function HeatmapPage() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white via-blue-50 to-blue-100">
      <div className="max-w-[1800px] mx-auto px-6 py-12">
        {/* Header Section */}
        <div className="text-center mb-10">
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">
            Global Tariff <span className="text-blue-600">Heatmap</span>
          </h1>
          <p className="text-xl text-gray-700 max-w-3xl mx-auto leading-relaxed">
            Explore tariff rates worldwide with our interactive visualization. 
            Discover which countries have the highest and lowest import duties at a glance.
          </p>
        </div>

        {/* Legend Section */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 text-center">Understanding the Heatmap</h2>
          <div className="flex flex-wrap justify-center items-center gap-6">
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-white rounded border-2 border-gray-300"></div>
              <span className="text-gray-700 font-medium">Low Tariff Rates (0-5%)</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 rounded border border-red-300" style={{ backgroundColor: '#fca5a5' }}></div>
              <span className="text-gray-700 font-medium">Medium Tariff Rates (5-15%)</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 rounded border border-red-500" style={{ backgroundColor: '#f87171' }}></div>
              <span className="text-gray-700 font-medium">High Tariff Rates (15-30%)</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 rounded border border-red-700" style={{ backgroundColor: '#dc2626' }}></div>
              <span className="text-gray-700 font-medium">Very High Tariff Rates (30%+)</span>
            </div>
          </div>
        </div>

        {/* Heatmap Container */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 border border-gray-200">
          <div className="rounded-xl overflow-hidden">
            <ClientHeatMap />
          </div>
        </div>

        {/* Info Section */}
        <div className="mt-12 grid md:grid-cols-2 gap-8 max-w-6xl mx-auto">
          <div className="bg-white p-8 rounded-xl shadow-lg">
            <div className="flex items-start space-x-4">
              <div className="flex-shrink-0">
                <svg className="w-10 h-10 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900 mb-2">Interactive Map</h3>
                <p className="text-gray-600">
                  Click on any country to view detailed tariff information and explore trade relationships with the United States.
                </p>
              </div>
            </div>
          </div>

          <div className="bg-white p-8 rounded-xl shadow-lg">
            <div className="flex items-start space-x-4">
              <div className="flex-shrink-0">
                <svg className="w-10 h-10 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900 mb-2">Real-Time Data</h3>
                <p className="text-gray-600">
                  Our heatmap is updated with the latest tariff data to ensure you have accurate information for your trade decisions.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
