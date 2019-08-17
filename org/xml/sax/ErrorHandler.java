package org.xml.sax;

public interface ErrorHandler {
  void warning(SAXParseException paramSAXParseException) throws SAXException;
  
  void error(SAXParseException paramSAXParseException) throws SAXException;
  
  void fatalError(SAXParseException paramSAXParseException) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\ErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */