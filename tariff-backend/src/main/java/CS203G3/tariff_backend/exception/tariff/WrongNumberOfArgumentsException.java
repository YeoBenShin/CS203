package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when wrong number of arguments are provided
 */
public class WrongNumberOfArgumentsException extends BusinessException {
    
    public WrongNumberOfArgumentsException(String operation, int expected, int actual) {
        super("WRONG_NUMBER_OF_ARGUMENTS", 
              String.format("Wrong number of arguments for %s. Expected: %d, Actual: %d", 
                            operation, expected, actual));
    }
    
    public WrongNumberOfArgumentsException(String message) {
        super("WRONG_NUMBER_OF_ARGUMENTS", message);
    }
}