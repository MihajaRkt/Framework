package donnees;

import java.util.HashMap;
import java.util.Map;

import annotation.Controller;

@Controller 
public class ModelAndView {
    private String view;
    private Map<String, Object> hashmap = new HashMap<>();

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
