package id.holigo.services.holigotrainservice;

import id.holigo.services.holigotrainservice.web.model.PassengerDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@EnableFeignClients
@SpringBootApplication
public class HoligoTrainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoligoTrainServiceApplication.class, args);
    }

}
