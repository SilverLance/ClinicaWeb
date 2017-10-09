package br.com.asfecer.dao.exceptions;

public class NonexistentEntityException extends RuntimeException {
    public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public NonexistentEntityException(String message) {
        super(message);
    }
}
