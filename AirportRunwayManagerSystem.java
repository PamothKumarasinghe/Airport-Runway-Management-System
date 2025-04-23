/**
 * Airport Runway Management System
 * 
 * This program simulates 10 airplanes trying to land or take off on 5 available runways.
 * Each airplane must request permission, use a runway, and then release it.
 * The program uses basic Java concurrency features to prevent multiple planes from using the same runway.
 */

 public class AirportRunwayManagerSystem {

    public static void main(String[] args) {
        RunwayManager manager = new RunwayManager(5); // 5 runways
        for (int i = 1; i <= 10; i++) {
            new Airplane("Airplane-" + i, manager).start();
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
     * @param numberOfRunways Number of runways at the airport
     */
    public RunwayManager(int numberOfRunways) {
        runways = new boolean[numberOfRunways]; // false means runway is free
    }

    /**
     * Tries to acquire a free runway. Waits if all are busy.
     * @return the index of the acquired runway
     */
    public synchronized int acquireRunway(String planeName) {
        while (true) {
            for (int i = 0; i < runways.length; i++) {
                if (!runways[i]) {
                    runways[i] = true;
                    System.out.println(planeName + " acquired Runway " + (i + 1));
                    return i;
                }
            }
            // If no runway is free, wait and retry
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Releases the runway so that other planes can use it.
     * @param runwayIndex The index of the runway to release
     */
    public synchronized void releaseRunway(int runwayIndex, String planeName) {
        runways[runwayIndex] = false;
        System.out.println(planeName + " released Runway " + (runwayIndex + 1));
        notifyAll(); // Notify waiting planes
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
     * @param name Name of the airplane
     * @param manager Shared runway manager
     */
    public Airplane(String name, RunwayManager manager) {
        this.name = name;
        this.manager = manager;
    }

    /**
     * Runs the airplane's landing/takeoff logic.
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
