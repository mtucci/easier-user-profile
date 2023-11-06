package it.univaq.easier;

import java.util.Map;
import java.util.stream.Collectors;

public class Profiler {

    /**
     * Compute the power consumption taking into account the utilization
     * of the processor, the maximum power consumption, and a scaling
     * factor to set the idle power consumption.
     * Taken from: https://doi.org/10.1007/s10586-023-04030-w
     *
     * @param utilization The utilization of the processor
     * @param maxConsumption Maximum power consumption
     * @param idleScalingFactor Scaling factor for the consumption when idle [0,1]
     * @return The power consumption
     */
    public static double computePower(
            final double utilization,
            final double maxConsumption,
            final double idleScalingFactor) {
        // k * p_max + (1 - k) * p_max * u
        return idleScalingFactor * maxConsumption + (1 - idleScalingFactor) * maxConsumption * utilization;
    }

    /**
     * General method to compute a profile on the basis of the utilization of
     * individual scenarios in the LQN and the coefficients (from UML nodes) that
     * will determine the profile. The coefficient of each node is multiplied
     * by the utilization of scenarios on that node, and then these values
     * are added to obtain the profile values for each scenario.
     *
     * @param scenarios The scenarios with invoked entries
     * @param coefficients The coefficients of the UML nodes
     * @return Map with the scenario name as key and the profile value as value.
     */
    public static Map<String, Double> computeProfile(
            final Map<String, Scenario> scenarios,
            final Map<String, Double> coefficients) {

        return scenarios.values().stream()
                .collect(Collectors.toMap(
                    sc -> sc.getName(),
                    sc -> sc.getEntries().stream()
                        .mapToDouble(e -> e.getUtilization() * coefficients.get(e.getProcessor()))
                        .sum()
        ));
    }

    /**
     * Compute the energy profile by using the energy coefficients defined for UML Nodes
     * and the utilization of the entries of specific scenarios.
     *
     * @param uml The UML model
     * @param lqn The LQN model
     * @return Map with the scenario name as key and the energy profile value as value.
     */
    public static Map<String, Double> computeEnergyProfile(
            final UML uml,
            final LQN lqn,
            final double idleScalingFactor) {

        final Map<String, Scenario> scenarios = lqn.getEntriesByScenario();
        final Map<String, Double> energyCoefficients = uml.getNodesEnergy();

        return scenarios.values().stream()
                .collect(Collectors.toMap(
                    sc -> sc.getName(),
                    sc -> sc.getEntries().stream()
                        .mapToDouble(e -> computePower(e.getUtilization(),
                                energyCoefficients.get(e.getProcessor()), idleScalingFactor))
                        .sum()
        ));
    }

    /**
     * Compute the energy consumption for the entire system by using the energy coefficients
     * defined for UML Nodes and the utilization of the processors.
     *
     * @param uml The UML model
     * @param lqn The LQN model
     * @param idleScalingFactor Scaling factor for the consumption when idle
     * @return The energy consumption for the entire system
     */
    public static double computeSystemEnergy(
            final UML uml,
            final LQN lqn,
            final double idleScalingFactor) {

        final Map<String, Double> energyCoefficients = uml.getNodesEnergy();
        final Map<String, Double> procsUtilizations = lqn.getProcsUtilization();

        return procsUtilizations.entrySet().stream()
                .filter(e -> energyCoefficients.containsKey(e.getKey()))
                .mapToDouble(e -> computePower(e.getValue(),
                        energyCoefficients.get(e.getKey()), idleScalingFactor))
                .sum();
    }

    /**
     * Compute the price profile by using the price tags defined for UML Nodes
     *
     * @param uml The UML model
     * @param lqn The LQN model
     * @return Map with the scenario name as key and the price profile value as value.
     */
    public static Map<String, Double> computePriceProfile(final UML uml, final LQN lqn) {
        return computeProfile(lqn.getEntriesByScenario(), uml.getNodesPrices());
    }

    /**
     * Compute the price for the entire system by summing up the price tags defined for UML Nodes
     *
     * @param uml The UML model
     * @return The price for the entire system
     */
    public static double computeSystemPrice(final UML uml) {
        return uml.getNodesPrices().values().stream()
				.mapToDouble(Double::doubleValue)
				.sum();
    }
}
