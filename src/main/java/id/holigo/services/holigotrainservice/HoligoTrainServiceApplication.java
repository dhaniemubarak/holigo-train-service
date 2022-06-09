package id.holigo.services.holigotrainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class HoligoTrainServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoligoTrainServiceApplication.class, args);
	}

}
