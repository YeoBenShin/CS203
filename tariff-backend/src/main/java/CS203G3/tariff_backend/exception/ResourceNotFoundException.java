package CS203G3.tariff_backend.exception;

/**
 * Exception for resource not found scenarios
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super("RESOURCE_NOT_FOUND", String.format("%s with id '%s' not found", resourceType, id));
    }
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}