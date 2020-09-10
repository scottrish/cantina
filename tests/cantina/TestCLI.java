package cantina;

import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.Before;

import cantina.CLI;
import cantina.UINode;

public class TestCLI {

    CLI cli;
    
    String input = "{\n" + 
    	"  \"identifier\": \"System\",\n" + 
    	"  \"subviews\": [\n" + 
    	"    {\n" + 
    	"      \"class\": \"StackView\",\n" + 
    	"      \"classNames\": [\n" + 
    	"        \"container\"\n" + 
    	"      ],\n" + 
    	"      \"subviews\": [\n" + 
    	"        {\n" + 
    	"          \"class\": \"StackView\",\n" + 
    	"          \"classNames\": [\n" + 
    	"            \"columns\",\n" + 
    	"            \"container\"\n" + 
    	"          ]\n" + 
    	"        }\n" + 
    	"      ]\n" + 
    	"  	}\n" + 
    	"	]\n" + 
    	"}";

    public TestCLI() {
    }
    
    @Before
    public void setUp() throws Exception {
//	InputStream in = new ByteArrayInputStream(input.getBytes());
	InputStream in = this.getClass().getClassLoader().getResourceAsStream("SettingsViewController.json");
	cli = new CLI(in);
    }

    @Test
    public void testFindUINodeClass() {
	String selector = "StackView";
	List<UINode> selectedNodes = cli.find(selector);
	assertEquals(6, selectedNodes.size());
    }

    @Test
    public void testFindUINodeClassname() {
	String selector = ".container";
	List<UINode> selectedNodes = cli.find(selector);
	assertEquals(6, selectedNodes.size());
    }

    @Test
    public void testFindUINodeChainedClassname() {
	String selector = ".container.columns";
	List<UINode> selectedNodes = cli.find(selector);
	assertEquals(1, selectedNodes.size());
    }    

    @Test
    public void testFindUINodeInput() {
	String selector = "Input";
	List<UINode> selectedNodes = cli.find(selector);
	assertEquals(26, selectedNodes.size());
    }    
}
