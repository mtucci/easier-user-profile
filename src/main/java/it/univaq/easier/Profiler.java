package it.univaq.easier;

import java.util.Map;
import java.util.stream.Collectors;

public class Profiler {

	
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
	public static Map<String, Double> computeEnergyProfile(final UML uml, final LQN lqn) {
		return computeProfile(lqn.getEntriesByScenario(), uml.getNodesEnergy());
	}
}
