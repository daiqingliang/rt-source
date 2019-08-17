package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;

final class CodeHTML implements Constants {
  private String class_name;
  
  private Method[] methods;
  
  private PrintWriter file;
  
  private BitSet goto_set;
  
  private ConstantPool constant_pool;
  
  private ConstantHTML constant_html;
  
  private static boolean wide = false;
  
  CodeHTML(String paramString1, String paramString2, Method[] paramArrayOfMethod, ConstantPool paramConstantPool, ConstantHTML paramConstantHTML) throws IOException {
    this.class_name = paramString2;
    this.methods = paramArrayOfMethod;
    this.constant_pool = paramConstantPool;
    this.constant_html = paramConstantHTML;
    this.file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_code.html"));
    this.file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\">");
    for (byte b = 0; b < paramArrayOfMethod.length; b++)
      writeMethod(paramArrayOfMethod[b], b); 
    this.file.println("</BODY></HTML>");
    this.file.close();
  }
  
  private final String codeToHTML(ByteSequence paramByteSequence, int paramInt) throws IOException {
    byte b1;
    String str5;
    String[] arrayOfString;
    ConstantNameAndType constantNameAndType;
    String str4;
    short s2;
    String str3;
    ConstantFieldref constantFieldref;
    int i5;
    int i4;
    int i3;
    int[] arrayOfInt;
    byte b;
    int i1;
    int n;
    int m;
    int k;
    int j;
    String str2;
    String str1;
    short s1 = (short)paramByteSequence.readUnsignedByte();
    int i = 0;
    int i2 = 0;
    StringBuffer stringBuffer = new StringBuffer("<TT>" + OPCODE_NAMES[s1] + "</TT></TD><TD>");
    if (s1 == 170 || s1 == 171) {
      int i6 = paramByteSequence.getIndex() % 4;
      i2 = (i6 == 0) ? 0 : (4 - i6);
      for (byte b2 = 0; b2 < i2; b2++)
        paramByteSequence.readByte(); 
      i = paramByteSequence.readInt();
    } 
    switch (s1) {
      case 170:
        j = paramByteSequence.readInt();
        k = paramByteSequence.readInt();
        i3 = paramByteSequence.getIndex() - 12 - i2 - 1;
        i += i3;
        stringBuffer.append("<TABLE BORDER=1><TR>");
        arrayOfInt = new int[k - j + 1];
        for (i4 = 0; i4 < arrayOfInt.length; i4++) {
          arrayOfInt[i4] = i3 + paramByteSequence.readInt();
          stringBuffer.append("<TH>" + (j + i4) + "</TH>");
        } 
        stringBuffer.append("<TH>default</TH></TR>\n<TR>");
        for (i4 = 0; i4 < arrayOfInt.length; i4++)
          stringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + arrayOfInt[i4] + "\">" + arrayOfInt[i4] + "</A></TD>"); 
        stringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + i + "\">" + i + "</A></TD></TR>\n</TABLE>\n");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 171:
        i4 = paramByteSequence.readInt();
        i3 = paramByteSequence.getIndex() - 8 - i2 - 1;
        arrayOfInt = new int[i4];
        i += i3;
        stringBuffer.append("<TABLE BORDER=1><TR>");
        for (i5 = 0; i5 < i4; i5++) {
          int i6 = paramByteSequence.readInt();
          arrayOfInt[i5] = i3 + paramByteSequence.readInt();
          stringBuffer.append("<TH>" + i6 + "</TH>");
        } 
        stringBuffer.append("<TH>default</TH></TR>\n<TR>");
        for (i5 = 0; i5 < i4; i5++)
          stringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + arrayOfInt[i5] + "\">" + arrayOfInt[i5] + "</A></TD>"); 
        stringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + i + "\">" + i + "</A></TD></TR>\n</TABLE>\n");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 167:
      case 168:
      case 198:
      case 199:
        m = paramByteSequence.getIndex() + paramByteSequence.readShort() - 1;
        stringBuffer.append("<A HREF=\"#code" + paramInt + "@" + m + "\">" + m + "</A>");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 200:
      case 201:
        i5 = paramByteSequence.getIndex() + paramByteSequence.readInt() - 1;
        stringBuffer.append("<A HREF=\"#code" + paramInt + "@" + i5 + "\">" + i5 + "</A>");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 169:
        if (wide) {
          i1 = paramByteSequence.readShort();
          wide = false;
        } else {
          i1 = paramByteSequence.readUnsignedByte();
        } 
        stringBuffer.append("%" + i1);
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 196:
        wide = true;
        stringBuffer.append("(wide)");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 188:
        stringBuffer.append("<FONT COLOR=\"#00FF00\">" + TYPE_NAMES[paramByteSequence.readByte()] + "</FONT>");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 178:
      case 179:
      case 180:
      case 181:
        m = paramByteSequence.readShort();
        constantFieldref = (ConstantFieldref)this.constant_pool.getConstant(m, (byte)9);
        n = constantFieldref.getClassIndex();
        str1 = this.constant_pool.getConstantString(n, (byte)7);
        str1 = Utility.compactClassName(str1, false);
        m = constantFieldref.getNameAndTypeIndex();
        str3 = this.constant_pool.constantToString(m, (byte)12);
        if (str1.equals(this.class_name)) {
          stringBuffer.append("<A HREF=\"" + this.class_name + "_methods.html#field" + str3 + "\" TARGET=Methods>" + str3 + "</A>\n");
        } else {
          stringBuffer.append(this.constant_html.referenceConstant(n) + "." + str3);
        } 
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 187:
      case 192:
      case 193:
        m = paramByteSequence.readShort();
        stringBuffer.append(this.constant_html.referenceConstant(m));
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 182:
      case 183:
      case 184:
      case 185:
        s2 = paramByteSequence.readShort();
        if (s1 == 185) {
          int i6 = paramByteSequence.readUnsignedByte();
          int i7 = paramByteSequence.readUnsignedByte();
          ConstantInterfaceMethodref constantInterfaceMethodref = (ConstantInterfaceMethodref)this.constant_pool.getConstant(s2, (byte)11);
          n = constantInterfaceMethodref.getClassIndex();
          String str = this.constant_pool.constantToString(constantInterfaceMethodref);
          m = constantInterfaceMethodref.getNameAndTypeIndex();
        } else {
          ConstantMethodref constantMethodref = (ConstantMethodref)this.constant_pool.getConstant(s2, (byte)10);
          n = constantMethodref.getClassIndex();
          String str = this.constant_pool.constantToString(constantMethodref);
          m = constantMethodref.getNameAndTypeIndex();
        } 
        str1 = Class2HTML.referenceClass(n);
        str4 = Class2HTML.toHTML(this.constant_pool.constantToString(this.constant_pool.getConstant(m, (byte)12)));
        constantNameAndType = (ConstantNameAndType)this.constant_pool.getConstant(m, (byte)12);
        str2 = this.constant_pool.constantToString(constantNameAndType.getSignatureIndex(), (byte)1);
        arrayOfString = Utility.methodSignatureArgumentTypes(str2, false);
        str5 = Utility.methodSignatureReturnType(str2, false);
        stringBuffer.append(str1 + ".<A HREF=\"" + this.class_name + "_cp.html#cp" + s2 + "\" TARGET=ConstantPool>" + str4 + "</A>(");
        for (b1 = 0; b1 < arrayOfString.length; b1++) {
          stringBuffer.append(Class2HTML.referenceType(arrayOfString[b1]));
          if (b1 < arrayOfString.length - 1)
            stringBuffer.append(", "); 
        } 
        stringBuffer.append("):" + Class2HTML.referenceType(str5));
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 19:
      case 20:
        m = paramByteSequence.readShort();
        stringBuffer.append("<A HREF=\"" + this.class_name + "_cp.html#cp" + m + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(this.constant_pool.constantToString(m, this.constant_pool.getConstant(m).getTag())) + "</a>");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 18:
        m = paramByteSequence.readUnsignedByte();
        stringBuffer.append("<A HREF=\"" + this.class_name + "_cp.html#cp" + m + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(this.constant_pool.constantToString(m, this.constant_pool.getConstant(m).getTag())) + "</a>");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 189:
        m = paramByteSequence.readShort();
        stringBuffer.append(this.constant_html.referenceConstant(m));
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 197:
        m = paramByteSequence.readShort();
        b1 = paramByteSequence.readByte();
        stringBuffer.append(this.constant_html.referenceConstant(m) + ":" + b1 + "-dimensional");
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
      case 132:
        if (wide) {
          i1 = paramByteSequence.readShort();
          b = paramByteSequence.readShort();
          wide = false;
        } else {
          i1 = paramByteSequence.readUnsignedByte();
          b = paramByteSequence.readByte();
        } 
        stringBuffer.append("%" + i1 + " " + b);
        stringBuffer.append("</TD>");
        return stringBuffer.toString();
    } 
    if (NO_OF_OPERANDS[s1] > 0)
      for (byte b2 = 0; b2 < TYPE_OF_OPERANDS[s1].length; b2++) {
        switch (TYPE_OF_OPERANDS[s1][b2]) {
          case 8:
            stringBuffer.append(paramByteSequence.readUnsignedByte());
            break;
          case 9:
            stringBuffer.append(paramByteSequence.readShort());
            break;
          case 10:
            stringBuffer.append(paramByteSequence.readInt());
            break;
          default:
            System.err.println("Unreachable default case reached!");
            System.exit(-1);
            break;
        } 
        stringBuffer.append("&nbsp;");
      }  
    stringBuffer.append("</TD>");
    return stringBuffer.toString();
  }
  
  private final void findGotos(ByteSequence paramByteSequence, Method paramMethod, Code paramCode) throws IOException {
    this.goto_set = new BitSet(paramByteSequence.available());
    if (paramCode != null) {
      CodeException[] arrayOfCodeException = paramCode.getExceptionTable();
      int i = arrayOfCodeException.length;
      for (byte b1 = 0; b1 < i; b1++) {
        this.goto_set.set(arrayOfCodeException[b1].getStartPC());
        this.goto_set.set(arrayOfCodeException[b1].getEndPC());
        this.goto_set.set(arrayOfCodeException[b1].getHandlerPC());
      } 
      Attribute[] arrayOfAttribute = paramCode.getAttributes();
      for (byte b2 = 0; b2 < arrayOfAttribute.length; b2++) {
        if (arrayOfAttribute[b2].getTag() == 5) {
          LocalVariable[] arrayOfLocalVariable = ((LocalVariableTable)arrayOfAttribute[b2]).getLocalVariableTable();
          for (byte b3 = 0; b3 < arrayOfLocalVariable.length; b3++) {
            int j = arrayOfLocalVariable[b3].getStartPC();
            int k = j + arrayOfLocalVariable[b3].getLength();
            this.goto_set.set(j);
            this.goto_set.set(k);
          } 
          break;
        } 
      } 
    } 
    for (byte b = 0; paramByteSequence.available() > 0; b++) {
      byte b1;
      int i2;
      int i1;
      int n;
      int m;
      int k;
      int i;
      int j = paramByteSequence.readUnsignedByte();
      switch (j) {
        case 170:
        case 171:
          k = paramByteSequence.getIndex() % 4;
          m = (k == 0) ? 0 : (4 - k);
          for (i2 = 0; i2 < m; i2++)
            paramByteSequence.readByte(); 
          n = paramByteSequence.readInt();
          if (j == 170) {
            i2 = paramByteSequence.readInt();
            int i4 = paramByteSequence.readInt();
            int i3 = paramByteSequence.getIndex() - 12 - m - 1;
            n += i3;
            this.goto_set.set(n);
            for (byte b2 = 0; b2 < i4 - i2 + 1; b2++) {
              int i5 = i3 + paramByteSequence.readInt();
              this.goto_set.set(i5);
            } 
            break;
          } 
          i2 = paramByteSequence.readInt();
          i1 = paramByteSequence.getIndex() - 8 - m - 1;
          n += i1;
          this.goto_set.set(n);
          for (b1 = 0; b1 < i2; b1++) {
            int i4 = paramByteSequence.readInt();
            int i3 = i1 + paramByteSequence.readInt();
            this.goto_set.set(i3);
          } 
          break;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 167:
        case 168:
        case 198:
        case 199:
          i = paramByteSequence.getIndex() + paramByteSequence.readShort() - 1;
          this.goto_set.set(i);
          break;
        case 200:
        case 201:
          i = paramByteSequence.getIndex() + paramByteSequence.readInt() - 1;
          this.goto_set.set(i);
          break;
        default:
          paramByteSequence.unreadByte();
          codeToHTML(paramByteSequence, 0);
          break;
      } 
    } 
  }
  
  private void writeMethod(Method paramMethod, int paramInt) throws IOException {
    String str1 = paramMethod.getSignature();
    String[] arrayOfString = Utility.methodSignatureArgumentTypes(str1, false);
    String str2 = Utility.methodSignatureReturnType(str1, false);
    String str3 = paramMethod.getName();
    String str4 = Class2HTML.toHTML(str3);
    String str5 = Utility.accessToString(paramMethod.getAccessFlags());
    str5 = Utility.replace(str5, " ", "&nbsp;");
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    this.file.print("<P><B><FONT COLOR=\"#FF0000\">" + str5 + "</FONT>&nbsp;<A NAME=method" + paramInt + ">" + Class2HTML.referenceType(str2) + "</A>&nbsp<A HREF=\"" + this.class_name + "_methods.html#method" + paramInt + "\" TARGET=Methods>" + str4 + "</A>(");
    for (byte b = 0; b < arrayOfString.length; b++) {
      this.file.print(Class2HTML.referenceType(arrayOfString[b]));
      if (b < arrayOfString.length - 1)
        this.file.print(",&nbsp;"); 
    } 
    this.file.println(")</B></P>");
    Code code = null;
    byte[] arrayOfByte = null;
    if (arrayOfAttribute.length > 0) {
      this.file.print("<H4>Attributes</H4><UL>\n");
      for (byte b1 = 0; b1 < arrayOfAttribute.length; b1++) {
        byte b2 = arrayOfAttribute[b1].getTag();
        if (b2 != -1) {
          this.file.print("<LI><A HREF=\"" + this.class_name + "_attributes.html#method" + paramInt + "@" + b1 + "\" TARGET=Attributes>" + ATTRIBUTE_NAMES[b2] + "</A></LI>\n");
        } else {
          this.file.print("<LI>" + arrayOfAttribute[b1] + "</LI>");
        } 
        if (b2 == 2) {
          code = (Code)arrayOfAttribute[b1];
          Attribute[] arrayOfAttribute1 = code.getAttributes();
          arrayOfByte = code.getCode();
          this.file.print("<UL>");
          for (byte b3 = 0; b3 < arrayOfAttribute1.length; b3++) {
            b2 = arrayOfAttribute1[b3].getTag();
            this.file.print("<LI><A HREF=\"" + this.class_name + "_attributes.html#method" + paramInt + "@" + b1 + "@" + b3 + "\" TARGET=Attributes>" + ATTRIBUTE_NAMES[b2] + "</A></LI>\n");
          } 
          this.file.print("</UL>");
        } 
      } 
      this.file.println("</UL>");
    } 
    if (arrayOfByte != null) {
      ByteSequence byteSequence = new ByteSequence(arrayOfByte);
      byteSequence.mark(byteSequence.available());
      findGotos(byteSequence, paramMethod, code);
      byteSequence.reset();
      this.file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Byte<BR>offset</TH><TH ALIGN=LEFT>Instruction</TH><TH ALIGN=LEFT>Argument</TH>");
      for (byte b1 = 0; byteSequence.available() > 0; b1++) {
        String str8;
        int i = byteSequence.getIndex();
        String str6 = codeToHTML(byteSequence, paramInt);
        String str7 = "";
        if (this.goto_set.get(i))
          str7 = "<A NAME=code" + paramInt + "@" + i + "></A>"; 
        if (byteSequence.getIndex() == arrayOfByte.length) {
          str8 = "<A NAME=code" + paramInt + "@" + arrayOfByte.length + ">" + i + "</A>";
        } else {
          str8 = "" + i;
        } 
        this.file.println("<TR VALIGN=TOP><TD>" + str8 + "</TD><TD>" + str7 + str6 + "</TR>");
      } 
      this.file.println("<TR><TD> </A></TD></TR>");
      this.file.println("</TABLE>");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\CodeHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */