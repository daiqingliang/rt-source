package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class MethodHTML implements Constants {
  private String class_name;
  
  private PrintWriter file;
  
  private ConstantHTML constant_html;
  
  private AttributeHTML attribute_html;
  
  MethodHTML(String paramString1, String paramString2, Method[] paramArrayOfMethod, Field[] paramArrayOfField, ConstantHTML paramConstantHTML, AttributeHTML paramAttributeHTML) throws IOException {
    this.class_name = paramString2;
    this.attribute_html = paramAttributeHTML;
    this.constant_html = paramConstantHTML;
    this.file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_methods.html"));
    this.file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    this.file.println("<TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Type</TH><TH ALIGN=LEFT>Field&nbsp;name</TH></TR>");
    byte b;
    for (b = 0; b < paramArrayOfField.length; b++)
      writeField(paramArrayOfField[b]); 
    this.file.println("</TABLE>");
    this.file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Return&nbsp;type</TH><TH ALIGN=LEFT>Method&nbsp;name</TH><TH ALIGN=LEFT>Arguments</TH></TR>");
    for (b = 0; b < paramArrayOfMethod.length; b++)
      writeMethod(paramArrayOfMethod[b], b); 
    this.file.println("</TABLE></BODY></HTML>");
    this.file.close();
  }
  
  private void writeField(Field paramField) throws IOException {
    String str1 = Utility.signatureToString(paramField.getSignature());
    String str2 = paramField.getName();
    String str3 = Utility.accessToString(paramField.getAccessFlags());
    str3 = Utility.replace(str3, " ", "&nbsp;");
    this.file.print("<TR><TD><FONT COLOR=\"#FF0000\">" + str3 + "</FONT></TD>\n<TD>" + Class2HTML.referenceType(str1) + "</TD><TD><A NAME=\"field" + str2 + "\">" + str2 + "</A></TD>");
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    byte b;
    for (b = 0; b < arrayOfAttribute.length; b++)
      this.attribute_html.writeAttribute(arrayOfAttribute[b], str2 + "@" + b); 
    for (b = 0; b < arrayOfAttribute.length; b++) {
      if (arrayOfAttribute[b].getTag() == 1) {
        String str = ((ConstantValue)arrayOfAttribute[b]).toString();
        this.file.print("<TD>= <A HREF=\"" + this.class_name + "_attributes.html#" + str2 + "@" + b + "\" TARGET=\"Attributes\">" + str + "</TD>\n");
        break;
      } 
    } 
    this.file.println("</TR>");
  }
  
  private final void writeMethod(Method paramMethod, int paramInt) throws IOException {
    String str1 = paramMethod.getSignature();
    String[] arrayOfString = Utility.methodSignatureArgumentTypes(str1, false);
    String str2 = Utility.methodSignatureReturnType(str1, false);
    String str3 = paramMethod.getName();
    String str5 = Utility.accessToString(paramMethod.getAccessFlags());
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    str5 = Utility.replace(str5, " ", "&nbsp;");
    String str4 = Class2HTML.toHTML(str3);
    this.file.print("<TR VALIGN=TOP><TD><FONT COLOR=\"#FF0000\"><A NAME=method" + paramInt + ">" + str5 + "</A></FONT></TD>");
    this.file.print("<TD>" + Class2HTML.referenceType(str2) + "</TD><TD><A HREF=" + this.class_name + "_code.html#method" + paramInt + " TARGET=Code>" + str4 + "</A></TD>\n<TD>(");
    byte b;
    for (b = 0; b < arrayOfString.length; b++) {
      this.file.print(Class2HTML.referenceType(arrayOfString[b]));
      if (b < arrayOfString.length - 1)
        this.file.print(", "); 
    } 
    this.file.print(")</TD></TR>");
    for (b = 0; b < arrayOfAttribute.length; b++) {
      this.attribute_html.writeAttribute(arrayOfAttribute[b], "method" + paramInt + "@" + b, paramInt);
      byte b1 = arrayOfAttribute[b].getTag();
      if (b1 == 3) {
        this.file.print("<TR VALIGN=TOP><TD COLSPAN=2></TD><TH ALIGN=LEFT>throws</TH><TD>");
        int[] arrayOfInt = ((ExceptionTable)arrayOfAttribute[b]).getExceptionIndexTable();
        for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
          this.file.print(this.constant_html.referenceConstant(arrayOfInt[b2]));
          if (b2 < arrayOfInt.length - 1)
            this.file.print(", "); 
        } 
        this.file.println("</TD></TR>");
      } else if (b1 == 2) {
        Attribute[] arrayOfAttribute1 = ((Code)arrayOfAttribute[b]).getAttributes();
        for (byte b2 = 0; b2 < arrayOfAttribute1.length; b2++)
          this.attribute_html.writeAttribute(arrayOfAttribute1[b2], "method" + paramInt + "@" + b + "@" + b2, paramInt); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\MethodHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */