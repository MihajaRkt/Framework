package tools;

import java.util.Objects;

public class URLDetails {

    private String url;
    private String methode;

    public URLDetails(String url, String methode) {
        this.url = url;
        this.methode = methode;
    }

    public String getUrl() {
        return url;
    }

    public String getMethode() {
        return methode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        URLDetails other = (URLDetails) obj;
        return Objects.equals(url, other.url) && Objects.equals(methode, other.methode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, methode);
    }
}