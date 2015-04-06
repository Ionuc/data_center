package main.org.cloudsim.master.model;

public class DistributionNode {

	private String vmName;
	private String hostName;

	public DistributionNode( String hostName, String vmName)
	{
		this.hostName = hostName;
		this.vmName = vmName;
	}
	
	public DistributionNode clone()
	{
		return new DistributionNode(hostName, vmName);
	}
	
	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}
