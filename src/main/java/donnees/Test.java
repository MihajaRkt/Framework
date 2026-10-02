package donnees;

import annotation.Controller;
import annotation.Url;
import annotation.WebAPI;
import objet.Model;

@Controller
public class Test {

    public Test() {
    }

    @Url(value = "/test", methode = "POST")
    public void testPOST() {
        System.out.println("Test méthode POST");
    }

    @Url(value = "/testGET")
    public void testGET() {
        System.out.println("Test méthode GET TEST");
    }

    @WebAPI
    @Url(value = "/testAPI")
    public Model nouveauTest(){
        return new Model(1, "test");
    }

    @WebAPI 
    @Url(value= "/robot")
    public String robot(){
        return "Test Robot";
    }

    @WebAPI 
    @Url(value= "/nombre")
    public Integer nombre(){
        return 145;
    }

    @Url(value= "/envoi", methode="POST")
    public void form(String nom, String age){
        System.out.println("Nom: " +nom+ ", Age: " +age);
    }   

}