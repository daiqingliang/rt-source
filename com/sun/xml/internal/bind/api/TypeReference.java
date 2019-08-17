package com.sun.xml.internal.bind.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import javax.xml.namespace.QName;

public final class TypeReference {
  public final QName tagName;
  
  public final Type type;
  
  public final Annotation[] annotations;
  
  public TypeReference(QName paramQName, Type paramType, Annotation... paramVarArgs) {
    if (paramQName == null || paramType == null || paramVarArgs == null) {
      String str = "";
      if (paramQName == null)
        str = "tagName"; 
      if (paramType == null)
        str = str + ((str.length() > 0) ? ", type" : "type"); 
      if (paramVarArgs == null)
        str = str + ((str.length() > 0) ? ", annotations" : "annotations"); 
      Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str });
      throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str }));
    } 
    this.tagName = new QName(paramQName.getNamespaceURI().intern(), paramQName.getLocalPart().intern(), paramQName.getPrefix());
    this.type = paramType;
    this.annotations = paramVarArgs;
  }
  
  public <A extends Annotation> A get(Class<A> paramClass) {
    for (Annotation annotation : this.annotations) {
      if (annotation.annotationType() == paramClass)
        return (A)(Annotation)paramClass.cast(annotation); 
    } 
    return null;
  }
  
  public TypeReference toItemType() {
    Type type1 = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(this.type, java.util.Collection.class);
    return (type1 == null) ? this : new TypeReference(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(type1, 0), new Annotation[0]);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    TypeReference typeReference = (TypeReference)paramObject;
    return !Arrays.equals(this.annotations, typeReference.annotations) ? false : (!this.tagName.equals(typeReference.tagName) ? false : (!!this.type.equals(typeReference.type)));
  }
  
  public int hashCode() {
    null = this.tagName.hashCode();
    null = 31 * null + this.type.hashCode();
    return 31 * null + Arrays.hashCode(this.annotations);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\api\TypeReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */