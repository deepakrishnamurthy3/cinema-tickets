package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    private static final Long ACC_ID = 1L;
    private static final int NO_OF_TIMES = 0;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private SeatReservationService seatReservationService;

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Test
    public void testValidPurchaseTicketOne() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
        verify(seatReservationService, times(NO_OF_TIMES)).reserveSeat(ACC_ID, 4);
        verify(ticketPaymentService, times(NO_OF_TIMES)).makePayment(ACC_ID, 60);
    }

    @Test
    public void testValidPurchaseTicketTwo() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
        verify(seatReservationService, times(NO_OF_TIMES)).reserveSeat(ACC_ID, 5);
        verify(ticketPaymentService, times(NO_OF_TIMES)).makePayment(ACC_ID, 100);
    }

    @Test
    public void testValidPurchaseTicketThree() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
        verify(seatReservationService, times(NO_OF_TIMES)).reserveSeat(ACC_ID, 8);
        verify(ticketPaymentService, times(NO_OF_TIMES)).makePayment(ACC_ID, 110);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketOne() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 4)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketTwo() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 4)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketThree() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketFour() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketFive() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInValidPurchaseTicketSix() {

        TicketTypeRequest[] trList = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5)
        };

        ticketService.purchaseTickets(ACC_ID, trList);
    }
}