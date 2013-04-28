
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


class GetCoAuthors2  {

	public static void main(String[] args) throws IOException {

		// variables to store user's input
		// authorName = users input
		// refinedAuthorName = author's name with spaces replaced with '+' 
		String authorName;
		String refinedAuthorName = "";
		
		int c;
		String query_key = "";

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

     	//Creating sting that will be used as query to query pubmed using esearch and obtain Query_Key and WebEnv values
		String esearchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi/?db=pubmed&term=" + refinedAuthorName + 					    "[Author]&usehistory=y";
		
		//Creating URL Object to be used for connecting to pubmed
    	URL esearchQuery = new URL(esearchURL); 
    	
    	//establishing connection with pubmed
    	URLConnection esearchQueryCon = esearchQuery.openConnection();
    	System.out.println("Connected to the database");

    	//Finding length of the content received from the pubmed
    	long len = esearchQueryCon.getContentLength();
    	

    	FileOutputStream fout = null;
		String esearchXML = "";

		System.out.println("Starting to extract information from input stream");
		

		//Extracting each character from the received stream and storing it in the file esearch.xml
    	if(len != 0) { 
      	
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

		System.out.println("Extracting information from input stream done");
		
		//System.out.println(esearchXML);

		System.out.println("Extracting WenEnv and QueryKey");

		//Parsing esearch.xml to extract values of QueryKey and WebEnv
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
 
			for (int temp = 0; temp < nList.getLength(); temp++) {
 				Node nNode = nList.item(temp);
 		
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
					Element eElement = (Element) nNode;
 					webenv = eElement.getTextContent();
				}
			}

			nList = doc.getElementsByTagName("QueryKey");
 			
			for (int temp = 0; temp < nList.getLength(); temp++) {
 
				Node nNode = nList.item(temp); 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
					Element eElement = (Element) nNode;
 			 		query_key = eElement.getTextContent();
					
				}
			}


			System.out.println("Extracting WenEnv and QueryKey done");

			//Creating URL to query docsum from the pubmed
			String esummaryURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&query_key="+query_key+"&WebEnv="+webenv+"&retmax=30";
			//System.out.println(esummaryURL);
			
			System.out.println("Connecting to download docsum");
    		URL esummaryQuery = new URL(esummaryURL); 
    		URLConnection esummaryQueryCon = esummaryQuery.openConnection();
    		



    		len = esummaryQueryCon.getContentLength();
    		fout = null;

    		String esummaryXML = "";
    		

    		
 			//Storing docsum in esummary.xml
    		if(len != 0) {
 				
      			InputStream input = esummaryQueryCon.getInputStream(); 
      			System.out.println("Docsums downloaded");
				fout = new FileOutputStream("esummary.xml");
				System.out.println("Starting to store input stream in XML file");
				while (((c = input.read()) != -1)) { 
        		
        			esummaryXML = esummaryXML + ((char) c);
        			fout.write(c); 
      			} 
      			input.close(); 
 			} 
 			else { 
      			System.out.println("No content available."); 
			}

		}	
		catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch(SAXException se) {
			se.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Storing input stream in XML file done");


		System.out.println("Starting to parase the summary.xml file");
		//Parsing esummary.xml to extract all the authors 
		try {
 
			File fXmlFile = new File("esummary.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
 
			doc.getDocumentElement().normalize();
  
			NodeList nList = doc.getElementsByTagName("Item");
			String attr = new String("AuthorList");
			String AuthorName = new String("");
			ArrayList<String> list = new ArrayList<String>();
 
			for (int temp = 0; temp < nList.getLength(); temp++) {
 
				Node nNode = nList.item(temp);
 
				if(nList.item(temp).getAttributes().getNamedItem("Name").getNodeValue().equals(attr)) {

					NodeList nl2 = nNode.getChildNodes();

					for(int i2=0; i2<nl2.getLength(); i2++) {
        			   
        				   Node an2 = nl2.item(i2);
					   	   AuthorName = an2.getTextContent();
					   	   if(!(AuthorName.equals("	")))
        			   	   list.add(AuthorName);

					}
					System.out.println("Extracting Author list" + temp + "done");
				}
			}
			
			//Converting list of authors into set to remove duplicate values
			Set<String> aSet = new HashSet<String>(list);
			list = new ArrayList<String>(new HashSet<String>(list));
			System.out.println(list);

			fout = new FileOutputStream("authors.sif");
			PrintStream out = new PrintStream(fout);
    


			for(int temp=0;temp<list.size();temp++){
				
					System.out.println(list.get(temp));
					out.print(list.get(temp));
					//fout.write(list.get(temp));

					
			}		
			out.close();
	
    	} 
    	catch (Exception e) {
			e.printStackTrace();
    	}
  

	}
}
