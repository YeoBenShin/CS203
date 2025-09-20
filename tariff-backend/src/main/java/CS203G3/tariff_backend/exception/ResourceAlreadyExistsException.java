package CS203G3.tariff_backend.exception;

/**
 * Exception for resource already exists scenarios
 */
public class ResourceAlreadyExistsException extends BusinessException {
    
    public ResourceAlreadyExistsException(String resourceType, Object identifier) {
        super("RESOURCE_ALREADY_EXISTS", String.format("%s with identifier '%s' already exists", resourceType, identifier));
    }
}