import java.util.*;

public class AirportSimulation {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Amount of minutes to land: ");
        int LAND_TIME = sc.nextInt();

        System.out.print("Amount of minutes to take off: ");
        int TAKE_OFF_TIME = sc.nextInt();

        System.out.print("Average amount of time between planes to land: ");
        double avgLand = sc.nextDouble();

        System.out.print("Average amount of time between planes to take off: ");
        double avgTake = sc.nextDouble();

        System.out.print("Maximum amount of time in the air before crashing: ");
        int MAX_AIRTIME = sc.nextInt();

        System.out.print("Total simulation minutes: ");
        int TOTAL_SIM_MIN = sc.nextInt();

        // Convert averages to probability per minute
        double ARRIVAL_PROBABILITY = 1.0 / avgLand;
        double DEPARTURE_PROBABILITY = 1.0 / avgTake;

        // Create two queue
        Queue<Integer> LandingQueue = new ArrayDeque<>();
        Queue<Integer> TakeOffQueue = new ArrayDeque<>();

        int totalCrashed = 0;
        int noOfPlanesTakenOff = 0;
        int noOfPlanesLanded = 0;

        int totallandingWait = 0;
        int totalTakeOfWait = 0;

        int runwayRemaining = 0;    // minutes left for current operation
        int runwayMode = 0;        // 0=idle, 1=landing, 2=takeoff
        Random rand = new Random();

        for (int t = 0; t < TOTAL_SIM_MIN; t++) {

           // Random arrivals / departures
           if (rand.nextDouble() < ARRIVAL_PROBABILITY) LandingQueue.add(t);
           if (rand.nextDouble() < DEPARTURE_PROBABILITY) TakeOffQueue.add(t);

           if(runwayRemaining>0) {
                runwayRemaining--;
                if (runwayRemaining == 0) {
                    if (runwayMode == 1) {
                        noOfPlanesLanded++;
                    } else if (runwayMode == 2) {
                        noOfPlanesTakenOff++;
                    }
                        runwayMode = 0;
                }
           }

            // If runway is free, start a new operation (landing priority)
            if (runwayRemaining == 0) {

                // Remove crashed landing planes
                while (!LandingQueue.isEmpty() && (t - LandingQueue.peek()) > MAX_AIRTIME) {
                 LandingQueue.poll();
                 totalCrashed++;
                }
                if (!LandingQueue.isEmpty()) {
                    int runwayEnterTime = LandingQueue.poll();
                    totallandingWait += (t - runwayEnterTime);
                    runwayMode = 1;
                    runwayRemaining = LAND_TIME;     // runway busy for Landing time
                } else if (!TakeOffQueue.isEmpty()) {

                    int runwayEnterTime = TakeOffQueue.poll();
                    totalTakeOfWait += (t - runwayEnterTime);
                    runwayMode = 2;
                    runwayRemaining = TAKE_OFF_TIME;

                } else {
                    runwayMode = 0;
                }
            }
        }
        // Final crash check for remaining landing planes
        while (!LandingQueue.isEmpty()) {
          int enterTime = LandingQueue.poll();
          if ((TOTAL_SIM_MIN - enterTime) > MAX_AIRTIME) totalCrashed++;
        }

        double avgTakeOffTime = (noOfPlanesTakenOff==0) ? 0.0: (double) totalTakeOfWait/noOfPlanesTakenOff;
        double avgLandingTime = (noOfPlanesLanded == 0) ? 0.0:(double) totallandingWait/noOfPlanesLanded;

        System.out.println("\n--- Simulation Result ---");
        System.out.println("Number of planes taken off: " + noOfPlanesTakenOff);
        System.out.println("Number of planes landed: " + noOfPlanesLanded);
        System.out.println("Number of planes crashed: " + totalCrashed);
        System.out.printf("Average waiting time for taking off: %.2f minutes%n", avgTakeOffTime);
        System.out.printf("Average waiting time for landing: %.2f minutes%n", avgLandingTime);

        sc.close();
    }
}