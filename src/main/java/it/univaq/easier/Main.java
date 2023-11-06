package it.univaq.easier;

import java.util.Map;

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

        // Get the utilization of processors
        final Map<String, Double> procsUtilizations = lqn.getProcsUtilization();
        System.out.println("\n### Processors utilization:");
        procsUtilizations.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Get the entries invoked by each scenario
        System.out.println("\n### Entries by scenario:");
        lqn.getEntriesByScenario().values().forEach(scenario -> {
            System.out.println(scenario.getName() + "->");
            scenario.getEntries().forEach(entry -> {
                System.out.println("\t" + entry);
            });
        });

        // Compute the energy profile
        System.out.println("\n### Energy profile:");
        Profiler.computeEnergyProfile(uml, lqn, idleScalingFactor).forEach((k, v) -> System.out.println(k + " -> " + v));

        // Compute the energy for the entire system
        System.out.println("\n### System energy:");
        System.out.println(Profiler.computeSystemEnergy(uml, lqn, idleScalingFactor));

    }
}
