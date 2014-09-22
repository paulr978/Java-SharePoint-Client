/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PRando
 */
public class SPUserService {
    
    private SharePointClient client = null;
    private SPSite parentSite = null;
    
    private Map<String, SPUserService.SimpleUser> users = null;
    private Map<String, String> namesToIds = null;
    private boolean loaded = false;
    
    public SPUserService(SPSite parentSite) throws IOException {
        this.client = this.client = parentSite.getClient();
        this.parentSite = parentSite;
        this.users = new HashMap<String, SPUserService.SimpleUser>();
        this.namesToIds = new HashMap<String, String>();
        
    }
    
    public SPUser getUser(String nameOrId) throws IOException, SPException {
        if(!loaded) {
            loadUsers();
        }
        String identifier = nameOrId;
        if(namesToIds.containsKey(identifier)) identifier = namesToIds.get(nameOrId);
        if(!doesUserIdentifierExist(identifier)) {throw new SPException("User " + nameOrId + " / " + identifier + " does not Exist!");}
        
        return new SPUser(users.get(identifier));
        
    }
    
    public boolean doesUserIdentifierExist(String nameOrId) {
        for(SPUserService.SimpleUser user : users.values()) {
            if(user.getId().equalsIgnoreCase(nameOrId)) return true;
            if(user.getLoginName().equalsIgnoreCase(nameOrId)) return true;
        }
        return false;
    }
    
    public void addUser(String id, String name, String loginName, String eMail) {
        users.put(id, new SimpleUser(id, name, loginName, eMail));
        namesToIds.put(name, id);
    }
    
    private void loadUsers() throws IOException {
        this.client.getUserCollection(parentSite);
        loaded = true;
    }
    
    public class SimpleUser {
        
        private String id = null;
        private String name = null;
        private String loginName = null;
        private String email = null;
        
        public SimpleUser() {}
        
        public SimpleUser(String id, String name, String loginName, String email) {
            this.id = id;
            this.name = name;
            this.loginName = loginName;
            this.email = email;
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
    
}
