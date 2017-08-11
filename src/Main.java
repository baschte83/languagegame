import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Initializes main application with view and controller.
 * @author Sebastian Baumann, Korbinian Karl, Ehsan Moslehi
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Locale.setDefault(Locale.ENGLISH);
            ResourceBundle bundle = ResourceBundle.getBundle("resources/Resources", Locale.getDefault());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/MainView.fxml"), bundle);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(this.getClass().getResource("resources/lg.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to start the client GUI.
     * @param args is the default-varargs for main method.
     */
    public static void main(String[] args) {
        launch(args);
    }
}