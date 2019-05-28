import com.gilecode.yagson.YaGson;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ChatRoom");
        View.getInstance().setPrimaryStage(primaryStage);
        View.getInstance().start();
    }

    Client(String username, int port) throws IOException {
        User user = new User(username);
        Socket socket = new Socket("localhost", port);
        ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
        ChatLineReader chatLineReader = new ChatLineReader(socket.getInputStream());
        this.pack = new SocketPack(socket, chatLineWriter, chatLineReader, user);
    }

    private ChatsMenu chatsMenu;
    private SocketPack pack;

    void run() throws Exception {
        chatsMenu = new ChatsMenu(this);

        pack.getChatLineWriter().start();

        ChatLineReader chatLineReader = new ChatLineReader(pack.getSocket().getInputStream()) {
            @Override
            public void run() {
//                System.out.println("start running");
                while (scanner.hasNextLine()) {
                    System.out.println("run updates...");
                    String json = scanner.nextLine();
                    Chat.updateFrom(json);
                    User.updateFrom(json, false);
                }
//                System.out.println("end running");
            }
        };
        pack.setChatLineReader(chatLineReader);
        chatLineReader.start();

        Thread.sleep(300);
        pack.getUser().resetID();
        User.updateTo(pack.getChatLineWriter());

        while (!pack.getSocket().isClosed()) {
            System.out.println("Which chat do you want? Enter the id.");

            Chat.showChats();
            User.showUsers();

            Scanner scanner = new Scanner(System.in);
            String chatName = scanner.nextLine();
            Chat chat = pack.getUser().getChatByID(Integer.parseInt(chatName));
            if (chat == null) {
                System.out.println("invalid chat.");
                continue;
            }
            while (true) {
                chat = pack.getUser().getChatByID(Integer.parseInt(chatName));
                if (chat == null) {
                    System.err.println("null");
                    break;
                }
                chat.show();
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("back"))
                    break;
                if (input.equalsIgnoreCase("show"))
                    continue;
                chat.addMessage(input);
                YaGson yaGson = new YaGson();
                pack.getChatLineWriter().writeLine(yaGson.toJson(chat));
            }
        }
    }
}
