package com.sun.istack.internal;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class FragmentContentHandler extends XMLFilterImpl {
  public FragmentContentHandler() {}
  
  public FragmentContentHandler(XMLReader paramXMLReader) { super(paramXMLReader); }
  
  public FragmentContentHandler(ContentHandler paramContentHandler) { setContentHandler(paramContentHandler); }
  
  public void startDocument() {}
  
  public void endDocument() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\FragmentContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */