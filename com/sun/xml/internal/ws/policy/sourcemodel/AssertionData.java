package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class AssertionData implements Cloneable, Serializable {
  private static final long serialVersionUID = 4416256070795526315L;
  
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionData.class);
  
  private final QName name;
  
  private final String value;
  
  private final Map<QName, String> attributes;
  
  private ModelNode.Type type;
  
  private boolean optional;
  
  private boolean ignorable;
  
  public static AssertionData createAssertionData(QName paramQName) throws IllegalArgumentException { return new AssertionData(paramQName, null, null, ModelNode.Type.ASSERTION, false, false); }
  
  public static AssertionData createAssertionParameterData(QName paramQName) throws IllegalArgumentException { return new AssertionData(paramQName, null, null, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false); }
  
  public static AssertionData createAssertionData(QName paramQName, String paramString, Map<QName, String> paramMap, boolean paramBoolean1, boolean paramBoolean2) throws IllegalArgumentException { return new AssertionData(paramQName, paramString, paramMap, ModelNode.Type.ASSERTION, paramBoolean1, paramBoolean2); }
  
  public static AssertionData createAssertionParameterData(QName paramQName, String paramString, Map<QName, String> paramMap) throws IllegalArgumentException { return new AssertionData(paramQName, paramString, paramMap, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false); }
  
  AssertionData(QName paramQName, String paramString, Map<QName, String> paramMap, ModelNode.Type paramType, boolean paramBoolean1, boolean paramBoolean2) throws IllegalArgumentException {
    this.name = paramQName;
    this.value = paramString;
    this.optional = paramBoolean1;
    this.ignorable = paramBoolean2;
    this.attributes = new HashMap();
    if (paramMap != null && !paramMap.isEmpty())
      this.attributes.putAll(paramMap); 
    setModelNodeType(paramType);
  }
  
  private void setModelNodeType(ModelNode.Type paramType) throws IllegalArgumentException {
    if (paramType == ModelNode.Type.ASSERTION || paramType == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
      this.type = paramType;
    } else {
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(paramType, ModelNode.Type.ASSERTION, ModelNode.Type.ASSERTION_PARAMETER_NODE)));
    } 
  }
  
  AssertionData(AssertionData paramAssertionData) {
    this.name = paramAssertionData.name;
    this.value = paramAssertionData.value;
    this.attributes = new HashMap();
    if (!paramAssertionData.attributes.isEmpty())
      this.attributes.putAll(paramAssertionData.attributes); 
    this.type = paramAssertionData.type;
  }
  
  protected AssertionData clone() throws CloneNotSupportedException { return (AssertionData)super.clone(); }
  
  public boolean containsAttribute(QName paramQName) {
    synchronized (this.attributes) {
      return this.attributes.containsKey(paramQName);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AssertionData))
      return false; 
    boolean bool = true;
    AssertionData assertionData = (AssertionData)paramObject;
    bool = (bool && this.name.equals(assertionData.name));
    bool = (bool && ((this.value == null) ? (assertionData.value == null) : this.value.equals(assertionData.value)));
    synchronized (this.attributes) {
      bool = (bool && this.attributes.equals(assertionData.attributes));
    } 
    return bool;
  }
  
  public String getAttributeValue(QName paramQName) {
    synchronized (this.attributes) {
      return (String)this.attributes.get(paramQName);
    } 
  }
  
  public Map<QName, String> getAttributes() {
    synchronized (this.attributes) {
      return new HashMap(this.attributes);
    } 
  }
  
  public Set<Map.Entry<QName, String>> getAttributesSet() {
    synchronized (this.attributes) {
      return new HashSet(this.attributes.entrySet());
    } 
  }
  
  public QName getName() { return this.name; }
  
  public String getValue() { return this.value; }
  
  public int hashCode() {
    int i = 17;
    i = 37 * i + this.name.hashCode();
    i = 37 * i + ((this.value == null) ? 0 : this.value.hashCode());
    synchronized (this.attributes) {
      i = 37 * i + this.attributes.hashCode();
    } 
    return i;
  }
  
  public boolean isPrivateAttributeSet() { return "private".equals(getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE)); }
  
  public String removeAttribute(QName paramQName) {
    synchronized (this.attributes) {
      return (String)this.attributes.remove(paramQName);
    } 
  }
  
  public void setAttribute(QName paramQName, String paramString) {
    synchronized (this.attributes) {
      this.attributes.put(paramQName, paramString);
    } 
  }
  
  public void setOptionalAttribute(boolean paramBoolean) { this.optional = paramBoolean; }
  
  public boolean isOptionalAttributeSet() { return this.optional; }
  
  public void setIgnorableAttribute(boolean paramBoolean) { this.ignorable = paramBoolean; }
  
  public boolean isIgnorableAttributeSet() { return this.ignorable; }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    String str3 = PolicyUtils.Text.createIndent(paramInt + 2);
    paramStringBuffer.append(str1);
    if (this.type == ModelNode.Type.ASSERTION) {
      paramStringBuffer.append("assertion data {");
    } else {
      paramStringBuffer.append("assertion parameter data {");
    } 
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("namespace = '").append(this.name.getNamespaceURI()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("prefix = '").append(this.name.getPrefix()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("local name = '").append(this.name.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("value = '").append(this.value).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("optional = '").append(this.optional).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("ignorable = '").append(this.ignorable).append('\'').append(PolicyUtils.Text.NEW_LINE);
    synchronized (this.attributes) {
      if (this.attributes.isEmpty()) {
        paramStringBuffer.append(str2).append("no attributes");
      } else {
        paramStringBuffer.append(str2).append("attributes {").append(PolicyUtils.Text.NEW_LINE);
        for (Map.Entry entry : this.attributes.entrySet()) {
          QName qName = (QName)entry.getKey();
          paramStringBuffer.append(str3).append("name = '").append(qName.getNamespaceURI()).append(':').append(qName.getLocalPart());
          paramStringBuffer.append("', value = '").append((String)entry.getValue()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        } 
        paramStringBuffer.append(str2).append('}');
      } 
    } 
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE).append(str1).append('}');
    return paramStringBuffer;
  }
  
  public ModelNode.Type getNodeType() { return this.type; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\AssertionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */