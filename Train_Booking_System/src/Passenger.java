public class Passenger {
    private String name;
    private String seat;
    private int secondsInQueue;
   // getters

    public int getSecondsInQueue() {
        return secondsInQueue;
    }

    public String getName() {
        return name;
    }

    public String getSeat() {
        return seat;
    }


    // Setter
    public void setSecondsInQueue(int seconds) {
        this.secondsInQueue = seconds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
