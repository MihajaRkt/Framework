package donnees;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class Test {

    @UrlMapping("/test")
    public void test() {
    }

    @UrlMapping("/tester")
    public void tester() {
    }
}