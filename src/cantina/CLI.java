package cantina;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CLI {

    enum SelectorType {
	CLASS,
	CLASSNAMES,
	IDENTIFIER
    }
    
    public static void main(String[] args) {
	if (args.length == 0) {
	    System.out.println("Usage: java cantina.CLI <input filename>");
	    System.exit(0);
	}
	
	InputStream in = null;
	try {
	    in = new FileInputStream(args[0]);
	} catch (FileNotFoundException e) {
	    System.out.println("File not found");
	    System.exit(0);
	}
	
	// Use Jackson to construct a tree of JsonNodes
	ObjectMapper objectMapper = new ObjectMapper();
	JsonNode jsonNode = null;
	try {
	    jsonNode = objectMapper.readTree(in);
	} catch (IOException e) {
	    System.out.println("Unable to process file");
	    System.exit(0);			
	}
	
	// As the JsonNode-based tree has a node for each line in the file, it does not reflect
	// any semantics of the file, so construct a tree of UINodes which does add 
	// some of those semantics.
	UINode rootNode = new UINode("root", jsonNode);
	
	// Accept input from command line and traverse the UINode tree for matches. 
	while (true) {
	    //Enter data using BufferReader 
	    BufferedReader reader =  
		new BufferedReader(new InputStreamReader(System.in)); 
	    
	    // Reading data using readLine 
	    String selector = null;
	    try {
		System.out.print("Enter selector: ");
		selector = reader.readLine();
	    } catch (IOException e) {
		e.printStackTrace();
	    } 
	    if (selector != null && selector.length() > 0) {
		System.out.println("--------------------------------------------------");
		CLI.find(rootNode, selector);
		System.out.println("--------------------------------------------------");
	    }
	}
    }
    
    public static void find(UINode node, String selector) {
	// Determine the selector type to avoid having to parse the selector.
	// This will need changed to support chained or compound selectors. 
	SelectorType selType = SelectorType.CLASS;
	
	if (selector.charAt(0) == '.') {
	    selector = selector.substring(1);
	    selType = SelectorType.CLASSNAMES;
	}
	else if (selector.charAt(0) == '#') {
	    selType = SelectorType.IDENTIFIER;
	    selector = selector.substring(1);
	}
	
	List<UINode> matchingNodes = new ArrayList<UINode>();
	CLI.find(node, selector, selType, matchingNodes);
	
	if (matchingNodes.size() == 0) {
	    System.out.println("No nodes found");
	}
	else {
	    System.out.println("Found " + matchingNodes.size() + " nodes:");
	    for (UINode n : matchingNodes) {
		System.out.println(n);
		System.out.println();
	    }
	}
    }
    
    public static void find(UINode node, String selector, SelectorType selType, List<UINode> matchingNodes) {
	
	// Does current node match, if so add to the list
	switch(selType) {
	case CLASS: {
	    if (node.getUiClass() != null && node.getUiClass().equals(selector)) {
		matchingNodes.add(node);
	    }
	    break;
	}
	case IDENTIFIER: {
	    if (node.getIdentifier() != null && node.getIdentifier().equals(selector)) {
		matchingNodes.add(node);
	    }
	    break;
	}
	case CLASSNAMES: {
	    if (node.getClassNames().contains(selector)) {
		matchingNodes.add(node);
	    }
	    break;
	}
	}
	
	// If the node has children continue traversing
	if (node.getChildren().size() > 0) {
	    for (UINode n : node.getChildren()) {
		CLI.find(n, selector, selType, matchingNodes);
	    }
	}
    }
}
