"use client";
import React, { useState, useEffect } from "react";
import { useUser } from '@clerk/nextjs';
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../../components/messages/SuccessMessageDisplay";
import ReactTable from "../../components/ReactTable";
import Button from "../../components/Button";
import PopUpWrapper from "../../components/popUps/PopUpWrapper";
import TariffDetailPopUp from "../../components/popUps/TariffDetailPopUp";
import DeleteTariffPopUp from "../../components/popUps/DeleteTariffPopUp";
import LoadingPage from "../../components/LoadingPage";
import { formatRate, formatUnitOfCalculation, formatDateForInput, formatDate } from "@/utils/formatDisplayHelpers";

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
    tariffRates: [],
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


  // ----------------------------------------------------------
  // Helper Functions
  // ----------------------------------------------------------
  const formatTariffRatesDisplay = (tariffRates) => {
    if (!tariffRates || tariffRates.length === 0) {
      return "N/A";
    }

    return tariffRates.map(tariffRate => {
      const rate = tariffRate.rate;
      const unit = tariffRate.unitOfCalculation;

      if (unit === 'AV') {
        // Ad Valorem - display as percentage
        return `${formatRate(rate)}`;
      } else {
        // Other units - display with unit symbol
        return `$${parseFloat(rate).toFixed(2)}${formatUnitOfCalculation(unit)}`;
      }
    }).join(" + ");
  };

  // ----------------------------------------------------------
  // Use Effects 
  // ----------------------------------------------------------
  useEffect(() => {
    fetchTariffs();
    // Cleanup effect to restore scrolling when component unmounts
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, []); 

  const fetchTariffs = async () => {
    try {
      setLoading(true)
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs`);
      if (response.ok) {
        const data = await response.json();
        // console.log("Fetched tariffs:", data);
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

  // ----------------------------------------------------------
  // View Tariff PopUp
  // ----------------------------------------------------------
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

  // ----------------------------------------------------------
  // Delete Tariff
  // ----------------------------------------------------------
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
        const updatedTariffs = tariffs.filter(tariff => tariff.tariffID !== tariffToDelete.tariffID);
        setTariffs(updatedTariffs);
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Tariff deleted successfully!");

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

  // ----------------------------------------------------------
  // Edit Tariff
  // ----------------------------------------------------------
  const openEditPopup = () => {
    setEditForm({
      effectiveDate: formatDateForInput(tariffToEdit.effectiveDate),
      expiryDate: formatDateForInput(tariffToEdit.expiryDate),
      reference: tariffToEdit.reference || "",
      tariffRates: tariffToEdit.tariffRates?.map(rate => ({
        tariffRateID: rate.tariffRateID,
        unitOfCalculation: rate.unitOfCalculation,
        rate: rate.unitOfCalculation === 'AV'
          ? (parseFloat(rate.rate) * 100).toFixed(2)
          : parseFloat(rate.rate).toFixed(2)
      })) || [],
    });
    setEditErrors([]);
    setShowEditPopup(true);
    document.body.style.overflow = 'hidden';
  };

  const handleCancelEdit = () => {
    setShowEditPopup(false);
    setTariffToEdit(null);
    setEditForm({
      tariffRates: [],
      effectiveDate: "",
      expiryDate: "",
      reference: ""
    });
    setEditErrors([]); // Clear edit errors when canceling
    setIsUpdating(false);
    document.body.style.overflow = 'unset';
  };

  const handleEditFormChange = (e) => {
    console.log("Edit form change:", e.target.name, e.target.value);
    setEditForm({ ...editForm, [e.target.name]: e.target.value });
    // Clear errors when user starts typing
    if (editErrors.length > 0) {
      setEditErrors([]);
    }
  };

  const handleTariffRateChange = (index, field, value) => {
    const updatedRates = [...editForm.tariffRates];
    updatedRates[index] = { ...updatedRates[index], [field]: value };
    setEditForm({ ...editForm, tariffRates: updatedRates });
    // Clear errors when user starts typing
    if (editErrors.length > 0) {
      setEditErrors([]);
    }
  };

  const validateEditForm = () => {
    const validationErrors = [];
    editForm.tariffRates.map(tariff => {
      if (!/^\d+(\.\d{1,2})?$/.test(tariff.rate)) {
        validationErrors.push(`Tariff Rate ${tariff.tariffRateID} must be a valid number`);
      }
    });

    // Check date logic
    if (editForm.effectiveDate && editForm.expiryDate) {
      const effectiveDate = formatDateForInput(editForm.effectiveDate);
      const expiryDate = formatDateForInput(editForm.expiryDate);
      if (expiryDate <= effectiveDate) {
        validationErrors.push("Expiry date must be after the effective date");
      }
    } else if (editForm.effectiveDate && tariffToEdit.expiryDate) { // past data must have expiry, else don't even need to compare
      const effectiveDate = formatDateForInput(editForm.effectiveDate);
      if (!tariffToEdit.expiryDate) {
        const currentExpiryDate = formatDateForInput(tariffToEdit.expiryDate);
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

      // Transform tariffRates array to Map format for backend
      const tariffRatesMap = {};
      editForm.tariffRates.forEach(rate => {
        tariffRatesMap[rate.unitOfCalculation] = rate.unitOfCalculation === 'AV'
          ? parseFloat(rate.rate) / 100
          : parseFloat(rate.rate);
      });

      const requestData = {
        exporter: tariffToEdit.exporterCode,
        importer: tariffToEdit.importerCode,
        hscode: tariffToEdit.hSCode,
        tariffRates: tariffRatesMap,
        effectiveDate: formatDateForInput(editForm.effectiveDate),
        expiryDate: formatDateForInput(editForm.expiryDate),
        reference: editForm.reference || null
      };
      console.log("Submitting edit with data:", requestData);

      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${tariffToEdit.tariffID}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
      });

      if (response.ok) {
        const updatedTariff = await response.json();
        const updatedTariffs = tariffs.map(prevTariff => prevTariff.tariffID === tariffToEdit.tariffID ? updatedTariff : prevTariff);
        setTariffs(updatedTariffs); // show the updated tariff
        handleCancelEdit();
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Tariff updated successfully!");
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
    return <LoadingPage />;
  }

  return (
    <div className="min-h-screen py-12 px-4 sm:px-6 lg:px-8">
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
        <ReactTable
          columns={[
            {
              header: "Exporting Country",
              accessorKey: "exporterName",
              enableSorting: true, // Enable sorting
              enableColumnFilter: true, // Enable filter dropdown
              filterFn: (row, columnId, filterValue) => {
                // console.log('Filtering row:', row.original.exporterName, 'columnId:', columnId, 'filterValue:', filterValue, 'row.getValue(columnId):', row.getValue(columnId));
                if (!filterValue || filterValue.length === 0) {
                  // console.log('No filter applied, returning true');
                  return true;
                }
                const result = filterValue.includes(row.getValue(columnId));
                //console.log('Filter result:', result);
                return result;
              }, // Filter function (case-insensitive includes)
              cell: info => <span className="text-gray-900 font-medium">{info.getValue() || "N/A"}</span>,
            },
            {
              header: "Destination Country",
              accessorKey: "importerName",
              enableSorting: true,
              enableColumnFilter: true,
              filterFn: (row, columnId, filterValue) => {
                // console.log('Filtering row:', row.original.exporterName, 'columnId:', columnId, 'filterValue:', filterValue, 'row.getValue(columnId):', row.getValue(columnId));
                if (!filterValue || filterValue.length === 0) {
                  // console.log('No filter applied, returning true');
                  return true;
                }
                const result = filterValue.includes(row.getValue(columnId));
                //console.log('Filter result:', result);
                return result;
              },
            },
            {
              header: "HS Code",
              accessorKey: "hSCode",
              enableSorting: true,
              filterFn: "includesString",
            },
            {
              header: "Product Description",
              accessorKey: "productDescription",
              enableSorting: true,
              filterFn: "includesString",
              cell: info => info.getValue() ? (info.getValue().length > 40 ? info.getValue().substring(0, 40) + "..." : info.getValue()) : "N/A",
            },
            {
              header: "Tariff Rates",
              accessorKey: "tariffRates",
              filterFn: (row, columnId, filterValue) => {
                // Custom filter for tariff rates (search within the formatted string)
                const formattedRates = formatTariffRatesDisplay(row.getValue(columnId));
                return formattedRates.toLowerCase().includes(filterValue.toLowerCase());
              },
              cell: info => (
                <div className="text-gray-900 font-medium">
                  {formatTariffRatesDisplay(info.getValue())}
                </div>
              ),
            },
          ]}
          data={tariffs}
          rowLevelFunction={handleShowDetails}
        />

        <div className="mt-6 text-center">
          <Button
            onClick={fetchTariffs}
            isLoading={loading}
            otherClass="inline-flex items-center"
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
              openEditPopup={openEditPopup}
              openDeletePopup={handleDelete}
              hasPermissionToEdit={role === "admin"}
              hasPermissionToDelete={role === "admin"}
            />
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

        {/* Edit Confirmation Popup */}
        {showEditPopup && tariffToEdit && (
          <PopUpWrapper OnClick={handleCancelEdit}>
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
                <p><span className="font-medium">Product:</span> {tariffToEdit.productDescription || "N/A"} ({tariffToEdit.hSCode})</p>
                <p><span className="font-medium">Current Rates:</span> {formatTariffRatesDisplay(tariffToEdit.tariffRates)}</p>
                <p><span className="font-medium">Effective Date:</span> {formatDate(tariffToEdit.effectiveDate)}</p>
                <p><span className="font-medium">Expiry Date:</span> {formatDate(tariffToEdit.expiryDate)}</p>
                <p><span className="font-medium">Reference:</span> {tariffToEdit.reference || "N/A"}</p>
              </div>
            </div>

            <form onSubmit={handleEditSubmit} className="space-y-2">
              {/* Tariff Rates Section */}
              <div>
                {editForm.tariffRates.map((tariffRate, index) => (
                  <div key={index}>
                    <div className="flex items-center justify-between mb-1 mt-1">
                      <label className="block text-gray-700 text-sm font-medium">
                        Tariff Rate {index + 1}
                        {tariffRate.unitOfCalculation === "AV" ? " (%)" : (" ($" + formatUnitOfCalculation(tariffRate.unitOfCalculation) + ")")}
                      </label>
                    </div>
                    <div className="flex space-x-2">
                      <input
                        name="rate"
                        type="number"
                        min="0"
                        step="0.01"
                        value={tariffRate.rate}
                        onChange={(e) => handleTariffRateChange(index, e.target.name, e.target.value)}
                        className="flex-1 cursor-pointer shadow-sm border border-gray-300 rounded py-1.5 px-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder={`Rate in ${formatUnitOfCalculation(tariffRate.unitOfCalculation)}`}
                      />
                    </div>
                  </div>
                ))}
              </div>

              {/* Rest of the form fields remain the same */}
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
          </PopUpWrapper>
        )}
      </div>
    </div >
  );
}