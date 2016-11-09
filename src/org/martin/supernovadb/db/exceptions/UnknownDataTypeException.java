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
public class UnknownDataTypeException extends Exception {

    /**
     * Creates a new instance of <code>UnknownDataTypeException</code> without
     * detail message.
     */
    public UnknownDataTypeException() {
    }

    /**
     * Constructs an instance of <code>UnknownDataTypeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownDataTypeException(String msg) {
        super(msg);
    }
}
