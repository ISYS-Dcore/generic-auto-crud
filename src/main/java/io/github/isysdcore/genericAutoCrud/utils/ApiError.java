/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author domingos.fernando
 */
public class ApiError
{

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String ex;
    private List<String> errors;
    private ArrayList<Object> objects;

    public ApiError()
    {
        super();
        setTimestamp(LocalDateTime.now());
    }

    public ApiError(HttpStatus status, String message)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, List<String> errors)
    {
        super();
        this.setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, List<String> errors, String ex)
    {
        super();
        this.setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.ex = ex;
    }

    public ApiError(HttpStatus status, String message, String error)
    {
        super();
        setTimestamp(LocalDateTime.now());
        this.status = status;
        this.message = message;
        this.details = error;
        errors = Arrays.asList(error);
    }

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

    public HttpStatus getStatus()
    {
        return status;
    }

    public void setStatus(HttpStatus status)
    {
        this.status = status;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> errors)
    {
        this.errors = errors;
    }

    public String getEx()
    {
        return ex;
    }

    public void setEx(String ex)
    {
        this.ex = ex;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public List<Object> getObjects()
    {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects)
    {
        this.objects = objects;
    }

    private void processDetails(String content)
    {
//        Pattern p = Pattern.compile("Detail:(.*?)\\,");
//        Matcher m = p.matcher(content);
//        this.details = "";
//        if (m.find()) {
//            this.details += m.group(1).trim();
//        }
        this.details = "";
        this.details = content.substring(content.indexOf("Detail:") + 7, content.length());
    }

}
