package objet;

import java.util.HashMap;
import java.util.Map;

public class MySession {
    private Map<String, Object> sessionData;

    // Constructeur : initialise la Map si elle est nulle
    public MySession() {
        this.sessionData = new HashMap<>();
    }

    public MySession(Map<String, Object> sessionData) {
        this.sessionData = (sessionData != null) ? sessionData : new HashMap<>();
    }

    // Getters et Setters existants
    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    public void setSessionData(Map<String, Object> sessionData) {
        this.sessionData = sessionData;
    }

    public Object get(String key) {
        if (this.sessionData != null) {
            return this.sessionData.get(key);
        }
        return null;
    }

    public void add(String key, Object value) {
        if (this.sessionData == null) {
            this.sessionData = new HashMap<>();
        }
        this.sessionData.put(key, value);
    }

    public void delete(String key) {
        if (this.sessionData != null) {
            this.sessionData.remove(key);
        }
    }

    public void destroy() {
        if (this.sessionData != null) {
            this.sessionData.clear();
        }
    }
}