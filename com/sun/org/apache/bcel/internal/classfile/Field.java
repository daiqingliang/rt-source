package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.DataInputStream;
import java.io.IOException;

public final class Field extends FieldOrMethod {
  public Field(Field paramField) { super(paramField); }
  
  Field(DataInputStream paramDataInputStream, ConstantPool paramConstantPool) throws IOException, ClassFormatException { super(paramDataInputStream, paramConstantPool); }
  
  public Field(int paramInt1, int paramInt2, int paramInt3, Attribute[] paramArrayOfAttribute, ConstantPool paramConstantPool) { super(paramInt1, paramInt2, paramInt3, paramArrayOfAttribute, paramConstantPool); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitField(this); }
  
  public final ConstantValue getConstantValue() {
    for (byte b = 0; b < this.attributes_count; b++) {
      if (this.attributes[b].getTag() == 1)
        return (ConstantValue)this.attributes[b]; 
    } 
    return null;
  }
  
  public final String toString() {
    String str3 = Utility.accessToString(this.access_flags);
    str3 = str3.equals("") ? "" : (str3 + " ");
    String str2 = Utility.signatureToString(getSignature());
    String str1 = getName();
    StringBuffer stringBuffer = new StringBuffer(str3 + str2 + " " + str1);
    ConstantValue constantValue = getConstantValue();
    if (constantValue != null)
      stringBuffer.append(" = " + constantValue); 
    for (byte b = 0; b < this.attributes_count; b++) {
      Attribute attribute = this.attributes[b];
      if (!(attribute instanceof ConstantValue))
        stringBuffer.append(" [" + attribute.toString() + "]"); 
    } 
    return stringBuffer.toString();
  }
  
  public final Field copy(ConstantPool paramConstantPool) { return (Field)copy_(paramConstantPool); }
  
  public Type getType() { return Type.getReturnType(getSignature()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */