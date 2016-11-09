/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.model;

import org.martin.supernovadb.db.exceptions.UnknownDataTypeException;

/**
 *
 * @author martin
 */
public class DataType {
    public static final DataType BLOB = new DataType("blob", TYPE.BINARY);
    public static final DataType BOOLEAN = new DataType("boolean", TYPE.BINARY);
    public static final DataType VARCHAR = new DataType("varchar", TYPE.ALPHANUMERIC);
    public static final DataType TEXT = new DataType("text", TYPE.ALPHANUMERIC);
    public static final DataType CHAR = new DataType("char", TYPE.ALPHANUMERIC);
    public static final DataType BYTE = new DataType("byte", TYPE.NUMERIC);
    public static final DataType UBYTE = new DataType("ubyte", TYPE.NUMERIC);
    public static final DataType SHORT = new DataType("short", TYPE.NUMERIC);
    public static final DataType USHORT = new DataType("ushort", TYPE.NUMERIC);
    public static final DataType INT = new DataType("int", TYPE.NUMERIC);
    public static final DataType UINT = new DataType("uint", TYPE.NUMERIC);
    public static final DataType LONG = new DataType("long", TYPE.NUMERIC);
    public static final DataType FLOAT = new DataType("float", TYPE.NUMERIC);
    public static final DataType DOUBLE = new DataType("double", TYPE.NUMERIC);
    public static final DataType DECIMAL = new DataType("decimal", TYPE.NUMERIC);
    private static final String[] dataTypeNames = {"blob", "boolean", "varchar", "text", 
        "char", "byte", "ubyte", "short", "ushort", "int", "uint", "long", "float",
        "double"};
    private static final byte dataTypesCount = (byte) dataTypeNames.length;
    
    private final String name;
    private final TYPE type;
    
    public static enum TYPE{
        NUMERIC, ALPHANUMERIC, BINARY;
    }

    private DataType(String name, TYPE type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TYPE getType() {
        return type;
    }
    
    public static DataType getDataTypeByName(String name) throws UnknownDataTypeException{
        if(name == null) 
            return null;
        
        else if (name.equalsIgnoreCase(DataType.BLOB.name))
            return DataType.BLOB;
        
        else if (name.equalsIgnoreCase(DataType.BOOLEAN.name))
            return DataType.BOOLEAN;
        
        else if (name.equalsIgnoreCase(DataType.BYTE.name))
            return DataType.BYTE;
        
        else if (name.equalsIgnoreCase(DataType.CHAR.name))
            return DataType.CHAR;
        
        else if (name.equalsIgnoreCase(DataType.DECIMAL.name))
            return DataType.DECIMAL;
        
        else if (name.equalsIgnoreCase(DataType.DOUBLE.name))
            return DataType.DOUBLE;
        
        else if (name.equalsIgnoreCase(DataType.INT.name))
            return DataType.INT;
        
        else if (name.equalsIgnoreCase(DataType.LONG.name))
            return DataType.LONG;
        
        else if (name.equalsIgnoreCase(DataType.SHORT.name))
            return DataType.SHORT;
        
        else if (name.equalsIgnoreCase(DataType.TEXT.name))
            return DataType.TEXT;
        
        else if (name.equalsIgnoreCase(DataType.UBYTE.name))
            return DataType.UBYTE;
        
        else if (name.equalsIgnoreCase(DataType.UINT.name))
            return DataType.UINT;
        
        else if (name.equalsIgnoreCase(DataType.USHORT.name))
            return DataType.USHORT;

        else if (name.equalsIgnoreCase(DataType.VARCHAR.name))
            return DataType.VARCHAR;
        
        else
            throw new UnknownDataTypeException(name);
    }
    
}
