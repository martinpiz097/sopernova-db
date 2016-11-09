/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.model;

import java.io.Serializable;
import org.martin.supernovadb.db.exceptions.UnknownDataTypeException;

/**
 *
 * @author martin
 * @param <T>
 */
public class Column implements Serializable{
    protected static final char SEPARATOR = '~';
    
    private String name;
    private DataType dataType;
    private boolean isAutoIncrement;
    private boolean isPK;
    private boolean isNotNull;
    
    // Al crear la tabla en la clase Database se debe comprobar que no deben 
    // haber dos columnas como primary key

    public Column(String name, String dataType, boolean isAutoIncrement, boolean isPK) throws UnknownDataTypeException {
        this(name, dataType, isAutoIncrement, isPK, isPK);
    }
    
    public Column(String name, String dataType, boolean isAutoIncrement, boolean isPK, boolean isNotNull) throws UnknownDataTypeException {
        this.name = name;
        this.dataType = DataType.getDataTypeByName(name);
        this.isAutoIncrement = isAutoIncrement;
        this.isPK = isPK;
        this.isNotNull = isNotNull;
    }

    public Column(String name, DataType dataType, boolean isAutoIncrement, boolean isPK) {
        this(name, dataType, isAutoIncrement, isPK, isPK);
    }

    public Column(String name, DataType dataType, boolean isAutoIncrement, boolean isPK, boolean isNotNull) {
        this.name = name;
        this.dataType = dataType;
        this.isAutoIncrement = isAutoIncrement;
        this.isPK = isPK;
        this.isNotNull = isNotNull;
    }
    
//    private boolean isNumberType(){
//    }
    
    public String getColumnDataType(){
        return dataType.getName();
    }

    public String getName() {
        return name;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public boolean isPK() {
        return isPK;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append('[');
        sBuilder.append(getColumnDataType());
        sBuilder.append(SEPARATOR);
        sBuilder.append(name);
        sBuilder.append(SEPARATOR);
        sBuilder.append(isAutoIncrement);
        sBuilder.append(SEPARATOR);
        sBuilder.append(isPK);
        sBuilder.append(SEPARATOR);
        sBuilder.append(isNotNull);
        sBuilder.append(']');
        String toString = sBuilder.toString();
        sBuilder = null;
        return toString;
    }
    
}
