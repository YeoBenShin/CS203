package CS203G3.tariff_backend.exception;

import CS203G3.tariff_backend.exception.tariff.SameCountryException;
import CS203G3.tariff_backend.exception.tariff.NegativeTariffRateException;
import CS203G3.tariff_backend.exception.tariff.InvalidTariffRateException;
import CS203G3.tariff_backend.exception.tariff.PastEffectiveDateException;
import CS203G3.tariff_backend.exception.tariff.ExpiryBeforeEffectiveException;
import CS203G3.tariff_backend.exception.tariff.OverlappingTariffPeriodException;
import CS203G3.tariff_backend.exception.tariff.DuplicateTariffMappingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for business rule exceptions
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(SameCountryException.class)
    public ResponseEntity<Map<String, Object>> handleSameCountryException(
            SameCountryException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NegativeTariffRateException.class)
    public ResponseEntity<Map<String, Object>> handleNegativeTariffRateException(
            NegativeTariffRateException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidTariffRateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTariffRateException(
            InvalidTariffRateException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(PastEffectiveDateException.class)
    public ResponseEntity<Map<String, Object>> handlePastEffectiveDateException(
            PastEffectiveDateException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ExpiryBeforeEffectiveException.class)
    public ResponseEntity<Map<String, Object>> handleExpiryBeforeEffectiveException(
            ExpiryBeforeEffectiveException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(OverlappingTariffPeriodException.class)
    public ResponseEntity<Map<String, Object>> handleOverlappingTariffPeriodException(
            OverlappingTariffPeriodException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(DuplicateTariffMappingException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTariffMappingException(
            DuplicateTariffMappingException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            BusinessException ex, HttpStatus status, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("errorCode", ex.getErrorCode());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorDetails, status);
    }
}