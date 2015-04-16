package main;

import condition.UpdateType;
import relation.Relation;

/**
 *
 * @author kaleo211
 *
 */
public class Main {

    public static void main(String args[]) {
        try {
            Relation city = new Relation("city", "city.csv");
            city.updateAll(UpdateType.INCREASE_BY_PERCENTAGE, "Population", 2);
            city.redo();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
