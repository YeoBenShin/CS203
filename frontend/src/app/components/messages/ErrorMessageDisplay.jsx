export default function ErrorDisplay({ errors, title = "Please fix the following issues:" }) {
  if (!errors || errors.length === 0) {
    return null;
  }

  return (
    <div className="mt-4 mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
      <div className="font-bold mb-2">{title}</div>
      <ul className="list-disc list-inside space-y-1">
        {errors.map((error, index) => (
          <li key={index} className="text-sm">{error}</li>
        ))}
      </ul>
    </div>
  );
};