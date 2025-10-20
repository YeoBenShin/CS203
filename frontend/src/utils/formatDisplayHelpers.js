const formatUnitOfCalculation = (unitOfCalculation) => {
    const unitMap = {
      'AV': '%', // Ad Valorem (percentage)
      'BBL': '/barrel',
      'C': '/°C',
      'CAR': '/carat',
      'CC': '/cm³',
      'CG': '/centigram',
      'CGM': '/component gram',
      'CKG': '/component kg',
      'CM': '/cm',
      'CM2': '/cm²',
      'CM3': '/cm³',
      'CTN': '/component ton',
      'CU': '/cubic',
      'CUR': '/Curie',
      'CY': '/clean yield',
      'CYK': '/clean yield kg',
      'D': '/denier',
      'DOZ': '/dozen',
      'DPC': '/dozen pieces',
      'DPR': '/dozen pairs',
      'DS': '/dose',
      'FBM': '/fiber meter',
      'G': '/gram',
      'GBQ': '/Gigabecquerel',
      'GCN': '/gross container',
      'GKG': '/gold content gram',
      'GM': '/gram',
      'GR': '/gross',
      'GRL': '/gross line',
      'GRS': '/gross',
      'GVW': '/gross vehicle weight',
      'HND': '/hundred units',
      'HUN': '/hundred units',
      'IRC': '/IRC unit',
      'JWL': '/jewel',
      'K': '/thousand units',
      'KCAL': '/kilocalorie',
      'KG': '/kg',
      'KHZ': '/kilohertz',
      'KM': '/kilometer',
      'KM3': '/kg per m³',
      'KN': '/kilonewton',
      'KTS': '/kg total sugar',
      'KVA': '/kilovolt-ampere',
      'KVAR': '/kilovolt-ampere reactive',
      'KW': '/kilowatt',
      'KWH': '/kilowatt-hour',
      'L': '/liter',
      'LIN': '/linear',
      'LNM': '/linear meter',
      'LTR': '/liter',
      'M': '/meter',
      'M2': '/m²',
      'M3': '/m³',
      'MBQ': '/Megabecquerel',
      'MC': '/millicurie',
      'MG': '/milligram',
      'MHZ': '/megahertz',
      'ML': '/milliliter',
      'MM': '/millimeter',
      'MPA': '/megapascal',
      'NA': 'N/A',
      'NO': '/number',
      'ODE': '/ozone depletion equivalent',
      'PCS': '/piece',
      'PF': '/proof',
      'PFL': '/proof liter',
      'PK': '/pack',
      'PRS': '/pair',
      'RPM': '/revolution per minute',
      'SBE': '/standard brick equivalent',
      'SME': '/m² equivalent',
      'SQ': '/square',
      'SQM': '/m²',
      'T': '/metric ton',
      'THS': '/thousand units',
      'TNV': '/ton raw value',
      'TON': '/ton',
      'V': '/volt',
      'W': '/watt',
      'WTS': '/weight',
      'X': 'No quantity data',
    };
    return unitMap[unitOfCalculation] || unitOfCalculation;
  };

  const formatRate = (rate) => {
    return `${(parseFloat(rate) * 100).toFixed(2)}%`;
  };


  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateForInput = (dateString) => {
    if (!dateString) return "";
    return new Date(dateString).toISOString().split('T')[0];
  };

  export {  formatUnitOfCalculation, formatRate, formatDate, formatDateForInput };