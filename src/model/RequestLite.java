package model;


public class RequestLite {
    private String workerName;
    private Day day;
    private ShiftTime shift;
    private boolean isDayOff;

    public RequestLite(String workerName, Day day, ShiftTime shift, boolean isDayOff) {
        this.workerName = workerName;
        this.day = day;
        this.shift = shift;
        this.isDayOff = isDayOff;
    }
    public Day getDay() {
        return day;
    }

    public ShiftTime getShift() {
        return shift;
    }

    public boolean isDayOff() {
        return isDayOff;
    }
}