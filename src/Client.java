import com.gilecode.yagson.YaGson;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter server port: ");
        int port = Integer.parseInt(scanner.nextLine());
        System.out.println("enter your name: ");
        String username = scanner.nextLine();
        Client client = new Client(username, port);
        client.run();
    }

    private User user;
    private Socket socket;

    private Client(String username, int port) throws IOException {
        this.user = new User(username);
        this.socket = new Socket("localhost", port);
    }

    private void run() throws IOException {
        ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
        chatLineWriter.start();

        ChatLineReader chatLineReader = new ChatLineReader(socket.getInputStream()) {
            @Override
            public void run() {
                System.out.println("start running");
                while (scanner.hasNextLine()) {
                    System.out.println("run updates...");
                    String json = scanner.nextLine();
                    Chat.updateFrom(json);
                    User.updateFrom(json, false);
                }
                System.out.println("end running");
            }
        };
        chatLineReader.start();

        User.updateTo(chatLineWriter);

        while (!socket.isClosed()) {
            System.out.println("Which chat do you want? Enter the id.");

            Chat.showChats();
            User.showUsers();

            Scanner scanner = new Scanner(System.in);
            String chatName = scanner.nextLine();
            Chat chat = user.getChatByID(Integer.parseInt(chatName));
            if (chat == null) {
                System.out.println("invalid chat.");
                continue;
            }
            while (true) {
                showChat(chat);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("back"))
                    break;
                chat.addMessage(input);
                YaGson yaGson = new YaGson();
                chatLineWriter.writeLine(yaGson.toJson(chat));
            }
        }
    }

    private void showChat(Chat chat) {
        System.out.println("chat " + chat.name + ": ");
        System.out.println("blah blah blah");
    }
}
