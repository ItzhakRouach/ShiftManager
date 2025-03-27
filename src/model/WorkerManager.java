package model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Class to handle all the workers.
public class WorkerManager {
    private static final Map<String , Worker> workers = new HashMap<>(); //create an hashmap to store each worker in it by his name and worker object
    private static final String FILE_PATH = "workers.json"; // create a file to save/load the workers from

    //Method to add worker into workers
    public static void addWorker(Worker worker){
        workers.put(worker.getName(), worker);
    }

    //Method to get all workers Object we store in it.
    public static Collection<Worker> getWorkers(){
        return workers.values();
    }

    //Method to clear all workers
    public static void clearWorkers(){
        workers.clear();
    }

    //Method to remove specific worker from the workers so far.
    public  static boolean removeWorker(Worker worker){
        return  workers.remove(worker.getName()) != null;
    }

    public static boolean removeWorkerByName(String name){
        return workers.remove(name) != null;
    }

    //Method to find worker by his name
    public static Worker findWorkerByName(String name){
        return workers.get(name);
    }

    //Method to store all workers that been added so far to json
    public static void saveToJson(){
        try(FileWriter w = new FileWriter(FILE_PATH)){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(workers,w);
            System.out.println("Worker has been saved successfully");
        }catch (IOException e){
            System.err.println("Failed to save workers to JSON: " +e.getMessage() );
        }
    }


    //method to load all the workers that have been saved.
    public static void loadFromJson(){
        try(FileReader r = new FileReader(FILE_PATH)){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Worker>>(){}.getType();
            Map<String , Worker> loaded = gson.fromJson(r,type);
            if (loaded != null){
                workers.clear();
                workers.putAll(loaded);
                System.out.println("Workers file has been loaded Successfully");
            }
        }catch (IOException e){
            System.err.println("No saved workers found , starting fresh. " +e.getMessage() );
        }
    }

}
