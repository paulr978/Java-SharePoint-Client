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
public class SPListService {
    
    private SharePointClient client = null;
    private SPSite parentSite = null;
    
    private Map<String, SPListService.SimpleList> lists = null;
    private Map<String, String> titlesToIds = null;
    
    public SPListService(SPSite parentSite) {
        this.client = this.client = parentSite.getClient();
        this.parentSite = parentSite;
        this.lists = new HashMap<String, SimpleList>();
        titlesToIds = new HashMap<String, String>();
    }
    
    public SPList getList(String titleOrId) throws IOException, SPException {
        String identifier = titleOrId;
        if(titlesToIds.containsKey(identifier)) identifier = titlesToIds.get(titleOrId);
        if(!doesListIdentifierExist(identifier)) {throw new SPException("List " + titleOrId + " / " + identifier + " does not Exist!");}
        
        return client.getList(titleOrId, parentSite);
    }
    
    public SPList getList(String titleOrId, String viewName) throws IOException, SPException {
        String identifier = titleOrId;
        if(titlesToIds.containsKey(identifier)) identifier = titlesToIds.get(titleOrId);
        if(!doesListIdentifierExist(identifier)) {throw new SPException("List " + titleOrId + " / " + identifier + " does not Exist!");}
        
        
        SPView[] views = client.getViewCollection(identifier, parentSite);
        for(SPView view : views) {
            if(view.getDisplayName().equalsIgnoreCase(viewName)) {
                return client.getList(titleOrId, view, parentSite);
            }
        }
         
        
        return client.getList(titleOrId, parentSite);
    }
        
    public void addList(String id, String name, String title, String description, int itemCount) {
        lists.put(id, new SimpleList(id, name, title, description, itemCount));
        titlesToIds.put(id, name);
    }
        
    public SimpleList[] getSimpleLists() {
        return lists.values().toArray(new SimpleList[lists.size()]);
    }
    
    public int getListsCount() {
        return lists.size();
    }
    
    public boolean doesListIdentifierExist(String titleOrId) {
        for(SimpleList list : lists.values()) {
            if(list.getId().equalsIgnoreCase(titleOrId)) return true;
            if(list.getTitle().equalsIgnoreCase(titleOrId)) return true;
        }
        return false;
    }
    
    
    public class SimpleList {
        
        private String id = null;
        private String name = null;
        private String title = null;
        private String description = null;
        private int itemCount = -1;
        
        public SimpleList() {
        
        }
        
        public SimpleList(String id, String name, String title, String description, int itemCount) {
            this.id = id;
            this.name = name;
            this.title = title;
            this.description = description;
            this.itemCount = itemCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }
    
    }
    
    
}
