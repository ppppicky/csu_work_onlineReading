package org.example.util;

public class GlobalException {
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class BookNotFoundException extends RuntimeException {

        public BookNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidPageException extends RuntimeException {
        public InvalidPageException(String message) {
            super(message);
        }
    }
}
