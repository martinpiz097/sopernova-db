/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.model;

import org.martin.supernovadb.db.exceptions.DuplicatedPrimaryKeyException;
import org.martin.supernovadb.db.exceptions.NullForeignKeyException;
import org.martin.supernovadb.db.exceptions.IncompatibleDataTypeException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.martin.electroList.structure.ElectroList;
import org.martin.supernovadb.db.TableManager;
import org.martin.supernovadb.db.exceptions.NullPrimaryKeyException;
import org.martin.supernovadb.db.exceptions.UnknownColumnException;

/**
 *
 * @author martin
 */
public final class Table implements Serializable, TableModel {
    private final String relatedDb;
    private String name;
    private final Column[] columns;
    //private final LinkedList<Object[]> records;
    private transient TableManager tableManager;
    
    public Table(String relatedDb, String name, Column[] columns) {
        this(relatedDb, name, columns, true);
    }
    
    public Table(String relatedDb, String name, Column[] columns, boolean store) {
        this.relatedDb = relatedDb;
        this.name = name;
        this.columns = columns;
        instanceManager();
        if(store) storeTable();
    }
    
    private boolean isPkAlreadyUsed(Object pk, byte columnIndex){
        return tableManager.getRecordBy(columnIndex, pk) != null;
    }

    public boolean hasRecords(){
        return tableManager.hasRecords();
    }
    
    public void instanceManager(){
        this.tableManager = new TableManager(relatedDb, name, columns);
    }
    
    public void setName(String newName){
        this.name = newName;
        storeTable();
    }

    public int selectCount(){
        return tableManager.getRecordsCount();
    }
    
    public List<Object[]> getRecords() {
        return tableManager.getRecords();
    }

    public TableManager getTableManager() {
        return tableManager;
    }
    
    public void storeTable() {
        try {
            tableManager.storeTable(this);
        } catch (IOException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void drop(){
        tableManager.deleteAll();
    }

    public boolean delete(String columnNames, Object... valuesToFind){
        String[] columnsSplit = columnNames.split(",");
        boolean columnFinded = false, toDeleted = false, fieldFinded = false;
        String colName;
        
        for (int i = 0; i < columnsSplit.length; i++){
            columnsSplit[i] = columnsSplit[i].trim();
            colName = columnsSplit[i];
            for (Column column : columns) {
                if (column.getName().equals(colName)) {
                    columnFinded = true;
                    for (Object[] record : tableManager.getRecords()) {
                        for (int j = 0; j < record.length; j++) {
                            if (record[j].equals(valuesToFind[i])) {
                                toDeleted = true;
                                fieldFinded = true;
                                break;
                            }
                            else{
                                toDeleted = false;
                                fieldFinded = false;
                            }
                        }
                        if (fieldFinded && toDeleted) 
                            tableManager.deleteRecord(record);
                        
                    }
                    break;
                }
            }
            if (!columnFinded)
                return false;
            else
                columnFinded = false;
        }
        
        return true;
    }
    
    public void deleteAll(){
        tableManager.deleteRecords();
    }
    
    public int getRecordsCount(){
        return tableManager.getRecordsCount();
    }
    
    public Object[] getFirst(){
        if (!hasRecords()) return null;

        return tableManager.getRecordAt(0);
    }
    
    public Object[] getLast(){
        if(!hasRecords()) return null;
        
        return tableManager.getRecordAt(tableManager.getRecordsCount()-1);
    }
    
    public void addRecord(Object... record) throws IncompatibleDataTypeException, IOException, 
            NullForeignKeyException, NullPrimaryKeyException, DuplicatedPrimaryKeyException{
        //byte counter = 0;
        
        // Comprobar si el registro no tiene claves nulas, si los tipos de datos coinciden
        // con la estructura de la tabla o si la cantidad de columnas es igual a la cantidad
        // establecida en la tabla.
        
        
        Column col;
        Object field;
        int recordLen = record.length; 
        long curData;

        for (byte i = 0; i < recordLen; i++) {
            // Considerar que se debe cambiar la condicion de auto incremento cuando
            // el dato no es numerico.
            field = record[i];
            if (field == null) {
                col = columns[i];
                if (col.isAutoIncrement()) {
                    if(hasRecords())
                        curData = Long.parseLong(getLast()[i].toString())+1;
                    
                    else
                        curData = 1;
                    
                    record[i] = curData;
                    field = record[i];
                }
                else if (col.isPK() || col instanceof ForeignKey)
                    if (col.isPK())
                        throw new NullPrimaryKeyException();
                    
                    else
                        throw new NullForeignKeyException();
            }
            else if (!field.getClass().getName().equals(columns[i].getColumnDataType()))
                    throw new IncompatibleDataTypeException();
            else if (columns[i].isPK() && isPkAlreadyUsed(field, i))
                throw new DuplicatedPrimaryKeyException(field.toString());
        }
        tableManager.addRecord(record);
    }
    
    public String getRelatedDB(){
        return relatedDb;
    }

    public Column[] getColumns() {
        return columns;
    }
    
    public ElectroList<Record> getRecordsBy(String columnName, Object valueToFind) 
            throws UnknownColumnException{
        boolean columnExists = false;
        int colIndex = 0;
        int colLen = columns.length;
        
        for (int i = 0; i < colLen; i++) {
            if (columns[i].getName().equals(columnName)) {
                columnExists = true;
                colIndex = i;
                break;
            }
        }
        if (!columnExists) 
            throw new UnknownColumnException(columnName, getName());
            
        return tableManager.getRecordsBy(colIndex, valueToFind);
    }
    
//    public List<Object[]> getRecordsBy(String columnName, Predicate predicate) 
//            throws UnknownColumnException{
//        boolean columnExists = false;
//        int colIndex = 0;
//        int colLen = columns.length;
//        
//        for (int i = 0; i < colLen; i++) {
//            if (columns[i].getName().equals(columnName)) {
//                columnExists = true;
//                colIndex = i;
//                break;
//            }
//        }
//        if (!columnExists) 
//            throw new UnknownColumnException(columnName, getName());
//            
//        return tableManager.getRecordsBy(colIndex, predicate);
//    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex].getName();
    }

    @Override
    public String getColumnDataType(int columnIndex) {
        return columns[columnIndex].getColumnDataType();
    }

    @Override
    public boolean isFieldEditable(int columnIndex) {
        Column c = columns[columnIndex];
        return !c.isPK() || !c.isAutoIncrement();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getRecords().get(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(String aValue, int rowIndex, int columnIndex) {
        tableManager.setRecord(aValue, rowIndex, columnIndex);
    }
//
//    public String toSerialString(){
//        return "db="+relatedDb+"\nname="+name+"columns="+Arrays.toString(columns);
//    }
//    
    @Override
    public String toString() {
        System.out.print('+');
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print('-');
            }
            System.out.print('+');
        }
        System.out.println("");
        
        int colNameLen;
        System.out.print("|");
        for (Column column : columns) {
            colNameLen = column.getName().length();
            System.out.print(column.getName());
            for (int i = 0; i < 10-colNameLen; i++) {
                System.out.print(' ');
            }
            System.out.print("|");
        }
        
        System.out.println("");
        System.out.print('+');
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print('-');
            }
            System.out.print('+');
        }
        System.out.println("");
        return null;
    }

}
