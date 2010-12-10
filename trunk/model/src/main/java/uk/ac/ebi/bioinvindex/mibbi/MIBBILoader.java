package uk.ac.ebi.bioinvindex.mibbi;

/*
 * __________
 * CREDITS
 * __________
 *
 * Team page: http://isatab.sf.net/
 * - Marco Brandizi (software engineer: ISAvalidator, ISAconverter, BII data management utility, BII model)
 * - Eamonn Maguire (software engineer: ISAcreator, ISAcreator configurator, ISAvalidator, ISAconverter,  BII data management utility, BII web)
 * - Nataliya Sklyar (software engineer: BII web application, BII model,  BII data management utility)
 * - Philippe Rocca-Serra (technical coordinator: user requirements and standards compliance for ISA software, ISA-tab format specification, BII model, ISAcreator wizard, ontology)
 * - Susanna-Assunta Sansone (coordinator: ISA infrastructure design, standards compliance, ISA-tab format specification, BII model, funds raising)
 *
 * Contributors:
 * - Manon Delahaye (ISA team trainee:  BII web services)
 * - Richard Evans (ISA team trainee: rISAtab)
 *
 *
 * ______________________
 * Contacts and Feedback:
 * ______________________
 *
 * Project overview: http://isatab.sourceforge.net/
 *
 * To follow general discussion: isatab-devel@list.sourceforge.net
 * To contact the developers: isatools@googlegroups.com
 *
 * To report bugs: http://sourceforge.net/tracker/?group_id=215183&atid=1032649
 * To request enhancements:  http://sourceforge.net/tracker/?group_id=215183&atid=1032652
 *
 *
 * __________
 * License:
 * __________
 *
 * This work is licenced under the Creative Commons Attribution-Share Alike 2.0 UK: England & Wales License. To view a copy of this licence, visit http://creativecommons.org/licenses/by-sa/2.0/uk/ or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA.
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */

import au.com.bytecode.opencsv.CSVReader;
import uk.ac.ebi.bioinvindex.model.Identifiable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Nataliya Sklyar
 * Date: Feb 24, 2009
 */
public class MIBBILoader {

	private int ACRONYM_INDEX = 5;
	private int FULL_NAME_INDEX = 4;

	private String filePath;

	private EntityManager entityManager;


	public MIBBILoader() {
	}

	public MIBBILoader(String filePath, EntityManager entityManager) {
		this.filePath = filePath;
		this.entityManager = entityManager;
	}


	public void loadProjects() throws IOException {

		InputStream resource = this.getClass().getClassLoader().getResourceAsStream(filePath);
		if (resource == null) {
			throw new IOException("File " + filePath + " is not found");
		}

		CSVReader reader = new CSVReader(new InputStreamReader(resource));

		List<String[]> entries = reader.readAll();

		if (entries.get(0) == null) {
			throw new IOException("MIBBI file is empty");
		}

		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		for (int i = 0; i < entries.get(0).length; i++) {
			String[] acronyms = entries.get(ACRONYM_INDEX);
			String[] full_names = entries.get(FULL_NAME_INDEX);
			MIProject project = new MIProject(acronyms[i], full_names[i]);

			entityManager.persist(project);
			System.out.println("project = " + project);
		}

		transaction.commit();

	}

	public String getFilePath() {
		if (filePath == null) {
			throw new IllegalStateException("filePath is required but has not been set");
		}
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("entityManager is required but has not been set");
		}
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
