import javax.xml.parsers.*;import org.xml.sax.*;import org.xml.sax.helpers.*;import org.w3c.dom.*;import java.io.*;import java.net.*;import java.awt.*;import java.awt.image.*;public class XMLParser{	public static void main(String []args) throws Exception {		for(int i = 0; i < args.length; i++){			try{				File file = new File(args[i]);				if(file.exists()){        			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();			        // Optional: set various configuration options			        dbf.setValidating(true);			        dbf.setIgnoringComments(true);			        dbf.setIgnoringElementContentWhitespace(true);//???			        dbf.setCoalescing(true);			        DocumentBuilder db = null;			        try {			            db = dbf.newDocumentBuilder();			        } catch (ParserConfigurationException pce) {			            System.err.println(pce);						db = null;			        }					if(db != null){	        			// Set an ErrorHandler before parsing	        			OutputStreamWriter errorWriter =	            		new OutputStreamWriter(System.err, "UTF-8");	       				db.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));				        Document doc = null;				        try {				            db.parse(file);				            System.err.println("Document is valid");				        } catch (SAXException se) {				            System.err.println(se.getMessage());				        } catch (IOException ioe) {				            System.err.println(ioe);				        } 					}				}							}catch(Exception e){			}		}	}    private static class MyErrorHandler implements ErrorHandler {        /** Error handler output goes here */        private PrintWriter out;        MyErrorHandler(PrintWriter out) {            this.out = out;        }        /**         * Returns a string describing parse exception details         */        private String getParseExceptionInfo(SAXParseException spe) {            String systemId = spe.getSystemId();            if (systemId == null) {                systemId = "null";            }            String info = "Line=" + spe.getLineNumber() +                ": " + spe.getMessage();            return info;        }        // The following methods are standard SAX ErrorHandler methods.        // See SAX documentation for more info.        public void warning(SAXParseException spe) throws SAXException {            out.println("Warning: " + getParseExceptionInfo(spe));        }                public void error(SAXParseException spe) throws SAXException {            String message = "Error: " + getParseExceptionInfo(spe);            throw new SAXException(message);        }        public void fatalError(SAXParseException spe) throws SAXException {            String message = "Fatal Error: " + getParseExceptionInfo(spe);            throw new SAXException(message);        }    }}