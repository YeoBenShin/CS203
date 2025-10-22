import React, { useState, memo, useMemo } from "react";
import { ComposableMap, Geographies, Geography } from "react-simple-maps";
import { scaleLinear } from "d3-scale";
import { Tooltip } from "react-tooltip";

// ✅ Your dataset
const mockTariffs = [
  { countryname: "AUS", tariffrate: 15.2 },
  { countryname: "AUT", tariffrate: 42.8 },
  { countryname: "BEL", tariffrate: 55.9 },
  { countryname: "BRA", tariffrate: 67.3 },
  { countryname: "CAN", tariffrate: 23.4 },
  { countryname: "CHE", tariffrate: 51.1 },
  { countryname: "CHN", tariffrate: 45.0 },
  { countryname: "DEU", tariffrate: 58.7 },
  { countryname: "DNK", tariffrate: 53.9 },
  { countryname: "ESP", tariffrate: 49.3 },
  { countryname: "FIN", tariffrate: 35.7 },
  { countryname: "FRA", tariffrate: 52.2 },
  { countryname: "GBR", tariffrate: 26.4 },
  { countryname: "GRC", tariffrate: 19.8 },
  { countryname: "HUN", tariffrate: 46.1 },
  { countryname: "IDN", tariffrate: 51.4 },
  { countryname: "IND", tariffrate: 47.9 },
  { countryname: "IRL", tariffrate: 31.7 },
  { countryname: "ITA", tariffrate: 48.6 },
  { countryname: "JPN", tariffrate: 38.2 },
  { countryname: "KOR", tariffrate: 44.3 },
  { countryname: "LTU", tariffrate: 33.1 },
  { countryname: "LUX", tariffrate: 28.9 },
  { countryname: "MEX", tariffrate: 41.5 },
  { countryname: "NLD", tariffrate: 37.7 },
  { countryname: "NOR", tariffrate: 16.5 },
  { countryname: "POL", tariffrate: 46.0 },
  { countryname: "PRT", tariffrate: 27.6 },
  { countryname: "ROU", tariffrate: 32.1 },
  { countryname: "RUS", tariffrate: 13.4 },
  { countryname: "SGP", tariffrate: 10.2 },
  { countryname: "SWE", tariffrate: 40.5 },
  { countryname: "TUR", tariffrate: 43.9 },
  { countryname: "TWN", tariffrate: 34.8 },
  { countryname: "USA", tariffrate: 39.6 },
  { countryname: "ZAF", tariffrate: 21.2 },
  { countryname: "NZL", tariffrate: 14.7 },
  { countryname: "SAU", tariffrate: 29.1 },
  { countryname: "ARE", tariffrate: 25.3 },
  { countryname: "EGY", tariffrate: 28.5 },
  { countryname: "ARG", tariffrate: 54.1 },
  { countryname: "ISR", tariffrate: 31.9 },
  { countryname: "THA", tariffrate: 47.3 },
  { countryname: "VNM", tariffrate: 52.0 },
  { countryname: "MYS", tariffrate: 37.5 },
  { countryname: "PHL", tariffrate: 40.2 },
  { countryname: "PAK", tariffrate: 35.8 },
  { countryname: "NGA", tariffrate: 49.0 },
  { countryname: "KEN", tariffrate: 44.4 },
  { countryname: "UGA", tariffrate: 39.9 },
];

// ✅ GeoJSON URL
const geoUrl =
  "https://raw.githubusercontent.com/datasets/geo-countries/master/data/countries.geojson";

const HeatMap = () => {
  const [content, setContent] = useState("");

  // Convert tariff list into lookup map
  const tariffDataMap = useMemo(() => {
    const map = {};
    mockTariffs.forEach((item) => {
      const code = item.countryname.trim().toUpperCase();
      map[code] = item.tariffrate;
    });
    return map;
  }, []);

  const maxTariffValue = Math.max(0, ...Object.values(tariffDataMap));
  const colorScale = scaleLinear()
    .domain([0, maxTariffValue])
    .range(["#ffeda0", "#f03b20"])
    .clamp(true);

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

              // ✅ Use the actual ISO property from your GeoJSON
              const iso3 = (p["ISO3166-1-Alpha-3"] || "").trim().toUpperCase();
              const countryName = p.name || p.ADMIN || p.NAME || iso3;

              const tariffValue = tariffDataMap[iso3];
              const hasTariff = tariffValue !== undefined;

              const fillColor = hasTariff ? colorScale(tariffValue) : "#f0f0f0ff";

              return (
                <Geography
                  key={geo.rsmKey}
                  geography={geo}
                  onClick={() => handleCountryClick(countryName, iso3)}
                  onMouseEnter={() => {
                    const tooltipText = hasTariff
                      ? `${countryName}: ${tariffValue}%`
                      : `${countryName}: not-updated`;
                    setContent(tooltipText);
                  }}
                  onMouseLeave={() => setContent("")}
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
                      cursor: "pointer",
                    },
                    pressed: {
                      fill: fillColor,
                      stroke: "#000000",
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
