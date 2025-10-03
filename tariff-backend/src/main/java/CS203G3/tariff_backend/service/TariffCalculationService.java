package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCalculationMap;

import java.util.List;
import java.math.BigDecimal;

public interface TariffCalculationService {
    CalculationResult calculate(List<TariffCalculationMap> tariffRates, BigDecimal productValue);
}
