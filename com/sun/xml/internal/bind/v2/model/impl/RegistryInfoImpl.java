package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElementDecl;

final class RegistryInfoImpl<T, C, F, M> extends Object implements Locatable, RegistryInfo<T, C> {
  final C registryClass;
  
  private final Locatable upstream;
  
  private final Navigator<T, C, F, M> nav;
  
  private final Set<TypeInfo<T, C>> references = new LinkedHashSet();
  
  RegistryInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC) {
    this.nav = paramModelBuilder.nav;
    this.registryClass = paramC;
    this.upstream = paramLocatable;
    paramModelBuilder.registries.put(getPackageName(), this);
    if (this.nav.getDeclaredField(paramC, "_useJAXBProperties") != null) {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.MISSING_JAXB_PROPERTIES.format(new Object[] { getPackageName() }, ), this));
      return;
    } 
    for (Object object : this.nav.getDeclaredMethods(paramC)) {
      ElementInfoImpl elementInfoImpl;
      XmlElementDecl xmlElementDecl = (XmlElementDecl)paramModelBuilder.reader.getMethodAnnotation(XmlElementDecl.class, object, this);
      if (xmlElementDecl == null) {
        if (this.nav.getMethodName(object).startsWith("create"))
          this.references.add(paramModelBuilder.getTypeInfo(this.nav.getReturnType(object), new MethodLocatable(this, object, this.nav))); 
        continue;
      } 
      try {
        elementInfoImpl = paramModelBuilder.createElementInfo(this, object);
      } catch (IllegalAnnotationException illegalAnnotationException) {
        paramModelBuilder.reportError(illegalAnnotationException);
        continue;
      } 
      paramModelBuilder.typeInfoSet.add(elementInfoImpl, paramModelBuilder);
      this.references.add(elementInfoImpl);
    } 
  }
  
  public Locatable getUpstream() { return this.upstream; }
  
  public Location getLocation() { return this.nav.getClassLocation(this.registryClass); }
  
  public Set<TypeInfo<T, C>> getReferences() { return this.references; }
  
  public String getPackageName() { return this.nav.getPackageName(this.registryClass); }
  
  public C getClazz() { return (C)this.registryClass; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RegistryInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */