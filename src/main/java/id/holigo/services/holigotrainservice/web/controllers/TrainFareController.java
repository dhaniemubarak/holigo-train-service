package id.holigo.services.holigotrainservice.web.controllers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.repositories.TrainFinalFareRepository;
import id.holigo.services.holigotrainservice.services.TrainService;
import id.holigo.services.holigotrainservice.web.mappers.TrainFinalFareMapper;
import id.holigo.services.holigotrainservice.web.model.RequestFinalFareDto;
import id.holigo.services.holigotrainservice.web.model.TrainFinalFareDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@RestController
public class TrainFareController {

    private TrainService trainService;

    private TrainFinalFareRepository trainFinalFareRepository;

    private TrainFinalFareMapper trainFinalFareMapper;

    public static final String PATH = "/api/v1/train/fares";

    @Autowired
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    @Autowired
    public void setTrainFinalFareRepository(TrainFinalFareRepository trainFinalFareRepository) {
        this.trainFinalFareRepository = trainFinalFareRepository;
    }

    @Autowired
    public void setTrainFinalFareMapper(TrainFinalFareMapper trainFinalFareMapper) {
        this.trainFinalFareMapper = trainFinalFareMapper;
    }

    @PostMapping(PATH)
    public ResponseEntity<HttpStatus> createFare(@RequestBody RequestFinalFareDto requestFinalFareDto, @RequestHeader("user-id") Long userId) {
        TrainFinalFare trainFinalFare = trainService.createFinalFare(requestFinalFareDto, userId);

        if (trainFinalFare == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(UriComponentsBuilder.fromPath(PATH + "/{id}").buildAndExpand(trainFinalFare.getId()).toUri());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping(PATH + "/{id}")
    public ResponseEntity<TrainFinalFareDto> getFare(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(trainFinalFareMapper.trainFinalFareToTrainFinalFareDto(trainFinalFareRepository.getReferenceById(id)), HttpStatus.OK);
    }
}
