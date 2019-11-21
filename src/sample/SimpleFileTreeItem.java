package sample;

import javafx.scene.control.TreeItem;

import java.nio.file.Path;

public class SimpleFileTreeItem extends TreeItem<Path> {
    public TreeItem<Path> getItem() {
        return item;
    }

    TreeItem<Path> item;
    public SimpleFileTreeItem(Path f) {
        super(f);
    }

    public boolean containItem(Path path){
        boolean flag = false;
        for (TreeItem<Path> t: super.getChildren()
             ) {
            if (t.getValue().equals(path)){
                flag = true;
                item = t;
                break;
            }
        }
        return flag;
    }

}
