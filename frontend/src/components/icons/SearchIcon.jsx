const SearchIcon = ({
    className= "h-5 w-5"
}) => {
    return (
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg className={`${className} text-gray-400`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
        </div>
    )
}

export default SearchIcon;