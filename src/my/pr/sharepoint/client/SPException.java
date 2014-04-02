/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

/**
 *
 * @author PRando
 */
public class SPException extends Exception {
    
    public SPException(String msg) {
        super(msg);
    }
    
    public SPException(String msg, Throwable t) {
        super(msg, t);
    }
    
    public SPException(String msg, Exception e) {
        super(msg, e);
    }
}
