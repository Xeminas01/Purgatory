package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.*;
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

public class MazeGenController implements Initializable {

    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    public Button saveBtn;
    public Button stopBtn;
    public Button backBtn;
    public Label timeTxt;
    private int cellDim;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private DFSGen dfsGen;
    private FractalTessellationGen fractalTessellationGen;
    private CellularGen cellularGen;
    private IRKGen irkGen;
    private IRPGen irpGen;
    private WilsonGen wilsonGen;
    private MazeGenType genType;
    private Timer timer;
    private boolean gen;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopBtn.setVisible(false);
        saveBtn.setVisible(false);
        gen = false;
    }

    public void setMaze(Maze maze, int sx, int sy, MazeGenType genType) {

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

        setGenerator(sx,sy);

    }

    private void setGenerator(int sx, int sy){
        switch(genType){
            case DFS_GEN -> {
                dfsGen = new DFSGen(this.maze,sx,sy);
                dfsGen.start();
                renderMaze(dfsGen.getMaze(),null);
            }
            case FRACTAL_GEN -> {
                int rounds = maze.h - 2;
                rounds = (int) (Math.log(rounds)/Math.log(2));
                System.out.println(rounds);
                fractalTessellationGen = new FractalTessellationGen(rounds,sx,sy);
                renderMaze(fractalTessellationGen.getMaze(),null);
            }
            case CELLULAR_GEN -> {
                cellularGen = new CellularGen(this.maze,sx,sy);
                renderMaze(this.maze,null);
            }
            case I_R_KRUSKAL_GEN -> {
                irkGen = new IRKGen(this.maze,sx,sy);
                renderMaze(this.maze,null);
            }
            case I_R_PRIM_GEM -> {
                irpGen = new IRPGen(this.maze,sx,sy);
                renderMaze(this.maze,null);
            }
            case WILSON_GEN -> {
                wilsonGen = new WilsonGen(this.maze,sx,sy);
                renderMaze(this.maze,null);
            }
        }
    }

    private void renderMaze(Maze maze, MazeCell cur){
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
                    timeTask();
                });
            }
        },0,genType.millis());
    }

    private void timeTask(){
        if(gen) return;
        int t = 0;
        switch(genType){
            case DFS_GEN -> {
                renderMaze(dfsGen.getMaze(),dfsGen.step());
                t = dfsGen.getT();
                gen = dfsGen.isGen();
                if(gen) {
                    timer.cancel();
                    setIsGenerated();
                }
            }
            case FRACTAL_GEN -> {
                fractalTessellationGen.step();
                t = fractalTessellationGen.getT();
                maze = fractalTessellationGen.getMaze();
                renderMaze(maze,null);
                gen = fractalTessellationGen.isGenerato();
                if(gen) {
                    timer.cancel();
                    setIsGenerated();
                }
            }
            case CELLULAR_GEN -> {
                cellularGen.step();
                t = cellularGen.getT();
                maze = cellularGen.getMaze();
                renderMaze(maze,null);
                gen = cellularGen.isGen();
                if(gen){
                    timer.cancel();
                    setIsGenerated();
                }
            }
            case I_R_KRUSKAL_GEN -> {
                irkGen.step();
                t = irkGen.getT();
                maze = irkGen.getMaze();
                renderMaze(maze,null);
                gen = irkGen.isGen();
                if(gen){
                    timer.cancel();
                    setIsGenerated();
                }
            }
            case I_R_PRIM_GEM -> {
                irpGen.step();
                t = irpGen.getT();
                maze = irpGen.getMaze();
                renderMaze(maze,null);
                gen = irpGen.isGen();
                if(gen){
                    timer.cancel();
                    setIsGenerated();
                }
            }
            case WILSON_GEN -> {
                wilsonGen.step();
                t = wilsonGen.getT();
                maze = wilsonGen.getMaze();
                renderMaze(maze,null);
                gen = wilsonGen.isGen();
                if(gen){
                    timer.cancel();
                    setIsGenerated();
                }
            }
        }
        timeTxt.setText("t="+t);
    }

    public void onStep(ActionEvent event) {
        if(gen) return;
        int t = 0;
        switch(genType){
            case DFS_GEN -> {
                renderMaze(dfsGen.getMaze(),dfsGen.step());
                t = dfsGen.getT();
                gen = dfsGen.isGen();
                if(gen)
                    setIsGenerated();
            }
            case FRACTAL_GEN -> {
                fractalTessellationGen.step();
                t = fractalTessellationGen.getT();
                maze = fractalTessellationGen.getMaze();
                renderMaze(maze,null);
                gen = fractalTessellationGen.isGenerato();
                if(gen)
                    setIsGenerated();
            }
            case CELLULAR_GEN -> {
                cellularGen.step();
                t = cellularGen.getT();
                maze = cellularGen.getMaze();
                renderMaze(maze,null);
                gen = cellularGen.isGen();
                if(gen)
                    setIsGenerated();
            }
            case I_R_KRUSKAL_GEN -> {
                irkGen.step();
                t = irkGen.getT();
                maze = irkGen.getMaze();
                renderMaze(maze,null);
                gen = irkGen.isGen();
                if(gen)
                    setIsGenerated();
            }
            case I_R_PRIM_GEM -> {
                irpGen.step();
                t = irpGen.getT();
                maze = irpGen.getMaze();
                renderMaze(maze,null);
                gen = irpGen.isGen();
                if(gen)
                    setIsGenerated();
            }
            case WILSON_GEN -> {
                wilsonGen.step();
                t = wilsonGen.getT();
                maze = wilsonGen.getMaze();
                renderMaze(maze,null);
                gen = wilsonGen.isGen();
                if(gen)
                    setIsGenerated();
            }
        }
        timeTxt.setText("t="+t);
    }

    private void setIsGenerated(){
        if(maze.isAllReachable()){
            System.out.println("all reachable");
            saveBtn.setVisible(true);
            startBtn.setVisible(false);
            stopBtn.setVisible(false);
            stepBtn.setVisible(false);
            backBtn.setVisible(true);
        }
    }

    public void onSave(ActionEvent event) {
        Maze.mazeToXML(maze);
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
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Mazes Handler");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
