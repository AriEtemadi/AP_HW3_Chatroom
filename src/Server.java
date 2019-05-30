import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server {
    private List<SocketPack> socketPacks = new ArrayList<>();
    private ServerSocket serverSocket;

    private Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(2048);
        server.run();
    }

    private void run() throws IOException {
        while (!serverSocket.isClosed()) {
            System.out.println("Waiting for the " + (socketPacks.size() + 1) + "th client...");
            Socket socket = serverSocket.accept();

            ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
            chatLineWriter.start();

            Chat.updateTo(chatLineWriter);
            User.updateTo(chatLineWriter);

            ChatLineReader chatLineReader = new ChatLineReader(socket.getInputStream()) {
                @Override
                public void run() {
//                    System.out.println("server run start");
                    while (scanner.hasNextLine()) {
//                        System.out.println("server run update...");
                        String json = scanner.nextLine();
                        Chat.updateFrom(json);
                        User.updateFrom(json, true);
                        refreshAll();
                        System.out.println(Chat.getChats().size() + " chats");
                        Chat.showChats();
                        System.out.println(User.getUsers().size() + " users");
                        User.showUsers();
                    }
//                    System.out.println("server run end.");
                }
            };
            chatLineReader.start();

            SocketPack socketPack = new SocketPack(socket, chatLineWriter, chatLineReader, null);
            socketPacks.add(socketPack);
        }
    }

    private void refreshAll() {
        for (SocketPack s : socketPacks) {
            ChatLineWriter writer = s.getChatLineWriter();
            Chat.updateTo(writer);
            User.updateTo(writer);
        }
    }
}
