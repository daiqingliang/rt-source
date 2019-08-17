package com.sun.xml.internal.ws.policy.sourcemodel.attach;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ExternalAttachmentsUnmarshaller {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ExternalAttachmentsUnmarshaller.class);
  
  public static final URI BINDING_ID;
  
  public static final URI BINDING_OPERATION_ID;
  
  public static final URI BINDING_OPERATION_INPUT_ID;
  
  public static final URI BINDING_OPERATION_OUTPUT_ID;
  
  public static final URI BINDING_OPERATION_FAULT_ID;
  
  private static final QName POLICY_ATTACHMENT;
  
  private static final QName APPLIES_TO;
  
  private static final QName POLICY;
  
  private static final QName URI;
  
  private static final QName POLICIES;
  
  private static final ContextClassloaderLocal<XMLInputFactory> XML_INPUT_FACTORY;
  
  private static final PolicyModelUnmarshaller POLICY_UNMARSHALLER;
  
  private final Map<URI, Policy> map = new HashMap();
  
  private URI currentUri = null;
  
  private Policy currentPolicy = null;
  
  public static Map<URI, Policy> unmarshal(Reader paramReader) throws PolicyException {
    LOGGER.entering(new Object[] { paramReader });
    try {
      XMLEventReader xMLEventReader = ((XMLInputFactory)XML_INPUT_FACTORY.get()).createXMLEventReader(paramReader);
      ExternalAttachmentsUnmarshaller externalAttachmentsUnmarshaller = new ExternalAttachmentsUnmarshaller();
      Map map1 = externalAttachmentsUnmarshaller.unmarshal(xMLEventReader, null);
      LOGGER.exiting(map1);
      return Collections.unmodifiableMap(map1);
    } catch (XMLStreamException xMLStreamException) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0086_FAILED_CREATE_READER(paramReader)), xMLStreamException);
    } 
  }
  
  private Map<URI, Policy> unmarshal(XMLEventReader paramXMLEventReader, StartElement paramStartElement) throws PolicyException {
    XMLEvent xMLEvent = null;
    while (paramXMLEventReader.hasNext()) {
      try {
        StartElement startElement;
        xMLEvent = paramXMLEventReader.peek();
        switch (xMLEvent.getEventType()) {
          case 5:
          case 7:
            paramXMLEventReader.nextEvent();
            continue;
          case 4:
            processCharacters(xMLEvent.asCharacters(), paramStartElement, this.map);
            paramXMLEventReader.nextEvent();
            continue;
          case 2:
            processEndTag(xMLEvent.asEndElement(), paramStartElement);
            paramXMLEventReader.nextEvent();
            return this.map;
          case 1:
            startElement = xMLEvent.asStartElement();
            processStartTag(startElement, paramStartElement, paramXMLEventReader, this.map);
            continue;
          case 8:
            return this.map;
        } 
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0087_UNKNOWN_EVENT(xMLEvent)));
      } catch (XMLStreamException xMLStreamException) {
        Location location = (xMLEvent == null) ? null : xMLEvent.getLocation();
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(location)), xMLStreamException);
      } 
    } 
    return this.map;
  }
  
  private void processStartTag(StartElement paramStartElement1, StartElement paramStartElement2, XMLEventReader paramXMLEventReader, Map<URI, Policy> paramMap) throws PolicyException {
    try {
      QName qName = paramStartElement1.getName();
      if (paramStartElement2 == null) {
        if (!qName.equals(POLICIES))
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<Policies>", qName, paramStartElement1.getLocation()))); 
      } else {
        QName qName1 = paramStartElement2.getName();
        if (qName1.equals(POLICIES)) {
          if (!qName.equals(POLICY_ATTACHMENT))
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<PolicyAttachment>", qName, paramStartElement1.getLocation()))); 
        } else if (qName1.equals(POLICY_ATTACHMENT)) {
          if (qName.equals(POLICY)) {
            readPolicy(paramXMLEventReader);
            return;
          } 
          if (!qName.equals(APPLIES_TO))
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<AppliesTo> or <Policy>", qName, paramStartElement1.getLocation()))); 
        } else if (qName1.equals(APPLIES_TO)) {
          if (!qName.equals(URI))
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<URI>", qName, paramStartElement1.getLocation()))); 
        } else {
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0090_UNEXPECTED_ELEMENT(qName, paramStartElement1.getLocation())));
        } 
      } 
      paramXMLEventReader.nextEvent();
      unmarshal(paramXMLEventReader, paramStartElement1);
    } catch (XMLStreamException xMLStreamException) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(paramStartElement1.getLocation()), xMLStreamException));
    } 
  }
  
  private void readPolicy(XMLEventReader paramXMLEventReader) throws PolicyException {
    PolicySourceModel policySourceModel = POLICY_UNMARSHALLER.unmarshalModel(paramXMLEventReader);
    PolicyModelTranslator policyModelTranslator = PolicyModelTranslator.getTranslator();
    Policy policy = policyModelTranslator.translate(policySourceModel);
    if (this.currentUri != null) {
      this.map.put(this.currentUri, policy);
      this.currentUri = null;
      this.currentPolicy = null;
    } else {
      this.currentPolicy = policy;
    } 
  }
  
  private void processEndTag(EndElement paramEndElement, StartElement paramStartElement) throws PolicyException { checkEndTagName(paramStartElement.getName(), paramEndElement); }
  
  private void checkEndTagName(QName paramQName, EndElement paramEndElement) throws PolicyException {
    QName qName = paramEndElement.getName();
    if (!paramQName.equals(qName))
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0091_END_ELEMENT_NO_MATCH(paramQName, paramEndElement, paramEndElement.getLocation()))); 
  }
  
  private void processCharacters(Characters paramCharacters, StartElement paramStartElement, Map<URI, Policy> paramMap) throws PolicyException {
    if (paramCharacters.isWhiteSpace())
      return; 
    String str = paramCharacters.getData();
    if (paramStartElement != null && URI.equals(paramStartElement.getName())) {
      processUri(paramCharacters, paramMap);
      return;
    } 
    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0092_CHARACTER_DATA_UNEXPECTED(paramStartElement, str, paramCharacters.getLocation())));
  }
  
  private void processUri(Characters paramCharacters, Map<URI, Policy> paramMap) throws PolicyException {
    String str = paramCharacters.getData().trim();
    try {
      URI uRI = new URI(str);
      if (this.currentPolicy != null) {
        paramMap.put(uRI, this.currentPolicy);
        this.currentUri = null;
        this.currentPolicy = null;
      } else {
        this.currentUri = uRI;
      } 
    } catch (URISyntaxException uRISyntaxException) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0093_INVALID_URI(str, paramCharacters.getLocation())), uRISyntaxException);
    } 
  }
  
  static  {
    try {
      BINDING_ID = new URI("urn:uuid:c9bef600-0d7a-11de-abc1-0002a5d5c51b");
      BINDING_OPERATION_ID = new URI("urn:uuid:62e66b60-0d7b-11de-a1a2-0002a5d5c51b");
      BINDING_OPERATION_INPUT_ID = new URI("urn:uuid:730d8d20-0d7b-11de-84e9-0002a5d5c51b");
      BINDING_OPERATION_OUTPUT_ID = new URI("urn:uuid:85b0f980-0d7b-11de-8e9d-0002a5d5c51b");
      BINDING_OPERATION_FAULT_ID = new URI("urn:uuid:917cb060-0d7b-11de-9e80-0002a5d5c51b");
    } catch (URISyntaxException uRISyntaxException) {
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0094_INVALID_URN()), uRISyntaxException);
    } 
    POLICY_ATTACHMENT = new QName("http://www.w3.org/ns/ws-policy", "PolicyAttachment");
    APPLIES_TO = new QName("http://www.w3.org/ns/ws-policy", "AppliesTo");
    POLICY = new QName("http://www.w3.org/ns/ws-policy", "Policy");
    URI = new QName("http://www.w3.org/ns/ws-policy", "URI");
    POLICIES = new QName("http://java.sun.com/xml/ns/metro/management", "Policies");
    XML_INPUT_FACTORY = new ContextClassloaderLocal<XMLInputFactory>() {
        protected XMLInputFactory initialValue() throws Exception { return XMLInputFactory.newInstance(); }
      };
    POLICY_UNMARSHALLER = PolicyModelUnmarshaller.getXmlUnmarshaller();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\attach\ExternalAttachmentsUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */