package org.xml.sax;

public interface XMLFilter extends XMLReader {
  void setParent(XMLReader paramXMLReader);
  
  XMLReader getParent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\XMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */