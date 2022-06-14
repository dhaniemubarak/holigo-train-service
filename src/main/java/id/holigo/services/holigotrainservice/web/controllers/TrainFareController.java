package id.holigo.services.holigotrainservice.web.controllers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.services.TrainService;
import id.holigo.services.holigotrainservice.web.model.RequestFinalFareDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
public class TrainFareController {

    private TrainService trainService;

    public static final String PATH = "/api/v1/train/fares";

    @Autowired
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping(PATH)
    public ResponseEntity<HttpStatus> createFare(@RequestBody RequestFinalFareDto requestFinalFareDto, @RequestHeader("user-id") Long userId) {

        log.info("Fare running");

        TrainFinalFare trainFinalFare = trainService.createFinalFare(requestFinalFareDto, userId);

        if (trainFinalFare == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(UriComponentsBuilder.fromPath(PATH + "/{id}").buildAndExpand(trainFinalFare.getId()).toUri());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
