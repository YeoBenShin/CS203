import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
} from "@tanstack/react-table";
import Button from "./Button";
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
  const clearAllFilters = (column) => {
    column.setFilterValue(undefined);
    setFilterSearch(prev => ({ ...prev, [column.id]: "" }));
  };

  const clearSearch = () => {
    setGlobalFilter("");
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
    <div>
      {/* Search Bar */}
      <div className="relative max-w-md mb-4" >
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <input
          type="text"
          value={globalFilter}
          onChange={(e) => { setGlobalFilter(e.target.value) }}
          placeholder="Global search across all columns..."
          className="block w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
        />
        {
          globalFilter && (
            <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
              <button
                onClick={clearSearch}
                className="text-gray-400 hover:text-gray-600"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          )
        }
      </div >
      {globalFilter && (
        <div className="mb-4 text-sm text-gray-600">
          Showing {table.getFilteredRowModel().rows.length} of {data.length} tariffs
        </div>
      )}

      {/* Table */}
      < div className="bg-white shadow overflow-auto sm:rounded-md" >
        < table className="w-full divide-y divide-gray-200" >
          <thead className="bg-gray-50">
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th
                    key={header.id}
                    className="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider"
                  >
                    {/* Custom header with sorting and optional filter dropdown */}
                    <div className="flex flex-col justify-between">
                      <div
                        className={`${header.column.columnDef.enableSorting ? 'cursor-pointer' : ''} select-none flex items-center`}
                        onClick={header.column.getToggleSortingHandler()}
                      >
                        {flexRender(header.column.columnDef.header, header.getContext())}
                        {header.column.columnDef.enableSorting && (
                          <div>
                            {header.column.getIsSorted() === "asc" && <span>&nbsp;🔼</span>}
                            {header.column.getIsSorted() === "desc" && <span>&nbsp;🔽</span>}
                            {header.column.getIsSorted() === false && <span>&nbsp;↕️</span>}
                          </div>
                        )}

                      </div>
                      {/* Checkbox dropdown only for enabled columns */}
                      {header.column.columnDef.enableColumnFilter ? (
                        <div className="relative" ref={el => dropdownRefs.current[header.column.id] = el}>
                          <button
                            onClick={() => toggleDropdown(header.column.id)}
                            className={`mt-2 w-full py-1 text-xs border rounded cursor-pointer items-center  ${header.column.getFilterValue() ? 'bg-green-100 hover:bg-blue-100' : 'hover:bg-gray-100'}`}
                          >
                            Filter Country
                          </button>
                          {dropdownOpen[header.column.id] && (
                            <div className="absolute right-0 mt-1 w-48 bg-white border rounded-md shadow-lg z-10 p-2 max-h-60 overflow-y-auto">
                              <input
                                type="text"
                                value={filterSearch[header.column.id] || ""}
                                onChange={(e) => setFilterSearch(prev => ({ ...prev, [header.column.id]: e.target.value }))}
                                placeholder="Search countries..."
                                className="w-full px-2 py-1 text-xs border rounded mb-2"
                              />
                              <button
                                onClick={() => clearAllFilters(header.column)}
                                className="w-full py-1 text-xs bg-red-100 hover:bg-red-200 border rounded mb-2"
                              >
                                Clear All
                              </button>
                              {uniqueValues[header.column.id]?.filter(value => 
                                !filterSearch[header.column.id] || value.toLowerCase().includes(filterSearch[header.column.id].toLowerCase())
                              ).sort((a, b) => {
                                const aChecked = header.column.getFilterValue()?.includes(a) || false;
                                const bChecked = header.column.getFilterValue()?.includes(b) || false;
                                if (aChecked && !bChecked) return -1;
                                if (!aChecked && bChecked) return 1;
                                return a.localeCompare(b);
                              }).map(value => (
                                <label key={value} className="flex items-center px-2 hover:bg-gray-50">
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
                                    className="mr-2"
                                  />
                                  <span className="px-2 text-sm">{value}</span>
                                </label>
                              ))}
                            </div>
                          )}
                        </div>
                      ) : (
                        <div>
                          <input
                            type="text"
                            value={header.column.getFilterValue() || ""}
                            onChange={(e) => header.column.setFilterValue(e.target.value)}
                            placeholder={`Search ${header.column.columnDef.header}...`}
                            className="mt-2 w-full px-2 py-1 text-xs border rounded"
                          />
                        </div>
                      )}
                    </div>
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {table.getRowModel().rows.map(row => (
              <tr key={row.id} className="hover:bg-gray-100 cursor-pointer" onClick={rowLevelFunction ? () => rowLevelFunction(row.original) : null}>
                {row.getVisibleCells().map(cell => (
                  <td key={cell.id} className="px-4 py-4 text-sm text-gray-500">
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table >

        {/* Pagination Controls */}
        < div className="flex justify-between p-4" >
          <Button
            onClick={table.previousPage}
            isLoading={!table.getCanPreviousPage()}
            width=""
          >
            {!table.getCanPreviousPage() ? "No Previous" : "Previous"}
          </Button>
          <span>
            Page {table.getState().pagination.pageIndex + 1} of {table.getPageCount()}
          </span>
          <Button
            onClick={table.nextPage}
            isLoading={!table.getCanNextPage()}
            width=""
          >
            {!table.getCanNextPage() ? "No More Next" : "Next"}
          </Button>
        </div >
      </div >
    </div>
  );
}