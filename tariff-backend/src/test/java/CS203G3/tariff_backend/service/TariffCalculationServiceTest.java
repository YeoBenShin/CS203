package CS203G3.tariff_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import CS203G3.tariff_backend.model.UnitOfCalculation;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCalculationMap;
import CS203G3.tariff_backend.service.TariffCalculationServiceImpl;

@ExtendWith(MockitoExtension.class)
class TariffCalculationServiceTest {

    @InjectMocks
    private TariffCalculationServiceImpl tariffCalculationService;

    private List<TariffCalculationMap> tariffRates;
    private BigDecimal productValue;

    @BeforeEach
    void setUp() {
        tariffRates = new ArrayList<>();
        productValue = new BigDecimal("1000.00");
    }

    @Test
    void calculate_WithSingleTariff_ReturnsCorrectCalculation() {
        // Arrange
        TariffCalculationMap tariff = new TariffCalculationMap(UnitOfCalculation.KG, new BigDecimal("0.10"), new BigDecimal(20.00));
        tariffRates.add(tariff);

        // Act
        CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("2.00"), result.getTotalTariffCost()); // 2
        assertEquals(new BigDecimal("1002.00"), result.getTotalCost()); // 1000 + 2
        assertEquals(new BigDecimal("0"), result.getTotalTariffRate()); // no AV
    }

    @Test
    void calculate_WithMultipleTariffs_ReturnsCorrectCalculation() {
        // Arrange
        TariffCalculationMap tariff1 = new TariffCalculationMap(UnitOfCalculation.AV, new BigDecimal("0.05"), new BigDecimal(1000));
        
        TariffCalculationMap tariff2 = new TariffCalculationMap(UnitOfCalculation.C, new BigDecimal(0.20), new BigDecimal(40.00));

        tariffRates.add(tariff1);
        tariffRates.add(tariff2);

        // Act
        CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("58.00"), result.getTotalTariffCost()); // 5% of 1000(50) + 40 * 0.20(8)
        assertEquals(new BigDecimal("1058.00"), result.getTotalCost()); // 1000 + 50 + 8
        assertEquals(new BigDecimal("0.05"), result.getTotalTariffRate());
    }

//     @Test
//     void calculate_WithEmptyTariffList_ReturnsZeroTariff() {
//         // Arrange - empty list already set in setUp

//         // Act
//         CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

//         // Assert
//         assertNotNull(result);
//         assertEquals(BigDecimal.ZERO, result.getTotalTariffCost());
//         assertEquals(productValue, result.getTotalCost());
//         assertEquals(BigDecimal.ZERO, result.getTotalTariffRate());
//     }

//     @Test
//     void calculate_WithZeroProductValue_ReturnsZeroTariffCost() {
//         // Arrange
//         TariffCalculationMap tariff = new TariffCalculationMap();
//         tariff.setRate(new BigDecimal("10.00"));
//         tariff.setUnit("PERCENTAGE");
//         tariffRates.add(tariff);
//         productValue = BigDecimal.ZERO;

//         // Act
//         CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

//         // Assert
//         assertNotNull(result);
//         assertEquals(BigDecimal.ZERO, result.getTotalTariffCost());
//         assertEquals(BigDecimal.ZERO, result.getTotalCost());
//     }

//     @Test
//     void calculate_WithNullTariffList_ThrowsException() {
//         // Arrange
//         tariffRates = null;

//         // Act & Assert
//         assertThrows(NullPointerException.class, () -> {
//             tariffCalculationService.calculate(tariffRates, productValue);
//         });
//     }

//     @Test
//     void calculate_WithNullProductValue_ThrowsException() {
//         // Arrange
//         TariffCalculationMap tariff = new TariffCalculationMap();
//         tariff.setRate(new BigDecimal("10.00"));
//         tariffRates.add(tariff);
//         productValue = null;

//         // Act & Assert
//         assertThrows(NullPointerException.class, () -> {
//             tariffCalculationService.calculate(tariffRates, productValue);
//         });
//     }

//     @Test
//     void calculate_WithHighTariffRate_ReturnsCorrectCalculation() {
//         // Arrange
//         TariffCalculationMap tariff = new TariffCalculationMap();
//         tariff.setRate(new BigDecimal("50.00")); // 50% tariff
//         tariff.setUnit("PERCENTAGE");
//         tariffRates.add(tariff);

//         // Act
//         CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

//         // Assert
//         assertNotNull(result);
//         assertEquals(new BigDecimal("500.00"), result.getTotalTariffCost()); // 50% of 1000
//         assertEquals(new BigDecimal("1500.00"), result.getTotalCost()); // 1000 + 500
//         assertEquals(new BigDecimal("50.00"), result.getTotalTariffRate());
//     }

//     @Test
//     void calculate_WithDecimalTariffRate_ReturnsCorrectCalculation() {
//         // Arrange
//         TariffCalculationMap tariff = new TariffCalculationMap();
//         tariff.setRate(new BigDecimal("2.50")); // 2.5% tariff
//         tariff.setUnit("PERCENTAGE");
//         tariffRates.add(tariff);

//         // Act
//         CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

//         // Assert
//         assertNotNull(result);
//         assertEquals(new BigDecimal("25.00"), result.getTotalTariffCost()); // 2.5% of 1000
//         assertEquals(new BigDecimal("1025.00"), result.getTotalCost()); // 1000 + 25
//         assertEquals(new BigDecimal("2.50"), result.getTotalTariffRate());
//     }

//     @Test
//     void calculate_WithLargeProductValue_ReturnsCorrectCalculation() {
//         // Arrange
//         TariffCalculationMap tariff = new TariffCalculationMap();
//         tariff.setRate(new BigDecimal("10.00"));
//         tariff.setUnit("PERCENTAGE");
//         tariffRates.add(tariff);
//         productValue = new BigDecimal("1000000.00"); // 1 million

//         // Act
//         CalculationResult result = tariffCalculationService.calculate(tariffRates, productValue);

//         // Assert
//         assertNotNull(result);
//         assertEquals(new BigDecimal("100000.00"), result.getTotalTariffCost()); // 10% of 1M
//         assertEquals(new BigDecimal("1100000.00"), result.getTotalCost());
//         assertEquals(new BigDecimal("10.00"), result.getTotalTariffRate());
//     }
}
