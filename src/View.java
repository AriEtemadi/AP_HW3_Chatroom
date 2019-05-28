import javafx.scene.Scene;
import javafx.stage.Stage;

class View {

    private View() {
    }

    private static final View instance = new View();

    static View getInstance() {
        return instance;
    }

    private Stage primaryStage;

    void start() {
        WelcomeScene.getInstance().run();
        primaryStage.show();
    }

    void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    static void printError(Exception e) {
        System.out.println(e.getMessage());
        for (StackTraceElement s : e.getStackTrace())
            System.out.println(s);
    }
}
