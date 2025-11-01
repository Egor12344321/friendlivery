package com.globallogix.flight.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private Long timestamp = System.currentTimeMillis();




    public static <T> CommonResponse<T> success(T data){
        return new CommonResponse<>(true, "Операция успешно выполнена", data, null, System.currentTimeMillis());
    }

    public static <T> CommonResponse<T> success(T data, String message){
        return new CommonResponse<>(true, message, data, null, System.currentTimeMillis());
    }

    public static CommonResponse<Void> success(String message){
        return new CommonResponse<>(true, message, null, null, System.currentTimeMillis());
    }

    public static <T> CommonResponse<T> error(String message){
        return new CommonResponse<>(false, message, null, null, System.currentTimeMillis());
    }

    public static <T> CommonResponse<T> error(String message, String errorCode) {
        return new CommonResponse<>(false, message, null, errorCode, System.currentTimeMillis());
    }

}
