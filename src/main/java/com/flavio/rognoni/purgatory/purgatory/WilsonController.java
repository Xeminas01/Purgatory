package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.IRPGen;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.WilsonGen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Timer;
import java.util.TimerTask;

public class WilsonController {
    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    private int cellDim;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private WilsonGen wilsonGen;
    private Timer timer;

    public void setMaze(Maze maze) {
        this.maze = maze;
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
                MazeSquare cell = maze.getCellAt(i,j);
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
                if(cell.isLimit())
                    label.setStyle("-fx-background-color: red");
                else if(cell.isWall())
                    label.setStyle("-fx-background-color: black");
                else if(cell.isPath())
                    label.setStyle("-fx-background-color: rgb(128,128,128)");
                else if(cell.isStartEnd())
                    label.setStyle("-fx-background-color: yellow");
                hBox.getChildren().add(label);
                cellsMatrix[i][j] = label;
            }
            rowsBox.getChildren().add(hBox);
            columnBoxes[i] = hBox;
        }

        wilsonGen = new WilsonGen(this.maze);
        renderMaze(wilsonGen.getMaze());

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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    wilsonGen.step();
                    renderMaze(wilsonGen.getMaze());
                    if(wilsonGen.isGen()) {
                        timer.cancel();
                        System.out.println("reach: "+maze.isAllReachable());
                    }
                });
            }
        },0,50);
    }

    public void onStep(ActionEvent event) {
        wilsonGen.step();
        renderMaze(wilsonGen.getMaze());
    }

}
