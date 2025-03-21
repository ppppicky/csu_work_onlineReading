package org.example.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(200, "Success", data);
    }

    public static StandardResponse<?> error(int code, String message) {
        return new StandardResponse<>(code, message, null);
    }
}