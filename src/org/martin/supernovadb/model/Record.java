/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.model;

import java.util.Arrays;

/**
 *
 * @author martin
 */
public class Record {
    private String[] fields;

    public Record(String[] fields) {
        this.fields = fields;
    }

    private void checkIndex(int index){
        if(index >= fields.length || index < 0) 
            throw new IndexOutOfBoundsException(index+"");
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String getField(int index){
        checkIndex(index);
        return fields[index];
    }
    
    public void setField(int index, String obj){
        checkIndex(index);
        fields[index] = obj;
    }

    @Override
    public String toString() {
        return Arrays.toString(fields);
    }
    
}
