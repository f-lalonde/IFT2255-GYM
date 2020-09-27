module com.equipeDix {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.desktop;

    exports com.equipeDix;
    opens com.equipeDix.database to javafx.base;
    exports com.equipeDix.database to javafx.base;

    opens com.equipeDix.logic to javafx.base;
    exports com.equipeDix.logic to javafx.base;

    opens com.rnb to javafx.web;
    exports com.rnb to javafx.web;


}

