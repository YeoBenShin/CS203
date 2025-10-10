"use client";
import React, { useState, useEffect } from "react";
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../../components/messages/SuccessMessageDisplay";
import ReactTable from "../../components/ReactTable";
import Button from "../../components/Button";
import PopUpWrapper from "../../components/popUps/PopUpWrapper";
import TariffDetailPopUp from "../../components/popUps/TariffDetailPopUp";
import DeleteTariffPopUp from "../../components/popUps/DeleteTariffPopUp";
import LoadingPage from "../../components/LoadingPage";

export default function ViewWatchListPage() {
    // loading tariffs
    const [tariffs, setTariffs] = useState([]);
    const [filteredTariffs, setFilteredTariffs] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [fetchingError, setFetchingError] = useState("");

    // clicking on a tariff to view details
    const [showDetailsPopup, setShowDetailsPopup] = useState(false);
    const [selectedTariff, setSelectedTariff] = useState(null);

    // deleting a tariff
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [tariffToDelete, setTariffToDelete] = useState(null);
    const [deleteMessage, setDeleteMessage] = useState("");

    // success popup
    const [showSuccessPopup, setShowSuccessPopup] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
        fetchTariffs();
        // Cleanup effect to restore scrolling when component unmounts
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, []);

    const fetchTariffs = async () => {
        try {
            setIsLoading(true);
            const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/watchlists`);
            if (response.ok) {
                const data = await response.json();
                setTariffs(data); // Store a copy of the original data
                setFilteredTariffs(data);
            } else {
                setFetchingError("Failed to fetch tariffs");
            }
        } catch (error) {
            setFetchingError(`Error: ${error.message}`);
        } finally {
            setIsLoading(false);
        }
    };

    // Search functionality
    const handleSearchChange = (e) => {
        const query = e.target.value.toLowerCase();
        setSearchQuery(query);

        if (query === "") {
            setFilteredTariffs(tariffs);
        } else {
            const filtered = tariffs.filter(tariff =>
                tariff.exporterName?.toLowerCase().includes(query) ||
                tariff.importerName?.toLowerCase().includes(query) ||
                tariff.HSCode?.toString().toLowerCase().includes(query) ||
                tariff.productDescription?.toLowerCase().includes(query) ||
                (parseFloat(tariff.rate) * 100).toFixed(2).includes(query)
            );
            setFilteredTariffs(filtered);
        }
    };

    const clearSearch = () => {
        setSearchQuery("");
        setFilteredTariffs(tariffs);
    };

    const handleShowDetails = (tariff) => {
        setSelectedTariff(tariff);
        setShowDetailsPopup(true);
        // Prevent scrolling on the body when popup is open
        document.body.style.overflow = 'hidden';
    };

    const closeDetailsPopup = () => {
        setShowDetailsPopup(false);
        setSelectedTariff(null);
        // Restore scrolling when popup is closed
        document.body.style.overflow = 'unset';
    };

    const handleDelete = () => {
        setTariffToDelete(selectedTariff);
        setShowDeletePopup(true);
        document.body.style.overflow = 'hidden';
    };

    const confirmDelete = async () => {
        if (!tariffToDelete) {
            setDeleteMessage("No tariff selected for deletion");
            return;
        }

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/watchlists/${tariffToDelete.watchlistID}`, {
                method: "DELETE",
            });

            if (response.ok) {
                const updatedTariffs = tariffs.filter(tariff => tariff.tariffID !== tariffToDelete.tariffID);
                setTariffs(updatedTariffs);
                // Update filtered tariffs to reflect the deletion
                setFilteredTariffs(filteredTariffs.filter(tariff => tariff.tariffID !== tariffToDelete.tariffID));
                showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Tariff removed from watchlist successfully!");

            } else {
                const errorData = await response.json();
                setDeleteMessage(`Error: ${errorData.message || "Failed to delete tariff"}`);
            }
        } catch (error) {
            setDeleteMessage(`Error: ${error.message}`);
        } finally {
            cancelDelete();
        }
    };

    const cancelDelete = () => {
        setShowDeletePopup(false);
        setTariffToDelete(null);
        document.body.style.overflow = 'unset';
    };

    if (isLoading) {
        return <LoadingPage />;
    }

    return (
        <div className="min-h-screen py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-7xl mx-auto">
                <div className="mb-8">
                    <h1 className="text-3xl font-extrabold text-gray-900">Tariff Watchlist</h1>
                    <p className="mt-2 text-sm text-gray-600"> View all your saved tariffs</p>
                    <p className="mt-1 text-sm text-gray-600"> Click on a tariff to view more details</p>
                </div>

                {/* Search Bar */}
                <div className="mb-6">
                    <div className="relative max-w-md">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                        <input
                            type="text"
                            value={searchQuery}
                            onChange={handleSearchChange}
                            placeholder="Search tariffs by country, HS code, product, or rate..."
                            className="block w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
                        />
                        {searchQuery && (
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
                        )}
                    </div>
                    {searchQuery && (
                        <p className="mt-2 text-sm text-gray-600">
                            Showing {filteredTariffs.length} of {tariffs.length} tariffs
                        </p>
                    )}
                </div>

                {fetchingError && (
                    <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-md">
                        {fetchingError}
                    </div>
                )}

                {filteredTariffs.length === 0 ? (
                    <div className="text-center py-12">
                        {searchQuery ? (
                            <div>
                                <p className="text-gray-500 text-lg">No tariffs found matching "{searchQuery}"</p>
                                <button
                                    onClick={clearSearch}
                                    className="mt-2 text-blue-600 hover:text-blue-800 text-sm underline"
                                >
                                    Clear search to show all tariffs
                                </button>
                            </div>
                        ) : (
                            <p className="text-gray-500 text-lg">No tariffs found</p>
                        )}
                    </div>
                ) : (
                    <ReactTable
                        columns={[
                            {
                                header: "Exporting Country", accessorKey: "exporterName",
                                cell: info => <span className="text-gray-900 font-medium">{info.getValue() || "N/A"}</span>
                            },
                            { header: "Destination Country", accessorKey: "importerName" },
                            { header: "HS Code", accessorKey: "HSCode" },
                            {
                                header: "Product Description", accessorKey: "productDescription",
                                cell: info => info.getValue() ? (info.getValue().length > 40 ? info.getValue().substring(0, 40) + "..." : info.getValue()) : "N/A"
                            },
                            {
                                header: "Rate (%)", accessorKey: "rate",
                                cell: info => <span className="text-gray-900 font-medium">{(parseFloat(info.getValue()) * 100).toFixed(2)}%</span>
                            },
                        ]}
                        data={filteredTariffs}
                        rowLevelFunction={handleShowDetails}
                    />
                )}

                <div className="mt-6 text-center">
                    <Button
                        onClick={fetchTariffs}
                        isLoading={isLoading}
                        className="inline-flex items-center"
                        width=""
                    >
                        Refresh
                    </Button>
                </div>

                {/* Success Popup */}
                {showSuccessPopup && <SuccessMessageDisplay successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}

                {/* Tariff Details Popup */}
                {showDetailsPopup && selectedTariff && (
                    <PopUpWrapper OnClick={closeDetailsPopup}>
                        <TariffDetailPopUp
                            selectedTariff={selectedTariff}
                            OnClick={closeDetailsPopup}
                            openDeletePopup={handleDelete}
                            hasPermissionToDelete={true}
                        />
                        {deleteMessage && deleteMessage.includes("Error") && (
                            <div className="mb-4 p-4 rounded-md bg-red-100 text-red-700">
                                {deleteMessage}
                            </div>
                        )}
                    </PopUpWrapper>
                )}

                {/* Delete Confirmation Popup */}
                {showDeletePopup && tariffToDelete && (
                    <PopUpWrapper OnClick={cancelDelete}>
                        <DeleteTariffPopUp
                            tariffToDelete={tariffToDelete}
                            handleCancelDelete={cancelDelete}
                            handleConfirmDelete={confirmDelete}
                        />
                    </PopUpWrapper>
                )}


            </div>
        </div>
    );
}