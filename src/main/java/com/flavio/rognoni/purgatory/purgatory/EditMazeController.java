package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
    public TextArea cellInfoTxt;
    public ComboBox<String> cellSubTypeChoice;
    public Spinner<Integer> nSpinner;
    public ComboBox<String> cellChoice;
    public Button plusBtn;
    public Button minusBtn;
    public TextField cellTextInput;
    public TextArea cellListArea;
    private List<MazeCell> cellList;
    private Maze maze;
    private List<MazeCell> celle;
    private MazeCell[] teles;
    private String fileName;
    private MazePanel mPanel;

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
            renderCellInputs(cellTypeChoice.getSelectionModel().getSelectedIndex() !=
                    maze.cellAt(xSpinner.getValue(),ySpinner.getValue()).type().ordinal());
        });
        cellTypeChoice.getSelectionModel().selectFirst();
        mazePanel.setStyle("-fx-background-color: transparent");
        booleanChoice.setVisible(false);
        cellInfoTxt.setEditable(false);
        cellSubTypeChoice.setVisible(false);
        nSpinner.setVisible(false);
        GUIMethods.renderSpinner(nSpinner,1,10);
        cellChoice.setVisible(false);
        plusBtn.setVisible(false);
        minusBtn.setVisible(false);
        cellTextInput.setVisible(false);
        cellListArea.setVisible(false);
        cellList = new ArrayList<>();
    }

    public void setMaze(Maze maze,String fileName) {
        this.maze = maze;
        this.fileName = fileName;
        double lx = mazePanel.getLayoutX(),
                ly = mazePanel.getLayoutY(),
                ph = mazePanel.getPrefHeight(),
                pw = mazePanel.getPrefWidth();
        this.mPanel = new MazePanel(maze.h,maze.w,(int) ph,(int) pw);
        mPanel.setLayoutX(lx);
        mPanel.setLayoutY(ly);
        backgroundPane.getChildren().add(mPanel);
        backgroundPane.getChildren().remove(mazePanel);
        mazePanel = mPanel;
        GUIMethods.renderSpinner(xSpinner,0,maze.h);
        GUIMethods.renderSpinner(ySpinner,0,maze.h);
        for(int i=0;i<mPanel.h;i++){
            for(int j=0;j<mPanel.w;j++){
                final int x = i, y = j;
                mPanel.cellsMatrix[i][j].setOnMouseClicked(e -> {
                    setPutSpinners(x,y);
                });
            }
        }

        mPanel.renderMaze(maze);
        resetSetChoice();

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

    private void elementsVis(boolean vis, Node ...nodes){
        for(Node n : nodes) n.setVisible(vis);
    }

    private void renderCellInputs(boolean typeFromChoice){
        int x = xSpinner.getValue(),
                y = ySpinner.getValue();
        var cell = maze.cellAt(x,y);
        MazeCellType type = (typeFromChoice) ?
                MazeCellType.values()[cellTypeChoice.getSelectionModel().getSelectedIndex()] :
                cell.type();
        switch(type){
            case LIMITE,MURO,PERCORSO,MURO_INVISIBILE,INTERRUTTORE ->
                    elementsVis(false,booleanChoice,cellSubTypeChoice,nSpinner,cellChoice,
                            plusBtn,minusBtn,cellTextInput,cellListArea);
            case INIZIO_FINE -> {
                elementsVis(true,booleanChoice);
                elementsVis(false,cellSubTypeChoice,nSpinner,cellChoice,
                        plusBtn,minusBtn,cellTextInput,cellListArea);
                if(!typeFromChoice){
                    booleanChoice.setText("Inizio");
                    booleanChoice.setSelected(((InizioFine) cell).isStart);
                }else{
                    booleanChoice.setText("Inizio");
                    booleanChoice.setSelected(true);
                }
            }
            case PORTA -> {
                elementsVis(true,cellSubTypeChoice);
                elementsVis(false,booleanChoice,nSpinner,cellChoice,
                        plusBtn,minusBtn,cellTextInput,cellListArea);
                var l = new ArrayList<String>();
                l.add("A chiavi");l.add("A interruttori");
                cellSubTypeChoice.setItems(FXCollections.observableArrayList(l));
                if(!typeFromChoice){
                    Porta porta = (Porta) cell;
                    cellSubTypeChoice.setOnAction(e -> {
                        int px = xSpinner.getValue(),
                                py = ySpinner.getValue();
                        var pcell = maze.cellAt(px,py);
                        if(pcell.type().isPorta()){
                            Porta porta1 = (Porta) pcell;
                            if(cellSubTypeChoice.getSelectionModel().getSelectedIndex() == 0){
                                elementsVis(true,nSpinner);
                                elementsVis(false,cellChoice,plusBtn,minusBtn,cellListArea);
                                nSpinner.getValueFactory().setValue((porta1.isChiavi()) ? porta1.nChiavi : 1);
                            }else{
                                elementsVis(true,cellChoice,plusBtn,minusBtn,cellListArea);
                                elementsVis(false,nSpinner);
                                resetCellList(porta1.interruttori);
                                cellList.clear();
                                cellList.addAll(porta1.interruttori);
                            }
                        }
                    });
                    cellSubTypeChoice.getSelectionModel().select(porta.type);
                }else{
                    cellSubTypeChoice.setOnAction(e -> {
                        if(cellSubTypeChoice.getSelectionModel().getSelectedIndex() == 0){
                            elementsVis(true,nSpinner);
                            elementsVis(false,cellChoice,plusBtn,minusBtn,cellListArea);
                            nSpinner.getValueFactory().setValue(1);
                        }else{
                            elementsVis(true,cellChoice,plusBtn,minusBtn,cellListArea);
                            elementsVis(false,nSpinner);
                            cellList.clear();
                            resetCellList(cellList);
                        }
                    });
                    cellSubTypeChoice.getSelectionModel().selectFirst();
                }
            }
            case TESORO -> {
                elementsVis(true,cellTextInput);
                elementsVis(false,booleanChoice,cellSubTypeChoice,nSpinner,cellChoice,
                        plusBtn,minusBtn,cellListArea);
                if(!typeFromChoice){
                    Tesoro tesoro = (Tesoro) cell;
                    cellTextInput.setText(tesoro.oggetto());
                }else{
                    cellTextInput.setText("");
                }
            }
            case TRAPPOLA -> {
                elementsVis(true,nSpinner);
                elementsVis(false,booleanChoice,cellSubTypeChoice,cellChoice,
                        plusBtn,minusBtn,cellTextInput,cellListArea);
                if(!typeFromChoice){
                    Trappola trappola = (Trappola) cell;
                    nSpinner.getValueFactory().setValue(trappola.danni);
                }else{
                    nSpinner.getValueFactory().setValue(1);
                }
            }
            case TELETRASPORTO -> {
                elementsVis(true,cellChoice);
                elementsVis(false,booleanChoice,cellSubTypeChoice,nSpinner,
                        plusBtn,minusBtn,cellTextInput,cellListArea);
                if(!typeFromChoice){
                    Teletrasporto t = (Teletrasporto) cell;
                    var list = new ArrayList<String>();
                    var tCells = maze.getAllOfTypes(MazeCellType.TELETRASPORTO);
                    tCells.remove(t);
                    tCells.remove(maze.cellAt(t.ex,t.ey));
                    for(MazeCell tCell : tCells)
                        list.add(tCell.x+","+tCell.y);
                    if(list.isEmpty()) {
                        elementsVis(false,cellChoice);
                        return;
                    }
                    cellChoice.setItems(FXCollections.observableArrayList(list));
                    cellChoice.getSelectionModel().selectFirst();
                }else{
                    var list = new ArrayList<String>();
                    var tCells = maze.getAllOfTypes(MazeCellType.TELETRASPORTO);
                    for(MazeCell tCell : tCells)
                        list.add(tCell.x+","+tCell.y);
                    if(list.isEmpty()) {
                        elementsVis(false,cellChoice);
                        return;
                    }
                    cellChoice.setItems(FXCollections.observableArrayList(list));
                    cellChoice.getSelectionModel().selectFirst();
                }
            }
        }
    }

    private void renderCellInfos(MazeCell cell){
        cellInfoTxt.setText(cell.toString());
        renderCellInputs(false);
    }

    public void onDistanze(ActionEvent event) { //todo: vedere se togliere
        mPanel.renderMaze(maze);
        var middle = maze.middleDistancesInizioToFine().get(0).cell;
        mPanel.cellsMatrix[middle.x][middle.y].setStyle("-fx-background-color: yellow");
    }

    private void renderSet(int idx,boolean choice){
        if(idx < 0) return;
        mPanel.renderMaze(maze);
        var set = maze.walkSets.get(idx);
        mPanel.renderSet(set,choice);
    }

    public void onOpp2WalkSet(ActionEvent event) {
        int idx = setChoice.getSelectionModel().getSelectedIndex();
        renderSet(idx,true);
        var o2Set = maze.oppNoWalk2Set(maze.walkSets.get(idx));
        if(o2Set == null){
            GUIMethods.showError("Set troppo piccolo!");
            return;
        }
        mPanel.colorCells(maze.oppNoWalk2Set(maze.walkSets.get(idx)),"pink");
    }

    public void on3WalkSet(ActionEvent event) {
        int idx = setChoice.getSelectionModel().getSelectedIndex();
        renderSet(idx,true);
        var w3Set = maze.noWalk3Set(maze.walkSets.get(idx));
        if(w3Set == null){
            GUIMethods.showError("Set troppo piccolo!");
            return;
        }
        mPanel.colorCells(w3Set,"pink");
    }

    public void onBestOpp2Sep(ActionEvent event) {
        mPanel.renderMaze(maze);
        var bestOpp2 = maze.bestSeparatorOpp2(percSpinner.getValue(),
                maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex()));
        if(bestOpp2 != null){
            mPanel.cellsMatrix[bestOpp2.x][bestOpp2.y].setStyle("-fx-background-color: turquoise");
            xSpinner.getValueFactory().setValue(bestOpp2.x);
            ySpinner.getValueFactory().setValue(bestOpp2.y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.PORTA.ordinal());
            if(cellSubTypeChoice.getItems().size() == 2)
                cellSubTypeChoice.getSelectionModel().selectFirst();
        }else
            GUIMethods.showError("Set troppo piccolo deve avere almeno 30 celle");
    }

    public void onBest3(ActionEvent event) {
        mPanel.renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var best3 = maze.bestsNoWalks3(set,best3QtySpinner.getValue());
        if(best3 != null && !best3.isEmpty()) {
            xSpinner.getValueFactory().setValue(best3.get(0).x);
            ySpinner.getValueFactory().setValue(best3.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.INTERRUTTORE.ordinal());
            celle.clear();
            celle.addAll(best3);
            addCelleBtn.setVisible(true);
            mPanel.colorCells(best3,"turquoise");
        }
    }

    public void onRandomOpp2(ActionEvent event) {
        mPanel.renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var ro2 = maze.randomOppNoWalk2(set, randomOpp2Spinner.getValue());
        if(ro2 != null && !ro2.isEmpty()){
            xSpinner.getValueFactory().setValue(ro2.get(0).x);
            ySpinner.getValueFactory().setValue(ro2.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.PORTA.ordinal());
            if(cellSubTypeChoice.getItems().size() == 2)
                cellSubTypeChoice.getSelectionModel().selectFirst();
            celle.clear();
            celle.addAll(ro2);
            addCelleBtn.setVisible(true);
            mPanel.colorCells(ro2,"turquoise");
        }
    }

    public void onRandom3(ActionEvent event) {
        mPanel.renderMaze(maze);
        var set = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var ro3 = maze.randomNoWalk3(set,random3Spinner.getValue());
        if(ro3 != null && !ro3.isEmpty()){
            xSpinner.getValueFactory().setValue(ro3.get(0).x);
            ySpinner.getValueFactory().setValue(ro3.get(0).y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.INTERRUTTORE.ordinal());
            celle.clear();
            celle.addAll(ro3);
            addCelleBtn.setVisible(true);
            mPanel.colorCells(ro3,"turquoise");
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
        mPanel.renderMaze(maze);
        var setA = maze.walkSets.get(setChoice.getSelectionModel().getSelectedIndex());
        var setB = maze.walkSets.get(setChoice1.getSelectionModel().getSelectedIndex());
        var ts = maze.randomTeleports(setA,setB);
        if(ts != null){
            teles = ts;
            xSpinner.getValueFactory().setValue(teles[0].x);
            ySpinner.getValueFactory().setValue(teles[0].y);
            cellTypeChoice.getSelectionModel().select(MazeCellType.TELETRASPORTO.ordinal());
            for(MazeCell ms : ts)
                mPanel.cellsMatrix[ms.x][ms.y].setStyle("-fx-background-color: turquoise");
            addTeleBtn.setVisible(true);
        }else
            GUIMethods.showError("I Set hanno intersezione");
    }

    public void onAddTele(ActionEvent event) {
        if(teles != null && teles.length == 2){
            cellTypeChoice.getSelectionModel().select(MazeCellType.TELETRASPORTO.ordinal());
            maze.cells[teles[0].x][teles[0].y] = teles[0];
            maze.cells[teles[1].x][teles[1].y] = teles[1];
            teles = null;
            addTeleBtn.setVisible(false);
            mPanel.renderMaze(maze);
            resetSetChoice();
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
                if(!maze.hasInizioOrFine(start)) {
                    if(start){
                        if(maze.isValidInizio(x,y))
                            maze.cells[x][y] = new InizioFine(x,y,true);
                        else
                            GUIMethods.showError("Inizio non valido!");
                    }else{
                        var inizio = maze.getInizio();
                        if(inizio == null){
                            GUIMethods.showError("Fine non impostabile, manca l'Inizio!");
                            return;
                        }
                        if(maze.isValidFine(inizio.x,inizio.y,x,y))
                            maze.cells[x][y] = new InizioFine(x,y,false);
                        else
                            GUIMethods.showError("Fine non valida!");
                    }
                }else
                    GUIMethods.showError(((start) ? "Inizio" : "Fine")+" già presente!");
            }
            case PORTA -> {
                if(maze.isOppNoWalk2(cell)){
                    int subType = cellSubTypeChoice.getSelectionModel().getSelectedIndex();
                    if(subType == Porta.PORTA_A_CHIAVI){
                        maze.cells[x][y] = new Porta(x,y,false,nSpinner.getValue());
                    }else if(subType == Porta.PORTA_A_INTERRUTTORI){
                        if(cell.type().isPorta()){
                            var interrs = maze.topoCorrectInterruttori((Porta) cell,cellList);
                            if(interrs != null && !interrs.isEmpty())
                                maze.cells[x][y] = new Porta(x,y,false,interrs);
                            else
                                GUIMethods.showError("Insieme di Interruttori topologicamente invalido\n" +
                                        "o nessun Interruttore specificato!");
                        }else
                            GUIMethods.showError("La Porta a cui modificare gli Interruttori non è una Porta");
                    }
                }else
                    GUIMethods.showError("Invalid cell for Porta");
            }
            case INTERRUTTORE -> {
                if(maze.isNoWalk3(cell))
                    maze.cells[x][y] = new Interruttore(x,y,false);
                else
                    GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case TESORO -> {
                if(maze.isNoWalk3(cell))
                    maze.cells[x][y] = new Tesoro(x,y,cellTextInput.getText(),false);
                else
                    GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case TRAPPOLA -> {
                if(maze.isNoWalk3(cell))
                    maze.cells[x][y] = new Trappola(x,y,nSpinner.getValue(),false);
                else
                    GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
            case MURO_INVISIBILE -> {
                if(maze.isOppNoWalk2(cell))
                    maze.cells[x][y] = new MuroInvisibile(x,y);
                else
                    GUIMethods.showError("Invalid cell for Muro Invisibile");
            }
            case TELETRASPORTO -> {
                if(maze.isNoWalk3(cell)) {
                    if(cellChoice.getValue() != null &&
                            cellChoice.getValue().split(",").length == 2){
                        String[] ch = cellChoice.getValue().split(",");
                        int tx = Integer.parseInt(ch[0]), ty = Integer.parseInt(ch[1]);
                        var tCell = maze.cellAt(tx,ty);
                        if(tCell.type().isTeletrasporto()){
                            Teletrasporto t = (Teletrasporto) tCell;
                            if(t.x != x && t.y != y){
                                maze.cells[x][y] = new Teletrasporto(x,y,t.x,t.y);
                            }else{
                                maze.cells[x][y] = new Teletrasporto(x,y);
                                GUIMethods.showWarning("Nessun end point specificato per questo teletrasporto");
                            }
                        }
                    }else{
                        maze.cells[x][y] = new Teletrasporto(x,y);
                        GUIMethods.showWarning("Nessun end point specificato per questo teletrasporto");
                    }
                }
                else
                    GUIMethods.showError("Invalid cell for Interruttore,Tesoro,Trappola o Teletrasporto");
            }
        }
        mPanel.renderMaze(maze);
        resetSetChoice();
    }

    public void onPlusCell(ActionEvent event) {
        if(cellList.size() >= 10){
            GUIMethods.showError("Lista piena! (max 10 elementi)");
            return;
        }
        String[] ch = cellChoice.getValue().split(",");
        if(ch.length == 2){
            int x = Integer.parseInt(ch[0]),
                    y = Integer.parseInt(ch[1]);
            cellList.add(maze.cellAt(x,y));
            resetCellList(cellList);
        }else
            GUIMethods.showError("Nessuna casa selezionata");
    }

    public void onMinusCell(ActionEvent event) {
        if(cellList.isEmpty()){
            GUIMethods.showError("Lista vuota!");
            return;
        }
        cellList.remove(cellList.size()-1);
        resetCellList(cellList);
    }

    private void resetCellList(List<? extends MazeCell> celle){
        var list = new ArrayList<String>();
        var mazeInterr = maze.getAllOfTypes(MazeCellType.INTERRUTTORE);
        mazeInterr.removeAll(celle);
        for(MazeCell inter : mazeInterr)
            list.add(inter.x+","+inter.y);
        if(mazeInterr.isEmpty()){
            elementsVis(false,cellChoice);
        }else{
            cellChoice.setItems(FXCollections.observableArrayList(list));
            cellChoice.getSelectionModel().selectFirst();
        }
        String s = "";
        for(MazeCell i : celle) s += i+",";
        if(s.endsWith(",")) s = s.substring(0,s.length()-1);
        cellListArea.setText(s);
    }

    public void onShowSets(ActionEvent event) {
        mPanel.renderMaze(maze);
        for(int i=0;i<maze.walkSets.size();i++)
            for(MazeCell cell : maze.walkSets.get(i))
                if(cell.type().isPercorso()) mPanel.cellsMatrix[cell.x][cell.y]
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
