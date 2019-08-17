package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class PolicySourceModel implements Cloneable {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySourceModel.class);
  
  private static final Map<String, String> DEFAULT_NAMESPACE_TO_PREFIX = new HashMap();
  
  private final Map<String, String> namespaceToPrefix = new HashMap(DEFAULT_NAMESPACE_TO_PREFIX);
  
  private ModelNode rootNode = ModelNode.createRootPolicyNode(this);
  
  private final String policyId;
  
  private final String policyName;
  
  private final NamespaceVersion nsVersion;
  
  private final List<ModelNode> references = new LinkedList();
  
  private boolean expanded = false;
  
  public static PolicySourceModel createPolicySourceModel(NamespaceVersion paramNamespaceVersion) { return new PolicySourceModel(paramNamespaceVersion); }
  
  public static PolicySourceModel createPolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2) { return new PolicySourceModel(paramNamespaceVersion, paramString1, paramString2); }
  
  private PolicySourceModel(NamespaceVersion paramNamespaceVersion) { this(paramNamespaceVersion, null, null); }
  
  private PolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2) { this(paramNamespaceVersion, paramString1, paramString2, null); }
  
  protected PolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, Collection<PrefixMapper> paramCollection) {
    this.nsVersion = paramNamespaceVersion;
    this.policyId = paramString1;
    this.policyName = paramString2;
    if (paramCollection != null)
      for (PrefixMapper prefixMapper : paramCollection)
        this.namespaceToPrefix.putAll(prefixMapper.getPrefixMap());  
  }
  
  public ModelNode getRootNode() { return this.rootNode; }
  
  public String getPolicyName() { return this.policyName; }
  
  public String getPolicyId() { return this.policyId; }
  
  public NamespaceVersion getNamespaceVersion() { return this.nsVersion; }
  
  Map<String, String> getNamespaceToPrefixMapping() throws PolicyException {
    HashMap hashMap = new HashMap();
    Collection collection = getUsedNamespaces();
    for (String str1 : collection) {
      String str2 = getDefaultPrefix(str1);
      if (str2 != null)
        hashMap.put(str1, str2); 
    } 
    return hashMap;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof PolicySourceModel))
      return false; 
    null = true;
    PolicySourceModel policySourceModel = (PolicySourceModel)paramObject;
    null = (null && ((this.policyId == null) ? (policySourceModel.policyId == null) : this.policyId.equals(policySourceModel.policyId)));
    null = (null && ((this.policyName == null) ? (policySourceModel.policyName == null) : this.policyName.equals(policySourceModel.policyName)));
    return (null && this.rootNode.equals(policySourceModel.rootNode));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + ((this.policyId == null) ? 0 : this.policyId.hashCode());
    null = 37 * null + ((this.policyName == null) ? 0 : this.policyName.hashCode());
    return 37 * null + this.rootNode.hashCode();
  }
  
  public String toString() {
    String str = PolicyUtils.Text.createIndent(1);
    StringBuffer stringBuffer = new StringBuffer(60);
    stringBuffer.append("Policy source model {").append(PolicyUtils.Text.NEW_LINE);
    stringBuffer.append(str).append("policy id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
    stringBuffer.append(str).append("policy name = '").append(this.policyName).append('\'').append(PolicyUtils.Text.NEW_LINE);
    this.rootNode.toString(1, stringBuffer).append(PolicyUtils.Text.NEW_LINE).append('}');
    return stringBuffer.toString();
  }
  
  protected PolicySourceModel clone() throws CloneNotSupportedException {
    PolicySourceModel policySourceModel = (PolicySourceModel)super.clone();
    policySourceModel.rootNode = this.rootNode.clone();
    try {
      policySourceModel.rootNode.setParentModel(policySourceModel);
    } catch (IllegalAccessException illegalAccessException) {
      throw (CloneNotSupportedException)LOGGER.logSevereException(new CloneNotSupportedException(LocalizationMessages.WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT()), illegalAccessException);
    } 
    return policySourceModel;
  }
  
  public boolean containsPolicyReferences() { return !this.references.isEmpty(); }
  
  private boolean isExpanded() { return (this.references.isEmpty() || this.expanded); }
  
  public void expand(PolicySourceModelContext paramPolicySourceModelContext) throws PolicyException {
    if (!isExpanded()) {
      for (ModelNode modelNode : this.references) {
        PolicySourceModel policySourceModel;
        PolicyReferenceData policyReferenceData = modelNode.getPolicyReferenceData();
        String str = policyReferenceData.getDigest();
        if (str == null) {
          policySourceModel = paramPolicySourceModelContext.retrieveModel(policyReferenceData.getReferencedModelUri());
        } else {
          policySourceModel = paramPolicySourceModelContext.retrieveModel(policyReferenceData.getReferencedModelUri(), policyReferenceData.getDigestAlgorithmUri(), str);
        } 
        modelNode.setReferencedModel(policySourceModel);
      } 
      this.expanded = true;
    } 
  }
  
  void addNewPolicyReference(ModelNode paramModelNode) {
    if (paramModelNode.getType() != ModelNode.Type.POLICY_REFERENCE)
      throw new IllegalArgumentException(LocalizationMessages.WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(paramModelNode.getType())); 
    this.references.add(paramModelNode);
  }
  
  private Collection<String> getUsedNamespaces() throws PolicyException {
    HashSet hashSet = new HashSet();
    hashSet.add(getNamespaceVersion().toString());
    if (this.policyId != null)
      hashSet.add("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"); 
    LinkedList linkedList = new LinkedList();
    linkedList.add(this.rootNode);
    ModelNode modelNode;
    while ((modelNode = (ModelNode)linkedList.poll()) != null) {
      for (ModelNode modelNode1 : modelNode.getChildren()) {
        if (modelNode1.hasChildren() && !linkedList.offer(modelNode1))
          throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0081_UNABLE_TO_INSERT_CHILD(linkedList, modelNode1))); 
        if (modelNode1.isDomainSpecific()) {
          AssertionData assertionData = modelNode1.getNodeData();
          hashSet.add(assertionData.getName().getNamespaceURI());
          if (assertionData.isPrivateAttributeSet())
            hashSet.add("http://java.sun.com/xml/ns/wsit/policy"); 
          for (Map.Entry entry : assertionData.getAttributesSet())
            hashSet.add(((QName)entry.getKey()).getNamespaceURI()); 
        } 
      } 
    } 
    return hashSet;
  }
  
  private String getDefaultPrefix(String paramString) { return (String)this.namespaceToPrefix.get(paramString); }
  
  static  {
    PrefixMapper[] arrayOfPrefixMapper = (PrefixMapper[])PolicyUtils.ServiceProvider.load(PrefixMapper.class);
    if (arrayOfPrefixMapper != null)
      for (PrefixMapper prefixMapper : arrayOfPrefixMapper)
        DEFAULT_NAMESPACE_TO_PREFIX.putAll(prefixMapper.getPrefixMap());  
    for (NamespaceVersion namespaceVersion : NamespaceVersion.values())
      DEFAULT_NAMESPACE_TO_PREFIX.put(namespaceVersion.toString(), namespaceVersion.getDefaultNamespacePrefix()); 
    DEFAULT_NAMESPACE_TO_PREFIX.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
    DEFAULT_NAMESPACE_TO_PREFIX.put("http://java.sun.com/xml/ns/wsit/policy", "sunwsp");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicySourceModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */