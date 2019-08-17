package com.sun.xml.internal.fastinfoset.stax.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXFilteredParser extends StAXParserWrapper {
  private StreamFilter _filter;
  
  public StAXFilteredParser() {}
  
  public StAXFilteredParser(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter) {
    super(paramXMLStreamReader);
    this._filter = paramStreamFilter;
  }
  
  public void setFilter(StreamFilter paramStreamFilter) { this._filter = paramStreamFilter; }
  
  public int next() throws XMLStreamException {
    if (hasNext())
      return super.next(); 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
  }
  
  public boolean hasNext() throws XMLStreamException {
    while (super.hasNext()) {
      if (this._filter.accept(getReader()))
        return true; 
      super.next();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sta\\util\StAXFilteredParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */