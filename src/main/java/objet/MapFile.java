package objet;

import java.nio.charset.StandardCharsets;

public class MapFile {
    private String fileName;
    private String extension;
    private byte[] fichier;

    public MapFile(String fileName, String extension, byte[] fichier) {
        this.fileName = fileName;
        this.extension = extension;
        this.fichier = fichier;
    }
    
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public byte[] getFichier() {
        return fichier;
    }
    public void setFichier(byte[] fichier) {
        this.fichier = fichier;
    }

    public static MapFile parsMapFile(String param){
        String[] parts= param.split("\\.");
        String nomFichier= parts[0];
        String ext= parts[1];
        byte[] byteArray= param.getBytes(StandardCharsets.UTF_8);
        
        return new MapFile(nomFichier, ext, byteArray);
    }
}
