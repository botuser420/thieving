package scripts.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tribot.api.General;
import org.tribot.api.Timing;
import scripts.utilities.gui.WindowStyle;

import javax.swing.*;
import java.net.URL;

public class GUI extends Application {

    private URL fxml;
    private URL stylesheet;

    private Stage stage;
    private Scene scene;
    private Controller controller;
    private boolean decorated = true;

    public boolean stopScript = false;

    private static double xOffset = 0;
    private static double yOffset = 0;

    private boolean isOpen = false;

    public GUI(URL fxml, boolean decorated) {
        this(fxml, null, decorated);
    }

    public GUI(URL fxml, URL stylesheet) {
        this(fxml, stylesheet, true);
    }

    public GUI(URL fxml, URL stylesheet, boolean decorated) {


        this.fxml = fxml;
        this.stylesheet = stylesheet;
        this.decorated = decorated;

        // We have to start the JFX thread from the EDT otherwise tribot will end it.
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // we have to init the toolkit
            Platform.runLater(() -> {
                try {
                    final Stage stage = new Stage();
                    start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        waitForInit();
    }

    public Scene getScene() {
        return this.scene;
    }

    public Stage getStage() {
        return this.stage;
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set. The primary stage will be embedded in
     *              the browser if the application was launched as an applet.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage stage) throws Exception {

        if (fxml == null) {
            General.println("fxml is null. aborting");
            return;
        }

        this.stage = stage;
        stage.setAlwaysOnTop(true);
        Platform.setImplicitExit(false);

        FXMLLoader loader = new FXMLLoader(fxml);

        // By default FXMLLoader uses a different classloader, this caused issues with upcasting
        loader.setClassLoader(this.getClass().getClassLoader());
        Parent box = loader.load();

        controller = loader.getController();
        if (controller == null) {
            General.println("Please add a controller to your fxml!");
            return;
        }

        controller.setGui(this);
        stage.setTitle("Script by Breaker");
        stage.setResizable(false);
        if (!this.decorated) {
            stage.initStyle(StageStyle.UNDECORATED);

        }



        scene = new Scene(box);



        if (this.stylesheet != null)
            scene.getStylesheets().add(this.stylesheet.toExternalForm());

        stage.setScene(scene);

        WindowStyle.allowDrag(box, stage);
    }


    public <T extends Controller> T getController() {

        return (T) this.controller;

    }

    public void show() {

        if (stage == null)
            return;

        isOpen = true;

        Platform.runLater(() -> stage.show());
    }

    public void close() {

        if (stage == null)
            return;

        isOpen = false;

        Platform.runLater(() -> stage.close());
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void waitForInit() {
        Timing.waitCondition(() -> stage != null, 5000);
    }




    /*public GUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                launch();
            }
        });
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ThievingGUI.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }*/


}
