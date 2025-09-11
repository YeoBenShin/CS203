class HSCodeList {
  constructor() {
    this.data = require('./tariffs.json'); 
    this.hscodeToDesc = {};
    this.descToHscode = {};

    this.data.forEach(item => {
      this.hscodeToDesc[item.hscode.toLowerCase()] = item.desc;
      this.descToHscode[item.desc.toLowerCase()] = item.hscode;
    });
  }

  // Get description by HS code
  getDesc(hscode) {
    return this.hscodeToDesc[hscode.toLowerCase()];
  }

  // Get HS code by description
  getHscode(desc) {
    return this.descToHscode[desc.toLowerCase()];
  }

  // Get all HS codes as array
  getHscodes() {
    return this.data.map(item => item.hscode);
  }

  // Get all descriptions as array
  getDescs() {
    return this.data.map(item => item.desc);
  }

  // Get raw data array
  getData() {
    return this.data;
  }

  // Set or update description by HS code
  setDesc(hscode, desc) {
    this.data.forEach(item => {
      if (item.hscode === hscode) {
        item.desc = desc;
        this.hscodeToDesc[hscode.toLowerCase()] = desc;
        this.descToHscode[desc.toLowerCase()] = hscode;
      }
    });
    return this;
  }

  // Add an empty HS code entry at the beginning
  setEmpty(desc) {
    this.data.unshift({
      hscode: '',
      desc: desc,
    });
    this.hscodeToDesc[''] = desc;
    this.descToHscode[desc.toLowerCase()] = '';
    return this;
  }

  getOptions() {
    return this.data.map(item => ({
        value: item.hscode,
        label: `${item.hscode} - ${item.desc}`
    }));
  }
}

// Factory function for convenience
const hsCodeList = function () {
  if (!(this instanceof HSCodeList)) return new HSCodeList();
};



module.exports = hsCodeList;
