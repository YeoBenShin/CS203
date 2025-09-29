"use client";
import React, { useState, useEffect } from "react";
import { useUser } from '@clerk/nextjs';
import { SuccessMessage, showSuccessPopupMessage } from "../components/SuccessMessage";

export default function ViewTariffsPage() {

  // loading tariffs
  const [tariffs, setTariffs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [fetchingError, setFetchingError] = useState("");

  // clicking on a tariff to view details
  const [showDetailsPopup, setShowDetailsPopup] = useState(false);
  const [selectedTariff, setSelectedTariff] = useState(null);

  // deleting a tariff
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [tariffToDelete, setTariffToDelete] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  // editing a tariff
  const [showEditPopup, setShowEditPopup] = useState(false);
  const [tariffToEdit, setTariffToEdit] = useState(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const [editErrors, setEditErrors] = useState([]);
  const [editForm, setEditForm] = useState({
    rate: "",
    effectiveDate: "",
    expiryDate: "",
    reference: ""
  });

  // success popup
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");


  // user role
  const { user } = useUser();
  const role = user?.publicMetadata?.role || "user";

  useEffect(() => {
    fetchTariffs();
    // Cleanup effect to restore scrolling when component unmounts
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, []);

  const fetchTariffs = async () => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs`);
      if (response.ok) {
        const data = await response.json();
        console.log("Fetched tariffs:", data);
        setTariffs(data);
      } else {
        setFetchingError("Failed to fetch tariffs");
      }
    } catch (error) {
      setFetchingError(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleShowDetails = (tariff) => {
    setSelectedTariff(tariff);
    setTariffToEdit(tariff);
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
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${tariffToDelete.tariffID}`, {
        method: "DELETE",
      });

      if (response.ok) {
        setTariffs(tariffs.filter(tariff => tariff.tariffID !== tariffToDelete.tariffID));
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup,"Tariff deleted successfully!");

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

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateForInput = (dateString) => {
    if (!dateString) return "";
    return new Date(dateString).toISOString().split('T')[0];
  };

  const openEditPopup = () => {
    setEditForm({
      rate: (parseFloat(tariffToEdit.rate) * 100).toFixed(2), // Convert from decimal to percentage
      effectiveDate: formatDateForInput(tariffToEdit.effectiveDate),
      expiryDate: formatDateForInput(tariffToEdit.expiryDate),
      reference: tariffToEdit.reference || ""
    });
    setEditErrors([]);
    setShowEditPopup(true);
    document.body.style.overflow = 'hidden';
  };

  const handleCancelEdit = () => {
    setShowEditPopup(false);
    setTariffToEdit(null);
    setEditForm({
      rate: "",
      effectiveDate: "",
      expiryDate: "",
      reference: ""
    });
    setEditErrors([]); // Clear edit errors when canceling
    setIsUpdating(false);
    document.body.style.overflow = 'unset';
  };

  const handleEditFormChange = (e) => {
    setEditForm({ ...editForm, [e.target.name]: e.target.value });
    // Clear errors when user starts typing
    if (editErrors.length > 0) {
      setEditErrors([]);
    }
  };

  const validateEditForm = () => {
    const validationErrors = [];
    if (!/^\d+(\.\d{1,2})?$/.test(editForm.rate)) {
      validationErrors.push("Tariff rate cannot be negative");
    }

    // Check date logic
    if (editForm.effectiveDate && editForm.expiryDate) {
      const effectiveDate = new Date(editForm.effectiveDate);
      const expiryDate = new Date(editForm.expiryDate);
      if (expiryDate <= effectiveDate) {
        validationErrors.push("Expiry date must be after the effective date");
      }
    } else if (editForm.effectiveDate && tariffToEdit.expiryDate) { // past data must have expiry, else don't even need to compare
      const effectiveDate = new Date(editForm.effectiveDate);
      if (!tariffToEdit.expiryDate) {
        const currentExpiryDate = new Date(tariffToEdit.expiryDate);
        if (effectiveDate >= currentExpiryDate) {
          validationErrors.push("Effective date must be before the expiry date");
        }
      }
    }
    return validationErrors;
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setEditErrors([]);
    setIsUpdating(true);

    try {
      const validationErrors = validateEditForm();
      if (validationErrors.length > 0) {
        setEditErrors(validationErrors);
        return;
      }
      if (!tariffToEdit) {
        setEditErrors(["Tariff data not available"]);
        return;
      }

      const requestData = {
        exporter: tariffToEdit.exporterCode,
        importer: tariffToEdit.importerCode,
        HSCode: tariffToEdit.HSCode,
        rate: parseFloat(editForm.rate) / 100, // Convert percentage back to decimal
        effectiveDate: new Date(editForm.effectiveDate).toISOString(),
        expiryDate: editForm.expiryDate ? new Date(editForm.expiryDate).toISOString() : null,
        reference: editForm.reference || null
      };

      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${tariffToEdit.tariffID}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
      });

      if (response.ok) {
        const updatedTariff = await response.json();
        setTariffs(tariffs.map(prevTariff => prevTariff.tariffID === tariffToEdit.tariffID ? updatedTariff : prevTariff)); // show the updated tariff
        handleCancelEdit();
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup,"Tariff updated successfully!");
      } else {
        const errorData = await response.json();
        setEditErrors([`Error: ${errorData.message || "Failed to update tariff"}`]);
      }
    } catch (error) {
      setEditErrors([`Error: ${error.message}`]);
    } finally {
      setIsUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Loading tariffs...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-white to-blue-400 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-extrabold text-gray-900">All Tariffs</h1>
          <p className="mt-2 text-sm text-gray-600"> View all tariff entries in the system</p>
          <p className="mt-1 text-sm text-gray-600"> Click on a tariff to view more details</p>
        </div>

        {deleteMessage && deleteMessage.includes("Error") && (
          <div className="mb-4 p-4 rounded-md bg-red-100 text-red-700">
            {deleteMessage}
          </div>
        )}

        {fetchingError && (
          <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-md">
            {fetchingError}
          </div>
        )}

        {tariffs.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">No tariffs found</p>
          </div>
        ) : (
          <div className="bg-white shadow overflow-hidden sm:rounded-md">
            <div className="overflow-hidden">
              <table className="w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/6">
                      Exporting Country
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/6">
                      Destination Country
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/12">
                      HS Code
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/4">
                      Product Description
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/12">
                      Rate (%)
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {tariffs.map((tariff) => (
                    <React.Fragment key={tariff.tariffID}>
                      <tr
                        className="hover:bg-gray-100 cursor-pointer"
                        onClick={() => handleShowDetails(tariff)}
                      >
                        <td className="px-4 py-4 text-sm font-medium text-gray-900">
                          {tariff.exporterName}
                        </td>
                        <td className="px-4 py-4 text-sm text-gray-500">
                          {tariff.importerName}
                        </td>
                        <td className="px-4 py-4 text-sm text-gray-500">
                          {tariff.HSCode}
                        </td>
                        <td className="px-4 py-4 text-sm text-gray-500" title={tariff.productDescription || "N/A"}>
                          {tariff.productDescription ? (tariff.productDescription.length > 40 ? tariff.productDescription.substring(0, 40) + "..." : tariff.productDescription) : "N/A"}
                        </td>
                        <td className="px-4 py-4 text-sm text-gray-900 font-medium">
                          {(parseFloat(tariff.rate) * 100).toFixed(2)}%
                        </td>
                      </tr>
                    </React.Fragment>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        <div className="mt-8 text-center">
          <button
            onClick={fetchTariffs}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 cursor-pointer"
          >
            Refresh
          </button>
        </div>

        {/* Success Popup */}
        {showSuccessPopup && <SuccessMessage successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}

        {/* Delete Confirmation Popup */}
        {showDeletePopup && tariffToDelete && (
          <div className="fixed inset-0 flex items-center justify-center z-50">
            <div className="fixed inset-0 bg-white/50 backdrop-blur-sm" onClick={cancelDelete}></div>
            <div className="bg-white rounded-lg p-6 shadow-xl border border-gray-200 max-w-md w-full mx-4 animate-fade-in relative">
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
                <div className="bg-gray-50 p-3 rounded-md">
                  <p className="text-sm font-medium text-gray-900">
                    {tariffToDelete.exporterName} → {tariffToDelete.importerName}
                  </p>
                  <p className="text-sm text-gray-600">
                    Rate: {(parseFloat(tariffToDelete.rate) * 100).toFixed(2)}%
                  </p>
                  <p className="text-sm text-gray-600">
                    Product: {tariffToDelete.productDescription} ({tariffToDelete.HSCode})
                  </p>
                  <p className="text-sm text-gray-600">
                    Expiry Date: {formatDate(tariffToDelete.expiryDate)}
                  </p>

                </div>
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  onClick={cancelDelete}
                  className="cursor-pointer px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={confirmDelete}
                  className="cursor-pointer px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md transition-colors"
                >
                  Delete Tariff
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Edit Confirmation Popup */}
        {showEditPopup && tariffToEdit && (
          <div className="fixed inset-0 flex items-center justify-center z-50" >
            <div className="fixed inset-0 bg-white/50 backdrop-blur-sm" onClick={handleCancelEdit}></div>
            <div className="bg-white shadow-xl rounded-lg px-8 py-4 w-full max-w-md animate-fade-in relative">
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-lg font-semibold text-gray-800">Edit Tariff</h3>
                <button
                  onClick={handleCancelEdit}
                  className="text-gray-400 hover:text-gray-500"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <div className="mb-3 bg-blue-50 p-2 rounded-md">
                <p className="text-sm font-medium text-gray-700 mb-1">Current Tariff</p>
                <div className="text-xs text-gray-600 space-y-1">
                  <p><span className="font-medium">Route:</span> {tariffToEdit.exporterName} → {tariffToEdit.importerName}</p>
                  <p><span className="font-medium">Product:</span> {tariffToEdit.productDescription || "N/A"} ({tariffToEdit.HSCode})</p>
                  <p><span className="font-medium">Current Rate:</span> {(parseFloat(tariffToEdit.rate) * 100).toFixed(2)}%</p>
                </div>
              </div>

              <form onSubmit={handleEditSubmit} className="space-y-2">
                <div>
                  <label className="block text-gray-700 text-sm font-medium mb-1" htmlFor="rate">
                    Rate (%)
                  </label>
                  <input
                    name="rate"
                    type="number"
                    min="0"
                    step="0.01"
                    value={editForm.rate}
                    onChange={handleEditFormChange}
                    className="cursor-pointer shadow-sm border border-gray-300 rounded w-full py- px-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-gray-700 text-sm font-medium mb-1 mt-1" htmlFor="effectiveDate">
                    Effective Date
                  </label>
                  <input
                    name="effectiveDate"
                    type="date"
                    value={editForm.effectiveDate}
                    onChange={handleEditFormChange}
                    className={`cursor-pointer shadow-sm border border-gray-300 rounded w-full py-1.5 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${editForm.effectiveDate ? " text-gray-700" : "text-white"}`}
                  />
                </div>

                <div>
                  <label className="block text-gray-700 text-sm font-medium mb-1 mt-1" htmlFor="expiryDate">
                    Expiry Date
                  </label>
                  <input
                    name="expiryDate"
                    type="date"
                    value={editForm.expiryDate}
                    onChange={handleEditFormChange}
                    className={`cursor-pointer shadow-sm border border-gray-300 rounded w-full py-1.5 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${editForm.expiryDate ? " text-gray-700" : "text-white"}`}
                  />
                </div>

                <div>
                  <label className="block text-gray-700 text-sm font-medium mb-1 mt-1" htmlFor="reference">
                    Reference
                  </label>
                  <input
                    name="reference"
                    type="text"
                    value={editForm.reference}
                    onChange={handleEditFormChange}
                    placeholder="Source URL"
                    className="cursor-pointer shadow-sm border border-gray-300 rounded w-full py-1.5 px-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>
                
                {editErrors.length > 0 && (
                  <div className="mt-2 p-2 bg-red-50 border border-red-200 rounded text-xs text-red-600">
                    {editErrors.map((error, index) => (
                      <p key={index}>{error}</p>
                    ))}
                  </div>
                )}

                <div className="flex justify-end space-x-3 mt-4 pt-3">
                  <button
                    type="button"
                    onClick={handleCancelEdit}
                    className="px-3 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md transition-colors cursor-pointer"
                  >
                    Cancel
                  </button>
                  
                  <button
                    type="submit"
                    disabled={isUpdating}
                    className={`px-3 py-1.5 text-sm font-medium rounded-md transition-colors ${isUpdating
                      ? 'bg-gray-400 cursor-not-allowed text-white'
                      : 'bg-blue-600 hover:bg-blue-700 text-white cursor-pointer'
                      }`}
                  >
                    {isUpdating ? 'Updating...' : 'Update'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Tariff Details Popup */}
        {showDetailsPopup && selectedTariff && (
          <div className="fixed inset-0 flex items-center justify-center z-50">
            <div className="fixed inset-0 bg-white/50 backdrop-blur-sm" onClick={closeDetailsPopup}></div>
            <div className="bg-white rounded-lg p-6 shadow-xl border border-gray-200 max-w-2xl w-full mx-4 animate-fade-in relative">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-medium text-gray-900">Tariff Details</h3>
                <button
                  onClick={closeDetailsPopup}
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
                    <p className="text-sm text-gray-900">{selectedTariff.HSCode}</p>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tariff Rate</label>
                    <p className="text-sm text-gray-900">{(parseFloat(selectedTariff.rate) * 100).toFixed(2)}%</p>
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
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">Product Description</label>
                  <p className="text-sm text-gray-900">{selectedTariff.productDescription || "N/A"}</p>
                </div>
              </div>

              {role === 'admin' && <div className="flex justify-end space-x-3 mt-6 pt-4 border-t border-gray-200">
                <button
                  onClick={() => {
                    closeDetailsPopup();
                    openEditPopup();
                  }}
                  className="px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-md transition-colors cursor-pointer"
                >
                  Update Tariff
                </button>
                <button
                  onClick={() => {
                    closeDetailsPopup();
                    handleDelete();
                  }}
                  className="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md transition-colors cursor-pointer"
                >
                  Delete Tariff
                </button>
              </div>}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}