import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
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
                    hendlerRead()
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
        System.out.println(command);
        if (command.equals("--help")) {
            channel.write(ByteBuffer.wrap("input ls for show file list".getBytes()));
        }
        if (command.equals("ls")) {
            channel.write(ByteBuffer.wrap(getFilesList().getBytes()));
        }

    }

    private String getFilesList() {
        return String.join(" ", new File(String.valueOf(rootPath)).list());
    }


    private void hendlerAccetion(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
        channel.configureBlocking(false);
        System.out.println("Клиент подконнектился");
        channel.register(selector, SelectionKey.OP_READ);

    }
}
