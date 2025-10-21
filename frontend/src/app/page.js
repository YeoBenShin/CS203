import Link from "next/link";
import dynamic from "next/dynamic";

const MapChart = dynamic(() => import("@/components/MapChart"), {
  ssr: false, // This line is important. It disables server-side rendering for the map.
  loading: () => <p>Loading map...</p> // Optional: show a loading message.
});

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
      <section className="max-w-7xl mx-auto px-6 py-20 flex flex-col md:flex-row items-center justify-between">
        <div className="max-w-xl text-center md:text-left md:flex-1">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 leading-tight mb-4">
            Instantly Calculate <br />
            US Import Duties & Taxes with Tariffy Easily
          </h1>
          <p className="text-gray-700 text-lg mb-6">
            Get an accurate estimate of the shipment&apos;s total landed cost.
            <br /> Avoid surprises and simplify international trade.
          </p>
          <Link href="/calculator" passHref>
            <button
              type="button"
              className="bg-white border border-black text-black font-bold px-8 py-4 text-xl rounded transition cursor-pointer hover:bg-gray-200"
            >
              Calculate Now
            </button>
          </Link>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-8">
          Global Tariff Hotspots
        </h2>
        <div className="border rounded-lg shadow-lg overflow-hidden">
          <MapChart />
        </div>
      </section>
    </main>
  );
}
