package id.holigo.services.holigotrainservice.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.TripType;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.services.TrainTransactionService;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.web.mappers.SeatMapMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainTransactionMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainTransactionTripMapper;
import id.holigo.services.holigotrainservice.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class TrainTransactionController {

    private TrainTransactionRepository trainTransactionRepository;

    private TrainTransactionMapper trainTransactionMapper;

    private TrainTransactionService trainTransactionService;

    private RetrossTrainService retrossTrainService;

    private TrainTransactionTripMapper trainTransactionTripMapper;

    private SeatMapMapper seatMapMapper;

    @Autowired
    public void setSeatMapMapper(SeatMapMapper seatMapMapper) {
        this.seatMapMapper = seatMapMapper;
    }

    @Autowired
    public void setTrainTransactionTripMapper(TrainTransactionTripMapper trainTransactionTripMapper) {
        this.trainTransactionTripMapper = trainTransactionTripMapper;
    }

    @Autowired
    public void setRetrossTrainService(RetrossTrainService retrossTrainService) {
        this.retrossTrainService = retrossTrainService;
    }

    @Autowired
    public void setTrainTransactionService(TrainTransactionService trainTransactionService) {
        this.trainTransactionService = trainTransactionService;
    }

    @Autowired
    public void setTrainTransactionMapper(TrainTransactionMapper trainTransactionMapper) {
        this.trainTransactionMapper = trainTransactionMapper;
    }

    @Autowired
    public void setTrainTransactionRepository(TrainTransactionRepository trainTransactionRepository) {
        this.trainTransactionRepository = trainTransactionRepository;
    }

    @GetMapping("/api/v1/train/transactions/{id}")
    public ResponseEntity<TrainTransactionDtoForUser> getTransaction(@PathVariable("id") Long id) {
        Optional<TrainTransaction> fetchTrainTransaction = trainTransactionRepository.findById(id);
        if (fetchTrainTransaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TrainTransaction trainTransaction = fetchTrainTransaction.get();
        return new ResponseEntity<>(trainTransactionMapper.trainTransactionToTrainTransactionDtoForUser(trainTransaction), HttpStatus.OK);
    }

    @GetMapping("/api/v1/train/transactions/{id}/trips")
    public ResponseEntity<List<TrainTransactionTripDtoForUser>> getSeatMap(@PathVariable("id") Long id, @RequestHeader("user-id") Long userId) {
        Optional<TrainTransaction> fetchTrainTransaction = trainTransactionRepository.findById(id);
        if (fetchTrainTransaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TrainTransaction trainTransaction = fetchTrainTransaction.get();
        if (userId.intValue() != trainTransaction.getUserId().intValue()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<TrainTransactionTripDtoForUser> trips = new ArrayList<>();
        SeatMapDto departureSeatMap = null;
        SeatMapDto returnSeatMap = null;
        if (trainTransaction.getOrderStatus().equals(OrderStatusEnum.BOOKED)) {
            RetrossRequestSeatMapDto retrossRequestSeatMapDto = RetrossRequestSeatMapDto.builder()
                    .des(trainTransaction.getTrips().get(0).getDestinationStation().getId())
                    .org(trainTransaction.getTrips().get(0).getOriginStation().getId())
                    .adt(trainTransaction.getTrips().get(0).getAdultAmount())
                    .chd(trainTransaction.getTrips().get(0).getChildAmount())
                    .inf(trainTransaction.getTrips().get(0).getInfantAmount())
                    .selectedIdDep(trainTransaction.getTrips().get(0).getSupplierSelectedId())
                    .tgl_dep(trainTransaction.getTrips().get(0).getDepartureDate().toString())
                    .trip(trainTransaction.getTripType().toString())
                    .build();
            if (trainTransaction.getTripType().equals(TripType.R)) {
                retrossRequestSeatMapDto.setTgl_ret(trainTransaction.getTrips().get(1).getDepartureDate().toString());
                retrossRequestSeatMapDto.setSelectedIdRet(trainTransaction.getTrips().get(1).getDepartureDate().toString());
            }
            try {
                RetrossResponseSeatMapDto retrossResponseSeatMapDto = retrossTrainService.getSeatMap(retrossRequestSeatMapDto);
                departureSeatMap = seatMapMapper.retrossSeatMapDtoToSeatMapDto(retrossResponseSeatMapDto.getSeat().getDepartureSeat());
                if (trainTransaction.getTripType().equals(TripType.R)) {
                    returnSeatMap = seatMapMapper.retrossSeatMapDtoToSeatMapDto(retrossResponseSeatMapDto.getSeat().getReturnSeat());
                }
            } catch (JsonProcessingException ignored) {
            }
            AtomicInteger segment = new AtomicInteger();
            SeatMapDto finalDepartureSeatMap = departureSeatMap;
            List<TrainTransactionTripDtoForUser> finalTrips = trips;
            SeatMapDto finalReturnSeatMap = returnSeatMap;
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                if (segment.get() == 0) {
                    finalTrips.add(trainTransactionTripMapper.trainTransactionTripToTrainTransactionTripDtoForUser(trainTransactionTrip, finalDepartureSeatMap));
                } else {
                    finalTrips.add(trainTransactionTripMapper.trainTransactionTripToTrainTransactionTripDtoForUser(trainTransactionTrip, finalReturnSeatMap));
                }
                segment.getAndIncrement();
            });
            return new ResponseEntity<>(finalTrips, HttpStatus.OK);
        } else {
            trips = trainTransaction.getTrips().stream().map(trainTransactionTripMapper::trainTransactionTripToTrainTransactionTripDtoForUser).collect(Collectors.toList());
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
//        return new ResponseEntity<>(trainTransactionMapper.trainTransactionToTrainTransactionDtoForUser(trainTransaction), HttpStatus.OK);
    }

    @PutMapping("/api/v1/train/transactions/{id}/trips")
    public ResponseEntity<List<TrainTransactionTripDtoForUser>> updateSeatPassenger(@PathVariable("id") Long id,
                                                                                    @RequestBody List<TrainTransactionTripDtoForUser> trainTransactionTripDtoForUsers,
                                                                                    @RequestHeader("user-id") Long userId) {
        Optional<TrainTransaction> fetchTrainTransaction = trainTransactionRepository.findById(id);
        if (fetchTrainTransaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TrainTransaction trainTransaction = fetchTrainTransaction.get();
        if (userId.intValue() != trainTransaction.getUserId().intValue()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        RetrossRequestChangeSeatDto retrossRequestChangeSeatDto = new RetrossRequestChangeSeatDto();
        retrossRequestChangeSeatDto.setNotrx(trainTransaction.getTrips().get(0).getSupplierId());
        retrossRequestChangeSeatDto.setTrips(trainTransactionTripDtoForUsers);
        RetrossResponseChangeSeatDto retrossResponseChangeSeatDto;
        try {
            retrossResponseChangeSeatDto = retrossTrainService.changeSeat(retrossRequestChangeSeatDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (retrossResponseChangeSeatDto.getError_code().equals("000")) {
            return new ResponseEntity<>(trainTransactionTripDtoForUsers, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @PutMapping("/api/v1/train/transactions/{id}")
    public ResponseEntity<TrainTransactionDtoForUser> updateTransaction(@PathVariable("id") Long id) {
        Optional<TrainTransaction> fetchTrainTransaction = trainTransactionRepository.findById(id);
        if (fetchTrainTransaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TrainTransaction trainTransaction = fetchTrainTransaction.get();
        trainTransactionService.cancelTransaction(trainTransaction);
        return new ResponseEntity<>(trainTransactionMapper.trainTransactionToTrainTransactionDtoForUser(trainTransaction), HttpStatus.OK);
    }
}
