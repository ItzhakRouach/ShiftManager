package controller;
import app.MainAddWorker;
import app.MainShiftRequest;
import app.ShiftSchedulerGUI;
import javafx.fxml.FXML;
import javafx.stage.Stage;

//controller to handle main dashboard fxml.
public class MainDashboardController {

    //handle when pressed on added worker button, send the user to the relevant gui that handle adding user
    @FXML
    public void handleAddWorker() {
        try {
            Stage stage = new Stage();
            new MainAddWorker().start(stage);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //handle when pressed on added worker button, send the user to the relevant gui that handle adding requests
    @FXML
    public void handleAddRequest() {
        try {
            Stage stage = new Stage();
            new MainShiftRequest().start(stage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //handle when pressed on added worker button, send the user to the relevant gui that handle schedule
    @FXML
    public void handleGenerateSchedule() {
        try {
            Stage stage = new Stage();
            new ShiftSchedulerGUI().start(stage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
