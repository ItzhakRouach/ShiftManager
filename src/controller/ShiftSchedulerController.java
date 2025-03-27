package controller;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.*;
import java.util.ArrayList;
import java.util.List;


//Controller to handle Shift Scheduler fxml
public class ShiftSchedulerController {

    @FXML
    private GridPane scheduleGrid;

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
        schedule.assignWorkerByRequests(workers);
        schedule.fillShifts(workers);
        schedule.fillMinShifts(workers);

        setupGridLayout();
        fillScheduleGrid();

        resetBtn.setOnAction(e -> {
            schedule.clearSchedule();
            fillScheduleGrid();
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
                    String workerName = (worker != null) ? worker.getName() : "";

                    Label cell = createLabel(workerName, "-fx-background-color: " + toHex(bgColor) + "; -fx-font-size:18px; -fx-font-weight: 700; -fx-font-family: 'Ariel';") ;
                    scheduleGrid.add(cell, days.length - day, shiftIndex * 3 + roleIndex + 1);
                }
            }
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
}