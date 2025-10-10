import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
} from "@tanstack/react-table";
import Button from "./Button";
import { useEffect } from "react";

export default function ReactTable({ columns, data, rowLevelFunction, globalFilter, setGlobalFilter, onFilteredCountChange}) {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(), // Enables sorting
    getFilteredRowModel: getFilteredRowModel(), // Enables filtering
    onGlobalFilterChange: setGlobalFilter, // For global search
    globalFilterFn: "includesString", // Default global filter (case-insensitive includes)
    initialState: {
      pagination: { pageIndex: 0, pageSize: 10 },
      globalFilter: "",
    },
    state: { globalFilter }, 
  });

  // Expose the filtered row count to the parent component
  // useEffect(() => {
  //   if (onFilteredCountChange) {
  //     onFilteredCountChange(table.getFilteredRowModel().rows.length);
  //   }
  // }, [table.getFilteredRowModel().rows.length, onFilteredCountChange]);


return (
    <div className="bg-white shadow overflow-hidden sm:rounded-md">
      <table className="w-full divide-y divide-gray-200 ">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map(headerGroup => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map(header => (
                <th
                  key={header.id}
                  className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  {flexRender(header.column.columnDef.header, header.getContext())}
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
      </table>

      {/* Pagination Controls */}
      <div className="flex justify-between p-4">
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
      </div>
    </div>
  );
}