

package is.apps;

import is.objects.Summary;
import is.xml.CameraListReader;
import is.xml.XMLFileIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;

/**
 *
 * @author msimoes
 */
public class CameraSummaryXML {

    public static final String XSD_FILE = "summary.xsd";
    public static final String NO_ARGS_ERROR = "No arguments provided. XML file paths, created by CameraSearchXML, must be passed to the application as argument.";
    public static final String XML_WRITING_ERROR = "Error writing XML to file: ";
    public static final String XML_READING_ERROR = "Error reading XML to file: ";
    public static final String XML_FORMAT_ERROR = "Corrupt XML file: ";

    //adds the xsd ref to the xml
    public static void addXSDtoRootElement(Element rootElement, String filename){
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(xsi);
        rootElement.setAttribute("noNamespaceSchemaLocation", filename, xsi);

    }

    public static void main(String []args){
        if (args.length < 1){
            Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, NO_ARGS_ERROR);
            return;
        }

        CameraListReader clr = null;
        
        
        
        Element  rootElement = new Element("summary");

        addXSDtoRootElement(rootElement, XSD_FILE);

        Document baseDoc = new Document(rootElement);


        Document d = null;
        Element e = null;
        for (String path : args){
            try {
                d = XMLFileIO.readFromFile(path);
                clr = new CameraListReader(d);
                Summary sm = clr.parse();
                sm.createXMLElement();
                e = sm.getXMLElement();
                rootElement.addContent(e);
            } catch (JDOMException ex) {
                Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, XML_FORMAT_ERROR+path, ex);
            } catch (IOException ex) {
                Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, XML_READING_ERROR+path, ex);
            }
        }

        String path = "xml" + File.separator + "summary.xml";
        try {            
            XMLFileIO.writeToFile(baseDoc, path);
        } catch (IOException ex) {
            Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, XML_WRITING_ERROR+path, ex);
        }

    }

}
