import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

class ChatsMenu {
    private static final int MAX_MSG_NUM = 20;
    private Client client;
    private Chat chat;
    private final int WIDTH = 350;
    private final int HEIGHT = 400;
    private final int BORDER_DIST = 5;
    private Group root;
    private VBox chatsColumn;
    private Group msgGroup;
    private TextField input;
    private TextField groupSize;
    private Button send;
    private Button back;
    private Button lastClickedButton;
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
        initGroupSize();
        showChatsColumn();
        root.getChildren().addAll(chatsColumn, createGroup, groupSize);

        createGroupButtonAction();
        groupSizeButtonAction();

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
        msgGroup.relocate(BORDER_DIST * 2 + chatsColumn.getPrefWidth() + 20, BORDER_DIST);
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
                        for (int i = Math.max(chat.getMessages().size() - MAX_MSG_NUM, 0); i < chat.getMessages().size(); i++) {
                            String message = chat.getMessages().get(i);
                            Label msg = new Label(message);
                            messages.getChildren().add(msg);
                        }
                        messages.relocate(0, 350 - messages.getHeight());
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
        input.setPrefWidth(165);
        input.setFocusTraversable(false);
        input.setOnAction(event -> sendAction());
    }

    private void initSend() {
        send = new Button("SEND");
        send.relocate(170, HEIGHT - 35);
    }

    private void initBack() {
        back = new Button("BACK");
        back.relocate(170, HEIGHT - 35 * 2);
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
                            button.setOnAction(event -> {
                                if (lastClickedButton != null)
                                    lastClickedButton.setStyle("-fx-background-color: #ffffff;");
                                ChatsMenu.this.chat = chat;
                                button.setStyle("-fx-background-color: #ffff00;");
                                lastClickedButton = button;
                            });
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
                if (now - last > 100) {
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

    private void createGroupButtonAction() {
        createGroup.setOnAction(event -> createGroupAction());
    }

    private void initGroupSize() {
        groupSize = new TextField();
        groupSize.setPromptText("GROUP SIZE");
        groupSize.setPrefWidth(chatsColumn.getPrefWidth());
        groupSize.relocate(BORDER_DIST, HEIGHT - 62);
        groupSize.setFocusTraversable(false);
        createGroup.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.SECONDARY)
                System.out.println("right click");
        });
    }

    private void groupSizeButtonAction() {
        groupSize.setOnAction(event -> createGroupAction());
    }

    private void createGroupAction() {

    }
}
