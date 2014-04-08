/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pjwstk.teaclassifier.model.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pl.edu.pjwstk.teaclassifier.classifying.TeaClassifier;
import pl.edu.pjwstk.teaclassifier.model.Tea;

/**
 *
 * @author sergio
 */
public class TeaTree implements Serializable{

	private TeaNode root;

	public TeaNode getRoot() {
		return root;
	}

	public String getRootStr() {
		return root.attribute + " " + root.label + " " + root.parent;
	}

	public TeaTree(TeaClassifier tc) {
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
		root.setAttribute(bestAttr);
		root.setAttributeNum(bestAttr);
		String[] valueCombo = new String[2];
		List<Integer> used = new ArrayList<>();
		buildTree(tc, root, valueCombo, 0, used);
	}

	private void buildTree(TeaClassifier tc, TeaNode parent, String[] valueCombo, int level, List<Integer> usedAttributes) {
		if (parent.getAttributeNum() == 0) {
			for (String val : Tea.TEA_TYPE_VALUES) {
				String[] newValueCombo = new String[2];
				newValueCombo[0] = valueCombo[0];
				newValueCombo[1] = valueCombo[1];
				List<Integer> newUsed = new ArrayList<>();
				newUsed.addAll(usedAttributes);
				newValueCombo[0] = val;
				newUsed.add(0);
				ArrayList<Tea> list = tc.getTeasWithValueS(newValueCombo);
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
					return;
				}

				TeaNode child = new TeaNode();
				child.level = level + 1;
				child.setLabel(val);
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
					buildTree(tc, child, newValueCombo, level + 1, newUsed);
				}
				parent.addChild(child);
			}
		} else if (parent.getAttributeNum() == 2) {

			for (String val : Tea.ADDITION_VALUES) {
				String[] newValueCombo = new String[2];
				newValueCombo[0] = valueCombo[0];
				newValueCombo[1] = valueCombo[1];
				List<Integer> newUsed = new ArrayList<>();
				newUsed.addAll(usedAttributes);
				newValueCombo[1] = val;
				newUsed.add(2);
				ArrayList<Tea> list = tc.getTeasWithValueS(newValueCombo);
				int positive = 0;
				for (Tea t : list) {
					if (t.isDrinkable()) {
						positive++;
					}
				}
				if (list.isEmpty()) {
					return;
				}

				if (newUsed.size() == 3) {
					addLeaf(list, positive, parent, val);
					return;
				}

				TeaNode child = new TeaNode();
				child.level = level + 1;
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
					buildTree(tc, child, newValueCombo, level + 1, newUsed);
				}
				child.setLabel(val);
				parent.addChild(child);
			}
		} else if (parent.getAttributeNum() == 1) {
			String[] newValueCombo = new String[2];
			newValueCombo[0] = valueCombo[0];
			newValueCombo[1] = valueCombo[1];
			List<Integer> newUsed = new ArrayList<>();
			newUsed.addAll(usedAttributes);
			newUsed.add(1);
			ArrayList<Tea> list = tc.getTeasWithValueS(newValueCombo);
			if (list.isEmpty()) {
				return;
			}
			if (list.isEmpty()) {
				return;
			}
			int positive = 0;
			for (Tea t : list) {
				if (t.getSugar() > tc.sugarGain()[1]) {
					positive++;
				} else {

				}
			}

			double dsize = (double) list.size();
			if (positive > 0) {
				TeaNode childPos = new TeaNode();
				childPos.level = level + 1;
				childPos.setLabel("<=" + tc.sugarGain()[1]);
				if (newUsed.size() == 3) {

				}
				childPos.classValue = "Good (" + positive + ")";
				parent.addChild(childPos);
			}
			if (list.size() - positive > 0) {
				TeaNode childNeg = new TeaNode();
				childNeg.level = level + 1;
				childNeg.setLabel(">" + tc.sugarGain()[1]);
				childNeg.classValue = "Bad (" + (list.size() - positive) + ")";
				parent.addChild(childNeg);
			}

		}
	}

	private void addLeaf(ArrayList<Tea> list, int positive, TeaNode parent, String val) {

		if (positive > 0) {
			TeaNode leaf = new TeaNode();
			leaf.level = parent.level + 1;
			leaf.setLabel(val);
			leaf.classValue = "Good (" + positive + ")";
			parent.addChild(leaf);
		}
		if (list.size() - positive > 0) {
			TeaNode leaf = new TeaNode();
			leaf.level = parent.level + 1;
			leaf.setLabel(val);
			leaf.classValue = "Bad (" + (list.size() - positive) + ")";
			parent.addChild(leaf);
		}

	}

	@Override
	public String toString() {
		String line = "\n";
		return null;

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
		System.out.println(line);
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

	
}
