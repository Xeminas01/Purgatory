package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

public class VisualizeMazeController implements Initializable {
    public AnchorPane backgroundPane;
    public AnchorPane mazePanel;
    public Button screenShotBtn;
    public Button backBtn;
    public TextArea cellInfoTxt;
    public ChoiceBox<String> setChoice;
    public Button showSetBtn;
    public Button showSetsBtn;
    public Button topoOrdBtn;
    private MazePanel mPanel;
    private Maze maze;
    private Map<Integer,List<Integer>> topoMap;
    private String fileName;
    private int shots;
    public static final int transparenceShadow = -16777216;

    //todo: aggiungere gli hypermaze quando ci saranno
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        shots = 0;
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
        for(int i=0;i<mPanel.h;i++){
            for(int j=0;j<mPanel.w;j++){
                final int x = i, y = j;
                mPanel.cellsMatrix[i][j].setOnMouseClicked(e -> {
                    cellInfoTxt.setText(maze.cellAt(x,y).toString());
                });
            }
        }

        System.out.println("rendermaze");
        mPanel.renderMaze(maze);
        System.out.println("fine rendermaze inizio combo");
        int d = maze.h * maze.w;
        if(d <= 40000) setChoiceCombo();
        System.out.println("fine combo calcolo topo");
        if(d <= 40000) topoMap = maze.topologicalOrderOfWalkSets();
        System.out.println("fine calcolo topo");

    }

    private void setChoiceCombo(){
        var l = new ArrayList<String>();
        var pS = maze.walkSets();
        for(int i=0;i<pS.size();i++)
            l.add("Set<"+i+"> "+pS.get(i).size());
        l.add(0,"");
        setChoice.setItems(FXCollections.observableArrayList(l));
    }

    public void onScreenshot(ActionEvent event) {
        saveImg(mPanel.rowsBox,
                "./src/main/resources/com/flavio/rognoni/purgatory/purgatory/screenshots/"
                        +fileName+"_"+shots+".png",
                true);
        shots++;
    }

    public void onShowSet(ActionEvent event) {
        int idx = setChoice.getSelectionModel().getSelectedIndex()-1;
        Set<MazeCell> set = null;
        if(idx >= 0) {
            set = maze.walkSets.get(idx);
            mPanel.renderSet(set,true);
        }
        for(int i=0;i<maze.walkSets.size();i++) {
            for(MazeCell cell : maze.walkSets.get(i)) {
                if(set != null && set.contains(cell))
                    mPanel.cellsMatrix[cell.x][cell.y].setText(i+"");
                else{
                    mPanel.cellsMatrix[cell.x][cell.y]
                            .setStyle("-fx-background-color: "+cell.color());
                    mPanel.cellsMatrix[cell.x][cell.y].setText("");
                }
            }
        }
    }

    public void onShowSets(ActionEvent event) {
        mPanel.renderMaze(maze);
        for(int i=0;i<maze.walkSets.size();i++) {
            for(MazeCell cell : maze.walkSets.get(i)) {
                if(cell.type().isPercorso()) {
                    mPanel.cellsMatrix[cell.x][cell.y]
                            .setStyle("-fx-background-color: "+
                                    GUIMethods.setColors[i%GUIMethods.setColors.length]);
                    mPanel.cellsMatrix[cell.x][cell.y].setText(i+"");
                }
            }
        }
    }

    public void onTopologicalOrder(ActionEvent event) {
        cellInfoTxt.setText(topoMap.toString());
    }

    public static void saveImg(Node imgPezzoPanel, String path, boolean clear){
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage wi = imgPezzoPanel.snapshot(sp, null);
        System.out.println(wi.getWidth() +" "+wi.getHeight());
        int width = (int) wi.getWidth();
        int height = (int) wi.getHeight();
        int[] pixels = new int[width * height];
        wi.getPixelReader().getPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbInstance(),
                pixels, 0, width
        );
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        System.out.println(bi.getWidth() + " " +bi.getHeight());
        for(int i=0;i<bi.getHeight();i++){
            for(int j=0;j<bi.getWidth();j++){
                int pixel = pixels[i*width+j];
                int r = (pixel & 0xFF0000) >> 16;
                int g = (pixel & 0xFF00) >> 8;
                int b = (pixel & 0xFF);
                bi.getRaster().setPixel(j,i,new int[]{r,g,b,255});
                //System.out.print(bi.getRGB(j,i)+" ");
            }
            //System.out.println();
        }
        //System.out.println(width + " " +height);
        //System.out.println(bi);
        if(clear) clearBI(bi);
        else clearBIInv(bi);
        //System.out.println(bi);
        File file = new File(path);
        try{
            ImageIO.write(bi,"png",file);
            System.out.println("salvata: "+path);
        }catch (Exception e){
            System.out.println("non riseco a scriver l'immagine!");
        }
    }

    private static void clearBI(BufferedImage bi){
        for(int i=0;i<bi.getHeight();i++){
            for(int j=0;j<bi.getWidth();j++){
                if(bi.getRGB(i,j) == transparenceShadow) {
                    //System.out.print("pixel("+i+","+j+"): "+bi.getRGB(i,j)+" ");
                    bi.setRGB(i,j,0);
                    //System.out.print("pixel("+i+","+j+"): "+bi.getRGB(i,j)+" ");
                }
            }
            //System.out.println();
        }
    }

    private static void clearBIInv(BufferedImage bi){
        for(int i=0;i<bi.getHeight();i++){
            for(int j=0;j<bi.getWidth();j++){
                if(bi.getRGB(j,i) == transparenceShadow) {
                    //System.out.print("pixel("+i+","+j+"): "+bi.getRGB(i,j)+" ");
                    bi.setRGB(j,i,0);
                    //System.out.print("pixel("+i+","+j+"): "+bi.getRGB(i,j)+" ");
                }
            }
            //System.out.println();
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
