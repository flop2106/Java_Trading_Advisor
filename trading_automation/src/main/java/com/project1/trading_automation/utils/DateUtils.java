package com.project1.trading_automation.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
    public static String getLastTradingDate(){
        Calendar calendar = Calendar.getInstance();
        do {
            calendar.add(Calendar.DAY_OF_MONTH,-1);
        } while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(calendar.getTime());
            }
    }

