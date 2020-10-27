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
public class AircraftConfigNameExistException extends Exception {

    /**
     * Creates a new instance of <code>AircraftConfigNameExistException</code>
     * without detail message.
     */
    public AircraftConfigNameExistException() {
    }

    /**
     * Constructs an instance of <code>AircraftConfigNameExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AircraftConfigNameExistException(String msg) {
        super(msg);
    }
}
