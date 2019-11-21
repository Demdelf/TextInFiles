package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.*;
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

    private String getFileType() {
        return fileType;
    }
    public void setFileType(String type){
        this.fileType = type;
        currentType.setText("*" + type);
    }



    //setPath("C:/logs");
    String searchedText;
    //= "qwerty";

    @FXML
    public TextArea text;

    @FXML
    public void setText(String s){
        text.setText(s);
    }

    @FXML
    private void buttonClicked() {
        setTextForSearch();
    }

    public void setSearchedText(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){

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
        fileList = getFileList();
    }

    public void showText(Path file){
        text.setText(getStringFromFile(fileList, file));
    }

    private List<Path> getFileList(){
        SimpleFileTreeItem root = new SimpleFileTreeItem(Paths.get(getPath()));
        tree.setRoot(root);
        SearchFileVisitor searchFileVisitor = new SearchFileVisitor();
        searchFileVisitor.setPartOfContent(getSearchedText());
        searchFileVisitor.setPartOfName(getFileType());
        try {
            Files.walkFileTree(Paths.get(getPath()), searchFileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Path> foundFiles = searchFileVisitor.getFoundFiles();
        if(foundFiles.size() == 0) {
            System.out.println("Ничего не найдено");
            return null;
        }
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
                stringBuffer.append(text);
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
