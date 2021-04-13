package scripts.gui;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.tribot.api.General;
import org.tribot.util.Util;
import scripts.ThievingTrainer;
import scripts.construction.Construction;
import scripts.construction.house.BuyHouse;
import scripts.thieving.Thieving;
import scripts.thieving.ThievingMethod;
import scripts.utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.ResourceBundle;

@DoNotRename
public class Controller implements Initializable {

    private final String profilePath = "\\BreakerScripts\\Thieving\\";
    DecimalFormat format = new DecimalFormat("#");


    @Setter
    @Getter
    private GUI gui = null;

    @FXML
    @DoNotRename
    private Button btnProfiles;

    @FXML
    @DoNotRename
    private Button btnScriptSettings;

    @FXML
    @DoNotRename
    private Button btnSettings;

    @FXML
    @DoNotRename
    private Button btnAntiban;

    @FXML
    @DoNotRename
    private Button btnHelp;

    @FXML
    @DoNotRename
    private Button btnStart;

    @FXML
    @DoNotRename
    private Button btnRight;

    @FXML
    @DoNotRename
    private Button btnLeft;

    @FXML
    @DoNotRename
    private Pane pnlProfiles;

    @FXML
    @DoNotRename
    private Pane pnlScriptSettings;

    @FXML
    @DoNotRename
    private Pane pnlSettings;

    @FXML
    @DoNotRename
    private Pane pnlAntiban;

    @FXML
    @DoNotRename
    private Pane pnlHelp;

    @FXML
    @DoNotRename
    private TitledPane arguments;

    @FXML
    @DoNotRename
    private ListView<String> listProfiles = new ListView<>();

    @FXML
    @DoNotRename
    private TableView<ThievingMethod> tableAvailable;

    @FXML
    @DoNotRename
    private TableColumn<ThievingMethod, String> availName;

    @FXML
    @DoNotRename
    private TableColumn<ThievingMethod, Number> availLvl;

    @FXML
    @DoNotRename
    private TableView<ThievingMethod> tableUsed;

    @FXML
    @DoNotRename
    private TableColumn<ThievingMethod, String> usedName;

    @FXML
    @DoNotRename
    private TableColumn<ThievingMethod, Number> usedLvl;

    @FXML
    @DoNotRename
    private TextField textProfileName;

    @FXML
    @DoNotRename
    private TextField stopLvl = new TextField();

    @SneakyThrows
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        createDirectory();
        populateMethods();
        readProfiles();
        setNumbersOnly(stopLvl);
    }

    private void createDirectory() {
        FileUtilities.createSubdirectories(profilePath);
    }

    private void readProfiles() {
        listProfiles.getItems().setAll(FileUtilities.getFileNamesWithoutExtension(profilePath));
    }

    @FXML
    @DoNotRename
    public void saveProfile(ActionEvent event) {
        sortTable();
        if (event.getSource() == btnStart)
            textProfileName.setText("last");
        if (!textProfileName.getText().isEmpty()) {
            GuiSettings settings = new GuiSettings();
            settings.setStopLevel(stopLvl.getText());
            settings.setTableAvailable(tableAvailable.getItems());
            settings.setTableUsed(tableUsed.getItems());
            FileUtilities.createFile(new Gson().toJson(settings), profilePath + textProfileName.getText() + ".json");
            readProfiles();
        }
    }

    @FXML
    @DoNotRename
    public void loadProfile(ActionEvent event) throws IOException {
        if (!listProfiles.getSelectionModel().getSelectedItem().isEmpty()) {
            loadProfile(listProfiles.getSelectionModel().getSelectedItem());
        } else
            General.println("No profile to load selected");
    }

    public void loadProfile(String profileName) throws IOException {
        GuiSettings settings = new Gson().fromJson(new String(FileUtilities.loadFile(new File(Util.getWorkingDirectory().getAbsolutePath() + profilePath + profileName + ".json"))), GuiSettings.class);
        stopLvl.setText(settings.getStopLevel());
        tableAvailable.getItems().setAll(settings.getTableAvailable());
        tableUsed.getItems().setAll(settings.getTableUsed());
        textProfileName.setText(profileName);
    }

    @FXML
    @DoNotRename
    public void deleteProfile(ActionEvent event) throws IOException {
        if (!listProfiles.getSelectionModel().getSelectedItem().isEmpty()) {
            FileUtilities.deleteFile(profilePath + listProfiles.getSelectionModel().getSelectedItem() + ".json");
            readProfiles();
        } else
            General.println("No profile to delete selected");
    }


    private void populateMethods() throws IOException {
        availName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        availLvl.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReqLvl()));
        usedName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        usedLvl.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReqLvl()));
        if (FileUtilities.checkExistance(profilePath + "last.json")) {
            General.println("Loading last settings");
            loadProfile("last");
        } else {
            General.println("Profile last not found");
            tableAvailable.getItems().addAll(ThievingMethod.values());
        }
    }

    private void setNumbersOnly(TextField field) {
        // force the field to be numeric only
        field.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        }));
    }

    @FXML
    @DoNotRename
    public void transferMethod(ActionEvent event) {
        if (event.getSource() == btnRight && !tableAvailable.getSelectionModel().isEmpty()) {
            ThievingMethod thievingMethod = tableAvailable.getSelectionModel().getSelectedItem();
            tableUsed.getItems().add(thievingMethod);
            tableAvailable.getItems().remove(thievingMethod);
            tableUsed.sort();
        }
        if (event.getSource() == btnLeft && !tableUsed.getSelectionModel().isEmpty()) {
            ThievingMethod thievingMethod = tableUsed.getSelectionModel().getSelectedItem();
            tableAvailable.getItems().add(thievingMethod);
            tableUsed.getItems().remove(thievingMethod);
            tableAvailable.sort();
        }
    }

    @FXML
    @DoNotRename
    public void handleClicks(ActionEvent event) {
        if (event.getSource() == btnProfiles) {
            pnlProfiles.toFront();
        }
        if (event.getSource() == btnScriptSettings) {
            pnlScriptSettings.toFront();
        }
        if (event.getSource() == btnSettings) {
            pnlSettings.toFront();
        }
        if (event.getSource() == btnAntiban) {
            pnlAntiban.toFront();
        }
        if (event.getSource() == btnHelp) {
            pnlHelp.toFront();
        }
    }

    @FXML
    @DoNotRename
    public void closeGui() throws Exception {
        gui.stopScript = true;
        gui.close();
    }

    @FXML
    @DoNotRename
    public void minimizeGui() throws Exception {
        ((Stage) gui.getScene().getWindow()).setIconified(true);
    }

    @FXML
    @DoNotRename
    public void startScript(ActionEvent event) {
        if (tableUsed.getItems().isEmpty()) {
            General.println("No stall selected. Try again....");
            return;
        }
        if (stopLvl.getText().isEmpty()) {
            General.println("No level to stop entered. Try again...");
            return;
        }

        saveProfile(event);

        List<ThievingMethod> methods = tableUsed.getItems();
        int stopLevel = Integer.parseInt(stopLvl.getText());
        for (ThievingMethod method : methods){
            General.println("Doing " + method.getName() + " till level " + stopLevel);
            ThievingTrainer.events.add(new Thieving(method, stopLevel));
            stopLevel = method.getReqLvl();
        }
        gui.close();

    }

    private void sortTable() {
        usedLvl.setSortType(TableColumn.SortType.DESCENDING);
        tableUsed.getSortOrder().add(usedLvl);
        tableUsed.sort();
    }


}
