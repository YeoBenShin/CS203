import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white via-blue-50 to-blue-100">
      <section className="max-w-7xl mx-auto px-6 py-32 flex flex-col md:flex-row items-center justify-between">
        <div className="max-w-2xl text-center md:text-left md:flex-1">
          <h1 className="text-5xl md:text-6xl font-extrabold text-gray-900 leading-tight mb-6">
            Instantly Calculate <br />
            <span className="text-blue-600">US Import Duties</span> & Taxes
          </h1>
          <p className="text-gray-700 text-xl mb-8 leading-relaxed">
            Get an accurate estimate of your shipment&apos;s total landed cost.
            <br /> Avoid surprises and simplify international trade with Tariffy.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center md:justify-start">
            <Link href="/calculator" passHref>
              <button
                type="button"
                className="bg-blue-600 text-white font-bold px-10 py-4 text-xl rounded-lg shadow-lg transition-all hover:bg-blue-700 hover:shadow-xl hover:scale-105"
              >
                Calculate Now
              </button>
            </Link>
            <Link href="/heatmap" passHref>
              <button
                type="button"
                className="bg-white border-2 border-blue-600 text-blue-600 font-bold px-10 py-4 text-xl rounded-lg shadow transition-all hover:bg-blue-50 hover:shadow-lg"
              >
                View Heatmap
              </button>
            </Link>
          </div>
        </div>
        
        <div className="mt-12 md:mt-0 md:ml-12">
          <div className="relative">
            <div className="absolute inset-0 bg-blue-400 rounded-full blur-3xl opacity-20"></div>
            <div className="relative bg-white p-8 rounded-2xl shadow-2xl">
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
                  <span className="text-gray-700 font-medium">Real-time Tariff Data</span>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-blue-500 rounded-full animate-pulse"></div>
                  <span className="text-gray-700 font-medium">Instant Calculations</span>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-purple-500 rounded-full animate-pulse"></div>
                  <span className="text-gray-700 font-medium">Global Coverage</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-6 py-20">
        <div className="grid md:grid-cols-3 gap-8">
          <Link href="/calculator" className="block">
            <div className="bg-white p-8 rounded-xl shadow-lg hover:shadow-2xl transition-all hover:scale-105 cursor-pointer h-full">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
                <svg className="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-3">Quick Calculations</h3>
              <p className="text-gray-600">Calculate import duties and taxes in seconds with our intuitive calculator.</p>
            </div>
          </Link>

          <Link href="/heatmap" className="block">
            <div className="bg-white p-8 rounded-xl shadow-lg hover:shadow-2xl transition-all hover:scale-105 cursor-pointer h-full">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                <svg className="w-8 h-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-3">Global Heatmap</h3>
              <p className="text-gray-600">Visualize tariff rates worldwide with our interactive heat map.</p>
            </div>
          </Link>

          <Link href="/view-tariffs" className="block">
            <div className="bg-white p-8 rounded-xl shadow-lg hover:shadow-2xl transition-all hover:scale-105 cursor-pointer h-full">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mb-4">
                <svg className="w-8 h-8 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-3">Detailed Reports</h3>
              <p className="text-gray-600">Get comprehensive breakdowns of all applicable tariffs and fees.</p>
            </div>
          </Link>
        </div>
      </section>
    </main>
  );
}
