

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
        while (true) {
            for (int i = 0; i < runways.length; i++) {
                if (!runways[i]) {
                    runways[i] = true;
                    System.out.println(planeName + " occupied the Runway " + (i + 1));
                    return i; //returns the index of the acquired runwy
                }
            }
            // If no runway is free, wait and retry
            try {
                System.out.println(planeName + " is waiting for a runway ");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Releases the runway so that other planes can use it.
     */
    public synchronized void releaseRunway(int runwayIndex, String planeName) {
        runways[runwayIndex] = false; //after releasing the runway - set it to false since it is free
        System.out.println(planeName + " has released the Runway " + (runwayIndex + 1));
        notifyAll(); // Notify waiting planes/ waiting threads
    }
}

/**
 * Simulates an airplane trying to land or take off.
 */
class Airplane extends Thread {
    private final String name;
    private final RunwayManager manager;

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
            Thread.sleep((int) (Math.random() * 3000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        manager.releaseRunway(runwayIndex, name);
    }
}
