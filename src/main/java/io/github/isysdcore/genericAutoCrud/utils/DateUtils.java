/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author domingos.fernando
 */
public class DateUtils
{
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
}
