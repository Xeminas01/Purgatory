package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazePanel extends AnchorPane { //todo: finire implementazione

    private ScrollPane scrollPane;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private final int h,w,height,width;
    private int cellDim;
    public static final int DEFAULT_CELL_DIM = 20;

    public MazePanel(int h,int w,int cellDim,
                     int height,int width){
        this.h = h;
        this.w = w;
        this.cellDim = cellDim;
        this.height = height;
        this.width = width;

        setMinWidth(width);setPrefWidth(width);setMaxWidth(width);
        setMinHeight(height);setPrefHeight(height);setMaxHeight(height);

        this.scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setMinWidth(width);scrollPane.setPrefWidth(width);scrollPane.setMaxWidth(width);
        scrollPane.setMinHeight(height);scrollPane.setPrefHeight(height);scrollPane.setMaxHeight(height);
        scrollPane.setLayoutX(0);scrollPane.setLayoutY(0);
        getChildren().add(scrollPane);

        this.rowsBox = new VBox();
        scrollPane.setContent(rowsBox);
        scrollPane.setPannable(true);

        this.columnBoxes = new HBox[h];
        this.cellsMatrix = new Label[h][w];
        //GUIMethods.renderSpinner(xSpinner,0,maze.h);
        //GUIMethods.renderSpinner(ySpinner,0,maze.h);
        for (int i = 0; i < h; i++) {
            HBox hBox = new HBox();
            for (int j = 0; j < w; j++) {
                Label label = new Label();
                //MazeCell cell = maze.cellAt(i,j);
                label.setAlignment(Pos.CENTER);
                label.setMinWidth(cellDim);
                label.setPrefWidth(cellDim);
                label.setMaxWidth(cellDim);
                label.setMinHeight(cellDim);
                label.setPrefHeight(cellDim);
                label.setMaxHeight(cellDim);
                label.setFont(new Font("Verdana", 10));
                label.setStyle("-fx-background-color: white");
                //final int x = i, y = j;
//                label.setOnMouseClicked(e -> {
//                    setPutSpinners(x,y);
//                });
                hBox.getChildren().add(label);
                cellsMatrix[i][j] = label;
            }
            rowsBox.getChildren().add(hBox);
            columnBoxes[i] = hBox;
        }
    }

    public MazePanel(int h,int w,int height,int width){
        this(h,w,DEFAULT_CELL_DIM,height,width);
    }

    public Map<String,Label> labelMap(){
        Map<String,Label> map = new HashMap<>();
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                map.put(h+","+w,cellsMatrix[i][j]);
        return map;
    }

    private void renderMaze(Maze maze) {
        if(maze.h != h || maze.w != w)
            return;
        for (int i = 0; i < maze.h; i++)
            for (int j = 0; j < maze.w; j++)
                cellsMatrix[i][j].setStyle("-fx-background-color: " +
                        maze.cellAt(i, j).color());
    }

    public void colorCells(List<MazeCell> cells,String color){
        for(MazeCell cell : cells)
            cellsMatrix[cell.x][cell.y]
                    .setStyle("-fx-background-color: "+color);
    }

}
