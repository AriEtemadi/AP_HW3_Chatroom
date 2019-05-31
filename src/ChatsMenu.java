import com.gilecode.yagson.YaGson;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

class ChatsMenu {
    private Client client;
    private Chat chat;

    private final int MAX_MSG_NUM = 20;
    private final int WIDTH = 350;
    private final int HEIGHT = 400;
    private final int BORDER_DIST = 5;
    private final String BACKGROUND_COLOR = "-fx-background-color: #18191D;";
    private final String CHATS_COLOR = "-fx-background-color: #282E33;";
    private final String SELECTED_CHAT_COLOR = "-fx-background-color: #009687;";
    private final String RIGHT_CLICKED_COLOR = "-fx-background-color: #65AADD;";
    private final String TEXT_COLOR = "FFFFFF";
    private final String TEXTFIELD_COLOR = "-fx-background-color: #3D444B; -fx-text-fill: #ffffff";
    private final String HOVERED_BUTTON_STYLE = "-fx-background-color: #004D46";

    private Scene scene;
    private Group root;
    private Group chatGroup;
    private Group messageGroup;

    private VBox chatsColumn;

    private TextField input;
    private Button emojiDisplayer;
    private HBox emojis;
    private Button send;
    private Button back;
    private VBox messages;

    private TextField groupName;
    private Button addMember;
    private Label createGroupErrMsg;
    private Button createGroup;

    private Button selectedChat;
    private List<Button> rightClickedButtons = new ArrayList<>();

    ChatsMenu(Client client) {
        this.client = client;
    }

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf("18191D"));
        View.getInstance().setScene(scene);

        initChatGroup();
        initMessageGroup();
        initCreateGroupNodes();

        root.getChildren().addAll(chatGroup, createGroup, groupName, createGroupErrMsg);
    }

    private void initChatGroup() {
        chatGroup = new Group();

        initChatsColumn();
        initBack();

        showChatsColumn();

        chatGroup.getChildren().addAll(chatsColumn, back);
    }

    private void initChatsColumn() {
        chatsColumn = new VBox();
        chatsColumn.relocate(0, 0);
        chatsColumn.setPrefWidth(100);
        chatsColumn.setPrefHeight(500);
        chatsColumn.setSpacing(3);
        chatsColumn.setStyle(CHATS_COLOR);
        Button chats = new Button("CHATS");
        chats.setTextFill(Color.valueOf(TEXT_COLOR));
        chatsColumn.getChildren().add(chats);
        chats.setPrefWidth(ChatsMenu.this.chatsColumn.getPrefWidth());
        chats.setStyle(SELECTED_CHAT_COLOR);
        chats.setShape(new Rectangle(1, 1));
    }

    private void initBack() {
        back = new Button("⬅");
        back.relocate(0, 0);
        back.setStyle(SELECTED_CHAT_COLOR);
        back.setTextFill(Color.valueOf(TEXT_COLOR));
        back.setFont(Font.font(11));
        back.setShape(new Rectangle(1, 1));
    }

    private void showChatsColumn() {
        AnimationTimer chatsColumn = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (client.getUser() != null)
                    if (now - last > 100) {
                        List<Chat> chats = client.getUser().getChats();
                        for (Chat chat : chats) {
                            if (!client.isShowed(chat)) {
                                client.addShowedChat(chat);
                                Button button = new Button();
                                button.setText(chat.getNameFor(client.getUser()));
                                button.setTextFill(Color.valueOf(TEXT_COLOR));
                                button.setOnMouseEntered(e -> button.setStyle(HOVERED_BUTTON_STYLE));
                                button.setOnMouseExited(e -> {
                                    if (selectedChat == button)
                                        button.setStyle(SELECTED_CHAT_COLOR);
                                    else
                                        button.setStyle(CHATS_COLOR);
                                });
                                button.setOnAction(event -> {
                                    if (selectedChat != null)
                                        selectedChat.setStyle(CHATS_COLOR);
                                    ChatsMenu.this.chat = chat;
                                    button.setStyle(SELECTED_CHAT_COLOR);
                                    selectedChat = button;
                                });
                                button.setOnMouseReleased(event -> {
                                    if (event.getButton() == MouseButton.SECONDARY)
                                        if (rightClickedButtons.contains(button)) {
                                            rightClickedButtons.remove(button);
                                            if (selectedChat == button)
                                                button.setStyle(SELECTED_CHAT_COLOR);
                                            else
                                                button.setStyle(CHATS_COLOR);
                                        } else {
                                            rightClickedButtons.add(button);
                                            button.setStyle(RIGHT_CLICKED_COLOR);
                                        }
                                });
                                button.setPrefWidth(ChatsMenu.this.chatsColumn.getPrefWidth());
                                button.setStyle(CHATS_COLOR);
                                button.setShape(new Rectangle(1, 1));
                                ChatsMenu.this.chatsColumn.getChildren().add(button);
                            }
                        }
                    }
            }
        };
        chatsColumn.start();
    }

    private void initMessageGroup() {
        messageGroup = new Group();
        messageGroup.relocate(BORDER_DIST * 2 + chatsColumn.getPrefWidth(), BORDER_DIST);

        initEmojis();
        initMessages();
        initInput();
        initSend();
        initEmojiDisplayer();

        showMessages();

        messageGroup.getChildren().addAll(messages, input, send, emojiDisplayer);

        backAction();
        sendButtonAction();
        emojiDisplayerAction();
    }

    private void initMessages() {
        messages = new VBox();
        messages.relocate(0, 0);
    }

    private void initInput() {
        input = new TextField();
        input.setPromptText("MESSAGE");
        input.relocate(30, HEIGHT - 34);
        input.setPrefWidth(165);
        input.setFocusTraversable(false);
        input.setOnAction(event -> sendAction());
        input.setStyle(TEXTFIELD_COLOR);
    }

    private void initSend() {
        send = new Button("\uD83D\uDCE8");
        send.relocate(200, HEIGHT - 34);
        send.setStyle(TEXTFIELD_COLOR);
        send.setAlignment(Pos.CENTER);
    }

    private void initEmojiDisplayer() {
        emojiDisplayer = new Button("\uD83D\uDE01");
        emojiDisplayer.relocate(0, HEIGHT - 34);
        emojiDisplayer.setStyle(TEXTFIELD_COLOR);
        emojiDisplayer.setAlignment(Pos.CENTER);
    }

    private void initEmojis() {
        emojis = new HBox();
        emojis.relocate(0, HEIGHT - 60);
        String[] emojiStrings = {"\uD83D\uDE02", "\uD83D\uDE0A", "\uD83D\uDE18", "\uD83D\uDE0D", "\uD83D\uDE01"
                , "\uD83D\uDE21", "\uD83D\uDE1C", "\uD83D\uDE31", "\uD83D\uDE2D"};
        for (String s : emojiStrings) {
            Button button = new Button(s);
            button.setFont(Font.font(10));
            button.setStyle(TEXTFIELD_COLOR);
            button.setAlignment(Pos.CENTER);
            button.setShape(new Rectangle(1, 1));
            button.setOnAction(event -> input.setText(input.getText().concat(s)));
            emojis.getChildren().add(button);
        }
    }

    private void backAction() {
        back.setOnAction(event -> View.getInstance().back());
    }

    private void sendButtonAction() {
        send.setOnAction(event -> sendAction());

    }

    private void emojiDisplayerAction() {
        emojiDisplayer.setOnAction(event -> {
            if (messageGroup.getChildren().contains(emojis))
                messageGroup.getChildren().remove(emojis);
            else
                messageGroup.getChildren().add(emojis);
        });
    }

    private void sendAction() {
        String msg;
        if (input.getText().equalsIgnoreCase("image"))
            msg = getImageAsString();
        else
            msg = input.getText();
        chat.addMessage(msg);
        client.updateChatsForServer();
        input.setText("");
    }

    private String getImageAsString() {
        try {
            BufferedImage bImage = ImageIO.read(new File("src/images.jpg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            byte[] data = bos.toByteArray();
            YaGson yaGson = new YaGson();
            return yaGson.toJson(data);

        } catch (Exception e) {
            View.printError(e);
        }
        return null;
    }

    private void showMessages() {
        AnimationTimer showMessages = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (now - last > 100) {
                    messages.getChildren().remove(0, messages.getChildren().size());
                    if (chat != null) {
                        if (!root.getChildren().contains(messageGroup))
                            root.getChildren().add(messageGroup);
                        for (int i = Math.max(chat.getMessages().size() - MAX_MSG_NUM, 0);
                             i < chat.getMessages().size(); i++) {
                            showMessage(chat.getMessages().get(i));
                        }
                        messages.relocate(0, 350 - messages.getHeight());
                    }
                    last = now;
                }
            }
        };
        showMessages.start();
    }

    private void showMessage(String message) {
        if (message.length() > 1000) {
            try {
                YaGson yaGson = new YaGson();
                byte[] data = yaGson.fromJson(message, byte[].class);
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                BufferedImage bImage2 = ImageIO.read(bis);
                ImageIO.write(bImage2, "jpg", new File("src/image2.jpg"));
                Image image = new Image(new FileInputStream("src/image2.jpg"));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                messages.getChildren().add(imageView);
            } catch (Exception e) {
                View.printError(e);
            }
        } else {
            Label msg = new Label(message);
            msg.setTextFill(Color.valueOf(TEXT_COLOR));
            msg.setOnMouseClicked(event -> {
                msg.setStyle("-fx-background-color: #ffffff;");
            });
            messages.getChildren().add(msg);
        }
    }

    private void initCreateGroupNodes() {
        initCreateGroup();
        initAddMember();
        initGroupName();
        initCreateGroupErrMsg();

        showAddMember();

        createGroupButtonAction();
        groupNameButtonAction();
        addMemberAction();
    }

    private void initCreateGroup() {
        createGroup = new Button("CREATE GROUP");
        createGroup.relocate(0, HEIGHT - 56);
        createGroup.setPrefWidth(ChatsMenu.this.chatsColumn.getPrefWidth());
        createGroup.setStyle("-fx-background-color: #df5b5e;");
        createGroup.setShape(new Rectangle(1, 1));
        createGroup.setTextFill(Color.valueOf(TEXT_COLOR));
        createGroup.setFont(Font.font(11));
    }

    private void initCreateGroupErrMsg() {
        createGroupErrMsg = new Label("");
        createGroupErrMsg.relocate(BORDER_DIST, HEIGHT - 70);
        createGroupErrMsg.setTextFill(Color.ORANGE);
        createGroupErrMsg.setFont(Font.font(8));
    }

    private void initAddMember() {
        addMember = new Button("ADD MEMBER");
        addMember.relocate(BORDER_DIST, HEIGHT - 105);
    }

    private void initGroupName() {
        groupName = new TextField();
        groupName.setPromptText("GROUP NAME");
        groupName.setPrefWidth(chatsColumn.getPrefWidth());
        groupName.relocate(0, HEIGHT - 27);
        groupName.setFocusTraversable(false);
        groupName.setFont(Font.font(10));
        groupName.setStyle(TEXTFIELD_COLOR);
        groupName.setAlignment(Pos.CENTER);
    }

    private void showAddMember() {
        AnimationTimer animationTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (now - last > 100) {
                    if (selectedChat != null
                            && Chat.isThisTheMakerOf(client.getUser(), selectedChat.getText())) {
                        if (!root.getChildren().contains(addMember)) {
                            root.getChildren().add(addMember);
                        }
                    } else root.getChildren().remove(addMember);
                    last = now;
                }
            }
        };
        animationTimer.start();
    }

    private void createGroupButtonAction() {
        createGroup.setOnAction(event -> {
            createGroupAction();
            eraseRightClicked();
        });
    }

    private void groupNameButtonAction() {
        groupName.setOnAction(event -> {
            createGroupAction();
            eraseRightClicked();
        });
    }

    private void eraseRightClicked() {
        for (Button button : rightClickedButtons)
            if (selectedChat == button)
                button.setStyle(SELECTED_CHAT_COLOR);
            else
                button.setStyle(CHATS_COLOR);
        rightClickedButtons = new ArrayList<>();
    }

    private void createGroupAction() {
        if (rightClickedButtons.size() < 2) {
            createGroupErrMsg.setText("NOT ENOUGH MEMBERS");
            return;
        }
        if (!Chat.isGroupNameValid(groupName.getText())) {
            createGroupErrMsg.setText("INVALID NAME");
            return;
        }
        List<String> usernames = new ArrayList<>();
        for (Button button : rightClickedButtons)
            usernames.add(button.getText());
        boolean wasSuccessful = client.createGroup(groupName.getText(), usernames);
        if (!wasSuccessful)
            createGroupErrMsg.setText("ERROR");
        else
            createGroupErrMsg.setText("");
        groupName.setText("");
    }

    private void addMemberAction() {
        addMember.setOnAction(event -> {
            if (rightClickedButtons.size() < 1)
                return;
            if (selectedChat == null)
                return;
            List<String> usernames = new ArrayList<>();
            for (Button button : rightClickedButtons)
                usernames.add(button.getText());
            boolean wasSuccessful = client.addMember(selectedChat.getText(), usernames);
            if (wasSuccessful)
                createGroupErrMsg.setText("");
            else
                createGroupErrMsg.setText("ERROR");
            eraseRightClicked();
        });
    }
}
