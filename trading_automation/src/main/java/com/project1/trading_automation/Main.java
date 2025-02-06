package com.project1.trading_automation;
import com.project1.trading_automation.model.*;
import com.project1.trading_automation.utils.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String csvFilePath = "trading_automation/src/main/resources/SP_500_Historical_Data.csv";

        US500 us500 = new US500(csvFilePath);
        us500.fetchAndUpdatePrice();
        Double pricePrediction = us500.predictNextDayPrice();
        String historicalPrice = us500.getFirstNumRows(200);

        
        List<News> newsList = News.fetchNews("US500", 50);
        StringBuilder newsResult = new StringBuilder();
        for (News news: newsList){
            newsResult.append(news).append("\n---------------------------\n");
        }

        String newsString = newsResult.toString();

        String query = """
            What is the expected movement of S&P500 today. 
            What is the top and bottom resistance. 
            Is ARIMA prediction as targer reasonable?
            Based on the news is it support the prediction to buy, hold or short?
            Historical Price: %s
            ARIMA Prediction: %s
            News Summary:
            %s
            """.formatted(historicalPrice, pricePrediction, newsString);
        String response = OpenAIClient.sendMessageToOpenAI(query);
        System.out.println("OpenAI Response: " + response);
    
    
    }

}
