package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Controller {
    public Text currentType;
    public TextField typeForSearch;
    public Text searchTime;
    public Text numberOfFoundedFiles;
    public Text currentFile;
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
    Model model = new Model(this);
    private Date timeStart;


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

    //запуск поиска
    public void setTextForSearch(){
        timeStart = new Date();
        searchTime.setText("Search time: ");
        numberOfFoundedFiles.setText("Files found: " + 0);
        model.setqF(0);
        this.searchedText = textForSearch.getText();
        filteredFiles = new ArrayList<>();
        setText("");
        fileList = model.getFileList(this);
        long timeDiff = new Date().getTime() - timeStart.getTime();
        searchTime.setText("Search time: " + String.valueOf(timeDiff) + " ms");
    }

    public void showText(Path file){
        setText(model.getStringFromFile(fileList, file));
    }

    public void setSearchedFile(MouseEvent mouseEvent) {
        //костыль для получения пути файла из TreeView
        String item = mouseEvent.getPickResult().getIntersectedNode().toString().split("\"")[1];
        System.out.println(item);
        showText(Paths.get(item));
    }
}
