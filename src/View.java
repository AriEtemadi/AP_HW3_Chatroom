import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

class View {

    private static Stack<Scene> scenes = new Stack<>();

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
        scenes.push(scene);
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    static void printError(Exception e) {
        System.out.println(e.getMessage());
        for (StackTraceElement s : e.getStackTrace())
            System.out.println(s);
    }

    void back() {
        scenes.pop();
        primaryStage.setScene(scenes.peek());
    }
}
