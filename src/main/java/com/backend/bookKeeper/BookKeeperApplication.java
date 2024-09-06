package com.backend.bookKeeper;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.backend.bookKeeper.Model.MongoConnect;

@SpringBootApplication
public class BookKeeperApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BookKeeperApplication.class, args);

		try {
			context.getBean(MongoConnect.class);
			System.out.println("Connected to the database!");
		} catch (BeansException e) {
			System.out.println("Connection Failed");
		}
		
	}

}
