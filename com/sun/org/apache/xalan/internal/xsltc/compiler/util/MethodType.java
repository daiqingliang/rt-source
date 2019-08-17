package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.util.Vector;

public final class MethodType extends Type {
  private final Type _resultType;
  
  private final Vector _argsType;
  
  public MethodType(Type paramType) {
    this._argsType = null;
    this._resultType = paramType;
  }
  
  public MethodType(Type paramType1, Type paramType2) {
    if (paramType2 != Type.Void) {
      this._argsType = new Vector();
      this._argsType.addElement(paramType2);
    } else {
      this._argsType = null;
    } 
    this._resultType = paramType1;
  }
  
  public MethodType(Type paramType1, Type paramType2, Type paramType3) {
    this._argsType = new Vector(2);
    this._argsType.addElement(paramType2);
    this._argsType.addElement(paramType3);
    this._resultType = paramType1;
  }
  
  public MethodType(Type paramType1, Type paramType2, Type paramType3, Type paramType4) {
    this._argsType = new Vector(3);
    this._argsType.addElement(paramType2);
    this._argsType.addElement(paramType3);
    this._argsType.addElement(paramType4);
    this._resultType = paramType1;
  }
  
  public MethodType(Type paramType, Vector paramVector) {
    this._resultType = paramType;
    this._argsType = (paramVector.size() > 0) ? paramVector : null;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("method{");
    if (this._argsType != null) {
      int i = this._argsType.size();
      for (byte b = 0; b < i; b++) {
        stringBuffer.append(this._argsType.elementAt(b));
        if (b != i - 1)
          stringBuffer.append(','); 
      } 
    } else {
      stringBuffer.append("void");
    } 
    stringBuffer.append('}');
    return stringBuffer.toString();
  }
  
  public String toSignature() { return toSignature(""); }
  
  public String toSignature(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    if (this._argsType != null) {
      int i = this._argsType.size();
      for (byte b = 0; b < i; b++)
        stringBuffer.append(((Type)this._argsType.elementAt(b)).toSignature()); 
    } 
    return stringBuffer.append(paramString).append(')').append(this._resultType.toSignature()).toString();
  }
  
  public Type toJCType() { return null; }
  
  public boolean identicalTo(Type paramType) {
    boolean bool = false;
    if (paramType instanceof MethodType) {
      MethodType methodType = (MethodType)paramType;
      if (this._resultType.identicalTo(methodType._resultType)) {
        int i = argsCount();
        bool = (i == methodType.argsCount());
        for (byte b = 0; b < i && bool; b++) {
          Type type1 = (Type)this._argsType.elementAt(b);
          Type type2 = (Type)methodType._argsType.elementAt(b);
          bool = type1.identicalTo(type2);
        } 
      } 
    } 
    return bool;
  }
  
  public int distanceTo(Type paramType) {
    int i = Integer.MAX_VALUE;
    if (paramType instanceof MethodType) {
      MethodType methodType = (MethodType)paramType;
      if (this._argsType != null) {
        int j = this._argsType.size();
        if (j == methodType._argsType.size()) {
          i = 0;
          for (byte b = 0; b < j; b++) {
            Type type1 = (Type)this._argsType.elementAt(b);
            Type type2 = (Type)methodType._argsType.elementAt(b);
            int k = type1.distanceTo(type2);
            if (k == Integer.MAX_VALUE) {
              i = k;
              break;
            } 
            i += type1.distanceTo(type2);
          } 
        } 
      } else if (methodType._argsType == null) {
        i = 0;
      } 
    } 
    return i;
  }
  
  public Type resultType() { return this._resultType; }
  
  public Vector argsType() { return this._argsType; }
  
  public int argsCount() { return (this._argsType == null) ? 0 : this._argsType.size(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\MethodType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */