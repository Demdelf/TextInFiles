package sample;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SearchFileVisitor extends SimpleFileVisitor<Path> {
    private String partOfName = ".";
    private String partOfContent;
    private int minSize;
    private int maxSize;
    private List<Path> foundFiles = new ArrayList<>();
    //private boolean[] isChecked = new boolean[4];

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        byte[] content = new byte[0];
        String strContent = null;
        boolean isFounded = true;
        if (partOfName != null){
            isFounded = file.getFileName().toString().contains(partOfName);
            if (isFounded){
                content = Files.readAllBytes(file);
                strContent = new String(content);
            }

        }

        if (partOfContent != null && isFounded)
            isFounded = strContent.contains(partOfContent);
        //if (maxSize != 0 && minSize != 0 && isFounded)
        // isFounded = (long) content.length < maxSize && (long) content.length > minSize;
        if (maxSize > 0 && isFounded)
            isFounded = (long) content.length < maxSize;
        if (minSize > 0 && isFounded)
            isFounded = (long) content.length > minSize;
        if (isFounded)
            foundFiles.add(file);
        return super.visitFile(file, attrs);
    }

    public void setPartOfName(String partOfName) {
        this.partOfName = partOfName;
        //if (partOfName != null) isChecked[0] = true;
    }

    public void setPartOfContent(String partOfContent) {
        this.partOfContent = partOfContent;
        //if (partOfContent != null) isChecked[1] = true;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
        //if (partOfName != null) isChecked[2] = true;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        //if (partOfName != null) isChecked[3] = true;
    }

    public List<Path> getFoundFiles() {
        return foundFiles;
    }
}
