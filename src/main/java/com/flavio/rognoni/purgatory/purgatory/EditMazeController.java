package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditMazeController implements Initializable {

    public AnchorPane backgroundPane;
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
    public Button addInvWallsBtn;
    public ChoiceBox<String> rmIWsChoice;
    public Button rmIWBtn;
    public Spinner<Integer> iwSpinner;
    public Button iwBtn;
    public ComboBox<String> cellTypeChoice;
    public Button putTypeInBtn;
    public Spinner<Integer> xSpinner;
    public Spinner<Integer> ySpinner;
    public Button addTeleBtn;
    public ChoiceBox<String> rmTeleChoice;
    public Button rmTeleBtn;
    public Button teleBtn;
    public ChoiceBox<String> setChoice1;
    public Button backBtn;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private int cellDim;
//    private MazeSquare porta;
//    private List<MazeSquare> objs,iws;
//    private MazeSquare[] teles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //renderSpinner(percSpinner);
        percSpinner.setEditable(true);
        //porta = null;
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
        //objs = new ArrayList<>();
        rmObjChoice.setVisible(false);
        rmObjBtn.setVisible(false);
        //iws = new ArrayList<>();
        addInvWallsBtn.setVisible(false);
        addInvWallsBtn.setVisible(false);
        rmIWsChoice.setVisible(false);
        rmIWBtn.setVisible(false);
        //teles = null;
        addTeleBtn.setVisible(false);
        rmTeleChoice.setVisible(false);
        rmTeleBtn.setVisible(false);
        l.clear();
        l.add("Muro");l.add("Path");l.add("SE");l.add("Porta");l.add("Interruttore");
        l.add("Tesoro");l.add("Trap");l.add("InvW");l.add("Tele");
        cellTypeChoice.setItems(FXCollections.observableArrayList(l));
        cellTypeChoice.getSelectionModel().selectFirst();
        xSpinner.setEditable(true);
        ySpinner.setEditable(true);
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
                MazeCell cell = maze.cellAt(i,j);
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
                label.setStyle("-fx-background-color: "+cell.color());
                hBox.getChildren().add(label);
                cellsMatrix[i][j] = label;
            }
            rowsBox.getChildren().add(hBox);
            columnBoxes[i] = hBox;
        }

        renderMaze(maze);
        resetSetChoice();

    }

    private void renderMaze(Maze maze){
        for (int i = 0; i < maze.h; i++) {
            for (int j = 0; j < maze.w; j++) {
                MazeCell cell = maze.cellAt(i,j);
                Label label = cellsMatrix[i][j];
                label.setStyle("-fx-background-color: "+cell.color());
            }
        }
    }

//    private void renderDist(List<SquareDist> dists){
//        for(SquareDist dist : dists){
//            Label label = cellsMatrix[dist.square.x][dist.square.y];
//            label.setText(dist.d+"");
//        }
//    }

//    private void renderSet(int idx,boolean choice){
//        if(idx < 0) return;
//        renderMaze(maze);
//        var set = maze.pathSets().get(idx);
//        for(MazeSquare ms : set){
//            if(choice){
//                if(!ms.isStartEnd())
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: coral");
//                else
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: cyan");
//            }else{
//                if(!ms.isStartEnd())
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: RoyalBlue");
//                else
//                    cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: magenta");
//            }
//        }
//    }

    public void onPorte(ActionEvent event) {
//        renderMaze(maze);
//        porta = maze.bestWallOrDoorOrIW(percSpinner.getValue(),
//                maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex()),
//                MazeSquare.PORTA);
//        if(porta != null){
//            cellsMatrix[porta.x][porta.y].setStyle("-fx-background-color: pink");
//            addPortaBtn.setVisible(true);
//            addPortaBtn.setText("Add Porta in ("+porta.x+","+porta.y+")");
//        }else{
//            addPortaBtn.setVisible(false);
//            Alert alert = new Alert(Alert.AlertType.ERROR,"Set troppo piccolo deve avere almeno 30 celle");
//            alert.show();
//        }
    }

    public void onDistanze(ActionEvent event) {
//        renderMaze(maze);
//        var middle = maze.middleDistanceStartEnd(percSpinner.getValue(),10);
//        System.out.println(middle);
//        cellsMatrix[middle.x][middle.y].setStyle("-fx-background-color: blue");
//        //renderDist();
    }

    public void onAddPorta(ActionEvent event) {
//        if(porta != null && maze.getCellAt(porta.x, porta.y).type != MazeSquare.PORTA){
//            maze.setTypeAt(porta.x,porta.y,MazeSquare.PORTA);
//            var porte = maze.getAllDoors();
//            renderMaze(maze);
//            var l = new ArrayList<String>();
//            for(MazeSquare ms : porte)
//                l.add("("+ms.x+","+ms.y+")");
//            rmDoorChoice.setItems(FXCollections.observableArrayList(l));
//            rmDoorChoice.setVisible(true);
//            rmDoorChoice.getSelectionModel().selectFirst();
//            rmPortaBtn.setVisible(true);
//            resetSetChoice();
//            addPortaBtn.setVisible(false);
//        }
    }

    public void onRmPorta(ActionEvent event) {
//        String ch = rmDoorChoice.getValue();
//        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
//                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
//        var door = maze.getCellAt(x,y);
//        if(door.isDoor()){
//            maze.setTypeAt(door.x,door.y,MazeSquare.PATH);
//            renderMaze(maze);
//            var porte = maze.getAllDoors();
//            var l = new ArrayList<String>();
//            for(MazeSquare ms : porte)
//                l.add("("+ms.x+","+ms.y+")");
//            rmDoorChoice.setItems(FXCollections.observableArrayList(l));
//            rmDoorChoice.setVisible(true);
//            rmDoorChoice.getSelectionModel().selectFirst();
//            resetSetChoice();
//            if(porte.isEmpty()){
//                rmDoorChoice.setVisible(false);
//                rmPortaBtn.setVisible(false);
//            }
//        }
    }

    public void onITT(ActionEvent event) {
//        renderMaze(maze);
//        var set = maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex());
//        int n = objSpinner.getValue(),
//                type = ittTypeChoice.getSelectionModel().getSelectedIndex() + 5;
//        var itt = maze.bestITT(set,n,type);
//        System.out.println(itt);
//        if(itt != null){
//            for(MazeSquare ms : itt){
//                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: turquoise");
//            }
//            objs.clear();
//            objs.addAll(itt);
//            addObjsBtn.setVisible(true);
//        }
    }

    public void onAddObjs(ActionEvent event) {
//        if(!objs.isEmpty()){
//            int type = ittTypeChoice.getSelectionModel().getSelectedIndex() + 5;
//            setSquares(objs,type,rmObjChoice,addObjsBtn,rmObjBtn);
//        }
    }

    public void onRmObj(ActionEvent event) {
//        String ch = rmObjChoice.getValue();
//        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
//                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
//        var obj = maze.getCellAt(x,y);
//        if(obj.isSwitch() || obj.isTreasure() || obj.isTrap()){
//            maze.setTypeAt(obj.x,obj.y,MazeSquare.PATH);
//            renderMaze(maze);
//            var objs = maze.getAllObjects();
//            var l = new ArrayList<String>();
//            for(MazeSquare ms : objs)
//                l.add("("+ms.x+","+ms.y+")");
//            rmObjChoice.setItems(FXCollections.observableArrayList(l));
//            rmObjChoice.setVisible(true);
//            rmObjChoice.getSelectionModel().selectFirst();
//            resetSetChoice();
//            if(objs.isEmpty()){
//                rmObjChoice.setVisible(false);
//                rmObjBtn.setVisible(false);
//            }
//        }
    }

    public void onIWs(ActionEvent event) {
//        renderMaze(maze);
//        var set = maze.pathSets().get(setChoice.getSelectionModel().getSelectedIndex());
//        int n = iwSpinner.getValue();
//        var invWalls = maze.randomMuriOrPorteOrIW(set,n,MazeSquare.MURI_INVISIBILI);
//        if(invWalls != null){
//            iws.clear();
//            iws.addAll(invWalls);
//            for(MazeSquare ms : invWalls){
//                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: MistyRose");
//            }
//            addInvWallsBtn.setVisible(true);
//        }
    }

    public void onAddIWs(ActionEvent event) {
//        if(!iws.isEmpty())
//            setSquares(iws,MazeSquare.MURI_INVISIBILI,rmIWsChoice,addInvWallsBtn,rmIWBtn);
    }

    public void onRmIW(ActionEvent event) {
//        String ch = rmIWsChoice.getValue();
//        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
//                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
//        var obj = maze.getCellAt(x,y);
//        if(obj.isInvWall()) {
//            maze.setTypeAt(obj.x,obj.y,MazeSquare.PATH);
//            renderMaze(maze);
//            var objs = maze.getAllInvWalls();
//            var l = new ArrayList<String>();
//            for(MazeSquare ms : objs)
//                l.add("("+ms.x+","+ms.y+")");
//            rmIWsChoice.setItems(FXCollections.observableArrayList(l));
//            rmIWsChoice.setVisible(true);
//            rmIWsChoice.getSelectionModel().selectFirst();
//            resetSetChoice();
//            if(objs.isEmpty()){
//                rmIWsChoice.setVisible(false);
//                rmIWBtn.setVisible(false);
//            }
//        }
    }

//    private void setSquares(List<MazeSquare> mazeSquares,int type,
//                            ChoiceBox<String> choice,Button addBtn,
//                            Button rmBtn){
//        var l = new ArrayList<String>();
//        for(MazeSquare ms : mazeSquares) {
//            l.add("("+ms.x+","+ms.y+")");
//            maze.setTypeAt(ms.x,ms.y,type);
//        }
//        choice.setItems(FXCollections.observableArrayList(l));
//        choice.setVisible(true);
//        choice.getSelectionModel().selectFirst();
//        choice.setVisible(true);
//        resetSetChoice();
//        addBtn.setVisible(false);
//        rmBtn.setVisible(true);
//        renderMaze(maze);
//    }

    private void resetSetChoice(){
//        var l = new ArrayList<String>();
//        var pS = maze.pathSets();
//        for(int i=0;i<pS.size();i++)
//            l.add("Set<"+i+"> "+pS.get(i).size());
//        setChoice.setItems(FXCollections.observableArrayList(l));
//        setChoice.setOnAction(e -> {
//            renderSet(setChoice.getSelectionModel().getSelectedIndex(),true);
//        });
//        setChoice1.setItems(FXCollections.observableArrayList(l));
//        setChoice1.setOnAction(e -> {
//            renderSet(setChoice1.getSelectionModel().getSelectedIndex(),false);
//        });
//        setChoice1.getSelectionModel().selectFirst();
//        setChoice.getSelectionModel().selectFirst();
    }

    public void onTele(ActionEvent event) {
//        renderMaze(maze);
//        var sets = maze.pathSets();
//        var setA = sets.get(setChoice.getSelectionModel().getSelectedIndex());
//        var setB = sets.get(setChoice1.getSelectionModel().getSelectedIndex());
//        var ts = maze.randomTeleport(setA,setB);
//        if(ts != null){
//            teles = ts;
//            for(MazeSquare ms : ts)
//                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: Aquamarine");
//            addTeleBtn.setVisible(true);
//        }else{
//            Alert alert = new Alert(Alert.AlertType.ERROR,"I Set hanno intersezione");
//            alert.show();
//        }
    }

    public void onAddTele(ActionEvent event) {
//        if(teles != null && teles.length == 2)
//            setSquares(new ArrayList<>(Arrays.asList(teles)),
//                    MazeSquare.TELETRASPORTI,rmTeleChoice,addTeleBtn,rmTeleBtn);
    }

    public void onRmTele(ActionEvent event) {
//        String ch = rmTeleChoice.getValue();
//        int x = Integer.parseInt(ch.split(",")[0].replace("(","")),
//                y = Integer.parseInt(ch.split(",")[1].replace(")",""));
//        var obj = maze.getCellAt(x,y);
//        if(obj.isTeleport()) {
//            maze.setTypeAt(obj.x,obj.y,MazeSquare.PATH);
//            renderMaze(maze);
//            var objs = maze.getAllTeles();
//            var l = new ArrayList<String>();
//            for(MazeSquare ms : objs)
//                l.add("("+ms.x+","+ms.y+")");
//            rmTeleChoice.setItems(FXCollections.observableArrayList(l));
//            rmTeleChoice.setVisible(true);
//            rmTeleChoice.getSelectionModel().selectFirst();
//            resetSetChoice();
//            if(objs.isEmpty()){
//                rmTeleChoice.setVisible(false);
//                rmTeleBtn.setVisible(false);
//            }
//        }
    }

    public void onPutTypeIn(ActionEvent event) {
//        int x = xSpinner.getValue(),
//                y = ySpinner.getValue();
//        var cell = maze.getCellAt(x,y);
//        System.out.println(cell);
//        int type = cellTypeChoice.getSelectionModel().getSelectedIndex()+1;
//        switch(type){
//            case MazeSquare.WALL,MazeSquare.PATH,MazeSquare.START_END -> {
//                maze.setTypeAt(cell.x,cell.y,type);
//            }
//            case MazeSquare.PORTA,MazeSquare.MURI_INVISIBILI -> {
//                if(maze.isOppWall2(cell)) maze.setTypeAt(cell.x,cell.y,type);
//                else{
//                    Alert alert = new Alert(Alert.AlertType.ERROR,
//                            "Invalid cell for Porta or Muro Invisibile",ButtonType.OK);
//                    alert.show();
//                }
//            }
//            case MazeSquare.INTERRUTTORE,MazeSquare.TESORO,MazeSquare.TRAPPOLA,MazeSquare.TELETRASPORTI -> {
//                if(maze.isWall3(cell)) maze.setTypeAt(cell.x,cell.y,type);
//                else{
//                    Alert alert = new Alert(Alert.AlertType.ERROR,
//                            "Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto",ButtonType.OK);
//                    alert.show();
//                }
//            }
//        }
//        renderMaze(maze);
//        resetSetChoice();
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
