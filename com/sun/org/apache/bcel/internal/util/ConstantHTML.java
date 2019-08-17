package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class ConstantHTML implements Constants {
  private String class_name;
  
  private String class_package;
  
  private ConstantPool constant_pool;
  
  private PrintWriter file;
  
  private String[] constant_ref;
  
  private Constant[] constants;
  
  private Method[] methods;
  
  ConstantHTML(String paramString1, String paramString2, String paramString3, Method[] paramArrayOfMethod, ConstantPool paramConstantPool) throws IOException {
    this.class_name = paramString2;
    this.class_package = paramString3;
    this.constant_pool = paramConstantPool;
    this.methods = paramArrayOfMethod;
    this.constants = paramConstantPool.getConstantPool();
    this.file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_cp.html"));
    this.constant_ref = new String[this.constants.length];
    this.constant_ref[0] = "&lt;unknown&gt;";
    this.file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    for (byte b = 1; b < this.constants.length; b++) {
      if (b % 2 == 0) {
        this.file.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
      } else {
        this.file.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
      } 
      if (this.constants[b] != null)
        writeConstant(b); 
      this.file.print("</TD></TR>\n");
    } 
    this.file.println("</TABLE></BODY></HTML>");
    this.file.close();
  }
  
  String referenceConstant(int paramInt) { return this.constant_ref[paramInt]; }
  
  private void writeConstant(int paramInt) {
    int k;
    ConstantNameAndType constantNameAndType2;
    String str15;
    ConstantString constantString;
    String str14;
    String str13;
    ConstantClass constantClass;
    String str12;
    String str11;
    String str10;
    ConstantFieldref constantFieldref;
    byte b1;
    String str9;
    StringBuffer stringBuffer;
    String str8;
    String str7;
    String[] arrayOfString;
    String str6;
    ConstantNameAndType constantNameAndType1;
    String str5;
    String str4;
    String str3;
    String str2;
    String str1;
    int j;
    int i;
    byte b = this.constants[paramInt].getTag();
    this.file.println("<H4> <A NAME=cp" + paramInt + ">" + paramInt + "</A> " + CONSTANT_NAMES[b] + "</H4>");
    switch (b) {
      case 10:
      case 11:
        if (b == 10) {
          ConstantMethodref constantMethodref = (ConstantMethodref)this.constant_pool.getConstant(paramInt, (byte)10);
          i = constantMethodref.getClassIndex();
          j = constantMethodref.getNameAndTypeIndex();
        } else {
          ConstantInterfaceMethodref constantInterfaceMethodref = (ConstantInterfaceMethodref)this.constant_pool.getConstant(paramInt, (byte)11);
          i = constantInterfaceMethodref.getClassIndex();
          j = constantInterfaceMethodref.getNameAndTypeIndex();
        } 
        str2 = this.constant_pool.constantToString(j, (byte)12);
        str3 = Class2HTML.toHTML(str2);
        str4 = this.constant_pool.constantToString(i, (byte)7);
        str5 = Utility.compactClassName(str4);
        str5 = Utility.compactClassName(str4);
        str5 = Utility.compactClassName(str5, this.class_package + ".", true);
        constantNameAndType1 = (ConstantNameAndType)this.constant_pool.getConstant(j, (byte)12);
        str6 = this.constant_pool.constantToString(constantNameAndType1.getSignatureIndex(), (byte)1);
        arrayOfString = Utility.methodSignatureArgumentTypes(str6, false);
        str7 = Utility.methodSignatureReturnType(str6, false);
        str8 = Class2HTML.referenceType(str7);
        stringBuffer = new StringBuffer("(");
        for (b1 = 0; b1 < arrayOfString.length; b1++) {
          stringBuffer.append(Class2HTML.referenceType(arrayOfString[b1]));
          if (b1 < arrayOfString.length - 1)
            stringBuffer.append(",&nbsp;"); 
        } 
        stringBuffer.append(")");
        str9 = stringBuffer.toString();
        if (str4.equals(this.class_name)) {
          str1 = "<A HREF=\"" + this.class_name + "_code.html#method" + getMethodNumber(str2 + str6) + "\" TARGET=Code>" + str3 + "</A>";
        } else {
          str1 = "<A HREF=\"" + str4 + ".html\" TARGET=_top>" + str5 + "</A>." + str3;
        } 
        this.constant_ref[paramInt] = str8 + "&nbsp;<A HREF=\"" + this.class_name + "_cp.html#cp" + i + "\" TARGET=Constants>" + str5 + "</A>.<A HREF=\"" + this.class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str3 + "</A>&nbsp;" + str9;
        this.file.println("<P><TT>" + str8 + "&nbsp;" + str1 + str9 + "&nbsp;</TT>\n<UL><LI><A HREF=\"#cp" + i + "\">Class index(" + i + ")</A>\n<LI><A HREF=\"#cp" + j + "\">NameAndType index(" + j + ")</A></UL>");
        return;
      case 9:
        constantFieldref = (ConstantFieldref)this.constant_pool.getConstant(paramInt, (byte)9);
        i = constantFieldref.getClassIndex();
        j = constantFieldref.getNameAndTypeIndex();
        str10 = this.constant_pool.constantToString(i, (byte)7);
        str11 = Utility.compactClassName(str10);
        str11 = Utility.compactClassName(str11, this.class_package + ".", true);
        str12 = this.constant_pool.constantToString(j, (byte)12);
        if (str10.equals(this.class_name)) {
          str1 = "<A HREF=\"" + str10 + "_methods.html#field" + str12 + "\" TARGET=Methods>" + str12 + "</A>";
        } else {
          str1 = "<A HREF=\"" + str10 + ".html\" TARGET=_top>" + str11 + "</A>." + str12 + "\n";
        } 
        this.constant_ref[paramInt] = "<A HREF=\"" + this.class_name + "_cp.html#cp" + i + "\" TARGET=Constants>" + str11 + "</A>.<A HREF=\"" + this.class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str12 + "</A>";
        this.file.println("<P><TT>" + str1 + "</TT><BR>\n<UL><LI><A HREF=\"#cp" + i + "\">Class(" + i + ")</A><BR>\n<LI><A HREF=\"#cp" + j + "\">NameAndType(" + j + ")</A></UL>");
        return;
      case 7:
        constantClass = (ConstantClass)this.constant_pool.getConstant(paramInt, (byte)7);
        j = constantClass.getNameIndex();
        str13 = this.constant_pool.constantToString(paramInt, b);
        str14 = Utility.compactClassName(str13);
        str14 = Utility.compactClassName(str14, this.class_package + ".", true);
        str1 = "<A HREF=\"" + str13 + ".html\" TARGET=_top>" + str14 + "</A>";
        this.constant_ref[paramInt] = "<A HREF=\"" + this.class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str14 + "</A>";
        this.file.println("<P><TT>" + str1 + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A></UL>\n");
        return;
      case 8:
        constantString = (ConstantString)this.constant_pool.getConstant(paramInt, (byte)8);
        j = constantString.getStringIndex();
        str15 = Class2HTML.toHTML(this.constant_pool.constantToString(paramInt, b));
        this.file.println("<P><TT>" + str15 + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A></UL>\n");
        return;
      case 12:
        constantNameAndType2 = (ConstantNameAndType)this.constant_pool.getConstant(paramInt, (byte)12);
        j = constantNameAndType2.getNameIndex();
        k = constantNameAndType2.getSignatureIndex();
        this.file.println("<P><TT>" + Class2HTML.toHTML(this.constant_pool.constantToString(paramInt, b)) + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A>\n<LI><A HREF=\"#cp" + k + "\">Signature index(" + k + ")</A></UL>\n");
        return;
    } 
    this.file.println("<P><TT>" + Class2HTML.toHTML(this.constant_pool.constantToString(paramInt, b)) + "</TT>\n");
  }
  
  private final int getMethodNumber(String paramString) {
    for (byte b = 0; b < this.methods.length; b++) {
      String str = this.methods[b].getName() + this.methods[b].getSignature();
      if (str.equals(paramString))
        return b; 
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ConstantHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */