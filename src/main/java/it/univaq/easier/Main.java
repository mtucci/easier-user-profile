package it.univaq.easier;

import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    // Scaling factor for the consumption when idle [0,1]
    // https://dl.acm.org/doi/10.5555/1387589.1387613
    private static final double idleScalingFactor = 0.66;

    private static void checkParams(final String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar easier-user-profile.jar <uml_model> <lqxo_file>");
            System.exit(1);
        }
    }

    public static void main (final String[] args) {
        // Expect two files in input: a UML model and a lxqo file
        checkParams(args);

        final UML uml = new UML(args[0]);
        final LQN lqn = new LQN(args[1]);

        // Get the energy coefficient for each node
        final Map<String, Double> energyCoefficients = uml.getNodesEnergy();
        System.out.println("### Energy coefficients:");
        energyCoefficients.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Get the price for each processor
        final Map<String, Double> prices = uml.getNodesPrices();
        System.out.println("\n### Prices:");
        prices.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Get the utilization of processors
        final Map<String, Double> procsUtilizations = lqn.getProcsUtilization();
        System.out.println("\n### Processors utilization:");
        procsUtilizations.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Get the entries invoked by each scenario
        final Map<String, Scenario> entriesByScenario = lqn.getEntriesByScenario();
        System.out.println("\n### Entries by scenario:");
        entriesByScenario.values().forEach(scenario -> {
            System.out.println(scenario.getName() + " ->");
            scenario.getEntries().forEach(entry -> {
                System.out.println("\t" + entry);
                System.out.println("\t  energy: " + Profiler.computePower(
                	entry.getUtilization(), energyCoefficients.get(entry.getProcessor()), idleScalingFactor));
                System.out.println("\t  price: " + entry.getUtilization() * prices.get(entry.getProcessor()));
            });
        });
        System.out.println("\n### Sum of the utilization of entries on the same processor:");
        entriesByScenario.values()
        	.stream().flatMap(l -> l.getEntries().stream())
        	.collect(Collectors.groupingBy(Entry::getProcessor))
        	.forEach((k, v) -> System.out.println(k + " -> " + v.stream().mapToDouble(Entry::getUtilization).sum()));

        // Compute the energy profile
        System.out.println("\n### Energy profile:");
        final Map<String, Double> energyProfile = Profiler.computeEnergyProfile(uml, lqn, idleScalingFactor);
        energyProfile.forEach((k, v) -> System.out.println(k + " -> " + v));
        System.out.println("\n### Sum of the energy profiles:");
        System.out.println(energyProfile.values().stream().mapToDouble(Double::doubleValue).sum());

        // Compute the energy for the entire system
        System.out.println("\n### System energy:");
        System.out.println(Profiler.computeSystemEnergy(uml, lqn, idleScalingFactor));

        // Compute the price profile
        System.out.println("\n### Price profile:");
        final Map<String, Double> priceProfile = Profiler.computePriceProfile(uml, lqn);
        priceProfile.forEach((k, v) -> System.out.println(k + " -> " + v));
        System.out.println("\n### Sum of the price profiles:");
        System.out.println(priceProfile.values().stream().mapToDouble(Double::doubleValue).sum());

        // Compute the system price
        System.out.println("\n### System price:");
        System.out.println(Profiler.computeSystemPrice(uml));
    }
}
