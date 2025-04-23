

/**  ------------------------------------------------------------Airport Runway Manager System-------------------------------------------------------------
 * Dining philosophers problem
 * Limited number of runaways - 5
 * airplanes - 10 - 10 threads
 * Multiple planes arrive - depart
 * Each plane request permission to use runway - if granted can use the runway - else hv to wait.
 * Then release the runway
 * Starvation? - no starvation? - all planes will hv to use the runway eventually.
 * No crashes of planes
 */
 
 import java.util.logging.*;

 public class AirportRunwayManagerSystem {

    public static void main(String[] args) {
        RunwayManager manager = new RunwayManager(5); // since there are 5 runways
        
        for (int i = 1; i < 10 + 1; i++) { // creating 10 airplanes
            new Airplane("Airplane - " + i, manager).start();
        }
    }
}

/**
 * Manages access to a limited number of runways using a simple array lock mechanism.
 */
class RunwayManager {
    private final boolean[] runways;
    private static final Logger logger = Logger.getLogger(RunwayManager.class.getName());

    /**
     * Constructor to initialize the runway availability.
     */
    public RunwayManager(int numberOfRunways) { //number of runways
        // at the beginning all the runways are available - boolean[] initialized to false
        runways = new boolean[numberOfRunways]; // if the runway is free - then false - else true

    }

    /**
     * Trying to acquire a free runway but Waits if all are allocated by planes
     */
    public synchronized int acquireRunway(String planeName) {
        logger.info(planeName + " is requesting a runway...");
        while (true) {
            
            for (int i = 0; i < runways.length; i++) {
                if (!runways[i]) {
                    runways[i] = true;
                    logger.info(planeName + " has acquired Runway " + (i + 1));
                    
                    return i; //returns the index of the acquired runwy
                }
            }
            // If no runway is free, wait and retry
            try {
                wait();
            } catch (InterruptedException e) {
                logger.warning("Interrupted while waiting for a runway: " + e.getMessage());
            }
        }
    }

    /**
     * Releases the runway so that other planes can use it.
     */
    public synchronized void releaseRunway(int runwayIndex, String planeName) {
        runways[runwayIndex] = false; //after releasing the runway - set it to false since it is free
        logger.info(planeName + " has released Runway " + (runwayIndex + 1));
        notifyAll(); // Notify waiting planes/ waiting threads
    }
}

/**
 * Simulates an airplane trying to land or take off.
 */
class Airplane extends Thread {
    private final String name;
    private final RunwayManager manager;
    private static final Logger logger = Logger.getLogger(Airplane.class.getName());

    /**
     * Constructor to set plane name and manager.
     */
    public Airplane(String name, RunwayManager manager) { // 'name' of the airplane, 'manager' is the runway manager
        this.name = name;
        this.manager = manager;
    }

    /**
     * Runs the airplane's landing/ takeoff logic.
     */
    @Override
    public void run() {
        System.out.println(name + " is requesting a runway...");
        int runwayIndex = manager.acquireRunway(name);

        try {
            // Simulate time taken to use the runway
            logger.info(name + " is using Runway " + (runwayIndex + 1));
            Thread.sleep((int) (Math.random() * 3000) + 1000);
        } catch (InterruptedException e) {
            logger.warning(name + " was interrupted during operation: " + e.getMessage());
        }

        manager.releaseRunway(runwayIndex, name);
    }
}
