package app.controller;

import app.MainApp;

import app.service.Convolution;
import app.service.KMeans;
import app.service.PreferenceService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageOverviewController
{
    private MainApp mainApp;
    @FXML private ImageView pic1;
    @FXML private ImageView pic2;
    @FXML private javafx.scene.control.TextField inputConvolution;
    private BufferedImage bufferedImage;

    public void setMainApp(MainApp mainApp)
    {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleOpenImage()
    {
        File file = mainApp.openImage();

        // При выборе файла нажали кнопку отмена
        if(file != null)
        {
            if(file.exists())
            {
                pic1.setImage(new Image("file:" + file.getAbsolutePath()));
                pic1.setPreserveRatio(true);
                centerImage(pic1);

                // Размытие
                Convolution convolution = new Convolution(file);
                Convolution convolution1 = new Convolution(file);

                // Кластеризация
                KMeans kmeans = new KMeans(convolution.getSourceImage());
                kmeans.removeBackground(convolution.getSourceImage());
                //kmeans.crop();
                //kmeans.crop(convolution1.getSourceImage());

                // Вывод на экран
                pic2.setImage(kmeans.getOutputImage().getWritableImage());
                bufferedImage = kmeans.getOutputImage().getBufferedImage();

                pic2.setPreserveRatio(true);
                centerImage(pic2);

                mainApp.getPrimaryStage().setTitle(file.getAbsolutePath());
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Открытие изображения");
                alert.setContentText("Файл не найден: " + file.getAbsolutePath());
                alert.showAndWait();
            }
            // Сохраняем директорию
            PreferenceService.getInstance().setLastOpenDirectory(file.getAbsolutePath());
        }
    }

    @FXML
    public void handleSaveImage()
    {
        mainApp.saveImage(bufferedImage);
    }

    private void centerImage(ImageView imageView)
    {
        Image img = imageView.getImage();
        if(img != null)
        {
            double w, h;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reduceCoefficient;
            if(ratioX >= ratioY)
                reduceCoefficient = ratioY;
            else
                reduceCoefficient = ratioX;

            w = img.getWidth() * reduceCoefficient;
            h = img.getHeight() * reduceCoefficient;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);
        }
    }
}