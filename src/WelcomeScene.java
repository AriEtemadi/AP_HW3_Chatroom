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

    private final String BACKGROUND_COLOR = "3D444B";
    private final int WIDTH = 350;
    private final int HEIGHT = 400;
    private Group root;
    private Scene scene;
    private TextField username;
    private Label enterYourName;

    void run() {
        root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT, Color.valueOf(BACKGROUND_COLOR));
        View.getInstance().setScene(scene);

        initUsername();
        initEnterYourName();

        usernameAction();

        root.getChildren().addAll(enterYourName, username);
    }

    private void initUsername() {
        username = new TextField();
        username.setPromptText("USERNAME");
        username.relocate(100, 200);
        username.setFocusTraversable(false);
        username.setStyle("-fx-text-fill: #3D444B");
    }

    private void initEnterYourName() {
        enterYourName = new Label("ENTER YOUR NAME:");
        enterYourName.setTextFill(Color.valueOf("009687"));
        enterYourName.relocate(120, 180);
    }

    private void usernameAction() {
        username.setOnAction(event -> {
            String name = username.getText();
            try {
                Client client = new Client(name, Server.getPORT());
                client.runn();
            } catch (Exception e) {
                View.printError(e);
            }
        });
    }
}
