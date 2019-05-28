import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

class WelcomeScene {
    private static WelcomeScene instance = new WelcomeScene();

    private WelcomeScene() {
    }

    static WelcomeScene getInstance() {
        return instance;
    }

    private final int WIDTH = 300;
    private final int HEIGHT = 300;
    private Group root;
    private Scene scene;
    private TextField username;
    private Label enterYourName;

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf("4a6084"));
        View.getInstance().setScene(scene);

        initUsername();
        initEnterYourName();

        usernameAction();

        root.getChildren().addAll(enterYourName, username);
    }

    private void initUsername() {
        username = new TextField();
        username.setPromptText("USERNAME");
        username.relocate(100, 100);
        username.setFocusTraversable(false);
    }

    private void initEnterYourName() {
        enterYourName = new Label("ENTER YOUR NAME:");
        enterYourName.relocate(100, 80);
    }

    private void usernameAction() {
        username.setOnAction(event -> {
            String name = username.getText();
            try {
                Client client = new Client(name, 2048);
                client.runn();
            } catch (Exception e) {
                View.printError(e);
            }
        });
    }
}
