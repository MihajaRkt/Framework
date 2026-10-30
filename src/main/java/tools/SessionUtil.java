package tools;

import jakarta.servlet.http.HttpSession;
import objet.MySession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SessionUtil {

    public static void copyToMySession(HttpSession httpSession, MySession mySession) {
        Map<String, Object> map = mySession.getSessionData();

        if (httpSession != null && mySession != null) {
            Enumeration<String> attributeNames = httpSession.getAttributeNames();

            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = httpSession.getAttribute(key);

                map.put(key, value);
            }
        }
    }

    public static void copyToHttpSession(MySession mySession, HttpSession httpSession) {
        if (httpSession != null && mySession != null) {
            for (Map.Entry<String, Object> entry : mySession.getSessionData().entrySet()) {
                httpSession.setAttribute(entry.getKey(), entry.getValue());
            }
        } else{
            System.out.println("HttpSession : " +httpSession);
            System.out.println("MySession : " +mySession);
        }
    }

    public static void testerSession(HttpSession httpSession) {
        // System.out.println("=== 🧪 DÉBUT DU TEST DE SESSION ===");

        MySession mySession = new MySession();
        Map<String, Object> map= new HashMap<>();
        mySession.setSessionData(map);

        map.put("user", "Paul");

        copyToHttpSession(mySession, httpSession);

        // System.out.println("Données dans HttpSession (Tomcat) après mise à jour :");
        // System.out.println(" - role : " + httpSession.getAttribute("role"));
        // System.out.println(" - User : " + httpSession.getAttribute("user"));

        // System.out.println("=== 🏁 FIN DU TEST DE SESSION ===");
    }

}