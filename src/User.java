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
    private static int idCount = 1;
    private String username;
    private int id;

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
            for (User value : users)
                if (user.id == value.id) {
                    value.copyFrom(user);
                    return;
                }

            users.add(user);
            updateIdCount(user.id);
            if (isServer)
                Chat.makeChats(user);
        } catch (Exception e) {
            //
        }
    }

    static void updateTo(ChatLineWriter chatLineWriter) {
        YaGson yaGson = new YaGson();
        for (User user : users)
            chatLineWriter.writeLine(yaGson.toJson(user));
    }

    void copyFrom(User user) {
        this.username = user.username;
        this.id = user.id;
    }

    List<Chat> getChats() {
        List<Chat> chats = new ArrayList<>();
        for (Chat chat : Chat.getChats())
            if (chat.hasThis(this))
                chats.add(chat);
        return chats;
    }

    Chat getChatByID(int id) {
        for (Chat chat : getChats())
            if (chat.getId() == id)
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
            isr.flush();
        } catch (IOException e) {
            View.printError(e);
        }
    }

    static void initializeUsers() {
        File path = new File("src/users");
        File[] files = path.listFiles();
        if (files == null)
            return;
        for (File file : files)
            if (file.isFile()) {
                User user = userMaker(file.getPath());
                updateIdCount(user.id);
                users.add(user);
            }
    }

    private static void updateIdCount(int id) {
        idCount = Math.max(idCount + 1, id + 1);
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

    static List<User> getUsers() {
        return users;
    }

    static User getUserByName(String username) {
        for (User user : users)
            if (user.username.equals(username))
                return user;
        return null;
    }

    static boolean hasThis(String username) {
        return getUserByName(username) != null;
    }

    static User getOrMake(String username) {
        if (hasThis(username))
            return getUserByName(username);
        return new User(username);
    }

    String getUsername() {
        return username;
    }

    int getId() {
        return id;
    }
}
