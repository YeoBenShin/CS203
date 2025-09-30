import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  flexRender,
} from "@tanstack/react-table";
import Button from "./Button";

export default function ReactTable({ columns, data, rowLevelFunction }) {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    initialState: { pagination: { pageIndex: 0, pageSize: 10 } },
  });

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