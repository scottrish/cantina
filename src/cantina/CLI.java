package cantina;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CLI {

    private UINode rootNode;

    enum SelectorType {
	CLASS, CLASSNAMES, IDENTIFIER
    }
    
    class Selector {
	SelectorType type;
	
	// For 'class' and 'identifier', there can be only one value.
	// 'classnames' can have multiple chained values.
	List<String> values;
	
	Selector(SelectorType type, List<String> values) {
	    this.type = type;
	    this.values = values;
	}
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

	CLI cli = new CLI(in);
	cli.run();
    }
    
    public CLI(InputStream in) {
	// Use Jackson to construct a tree of JsonNodes
	ObjectMapper objectMapper = new ObjectMapper();
	JsonNode jsonNode = null;
	try {
	    jsonNode = objectMapper.readTree(in);
	} catch (IOException e) {
	    System.out.println("Unable to process file");
	    System.exit(0);
	}

	// As the JsonNode-based tree has a node for each line in the file, it does not
	// reflect any semantics of the file, so construct a tree of UINodes which does add
	// some of those semantics.
	this.rootNode = new UINode("root", jsonNode);
    }
    
    public void run() {
	// Accept input from command line and traverse the UINode tree for matches.
	while (true) {
	    // Enter data using BufferReader
	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	    String selector = null;
	    try {
		System.out.print("Enter selector: ");
		selector = reader.readLine();
		if (selector.equals("bye")) {
		    System.out.println("Exiting.......");
		    System.exit(0);
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    if (selector != null && selector.length() > 0) {
		System.out.println("--------------------------------------------------");
		List<UINode> matchingNodes = find(rootNode, selector);
		if (matchingNodes.size() == 0) {
		    System.out.println("No nodes found");
		} else {
		    System.out.println("Found " + matchingNodes.size() + " nodes:");
		    for (UINode n : matchingNodes) {
			System.out.println(n);
			System.out.println();
		    }
		}
		System.out.println("--------------------------------------------------");
	    }
	}
    }

    public List<UINode> find(String selector) {
	return this.find(rootNode, selector);
    }
    
    public List<UINode> find(UINode node, String selector) {
	List<Selector> selectors = new ArrayList<Selector>();
	
	// If the selector starts with a class then create a Selector and strip off the 
	// class from the remainder of the selector string.
	if (selector.charAt(0) != '.' && selector.charAt(0) != '#') {
	    // Trim any selectors after the class.
	    String c = selector;
	    if (selector.indexOf('.') > 0) {
		c = selector.substring(0, selector.indexOf('.'));
		selector = selector.substring(c.length(), selector.length());
	    }
	    else if (selector.indexOf('#') > 0) { 
		c = selector.substring(0, selector.indexOf('#'));
		selector = selector.substring(c.length(), selector.length());
	    }
	    List<String> selValue = new ArrayList<String>();
	    selValue.add(c);
	    selectors.add(new Selector(SelectorType.CLASS, selValue));
	}
	
	// Add onto the selector array any possibly chained classname values.
	if (selector.indexOf('.') >= 0) {
	    String[] classnames = selector.substring(1).split("\\.");
	    List<String> c = Arrays.asList(classnames);
	    selectors.add(new Selector(SelectorType.CLASSNAMES, c));
	}	
	// Add onto the selector array the identifiervalue 
	else if (selector.indexOf('#') >= 0) {
	    String identifier = selector.substring(1);
	    List<String> c = Arrays.asList(identifier);
	    selectors.add(new Selector(SelectorType.IDENTIFIER, c));
	}	
	
	List<UINode> matchingNodes = new ArrayList<UINode>();
	find(node, selectors, matchingNodes);

	return matchingNodes;
    }

    public void find(UINode node, List<Selector> selectors, List<UINode> matchingNodes) {

	boolean candidate = true;
	for (Selector s : selectors) {
	    switch(s.type) {
	    case CLASS: {
		candidate &= (node.getUiClass() != null && node.getUiClass().equals(s.values.get(0)));
		break;
	    }
	    case IDENTIFIER: {
		candidate &= (node.getIdentifier() != null && node.getIdentifier().equals(s.values.get(0)));
		break;		
	    }
	    case CLASSNAMES: {
		candidate &= (node.getClassNames() != null && 
			(node.getClassNames().containsAll(s.values)));
		break;				
	    }
	    }
	}
	if (candidate) {
	    matchingNodes.add(node);
	}

	// If the node has children continue traversing
	if (node.getChildren().size() > 0) {
	    for (UINode n : node.getChildren()) {
		find(n, selectors, matchingNodes);
	    }
	}
    }
}
