package relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *
 *
 * @author kaleo211
 *
 */
public class Record {

    private String[] values = null;
    private String[] titles = null;

    public Record(String[] titles, String[] values) {
        this.values = new String[titles.length];
        this.titles = new String[titles.length];
        for (int i=0; i<titles.length; i+=1) {
            this.values[i] = values[i].trim();
            this.titles[i] = titles[i].trim();
        }
    }

    public Record(Record r) {
        this.values = Arrays.copyOf(r.getValues(), r.getValues().length);
        this.titles = Arrays.copyOf(r.getTitles(), r.getTitles().length);
    }

    public String getValue(int index) {
        if (values==null || values.length<index) {
            return null;
        }
        return values[index];
    }

    public String getTitle(int index) {
        if (titles==null || titles.length<index) {
            return null;
        }
        return titles[index];
    }

    public String[] getValues() {
        return this.values;
    }

    public String[] getTitles() {
        return this.titles;
    }

    /**
     * will remove all other attributes except the select one
     * @param name
     */
    public void select(String name) {
        for (int i=0; i<titles.length; i+=1) {
            if (name.equals(titles[i])) {
                values = new String[]{values[i]};
                titles = new String[]{titles[i]};
                break;
            }
        }
    }

    /**
     * will remove all other attributes except the select ones
     * @param name
     */
    public void select(String[] names) {
        HashSet<String> set = new HashSet<String>(Arrays.asList(names));
        List<String> new_titles = new ArrayList<String>();
        List<String> new_values = new ArrayList<String>();
        for (int i=0; i<titles.length; i+=1) {
            if (set.contains(titles[i])) {
                new_titles.add(titles[i]);
                new_values.add(values[i]);
            }
        }
        titles = new_titles.toArray(new String[]{});
        values = new_values.toArray(new String[]{});
    }

    /**
     * union another record with the current one
     * wont remove duplicate
     * @param r
     */
    public void union(Record r) {
        String[] t = r.getTitles();
        String[] v = r.getValues();
        List<String> titles_list = new ArrayList<String>(Arrays.asList(titles));
        List<String> values_list = new ArrayList<String>(Arrays.asList(values));

        for (int i=0; i<t.length; i+=1) {
            titles_list.add(t[i]);
            values_list.add(v[i]);
        }
        String[] tmp = new String[1];
        titles = titles_list.toArray(tmp);
        values = values_list.toArray(tmp);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (values==null) {
            return null;
        }
        for (int i=0; i<values.length; i+=1) {
            sb.append(values[i]);
            sb.append(", ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
