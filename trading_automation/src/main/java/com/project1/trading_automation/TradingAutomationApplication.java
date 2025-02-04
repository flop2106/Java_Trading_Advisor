package com.project1.trading_automation;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TradingAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingAutomationApplication.class, args);
	}

}
