package test.org.cloudsim.master.algorithm;

import static java.util.Arrays.asList;
import static main.org.cloudsim.master.algorithm.ClonalSelectionAlgoritm.CONFIGURATION_PATH;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import main.org.cloudsim.master.algorithm.ClonalSelectionAlgoritm;
import main.org.cloudsim.master.algorithm.Operation;
import main.org.cloudsim.master.model.Antibody;
import main.org.cloudsim.master.model.DistributionNode;
import main.org.cloudsim.master.model.Migration;
import main.org.cloudsim.master.utils.XmlParser;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import test.org.cloudsim.master.factories.AntibodyFactory;

public class TestOperation {

	@Test
	public void shouldComputeIndividualSharedFitness() throws ParserConfigurationException, SAXException, IOException
	{
		Antibody initialAntibody = AntibodyFactory.create("initial-antibody");
		
		Antibody antibody = AntibodyFactory.create("test");
		antibody.setMigrations(createMigrations());
		antibody.setVmDistribution(createDistributedNodes());
		
		Document document = XmlParser.createDocument(CONFIGURATION_PATH);
		Operation operation = new Operation(document);
		
		BigDecimal result = operation.individualSharedFitness(antibody, initialAntibody);
		
		Assert.assertEquals(new BigDecimal("170"), result);
	}
	private Map<String, List<DistributionNode>> createDistributedNodes()
	{
		Map<String, List<DistributionNode>> distributedNodes = new HashMap<String, List<DistributionNode>>();
		distributedNodes.put("host1", Arrays.asList(
				new DistributionNode("host1", "vm1"),
				new DistributionNode("host1", "vm2"),
				new DistributionNode("host1", "vm3"),
				new DistributionNode("host1", "vm4")));
		distributedNodes.put("host3", Arrays.asList(
				new DistributionNode("host3", "vm1"),
				new DistributionNode("host3", "vm5")));
		return distributedNodes;
	}
	
	private List<Migration> createMigrations()
	{
		Migration migration1 = new Migration();
		migration1.setFrom(new DistributionNode("host2", "vm3"));
		migration1.setTo(new DistributionNode("host1", "vm3"));
		
		Migration migration2 = new Migration();
		migration2.setFrom(new DistributionNode("host2", "vm4"));
		migration2.setTo(new DistributionNode("host1", "vm4"));
		
		return asList(migration1, migration2);
	}
}
