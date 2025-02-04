package com.project1.trading_automation;
import java.util.List;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

abstract class Price {
    protected String ticker;
    protected String date;
    protected List<Double> prices;

    public Price (String ticker){
        this.ticker = ticker;

        ZonedDateTime nycDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        date = nycDateTime.format(formatter);

        this.date = date;
        this.prices = new ArrayList<Double>();
    }

    public abstract float getPrice(String ticker, String date);
    public abstract List<Double> priceUpdate(String ticker, String date, float price);
    public abstract List<Double> pricePrediction(String ticker, String date, List<Double> updatedPrices);
}

class US500 extends Price{

    public US500(){
        super("US500");
    }
    @Override
    public float getPrice(String ticker, String date){
        return 1000.0f;
    }

    @Override
    public List<Double> priceUpdate(String ticker, String date, float price){
        this.prices.add((double) price);
        return this.prices;
    }

    @Override
    public List<Double> pricePrediction(String ticker, String date, List<Double> updatedPrices){
        List<Double> predictions = new ArrayList<>();
        for (Double currentPrice: updatedPrices){
            predictions.add(currentPrice * 1.01);
        }
        return predictions;
    }

    
}

// Main class to test the implementation
public class Main {
    public static void main(String[] args) {
        US500 us500 = new US500();

        // Get the current price
        float price = us500.getPrice(us500.ticker, us500.date);
        System.out.println("Current Price: " + price);

        // Update prices
        List<Double> updatedPrices = us500.priceUpdate(us500.ticker, us500.date, price);
        System.out.println("Updated Prices: " + updatedPrices);

        // Predict future prices
        List<Double> predictions = us500.pricePrediction(us500.ticker, us500.date, updatedPrices);
        System.out.println("Predicted Prices: " + predictions);
    }
}

