/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.supernovadb.system;

import java.io.File;

/**
 *
 * @author martin
 */
public class SysInfo {
    public static final File ROOT_DIR = new File("omegadb");
    
    static {
        if (!ROOT_DIR.exists()) ROOT_DIR.mkdir();
    }
}
