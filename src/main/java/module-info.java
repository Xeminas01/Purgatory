module com.flavio.rognoni.purgatory.purgatory {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.flavio.rognoni.purgatory.purgatory to javafx.fxml;
    exports com.flavio.rognoni.purgatory.purgatory;
    exports com.flavio.rognoni.purgatory.purgatory.mazes;
    exports com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;
    exports com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

}