package CS203G3.tariff_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        final BigDecimal hundred = BigDecimal.valueOf(100);
        final RoundingMode RM = RoundingMode.HALF_UP;

        for (TariffCalculationMap tMap : tariffRates) {
            BigDecimal rate = tMap.getRate() != null ? tMap.getRate() : BigDecimal.ZERO;
            BigDecimal subCost;

            if (tMap.getUnitOfCalculation() == UnitOfCalculation.AV) {
                BigDecimal base = productValue != null ? productValue : BigDecimal.ZERO;
                subCost = base.multiply(rate).divide(hundred, 6, RM).setScale(2, RM);
            } else {
                BigDecimal qty = tMap.getValue() != null ? tMap.getValue() : BigDecimal.ZERO;
                subCost = qty.multiply(rate).setScale(2, RM);
            }

            TariffBreakdown breakdown = new TariffBreakdown(
                tMap.getUnitOfCalculation(),
                rate,
                subCost
            );
            breakdownList.add(breakdown);
            totalTariffCost = totalTariffCost.add(subCost);
        }

        BigDecimal baseValue = productValue != null ? productValue : BigDecimal.ZERO;
        BigDecimal netTotal = baseValue.add(totalTariffCost);

        CalculationResult result = new CalculationResult(
                netTotal,
                null,
                null,
                null,
                null,
                breakdownList
        );
        return result;
    }
}
