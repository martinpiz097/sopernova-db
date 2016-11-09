/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.db.exceptions;

/**
 *
 * @author martin
 */
public class IncompatibleDataTypeException extends Exception {

    /**
     * Creates a new instance of <code>IncompatibleObjectTypeException</code>
     * without detail message.
     */
    public IncompatibleDataTypeException() {
    }

    /**
     * Constructs an instance of <code>IncompatibleObjectTypeException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public IncompatibleDataTypeException(String msg) {
        super(msg);
    }
}
