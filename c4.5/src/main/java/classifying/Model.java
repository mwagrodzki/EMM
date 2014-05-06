/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classifying;

import java.util.ArrayList;
import java.util.List;
import classifying.Node;

/**
 *
 * @author Macha
 */
public class Model {
    
    public static final String 
			SUNNY = "sunny",OVERCAST = "overcast", RAIN = "rain",
			HOT = "hot", MILD = "mild", COOL = "cool",
                        HIGH = "high", NORMAL = "normal",
                        TRUE = "true", FALSE = "false";
    
    public static final String[] OUTLOOK_VALUES = {SUNNY, OVERCAST, RAIN};
    public static final String[] TEMPERATURE_VALUES = {HOT, MILD, COOL};
    public static final String[] HUMIDITY_VALUES = {HIGH, NORMAL};
    public static final String[] WINDY_VALUES = {TRUE, FALSE};
    
    public boolean goodWeather;
    public String outlook;
    public String temperature;
    public String humidity;
    public String windy;

    public boolean isGoodWeather() {
        return goodWeather;
    }

    public void setGoodWeather(boolean goodWeather) {
        this.goodWeather = goodWeather;
    }

    public String getOutlook() {
        return outlook;
    }

    public void setOutlook(String outlook) {
        this.outlook = outlook;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindy() {
        return windy;
    }

    public void setWindy(String windy) {
        this.windy = windy;
    }
    
    public Model(String outlook, String temperature, String humidity, String windy) {
		super();
		this.goodWeather = false;
		this.outlook = outlook;
		this.temperature = temperature;
		this.humidity = humidity;
                this.windy = windy;
    }
    
    public Model(String outlook, String temperature, String humidity, String windy, boolean b) {
		super();
		this.goodWeather = b;
		this.outlook = outlook;
		this.temperature = temperature;
		this.humidity = humidity;
                this.windy = windy;
    }
	
    public Model() {
		super();
		this.goodWeather = false;
    }
    
    @Override
	public String toString() {
		return this.outlook+", "+this.temperature+", "+this.humidity+", "+this.windy+" => "+this.goodWeather;
	}
	
	public static List<Model> convertNToTea(List<Node> nodes) {		
		List<Model> models = new ArrayList<>();
		for (Node node : nodes) {
			Model m = new Model();
                        m.setWindy(node.valuesCombination[3]);
			m.setHumidity(node.valuesCombination[2]);
                        m.setOutlook(node.valuesCombination[1]);
                        m.setTemperature(node.valuesCombination[0]);
			models.add(m);
		}
		return models;
	}
}
