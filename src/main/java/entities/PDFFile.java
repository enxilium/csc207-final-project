package entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PDFFile {
    private final Path path;
    private List<Path> paths = new ArrayList<>();

    public PDFFile(String path) {
        this.path = Paths.get(path);
    }

    public Path getPath() {
        return path;
    }

    public void storePath(String path) {
        this.paths.add(Paths.get(path));
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }
}
