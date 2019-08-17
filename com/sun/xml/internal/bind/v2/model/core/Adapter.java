package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Adapter<TypeT, ClassDeclT> extends Object {
  public final ClassDeclT adapterType;
  
  public final TypeT defaultType;
  
  public final TypeT customType;
  
  public Adapter(XmlJavaTypeAdapter paramXmlJavaTypeAdapter, AnnotationReader<TypeT, ClassDeclT, ?, ?> paramAnnotationReader, Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator) { this(paramNavigator.asDecl(paramAnnotationReader.getClassValue(paramXmlJavaTypeAdapter, "value")), paramNavigator); }
  
  public Adapter(ClassDeclT paramClassDeclT, Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator) {
    this.adapterType = paramClassDeclT;
    Object object = paramNavigator.getBaseClass(paramNavigator.use(paramClassDeclT), paramNavigator.asDecl(javax.xml.bind.annotation.adapters.XmlAdapter.class));
    assert object != null;
    if (paramNavigator.isParameterizedType(object)) {
      this.defaultType = paramNavigator.getTypeArgument(object, 0);
    } else {
      this.defaultType = paramNavigator.ref(Object.class);
    } 
    if (paramNavigator.isParameterizedType(object)) {
      this.customType = paramNavigator.getTypeArgument(object, 1);
    } else {
      this.customType = paramNavigator.ref(Object.class);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\Adapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */