/*
    This class is for handle worker data as his name , role  and so on
    also the request that the worker have
    and the shifts that the worker will get.
 */


package model;
import java.util.ArrayList;
import java.util.List;

public class Worker {
   private String name; //User Name
   private Role role; //User model.Role: ShiftManager/Dispatcher/Guard
   private boolean ShomerShabat; // User Shomer shabat or not
   private boolean highpriority; // User requests must be
   private int minShifts; // Min shift user need to have
   private int maxShifts; // Max shift user can  have
   private List<Request> request; // Create List of model.Request type
    private List<Shift> assignedShifts; // List of the shifts that worker got assign to

   public Worker(String name , Role role , boolean ShomerShabat , boolean highpriority , int minShifts , int maxShifts){
       this.name = name;
       this.role = role;
       this.ShomerShabat = ShomerShabat;
       this.highpriority = highpriority;
       this.minShifts = minShifts;
       this.maxShifts = maxShifts;
       this.request = new ArrayList<>();
       this.assignedShifts = new ArrayList<>();
   }

   //method to let user edit Worker name
   public void setName(String name){
       this.name = name;
   }

    //method to let user edit Worker role
   public void setRole(Role role){
       this.role = role;
   }

    //method to let user edit Worker Religious
   public void setShomerShabat(boolean shomerShabat){
       this.ShomerShabat = shomerShabat;
   }

    //method to let user edit Worker priority
   public void setHighpriority(boolean highpriority){
       this.highpriority = highpriority;
   }
    //method to let user edit Worker min shifts
   public void setMinShifts(int min){
       this.minShifts = min;
   }

    //method to let user edit Worker max shifts
    public void setMaxShifts(int max){
        this.maxShifts = max;
    }

   //Return the name of the worker
    public String getName() {
        return name;
    }

    //Return true/false if the worker is high priority or not.
    public boolean getPriority() {
        return highpriority;
    }

    //return max shifts the worker can have
    public int getMaxShifts() {
        return maxShifts;
    }

    //return min shifts the worker must have
    public int getMinShifts() {
        return minShifts;
    }

    //return Worker role
    public Role getRole(){
       return role;
    }

    //return whatever the worker shomer shabat or not
    public boolean IsShomerShabat(){
       return ShomerShabat;
    }

    // method to add  Worker request o into his requests list.
    public void addRequest(Request request){
       this.request.add(request);
    }

    //Return all the Worker requests
    public List<Request> getRequest(){
       return request;
    }

    //Method to add assign shift to Worker shifts list.
    public void addAssignedShift(Shift shift) {
        if (!assignedShifts.contains(shift)) {
            assignedShifts.add(shift);
        }
    }

    // method to remove shift that we want to from the Worker.
    public void removeAssignedShift(Shift shift) {
        assignedShifts.remove(shift);
    }

    // Return all shifts the worker assign to
    public List<Shift> getAssignedShifts() {
        return assignedShifts;
    }
    // remove worker request
    public void removeRequest(Request r) {
        request.remove(r);
    }

    // clear all Worker requests
    public void clearRequests() {
        request.clear();
    }
    @Override
    public String toString(){
       return name + " (" + role + ")";
    }

    public void editWorker(String name ,  Role role , boolean ShomerShabat , boolean highpriority , int minShifts , int maxShifts){
       this.name = name;
       this.role = role;
       this.ShomerShabat = ShomerShabat;
       this.highpriority = highpriority;
       this.minShifts = minShifts;
       this.maxShifts = maxShifts;
    }

}

