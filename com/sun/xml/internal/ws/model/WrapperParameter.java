package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebParam;

public class WrapperParameter extends ParameterImpl {
  protected final List<ParameterImpl> wrapperChildren = new ArrayList();
  
  public WrapperParameter(JavaMethodImpl paramJavaMethodImpl, TypeInfo paramTypeInfo, WebParam.Mode paramMode, int paramInt) {
    super(paramJavaMethodImpl, paramTypeInfo, paramMode, paramInt);
    paramTypeInfo.properties().put(WrapperParameter.class.getName(), this);
  }
  
  public boolean isWrapperStyle() { return true; }
  
  public List<ParameterImpl> getWrapperChildren() { return this.wrapperChildren; }
  
  public void addWrapperChild(ParameterImpl paramParameterImpl) {
    this.wrapperChildren.add(paramParameterImpl);
    paramParameterImpl.wrapper = this;
    assert paramParameterImpl.getBinding() == ParameterBinding.BODY;
  }
  
  public void clear() { this.wrapperChildren.clear(); }
  
  void fillTypes(List<TypeInfo> paramList) {
    super.fillTypes(paramList);
    if (com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals((getTypeInfo()).type))
      for (ParameterImpl parameterImpl : this.wrapperChildren)
        parameterImpl.fillTypes(paramList);  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\WrapperParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */