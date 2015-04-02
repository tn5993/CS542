package join;

import java.util.ArrayList;
import java.util.List;

import relation.Record;
import relation.Relation;
import condition.CompareType;
import condition.Condition;
import exception.DirtyDataException;

/**
 *
 * @author kaleo211
 *
 */
public class Join {

    private Relation left = null;
    private Relation right = null;

    private List<Condition> equalities = null;

    private Record l_record = null;
    private Record r_record = null;

    public Join(Relation l, Relation r) throws DirtyDataException {
        this.left = l;
        this.right = r;

        left.open();
        l_record = left.getNext();
        right.open();
        r_record = right.getNext();

        equalities = new ArrayList<Condition>();
    }

    /**
     * add a new condition for the join of the two relations
     * @param l
     * @param r
     * @param condition
     */
    public void addCondition(String l, String r, CompareType condition) {
        if (left!=null && right!=null && left.getIndex(l)!=-1 && right.getIndex(r)!=-1) {
            Condition e = new Condition(left.getIndex(l), right.getIndex(r), condition);
            equalities.add(e);
        }
    }

    public void addCondition(String l, String r, CompareType condition, double coefficient) {
        if (left!=null && right!=null && left.getIndex(l)!=-1 && right.getIndex(r)!=-1) {
            Condition e = new Condition(left.getIndex(l), right.getIndex(r), condition, coefficient);
            equalities.add(e);
        }
    }

    /**
     * iterate the records in left.
     * for every record in left, find all records in right that could join with this record.
     * @return
     * @throws DirtyDataException
     */
    public Record getNext() throws DirtyDataException {
        // all records in left relation had been iterated, join operation is done.
        if (l_record==null) {
            return null;
        }

        Record result = null;

        while (true) {
            // all records in right had been iterated for a record in l.
            // need iterate all records in r for next record in l;
            if (r_record==null) {
                l_record = left.getNext();
                if (l_record==null) {
                    break;
                }
                right.close();
                right.open();
                r_record = right.getNext();
            }

            // if this two records meet the condition
            boolean meet = true;
            for (int i=0; i<equalities.size(); i+=1) {
                Condition e = equalities.get(i);
                if (!e.compare(l_record, r_record)) {
                   meet = false;
                   break;
                }
            }
            if (meet) {
                result = new Record(r_record);
                result.union(l_record);
                r_record = right.getNext();
                break;
            } else {
                r_record = right.getNext();
            }
        }

        return result;
    }
}
