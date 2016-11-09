/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.db;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.martin.electroList.structure.ElectroList;
import org.martin.supernovadb.model.Column;
import org.martin.supernovadb.model.Record;
import org.martin.supernovadb.model.Table;
import org.martin.supernovadb.stream.DataReader;
import org.martin.supernovadb.stream.DataWriter;
import org.martin.supernovadb.system.SysInfo;

/**
 *
 * @author martin
 */
public final class TableManager implements Serializable{
    private File tableDir;
    private File recordsFile;
    
    // Usados para gestionar como objeto los datos de la tabla(nombre, columnas, clase)
    // Ver más adelante una posible migracion a texto.
    private transient TableMetadata tblMetadata;
    
    // Usados para los gestionar registros como texto.
    private transient DataWriter dataWriter;
    private transient DataReader dataReader;
    
    private transient Column[] tblColumns;
    private transient ElectroList<Record> records;

    //private transient ExecutorService threadsExecutor;
    private transient boolean autoSave;
    
    private class SAVE_TYPE{
        static final boolean APPEND = true;
        static final boolean NOT_APPEND = false;
    }
    
    public TableManager(String relatedDb, String tableName, Column[] tblColumns) {
        try {
            tableDir = new File(SysInfo.ROOT_DIR.getCanonicalPath()+"/"+relatedDb, tableName);
            
            if (!tableDir.exists())
                tableDir.mkdir();

            tblMetadata = new TableMetadata(tableDir, tableName);
            this.tblColumns = tblColumns;
            
            recordsFile = new File(tableDir, "records.db");
            
            if(!recordsFile.exists())
                recordsFile.createNewFile();

            openConnection();
            records = getDeserializedRecords();
            //threadsExecutor = Executors.newFixedThreadPool(1000);
            verifyWriter();
            enableAutoSave();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setProperties(File file, boolean bool){
        file.setReadable(bool);
        file.setReadable(bool);
        file = null;
    }
    
    private void lockRecordsFile(){
        setProperties(recordsFile, false);
    }
    
    private void unlockRecordsFile(){
        setProperties(recordsFile, true);
    }

    private boolean isWriterClosed(){
        return dataWriter == null;
    }
    
    private boolean isReaderClosed(){
        return dataReader == null;
    }
    
    private void verifyWriter() {
        if (isWriterClosed()) 
            try {
                dataWriter = new DataWriter(recordsFile);
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void verifyReader() {
        if (isReaderClosed())
            try {
                dataReader = new DataReader(recordsFile);
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeWriter(){
        try {
            dataWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataWriter = null;
    }
    
    private void closeReader(){
        dataReader = null;
    }
    
    private String[] getTransformedObject(String str){
        return str.substring(1, str.length()-1).split(", ");
    }
    
//    public Table getTable() throws IOException, ClassNotFoundException{
//        return tblProperties.getTable();
//    }
    
    public boolean hasRecords(){
        return !records.isEmpty();
    }
    
    public void storeTable(Table t) throws IOException{
        tblMetadata.setRelatedDB(t.getRelatedDB());
        tblMetadata.setName(t.getName());
        tblMetadata.setColumns(t.getColumns());
    }

    private void clearFile() throws IOException {
        recordsFile.delete();
        recordsFile.createNewFile();
    }

    private void clearAndReboot() throws IOException{
        clearFile();
        closeWriter();
        verifyWriter();
    }
    
    private void updateRecords(){
        try {
            clearAndReboot();
            for (Record r : records)
                dataWriter.writeRecord(Arrays.toString(r.getFields()));
            
            dataWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void openConnection(){
        unlockRecordsFile();
    }
    
    public void shutdown(){
        lockRecordsFile();
    }
    
    public void saveChanges(){
        updateRecords();
    }
    
    public void flushRecordsFile() {
        try {
            dataWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addRecord(String[] fields) throws IOException{
        records.add(new Record(fields));
        dataWriter.writeRecord(Arrays.toString(fields));
        if(autoSave)
            flushRecordsFile();
    }
    
    public Record getRecordAt(int index){
        return records.isEmpty() ? null : records.get(index);
    }
    
    public Record getRecordBy(int columnIndex, Object valueToFind){
        Object[] r;
        for (Record record : records)
            if (record.getField(columnIndex) == valueToFind)
                return record;
        return null;
    }
    
    public ElectroList<Record> getRecordsBy(int columnIndex, Object valueToFind){
        ElectroList<Record> listResults = new ElectroList<>();
        
        for (Record record : records) 
            if (record.getField(columnIndex).equals(valueToFind))
                listResults.add(record);
        
        return listResults;
    }

    private ElectroList<Record> getDeserializedRecords(){
        ElectroList<Record> listRecords = new ElectroList<>();
        try {
            verifyReader();
            for (String line : dataReader.readLines()){
                listRecords.add(new Record(getTransformedObject(line)));
            }
            closeReader();
        } catch (IOException ex) {
            closeReader();
        } finally {
            return listRecords;
        }
    }
    
    public int getRecordsCount(){
        return records.size();
    }
    
    public ElectroList<Record> getRecords() {
        return records;
    }
    
    public void setRecord(int index, String... record) {
        records.get(index).setFields(record);
        if(autoSave) updateRecords();
    }
    
    public void enableAutoSave(){
        autoSave = true;
    }
    
    public void disableAutoSave(){
        autoSave = false;
    }
    
    public void setRecord(String object, int row, int column){
        Record get = records.get(row);
        get.setField(column, object);
        records.set(row, get);
        if(autoSave) updateRecords();
        get = null;
    }
    
    public void deleteRecordAt(int index){
        try {
            records.remove(index);
            
            verifyWriter();
            clearFile();
            
            // Ver si concatenar String y despues insertar en el archivo es más
            // rápido que escribir en el archivo por cada línea.
            
            for (Record r : records)
                dataWriter.writeRecord(Arrays.toString(r.getFields()));
            
            dataWriter.flush();
            closeWriter();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteRecord(Object[] record){
        try {
            records.remove(record);
            verifyWriter();
            clearFile();
            for (Record r : records)
                dataWriter.writeRecord(Arrays.toString(r.getFields()));
                
            dataWriter.flush();
            // Ver ese tema para editar directamente el archivo con los registros.
            //RandomAccessFile raf = new RandomAccessFile(tableDir, "rw");
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteRecords(){
        try {
            clearFile();
            records.clear();
        } catch (IOException ex) {
            Logger.getLogger(TableManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteAll(){
        tblMetadata.deleteLogFile();
        recordsFile.delete();
        tableDir.delete();
    }
}
