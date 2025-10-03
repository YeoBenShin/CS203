// package CS203G3.tariff_backend.validation;

// import CS203G3.tariff_backend.dto.TariffCreateDto;
// import CS203G3.tariff_backend.exception.ValidationException;
// import org.springframework.stereotype.Component;

// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Date;

// @Component
// public class TariffValidator {

//     public void validate(TariffCreateDto tariff) {
//         List<String> errors = new ArrayList<>();

//         // Validate Rate
//         if (tariff.getRate() == null) {
//             errors.add("Tariff rate cannot be null");
//         } else if (tariff.getRate().compareTo(BigDecimal.ZERO) < 0 || 
//                   tariff.getRate().compareTo(new BigDecimal("1")) > 0) {
//             errors.add("Tariff rate must be between 0 and 1");
//         }

//         // Validate HSCode
//         if (tariff.getHSCode() == null) {
//             errors.add("HS Code cannot be null");
//         } else if (tariff.getHSCode() < 0 || tariff.getHSCode() > 9999999999L) {
//             errors.add("HS Code must be a 10 digit number");
//         }

//         // Validate Country code
//         if (tariff.getExporter() == null || tariff.getExporter().trim().isEmpty()) {
//             errors.add("Exporter country code cannot be empty");
//         } else if (!tariff.getExporter().matches("^[A-Z]{2,3}$")) {
//             errors.add("Exporter country code must be 2-3 uppercase letters");
//         }

//         // Validate Dates
//         Date currentDate = new Date();
//         if (tariff.getEffectiveDate() == null) {
//             errors.add("Effective date cannot be null");
//         }
//         if (tariff.getExpiryDate() == null) {
//             errors.add("Expiry date cannot be null");
//         }
//         if (tariff.getEffectiveDate() != null && tariff.getExpiryDate() != null) {
//             if (tariff.getEffectiveDate().after(tariff.getExpiryDate())) {
//                 errors.add("Effective date must be before expiry date");
//             }
//         }

//         // Validate Reference (if provided)
//         if (tariff.getReference() != null && !tariff.getReference().trim().isEmpty()) {
//             if (tariff.getReference().length() > 255) {
//                 errors.add("Reference must not exceed 255 characters");
//             }
//             // Additional validation to prevent potential XSS
//             if (tariff.getReference().matches(".*[<>].*")) {
//                 errors.add("Reference contains invalid characters");
//             }
//         }

//         if (!errors.isEmpty()) {
//             throw new ValidationException("Tariff validation failed", errors);
//         }
//     }
// }