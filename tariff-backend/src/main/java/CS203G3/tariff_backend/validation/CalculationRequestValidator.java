package CS203G3.tariff_backend.validation;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Component
public class CalculationRequestValidator {

    public void validate(CalculationRequest request) {
        List<String> errors = new ArrayList<>();

        // Validate HSCode
        if (request.gethsCode() < 0 || request.gethsCode() > 9999999999L) {
            errors.add("HS Code must be a 10 digit number");
        }

        // Validate Country code
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            errors.add("Country code cannot be empty");
        } else if (!request.getCountry().matches("^[A-Z]{2,3}$")) {
            errors.add("Country code must be 2-3 uppercase letters");
        }

        // Validate Shipping Cost
        if (request.getShippingCost() == null) {
            errors.add("Shipping cost cannot be null");
        } else if (request.getShippingCost().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Shipping cost must be greater than 0");
        }

        // Validate Trade Date
        if (request.getTradeDate() == null) {
            errors.add("Trade date cannot be null");
        } else {
            // Date currentDate = new Date(System.currentTimeMillis());
            // if (request.getTradeDate().after(currentDate)) {
            //     errors.add("Trade date cannot be in the future");
            // }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Calculation request validation failed", errors);
        }
    }
}