package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;
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
import java.util.List;
import java.util.ResourceBundle;

public class EditMazeController implements Initializable {

    public AnchorPane backgroundPane;
    public AnchorPane mazePanel;
    public Button distBtn;
    public Spinner<Double> percSpinner;
    public ChoiceBox<String> setChoice;
    public Button ittBtn;
    public Spinner<Integer> best3QtySpinner,
            randomOpp2Spinner,random3Spinner;
    public ComboBox<String> cellTypeChoice;
    public Button putTypeInBtn;
    public Spinner<Integer> xSpinner;
    public Spinner<Integer> ySpinner;
    public Button addTeleBtn;
    public Button teleBtn;
    public ChoiceBox<String> setChoice1;
    public Button backBtn;
    public Button oppWalk2Setbtn;
    public Button walk3SetBtn;
    public Button addCelleBtn;
    public Button randomOpp2Btn;
    public Button random3Btn;
    public Button saveBtn;
    public Button showSetsBtn;
    public CheckBox booleanChoice;
    public CheckBox booleanChoice1;
    public Button modBtn;
    public TextArea cellInfoTxt;
    private VBox rowsBox;
    private HBox[] columnBoxes;
    private Label[][] cellsMatrix;
    private Maze maze;
    private int cellDim;
    private List<MazeCell> celle;
    private MazeCell[] teles;
    private String fileName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        percSpinner.setEditable(true);
        addCelleBtn.setVisible(false);
        celle = new ArrayList<>();
        var l = new ArrayList<String>();
        addTeleBtn.setVisible(false);
        for(MazeCellType cellType : MazeCellType.values())
            l.add(cellType.nome);
        cellTypeChoice.setItems(FXCollections.observableArrayList(l));
        cellTypeChoice.setOnAction(e -> {
            renderCellInputs();
        });
        cellTypeChoice.getSelectionModel().selectFirst();
        mazePanel.setStyle("-fx-background-color: transparent");
        booleanChoice.setVisible(false);
        booleanChoice1.setVisible(false);
        modBtn.setVisible(false);
        cellInfoTxt.setEditable(false);
    }

    public void setMaze(Maze maze,String fileName) {
        this.maze = maze;
        this.fileName = fileName;
        this.cellDim = 720/Math.max(maze.h,maze.w);
        //System.out.println(cellDim);
        this.rowsBox = new VBox();
        mazePanel.getChildren().add(rowsBox);
        this.columnBoxes = new HBox[maze.h];
        this.cellsMatrix = new Label[maze.h][maze.w];
        GUIMethods.renderSpinner(xSpinner,0,maze.h);
        GUIMethods.renderSpinner(ySpinner,0,maze.h);
        for (int i = 0; i < maze.h; i++) {
            HBox hBox = new HBox();
            for (int j = 0; j < maze.w; j++) {
                Label label = new Label();
                MazeCell cell = maze.cellAt(i,j);
                label.setAlignment(Pos.CENTER);
                label.setMinWidth(cellDim);
                label.setPrefWidth(cellDim);
                label.setMaxWidth(cellDim);
                label.setMinHeight(cellDim);
                label.setPrefHeight(cellDim);
                label.setMaxHeight(cellDim);
                label.setFont(new Font("Verdana",10));
                label.setStyle("-fx-background-color: "+cell.color());
                final int x = i, y = j;
                label.setOnMouseClicked(e -> {
                    setPutSpinners(x,y);
                });
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

    private void resetSetChoice(){
        var l = new ArrayList<String>();
        var pS = maze.walkSets();
        for(int i=0;i<pS.size();i++)
            l.add("Set<"+i+"> "+pS.get(i).size());
        setChoice.setItems(FXCollections.observableArrayList(l));
        setChoice.setOnAction(e ->
                renderSet(setChoice.getSelectionModel().getSelectedIndex(),true));
        setChoice1.setItems(FXCollections.observableArrayList(l));
        setChoice1.setOnAction(e ->
                renderSet(setChoice1.getSelectionModel().getSelectedIndex(),false));
        setChoice1.getSelectionModel().selectFirst();
        setChoice.getSelectionModel().selectFirst();
        var topoMap = maze.topologicalOrderOfWalkSets();
        System.out.println("topoMap: "+topoMap+" "+maze.isValidTopoMap(topoMap));
    }

    private void setPutSpinners(int x,int y){
        xSpinner.getValueFactory().setValue(x);
        ySpinner.getValueFactory().setValue(y);
        cellTypeChoice.getSelectionModel().select(
                maze.cellAt(x,y).type().ordinal());
        renderCellInfos(maze.cellAt(x,y));
    }

    private void renderCellInputs(){
        MazeCellType type = MazeCellType.values()
                [cellTypeChoice.getSelectionModel().getSelectedIndex()];
        switch(type){
            case LIMITE,MURO,PERCORSO ->
                    booleanChoice.setVisible(false);
            case INIZIO_FINE -> {
                booleanChoice.setVisible(true);
                booleanChoice.setText("Inizio");
                booleanChoice.setSelected(true);
            }
        }
    }

    private void renderCellInfos(MazeCell cell){
        cellInfoTxt.setText(cell.toString());
        switch(cell.type()){
            case LIMITE,MURO,PERCORSO,MURO_INVISIBILE -> {
                booleanChoice1.setVisible(false);
                modBtn.setVisible(false);
            }
            case INIZIO_FINE -> {
                booleanChoice1.setVisible(true);
                booleanChoice1.setVisible(true);
                booleanChoice1.setText("Inizio");
                booleanChoice1.setSelected(((InizioFine) cell).isStart);
                modBtn.setVisible(true);
            }
        }
    }

    public void onDistanze(ActionEvent event) { //todo: vedere se togliere
        renderMaze(maze);
        var middle = maze.middleDistancesInizioToFine().get(0).cell;
        cellsMatrix[middle.x][middle.y].setStyle("-fx-background-color: yellow");
    }

    private void renderSet(int idx,boolean choice){
        if(idx < 0) return;
        renderMaze(maze);
        var set = maze.walkSets.get(idx);
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

    public void onOpp2WalkSet(ActionEvent event) {
        int idx = setChoice.getSelectionModel().getSelectedIndex();
        renderSet(idx,true);
        var o2Set = maze.oppNoWalk2Set(maze.walkSets.get(idx));
        if(o2Set == null){
            GUIMethods.showError("Set troppo piccolo!");
            return;
        }
        for(MazeCell cell : maze.oppNoWalk2Set(maze.walkSets.get(idx)))
            cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: pink");
    }

    public void on3WalkSet(ActionEvent event) {
        int idx = setChoice.getSelectionModel().getSelectedIndex();
        renderSet(idx,true);
        var w3Set = maze.noWalk3Set(maze.walkSets.get(idx));
        if(w3Set == null){
            GUIMethods.showError("Set troppo piccolo!");
            return;
        }
        for(MazeCell cell : w3Set)
            cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: pink");
    }

    public void onBestOpp2Sep(ActionEvent event) {
        renderMaze(maze);
        var bestOpp2 = maze.bestSeparatorOpp2(percSpinner.getValue(),
                maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex()));
        if(bestOpp2 != null){
            cellsMatrix[bestOpp2.x][bestOpp2.y].setStyle("-fx-background-color: turquoise");
            xSpinner.getValueFactory().setValue(bestOpp2.x);
            ySpinner.getValueFactory().setValue(bestOpp2.y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.PORTA.ordinal());
        }else
            GUIMethods.showError("Set troppo piccolo deve avere almeno 30 celle");
    }

    public void onBest3(ActionEvent event) {
        renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var best3 = maze.bestsNoWalks3(set,best3QtySpinner.getValue());
        if(best3 != null && !best3.isEmpty()) {
            xSpinner.getValueFactory().setValue(best3.get(0).x);
            ySpinner.getValueFactory().setValue(best3.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.INTERRUTTORE.ordinal());
            celle.clear();
            celle.addAll(best3);
            addCelleBtn.setVisible(true);
            for(MazeCell cell : best3)
                cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: turquoise");
        }
    }

    public void onRandomOpp2(ActionEvent event) {
        renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var ro2 = maze.randomOppNoWalk2(set, randomOpp2Spinner.getValue());
        if(ro2 != null && !ro2.isEmpty()){
            xSpinner.getValueFactory().setValue(ro2.get(0).x);
            ySpinner.getValueFactory().setValue(ro2.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.PORTA.ordinal());
            celle.clear();
            celle.addAll(ro2);
            addCelleBtn.setVisible(true);
            for(MazeCell cell : ro2)
                cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: turquoise");
        }
    }

    public void onRandom3(ActionEvent event) {
        renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var ro2 = maze.randomNoWalk3(set,random3Spinner.getValue());
        if(ro2 != null && !ro2.isEmpty()){
            xSpinner.getValueFactory().setValue(ro2.get(0).x);
            ySpinner.getValueFactory().setValue(ro2.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.INTERRUTTORE.ordinal());
            celle.clear();
            celle.addAll(ro2);
            addCelleBtn.setVisible(true);
            for(MazeCell cell : ro2)
                cellsMatrix[cell.x][cell.y].setStyle("-fx-background-color: turquoise");
        }
    }

    public void onAddCelle(ActionEvent event) {
        if(celle != null && !celle.isEmpty()){
            for(MazeCell cell : celle){
                xSpinner.getValueFactory().setValue(cell.x);
                ySpinner.getValueFactory().setValue(cell.y);
                onPutTypeIn(null);
            }
            celle.clear();
            addCelleBtn.setVisible(false);
        }
    }

    public void onTele(ActionEvent event) {
        renderMaze(maze);
        var setA = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var setB = maze.walkSets.get(setChoice1.getSelectionModel().getSelectedIndex());
        var ts = maze.randomTeleports(setA,setB);
        if(ts != null){
            teles = ts;
            xSpinner.getValueFactory().setValue(teles[0].x);
            ySpinner.getValueFactory().setValue(teles[0].y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.TELETRASPORTO.ordinal());
            for(MazeCell ms : ts)
                cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: turquoise");
            addTeleBtn.setVisible(true);
        }else
            GUIMethods.showError("I Set hanno intersezione");
    }

    public void onAddTele(ActionEvent event) {
        if(teles != null && teles.length == 2){
            cellTypeChoice.getSelectionModel().select(MazeCellType.TELETRASPORTO.ordinal());
            for(MazeCell cell : teles){
                xSpinner.getValueFactory().setValue(cell.x);
                ySpinner.getValueFactory().setValue(cell.y);
                onPutTypeIn(null);
            }
            teles = null;
            addTeleBtn.setVisible(false);
        }
    }

    public void onPutTypeIn(ActionEvent event) {
        int x = xSpinner.getValue(),
                y = ySpinner.getValue();
        var cell = maze.cellAt(x,y);
        System.out.println(cell);
        MazeCellType type = MazeCellType.values()[cellTypeChoice.getSelectionModel().getSelectedIndex()];
        switch(type){
            case LIMITE -> {
                if(maze.areCoordinateLimite(x,y))
                    maze.cells[x][y] = new Limite(x,y);
                else
                    GUIMethods.showError("Coordinate invalide per Limite");
            }
            case MURO -> {
                if(!maze.areCoordinateLimite(x,y))
                    maze.cells[x][y] = new Muro(x,y);
                else
                    GUIMethods.showError("Coordinate invalide per Muro");
            }
            case PERCORSO -> maze.cells[x][y] = new Percorso(x,y);
            case INIZIO_FINE -> {
                boolean start = booleanChoice.isSelected();
                if(!maze.hasInizioOrFine(start))
                    maze.cells[x][y] = new InizioFine(x,y,start);
                else
                    GUIMethods.showError(((start) ? "Inizio" : "Fine")+" già presente!");
            }
            case PORTA -> {
                if(maze.isOppNoWalk2(cell)) maze.cells[x][y] = new Porta(x,y,false,1);
                //todo: gestire quali interruttori o quante chiavi
                else GUIMethods.showError("Invalid cell for Porta or Muro Invisibile");
            }
            case INTERRUTTORE -> {
                if(maze.isNoWalk3(cell)) maze.cells[x][y] = new Interruttore(x,y,false);
                else GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case TESORO -> {
                if(maze.isNoWalk3(cell)) maze.cells[x][y] = new Tesoro(x,y,"",false);
                //todo: input del nome dell'oggetto
                else GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case TRAPPOLA -> {
                if(maze.isNoWalk3(cell)) maze.cells[x][y] = new Trappola(x,y,1,false);
                //todo: input dei danni della trappola
                else GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case MURO_INVISIBILE -> {
                if(maze.isOppNoWalk2(cell)) maze.cells[x][y] = new MuroInvisibile(x,y);
                else GUIMethods.showError("Invalid cell for Porta or Muro Invisibile");
            }
            case TELETRASPORTO -> {
                if(maze.isNoWalk3(cell)) maze.cells[x][y] = new Teletrasporto(x,y,null);
                //todo: gestire inserimento endPoint
                else GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
        }
        renderMaze(maze);
        resetSetChoice();
    }

    public void onModCell(ActionEvent event) {
        MazeCellType type = MazeCellType.values()[cellTypeChoice.getSelectionModel().getSelectedIndex()];
        switch(type){
            case INIZIO_FINE -> {
                booleanChoice.setSelected(booleanChoice1.isSelected());
                onPutTypeIn(null);
            }
        }
    }

    public void onShowSets(ActionEvent event) {
        renderMaze(maze);
        for(int i=0;i<maze.walkSets.size();i++)
            for(MazeCell cell : maze.walkSets.get(i))
                if(cell.type().isPercorso()) cellsMatrix[cell.x][cell.y]
                        .setStyle("-fx-background-color: "+
                                GUIMethods.setColors[i%GUIMethods.setColors.length]);
    }

    public void onSave(ActionEvent event) {
        try{
            if(maze.isSolvable())
                Maze.mazeToXML(maze,fileName);
        }catch (Exception e){
            GUIMethods.showError(e.getMessage());
        }
    }

    public void onBack(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("home.fxml"));
            Parent parent = fxmlLoader.load();
            //HomeController homeController = fxmlLoader.getController();
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
