package is.apps;


import is.parsing.CameraListParser;
import is.objects.Camera;
import is.xml.XMLFileIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;


/**
 *
 * @author lopes and msimoes
 */
public class CameraSearchXML {

    public static final String LINK = "http://www.dpreview.com/reviews/specs/";
    public static final String XSD_FILE = "camera_list.xsd";
    public static final String XSL_FILE = "CameraListBeautifier.xsl";
    public static final String NO_ARGS_ERROR = "No arguments provided. Cameras' brand name must be passed to the application as argument.";
    public static final String XML_WRITING_ERROR = "Error writing XML to file: ";
    public static final String URL_ERROR = "Error getting url. Check your Internet connection and try again.";


    //adds the xsl ref to the xml doc
    public static void addXSLtoDoc(Document doc, String fileName) {
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("type", "text/xsl");
        params.put("href", fileName);
        ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", params);
        doc.getContent().add(0, pi);
    }

    //adds the xsl ref to the xml doc
    public static void addXSDtoRootElement(Element rootElement, String filename){
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(xsi);
        rootElement.setAttribute("noNamespaceSchemaLocation", filename, xsi);

    }

    public static void main (String args[]){

          if (args.length == 0){
              Logger.getLogger(CameraSearchXML.class.getName()).log(Level.SEVERE, NO_ARGS_ERROR);
              return;
          }

          String brand = args[0].toLowerCase();


          try{
              //Create instance of the parser
              CameraListParser clp = new CameraListParser(LINK + brand + "/");

              //Do the parsing
              clp.parse();

              //Get the result
              ArrayList <Camera> list = clp.getCameraList();

              Element  rootElement = new Element("cameras");
              addXSDtoRootElement(rootElement, XSD_FILE);

              //create root element of XML file
              rootElement.setAttribute("brand", brand);

              Document baseDoc = new Document(rootElement);
              addXSLtoDoc(baseDoc, XSL_FILE);



              for (Camera c: list){
                  rootElement.addContent(c.getXMLElement());
              }

              String path = "xml"+File.separator+brand+".xml";

              try{
                XMLFileIO.writeToFile(baseDoc, path);
              }catch(IOException ex){
                  Logger.getLogger(CameraSearchXML.class.getName()).log(Level.SEVERE, XML_WRITING_ERROR+path, ex);
              }

          }catch(IOException ex){
              Logger.getLogger(CameraSearchXML.class.getName()).log(Level.SEVERE, URL_ERROR, ex);
          }
    }

}
