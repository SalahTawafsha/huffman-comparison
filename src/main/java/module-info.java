module com.example.huffmancomparison {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.huffmancomparison to javafx.fxml;
    exports com.example.huffmancomparison;
}