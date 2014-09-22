/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author PRando
 */
public class SPListRow extends SPObject {
    
    private int id = -1;
    private Map<String, String> rowColumns = null;
    private SPList list = null;
    private boolean listDerived = false;
    
    private boolean hasAttachments = false;
    private boolean attachmentsLoaded = false;
    
    private Map<String, SPAttachment> attachments = null;
    //private Map<String, String> displayNamesToColumns = null;
    
    public SPListRow() {
        rowColumns = new HashMap<String, String>();
        attachments = new HashMap<String, SPAttachment>();
        //displayNamesToColumns = new HashMap<String, String>();
    }
    
    public SPListRow(SPList list) {
        this.list = list;
        rowColumns = new HashMap<String, String>();
        listDerived = true;
        attachments = new HashMap<String, SPAttachment>();
        //displayNamesToColumns = new HashMap<String, String>();
    }
    
    public void addAttachment(SPAttachment attachment) {
        attachments.put(attachment.getFileName(), attachment);
    }
    
    public SPAttachment getAttachment(String fileName) throws SPException, IOException {
        //if(!attachmentsLoaded) loadAttachments();
        return attachments.get(fileName);
    }
    
    public Map<String, SPAttachment> getAttachmentsMap() throws SPException, IOException {
        //if(!attachmentsLoaded) loadAttachments();
        return attachments;
    }
    
    public SPList getList() {
        return list;
    }
    
    public SPAttachment[] loadAttachments() throws SPException, IOException {
        if(hasAttachments) {
            SPAttachment[] attachments = list.getAttachments(this);
            attachmentsLoaded = true;
            return attachments;
        }
        return null;
    }
    
    /**
     * May be used to update specific columns for a row
     * 
     * @return 
     */
    public SPListRow cloneNewRow() {
        SPListRow row = null;
        if(list != null) {
            row = new SPListRow(list);
        }
        else {
            row = new SPListRow();
        }
        row.setId(getId());
        
        return row;
    }
    
    public SPListRow cloneNewRowWithData() {
        SPListRow row = cloneNewRow();
        
        Iterator<String> keys = rowColumns.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            String value = rowColumns.get(key);
            row.addRowColumn(key, value);
        }
        
        return row;
    }
    
    public boolean isListDerived() {
        return listDerived;
    }
    
    public boolean containsColumnName(String name) {
        String identifier = getIdentifier(name);
        return rowColumns.containsKey(identifier);
    }
    
    public String[] getColumnNames() {
        return rowColumns.keySet().toArray(new String[rowColumns.size()]);
    }
    
    public int getColumnsCount() {
        return rowColumns.size();
    }
    
    public String getIdentifier(String name) {
        String identifier = name;
        if(isListDerived() && list.isDisplayName(name)) {
            identifier = list.convertDisplayNameToName(name);
        }
        return identifier;
    }
    
    public void addRowColumn(String name, String value) {
        String identifier = getIdentifier(name);
        
        //Cleanup for Cascaded Lookup Add-on
        if(value != null) {
            if(value.startsWith("string;#")) value = value.replace("string;#", "");
        }
        value = value.trim();
        rowColumns.put(identifier, value);
    }
    
    public String getRowColumn(String name) throws SPException {
        String identifier = getIdentifier(name);
        
        if(!rowColumns.containsKey(identifier)) {
            Iterator<String> rc = rowColumns.keySet().iterator();
            while(rc.hasNext()) {
                String key = rc.next();
                String value = rowColumns.get(key);
                System.out.println("Row Columns: " + key + " " + value);
            }
            throw new SPException("Column Name " + identifier + " does not Exist!");
        }
        return rowColumns.get(identifier);
    }
    
    public String getRowCascadedLookupColumn(String name) throws SPException {
        String identifier = getIdentifier(name);
        
        if(!rowColumns.containsKey(identifier)) throw new SPException("Column Name " + identifier + " does not Exist!");
        return SPListUtils.stripCascadedLookupId(rowColumns.get(identifier));
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();
        for(String column : rowColumns.keySet()) {
            s.append(column + ": " + rowColumns.get(column) + "\t");
        }
        return s.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the hasAttachments
     */
    public boolean isHasAttachments() {
        return hasAttachments;
    }

    /**
     * @param hasAttachments the hasAttachments to set
     */
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }
}
