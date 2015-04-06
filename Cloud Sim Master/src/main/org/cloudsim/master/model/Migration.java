package main.org.cloudsim.master.model;

public class Migration {

	private DistributionNode from;
	private DistributionNode to;

	public Migration clone()
	{
		Migration migration = new Migration();
		migration.setFrom(from.clone());
		migration.setTo(to.clone());
		return migration;
	}
	
	public DistributionNode getFrom() {
		return from;
	}

	public void setFrom(DistributionNode from) {
		this.from = from;
	}

	public DistributionNode getTo() {
		return to;
	}

	public void setTo(DistributionNode to) {
		this.to = to;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" ( from : ").append(from.getHostName()).append(" \\ ").append(from.getVmName());
		sb.append(" to : ").append(to.getHostName()).append(" \\ ").append(to.getVmName());
		sb.append(" ) ");
		return sb.toString();
	}
}
