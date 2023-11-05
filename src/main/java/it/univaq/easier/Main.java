package it.univaq.easier;

public class Main {
	
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
		System.out.println("Energy coefficients:");
		uml.getNodesEnergy().forEach((k, v) -> System.out.println(k + " -> " + v));
		
		// Get the entries invoked by each scenario
		System.out.println("\nEntries by scenario:");
		lqn.getEntriesByScenario().values().forEach(scenario -> {
			System.out.println(scenario.getName() + "->");
			scenario.getEntries().forEach(entry -> {
				System.out.println("\t" + entry);
			});
		});
		
		// Compute the energy profile
		System.out.println("\nEnergy profile:");
		Profiler.computeEnergyProfile(uml, lqn).forEach((k, v) -> System.out.println(k + " -> " + v));
	
	}
}
