import React, { useState, memo, useMemo } from "react";
import { ComposableMap, Geographies, Geography } from "react-simple-maps";
import { scaleLinear } from "d3-scale";
import { Tooltip } from "react-tooltip";

// The tariff data is now placed directly inside the component file.
// This removes any issues related to incorrect file paths.
const mockTariffs = [
  { "countryname": "USA", "tariffrate": 3.4 },
  { "countryname": "CHN", "tariffrate": 7.6 },
  { "countryname": "JPN", "tariffrate": 2.5 },
  { "countryname": "DEU", "tariffrate": 2.1 },
  { "countryname": "GBR", "tariffrate": 2.1 },
  { "countryname": "IND", "tariffrate": 6.9 },
  { "countryname": "FRA", "tariffrate": 2.1 },
  { "countryname": "ITA", "tariffrate": 2.1 },
  { "countryname": "BRA", "tariffrate": 10.2 },
  { "countryname": "CAN", "tariffrate": 3.1 },
  { "countryname": "RUS", "tariffrate": 5.3 },
  { "countryname": "AUS", "tariffrate": 2.6 },
  { "countryname": "MEX", "tariffrate": 5.8 },
  { "countryname": "IDN", "tariffrate": 5.7 },
  { "countryname": "SAU", "tariffrate": 4.9 },
  { "countryname": "TUR", "tariffrate": 8.1 },
  { "countryname": "CHE", "tariffrate": 1.8 },
  { "countryname": "ARG", "tariffrate": 11.5 },
  { "countryname": "NGA", "tariffrate": 12.7 },
  { "countryname": "ZAF", "tariffrate": 7.6 }
];

// Use a dataset that provides ISO_A3 and names in properties
const geoUrl = "https://raw.githubusercontent.com/datasets/geo-countries/master/data/countries.geojson";

const HeatMap = () => {
  const [content, setContent] = useState("");

  // Convert the array of tariffs into a map for efficient lookups
  const tariffDataMap = useMemo(() => {
    const map = {};
    if (Array.isArray(mockTariffs)) {
      mockTariffs.forEach(item => {
        // FIX: Trim the country name to remove any hidden whitespace
        if (item.countryname && typeof item.tariffrate === 'number') {
          map[item.countryname.trim()] = item.tariffrate;
        }
      });
    }
    return map;
  }, []);

  // Dynamically calculate the maximum tariff from the data
  const maxTariffValue = Math.max(0, ...Object.values(tariffDataMap));

  // A vibrant color scale mapping the lowest tariff to light yellow and the highest to a strong red
  const colorScale = scaleLinear().domain([0, maxTariffValue]).range(["#ffeda0", "#f03b20"]).clamp(true);

  const handleCountryClick = (countryName, iso3) => {
    console.log("Clicked:", { countryName, iso3 });
  };

  return (
    <>
      <ComposableMap data-tooltip-id="my-tooltip" data-tooltip-float>
        <Geographies geography={geoUrl}>
          {({ geographies }) =>
            geographies.map((geo) => {
              const p = geo.properties ?? {};
              // FIX: Trim the ISO code from the map data to ensure a clean match
              let iso3 =
                (p.ISO_A3 || p.ADM0_A3 || p.iso_a3 || p.A3 || p.id || "").toString().toUpperCase().trim();
              
              if (iso3 === "-99") {
                iso3 = (p.ADM0_A3 || p.A3 || "").toString().toUpperCase().trim();
              }
              const countryName = p.ADMIN || p.NAME || p.NAME_LONG || p.name || iso3 || "Unknown";

              // Look up the tariff value from our created map
              const tariffValue = tariffDataMap[iso3];
              const hasTariff = tariffValue !== undefined && tariffValue !== null;
              const fillColor = hasTariff ? colorScale(tariffValue) : "#f0f0f0ff";

              return (
                <Geography
                  key={geo.rsmKey}
                  geography={geo}
                  onClick={() => handleCountryClick(countryName, iso3)}
                  onMouseEnter={() => {
                    const tooltipText = hasTariff
                      ? `${iso3}: ${tariffValue}%`
                      : `${countryName}: not-updated`;
                    setContent(tooltipText);
                  }}
                  onMouseLeave={() => setContent("")}
                  style={{
                    default: {
                      fill: fillColor,
                      stroke: "#ffffffff",
                      strokeWidth: 1,
                      vectorEffect: "non-scaling-stroke",
                      outline: "none",
                    },
                    hover: {
                      fill: fillColor,
                      stroke: "#ffffffff",
                      outline: "none",
                      cursor: "pointer",
                    },
                    pressed: {
                      fill: fillColor,
                      stroke: "#ffffffff",
                      strokeWidth: 1,
                      vectorEffect: "non-scaling-stroke",
                      outline: "none",
                    },
                  }}
                />
              );
            })
          }
        </Geographies>
      </ComposableMap>
      <Tooltip id="my-tooltip" content={content} />
    </>
  );
};

export default memo(HeatMap);

