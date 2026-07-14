package donnees;

import annotation.Controller;
import annotation.Url;

@Controller
public class Test {

    public Test() {
    }

    @Url(value = "/test", methode = "POST")
    public void testPOST() {
        System.out.println("Test méthode POST");
    }

    @Url(value = "/test")
    public void testGET() {
        System.out.println("Test méthode GET TEST");
    }

}