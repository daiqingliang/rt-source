package com.sun.xml.internal.ws.spi.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class TypeInfo {
  public final QName tagName;
  
  public Type type;
  
  public final Annotation[] annotations;
  
  private Map<String, Object> properties = new HashMap();
  
  private boolean isGlobalElement = true;
  
  private TypeInfo parentCollectionType;
  
  private Type genericType;
  
  private boolean nillable = true;
  
  public TypeInfo(QName paramQName, Type paramType, Annotation... paramVarArgs) {
    if (paramQName == null || paramType == null || paramVarArgs == null) {
      String str = "";
      if (paramQName == null)
        str = "tagName"; 
      if (paramType == null)
        str = str + ((str.length() > 0) ? ", type" : "type"); 
      if (paramVarArgs == null)
        str = str + ((str.length() > 0) ? ", annotations" : "annotations"); 
      throw new IllegalArgumentException("Argument(s) \"" + str + "\" can''t be null.)");
    } 
    this.tagName = new QName(paramQName.getNamespaceURI().intern(), paramQName.getLocalPart().intern(), paramQName.getPrefix());
    this.type = paramType;
    if (paramType instanceof Class && ((Class)paramType).isPrimitive())
      this.nillable = false; 
    this.annotations = paramVarArgs;
  }
  
  public <A extends Annotation> A get(Class<A> paramClass) {
    for (Annotation annotation : this.annotations) {
      if (annotation.annotationType() == paramClass)
        return (A)(Annotation)paramClass.cast(annotation); 
    } 
    return null;
  }
  
  public TypeInfo toItemType() {
    Type type1 = (this.genericType != null) ? this.genericType : this.type;
    Type type2 = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(type1, java.util.Collection.class);
    return (type2 == null) ? this : new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(type2, 0), new Annotation[0]);
  }
  
  public Map<String, Object> properties() { return this.properties; }
  
  public boolean isGlobalElement() { return this.isGlobalElement; }
  
  public void setGlobalElement(boolean paramBoolean) { this.isGlobalElement = paramBoolean; }
  
  public TypeInfo getParentCollectionType() { return this.parentCollectionType; }
  
  public void setParentCollectionType(TypeInfo paramTypeInfo) { this.parentCollectionType = paramTypeInfo; }
  
  public boolean isRepeatedElement() { return (this.parentCollectionType != null); }
  
  public Type getGenericType() { return this.genericType; }
  
  public void setGenericType(Type paramType) { this.genericType = paramType; }
  
  public boolean isNillable() { return this.nillable; }
  
  public void setNillable(boolean paramBoolean) { this.nillable = paramBoolean; }
  
  public String toString() { return "TypeInfo: Type = " + this.type + ", tag = " + this.tagName; }
  
  public TypeInfo getItemType() {
    if (this.type instanceof Class && ((Class)this.type).isArray() && !byte[].class.equals(this.type)) {
      Type type3 = ((Class)this.type).getComponentType();
      Type type4 = null;
      if (this.genericType != null && this.genericType instanceof GenericArrayType) {
        GenericArrayType genericArrayType = (GenericArrayType)this.type;
        type4 = genericArrayType.getGenericComponentType();
        type3 = genericArrayType.getGenericComponentType();
      } 
      TypeInfo typeInfo = new TypeInfo(this.tagName, type3, this.annotations);
      if (type4 != null)
        typeInfo.setGenericType(type4); 
      return typeInfo;
    } 
    Type type1 = (this.genericType != null) ? this.genericType : this.type;
    Type type2 = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(type1, java.util.Collection.class);
    return (type2 != null) ? new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(type2, 0), this.annotations) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */