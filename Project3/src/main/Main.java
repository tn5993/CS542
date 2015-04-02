package main;

import join.Join;
import relation.Record;
import relation.Relation;
import condition.CompareType;

/**
 *
 * @author kaleo211
 *
 */
public class Main {
    public static void main(String args[]) {
        try {
            Relation country = new Relation("country", "country.csv");
            Relation city = new Relation("city", "city.csv");
            country.open();
            city.open();
            Join join = new Join(country, city);
            join.addCondition("Code", "CountryCode", CompareType.EQUAL);
            join.addCondition("Population", "Population", CompareType.INTEGER_SMALLER, 2.5);
            while (true) {
                Record record = join.getNext();
                if (record==null) {
                    break;
                }
                record.select(new String[]{"Name", "Population"});
                System.out.println(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
