package com.flavio.rognoni.purgatory.purgatory;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

}
