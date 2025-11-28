package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.FractalTessellationGen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class FractalController implements Initializable {
    public AnchorPane backgroundPane;
    public AnchorPane mazePanel;
    public Button startBtn;
    public Button stepBtn;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private int cellDim;
    private FractalTessellationGen fractalTessellationGen;
    private Timer timer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setMaze(int rounds) {

        fractalTessellationGen = new FractalTessellationGen(rounds);
        int d = fractalTessellationGen.dim;
        this.cellDim = 720/d;
        this.rowsBox = new VBox();
        mazePanel.getChildren().add(rowsBox);
        this.columnBoxes = new HBox[d];
        this.cellsMatrix = new Label[d][d];
        for (int i = 0; i < d; i++) {
            HBox hBox = new HBox();
            for (int j = 0; j < d; j++) {
                Label label = new Label();
                MazeSquare cell = fractalTessellationGen.getMatrix()[i][j];
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


    }

    private void renderMaze(MazeSquare[][] matrix){
        for (int i = 0; i < fractalTessellationGen.dim; i++) {
            for (int j = 0; j < fractalTessellationGen.dim; j++) {
                MazeSquare cell = matrix[i][j];
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
                    fractalTessellationGen.step();
                    renderMaze(fractalTessellationGen.getMatrix());
                    if(fractalTessellationGen.isGenerato()) {
                        timer.cancel();
                        System.out.println(fractalTessellationGen.getMaze().isAllReachable());
                    }
                });
            }
        },0,1000);
    }

    public void onStep(ActionEvent event) {
        startBtn.setVisible(false);
        fractalTessellationGen.step();
        renderMaze(fractalTessellationGen.getMatrix());
    }

}
