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
    private static int idCount = 1;
    private static List<Chat> chats = new ArrayList<>();

    private int id;
    private String name;
    private List<User> users = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private User maker;

    Chat() {
    }

    Chat(String name, User maker, List<User> users) {
        this.name = name;
        this.maker = maker;
        this.users.add(maker);
        this.users.addAll(users);
        this.id = idCount++;
        chats.add(this);
    }

    void addMessage(String message) {
        messages.add(message);
    }

    static void updateFrom(String json) {
        try {
            YaGson yaGson = new YaGson();
            Chat chat = yaGson.fromJson(json, Chat.class);
            for (Chat value : chats)
                if (value.id == chat.id) {
                    value.copyFrom(chat);
                    return;
                }

            idCount = Math.max(idCount, chat.id + 1);
            chats.add(chat);
        } catch (Exception e) {
            //
        }
    }

    static void updateTo(ChatLineWriter chatLineWriter) {
        YaGson yaGson = new YaGson();
        for (Chat chat : chats)
            chatLineWriter.writeLine(yaGson.toJson(chat));
    }

    void copyFrom(Chat chat) {
        this.messages = chat.messages;
        this.name = chat.name;
        this.id = chat.id;
        this.users = chat.users;
    }

    static void showChats() {
        System.out.println(chats.size() + " chats:");
        for (Chat chat : chats) {
            System.out.print(chat.id + ": ");
            for (User user : chat.users)
                System.out.print(user.getUsername() + ", ");
            System.out.println();
        }
    }

    static void makeChats(User user) {
        for (User u : User.getUsers())
            if (!u.getUsername().equals(user.getUsername())) {
                Chat chat = new Chat();
                chat.setID();
                chat.addUser(user);
                chat.addUser(u);
                chats.add(chat);
            }
    }

    void addUser(User user) {
        if (!hasThis(user))
            users.add(user);
    }

    private void setID() {
        this.id = idCount++;
    }

    boolean hasThis(User user) {
        for (User u : users)
            if (u.getId() == user.getId())
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
            isr.flush();
        } catch (IOException e) {
            View.printError(e);
        }
    }

    static void initializeChats() {
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

    static List<Chat> getChats() {
        return chats;
    }

    String getName() {
        return name;
    }

    List<User> getUsers() {
        return users;
    }

    List<String> getMessages() {
        return messages;
    }

    int getId() {
        return id;
    }

    static void addChat(Chat chat) {
        if (chat != null)
            chats.add(chat);
    }

    String getNameFor(User user) {
        if (users.size() > 2)
            return name;
        for (User u : users)
            if (!u.getUsername().equals(user.getUsername()))
                return u.getUsername();
        return null;
    }

    static boolean hasThisName(String name) {
        for (Chat chat : chats)
            if (chat.name != null && chat.name.equals(name))
                return true;
        return false;
    }

    static boolean isGroupNameValid(String name) {
        if (name == null || name.equals(""))
            return false;
        if (hasThisName(name))
            return false;
        return true;
    }
}
