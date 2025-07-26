package never.say.never.testtest.excel.eventmode.read.strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 */
public class StringList implements Serializable {
    private static final long serialVersionUID = 4246611125837593704L;
    protected List<String> strings;

    public StringList() {
        this.strings = new ArrayList<>();
    }

    public StringList(int initialCapacity) {
        this.strings = new ArrayList<>(initialCapacity);
    }

    public StringList(Collection<? extends String> c) {
        this.strings = new ArrayList<>(c);
    }

    public void add(String s) {
        strings.add(s);
    }

    public String get(int index){
        return strings.get(index);
    }

    public boolean isEmpty(){
        return strings.isEmpty();
    }

    public StringList copy() {
        return new StringList(this.strings);
    }

    public void reset() {
        strings.clear();
    }
}
