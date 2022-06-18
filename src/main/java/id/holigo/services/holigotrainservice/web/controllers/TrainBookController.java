package id.holigo.services.holigotrainservice.web.controllers;


import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.services.TrainTransactionService;
import id.holigo.services.holigotrainservice.web.model.TrainBookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class TrainBookController {
    private final static String PATH = "/api/v1/train/book";

    private final static String TRANSACTION_PATH = "/api/v1/transactions/{id}";

    private TrainTransactionService trainTransactionService;

    @Autowired
    public void setTrainTransactionService(TrainTransactionService trainTransactionService) {
        this.trainTransactionService = trainTransactionService;
    }

    @PostMapping(PATH)
    public ResponseEntity<HttpStatus> createBook(@RequestBody TrainBookDto trainBookDto, @RequestHeader("user-id") Long userId) {

        TrainTransaction trainTransaction = trainTransactionService.createTransaction(trainBookDto, userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(UriComponentsBuilder.fromPath(TRANSACTION_PATH).buildAndExpand(trainTransaction.getId().toString()).toUri());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
