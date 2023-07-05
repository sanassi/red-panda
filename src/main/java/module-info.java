module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.apache.lucene.core;
    requires org.apache.lucene.queryparser;
    requires net.bytebuddy;
    requires java.validation;
    requires org.assertj.core;
    requires org.eclipse.jgit;
    requires lombok;
    requires wellbehavedfx;
    requires java.desktop;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}