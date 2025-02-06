package com.project1.trading_automation.model;

import com.project1.trading_automation.utils.DateUtils;
import com.project1.trading_automation.utils.WebScraper;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.opencsv.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class US500Test {
    private static final String Test_CSV_PATH = "src/test/resources/SP_500_Historical_Data.csv";
    private US500 us500;
    
    @BeforeAll
    void setup() {
        // Ensure test directory exists
        try {
            Files.createDirectories(Paths.get("src/test/resources/"));
        } catch (IOException e) {
            System.err.println("Error creating test directory: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() throws IOException{
        Files.deleteIfExists(Paths.get(Test_CSV_PATH));

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Test_CSV_PATH))){
            writer.write("Date,Price,Open,High,Low,Vol.,Change %\n");
            writer.write("01/29/2025,6039.31,6057.70,6062.83,6012.96,,-0.47%\n");
            writer.write("01/28/2025,6067.70,6026.97,6074.54,5994.63,,-0.92%\n");
        }
        us500 = new US500(Test_CSV_PATH);

    }

    @Test
    @DisplayName("Test Fetch and Update Price - Adds New Price")
    public void testFetchAndUpdatePrice(){
        String lastTradingDay = "01/30/2025";
        double simulatedPrice = 6100.50;

        double fetchedPrice = WebScraper.getSP500Price();
        if (fetchedPrice == -1){
            fetchedPrice = simulatedPrice;
        }

        if (!us500.prices.stream().anyMatch(row -> Objects.equals(row.get("Date"), lastTradingDay))){
            Map<String, String> newPriceRow = new HashMap<>();
            newPriceRow.put("Date", lastTradingDay);
            newPriceRow.put("Price", String.format("%.2f", fetchedPrice));
            newPriceRow.put("Open", "-");
            newPriceRow.put("High", "-");
            newPriceRow.put("Low", "-");
            newPriceRow.put("Vol.", "-");
            newPriceRow.put("Change %", "-");

            us500.prices.add(0, newPriceRow);
            us500.SavePricesToCSV();
        }
        List<String> lines = readCsvLines(Test_CSV_PATH);
        assertEquals(4, lines.size(), "Expected 4 rows including header after update");
        assertTrue(lines.get(1).contains(lastTradingDay), "New date entry should be present in the csv");
    }

    @Test
    @DisplayName("Test Fetch and Update Price - No Duplicate Entry")
    void testNoDuplicateEntriesForSameDay(){
        String existingTradingDay = "01/29/2025";
        us500.fetchAndUpdatePrice();
        List<String> lines = readCsvLines(Test_CSV_PATH);
        assertEquals(4, lines.size(), "No new rows should be added when price already exists");
        assertTrue(lines.get(2).contains(existingTradingDay), "Existing trading day should still be present");
    }

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Paths.get(Test_CSV_PATH)); // Remove test file after each test
        } catch (IOException e) {
            System.err.println("Error deleting test file: " + e.getMessage());
        }
    }

    private List<String> readCsvLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading test CSV file: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
