package model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.*;


// Class to handle all the logic that make the weekly schedule.
public class Schedule {
    private List<Shift> shifts; // shifts of typle Shift to hold all the shifts.


    /*
        create a new arrayList to hold all the shifts, and set with
        days and shift time to each day.
     */
    public Schedule(){
        this.shifts = new ArrayList<>();
        for (Day day : Day.values()){
            for (ShiftTime time : ShiftTime.values()){
                shifts.add(new Shift(day,time));
            }
        }
    }
    /*
    Method to assign workers shifts by their request.
        logic: 1. make sure that worker gets shifts only if it's free, and they can fill the role.
               2. create a list of workers with priority and one without that way it will first fill the ones with
                  priority and only then the ones without,
               3. check if there are more than 3 people that requested the same day, and if there is so it starts by filling the ones with
                higher priority and only after the ones without a priority.
     */
    public void assignWorkerByRequests(List<Worker> workers){
        for(Shift shift : shifts){
            for (Role role : Role.values()) {
                if (shift.getWorker(role) != null) continue;

                // שלב 1: חיפוש כל המועמדים שמתאימים לתפקיד ולמשמרת
                List<Worker> priorityCandidates = new ArrayList<>();
                List<Worker> normalCandidates = new ArrayList<>();

                for (Worker w : workers) {
                    for (Request r : w.getRequest()) {
                        if (!r.isDayOff()
                                && r.getDay() == shift.getDay()
                                && r.getShift() == shift.getTime()
                                && canFillRole(w, role)
                                && !shomerShabatConstrain(w, shift)
                                && !alreadyAssignedTooMuch(w, shift)
                                && minRest(w, shift)
                                && canAssignMoreShifts(w)) {

                            if (w.getPriority()) {
                                priorityCandidates.add(w);
                            } else {
                                normalCandidates.add(w);
                            }
                            break;
                        }
                    }
                }
                List<Worker> allCandidates = new ArrayList<>();
                allCandidates.addAll(priorityCandidates);
                allCandidates.addAll(normalCandidates);

                for (Worker chosen : allCandidates) {
                    if (shift.getWorker(role) == null) {
                        shift.assignWorker(role, chosen);
                    }
                }
            }
        }
    }

    //Method to return shift with all workers that's assign to it by day and time of the shift
    public Shift getShift(Day day, ShiftTime time) {
        for (Shift shift : shifts) {
            if (shift.getDay() == day && shift.getTime() == time) {
                return shift;
            }
        }
        return null; // לא נמצא
    }


    /*
        Method that check if worker work 1 or max 2 shift the same day if 2 then make sure that there is 2shift space between
        meaning allow only morning and nigh on the same day.
    */
    private boolean alreadyAssignedTooMuch(Worker worker, Shift targetShift) {
        List<ShiftTime> assignedTimes = new ArrayList<>();

        for (Shift shift : shifts) {
            if (shift.getDay() == targetShift.getDay()
                    && shift.getAssignedWorkers().containsValue(worker)) {
                assignedTimes.add(shift.getTime());
            }
        }

        if (assignedTimes.isEmpty()) return false;
        if (assignedTimes.size() >= 2) return true;

        ShiftTime existing = assignedTimes.get(0);
        ShiftTime incoming = targetShift.getTime();

        return !(existing == ShiftTime.MORNING && incoming == ShiftTime.NIGHT)
                && !(existing == ShiftTime.NIGHT && incoming == ShiftTime.MORNING);
    }


    /*
    Method that check if worker has the minimum rest between his working shifts
    while minimum rest meaning a shift free between his shifts (8-hours).

*/
    private boolean minRest(Worker worker, Shift targetShift) {
        Day targetDay = targetShift.getDay();
        ShiftTime targetTime = targetShift.getTime();

        for (Shift s : shifts) {
            if (!s.getAssignedWorkers().containsValue(worker)) continue;

            Day day = s.getDay();
            ShiftTime time = s.getTime();

            //make sure that no one will work shift after shift make sure that there are 8 hours free between given shifts.
            if (day == targetDay) {
                if ((time == ShiftTime.MORNING && targetTime == ShiftTime.AFTERNOON) ||
                        (time == ShiftTime.AFTERNOON && targetTime == ShiftTime.NIGHT) ||
                        (time == ShiftTime.NIGHT && targetTime == ShiftTime.AFTERNOON) ||
                        (time == ShiftTime.AFTERNOON && targetTime == ShiftTime.MORNING)) {
                    return false;
                }
            }

            // check if prev day when the worker work is night, and target shift day is morning if yes than return false.
            if (isPreviousDay(day, targetDay) &&
                    time == ShiftTime.NIGHT &&
                    targetTime == ShiftTime.MORNING) {
                return false;
            }

            // Check the other way
            if (isPreviousDay(targetDay, day) &&
                    targetTime == ShiftTime.NIGHT &&
                    time == ShiftTime.MORNING) {
                return false;
            }
        }

        return true; //if all conditions meet, then return true meaning worker can be assigned.
    }


    /*
     Method to check if the day current is the previous day.
     */
    private boolean isPreviousDay(Day current , Day target){
        return (current.ordinal()+1) % 7 == target.ordinal();
    }




    /*
     Method to fill all the empty shifts after given the worker's shifts by their requests.
     and also assign workers by if they get the least shifts that way make sure the shifts are distributed equally by the workers
 */
    public void fillShifts(List<Worker> workers){
        for (Shift shift : shifts){
            for (Role role : Role.values()){
                if (shift.getWorker(role) != null){
                    continue;
                }
                List<Worker> candidates = new ArrayList<>();

                for (Worker w : workers){
                    if (canFillRole(w,role) && !alreadyAssignedTooMuch(w,shift)
                        && minRest(w,shift)
                        && !shomerShabatConstrain(w,shift)
                        && canAssignMoreShifts(w)){
                        candidates.add(w);
                    }
                }
                Worker chosen = findLeastAssigned(candidates);
                if (chosen != null){
                    shift.assignWorker(role,chosen);
                }
            }
        }
    }



    /*
      Method to check if the worker is shomer-shabat and if it does then use the constraints that the worker have
      when assigned shifts to him
  */
    private boolean shomerShabatConstrain(Worker w, Shift shift) {
        return w.IsShomerShabat() && (
                (shift.getDay() == Day.FRIDAY && (shift.getTime() == ShiftTime.AFTERNOON || shift.getTime() == ShiftTime.NIGHT)) ||
                        (shift.getDay() == Day.SATURDAY && (shift.getTime() == ShiftTime.MORNING || shift.getTime() == ShiftTime.AFTERNOON))
        );
    }



    /*
    Method to check if a worker can fill the role, if it does then return true else false.
    also check that if its Shift manager he can fill all the roles
    and if Dispatcher he can also fill the guard role.
     */
    private boolean canFillRole(Worker w, Role neededRole) {
        Role actual = w.getRole();

        if (actual == neededRole) return true;
        if (neededRole == Role.GUARD && (actual == Role.DISPATCHER || actual == Role.SHIFT_MANAGER)) return true;
        if (neededRole == Role.DISPATCHER && actual == Role.SHIFT_MANAGER) return true;

        return false;
    }



    /*
       Method to count how many shift workers are assigned to
     */
    public int countShifts(Worker worker) {
        int count = 0;
        for (Shift s : shifts) {
            if (s.getAssignedWorkers().containsValue(worker)) {
                count++;
            }
        }
        return count;
    }


    /*
        Method to find worker that have the fewest shifts and
     */
    private Worker findLeastAssigned(List<Worker> candidates) {
        if (candidates.isEmpty()) return null;

        Worker least = candidates.get(0);
        int min = countShifts(least);

        for (Worker w : candidates) {
            int shifts = countShifts(w);
            if (shifts < min) {
                min = shifts;
                least = w;
            }
        }
        return least;
    }

    //Method to check if a worker can be assigned to shifts by checking if they reach their max shifts.
    private boolean canAssignMoreShifts(Worker w) {
        return countShifts(w) < w.getMaxShifts();
    }


    /*
        Method that will be used to check after all the shifts been assigned, will check who has the fewest shifts and assigned them more
        by replacing thous who reach their maximum, and if you can fill the role, then he will get the shift instead of the one with the max shifts.
        */

    public void fillMinShifts(List<Worker> workers) {
        for (Worker w : workers) {
            while (countShifts(w) < w.getMinShifts()) {
                boolean assigned = false;

                // try to fill an empty shifts
                for (Shift shift : shifts) {
                    if (alreadyAssignedTooMuch(w, shift) || !minRest(w, shift) || shomerShabatConstrain(w, shift) || !canAssignMoreShifts(w))
                        continue;

                    for (Role role : Role.values()) {
                        if (shift.getWorker(role) == null && canFillRole(w, role)) {
                            shift.assignWorker(role, w);
                            w.addAssignedShift(shift);
                            assigned = true;
                            break;
                        }
                    }

                    if (assigned) break;
                }

                // try switch with someone other
                if (!assigned) {
                    for (Shift shift : shifts) {
                        for (Role role : Role.values()) {
                            Worker swappedWith = trySwapShift(w, shift, role);
                            if (swappedWith != null) {
                                assigned = true;
                                System.out.println("↪️ Swapped " + swappedWith.getName() + " with " + w.getName() +
                                        " on " + shift.getDay() + " " + shift.getTime());
                                break;
                            }
                        }
                        if (assigned) break;
                    }
                }
            }
        }
    }


    //Method that try to swap between shifts if available.
    private Worker trySwapShift(Worker target, Shift shift, Role role) {
        Worker current = shift.getWorker(role);
        if (current == null) return null;

        // check the currrent worker in the shift we want to swap with
        if (countShifts(current) <= current.getMinShifts()) return null;

        //check if the target worker can fill the shift
        if (alreadyAssignedTooMuch(target, shift)) return null;
        if (!minRest(target, shift)) return null;
        if (shomerShabatConstrain(target, shift)) return null;
        if (!canAssignMoreShifts(target)) return null;
        if (!canFillRole(target, role)) return null;

        // try to unassign current worker from the shift, and see if you will be valid
        shift.unassignWorker(role);

        boolean currentStillValid = minRest(current, shift);

        if (!currentStillValid) {
            // מחזיר את הנוכחי אם ההוצאה תגרום להפרת מנוחה
            shift.assignWorker(role, current);
            return null;
        }

        // assign the target worker to the shift that we swapped
        shift.assignWorker(role, target);
        return current;
    }



    //Method to clear the schedule that has been made.
    public void clearSchedule() {
        for (Shift shift : shifts) {
            shift.clearAllWorkers();
        }
    }

}
