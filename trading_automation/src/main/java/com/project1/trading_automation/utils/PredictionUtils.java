package com.project1.trading_automation.utils;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PredictionUtils {
    private static final Logger logger = LoggerFactory.getLogger(PredictionUtils.class);
    public static double predictWithARIMA(double[] historicalPrices){
        try{
            if (historicalPrices.length < 2){
                throw new IllegalArgumentException("Not Enough Data Points");
            }
            double[] differences = computeDifferences(historicalPrices);
            double predictedDifference = forecastDifference(differences);

            return historicalPrices[historicalPrices.length - 1] + predictedDifference;
        } catch (Exception e){
            logger.warn("Arima Prediction Failed " + e.getMessage());
            return -1;
        }
    }

    private static double[] computeDifferences(double[] data){
        double[] differences = new double[data.length - 1];
        for (int i = 1; i < data.length - 1; i++){
            differences[i-1] = data[i] - data[i-1];
        }
        return differences;
    }

    private static double forecastDifference(double[] differences){
        SimpleRegression regression = new SimpleRegression(true);

        for (int i = 0; i < differences.length; i++){
            regression.addData(i, differences[i]);
        }

        return regression.predict(differences.length);
    }
}
