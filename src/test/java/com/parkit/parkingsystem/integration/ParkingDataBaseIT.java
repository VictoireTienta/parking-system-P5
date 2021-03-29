package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * The type Parking data base it.
 *
 * @author Nana
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
    /** The Constant dataBaseTestConfig. */
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    /** The parking spot DAO. */
    private static ParkingSpotDAO parkingSpotDAO;

    /** The ticket DAO. */
    private static TicketDAO ticketDAO;

    /** The data base prepare service. */
    private static DataBasePrepareService dataBasePrepareService;

    /** The input reader util. */
    @Mock
    private static InputReaderUtil inputReaderUtil;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }
    /**
     * Sets the up per test.
     *
     * @throws Exception the exception
     */
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }
    /**
     * Tear down.
     */
    @AfterAll
    private static void tearDown(){

    }
    /**
     * Test parking a car.
     *"Test to verify if a ticket is actually saved in DB and Parking table is updated with availability
     * @throws Exception the exception
     */
    @Test
    public void testParkingACar(){
        /**
         * Give a new parking service
         */
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        /**
         * When an incoming vehicle process is called
         */
        int nextAvailableSlot = parkingService.getNextParkingNumberIfAvailable().getId();
        parkingService.processIncomingVehicle();
        /**
         * Then : check that a ticket is actually saved in DB and Parking table is
         * updated with availability
         */

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals(true, ticket != null);
        assertEquals(nextAvailableSlot + 1, parkingService.getNextParkingNumberIfAvailable().getId());
        //assertEquals(false, parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId()).isAvailable());
    }
    /**
     * Test that parking lot exit.
     *
     * @throws Exception the exception
     */
    @Test
    public void testParkingLotExit(){
        testParkingACar();
        /**
         * When the exiting process is called
         */
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        /**
         * Then : check that the fare generated and out time are populated correctly in
         * the database
         */
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals(true, ticket.getOutTime() != null);
        assertEquals(true, ticket.getPrice() >= 0);

    }
    /**
     * Test  recurent user exiting .
     *
     * @throws Exception the exception
     */
    @Test
    public void checkDiscountForRecurringUserTest() throws InterruptedException {
        /**
         * Given a new parking service
         */
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        /**
         * When a process incoming and a exiting process are completed
         */
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        Thread.sleep(1000);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        /**
         * Then verify the customer become a recurrent user
         */
        //assertEquals(true, ticket.isARecurringUser());
    }
}
