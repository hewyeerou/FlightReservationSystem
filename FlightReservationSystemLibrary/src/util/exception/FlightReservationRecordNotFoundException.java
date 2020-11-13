/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author seowtengng
 */
public class FlightReservationRecordNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>FlightReservationRecordNotFoundException</code> without detail
     * message.
     */
    public FlightReservationRecordNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>FlightReservationRecordNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public FlightReservationRecordNotFoundException(String msg) {
        super(msg);
    }
}
