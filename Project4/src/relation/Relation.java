package relation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import condition.UpdateType;
import exception.DirtyDataException;

public class Relation {

    private String[] attrs = null;
    private int attrs_no = 0;

    private String name = null;
    private String path =null;
    private FileInputStream fis = null;
    private StringBuffer input_buffer = new StringBuffer();

    private List<Record> records = new ArrayList<Record>();

    private File log_file = null;

    public Relation(String name, String path) throws FileNotFoundException, DirtyDataException {
        this.name = name;
        this.path = path;
        open();
        records.clear();
        this.fis = new FileInputStream(new File(path));

        this.log_file = new File(path+".log");
    }

    /**
     * Simple version of update
     * this update will apply the same operation on all the records that in this relation
     * @param type
     * @param column
     * @param coe
     * @throws IOException
     */
    public void updateAll(UpdateType type, String column, int coe) throws IOException {

        // copy the content of original file to a new file
        // then write the new data to the original file
        File file = new File(path);
        File tmp = new File("tmp_"+path);
        file.renameTo(tmp);

        // scan the temporary file
        Scanner scanner = new Scanner(tmp);

        // write new data to original file
        FileWriter writer = new FileWriter(file);
        writer.write(scanner.nextLine());
        writer.write('\n');

        // write log information to log file
        // every relation has a log file
        FileWriter log = new FileWriter(log_file, true);

        if (type==UpdateType.INCREASE_BY_PERCENTAGE) {
            while (scanner.hasNext()) {
                String[] atts = scanner.nextLine().split(",");

                // update the records
                int origin = Integer.parseInt(atts[getIndex(column)]);
                int current = (int)(origin*(1+coe/100.0));
                atts[getIndex(column)] = String.valueOf(current);

                // write the new data
                int i=0;
                for (; i<attrs_no-1; i+=1) {
                    writer.write(atts[i]);
                    writer.write(',');
                }
                writer.write(atts[i]);
                writer.write('\n');

                // write log
                log.write(atts[0]);
                log.write(',');
                log.write(column);
                log.write(',');
                log.write(String.valueOf(origin));
                log.write(',');
                log.write(String.valueOf(current));
                log.write('\n');
            }
        }
        log.write("commit\n");

        // release all allocated resources
        log.close();
        tmp.delete();
        scanner.close();
        writer.close();
    }


    public void redo() throws IOException {
        // read log file
        Scanner logf = new Scanner(log_file);

        // copy data to a temporary file
        File f = new File(path);
        File tmp = new File("tmp_"+path);
        f.renameTo(tmp);

        // read old data
        Scanner dataf = new Scanner(tmp);
        // write new data
        FileWriter writer = new FileWriter(f);

        while (logf.hasNextLine()) {
            String line = logf.nextLine();
            // come to the end of this commit
            if (line.equals("commit")) {
                break;
            }
            String[] logs = line.split(",");
            System.out.println("line: "+line);

            boolean match = false;
            // if matched, should get the next log
            while (dataf.hasNextLine() && !match) {

                String[] datas = dataf.nextLine().split(",");

                // if this record is the next one to do redo
                if (datas[0].equals(logs[0])) {
                    int index = getIndex(logs[1]);
                    datas[index] = logs[3];
                    match = true;
                } else {
                    match = false;
                }

                // write new data to file
                int i=0;
                for (; i<attrs_no-1; i+=1) {
                    writer.write(datas[i]);
                    writer.write(',');
                }
                writer.write(datas[i]);
                writer.write('\n');
            }
        }

        // release all allocated resources
        logf.close();
        writer.close();
        dataf.close();
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
