import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class ChatScene {
    private Chat chat;
    private User user;

    ChatScene(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }

    private Group root;
    private Scene scene;
    private TextField input;
    private Button send;
    private Button back;
    private VBox messages;
    private final int WIDTH = 400;
    private final int HEIGHT = 500;

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf("4a6084"));
        View.getInstance().setScene(scene);

        initMessages();
        initInput();
        initSend();
        initBack();

        root.getChildren().addAll(messages, input, send, back);

        backAction();
    }

    private void initMessages() {
        messages = new VBox();
        messages.relocate(50, 50);

        for (String message : chat.getMessages()) {
            Label msg = new Label(message);
            messages.getChildren().add(msg);
        }
    }

    private void initInput() {
        input = new TextField();
        input.setPromptText("MESSAGE");
        input.relocate(10, 450);
        input.setFocusTraversable(false);
    }

    private void initSend() {
        send = new Button("SEND");
        send.relocate(300, 450);
    }

    private void initBack() {
        back = new Button("BACK");
        back.relocate(300, 400);
    }

    private void backAction() {
        back.setOnAction(event -> View.getInstance().back());
    }

}
