package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.DivHyperMaze;
import com.flavio.rognoni.purgatory.purgatory.mazes.HyperMaze;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class EditDivHyperMazeController implements Initializable {

    public AnchorPane backgroundPane;
    public AnchorPane matrixPanel;
    public Button backBtn;
    public TextArea mazeInfoTxt;
    public ChoiceBox<String> mazeChoice;
    public Button saveBtn;
    public Spinner<Integer> sxSpinner,sySpinner,
            exSpinner,eySpinner;
    public Label hTxt;
    public Label wTxt;
    private int dh,dw;
    private Integer h,w;
    private Label[][] matrixCells;
    private MatrixContent[][] contents;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mazeChoice.setItems(FXCollections.observableArrayList(
                Arrays.asList(Objects.requireNonNull(new File(HomeController.MAZE_PATH).list())))
        );
        GUIMethods.renderSpinner(sxSpinner,0, HyperMaze.MAX_DIM-1);
        GUIMethods.renderSpinner(sySpinner,0,HyperMaze.MAX_DIM-1);
        GUIMethods.renderSpinner(exSpinner,0,HyperMaze.MAX_DIM-1);
        GUIMethods.renderSpinner(eySpinner,0,HyperMaze.MAX_DIM-1);
        h = null;
        w = null;
    }

    public void setD(int dh,int dw) {
        this.dh = dh;
        this.dw = dw;
        buildHyperMatrix();
        mazeChoice.setOnAction(e -> {
            setHW();
        });
        mazeChoice.getSelectionModel().selectFirst();
        contents = new MatrixContent[dh][dw];
        for(int i=0;i<dh;i++)
            for(int j=0;j<dw;j++)
                contents[i][j] = null;
    }

    private void buildHyperMatrix(){
        matrixCells = new Label[dh][dw];
        int aph = (int) matrixPanel.getPrefHeight(),
                apw = (int) matrixPanel.getPrefWidth(),
                ch = (aph-dh*10)/dh,cw = (apw-dw*10)/dw;
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        for(int i=0;i<dh;i++){
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            for(int j=0;j<dw;j++){
                Label mCell = new Label(i+","+j);
                final int x = i, y = j;
                mCell.setOnMouseClicked(e -> setMaze(x,y));
                mCell.setMinHeight(ch);mCell.setPrefHeight(ch);mCell.setMaxHeight(ch);
                mCell.setMinWidth(cw);mCell.setPrefWidth(cw);mCell.setMaxWidth(cw);
                mCell.setFont(new Font("Verdana", (double) ch /16));
                mCell.setStyle("-fx-background-radius: 50px; -fx-border-radius: 50px; " +
                        "-fx-background-color: black");
                mCell.setTextFill(Color.WHITE);
                mCell.setAlignment(Pos.CENTER);
                matrixCells[i][j] = mCell;
                hBox.getChildren().add(mCell);
            }
            hBox.setLayoutX(0);
            hBox.setLayoutY(0);
            vBox.getChildren().add(hBox);
        }
        vBox.setLayoutX(0);
        vBox.setLayoutY(0);
        matrixPanel.getChildren().add(vBox);
    }

    private void setMaze(int x,int y){
        if(h == null || w == null){
            GUIMethods.showError("h or w are not defined for this maze "+
                    mazeChoice.getValue());
            return;
        }
        if(contents[x][y] == null){
            if(invalidAddToMatrix(x,y)){
                GUIMethods.showError("wrong size for this cell");
                return;
            }
            String n = mazeChoice.getValue();
            MatrixContent mc = new MatrixContent(n,h,w);
            contents[x][y] = mc;
            String s = "";
            for(String t : n.split(" "))
                s += t+"\n";
            s = s.replace(".xml","");
            matrixCells[x][y].setText(s+"\n"+h+"x"+w);
            matrixCells[x][y].setStyle("-fx-background-radius: 50px; -fx-border-radius: 50px; " +
                    "-fx-background-color: white");
            matrixCells[x][y].setTextFill(Color.BLACK);
        }else{
            contents[x][y] = null;
            matrixCells[x][y].setText(x+","+y);
            matrixCells[x][y].setStyle("-fx-background-radius: 50px; -fx-border-radius: 50px; " +
                    "-fx-background-color: black");
            matrixCells[x][y].setTextFill(Color.WHITE);
        }
    }

    private boolean invalidAddToMatrix(int r,int c){
        Integer hr = null, wc = null;
        for(int i=0;i<dw;i++){
            if(contents[r][i] != null){
                hr = contents[r][i].h;
                break;
            }
        }
        for(int i=0;i<dh;i++){
            if(contents[i][c] != null){
                wc = contents[i][c].w;
                break;
            }
        }
        if(hr == null && wc == null)
            return false;
        else if(hr == null)
            return !wc.equals(w);
        else if(wc == null)
            return !hr.equals(h);
        else
            return !hr.equals(h) || !wc.equals(w);
    }

    private void setHW(){
        String n = mazeChoice.getValue();
        InputStream is = App.class.getResourceAsStream("labirinti/"+n);
        var hw = Maze.hwFromXML(is);
        if(hw != null){
            h = hw[0];
            w = hw[1];
            hTxt.setText("h="+h);
            wTxt.setText("w="+w);
        }else{
            h = null;
            w = null;
            hTxt.setText("h err");
            wTxt.setText("w err");
            GUIMethods.showError("Altezza o Larghezza labirinto non definite per "+
                    mazeChoice.getValue());
        }
    }

    public void onSaveHyperMaze(ActionEvent event) {
        try{
            List<Maze> mazeList = new ArrayList<>();
            Map<String,Maze> mazeMap = new HashMap<>();
            for(int i=0;i<dh;i++){
                for(int j=0;j<dw;j++){
                    if(mazeMap.get(contents[i][j].mId) == null){
                        InputStream is = App.class.getResourceAsStream("labirinti/"+contents[i][j].mId);
                        mazeMap.put(contents[i][j].mId,Maze.mazeFromXML(is));
                    }
                    if(mazeMap.get(contents[i][j].mId) != null)
                        mazeList.add(mazeMap.get(contents[i][j].mId));
                }
            }
            DivHyperMaze hm = new DivHyperMaze(dh,dw,mazeList,
                    sxSpinner.getValue(),sySpinner.getValue(),
                    exSpinner.getValue(),eySpinner.getValue());
            Maze m = hm.getMaze();
            Maze.mazeToXML(m,null);
        }catch(Exception e){
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

    private static class MatrixContent{
        public final String mId;
        public final int h,w;

        public MatrixContent(String mId,int h,int w){
            this.mId = mId;
            this.h = h;
            this.w = w;
        }

    }

}
