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

class Chat {
    static int idCount = 1;
    static List<Chat> chats = new ArrayList<>();

    int id;
    String name;
    List<User> users = new ArrayList<>();
    List<String> messages = new ArrayList<>();

    static {
        initializeChats();
    }

    void addMessage(String message) {
        messages.add(message);
    }

    static void updateFrom(String json) {
        try {
            YaGson yaGson = new YaGson();
            Chat chat = yaGson.fromJson(json, Chat.class);
            for (int i = 0; i < chats.size(); i++)
                if (chats.get(i).id == chat.id) {
                    chats.set(i, chat);
                    return;
                }

            idCount = Math.max(idCount, chat.id + 1);
            chats.add(chat);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
    }

    static void updateTo(ChatLineWriter chatLineWriter) {
        for (Chat chat : chats) {
            YaGson yaGson = new YaGson();
            chatLineWriter.writeLine(yaGson.toJson(chat));
        }
    }

    static void showChats() {
        System.out.println(chats.size() + " chats:");
        for (Chat chat : chats) {
            System.out.print(chat.id + ": ");
            for (User user : chat.users)
                System.out.print(user.username + ", ");
            System.out.println();
        }
    }

    static void makeChats(User user) {
        for (User u : User.getUsers())
            if (!u.username.equals(user.username)) {
                Chat chat = new Chat();
                chat.setID();
                chat.addUser(user);
                chat.addUser(u);
                chats.add(chat);
            }
    }

    private void addUser(User user) {
        users.add(user);
    }

    private void setID() {
        this.id = idCount++;
    }

    boolean hasThis(User user) {
        for (User u : users)
            if (u.id == user.id)
                return true;
        return false;
    }

    void show() {
        System.out.println("chat " + name + ": ");
        for (int i = Math.max(0, messages.size() - 10); i < messages.size(); i++) {
            System.out.println(messages.get(i));
        }
    }

    void save() {
        String path = "src/chats/";
        path += Integer.toString(id).toLowerCase().replaceAll("\\s+", "");
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

    private static void initializeChats() {
        File path = new File("src/chats");
        File[] files = path.listFiles();
        if (files == null)
            return;
        for (File file : files)
            if (file.isFile())
                chats.add(chatMaker(file.getPath()));
    }

    private static Chat chatMaker(String path) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            YaGson yaGson = new YaGson();
            return yaGson.fromJson(json, Chat.class);
        } catch (IOException e) {
            View.printError(e);
        }
        //  shouldn't reach here
        return null;
    }

    static void saveAll() {
        chats.forEach(Chat::save);
    }
}
