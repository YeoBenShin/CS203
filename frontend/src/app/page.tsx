import Image from "next/image";

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-50">
        <section className="max-w-7xl mx-auto px-6 py-20 flex flex-col md:flex-row items-center justify-between">
          {}
        </section>
        <div className="max-w-xl text-center md:text-left md:ml-10 md:flex-1">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 leading-tight mb-4">
            Instantly Calculate <br />
            Import Duties & Taxes with Tariffs Easily
          </h1>
          <p className="text-gray-700 text-lg mb-6">
            Get an accurate estimate of the shipment’s total landed cost.
            Avoid surprises and simplify international trade.
          </p>
        </div>
      
    </main>
  );
}
