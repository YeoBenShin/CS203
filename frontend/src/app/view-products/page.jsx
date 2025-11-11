"use client";
import React, { useState, useEffect } from "react";
import { useUser, useAuth } from '@clerk/nextjs';
import { SuccessMessageDisplay, showSuccessPopupMessage } from "../../components/messages/SuccessMessageDisplay";
import ReactTable from "../../components/ReactTable";
import Button from "../../components/Button";
import PopUpWrapper from "../../components/popUps/PopUpWrapper";
import ProductDetailPopUp from "../../components/popUps/ProductDetailPopUp";
import DeleteProductPopUp from "../../components/popUps/DeleteProductPopUp";
import LoadingPage from "../../components/LoadingPage";
import fetchApi from "@/utils/fetchApi";

export default function ViewProductsPage() {

  // loading products
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [fetchingError, setFetchingError] = useState("");

  // clicking on a product to view details
  const [showDetailsPopup, setShowDetailsPopup] = useState(false);
  const [selectedProduct, setSelectedProducts] = useState(null);

  // deleting a product
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [productToDelete, setProductToDelete] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  // editing a product
  const [showEditPopup, setShowEditPopup] = useState(false);
  const [productToEdit, setProductToEdit] = useState(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const [editErrors, setEditErrors] = useState([]);
  const [editForm, setEditForm] = useState({
    description: "",
  });

  // success popup
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  // user role
  const { user } = useUser();
  const role = user?.publicMetadata?.role || "user";
  const { getToken } = useAuth();

  // ----------------------------------------------------------
  // Use Effects 
  // ----------------------------------------------------------
  useEffect(() => {
    fetchProducts();
    // Cleanup effect to restore scrolling when component unmounts
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, []); 

  const fetchProducts = async () => {
    try {
      setLoading(true)
      const token = await getToken();
      const response = await fetchApi(token,  "api/products");
      if (response.ok) {
        const data = await response.json();
        // console.log("Fetched products:", data);
        setProducts(data);
      } else {
        setFetchingError("Failed to fetch products");
      }
    } catch (error) {
      setFetchingError(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // ----------------------------------------------------------
  // View Product PopUp
  // ----------------------------------------------------------
  const handleShowDetails = (product) => {
    setSelectedProducts(product);
    setProductToEdit(product);
    setShowDetailsPopup(true);
    // Prevent scrolling on the body when popup is open
    document.body.style.overflow = 'hidden';
  };

  const closeDetailsPopup = () => {
    setShowDetailsPopup(false);
    setSelectedProducts(null);
    // Restore scrolling when popup is closed
    document.body.style.overflow = 'unset';
  };

  // ----------------------------------------------------------
  // Delete Product
  // ----------------------------------------------------------
  const handleDelete = () => {
    setProductToDelete(selectedProduct);
    setShowDeletePopup(true);
    document.body.style.overflow = 'hidden';
  };

  const confirmDelete = async () => {
    if (!productToDelete) {
      setDeleteMessage("No product selected for deletion");
      return;
    }

    try {
      const token = await getToken();
      const response = await fetchApi(token, `api/products/${productToDelete.hsCode}`, "DELETE");
      if (response.ok) {
        const updatedProducts = products.filter(product => product.hsCode !== productToDelete.hsCode);
        setProducts(updatedProducts);
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Product deleted successfully!");

      } else {
        const errorData = await response.json();
        setDeleteMessage(`Error: ${errorData.message || "Failed to delete product"}`);
      }
    } catch (error) {
      setDeleteMessage(`Error: ${error.message}`);
    } finally {
      cancelDelete();
    }
  };

  const cancelDelete = () => {
    setShowDeletePopup(false);
    setProductToDelete(null);
    document.body.style.overflow = 'unset';
  };

  // ----------------------------------------------------------
  // Edit Product
  // ----------------------------------------------------------
  const openEditPopup = () => {
    setEditForm({
      description: productToEdit.description
    });
    setEditErrors([]);
    setShowEditPopup(true);
    document.body.style.overflow = 'hidden';
  };

  const handleCancelEdit = () => {
    setShowEditPopup(false);
    setProductToEdit(null);
    setEditForm({
      hsCode: "",
      description: "",
    });
    setEditErrors([]); // Clear edit errors when canceling
    setIsUpdating(false);
    document.body.style.overflow = 'unset';
  };

  const handleEditFormChange = (e) => {
    // console.log("Edit form change:", e.target.name, e.target.value);
    setEditForm({ ...editForm, [e.target.name]: e.target.value });
    // Clear errors when user starts typing
    if (editErrors.length > 0) {
      setEditErrors([]);
    }
  };

  const validateEditForm = () => {
    const validationErrors = [];
    if (editForm.description.trim() === "") {
      validationErrors.push("Description is required");
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
      if (!productToEdit) {
        setEditErrors(["Product data not available"]);
        return;
      }

      const requestData = {
        description: editForm.description ? editForm.description : productToEdit.description,
      };
      console.log("Submitting edit with data:", requestData);

      const token = await getToken();
      const response = await fetchApi(token, `api/products/${productToEdit.hsCode}`, "PUT", requestData);

      if (response.ok) {
        const updatedProduct = await response.json();
        const updatedProducts = products.map(prevProduct => prevProduct.hsCode === productToEdit.hsCode ? updatedProduct : prevProduct);
        setProducts(updatedProducts); // show the updated product
        handleCancelEdit();
        showSuccessPopupMessage(setSuccessMessage, setShowSuccessPopup, "Product updated successfully!");
      } else {
        const errorData = await response.json();
        setEditErrors([`Error: ${errorData.message || "Failed to update product"}`]);
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
          <h1 className="text-3xl font-extrabold text-gray-900">All Products</h1>
          <p className="mt-2 text-sm text-gray-600"> View all products in the system</p>
          <p className="mt-1 text-sm text-gray-600"> Click on a product to view more details</p>
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
              header: "HS Code",
              accessorKey: "hsCode",
              enableSorting: true,
              filterFn: "includesString",
            },
            {
              header: "Product Description",
              accessorKey: "description",
              enableSorting: true,
              filterFn: "includesString",
              cell: info => info.getValue() ? (info.getValue().length > 40 ? info.getValue().substring(0, 40) + "..." : info.getValue()) : "N/A",
            },
          ]}
          data={products}
          rowLevelFunction={handleShowDetails}
        />

        <div className="mt-6 text-center">
          <Button
            onClick={fetchProducts}
            isLoading={loading}
            otherClass="inline-flex items-center"
            width=""
          >
            Refresh
          </Button>
        </div>

        {/* Success Popup */}
        {showSuccessPopup && <SuccessMessageDisplay successMessage={successMessage} setShowSuccessPopup={setShowSuccessPopup} />}

        {/* Product Details Popup */}
        {showDetailsPopup && selectedProduct && (
          <PopUpWrapper OnClick={closeDetailsPopup}>
            <ProductDetailPopUp
              selectedProduct={selectedProduct}
              OnClick={closeDetailsPopup}
              openEditPopup={openEditPopup}
              openDeletePopup={handleDelete}
              hasPermissionToEdit={role === "admin"}
              hasPermissionToDelete={role === "admin"}
            />
          </PopUpWrapper>
        )}

        {/* Delete Confirmation Popup */}
        {showDeletePopup && productToDelete && (
          <PopUpWrapper OnClick={cancelDelete}>
            <DeleteProductPopUp
              productToDelete={productToDelete}
              handleCancelDelete={cancelDelete}
              handleConfirmDelete={confirmDelete}
            />
          </PopUpWrapper>
        )}

        {/* Edit Confirmation Popup */}
        {showEditPopup && productToEdit && (
          <PopUpWrapper OnClick={handleCancelEdit}>
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-lg font-semibold text-gray-800">Edit Product</h3>
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
              <p className="text-sm font-medium text-gray-700 mb-1">Current Product</p>
              <div className="text-xs text-gray-600 space-y-1">
                <p><span className="font-medium">HS Code:</span> {productToEdit.hsCode}</p>
                <p><span className="font-medium">Description:</span> {productToEdit.description || "N/A"}</p>
              </div>
            </div>

            <form onSubmit={handleEditSubmit} className="space-y-2">
              <div>
                <label className="block text-gray-700 text-sm font-medium mb-1 mt-1" htmlFor="description">
                  Product Description
                </label>
                <input
                  name="description"
                  type="text"
                  value={editForm.description}
                  onChange={handleEditFormChange}
                  placeholder="New Product Description..."
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