import com.gilecode.yagson.YaGson;

import java.util.ArrayList;
import java.util.List;

class User {
    static List<User> users = new ArrayList<>();
    static int idCount;
    String username;
    int id;

    User(String username) {
        this.username = username;
        this.id = 0;
        users.add(this);
    }

    static void showUsers() {
        System.out.println(users.size() + " users:");
        for (User user : users)
            System.out.println(user.username);
    }

    static void updateFrom(String json, boolean isServer) {
        try {
            YaGson yaGson = new YaGson();
            User user = yaGson.fromJson(json, User.class);
            for (int i = 0; i < users.size(); i++)
                if (user.username.equals(users.get(i).username)) {
                    users.set(i, user);
                    return;
                }
            users.add(user);
            idCount = Math.max(idCount, user.id);
            if (isServer)
                Chat.makeChats(user);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
    }

    static void updateTo(ChatLineWriter chatLineWriter) {
        for (User user : users) {
            YaGson yaGson = new YaGson();
            chatLineWriter.writeLine(yaGson.toJson(user));
        }
    }

    private List<Chat> getChats() {
        List<Chat> chats = new ArrayList<>();
        for (Chat chat : Chat.chats)
            if (chat.users.contains(this))
                chats.add(chat);
        return chats;
    }

    Chat getChatByID(int id) {
        for (Chat chat : getChats())
            if (chat.id == id)
                return chat;
        return null;
    }


}
