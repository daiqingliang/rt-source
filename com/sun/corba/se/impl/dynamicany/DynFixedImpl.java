package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynFixed;

public class DynFixedImpl extends DynAnyBasicImpl implements DynFixed {
  private DynFixedImpl() { this(null, (Any)null, false); }
  
  protected DynFixedImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynFixedImpl(ORB paramORB, TypeCode paramTypeCode) {
    super(paramORB, paramTypeCode);
    this.index = -1;
  }
  
  public String get_value() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return this.any.extract_fixed().toString();
  }
  
  public boolean set_value(String paramString) throws TypeMismatch, InvalidValue {
    BigDecimal bigDecimal;
    int k;
    String str4;
    String str3;
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = 0;
    short s = 0;
    boolean bool = true;
    try {
      i = this.any.type().fixed_digits();
      s = this.any.type().fixed_scale();
    } catch (BadKind badKind) {}
    String str1 = paramString.trim();
    if (str1.length() == 0)
      throw new TypeMismatch(); 
    String str2 = "";
    if (str1.charAt(0) == '-') {
      str2 = "-";
      str1 = str1.substring(1);
    } else if (str1.charAt(0) == '+') {
      str2 = "+";
      str1 = str1.substring(1);
    } 
    int j = str1.indexOf('d');
    if (j == -1)
      j = str1.indexOf('D'); 
    if (j != -1)
      str1 = str1.substring(0, j); 
    if (str1.length() == 0)
      throw new TypeMismatch(); 
    int m = str1.indexOf('.');
    if (m == -1) {
      str3 = str1;
      str4 = null;
      boolean bool1 = false;
      k = str3.length();
    } else if (m == 0) {
      str3 = null;
      str4 = str1;
      int n = str4.length();
      k = n;
    } else {
      str3 = str1.substring(0, m);
      str4 = str1.substring(m + 1);
      int n = str4.length();
      k = str3.length() + n;
    } 
    if (k > i) {
      bool = false;
      if (str3.length() < i) {
        str4 = str4.substring(0, i - str3.length());
      } else if (str3.length() == i) {
        str4 = null;
      } else {
        throw new InvalidValue();
      } 
    } 
    try {
      new BigInteger(str3);
      if (str4 == null) {
        bigDecimal = new BigDecimal(str2 + str3);
      } else {
        new BigInteger(str4);
        bigDecimal = new BigDecimal(str2 + str3 + "." + str4);
      } 
    } catch (NumberFormatException numberFormatException) {
      throw new TypeMismatch();
    } 
    this.any.insert_fixed(bigDecimal, this.any.type());
    return bool;
  }
  
  public String toString() {
    short s1 = 0;
    short s2 = 0;
    try {
      s1 = this.any.type().fixed_digits();
      s2 = this.any.type().fixed_scale();
    } catch (BadKind badKind) {}
    return "DynFixed with value=" + get_value() + ", digits=" + s1 + ", scale=" + s2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynFixedImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */