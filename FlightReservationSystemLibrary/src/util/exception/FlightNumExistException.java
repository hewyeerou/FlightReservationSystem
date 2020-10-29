/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author yeerouhew
 */
public class FlightNumExistException extends Exception {

    /**
     * Creates a new instance of <code>FlightNumExistException</code> without
     * detail message.
     */
    public FlightNumExistException() {
    }

    /**
     * Constructs an instance of <code>FlightNumExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FlightNumExistException(String msg) {
        super(msg);
    }
}
