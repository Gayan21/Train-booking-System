public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[42];
    private int last=0;

    public Passenger[] getQueueArray() {
        return queueArray;
    }

    public void add(Passenger passenger){

        if (isFull())
        {
            System.out.println(" Queue is full. Cannot add any passenger");
            return;
        }
        queueArray[last] = passenger;
        last = last + 1;
    }
    public void remove(Passenger passenger)
    {
        for (int i = 0; i < last; i++)
        {
            if (queueArray[i].getName().equals(passenger.getName()))
            {
                //shift all other passenger
                for (int j = i + 1; j < last; j++)
                {
                    queueArray[j - 1] = queueArray[j];
                }
                //clear last element
                queueArray[--last] = null;
                System.out.println(passenger.getName() + " deleted successfully");
                return;
            }
        }
        System.out.println("Cannot find " + passenger.getName());
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public boolean isFull()
    {
        return last >= 42;
    }
}
