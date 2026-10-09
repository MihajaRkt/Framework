package donnees;

import java.util.HashMap;
import java.util.Map;

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

    public static ModelAndView mavTest(){
        Map<String, Object> page= new HashMap<>();
        ModelAndView mav= new ModelAndView();

        mav.setView("index");
        page.put("test", "test1");
        page.put("testest", "test2");
        mav.setHashmap(page);

        return mav;
        
    }

}
