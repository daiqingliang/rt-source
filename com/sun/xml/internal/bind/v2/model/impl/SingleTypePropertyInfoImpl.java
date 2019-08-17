package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class SingleTypePropertyInfoImpl<T, C, F, M> extends PropertyInfoImpl<T, C, F, M> {
  private NonElement<T, C> type;
  
  private final Accessor acc;
  
  private Transducer xducer;
  
  public SingleTypePropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed) {
    super(paramClassInfoImpl, paramPropertySeed);
    if (this instanceof RuntimePropertyInfo) {
      Accessor accessor = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
      if (getAdapter() != null && !isCollection())
        accessor = accessor.adapt(((RuntimePropertyInfo)this).getAdapter()); 
      this.acc = accessor;
    } else {
      this.acc = null;
    } 
  }
  
  public List<? extends NonElement<T, C>> ref() { return Collections.singletonList(getTarget()); }
  
  public NonElement<T, C> getTarget() {
    if (this.type == null) {
      assert this.parent.builder != null : "this method must be called during the build stage";
      this.type = this.parent.builder.getTypeInfo(getIndividualType(), this);
    } 
    return this.type;
  }
  
  public PropertyInfo<T, C> getSource() { return this; }
  
  public void link() {
    super.link();
    if (!NonElement.ANYTYPE_NAME.equals(this.type.getTypeName()) && !this.type.isSimpleType() && id() != ID.IDREF)
      this.parent.builder.reportError(new IllegalAnnotationException(Messages.SIMPLE_TYPE_IS_REQUIRED.format(new Object[0]), this.seed)); 
    if (!isCollection() && this.seed.hasAnnotation(javax.xml.bind.annotation.XmlList.class))
      this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(new Object[0]), this)); 
  }
  
  public Accessor getAccessor() { return this.acc; }
  
  public Transducer getTransducer() {
    if (this.xducer == null) {
      this.xducer = RuntimeModelBuilder.createTransducer((RuntimeNonElementRef)this);
      if (this.xducer == null)
        this.xducer = RuntimeBuiltinLeafInfoImpl.STRING; 
    } 
    return this.xducer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\SingleTypePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */