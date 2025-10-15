const Cross = ({
    onClick
}) => {
    return (
        <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
            <button
                onClick={onClick}
                className="text-gray-400 cursor-pointer hover:text-gray-600 transition-colors duration-200"
            >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
        </div>
    )
}

export default Cross;