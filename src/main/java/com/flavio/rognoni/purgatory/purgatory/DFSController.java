package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.SquareDist;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.DFSGen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DFSController {

    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    private int cellDim;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private DFSGen dfsGen;
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

        dfsGen = new DFSGen(this.maze);
        dfsGen.start();
        renderMaze(dfsGen.getMaze(),null);

    }

    private void renderMaze(Maze maze, MazeSquare cur){
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
                if(cell.equals(cur))
                    label.setStyle("-fx-background-color: green");
            }
        }
    }

    private void renderDist(List<SquareDist> dists){
        for(SquareDist dist : dists){
            Label label = cellsMatrix[dist.square.x][dist.square.y];
            label.setText(dist.d+"");
        }
    }

    public void onStart(ActionEvent event) {
        stepBtn.setVisible(false);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    var p = dfsGen.step();
                    renderMaze(dfsGen.getMaze(),p);
                    if(p == null || dfsGen.isGen()) {
                        timer.cancel();
                        System.out.println(maze.isAllReachable());
                        renderDist(maze.distancesFrom(maze.getAllStartEnd().get(1)));
                        var middle = maze.middleDistanceStartEnd(1.0,10);
                        System.out.println(middle);
                        cellsMatrix[middle.x][middle.y].setStyle("-fx-background-color: blue");
                        Maze.mazeToXML(maze);
                    }
                });
            }
        },0,10);
    }

    public void onStep(ActionEvent event) {
        var c = dfsGen.step();
        renderMaze(dfsGen.getMaze(),c);
    }

}
