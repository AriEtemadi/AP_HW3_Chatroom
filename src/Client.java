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

    // this is necessary:
    public Client() {
    }

    static {
        prepareForTest();
    }

    Client(String username, int port) throws IOException {
        User user = User.getOrMake(username);
        Socket socket = new Socket("localhost", port);
        ChatLineWriter chatLineWriter = new ChatLineWriter(socket.getOutputStream());
        ChatLineReader chatLineReader = new ChatLineReader(socket.getInputStream());
        this.pack = new SocketPack(socket, chatLineWriter, chatLineReader, user);
    }

    private ChatsMenu chatsMenu;
    private SocketPack pack;

    void runn() {
        new Thread(this::run).start();
        chatsMenu = new ChatsMenu(this);
        chatsMenu.run();
    }

    void run() {
        try {

            getChatLineWriter().start();

            ChatLineReader chatLineReader = new ChatLineReader(getSocket().getInputStream()) {
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
            getUser().resetID();
            User.updateTo(getChatLineWriter());

            while (!pack.getSocket().isClosed()) {
                System.out.println("Which chat do you want? Enter the id.");

                Chat.showChats();
                User.showUsers();

                Scanner scanner = new Scanner(System.in);
                String chatName = scanner.nextLine();
                Chat chat = getUser().getChatByID(Integer.parseInt(chatName));
                if (chat == null) {
                    System.out.println("invalid chat.");
                    continue;
                }
                while (true) {
                    chat = getUser().getChatByID(Integer.parseInt(chatName));
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
                    getChatLineWriter().writeLine(yaGson.toJson(chat));
                }
            }
        } catch (Exception e) {
            View.printError(e);
        }
    }

    User getUser() {
        return pack.getUser();
    }

    private ChatLineReader getChatLineReader() {
        return pack.getChatLineReader();
    }

    private ChatLineWriter getChatLineWriter() {
        return pack.getChatLineWriter();
    }

    private Socket getSocket() {
        return pack.getSocket();
    }

    private static void prepareForTest() {
        User a = new User("a");
        User b = new User("b");
        User c = new User("c");

        Chat c1 = new Chat();
        c1.addUser(a);
        c1.addUser(b);
        c1.addMessage("a: hi b!");
        c1.addMessage("b: fuck you a;");
        Chat.addChat(c1);

        Chat c2 = new Chat();
        c2.addUser(a);
        c2.addUser(c);
        c2.addMessage("A chat between a and c");
        Chat.addChat(c2);
    }
}
