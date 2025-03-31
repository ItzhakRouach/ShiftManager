package model;


public class RequestLite {
    private String workerName;
    private Day day;
    private ShiftTime shift;
    private boolean isDayOff;
    private boolean isTrainingDay;
    private boolean mustDayOff;

    public RequestLite(String workerName, Day day, ShiftTime shift, boolean isDayOff , boolean isTrainingDay , boolean mustDayOff) {
        this.workerName = workerName;
        this.day = day;
        this.shift = shift;
        this.isDayOff = isDayOff;
        this.isTrainingDay = isTrainingDay;
        this.mustDayOff = mustDayOff;
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

    public boolean isTrainingDay(){ return  isTrainingDay;}

    public boolean isMustDayOff() {return mustDayOff;}
}