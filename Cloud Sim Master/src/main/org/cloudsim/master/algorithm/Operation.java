package main.org.cloudsim.master.algorithm;

import static java.math.BigDecimal.ZERO;
import static main.org.cloudsim.master.utils.Constants.ENERGY_PER_HOST;
import static main.org.cloudsim.master.utils.Constants.ENERGY_PER_HOST_CREATION;
import static main.org.cloudsim.master.utils.Constants.ENERGY_PER_HOST_DELETION;
import static main.org.cloudsim.master.utils.Constants.ENERGY_PER_HOST_LOADED;
import static main.org.cloudsim.master.utils.Constants.ENERGY_PER_VM_MIGRATION;
import static main.org.cloudsim.master.utils.Constants.MAX_DISTRIBUTION_PERCENTAGE;
import static main.org.cloudsim.master.utils.XmlParser.getBigDecimalValue;
import static main.org.cloudsim.master.utils.XmlParser.getStringValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import main.org.cloudsim.master.model.Antibody;
import main.org.cloudsim.master.model.DistributionNode;
import main.org.cloudsim.master.model.Migration;
import main.org.cloudsim.master.utils.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Operation {
	private Random random = new Random();
	
	private Map<String, List<String>> hostVmNames;
	private BigDecimal maxDistributionPercentage;
	
	private BigDecimal energyPerHostCreation;
	private BigDecimal energyPerHostDeletion;
	private BigDecimal energyPerHost;
	private BigDecimal energyPerVmMigration;
	private BigDecimal energyPerHostLoaded;

	public Operation(Document document) throws IOException, ParserConfigurationException,SAXException {
		hostVmNames = getHostVmNames(document);
		maxDistributionPercentage = new BigDecimal(getStringValue(document, MAX_DISTRIBUTION_PERCENTAGE)).divide(new BigDecimal("100"));
		
		energyPerHostCreation = getBigDecimalValue(document, ENERGY_PER_HOST_CREATION);
		energyPerHostDeletion = getBigDecimalValue(document, ENERGY_PER_HOST_DELETION);
		energyPerHost = getBigDecimalValue(document, ENERGY_PER_HOST);
		energyPerVmMigration = getBigDecimalValue(document, ENERGY_PER_VM_MIGRATION);
		energyPerHostLoaded =  getBigDecimalValue(document, ENERGY_PER_HOST_LOADED);
	}

	private  Map<String, List<String>> getHostVmNames(Document document){
		Map<String, List<String>> hostVmNames = new HashMap<String, List<String>>();
		Node hostsNode = document.getElementsByTagName(Constants.HOSTS).item(0);
		NodeList hostList = hostsNode.getChildNodes();
		for ( int i = 1 ; i < hostList.getLength() ; i +=2)
		{
			Node host = hostList.item(i);
			NodeList vmList = host.getChildNodes();
			List<String> vmNames = new ArrayList<>();
			for (int j = 1 ; j < vmList.getLength() ; j +=2)
			{
				vmNames.add(vmList.item(j).getTextContent());
			}
			hostVmNames.put(host.getNodeName(), vmNames);
		}
		return hostVmNames;
		
	}
	
	/**
	 * selectam toti anticorpii din populatie n=N
	 * */
	public List<Antibody> select(List<Antibody> antibodies, int n) {
		// the list of antibodies are already sorted by individualSharedFitness function, so the last n of them are the best 
		int value = min(antibodies.size(), n);
		List<Antibody> sublist = new ArrayList<>(value);
		for (int index = 0 ; index < value ; index++)
		{
			sublist.add(antibodies.get(index));
		}
		return sublist;
	}
	
	private int min(int value1, int value2)
	{
		return value1 < value2 ? value1 : value2;
	}
	/**
	 * acei n=N anticorpi selectati for fi generate Nc clone  ,
	 * iar ca iesire avem Cj 
	 * Nc = round(beta*N)
	 * nu avem nevoie neaparat de  afinitati prin urmare numarul de clone pt fiecare
	 * anticorp va fi acelasi 
	 * creez un Map care are ca si cheie un antibody si ca si valoare o lista de antibodies care sunt clonele(copiilesale)
	 * @throws Exception 
	 * 
	 * */
	public Map<Antibody ,List<Antibody>> cloneSelectedAntibodies(List<Antibody> antibodies, int beta, int antibodyNumber) throws Exception {
		Map<Antibody ,List<Antibody>> cloneAntibodies =new HashMap<Antibody ,List<Antibody>>();
		int value = 0;
		for(int index = 0 ; index < antibodies.size(); index ++) {
			value = antibodyNumber * beta / ( index + 1 ) + 1;
			cloneAntibodies.put(antibodies.get(index), cloneNAntibody(antibodies.get(index), value));
		}
		return cloneAntibodies;
	}
	
	public List<Antibody> cloneNAntibody(Antibody antibody, int value) throws Exception{
		List<Antibody> antibodies = new ArrayList<Antibody>();
		for (int i = 0 ; i < value ; i++){
			antibodies.add(antibody.cloneAntibody());
		}
		return antibodies;
	}

	public void insertAntibodyInOrder(List<Antibody> antibodies,Antibody initialAntibody, Antibody newAntibody, int antibodyNumber) {
		for (int index  = 0 ; index < antibodies.size() ; index ++){
			if (individualSharedFitness(antibodies.get(index) , initialAntibody)
					.compareTo(individualSharedFitness(newAntibody , initialAntibody)) < 0){
				antibodies.add(index, newAntibody);
				return;
			}
		}
		antibodies.add(newAntibody);
	}
	
	public void insertAllAntibodiesInOrder(List<Antibody> oldAntibodies, List<Antibody> newAntibodies, Antibody initialAntibody) {
		oldAntibodies.addAll(newAntibodies);
		Collections.sort(oldAntibodies, new AntibodyComparator(initialAntibody));
	}

	private class AntibodyComparator implements Comparator<Antibody>
	{
		private Antibody initialAntibody;
		
		private AntibodyComparator(Antibody initialAntibody)
		{
			this.initialAntibody = initialAntibody;
		}
		
		@Override
		public int compare(Antibody value1, Antibody value2) {
			BigDecimal value1BigDecimal = individualSharedFitness(value1 , initialAntibody);
			BigDecimal value2BigDecimal = individualSharedFitness(value2 , initialAntibody);
			return value1BigDecimal.compareTo(value2BigDecimal);
		}
		
	}
	public List<Antibody> replace(List<Antibody> antibodies,
			List<Antibody> generatedAntibodies, List<Double> afinitys) {
		return null;
	}

	/**
	 * mutation rule is alpha = exp (-p * f)
	 * alpha = rata de mutatie 
	 * p = controleaza gradul de mutatie (degradare)
	 * f = afinitatile (fitnesul in cazul nostru)
	 * */
	public void hypermut(Map < Antibody, List<Antibody>> antibodiesMap, List<Antibody> selectedAntibodies, int antibodyNumber) {
		int hypermutValue = 0;
		for (int index = 0 ; index < selectedAntibodies.size(); index ++){
			hypermutValue = antibodyNumber/ (antibodyNumber - index) < 3 ? 3 : antibodyNumber/ (antibodyNumber - index) ;
			hypermutAntibodies(antibodiesMap.get(selectedAntibodies.get(index)), hypermutValue);
		}
		return;
	}
	
	private void hypermutAntibodies(List<Antibody> antibodies, int permuteValue){ 
		// permute an list of Antibodies with random value
		// permuteValue is a number between 1 and 3 an tell us the number of food item that will be generated random in meniu
		for (Antibody antibody : antibodies){
//			for (int i = 0 ; i < permuteValue ; i++)
//			{
				hypermutAntibody(antibody);
//			}
		}
		
	}
	
	private void hypermutAntibody(Antibody antibody)
	{
		Map<String, List<DistributionNode>> distributions = antibody.getVmDistribution();
		List<String> hostNames = getShuffledList(distributions.keySet());
		if (hostNames.size() == 1)
		{
			return;
		}
		DistributionNode nodeFrom = getRandomDistribution(hostNames, distributions);
		DistributionNode nodeTo = null;
		do{
			nodeTo = getMigratedNode(distributions, hostNames, nodeFrom.getHostName());
		}
		while ( nodeTo == null);
		// update distribution map
		if (distributions.get(nodeTo.getHostName()) == null)
		{
			distributions.put(nodeTo.getHostName(), new ArrayList<DistributionNode>());
		}
		distributions.get(nodeTo.getHostName()).add(nodeTo);
		distributions.get(nodeFrom.getHostName()).remove(nodeFrom);
		if (distributions.get(nodeFrom.getHostName()).isEmpty())
		{
			distributions.remove(nodeFrom.getHostName());
		}
		
		// update migration list 
		Migration newMigration = new Migration();
		newMigration.setFrom(nodeFrom);
		newMigration.setTo(nodeTo);
		antibody.updateMigrations(newMigration);
	}
	
	private List<String> getShuffledList(Set<String> hostNames)
	{
		List<String> hostsName = new ArrayList<>();
		hostsName.addAll(hostNames);
		Collections.shuffle(hostsName);
		return hostsName;
	}
	private DistributionNode getMigratedNode(Map<String, List<DistributionNode>> distributions, List<String> hostNames, String fromHostNode)
	{
		for(String chosedHost : hostNames)
		{
			DistributionNode distributionNode = retrieveDistributedNode(chosedHost, fromHostNode, distributions);
			if (distributionNode != null)
			{
				return distributionNode;
			}
		}
		for (String chosedHost : hostVmNames.keySet())
		{
			DistributionNode distributionNode = retrieveDistributedNode(chosedHost, fromHostNode, distributions);
			if (distributionNode != null)
			{
				return distributionNode;
			}
		}
		return null;
	}
	
	private DistributionNode retrieveDistributedNode(String chosedHost, String fromHostNode, Map<String, List<DistributionNode>> distributions)
	{
		if (chosedHost.equals(fromHostNode))
		{
			return null;
		}
		if (isEligibleHostForMigration(chosedHost, distributions))
		{
			String migratedVmName = getNextVmName(chosedHost, distributions);
			return new DistributionNode(chosedHost, migratedVmName);
		}
		return null;
	}
	
	
	private String getNextVmName(String hostname, Map<String, List<DistributionNode>> distributions)
	{
		List<String> vmNames = hostVmNames.get(hostname);
		Collections.shuffle(vmNames);
		for( String vmName : vmNames)
		{
			if (!isContained(distributions.get(hostname), vmName))
			{
				return vmName;
			}
		}
		// TODO : should not go through here
		return null;
	}
	
	private boolean isContained(List<DistributionNode> distributions, String vmName)
	{
		if (distributions == null || distributions.isEmpty())
		{
			return false;
		}
		for (DistributionNode distributionNode : distributions)
		{
			if (distributionNode.getVmName().equals(vmName))
			{
				return true;
			}
		}
		return false;
	}
	private boolean isEligibleHostForMigration(String hostname, Map<String, List<DistributionNode>> distributions)
	{
		List<DistributionNode> vmList = distributions.get(hostname);
		if (vmList == null || vmList.isEmpty())
		{
			return true;
		}
		double listSize = vmList.size();
		BigDecimal rate = new BigDecimal(listSize / hostVmNames.get(hostname).size());
		return maxDistributionPercentage.compareTo(rate) > 0;
	}
	
	private DistributionNode getRandomDistribution(List<String> hostNames, Map<String, List<DistributionNode>> distributions)
	{
		for (String hostName : hostNames)
		{
			if (distributions.get(hostName).size() == 1)
			{
				return distributions.get(hostName).get(0);
			}
		}
		String hostName = hostNames.get(0);
		int randomIndex = random.nextInt(distributions.get(hostName).size());
		return distributions.get(hostName).get(randomIndex);
	}
	
	public List<Antibody> getOrderedListFromMap(Map<Antibody, List<Antibody>> antibodiesMap, Antibody initialAntibody){
		// get antibodies from map and set into a list ordered by fitness value
		List<Antibody> antibodies = new ArrayList<Antibody>();
		for (List<Antibody> arrays : antibodiesMap.values()){
			antibodies.addAll(arrays);
		}
		Collections.sort(antibodies, new AntibodyComparator(initialAntibody));
		return antibodies;
	}

	public List<Antibody> generateRandomAntibodies(int number){
		List<Antibody> antibodies = new ArrayList<Antibody>();
		for (int i = 0 ; i < number ; i++){
			
//			Antibody antibody = new Antibody();
//			
//			antibody.setActivities(this.generateRandomActivities(weight));
//			antibody.setAlimentation(this.generateRandomAlimentation(constraintMethod));
//			antibody.setWeight(weight);
		}
		return antibodies;
	}

	public BigDecimal individualSharedFitness(Antibody antibody, Antibody initialAntibody) {
		BigDecimal energyPerHosts = getEnergyPerHosts(antibody);
		BigDecimal energyPerMigration = getEnergyPerMigration(antibody);
		BigDecimal energyPerHostCreation = getEnergyPerHostCreation(antibody, initialAntibody);
		BigDecimal energyPerHostDeletion = getEnergyPerHostDeletion(antibody, initialAntibody);
		return energyPerHosts.add(energyPerMigration).add(energyPerHostCreation).add(energyPerHostDeletion);
	}
	
	private BigDecimal getEnergyPerHosts(Antibody antibody)
	{
		BigDecimal total = ZERO;
		for (String host: antibody.getVmDistribution().keySet())
		{
			BigDecimal energy = energyPerHost
					.multiply(new BigDecimal(antibody.getVmDistribution().get(host).size()))
					.divide(new BigDecimal(hostVmNames.get(host).size()));
			total = total.add(energy).add(energyPerHostLoaded);
		}
		return total;
	}
	
	private BigDecimal getEnergyPerMigration(Antibody antibody)
	{
		return energyPerVmMigration.multiply(new BigDecimal(antibody.getMigrations().size()));
	}
	
	private BigDecimal getEnergyPerHostCreation(Antibody antibody, Antibody initialAntibody)
	{
		BigDecimal total = ZERO;
		Set<String> initialhosts = initialAntibody.getVmDistribution().keySet();
		for (String host : antibody.getVmDistribution().keySet())
		{
			if (!initialhosts.contains(host))
			{
				total = total.add(energyPerHostCreation);
			}
		}
		return total;
	}
	
	private BigDecimal getEnergyPerHostDeletion(Antibody antibody, Antibody initialAntibody)
	{
		BigDecimal total = ZERO;
		Set<String> hosts = antibody.getVmDistribution().keySet();
		for (String host : initialAntibody.getVmDistribution().keySet())
		{
			if (!hosts.contains(host))
			{
				total = total.add(energyPerHostDeletion);
			}
		}
		return total;
	}
	
}
