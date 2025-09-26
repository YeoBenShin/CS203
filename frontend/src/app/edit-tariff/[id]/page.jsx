"use client";
import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { ErrorDisplay, SuccessDisplay, LoadingSpinner } from "../../../components/MessageComponents";

export default function EditTariffPage({ params }) {
    const router = useRouter();
    const { id } = params;

    const [form, setForm] = useState({
        rate: "",
        effectiveDate: "",
        expiryDate: "",
        reference: ""
    });

    const [tariff, setTariff] = useState(null);
    const [errors, setErrors] = useState([]);
    const [successMessage, setSuccessMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    useEffect(() => {
        const fetchTariff = async () => {
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080"}/api/tariffs/${id}`);
                if (!response.ok) {
                    throw new Error("Failed to fetch tariff");
                }

                const data = await response.json();
                setTariff(data);

                // Populate form with existing values
                setForm({
                    rate: (parseFloat(data.rate) * 100).toFixed(2), // Convert from decimal to percentage
                    effectiveDate: formatDateForInput(data.effectiveDate),
                    expiryDate: formatDateForInput(data.expiryDate),
                    reference: data.reference || ""
                });
            } catch (error) {
                console.error("Error fetching tariff:", error);
                setErrors(["Failed to load tariff data. Please try again."]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchTariff();
    }, [id]);

    const formatDateForInput = (dateString) => {
        if (!dateString) return "";
        return new Date(dateString).toISOString().split('T')[0];
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        // Clear errors when user starts typing
        if (errors.length > 0) {
            setErrors([]);
        }
    };

    // Client-side validation
    const validateForm = () => {
        const validationErrors = [];

        if (!form.rate) {
            validationErrors.push("Please enter a tariff rate");
        } else {
            const rate = parseFloat(form.rate);
            if (rate < 0) {
                validationErrors.push("Tariff rate cannot be negative");
            }
        }

        if (!form.effectiveDate) {
            validationErrors.push("Please enter an effective date");
        }

        // Check if expiry date is before effective date
        if (form.effectiveDate && form.expiryDate) {
            const effectiveDate = new Date(form.effectiveDate);
            const expiryDate = new Date(form.expiryDate);
            if (expiryDate <= effectiveDate) {
                validationErrors.push("Expiry date must be after the effective date");
            }
        }

        return validationErrors;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors([]);
        setSuccessMessage("");
        setIsSaving(true);

        // Client-side validation
        const validationErrors = validateForm();
        if (validationErrors.length > 0) {
            setErrors(validationErrors);
            setIsSaving(false);
            return;
        }

        try {
            // Only proceed if we have tariff data
            if (!tariff) {
                setErrors(["Tariff data not available"]);
                setIsSaving(false);
                return;
            }

            // Maintain the existing mapping relationship by including the original mapping data
            const requestData = {
                exporter: tariff.exporterCode,
                importer: tariff.importerCode,
                HSCode: tariff.HSCode,
                rate: parseFloat(form.rate) / 100, // Convert percentage to decimal
                effectiveDate: new Date(form.effectiveDate).toISOString(),
                expiryDate: form.expiryDate ? new Date(form.expiryDate).toISOString() : null,
                reference: form.reference || null
            };

            const base_url = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080";
            const response = await fetch(`${base_url}/api/tariffs/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                setSuccessMessage("✅ Tariff updated successfully!");
                // Give users time to see the success message before redirecting
                setTimeout(() => {
                    router.push("/view-tariffs");
                }, 2000);
            } else {
                const errorText = await response.text();
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                } catch {
                    errorData = errorText;
                }

                if (errorData && errorData.message) {
                    setErrors([errorData.message]);
                } else {
                    setErrors(["An error occurred while updating the tariff"]);
                }
            }
        } catch (err) {
            console.error("Network error:", err);
            setErrors(["❌ Network error: Please check your connection and try again."]);
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
                    <p className="mt-4 text-gray-600">Loading tariff data...</p>
                </div>
            </div>
        );
    }

    if (!tariff && !isLoading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="bg-white shadow-md rounded px-8 py-6 max-w-md w-full">
                    <h1 className="text-2xl font-bold text-red-600 mb-4">Tariff Not Found</h1>
                    <p className="text-gray-700 mb-4">
                        The tariff you are trying to edit could not be found. It may have been deleted.
                    </p>
                    <button
                        onClick={() => router.push("/view-tariffs")}
                        className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                    >
                        Return to Tariffs List
                    </button>
                </div>
            </div>
        );
    }

    return (
        <main className="min-h-screen bg-gradient-to-br from-white to-blue-200 flex flex-col items-center justify-start p-8">
            <h1 className="text-3xl font-bold mb-6 text-black">Edit Tariff</h1>

            <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md">
                <div className="mb-6 bg-blue-50 p-4 rounded-md">
                    <h2 className="text-lg font-semibold text-gray-800 mb-2">Tariff Information</h2>
                    <div className="grid grid-cols-2 gap-3 text-sm">
                        <div>
                            <p className="font-medium text-gray-600">Exporter:</p>
                            <p className="text-gray-800">{tariff.exporterName}</p>
                        </div>
                        <div>
                            <p className="font-medium text-gray-600">Importer:</p>
                            <p className="text-gray-800">{tariff.importerName}</p>
                        </div>
                        <div>
                            <p className="font-medium text-gray-600">HS Code:</p>
                            <p className="text-gray-800">{tariff.HSCode}</p>
                        </div>
                        <div>
                            <p className="font-medium text-gray-600">Product:</p>
                            <p className="text-gray-800">{tariff.productDescription || "N/A"}</p>
                        </div>
                    </div>
                </div>

                <p className="text-sm text-gray-600 mb-4">
                    Fields marked with <span className="text-red-500">*</span> are required
                </p>

                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="rate">
                            Rate (%) <span className="text-red-500">*</span>
                        </label>
                        <input
                            name="rate"
                            type="number"
                            min="0"
                            step="0.01"
                            value={form.rate}
                            onChange={handleChange}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="effectiveDate">
                            Effective Date <span className="text-red-500">*</span>
                        </label>
                        <input
                            name="effectiveDate"
                            type="date"
                            value={form.effectiveDate}
                            onChange={handleChange}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="expiryDate">
                            Expiry Date
                        </label>
                        <input
                            name="expiryDate"
                            type="date"
                            value={form.expiryDate}
                            onChange={handleChange}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="reference">
                            Reference
                        </label>
                        <input
                            name="reference"
                            type="text"
                            value={form.reference}
                            onChange={handleChange}
                            placeholder="Source URL"
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>

                    <div className="flex items-center justify-between">
                        <button
                            type="button"
                            onClick={() => router.push("/view-tariffs")}
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isSaving}
                            className={`flex items-center justify-center font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline ${isSaving
                                    ? 'bg-gray-400 cursor-not-allowed text-white'
                                    : 'bg-blue-500 hover:bg-blue-700 text-white'
                                }`}
                        >
                            {isSaving && <LoadingSpinner />}
                            {isSaving ? 'Updating...' : 'Update Tariff'}
                        </button>
                    </div>

                    {/* Success Message */}
                    <SuccessDisplay message={successMessage} />

                    {/* Error Messages */}
                    <ErrorDisplay errors={errors} />
                </form>
            </div>
        </main>
    );
}