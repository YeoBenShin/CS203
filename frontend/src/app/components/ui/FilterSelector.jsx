// options is an array of objects with {value, label}
// handlerFunction is a function that selects that object
const FilterSelector = ({options, handlerFunction}) => { 
    return (
        <div className="absolute z-10 w-full bg-white border border-gray-300 rounded-b max-h-48 overflow-y-auto shadow-lg">
            {options.map((option) => (
                <div
                    key={option.value}
                    className="px-3 py-2 hover:bg-blue-100 cursor-pointer"
                    onMouseDown={(e) => e.preventDefault()} // Prevent blur on click
                    onClick={() => handlerFunction(option)}
                >
                    {option.label}
                </div>
            ))}
        </div>
    );
};

export default FilterSelector;