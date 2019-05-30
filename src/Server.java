import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server {
    private List<Socket> sockets = new ArrayList<>();
    private List<ChatLineWriter> writers = new ArrayList<>();
    private ServerSocket serverSocket;

    Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    void run() throws IOException {
        while (!serverSocket.isClosed()) {
            System.out.println("Waiting for the " + (sockets.size() + 1) + "th client...");
            Socket socket = serverSocket.accept();
            sockets.add(socket);

            ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
            chatLineWriter.start();
            writers.add(chatLineWriter);

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
        }
    }

    private void refreshAll() {
        for (ChatLineWriter writer : writers) {
            Chat.updateTo(writer);
            User.updateTo(writer);
        }
    }
}
