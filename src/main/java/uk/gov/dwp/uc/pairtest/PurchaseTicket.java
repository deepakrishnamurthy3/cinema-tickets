package uk.gov.dwp.uc.pairtest;

import lombok.extern.java.Log;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Random;
import java.util.Scanner;

@Log
public class PurchaseTicket {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        log.info("Enter number of tickets required for ADULT : ");
        int noOfAdultTickets = scanner.nextInt();

        log.info("Enter number of tickets required for CHILD : ");
        int noOfChildTickets = scanner.nextInt();

        log.info("Enter number of tickets required for INFANT : ");
        int noOfInfantTickets = scanner.nextInt();

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, noOfAdultTickets),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, noOfChildTickets),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, noOfInfantTickets)
        };

        TicketServiceImpl ticketService = new TicketServiceImpl();
        ticketService.purchaseTickets(new Random().nextLong(), ticketTypeRequests);
    }
}
