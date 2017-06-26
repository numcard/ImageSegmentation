package app;

import app.controller.ImageOverviewController;
import app.controller.RootLayoutController;
import app.service.PreferenceService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainApp extends Application
{
    private final static String APP_NAME = "Кадрирование изображений";
    private Stage primaryStage;
    private BorderPane rootLayout;
    private final PreferenceService preferenceService = PreferenceService.getInstance();
    private ImageOverviewController imageOverviewController;
    public Stage getPrimaryStage()
    {
        return primaryStage;
    }
    public ImageOverviewController getImageOverviewController()
    {
        return imageOverviewController;
    }

    // Точка входа
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        File iconFile = new File("src/app/resource/icon.jpg");
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(APP_NAME);
        this.primaryStage.getIcons().add(new Image("file:" + iconFile.getAbsolutePath()));
        initRootLayout();
        showImageOverview();
    }

    private void initRootLayout()
    {
        try
        {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Даём контроллеру доступ к главному приложению.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        }
        catch(IOException e)
        {
            AppException.Throw(e);
        }
    }

    private void showImageOverview()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ImageOverview.fxml"));
            AnchorPane lotOverviewPage = loader.load();

            rootLayout.setCenter(lotOverviewPage);

            imageOverviewController = loader.getController();
            imageOverviewController.setMainApp(this);
        }
        catch(IOException e)
        {
            AppException.Throw(e);
        }
    }

    public File openImage()
    {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);

        File lastFolder = new File(preferenceService.getLastOpenDirectory());
        if(lastFolder.exists())
            fileChooser.setInitialDirectory(lastFolder);

        return fileChooser.showOpenDialog(primaryStage);
    }

    public void saveImage(BufferedImage image)
    {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);

        // Ищем последний путь сохранения
        File lastFolder = new File(preferenceService.getLastOpenDirectory());
        if(lastFolder.exists())
            fileChooser.setInitialDirectory(lastFolder);

        // Показываем диалог сохранения файла
        File file = fileChooser.showSaveDialog(primaryStage);
        if(file != null && image != null)
        {
            if(!file.exists())
                try
                {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                catch(IOException e)
                {
                    AppException.Throw(e);
                }

            if(!file.getPath().endsWith(".jpg"))
            {
                file = new File(file.getPath() + ".jpg");
            }
            try
            {
                ImageIO.write(image, "jpg", file);
            }
            catch(IOException e)
            {
                AppException.Throw(e);
            }
            preferenceService.setLastOpenDirectory(file.getAbsolutePath());
        }
    }
}
