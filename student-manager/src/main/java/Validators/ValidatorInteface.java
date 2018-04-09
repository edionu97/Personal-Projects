package Validators;

public interface ValidatorInteface<T> {
    void validate(T obj) throws  ValidationException;
}
