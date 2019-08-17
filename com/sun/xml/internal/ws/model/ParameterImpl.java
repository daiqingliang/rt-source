package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.jws.WebParam;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

public class ParameterImpl implements Parameter {
  private ParameterBinding binding;
  
  private ParameterBinding outBinding;
  
  private String partName;
  
  private final int index;
  
  private final WebParam.Mode mode;
  
  private TypeReference typeReference;
  
  private TypeInfo typeInfo;
  
  private QName name;
  
  private final JavaMethodImpl parent;
  
  WrapperParameter wrapper;
  
  TypeInfo itemTypeInfo;
  
  public ParameterImpl(JavaMethodImpl paramJavaMethodImpl, TypeInfo paramTypeInfo, WebParam.Mode paramMode, int paramInt) {
    assert paramTypeInfo != null;
    this.typeInfo = paramTypeInfo;
    this.name = paramTypeInfo.tagName;
    this.mode = paramMode;
    this.index = paramInt;
    this.parent = paramJavaMethodImpl;
  }
  
  public AbstractSEIModelImpl getOwner() { return this.parent.owner; }
  
  public JavaMethod getParent() { return this.parent; }
  
  public QName getName() { return this.name; }
  
  public XMLBridge getXMLBridge() { return getOwner().getXMLBridge(this.typeInfo); }
  
  public XMLBridge getInlinedRepeatedElementBridge() {
    TypeInfo typeInfo1 = getItemType();
    if (typeInfo1 != null) {
      XMLBridge xMLBridge = getOwner().getXMLBridge(typeInfo1);
      if (xMLBridge != null)
        return new RepeatedElementBridge(this.typeInfo, xMLBridge); 
    } 
    return null;
  }
  
  public TypeInfo getItemType() {
    if (this.itemTypeInfo != null)
      return this.itemTypeInfo; 
    if (this.parent.getBinding().isRpcLit() || this.wrapper == null)
      return null; 
    if (!com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals((this.wrapper.getTypeInfo()).type))
      return null; 
    if (!getBinding().isBody())
      return null; 
    this.itemTypeInfo = this.typeInfo.getItemType();
    return this.itemTypeInfo;
  }
  
  public Bridge getBridge() { return getOwner().getBridge(this.typeReference); }
  
  protected Bridge getBridge(TypeReference paramTypeReference) { return getOwner().getBridge(paramTypeReference); }
  
  public TypeReference getTypeReference() { return this.typeReference; }
  
  public TypeInfo getTypeInfo() { return this.typeInfo; }
  
  void setTypeReference(TypeReference paramTypeReference) {
    this.typeReference = paramTypeReference;
    this.name = paramTypeReference.tagName;
  }
  
  public WebParam.Mode getMode() { return this.mode; }
  
  public int getIndex() { return this.index; }
  
  public boolean isWrapperStyle() { return false; }
  
  public boolean isReturnValue() { return (this.index == -1); }
  
  public ParameterBinding getBinding() { return (this.binding == null) ? ParameterBinding.BODY : this.binding; }
  
  public void setBinding(ParameterBinding paramParameterBinding) { this.binding = paramParameterBinding; }
  
  public void setInBinding(ParameterBinding paramParameterBinding) { this.binding = paramParameterBinding; }
  
  public void setOutBinding(ParameterBinding paramParameterBinding) { this.outBinding = paramParameterBinding; }
  
  public ParameterBinding getInBinding() { return this.binding; }
  
  public ParameterBinding getOutBinding() { return (this.outBinding == null) ? this.binding : this.outBinding; }
  
  public boolean isIN() { return (this.mode == WebParam.Mode.IN); }
  
  public boolean isOUT() { return (this.mode == WebParam.Mode.OUT); }
  
  public boolean isINOUT() { return (this.mode == WebParam.Mode.INOUT); }
  
  public boolean isResponse() { return (this.index == -1); }
  
  public Object getHolderValue(Object paramObject) { return (paramObject != null && paramObject instanceof Holder) ? ((Holder)paramObject).value : paramObject; }
  
  public String getPartName() { return (this.partName == null) ? this.name.getLocalPart() : this.partName; }
  
  public void setPartName(String paramString) { this.partName = paramString; }
  
  void fillTypes(List<TypeInfo> paramList) {
    TypeInfo typeInfo1 = getItemType();
    paramList.add((typeInfo1 != null) ? typeInfo1 : getTypeInfo());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\ParameterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */