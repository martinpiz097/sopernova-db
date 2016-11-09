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
 */
public class ForeignKey extends Column implements Serializable{
    // Tabla relacionada con la foreign key
    private final Table relatedTable;

    public ForeignKey(Table relatedTable, String dataType, String name) throws UnknownDataTypeException {
        super(dataType, name, false, false, true);
        this.relatedTable = relatedTable;
    }

    public Table getRelatedTable() {
        return relatedTable;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        String superToString = super.toString();
        sBuilder.append(superToString.substring(0, superToString.length()-1));
        sBuilder.append(SEPARATOR);
        sBuilder.append(relatedTable.getName());
        sBuilder.append(']');
        String toString = sBuilder.toString();
        sBuilder = null;
        return toString;
    }
    
}
