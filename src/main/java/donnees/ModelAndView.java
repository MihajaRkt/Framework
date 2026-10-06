package donnees;

import java.util.HashMap;
import java.util.Map;

import annotation.Controller;
import annotation.Url;
import annotation.WebAPI;

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

    @WebAPI 
    @Url(value = "/mav")
    public static ModelAndView mavTest(){
        Map<String, Object> page= new HashMap<>();
        ModelAndView mav= new ModelAndView();

        mav.setView("APITest");
        page.put("test", "test");
        page.put("testest", "test2");
        mav.setHashmap(page);

        return mav;
        
    }

    @WebAPI 
    @Url(value = "/form")
    public ModelAndView afficherForm() {
        ModelAndView form= new ModelAndView();
        form.setView("form");

        return form;
    }
}
