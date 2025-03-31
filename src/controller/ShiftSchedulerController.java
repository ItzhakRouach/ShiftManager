package controller;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//Controller to handle Shift Scheduler fxml
public class ShiftSchedulerController {

    @FXML
    public GridPane summaryGrid;
    @FXML
    private Button editBtn;
    @FXML
    private GridPane scheduleGrid;
    private boolean isEditMode = false;
    @FXML
    private StackPane gridWrapper;
    @FXML
    private Button resetBtn;
    private Schedule schedule;
    private List<Worker> workers;


    @FXML
    public void initialize() {
        WorkerManager.loadFromJson();
        RequestManager.loadFromJson();
        workers = new ArrayList<>(WorkerManager.getWorkers());

        schedule = new Schedule();
        schedule.setWeekSchedule(workers);

        setupGridLayout();
        fillScheduleGrid();
        updateShiftSummary();

        resetBtn.setOnAction(e -> {
            schedule.clearSchedule();
            fillScheduleGrid();
        });

        editBtn.setOnAction(e->{
            isEditMode = !isEditMode;
            editBtn.setText(isEditMode ? "סיים עריכה" : "ערוך");
        });

    }

    private void setupGridLayout() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();
        scheduleGrid.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        scheduleGrid.setGridLinesVisible(true);
        scheduleGrid.setAlignment(Pos.CENTER);
        scheduleGrid.setHgap(1);
        scheduleGrid.setVgap(1);

        for (int i = 0; i <= 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 8);
            scheduleGrid.getColumnConstraints().add(col);
        }

        for (int i = 0; i <= 9; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 10);
            scheduleGrid.getRowConstraints().add(row);
        }

        String[] days = {"שבת", "שישי", "חמישי", "רביעי", "שלישי", "שני", "ראשון"};
        String[] shifts = {"בוקר", "צהריים", "לילה"};
        String[] roles = {"אחמ\"ש", "מוקדן", "מאבטח"};

        for (int col = 0; col < days.length; col++) {
            Label dayLabel = createLabel(days[col], "-fx-font-weight: bold; -fx-background-color: orange; -fx-font-size: 14px");
            scheduleGrid.add(dayLabel, days.length - col, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
        }

        for (int i = 0; i < shifts.length; i++) {
            for (int j = 0; j < roles.length; j++) {
                String roleText = shifts[i] + " - " + roles[j];
                Label roleLabel = createLabel(roleText, "-fx-font-weight: bold; -fx-background-color: lightgray; -fx-padding: 5;");
                scheduleGrid.add(roleLabel, 0, i * 3 + j + 1);
            }
        }
    }

    private void fillScheduleGrid() {
        String[] shifts = {"בוקר", "צהריים", "לילה"};
        String[] roles = {"אחמ\"ש", "מוקדן", "מאבטח"};
        Day[] days = {Day.SATURDAY, Day.FRIDAY, Day.THURSDAY, Day.WEDNESDAY, Day.TUESDAY, Day.MONDAY, Day.SUNDAY};

        for (int day = 0; day < days.length; day++) {
            for (int shiftIndex = 0; shiftIndex < shifts.length; shiftIndex++) {
                Color bgColor;
                switch (shiftIndex) {
                    case 0: bgColor = Color.LIGHTBLUE; break;
                    case 1: bgColor = Color.LIGHTYELLOW; break;
                    case 2: bgColor = Color.LIGHTGRAY; break;
                    default: bgColor = Color.WHITE;
                }

                for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                    Day dayEnum = days[day];
                    ShiftTime shiftEnum = ShiftTime.values()[shiftIndex];
                    Role roleEnum = Role.values()[roleIndex];

                    Shift shift = schedule.getShift(dayEnum, shiftEnum);
                    Worker worker = shift.getWorker(roleEnum);
                    String lable = shift.getLable(roleEnum);
                    String workerName = (worker != null) ? worker.getName() : lable;

                    Label cell = createLabel(workerName, "-fx-background-color: " + toHex(bgColor) + "; -fx-font-size:18px; -fx-font-weight: 700; -fx-font-family: 'Ariel';") ;
                    final int finalday = day;
                    final int finalShiftIndex = shiftIndex;
                    final int finalRoleIndex = roleIndex;
                    cell.setOnMouseClicked(e -> {
                        if (isEditMode){
                            showEditWorkCell(days[finalday],ShiftTime.values()[finalShiftIndex],Role.values()[finalRoleIndex]);
                        }
                    });

                    scheduleGrid.add(cell, days.length - day, shiftIndex * 3 + roleIndex + 1);
                }
            }
        }
    }

    private void updateShiftSummary() {
        summaryGrid.getChildren().clear();
        Map<Worker, Integer> shiftCounts = schedule.countAllShifts(workers);
        Label nameHeader = new Label("שם העובד");
        Label countHeader = new Label("כמות משמרות");
        nameHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-padding:15;");
        countHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-padding:15;");
        summaryGrid.add(nameHeader, 0, 0);
        summaryGrid.add(countHeader, 1, 0);

        int row = 1;
        for (Map.Entry<Worker, Integer> entry : shiftCounts.entrySet()) {
            Label nameLabel = new Label(entry.getKey().getName());
            Label countLabel = new Label(String.valueOf(entry.getValue()));

            nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding:8;");
            countLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding:8;");

            summaryGrid.add(nameLabel, 0, row);
            summaryGrid.add(countLabel, 1, row);
            row++;
        }
    }

    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        return label;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    private void showEditWorkCell(Day day , ShiftTime shift , Role role) {
        Shift targetShift = schedule.getShift(day, shift);
        Worker cuurentWorker = targetShift.getWorker(role);

        List<Worker> validWorkers = workers.stream().filter(w->schedule.canFillRole(w,role)).toList();

        ChoiceDialog<Worker> dialog = new ChoiceDialog<>();
        dialog.setTitle("ערוך משבצת עובד");
        dialog.setHeaderText("בחר עובד למשבצת: " + day + " - " + shift + " - " + role);
        dialog.getItems().addAll(validWorkers);
        dialog.setSelectedItem(null);

        DialogPane pane = dialog.getDialogPane();
        pane.setMinWidth(300);
        pane.setMaxWidth(300);
        pane.setPrefWidth(300);

        pane.setMinHeight(200);
        pane.setMaxHeight(200);
        pane.setPrefHeight(200);

        pane.setMinSize(300,200);
        pane.setMaxSize(300,200);
        pane.setPrefSize(300,200);


        dialog.showAndWait().ifPresent(selectedWorker -> {


            //make sure the worker can fill the necessary role
            if (selectedWorker.equals(cuurentWorker)) {
                showAlert("שגיאה", "העובד כבר שובץ למשבצת זו.");
                return;
            }
            if(!schedule.minRest(selectedWorker, targetShift)) {
                showAlert("שגיאה", "לעובד אין מספיק שעות מנוחה לפני/אחרי המשמרת.");
                return;
            }
            if (schedule.shomerShabatConstrain(selectedWorker, targetShift)) {
                showAlert("שגיאה", "העובד שומר שבת ואינו יכול לעבוד במשמרת זו.");
                return;
            }
            targetShift.unassignWorker(role);
            boolean assigned  = targetShift.assignWorker(role,selectedWorker);

            if (assigned){
                showAlert("הצלחה", "העובד שובץ בהצלחה!");
            }else{
                targetShift.assignWorker(role, cuurentWorker);
                showAlert("שגיאה", "השיבוץ נכשל.");
            }
            fillScheduleGrid();
            updateShiftSummary();
        });
    }
    public void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}