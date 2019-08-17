package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class SafePolicyReader {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(SafePolicyReader.class);
  
  private final Set<String> urlsRead = new HashSet();
  
  private final Set<String> qualifiedPolicyUris = new HashSet();
  
  public PolicyRecord readPolicyElement(XMLStreamReader paramXMLStreamReader, String paramString) {
    if (null == paramXMLStreamReader || !paramXMLStreamReader.isStartElement())
      return null; 
    StringBuffer stringBuffer = new StringBuffer();
    PolicyRecord policyRecord = new PolicyRecord();
    QName qName = paramXMLStreamReader.getName();
    byte b = 0;
    try {
      do {
        byte b1;
        StringBuffer stringBuffer2;
        int i;
        HashSet hashSet;
        StringBuffer stringBuffer1;
        QName qName1;
        boolean bool;
        switch (paramXMLStreamReader.getEventType()) {
          case 1:
            qName1 = paramXMLStreamReader.getName();
            bool = (NamespaceVersion.resolveAsToken(qName1) == XmlToken.PolicyReference) ? 1 : 0;
            if (qName.equals(qName1))
              b++; 
            stringBuffer1 = new StringBuffer();
            hashSet = new HashSet();
            if (null == qName1.getPrefix() || "".equals(qName1.getPrefix())) {
              stringBuffer.append('<').append(qName1.getLocalPart());
              stringBuffer1.append(" xmlns=\"").append(qName1.getNamespaceURI()).append('"');
            } else {
              stringBuffer.append('<').append(qName1.getPrefix()).append(':').append(qName1.getLocalPart());
              stringBuffer1.append(" xmlns:").append(qName1.getPrefix()).append("=\"").append(qName1.getNamespaceURI()).append('"');
              hashSet.add(qName1.getPrefix());
            } 
            i = paramXMLStreamReader.getAttributeCount();
            stringBuffer2 = new StringBuffer();
            for (b1 = 0; b1 < i; b1++) {
              boolean bool1 = false;
              if (bool && "URI".equals(paramXMLStreamReader.getAttributeName(b1).getLocalPart())) {
                bool1 = true;
                if (null == policyRecord.unresolvedURIs)
                  policyRecord.unresolvedURIs = new HashSet(); 
                policyRecord.unresolvedURIs.add(relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(b1), paramString));
              } 
              if (!"xmlns".equals(paramXMLStreamReader.getAttributePrefix(b1)) || !hashSet.contains(paramXMLStreamReader.getAttributeLocalName(b1)))
                if (null == paramXMLStreamReader.getAttributePrefix(b1) || "".equals(paramXMLStreamReader.getAttributePrefix(b1))) {
                  stringBuffer2.append(' ').append(paramXMLStreamReader.getAttributeLocalName(b1)).append("=\"").append(bool1 ? relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(b1), paramString) : paramXMLStreamReader.getAttributeValue(b1)).append('"');
                } else {
                  stringBuffer2.append(' ').append(paramXMLStreamReader.getAttributePrefix(b1)).append(':').append(paramXMLStreamReader.getAttributeLocalName(b1)).append("=\"").append(bool1 ? relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(b1), paramString) : paramXMLStreamReader.getAttributeValue(b1)).append('"');
                  if (!hashSet.contains(paramXMLStreamReader.getAttributePrefix(b1))) {
                    stringBuffer1.append(" xmlns:").append(paramXMLStreamReader.getAttributePrefix(b1)).append("=\"").append(paramXMLStreamReader.getAttributeNamespace(b1)).append('"');
                    hashSet.add(paramXMLStreamReader.getAttributePrefix(b1));
                  } 
                }  
            } 
            stringBuffer.append(stringBuffer1).append(stringBuffer2).append('>');
            break;
          case 2:
            qName1 = paramXMLStreamReader.getName();
            if (qName.equals(qName1))
              b--; 
            stringBuffer.append("</").append("".equals(qName1.getPrefix()) ? "" : (qName1.getPrefix() + ':')).append(qName1.getLocalPart()).append('>');
            break;
          case 4:
            stringBuffer.append(paramXMLStreamReader.getText());
            break;
          case 12:
            stringBuffer.append("<![CDATA[").append(paramXMLStreamReader.getText()).append("]]>");
            break;
        } 
        if (!paramXMLStreamReader.hasNext() || b <= 0)
          continue; 
        paramXMLStreamReader.next();
      } while (8 != paramXMLStreamReader.getEventType() && b > 0);
      policyRecord.policyModel = ModelUnmarshaller.getUnmarshaller().unmarshalModel(new StringReader(stringBuffer.toString()));
      if (null != policyRecord.policyModel.getPolicyId()) {
        policyRecord.setUri(paramString + "#" + policyRecord.policyModel.getPolicyId(), policyRecord.policyModel.getPolicyId());
      } else if (policyRecord.policyModel.getPolicyName() != null) {
        policyRecord.setUri(policyRecord.policyModel.getPolicyName(), policyRecord.policyModel.getPolicyName());
      } 
    } catch (Exception exception) {
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(stringBuffer.toString()), exception));
    } 
    this.urlsRead.add(paramString);
    return policyRecord;
  }
  
  public Set<String> getUrlsRead() { return this.urlsRead; }
  
  public String readPolicyReferenceElement(XMLStreamReader paramXMLStreamReader) {
    try {
      if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.PolicyReference)
        for (byte b = 0; b < paramXMLStreamReader.getAttributeCount(); b++) {
          if (XmlToken.resolveToken(paramXMLStreamReader.getAttributeName(b).getLocalPart()) == XmlToken.Uri) {
            String str = paramXMLStreamReader.getAttributeValue(b);
            paramXMLStreamReader.next();
            return str;
          } 
        }  
      paramXMLStreamReader.next();
      return null;
    } catch (XMLStreamException xMLStreamException) {
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE(), xMLStreamException));
    } 
  }
  
  public static String relativeToAbsoluteUrl(String paramString1, String paramString2) { return ('#' != paramString1.charAt(0)) ? paramString1 : ((null == paramString2) ? paramString1 : (paramString2 + paramString1)); }
  
  public final class PolicyRecord {
    PolicyRecord next;
    
    PolicySourceModel policyModel;
    
    Set<String> unresolvedURIs;
    
    private String uri;
    
    PolicyRecord insert(PolicyRecord param1PolicyRecord) {
      if (null == param1PolicyRecord.unresolvedURIs || param1PolicyRecord.unresolvedURIs.isEmpty()) {
        param1PolicyRecord.next = this;
        return param1PolicyRecord;
      } 
      PolicyRecord policyRecord1 = this;
      PolicyRecord policyRecord2 = null;
      PolicyRecord policyRecord3;
      for (policyRecord3 = policyRecord1; null != policyRecord3.next; policyRecord3 = policyRecord3.next) {
        if (null != policyRecord3.unresolvedURIs && policyRecord3.unresolvedURIs.contains(param1PolicyRecord.uri)) {
          if (null == policyRecord2) {
            param1PolicyRecord.next = policyRecord3;
            return param1PolicyRecord;
          } 
          policyRecord2.next = param1PolicyRecord;
          param1PolicyRecord.next = policyRecord3;
          return policyRecord1;
        } 
        if (param1PolicyRecord.unresolvedURIs.remove(policyRecord3.uri) && param1PolicyRecord.unresolvedURIs.isEmpty()) {
          param1PolicyRecord.next = policyRecord3.next;
          policyRecord3.next = param1PolicyRecord;
          return policyRecord1;
        } 
        policyRecord2 = policyRecord3;
      } 
      param1PolicyRecord.next = null;
      policyRecord3.next = param1PolicyRecord;
      return policyRecord1;
    }
    
    public void setUri(String param1String1, String param1String2) throws PolicyException {
      if (SafePolicyReader.this.qualifiedPolicyUris.contains(param1String1))
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1020_DUPLICATE_ID(param1String2))); 
      this.uri = param1String1;
      SafePolicyReader.this.qualifiedPolicyUris.add(param1String1);
    }
    
    public String getUri() { return this.uri; }
    
    public String toString() {
      String str = this.uri;
      if (null != this.next)
        str = str + "->" + this.next.toString(); 
      return str;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\SafePolicyReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */