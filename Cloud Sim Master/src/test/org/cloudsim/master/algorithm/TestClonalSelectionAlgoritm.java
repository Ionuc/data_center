package test.org.cloudsim.master.algorithm;

import static main.org.cloudsim.master.algorithm.ClonalSelectionAlgoritm.CONFIGURATION_PATH;
import static main.org.cloudsim.master.utils.XmlParser.createDocument;

import java.math.BigDecimal;
import java.util.Calendar;

import junit.framework.Assert;

import main.org.cloudsim.master.algorithm.ClonalSelectionAlgoritm;
import main.org.cloudsim.master.algorithm.Operation;
import main.org.cloudsim.master.model.Antibody;
import main.org.cloudsim.master.utils.XmlParser;

import org.cloudbus.cloudsim.core.CloudSim;
import org.junit.Test;
import org.w3c.dom.Document;

import test.org.cloudsim.master.factories.AntibodyFactory;

public class TestClonalSelectionAlgoritm {

	@Test
	public void shouldGenerateAntibody() throws Exception
	{
		ClonalSelectionAlgoritm clonalSelectionAlgoritm = new ClonalSelectionAlgoritm(1);
		
		Antibody antibody = AntibodyFactory.create("test");
		Antibody result = clonalSelectionAlgoritm.clonalSelectionOptimization(antibody);
		
		Document document = createDocument(CONFIGURATION_PATH);
		Operation operation = new Operation(document);
		BigDecimal initialAntibodyFitness = operation.individualSharedFitness(antibody, antibody);
		BigDecimal antibodyFitness = operation.individualSharedFitness(result, antibody);
		
		System.out.println("initialAntibodyFitness : " + initialAntibodyFitness);
		System.out.println("antibodyFitness : " + antibodyFitness);
		Assert.assertTrue(initialAntibodyFitness.compareTo(antibodyFitness) > 0);
		
		System.out.println(antibody.toString());
		System.out.println(result.toString());
	}
}
