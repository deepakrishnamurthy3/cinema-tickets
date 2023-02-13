package uk.gov.dwp.uc.pairtest;

import lombok.extern.java.Log;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

@Log
public class TicketServiceImpl implements TicketService, TicketPaymentService, SeatReservationService {

    private static final int ZERO = 0;
    private static final int MIN_ADULT_TICKET = 1;
    private static final int MAX_ADULT_TICKET = 20;

    EnumMap<TicketTypeRequest.Type, Integer> ticketTypeCountMap;

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        log.info("Step 1 : Validating");
        validatePurchaserInfo(accountId, ticketTypeRequests);

        log.info("Step 2 : Calculating total seats to be allocated by filtering Infants");
        int totalSeatsToAllocate = calculateTotalSeatsForAllocation(ticketTypeRequests);

        log.info("Step 3 : Reserving Seats");
        reserveSeat(accountId, totalSeatsToAllocate);

        log.info("Step 4 : Calculating total amount to be paid");
        int totalAmountToPay = calculatingTotalAmountToPay();

        log.info("Step 5 : Processing Payment");
        makePayment(accountId, totalAmountToPay);

        log.info("For Account ID : " + accountId + " the total amount paid for " + totalSeatsToAllocate + " reserved seats is : £" + totalAmountToPay);
    }

    /**
     * @param accountId          unique id of the booking.
     * @param ticketTypeRequests An immutable object where price , no of tickts for each type is stored.
     *                           Step 1: isValidAccIdAndTicketTypeRequestLengthPredicate checks valid account Id & length of the array
     *                           Step 2: isMinAdultTicketPresentPredicate checks at least one Adult ticket is present or not
     *                           Step 3: Checks total no of tickets is <= 20
     *                           Step 4: No of Adult Ticket should be >= No of Infant Ticket
     */
    private void validatePurchaserInfo(Long accountId, TicketTypeRequest... ticketTypeRequests) {

//        Step 1: isValidAccIdAndTicketTypeRequestLengthPredicate checks valid account Id & length of the array
        BiPredicate<Long, Integer> isValidAccIdAndTicketTypeRequestLengthPredicate = (accId, ticketTypeRequestsLength) -> accId > ZERO && ticketTypeRequestsLength > ZERO;

//        Step 2: isMinAdultTicketPresentPredicate checks at least one Adult ticket is present or not
        Predicate<TicketTypeRequest> isMinAdultTicketPresentPredicate = ticketTypeRequest -> ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.ADULT) && ticketTypeRequest.getNoOfTickets() >= MIN_ADULT_TICKET;

//       Step 3: Checks total no of tickets is <= 20
        int totalTickets = calculateTotalNumberOfTicket(ticketTypeRequests);

//      On success of steps 1,2 & 3 a consumer is used iterate the TicketTypeRequests & store it in a map
        ticketTypeCountMap = new EnumMap<>(TicketTypeRequest.Type.class);
        ObjIntConsumer<TicketTypeRequest.Type> typeRequestIntegerBiConsumer = (type, integer) -> ticketTypeCountMap.put(type, integer);

        Comparator<Integer> comparator = Integer::compareTo;

        if (isValidAccIdAndTicketTypeRequestLengthPredicate.test(accountId, ticketTypeRequests.length)
                && Arrays.stream(ticketTypeRequests).anyMatch(isMinAdultTicketPresentPredicate)
                && totalTickets <= MAX_ADULT_TICKET) {
            Arrays.stream(ticketTypeRequests)
                    .forEach(ticketTypeRequest -> typeRequestIntegerBiConsumer.accept(ticketTypeRequest.getTicketType(), ticketTypeRequest.getNoOfTickets()));
        }

//        Step 4: No of Adult Ticket should be >= No of Infant Ticket
        if (ticketTypeCountMap.containsKey(TicketTypeRequest.Type.ADULT) && ticketTypeCountMap.containsKey(TicketTypeRequest.Type.INFANT)) {
            if (comparator.compare(ticketTypeCountMap.get(TicketTypeRequest.Type.ADULT), ticketTypeCountMap.get(TicketTypeRequest.Type.INFANT)) < ZERO) {
                throw new InvalidPurchaseException("No of Adult Ticket should be greater than equal to No of Infant Ticket");
            }
        } else {
            throw new InvalidPurchaseException("Invalid Purchase.");
        }
    }

    private int calculateTotalNumberOfTicket(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int calculateTotalSeatsForAllocation(TicketTypeRequest... ticketTypeRequests) {
        Predicate<TicketTypeRequest> filterInfants = ticketTypeRequest -> ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT);
        return Arrays.stream(ticketTypeRequests)
                .filter(filterInfants.negate())
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int calculatingTotalAmountToPay() {
        return ticketTypeCountMap.entrySet().stream()
                .mapToInt(ticketTypeCount -> {
                    int noOfTickets = ticketTypeCount.getValue();
                    int price = ticketTypeCount.getKey().getPrice();
                    return noOfTickets * price;
                }).sum();
    }

    @Override
    public void makePayment(long accountId, int totalAmountToPay) {
        log.info("Processing payment of £" + totalAmountToPay + " for Account ID : " + accountId);
    }

    @Override
    public void reserveSeat(long accountId, int totalSeatsToAllocate) {
        log.info("Total seats reserved for Account ID : " + accountId + "is : " + totalSeatsToAllocate);
    }
}
