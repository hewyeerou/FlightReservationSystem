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
public class InvalidDateTimeException extends Exception {

    /**
     * Creates a new instance of <code>InvalidDateTimeException</code> without
     * detail message.
     */
    public InvalidDateTimeException() {
    }

    /**
     * Constructs an instance of <code>InvalidDateTimeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidDateTimeException(String msg) {
        super(msg);
    }
}
