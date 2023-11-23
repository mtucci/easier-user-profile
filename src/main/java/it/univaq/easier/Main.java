package it.univaq.easier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    // Scaling factor for the consumption when idle [0,1]
    // https://dl.acm.org/doi/10.5555/1387589.1387613
    private static final double idleScalingFactor = 0.3;

    private static void checkParams(final String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar easier-user-profile.jar <uml_model> <lqxo_file>");
            System.exit(1);
        }
    }

    private static String mapToCSV(final Map<String, Double> map) {
    	// headers
    	String out = map.keySet().stream().collect(Collectors.joining(","));
    	// values
    	out += "\n" + map.values().stream().map(Object::toString).collect(Collectors.joining(","));

    	return out;
    }

    public static void main (final String[] args) {
        // Expect two files in input: a UML model and a lxqo file
        checkParams(args);

        final UML uml = new UML(args[0]);
        final LQN lqn = new LQN(args[1]);
        final Map<String,Double> out = new HashMap<>();

        // Get the energy coefficient for each node
        final Map<String, Double> energyCoefficients = uml.getNodesEnergy();
        System.err.println("### Energy coefficients:");
        energyCoefficients.forEach((k, v) -> System.err.println(k + " -> " + v));

        // Get the price for each processor
        final Map<String, Double> prices = uml.getNodesPrices();
        System.err.println("\n### Prices:");
        prices.forEach((k, v) -> System.err.println(k + " -> " + v));

        // Get the utilization of processors
        final Map<String, Double> procsUtilizations = lqn.getProcsUtilization();
        System.err.println("\n### Processors utilization:");
        procsUtilizations.forEach((k, v) -> System.err.println(k + " -> " + v));

        // Get the entries invoked by each scenario
        final Map<String, Scenario> entriesByScenario = lqn.getEntriesByScenario();
        System.err.println("\n### Entries by scenario:");
        entriesByScenario.values().forEach(scenario -> {
            System.err.println(scenario.getName() + " ->");
            scenario.getEntries().forEach(entry -> {
                System.err.println("\t" + entry);
                System.err.println("\t  energy: " + Profiler.computePower(
                	entry.getUtilization(), energyCoefficients.get(entry.getProcessor()), idleScalingFactor));
                System.err.println("\t  price: " + entry.getUtilization() * prices.get(entry.getProcessor()));
            });
        });
        System.err.println("\n### Sum of the utilization of entries on the same processor:");
        entriesByScenario.values()
        	.stream().flatMap(l -> l.getEntries().stream())
        	.collect(Collectors.groupingBy(Entry::getProcessor))
        	.forEach((k, v) -> System.err.println(k + " -> " + v.stream().mapToDouble(Entry::getUtilization).sum()));

        // Compute the energy profile
        System.err.println("\n### Energy profile:");
        final Map<String, Double> energyProfile = Profiler.computeEnergyProfile(uml, lqn, idleScalingFactor);
        energyProfile.forEach((k, v) -> out.put("power__" + k, v));
        energyProfile.forEach((k, v) -> System.err.println(k + " -> " + v));
        System.err.println("\n### Sum of the energy profiles:");
        System.err.println(energyProfile.values().stream().mapToDouble(Double::doubleValue).sum());

        // Compute the energy for the entire system
        System.err.println("\n### System energy:");
        out.put("power", Profiler.computeSystemEnergy(uml, lqn, idleScalingFactor));
        System.err.println(out.get("power"));

        // Compute the price profile
        System.err.println("\n### Price profile:");
        final Map<String, Double> priceProfile = Profiler.computePriceProfile(uml, lqn);
        priceProfile.forEach((k, v) -> out.put("price__" + k, v));
        priceProfile.forEach((k, v) -> System.err.println(k + " -> " + v));
        System.err.println("\n### Sum of the price profiles:");
        System.err.println(priceProfile.values().stream().mapToDouble(Double::doubleValue).sum());

        // Compute the system price
        System.err.println("\n### System price:");
        out.put("economicCost", Profiler.computeSystemPrice(uml));
        System.err.println(out.get("economicCost"));

        // Print the CSV
        System.out.println(mapToCSV(out));
    }
}
