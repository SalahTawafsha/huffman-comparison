module com.example.huffmancomparison {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.huffmancomparison.view to javafx.fxml;
    exports com.example.huffmancomparison.view;
}