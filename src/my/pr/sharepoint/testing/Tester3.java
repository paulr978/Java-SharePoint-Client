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
public class Tester3 {
    
    
    
    public static void main(String[] args) throws Exception {
        viewEnvironmentsList();
        //updateEnvironments();
    }
    
    
    public static void viewEnvironmentsList() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);

        SPList newEnvList = custSite.getListService().getList("Customer Environments");
        
       
    }
    
    
    public static void updateEnvironments() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList prdAssignSyncList = refSite.getListService().getList("Product Offering Assignments Sync");
        prdAssignSyncList.getRows();
        
        SPList envList = testSite.getListService().getList("CustomerGeneral0331");
        envList.getRows();
        
        SPList newEnvList = custSite.getListService().getList("Customer Environments");
        
        int count = 0;
        loop:
        for (SPListRow envListRow : envList.getRows().values()) {
            if(!envListRow.containsColumnName("LinkTitle")) continue loop;
            if(!envListRow.containsColumnName("URL")) continue loop;
            if(!envListRow.containsColumnName("Hosting Center")) continue loop;
            
            String solutionId = envListRow.getRowColumn("LinkTitle");
            
            String urls = "";
            if(envListRow.containsColumnName("URL")) {
                urls = envListRow.getRowColumn("URL");
            }

            String serverInfo1 = "";
            if(envListRow.containsColumnName("Server_x002f_Device_x0020_Names")) {
                serverInfo1 = envListRow.getRowColumn("Server_x002f_Device_x0020_Names");
            }
            
            String passwords = "";
            if(envListRow.containsColumnName("SuperUserPWD")) {
                passwords = envListRow.getRowColumn("SuperUserPWD");
            }
            
            String dc = envListRow.getRowColumn("Hosting Center");
            
            String serverInfo2 = "";
            if(envListRow.containsColumnName("Server_x0020_IPAddress")) {
                serverInfo2 = envListRow.getRowColumn("Server_x0020_IPAddress");
            }
            
            Map map = new HashMap();
            map.put("Solution_x0020_Id", solutionId);
            map.put("Data_x0020_Center", dc);
            SPListRow prdSyncRow = prdAssignSyncList.getFirstRowContainingColumnValues(map);
            
            SPListRow prdRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(prdSyncRow == null) {
                //System.out.println("Unable to find PrdAssignment! - " + solutionId);
                continue loop;
            }
            
            String envType = "6;#ALL";
            
            String customerName = prdSyncRow.getRowColumn("Customer Name");
            String prdIdentifier = prdSyncRow.getId() + ";#" + prdSyncRow.getRowColumn("Product Identifier");
            
            if(count < 12) {
            
            } else if(count >= 12 && count < 16) {
            //if(solutionId.contains("6115164")) {
                /*
                System.out.println("Solution Id: " + solutionId);
                System.out.println("Customer Name: " + customerName);
                System.out.println("Prd Identifier: " + prdIdentifier);
                System.out.println("URLS: " + urls);
                System.out.println("Server Info: " + serverInfo1 + "\n" + serverInfo2);
                System.out.println("Environment Type: " + envType);
                System.out.println("Passwords: " + passwords);
                */ 
                
                SPListRow newRow = newEnvList.getNewListRow();
                newRow.addRowColumn("Solution_x0020_Id", prdRow.getRowColumn("Solution Id"));
                newRow.addRowColumn("Customer_x0020_Name", prdRow.getRowColumn("Customer Name"));
                newRow.addRowColumn("Product_x0020_Identifier", prdIdentifier);
                newRow.addRowColumn("Environment_x0020_Type", envType);
                newRow.addRowColumn("Application_x0020_URL_x0020_List", urls);
                newRow.addRowColumn("Host_x0020_Server_x0020_Informat", serverInfo1 + "\n" + serverInfo2);
                newRow.addRowColumn("Legacy_x0020__x002d__x0020_Passw", passwords);
                
                newEnvList.insertNewRecord(newRow);
                
            }
            else {
                break loop;
            }
            
            count++;
        }
    
    }
    
}
