import { formatRate, formatUnitOfCalculation } from "@/utils/formatDisplayHelpers";

export default function TariffDetailPopUp({
    selectedTariff,
    OnClick,
    openEditPopup = () => { },
    openDeletePopup = () => { },
    hasPermissionToEdit = false,
    hasPermissionToDelete = false,
}) {
    const formatDate = (dateString) => {
        if (!dateString) return "N/A";
        return new Date(dateString).toLocaleDateString();
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-medium text-gray-900">Tariff Details</h3>
                <button
                    onClick={OnClick}
                    className="text-gray-400 hover:text-gray-500"
                >
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Exporting Country</label>
                        <p className="text-sm text-gray-900">{selectedTariff.exporterName}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Destination Country</label>
                        <p className="text-sm text-gray-900">{selectedTariff.importerName}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">HS Code</label>
                        <p className="text-sm text-gray-900">{selectedTariff.hSCode}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-gray-700">Tariff Rates:</label>
                        {selectedTariff.tariffRates.map((tariffRate, index) => (
                            <div key={index} className="text-sm font-medium text-gray-700">
                                {tariffRate.unitOfCalculation === "AV" ? `Rate ${index + 1}: ${formatRate(tariffRate.rate)}` :
                                    `Rate ${index + 1}: $${tariffRate.rate.toFixed(2)}${formatUnitOfCalculation(tariffRate.unitOfCalculation)}`}
                            </div>
                        ))}
                    </div>
                </div>

                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Effective Date</label>
                        <p className="text-sm text-gray-900">{formatDate(selectedTariff.effectiveDate)}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Expiry Date</label>
                        <p className="text-sm text-gray-900">{formatDate(selectedTariff.expiryDate)}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Reference</label>
                        <p className="text-sm text-gray-900 break-all">{selectedTariff.reference || "N/A"}</p>
                    </div>

                    <div className="md:col-span-2">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Product Description</label>
                        <p className="text-sm text-gray-900">{selectedTariff.productDescription || "N/A"}</p>
                    </div>
                </div>


            </div>

            {(hasPermissionToEdit || hasPermissionToDelete) && <div className="flex justify-end space-x-3 mt-6 pt-4 border-t border-gray-200">
                {hasPermissionToEdit && <button
                    onClick={() => {
                        OnClick();
                        openEditPopup();
                    }}
                    className="px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-md transition-colors cursor-pointer"
                >
                    Update Tariff
                </button>}
                {hasPermissionToDelete && <button
                    onClick={() => {
                        OnClick();
                        openDeletePopup();
                    }}
                    className="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md transition-colors cursor-pointer"
                >
                    Delete Tariff
                </button>}
            </div>}
        </div>
    )
}