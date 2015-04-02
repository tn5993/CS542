package condition;

import relation.Record;

/**
 * this class is used to do the selection from both relation and the join condition
 * a condition is like "CountryCode=CHN" or "CountryCode=Code"
 * @author kaleo211
 *
 */
public class Condition {

    // the index of the attributes in that relation
    private int right_index = -1;
    private int left_index = -1;

    private String right_title = null;
    private String left_title = null;

    private CompareType condition = CompareType.EQUAL;

    // right value will be compared to left value multiplied coefficient
    private double coefficient = 1;

    public Condition(int left, int right, CompareType c) {
        this.left_index = left;
        this.right_index = right;
        this.condition = c;
    }

    public Condition(int left, int right, CompareType c, double coefficient) {
        this(left, right, c);
        this.coefficient = coefficient;
    }

    public Condition(String left, String right, CompareType c) {
        this.left_title = left;
        this.right_title = right;
        this.condition = c;
    }

    public int getLeftIndex() {
        return this.left_index;
    }

    public String getLeftTitle() {
        return this.left_title;
    }

    public int getRightIndex() {
        return this.right_index;
    }

    public String getRightTitle() {
        return this.right_title;
    }

    /**
     * compare these record from the two relations whether meet this condition
     * @param l: record from left relation
     * @param r: record from right relation
     * @return
     */
    public boolean compare(Record l, Record r) {
        if (l==null || r==null) {
            return false;
        }

        if (this.condition==CompareType.EQUAL) {
            if (l.getValue(left_index).equals(r.getValue(right_index))) {
                return true;
            }

        } else if (this.condition==CompareType.INTEGER_SMALLER) {
            double l_value = Double.valueOf(l.getValue(left_index));
            double r_value = Double.valueOf(r.getValue(right_index));
            if (l_value<r_value*coefficient) {
                return true;
            }

        } else if (this.condition==CompareType.INTEGER_LARGER) {
            double l_value = Double.valueOf(l.getValue(left_index));
            double r_value = Double.valueOf(r.getValue(right_index));
            if (l_value>r_value*coefficient) {
                return true;
            }
        }

        return false;
    }
}
