package traffic;

import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int numOfRoads = 0;
    static int interval = 0;
    static ArrayDeque<TrafficLight> trafficLightQueue; // Circular Queue
    private static volatile boolean inSystem = false; // Flag to control system mode
    static final String WELCOME = "Welcome to the traffic management system!";
    static final String MENU = """
                            Menu:
                            1. Add road
                            2. Delete road
                            3. Open System
                            0. Quit
                            """;

    public static void main(String[] args) {
        System.out.println(WELCOME);
        numOfRoads = readInput("Input the number of roads: ");
        interval = readInput("Input the interval: ");
        trafficLightQueue = new ArrayDeque<>(numOfRoads);
        Thread queueThread = new Thread(() -> {
            int seconds = 0;
            try {
                while (true) {
                    if (inSystem) {
                        System.out.printf("""
                        ! %ds. have passed since system startup !
                        ! Number of roads: %d !
                        ! Interval: %d !
                        
                        %s
                        
                        ! Press Enter to open menu or enter any input to stop !
                        """, seconds, numOfRoads, interval, listRoads());
                    }
                    seconds++;
                    lightSwitch();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // Thread interrupted, break out of the loop
            }
        });
        queueThread.setName("QueueThread");
        queueThread.start();

        readMenuOption();

        queueThread.interrupt(); // Kill created thread

        // Wait for the queue thread to finish before exiting
        try {
            queueThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int readInput(String prompt) {
        int value;
        System.out.print(prompt);
        do {
            try {
                value = scanner.nextInt();
                if (value <= 0) {
                    System.out.print("Error! Incorrect Input. Try again: ");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.print("Error! Incorrect Input. Try again: ");
                scanner.nextLine();
            }
        } while (true);
        return value;
    }

    private static void readMenuOption() {
        boolean exit = false;
        while (!exit) {
            inSystem = false;
            System.out.print(MENU);
            try {
                int menuOption = scanner.nextInt();

                switch (menuOption) {
                    case 0   -> {
                        System.out.println("Bye!");
                        exit = true;
                        scanner.close();
                    } case 1 -> {
                        addRoad();
                        scanner.nextLine(); // Consume the newline character
                        scanner.nextLine();
                    } case 2 -> {
                        deleteRoad();
                        scanner.nextLine();
                        scanner.nextLine();
                    } case 3 -> {
                        inSystem = true;
                        System.out.println("System opened");
                        scanner.nextLine();
                    } default -> {
                        System.out.println("Incorrect option.");
                        scanner.nextLine();
                        scanner.nextLine();
                    }
                }
            } catch (Exception e) {
                System.out.println("Incorrect option.");
                scanner.nextLine();
                scanner.nextLine();
            }

            if (inSystem) {
                scanner.nextLine();
                inSystem = false;
            }
        }
    }

    public static void lightSwitch() {
        int roadAmount = trafficLightQueue.size();
        int maxTimer = interval * (roadAmount - 1);
        trafficLightQueue.forEach(
                road -> {
                    int timer = road.getTimer();
                    boolean isOpen = road.isOpen();
                    if (timer == 1) {
                        if (roadAmount > 1){
                            road.setOpen(!isOpen);
                        }
                        road.setTimer(isOpen && roadAmount > 1 ? interval * (roadAmount - 1) : interval);
                    } else if (timer > maxTimer && roadAmount > 1) {
                        road.setTimer(maxTimer - 1);
                    } else {
                        road.setTimer(--timer);
                    }
                });
    }

    public static void addRoad() {
        System.out.print("Input the road name: ");
        String roadToAdd = scanner.next();
        int roadAmount = trafficLightQueue.size();
        if (roadAmount != numOfRoads) {
            if (roadAmount == 0) {
                trafficLightQueue.offer(new TrafficLight(roadToAdd, true, interval));
            } else {
                int tailTime = trafficLightQueue.peekLast().getTimer();
                if (roadAmount == 1) {
                    trafficLightQueue.offer(new TrafficLight(roadToAdd, false, tailTime));
                } else {
                    trafficLightQueue.offer(new TrafficLight(roadToAdd, false, tailTime + interval));
                }
            }
            System.out.println(roadToAdd + " added!");
        } else {
            System.out.println("Queue is full");
        }
    }

    public static void deleteRoad() {
        if (!trafficLightQueue.isEmpty()) {
            String roadToDelete = trafficLightQueue.poll().getRoad();
            System.out.println(roadToDelete + " deleted!");
        } else {
            System.out.println("Queue is empty");
        }
    }

    public static String listRoads() {
        return trafficLightQueue.stream()
                .map((road) -> road.getRoad() + " will be "
                        + (road.isOpen() ? ("\u001B[32m" +  "open") : ("\u001B[31m" + "closed"))
                        + String.format(" for %ds.\u001B[0m", road.getTimer()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}