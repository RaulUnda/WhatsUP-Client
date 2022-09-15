module com.example.whatsupclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.whatsupclient to javafx.fxml;
    exports com.example.whatsupclient;
}