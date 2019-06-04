import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    private Socket socket;
    private ChatLineWriter writer;
    private ChatLineReader reader;
    private User user;
    private String username;
    private ChatsMenu chatsMenu;
    private List<Chat> showedChats = new ArrayList<>();

    Client(String username, int port) throws IOException {
        this.username = username;
        this.socket = new Socket("localhost", port);
        this.writer = new ChatLineWriter(socket.getOutputStream());
    }


    void runn() {
        new Thread(this::run).start();
        chatsMenu = new ChatsMenu(this);
        chatsMenu.run();
    }

    private void run() {
        try {
            writer.start();
            reader = new ChatLineReader(socket.getInputStream()) {
                @Override
                public void run() {
                    while (scanner.hasNextLine()) {
                        System.out.println("running updates...");
                        String json = scanner.nextLine();
                        Chat.updateFrom(json);
                        User.updateFrom(json, false);
                    }
                }
            };
            reader.start();

            Thread.sleep(500);

            user = User.getOrMake(username);
            User.updateTo(writer);

        } catch (Exception e) {
            View.printError(e);
        }
    }

    User getUser() {
        return user;
    }

    void updateChatsForServer() {
        Chat.updateTo(writer);
    }

    boolean createGroup(String text, List<String> usernames) {
        if (text == null || usernames == null || usernames.size() < 2)
            return false;
        List<User> members = new ArrayList<>();
        for (String name : usernames) {
            User user = User.getUserByName(name);
            if (user == null)
                return false;
            members.add(user);
        }
        new Chat(text, user, members);
        Chat.updateTo(writer);
        return true;
    }

    boolean addMember(String group, List<String> usernames) {
        if (group == null || usernames == null || usernames.isEmpty())
            return false;
        Chat chat = Chat.getChatByName(group);
        if (chat == null || chat.getMaker().getId() != user.getId())
            return false;
        List<User> users = new ArrayList<>();
        for (String name : usernames) {
            User user = User.getUserByName(name);
            if (user == null)
                return false;
            if (chat.hasThis(user))
                return false;
            users.add(user);
        }
        chat.addUser(users);
        Chat.updateTo(writer);
        return true;
    }

    void addShowedChat(Chat chat) {
        if (chat != null)
            showedChats.add(chat);
    }

    boolean isShowed(Chat chat) {
        for (Chat c : showedChats)
            if (c.getId() == chat.getId())
                return true;
        return false;
    }

}
