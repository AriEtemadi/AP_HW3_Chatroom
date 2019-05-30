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
        root.getChildren().addAll(chatsColumn);

        save();
    }

    private void initChatsColumn() {
        chatsColumn = new VBox();
        chatsColumn.relocate(50, 50);

        List<Chat> chats = client.getUser().getChats();
        for (Chat chat : chats) {
            Button button = new Button();
            button.setText(chat.getNameFor(client.getUser()));
            chatsColumn.getChildren().add(button);
        }
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
