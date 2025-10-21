package CS203G3.tariff_backend.service;

import java.math.BigDecimal;
import java.util.List;

import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCalculationMap;

public interface TariffCalculationService {
    CalculationResult calculate(List<TariffCalculationMap> tariffRates, BigDecimal productValue);
}
