import com.gilecode.yagson.YaGson;

import java.util.ArrayList;
import java.util.List;

class Chat {
    static int idCount = 1;
    static List<Chat> chats = new ArrayList<>();

    int id;
    String name;
    List<User> users = new ArrayList<>();
    List<String> messages = new ArrayList<>();

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
        for (User u : User.users)
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

    void setID() {
        this.id = idCount++;
    }
}
