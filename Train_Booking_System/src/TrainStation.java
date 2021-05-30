import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrainStation extends Application {
    private Passenger[] waitingRoom = new Passenger[42];
    private int waitingRoomNext = 0;
    private  PassengerQueue queueArray = new PassengerQueue();
    private Passenger[] train = new Passenger[42];
    private int trainNext = 0;
    private int secondsInQueue = 0;

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start (Stage primaryStage) throws Exception{
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("Bookings");
        MongoCollection<Document> fromColombo = mongoDatabase.getCollection("Colombo");
        MongoCollection<Document>  fromBadulla = mongoDatabase.getCollection("Badulla");
        MongoCollection<Document> Trainqueue = mongoDatabase.getCollection("TrainQueue");


        HashMap<Integer, String> train_seatsColomboToBadulla = new HashMap<Integer, String>();
        HashMap<Integer, String> train_seatsBadullaToColombo = new HashMap<Integer, String>();

        loadData(train_seatsColomboToBadulla, fromColombo);
        loadData(train_seatsBadullaToColombo, fromBadulla);

        System.out.println("====Enter 1 if your Train leaving from colombo====");
        System.out.println("====Enter 2 if your Train leaving from badulla====");

        Scanner sc = new Scanner(System.in);
        String userInput = sc.next();

        if (userInput.equalsIgnoreCase("1")){
            addToWaitingRoom(train_seatsColomboToBadulla);
        } else if (userInput.equalsIgnoreCase("2")){
            addToWaitingRoom(train_seatsBadullaToColombo);
        } else {
            System.out.println("Invalid Input");
        }

        menu:
        while (true){
            System.out.println("*************************************************************");
            System.out.println("..........Welcome to Denuwara Menike  Train queue System..............  ");
            System.out.println("*************************************************************");
            System.out.println("Enter A----> to add customer to trainQueue ");
            System.out.println("Enter V----> to view the trainQueue");
            System.out.println("Enter D----> to delete passenger from the trainQueue ");
            System.out.println("Enter S----> to store data into a  file ");
            System.out.println("Enter L----> to load data back from the file into the trainQueue ");
            System.out.println("Enter R----> to run the simulation and produce report");
            System.out.println("Enter Q----> to quit");

            System.out.println("*************************************************************");


            System.out.println("Enter your option:-");
            String userOption = sc.next();

            switch (userOption.toUpperCase()){
                case "A":
                    addNextPassengerFromWaitingRoom();
                    break ;
                case "R":
                    runSimulation();
                    break ;
                case "V":
                    showTrainQueue();
                    break ;
                case "D":
                    System.out.println("Enter a name:");
                    String name=sc.next();
                    deletePassenger(name);
                    break;
                case "S":
                    saveQueue(Trainqueue);
                    System.out.println("=======stored data to a file===========");
                    break;
                case "L":
                    loadQueue(Trainqueue);
                    System.out.println("=======loaded data from a file=========");
                    break ;
                case "Q":
                    System.out.println("Exit");
                    break menu;
            }
        }

    }
    //delete method............................................................................
    private void deletePassenger(String name)
    {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        queueArray.remove(passenger);
    }

   //view train queue method..................................................................
    private void showTrainQueue() {
        System.out.println("========Waiting Room==========");
        for (int i=0; i<waitingRoomNext;i++){
            System.out.println(waitingRoom[i].getName() + " ~ " + waitingRoom[i].getSeat());
        }


        System.out.println("=======Train Queue=========");
        for (int i=0; i<queueArray.getLast(); i++){
            System.out.println(queueArray.getQueueArray()[i].getName() + " ~ " + queueArray.getQueueArray()[i].getSeat());
        }

        System.out.println("=======Train=============");
        for (int i=0; i<trainNext; i++){
            System.out.println(train[i].getName() + " ~ " + train[i].getSeat());
        }


        Stage stage = new Stage();


        VBox vB = new VBox();
        vB.setSpacing(8);
        VBox vB1 = new VBox();
        vB1.setSpacing(8);
        VBox vB2 = new VBox();
        vB2.setSpacing(8);
        VBox vB3 = new VBox();
        vB3.setSpacing(8);
        VBox vB4 = new VBox();
        vB4.setSpacing(8);
        VBox vB5 = new VBox();
        vB5.setSpacing(8);
        VBox vB6 = new VBox();
        vB6.setSpacing(8);


        for (int i=0;i<waitingRoomNext;i++){
            Label lbl3 = new Label((i+1) + " " +waitingRoom[i].getName());
            lbl3.setStyle("-fx-font-size:20px");
            if (i>21){
                vB1.getChildren().add(lbl3);
            } else {
                vB.getChildren().add(lbl3);
            }

        }

        for (int i=0;i<queueArray.getLast();i++){
            Label lbl4 = new Label((i+1) + " " +queueArray.getQueueArray()[i].getName());
            lbl4.setStyle("-fx-font-size: 20px");
            if (i>21){
                vB3.getChildren().add(lbl4);
            } else {
                vB2.getChildren().add(lbl4);
            }

        }

        Button[] temp = new Button[42];
        for (int i=0;i<42;i++){
            Button btn1 = new Button((i+1) + " Empty");
            btn1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #9999ff; -fx-background-radius: 10");
            btn1.setPrefWidth(140);
            btn1.setId(String.valueOf(i+1));
            temp[i]=btn1;

            if (i>27){
                vB6.getChildren().add(btn1);
            } else if (i>13){
                vB5.getChildren().add(btn1);
            } else {
                vB4.getChildren().add(btn1);
            }
        }

        for (int i=0;i<trainNext;i++){
            temp[Integer.valueOf(train[i].getSeat())-1].setText(train[i].getSeat() + " - " + train[i].getName());
            temp[Integer.valueOf(train[i].getSeat())-1].setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color:  #ff4d4d; -fx-background-radius: 20");
        }


        HBox hB= new HBox();
        hB.setSpacing(20);
        HBox hB1 = new HBox();
        hB1.setSpacing(20);
        HBox hB2 = new HBox();
        hB2.setSpacing(20);

        Label lbl3 = new Label("Waiting Room");
        lbl3.setLayoutX(36);
        lbl3.setStyle("-fx-font-size: 45px; -fx-font-weight: bold;");
        Label lbl4 = new Label("Train Queue");
        lbl4.setLayoutX(335);
        lbl4.setStyle("-fx-font-size: 45px; -fx-font-weight: bold;");
        Label lbl5 = new Label("Train");
        lbl5.setLayoutX(500);
        lbl5.setStyle("-fx-font-size: 45px; -fx-font-weight: bold;");
        HBox hB3 = new HBox();
        hB3.getChildren().addAll(lbl3,lbl4,lbl5);

        hB3.setMargin(lbl3, new Insets(10, 0, 10, 10));
        hB3.setMargin(lbl4, new Insets(10, 0, 10, 220));
        hB3.setMargin(lbl5, new Insets(10, 0, 10, 440));

        hB.getChildren().addAll(vB,vB1);
        hB1.getChildren().addAll(vB2,vB3);
        hB2.getChildren().addAll(vB4,vB5,vB6);

        hB.setPrefWidth(450);
        hB1.setPrefWidth(490);
        hB2.setPrefWidth(500);


        BorderPane bPane = new BorderPane();
        bPane.setLeft(hB);
        bPane.setCenter(hB1);
        bPane.setRight(hB2);
        bPane.setTop(hB3);


        stage.setScene(new Scene(bPane, 1500, 850));
        stage.showAndWait();

    }
    //Save queue method...........................................................................
    private void saveQueue(MongoCollection<Document> Trainqueue) {
        //Trainqueue.drop();
        try {
            List<Document> Names = new ArrayList<Document>();
            for (int i = 0; i < queueArray.getLast(); i++) {
                Document document = new Document();
                document.append("Name", queueArray.getQueueArray()[i].getName());
                document.append("Seat", queueArray.getQueueArray()[i].getSeat());
                Names.add(document);
            }
            Trainqueue.insertMany(Names);
        } catch (Exception e){}
    }
   //Load queue method.................................................................................
    private void loadQueue(MongoCollection<Document> Trainqueue) {
        FindIterable<Document> Doc = Trainqueue.find();

        for (Document record : Doc){
            String firstName = (String) record.get("Name");
            String seatNo =String.valueOf(record.get("Seat"));

            Passenger passenger = new Passenger();
            passenger.setName(firstName);
            passenger.setSeat(seatNo);
            queueArray.add(passenger);
        }
    }
    //runSimulation method.................................................................................
    private void runSimulation() {
        for (int i=0; i<queueArray.getLast();i++){
            train[trainNext++] = queueArray.getQueueArray()[i];
        }
        secondsInQueue=0;
        System.out.println("*********Report*************");
        System.out.println("Maximum Length - " + queueArray.getLast());
        System.out.println("Maximum Seconds of Waiting Time - " + queueArray.getQueueArray()[queueArray.getLast()-1].getSecondsInQueue());
        System.out.println("Minimum Seconds of Waiting Time - " + queueArray.getQueueArray()[0].getSecondsInQueue());
        System.out.println("Average Seconds of Waiting Time - " + queueArray.getQueueArray()[queueArray.getLast()-1].getSecondsInQueue()/(double)queueArray.getLast());
        queueArray.setLast(0);

    }
//add a passenger to the train queue method...................................................................
    private void addNextPassengerFromWaitingRoom() {
        Random random = new Random();
        int randomNum = random.nextInt(6) + 1;



        if (randomNum>waitingRoomNext){
            randomNum=waitingRoomNext;
        }

        System.out.println("Random num - " + randomNum);

        for (int i=0; i<randomNum; i++){
            int waitingT = random.nextInt(6)+random.nextInt(6)+random.nextInt(6);
            secondsInQueue = secondsInQueue+waitingT;
            waitingRoom[i].setSecondsInQueue(secondsInQueue);
            queueArray.add(waitingRoom[i]);
        }


        for (int j=0; j<randomNum; j++) {

            for (int i = 0; i < waitingRoomNext; i++) {
                if ((i+1)<42){
                    waitingRoom[i] = waitingRoom[i + 1];
                }

            }
        }
        waitingRoomNext = waitingRoomNext - randomNum;

        System.out.println(" ");
        System.out.println("=========Train Queue========");
        for (int i=0; i<queueArray.getLast(); i++){
            System.out.println(queueArray.getQueueArray()[i].getName() + " ~ " + queueArray.getQueueArray()[i].getSeat());
        }



    }

    private void addToWaitingRoom(HashMap<Integer, String> train_seatsColomboToBadulla) {
        for (int i : train_seatsColomboToBadulla.keySet()){
            Passenger passenger = new Passenger();
            passenger.setSeat(String.valueOf(i));
            passenger.setName(train_seatsColomboToBadulla.get(i));
            waitingRoom[waitingRoomNext] = passenger;
            waitingRoomNext = waitingRoomNext + 1;
        }
    }

    private void loadData(HashMap<Integer, String> train_seatsColomboToBadulla, MongoCollection<Document> fromColombo) {
        FindIterable<Document> Doc = fromColombo.find();

        for (Document record : Doc){
            String fName = (String) record.get("Name");

            int num = (int) record.get("Seat Number");
            train_seatsColomboToBadulla.put(num, fName);

        }
    }
}
