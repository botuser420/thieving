package scripts.thieving;

import org.tribot.api2007.Skills;
import scripts.framework.event.EventFramework;
import scripts.quests.requirements.Requirement;
import scripts.quests.requirements.player.SkillRequirement;

import java.util.ArrayList;
import java.util.List;

public class Thieving extends EventFramework {

    private int stopLevel;
    ThievingMethod thievingMethod;

    public Thieving(ThievingMethod thievingMethod, int stopLevel){
        this.thievingMethod = thievingMethod;
        this.stopLevel = stopLevel;
    }

    @Override
    public void execute() {
        if (thievingMethod.getMethod().getClass().isAssignableFrom(Stall.class)){
            ((Stall) thievingMethod.getMethod()).execute();
        }
    }

    @Override
    public String getStatus() {
        return "Thieving lvl " + Skills.SKILLS.THIEVING.getActualLevel();
    }

    @Override
    public boolean isCompleted() {
        return Skills.SKILLS.THIEVING.getActualLevel() >= stopLevel;
    }

    @Override
    public String toString() {
        return thievingMethod.name();
    }

    @Override
    public List<Requirement> getGeneralRequirements() {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skills.SKILLS.THIEVING, thievingMethod.getReqLvl()));
        return req;
    }

}
