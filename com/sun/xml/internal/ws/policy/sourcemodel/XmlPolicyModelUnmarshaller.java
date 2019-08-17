package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlPolicyModelUnmarshaller extends PolicyModelUnmarshaller {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelUnmarshaller.class);
  
  public PolicySourceModel unmarshalModel(Object paramObject) throws PolicyException {
    XMLEventReader xMLEventReader = createXMLEventReader(paramObject);
    PolicySourceModel policySourceModel = null;
    while (xMLEventReader.hasNext()) {
      try {
        XMLEvent xMLEvent = xMLEventReader.peek();
        switch (xMLEvent.getEventType()) {
          case 5:
          case 7:
            xMLEventReader.nextEvent();
            continue;
          case 4:
            processCharacters(ModelNode.Type.POLICY, xMLEvent.asCharacters(), null);
            xMLEventReader.nextEvent();
            continue;
          case 1:
            if (NamespaceVersion.resolveAsToken(xMLEvent.asStartElement().getName()) == XmlToken.Policy) {
              StartElement startElement = xMLEventReader.nextEvent().asStartElement();
              policySourceModel = initializeNewModel(startElement);
              unmarshalNodeContent(policySourceModel.getNamespaceVersion(), policySourceModel.getRootNode(), startElement.getName(), xMLEventReader);
              break;
            } 
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
        } 
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
      } catch (XMLStreamException xMLStreamException) {
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), xMLStreamException));
      } 
    } 
    return policySourceModel;
  }
  
  protected PolicySourceModel createSourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2) { return PolicySourceModel.createPolicySourceModel(paramNamespaceVersion, paramString1, paramString2); }
  
  private PolicySourceModel initializeNewModel(StartElement paramStartElement) throws PolicyException, XMLStreamException {
    NamespaceVersion namespaceVersion = NamespaceVersion.resolveVersion(paramStartElement.getName().getNamespaceURI());
    Attribute attribute1 = getAttributeByName(paramStartElement, namespaceVersion.asQName(XmlToken.Name));
    Attribute attribute2 = getAttributeByName(paramStartElement, PolicyConstants.XML_ID);
    Attribute attribute3 = getAttributeByName(paramStartElement, PolicyConstants.WSU_ID);
    if (attribute3 == null) {
      attribute3 = attribute2;
    } else if (attribute2 != null) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED()));
    } 
    return createSourceModel(namespaceVersion, (attribute3 == null) ? null : attribute3.getValue(), (attribute1 == null) ? null : attribute1.getValue());
  }
  
  private ModelNode addNewChildNode(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, StartElement paramStartElement) throws PolicyException {
    ModelNode modelNode;
    QName qName = paramStartElement.getName();
    if (paramModelNode.getType() == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
      modelNode = paramModelNode.createChildAssertionParameterNode();
    } else {
      Attribute attribute;
      XmlToken xmlToken = NamespaceVersion.resolveAsToken(qName);
      switch (xmlToken) {
        case Policy:
          return paramModelNode.createChildPolicyNode();
        case All:
          return paramModelNode.createChildAllNode();
        case ExactlyOne:
          return paramModelNode.createChildExactlyOneNode();
        case PolicyReference:
          attribute = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.Uri));
          if (attribute == null)
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND())); 
          try {
            PolicyReferenceData policyReferenceData;
            URI uRI = new URI(attribute.getValue());
            Attribute attribute1 = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.Digest));
            if (attribute1 == null) {
              policyReferenceData = new PolicyReferenceData(uRI);
            } else {
              Attribute attribute2 = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.DigestAlgorithm));
              URI uRI1 = null;
              if (attribute2 != null)
                uRI1 = new URI(attribute2.getValue()); 
              policyReferenceData = new PolicyReferenceData(uRI, attribute1.getValue(), uRI1);
            } 
            modelNode = paramModelNode.createChildPolicyReferenceNode(policyReferenceData);
          } catch (URISyntaxException uRISyntaxException) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI(), uRISyntaxException));
          } 
          return modelNode;
      } 
      if (paramModelNode.isDomainSpecific()) {
        modelNode = paramModelNode.createChildAssertionParameterNode();
      } else {
        modelNode = paramModelNode.createChildAssertionNode();
      } 
    } 
    return modelNode;
  }
  
  private void parseAssertionData(NamespaceVersion paramNamespaceVersion, String paramString, ModelNode paramModelNode, StartElement paramStartElement) throws IllegalArgumentException, PolicyException {
    HashMap hashMap = new HashMap();
    boolean bool1 = false;
    boolean bool2 = false;
    Iterator iterator = paramStartElement.getAttributes();
    while (iterator.hasNext()) {
      Attribute attribute = (Attribute)iterator.next();
      QName qName = attribute.getName();
      if (hashMap.containsKey(qName))
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(attribute.getName(), paramStartElement.getName()))); 
      if (paramNamespaceVersion.asQName(XmlToken.Optional).equals(qName)) {
        bool1 = parseBooleanValue(attribute.getValue());
        continue;
      } 
      if (paramNamespaceVersion.asQName(XmlToken.Ignorable).equals(qName)) {
        bool2 = parseBooleanValue(attribute.getValue());
        continue;
      } 
      hashMap.put(qName, attribute.getValue());
    } 
    AssertionData assertionData = new AssertionData(paramStartElement.getName(), paramString, hashMap, paramModelNode.getType(), bool1, bool2);
    if (assertionData.containsAttribute(PolicyConstants.VISIBILITY_ATTRIBUTE)) {
      String str = assertionData.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE);
      if (!"private".equals(str))
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(str))); 
    } 
    paramModelNode.setOrReplaceNodeData(assertionData);
  }
  
  private Attribute getAttributeByName(StartElement paramStartElement, QName paramQName) {
    Attribute attribute = paramStartElement.getAttributeByName(paramQName);
    if (attribute == null) {
      String str = paramQName.getLocalPart();
      Iterator iterator = paramStartElement.getAttributes();
      while (iterator.hasNext()) {
        Attribute attribute1 = (Attribute)iterator.next();
        QName qName = attribute1.getName();
        boolean bool = (qName.equals(paramQName) || (qName.getLocalPart().equals(str) && (qName.getPrefix() == null || "".equals(qName.getPrefix())))) ? 1 : 0;
        if (bool) {
          attribute = attribute1;
          break;
        } 
      } 
    } 
    return attribute;
  }
  
  private String unmarshalNodeContent(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, QName paramQName, XMLEventReader paramXMLEventReader) throws PolicyException {
    StringBuilder stringBuilder = null;
    while (paramXMLEventReader.hasNext()) {
      try {
        String str;
        ModelNode modelNode;
        StartElement startElement;
        XMLEvent xMLEvent = paramXMLEventReader.nextEvent();
        switch (xMLEvent.getEventType()) {
          case 5:
            continue;
          case 4:
            stringBuilder = processCharacters(paramModelNode.getType(), xMLEvent.asCharacters(), stringBuilder);
            continue;
          case 2:
            checkEndTagName(paramQName, xMLEvent.asEndElement());
            break;
          case 1:
            startElement = xMLEvent.asStartElement();
            modelNode = addNewChildNode(paramNamespaceVersion, paramModelNode, startElement);
            str = unmarshalNodeContent(paramNamespaceVersion, modelNode, startElement.getName(), paramXMLEventReader);
            if (modelNode.isDomainSpecific())
              parseAssertionData(paramNamespaceVersion, str, modelNode, startElement); 
            continue;
        } 
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED()));
      } catch (XMLStreamException xMLStreamException) {
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), xMLStreamException));
      } 
    } 
    return (stringBuilder == null) ? null : stringBuilder.toString().trim();
  }
  
  private XMLEventReader createXMLEventReader(Object paramObject) throws PolicyException {
    if (paramObject instanceof XMLEventReader)
      return (XMLEventReader)paramObject; 
    if (!(paramObject instanceof Reader))
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(paramObject.getClass().getName()))); 
    try {
      return XMLInputFactory.newInstance().createXMLEventReader((Reader)paramObject);
    } catch (XMLStreamException xMLStreamException) {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE(), xMLStreamException));
    } 
  }
  
  private void checkEndTagName(QName paramQName, EndElement paramEndElement) throws PolicyException {
    QName qName = paramEndElement.getName();
    if (!paramQName.equals(qName))
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(paramQName, qName))); 
  }
  
  private StringBuilder processCharacters(ModelNode.Type paramType, Characters paramCharacters, StringBuilder paramStringBuilder) throws PolicyException {
    if (paramCharacters.isWhiteSpace())
      return paramStringBuilder; 
    StringBuilder stringBuilder = (paramStringBuilder == null) ? new StringBuilder() : paramStringBuilder;
    String str = paramCharacters.getData();
    if (paramType == ModelNode.Type.ASSERTION || paramType == ModelNode.Type.ASSERTION_PARAMETER_NODE)
      return stringBuilder.append(str); 
    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(paramType, str)));
  }
  
  private boolean parseBooleanValue(String paramString) throws PolicyException {
    if ("true".equals(paramString) || "1".equals(paramString))
      return true; 
    if ("false".equals(paramString) || "0".equals(paramString))
      return false; 
    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0095_INVALID_BOOLEAN_VALUE(paramString)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\XmlPolicyModelUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */