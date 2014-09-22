/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
