package com.sun.xml.internal.ws.config.metro.dev;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public interface FeatureReader<T extends WebServiceFeature> {
  public static final QName ENABLED_ATTRIBUTE_NAME = new QName("enabled");
  
  T parse(XMLEventReader paramXMLEventReader) throws WebServiceException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\config\metro\dev\FeatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */