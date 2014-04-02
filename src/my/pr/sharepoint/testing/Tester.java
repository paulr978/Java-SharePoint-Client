/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.pr.sharepoint.testing;

import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SPNTCreds;
import my.pr.sharepoint.client.SPSite;

/**
 *
 * @author Paul
 */
public class Tester {
    
    
    public static void main(String[] args) throws Exception {
        viewEnvironmentsList();
        //updateEnvironments();
    }
    
    
    public static void viewEnvironmentsList() throws Exception {
        SPNTCreds creds = new SPNTCreds("Administrator", "pyramid007", "server-a");
        SPSite custSite = new SPSite("http://server-a/Test1", creds);

        SPList newEnvList = custSite.getListService().getList("Test List");
        
       
    }
}
