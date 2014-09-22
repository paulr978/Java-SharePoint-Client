/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.client;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author PRando
 */
public class SPListUtils {
    
    public static String stripCascadedLookupId(String value) {
        if(value.contains(";#")) {
            String newValue = value.substring(value.indexOf(";#") + 2);
            //System.out.println("Returning New Value: " + newValue);
            return newValue;
        }
        //System.out.println("Returning Same Value: " + value);
        return value;
    }
    
    public static int AppendAllRowsSerial(SPList src, SPList des, Map<String, String> columnNames) throws SPException, IOException {
        return AppendAllRowsSerial(src, des, columnNames, 999999999);
    }
    
    public static int AppendAllRowsSerial(SPList src, SPList des, Map<String, String> columnNames, int length) throws SPException, IOException {
        if(columnNames.size() == 0) throw new SPException("Columns Map can not be Empty!");
        
        src.refreshData();
        int count = 0;
        loop:for(SPListRow srcRow : src.getRows().values()) {
            SPListRow desRow = des.getNewListRow();
            for(String srcColumnName : columnNames.keySet()) {
                String desColumnName = columnNames.get(srcColumnName);
                desRow.addRowColumn(desColumnName, srcRow.getRowColumn(srcColumnName));
            }
            des.insertNewRecord(desRow);
            if(++count >= length) break loop;
        }
        return count;
    }
    
    
    
}
