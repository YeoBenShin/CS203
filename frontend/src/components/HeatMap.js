"use client";

import React, { useState, memo, useMemo, useEffect, useCallback } from "react";
import { ComposableMap, Geographies, Geography, ZoomableGroup } from "react-simple-maps";
import { scaleLinear } from "d3-scale";
import { Tooltip } from "react-tooltip";
import { useAuth } from "@clerk/nextjs";

import FieldSelector from "./FieldSelector";
import fetchApi from "@/utils/fetchApi";

const MemoGeography = memo(({ 
  geography, 
  fillColor, 
  onCountryClick,
  tooltipContent
}) => {
  // Create a stable click handler that won't change on re-renders
  const handleClick = useCallback(() => {
    onCountryClick(geography);
  }, [geography, onCountryClick]);

  return (
    <Geography
      geography={geography}
      onClick={handleClick}
      // Pass tooltip content declaratively to avoid state updates on hover
      data-tooltip-id="map-tooltip"
      data-tooltip-content={tooltipContent}
      style={{
        default: {
          fill: fillColor,
          stroke: "#ffffff",
          strokeWidth: 0.5,
          outline: "none",
        },
        hover: {
          fill: fillColor,
          stroke: "#000000",
          strokeWidth: 1,
          cursor: "pointer",
        },
        pressed: {
          fill: fillColor,
          stroke: "#000000",
          strokeWidth: 1,
        },
      }}
    />
  );
});

MemoGeography.displayName = "MemoGeography";

const HeatMap = ({ onCountrySelect }) => {
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [importingCountry, setImportingCountry] = useState(null);
  const [tariffs, setTariffs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [countryOptions, setCountryOptions] = useState([]);
  const [selectedCountryDetails, setSelectedCountryDetails] = useState(null);
  const [zoom, setZoom] = useState(1);
  const [center, setCenter] = useState([0, 20]);
  const { getToken } = useAuth();

  // Fetch products for dropdown
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = await getToken();
        const response = await fetchApi(token, "/api/products");
        const data = await response.json();

        const mappedProducts = data.map((product) => ({
          label: `${product.hsCode}${
            product.description ? ` - ${product.description}` : ""
          }`,
          value: product.hsCode,
        }));

        setProducts(mappedProducts);
      } catch (err) {
        setError("Failed to load products");
        console.error("Error fetching products:", err);
      }
    };
    fetchProducts();
  }, []);

  // Fetch tariff data when product or importing country changes
  useEffect(() => {
    const fetchTariffData = async () => {
      if (!selectedProduct || !importingCountry) {
        setTariffs([]);
        return;
      }

      setLoading(true);
      setError(null);
      try {
        console.log("Fetching tariff data for:", {
          productId: selectedProduct.value,
          importingCountry: importingCountry.value,
        });
        const token = await getToken();
        if (!importingCountry?.value) {
          throw new Error("Importing country is required");
        }

        // Get all tariffs for this product and importing country combination
        const response = await fetchApi(
          token,
          `/api/tariffs/search/${selectedProduct.value}/${importingCountry.value}`
        );
        const data = await response.json();
        console.log("Tariffs data received:", data);

        const processedTariffs = [];

        if (Array.isArray(data)) {
          data.forEach((tariff) => {
            // We only care about the exporter code since we're showing tariffs for exporters to the selected importing country
            const exporterCode = tariff.exporterCode;
            if (!exporterCode) return;

            const tariffRates = tariff.tariffRates || [];
            const highestRate =
              tariffRates.length > 0
                ? Math.max(...tariffRates.map((r) => parseFloat(r.rate) || 0))
                : 0;

            processedTariffs.push({
              countryname: exporterCode,
              tariffrate: highestRate,
              details: {
                countryName: tariff.exporterName,
                rates: tariffRates.map((rate) => ({
                  type: rate.unitOfCalculation,
                  rate: parseFloat(rate.rate) || 0,
                  id: rate.tariffRateID,
                })),
                effectiveDate: tariff.effectiveDate,
                expiryDate: tariff.expiryDate,
                reference: tariff.reference,
                hSCode: tariff.hSCode,
                productDescription: tariff.productDescription,
              },
            });
          });
        }

        setTariffs(processedTariffs);
      } catch (err) {
        setError("Failed to load tariff data");
        console.error("Error fetching tariff data:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchTariffData();
  }, [selectedProduct, importingCountry, getToken]);

  // Load countries for dropdown from backend API
  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const token = await getToken();
        const response = await fetchApi(token, "/api/countries");
        const countries = await response.json();

        const options = countries.map((country) => ({
          value: country.isoCode,
          label: country.name,
        }));

        console.log("Countries loaded from backend:", options);
        setCountryOptions(options);
      } catch (error) {
        console.error("Error loading country data:", error);
        setError("Failed to load country data");
      }
    };
    fetchCountries();
  }, [getToken]);

  // allow mapping and mouse hover through coordinate system
  const geoUrl =
    "https://raw.githubusercontent.com/datasets/geo-countries/master/data/countries.geojson";

  // Convert tariff list into lookup map
  const tariffDataMap = useMemo(() => {
    console.log("Raw tariffs before processing:", tariffs); // Debug the raw data
    const map = {};
    tariffs.forEach((item) => {
      if (!item || !item.countryname) {
        console.warn("Skipping invalid tariff item:", item);
        return;
      }
      // Ensure the country code is valid
      const code = item.countryname.trim().toUpperCase();
      if (code.length !== 3) {
        console.warn("Invalid country code:", code);
        return;
      }

      // Ensure we have valid rates
      if (!item.details || !Array.isArray(item.details.rates)) {
        console.warn("Missing or invalid rates for country:", code);
        return;
      }

      const validRates = item.details.rates.filter(
        (rate) => rate && typeof rate.rate === "number" && !isNaN(rate.rate)
      );

      map[code] = {
        countryName: item.details.countryName || code,
        rates: validRates,
        maxRate:
          validRates.length > 0
            ? Math.max(...validRates.map((r) => r.rate))
            : 0,
        details: item.details, // Store all details for the click handler
      };
    });
    console.log("Processed tariff map:", map); // Debug the processed map
    return map;
  }, [tariffs]);

  // Memoize maxTariffValue calculation
  const maxTariffValue = useMemo(() => 
    Math.max(
      0,
      ...Object.values(tariffDataMap).map((t) => t.maxRate)
    )
  , [tariffDataMap]);

  // Memoize the color scale
  const colorScale = useMemo(() => 
    scaleLinear()
      .domain([0, maxTariffValue || 1]) // Use || 1 to prevent domain [0, 0]
      .range(["#ffeda0", "#f03b20"])
      .clamp(true)
  , [maxTariffValue]);

  const handleCountryClick = useCallback(
    (geo) => {
      const p = geo.properties ?? {};
      const iso3 = (p["ISO3166-1-Alpha-3"] || "").trim().toUpperCase();
      const countryName = p.name || p.ADMIN || p.NAME || iso3;
      const tariffData = tariffDataMap[iso3];

      console.log("Clicked:", { countryName, iso3 });

      if (tariffData && tariffData.details) {
        setSelectedCountryDetails({
          countryCode: iso3,
          countryName: tariffData.countryName,
          rates: tariffData.rates,
          maxRate: tariffData.maxRate,
          hSCode: tariffData.details.hSCode,
          productDescription: tariffData.details.productDescription,
          effectiveDate: tariffData.details.effectiveDate,
          expiryDate: tariffData.details.expiryDate,
          reference: tariffData.details.reference,
        });
      } else {
        // Clear details if clicking a country with no data
        setSelectedCountryDetails(null);
      }
    },
    [tariffDataMap]
  );

  const handleZoomIn = useCallback(() => {
    if (zoom < 16) {
      setZoom(zoom * 1.5);
    }
  }, [zoom]);

  const handleZoomOut = useCallback(() => {
    if (zoom > 1) {
      setZoom(zoom / 1.5);
    }
  }, [zoom]);

  const handleResetZoom = useCallback(() => {
    setZoom(1);
    setCenter([0, 20]);
  }, []);

  const handleMoveEnd = useCallback((position) => {
    setCenter(position.coordinates);
    setZoom(position.zoom);
  }, []);

  const renderMap = () => {
    if (loading)
      return <div className="text-center py-4">Loading tariff data...</div>;
    if (error)
      return <div className="text-red-500 text-center py-4">{error}</div>;
    if (!selectedProduct)
      return (
        <div className="text-center py-4">
          Please select a product to view tariff data
        </div>
      );
    if (!importingCountry)
      return (
        <div className="text-center py-4">
          Please select an importing country to view tariff data
        </div>
      );

    return (
      <div className="relative">
        <ComposableMap 
          id="map-tooltip" 
          projection="geoMercator"
          projectionConfig={{
            scale: 147,
          }}
        >
          <ZoomableGroup
            zoom={zoom}
            center={center}
            onMoveEnd={handleMoveEnd}
            maxZoom={16}
            minZoom={1}
          >
            <Geographies geography={geoUrl}>
              {({ geographies }) =>
                geographies.map((geo) => {
                  const p = geo.properties ?? {};
                  const iso3 = (p["ISO3166-1-Alpha-3"] || "").trim().toUpperCase();
                  const countryName = p.name || p.ADMIN || p.NAME || iso3;

                  const tariffData = tariffDataMap[iso3];
                  const hasTariff = tariffData && tariffData.maxRate > 0;
                  const isImportingCountry = importingCountry?.value === iso3;

                  let fillColor;
                  if (isImportingCountry) {
                    fillColor = "#4a90e2";
                  } else if (hasTariff) {
                    fillColor = colorScale(tariffData.maxRate);
                  } else {
                    fillColor = "#f0f0f0ff";
                  }

                  // Calculate tooltip text here instead of in an event handler
                  let tooltipText;
                  if (isImportingCountry) {
                    tooltipText = `${countryName} (Importing Country)`;
                  } else if (tariffData && tariffData.maxRate > 0) {
                    tooltipText = `${countryName}: ${tariffData.maxRate.toFixed(2)}%`;
                  } else {
                    tooltipText = `${countryName} (No tariff data)`;
                  }

                  return (
                    <MemoGeography
                      key={geo.rsmKey}
                      geography={geo}
                      fillColor={fillColor}
                      onCountryClick={handleCountryClick}
                      tooltipContent={tooltipText}
                    />
                  );
                })
              }
            </Geographies>
          </ZoomableGroup>
        </ComposableMap>

        {/* Zoom Controls */}
        <div className="absolute top-4 right-4 flex flex-col gap-2 bg-white rounded-lg shadow-lg p-2">
          <button
            onClick={handleZoomIn}
            disabled={zoom >= 16}
            className="w-10 h-10 flex items-center justify-center bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
            title="Zoom in"
          >
            <span className="text-xl font-bold">+</span>
          </button>
          <button
            onClick={handleZoomOut}
            disabled={zoom <= 1}
            className="w-10 h-10 flex items-center justify-center bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
            title="Zoom out"
          >
            <span className="text-xl font-bold">−</span>
          </button>
          <button
            onClick={handleResetZoom}
            className="w-10 h-10 flex items-center justify-center bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors text-xs"
            title="Reset zoom"
          >
            ↺
          </button>
        </div>

        {/* Zoom Level Indicator */}
        <div className="absolute bottom-4 right-4 bg-white rounded-lg shadow-lg px-3 py-1 text-sm">
          Zoom: {zoom.toFixed(1)}x
        </div>
      </div>
    );
  };

  return (
    <div className="space-y-4 max-w-4xl mx-auto">
      <div className="grid grid-cols-1 gap-6">
        <div className="w-full">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Select Product <span className="text-red-500">*</span>
          </label>
          <FieldSelector
            value={selectedProduct}
            onChange={setSelectedProduct}
            options={products}
            placeholder="Select a product (required)"
          />
        </div>
        <div className="w-full">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Importing Country <span className="text-red-500">*</span>
          </label>
          <FieldSelector
            value={importingCountry}
            onChange={setImportingCountry}
            options={countryOptions}
            placeholder="Click on the map or search (required)"
          />
        </div>
      </div>

      {renderMap()}
      
      <Tooltip
        id="map-tooltip"
        place="top"
        float={true}
        delayShow={0}
        delayHide={0}
        className="bg-white text-black p-2 shadow-lg rounded-lg border"
        style={{
          zIndex: 1000,
          transition: "all 0.1s ease-out",
        }}
      />

      {/* Configuration Panel */}
      {selectedProduct && (
        <div className="mt-4 p-4 bg-gray-50 rounded-lg">
          <h3 className="font-semibold">Selected Configuration:</h3>
          <p>Product: {selectedProduct.label}</p>
          {importingCountry && (
            <p className="mt-2">Importing Country: {importingCountry.label}</p>
          )}
        </div>
      )}

      {/* Country Details Panel */}
      {selectedCountryDetails && (
        <div className="mt-4 p-4 bg-white shadow-lg rounded-lg border">
          <div className="flex justify-between items-start">
            <h3 className="text-lg font-bold">
              {selectedCountryDetails.countryName}
            </h3>
            <button
              onClick={() => setSelectedCountryDetails(null)}
              className="text-gray-500 hover:text-gray-700"
            >
              ×
            </button>
          </div>

          <div className="mt-4 space-y-4">
            <div>
              <h4 className="font-semibold text-gray-700">
                Product Information
              </h4>
              <p>HS Code: {selectedCountryDetails.hSCode}</p>
              <p>{selectedCountryDetails.productDescription}</p>
            </div>

            <div>
              <h4 className="font-semibold text-gray-700">Tariff Rates</h4>
              <div className="mt-2 space-y-2">
                {selectedCountryDetails.rates.map((rate, idx) => (
                  <div
                    key={idx}
                    className="flex justify-between bg-gray-50 p-2 rounded"
                  >
                    <span>{rate.type}</span>
                    <span className="font-semibold">
                      {rate.rate.toFixed(2)}%
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div className="text-sm text-gray-600">
              <div className="flex justify-between mt-2">
                <span>Effective Date:</span>
                <span>{selectedCountryDetails.effectiveDate}</span>
              </div>
              <div className="flex justify-between mt-1">
                <span>Expiry Date:</span>
                <span>{selectedCountryDetails.expiryDate}</span>
              </div>
              <div className="flex justify-between mt-1">
                <span>Reference:</span>
                <span>{selectedCountryDetails.reference}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default memo(HeatMap);
