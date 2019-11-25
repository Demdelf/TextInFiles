package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    public Text currentType;
    public TextField typeForSearch;
    List<Path> fileList;
    public TextField textForSearch;
    public TextField pathForSearch;
    public Text currentPath;
    public TreeView tree;
    String path = "C:/logs";
    private String fileType = ".log";
    String searchedText;
    @FXML
    public TextArea text;
    List<Path> filteredFiles = new ArrayList<>();


    public List<Path> getFilteredFiles() {
        return filteredFiles;
    }

    public String getSearchedText() {
        return searchedText;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        currentPath.setText(path);
    }

    public String getFileType() {
        return fileType;
    }
    public void setFileType(String type){
        this.fileType = type;
        currentType.setText(type);
    }

    @FXML
    public void setText(String s){
        text.setText(s);
    }

    @FXML
    private void buttonClicked()
    {
        tree.setRoot(null);
        setTextForSearch();
    }

    public void setSearchedText(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            tree.setRoot(null);
            setTextForSearch();
        }

    }

    public void setSearchedPath(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){

            setPathForSearch();
        }
    }

    public void setSearchedType(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){

            setTypeForSearch();
        }
    }

    private void setPathForSearch() {
        setPath(pathForSearch.getText());
    }

    private void setTypeForSearch() {
        setFileType(typeForSearch.getText());
    }

    public void setTextForSearch(){
        //tree.addEventHandler();
        this.searchedText = textForSearch.getText();
        filteredFiles = new ArrayList<>();
        text.setText("");
        fileList = getFileList();
    }

    public void showText(Path file){
        text.setText(getStringFromFile(fileList, file));
    }

    private void getFilteredFiles(File dir){
        File[] files = null;
        List<Path> list = new ArrayList<>();
        try{

                    Files.newDirectoryStream(Paths.get(dir.getCanonicalPath()),
                            path -> path.toString().endsWith(getFileType())||path.toFile().isDirectory()).forEach(p -> list.add(p));
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
                    if (searchText(file, getSearchedText())) {
                        filteredFiles.add(Paths.get(file.getCanonicalPath()));
                    }
                } catch (IOException e) {
                    System.out.println("Error 1");
                }
            } else {
                getFilteredFiles(file);

                /*if (jCheckBox4.isSelected()) {
                    findFiles(file);
                }*/
            }
        }
    }

    private boolean searchText(File file, String string) throws IOException {
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

    private List<Path> getFileList(){
        tree.setRoot(null);
        Path base = Paths.get(getPath());
        SimpleFileTreeItem root = new SimpleFileTreeItem(base);
        tree.setRoot(root);

        getFilteredFiles(base.toFile());
        if(getFilteredFiles().size() == 0) {
            System.out.println("Ничего не найдено");
            tree.setRoot(new SimpleFileTreeItem(Paths.get("Didn't find anything")));
            return null;
        }
        List<Path> foundFiles = getFilteredFiles();


        if(foundFiles.size() > 1){
            foundFiles.sort((o1, o2) -> o1.toString().length() - o2.toString().length());
        }

        for (Path file : foundFiles) {
            List<Path> list = new ArrayList<>();
            System.out.println(file);
            while (!file.equals(Paths.get(getPath()))){

                list.add(file);
                Path parent = file.getParent();
                file = parent;
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

    private String getStringFromFile(List<Path> foundFiles, Path chosenFile){
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

    private static String readFile(File file){
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

    public void setSearchedFile(MouseEvent mouseEvent) {
        //костыль для получения пути файла из TreeView
        String item = mouseEvent.getPickResult().getIntersectedNode().toString().split("\"")[1];
        System.out.println(item);
        showText(Paths.get(item));
    }
}
