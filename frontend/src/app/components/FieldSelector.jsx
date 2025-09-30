import dynamic from "next/dynamic";

const Selector = dynamic(() => import("react-select"), { 
  ssr: false,
  loading: () => <div className="h-9 bg-gray-100 border rounded animate-pulse"></div>
});

export default function FieldSelector({ options, value, onChange, placeholder}) {
  return (
    <Selector
      options={options}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      className="text-blue"
      isClearable
    />
  );
}
