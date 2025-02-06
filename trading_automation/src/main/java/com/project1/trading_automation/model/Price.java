package com.project1.trading_automation.model;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.*;
import java.io.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Price {
    protected String ticker;
    protected List<Map<String, String>> prices = new ArrayList<>();
    protected String csvFilePath;
    private static final Logger logger = LoggerFactory.getLogger(Price.class);

    public Price (String ticker, String csvFilePath){
        this.ticker = ticker;
        this.csvFilePath = csvFilePath;
        loadPricesFromCSV();
    }

    private void loadPricesFromCSV(){
        prices.clear();
        File file = new File(csvFilePath);

        if (!file.exists()){
            logger.info("CSV File Does not exists. Creating new file...");
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(file.toPath())){
            String headerLine = br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (values.length < 7){
                    logger.warn("skipping malformed CSV row: " +  line);
                    continue;
                }
                Map<String, String> row = new HashMap<>();
                row.put("Date", values[0].replace("\"", "").trim());
                row.put("Price", values[1].replace("\"", "").trim());
                row.put("Open", values[2].replace("\"", "").trim());
                row.put("High", values[3].replace("\"", "").trim());
                row.put("Low", values[4].replace("\"", "").trim());
                row.put("Vol.", values[5].replace("\"", "").trim());
                row.put("Change %", values[6].replace("\"", "").trim());

                prices.add(row);

            }
        } catch (IOException e){
            logger.warn("Error reading CSV: " + e.getMessage());
        }
    }

    protected void SavePricesToCSV(){
        File file = new File(csvFilePath);
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))){
            String[] header = {"Date", "Price"," Open"," High", "Low", "Vol.", "Change %"};
            writer.writeNext(header);

            for (Map<String, String> row: prices){
                String[] line = {
                    row.getOrDefault("Date", "-"),
                    row.getOrDefault("Price", "-"),
                    row.getOrDefault("Open", "-"),
                    row.getOrDefault("High", "-"),
                    row.getOrDefault("Low", "-"),
                    row.getOrDefault("Vol.", "-"),
                    row.getOrDefault("Change %", "-")
                };
                writer.writeNext(line);
            }
            logger.info("CSV Successfully written: " + csvFilePath);    
        } catch (IOException e){
            logger.warn("Error writing CSV: " + e.getMessage());
        }
        }
    
    public String getFirstNumRows(int num){
        StringBuilder result = new StringBuilder();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))){
            for(int i = 0; i< num; i++){
                try {
                String[] row = reader.readNext();
                if (row == null) break;
                result.append(String.join(",", row)).append("\n");
                } catch (CsvValidationException e){
                    logger.warn("Validation error reading CSV: " + e.getMessage());
                }
            } 
        } catch (IOException e){
            logger.warn("Error reading CSV: " + e.getMessage());
        }
        return result.toString();
    }    
    public abstract void fetchAndUpdatePrice();
    public abstract double predictNextDayPrice();
    }

