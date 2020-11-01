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
public class InvalidSeatNumberInputException extends Exception {

    /**
     * Creates a new instance of <code>InvalidSeatNumberInputException</code>
     * without detail message.
     */
    public InvalidSeatNumberInputException() {
    }

    /**
     * Constructs an instance of <code>InvalidSeatNumberInputException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidSeatNumberInputException(String msg) {
        super(msg);
    }
}
