package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.InnerClass;
import com.sun.org.apache.bcel.internal.classfile.InnerClasses;
import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class AttributeHTML implements Constants {
  private String class_name;
  
  private PrintWriter file;
  
  private int attr_count = 0;
  
  private ConstantHTML constant_html;
  
  private ConstantPool constant_pool;
  
  AttributeHTML(String paramString1, String paramString2, ConstantPool paramConstantPool, ConstantHTML paramConstantHTML) throws IOException {
    this.class_name = paramString2;
    this.constant_pool = paramConstantPool;
    this.constant_html = paramConstantHTML;
    this.file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_attributes.html"));
    this.file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
  }
  
  private final String codeLink(int paramInt1, int paramInt2) { return "<A HREF=\"" + this.class_name + "_code.html#code" + paramInt2 + "@" + paramInt1 + "\" TARGET=Code>" + paramInt1 + "</A>"; }
  
  final void close() {
    this.file.println("</TABLE></BODY></HTML>");
    this.file.close();
  }
  
  final void writeAttribute(Attribute paramAttribute, String paramString) throws IOException { writeAttribute(paramAttribute, paramString, 0); }
  
  final void writeAttribute(Attribute paramAttribute, String paramString, int paramInt) throws IOException {
    byte b4;
    byte b3;
    InnerClass[] arrayOfInnerClass;
    byte b2;
    LocalVariable[] arrayOfLocalVariable;
    LineNumber[] arrayOfLineNumber;
    byte b1;
    int[] arrayOfInt;
    int j;
    CodeException[] arrayOfCodeException;
    Code code;
    int i;
    byte b = paramAttribute.getTag();
    if (b == -1)
      return; 
    this.attr_count++;
    if (this.attr_count % 2 == 0) {
      this.file.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
    } else {
      this.file.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
    } 
    this.file.println("<H4><A NAME=\"" + paramString + "\">" + this.attr_count + " " + ATTRIBUTE_NAMES[b] + "</A></H4>");
    switch (b) {
      case 2:
        code = (Code)paramAttribute;
        this.file.print("<UL><LI>Maximum stack size = " + code.getMaxStack() + "</LI>\n<LI>Number of local variables = " + code.getMaxLocals() + "</LI>\n<LI><A HREF=\"" + this.class_name + "_code.html#method" + paramInt + "\" TARGET=Code>Byte code</A></LI></UL>\n");
        arrayOfCodeException = code.getExceptionTable();
        j = arrayOfCodeException.length;
        if (j > 0) {
          this.file.print("<P><B>Exceptions handled</B><UL>");
          for (byte b5 = 0; b5 < j; b5++) {
            int k = arrayOfCodeException[b5].getCatchType();
            this.file.print("<LI>");
            if (k != 0) {
              this.file.print(this.constant_html.referenceConstant(k));
            } else {
              this.file.print("Any Exception");
            } 
            this.file.print("<BR>(Ranging from lines " + codeLink(arrayOfCodeException[b5].getStartPC(), paramInt) + " to " + codeLink(arrayOfCodeException[b5].getEndPC(), paramInt) + ", handled at line " + codeLink(arrayOfCodeException[b5].getHandlerPC(), paramInt) + ")</LI>");
          } 
          this.file.print("</UL>");
        } 
        break;
      case 1:
        i = ((ConstantValue)paramAttribute).getConstantValueIndex();
        this.file.print("<UL><LI><A HREF=\"" + this.class_name + "_cp.html#cp" + i + "\" TARGET=\"ConstantPool\">Constant value index(" + i + ")</A></UL>\n");
        break;
      case 0:
        i = ((SourceFile)paramAttribute).getSourceFileIndex();
        this.file.print("<UL><LI><A HREF=\"" + this.class_name + "_cp.html#cp" + i + "\" TARGET=\"ConstantPool\">Source file index(" + i + ")</A></UL>\n");
        break;
      case 3:
        arrayOfInt = ((ExceptionTable)paramAttribute).getExceptionIndexTable();
        this.file.print("<UL>");
        for (b1 = 0; b1 < arrayOfInt.length; b1++)
          this.file.print("<LI><A HREF=\"" + this.class_name + "_cp.html#cp" + arrayOfInt[b1] + "\" TARGET=\"ConstantPool\">Exception class index(" + arrayOfInt[b1] + ")</A>\n"); 
        this.file.print("</UL>\n");
        break;
      case 4:
        arrayOfLineNumber = ((LineNumberTable)paramAttribute).getLineNumberTable();
        this.file.print("<P>");
        for (b2 = 0; b2 < arrayOfLineNumber.length; b2++) {
          this.file.print("(" + arrayOfLineNumber[b2].getStartPC() + ",&nbsp;" + arrayOfLineNumber[b2].getLineNumber() + ")");
          if (b2 < arrayOfLineNumber.length - 1)
            this.file.print(", "); 
        } 
        break;
      case 5:
        arrayOfLocalVariable = ((LocalVariableTable)paramAttribute).getLocalVariableTable();
        this.file.print("<UL>");
        for (b3 = 0; b3 < arrayOfLocalVariable.length; b3++) {
          i = arrayOfLocalVariable[b3].getSignatureIndex();
          String str = ((ConstantUtf8)this.constant_pool.getConstant(i, (byte)1)).getBytes();
          str = Utility.signatureToString(str, false);
          int k = arrayOfLocalVariable[b3].getStartPC();
          int m = k + arrayOfLocalVariable[b3].getLength();
          this.file.println("<LI>" + Class2HTML.referenceType(str) + "&nbsp;<B>" + arrayOfLocalVariable[b3].getName() + "</B> in slot %" + arrayOfLocalVariable[b3].getIndex() + "<BR>Valid from lines <A HREF=\"" + this.class_name + "_code.html#code" + paramInt + "@" + k + "\" TARGET=Code>" + k + "</A> to <A HREF=\"" + this.class_name + "_code.html#code" + paramInt + "@" + m + "\" TARGET=Code>" + m + "</A></LI>");
        } 
        this.file.print("</UL>\n");
        break;
      case 6:
        arrayOfInnerClass = ((InnerClasses)paramAttribute).getInnerClasses();
        this.file.print("<UL>");
        for (b4 = 0; b4 < arrayOfInnerClass.length; b4++) {
          String str1;
          i = arrayOfInnerClass[b4].getInnerNameIndex();
          if (i > 0) {
            str1 = ((ConstantUtf8)this.constant_pool.getConstant(i, (byte)1)).getBytes();
          } else {
            str1 = "&lt;anonymous&gt;";
          } 
          String str2 = Utility.accessToString(arrayOfInnerClass[b4].getInnerAccessFlags());
          this.file.print("<LI><FONT COLOR=\"#FF0000\">" + str2 + "</FONT> " + this.constant_html.referenceConstant(arrayOfInnerClass[b4].getInnerClassIndex()) + " in&nbsp;class " + this.constant_html.referenceConstant(arrayOfInnerClass[b4].getOuterClassIndex()) + " named " + str1 + "</LI>\n");
        } 
        this.file.print("</UL>\n");
        break;
      default:
        this.file.print("<P>" + paramAttribute.toString());
        break;
    } 
    this.file.println("</TD></TR>");
    this.file.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\AttributeHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */