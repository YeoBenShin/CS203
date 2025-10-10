import { formatRate, formatUnitOfCalculation } from "@/utils/formatDisplayHelpers";

export default function DeleteTariffPopUp({
    tariffToDelete,
    handleCancelDelete,
    handleConfirmDelete
}) {
    const formatDate = (dateString) => {
        if (!dateString) return "N/A";
        return new Date(dateString).toLocaleDateString();
    };

    return (
        <div>
            <div className="flex items-center mb-4">
                <div className="flex-shrink-0">
                    <svg className="h-6 w-6 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                    </svg>
                </div>
                <div className="ml-3">
                    <h3 className="text-lg font-medium text-gray-900">Delete Tariff</h3>
                </div>
            </div>

            <div className="mb-6">
                <p className="text-sm text-gray-500 mb-3">
                    Are you sure you want to delete this tariff?
                    <br /> This action cannot be undone.
                </p>
                <div className="bg-gray-50 p-3 rounded-md grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <p className="text-sm font-medium text-gray-900">
                            {tariffToDelete.exporterName} → {tariffToDelete.importerName}
                        </p>
                        {tariffToDelete.tariffRates.map((tariffRate, index) => (
                            <p key={index} className="text-sm font-medium text-gray-900">
                                {tariffRate.unitOfCalculation === "AV" ? `Rate ${index + 1}: ${formatRate(tariffRate.rate)}` :
                                    `Rate ${index + 1}: $${tariffRate.rate.toFixed(2)}${formatUnitOfCalculation(tariffRate.unitOfCalculation)}`}
                            </p>
                        ))}
                        
                    </div>

                    <div className="space-y-2">
                        
                        <p className="text-sm font-medium text-gray-900">
                            Product: {tariffToDelete.productDescription} ({tariffToDelete.hSCode})
                        </p>
                        <p className="text-sm font-medium text-gray-900">
                            Effective Date: {formatDate(tariffToDelete.effectiveDate)}
                        </p>
                        <p className="text-sm font-medium text-gray-900">
                            Expiry Date: {formatDate(tariffToDelete.expiryDate)}
                        </p>
                    </div>

                </div>
            </div>

            <div className="flex justify-end space-x-3">
                <button
                    onClick={handleCancelDelete}
                    className="cursor-pointer px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md transition-colors"
                >
                    Cancel
                </button>
                <button
                    onClick={handleConfirmDelete}
                    className="cursor-pointer px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md transition-colors"
                >
                    Delete Tariff
                </button>
            </div>
        </div>
    )
}
