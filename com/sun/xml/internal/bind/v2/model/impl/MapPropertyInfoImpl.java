package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

class MapPropertyInfoImpl<T, C, F, M> extends PropertyInfoImpl<T, C, F, M> implements MapPropertyInfo<T, C> {
  private final QName xmlName;
  
  private boolean nil;
  
  private final T keyType;
  
  private final T valueType;
  
  private NonElement<T, C> keyTypeInfo;
  
  private NonElement<T, C> valueTypeInfo;
  
  public MapPropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed) {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper xmlElementWrapper = (XmlElementWrapper)paramPropertySeed.readAnnotation(XmlElementWrapper.class);
    this.xmlName = calcXmlName(xmlElementWrapper);
    this.nil = (xmlElementWrapper != null && xmlElementWrapper.nillable());
    Object object1 = getRawType();
    Object object2 = nav().getBaseClass(object1, nav().asDecl(java.util.Map.class));
    assert object2 != null;
    if (nav().isParameterizedType(object2)) {
      this.keyType = nav().getTypeArgument(object2, 0);
      this.valueType = nav().getTypeArgument(object2, 1);
    } else {
      this.keyType = this.valueType = nav().ref(Object.class);
    } 
  }
  
  public Collection<? extends TypeInfo<T, C>> ref() { return Arrays.asList(new NonElement[] { getKeyType(), getValueType() }); }
  
  public final PropertyKind kind() { return PropertyKind.MAP; }
  
  public QName getXmlName() { return this.xmlName; }
  
  public boolean isCollectionNillable() { return this.nil; }
  
  public NonElement<T, C> getKeyType() {
    if (this.keyTypeInfo == null)
      this.keyTypeInfo = getTarget(this.keyType); 
    return this.keyTypeInfo;
  }
  
  public NonElement<T, C> getValueType() {
    if (this.valueTypeInfo == null)
      this.valueTypeInfo = getTarget(this.valueType); 
    return this.valueTypeInfo;
  }
  
  public NonElement<T, C> getTarget(T paramT) {
    assert this.parent.builder != null : "this method must be called during the build stage";
    return this.parent.builder.getTypeInfo(paramT, this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\MapPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */