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
public class AirportIataCodeExistException extends Exception {

    /**
     * Creates a new instance of <code>AirportIataCodeExistException</code>
     * without detail message.
     */
    public AirportIataCodeExistException() {
    }

    /**
     * Constructs an instance of <code>AirportIataCodeExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public AirportIataCodeExistException(String msg) {
        super(msg);
    }
}
