import org.concord.LabBook.*;import org.concord.CCProbe.*;import javax.xml.parsers.*;import org.xml.sax.*;import org.xml.sax.helpers.*;import org.w3c.dom.*;import java.io.*;public class XML2LabBook{public static int newIndex = 0;private  static int indent = 0;public final static int FOLDER_TAG 				= 0;public final static int NOTES_TAG 				= 1;public final static int DATACOLLECTOR_TAG 		= 2;public final static int DRAWING_TAG 			= 3;public final static int UNITCONV_TAG 			= 4;public final static int IMAGE_TAG 				= 5;public final static int EXPOBJECT_TAG 			= 6;public final static int SUPERNOTES_TAG 			= 7;public static String []labBookObjectTAGs = {"FOLDER","NOTES","DATACOLLECTOR","DRAWING","UNITCONV","IMAGE","EXPOBJECT","SUPERNOTES"};public static String []labBookObjectNames = {"Folder","Notes","Data Collector","Drawing","UnitConvertor","Image",null,"SuperNotes"};	public static void main(String []args) throws Exception {        // Step 1: create a DocumentBuilderFactory and configure it        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();        // Optional: set various configuration options        dbf.setValidating(true);        dbf.setIgnoringComments(true);        dbf.setIgnoringElementContentWhitespace(true);//???        dbf.setCoalescing(true);        // The opposite of creating entity ref nodes is expanding them inline//        dbf.setExpandEntityReferences(!createEntityRefs);        DocumentBuilder db = null;        try {            db = dbf.newDocumentBuilder();        } catch (ParserConfigurationException pce) {            System.err.println(pce);            System.exit(1);        }        // Set an ErrorHandler before parsing        OutputStreamWriter errorWriter =            new OutputStreamWriter(System.err, "UTF-8");        db.setErrorHandler(            new MyErrorHandler(new PrintWriter(errorWriter, true)));        // Step 3: parse the input file        Document doc = null;        try {            doc = db.parse(new File("labbook.xml"));        } catch (SAXException se) {            System.err.println(se.getMessage());            System.exit(1);        } catch (IOException ioe) {            System.err.println(ioe);            System.exit(1);        }                String rootName = doc.getDocumentElement().getTagName();       if(rootName == null || !rootName.equals("LABBOOK")){            System.err.println("LabBook description wasn't found");            System.exit(1);        }                LabBook labBook = createLabBook(doc.getDocumentElement());   		if(labBook == null){   			System.out.println("Error creating LabBook");            System.exit(1);   		} 		LObjDictionary loDict = initRootDictionary(labBook);       		NodeList nodeList = doc.getDocumentElement().getChildNodes();		for(int i = 0; i < nodeList.getLength(); i++){			Node node = nodeList.item(i);			int type = node.getNodeType();			if(type == Node.ELEMENT_NODE) addObjectToLabBook(labBook,loDict,loDict,(Element)node);   		}   		   				labBook.commit();		labBook.close();   		        /*        Node node = doc.getElementById("image1");        				if(node != null){			System.out.println("----------------------");			printNode(node);			System.out.println("----------------------");		}*/		System.out.println("END");		System.exit(0);	}	    private static void outputIndentation() {        for (int i = 0; i < indent; i++) {            System.out.print(" ");        }    }        public static void printAttributes(Node nd){    	if(nd == null) return;		NamedNodeMap atts = nd.getAttributes();		if(atts == null) return;		indent += 2;        for (int atr = 0; atr < atts.getLength(); atr++) {            Node att = atts.item(atr);			if(att instanceof Attr){				outputIndentation();				Attr a = (Attr)att;				System.out.println("Attribute "+a.getName()+"="+a.getValue());			}        }		indent -= 2;    }    	public static void printNode(Node node){        outputIndentation();        printAttributes(node);		NodeList nl = node.getChildNodes();        indent += 2;		for(int i = 0; i < nl.getLength(); i++){			Node nd = nl.item(i);        	outputIndentation();       		int type = nd.getNodeType();			System.out.println("NODE "+nd.getNodeName()+" TYPE "+type);				       	switch (type) {		        case Node.ELEMENT_NODE:		            // Print attributes if any.  Note: element attributes are not		            // children of ELEMENT_NODEs but are properties of their		            // associated ELEMENT_NODE.  For this reason, they are printed		            // with 2x the indent level to indicate this.//					System.out.println("ELEMENT "+nd.getNodeName());//					printAttributes(nd);					printNode(nd);		            break;		        case Node.TEXT_NODE:		            if(nd instanceof CharacterData){		            	Node parent = nd.getParentNode().getParentNode();		            	String tData = ((CharacterData)nd).getData().trim();		            	int indentValue = 0;		            	boolean isSNText = "SNTEXT".equals(parent.getNodeName());		            	if(isSNText){		            		if(parent.getAttributes() != null){		            			Attr indentAttr = (Attr)(parent.getAttributes().getNamedItem("indent"));		            			if(indentAttr != null){		            				try{		            					indentValue = Integer.parseInt(indentAttr.getValue());		            				}catch(Exception te){		            					indentValue = 0;		            				}		            				if(indentValue < 0) indentValue = 0;		            			}		            		}		            	}		            	if(tData.length() > 0){        					outputIndentation();        					if(indentValue > 0){        						for(int p=0; p < indentValue; p++) tData = "\t"+tData;        					}        					if(isSNText){        						tData = tData.replace('\n',' ');        						tData = tData.replace('\r',' ');        					}		            		System.out.println("<\n"+tData+"\n>");		            	}		            }		            break;		        case Node.CDATA_SECTION_NODE:		            System.out.println("CDATA:");		            break;		        default://		            System.out.println("UNSUPPORTED NODE: " + type);		            break;	        }		}        indent -= 2;	}		public static LabBook createLabBook(Node labBookNode){		LabBook 	retBook = null;		if(labBookNode == null) return retBook;		LabBookDB 	lbDB = null;		labBookInit();		try{			File labBookFile = new File("LabBook.PDB");			if(labBookFile.exists()){				labBookFile.delete();			}			lbDB = new LabBookCatalog("LabBook");			if(lbDB.getError()){				retBook = null;			}else{				retBook = new LabBook();			}		}catch(Exception e){			retBook = null;		}		if(retBook != null){			LabObject.lBook = retBook;			retBook.open(lbDB);		}		return retBook;	}	public static void labBookInit(){		LabBook.init();		LabBook.registerFactory(new DataObjFactory());	}		public static void addObjectToLabBook(LabBook labBook,LObjDictionary mainDict,LObjDictionary dict,Element element){		if(labBook == null || dict == null) return;		if(!isElementLabBookObject(element)) return;		System.out.println("addObjectToLabBook "+element.getTagName());				if(element.getTagName().equals(labBookObjectTAGs[FOLDER_TAG])){			addFolderToDictionary(labBook,mainDict,dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[NOTES_TAG])){			addNotesToDictionary(dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[DATACOLLECTOR_TAG])){			addDataCollectorToDictionary(dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[DRAWING_TAG])){			addDrawingToDictionary(dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[UNITCONV_TAG])){			addUnitConvertorToDictionary(dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[IMAGE_TAG])){			addImageToDictionary(dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[EXPOBJECT_TAG])){			exportObjectToDictionary(labBook,dict,element);		}else if(element.getTagName().equals(labBookObjectTAGs[SUPERNOTES_TAG])){		}		if(mainDict != null) labBook.store(mainDict);	}		public static boolean isElementLabBookObject(Element element){		if(element == null) return false;		String elementName = element.getTagName();		if(elementName == null) return false;		for(int i = 0; i < labBookObjectTAGs.length; i++){			if(elementName.equals(labBookObjectTAGs[i])) return true;		}		return true;	}	public static LObjDictionary initRootDictionary(LabBook labBook){		if(labBook == null) return null; 		LObjDictionary dict = DefaultFactory.createDictionary();		dict.name = "Root";		labBook.store(dict);		return dict;	}		public static LabObject createRegularObject(Element element,String str,String objName){		if(element == null) return null;		if(!element.getTagName().equals(str)) return null;		LabObject labObject = createObj(objName);		if(labObject == null) return null;		String nameObject = element.getAttribute("name");		if(nameObject != null) labObject.name = nameObject;		return labObject;	}		public static void exportObjectToDictionary(LabBook labBook,LObjDictionary dict,Element element){		if(labBook == null || dict == null || element == null) return;		if(!element.getTagName().equals(labBookObjectTAGs[EXPOBJECT_TAG])) return;		String url = element.getAttribute("url");		if(url == null) return;		LabBookFile imFile = new LabBookFile(url);		LabObject labObject = labBook.importDB(imFile);		imFile.close();		if(labObject != null){			dict.add(labObject);		}	}	public static void addFolderToDictionary(LabBook labBook,LObjDictionary mainDict,LObjDictionary dict,Element element){		if(labBook == null || dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[FOLDER_TAG],labBookObjectNames[FOLDER_TAG]);		if(labObject == null) return;		LObjDictionary folder = (LObjDictionary)labObject;		dict.add(folder);		if(mainDict != null) labBook.store(mainDict);		NodeList nodeList = element.getChildNodes();		for(int i = 0; i < nodeList.getLength(); i++){			Node node = nodeList.item(i);			int type = node.getNodeType();			if(type == Node.ELEMENT_NODE) addObjectToLabBook(labBook,mainDict,folder,(Element)node);   		}	}		public static void addDataCollectorToDictionary(LObjDictionary dict,Element element){		if(dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[DATACOLLECTOR_TAG],labBookObjectNames[DATACOLLECTOR_TAG]);		if(labObject == null) return;		dict.add(labObject);	}	public static void addDrawingToDictionary(LObjDictionary dict,Element element){		if(dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[DRAWING_TAG],labBookObjectNames[DRAWING_TAG]);		if(labObject == null) return;		dict.add(labObject);	}	public static void addUnitConvertorToDictionary(LObjDictionary dict,Element element){		if(dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[UNITCONV_TAG],labBookObjectNames[UNITCONV_TAG]);		if(labObject == null) return;		dict.add(labObject);	}		public static void addNotesToDictionary(LObjDictionary dict,Element element){		if(dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[NOTES_TAG],labBookObjectNames[NOTES_TAG]);		if(labObject == null) return;		int nSpaces = 0;		try{			nSpaces = Integer.parseInt(element.getAttribute("indent"));		}catch(Exception e){		}		Node textNode = element.getFirstChild();		String tData = "";		if(textNode instanceof CharacterData){			tData = ((CharacterData)textNode).getData().trim();        	if(tData.length() > 0){				if(nSpaces > 0){					for(int p=0; p < nSpaces; p++) tData = "\t"+tData;				}        	}		}		((LObjDocument)labObject).setText(tData);		dict.add(labObject);	}		public static void addImageToDictionary(LObjDictionary dict,Element element){		if(dict == null) return;		LabObject labObject = createRegularObject(element,labBookObjectTAGs[IMAGE_TAG],labBookObjectNames[IMAGE_TAG]);		if(labObject == null) return;		String imageURL = element.getAttribute("src");		if(imageURL != null){			LObjImageView view = (LObjImageView)labObject.getView(null,false,null);			if(view != null){				view.loadImage(imageURL);			}		}						dict.add(labObject);	}	/*	public static void main(String []args){		LabBook.init();		LabBook.registerFactory(new DataObjFactory());		LabBookDB lbDB = null;		try{			lbDB = new LabBookCatalog("LabBook");			if(lbDB.getError()){				System.out.println("Error creating LabBook");				System.exit(0);			}		}catch(Exception e){			System.out.println("Exception "+e);			e.printStackTrace();		}		if(lbDB == null){		}		LabBook labBook = new LabBook();		LabObject.lBook = labBook;		labBook.open(lbDB);		LObjDictionary loDict = DefaultFactory.createDictionary();		loDict.name = "Root";		labBook.store(loDict);		LabObject lobj = createObj("Folder");		lobj.name = "DimaFolder";		if(lobj != null){			loDict.add(lobj);		}				if(lobj instanceof LObjDictionary){			LObjDictionary folder = (LObjDictionary)lobj;			folder.add(createObj("Data Collector"));			labBook.store(loDict);			folder.add(createObj("UnitConvertor"));			labBook.store(loDict);			folder.add(createObj("Drawing"));			labBook.store(loDict);		}		lobj = createObj("Image");		loDict.add(lobj);				labBook.store(loDict);		labBook.commit();		labBook.close();						System.exit(0);	}*/	public static LabObject createObj(String objType)	{		LabObject newObj = null;		for(int f = 0; f < LabBook.objFactories.length; f++){			if(LabBook.objFactories[f] == null) continue;			LabObjDescriptor []desc = LabBook.objFactories[f].getLabBookObjDesc();			if(desc == null) continue;			boolean doExit = false;			for(int d = 0; d < desc.length; d++){				if(desc[d] == null) continue;				if(objType.equals(desc[d].name)){					newObj = LabBook.objFactories[f].makeNewObj(desc[d].objType);					doExit = true;					break;				}			}			if(doExit) break;		}		if(newObj != null){			if(newIndex == 0){				newObj.name = objType;		    			} else {				newObj.name = objType + " " + newIndex;		    			}			newIndex++;		}		return newObj;	}    // Error handler to report errors and warnings    private static class MyErrorHandler implements ErrorHandler {        /** Error handler output goes here */        private PrintWriter out;        MyErrorHandler(PrintWriter out) {            this.out = out;        }        /**         * Returns a string describing parse exception details         */        private String getParseExceptionInfo(SAXParseException spe) {            String systemId = spe.getSystemId();            if (systemId == null) {                systemId = "null";            }            String info = "URI=" + systemId +                " Line=" + spe.getLineNumber() +                ": " + spe.getMessage();            return info;        }        // The following methods are standard SAX ErrorHandler methods.        // See SAX documentation for more info.        public void warning(SAXParseException spe) throws SAXException {            out.println("Warning: " + getParseExceptionInfo(spe));        }                public void error(SAXParseException spe) throws SAXException {            String message = "Error: " + getParseExceptionInfo(spe);            throw new SAXException(message);        }        public void fatalError(SAXParseException spe) throws SAXException {            String message = "Fatal Error: " + getParseExceptionInfo(spe);            throw new SAXException(message);        }    }}