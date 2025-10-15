package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;

public interface CalculationService {
    CalculationResult calculateTariff(CalculationRequest request);
}