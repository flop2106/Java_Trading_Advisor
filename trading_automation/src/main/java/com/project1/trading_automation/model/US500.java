package com.project1.trading_automation.model;
import com.project1.trading_automation.utils.WebScraper;
import com.project1.trading_automation.utils.DateUtils;
import com.project1.trading_automation.utils.PredictionUtils;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class US500 extends Price{
    private static final Logger logger = LoggerFactory.getLogger(US500.class);
    public US500(String csvFilePath){
        super("US500", csvFilePath);
    }
    
    @Override
    public void fetchAndUpdatePrice(){
        try{    String lastTradingDay  = DateUtils.getLastTradingDate();

                boolean priceExists = false;

                //Simplified Null-Safe Comparison
                for (Map<String, String> row : prices) {
                    if (Objects.equals(row.get("Date"), lastTradingDay)) { //Prevents NullPointerException
                        priceExists = true;
                        break;
                    }
                }


                if (!priceExists){
                    double lastClosingPrice = WebScraper.getSP500Price();
                    if (lastClosingPrice == -1){
                        logger.warn("Failed to fetch last confirmed closing price");
                        return;
                    }
                    Map<String, String> newPriceRow = new HashMap<>();
                    newPriceRow.put("Date", lastTradingDay);
                    newPriceRow.put("Price", String.format("%.2f", lastClosingPrice));
                    newPriceRow.put("Open", "-"); // Placeholder for other fields
                    newPriceRow.put("High", "-");
                    newPriceRow.put("Low", "-");
                    newPriceRow.put("Vol.", "-");
                    newPriceRow.put("Change %", "-");
                
                    prices.add(0, newPriceRow);
                    SavePricesToCSV();
                    logger.info("Added price for " + lastTradingDay + ": " + lastClosingPrice);
                } else {
                    logger.info("Price for " + lastTradingDay + " already exists");
                }
        } catch (Exception e){
            logger.warn("Error fetching price: " + e.getMessage());
        }
    }
    @Override
    public double predictNextDayPrice(){
        double[] historicalPrices = prices.stream()
            .mapToDouble(row -> Double.parseDouble(row.get("Price").replace(",","")))
            .toArray();
        
        reverseArray(historicalPrices);
        return PredictionUtils.predictWithARIMA(historicalPrices);
    }

    private void reverseArray(double[] array){
        int left = 0, right = array.length - 1;
        while (left<right){
            double temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }
}