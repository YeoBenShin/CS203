// package CS203G3.tariff_backend.exception;

// /**
//  * Exception for resource already exists scenarios
//  */
// public class ResourceAlreadyExistsException extends BusinessException {
    
//     public ResourceAlreadyExistsException(String resourceType, Object identifier) {
//         super("RESOURCE_ALREADY_EXISTS", String.format("%s with identifier '%s' already exists", resourceType, identifier));
//     }
// }

// /**
//  * Exception for invalid business operations
//  */
// public class InvalidOperationException extends BusinessException {
    
//     public InvalidOperationException(String message) {
//         super("INVALID_OPERATION", message);
//     }
// }

// /**
//  * Exception for validation failures
//  */
// public class ValidationException extends BusinessException {
    
//     public ValidationException(String message) {
//         super("VALIDATION_ERROR", message);
//     }
// }

// /**
//  * Exception for date-related business logic errors
//  */
// public class InvalidDateRangeException extends BusinessException {
    
//     public InvalidDateRangeException(String message) {
//         super("INVALID_DATE_RANGE", message);
//     }
// }

// /**
//  * Exception for tariff rate validation errors
//  */
// public class InvalidTariffRateException extends BusinessException {
    
//     public InvalidTariffRateException(String message) {
//         super("INVALID_TARIFF_RATE", message);
//     }
// }

// /**
//  * Exception for duplicate tariff mapping combinations
//  */
// public class DuplicateTariffMappingException extends BusinessException {
    
//     public DuplicateTariffMappingException(String exporter, String importer, Integer hsCode) {
//         super("DUPLICATE_TARIFF_MAPPING", 
//               String.format("Tariff mapping already exists for %s -> %s, HSCode: %d", exporter, importer, hsCode));
//     }
// }

// /**
//  * Exception for calculation errors
//  */
// public class CalculationException extends BusinessException {
    
//     public CalculationException(String message) {
//         super("CALCULATION_ERROR", message);
//     }
    
//     public CalculationException(String message, Throwable cause) {
//         super("CALCULATION_ERROR", message, cause);
//     }
// }