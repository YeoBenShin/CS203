// Mapping of tariff unit codes to their full names based on US tariff schedules
export const TARIFF_UNIT_NAMES = {
  "AV": "Ad Valorem",
  "BBL": "Barrel",
  "C": "Carat",
  "CAR": "Car",
  "CC": "Cubic Centimeter",
  "CG": "Centigram",
  "CGM": "Centigram",
  "CKG": "Centigrade Kilogram",
  "CM": "Centimeter",
  "CM2": "Square Centimeter",
  "CM3": "Cubic Centimeter",
  "CTN": "Carton",
  "CU": "Cubic",
  "CUR": "Curie",
  "CY": "Cubic Yard",
  "CYK": "Clean Yield Kilogram",
  "D": "Dozen",
  "DOZ": "Dozen",
  "DPC": "Dozen Pieces",
  "DPR": "Dozen Pairs",
  "DS": "Dosage",
  "FBM": "Foot Board Measure",
  "G": "Gram",
  "GBQ": "Gigabecquerel",
  "GCN": "Gross Content",
  "GKG": "Gross Kilogram",
  "GM": "Gram",
  "GR": "Gross",
  "GRL": "Gross Liter",
  "GRS": "Gross",
  "GVW": "Gross Vehicle Weight",
  "HND": "Hundred",
  "HUN": "Hundred",
  "IRC": "Individual Retail Container",
  "JWL": "Jewel",
  "K": "Kilogram",
  "KCAL": "Kilocalorie",
  "KG": "Kilogram",
  "KHZ": "Kilohertz",
  "KM": "Kilometer",
  "KM3": "Cubic Kilometer",
  "KN": "Kilonewton",
  "KTS": "Kit",
  "KVA": "Kilovolt Ampere",
  "KVAR": "Kilovolt Ampere Reactive",
  "KW": "Kilowatt",
  "KWH": "Kilowatt Hour",
  "L": "Liter",
  "LIN": "Linear",
  "LNM": "Linear Meter",
  "LTR": "Liter",
  "M": "Meter",
  "M2": "Square Meter",
  "M3": "Cubic Meter",
  "MBQ": "Megabecquerel",
  "MC": "Millicurie",
  "MG": "Milligram",
  "MHZ": "Megahertz",
  "ML": "Milliliter",
  "MM": "Millimeter",
  "MPA": "Megapascal",
  "NA": "Not Applicable",
  "NO": "Number",
  "ODE": "Ounce Dry Equivalent",
  "PCS": "Pieces",
  "PF": "Proof",
  "PFL": "Proof Liter",
  "PK": "Pack",
  "PRS": "Pairs",
  "RPM": "Revolution Per Minute",
  "SBE": "Square Meter Equivalent",
  "SME": "Square Meter Equivalent",
  "SQ": "Square",
  "SQM": "Square Meter",
  "T": "Metric Ton",
  "THS": "Thousand",
  "TNV": "Ton Net Volume",
  "TON": "Ton",
  "V": "Volt",
  "W": "Watt",
  "WTS": "Watt",
  "X": "Pack",
};

/**
 * Get the full name for a tariff unit code
 * @param {string} unitCode - The unit code (e.g., "AV", "KG")
 * @returns {string} - Full name with code (e.g., "Ad Valorem (AV)", "Kilogram (KG)")
 */
export function getTariffUnitDisplay(unitCode) {
  if (!unitCode) return "";
  
  const fullName = TARIFF_UNIT_NAMES[unitCode];
  if (fullName) {
    return `${fullName} (${unitCode})`;
  }
  
  // Return just the code if no mapping found
  return unitCode;
}

/**
 * Get options array for dropdowns with full names
 * @param {Array<string>} unitCodes - Array of unit codes
 * @returns {Array<{label: string, value: string}>} - Options for react-select
 */
export function getTariffUnitOptions(unitCodes) {
  return unitCodes.map(code => ({
    label: getTariffUnitDisplay(code),
    value: code
  }));
}
