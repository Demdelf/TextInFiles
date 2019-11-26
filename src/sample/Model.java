package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {

    private final Controller controller;

    public Model(Controller controller) {
        this.controller = controller;
    }

    public static String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append("\n" + text);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return stringBuffer.toString();
    }

    boolean searchText(File file, String string) throws IOException {
        Path path = Paths.get(file.getCanonicalPath());
        System.out.println("Searching " + string + " in " + path.toString());
        boolean result = false;
        try {
            result = Files.
                    lines(path, StandardCharsets.UTF_8).
                    anyMatch((s) -> s.contains(string));
        }catch (Exception e){
            System.out.println("Bad format");
        }

        System.out.println(result);
        return result;
    }

    public String getStringFromFile(List<Path> foundFiles, Path chosenFile){
        String s = "";
        File file = null;
        for (Path p: foundFiles
        ) {
            if(p.equals(chosenFile)){
                file = p.toFile();
            }
        }
        if(file.length() > 0){
            s = readFile(file);
            System.out.println(s);
        }
        return s;
    }
}
