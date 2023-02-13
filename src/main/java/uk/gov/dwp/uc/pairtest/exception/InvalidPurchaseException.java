package uk.gov.dwp.uc.pairtest.exception;

import lombok.extern.java.Log;

@Log
public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String errMsg) {
        log.severe(errMsg);
    }
}
