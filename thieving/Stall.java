package scripts.thieving;


import org.tribot.api.General;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.Sleep;
import scripts.combat.Fight;
import scripts.combat.safespot.SafeSpot;
import scripts.dax.api_lib.DaxWalker;
import scripts.dax.walker_engine.WalkingCondition;
import scripts.utilities.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Stall {
    FRUIT(28823, 25, new RSTile(1796, 3608, 0), Arrays.asList("Cooking apple", "Banana", "Jangerberries", "Lemon", "Redberries", "Pineapple", "Lime", "Strawberry", "Strange fruit", "Golovanova fruit top", "Papaya fruit"), null),
    SILK(11729, 20, new RSTile(2663, 3316, 0), Arrays.asList("Silk"), new RSTile(2657, 3338, 0)),
    TEA(635, 5, new RSTile(3268, 3410, 0), Arrays.asList("Cup of tea", "Empty cup"), null),
    CAKE(11730, 5, new RSTile(2669, 3310, 0), Arrays.asList("Cake", "2/3 cake", "Slice of cake", "Chocolate slice", "Bread"), new RSTile(2686, 3316, 0));

    int id, reqLvl;
    RSTile location;

    RSTile runAwayTile;
    List<String> items;

    public int getId() {
        return id;
    }

    public List<String> getItems() {
        return items;
    }

    public int getReqLvl() {
        return reqLvl;
    }

    public RSTile getLocation() {
        return location;
    }

    public RSTile getRunAwayTile() {
        return runAwayTile;
    }

    Stall(int id, int reqLvl, RSTile location, List<String> items, RSTile runAwayTile) {
        this.id = id;
        this.reqLvl = reqLvl;
        this.location = location;
        this.items = items;
        this.runAwayTile = runAwayTile;
    }

    public boolean execute() {
        Optional<RSObject> stallObject = findMarketStall(this);
        if (Fight.getHealth() < 7 && Fight.getFood().length > 0) {
            Fight.eat();
        } else if (Player.getPosition().distanceTo(getLocation()) > 10) {
            if (DaxWalker.walkTo(getLocation()))
                Sleep.till(() -> !Player.isMoving());
        } else if (getRunAwayTile() != null && Fight.getAttackingEntities().length > 0) {
            Walking.blindWalkTo(getRunAwayTile(), () -> Fight.getAttackingEntities().length == 0, General.random(1000, 3000));
        } else if (Inventory.isFull()) {
            Inventory.dropByNames(getItems());
        } else if (stallObject.isPresent()) {
            General.println(stallObject.get().getPosition());
            if (stallObject.get().isClickable()) {
                Sleep.small();
                if (stallObject.get().click("Steal-from")) {
                    if (Sleep.till(() -> Player.getAnimation() == 832) || Fight.getAttackingEntities().length > 0)
                        Sleep.till(() -> Player.getAnimation() == -1 || Player.getAnimation() == 424);
                }
            } else {
                stallObject.get().adjustCameraTo();
            }
        }
        return false;
    }

    private Optional<RSObject> findMarketStall(Stall stall) {
        return Arrays.stream(Objects.find(3, o -> o.getID() == stall.getId() && ObjectUtil.hasAction(o, "Steal-from"))).findFirst();
    }

}
