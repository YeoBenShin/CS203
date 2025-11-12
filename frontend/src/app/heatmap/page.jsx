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
            Explore tariff rates worldwide with our interactive visualisation.
            Discover which countries have the highest and lowest import duties at a glance.
          </p>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 text-center">Understanding the Heatmap</h2>

          <div className="grid md:grid-cols-2 gap-6 items-start">
            <div className="md:col-span-2">
              <div className="flex items-center justify-between text-sm text-gray-600 mb-2">
                <span className="font-medium text-gray-700">Low</span>
                <span className="text-xs text-gray-400">(relative)</span>
                <span className="font-medium text-gray-700">High</span>
              </div>

              <div className="h-6 rounded-md overflow-hidden border mb-2 border-gray-200">
                <div
                  className="h-full w-full"
                  style={{
                    background: "linear-gradient(90deg, #ffffff 0%, #ffefeb 25%, #fca5a5 50%, #f87171 75%, #dc2626 100%)"
                  }}
                />
              </div>

              <div className="flex items-center justify-between text-xs text-gray-500 mb-3">
                <span>0</span>
                <span>25</span>
                <span>50</span>
                <span>75</span>
                <span>100</span>
              </div>
            </div>
          </div>
          <p className="text-m text-gray-600 text-center">
            Colour intensity shows each country's relative tariff impact — how its tariffs compare with others.
            <br /> Darker shades represent countries with higher tariff impacts.
          </p>
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
