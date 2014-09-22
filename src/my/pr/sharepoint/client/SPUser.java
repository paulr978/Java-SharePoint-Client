/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

/**
 *
 * @author PRando
 */
public class SPUser extends SPObject {

    private String id = null;
    private String name = null;
    private String loginName = null;
    private String email = null;

    public SPUser() {
    
    }
    
    public SPUser(String lookupValue) {
        
    }

    public SPUser(SPUserService.SimpleUser sUser) {
        this.id = sUser.getId();
        this.name = sUser.getName();
        this.loginName = sUser.getLoginName();
        this.email = sUser.getEmail();
    }
    
    public String getLookupValue() {
        return getId() + ";#" + getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
