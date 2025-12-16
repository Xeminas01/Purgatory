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
import java.util.Objects;
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
    private Maze maze;
    private MazeGen mazeGen;
    private MazeGenType genType;
    private Timer timer;
    private boolean gen;
    private MazePanel mPanel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopBtn.setVisible(false);
        saveBtn.setVisible(false);
        gen = false;
    }

    public void setMaze(Maze maze, int sx, int sy, MazeGenType genType) {

        this.maze = maze;
        double lx = mazePanel.getLayoutX(),
                ly = mazePanel.getLayoutY(),
                ph = mazePanel.getPrefHeight(),
                pw = mazePanel.getPrefWidth();
        this.mPanel = new MazePanel(maze.h,maze.w,(int) ph,(int) pw);
        mPanel.setLayoutX(lx);
        mPanel.setLayoutY(ly);
        backgroundPane.getChildren().add(mPanel);
        backgroundPane.getChildren().remove(mazePanel);
        mazePanel = mPanel;
        this.genType = genType;

        setGenerator(sx,sy);

    }

    private void setGenerator(int sx, int sy){
        switch(genType){
            case DFS_GEN -> {
                mazeGen = new DFSGen(this.maze,sx,sy);
                ((DFSGen) mazeGen).start();
                mPanel.renderMaze(mazeGen.getMaze());
            }
            case FRACTAL_GEN -> {
                int rounds = maze.h - 2;
                rounds = (int) (Math.log(rounds)/Math.log(2));
                mazeGen = new FractalTessellationGen(rounds,sx,sy);
                mPanel.renderMaze(mazeGen.getMaze());
            }
            case CELLULAR_GEN -> {
                mazeGen = new CellularGen(this.maze,sx,sy);
                mPanel.renderMaze(this.maze);
            }
            case I_R_KRUSKAL_GEN -> {
                mazeGen = new IRKGen(this.maze,sx,sy);
                mPanel.renderMaze(this.maze);
            }
            case I_R_PRIM_GEM -> {
                mazeGen = new IRPGen(this.maze,sx,sy);
                mPanel.renderMaze(this.maze);
            }
            case WILSON_GEN -> {
                mazeGen = new WilsonGen(this.maze,sx,sy);
                mPanel.renderMaze(this.maze);
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
                    timeTask(true);
                });
            }
        },0,genType.millis(maze.h*maze.w));
    }

    private void timeTask(boolean fromTimer){
        if(gen) return;
        int t;
        if (genType == MazeGenType.DFS_GEN) {
            mPanel.renderMaze(mazeGen.getMaze(), mazeGen.step());
            t = mazeGen.getT();
        } else {
            mazeGen.step();
            t = mazeGen.getT();
            maze = mazeGen.getMaze();
            mPanel.renderMaze(maze);
        }
        gen = mazeGen.isGen();
        if (gen) {
            if(fromTimer) timer.cancel();
            setIsGenerated();
        }
        timeTxt.setText("t="+t);
    }

    public void onStep(ActionEvent event) {
        timeTask(false);
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
        Maze.mazeToXML(maze,null);
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
