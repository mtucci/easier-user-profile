package it.univaq.easier;

import org.w3c.dom.Element;

public class Entry {

    private Element entry;
    private String name;
    private String task;
    private String processor;
    private Double utilization;

    public Entry(final Element entry) {
        super();
        this.entry = entry;
        this.name = getEntryName(entry);
        this.task = getTaskName(entry);
        this.processor = getProcessorName(entry);
        this.utilization = getUtilization(entry);
    }

    private String getEntryName(final Element entry) {
        return entry.getAttribute("name");
    }

    private String getTaskName(final Element entry) {
        return ((Element) entry.getParentNode()).getAttribute("name");
    }

    private String getProcessorName(final Element entry) {
        return ((Element) entry.getParentNode().getParentNode()).getAttribute("name");
    }

    private Double getUtilization(final Element entry) {
        return Double.parseDouble(
                ((Element) entry.getElementsByTagName("result-entry").item(0))
                .getAttribute("proc-utilization"));
    }

    public Element getEntry() {
        return entry;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(final String processor) {
        this.processor = processor;
    }

    public Double getUtilization() {
        return utilization;
    }

    public void setUtilization(final Double utilization) {
        this.utilization = utilization;
    }

    @Override
    public String toString() {
        return String.format("Entry %s on processor %s with utilization %f", name, processor, utilization);
    }
}
