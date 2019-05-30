import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class View {

    private static List<Scene> scenes = new ArrayList<>();

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
        scenes.add(scene);
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
        if (scenes.size() < 2)
            return;
        setScene(scenes.get(scenes.size() - 2));
    }
}
