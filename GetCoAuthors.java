
import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


class GetCoAuthors  {

	public static void main(String[] args) throws IOException {

		// variables to store user's input
		// authorName = users input
		// refinedAuthorName = author's name with spaces replaced with '+' 
		String authorName;
		String refinedAuthorName = "";
		

		//Asking User to give input
		System.out.println("Enter author name\nHint:Enter full name to avoid any other author with the same name\n");


		//Reading User's Input from console
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in));	
		authorName = br.readLine();
		

		//Replacing blank spaces in name with '+' sign
		for(int i=0;i<authorName.length();i++) {  
         
         	char ch = authorName.charAt(i);  
         	if(ch == ' ') {  
            	refinedAuthorName = refinedAuthorName + '+';       
         	} 
         	else {
         	 	refinedAuthorName = refinedAuthorName + ch;
         	}
     	}

     	System.out.println(refinedAuthorName);



		String esearchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi/?db=pubmed&term=" + refinedAuthorName + "[Author]&usehistory=y";
		System.out.println(esearchURL);
		int c;
    	URL esearchQuery = new URL(esearchURL); 
    	URLConnection esearchQueryCon = esearchQuery.openConnection();

    	long len = esearchQueryCon.getContentLength();
    	FileOutputStream fout = null;

    	String esearchXML = "";
    	if(len != 0) { 
        System.out.println("=== Content ==="); 
      	InputStream input = esearchQueryCon.getInputStream(); 

      	fout = new FileOutputStream("esearch.xml");


      	while (((c = input.read()) != -1)) { 
        esearchXML = esearchXML + ((char) c);
        fout.write(c); 
      	} 
      	input.close(); 
 		} 
 		else { 
      	System.out.println("No content available."); 
		}

		System.out.println(esearchXML);

		try {

		File feSearchResult = new File("esearch.xml");

		//get the factory
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		

		//Using factory get an instance of document builder
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		//parse using builder to get DOM representation of the XML file
		Document doc = dBuilder.parse(feSearchResult);
		doc.getDocumentElement().normalize();
		String webenv = "";
		NodeList nList = doc.getElementsByTagName("WebEnv");
 
		System.out.println("----------------------------");
 
		for (int temp = 0; temp < nList.getLength(); temp++) {
 
		Node nNode = nList.item(temp);
 
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
 		
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
 			webenv = eElement.getTextContent();
			//System.out.println("Staff id : " + eElement.getNodeValue());
			System.out.println("WebEnv: " + webenv);


		}}

		 nList = doc.getElementsByTagName("QueryKey");
 			String query_key = "";
		System.out.println("----------------------------");
 
		for (int temp = 0; temp < nList.getLength(); temp++) {
 
		Node nNode = nList.item(temp);
 
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
 
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
 			 query_key = eElement.getTextContent();
			//System.out.println("Staff id : " + eElement.getNodeValue());
			System.out.println("QueryKey: " + query_key);
		}}

		String esummaryURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&query_key="+query_key+"&WebEnv="+webenv;
		System.out.println(esummaryURL);
		//int c;
    	URL esummaryQuery = new URL(esummaryURL); 
    	URLConnection esummaryQueryCon = esummaryQuery.openConnection();

    	len = esummaryQueryCon.getContentLength();
    	fout = null;

    	String esummaryXML = "";
    	if(len != 0) { 
        System.out.println("=== Content ==="); 
      	InputStream input = esummaryQueryCon.getInputStream(); 

      	fout = new FileOutputStream("esummary.xml");


      	while (((c = input.read()) != -1)) { 
        esummaryXML = esummaryXML + ((char) c);
        fout.write(c); 
      	} 
      	input.close(); 
 		} 
 		else { 
      	System.out.println("No content available."); 
		}
		/*get a nodelist of elements
		NodeList nl = doc.getElementsByTagName("eSearchResult");
		if(nl != null && nl.getLength() > 0) {
			
				//get the employee element
				Node node = nl.item(0);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element)node;

				//get the WebEnv object
				//String webenv = 

				System.out.println(el.getTextContent());
			}
				//add it to list
				//myEmpls.add(e);
			}
*/
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		try {
 
	File fXmlFile = new File("esummary.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
 
	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();
 
	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
 
	NodeList nList = doc.getElementsByTagName("Item");
	String attr = new String("AuthorList");
	String AuthorName = new String("");
	ArrayList<String> list = new ArrayList<String>();

 
	System.out.println("----------------------------");
 
	for (int temp = 0; temp < nList.getLength(); temp++) {
 
		Node nNode = nList.item(temp);
	
		//System.out.println("\n Attr value is:" + nNode.getAtrribute("Name"));
 
		if(nList.item(temp).getAttributes().getNamedItem("Name").getNodeValue().equals(attr)) {
				//System.out.println("First name:" + nList.item(temp).getAttributes().getNamedItem("Name").getNodeValue());
				NodeList nl2 = nNode.getChildNodes();
				for(int i2=0; i2<nl2.getLength(); i2++) {
        			   Node an2 = nl2.item(i2);
				AuthorName = an2.getTextContent();
        			//System.out.println("Authors	:" + AuthorName);
				list.add(AuthorName);

			}}
		
		/*if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
 
			//System.out.println("Staff id : " + eElement.getNodeValue());
			System.out.println("First Name : " + eElement.getTextContent());
			
        NodeList nl2 = nNode.getChildNodes();

        for(int i2=0; i2<nl2.getLength(); i2++) {
           Node an2 = nl2.item(i2);
               // DEBUG PRINTS
		System.out.println("---------------o------------------\n");
               System.out.println(an2.getNodeName() + ": type (" + an2.getNodeType() + "):");
               if(an2.hasChildNodes()) System.out.println(an2.getFirstChild().getTextContent());
               if(an2.hasChildNodes()) System.out.println(an2.getFirstChild().getNodeValue());
               System.out.println(an2.getTextContent());
               System.out.println(an2.getNodeValue());
        }

    
}*/
}
Set<String> aSet = new HashSet<String>(list);
		list = new ArrayList<String>(new HashSet<String>(list));

		System.out.println(list);
 
/*
			System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
			System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
			System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
 
		}
*/

	
    } catch (Exception e) {
	e.printStackTrace();
    }
  

}
}