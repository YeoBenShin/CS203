export default function CalculatorPage() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-white to-blue-50">
      <div className="flex justify-center items-start gap-8 p-8 min-h-screen">
      <div>
        <h2 className="text-2xl font-bold mb-2 text-black">"Left"</h2>
        <p className="text-black">This is the left column</p>
      </div>

      <div className="w-px bg-gray-400 self-stretch my-8"></div>

      <div>
        <h2 className="text-2xl font-bold mb-2 text-black">"Right"</h2>
          <p className="text-black">This is the right column</p>
      </div>
      <footer className="row-start-3 flex gap-[24px] flex-wrap items-center justify-center">
      </footer>
      </div>
    </main>
  );
}
