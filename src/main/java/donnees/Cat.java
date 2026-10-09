package donnees;

import annotation.Controller;
import annotation.Url;

@Controller
public class Cat {
    @Url(value = "/cat", methode = "GET")
    public void cat() {
        System.out.println("Le chat miaou");
    }

}
