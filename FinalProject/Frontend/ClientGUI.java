package FinalProject.Frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Java 8: because we are using lambda expressions
 */

public class ClientGUI extends Application  {

    Client client = null;
    Scene loginS, messageS;

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
        buttonLogin.setOnAction(e -> validateUserName(primaryStage, textFieldLogin));

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
        connectedListView.getItems().add("Josue2");
        connectedListView.getItems().add("Josue3");
        connectedListView.getItems().add("Josue4");
        layoutMes.setRight(connectedListView);

        //-> Enter button
        Button buttonEnterMes = new Button("Enter");
        buttonEnterMes.setOnAction(e -> {
            String t = userInputMessage.getText();

            if(t.length() != 0){
                Text mes = new Text("\n" + t);
                mesWindowText.getChildren().add(mes);
                userInputMessage.clear();
                userInputMessage.requestFocus();
                mesScroll.setVvalue(mesDisplayVBox.getHeight());
            }
        });

        // Bottom Pane
        mesLayout.getChildren().addAll(userInputMessage, buttonEnterMes);
        HBox.setHgrow(userInputMessage, Priority.ALWAYS);
        layoutMes.setBottom(mesLayout);

        messageS = new Scene(layoutMes, 500, 400);

        // MAIN STAGE
        primaryStage.setOnCloseRequest(e -> { if(client != null){ client.close(); } });

        primaryStage.setTitle("Network With Strangers");
        primaryStage.setScene(loginS);
        primaryStage.show();

        mesWindowText.getChildren().add(new Text("Hi\nbye\n what up\n bye\ngo"));
    }

    private boolean validateUserName(Stage primaryScene, TextField input){
        try {
            int userN = Integer.parseInt(input.getText());
            input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
            System.out.println("Incorrect");
            return false;
        } catch (NumberFormatException e) {
            try {
                client = new Client(input.getText(), 5056);
                primaryScene.setScene(messageS);
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
