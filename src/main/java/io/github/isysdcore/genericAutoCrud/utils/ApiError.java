/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ApiError is a class that represents an error response in the API.
 * It contains information about the error status, timestamp, message, details,
 * exception, and a list of errors.
 * This class is used to standardize error responses across the API.
 * @author domingos.fernando
 */
@Getter
@Setter
public class ApiError
{

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String ex;
    private List<String> errors;
    @JsonIgnore
    private ArrayList<Object> objects;

    /**
     * Empty default constructor
     */
    public ApiError()
    {
        super();
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Constructor base with http status and the error message
     * @param status The http status of response
     * @param message The description of the error or operation
     */
    public ApiError(HttpStatus status, String message)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
    }

    /**
     * Constructor base with http status, the final message and other errors
     * @param status The http status of response
     * @param message The description of the error or operation
     * @param errors The List of other errors founded
     */
    public ApiError(HttpStatus status, String message, List<String> errors)
    {
        super();
        this.setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    /**
     * Constructor base with http status, the final message and other errors
     * @param status The http status of response
     * @param message The description of the error or operation
     * @param errors The List of other errors founded
     * @param ex The exception founded to return
     */
    public ApiError(HttpStatus status, String message, List<String> errors, String ex)
    {
        super();
        this.setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.ex = ex;
    }

    /**
     * Constructor base with http status, the final message and other errors
     * @param status The http status of response
     * @param message The description of the error or operation
     * @param error The error message detailed
     */
    public ApiError(HttpStatus status, String message, String error)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.details = error;
        errors = Arrays.asList(error);
    }

    /**
     * The constructor base with more specific details
     * @param status The http status of response
     * @param objects The Objects affected
     * @param message The Error message
     * @param error The detailed reason off the error
     */
    public ApiError(HttpStatus status, ArrayList<Object> objects, String message, String error)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.details = error;
        this.objects = objects;
        errors = Arrays.asList(error);
    }

    /**
     * The constructor base with more specific details
     * @param status The http status of response
     * @param message The Error message
     * @param error The detailed reason off the error
     * @param ex The entire exception
     */
    public ApiError(HttpStatus status, String message, String error, String ex)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.ex = ex;
        processDetails(ex);
        errors = Arrays.asList(error);
    }

    /**
     * This method is responsible to extract the reason wy error occur
     * @param content The entire stack trace of the error
     */
    private void processDetails(String content)
    {
        this.details = content.substring(content.indexOf("Detail:") + 7, content.length());
    }

}
