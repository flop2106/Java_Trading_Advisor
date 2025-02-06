package com.project1.trading_automation.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebScraper {
    private static final String SP500_URL = "https://www.investing.com/indices/us-spx-500-historical-data";
    private static final Logger logger = LoggerFactory.getLogger(WebScraper.class);
    public static double getSP500Price(){
        try{
            Document doc = Jsoup.connect(SP500_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();
        

            // Find the historical data table
            Element table = doc.select("table.freeze-column-w-1").first();
            if (table == null) {
                throw new RuntimeException("Historical data table not found.");
            }

            // Extract the second row (the first row after headers) → Yesterday’s price
            Elements rows = table.select("tbody tr");
            if (rows.size() < 2) {
                throw new RuntimeException("Not enough historical data available.");
            }

            // Extract columns (Date, Price, Open, High, Low)
            Elements columns = rows.get(1).select("td"); // 2nd row after the header

            String date = columns.get(0).text();
            String closePriceText = columns.get(1).text().replace(",", ""); // Closing price
            double closePrice = Double.parseDouble(closePriceText);

            logger.info("Scraped historical data: " + date + " | Close Price: " + closePrice);
            return closePrice;
        } catch (Exception e){
            logger.warn("Error fetching price: " + e.getMessage());
            return -1;
        }
    
    }
}   
