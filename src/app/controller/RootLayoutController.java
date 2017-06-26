package app.controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class RootLayoutController
{
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp)
    {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleOpenImage()
    {
        mainApp.getImageOverviewController().handleOpenImage();
    }

    @FXML
    public void handleSaveImage()
    {
        mainApp.getImageOverviewController().handleSaveImage();
    }

    @FXML
    private void handleAbout()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Информация");
        alert.setContentText("Автор: Авдеев А.О.");
        alert.showAndWait();
    }
}