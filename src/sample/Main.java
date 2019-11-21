package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("searcher.fxml"));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("searcher.fxml"));
        Controller controller = loader.getController();



        primaryStage.setTitle("File searcher");
        Scene primaryScene = new Scene(root, 1800, 800);


        primaryStage.setScene(primaryScene);

        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);


    }






}
