package com.sun.xml.internal.ws.runtime.config;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;
import com.sun.xml.internal.ws.config.metro.util.ParserUtil;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class TubelineFeatureReader implements FeatureReader {
  private static final Logger LOGGER = Logger.getLogger(TubelineFeatureReader.class);
  
  private static final QName NAME_ATTRIBUTE_NAME = new QName("name");
  
  public TubelineFeature parse(XMLEventReader paramXMLEventReader) throws WebServiceException {
    try {
      StartElement startElement = paramXMLEventReader.nextEvent().asStartElement();
      boolean bool = true;
      Iterator iterator = startElement.getAttributes();
      while (iterator.hasNext()) {
        Attribute attribute = (Attribute)iterator.next();
        QName qName = attribute.getName();
        if (ENABLED_ATTRIBUTE_NAME.equals(qName)) {
          bool = ParserUtil.parseBooleanValue(attribute.getValue());
          continue;
        } 
        if (NAME_ATTRIBUTE_NAME.equals(qName))
          continue; 
        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected attribute"));
      } 
      return parseFactories(bool, startElement, paramXMLEventReader);
    } catch (XMLStreamException xMLStreamException) {
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", xMLStreamException));
    } 
  }
  
  private TubelineFeature parseFactories(boolean paramBoolean, StartElement paramStartElement, XMLEventReader paramXMLEventReader) throws WebServiceException {
    byte b = 0;
    while (paramXMLEventReader.hasNext()) {
      try {
        XMLEvent xMLEvent = paramXMLEventReader.nextEvent();
        switch (xMLEvent.getEventType()) {
          case 5:
            continue;
          case 4:
            if (xMLEvent.asCharacters().isWhiteSpace())
              continue; 
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + xMLEvent.asCharacters()));
          case 1:
            b++;
            continue;
          case 2:
            if (--b < 0) {
              EndElement endElement = xMLEvent.asEndElement();
              if (!paramStartElement.getName().equals(endElement.getName()))
                throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("End element does not match " + endElement)); 
              break;
            } 
            continue;
        } 
        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + xMLEvent));
      } catch (XMLStreamException xMLStreamException) {
        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", xMLStreamException));
      } 
    } 
    return new TubelineFeature(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineFeatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */