module co.edu.uniquindio.proyectofinalprog2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.proyectofinalprog2 to javafx.fxml;
    exports co.edu.uniquindio.proyectofinalprog2;
}