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
public class FareBasisCodeExistException extends Exception {

    /**
     * Creates a new instance of <code>FareBasisCodeExistException</code>
     * without detail message.
     */
    public FareBasisCodeExistException() {
    }

    /**
     * Constructs an instance of <code>FareBasisCodeExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public FareBasisCodeExistException(String msg) {
        super(msg);
    }
}
