package controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import java.util.ArrayList;
import java.util.List;


//
public class ShiftRequestController {

    @FXML
    public ComboBox<Worker> workerComboBox;
    @FXML
    public ComboBox<Day> dayComboBox;
    @FXML
    public ComboBox<ShiftTime> shiftComboBox;
    @FXML
    public CheckBox isDayOffCheckBox;
    @FXML
    private TableView<Request> requestTable;
    @FXML
    private TableColumn<Request, String> workerColumn;
    @FXML
    private TableColumn<Request, String> dayColumn;
    @FXML
    private TableColumn<Request, String> shiftColumn;
    @FXML
    private TableColumn<Request, String> dayOffColumn;

    @FXML
    public void handleSaveRequests() {
        RequestManager.saveToJson();
        showAlert("הצלחה", "כל הבקשות נשמרו בהצלחה");
    }

    @FXML
    public void initialize() {
        WorkerManager.loadFromJson();
        workerComboBox.setItems(FXCollections.observableArrayList(WorkerManager.getWorkers()));
        dayComboBox.setItems(FXCollections.observableArrayList(Day.values()));
        shiftComboBox.setItems(FXCollections.observableArrayList(ShiftTime.values()));


        isDayOffCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            shiftComboBox.setDisable(newVal);
            if (newVal) {
                shiftComboBox.setValue(null);
            }
        });


        workerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWorker().getName()));
        ;
        dayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDay().toString()));

        shiftColumn.setCellValueFactory(cellData -> {
            ShiftTime shift = cellData.getValue().getShift();
            String text = (shift != null) ? shift.toString() : "—"; // או "חופש" אם אתה מעדיף
            return new SimpleStringProperty(text);
        });

        // אייקון של חופש במקום טקסט רגיל
        dayOffColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isDayOff() ? "✔️" : "➖"));

        RequestManager.loadFromJson();

        List<Request> all = new ArrayList<>();
        for (List<Request> r : RequestManager.getAllRequests().values()) {
            all.addAll(r);
        }
        requestTable.setItems(FXCollections.observableArrayList(all));
    }

    @FXML
    private void handleAddRequests() {
        Worker selectedWorker = workerComboBox.getValue();
        Day selectedDay = dayComboBox.getValue();
        ShiftTime selectedShift = shiftComboBox.getValue();
        boolean isDayOff = isDayOffCheckBox.isSelected();


        if (selectedWorker == null || selectedDay == null || (!isDayOff && selectedShift == null)) {
            showAlert("שגיאה", "אנא בחר עובד, יום ומשמרת לפני הוספת בקשה.");
            return;
        }

        Request request = new Request(selectedWorker, selectedDay, selectedShift, isDayOff);
        selectedWorker.addRequest(request);
        RequestManager.addRequest(selectedWorker.getName(), request);
        requestTable.getItems().add(request);

        showAlert("הצלחה", "הבקשה נוספה לעובד " + selectedWorker.getName());

        // איפוס בחירות לטובת שימוש חוזר
        dayComboBox.setValue(null);
        shiftComboBox.setValue(null);
        isDayOffCheckBox.setSelected(false);

    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();

    }
    @FXML
    private void handleEditRequest() {
        Request selected = requestTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("שגיאה", "אנא בחר בקשה לעריכה");
            return;
        }

        // מציבים את הערכים בשדות
        workerComboBox.setValue(selected.getWorker());
        dayComboBox.setValue(selected.getDay());
        shiftComboBox.setValue(selected.getShift());
        isDayOffCheckBox.setSelected(selected.isDayOff());

        // מסירים מהטבלה וה־Map כדי שנוכל להכניס גרסה חדשה מעודכנת אח"כ
        requestTable.getItems().remove(selected);
        RequestManager.getRequestsForWorker(selected.getWorker().getName()).remove(selected);

        showAlert("מצב עריכה", "ערוך את הבקשה בשדות ולחץ שוב על 'הוסף בקשה'");
    }

    @FXML
    private void handleDeleteRequest() {
        Request selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("שגיאה", "יש לבחור בקשה למחיקה");
            return;
        }

        String workerName = selected.getWorker().getName();
        boolean removed = RequestManager.getRequestsForWorker(workerName).remove(selected);

        if (removed) {
            requestTable.getItems().remove(selected);
            RequestManager.saveToJson(); // אם תרצה לשמור מיידית
            showAlert("הצלחה", "הבקשה נמחקה");
        } else {
            showAlert("שגיאה", "לא ניתן למחוק את הבקשה");
        }
    }

    public void handleDeleteAll() {
        RequestManager.clearAllRequests();
        requestTable.getItems().clear();
        showAlert("בוצע" ,"כל הבקשות נמחקו בהצלה");
    }
}