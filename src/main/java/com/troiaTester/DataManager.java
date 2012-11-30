package main.com.troiaTester;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;



import troiaClient.CategoryFactory;
import troiaClient.GoldLabel;
import troiaClient.Label;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.*;

/**
 * Test data manager is used for managing text files containing test data.
 *
 * @author piotr.gnys@10clouds.com
 */
public class DataManager {

	/**
	 * Saves collection of test objects to file.
	 *
	 * @param filename
	 *            Name of file to with collection will be saved
	 * @param objects
	 *            Collection of test objects
	 * @throws IOException
	 *             Thrown if program was unable to save objects to file
	 */
	public void saveTestObjectsToFile(String filename,
			TroiaObjectCollection objects) throws IOException {
		logger.info("Saving test objects to file");
		FileOutputStream stream = new FileOutputStream(filename);
		Writer out = new OutputStreamWriter(stream);
		for (String object : objects) {
			out.write(object + '\t' + objects.getCategory(object) + '\n');
		}
		out.close();
	}

	/**
	 * Loads collection of test object from file. File must be correctly
	 * formatted with means that each line should have pairs object-category
	 * separated by tabulator, for example : Object-4 Category-0 Object-5
	 * Category-1 Object-2 Category-0
	 *
	 * @param filename
	 *            Name of file that contains test objects.
	 * @return Collection of test object generated with data from file.
	 * @throws FileNotFoundException
	 *             If there is no file with given name.
	 */
	public TroiaObjectCollection loadTestObjectsFromFile(String filename)
	throws FileNotFoundException {
		logger.info("Loadin test objects from file");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		String line, objectName, objectCategory;
		TroiaObjectCollection testObjects = new TroiaObjectCollection();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			objectName = line.substring(0, line.indexOf('\t'));
			objectCategory = line.substring(line.indexOf('\t') + 1,
											line.length());
			testObjects.setCategory(objectName, objectCategory);
		}
		scanner.close();
		return testObjects;
	}

	/**
	 * Saves JSONified collection of artificial workers to file.
	 *
	 * @param filename
	 *            Target file
	 * @param workers
	 *            Collection of workers that will be saved
	 * @throws IOException
	 *             Thrown if program was unable to save workers to file
	 */
	public void saveArtificialWorkers(String filename,
			Collection<ArtificialWorker> workers)
			throws IOException {
		logger.info("Saving artificial workers to file");
		FileOutputStream stream = new FileOutputStream(filename);
		Writer out = new OutputStreamWriter(stream);
		Gson gson = new Gson();
		out.write(gson.toJson(workers));
		out.close();
	}

	public void saveArtificialWorkersStats(String filename,
			Collection<ArtificialWorkerStats> stats)
			throws IOException {
		logger.info("Saving  artificial workers to file");
		FileOutputStream stream = new FileOutputStream(filename);
		Writer out = new OutputStreamWriter(stream);
		for (ArtificialWorkerStats stat : stats) {
			out.write(stat.getWorker().getName() + "\t" +
				stat.getAccuracy() + '\t' +
				stat.getQualityExpected() + '\t' +
				stat.getQualityExpected() + '\n');
		}
		out.close();
	}

	/**
	 * Loads fully configured artificial workers from file. This file should
	 * contain JSONified ArtificialWorker classes. File containing those workers
	 * should be generated by "saveArtificialWorkers" function, not manually by
	 * user. For files in with user can define workers you should see
	 * "loadBasicWorkers" function.
	 *
	 * @see saveArtificialWorkers
	 * @see loadBasicWorkers
	 * @param filename
	 *            Name of file containing JSONified artificial workers
	 * @return Collection of artificial workers fetched from file
	 * @throws FileNotFoundException
	 *             If there is no file with given name.
	 */
	public Collection<ArtificialWorker> loadArtificialWorkersFromFile(
		String filename) throws FileNotFoundException {
		logger.info("Loading artificial workers from file");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<ArtificialWorker>>() {} .getType();
		Collection<ArtificialWorker> workers;
		scanner.useDelimiter("\\Z");
		workers = gson.fromJson(scanner.next(),collectionType);
		scanner.close();
		return workers;
	}

	/**
	 * Loads basic workers description from file and uses it to generate
	 * collection of artificial workers in environment given as a parameter.
	 * Correct format of file is worker-quality pairs separated by tabulation,
	 * for example Worker1 0.4 Worker2 0.2 Worker3 1
	 *
	 * @param filename
	 *            Name of file with contains basic workers definitions
	 * @param categories
	 *            Categoris with workers will be assigning
	 * @return Collection of artificial workers with names and qualities defined
	 *         in file
	 * @throws FileNotFoundException
	 *             If there is no file with given name.
	 */
	public Collection<ArtificialWorker> loadBasicWorkers(String filename,
			Collection<String> categories) throws FileNotFoundException {
		logger.info("Loading worker definintion from baisc workers file.");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		String line;
		Collection<ArtificialWorker> workers = new ArrayList<ArtificialWorker>();
		DataGenerator genreator = DataGenerator.getInstance();
		String workerName;
		Double workerQuality;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			workerName = line.substring(0, line.indexOf('\t'));
			workerQuality = Double.parseDouble(line.substring(
												   line.indexOf('\t') + 1, line.length()));
			workers.add(genreator.generateArtificialWorker(workerName,
						workerQuality, categories));
		}
		scanner.close();
		return workers;
	}

	/**
	 * Saves labels to file
	 *
	 * @param filename
	 *            Name of file
	 * @param labels
	 * @throws IOException
	 */
	public void saveLabelsToFile(String filename, Collection<Label> labels)
	throws IOException {
		logger.info("Saving labels to file");
		FileOutputStream stream = new FileOutputStream(filename);
		Writer out = new OutputStreamWriter(stream);
		for (Label label : labels) {
			out.write(label.getWorkerName() + '\t' + label.getObjectName()
					  + '\t' + label.getCategoryName() + '\n');
		}
		out.close();
	}

	/**
	 * Loads labels from correctly formatted text file. Format must be
	 * worker_name tabulator object_id tabulator object class For example :
	 * Worker-7 Object-6 Category-1 Worker-8 Object-7 Category-2 Worker-9
	 * Object-7 Category-2
	 *
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
	public Collection<Label> loadLabelsFromFile(String filename)
	throws FileNotFoundException {
		logger.info("Loading labels from file");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		String line;
		Collection<Label> labels = new ArrayList<Label>();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			labels.add(this.parseLabelFromString(line));
		}
		scanner.close();
		return labels;
	}

	/**
	 * Parses string formatted as
	 * <workerName><tabulation><objectName><tabulation><categoryName> into
	 * label.
	 *
	 * @param line
	 * @return
	 */
	public Label parseLabelFromString(String line) {
		String objectName, objectCategory, workerName;
		workerName = line.substring(0, line.indexOf('\t'));
		objectName = line.substring(line.indexOf('\t') + 1,
									line.lastIndexOf('\t'));
		objectCategory = line.substring(line.lastIndexOf('\t') + 1,
										line.length());
		return new Label(workerName, objectName, objectCategory);
	}

	/**
	 * Saves gold labels to file
	 *
	 * @param filename
	 * @param labels
	 * @throws IOException
	 */
	public void saveGoldLabelsToFile(String filename,
			Collection<GoldLabel> labels) throws IOException {
		logger.info("Saving gold labels objects to file");
		FileOutputStream stream = new FileOutputStream(filename);
		Writer out = new OutputStreamWriter(stream);
		for (GoldLabel label : labels) {
			out.write(label.getObjectName() + '\t' + label.getCorrectCategory()
					  + '\n');
		}
		out.close();
	}

	/**
	 * Loads gold labels from file
	 *
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
	public Collection<GoldLabel> loadGoldLabelsFromFile(String filename)
			throws FileNotFoundException {
		logger.info("Loading gold labels from file");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		String line, objectName, objectCategory;
		Collection<GoldLabel> goldLabels = new ArrayList<GoldLabel>();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			objectName = line.substring(0, line.indexOf('\t'));
			objectCategory = line.substring(line.indexOf('\t') + 1,
											line.length());
			goldLabels.add(new GoldLabel(objectName, objectCategory));
		}
		scanner.close();
		return goldLabels;
	}


	/**
	 * Loads category names with probabilities of their occurence from file
	 * with formatting <category_name><tabulation><category_prior>
	 * @param filename Name of file containing categories with probabilities
	 * @return Map that associates category name with probability of category occurence
	 */
	public Map<String, Double> loadCategoriesWithProbabilities(String filename)
	throws FileNotFoundException {
		logger.info("Loading prior file");
		FileInputStream stream = new FileInputStream(filename);
		Scanner scanner = new Scanner(stream);
		String line;
		Map<String, Double> categories = new HashMap<String, Double>();
		String categoryName;
		Double categoryProbability;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			categoryName = line.substring(0, line.indexOf('\t'));
			categoryProbability = Double.parseDouble(line.substring(
									  line.indexOf('\t') + 1, line.length()));
			categories.put(categoryName, categoryProbability);
		}
		scanner.close();
		return categories;
	}

	public void saveTestData(String filename_base, Data data)
	throws IOException {
		logger.info("Saving test data");
		if(data.getArtificialWorkers()!=null) {
			this.saveArtificialWorkers(filename_base +
				ARTIFICIAL_WORKERS_TAG +
				FILE_EXTENSION, data.getArtificialWorkers());
		}
		if(data.getArtificialWorkersStats()!=null) {
			this.saveArtificialWorkersStats(filename_base +
				ARTIFICIAL_WORKERS_STATISTICS_TAG +
				FILE_EXTENSION,
				data.getArtificialWorkersStats());
		}
		if(data.getGoldLabels()!=null) {
			this.saveGoldLabelsToFile(filename_base + GOLD_LABELS_TAG
									  + FILE_EXTENSION, data.getGoldLabels());
		}
		if(data.getLabels()!=null) {
			this.saveLabelsToFile(filename_base + LABELS_TAG + FILE_EXTENSION,
								  data.getLabels());
		}
		if(data.getObjectCollection()!=null) {
			this.saveTestObjectsToFile(
				filename_base + OBJECTS_TAG + FILE_EXTENSION,
				data.getObjectCollection());
		}
	}

	public Data loadTestData(String filename_base)
	throws FileNotFoundException {
		Data data = new Data();
		data.setArtificialWorkers(this
								  .loadArtificialWorkersFromFile(filename_base
										  + ARTIFICIAL_WORKERS_TAG + FILE_EXTENSION));
		data.setGoldLabels(this.loadGoldLabelsFromFile(filename_base
						   + GOLD_LABELS_TAG + FILE_EXTENSION));
		data.setLabels(this.loadLabelsFromFile(filename_base + LABELS_TAG
											   + FILE_EXTENSION));
		data.setObjectCollection(this.loadTestObjectsFromFile(filename_base
								 + OBJECTS_TAG + FILE_EXTENSION));
		data.setWorkers(this.extractWorkerNamesFromLabels(data.getLabels()));
		data.setCategories(CategoryFactory.getInstance().createCategories(
							   this.extractCategoryNamesFromLabels(data.getLabels())));
		return data;
	}

	public Collection<String> extractWorkerNamesFromLabels(
		Collection<Label> labels) {
		Collection<String> workers = new ArrayList<String>();
		for (Label label : labels) {
			if (!workers.contains(label.getWorkerName())) {
				workers.add(label.getWorkerName());
			}
		}
		return workers;
	}

	public Collection<String> extractCategoryNamesFromLabels(
		Collection<Label> labels) {
		Collection<String> categories = new ArrayList<String>();
		for (Label label : labels) {
			if (!categories.contains(label.getCategoryName())) {
				categories.add(label.getCategoryName());
			}
		}
		return categories;
	}

	public String converToJSON(Data data) {
		Gson gson = new Gson();
		return gson.toJson(data);
	}

	public Data loadFromJSON(String jsonifiedData) {
		Gson gson = new Gson();
		return gson.fromJson(jsonifiedData, Data.class);
	}

	/**
	 * @return the instance
	 */
	public static DataManager getInstance() {
		return instance;
	}

	private static DataManager instance = new DataManager();

	private DataManager() {

	}

	private static final String ARTIFICIAL_WORKERS_TAG = "_aiworker";
	private static final String ARTIFICIAL_WORKERS_STATISTICS_TAG =
		ARTIFICIAL_WORKERS_TAG + "_statistics";
	private static final String LABELS_TAG = "_labels";
	private static final String GOLD_LABELS_TAG = "_goldLabels";
	private static final String OBJECTS_TAG = "_objects";
	private static final String FILE_EXTENSION = ".txt";

	private static Logger  logger = Logger.getLogger(DataManager.class);
}
