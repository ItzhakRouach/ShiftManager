package controller;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import model.Role;
import javafx.fxml.FXML;
import model.Worker;
import model.WorkerManager;
import java.util.List;

//Controller to handle all components inside addWorker fxml .

public class AddWorkerController {
    @FXML private TextField minShiftsField; //binding to get the minimum shifts the worker set to.
    @FXML private TextField maxShiftsField; //binding to get the maximum shifts the worker set to.
    @FXML private TextField nameField; //binding to get the Worker name that he enters.
    @FXML private ComboBox<Role> roleComboBox; //use a combo box that way worker selects his role from the given options.
    @FXML private CheckBox shomerShabatCheckBox; // use check box that the worker check if he shomer-shabat, default false.
    @FXML private CheckBox highPriorityCheckBox; // use check box that the worker checks if he has priority default false.
    @FXML private TableView<Worker> workerTable; //let as handle the table that will show all the workers and their properties
    @FXML private TableColumn<Worker, String> nameColumn; //column of worker name
    @FXML private TableColumn<Worker, String> roleColumn; // column of a worker role
    @FXML private TableColumn<Worker, String> priorityColumn; // column of worker priority
    @FXML private TableColumn<Worker, String> shabatColumn; // column of the worker is shomer shabbat or not
    @FXML private TableColumn<Worker, Integer> minColumn; // column of minimum shifts worker
    @FXML private TableColumn<Worker, Integer> maxColumn; // column of maximum shifts worker


    //Method that start each time the gui of added worker been called.
    public void initialize() {
        System.out.println("AddWorkController loaded!"); //print to console to see if been loaded successfully
        roleComboBox.getItems().addAll(Role.values()); // add the role option to the role combo box.
        WorkerManager.loadFromJson(); //loaded the workers that user saves if there isn't none then start fresh.
        updateTable(); // update the table according to the workers that loaded from JSON file.

        //method that if a user presses on someone in the table, it will show their properties in the fields.
        workerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                WorkerDetails(newSelection);
            }
        });
    }


    //this is the main method, it handles the adding of worker to our table.
    @FXML
    private void handleAddWorker() {
        String name = nameField.getText();
        Role selectedRole = roleComboBox.getValue();
        boolean isPriority = highPriorityCheckBox.isSelected();
        boolean shomerShabat = shomerShabatCheckBox.isSelected();
        if (name == null || name.trim().isEmpty() || selectedRole == null || minShiftsField.getText().isEmpty() || maxShiftsField.getText().isEmpty()) {
            showAlert("שגיאה!", "אנא מלאו את כל השדות הנדרשים"); // pop alert if one of the fields is empty
            return;
        }
        try {
            int min = Integer.parseInt(minShiftsField.getText()); //convert String to Int
            int max = Integer.parseInt(maxShiftsField.getText()); //convert String to Int
            if (min > max) {
                showAlert("שגיאה!", "מינימום לא יכול להיות גדול ממקסימום"); // pop alert because min can't be more than max
                return;
            }
            Worker newWorker = new Worker(name, selectedRole, shomerShabat, isPriority, min, max); //use constructor to define new worker.
            WorkerManager.addWorker(newWorker); // add the worker to the main worker list.
            WorkerManager.saveToJson(); // save the worker that way if the program is
            showAlert("הצלחה!", "העובד נוסף ונשמר");
            workerTable.getItems().add(newWorker);
        } catch (NumberFormatException e) {
            showAlert("שגיאה.", "אנא הזן מספרים חוקיים במינימום/מקסימום");
        }
    }

    //Method to handle search worker
    public void handleSearchWorker() {
        String name = nameField.getText().trim(); //trim any white space in the name field
        if (name.isEmpty()){
            workerTable.setItems(FXCollections.observableArrayList(WorkerManager.getWorkers()));
            return;
        }
        List<Worker> filterd = WorkerManager.getWorkers().stream() //return the name the user searched for
                .filter(w -> w.getName().contains(name)).toList();

        workerTable.setItems(FXCollections.observableArrayList(filterd));
    }

    //Method to show alert to the user given an title and msg.
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    //Mrthod to handle delete worker.
    private void handleDeleteWorker() {
        Worker selected = workerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean removed = WorkerManager.removeWorker(selected);
            if (removed) {
                WorkerManager.saveToJson();
                workerTable.getItems().remove(selected);
                showAlert("הצלחה!", "העובד/ת נמחק מן המערכת");
                System.out.println("Worker has been deleted successfully !");
            } else {
                showAlert("שגיאה", "לא ניתן למחוק את העובד מהמערכת");
                System.out.println("Error in try delete Worker from the list!");
            }
        } else {
            showAlert("שגיאה", "אנא בחר עובד למחיקה");
        }
    }

    //Method to update the table.
    private void updateTable(){
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority() ? "✅" : "❌"));
        shabatColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().IsShomerShabat() ? "✅" : "❌"));
        minColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMinShifts()).asObject());
        maxColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaxShifts()).asObject());
        workerTable.setItems(FXCollections.observableArrayList(WorkerManager.getWorkers()));
    }

    //Method to edit an existing worker in our table.
    public void handleEditWorker() {
        Worker selected = workerTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            return;
        }
        String name = nameField.getText();
        Role role= roleComboBox.getValue();
        boolean isPriority = highPriorityCheckBox.isSelected();
        boolean shomerShabat = shomerShabatCheckBox.isSelected();
        if (name == null || name.trim().isEmpty() || role == null || minShiftsField.getText().isEmpty() || maxShiftsField.getText().isEmpty()) {
            showAlert("שגיאה!", "אנא מלאו את כל השדות הנדרשים");
            return;
        }
        try {
            int min = Integer.parseInt(minShiftsField.getText());
            int max = Integer.parseInt(maxShiftsField.getText());
            if (min > max) {
                showAlert("שגיאה!", "מינימום לא יכול להיות גדול ממקסימום");
                return;
            }
            selected.editWorker(name, role, shomerShabat, isPriority, min, max);
            WorkerManager.saveToJson();
            workerTable.refresh();
            showAlert("הצלחה!", "העובד נערך בהצלחה");
            System.out.println("Worker has been edited successfully");
        } catch (NumberFormatException e) {
            showAlert("שגיאה.", "אנא הזן מספרים חוקיים במינימום/מקסימום");
            System.out.println("Error can't edit worker");
        }
    }

    //Method to show given worker detailed in the relevant fields
    private void WorkerDetails(Worker worker) {
        nameField.setText(worker.getName());
        roleComboBox.setValue(worker.getRole());
        shomerShabatCheckBox.setSelected(worker.IsShomerShabat());
        highPriorityCheckBox.setSelected(worker.getPriority());
        minShiftsField.setText(String.valueOf(worker.getMinShifts()));
        maxShiftsField.setText(String.valueOf(worker.getMaxShifts()));
    }
}