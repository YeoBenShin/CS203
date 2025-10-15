import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
} from "@tanstack/react-table";
import Button from "./Button";
import Cross from "./Cross";
import { useState, useMemo, useEffect, useRef } from "react";

export default function ReactTable({ columns, data, rowLevelFunction }) {
  const [globalFilter, setGlobalFilter] = useState("");
  const [columnFilters, setColumnFilters] = useState([]);

  // State for managing dropdown visibility and search
  const [dropdownOpen, setDropdownOpen] = useState({});
  const [filterSearch, setFilterSearch] = useState({});
  const dropdownRefs = useRef({});

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(), // Enables sorting
    getFilteredRowModel: getFilteredRowModel(), // Enables filtering
    onColumnFiltersChange: setColumnFilters, // Enable column filters
    onGlobalFilterChange: setGlobalFilter, // For global search
    globalFilterFn: "includesString", // Default global filter (case-insensitive includes)
    initialState: {
      pagination: { pageIndex: 0, pageSize: 10 },
      globalFilter: "",
      columnFilters: [],
    },
    state: { globalFilter, columnFilters },
  });

  // Compute unique values only for columns that have checkbox filtering enabled
  const uniqueValues = useMemo(() => {
    const uniques = {};
    table.getAllColumns().forEach(column => {
      if (column.columnDef.enableColumnFilter && column.columnDef.accessorKey) {
        const values = data.map(row => row[column.columnDef.accessorKey]).filter(val => val != null);
        uniques[column.id] = [...new Set(values)]; // Unique values for checkbox columns
      }
    });
    return uniques;
  }, [data, table.getAllColumns()]);
  // console.log("Unique Values:", uniqueValues);

  // Function to toggle dropdown
  const toggleDropdown = (columnId) => {
    setDropdownOpen(prev => ({ ...prev, [columnId]: !prev[columnId] }));
  };

  // Function to close dropdown
  const closeDropdown = (columnId) => {
    setDropdownOpen(prev => ({ ...prev, [columnId]: false }));
  };

  // Function to clear all filters for a column
  const clearColFilter = (column) => {
    column.setFilterValue(undefined);
    setFilterSearch(prev => ({ ...prev, [column.id]: "" }));
  };

  const clearSearch = () => {
    setGlobalFilter("");
  };

  // Function to clear all filters globally
  const clearAllFilters = () => {
    setGlobalFilter("");
    setColumnFilters([]);
    setFilterSearch({});
  };

  // Effect to handle outside click and focus loss
  useEffect(() => {
    const handleClickOutside = (event) => {
      // console.log("Click outside event:", event);
      Object.keys(dropdownRefs.current).forEach(columnId => {
        if (dropdownRefs.current[columnId] && !dropdownRefs.current[columnId].contains(event.target)) {
          closeDropdown(columnId);
        }
      });
    };
    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div className="space-y-6 p-6 bg-gray-50 min-h-screen">
      {/* Header Section */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
          {/* Search Bar */}
          <div className="relative max-w-md flex-1">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              type="text"
              value={globalFilter}
              onChange={(e) => { setGlobalFilter(e.target.value) }}
              placeholder="Search across all columns..."
              className="block w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
            />
            {globalFilter && <Cross onClick={clearSearch} />}
          </div>

          {/* Filter Status and Clear Button */}
          {(globalFilter || columnFilters.length > 0) && (
            <div className="flex items-center justify-between lg:justify-end space-x-4">
              <div className="text-sm text-gray-600 bg-blue-50 px-3 py-2 rounded-lg border border-blue-200">
                <span className="font-medium text-blue-700">
                  {table.getFilteredRowModel().rows.length}
                </span>
                <span className="text-gray-600"> of </span>
                <span className="font-medium">
                  {data.length}
                </span>
                <span className="text-gray-600"> results</span>
              </div>

              <Button
                onClick={clearAllFilters}
                width=""
                colorBg="bg-red-500 hover:bg-red-600 focus:ring-red-500"
              >
                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
                Clear All Filters
              </Button>
            </div>
          )}
        </div>
      </div>

      {/* Table Section */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-visible">
        <div className="overflow-visible">
          <table className="w-full divide-y divide-gray-200">
            <thead className="bg-gradient-to-r from-gray-50 to-gray-100">
              {table.getHeaderGroups().map(headerGroup => (
                <tr key={headerGroup.id}>
                  {headerGroup.headers.map((header, headerIndex) => (
                    <th
                      key={header.id}
                      className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider border-b border-gray-200 relative"
                    >
                      <div className="flex flex-col space-y-3">
                        {/* Header with sorting */}
                        <div
                          className={`${header.column.columnDef.enableSorting ? 'cursor-pointer hover:text-blue-600' : ''} select-none flex items-center space-x-2 transition-colors duration-200`}
                          onClick={header.column.getToggleSortingHandler()}
                        >
                          <span>{flexRender(header.column.columnDef.header, header.getContext())}</span>
                          {header.column.columnDef.enableSorting && (
                            <div className="flex items-center">
                              {header.column.getIsSorted() === "asc" && (
                                <svg className="w-4 h-4 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                                  <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                                </svg>
                              )}
                              {header.column.getIsSorted() === "desc" && (
                                <svg className="w-4 h-4 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                                  <path d="M14.707 12.707a1 1 0 01-1.414 0L10 9.414l-3.293 3.293a1 1 0 01-1.414-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 010 1.414z" />
                                </svg>
                              )}
                              {header.column.getIsSorted() === false && (
                                <svg className="w-4 h-4 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                                  <path d="M5 12a1 1 0 102 0V6.414l1.293 1.293a1 1 0 001.414-1.414l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L5 6.414V12zM15 8a1 1 0 10-2 0v5.586l-1.293-1.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L15 13.586V8z" />
                                </svg>
                              )}
                            </div>
                          )}
                        </div>

                        {/* Filters */}
                        {header.column.columnDef.enableColumnFilter ? (
                          <div className="relative" ref={el => dropdownRefs.current[header.column.id] = el}>
                            <button
                              onClick={() => toggleDropdown(header.column.id)}
                              className={`w-full py-2 px-3 text-xs font-medium border rounded-lg cursor-pointer transition-all duration-200 flex items-center justify-between ${header.column.getFilterValue()
                                ? 'bg-blue-50 border-blue-300 text-blue-700 hover:bg-blue-100'
                                : 'bg-white border-gray-300 text-gray-600 hover:bg-gray-50'
                                }`}
                            >
                              <span>Filter {header.column.columnDef.header.substring(0, 11)}{header.column.columnDef.header.length > 11 ? "..." : ""}</span>
                              <div className="flex items-center space-x-1">
                                {header.column.getFilterValue() && header.column.getFilterValue().length > 0 && (
                                  <span className="bg-blue-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                                    {header.column.getFilterValue().length}
                                  </span>
                                )}
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
                                </svg>
                              </div>
                            </button>
                            {dropdownOpen[header.column.id] && (
                              <div
                                className={`absolute mt-2 w-64 bg-white border border-gray-200 rounded-lg shadow-xl z-[9999] p-4`}
                              >
                                <div className="space-y-3">
                                  <div className="space-y-3">
                                    {/* Search input */}
                                    <div className="relative">
                                      <svg className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                      </svg>
                                      <input
                                        type="text"
                                        value={filterSearch[header.column.id] || ""}
                                        onChange={(e) => setFilterSearch(prev => ({ ...prev, [header.column.id]: e.target.value }))}
                                        placeholder="Search countries..."
                                        className="w-full pl-10 pr-3 py-2 text-s border border-gray-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
                                      />
                                      {filterSearch[header.column.id] && (<Cross onClick={() => setFilterSearch(prev => ({ ...prev, [header.column.id]: "" }))} />)}
                                    </div>

                                    {/* Clear button */}
                                    <button
                                      onClick={() => clearColFilter(header.column)}
                                      disabled={!header.column.getFilterValue()}
                                      className={`w-full py-2 text-s font-medium border rounded-lg transition-colors duration-200 ${header.column.getFilterValue()
                                        ? 'bg-red-50 border-red-300 text-red-700 hover:bg-red-100'
                                        : 'bg-gray-50 border-gray-300 text-gray-400 cursor-not-allowed'
                                        }`}
                                    >
                                      Clear Selection
                                    </button>
                                  </div>

                                  {/* Options list */}
                                  <div className="flex-1 overflow-scroll max-h-96">
                                    <div className="border border-gray-200 rounded-lg">
                                      {uniqueValues[header.column.id]?.filter(value =>
                                        !filterSearch[header.column.id] || value.toLowerCase().includes(filterSearch[header.column.id].toLowerCase())
                                      ).sort((a, b) => {
                                        const aChecked = header.column.getFilterValue()?.includes(a) || false;
                                        const bChecked = header.column.getFilterValue()?.includes(b) || false;
                                        if (aChecked && !bChecked) return -1;
                                        if (!aChecked && bChecked) return 1;
                                        return a.localeCompare(b);
                                      }).map(value => (
                                        <label key={value} className="flex items-center p-3 hover:bg-gray-50 cursor-pointer transition-colors duration-150 border-b border-gray-100 last:border-b-0">
                                          <input
                                            type="checkbox"
                                            checked={header.column.getFilterValue()?.includes(value) || false}
                                            onChange={(e) => {
                                              const currentFilters = header.column.getFilterValue() || [];
                                              const newFilters = e.target.checked
                                                ? [...currentFilters, value]
                                                : currentFilters.filter(v => v !== value);
                                              header.column.setFilterValue(newFilters.length > 0 ? newFilters : undefined);
                                            }}
                                            className="w-3 h-3 text-blue-600 border-gray-300 rounded focus:ring-blue-500 focus:ring-1"
                                          />
                                          <span className="ml-3 text-s text-gray-700">{value}</span>
                                        </label>
                                      ))}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            )}
                          </div>
                        ) : (
                          <div className="relative">
                            <input
                              type="text"
                              value={header.column.getFilterValue() || ""}
                              onChange={(e) => header.column.setFilterValue(e.target.value)}
                              placeholder={`Search ${header.column.columnDef.header}...`}
                              className="w-full px-3 py-2 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                            />
                            {header.column.getFilterValue() && <Cross onClick={() => header.column.setFilterValue("")} />}
                          </div>
                        )}
                      </div>
                    </th>
                  ))}
                </tr>
              ))}
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {table.getRowModel().rows.map((row, index) => (
                <tr
                  key={row.id}
                  className={`transition-colors duration-150 ${rowLevelFunction ? 'cursor-pointer hover:bg-blue-100' : ''
                    } ${index % 2 === 0 ? 'bg-white' : 'bg-gray-100'}`}
                  onClick={rowLevelFunction ? () => rowLevelFunction(row.original) : null}
                >
                  {row.getVisibleCells().map(cell => (
                    <td key={cell.id} className="px-6 py-4 text-sm text-gray-700 border-b border-gray-100">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination Controls */}
        <div className="bg-gray-50 border-t border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Button
                onClick={table.previousPage}
                isLoading={!table.getCanPreviousPage()}
                width=""
                textConfig="text-sm text-gray-700 font-medium"
                colorBg={table.getCanPreviousPage() ? "bg-white hover:bg-gray-50 border border-gray-300 text-gray-700" : "bg-gray-100 text-gray-400 cursor-not-allowed"}
              >
                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
                </svg>
                Previous
              </Button>
              <Button
                onClick={() => { table.nextPage }}
                isLoading={!table.getCanNextPage()}
                width=""
                textConfig="text-sm text-gray-700 font-medium"
                colorBg={table.getCanNextPage() ? "bg-white hover:bg-gray-50 border border-gray-300 text-gray-700" : "bg-gray-100 text-gray-400 cursor-not-allowed"}
              >
                Next
                <svg className="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
                </svg>
              </Button>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-600">
                Page <span className="font-medium text-gray-900">{table.getState().pagination.pageIndex + 1}</span> of{" "}
                <span className="font-medium text-gray-900">{table.getPageCount()}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}