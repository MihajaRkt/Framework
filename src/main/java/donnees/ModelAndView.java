package donnees;

import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> hashmap;
    
    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public Map<String, Object> getHashmap() {
        return hashmap;
    }
    public void setHashmap(Map<String, Object> hashmap) {
        this.hashmap = hashmap;
    }
}
