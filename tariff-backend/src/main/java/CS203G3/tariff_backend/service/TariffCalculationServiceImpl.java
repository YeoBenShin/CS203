package CS203G3.tariff_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffBreakdown;
import CS203G3.tariff_backend.dto.TariffCalculationMap;

@Service
public class TariffCalculationServiceImpl implements TariffCalculationService {

    @Override
    public CalculationResult calculate(List<TariffCalculationMap> tariffRates, BigDecimal productValue) {
        if (tariffRates == null || productValue == null) {
            throw new NullPointerException();
        }
        
        List<TariffBreakdown> breakdownList = new ArrayList<>();
        BigDecimal netTotal = productValue;

        for (TariffCalculationMap tMap : tariffRates) {
            BigDecimal subCost = tMap.getRate().multiply(tMap.getValue()).setScale(2, RoundingMode.HALF_UP);
            TariffBreakdown breakdown = new TariffBreakdown(tMap.getUnitOfCalculation(), tMap.getRate(), subCost);

            breakdownList.add(breakdown);
            netTotal = netTotal.add(subCost);
        }

        CalculationResult result = new CalculationResult(
            netTotal.setScale(2, RoundingMode.HALF_UP),
            null, // tariffName
            null, // effectiveDate
            null, // expiryDate
            null, // reference
            breakdownList
        );
        return result;
    }
}
