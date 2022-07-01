package id.holigo.services.holigotrainservice.events;

public enum OrderStatusTripEvent {
    BOOK_SUCCESS, BOOK_FAIL, PROCESS_ISSUED, ISSUED_SUCCESS, WAITING_ISSUED, RETRYING_ISSUED, ISSUED_FAIL, BOOK_CANCEL, BOOK_EXPIRE
}
