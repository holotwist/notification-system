module com.topglobales.comportamientoptrn.patronescomportamiento {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;

    opens com.topglobales.comportamientoptrn.patronescomportamiento to javafx.fxml;
    exports com.topglobales.comportamientoptrn.patronescomportamiento;
}