package cantina;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class UINode {
    private String name;
    private String identifier;
    private String uiClass;
    private List<String> classnames = new ArrayList<String>();
    private ArrayList<UINode> children = new ArrayList<UINode>();
    private JsonNode node;
    
    public UINode(String name, JsonNode node) {
	this.node = node;
	this.name = name;
	
	if (node.get("identifier") != null) {
	    this.identifier = node.get("identifier").asText();
	}
	if (node.get("class") != null) {
	    this.uiClass = node.get("class").asText();
	}
	
	JsonNode n = node.get("classNames");
	if (n != null && n.getNodeType() == JsonNodeType.ARRAY) {
	    for (JsonNode c : n) {
		classnames.add(c.asText());
	    }
	}
	
	JsonNode jsonContentView = node.get("contentView");
	if (jsonContentView != null) {
	    this.children.add(new UINode("contentView", jsonContentView));
	}
	
	JsonNode jsonControl = node.get("control");
	if (jsonControl != null) {
	    this.children.add(new UINode("control", jsonControl));
	}
	
	JsonNode jsonSubviewArray = node.get("subviews");
	if (jsonSubviewArray != null && jsonSubviewArray.getNodeType() == JsonNodeType.ARRAY) {
	    for (JsonNode jsonSubview : jsonSubviewArray) {
		UINode subview = new UINode("", jsonSubview);
		this.children.add(subview);
	    }
	}
    }
    
    public String getIdentifier() {
	return identifier;
    }
    
    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }
    
    public String getUiClass() {
	return uiClass;
    }
    
    public void setUiClass(String uiClass) {
	this.uiClass = uiClass;
    }
    
    public ArrayList<UINode> getChildren() {
	return children;
    }
    
    public void setChildren(ArrayList<UINode> children) {
	this.children = children;
    }	
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public List<String> getClassNames() {
	return classnames;
    }
    
    public void setClassNames(List<String> classNames) {
	this.classnames = classNames;
    }
    
    public String toString() {
	return node.toString();
    }
}
