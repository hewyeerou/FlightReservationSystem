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
public class createOutboundReturnFlightCheckException extends Exception {

    /**
     * Creates a new instance of
     * <code>createOutboundReturnFlightCheckException</code> without detail
     * message.
     */
    public createOutboundReturnFlightCheckException() {
    }

    /**
     * Constructs an instance of
     * <code>createOutboundReturnFlightCheckException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public createOutboundReturnFlightCheckException(String msg) {
        super(msg);
    }
}
