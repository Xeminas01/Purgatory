package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.HyperMaze;
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
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    public AnchorPane backgroundPane;
    public Button genMazeBtn;
    public ComboBox<String> genMazeChoice;
    public Spinner<Integer> hSpinner,wSpinner,sxSpinner,sySpinner,
            exSpinner,eySpinner,mxSpinner,mySpinner;
    public ComboBox<String> mazeEditChoice;
    public Button editMazeBtn;
    public Label titleTxt;
    private static final String
            MAZE_PATH = "src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti/",
            MAZE_PATH_RES = "labirinti/";
    public Button visMazeBtn;
    public Button createHyperMazeBtn;

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
        GUIMethods.renderSpinner(sxSpinner, 0, Maze.MAX_DIM);
        GUIMethods.renderSpinner(sySpinner, 0, Maze.MAX_DIM);
        GUIMethods.renderSpinner(exSpinner, 0, Maze.MAX_DIM);
        GUIMethods.renderSpinner(eySpinner, 0, Maze.MAX_DIM);
        GUIMethods.renderSpinner(mxSpinner, HyperMaze.MIN_D, HyperMaze.MAX_D);
        GUIMethods.renderSpinner(mySpinner, HyperMaze.MIN_D, HyperMaze.MAX_D);
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
                    sx = sxSpinner.getValue(), sy = sySpinner.getValue(),
                    ex = exSpinner.getValue(), ey = eySpinner.getValue();
            if(genType == MazeGenType.FRACTAL_GEN){
                int rounds = (int) (Math.log(h)/Math.log(2));
                h = (int) (Math.pow(2,rounds) + 2);
                w = h;
                System.out.println(h+"x"+w+" "+rounds);
            }
            if(!Maze.isValidInizio(h,w,sx,sy)){
                GUIMethods.showError("Inizio non valido");
                return;
            }
            if(!Maze.isValidFine(h,w,sx,sy,ex,ey)){
                GUIMethods.showError("Fine non valida");
                return;
            }
            if(sx > h-2 || sy > w-2){
                GUIMethods.showError("Invalid start point >h-2 or >w-2");
                return;
            }
            Maze maze = new Maze(h,w,genType);
            mazeGenController.setMaze(maze,sx,sy,ex,ey,genType);
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
            InputStream is = getClass().getResourceAsStream(MAZE_PATH_RES+mazeEditChoice.getValue());
            Maze maze = Maze.mazeFromXML(is);
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

    public void onVisMaze(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("visMaze.fxml"));
            Parent parent = fxmlLoader.load();
            VisualizeMazeController editMazeController = fxmlLoader.getController();
            InputStream is = getClass().getResourceAsStream(MAZE_PATH_RES+mazeEditChoice.getValue());
            Maze maze = Maze.mazeFromXML(is);
            if(maze != null)
                editMazeController.setMaze(maze,mazeEditChoice.getValue());
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Vis Maze");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onCreHyMaze(ActionEvent event) {

    }

}