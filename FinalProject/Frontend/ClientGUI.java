package FinalProject.Frontend;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *  Client GUI that prompts the user to enter a unique username
 *  and verifies in the frontend and banckend of the chat program.
 *  It then enables the user to select from a list of connected
 *  users on the right side of the window. The connection list can
 *  be either refreshed by the user or it updates every heartbeat
 *  set time interval to see the currently connected users.
 *  The messages on the screen are direct messages or 'whispers' to
 *  each user only and cannot be seen by other users.
 *
 *  Important: This program has direct messaging and take into
 *  consideration security.
 *
 * Note: Run on the latest release of Java 8 because lambda
 * expressions are used for code readability.
 */

public class ClientGUI extends Application  {

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
        titleLogin.setFont(Font.font(null, FontWeight.BOLD, 30));
        Label instructionsLogin = new Label("Enter Username");

        TextField textFieldLogin = new TextField();
        textFieldLogin.setPromptText("User");

        Button buttonLogin = new Button("Login");
        buttonLogin.setId("buttonLogin");

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
        ListView<String> connectedListView = new ListView<String>();
        connectedListView.getItems().add("Josue");
        connectedListView.getItems().add("Steve");
        layoutMes.setRight(connectedListView);

        //-> Enter button
        Button buttonConnectedListRefresh = new Button("Refresh");
        Button buttonEnterMes = new Button("Enter");

        // Bottom Pane
        mesLayout.getChildren().addAll(userInputMessage, buttonEnterMes, buttonConnectedListRefresh);
        HBox.setHgrow(userInputMessage, Priority.ALWAYS);
        layoutMes.setBottom(mesLayout);


        messageS = new Scene(layoutMes, 500, 400);

        // Handle when exit button is pressed
        primaryStage.setOnCloseRequest(e -> { if(client != null && client.loggedIn){ client.close(); } });

        // MAIN STAGE
        primaryStage.setTitle("Network With Strangers");
        primaryStage.setScene(loginS);
        primaryStage.show();

        // -------------------------------THREADS--------------------------------
        // Receiving message thread
        Runnable receiveMR = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Integer controlID = 0;
                    Boolean updateConnectedUsers = false;
                    try
                    {
                        if(client.loggedIn)
                        {
                            String[] packet = client.inputC.readUTF().split("@", 3);
                            controlID = Integer.parseInt(packet[0]);
                            Text processedPacket = new Text("");

                            switch (controlID) {
                                case CommunicationConstants.WHISPER_MESSAGE:
                                    processedPacket = new Text(packet[1] + ": " + packet[2] + "\n");
                                    break;

                                case CommunicationConstants.CONNECTED_USERS_REQUEST:
                                    connectedUsers = packet[1];
                                    updateConnectedUsers = true;
                                    break;

                                case CommunicationConstants.USER_NOT_FOUND:
                                    processedPacket = new Text(packet[1] + " USER NOT FOUND\n");
                                    processedPacket.setFont(Font.font(null, FontWeight.BOLD, 12));
                                    break;

                                default:
                                    System.out.println(" ERROR IN RECEIVER THREAD");
                            }

                            // Display packet received by the server through the GUI thread
                            Text finalProcessedPacket = processedPacket;
                            if(!updateConnectedUsers)
                            {
                                Platform.runLater(() -> mesWindowText.getChildren().add(finalProcessedPacket));
                            }
                            else
                            {
                                String[] arrayUserNames = parseUserNames(connectedUsers);
                                ObservableList<String> userNames = FXCollections.observableArrayList(arrayUserNames);
                                Platform.runLater(() -> connectedListView.setItems(userNames));
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        Text serverFail = new Text("SERVER DISCONNECTED");
                        serverFail.setFont(Font.font(null, FontWeight.BOLD, 12));
                        Platform.runLater(() -> mesWindowText.getChildren().add(serverFail));
                        client.close();
                        return;
                    }
                }
            }
        };

        // Heartbeat for user connected list thread
        Runnable heartBeat = new Runnable() {
            @Override
            public void run() {
                while (client.loggedIn)
                {
                    System.out.println("Sending heartbeat");
                    try
                    {
                        client.outputC.writeUTF(CommunicationConstants.CONNECTED_USERS_REQUEST + "@");
                        Thread.sleep(CommunicationConstants.HEART_BEAT_INTERVAL);
                    }
                    catch(InterruptedException i)
                    {
                        // Low priority thread so nothing to worry
                        i.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        client.close();
                        break;
                    }
                }
            }
        };

        // Start thread when login button is pressed
        Thread receiveMT = new Thread(receiveMR);
        Thread heartBeatThread = new Thread(heartBeat);
        // Set heart beat priority lower than receive
        receiveMT.setPriority(Thread.NORM_PRIORITY);
        heartBeatThread.setPriority(Thread.MIN_PRIORITY);

        // Login Validation and Handling
        buttonLogin.setOnAction(e ->
        {
            if(layoutLogin.getChildren().size() > 4)
            {
                layoutLogin.getChildren().remove(layoutLogin.getChildren().size()-1);
            }
            validateUserName(primaryStage, textFieldLogin, layoutLogin, receiveMT, heartBeatThread);
        });

        // Send Message
        buttonEnterMes.setOnAction(e ->
        {
            String t = userInputMessage.getText();

            if(t.length() != 0 && recipient != null)
            {
                Text mes = new Text("Me->" + recipient + ": " + t + "\n");
                mesWindowText.getChildren().add(mes);
                userInputMessage.clear();
                userInputMessage.requestFocus();
                mesScroll.setVvalue(mesDisplayVBox.getHeight());
                try
                {
                    client.outputC.writeUTF(CommunicationConstants.WHISPER_MESSAGE + "@" + recipient + "@" + t);
                }
                catch (IOException ex)
                {
                    System.out.println("ERROR IN SENDER THREAD");
                    ex.printStackTrace();
                }
            }
            else if(recipient == null)
            {
                Text s = new Text("No user selected\n");
                s.setFont(Font.font(null, FontWeight.BOLD, 14));
                mesWindowText.getChildren().add(s);
            }
        });

        // Listener for list view of the connected users
        connectedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                recipient = newValue;
            }
        });

        buttonConnectedListRefresh.setOnAction(e ->
        {
            String[] arrayUserNames = parseUserNames(connectedUsers);
            ObservableList<String> userNames = FXCollections.observableArrayList(arrayUserNames);
            connectedListView.setItems(userNames);
        });

    }

    /**
     * Validates the username on the frontend and backend.
     *      -> Frontend validation:
     *            1) Requires the username to be of length one or greater
     *            2) Requires the username to be more than just a number
     *      -> Backend validation:
     *            1) Checks for duplicate username on the server side
     * @param primaryScene
     * @param input
     * @param layoutLogin
     * @param recieveThread
     * @param heartBeat
     * @return: logged in successfully or not
     */
    private boolean validateUserName(Stage primaryScene, TextField input, VBox layoutLogin, Thread recieveThread, Thread heartBeat){
        try
        {    // Check if a String is present (cannot be only a number for the username)
            Integer.parseInt(input.getText());
            input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
            layoutLogin.getChildren().add(new Label("Invalid Username"));
            return false;
        }
        catch (NumberFormatException e)
        {
            try
            {
                client = new Client(input.getText(), CommunicationConstants.CONNECTION_SOCKET);
                Integer dup = client.inputC.read();
                if(CommunicationConstants.IS_DUPLICATE == dup)
                { // Username is duplicate
                    input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
                    layoutLogin.getChildren().add(new Label("Username Taken"));
                    return false;
                }
                primaryScene.setTitle(input.getText());
                primaryScene.setScene(messageS);
                recieveThread.start();
                heartBeat.start();
                return true;
            }
            catch (IOException p)
            { // Socket error from server
                input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
                if(p.getMessage().equals(CommunicationConstants.SERVER_FAILED))
                {
                    layoutLogin.getChildren().add(new Label("SERVER OFFLINE\nTry Again Later"));
                }
                else
                {
                    p.printStackTrace();
                }
                return false;
            }
            catch (Exception u)
            { // Incorrect username exception form client
                if(u.getMessage().equals(CommunicationConstants.INVALID_USERNAME))
                {
                    input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
                    layoutLogin.getChildren().add(new Label("Invalid Username"));
                }
                else
                {
                    u.printStackTrace();
                }
                return false;
            }
        }
    }

    /**
     * Helper function that parses the list sent by the server of connected users and splits it
     * into an array of strings.
     *
     * @param connectedUsers
     * @return
     */
    private String[] parseUserNames(String connectedUsers)
    {
        if(connectedUsers == null || connectedUsers.equals(""))
        {
            return new String[]{"NO ONE CONNECTED"};
        }
        return connectedUsers.split(",");
    }
}