package it.univaq.easier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UML {

    private String umlFile;
    private Document xmlDoc;

    public UML(final String umlFile) {
        super();
        this.umlFile = umlFile;
        this.xmlDoc = XMLHelper.parseXML(umlFile);
    }

    /**
     * Get all the nodes from the UML file.
     *
     * @return Map<node ID, node name>
     */
    public Map<String, String> getNodes() {
        return XMLHelper
                .getElementsByTagName(xmlDoc.getDocumentElement(), "packagedElement")
                .stream()
                .filter(e -> e.getAttribute("xmi:type").equals("uml:Node"))
                .collect(Collectors.toMap(
                        e -> e.getAttribute("xmi:id"),
                        e -> e.getAttribute("name")
                ));
    }

    /**
     * Read the 'energy' tag of the GRM:ResourceUsage stereotype from the UML file.
     *
     * @return Map with the element ID as key and the energy as value.
     */
    public Map<String, Double> getEnergyFromResourceUsage() {
        // Get the stereotypes applications
        final List<Element> resourceUsages = XMLHelper
                .getElementsByTagName(xmlDoc.getDocumentElement(), "GRM:ResourceUsage");

        // Build a Map<base_NamedElement, energy>
        return resourceUsages.stream()
                .collect(Collectors.toMap(
                        r -> r.getAttribute("base_NamedElement"),
                        r -> Double.parseDouble(
                                r.getElementsByTagName("energy").item(0)
                                .getFirstChild().getNodeValue())
                ));
    }

    /**
     * Get the energy coefficient for each node.
     *
     * @return Map<node name, energy coefficient>
     */
    public Map<String, Double> getNodesEnergy() {
        // Get the energy tag from GRM:ResourceUsage
        final Map<String, Double> energy = getEnergyFromResourceUsage();

        // Get the Nodes
        final Map<String, String> nodes = getNodes();

        // Match the base_NamedElement with the UML Node ID
        return nodes.entrySet().stream().collect(Collectors.toMap(
                node -> node.getValue(),
                node -> energy.get(node.getKey())
        ));
    }

    /**
     * Read the 'price' tag of the HwLayout:HwComponent stereotype from the UML file.
     *
     * @return Map with the element ID as key and the price as value.
     */
    public Map<String, Double> getPricesFromHwComponent() {
        // Get the stereotypes applications
        final List<Element> hwcomponents = XMLHelper
                .getElementsByTagName(xmlDoc.getDocumentElement(), "HwLayout:HwComponent");

        // Build a Map<base_NamedElement, energy>
        return hwcomponents.stream()
                .collect(Collectors.toMap(
                        h -> h.getAttribute("base_Classifier"),
                        h -> Double.parseDouble(h.getAttribute("price"))
                ));
    }

    /**
     * Get the price for each node.
     *
     * @return Map<node name, price>
     */
    public Map<String, Double> getNodesPrices() {
        // Get the price tag from HwLayout:HwComponent
        final Map<String, Double> prices = getPricesFromHwComponent();

        // Get the Nodes
        final Map<String, String> nodes = getNodes();

        // Match the base_Classifier with the UML Node ID
        return nodes.entrySet().stream().collect(Collectors.toMap(
				node -> node.getValue(),
				node -> prices.get(node.getKey())
		));
    }

    public String getUmlFile() {
        return umlFile;
    }

    public void setUmlFile(final String umlFile) {
        this.umlFile = umlFile;
    }

    public Document getXmlDoc() {
        return xmlDoc;
    }

    public void setXmlDoc(final Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }
}
