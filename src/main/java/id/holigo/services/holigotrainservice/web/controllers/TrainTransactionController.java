package id.holigo.services.holigotrainservice.web.controllers;

import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.services.TrainService;
import id.holigo.services.holigotrainservice.services.TrainTransactionService;
import id.holigo.services.holigotrainservice.web.mappers.TrainTransactionMapper;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionDtoForUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TrainTransactionController {

    private TrainTransactionRepository trainTransactionRepository;

    private TrainTransactionMapper trainTransactionMapper;

    private TrainTransactionService trainTransactionService;

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
