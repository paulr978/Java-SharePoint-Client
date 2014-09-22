/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.client;

/**
 *
 * @author PRando
 */
public class DefaultRetryProcessor implements SPCommRetryProcessor {
    
    public DefaultRetryProcessor() {
    
    }

    @Override
    public void onRetry(SPListRow row) {
        System.out.println("On (Default) Retry Processed...");
    }
    
}
