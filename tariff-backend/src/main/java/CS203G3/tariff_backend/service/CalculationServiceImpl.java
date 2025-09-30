package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffForCalDisplayDto;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.validation.CalculationRequestValidator;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service for tariff-related calculations
 * Handles business logic for cost computations
 */
@Service
public class CalculationServiceImpl {

    @Autowired
    private TariffService tariffService;

    @Autowired
    private CalculationRequestValidator validator;

    private List<TariffForCalDisplayDto> convertToDto(List<Tariff> tariffs, BigDecimal shippingCost) {
        List<TariffForCalDisplayDto> dtoList = tariffs.stream().map(tariff -> {
            TariffForCalDisplayDto dto = new TariffForCalDisplayDto();
            dto.setTariffID(tariff.getTariffID());
            dto.setRate(tariff.getRate());
            dto.setEffectiveDate(tariff.getEffectiveDate());
            dto.setExpiryDate(tariff.getExpiryDate());
            dto.setReference(tariff.getReference());
            dto.setAmountApplied(tariff.getRate().multiply(shippingCost).setScale(2, RoundingMode.HALF_UP));
            return dto;
        }).toList();

        return dtoList;
    }

    private BigDecimal sumTariffRates(List<TariffForCalDisplayDto> tariffDtos) {
        return tariffDtos.stream()
                .map(TariffForCalDisplayDto::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumTariffAmounts(List<TariffForCalDisplayDto> tariffDtos) {
        return tariffDtos.stream()
                .map(TariffForCalDisplayDto::getAmountApplied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CalculationResult calculateTariff(CalculationRequest input) {
        // Validate input
        validator.validate(input);

        // Sanitize input
        String sanitizedCountry = input.getCountry().trim().toUpperCase();
        BigDecimal sanitizedShippingCost = input.getShippingCost().setScale(2, RoundingMode.HALF_UP);

        // Process calculation with sanitized input
        List<Tariff> tariffs = tariffService.getTariffRatesByCountries(sanitizedCountry, input.gethsCode(), input.getTradeDate());
        List<TariffForCalDisplayDto> tariffDtos = convertToDto(tariffs, sanitizedShippingCost);
        BigDecimal totalTariffRate = sumTariffRates(tariffDtos);
        BigDecimal totalTariffCost = sumTariffAmounts(tariffDtos);
        BigDecimal totalCost = sanitizedShippingCost.add(totalTariffCost).setScale(2, RoundingMode.HALF_UP);
        
        return new CalculationResult(totalCost, totalTariffRate, totalTariffCost, tariffDtos);
    }
}