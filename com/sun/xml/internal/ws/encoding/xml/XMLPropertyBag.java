package com.sun.xml.internal.ws.encoding.xml;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;

public class XMLPropertyBag extends BasePropertySet {
  private String contentType;
  
  private static final BasePropertySet.PropertyMap model = parse(XMLPropertyBag.class);
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
  
  @Property({"com.sun.jaxws.rest.contenttype"})
  public String getXMLContentType() { return this.contentType; }
  
  public void setXMLContentType(String paramString) { this.contentType = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\xml\XMLPropertyBag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */