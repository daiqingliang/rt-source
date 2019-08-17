package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.StaxSerializer;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

public final class XmlPolicyModelMarshaller extends PolicyModelMarshaller {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelMarshaller.class);
  
  private final boolean marshallInvisible;
  
  XmlPolicyModelMarshaller(boolean paramBoolean) { this.marshallInvisible = paramBoolean; }
  
  public void marshal(PolicySourceModel paramPolicySourceModel, Object paramObject) throws PolicyException {
    if (paramObject instanceof StaxSerializer) {
      marshal(paramPolicySourceModel, (StaxSerializer)paramObject);
    } else if (paramObject instanceof TypedXmlWriter) {
      marshal(paramPolicySourceModel, (TypedXmlWriter)paramObject);
    } else if (paramObject instanceof XMLStreamWriter) {
      marshal(paramPolicySourceModel, (XMLStreamWriter)paramObject);
    } else {
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(paramObject.getClass().getName())));
    } 
  }
  
  public void marshal(Collection<PolicySourceModel> paramCollection, Object paramObject) throws PolicyException {
    for (PolicySourceModel policySourceModel : paramCollection)
      marshal(policySourceModel, paramObject); 
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, StaxSerializer paramStaxSerializer) throws PolicyException {
    TypedXmlWriter typedXmlWriter = TXW.create(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, paramStaxSerializer);
    marshalDefaultPrefixes(paramPolicySourceModel, typedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, typedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), typedXmlWriter);
    typedXmlWriter.commit();
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter) throws PolicyException {
    TypedXmlWriter typedXmlWriter = paramTypedXmlWriter._element(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class);
    marshalDefaultPrefixes(paramPolicySourceModel, typedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, typedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), typedXmlWriter);
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, XMLStreamWriter paramXMLStreamWriter) throws PolicyException {
    StaxSerializer staxSerializer = new StaxSerializer(paramXMLStreamWriter);
    TypedXmlWriter typedXmlWriter = TXW.create(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, staxSerializer);
    marshalDefaultPrefixes(paramPolicySourceModel, typedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, typedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), typedXmlWriter);
    typedXmlWriter.commit();
    staxSerializer.flush();
  }
  
  private static void marshalPolicyAttributes(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter) throws PolicyException {
    String str1 = paramPolicySourceModel.getPolicyId();
    if (str1 != null)
      paramTypedXmlWriter._attribute(PolicyConstants.WSU_ID, str1); 
    String str2 = paramPolicySourceModel.getPolicyName();
    if (str2 != null)
      paramTypedXmlWriter._attribute(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Name), str2); 
  }
  
  private void marshal(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, TypedXmlWriter paramTypedXmlWriter) {
    for (ModelNode modelNode : paramModelNode) {
      AssertionData assertionData = modelNode.getNodeData();
      if (this.marshallInvisible || assertionData == null || !assertionData.isPrivateAttributeSet()) {
        TypedXmlWriter typedXmlWriter = null;
        if (assertionData == null) {
          typedXmlWriter = paramTypedXmlWriter._element(paramNamespaceVersion.asQName(modelNode.getType().getXmlToken()), TypedXmlWriter.class);
        } else {
          typedXmlWriter = paramTypedXmlWriter._element(assertionData.getName(), TypedXmlWriter.class);
          String str = assertionData.getValue();
          if (str != null)
            typedXmlWriter._pcdata(str); 
          if (assertionData.isOptionalAttributeSet())
            typedXmlWriter._attribute(paramNamespaceVersion.asQName(XmlToken.Optional), Boolean.TRUE); 
          if (assertionData.isIgnorableAttributeSet())
            typedXmlWriter._attribute(paramNamespaceVersion.asQName(XmlToken.Ignorable), Boolean.TRUE); 
          for (Map.Entry entry : assertionData.getAttributesSet())
            typedXmlWriter._attribute((QName)entry.getKey(), entry.getValue()); 
        } 
        marshal(paramNamespaceVersion, modelNode, typedXmlWriter);
      } 
    } 
  }
  
  private void marshalDefaultPrefixes(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter) throws PolicyException {
    Map map = paramPolicySourceModel.getNamespaceToPrefixMapping();
    if (!this.marshallInvisible && map.containsKey("http://java.sun.com/xml/ns/wsit/policy"))
      map.remove("http://java.sun.com/xml/ns/wsit/policy"); 
    for (Map.Entry entry : map.entrySet())
      paramTypedXmlWriter._namespace((String)entry.getKey(), (String)entry.getValue()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\XmlPolicyModelMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */