"use client";
import React, { useState, useEffect } from "react";

export default function ViewTariffsPage() {
  const [tariffs, setTariffs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleteMessage, setDeleteMessage] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({});
  const [activeMenu, setActiveMenu] = useState(null);
  const [editError, setEditError] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [tariffToDelete, setTariffToDelete] = useState(null);

  useEffect(() => {
    fetchTariffs();
  }, []);

  useEffect(() => {
    const handleClickOutside = () => {
      setActiveMenu(null);
    };

    if (activeMenu) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [activeMenu]);

  const fetchTariffs = async () => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs`);
      if (response.ok) {
        const data = await response.json();
        setTariffs(data);
      } else {
        setError("Failed to fetch tariffs");
      }
    } catch (error) {
      setError(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (id) => {
    const tariff = tariffs.find(t => t.tariffID === id);
    setTariffToDelete(tariff);
    setShowDeletePopup(true);
  };

  const confirmDelete = async () => {
    if (!tariffToDelete) return;

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${tariffToDelete.tariffID}`, {
        method: "DELETE",
      });

      if (response.ok) {
        setTariffs(tariffs.filter(tariff => tariff.tariffID !== tariffToDelete.tariffID));
        showSuccessPopupMessage("Tariff deleted successfully!");
        setShowDeletePopup(false);
        setTariffToDelete(null);
      } else {
        const errorData = await response.json();
        setDeleteMessage(`Error: ${errorData.message || "Failed to delete tariff"}`);
        setShowDeletePopup(false);
        setTariffToDelete(null);
      }
    } catch (error) {
      setDeleteMessage(`Error: ${error.message}`);
      setShowDeletePopup(false);
      setTariffToDelete(null);
    }
  };

  const cancelDelete = () => {
    setShowDeletePopup(false);
    setTariffToDelete(null);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateForInput = (dateString) => {
    if (!dateString) return "";
    return new Date(dateString).toISOString().split('T')[0];
  };

  const handleEdit = (tariff) => {
    setEditingId(tariff.tariffID);
    setEditForm({
      rate: (parseFloat(tariff.rate) * 100).toFixed(2), // Convert to percentage for display
      effectiveDate: formatDateForInput(tariff.effectiveDate),
      expiryDate: formatDateForInput(tariff.expiryDate),
      reference: tariff.reference || ""
    });
    setActiveMenu(null);
    setEditError(""); // Clear any previous edit errors
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditForm({});
    setEditError(""); // Clear edit errors when canceling
  };

  const handleSaveEdit = async (tariffId) => {
    try {
      const tariff = tariffs.find(t => t.tariffID === tariffId);
      const requestData = {
        exporter: tariff.exporterCode,
        importer: tariff.importerCode,
        HSCode: tariff.HSCode, // Now correctly using HSCode property name
        rate: parseFloat(editForm.rate) / 100, // Convert percentage back to decimal
        effectiveDate: new Date(editForm.effectiveDate).toISOString(),
        expiryDate: editForm.expiryDate ? new Date(editForm.expiryDate).toISOString() : null,
        reference: editForm.reference || null
      };

      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${tariffId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
      });

      if (response.ok) {
        const updatedTariff = await response.json();
        setTariffs(tariffs.map(t => t.tariffID === tariffId ? updatedTariff : t));
        setEditingId(null);
        setEditForm({});
        setEditError(""); // Clear edit error on success
        showSuccessPopupMessage("Tariff updated successfully!");
      } else {
        const errorData = await response.json();
        setEditError(`Error: ${errorData.message || "Failed to update tariff"}`);
      }
    } catch (error) {
      setEditError(`Error: ${error.message}`);
    }
  };

  const handleEditFormChange = (field, value) => {
    setEditForm(prev => ({ ...prev, [field]: value }));
  };

  const toggleMenu = (tariffId) => {
    setActiveMenu(activeMenu === tariffId ? null : tariffId);
  };

  const getMenuPosition = (index, totalCount) => {
    // Show menu below for first half of items, above for second half
    const isTopHalf = index < totalCount / 2;
    return isTopHalf ? 'below' : 'above';
  };

  const showSuccessPopupMessage = (message) => {
    setSuccessMessage(message);
    setShowSuccessPopup(true);
    setTimeout(() => {
      setShowSuccessPopup(false);
      setSuccessMessage("");
    }, 3000);
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
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-extrabold text-gray-900">All Tariffs</h1>
          <p className="mt-2 text-sm text-gray-600">
            Manage and view all tariff entries in the system
          </p>
        </div>

        {deleteMessage && deleteMessage.includes("Error") && (
          <div className="mb-4 p-4 rounded-md bg-red-100 text-red-700">
            {deleteMessage}
          </div>
        )}

        {error && (
          <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-md">
            {error}
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
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/6">
                      Exporting Country
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/6">
                      Destination Country
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/8">
                      Rate (%)
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/8">
                      Effective Date
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/8">
                      Expiry Date
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/6">
                      Reference
                    </th>
                    <th className="px-2 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/8">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {tariffs.map((tariff, index) => (
                    <React.Fragment key={tariff.tariffID}>
                      <tr>
                      <td className="px-2 py-3 text-sm font-medium text-gray-900 truncate">
                        {tariff.exporterName}
                      </td>
                      <td className="px-2 py-3 text-sm text-gray-500 truncate">
                        {tariff.importerName}
                      </td>
                      <td className="px-2 py-3 text-sm text-gray-500">
                        {editingId === tariff.tariffID ? (
                          <input
                            type="number"
                            step="0.01"
                            value={editForm.rate}
                            onChange={(e) => handleEditFormChange('rate', e.target.value)}
                            className="w-full px-1 py-1 text-xs border rounded"
                          />
                        ) : (
                          `${(parseFloat(tariff.rate) * 100).toFixed(2)}%`
                        )}
                      </td>
                      <td className="px-2 py-3 text-sm text-gray-500">
                        {editingId === tariff.tariffID ? (
                          <input
                            type="date"
                            value={editForm.effectiveDate}
                            onChange={(e) => handleEditFormChange('effectiveDate', e.target.value)}
                            className="w-full px-1 py-1 text-xs border rounded"
                          />
                        ) : (
                          formatDate(tariff.effectiveDate)
                        )}
                      </td>
                      <td className="px-2 py-3 text-sm text-gray-500">
                        {editingId === tariff.tariffID ? (
                          <input
                            type="date"
                            value={editForm.expiryDate}
                            onChange={(e) => handleEditFormChange('expiryDate', e.target.value)}
                            className="w-full px-1 py-1 text-xs border rounded"
                          />
                        ) : (
                          formatDate(tariff.expiryDate)
                        )}
                      </td>
                      <td className="px-2 py-3 text-sm text-gray-500 truncate" title={tariff.reference || "N/A"}>
                        {editingId === tariff.tariffID ? (
                          <input
                            type="text"
                            value={editForm.reference}
                            onChange={(e) => handleEditFormChange('reference', e.target.value)}
                            className="w-full px-1 py-1 text-xs border rounded"
                            placeholder="Reference URL"
                          />
                        ) : (
                          tariff.reference ? (tariff.reference.length > 20 ? tariff.reference.substring(0, 20) + "..." : tariff.reference) : "N/A"
                        )}
                      </td>
                      <td className="px-2 py-3 text-sm font-medium relative">
                        {editingId === tariff.tariffID ? (
                          <div>
                            <div className="flex space-x-1">
                              <button
                                onClick={() => handleSaveEdit(tariff.tariffID)}
                                className="text-green-600 hover:text-green-900 text-xs px-2 py-1 rounded bg-green-50 hover:bg-green-100"
                              >
                                Save
                              </button>
                              <button
                                onClick={handleCancelEdit}
                                className="text-gray-600 hover:text-gray-900 text-xs px-2 py-1 rounded bg-gray-50 hover:bg-gray-100"
                              >
                                Cancel
                              </button>
                            </div>
                          </div>
                        ) : (
                          <>
                            <button
                              onClick={() => toggleMenu(tariff.tariffID)}
                              className="text-gray-600 hover:text-gray-900 p-1 rounded hover:bg-gray-100"
                            >
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
                              </svg>
                            </button>
                            {activeMenu === tariff.tariffID && (
                              <div className={`absolute right-0 w-32 bg-white border border-gray-200 rounded-md shadow-lg z-50 ${
                                getMenuPosition(index, tariffs.length) === 'below' 
                                  ? 'top-full mt-1' 
                                  : 'bottom-full mb-1'
                              }`}>
                                <button
                                  onClick={() => handleEdit(tariff)}
                                  className="block w-full text-left px-3 py-2 text-xs text-gray-700 hover:bg-gray-100 rounded-t-md"
                                >
                                  Edit
                                </button>
                                <button
                                  onClick={() => {
                                    setActiveMenu(null);
                                    handleDelete(tariff.tariffID);
                                  }}
                                  className="block w-full text-left px-3 py-2 text-xs text-red-600 hover:bg-red-50 rounded-b-md"
                                >
                                  Delete
                                </button>
                              </div>
                            )}
                          </>
                        )}
                      </td>
                    </tr>
                    {/* Error message row for inline editing */}
                    {editingId === tariff.tariffID && editError && (
                      <tr className="border-l-4 border-red-400 bg-red-50">
                        <td colSpan="6" className="px-2 py-2">
                          <div className="text-red-700 text-sm bg-red-100 p-2 rounded">
                            {editError}
                          </div>
                        </td>
                      </tr>
                    )}
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
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Refresh
          </button>
        </div>

        {/* Success Popup */}
        {showSuccessPopup && (
          <div className="fixed top-4 right-4 z-50">
            <div className="bg-white rounded-lg p-4 shadow-lg border border-gray-200 max-w-sm animate-fade-in">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <svg className="h-5 w-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-900">{successMessage}</p>
                </div>
                <div className="ml-auto pl-3">
                  <button
                    onClick={() => setShowSuccessPopup(false)}
                    className="inline-flex text-gray-400 hover:text-gray-500"
                  >
                    <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Delete Confirmation Popup */}
        {showDeletePopup && tariffToDelete && (
          <div className="fixed inset-0 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 shadow-xl border border-gray-200 max-w-md w-full mx-4 animate-fade-in">
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
                  Are you sure you want to delete this tariff? This action cannot be undone.
                </p>
                <div className="bg-gray-50 p-3 rounded-md">
                  <p className="text-sm font-medium text-gray-900">
                    {tariffToDelete.exporterName} → {tariffToDelete.importerName}
                  </p>
                  <p className="text-sm text-gray-600">
                    Rate: {(parseFloat(tariffToDelete.rate) * 100).toFixed(2)}%
                  </p>
                </div>
              </div>
              
              <div className="flex justify-end space-x-3">
                <button
                  onClick={cancelDelete}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={confirmDelete}
                  className="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md transition-colors"
                >
                  Delete Tariff
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}