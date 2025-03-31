/*
    Class to handle Worker Store  Worker Request.
 */

package model;
import model.*;
public class Request {
    private  Worker worker;  //Object of class Woker
    private  Day day; //Which Day the request will be
    private  ShiftTime shift; // Which time of the day the request is for
    private  boolean isDayOff; //Check if whatever or not the request is dayOff.
    private  boolean isTrainingDay;
    private  boolean mustDayOff;

    /*
    Constructor that get model.Day,Time of the shift(morning,afternoon,night) and whatever its day off (true/false).
     */
    public Request(Worker worker,Day day , ShiftTime shift , boolean isDayOff, boolean isTrainingDay,boolean mustDayOff){
        this.worker = worker;
        this.day = day;
        this.shift = shift;
        this.isDayOff = isDayOff;
        this.isTrainingDay = isTrainingDay;
        this.mustDayOff = mustDayOff;
    }

    //Getter To return Worker
    public Worker getWorker(){
        return worker;
    }

    //Geter to get the day
    public Day getDay() {
        return day;
    }

    //Geter to get if is day off request
    public boolean isDayOff(){
        return isDayOff;
    }

    public boolean isTrainingDay(){return isTrainingDay ; }

    public boolean isMustDayOff() {
        return mustDayOff;
    }



    //Return the time of the shift (morning,afternoon,night)
    public ShiftTime getShift(){
        return shift;
    }

    @Override
    public String toString(){
        if (isDayOff) {
            return "request day off on " + day;
        }
        if(isTrainingDay){
            return  "Has training day on" + day;
        }
        else{
            return "model.Request to work on " + day + " " + shift;
        }
    }
}
