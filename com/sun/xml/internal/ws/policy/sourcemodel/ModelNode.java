package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public final class ModelNode extends Object implements Iterable<ModelNode>, Cloneable {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelNode.class);
  
  private LinkedList<ModelNode> children;
  
  private Collection<ModelNode> unmodifiableViewOnContent;
  
  private final Type type;
  
  private ModelNode parentNode;
  
  private PolicySourceModel parentModel;
  
  private PolicyReferenceData referenceData;
  
  private PolicySourceModel referencedModel;
  
  private AssertionData nodeData;
  
  static ModelNode createRootPolicyNode(PolicySourceModel paramPolicySourceModel) throws IllegalArgumentException {
    if (paramPolicySourceModel == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL())); 
    return new ModelNode(Type.POLICY, paramPolicySourceModel);
  }
  
  private ModelNode(Type paramType, PolicySourceModel paramPolicySourceModel) {
    this.type = paramType;
    this.parentModel = paramPolicySourceModel;
    this.children = new LinkedList();
    this.unmodifiableViewOnContent = Collections.unmodifiableCollection(this.children);
  }
  
  private ModelNode(Type paramType, PolicySourceModel paramPolicySourceModel, AssertionData paramAssertionData) {
    this(paramType, paramPolicySourceModel);
    this.nodeData = paramAssertionData;
  }
  
  private ModelNode(PolicySourceModel paramPolicySourceModel, PolicyReferenceData paramPolicyReferenceData) {
    this(Type.POLICY_REFERENCE, paramPolicySourceModel);
    this.referenceData = paramPolicyReferenceData;
  }
  
  private void checkCreateChildOperationSupportForType(Type paramType) throws UnsupportedOperationException {
    if (!this.type.isChildTypeSupported(paramType))
      throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(paramType, this.type))); 
  }
  
  public ModelNode createChildPolicyNode() {
    checkCreateChildOperationSupportForType(Type.POLICY);
    ModelNode modelNode = new ModelNode(Type.POLICY, this.parentModel);
    addChild(modelNode);
    return modelNode;
  }
  
  public ModelNode createChildAllNode() {
    checkCreateChildOperationSupportForType(Type.ALL);
    ModelNode modelNode = new ModelNode(Type.ALL, this.parentModel);
    addChild(modelNode);
    return modelNode;
  }
  
  public ModelNode createChildExactlyOneNode() {
    checkCreateChildOperationSupportForType(Type.EXACTLY_ONE);
    ModelNode modelNode = new ModelNode(Type.EXACTLY_ONE, this.parentModel);
    addChild(modelNode);
    return modelNode;
  }
  
  public ModelNode createChildAssertionNode() {
    checkCreateChildOperationSupportForType(Type.ASSERTION);
    ModelNode modelNode = new ModelNode(Type.ASSERTION, this.parentModel);
    addChild(modelNode);
    return modelNode;
  }
  
  public ModelNode createChildAssertionNode(AssertionData paramAssertionData) {
    checkCreateChildOperationSupportForType(Type.ASSERTION);
    ModelNode modelNode = new ModelNode(Type.ASSERTION, this.parentModel, paramAssertionData);
    addChild(modelNode);
    return modelNode;
  }
  
  public ModelNode createChildAssertionParameterNode() {
    checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
    ModelNode modelNode = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel);
    addChild(modelNode);
    return modelNode;
  }
  
  ModelNode createChildAssertionParameterNode(AssertionData paramAssertionData) {
    checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
    ModelNode modelNode = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel, paramAssertionData);
    addChild(modelNode);
    return modelNode;
  }
  
  ModelNode createChildPolicyReferenceNode(PolicyReferenceData paramPolicyReferenceData) {
    checkCreateChildOperationSupportForType(Type.POLICY_REFERENCE);
    ModelNode modelNode = new ModelNode(this.parentModel, paramPolicyReferenceData);
    this.parentModel.addNewPolicyReference(modelNode);
    addChild(modelNode);
    return modelNode;
  }
  
  Collection<ModelNode> getChildren() { return this.unmodifiableViewOnContent; }
  
  void setParentModel(PolicySourceModel paramPolicySourceModel) throws IllegalAccessException {
    if (this.parentNode != null)
      throw (IllegalAccessException)LOGGER.logSevereException(new IllegalAccessException(LocalizationMessages.WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED())); 
    updateParentModelReference(paramPolicySourceModel);
  }
  
  private void updateParentModelReference(PolicySourceModel paramPolicySourceModel) throws IllegalAccessException {
    this.parentModel = paramPolicySourceModel;
    for (ModelNode modelNode : this.children)
      modelNode.updateParentModelReference(paramPolicySourceModel); 
  }
  
  public PolicySourceModel getParentModel() { return this.parentModel; }
  
  public Type getType() { return this.type; }
  
  public ModelNode getParentNode() { return this.parentNode; }
  
  public AssertionData getNodeData() { return this.nodeData; }
  
  PolicyReferenceData getPolicyReferenceData() { return this.referenceData; }
  
  public AssertionData setOrReplaceNodeData(AssertionData paramAssertionData) {
    if (!isDomainSpecific())
      throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(this.type))); 
    AssertionData assertionData = this.nodeData;
    this.nodeData = paramAssertionData;
    return assertionData;
  }
  
  boolean isDomainSpecific() { return (this.type == Type.ASSERTION || this.type == Type.ASSERTION_PARAMETER_NODE); }
  
  private boolean addChild(ModelNode paramModelNode) {
    this.children.add(paramModelNode);
    paramModelNode.parentNode = this;
    return true;
  }
  
  void setReferencedModel(PolicySourceModel paramPolicySourceModel) throws IllegalAccessException {
    if (this.type != Type.POLICY_REFERENCE)
      throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(this.type))); 
    this.referencedModel = paramPolicySourceModel;
  }
  
  PolicySourceModel getReferencedModel() { return this.referencedModel; }
  
  public int childrenSize() { return this.children.size(); }
  
  public boolean hasChildren() { return !this.children.isEmpty(); }
  
  public Iterator<ModelNode> iterator() { return this.children.iterator(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ModelNode))
      return false; 
    null = true;
    ModelNode modelNode = (ModelNode)paramObject;
    null = (null && this.type.equals(modelNode.type));
    null = (null && ((this.nodeData == null) ? (modelNode.nodeData == null) : this.nodeData.equals(modelNode.nodeData)));
    return (null && ((this.children == null) ? (modelNode.children == null) : this.children.equals(modelNode.children)));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + this.type.hashCode();
    null = 37 * null + ((this.parentNode == null) ? 0 : this.parentNode.hashCode());
    null = 37 * null + ((this.nodeData == null) ? 0 : this.nodeData.hashCode());
    return 37 * null + this.children.hashCode();
  }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append(this.type).append(" {").append(PolicyUtils.Text.NEW_LINE);
    if (this.type == Type.ASSERTION) {
      if (this.nodeData == null) {
        paramStringBuffer.append(str2).append("no assertion data set");
      } else {
        this.nodeData.toString(paramInt + 1, paramStringBuffer);
      } 
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    } else if (this.type == Type.POLICY_REFERENCE) {
      if (this.referenceData == null) {
        paramStringBuffer.append(str2).append("no policy reference data set");
      } else {
        this.referenceData.toString(paramInt + 1, paramStringBuffer);
      } 
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    } else if (this.type == Type.ASSERTION_PARAMETER_NODE) {
      if (this.nodeData == null) {
        paramStringBuffer.append(str2).append("no parameter data set");
      } else {
        this.nodeData.toString(paramInt + 1, paramStringBuffer);
      } 
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    } 
    if (this.children.size() > 0) {
      for (ModelNode modelNode : this.children)
        modelNode.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE); 
    } else {
      paramStringBuffer.append(str2).append("no child nodes").append(PolicyUtils.Text.NEW_LINE);
    } 
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  protected ModelNode clone() {
    ModelNode modelNode = (ModelNode)super.clone();
    if (this.nodeData != null)
      modelNode.nodeData = this.nodeData.clone(); 
    if (this.referencedModel != null)
      modelNode.referencedModel = this.referencedModel.clone(); 
    modelNode.children = new LinkedList();
    modelNode.unmodifiableViewOnContent = Collections.unmodifiableCollection(modelNode.children);
    for (ModelNode modelNode1 : this.children)
      modelNode.addChild(modelNode1.clone()); 
    return modelNode;
  }
  
  PolicyReferenceData getReferenceData() { return this.referenceData; }
  
  public enum Type {
    POLICY(XmlToken.Policy),
    ALL(XmlToken.All),
    EXACTLY_ONE(XmlToken.ExactlyOne),
    POLICY_REFERENCE(XmlToken.PolicyReference),
    ASSERTION(XmlToken.UNKNOWN),
    ASSERTION_PARAMETER_NODE(XmlToken.UNKNOWN);
    
    private XmlToken token;
    
    Type(XmlToken param1XmlToken1) { this.token = param1XmlToken1; }
    
    public XmlToken getXmlToken() { return this.token; }
    
    private boolean isChildTypeSupported(Type param1Type) {
      switch (ModelNode.null.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[ordinal()]) {
        case 2:
        case 4:
        case 5:
          switch (ModelNode.null.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[param1Type.ordinal()]) {
            case 1:
              return false;
          } 
          return true;
        case 3:
          return false;
        case 6:
          switch (ModelNode.null.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[param1Type.ordinal()]) {
            case 1:
            case 2:
            case 3:
              return true;
          } 
          return false;
        case 1:
          switch (ModelNode.null.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[param1Type.ordinal()]) {
            case 1:
              return true;
          } 
          return false;
      } 
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(this)));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\ModelNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */