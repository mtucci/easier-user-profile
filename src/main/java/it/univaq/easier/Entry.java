package it.univaq.easier;

import org.w3c.dom.Element;

public class Entry {

    private Element entry;
    private String name;
    private String task;
    private String processor;
    private Double utilization;
    private Double procUtilization;

    public Entry(final Element entry) {
        super();
        this.entry = entry;
        this.name = getEntryName(entry);
        this.task = getTaskName(entry);
        this.processor = getProcessorName(entry);
        this.utilization = readUtilization(entry);
        this.procUtilization = readProcUtilization();
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

    private Double readUtilization(final Element entry) {
        return Double.parseDouble(
                XMLHelper.getElementsByTagName(entry, "result-entry").get(0)
                .getAttribute("proc-utilization"));
    }

    private Double readProcUtilization() {
        final Element proc = (Element) entry.getParentNode().getParentNode();
        return Double.parseDouble(
                XMLHelper.getElementsByTagName(proc, "result-processor").get(0)
                .getAttribute("utilization"));
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

    public Double getProcUtilization() {
        return this.procUtilization;
    }

    @Override
    public String toString() {
        return String.format("Entry [%s]\n\t  on processor [%s]\n\t  with utilization [%f]", name, processor, utilization);
    }
}
