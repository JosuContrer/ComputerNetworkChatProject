package FinalProject.Frontend;

import FinalProject.CommunicationConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.junit.Test;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.Serializable;
import java.util.Queue;

/**
 * Java 8: because we are using lambda expressions
 */

public class Client2 extends Application  {

    Client client = new Client();
    String recipient = null;
    Scene loginS, messageS;
    String connectedUsers = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // LOGIN SCENE
        // -> Labels and button (with lambda expressions)
        Label titleLogin = new Label("Login");
        Label instructionsLogin = new Label("Enter Username");

        TextField textFieldLogin = new TextField();
        textFieldLogin.setPromptText("User");

        Button buttonLogin = new Button("Login");
        buttonLogin.setId("buttonLogin");
        // TODO: Add verification with the server to see if username has already been taken
        // -> Layout login
        VBox layoutLogin = new VBox(20);
        layoutLogin.setPadding(new Insets(10, 10, 10, 10));
        layoutLogin.getChildren().addAll(titleLogin, instructionsLogin, textFieldLogin, buttonLogin);
        layoutLogin.setAlignment(Pos.CENTER);
        loginS = new Scene(layoutLogin, 200, 300);

        // MESSAGING SCENE
        // -> Labels and button (with lambda expresssions)
        TextField userInputMessage = new TextField();
        ScrollPane mesScroll = new ScrollPane();
        TextFlow mesWindowText = new TextFlow();
        mesWindowText.setLineSpacing(5d);
        userInputMessage.setPromptText("Enter your message here");
        TextField connectedUsersWindow = new TextField("Connected...");

        // -> Layout Messaging
        BorderPane layoutMes = new BorderPane();
        HBox mesLayout = new HBox();
        VBox mesDisplayVBox = new VBox();

        // Center Pane
        mesScroll.setContent(mesWindowText);
        mesDisplayVBox.getChildren().addAll(mesScroll);
        VBox.setVgrow(mesScroll, Priority.ALWAYS);
        layoutMes.setCenter(mesDisplayVBox);

        // Right Pane
        ListView connectedListView = new ListView();
        connectedListView.getItems().add("Josue");
        connectedListView.getItems().add("STeve");
        layoutMes.setRight(connectedListView);

        //-> Enter button
        Button buttonConnectedListRefresh = new Button("Refresh");
        Button buttonEnterMes = new Button("Enter");

        // Bottom Pane
        mesLayout.getChildren().addAll(userInputMessage, buttonEnterMes, buttonConnectedListRefresh);
        HBox.setHgrow(userInputMessage, Priority.ALWAYS);
        layoutMes.setBottom(mesLayout);

        messageS = new Scene(layoutMes, 500, 400);

        // MAIN STAGE
        primaryStage.setOnCloseRequest(e -> { if(client != null){ client.close(); } });

        primaryStage.setTitle("Network With Strangers");
        primaryStage.setScene(loginS);
        primaryStage.show();

        // -------------------------------THREAD--------------------------------
        // Set thread for receiving message
        Runnable receiveMR = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Integer controlID = 0;
                    try {
                        if(client.loggedIn) {
                            System.out.println("Message Received:");
                            String[] packet = client.inputC.readUTF().split("@", 3);
                            controlID = Integer.parseInt(packet[0]);
                            System.out.println(" ControlID: " + controlID);
                            Text processedPacket = new Text("");

                            switch (controlID) {
                                case CommunicationConstants.WHISPER_MESSAGE:
                                    processedPacket = new Text(packet[1] + ": " + packet[2] + "\n");
                                    break;

                                case CommunicationConstants.CONNECTED_USERS_REQUEST:
                                    connectedUsers = packet[1];
                                    break;

                                case CommunicationConstants.USER_NOT_FOUND:
                                    processedPacket = new Text(packet[1] + " USER NOT FOUND\n");
                                    break;

                                default:
                                    System.out.println(" ERROR IN RECEIVER THREAD");
                            }
                            System.out.println(" Processed Packet: " + processedPacket.getText());
                            Text finalProcessedPacket = processedPacket;
                            Platform.runLater(() -> mesWindowText.getChildren().add(finalProcessedPacket));
                        }
                    } catch (IOException e) {
                        System.out.println("Client input error disconnected");
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };

        // Heartbeat for connected list
        Runnable heartBeat = new Runnable() {
            @Override
            public void run() {
                while (client.loggedIn){
                    System.out.println("Sending heartbeat");
                    try{
                        client.outputC.writeUTF(CommunicationConstants.CONNECTED_USERS_REQUEST + "@");
                        Thread.sleep(3000);
                    }catch(InterruptedException i){
                        // Low priority thread so nothing to worry
                        i.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        // Start thread when login button is pressed
        Thread receiveMT = new Thread(receiveMR);
        Thread heartBeatThread = new Thread(heartBeat);
        receiveMT.setPriority(Thread.NORM_PRIORITY);
        heartBeatThread.setPriority(Thread.MIN_PRIORITY);
        buttonLogin.setOnAction(e -> validateUserName(primaryStage, textFieldLogin, receiveMT, heartBeatThread));

        // Sending message
        buttonEnterMes.setOnAction(e -> {
            String t = userInputMessage.getText();

            if(t.length() != 0 && recipient != null){
                Text mes = new Text("Me: " + t + "\n");
                mesWindowText.getChildren().add(mes);
                userInputMessage.clear();
                userInputMessage.requestFocus();
                mesScroll.setVvalue(mesDisplayVBox.getHeight());
                try {
                    client.outputC.writeUTF(CommunicationConstants.WHISPER_MESSAGE + "@" + recipient + "@" + t);
                } catch (IOException ex) {
                    System.out.println("ERROR IN SENDER THREAD");
                    ex.printStackTrace();
                }
            }
        });

        // Add listener for list view of the connected users
        connectedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                recipient = newValue;
            }
        });

        buttonConnectedListRefresh.setOnAction(e -> {
            String[] arrayUserNames = parseUserNames(connectedUsers);
            ObservableList<String> userNames = FXCollections.observableArrayList(arrayUserNames);
            connectedListView.setItems(userNames);
        });

    }

    private String[] parseUserNames(String connectedUsers){
        if(connectedUsers == null || connectedUsers.equals("")){
            return new String[]{"NO ONE CONNECTED"};
        }

        return connectedUsers.split(",");

    }

    private boolean validateUserName(Stage primaryScene, TextField input, Thread recieveThread, Thread heartBeat){
        try {
            int userN = Integer.parseInt(input.getText());
            input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
            System.out.println("Incorrect");
            return false;
        } catch (NumberFormatException e) {
            try {
                client = new Client(input.getText(), 5056);
                client.outputC.writeUTF(input.getText());
                primaryScene.setTitle(input.getText());
                primaryScene.setScene(messageS);
                recieveThread.start();
                heartBeat.start();
                return true;
            } catch (IOException p) { // Socket error
                input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
                System.out.println("Incorrect");
                p.printStackTrace();
                return false;
            } catch (Exception u){ // Incorrect username expcetion form client
                u.printStackTrace();
                input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
                System.out.println("Incorrect");
                return false;
            }
        }
    }

}
