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
public class IncompatibleAutoIncrementException extends Exception{
    
    public IncompatibleAutoIncrementException() {
    }

    public IncompatibleAutoIncrementException(String message) {
        super(message);
    }
    
}
