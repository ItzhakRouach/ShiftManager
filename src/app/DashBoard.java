package app;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashBoard  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainDashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

        primaryStage.setTitle("Schedule Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}
