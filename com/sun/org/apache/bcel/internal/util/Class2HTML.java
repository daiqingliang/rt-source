package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Class2HTML implements Constants {
  private JavaClass java_class;
  
  private String dir;
  
  private static String class_package;
  
  private static String class_name;
  
  private static ConstantPool constant_pool;
  
  public Class2HTML(JavaClass paramJavaClass, String paramString) throws IOException {
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    this.java_class = paramJavaClass;
    this.dir = paramString;
    class_name = paramJavaClass.getClassName();
    constant_pool = paramJavaClass.getConstantPool();
    int i = class_name.lastIndexOf('.');
    if (i > -1) {
      class_package = class_name.substring(0, i);
    } else {
      class_package = "";
    } 
    ConstantHTML constantHTML = new ConstantHTML(paramString, class_name, class_package, arrayOfMethod, constant_pool);
    AttributeHTML attributeHTML = new AttributeHTML(paramString, class_name, constant_pool, constantHTML);
    MethodHTML methodHTML = new MethodHTML(paramString, class_name, arrayOfMethod, paramJavaClass.getFields(), constantHTML, attributeHTML);
    writeMainHTML(attributeHTML);
    new CodeHTML(paramString, class_name, arrayOfMethod, constant_pool, constantHTML);
    attributeHTML.close();
  }
  
  public static void _main(String[] paramArrayOfString) {
    String[] arrayOfString = new String[paramArrayOfString.length];
    byte b = 0;
    ClassParser classParser = null;
    JavaClass javaClass = null;
    String str1 = null;
    char c = SecuritySupport.getSystemProperty("file.separator").toCharArray()[0];
    String str2 = "." + c;
    try {
      byte b1;
      for (b1 = 0; b1 < paramArrayOfString.length; b1++) {
        if (paramArrayOfString[b1].charAt(0) == '-') {
          if (paramArrayOfString[b1].equals("-d")) {
            str2 = paramArrayOfString[++b1];
            if (!str2.endsWith("" + c))
              str2 = str2 + c; 
            (new File(str2)).mkdirs();
          } else if (paramArrayOfString[b1].equals("-zip")) {
            str1 = paramArrayOfString[++b1];
          } else {
            System.out.println("Unknown option " + paramArrayOfString[b1]);
          } 
        } else {
          arrayOfString[b++] = paramArrayOfString[b1];
        } 
      } 
      if (b == 0) {
        System.err.println("Class2HTML: No input files specified.");
      } else {
        for (b1 = 0; b1 < b; b1++) {
          System.out.print("Processing " + arrayOfString[b1] + "...");
          if (str1 == null) {
            classParser = new ClassParser(arrayOfString[b1]);
          } else {
            classParser = new ClassParser(str1, arrayOfString[b1]);
          } 
          javaClass = classParser.parse();
          new Class2HTML(javaClass, str2);
          System.out.println("Done.");
        } 
      } 
    } catch (Exception exception) {
      System.out.println(exception);
      exception.printStackTrace(System.out);
    } 
  }
  
  static String referenceClass(int paramInt) {
    String str = constant_pool.getConstantString(paramInt, (byte)7);
    str = Utility.compactClassName(str);
    str = Utility.compactClassName(str, class_package + ".", true);
    return "<A HREF=\"" + class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str + "</A>";
  }
  
  static final String referenceType(String paramString) {
    String str = Utility.compactClassName(paramString);
    str = Utility.compactClassName(str, class_package + ".", true);
    int i = paramString.indexOf('[');
    if (i > -1)
      paramString = paramString.substring(0, i); 
    return (paramString.equals("int") || paramString.equals("short") || paramString.equals("boolean") || paramString.equals("void") || paramString.equals("char") || paramString.equals("byte") || paramString.equals("long") || paramString.equals("double") || paramString.equals("float")) ? ("<FONT COLOR=\"#00FF00\">" + paramString + "</FONT>") : ("<A HREF=\"" + paramString + ".html\" TARGET=_top>" + str + "</A>");
  }
  
  static String toHTML(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      for (byte b = 0; b < paramString.length(); b++) {
        char c;
        switch (c = paramString.charAt(b)) {
          case '<':
            stringBuffer.append("&lt;");
            break;
          case '>':
            stringBuffer.append("&gt;");
            break;
          case '\n':
            stringBuffer.append("\\n");
            break;
          case '\r':
            stringBuffer.append("\\r");
            break;
          default:
            stringBuffer.append(c);
            break;
        } 
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
    return stringBuffer.toString();
  }
  
  private void writeMainHTML(AttributeHTML paramAttributeHTML) throws IOException {
    PrintWriter printWriter = new PrintWriter(new FileOutputStream(this.dir + class_name + ".html"));
    Attribute[] arrayOfAttribute = this.java_class.getAttributes();
    printWriter.println("<HTML>\n<HEAD><TITLE>Documentation for " + class_name + "</TITLE></HEAD>\n<FRAMESET BORDER=1 cols=\"30%,*\">\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"ConstantPool\" SRC=\"" + class_name + "_cp.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n<FRAME NAME=\"Attributes\" SRC=\"" + class_name + "_attributes.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n</FRAMESET>\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"Code\" SRC=\"" + class_name + "_code.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n<FRAME NAME=\"Methods\" SRC=\"" + class_name + "_methods.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n</FRAMESET></FRAMESET></HTML>");
    printWriter.close();
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      paramAttributeHTML.writeAttribute(arrayOfAttribute[b], "class" + b); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\Class2HTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */