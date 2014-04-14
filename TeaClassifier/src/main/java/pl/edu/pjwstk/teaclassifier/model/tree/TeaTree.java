package pl.edu.pjwstk.teaclassifier.model.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pl.edu.pjwstk.teaclassifier.classifying.TeaClassifier;
import pl.edu.pjwstk.teaclassifier.model.Tea;

public class TeaTree implements Serializable {

	private TeaNode root;
	private Integer length = 0;
	private TeaClassifier teaClassifier;

	public TeaNode getRoot() {
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

	public TeaTree(TeaClassifier tc) {
		this.teaClassifier = tc;
		double bestGain = 0.0;
		int bestAttr = 0;
		//0 - tea type, 1 - sugar, 2 - add
		for (int i = 0; i < 3; i++) {
			double tmpGain = tc.gainRatio(i);
			if (tmpGain > bestGain) {
				bestAttr = i;
				bestGain = tmpGain;
			}
		}
		root = new TeaNode();
		root.label = "root";
		root.setAttribute(bestAttr);
		root.setAttributeNum(bestAttr);
		root.setIdString("root");
		String[] valueCombo = new String[2];
		List<Integer> used = new ArrayList<>();
		buildTree(tc, root, valueCombo, 0, used, -1);
	}

	private void buildTree(TeaClassifier tc, TeaNode parent, String[] valueCombo, int level, List<Integer> usedAttributes, int sugar) {
		if (length < level + 1) {
			length = level + 1;
		}
		if (parent.getAttributeNum() == 0) {
			for (String val : Tea.TEA_TYPE_VALUES) {
				buildForStringFeature(tc, parent, valueCombo, level, usedAttributes, sugar, val, 0);
			}
		} else if (parent.getAttributeNum() == 2) {
			for (String val : Tea.ADDITION_VALUES) {
				buildForStringFeature(tc, parent, valueCombo, level, usedAttributes, sugar, val, 1);
			}
		} else if (parent.getAttributeNum() == 1) {
			buildForDoubleFeature(tc, parent, valueCombo, level, usedAttributes, sugar, 1);
		}
	}

	private void buildForDoubleFeature(TeaClassifier tc, TeaNode parent, String[] valueCombo, int level, List<Integer> usedAttributes, int sugar, int index) {
		for (int p = 0; p < 2; p++) {
			int newSugar = p;
			String label = newSugar == 0 ? "<=" + tc.sugarGain()[1] : ">" + tc.sugarGain()[1];
			List<Integer> newUsed = new ArrayList<>();
			newUsed.addAll(usedAttributes);
			newUsed.add(1);
			ArrayList<Tea> list = tc.getTeasWithValueS(valueCombo, newSugar);
			if (list.isEmpty()) {
				return;
			}

			int positive = 0;
			for (Tea t : list) {
				if (t.isDrinkable()) {
					positive++;
				}
			}
			if (newUsed.size() == 3) {
				addLeaf(list, positive, parent, label);
			} else {
				TeaNode child = new TeaNode();
				child.level = level + 1;
				child.setLabel(label);
				child.setIdString(parent.getIdString() + child.getLabel());
				double percentGood = (double) positive / (double) list.size();
				if (percentGood == 1.0) {
					child.classValue = "Good (" + positive + ")";
				} else if (percentGood == 0.0) {
					child.classValue = "Bad (" + (list.size() - positive) + ")";
				} else {
					double bestGain = 0.0;
					int bestAttr = 0;
					//0 - tea type, 1 - sugar, 2 - add
					for (int i = 0; i < 3; i++) {
						double tmpGain = tc.gainRatio(i);
						if (tmpGain > bestGain
								&& !newUsed.contains(i)) {
							bestAttr = i;
							bestGain = tmpGain;
						}
					}
					child.setAttribute(bestAttr);
					buildTree(tc, child, valueCombo, level + 1, newUsed, newSugar);
				}
				parent.addChild(child);
			}
		}

	}

	private void buildForStringFeature(TeaClassifier tc, TeaNode parent, String[] valueCombo, int level, List<Integer> usedAttributes, int sugar, String val, int index) {
		String[] newValueCombo = new String[2];
		newValueCombo[0] = valueCombo[0];
		newValueCombo[1] = valueCombo[1];
		List<Integer> newUsed = new ArrayList<>();
		newUsed.addAll(usedAttributes);
		newValueCombo[index] = val;
		newUsed.add(index);
		ArrayList<Tea> list = tc.getTeasWithValueS(newValueCombo, sugar);
		int positive = 0;
		for (Tea t : list) {
			if (t.isDrinkable()) {
				positive++;
			}
		}
		if (list.isEmpty()) {
			return;
		}
		double percentGood = (double) positive / (double) list.size();
		if (newUsed.size() == 3) {
			addLeaf(list, positive, parent, val);
		} else {

			TeaNode child = new TeaNode();
			child.level = level + 1;
			child.setLabel(val);
			child.setIdString(parent.getIdString() + child.getLabel());
			if (percentGood == 1.0) {
				child.classValue = "Good (" + positive + ")";
			} else if (percentGood == 0.0) {
				child.classValue = "Bad (" + (list.size() - positive) + ")";
			} else {
				double bestGain = 0.0;
				int bestAttr = 0;
				//0 - tea type, 1 - sugar, 2 - add
				for (int i = 0; i < 3; i++) {
					double tmpGain = tc.gainRatio(i);
					if (tmpGain > bestGain
							&& !newUsed.contains(i)) {
						bestAttr = i;
						bestGain = tmpGain;
					}
				}
				child.setAttribute(bestAttr);
				buildTree(tc, child, newValueCombo, level + 1, newUsed, sugar);
			}
			parent.addChild(child);
		}
	}

	private void addLeaf(ArrayList<Tea> list, int positive, TeaNode parent, String val) {
		int a = 0;
		int negative = list.size() - positive;
		TeaNode leaf = new TeaNode();
		if (positive > negative) {
			leaf.level = parent.level + 1;
			leaf.setLabel(val);
			leaf.classValue = "Good (" + positive + ")";
			leaf.setIdString(parent.getIdString() + leaf.getLabel());
			parent.addChild(leaf);
		} else {
			leaf.level = parent.level + 1;
			leaf.setLabel(val);
			leaf.classValue = "Bad (" + (list.size() - positive) + ")";
			leaf.setIdString(parent.getIdString() + leaf.getLabel());
			parent.addChild(leaf);
		}

	}
	
	public void print() {
		printString(root);
	}

	public void printString(TeaNode parent) {
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
		for (TeaNode node : parent.children) {
			printString(node);
		}
	}

	public String htmlString() {
		return nodeHtmlString(root);
	}

	private String nodeHtmlString(TeaNode parent) {
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
		for (TeaNode node : parent.children) {
			line += nodeHtmlString(node);
		}
		return line;
	}

	public static List<TeaNode> nodes(TeaNode node) {
		List<TeaNode> list = new ArrayList<>();
		if (node.children.size() > 0) {
			for (TeaNode child : node.children) {
				list.add(child);
				list.addAll(nodes(child));
			}
		}
		return list;
	}

	/**
	 * 0 - false, 1 - true, 2 - not known.
	 *
	 * @param teaType
	 * @param addition
	 * @param sugar
	 * @return
	 */
	public int queryTea(String teaType, String addition, Double sugar) {
		return queryNode(teaType, addition, sugar, root);
	}

	private int queryNode(String teaType, String addition, Double sugar, TeaNode node) {
		if (node.classValue != null) {
			if (node.classValue.contains("Bad")) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (node.attributeNum == TeaClassifier.ADDITION) {
				for (TeaNode child : node.children) {
					if (child.label.equals(addition)) {
						return queryNode(teaType, addition, sugar, child);
					}
				}
				return 2;
			} else if (node.attributeNum == TeaClassifier.TEA_TYPE) {
				for (TeaNode child : node.children) {
					if (child.label.equals(teaType)) {
						return queryNode(teaType, addition, sugar, child);
					}
				}
				return 2;
			} else {
				for (TeaNode child : node.children) {
					if (child.label.contains("<=")
							&& sugar <= teaClassifier.sugarGain()[1]) {
						return queryNode(teaType, addition, sugar, child);
					} else if (child.label.contains(">")
							&& sugar > teaClassifier.sugarGain()[1]) {
						return queryNode(teaType, addition, sugar, child);
					}
				}
				return 2;
			}

		}
	}

}
