package scripts;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;
import org.tribot.util.Util;
import scripts.construction.Construction;
import scripts.construction.house.BuyHouse;
import scripts.dax.api_lib.DaxWalker;
import scripts.framework.event.EventSet;

import scripts.gui.GUI;
import scripts.gui.GuiSettings;
import scripts.paint.PaintInfo;
import scripts.paint.SimplePaint;
import scripts.paint.SkillTracker;
import scripts.thieving.Thieving;
import scripts.thieving.ThievingMethod;
import scripts.utilities.FileUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@ScriptManifest(
        category = "Thieving",
        authors = "Botuser420",
        name = "Thieving by Breaker")
public class ThievingTrainer extends Script implements PaintInfo, Painting, Starting, Arguments {

    public static EventSet events = new EventSet();

    private GUI gui;
    private URL fxml;
    private String profileName;


    private final SimplePaint PAINT = new SimplePaint(this, SimplePaint.PaintLocations.TOP_MID_PLAY_SCREEN,
            new Color[]{new Color(255, 251, 255)}, "Trebuchet MS", new Color[]{new Color(50, 50, 50, 128)},
            new Color[]{new Color(50, 50, 50)}, 2, false, 3, 5, 0);

    private final SkillTracker skillTracker = new SkillTracker(Skills.SKILLS.THIEVING, this);

    public String[] getPaintInfo() {
        return new String[]{
                "Runtime: " + PAINT.getRuntimeString(),
                events.getString(),
                skillTracker.getXpText(),
                skillTracker.getLevelText()
        };
    }

    @SneakyThrows
    @Override
    public void run() {
        if (profileName == null) {
            if (openGUI()) {
                General.println("Closed GUI. Stopping script.");
                return;
            }
        } else
            loadProfile();
        events.execute();
    }

    @Override
    public void onPaint(Graphics graphics) {
        PAINT.paint(graphics);
    }

    @Override
    public void onStart() {
        Walk.setKey();
        DaxWalker.setGlobalWalkingCondition(Walk.getDefaultCondition());
    }

    private boolean openGUI() {
        try {
            //fxml = new File("C:\\Users\\Boaz\\Dropbox\\TRiBot scripts\\scripts\\thieving\\src\\scripts\\gui\\ThievingGUI.fxml").toURI().toURL();
            fxml = new URL("https://raw.githubusercontent.com/botuser420/tribot-resources/main/thieving/ThievingGUI.fxml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        gui = new GUI(fxml, null, false);
        gui.show();
        while (gui.isOpen())
            sleep(500);
        return gui.stopScript;
    }

    @Override
    public void passArguments(HashMap<String, String> hashMap) {
        String scriptSelect = hashMap.get("custom_input");
        String clientStarter = hashMap.get("autostart");
        String input = clientStarter != null ? clientStarter : scriptSelect;
        String[] settings = input.split(",");
        if (settings.length > 0) {
            for (String s : settings) {
                if (s.contains("settings:")) {
                    profileName = s.split(":")[1] != null ? s.split(":")[1] : null;
                }
            }
        }
    }

    private void loadProfile() throws IOException {
        General.println("Loading profile " + profileName);
        try {
            GuiSettings settings = new Gson().fromJson(new String(FileUtilities.loadFile(new File(Util.getWorkingDirectory().getAbsolutePath() + "\\BreakerScripts\\Construction\\" + profileName + ".json"))), GuiSettings.class);
            int stopLevel = Integer.parseInt(settings.getStopLevel());
            for (ThievingMethod method : settings.getTableUsed()){
                General.println("Doing " + method.getName() + " till level " + stopLevel);
                ThievingTrainer.events.add(new Thieving(method, stopLevel));
                stopLevel = method.getReqLvl();
            }        } catch (NumberFormatException exception) {
            General.println(exception);
        }
    }
}
