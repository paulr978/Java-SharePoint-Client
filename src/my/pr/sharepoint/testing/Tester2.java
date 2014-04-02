/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.testing;

import java.util.HashMap;
import java.util.Map;
import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SPListRow;
import my.pr.sharepoint.client.SPNTCreds;
import my.pr.sharepoint.client.SPSite;

/**
 *
 * @author PRando
 */
public class Tester2 {
    
    
    public static void main(String[] args) throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
    
        SPList oldList = testSite.getListService().getList("CustomerMaster20140205");
        oldList.getRows();
        SPList newList = testSite.getListService().getList("CustomerMaster0219");
        newList.getRows();
        
        for(SPListRow newRow : newList.getRows().values()) {
            String newSolutionId = newRow.getRowColumn("SID");
            String newCustomerName = newRow.getRowColumn("Solution_Owner_Name");
            String dc = newRow.getRowColumn("Hosting_Center");
            
            Map map = new HashMap();
            map.put("SID", newSolutionId);
            map.put("Hosting_Center", dc);
            SPListRow oldRow = oldList.getFirstRowContainingColumnValues(map);
            
            if(oldRow == null) {
                buffer.append("Missing Customer: " + newSolutionId + ", Name: " + newCustomerName + ", DC: " + dc + "\n");
                System.out.println("Customer Missing!!! - " + newSolutionId);
            }
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
    
    }
    
    public static void main1(String[] args) throws Exception {
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        SPList testList = testSite.getListService().getList("Test List 5");
        testList.getRows();
    
    }
}
