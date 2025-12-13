package com.flavio.rognoni.purgatory.purgatory;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class GUIMethods {

    public static void showInfo(String info){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,info, ButtonType.OK);
        alert.show();
    }

    public static void showWarning(String warn){
        Alert alert = new Alert(Alert.AlertType.WARNING,warn, ButtonType.OK);
        alert.show();
    }

    public static void showError(String err){
        Alert alert = new Alert(Alert.AlertType.ERROR,err, ButtonType.OK);
        alert.show();
    }

    public static void showConfirm(String conf){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,conf, ButtonType.OK, ButtonType.NO);
        alert.show();
    }

    public static void renderSpinner(Spinner<Integer> spinner, int lower, int upper){
        spinner.setEditable(true);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(lower,upper,lower,1));
        spinner.getEditor().textProperty().addListener((e, old, newV) -> {
            try{
                Integer.parseInt(newV);
            }catch (Exception ex){
                spinner.getEditor().textProperty().setValue(String.valueOf(lower));
            }
        });
        spinner.getEditor().setAlignment(Pos.CENTER);
    }

}
