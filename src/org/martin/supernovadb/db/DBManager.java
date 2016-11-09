/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.db;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.martin.electroList.structure.ElectroList;
import org.martin.supernovadb.db.exceptions.IncompatibleAutoIncrementException;
import org.martin.supernovadb.db.exceptions.UnknownDataTypeException;
import org.martin.supernovadb.model.Column;
import org.martin.supernovadb.model.ForeignKey;
import org.martin.supernovadb.model.Table;
import org.martin.supernovadb.stream.OIS;
import org.martin.supernovadb.system.SysInfo;

/**
 *
 * @author martin
 */
public class DBManager implements Serializable{
    private final File dbDir;
    private OIS inputStream;
    
    public DBManager(String dbName) {
        dbDir = new File(SysInfo.ROOT_DIR, dbName);
        if (!dbDir.exists())
            dbDir.mkdir();
        
    }
    
    private Column[] getColumnsFromString(String strColumns) throws ClassNotFoundException, IncompatibleAutoIncrementException, UnknownDataTypeException{
        strColumns = strColumns.substring(1, strColumns.length()-1);
        String[] columnSplit = strColumns.split(", ");
        String[] fieldsSplit;
        ElectroList<Column> listColumns = new ElectroList<>();
        
        String fieldType;
        Column[] columns;
        Table relatedTbl;
        String columnName;
        
        boolean isAutoIncrement, isPK, isNotNull;
        
        for (String column : columnSplit) {
            fieldsSplit = column.substring(1, column.length()-1).split("~");
            
            fieldType = fieldsSplit[0];
            columnName = fieldsSplit[1];
            
            // Es columna normal
            if (fieldsSplit.length == 5) {
                isAutoIncrement = Boolean.valueOf(fieldsSplit[2]);
                isPK = Boolean.valueOf(fieldsSplit[3]);
                isNotNull = Boolean.valueOf(fieldsSplit[4]);
                listColumns.add(new Column(columnName, fieldType, isAutoIncrement, isPK, isNotNull));
            }
            // Es foreign key --> tiene 6 campos
            else{
                relatedTbl = getTableAt(new File(dbDir, fieldsSplit[5]+".xml"));
                listColumns.add(new ForeignKey(relatedTbl, fieldType, columnName));
            }
        }
        return listColumns.toArray();
    }
    
    private Table getTableAt(File tblFile){
        try {
            TableMetadata metadata = new TableMetadata(tblFile);
            Column[] columns = getColumnsFromString(metadata.getColumns());
            Table tbl = new Table(metadata.getRelatedDB(), metadata.getName(), columns, false);
            
            columns = null;
            metadata = null;
            return tbl;
        } catch (IOException | ClassNotFoundException | IncompatibleAutoIncrementException | UnknownDataTypeException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void addFoundTablesTo(ElectroList<Table> listTables){
        File[] tblDirs = dbDir.listFiles(File::isDirectory);
        
        if (tblDirs != null) {
            File[] infoFiles;
            for (File tblDir : tblDirs) {
                infoFiles = tblDir.listFiles(f->!f.isDirectory());
                
                if (infoFiles != null) {
                    for (File infoFile : infoFiles) {
                        if (infoFile.getName().contains(tblDir.getName()) && 
                                infoFile.getName().endsWith(".xml")){
                            System.out.println("infoFile: "+infoFile);
                            System.out.println("infoFileExiste: "+infoFile.exists());
                            listTables.add(getTableAt(infoFile));
                            break;
                        }
                    }
                }
                
            }
        }
    }
    
    public ElectroList<Table> getAllTables(){
        ElectroList<Table> listTables = new ElectroList<>();
        addFoundTablesTo(listTables);
        return listTables;
    }
}
