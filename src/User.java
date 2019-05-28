import com.gilecode.yagson.YaGson;
import com.gilecode.yagson.YaGsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class User {
    private static List<User> users = new ArrayList<>();
    private static int idCount;
    String username;
    int id;

    static {
        initializeUsers();
    }

    User(String username) {
        this.username = username;
        this.id = idCount++;
        users.add(this);
    }

    static void showUsers() {
        System.out.println(users.size() + " users:");
        for (User user : users)
            System.out.println(user.id + ". " + user.username);
    }

    static void updateFrom(String json, boolean isServer) {
        try {
            YaGson yaGson = new YaGson();
            User user = yaGson.fromJson(json, User.class);
            for (int i = 0; i < users.size(); i++)
                if (user.id == users.get(i).id) {
                    users.set(i, user);
                    return;
                }

            users.add(user);
            idCount = Math.max(idCount + 1, user.id);
            if (isServer)
                Chat.makeChats(user);
        } catch (Exception e) {
            View.printError(e);
        }
    }

    static void updateTo(ChatLineWriter chatLineWriter) {
        for (User user : users) {
            YaGson yaGson = new YaGson();
            chatLineWriter.writeLine(yaGson.toJson(user));
        }
    }

    List<Chat> getChats() {
        List<Chat> chats = new ArrayList<>();
        for (Chat chat : Chat.chats)
            if (chat.hasThis(this))
                chats.add(chat);
        return chats;
    }

    Chat getChatByID(int id) {
        for (Chat chat : getChats())
            if (chat.id == id)
                return chat;
        return null;
    }

    void resetID() {
        this.id = idCount++;
    }

    void save() {
        String path = "src/users/";
        path += username.toLowerCase().replaceAll("\\s+", "");
        path += ".json";
        try (FileOutputStream fos = new FileOutputStream(path);
             OutputStreamWriter isr = new OutputStreamWriter(fos,
                     StandardCharsets.UTF_8)) {

            YaGsonBuilder yaGsonBuilder = new YaGsonBuilder();
            yaGsonBuilder.serializeNulls();

            YaGson yaGson = yaGsonBuilder.create();

            yaGson.toJson(this, isr);
        } catch (IOException e) {
            View.printError(e);
        }
    }

    private static void initializeUsers() {
        File path = new File("src/users");
        File[] files = path.listFiles();
        if (files == null)
            return;
        for (File file : files)
            if (file.isFile())
                users.add(userMaker(file.getPath()));
    }

    private static User userMaker(String path) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            YaGson yaGson = new YaGson();
            return yaGson.fromJson(json, User.class);
        } catch (IOException e) {
            View.printError(e);
        }
        //  shouldn't reach here
        return null;
    }

    static void saveAll() {
        users.forEach(User::save);
    }

    public static List<User> getUsers() {
        return users;
    }
}
