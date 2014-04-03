/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author PRando
 */
public class SPList extends SPObject {

    private SharePointClient client = null;
    private SPSite parentSite = null;
    private String id = null;
    private String name = null;
    private String title = null;
    private String description = null;
    private String url = null;
    private int itemCount = -1;
    private ArrayList<SPField> fields = null;
    private LinkedHashMap<Integer, SPListRow> rows = null;
    private LinkedHashMap<Integer, SPListRow> markedRows = null;
    private boolean refreshedOnce = false;

    public SPList(SPSite parentSite) {
        this.client = parentSite.getClient();
        this.parentSite = parentSite;
        this.client = parentSite.getClient();

        fields = new ArrayList<SPField>();
        rows = new LinkedHashMap<Integer, SPListRow>();
        markedRows = new LinkedHashMap<Integer, SPListRow>();
    }
    
    public SharePointClient getSharePointClient() {
        return client;
    }

    public SPListRow getBlankListRow() {
        SPListRow row = new SPListRow();
        for (SPField field : fields) {
            row.addRowColumn(field.getName(), null);
            System.out.println("field.getName(): " + field.getName() + " Display Name: " + field.getDisplayName());
        }
        return row;
    }

    public SPListRow getNewListRow() {
        return new SPListRow(this);
    }
    
    public void markRow(SPListRow row) {
        markedRows.put(row.getId(), row);
    }
    
    public void unMarkRow(SPListRow row) {
        if(markedRows.containsKey(row.getId())) {
            markedRows.remove(row.getId());
        }
    }
    
    public boolean isRowMarked(SPListRow row) {
        return markedRows.containsKey(row.getId());
    }
    
    public LinkedHashMap<Integer, SPListRow> getUnMarkedRows() throws IOException {
        LinkedHashMap<Integer, SPListRow> unMarkedRows = new LinkedHashMap<Integer, SPListRow>();
        Iterator<SPListRow> iRows = getRows().values().iterator();
        while(iRows.hasNext()) {
            SPListRow row = iRows.next();
            if(!isRowMarked(row)) {
                unMarkedRows.put(row.getId(), row);
            }
        }
    
        return unMarkedRows;
    }
    
    public LinkedHashMap<Integer, SPListRow> getMarkedRows() {
        return markedRows;
    }

    public boolean isDisplayName(String displayName) {
        for (SPField field : fields) {
            if (field.getDisplayName() != null) {
                if (field.getDisplayName().equalsIgnoreCase(displayName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String convertDisplayNameToName(String displayName) {
        for (SPField field : fields) {
            if (field.getDisplayName() != null) {
                if (field.getDisplayName().equalsIgnoreCase(displayName)) {
                    return field.getName();
                }
            }
        }
        return displayName;
    }

    public boolean isRefreshedOnce() {
        return refreshedOnce;
    }

    public void refreshData() throws IOException {
        client.getListItems(this, 999999999);
        refreshedOnce = true;
    }

    public void refreshData(int rowLimit) throws IOException {
        client.getListItems(this, rowLimit);
        refreshedOnce = true;
    }

    public ArrayList<SPField> getFields() {
        return fields;
    }

    public String[] getFieldNames() {
        String[] names = new String[fields.size()];
        int i = 0;
        for (SPField field : fields) {
            names[i++] = field.getName();
        }
        return names;
    }

    /*
     public void updateListDescription(String description) throws SPException, IOException {
     setDescription(description);
     client.updateListDescription(this);
     }
     */
    public SPListRow insertNewRecord(SPListRow row) throws SPException, IOException {
        return client.insertListItem(this, row);
    }
    
    public SPAttachment[] getAttachments(SPListRow row) throws SPException, IOException {
        client.getAttachments(this, row);
        Map<String, SPAttachment> attachments = row.getAttachmentsMap();
        return attachments.values().toArray(new SPAttachment[attachments.size()]);
    }
    
    public void addAttachment(SPListRow row, String fileName, byte[] bytes) throws SPException, IOException {
        SPAttachment attachment = new SPAttachment(fileName, bytes);
        addAttachment(row, attachment);
    }
    
    public void addAttachment(SPListRow row, SPAttachment attachment) throws SPException, IOException {
        client.addAttachment(this, row, attachment);
    }

    public void updateRecord(SPListRow row) throws SPException, IOException {
        client.updateListItem(this, row);
    }

    public void deleteRecord(SPListRow row) throws IOException {
        if (row.getId() == -1) {
            throw new IOException("Row Id Missing!");
        }
        client.deleteListItem(this, row);
    }

    public void deleteRecords(SPListRow searchRow) throws SPException, IOException {
        refreshData();
        List<SPListRow> foundRows = new ArrayList<SPListRow>();
        Iterator<Integer> ids = rows.keySet().iterator();
        loop:
        while (ids.hasNext()) {
            int id = ids.next();
            SPListRow row = rows.get(id);

            if (searchRow.getId() != -1 && searchRow.getId() == row.getId()) {
                foundRows.add(row);
                continue loop;
            }
            for (String col : searchRow.getColumnNames()) {
                String value = searchRow.getRowColumn(col);
                if (row.containsColumnName(col)) {
                    if (row.getRowColumn(col).equalsIgnoreCase(value)) {
                        foundRows.add(row);
                    }
                }

            }

        }
        client.deleteListItems(this, foundRows);
    }

    public int getRowCount() throws IOException {
        if (!isRefreshedOnce()) {
            refreshData();
        }
        return this.rows.keySet().size();
    }

    public void addRowToCache(SPListRow row) {
        this.rows.put(row.getId(), row);
    }

    public SPListRow getLookupColumn(String column, String value) throws SPException, IOException {
        return getFirstRowContainingColumnValue(column, value);
    }

    public SPListRow getFirstRowContainingColumnValue(String column, String value) throws SPException, IOException {
        //System.out.println("Looking for value: " + value + " in Column: " + column);
        if (!isRefreshedOnce()) {
            refreshData();
        }
        for (SPListRow row : rows.values()) {
            if (row.getRowColumn(column).toUpperCase().contains(value.toUpperCase())) {
                return row;
            }
        }
        return null;
    }

    public SPListRow getFirstRowContainingColumnValues(Map<String, String> columnValues) throws SPException, IOException {
        if (!isRefreshedOnce()) {
            refreshData();
        }
        loop:
        for (SPListRow row : rows.values()) {
            boolean meetsCriteria = false;
            Iterator<String> keys = columnValues.keySet().iterator();
            while (keys.hasNext()) {
                String column = keys.next();
                String value = columnValues.get(column);
                //System.out.println("Looking for value: " + value + " in Column: " + column);

                if (row.containsColumnName(column)) {
                    //System.out.println("Source:_" + row.getRowColumn(column) + "_Comparison:_" + value);
                    if (row.getRowColumn(column).toUpperCase().contains(value.toUpperCase())) {
                        //return row;
                        meetsCriteria = true;
                    } else {
                        meetsCriteria = false;
                        continue loop;
                    }
                }
                else {
                    meetsCriteria = false;
                    continue loop;
                }

            }

            if (meetsCriteria) {
                return row;
            }

        }
        return null;
    }

    public SPListRow[] getDuplicateRows(String column, boolean showAll) throws SPException, IOException {
        Map<String, SPListRow> map = new HashMap<String, SPListRow>();
        ArrayList<SPListRow> dups = new ArrayList<SPListRow>();
        if (!isRefreshedOnce()) {
            refreshData();
        }
        loop:
        for (SPListRow row : rows.values()) {
            String value = row.getRowCascadedLookupColumn(column);
            if (map.containsKey(value)) {
                dups.add(row);
                if (showAll) {
                    dups.add(map.get(value));
                }
                continue loop;
            }
            map.put(value, row);

        }
        return dups.toArray(new SPListRow[dups.size()]);
    }

    public SPListRow[] getRowsByContainingSingleColumn(String column, String value) throws SPException, IOException {
        //System.out.println("Looking for value: " + value + " in Column: " + column);
        ArrayList<SPListRow> list = new ArrayList<SPListRow>();

        if (!isRefreshedOnce()) {
            refreshData();
        }
        for (SPListRow row : rows.values()) {
            if (row.getRowColumn(column).toUpperCase().contains(value.toUpperCase())) {
                list.add(row);
            }
        }
        return list.toArray(new SPListRow[list.size()]);
    }

    public LinkedHashMap<Integer, SPListRow> getRows() throws IOException {
        if (!isRefreshedOnce()) {
            refreshData();
        }
        return rows;
    }

    public SPListRow[] getFirstRows(int length) throws IOException {
        SPListRow[] listRows = null;
        if (getRowCount() < length) {
            listRows = new SPListRow[getRowCount()];
        } else {
            listRows = new SPListRow[length];
        }

        int count = 0;
        Iterator<SPListRow> iRows = rows.values().iterator();
        loop:
        while (iRows.hasNext()) {
            listRows[count] = iRows.next();
            if (++count >= length) {
                break loop;
            }

        }

        return listRows;
    }

    public SPListRow getRow(int id) throws IOException, SPException {
        if (!isRefreshedOnce()) {
            refreshData();
        }
        SPListRow row = this.rows.get(id);
        
        if(row == null) throw new SPException("Row with Id: " + id + " Not Found!");
        return row;
    }

    public void addField(SPField field) {
        this.fields.add(field);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SPSite getParentSite() {
        return parentSite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
