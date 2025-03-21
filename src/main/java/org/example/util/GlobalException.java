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
    public static class BookTypeNotFoundException extends RuntimeException {

        public BookTypeNotFoundException(String message) {
            super(message);
        }
    }
    public static class ChapterNotFoundException extends RuntimeException {

        public ChapterNotFoundException(String message) {
            super(message);
        }
    }
    public static class FontNotFoundException extends RuntimeException {

        public FontNotFoundException(String message) {super(message);
        }
    }
    public static class BackgroundNotFoundException extends RuntimeException {

        public BackgroundNotFoundException(String message) {
            super(message);
        }
    }
    public static class ReadingSettingNotFoundException extends RuntimeException {

        public ReadingSettingNotFoundException(String message) {
            super(message);
        }
    }
}
