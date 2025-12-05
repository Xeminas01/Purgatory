package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.SquareDist;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.CellularAutomata2D;
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

import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    public AnchorPane backgroundPane;
    public Button startBtn;
    public Button stepBtn;
    public AnchorPane mazePanel;
    public Button fractalBtn;
    public Button caBtn;
    public Button irkBtn;
    public Button irpBtn;
    public Button wilsonBtn;
    public Button dfsBtn;
    public Button porteBtn;
    public Button distBtn;
    public Spinner<Double> percSpinner;
    public ChoiceBox<String> setChoice;
    public Button addPortaBtn;
    public ChoiceBox<String> rmDoorChoice;
    public Button rmPortaBtn;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private int cellDim;
    private MazeSquare porta;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //renderSpinner(percSpinner);
        percSpinner.setEditable(true);
        porta = null;
        addPortaBtn.setVisible(false);
        rmPortaBtn.setVisible(false);
        rmDoorChoice.setVisible(false);
    }

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
                label.setFont(new Font("Verdana",10));
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

        renderMaze(maze);

        var l = new ArrayList<String>();
        var pS = maze.pathSets();
        for(int i=0;i<pS.size();i++)
            l.add("Set<"+i+"> "+pS.get(i).size());
        setChoice.setItems(FXCollections.observableArrayList(l));
        setChoice.setOnAction(e -> {
            renderSet(setChoice.getSelectionModel().getSelectedIndex());
        });
        setChoice.getSelectionModel().selectFirst();

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
                else if(cell.isDoor())
                    label.setStyle("-fx-background-color: purple");
            }
        }
    }

    private void renderDist(List<SquareDist> dists){
        for(SquareDist dist : dists){
            Label label = cellsMatrix[dist.square.x][dist.square.y];
            label.setText(dist.d+"");
        }
    }

    private void renderSet(int idx){
        if(idx < 0) return;
        renderMaze(maze);
        var set = maze.pathSets().get(idx);
        for(MazeSquare ms : set){
            if(!ms.isStartEnd())
                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: coral");
            else
                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: cyan");
        }
    }

    public void onFractal(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fractal.fxml"));
            Parent parent = fxmlLoader.load();
            FractalController fractalController = fxmlLoader.getController();
            fractalController.setMaze(6);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Fractal Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onCellularAutoma(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("cellularAutoma.fxml"));
            Parent parent = fxmlLoader.load();
            CellularAutomaController fractalController = fxmlLoader.getController();
            int dim = 40;
            CellularAutomata2D ca2d = new CellularAutomata2D(dim,dim,
                    CellularAutomata2D.MOORE_TYPE,1, Set.of(0,1),
                    "0,1,3,1;1,1,5-n,0;1,1,0,0",
                    //Maze: "0,1,3,1;1,1,6-n,0;1,1,0,0" Mazectric: "0,1,3,1;1,1,5-n,0;1,1,0,0" Game of life: "1,1,0-1,0;1,1,4-n,0;0,1,3,1"
                    new HashMap<>(){{
                        put(1,CellularAutomata2D.randomState(dim,dim,0.5));
                        //aliante "50,50;50,49;50,51;49,51;48,50" barca "50,50;49,49;49,51;48,50;48,49"
                    }},
                    0
            );
            fractalController.setCellularAutomata2D(ca2d);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("The Cellular Automa!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onIRK(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("irk.fxml"));
            Parent parent = fxmlLoader.load();
            IRKController fractalController = fxmlLoader.getController();
            Maze maze = new Maze(50,50);
            fractalController.setMaze(maze);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("IRK Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void onIRP(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("irp.fxml"));
            Parent parent = fxmlLoader.load();
            IRPController fractalController = fxmlLoader.getController();
            Maze maze = new Maze(50,50);
            fractalController.setMaze(maze);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("IRK Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onWilson(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("wilson.fxml"));
            Parent parent = fxmlLoader.load();
            WilsonController fractalController = fxmlLoader.getController();
            Maze maze = new Maze(50,50);
            fractalController.setMaze(maze);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("Wilson Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onDFS(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("dfs.fxml"));
            Parent parent = fxmlLoader.load();
            DFSController fractalController = fxmlLoader.getController();
            Maze maze = new Maze(20,30);
            fractalController.setMaze(maze);
            Scene scene = new Scene(parent, 1280, 720);
            Stage stage = (Stage) backgroundPane.getScene().getWindow();
            stage.setTitle("DFS Mazes!");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onPorte(ActionEvent event) {
        renderMaze(maze);
        porta = maze.bestDoor(percSpinner.getValue(),
                maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex()));
        if(porta != null){
            cellsMatrix[porta.x][porta.y].setStyle("-fx-background-color: pink");
            addPortaBtn.setVisible(true);
            addPortaBtn.setText("Add Porta in ("+porta.x+","+porta.y+")");
        }else{
            addPortaBtn.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.ERROR,"Set troppo piccolo deve avere almeno 30 celle");
            alert.show();
        }
//        int nPaths = maze.getAllPaths().size();
//        var pathSets = maze.pathSets();
//        System.out.println(pathSets.size()+" "+pathSets);
//        var doorSet = maze.doorSet(pathSets.get(0));
//        if(doorSet == null) return;
//        System.out.println(doorSet.size()+" "+doorSet);
////        for(MazeSquare ms : doorSet){
////            cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: pink");
////        }
//        var door = maze.bestDoor(percSpinner.getValue(),pathSets.get(0));
//        if(door == null){
//            System.out.println("no porte per questo set è troppo piccolo!");
//            return;
//        }
//        maze.setTypeAt(door.x,door.y,MazeSquare.PORTA);
//        pathSets = maze.pathSets();
//        System.out.println(door);
//        int c = 0;
//        for(Set<MazeSquare> set : pathSets){
//            System.out.println("|set|="+set.size()+" set:"+set);
//            for(MazeSquare ms : set){
//                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: "+((c%2==0) ? "green" : "skyblue"));
//            }
//            c++;
//        }
//        cellsMatrix[door.x][door.y].setStyle("-fx-background-color: purple");
//        doorSet = maze.doorSet(pathSets.get(0));
//        if(doorSet == null) return;
//        System.out.println(doorSet.size()+" "+doorSet);
////        for(MazeSquare ms : doorSet){
////            cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: pink");
////        }
//        var sdoor = maze.bestDoor(percSpinner.getValue(),pathSets.get(0));
//        if(sdoor == null){
//            System.out.println("no porte per questo set è troppo piccolo!");
//            return;
//        }
//        cellsMatrix[sdoor.x][sdoor.y].setStyle("-fx-background-color: magenta");
//        maze.setTypeAt(sdoor.x,sdoor.y,MazeSquare.PORTA);
//        pathSets = maze.pathSets();
//        String[] colors = {"green","skyblue","coral"},
//                colors2 = {"lime","cyan","firebrick"};
//        c = 0;
//        for(Set<MazeSquare> set : pathSets){
//            System.out.println("|set|="+set.size()+" set:"+set);
//            String color = colors[c%colors.length],color2 = colors2[c%colors.length];
//            System.out.println(color);
//            for(MazeSquare ms : set){
//                if(!ms.isStartEnd())
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: "+color);
//                else
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: "+color2);
//            }
//            c++;
//        }
//        System.out.println("porte create: "+maze.getAllDoors());
//        System.out.println(nPaths+" "+maze.getAllPaths().size());
    }

    public void onDistanze(ActionEvent event) {
        renderMaze(maze);
        var middle = maze.middleDistanceStartEnd(percSpinner.getValue(),10);
        System.out.println(middle);
        cellsMatrix[middle.x][middle.y].setStyle("-fx-background-color: blue");
        //renderDist();
    }

    public void onAddPorta(ActionEvent event) {
        if(porta != null && maze.getCellAt(porta.x, porta.y).type != MazeSquare.PORTA){
            maze.setTypeAt(porta.x,porta.y,MazeSquare.PORTA);
            var porte = maze.getAllDoors();
            renderMaze(maze);
            var l = new ArrayList<String>();
            for(MazeSquare ms : porte)
                l.add("("+ms.x+","+ms.y+")");
            rmDoorChoice.setItems(FXCollections.observableArrayList(l));
            rmDoorChoice.setVisible(true);
            rmDoorChoice.getSelectionModel().selectFirst();
            rmPortaBtn.setVisible(true);
            l.clear();
            var pS = maze.pathSets();
            for(int i=0;i<pS.size();i++)
                l.add("Set<"+i+"> "+pS.get(i).size());
            setChoice.setItems(FXCollections.observableArrayList(l));
            setChoice.setOnAction(e -> {
                renderSet(setChoice.getSelectionModel().getSelectedIndex());
            });
            setChoice.getSelectionModel().selectFirst();
            addPortaBtn.setVisible(false);
        }
    }

    public void onRmPorta(ActionEvent event) {
        String ch = rmDoorChoice.getValue();
        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
        var door = maze.getCellAt(x,y);
        if(door.isDoor()){
            maze.setTypeAt(door.x,door.y,MazeSquare.PATH);
            renderMaze(maze);
            var porte = maze.getAllDoors();
            var l = new ArrayList<String>();
            for(MazeSquare ms : porte)
                l.add("("+ms.x+","+ms.y+")");
            rmDoorChoice.setItems(FXCollections.observableArrayList(l));
            rmDoorChoice.setVisible(true);
            rmDoorChoice.getSelectionModel().selectFirst();
            l.clear();
            var pS = maze.pathSets();
            for(int i=0;i<pS.size();i++)
                l.add("Set<"+i+"> "+pS.get(i).size());
            setChoice.setItems(FXCollections.observableArrayList(l));
            setChoice.setOnAction(e -> {
                renderSet(setChoice.getSelectionModel().getSelectedIndex());
            });
            setChoice.getSelectionModel().selectFirst();
            if(porte.isEmpty()){
                rmDoorChoice.setVisible(false);
                rmPortaBtn.setVisible(false);
            }
        }
    }

}