package CS203G3.tariff_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffBreakdown;
import CS203G3.tariff_backend.dto.TariffCalculationMap;
import CS203G3.tariff_backend.model.UnitOfCalculation;

@Service
public class TariffCalculationServiceImpl implements TariffCalculationService {
    
    @Override
    public CalculationResult calculate(List<TariffCalculationMap> tariffRates, BigDecimal productValue) {
        List<TariffBreakdown> breakdownList = new ArrayList<>();
        BigDecimal totalTariffCost = BigDecimal.ZERO;

        for (TariffCalculationMap tMap : tariffRates) {
            BigDecimal subCost;
            if (tMap.getUnitOfCalculation() == UnitOfCalculation.AV) {
            // Correctly calculate the percentage
                subCost = tMap.getValue().multiply(tMap.getRate().divide(new BigDecimal("100")));
            } else {
                // Logic for specific rates (e.g., per KG, per item) is correct
                subCost = tMap.getValue().multiply(tMap.getRate().divide(new BigDecimal("100")));
            }

            TariffBreakdown breakdown = new TariffBreakdown(tMap.getUnitOfCalculation(), tMap.getRate(), subCost);
            breakdownList.add(breakdown);
            totalTariffCost = totalTariffCost.add(subCost);
        }
        BigDecimal netTotal = productValue.add(totalTariffCost);

        CalculationResult result = new CalculationResult(
            netTotal,
            null, // tariffName
            null, // effectiveDate
            null, // expiryDate
            null, // reference
            breakdownList
        );
        return result;
    }
}
