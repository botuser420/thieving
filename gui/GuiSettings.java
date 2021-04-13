package scripts.gui;

import com.allatori.annotations.DoNotRename;
import lombok.Getter;
import lombok.Setter;
import scripts.construction.Furniture;
import scripts.thieving.ThievingMethod;

import java.util.List;

public class GuiSettings {

    @Getter
    @Setter
    @DoNotRename
    List<ThievingMethod> tableAvailable;

    @Getter
    @Setter
    @DoNotRename
    List<ThievingMethod> tableUsed;

    @Getter
    @Setter
    @DoNotRename
    String stopLevel;
}
