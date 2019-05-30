import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server {
    private List<SocketPack> packs = new ArrayList<>();
    private ServerSocket serverSocket;
    private static final int PORT = 2048;

    private Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(PORT);
        server.run();
    }

    private void run() throws IOException {
        User.initializeUsers();
        Chat.initializeChats();

        while (!serverSocket.isClosed()) {
            System.out.println("Waiting for the " + (packs.size() + 1) + "th client...");
            Socket socket = serverSocket.accept();

            ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
            chatLineWriter.start();

            Chat.updateTo(chatLineWriter);
            User.updateTo(chatLineWriter);

            ChatLineReader chatLineReader = new ChatLineReader(socket.getInputStream()) {
                @Override
                public void run() {
                    while (scanner.hasNextLine()) {
                        String json = scanner.nextLine();
                        Chat.updateFrom(json);
                        User.updateFrom(json, true);
                        Chat.saveAll();
                        User.saveAll();
                        refreshAll();
                        System.out.println(Chat.getChats().size() + " chats");
                        Chat.showChats();
                        System.out.println(User.getUsers().size() + " users");
                        User.showUsers();
                    }
                }
            };
            chatLineReader.start();

            SocketPack pack = new SocketPack(socket, chatLineWriter, chatLineReader);
            packs.add(pack);
        }
    }

    private void refreshAll() {
        for (SocketPack s : packs) {
            ChatLineWriter writer = s.getChatLineWriter();
            Chat.updateTo(writer);
            User.updateTo(writer);
        }
    }

    static int getPORT() {
        return PORT;
    }
}
