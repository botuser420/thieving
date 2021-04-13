package scripts.thieving;

import lombok.Getter;

public enum ThievingMethod {
    FRUIT_STALL("Fruit stall", 25, Stall.FRUIT),
    SILK_STALL("Silk stall", 20, Stall.SILK),
    CAKE_STALL("Cake Stall", 5, Stall.CAKE),
    TEA_STALL("Tea stall", 5, Stall.TEA);

    @Getter
    String name;

    @Getter
    int reqLvl;

    @Getter
    Object method;

    ThievingMethod(String name, int reqLvl, Object method) {
        this.name = name;
        this.reqLvl = reqLvl;
        this.method = method;
    }
}
