package main.org.cloudsim.master.utils;

public final class Constants {
	private Constants() {

	}

	public static final String MAX_NUMBER_VM = "maxNumberVM";
	public static final String MAX_NUMBER_HOST = "maxNumberHost";
	
	public static final String HOSTS = "hosts";
	public static final String MAX_DISTRIBUTION_PERCENTAGE = "maxDistributionPercentage";
	
	public static final String ANTIBODY_NUMBER = "antibodyNumber";
	public static final String ANTIBODY_ITERATIONS = "antibodyIterations";
	public static final String CLONED_ANTIBODY_NUMBER = "clonedAntibodyNumber";
	
	public static final String ENERGY_PER_HOST_CREATION = "hostCreation";
	public static final String ENERGY_PER_HOST_DELETION = "hostDeletion";
	public static final String ENERGY_PER_HOST = "hostConsumption";
	public static final String ENERGY_PER_VM_MIGRATION = "vmMigration";
	public static final String ENERGY_PER_HOST_LOADED = "hostLoaded";
}
