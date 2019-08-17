package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class BuiltinLeafInfoImpl<TypeT, ClassDeclT> extends LeafInfoImpl<TypeT, ClassDeclT> implements BuiltinLeafInfo<TypeT, ClassDeclT> {
  private final QName[] typeNames;
  
  protected BuiltinLeafInfoImpl(TypeT paramTypeT, QName... paramVarArgs) {
    super(paramTypeT, (paramVarArgs.length > 0) ? paramVarArgs[0] : null);
    this.typeNames = paramVarArgs;
  }
  
  public final QName[] getTypeNames() { return this.typeNames; }
  
  public final boolean isElement() { return false; }
  
  public final QName getElementName() { return null; }
  
  public final Element<TypeT, ClassDeclT> asElement() { return null; }
  
  public static <TypeT, ClassDeclT> Map<TypeT, BuiltinLeafInfoImpl<TypeT, ClassDeclT>> createLeaves(Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator) {
    HashMap hashMap = new HashMap();
    for (RuntimeBuiltinLeafInfoImpl runtimeBuiltinLeafInfoImpl : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos) {
      Object object = paramNavigator.ref(runtimeBuiltinLeafInfoImpl.getClazz());
      hashMap.put(object, new BuiltinLeafInfoImpl(object, runtimeBuiltinLeafInfoImpl.getTypeNames()));
    } 
    return hashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\BuiltinLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */