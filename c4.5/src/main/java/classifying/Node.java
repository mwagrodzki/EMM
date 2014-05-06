package classifying;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {

        public String idString;
    
	public ArrayList<Node> children;
	public Node parent;
	public String label;
        
	public String attribute;
	public int attributeNum;
	public String classValue;
	public int level;
	
	public int positive;
	public int negative;
	public String[] valuesCombination = new String[4];

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }
    
	public String getClassValue() {
		return classValue;
	}

	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}

	public void addChild(Node node) {
		children.add(node);
		node.setParent(this);
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
	
	@JsonIgnore
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setAttribute(int num) {
		switch (num) {
			case (0):
				this.attribute = "OUTLOOK";
				break;
			case (1):
				this.attribute = "TEMPERATURE";
				break;
                        case (2):
                                this.attribute = "HUMIDITY";
                                break;
			default:
				this.attribute = "WINDY";
		}
		this.attributeNum = num;
	}

	public int getAttributeNum() {
		return attributeNum;
	}

	public void setAttributeNum(int attributeNum) {
		this.attributeNum = attributeNum;
	}

	public Node() {
		label = "";
		children = new ArrayList<>();
		parent = null;
		classValue = null;
	}

	@Override
	public String toString() {
		return this.classValue != null ? this.classValue : this.attribute;
	}
	
	@Override
	public boolean equals(Object o) {
		Node tn = (Node) o;
		return tn.valuesCombination == this.valuesCombination;
	}
}
