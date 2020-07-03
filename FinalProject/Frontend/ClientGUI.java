package FinalProject.Frontend;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Node;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

/**
 * Java 8: because we are using lambda expressions
 */

public class ClientGUI extends Application  {

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
        buttonLogin.setOnAction(e -> isText(primaryStage, textFieldLogin, textFieldLogin.getPromptText()));

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
                //mesDisplayVBox.heightProperty());


        // Right Pane
        layoutMes.setRight(connectedUsersWindow);

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
        primaryStage.setTitle("Network With Strangers");
        primaryStage.setScene(loginS);
        primaryStage.show();

        mesWindowText.getChildren().add(new Text("Hi\nbye\n what up\n bye\ngo"));
    }

    private boolean isText(Stage primaryScene, TextField input, String username){
        try{
            int userN = Integer.parseInt(input.getText());
            input.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,CornerRadii.EMPTY, BorderStroke.MEDIUM)));
            System.out.println("Incorrect");
            return false;
        }catch (NumberFormatException e){
            primaryScene.setScene(messageS);
            return true;
        }
    }

}
