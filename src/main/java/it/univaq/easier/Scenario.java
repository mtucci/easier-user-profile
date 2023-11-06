package it.univaq.easier;

import java.util.ArrayList;
import java.util.List;

public class Scenario {

    private String name;
    private List<Entry> entries;

    public Scenario(String name) {
        super();
        this.name = name;
        this.entries = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(final List<Entry> entries) {
        this.entries = entries;
    }
}
