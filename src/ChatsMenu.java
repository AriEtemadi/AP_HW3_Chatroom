import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

class ChatsMenu {
    private Client client;
    private Chat chat;
    private final int WIDTH = 330;
    private final int HEIGHT = 400;
    private final int BORDER_DIST = 5;
    private Group root;
    private VBox chatsColumn;
    private Group msgGroup;
    private TextField input;
    private Button send;
    private Button back;
    private Button createGroup;
    private VBox messages;
    private Scene scene;

    ChatsMenu(Client client) {
        this.client = client;
    }

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf("4a6084"));
        View.getInstance().setScene(scene);

        initChatsColumn();
        initMsgGroup();
        initCreateGroup();
        showChatsColumn();
        root.getChildren().addAll(chatsColumn, createGroup);

        save();
    }

    private void initChatsColumn() {
        chatsColumn = new VBox();
        chatsColumn.relocate(BORDER_DIST, BORDER_DIST);
        chatsColumn.setPrefWidth(100);
        chatsColumn.setSpacing(3);
    }

    private void initMsgGroup() {
        msgGroup = new Group();
        msgGroup.relocate(BORDER_DIST * 2 + chatsColumn.getPrefWidth(), BORDER_DIST);
        initMessages();
        initInput();
        initSend();
        initBack();

        showMessages();

        msgGroup.getChildren().addAll(messages, input, send, back);

        backAction();
        sendButtonAction();
    }

    private void initMessages() {
        messages = new VBox();
        messages.relocate(0, 0);
    }

    private void showMessages() {
        AnimationTimer animationTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (now - last > 100) {
                    messages.getChildren().remove(0, messages.getChildren().size());
                    if (chat != null) {
                        if (!root.getChildren().contains(msgGroup))
                            root.getChildren().add(msgGroup);
                        for (String message : chat.getMessages()) {
                            Label msg = new Label(message);
                            messages.getChildren().add(msg);
                        }
                    }
                    last = now;
                }
            }
        };
        animationTimer.start();
    }

    private void initInput() {
        input = new TextField();
        input.setPromptText("MESSAGE");
        input.relocate(0, HEIGHT - 35);
        input.setFocusTraversable(false);
        input.setOnAction(event -> sendAction());
    }

    private void initSend() {
        send = new Button("SEND");
        send.relocate(150, HEIGHT - 35);
    }

    private void initBack() {
        back = new Button("BACK");
        back.relocate(150, HEIGHT - 35 * 2);
    }

    private void backAction() {
        back.setOnAction(event -> View.getInstance().back());
    }

    private void sendButtonAction() {
        send.setOnAction(event -> sendAction());

    }

    private void sendAction() {
        chat.addMessage(input.getText());
        client.updateChatsForServer();
        input.setText("");
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
                            button.setOnAction(event -> ChatsMenu.this.chat = chat);
                            button.setPrefWidth(chatsColumn.getPrefWidth());
                            button.setStyle("-fx-background-color: #ffffff;");
                            button.setShape(new Rectangle(1, 1));
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

    private void initCreateGroup() {
        createGroup = new Button("CREATE GROUP");
        createGroup.relocate(BORDER_DIST, HEIGHT - 30);
    }
}
