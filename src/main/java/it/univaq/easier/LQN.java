package it.univaq.easier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LQN {

    private String lqxoFile;
    private Document xmlDoc;

    public LQN(final String lqxoFile) {
        super();
        this.lqxoFile = lqxoFile;
        this.xmlDoc = XMLHelper.parseXML(lqxoFile);
    }

    /**
     * Get the utilization of each processor.
     *
     * @return Map<processor name, utilization>
     */
    public Map<String, Double> getProcsUtilization() {
        // Get the processors
        final List<Element> processors = XMLHelper
                .getElementsByTagName(xmlDoc.getDocumentElement(), "processor");

        // Get the utilizations
        return processors.stream()

                // Exclude processors used to generate jobs
                .filter(proc -> !XMLHelper.getElementsByTagName(proc, "task").get(0)
                        .getAttribute("name").endsWith("_job_class"))

                // Build the Map
                .collect(Collectors.toMap(
                proc -> proc.getAttribute("name"),
                proc -> Double.parseDouble(
                        XMLHelper.getElementsByTagName(proc, "result-processor").get(0)
                        .getAttribute("utilization"))
        ));
    }

    /**
     * Get all the entries from the LQN file.
     *
     * @return Map<entry name, Entry>
     */
    public Map<String, Entry> getEntries() {
        return XMLHelper
                .getElementsByTagName(xmlDoc.getDocumentElement(), "entry")
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getAttribute("name"),
                        Entry::new
                ));
    }

    /**
     * Get all the synch-call elements from an entry.
     *
     * @param entry The entry to analyze
     * @return List of calls
     */
    public List<Call> getSynchCalls(final Element entry) {
        // Get all the synch-call elements
        final List<Element> synchCalls = XMLHelper.getElementsByTagName(entry, "synch-call");

        return synchCalls.stream().map(call -> new Call(
                entry.getAttribute("name"),    // caller
                call.getAttribute("dest")    // called
        )).collect(Collectors.toList());
    }

    /**
     * Given a list of calls, assign the entries to the scenarios.
     *
     * @param entries The entries
     * @param calls The calls
     * @param entriesUtilization The scenarios
     */
    public void assignEntriesToScenarios(
            final Map<String, Entry> entries,
            final List<Call> calls,
            final Map<String, Scenario> scenarios) {

        for (Scenario scenario : scenarios.values()) {
            final List<String> toTraverse = new ArrayList<>();
            toTraverse.add(scenario.getName());

            ListIterator<String> i = toTraverse.listIterator();
            while (i.hasNext()) {
                // Pop a caller from the list of calls to traverse
                final String caller = i.next();
                i.remove();

                // Get all the entries called by this caller
                final List<Entry> calledEntries = calls.stream()
                        .filter(call -> call.getCaller().equals(caller))
                        .map(call -> entries.get(call.getCalled()))
                        .collect(Collectors.toList());

                // Add the newly discovered called entries
                // to the list of entries we still need to traverse
                for (Entry entry : calledEntries) {
                    i.add(entry.getName());
                }

                // Refresh the iterator
                if (!i.hasNext()) {
                    i = toTraverse.listIterator();
                }

                // If we are processing the first entries in a task that represents a job class,
                // we don't want to get the utilization, because this represents a customer.
                if (caller.endsWith("_job_class")) {
                    continue;
                }

                // Add the entries to the scenario
                scenario.getEntries().addAll(calledEntries);
            }
        }
    }

    /**
     * Get the entries from the LQN file.
     *
     * @return List of scenarios with invoked entries
     */
    public Map<String, Scenario> getEntriesByScenario() {
        // The list of Scenarios we will return
        final Map<String, Scenario> scenarios = new HashMap<>();

        // Get the entries
        final Map<String, Entry> entries = getEntries();

        // Build a List of caller -> called
        final List<Call> calls = new ArrayList<>();
        for (Entry entry : entries.values()) {

            // Get the synch-calls
            final List<Call> synchCalls = getSynchCalls(entry.getEntry());

            if (synchCalls.isEmpty()) {
                // No outgoing calls
                continue;
            }

            // Add them to the list
            calls.addAll(synchCalls);

            // Take note of the entry points by initializing the scenarios
            final String task = entry.getTask();
            if (task.endsWith("_job_class")) {
                scenarios.put(task, new Scenario(task));

                // Create an additional call to its entries
                // for the task representing a job class
                calls.add(new Call(task, entry.getName()));
            }
        }

        // Assign entries to the invoking scenario
        assignEntriesToScenarios(entries, calls, scenarios);

        return scenarios;
    }

    public String getLqxoFile() {
        return lqxoFile;
    }

    public void setLqxoFile(final String lqxoFile) {
        this.lqxoFile = lqxoFile;
    }

    public Document getXmlDoc() {
        return xmlDoc;
    }

    public void setXmlDoc(final Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }
}
