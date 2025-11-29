package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.CellularAutomata2D;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class CellularAutomaController implements Initializable {
    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    public Button stopBtn;
    public Button resetBtn;
    public Label timeTxt;
    public Button randomStateBtn;
    public ChoiceBox<Double> densityChoice;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private int cellDim;
    private CellularAutomata2D cellularAutomata2D;
    private Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopBtn.setVisible(false);
        timeTxt.setText("t=0");
        var dL = new ArrayList<Double>();
        for(double d=0.1;d<=1.0;d+=0.1)
            dL.add(d);
        densityChoice.setItems(FXCollections.observableArrayList(dL));
        densityChoice.getSelectionModel().select(4);
    }

    public void setCellularAutomata2D(CellularAutomata2D cellularAutomata2D) {
        this.cellularAutomata2D = cellularAutomata2D;
        this.cellDim = 720/Math.max(cellularAutomata2D.h,cellularAutomata2D.w);
        this.rowsBox = new VBox();
        mazePanel.getChildren().add(rowsBox);
        this.columnBoxes = new HBox[cellularAutomata2D.h];
        this.cellsMatrix = new Label[cellularAutomata2D.h][cellularAutomata2D.w];
        var matrix = cellularAutomata2D.getStateMatrix();
        for (int i = 0; i < cellularAutomata2D.h; i++) {
            HBox hBox = new HBox();
            for (int j = 0; j < cellularAutomata2D.w; j++) {
                Label label = new Label();
                int cell = matrix[i][j];
                label.setAlignment(Pos.CENTER);
//                label.setLayoutX(i*cellDim+cellDim);
//                label.setLayoutY(j*cellDim+cellDim);
                label.setMinWidth(cellDim);
                label.setPrefWidth(cellDim);
                label.setMaxWidth(cellDim);
                label.setMinHeight(cellDim);
                label.setPrefHeight(cellDim);
                label.setMaxHeight(cellDim);
                label.setFont(new Font("Verdana",20));
                if(cell == 0)
                    label.setStyle("-fx-background-color: rgb(200,200,200)");
                else if(cell == 1)
                    label.setStyle("-fx-background-color: black");
                hBox.getChildren().add(label);
                cellsMatrix[i][j] = label;
            }
            rowsBox.getChildren().add(hBox);
            columnBoxes[i] = hBox;
        }
    }

    private void renderMaze(int[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int cell = matrix[i][j];
                Label label = cellsMatrix[i][j];
                if(cell == 0)
                    label.setStyle("-fx-background-color: rgb(200,200,200)");
                else if(cell == 1)
                    label.setStyle("-fx-background-color: black");
            }
        }
    }

    private void renderMaze(Maze maze){
        for (int i = 0; i < maze.h; i++) {
            for (int j = 0; j < maze.w; j++) {
                MazeSquare cell = maze.getCellAt(i,j);
                Label label = cellsMatrix[i][j];
                if(cell.isLimit())
                    label.setStyle("-fx-background-color: red");
                else if(cell.isWall())
                    label.setStyle("-fx-background-color: black");
                else if(cell.isPath())
                    label.setStyle("-fx-background-color: rgb(128,128,128)");
                else if(cell.isStartEnd())
                    label.setStyle("-fx-background-color: yellow");
            }
        }
    }

    public void onStart(ActionEvent event) {
        stepBtn.setVisible(false);
        startBtn.setVisible(false);
        resetBtn.setVisible(false);
        stopBtn.setVisible(true);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    cellularAutomata2D.step();
                    renderMaze(cellularAutomata2D.getStateMatrix());
                    timeTxt.setText("t="+cellularAutomata2D.getT());
                    if(cellularAutomata2D.getT() == 100){
                        timer.cancel();
                        var maze = cellularAutomata2D.getMaze();
                        renderMaze(maze);
                        System.out.println(maze.isAllReachable());
                    }
                });
            }
        },0,100);
    }

    public void onStep(ActionEvent event) {
        cellularAutomata2D.step();
        renderMaze(cellularAutomata2D.getStateMatrix());
        timeTxt.setText("t="+cellularAutomata2D.getT());
    }

    public void onStop(ActionEvent event) {
        if(timer != null){
            timer.cancel();
            startBtn.setVisible(true);
            stopBtn.setVisible(false);
            resetBtn.setVisible(true);
            stepBtn.setVisible(true);
        }
    }

    public void onReset(ActionEvent event) {
        cellularAutomata2D.reset();
        renderMaze(cellularAutomata2D.getStateMatrix());
        timeTxt.setText("t="+cellularAutomata2D.getT());
    }

    public void onRandom(ActionEvent event) {
        if(timer != null){
            timer.cancel();
        }
        cellularAutomata2D.setInitState(new HashMap<>(){{
            put(1,CellularAutomata2D.randomState(
                    cellularAutomata2D.h,cellularAutomata2D.w,densityChoice.getValue()));
        }});
        cellularAutomata2D.reset();
        renderMaze(cellularAutomata2D.getStateMatrix());
        timeTxt.setText("t="+cellularAutomata2D.getT());
    }
}
