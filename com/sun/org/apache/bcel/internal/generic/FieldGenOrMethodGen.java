package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.util.ArrayList;

public abstract class FieldGenOrMethodGen extends AccessFlags implements NamedAndTyped, Cloneable {
  protected String name;
  
  protected Type type;
  
  protected ConstantPoolGen cp;
  
  private ArrayList attribute_vec = new ArrayList();
  
  public void setType(Type paramType) {
    if (paramType.getType() == 16)
      throw new IllegalArgumentException("Type can not be " + paramType); 
    this.type = paramType;
  }
  
  public Type getType() { return this.type; }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public ConstantPoolGen getConstantPool() { return this.cp; }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen) { this.cp = paramConstantPoolGen; }
  
  public void addAttribute(Attribute paramAttribute) { this.attribute_vec.add(paramAttribute); }
  
  public void removeAttribute(Attribute paramAttribute) { this.attribute_vec.remove(paramAttribute); }
  
  public void removeAttributes() { this.attribute_vec.clear(); }
  
  public Attribute[] getAttributes() {
    Attribute[] arrayOfAttribute = new Attribute[this.attribute_vec.size()];
    this.attribute_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public abstract String getSignature();
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      System.err.println(cloneNotSupportedException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldGenOrMethodGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */