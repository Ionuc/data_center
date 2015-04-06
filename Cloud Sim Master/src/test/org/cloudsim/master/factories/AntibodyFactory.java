package test.org.cloudsim.master.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import main.org.cloudsim.master.model.Antibody;
import main.org.cloudsim.master.model.DistributionNode;

public final class AntibodyFactory {

	private AntibodyFactory()
	{
	}

	public static Antibody create(String name) {

		int num_user = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		CloudSim.init(num_user, calendar, trace_flag);
		
		List<Host> hostList = new ArrayList<Host>();
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		int hostId = 0;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(
			new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerTimeShared(peList)
			)
		); // This is our machine

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		Antibody antibody = null;
		try {
			antibody = new Antibody(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		antibody.setVmDistribution(createDistributedNodes());
		return antibody;
	}
	
	private static Map<String, List<DistributionNode>> createDistributedNodes()
	{
		Map<String, List<DistributionNode>> distributedNodes = new HashMap<String, List<DistributionNode>>();
		distributedNodes.put("host1", Arrays.asList(
				new DistributionNode("host1", "vm1"),
				new DistributionNode("host1", "vm2")));
		distributedNodes.put("host2", Arrays.asList(
				new DistributionNode("host2", "vm3"),
				new DistributionNode("host2", "vm4")));
		distributedNodes.put("host3", Arrays.asList(
				new DistributionNode("host3", "vm1"),
				new DistributionNode("host3", "vm5")));
		return distributedNodes;
	}
}
