package relation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exception.DirtyDataException;
/**
 * Representation of a relation with operation open(), getNext(), and close()
 * 
 *
 */
public class Relation {

    private String[] attrs = null;
    private int attrs_no = 0;

    private String name = null;
    private String path =null;
    private FileInputStream fis = null;
    private StringBuffer input_buffer = new StringBuffer();

    private List<Record> records = new ArrayList<Record>();

    public Relation(String name, String path) throws FileNotFoundException {
        this.name = name;
        this.path = path;
        this.fis = new FileInputStream(path);
    }

    /**
     * read next block
     * @throws DirtyDataException
     */
    public void open() throws DirtyDataException {

        // We assume that a block has 512 byte, so we read 512 byte from disk every time
        byte[] block = new byte[512];
        int size = 0;
        try {
            if (fis==null) {
                fis = new FileInputStream(this.path);
            }
            size = fis.read(block);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (size>0) {
            // add the bytes were not used to generate a record to the front of the new bytes.
            input_buffer.append(new String(block));
            String input = input_buffer.toString();
            // delete the data that could convert to a record this round
            input_buffer.delete(0, input_buffer.lastIndexOf("\n")+1);

            String[] lines = input.split("\n");
            int lines_no = lines.length + (input.endsWith("\n")?0:-1);

            for (int i=0; i<lines_no; i+=1) {
                // the first line in the file is the titles for each attribute
                if (attrs==null) {
                    attrs = lines[0].split(",");
                    attrs_no = attrs.length;
                    for (int j=0; j<attrs_no; j+=1) {
                        attrs[j] = attrs[j].trim();
                    }

                } else {
                    String[] r = lines[i].split(",");
                    if (r.length!=attrs_no) {
                        throw new DirtyDataException();
                    }
                    records.add(new Record(attrs, r));
                }
            }
        }
    }

    /**
     * get the next record in this relation
     * @return
     * @throws DirtyDataException
     */
    public Record getNext() throws DirtyDataException {

        if (records.size()>0) {
            Record r = records.get(0);
            records.remove(0);
            return r;

        // if there are no more records, we need to read a new block from disk
        } else {
            open();
            if (records.size()==0) {
                return null;
            }
            Record r = records.get(0);
            records.remove(0);
            return r;
        }
    }

    /**
     * it will close all the resource that we allocate for this round of reading.
     */
    public void close() {
        try {
            this.records.clear();
            this.input_buffer = new StringBuffer();
            this.fis.close();
            this.fis = null;
            this.attrs = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getName() {
        return this.name;
    }

    /**
     * return the column name of the given index
     * null if not found
     * @param index
     * @return
     */
    public String getAttr(int index) {
        if (attrs.length>index) {
            return attrs[index];
        }
        return null;
    }

    public String[] getAttrs() {
        return attrs;
    }

    /**
     * return the index of the given column
     * -1 if not found
     * @param attr
     * @return
     */
    public int getIndex(String attr) {
        for (int i=0; i<attrs.length; i+=1) {
            if (attr.equals(attrs[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * return how many columns in the table
     * @return
     */
    public int getAttrsSize() {
        if (attrs==null) {
            return 0;
        }
        return attrs.length;
    }
}
