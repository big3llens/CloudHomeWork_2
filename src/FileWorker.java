import com.sun.source.tree.IfTree;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FileWorker {
    static Path dir3 = Path.of("D:\\Java\\JavaCloud\\NIO_Chat\\Server\\d1\\d2");
    static Path rootPath = Path.of("Server");
    static Path currentPath = rootPath;

    public static void main(String[] args) throws IOException {

        try {
            System.out.println(changeDirectory("Server/d1/d2/2.txt"));
//            touch("444");
//            makedirectory("d2");
//            remove("444.txt");
//            cat("2.txt");
            cope("2.txt", "Server/d1/1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String arrDir = Files.readString(dir3, Charset.defaultCharset());
//        System.out.println(arrDir);

//        System.out.println(dir3.toAbsolutePath());
//        System.out.println(dir3.getParent());
//        System.out.println(dir3.getRoot());
//        System.out.println(dir3.getFileName());
//        Path d = Path.of("d3");

//        System.out.println(d.toAbsolutePath());
//        System.out.println(d.getParent());
//        System.out.println(d.getRoot());
//        System.out.println(d.getFileName());

//        Path f = Path.of("d1/d2");
//        System.out.println(String.join(" ", f.toFile().list()));
//        System.out.println(Files.exists(f));


//        Iterator it = dir3.iterator();
//        while (it.hasNext()){
//            System.out.println(it);
//            it.next();
//        }


//        System.out.println("__________________________________________");
//
//        Path dir2 = Path.of("/d1/d2", "d3");
//        System.out.println(dir2.toAbsolutePath());
//        System.out.println(dir2.getParent());
//        System.out.println(dir2.getRoot());
//        System.out.println(dir2.getFileName());


    }

    public static String changeDirectory(String dir) throws IOException {
        Path newDir = Path.of(dir);
        if (!Files.exists(newDir)) {
            System.out.println("Такой директории не существует");
            return rootPath.toString();
        }
        StringBuilder sb = new StringBuilder();
        if (!Files.isDirectory(newDir)) {
            for (int i = 0; i < newDir.getNameCount() - 1; i++) {
                sb.append(newDir.getName(i) + "/");
            }
            currentPath = Path.of(sb.toString());
            return sb.toString();
        }
        currentPath = Path.of(newDir.toString());
        return newDir.toString();
    }

    public static void touch(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString(), name + ".txt"))) {
            Files.createFile(Path.of(currentPath.toString(), name + ".txt"));
        } else System.out.println("Такой файл уже существует");
    }

    public static void makedirectory(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString() + "/" + name))) {
            Files.createDirectory(Path.of(currentPath.toString() + "/" + name));
        } else System.out.println("Такая директория уже существует");
    }

    public static String remove(String name) throws IOException {
        List<String> filesList = Arrays.asList(currentPath.toFile().list());
        if (filesList == null) {
            return "В данной директории нет файлов";
        }
        for (String file : filesList) {
            if (file.equals(name)) {
                Files.delete(Path.of(currentPath.toString(), file));
                return String.format("Файл %s успешно удален", name);
            }
        }
        return "В данной директории нет такого файла";
    }

    public static void cope(String src, String target) throws IOException {
        Files.copy(Path.of(currentPath.toString(), src), Path.of(target), StandardCopyOption.REPLACE_EXISTING);
    }

    public static String cat(String name) throws IOException {
        if (Files.exists(Path.of(currentPath.toString(), name))) {
            if (Files.readString(Path.of(currentPath.toString(), name)) == null) return "Файл пустой";
            return Files.readString(Path.of(currentPath.toString(), name));
        }
        return "В данной директории нет такого файла";
    }
}
