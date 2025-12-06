package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.SquareDist;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.CellularAutomata2D;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
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
    public Button porteBtn;
    public Button distBtn;
    public Spinner<Double> percSpinner;
    public ChoiceBox<String> setChoice;
    public Button addPortaBtn;
    public ChoiceBox<String> rmDoorChoice;
    public Button rmPortaBtn;
    public Button ittBtn;
    public ChoiceBox<String> ittTypeChoice;
    public ChoiceBox<String> rmObjChoice;
    public Button addObjsBtn;
    public Button rmObjBtn;
    public Spinner<Integer> objSpinner;
    public Button genMazeBtn;
    public ComboBox<String> genMazeChoice;
    public Button addInvWallsBtn;
    public ChoiceBox<String> rmIWsChoice;
    public Button rmIWBtn;
    public Spinner<Integer> iwSpinner;
    public Button iwBtn;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private int cellDim;
    private MazeSquare porta;
    private List<MazeSquare> objs,iws;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //renderSpinner(percSpinner);
        percSpinner.setEditable(true);
        porta = null;
        addPortaBtn.setVisible(false);
        rmPortaBtn.setVisible(false);
        rmDoorChoice.setVisible(false);
        var l = new ArrayList<String>();
        l.add("Interruttori");
        l.add("Tesori");
        l.add("Trappole");
        ittTypeChoice.setItems(FXCollections.observableArrayList(l));
        ittTypeChoice.getSelectionModel().selectFirst();
        addObjsBtn.setVisible(false);
        objs = new ArrayList<>();
        rmObjChoice.setVisible(false);
        rmObjBtn.setVisible(false);
        l.clear();
        for(MazeGenType genType : MazeGenType.values())
            l.add(genType.getNome());
        genMazeChoice.setItems(FXCollections.observableArrayList(l));
        genMazeChoice.getSelectionModel().selectFirst();
        iws = new ArrayList<>();
        addInvWallsBtn.setVisible(false);
        addInvWallsBtn.setVisible(false);
        rmIWsChoice.setVisible(false);
        rmIWBtn.setVisible(false);
        mazePanel.setStyle("-fx-background-color: transparent");
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
                else if(cell.isSwitch())
                    label.setStyle("-fx-background-color: Orchid");
                else if(cell.isTreasure())
                    label.setStyle("-fx-background-color: gold");
                else if(cell.isTrap())
                    label.setStyle("-fx-background-color: FireBrick");
                else if(cell.isInvWall())
                    label.setStyle("-fx-background-color: white");
                else if(cell.isTeleport())
                    label.setStyle("-fx-background-color: DodgerBlue");
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
//        var oppWall2Set = maze.oppWall2Set(pathSets.get(0));
//        if(oppWall2Set == null) return;
//        System.out.println(oppWall2Set.size()+" "+oppWall2Set);
////        for(MazeSquare ms : oppWall2Set){
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
//        oppWall2Set = maze.oppWall2Set(pathSets.get(0));
//        if(oppWall2Set == null) return;
//        System.out.println(oppWall2Set.size()+" "+oppWall2Set);
////        for(MazeSquare ms : oppWall2Set){
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
            resetSetChoice();
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
            resetSetChoice();
            if(porte.isEmpty()){
                rmDoorChoice.setVisible(false);
                rmPortaBtn.setVisible(false);
            }
        }
    }

    public void onITT(ActionEvent event) {
        renderMaze(maze);
        var set = maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex());
        int n = objSpinner.getValue(),
                type = ittTypeChoice.getSelectionModel().getSelectedIndex() + 5;
        var itt = maze.bestITT(set,n,type);
        System.out.println(itt);
        if(itt != null){
            for(MazeSquare ms : itt){
                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: turquoise");
            }
            objs.clear();
            objs.addAll(itt);
            addObjsBtn.setVisible(true);
        }
    }

    public void onAddObjs(ActionEvent event) {
        if(!objs.isEmpty()){
            int type = ittTypeChoice.getSelectionModel().getSelectedIndex() + 5;
            setSquares(objs,type,rmObjChoice,addObjsBtn,rmObjBtn);
        }
    }

    public void onRmObj(ActionEvent event) {
        String ch = rmObjChoice.getValue();
        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
        var obj = maze.getCellAt(x,y);
        if(obj.isSwitch() || obj.isTreasure() || obj.isTrap()){
            maze.setTypeAt(obj.x,obj.y,MazeSquare.PATH);
            renderMaze(maze);
            var objs = maze.getAllObjects();
            var l = new ArrayList<String>();
            for(MazeSquare ms : objs)
                l.add("("+ms.x+","+ms.y+")");
            rmObjChoice.setItems(FXCollections.observableArrayList(l));
            rmObjChoice.setVisible(true);
            rmObjChoice.getSelectionModel().selectFirst();
            resetSetChoice();
            if(objs.isEmpty()){
                rmObjChoice.setVisible(false);
                rmObjBtn.setVisible(false);
            }
        }
    }

    public void onIWs(ActionEvent event) {
        renderMaze(maze);
        var set = maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex());
        int n = iwSpinner.getValue();
        var invWalls = maze.randomInvWalls(set,n);
        if(invWalls != null){
            iws.clear();
            iws.addAll(invWalls);
            for(MazeSquare ms : invWalls){
                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: SlateGray");
            }
            addInvWallsBtn.setVisible(true);
        }
    }

    public void onAddIWs(ActionEvent event) {
        if(!iws.isEmpty())
            setSquares(iws,MazeSquare.MURI_INVISIBILI,rmIWsChoice,addInvWallsBtn,rmIWBtn);
    }

    public void onRmIW(ActionEvent event) {
        String ch = rmIWsChoice.getValue();
        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
        var obj = maze.getCellAt(x,y);
        if(obj.isInvWall()) {
            maze.setTypeAt(obj.x,obj.y,MazeSquare.PATH);
            renderMaze(maze);
            var objs = maze.getAllInvWalls();
            var l = new ArrayList<String>();
            for(MazeSquare ms : objs)
                l.add("("+ms.x+","+ms.y+")");
            rmIWsChoice.setItems(FXCollections.observableArrayList(l));
            rmIWsChoice.setVisible(true);
            rmIWsChoice.getSelectionModel().selectFirst();
            resetSetChoice();
            if(objs.isEmpty()){
                rmIWsChoice.setVisible(false);
                rmIWBtn.setVisible(false);
            }
        }
    }

    private void setSquares(List<MazeSquare> mazeSquares,int type,
                            ChoiceBox<String> choice,Button addBtn,
                            Button rmBtn){
        var l = new ArrayList<String>();
        for(MazeSquare ms : mazeSquares) {
            l.add("("+ms.x+","+ms.y+")");
            maze.setTypeAt(ms.x,ms.y,type);
        }
        choice.setItems(FXCollections.observableArrayList(l));
        choice.setVisible(true);
        choice.getSelectionModel().selectFirst();
        choice.setVisible(true);
        resetSetChoice();
        addBtn.setVisible(false);
        rmBtn.setVisible(true);
        renderMaze(maze);
    }

    private void resetSetChoice(){
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

    public void onGenMaze(ActionEvent event) {
        MazeGenType genType =
                MazeGenType.values()[genMazeChoice
                        .getSelectionModel().getSelectedIndex()];
        switch(genType){
            case DFS_GEN -> {
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
            case FRACTAL_GEN -> {
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
            case CELLULAR_GEN -> {
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
                    stage.setTitle("The Cellular Automa Maze!");
                    stage.setScene(scene);
                    stage.show();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            case I_R_KRUSKAL_GEN -> {
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
            case I_R_PRIM_GEM -> {
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("irp.fxml"));
                    Parent parent = fxmlLoader.load();
                    IRPController fractalController = fxmlLoader.getController();
                    Maze maze = new Maze(50,50);
                    fractalController.setMaze(maze);
                    Scene scene = new Scene(parent, 1280, 720);
                    Stage stage = (Stage) backgroundPane.getScene().getWindow();
                    stage.setTitle("IRP Mazes!");
                    stage.setScene(scene);
                    stage.show();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            case WILSON_GEN -> {
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
        }
    }

}