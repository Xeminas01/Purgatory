package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.*;

public class MazePanel extends AnchorPane { //todo: finire implementazione

    public final ScrollPane scrollPane;
    public final VBox rowsBox;
    public final HBox[] columnBoxes;
    public final Label[][] cellsMatrix;
    public final int h,w,height,width,cellDim;
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
                System.out.println(i+","+j);
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

    public void renderMaze(Maze maze) {
        if(maze == null) return;
        if(maze.h != h || maze.w != w)
            return;
        for (int i = 0; i < maze.h; i++)
            for (int j = 0; j < maze.w; j++)
                cellsMatrix[i][j].setStyle("-fx-background-color: " +
                        maze.cellAt(i, j).color());
    }

    public void renderMaze(Maze maze, MazeCell cur){
        if(maze == null) return;
        if(maze.h != h || maze.w != w)
            return;
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

    public void renderSet(Set<MazeCell> set, boolean choice){
        for(MazeCell cell : set){
            if(choice){
                if(cell.type().isPercorso())
                    cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: red");
                else if(cell.type().isInizioFine()){
                    if(((InizioFine) cell).isStart)
                        cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: yellow");
                    else
                        cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: orange");
                }
            }else{
                if(cell.type().isPercorso())
                    cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: blue");
                else if(cell.type().isInizioFine()){
                    if(((InizioFine) cell).isStart)
                        cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: cyan");
                    else
                        cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: skyblue");
                }
            }
        }
    }

    public void colorCells(Collection<MazeCell> cells, String color){
        for(MazeCell cell : cells)
            cellsMatrix[cell.x][cell.y]
                    .setStyle("-fx-background-color: "+color);
    }

}
