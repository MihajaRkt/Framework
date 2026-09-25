package donnees;

import annotation.Controller;
import annotation.Url;
import annotation.WebAPI;

@Controller
public class Test {

    public Test() {
    }

    @Url(value = "/test", methode = "POST")
    public void testPOST() {
        System.out.println("Test méthode POST");
    }

    @Url(value = "/APITest")
    public void testGET() {
        System.out.println("Test méthode GET TEST");
    }

    @WebAPI
    @Url(value = "/testAPI")
    public void nouveauTest(){
        System.out.println("Test API");
    }

}