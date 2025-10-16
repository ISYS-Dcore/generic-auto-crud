/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author domingos.fernando
 */
public class DateUtils
{
    // Try common date formats
    public static DateTimeFormatter[] formatters = {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT
    };

    public static Date strToDate(String data)
    {
        if (data == null) {
            return null;
        }

        Date dataF = null;
        try {
            DateFormat dateFormat;
            if (data.indexOf('-') >= 0) {
                int value = Integer.parseInt(data.split("-")[0]);
                if (value < 1000) {
                    dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                }
                else {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }
            }
            else {
                int value = Integer.parseInt(data.split("/")[0]);
                if (value < 1000) {
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                }
                else {
                    dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                }
            }
            long timestamp = dateFormat.parse(data).getTime();
            dataF = new Date(timestamp);
        }
        catch (ParseException pe) {
            System.err.println("Erro ao converter String em data: " + pe.getLocalizedMessage());
        }
        return dataF;
    }

    public static Instant strToInstant(String dateString)
    {
        if (dateString.isBlank()){
            return null;
        }
        Instant instant = null;
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (formatter.equals(DateTimeFormatter.ISO_INSTANT)) {
                    instant = java.time.Instant.parse(dateString);
                } else if (formatter.equals(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) {
                    instant = java.time.LocalDateTime.parse(dateString, formatter).toInstant(ZoneOffset.UTC);
                } else {
                    instant = Instant.from(LocalDate.parse(dateString, formatter));
                }
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        return instant;
    }
}
