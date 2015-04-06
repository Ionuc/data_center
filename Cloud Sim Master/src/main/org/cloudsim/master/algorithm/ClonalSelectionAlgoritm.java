package main.org.cloudsim.master.algorithm;

import static main.org.cloudsim.master.utils.Constants.ANTIBODY_ITERATIONS;
import static main.org.cloudsim.master.utils.Constants.ANTIBODY_NUMBER;
import static main.org.cloudsim.master.utils.Constants.CLONED_ANTIBODY_NUMBER;
import static main.org.cloudsim.master.utils.Constants.MAX_DISTRIBUTION_PERCENTAGE;
import static main.org.cloudsim.master.utils.Constants.MAX_NUMBER_HOST;
import static main.org.cloudsim.master.utils.Constants.MAX_NUMBER_VM;
import static main.org.cloudsim.master.utils.XmlParser.createDocument;
import static main.org.cloudsim.master.utils.XmlParser.getIntegerValue;
import static main.org.cloudsim.master.utils.XmlParser.getStringValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import main.org.cloudsim.master.model.Antibody;
import main.org.cloudsim.master.utils.Constants;
import main.org.cloudsim.master.utils.XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClonalSelectionAlgoritm {

	public static final String CONFIGURATION_PATH = "D:\\Proiecte-acasa\\Cloud Sim Master\\configurations.xml";
	
	private int antibodyNumber;
	private int cloneIteration;
	private int betaValue;

	private int clonedAntibodyNr;

	private Operation operation;

	private int nrTakeFromSelected ;
	private int nrTakeFromCloned ;
	
	public ClonalSelectionAlgoritm(int betaValue) throws Exception{
		 
		Document document = XmlParser.createDocument(CONFIGURATION_PATH);
		operation = new Operation(document);

		this.betaValue = betaValue;
		antibodyNumber = getIntegerValue(document, ANTIBODY_NUMBER);
		cloneIteration = getIntegerValue(document, ANTIBODY_ITERATIONS);
		clonedAntibodyNr = getIntegerValue(document, CLONED_ANTIBODY_NUMBER);
		
		nrTakeFromCloned = (antibodyNumber * 40) / 100;
		nrTakeFromSelected = antibodyNumber - nrTakeFromCloned;
	}

	private List<Antibody> initializeAntibodies(Antibody antibody, int antibodyNr) throws Exception {
		List<Antibody> antibodies = new ArrayList<Antibody>();
		for (int i = 0 ; i < antibodyNr; i ++)
		{
			antibodies.add(antibody.cloneAntibody());
		}
		return antibodies;
	}

	public Antibody clonalSelectionOptimization(Antibody initialAntibody) throws Exception {

		List<Antibody> selectedAntibodies = initializeAntibodies(initialAntibody, antibodyNumber);

		Antibody bestAntibody = null;
		for (int iteration = 1; iteration <= cloneIteration; iteration++) {
			System.out.println("iteration : " + iteration);
			
			// select first N antibodies
		    selectedAntibodies = operation.select(selectedAntibodies, antibodyNumber);
			// clone them according to index values which reflects how good is
			// his fitness value (first index is the best, last position is the
			// worst)
			Map<Antibody, List<Antibody>> amtibodyMap = operation.cloneSelectedAntibodies(selectedAntibodies, betaValue, antibodyNumber);
			// permute the VM items between them
			operation.hypermut(amtibodyMap, selectedAntibodies, antibodyNumber);
			// get a list of all cloned antibodies inserted in order of fitness value
			List<Antibody> antibodyClones = operation.getOrderedListFromMap(amtibodyMap, initialAntibody);
			// generated m clones randomly
			List<Antibody> randomAntibodies = operation.generateRandomAntibodies(clonedAntibodyNr);
			// insert these selected antibody clones into selectedAntibodies
			operation.insertAllAntibodiesInOrder(antibodyClones, randomAntibodies, initialAntibody);
			// select first n of them
			antibodyClones = operation.select(antibodyClones, nrTakeFromCloned);
			selectedAntibodies = operation.select(selectedAntibodies, nrTakeFromSelected);
			// add these clones to selectedAntibodies
			operation.insertAllAntibodiesInOrder(selectedAntibodies, antibodyClones, initialAntibody);

			Antibody selectedAntibody = selectedAntibodies.get(0);
			if (bestAntibody == null) {
				bestAntibody = selectedAntibody;
			} else if (operation.individualSharedFitness(bestAntibody, initialAntibody)
					.compareTo(operation.individualSharedFitness(selectedAntibody, initialAntibody)) > 0) {
				bestAntibody = selectedAntibody;
			}
		}
		return bestAntibody;
	}

}
