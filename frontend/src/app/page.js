import Link from "next/link"

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-400">
        <section className="max-w-7xl mx-auto px-6 py-20 flex flex-col md:flex-row items-center justify-between">
          {}
        </section>
        <div className="max-w-xl text-center md:text-left md:ml-10 md:flex-1">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 leading-tight mb-4">
            Instantly Calculate <br />
            US Import Duties & Taxes with Tariffy Easily
          </h1>
          <p className="text-gray-700 text-lg mb-6">
            Get an accurate estimate of the shipment's total landed cost.
            <br /> Avoid surprises and simplify international trade.
          </p>
        </div>

        <Link href="/calculator" passHref>
          <button
            type="button"
            className="md:ml-10 bg-white border border-black text-black font-bold px-8 py-4 text-xl rounded transition cursor-pointer hover:bg-gray-100"
            >Calculate Now</button>
        </Link>
    </main>
  );
}
