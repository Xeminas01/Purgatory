package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("home.fxml"));
        Parent parent = fxmlLoader.load();
        HomeController homeController = fxmlLoader.getController();
        Maze maze = Maze.mazeFromXML("");
        if(maze != null)
            homeController.setMaze(maze);
        Scene scene = new Scene(parent, 1280, 720);
        stage.setTitle("Mazes!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}