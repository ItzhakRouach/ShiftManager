package model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

//Class to handle all workers requests.

public class RequestManager {
    private static final Map<String, List<Request>> requestsMap = new HashMap<>(); //create an hash map that hold all workers requests
    private static final String REQUESTS_FILE = "requests.json"; //create an file to store all the requests.


    //Method to add worker request into his place in requestMap.
    public static void addRequest(String workerName , Request request){
        requestsMap.computeIfAbsent(workerName,k -> new ArrayList<>()).add(request);
    }


    //return all requests of a Worker by using his name.
    public static List<Request> getRequestsForWorker(String workerName) {
        return requestsMap.getOrDefault(workerName, new ArrayList<>());
    }

    public static Request getRequestFor(Worker worker, Day day, ShiftTime shift) {
        List<Request> requests = requestsMap.get(worker.getName());
        if (requests == null) return null;

        for (Request r : requests) {
            if (r.getDay() == day && r.getShift() == shift) {
                return r;
            }
        }
        return null;
    }




    //RETURN A COPY of all requests .
    public static Map<String, List<Request>> getAllRequests() {
        return new HashMap<>(requestsMap); // return a copy
    }

    //Method to save the requests into json format that way User can save the requests so far and if closed the program the
    //Requests will be saved.
    public static void saveToJson() {
        try (FileWriter writer = new FileWriter(REQUESTS_FILE)) {
            Gson gson = new Gson();
            Map<String, List<RequestLite>> toSave = new HashMap<>();

            for (Map.Entry<String, List<Request>> entry : requestsMap.entrySet()) {
                List<RequestLite> liteList = new ArrayList<>();
                for (Request req : entry.getValue()) {
                    liteList.add(new RequestLite(
                            entry.getKey(), // שם העובד
                            req.getDay(),
                            req.getShift(),
                            req.isDayOff(),
                            req.isTrainingDay(),
                            req.isMustDayOff()
                    ));
                }
                toSave.put(entry.getKey(), liteList);
            }
            gson.toJson(toSave, writer);
        } catch (IOException e) {
            System.err.println("❌ Failed to save requests: " + e.getMessage());
        }
    }

    //Method to clear all requests that has been so far in requestMap
    //Will be used in "נקה כל הבקשות  " button.
    public static void  clearAllRequests(){
        requestsMap.clear();
        for (Worker w: WorkerManager.getWorkers()){
            w.getRequest().clear();
        }
        saveToJson();
    }


    //Method to load the requests thats we save so far when then the program load,
    public static void loadFromJson() {
        try (FileReader reader = new FileReader(REQUESTS_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<RequestLite>>>() {}.getType();
            Map<String, List<RequestLite>> loaded = gson.fromJson(reader, type);

            if (loaded != null) {
                requestsMap.clear();
                for (Map.Entry<String, List<RequestLite>> entry : loaded.entrySet()) {
                    List<Request> requestList = new ArrayList<>();
                    Worker worker = WorkerManager.findWorkerByName(entry.getKey());
                    if (worker != null) {
                        for (RequestLite lite : entry.getValue()) {
                            Request req = new Request(worker, lite.getDay(), lite.getShift(), lite.isDayOff() , lite.isTrainingDay() , lite.isMustDayOff());
                            requestList.add(req);
                            worker.addRequest(req); // אם יש לך את זה
                        }
                    }
                    requestsMap.put(entry.getKey(), requestList);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ No saved requests found, starting fresh: " + e.getMessage());
        }
    }



}
