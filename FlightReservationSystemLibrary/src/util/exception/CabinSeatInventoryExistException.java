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
public class CabinSeatInventoryExistException extends Exception {

    /**
     * Creates a new instance of <code>CabinSeatInventoryExistException</code>
     * without detail message.
     */
    public CabinSeatInventoryExistException() {
    }

    /**
     * Constructs an instance of <code>CabinSeatInventoryExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CabinSeatInventoryExistException(String msg) {
        super(msg);
    }
}
