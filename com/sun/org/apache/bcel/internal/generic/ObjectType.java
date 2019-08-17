package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public final class ObjectType extends ReferenceType {
  private String class_name;
  
  public ObjectType(String paramString) {
    super((byte)14, "L" + paramString.replace('.', '/') + ";");
    this.class_name = paramString.replace('/', '.');
  }
  
  public String getClassName() { return this.class_name; }
  
  public int hashCode() { return this.class_name.hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof ObjectType) ? ((ObjectType)paramObject).class_name.equals(this.class_name) : 0; }
  
  public boolean referencesClass() {
    JavaClass javaClass = Repository.lookupClass(this.class_name);
    return (javaClass == null) ? false : javaClass.isClass();
  }
  
  public boolean referencesInterface() {
    JavaClass javaClass = Repository.lookupClass(this.class_name);
    return (javaClass == null) ? false : (!javaClass.isClass());
  }
  
  public boolean subclassOf(ObjectType paramObjectType) { return (referencesInterface() || paramObjectType.referencesInterface()) ? false : Repository.instanceOf(this.class_name, paramObjectType.class_name); }
  
  public boolean accessibleTo(ObjectType paramObjectType) {
    JavaClass javaClass1 = Repository.lookupClass(this.class_name);
    if (javaClass1.isPublic())
      return true; 
    JavaClass javaClass2 = Repository.lookupClass(paramObjectType.class_name);
    return javaClass2.getPackageName().equals(javaClass1.getPackageName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\ObjectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */