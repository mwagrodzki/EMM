package classifying;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Tree implements Serializable {
	private static final Logger LOG = Logger.getLogger(Tree.class.getName());
		
	private Node root;
	private Integer length = 0;
	private Classifier classifier;

	public Node getRoot() {
		return root;
	}

	public String getRootStr() {
		return root.attribute + " " + root.label + " " + root.parent;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Tree(Classifier c) {
		this.classifier = c;
                root = new Node();
		root.label = "root";
                root.setIdString("root");
                String[] valueCombo = new String[4];
		List<Integer> used = new ArrayList<>();
                
		double bestGain = 0.0;
		int bestAttr = 0;
		for (int i = 0; i < 4; i++) {
			double tmpGain = c.gainRatio(i);
			if (tmpGain > bestGain) {
				bestAttr = i;
				bestGain = tmpGain;
			}
		}
		root.setAttribute(bestAttr);
		root.setAttributeNum(bestAttr);
		
		buildTree(c, root, valueCombo, 0, used);
	}

	private void buildTree(Classifier c, Node parent, String[] valueCombo, int level, List<Integer> usedAttributes) {
		if (length < level + 1) {
			length = level + 1;
		}
		if (parent.getAttributeNum() == 0) {
			for (String val : Model.OUTLOOK_VALUES) {
				build(c, parent, valueCombo, level, usedAttributes, val, 0);
			}
		} else if (parent.getAttributeNum() == 1) {
			for (String val : Model.TEMPERATURE_VALUES) {
				build(c, parent, valueCombo, level, usedAttributes, val, 1);
			}
		} else if (parent.getAttributeNum() == 2) {
			for (String val : Model.HUMIDITY_VALUES) {
				build(c, parent, valueCombo, level, usedAttributes, val, 2);
			}
		} else if (parent.getAttributeNum() == 3) {
			for (String val : Model.WINDY_VALUES) {
				build(c, parent, valueCombo, level, usedAttributes, val, 3);
			}
		} 
	}

	private void build(Classifier c, Node parent, String[] valueCombo, int level, List<Integer> usedAttributes, String val, int index) {
		String[] newValueCombo = new String[4];
		newValueCombo[0] = valueCombo[0];
		newValueCombo[1] = valueCombo[1];
                newValueCombo[2] = valueCombo[2];
                newValueCombo[3] = valueCombo[3];
		List<Integer> newUsed = new ArrayList<>();
		newUsed.addAll(usedAttributes);
		newValueCombo[index] = val;
		newUsed.add(index);
		ArrayList<Model> list = c.getModelsWithValues(newValueCombo);
		int positive = 0;
		for (Model m : list) {
			if (m.isGoodWeather()) {
				positive++;
			}
		}
		if (list.isEmpty()) {
			return;
		}
		double good = (double) positive / (double) list.size();
		if (newUsed.size() == 4) {
			addLeaf(list, positive, parent, val, newValueCombo);
		} else {
			Node child = new Node();
			child.level = level + 1;
			child.setLabel(val);
			child.setIdString(parent.getIdString() + child.getLabel());
			child.positive = positive;
			child.negative = list.size() - positive;
			child.valuesCombination[0] = newValueCombo[0];
			child.valuesCombination[1] = newValueCombo[1];
                        child.valuesCombination[2] = newValueCombo[2];
                        child.valuesCombination[3] = newValueCombo[3];
			if (good == 1.0) {
				addLeaf(list, positive, parent, val, newValueCombo);
			} else if (good == 0.0) {
				addLeaf(list, positive, parent, val, newValueCombo);
			} else {
				double bestGain = 0.0;
				int bestAttr = 0;
				for (int i = 0; i < 4; i++) {
					double tmpGain = c.gainRatio(i);
					if (tmpGain > bestGain
							&& !newUsed.contains(i)) {
						bestAttr = i;
						bestGain = tmpGain;
					}
				}
				child.setAttribute(bestAttr);
				buildTree(c, child, newValueCombo, level + 1, newUsed);
				parent.addChild(child);
			}
			
		}
	}
	private void addLeaf(ArrayList<Model> list, int positive, Node parent, String val, String[] valuesCombo) {
		int negative = list.size() - positive;
		Node l = new Node();
		l.valuesCombination[0] = valuesCombo[0];
                l.valuesCombination[1] = valuesCombo[1];
                l.valuesCombination[2] = valuesCombo[2];
		l.valuesCombination[3] = valuesCombo[3];
		l.positive = positive;
		l.negative = list.size() - positive;
		if (positive > negative) {
			l.level = parent.level + 1;
			l.setLabel(val);
			l.classValue = "Good (" + positive + ")";
			l.setIdString(parent.getIdString() + l.getLabel());
			parent.addChild(l);
		} else {
			l.level = parent.level + 1;
			l.setLabel(val);
			l.classValue = "Bad (" + (list.size() - positive) + ")";
			l.setIdString(parent.getIdString() + l.getLabel());
			parent.addChild(l);
		}

	}

	public void print() {
		printString(root);
	}
	public void printString(Node parent) {
		String line = "|";
		for (int i = 0; i < parent.level; i++) {
			line += "-";
		}
		if (!parent.label.equals("")) {
			line += "[" + parent.label + "]";
		}
		if (parent.classValue == null) {
			line += parent.attribute;
		} else {
			line += " -> " + parent.classValue;
		}
		LOG.info(line);
		for (Node node : parent.children) {
			printString(node);
		}
	}

	public String htmlString() {
		return nodeHtmlString(root);
	}
	private String nodeHtmlString(Node parent) {
		String line = "<br/>|";
		for (int i = 0; i < parent.level; i++) {
			line += "-";
		}
		if (!parent.label.equals("")) {
			line += "[" + parent.label + "]";
		}
		if (parent.classValue == null) {
			line += parent.attribute;
		} else {
			line += " -> " + parent.classValue;
		}
		for (Node node : parent.children) {
			line += nodeHtmlString(node);
		}
		return line;
	}

	public static List<Node> nodes(Node node) {
		List<Node> list = new ArrayList<>();
		if (node.children.size() > 0) {
			for (Node child : node.children) {
				list.add(child);
				list.addAll(nodes(child));
			}
		}
		return list;
	}

	public int queryModel(String outlook, String temperature, String humidity, String windy) {
		return queryNode(outlook, temperature, humidity, windy, root);
	}
	private int queryNode(String outlook, String temperature, String humidity, String windy, Node node) {
		if (node.classValue != null) {
			if (node.classValue.contains("Bad")) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (node.attributeNum == Classifier.OUTLOOK) {
				for (Node child : node.children) {
					if (child.label.equals(outlook)) {
						return queryNode(outlook, temperature, humidity, windy, child);
					}
				}
				return 2;
			} else if (node.attributeNum == Classifier.TEMPERATURE) {
				for (Node child : node.children) {
					if (child.label.equals(temperature)) {
						return queryNode(outlook, temperature, humidity, windy, child);
					}
				}
				return 2;
			} else if (node.attributeNum == Classifier.HUMIDITY) {
				for (Node child : node.children) {
					if (child.label.equals(humidity)) {
						return queryNode(outlook, temperature, humidity, windy, child);
					}
				}
				return 2;
			} else {
				for (Node child : node.children) {
					if (child.label.equals(windy)) {
						return queryNode(outlook, temperature, humidity, windy, child);
					}
				}
				return 2;
			} 

		}
	}

}
