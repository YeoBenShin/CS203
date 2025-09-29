import dynamic from "next/dynamic";

const FieldSelector = dynamic(() => import("react-select"), { 
  ssr: false,
  loading: () => <div className="h-9 bg-gray-100 border rounded animate-pulse"></div>
});

export default FieldSelector;
