package keyValueStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Worker extends Thread {

    // the worker's id
    private int id;
    // the worker's file name 
    private String filename = "worker";
    private Map<String, String> parameters = new HashMap<>();
    private File keyValPairs;

    // create the worker
    public Worker(int id) {
        this.id = id;
        this.filename = filename.concat(id + ".txt");
        this.keyValPairs = new File(filename);
    }

    @Override
    public void run() {
        while (true) {}
    }

    @Override
    public void start() {
        System.out.println("The worker #" + id + " started.");
        if (readFile()) {
            System.out.println("Searching data in file named \"" + filename + "\"");
        } else {
            System.out.println("The worker #" + id + " finishing due to file problems.");
            try {
                this.join(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.start();
    }

    private boolean readFile() {
        // check if file exists
        if (!(keyValPairs.exists())) {
            try {
                keyValPairs.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else { // else, read the values from file 
            try {
                BufferedReader br = new BufferedReader(new FileReader(keyValPairs));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] value = line.split("=");
                    if (value.length > 1) {
                        parameters.put(value[0], value[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
