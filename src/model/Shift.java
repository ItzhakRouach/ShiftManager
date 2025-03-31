/*
    Class to handle shifts ,as who's Worekrs going to be in that shift.
 */



package model;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;

//Each shift we have DAY, TIME (MORNING,EVENING,NIGHT) And workers that assign to it.
public class Shift {
    private  Day day;
    private ShiftTime time;
    private Map<Role, Worker> assignedWorkers;
    private Map<Role , String> lables;



    /*
    Constructor to set model.Shift :
    day = the day of the shift.
    time = which time the shift is : morning / afternoon / night
    assignedWorker = Create map to assign worker to each role , enum map is used beacuse model.Role is enum and its faster and use less memory.
     */
    public Shift(Day day , ShiftTime time){
        this.day = day;
        this.time = time;
        this.assignedWorkers = new EnumMap<>(Role.class);
        this.lables = new HashMap<>();
    }

    /*
    method that retuen boolean true/false .
    args1 = Role
    args2 = Worker
    check if the role in the shift is free means no one ocupied the role , then assign worker that fit to that role .
    else return false (meaning someone allready assign to it)
     */
    public boolean assignWorker(Role role , Worker worker){
        Worker current = assignedWorkers.get(role);
        if (current != null && current.equals(worker)) {
            return false; // No change
        }
        assignedWorkers.put(role, worker);
        return true;
    }
    /*
    Return worker that assign to the given role
     */
    public Worker getWorker(Role role){
        return assignedWorkers.get(role);
    }


    //return model.Day enum , the day of the shift.
    public Day getDay(){
        return day;
    }


    //reTURN model.ShiftTime enum , the time of the shift
    public ShiftTime getTime(){
        return time;
    }


    // return Map of role and Worker(model.Role is enum , Worker of class Work)
    // ,return the assigned Workers of the shift in map .
    public Map<Role, Worker> getAssignedWorkers(){
        return assignedWorkers;
    }

    //This method remove worker from the role he fills in a shift
    public void unassignWorker(Role role) {
        assignedWorkers.remove(role);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(day).append(" ").append(time).append(":\n");
        for(Role role : Role.values()){
            Worker w = assignedWorkers.get(role);
            sb.append("  ").append(role).append(": ").append(w != null ? w.getName() : "Unassigned").append("\n");
        }
        return sb.toString();
    }

    //This method used for clear all the workers shifts .
    public void clearAllWorkers() {
        for (Role role : Role.values()) {
            assignedWorkers.put(role, null);
        }
    }

    public void setLables(Role role ,String lable){
        lables.put(role,lable);
    }
    public String getLable(Role role){
        return lables.getOrDefault(role,"");
    }


}
