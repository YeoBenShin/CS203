package CS203G3.tariff_backend.service;

import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffBreakdown;
import CS203G3.tariff_backend.dto.TariffCalculationMap;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class TariffCalculationServiceImpl implements TariffCalculationService {
    
    @Override
    public CalculationResult calculate(List<TariffCalculationMap> tariffRates, BigDecimal productValue) {
        List<TariffBreakdown> breakdownList = new ArrayList<>();
        BigDecimal netTotal = productValue;

        for (TariffCalculationMap tMap : tariffRates) {
            BigDecimal subCost = tMap.getRate().multiply(tMap.getValue());
            TariffBreakdown breakdown = new TariffBreakdown(tMap.getUnitOfCalculation(), tMap.getRate(), subCost);

            breakdownList.add(breakdown);
            netTotal = netTotal.add(subCost);
        }

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
