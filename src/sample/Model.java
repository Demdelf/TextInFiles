package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {

    private final Controller controller;
    private int qF = 0;


    public Model(Controller controller) {
        this.controller = controller;
    }


    //Возвращаем текс из файла
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

    //Проверяем содержит ли файл искомый текст
    boolean searchText(File file, String string) throws IOException {
        Path path = Paths.get(file.getCanonicalPath());
        controller.currentFile.setText("Current file:" + path.toString());
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

    //Возвращаем текс из файла (по пути из списка отобранных файлов)
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

    //Возвращаем список файлов искомого типа и содержащие искомый текст
    public List<Path> getFileList(Controller controller){
        controller.tree.setRoot(null);
        Path base = Paths.get(controller.getPath());
        SimpleFileTreeItem root = new SimpleFileTreeItem(base);
        controller.tree.setRoot(root);

        controller.model.getFilteredFiles(base.toFile(), controller);
        if(controller.getFilteredFiles().size() == 0) {
            System.out.println("Ничего не найдено");
            controller.tree.setRoot(new SimpleFileTreeItem(Paths.get("Didn't find anything")));
            return null;
        }
        List<Path> foundFiles = controller.getFilteredFiles();

        if(foundFiles.size() > 1){
            foundFiles.sort((o1, o2) -> o1.toString().length() - o2.toString().length());
        }

        for (Path file : foundFiles) {
            List<Path> list = new ArrayList<>();
            System.out.println(file);
            while (!file.equals(Paths.get(controller.getPath()))){
                list.add(file);
                file = file.getParent();
            }

            list.sort(new Comparator<Path>() {
                @Override
                public int compare(Path o1, Path o2) {
                    return o1.toString().length() - o2.toString().length();
                }
            });

            System.out.println(list);

            SimpleFileTreeItem temp = root;

            for (Path path1 : list) {

                if (temp.containItem(path1)){
                    temp = (SimpleFileTreeItem) temp.getItem();
                }else {
                    SimpleFileTreeItem fresh = new SimpleFileTreeItem(path1);
                    temp.getChildren().add(fresh);
                    temp = fresh;
                }
            }
        }
        return foundFiles;
    }

    //Заполняем список файлов искомого типа
    public void getFilteredFiles(File dir, Controller controller){
        List<Path> list = new ArrayList<>();
        try{

                    Files.newDirectoryStream(Paths.get(dir.getCanonicalPath()),
                            path -> path.toString().endsWith(controller.getFileType())||path.toFile().isDirectory()).forEach(p -> list.add(p));
        }catch (IOException e) {
            e.printStackTrace();
        }
        for (Path path1 : list) {
            System.out.println("**********************");
            System.out.println(path1);
            System.out.println("**********************");
        }

        for ( Path p : list) {
            File file = p.toFile();
            if  (Thread.currentThread().isInterrupted()) {
                return;
            }
            if (!file.isDirectory()) {
                try {
                    if (searchText(file, controller.getSearchedText())) {
                        controller.filteredFiles.add(Paths.get(file.getCanonicalPath()));
                        qF++;
                        controller.numberOfFoundedFiles.setText("Files found: " + qF);

                    }
                } catch (IOException e) {
                    System.out.println("Error 1");
                }
            } else {
                getFilteredFiles(file, controller);

                /*if (jCheckBox4.isSelected()) {
                    findFiles(file);
                }*/
            }
        }
    }
}
