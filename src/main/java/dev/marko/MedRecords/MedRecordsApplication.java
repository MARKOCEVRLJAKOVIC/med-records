package dev.marko.MedRecords;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MedRecordsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedRecordsApplication.class, args);
	}

}
