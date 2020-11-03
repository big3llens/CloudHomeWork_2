import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NIO_Server {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Path rootPath = Path.of("Server");
    Path currentPath = rootPath;

    public static void main(String[] args) {
        try {
            new NIO_Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NIO_Server() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(9000));
        server.configureBlocking(false);
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(server.getLocalAddress());
        System.out.println("Сервер запущен");
        Iterator iterator = selector.selectedKeys().iterator();
        while (server.isOpen()){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator it = selectionKeys.iterator();
            while (it.hasNext()){
                SelectionKey key = (SelectionKey) it.next();
                if(key.isAcceptable()){
                    hendlerAccetion (key, selector);
                }
                if(key.isReadable()){
                    hendlerRead(key, selector);
                }
            }
            it.remove();
        }
    }

    private void hendlerRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int read = channel.read(buffer);
        if (read == -1) {
            channel.close();
            return;
        }
        if (read == 0) {
            return;
        }
        buffer.flip();
        byte[] buf = new byte[read];
        int pos = 0;
        while (buffer.hasRemaining()) {
            buf[pos++] = buffer.get();
        }
        buffer.clear();
        String command = new String(buf, StandardCharsets.UTF_8)
                .replace("\n", "")
                .replace("\r", "");
//        System.out.println(command);
        String[] arrCommand = command.split(" ");
        if (arrCommand[0].equals("--help")) {
            channel.write(ByteBuffer.wrap("input ls for show file list".getBytes()));
        }
        if (arrCommand[0].equals("ls")) {
            channel.write(ByteBuffer.wrap(getFilesList().getBytes()));
        }
        if (arrCommand[0].equals("cd")) {
            channel.write(ByteBuffer.wrap(changeDirectory(arrCommand[1]).getBytes()));
        }
        if (arrCommand[0].equals("touch")) {
            channel.write(ByteBuffer.wrap(touch(arrCommand[1]).getBytes()));
        }
        if (arrCommand[0].equals("mkdir")) {
            channel.write(ByteBuffer.wrap(makeDirectory(arrCommand[1]).getBytes()));
        }
        if (arrCommand[0].equals("rm")) {
            channel.write(ByteBuffer.wrap(remove(arrCommand[1]).getBytes()));
        }
        if (arrCommand[0].equals("copy")) {
            channel.write(ByteBuffer.wrap(copy(arrCommand[1], arrCommand[2]).getBytes()));
        }
        if (arrCommand[0].equals("cat")) {
            channel.write(ByteBuffer.wrap(cat(arrCommand[1]).getBytes()));
        }
    }

    private String getFilesList() {
        return String.join(" ", new File(rootPath.toString()).list());
    }

    public String changeDirectory(String dir) throws IOException {
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

    public String touch(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString(), name))) {
            Files.createFile(Path.of(currentPath.toString(), name));
            return "Файл успешно создан";
        } else return "Такой файл уже существует";
    }

    public String makeDirectory(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString() + "/" + name))) {
            Files.createDirectory(Path.of(currentPath.toString() + "/" + name));
            return "Директория успешно создан";
        } else return "Такая директория уже существует";
    }

    public String remove(String name) throws IOException {
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

    public String copy (String src, String target) throws IOException {
        Files.copy(Path.of(currentPath.toString(), src), Path.of(target), StandardCopyOption.REPLACE_EXISTING);
        return "Файл успешно скопирован";
    }

    public String cat(String name) throws IOException {
        if (Files.exists(Path.of(currentPath.toString(), name))) {
            if (Files.readString(Path.of(currentPath.toString(), name)) == null) return "Файл пустой";
            return Files.readString(Path.of(currentPath.toString(), name));
        }
        return "В данной директории нет такого файла";
    }


    private void hendlerAccetion(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
        channel.configureBlocking(false);
        System.out.println("Клиент подконнектился");
        channel.register(selector, SelectionKey.OP_READ);

    }
}
