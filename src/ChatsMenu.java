import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

class ChatsMenu {
    private Client client;
    private final int WIDTH = 400;
    private final int HEIGHT = 500;
    private Group root;
    private Scene scene;
    private VBox chatsColumn;

    ChatsMenu(Client client) {
        this.client = client;
    }

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf("4a6084"));
        View.getInstance().setScene(scene);

        initChatsColumn();
        showChatsColumn();
        root.getChildren().addAll(chatsColumn);

        save();
    }

    private void initChatsColumn() {
        chatsColumn = new VBox();
        chatsColumn.relocate(50, 50);
    }

    private void showChatsColumn() {
        AnimationTimer animationTimer = new AnimationTimer() {
            long last = 0;
            int chatCount = 0;

            @Override
            public void handle(long now) {
                if (client.getUser() != null)
                    if (now - last > 100) {
                        List<Chat> chats = client.getUser().getChats();
                        for (int i = chatCount; i < chats.size(); i++) {
                            Chat chat = chats.get(i);
                            Button button = new Button();
                            button.setText(chat.getNameFor(client.getUser()));
                            button.setOnAction(event -> {
                                ChatScene chatScene = new ChatScene(chat, client);
                                chatScene.run();
                            });
                            chatsColumn.getChildren().add(button);
                            chatCount++;
                        }
                    }
            }
        };
        animationTimer.start();
    }

    private void save() {
        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (now - last > 300) {
                    User.saveAll();
                    Chat.saveAll();
                    last = now;
                }
            }
        }.start();
    }
}
