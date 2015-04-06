package main.org.cloudsim.master.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class Antibody extends Datacenter {

	public Antibody(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
		// TODO Auto-generated constructor stub
	}

	/**
	 * vmDistribution is a map having - as key the host name - as value a
	 * List<Integer> with values { 0, 1 }
	 */

	private Map<String, List<DistributionNode>> vmDistribution = new HashMap<String, List<DistributionNode>>();

	private List<Migration> migrations = new ArrayList<>();


	public List<Host> getHosts() {
		return getCharacteristics().getHostList();
	}

	public Antibody cloneAntibody() throws Exception {
		Antibody antibody = new Antibody(
				getName(), 
				getCharacteristics(), 
				getVmAllocationPolicy(), 
				getStorageList(), 
				getSchedulingInterval());
		antibody.setMigrations(cloneMigration());
		antibody.setVmDistribution(cloneVmDistributions());
		return antibody;
	}

	public void updateMigrations(Migration newMigration)
	{
		boolean isFound = false;
		for (Migration migration : migrations)
		{
			if (migration.getTo().equals(newMigration.getFrom()))
			{
				migration.setTo(newMigration.getTo());
				isFound = true;
			}
		}
		if (!isFound)
		{
			migrations.add(newMigration);
		}
	}
	
	private List<Migration> cloneMigration()
	{
		List<Migration> clonedMigrations = new ArrayList<>(migrations.size());
		for (Migration migration : migrations)
		{
			clonedMigrations.add(migration.clone());
		}
		return clonedMigrations;
	}
	
	private Map<String, List<DistributionNode>> cloneVmDistributions()
	{
		Map<String, List<DistributionNode>> clonedVmDistributions = new HashMap<>();
		for (String key: vmDistribution.keySet())
		{
			List<DistributionNode> clonedDistributions = new ArrayList<>();
			for (DistributionNode distribution : vmDistribution.get(key))
			{
				clonedDistributions.add(distribution.clone());
			}
			clonedVmDistributions.put(key, clonedDistributions);
		}
		return clonedVmDistributions;
	}
	
	public List<Migration> getMigrations() {
		return migrations;
	}

	public void setMigrations(List<Migration> migrations) {
		this.migrations = migrations;
	}

	public Map<String, List<DistributionNode>> getVmDistribution() {
		return vmDistribution;
	}

	public void setVmDistribution(Map<String, List<DistributionNode>> vmDistribution) {
		this.vmDistribution = vmDistribution;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder("Antibody with name : ").append(getName());
		stringBuilder.append("\n has migrations : [ ");
		stringBuilder.append(createMigrationsStringBuilders());
		stringBuilder.append(" ] ");
		
		stringBuilder.append("\n and has VM distribution : [ ");
		stringBuilder.append(createVmDistribution());
		stringBuilder.append(" ] ");
		
		return stringBuilder.toString();
	}
	
	private StringBuilder createMigrationsStringBuilders()
	{
		StringBuilder sb = new StringBuilder();
		for (Migration migration : migrations)
		{
			if (sb.length() > 0)
			{
				sb.append(" , ");
			}
			sb.append(migration.toString());
		}
		return sb;
	}
	
	private StringBuilder createVmDistribution()
	{
		StringBuilder sb = new StringBuilder();
		for (String host : vmDistribution.keySet())
		{
			if (sb.length() > 0 )
			{
				sb.append(" , ");
			}
			sb.append(host).append(" : [");
			StringBuilder disNodeBuilder = new StringBuilder();
			for(DistributionNode distributionNode : vmDistribution.get(host))
			{
				if (disNodeBuilder.length() > 0)
				{
					disNodeBuilder.append(" , ");
				}
				disNodeBuilder.append(distributionNode.getVmName());
			}
			sb.append(disNodeBuilder);
			sb.append(" ] ");
		}
		return sb;
	}

}
