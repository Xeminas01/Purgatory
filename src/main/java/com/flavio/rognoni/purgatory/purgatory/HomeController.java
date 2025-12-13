package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    public AnchorPane backgroundPane;
    public Button genMazeBtn;
    public ComboBox<String> genMazeChoice;
    public Spinner<Integer> hSpinner,wSpinner,sxSpinner,sySpinner;
    public ComboBox<String> mazeEditChoice;
    public Button editMazeBtn;
    public Label titleTxt;
    private static final String MAZE_PATH = "src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var l = new ArrayList<String>();
        for(MazeGenType genType : MazeGenType.values())
            l.add(genType.getNome());
        genMazeChoice.setItems(FXCollections.observableArrayList(l));
        genMazeChoice.getSelectionModel().selectFirst();
        l.clear();
        l.addAll(Arrays.asList(Objects.requireNonNull(new File(MAZE_PATH).list())));
        mazeEditChoice.setItems(FXCollections.observableArrayList(l));
        mazeEditChoice.getSelectionModel().selectFirst();
        GUIMethods.renderSpinner(hSpinner, Maze.MIN_DIM, Maze.MAX_DIM);
        GUIMethods.renderSpinner(wSpinner, Maze.MIN_DIM, Maze.MAX_DIM);
        GUIMethods.renderSpinner(sxSpinner, 1, Maze.MAX_DIM-2);
        GUIMethods.renderSpinner(sySpinner, 1, Maze.MAX_DIM-2);
    }

    public void onGenMaze(ActionEvent event) {
        MazeGenType genType =
                MazeGenType.values()[genMazeChoice
                        .getSelectionModel().getSelectedIndex()];
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("mazeGen.fxml"));
            Parent parent = fxmlLoader.load();
            MazeGenController mazeGenController = fxmlLoader.getController();
            int h = hSpinner.getValue(), w = wSpinner.getValue(),
                    sx = sxSpinner.getValue(), sy = sySpinner.getValue();
            if(genType == MazeGenType.FRACTAL_GEN){
                int rounds = (int) (Math.log(h)/Math.log(2));
                h = (int) (Math.pow(2,rounds) + 2);
                w = h;
                System.out.println(h+"x"+w+" "+rounds);
            }
            if(sx > h-2 || sy > w-2){
                GUIMethods.showError("Invalid start point >h-2 or >w-2");
                return;
            }
            Maze maze = new Maze(h,w,genType);
            mazeGenController.setMaze(maze,sx,sy,genType);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Generate Maze with "+genType.getNome());
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onEditMaze(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("editMaze.fxml"));
            Parent parent = fxmlLoader.load();
            EditMazeController editMazeController = fxmlLoader.getController();
            Maze maze = Maze.mazeFromXML(MAZE_PATH+mazeEditChoice.getValue());
            if(maze != null)
                editMazeController.setMaze(maze,mazeEditChoice.getValue());
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Edit Maze");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}