package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze2;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.DFSGen;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.FractalTessellationGen;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class DFSController implements Initializable {

    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    public Button saveBtn;
    public Button stopBtn;
    public Button backBtn;
    private int cellDim;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze2 maze;
    private DFSGen dfsGen;
    private FractalTessellationGen fractalTessellationGen;
    private MazeGenType genType;
    private Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopBtn.setVisible(false);
        saveBtn.setVisible(false);
    }

    public void setMaze(Maze2 maze, int sx, int sy, MazeGenType genType) {
        this.maze = maze;
        this.genType = genType;
        this.cellDim = 720/Math.max(maze.h,maze.w);
        //System.out.println(cellDim);
        this.rowsBox = new VBox();
        mazePanel.getChildren().add(rowsBox);
        this.columnBoxes = new HBox[maze.h];
        this.cellsMatrix = new Label[maze.h][maze.w];
        for (int i = 0; i < maze.h; i++) {
            HBox hBox = new HBox();
            for (int j = 0; j < maze.w; j++) {
                Label label = new Label();
                MazeCell cell = maze.cellAt(i,j);
                label.setAlignment(Pos.CENTER);
                label.setMinWidth(cellDim);
                label.setPrefWidth(cellDim);
                label.setMaxWidth(cellDim);
                label.setMinHeight(cellDim);
                label.setPrefHeight(cellDim);
                label.setMaxHeight(cellDim);
                label.setFont(new Font("Verdana",20));
                label.setStyle("-fx-background-color: "+cell.color());
                hBox.getChildren().add(label);
                cellsMatrix[i][j] = label;
            }
            rowsBox.getChildren().add(hBox);
            columnBoxes[i] = hBox;
        }

        if(genType == MazeGenType.DFS_GEN){
            dfsGen = new DFSGen(this.maze,sx,sy);
            dfsGen.start();
            renderMaze(dfsGen.getMaze(),null);
        }else{
            int rounds = maze.h - 2;
            rounds = (int) (Math.log(rounds)/Math.log(2));
            System.out.println(rounds);
            fractalTessellationGen = new FractalTessellationGen(rounds);
            renderMaze(fractalTessellationGen.getMaze(),null);
        }

    }

    private void renderMaze(Maze2 maze, MazeCell cur){
        if(maze == null) return;
        for (int i = 0; i < maze.h; i++) {
            for (int j = 0; j < maze.w; j++) {
                MazeCell cell = maze.cellAt(i,j);
                Label label = cellsMatrix[i][j];
                label.setStyle("-fx-background-color: "+cell.color());
                if(cell.equals(cur))
                    label.setStyle("-fx-background-color: green");
            }
        }
    }

    public void onStart(ActionEvent event) {
        stepBtn.setVisible(false);
        startBtn.setVisible(false);
        stopBtn.setVisible(true);
        backBtn.setVisible(false);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(genType == MazeGenType.DFS_GEN){
                        var p = dfsGen.step();
                        renderMaze(dfsGen.getMaze(),p);
                        if(p == null || dfsGen.isGen()) {
                            timer.cancel();
                            if(maze.isAllReachable()){
                                System.out.println("all reachable");
                                saveBtn.setVisible(true);
                                startBtn.setVisible(false);
                                stopBtn.setVisible(false);
                                stepBtn.setVisible(false);
                                backBtn.setVisible(true);
                            }
                        }
                    }else if(genType == MazeGenType.FRACTAL_GEN){
                        fractalTessellationGen.step();
                        renderMaze(fractalTessellationGen.getMaze(),null);
                        if(fractalTessellationGen.isGenerato()) {
                            timer.cancel();
                            maze = fractalTessellationGen.getMaze();
                            if(maze.isAllReachable()){
                                System.out.println("all reachable");
                                saveBtn.setVisible(true);
                                startBtn.setVisible(false);
                                stopBtn.setVisible(false);
                                stepBtn.setVisible(false);
                                backBtn.setVisible(true);
                            }
                        }
                    }
                });
            }
        },0,10);
    }

    public void onStep(ActionEvent event) {
        if(genType == MazeGenType.DFS_GEN){
            var c = dfsGen.step();
            renderMaze(dfsGen.getMaze(),c);
            if(dfsGen.isGen() && maze.isAllReachable()){
                saveBtn.setVisible(true);
                startBtn.setVisible(false);
                stopBtn.setVisible(false);
                stepBtn.setVisible(false);
                backBtn.setVisible(true);
            }
        }else if(genType == MazeGenType.FRACTAL_GEN){
            fractalTessellationGen.step();
            renderMaze(fractalTessellationGen.getMaze(),null);
            if(fractalTessellationGen.isGenerato() && maze.isAllReachable()){
                System.out.println("all reachable");
                saveBtn.setVisible(true);
                startBtn.setVisible(false);
                stopBtn.setVisible(false);
                stepBtn.setVisible(false);
                backBtn.setVisible(true);
            }
        }
    }

    public void onSave(ActionEvent event) {
        //Maze.mazeToXML(maze);
    }

    public void onStop(ActionEvent event) {
        timer.cancel();
        startBtn.setVisible(true);
        stepBtn.setVisible(true);
        stopBtn.setVisible(false);
        backBtn.setVisible(true);
    }

    public void onBack(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("home.fxml"));
            Parent parent = fxmlLoader.load();
            HomeController homeController = fxmlLoader.getController();
            Maze maze = Maze.mazeFromXML("");
            if(maze != null)
                homeController.setMaze(maze);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
