package classifying;

import java.util.ArrayList;

public class Classifier {

	private ArrayList<Model> trainingSet = new ArrayList<>();

	public static int OUTLOOK = 0, TEMPERATURE = 1, HUMIDITY = 2, WINDY = 3;
	public Classifier() {
		super();
	}

	public ArrayList<Model> getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(ArrayList<Model> trainingSet) {
		this.trainingSet = trainingSet;
	}

	public void generateTrainingSet() {
		if (trainingSet.isEmpty()) {
                    ArrayList<Model> list = new ArrayList<>();
                    
                    list.add(new Model(Model.SUNNY, Model.HOT, Model.HIGH, Model.FALSE, false));
                    list.add(new Model(Model.SUNNY, Model.HOT, Model.HIGH, Model.TRUE, false));
                    list.add(new Model(Model.OVERCAST, Model.HOT, Model.HIGH, Model.FALSE, true));
                    list.add(new Model(Model.RAIN, Model.MILD, Model.HIGH, Model.FALSE, true));
                    list.add(new Model(Model.RAIN, Model.COOL, Model.NORMAL, Model.FALSE, true));
                    list.add(new Model(Model.RAIN, Model.COOL, Model.NORMAL, Model.TRUE, false));
                    list.add(new Model(Model.OVERCAST, Model.COOL, Model.NORMAL, Model.TRUE, true));
                    list.add(new Model(Model.SUNNY, Model.MILD, Model.HIGH, Model.FALSE, false));
                    list.add(new Model(Model.SUNNY, Model.COOL, Model.NORMAL, Model.FALSE, true));
                    list.add(new Model(Model.RAIN, Model.MILD, Model.NORMAL, Model.FALSE, true));
                    list.add(new Model(Model.SUNNY, Model.MILD, Model.NORMAL, Model.TRUE, true));
                    list.add(new Model(Model.OVERCAST, Model.MILD, Model.HIGH, Model.TRUE, true));
                    list.add(new Model(Model.OVERCAST, Model.HOT, Model.NORMAL, Model.FALSE, true));
                    list.add(new Model(Model.RAIN, Model.MILD, Model.HIGH, Model.TRUE, false));
                    
                    trainingSet = list;
		}

	}

	private double log2(double x) {
		return Math.log(x) / Math.log(2);
	}
	private double entropy(double... values) {
		double result = 0.0;
		for (double d : values) {
			result += d * log2(d);
		}
		result *= (double) -1;
		if (Double.isNaN(result)) {
			return 0.0;
		}
		return result;
	}

        public double temperatureInfo() {
		double hotSize = 0.0;
		double hotGood = 0.0;
		double hotBad = 0.0;
		double mildSize = 0.0;
		double mildGood = 0.0;
		double mildBad = 0.0;
		double coolSize = 0.0;
		double coolGood = 0.0;
		double coolBad = 0.0;
		for (Model m : trainingSet) {
			if (m.getTemperature().equals(Model.HOT)) {
				hotSize++;
				if (m.isGoodWeather()) {
					hotGood++;
				} else {
					hotBad++;
				}
			}
			if (m.getTemperature().equals(Model.MILD)) {
				mildSize++;
				if (m.isGoodWeather()) {
					mildGood++;
				} else {
					mildBad++;
				}
			}
			if (m.getTemperature().equals(Model.COOL)) {
				coolSize++;
				if (m.isGoodWeather()) {
					coolGood++;
				} else {
					coolBad++;
				}
			}
		}
		double result = hotSize / (double) trainingSet.size() * entropy(hotGood / hotSize, hotBad / hotSize);
		result += mildSize / (double) trainingSet.size() * entropy(mildGood / mildSize, mildBad / mildSize);
		result += coolSize / (double) trainingSet.size() * entropy(coolGood / coolSize, coolBad / coolSize);
		return result;
	}
	public double outlookInfo() {
		double sunnySize = 0.0;
		double sunnyGood = 0.0;
		double sunnyBad = 0.0;
		double overcastSize = 0.0;
		double overcastGood = 0.0;
		double overcastBad = 0.0;
		double rainSize = 0.0;
		double rainGood = 0.0;
		double rainBad = 0.0;
		for (Model m : trainingSet) {
			if (m.getOutlook().equals(Model.SUNNY)) {
				sunnySize++;
				if (m.isGoodWeather()) {
					sunnyGood++;
				} else {
					sunnyBad++;
				}
			}
			if (m.getOutlook().equals(Model.OVERCAST)) {
				overcastSize++;
				if (m.isGoodWeather()) {
					overcastGood++;
				} else {
					overcastBad++;
				}
			}
			if (m.getOutlook().equals(Model.RAIN)) {
				rainSize++;
				if (m.isGoodWeather()) {
					rainGood++;
				} else {
					rainBad++;
				}
			}
		}
		double result = sunnySize / trainingSet.size() * entropy(sunnyGood / sunnySize, sunnyBad / sunnySize);
		result += overcastSize / trainingSet.size() * entropy(overcastGood / overcastSize, overcastBad / overcastSize);
		result += rainSize / trainingSet.size() * entropy(rainGood / rainSize, rainBad / rainSize);
		return result;
	}
        public double humidityInfo() {
		double highSize = 0.0;
		double highGood = 0.0;
		double highBad = 0.0;
		double normalSize = 0.0;
		double normalGood = 0.0;
		double normalBad = 0.0;
		for (Model m : trainingSet) {
			if (m.getHumidity().equals(Model.HIGH)) {
				highSize++;
				if (m.isGoodWeather()) {
					highGood++;
				} else {
					highBad++;
				}
			}
			if (m.getHumidity().equals(Model.NORMAL)) {
				normalSize++;
				if (m.isGoodWeather()) {
					normalGood++;
				} else {
					normalBad++;
				}
			}
		}
		double result = highSize / (double) trainingSet.size() * entropy(highGood / highSize, highBad / highSize);
		result += normalSize / (double) trainingSet.size() * entropy(normalGood / normalSize, normalBad / normalSize);
		return result;
	}
        public double windyInfo() {
		double trueSize = 0.0;
		double trueGood = 0.0;
		double trueBad = 0.0;
		double falseSize = 0.0;
		double falseGood = 0.0;
		double falseBad = 0.0;
		for (Model m : trainingSet) {
			if (m.getWindy().equals(Model.TRUE)) {
				trueSize++;
				if (m.isGoodWeather()) {
					trueGood++;
				} else {
					trueBad++;
				}
			}
			if (m.getWindy().equals(Model.FALSE)) {
				falseSize++;
				if (m.isGoodWeather()) {
					falseGood++;
				} else {
					falseBad++;
				}
			}
		}
		double result = trueSize / (double) trainingSet.size() * entropy(trueGood / trueSize, trueBad / trueSize);
		result += falseSize / (double) trainingSet.size() * entropy(falseGood / falseSize, falseBad / falseSize);
                return result;
	}
	
	public double temperatureSplitInfo() {
		double hotSize = 0.0;
		double mildSize = 0.0;
		double coolSize = 0.0;
		for (Model m : trainingSet) {
			if (m.getTemperature().equals(Model.HOT)) {
				hotSize++;
			}
			if (m.getTemperature().equals(Model.MILD)) {
				mildSize++;
			}
			if (m.getTemperature().equals(Model.COOL)) {
				coolSize++;
			}
		}
		double result = (-1.0*hotSize / (double)trainingSet.size()) * log2(hotSize/(double)trainingSet.size());
		result+= (-1.0*mildSize / (double)trainingSet.size()) * log2(mildSize/(double)trainingSet.size());
		result+= (-1.0*coolSize / (double)trainingSet.size()) * log2(coolSize/(double)trainingSet.size());
		return result;
	}
	public double outlookSplitInfo() {
		double sunnySize = 0.0;
		double overcastSize = 0.0;
		double rainSize = 0.0;
		for (Model m : trainingSet) {
			if (m.getOutlook().equals(Model.SUNNY)) {
				sunnySize++;
			}
			if (m.getOutlook().equals(Model.OVERCAST)) {
				overcastSize++;
			}
			if (m.getOutlook().equals(Model.RAIN)) {
				rainSize++;
			}
		}
		double result = (-1.0 * sunnySize / (double)trainingSet.size()) * log2(sunnySize/(double)trainingSet.size());
		result+= (-1.0 * overcastSize / (double)trainingSet.size()) * log2(overcastSize/(double)trainingSet.size());
		result+= (-1.0 * rainSize / (double)trainingSet.size()) * log2(rainSize/(double)trainingSet.size());
		return result;
	}
        public double humiditySplitInfo() {
		double highSize = 0.0;
		double normalSize = 0.0;
		for (Model m : trainingSet) {
			if (m.getHumidity().equals(Model.HIGH)) {
				highSize++;
			}
			if (m.getHumidity().equals(Model.NORMAL)) {
				normalSize++;
			}
		}
		double result = (-1.0 * highSize / (double)trainingSet.size()) * log2(highSize/(double)trainingSet.size());
		result+= (-1.0 * normalSize / (double)trainingSet.size()) * log2(normalSize/(double)trainingSet.size());
		return result;
	}
        public double windySplitInfo() {
		double trueSize = 0.0;
		double falseSize = 0.0;
		for (Model m : trainingSet) {
			if (m.getWindy().equals(Model.TRUE)) {
				trueSize++;
			}
			if (m.getWindy().equals(Model.FALSE)) {
				falseSize++;
			}
		}
		double result = (-1.0 * trueSize / (double)trainingSet.size()) * log2(trueSize/(double)trainingSet.size());
		result+= (-1.0 * falseSize / (double)trainingSet.size()) * log2(falseSize/(double)trainingSet.size());
		return result;
	}
        
	public double gainRatio(int attr) {
            double positive = 0.0;
            for (Model m : trainingSet) {
                if (m.isGoodWeather()) {
                    positive++;
		}
            }
            double negative = trainingSet.size() - positive;
            double info = entropy(positive / trainingSet.size(), negative / trainingSet.size());
            
            switch(attr) {
		case(0): return (info - outlookInfo())/outlookSplitInfo();
		case(1): return (info - temperatureInfo())/temperatureSplitInfo();
                case(2): return (info - humidityInfo())/humiditySplitInfo();
		default: return (info - windyInfo())/windySplitInfo();
            }
	}

	public ArrayList<Model> getModelsWithValues(String[] values) {

		ArrayList<Model> models = new ArrayList<>();

		for (Model m : trainingSet) {
			if ((m.getOutlook().equals(values[0]) || values[0] == null)
                            && (m.getTemperature().equals(values[1]) || values[1] == null)
                            && (m.getHumidity().equals(values[2]) || values[2] == null)
                            && (m.getWindy().equals(values[3]) || values[3] == null)){
                        models.add(m);	
			}
		}
		return models;
	}

}
