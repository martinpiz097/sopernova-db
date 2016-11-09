/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.model;

/**
 *
 * @author martin
 */
public interface TableModel {
    public String getName();
    
    public int getColumnCount();

    public String getColumnName(int columnIndex);

    public String getColumnDataType(int columnIndex);

    public boolean isFieldEditable(int columnIndex);
    
    public Object getValueAt(int rowIndex, int columnIndex);
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex);
    
}
