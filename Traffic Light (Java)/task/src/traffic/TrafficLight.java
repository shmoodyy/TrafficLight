package traffic;

public class TrafficLight {
    private String road;
    private boolean isOpen;
    private int timer;

    public TrafficLight(String road, boolean isOpen, int timer) {
        this.road = road;
        this.isOpen = isOpen;
        this.timer = timer;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}