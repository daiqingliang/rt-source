package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.ClassQueue;
import com.sun.org.apache.bcel.internal.util.ClassVector;
import com.sun.org.apache.bcel.internal.util.Repository;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class JavaClass extends AccessFlags implements Cloneable, Node {
  private String file_name;
  
  private String package_name;
  
  private String source_file_name = "<Unknown>";
  
  private int class_name_index;
  
  private int superclass_name_index;
  
  private String class_name;
  
  private String superclass_name;
  
  private int major;
  
  private int minor;
  
  private ConstantPool constant_pool;
  
  private int[] interfaces;
  
  private String[] interface_names;
  
  private Field[] fields;
  
  private Method[] methods;
  
  private Attribute[] attributes;
  
  private byte source = 1;
  
  public static final byte HEAP = 1;
  
  public static final byte FILE = 2;
  
  public static final byte ZIP = 3;
  
  static boolean debug = false;
  
  static char sep = '/';
  
  private Repository repository = SyntheticRepository.getInstance();
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute, byte paramByte) {
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[0]; 
    if (paramArrayOfAttribute == null)
      this.attributes = new Attribute[0]; 
    if (paramArrayOfField == null)
      paramArrayOfField = new Field[0]; 
    if (paramArrayOfMethod == null)
      paramArrayOfMethod = new Method[0]; 
    this.class_name_index = paramInt1;
    this.superclass_name_index = paramInt2;
    this.file_name = paramString;
    this.major = paramInt3;
    this.minor = paramInt4;
    this.access_flags = paramInt5;
    this.constant_pool = paramConstantPool;
    this.interfaces = paramArrayOfInt;
    this.fields = paramArrayOfField;
    this.methods = paramArrayOfMethod;
    this.attributes = paramArrayOfAttribute;
    this.source = paramByte;
    int i;
    for (i = 0; i < paramArrayOfAttribute.length; i++) {
      if (paramArrayOfAttribute[i] instanceof SourceFile) {
        this.source_file_name = ((SourceFile)paramArrayOfAttribute[i]).getSourceFileName();
        break;
      } 
    } 
    this.class_name = paramConstantPool.getConstantString(paramInt1, (byte)7);
    this.class_name = Utility.compactClassName(this.class_name, false);
    i = this.class_name.lastIndexOf('.');
    if (i < 0) {
      this.package_name = "";
    } else {
      this.package_name = this.class_name.substring(0, i);
    } 
    if (paramInt2 > 0) {
      this.superclass_name = paramConstantPool.getConstantString(paramInt2, (byte)7);
      this.superclass_name = Utility.compactClassName(this.superclass_name, false);
    } else {
      this.superclass_name = "java.lang.Object";
    } 
    this.interface_names = new String[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      String str = paramConstantPool.getConstantString(paramArrayOfInt[b], (byte)7);
      this.interface_names[b] = Utility.compactClassName(str, false);
    } 
  }
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute) { this(paramInt1, paramInt2, paramString, paramInt3, paramInt4, paramInt5, paramConstantPool, paramArrayOfInt, paramArrayOfField, paramArrayOfMethod, paramArrayOfAttribute, (byte)1); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitJavaClass(this); }
  
  static final void Debug(String paramString) {
    if (debug)
      System.out.println(paramString); 
  }
  
  public void dump(File paramFile) throws IOException {
    String str = paramFile.getParent();
    if (str != null) {
      File file = new File(str);
      if (file != null)
        file.mkdirs(); 
    } 
    dump(new DataOutputStream(new FileOutputStream(paramFile)));
  }
  
  public void dump(String paramString) { dump(new File(paramString)); }
  
  public byte[] getBytes() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      dump(dataOutputStream);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } finally {
      try {
        dataOutputStream.close();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public void dump(OutputStream paramOutputStream) throws IOException { dump(new DataOutputStream(paramOutputStream)); }
  
  public void dump(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeInt(-889275714);
    paramDataOutputStream.writeShort(this.minor);
    paramDataOutputStream.writeShort(this.major);
    this.constant_pool.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.access_flags);
    paramDataOutputStream.writeShort(this.class_name_index);
    paramDataOutputStream.writeShort(this.superclass_name_index);
    paramDataOutputStream.writeShort(this.interfaces.length);
    byte b;
    for (b = 0; b < this.interfaces.length; b++)
      paramDataOutputStream.writeShort(this.interfaces[b]); 
    paramDataOutputStream.writeShort(this.fields.length);
    for (b = 0; b < this.fields.length; b++)
      this.fields[b].dump(paramDataOutputStream); 
    paramDataOutputStream.writeShort(this.methods.length);
    for (b = 0; b < this.methods.length; b++)
      this.methods[b].dump(paramDataOutputStream); 
    if (this.attributes != null) {
      paramDataOutputStream.writeShort(this.attributes.length);
      for (b = 0; b < this.attributes.length; b++)
        this.attributes[b].dump(paramDataOutputStream); 
    } else {
      paramDataOutputStream.writeShort(0);
    } 
    paramDataOutputStream.close();
  }
  
  public Attribute[] getAttributes() { return this.attributes; }
  
  public String getClassName() { return this.class_name; }
  
  public String getPackageName() { return this.package_name; }
  
  public int getClassNameIndex() { return this.class_name_index; }
  
  public ConstantPool getConstantPool() { return this.constant_pool; }
  
  public Field[] getFields() { return this.fields; }
  
  public String getFileName() { return this.file_name; }
  
  public String[] getInterfaceNames() { return this.interface_names; }
  
  public int[] getInterfaceIndices() { return this.interfaces; }
  
  public int getMajor() { return this.major; }
  
  public Method[] getMethods() { return this.methods; }
  
  public Method getMethod(Method paramMethod) {
    for (byte b = 0; b < this.methods.length; b++) {
      Method method = this.methods[b];
      if (paramMethod.getName().equals(method.getName()) && paramMethod.getModifiers() == method.getModifiers() && Type.getSignature(paramMethod).equals(method.getSignature()))
        return method; 
    } 
    return null;
  }
  
  public int getMinor() { return this.minor; }
  
  public String getSourceFileName() { return this.source_file_name; }
  
  public String getSuperclassName() { return this.superclass_name; }
  
  public int getSuperclassNameIndex() { return this.superclass_name_index; }
  
  public void setAttributes(Attribute[] paramArrayOfAttribute) { this.attributes = paramArrayOfAttribute; }
  
  public void setClassName(String paramString) { this.class_name = paramString; }
  
  public void setClassNameIndex(int paramInt) { this.class_name_index = paramInt; }
  
  public void setConstantPool(ConstantPool paramConstantPool) { this.constant_pool = paramConstantPool; }
  
  public void setFields(Field[] paramArrayOfField) { this.fields = paramArrayOfField; }
  
  public void setFileName(String paramString) { this.file_name = paramString; }
  
  public void setInterfaceNames(String[] paramArrayOfString) { this.interface_names = paramArrayOfString; }
  
  public void setInterfaces(int[] paramArrayOfInt) { this.interfaces = paramArrayOfInt; }
  
  public void setMajor(int paramInt) { this.major = paramInt; }
  
  public void setMethods(Method[] paramArrayOfMethod) { this.methods = paramArrayOfMethod; }
  
  public void setMinor(int paramInt) { this.minor = paramInt; }
  
  public void setSourceFileName(String paramString) { this.source_file_name = paramString; }
  
  public void setSuperclassName(String paramString) { this.superclass_name = paramString; }
  
  public void setSuperclassNameIndex(int paramInt) { this.superclass_name_index = paramInt; }
  
  public String toString() {
    String str = Utility.accessToString(this.access_flags, true);
    str = str.equals("") ? "" : (str + " ");
    StringBuffer stringBuffer = new StringBuffer(str + Utility.classOrInterface(this.access_flags) + " " + this.class_name + " extends " + Utility.compactClassName(this.superclass_name, false) + '\n');
    int i = this.interfaces.length;
    if (i > 0) {
      stringBuffer.append("implements\t\t");
      for (byte b = 0; b < i; b++) {
        stringBuffer.append(this.interface_names[b]);
        if (b < i - 1)
          stringBuffer.append(", "); 
      } 
      stringBuffer.append('\n');
    } 
    stringBuffer.append("filename\t\t" + this.file_name + '\n');
    stringBuffer.append("compiled from\t\t" + this.source_file_name + '\n');
    stringBuffer.append("compiler version\t" + this.major + "." + this.minor + '\n');
    stringBuffer.append("access flags\t\t" + this.access_flags + '\n');
    stringBuffer.append("constant pool\t\t" + this.constant_pool.getLength() + " entries\n");
    stringBuffer.append("ACC_SUPER flag\t\t" + isSuper() + "\n");
    if (this.attributes.length > 0) {
      stringBuffer.append("\nAttribute(s):\n");
      for (byte b = 0; b < this.attributes.length; b++)
        stringBuffer.append(indent(this.attributes[b])); 
    } 
    if (this.fields.length > 0) {
      stringBuffer.append("\n" + this.fields.length + " fields:\n");
      for (byte b = 0; b < this.fields.length; b++)
        stringBuffer.append("\t" + this.fields[b] + '\n'); 
    } 
    if (this.methods.length > 0) {
      stringBuffer.append("\n" + this.methods.length + " methods:\n");
      for (byte b = 0; b < this.methods.length; b++)
        stringBuffer.append("\t" + this.methods[b] + '\n'); 
    } 
    return stringBuffer.toString();
  }
  
  private static final String indent(Object paramObject) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramObject.toString(), "\n");
    StringBuffer stringBuffer = new StringBuffer();
    while (stringTokenizer.hasMoreTokens())
      stringBuffer.append("\t" + stringTokenizer.nextToken() + "\n"); 
    return stringBuffer.toString();
  }
  
  public JavaClass copy() {
    JavaClass javaClass = null;
    try {
      javaClass = (JavaClass)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    javaClass.constant_pool = this.constant_pool.copy();
    javaClass.interfaces = (int[])this.interfaces.clone();
    javaClass.interface_names = (String[])this.interface_names.clone();
    javaClass.fields = new Field[this.fields.length];
    byte b;
    for (b = 0; b < this.fields.length; b++)
      javaClass.fields[b] = this.fields[b].copy(javaClass.constant_pool); 
    javaClass.methods = new Method[this.methods.length];
    for (b = 0; b < this.methods.length; b++)
      javaClass.methods[b] = this.methods[b].copy(javaClass.constant_pool); 
    javaClass.attributes = new Attribute[this.attributes.length];
    for (b = 0; b < this.attributes.length; b++)
      javaClass.attributes[b] = this.attributes[b].copy(javaClass.constant_pool); 
    return javaClass;
  }
  
  public final boolean isSuper() { return ((this.access_flags & 0x20) != 0); }
  
  public final boolean isClass() { return ((this.access_flags & 0x200) == 0); }
  
  public final byte getSource() { return this.source; }
  
  public Repository getRepository() { return this.repository; }
  
  public void setRepository(Repository paramRepository) { this.repository = paramRepository; }
  
  public final boolean instanceOf(JavaClass paramJavaClass) {
    if (equals(paramJavaClass))
      return true; 
    JavaClass[] arrayOfJavaClass = getSuperClasses();
    for (byte b = 0; b < arrayOfJavaClass.length; b++) {
      if (arrayOfJavaClass[b].equals(paramJavaClass))
        return true; 
    } 
    return paramJavaClass.isInterface() ? implementationOf(paramJavaClass) : 0;
  }
  
  public boolean implementationOf(JavaClass paramJavaClass) {
    if (!paramJavaClass.isInterface())
      throw new IllegalArgumentException(paramJavaClass.getClassName() + " is no interface"); 
    if (equals(paramJavaClass))
      return true; 
    JavaClass[] arrayOfJavaClass = getAllInterfaces();
    for (byte b = 0; b < arrayOfJavaClass.length; b++) {
      if (arrayOfJavaClass[b].equals(paramJavaClass))
        return true; 
    } 
    return false;
  }
  
  public JavaClass getSuperClass() {
    if ("java.lang.Object".equals(getClassName()))
      return null; 
    try {
      return this.repository.loadClass(getSuperclassName());
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println(classNotFoundException);
      return null;
    } 
  }
  
  public JavaClass[] getSuperClasses() {
    JavaClass javaClass = this;
    ClassVector classVector = new ClassVector();
    for (javaClass = javaClass.getSuperClass(); javaClass != null; javaClass = javaClass.getSuperClass())
      classVector.addElement(javaClass); 
    return classVector.toArray();
  }
  
  public JavaClass[] getInterfaces() {
    String[] arrayOfString = getInterfaceNames();
    JavaClass[] arrayOfJavaClass = new JavaClass[arrayOfString.length];
    try {
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfJavaClass[b] = this.repository.loadClass(arrayOfString[b]); 
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println(classNotFoundException);
      return null;
    } 
    return arrayOfJavaClass;
  }
  
  public JavaClass[] getAllInterfaces() {
    ClassQueue classQueue = new ClassQueue();
    ClassVector classVector = new ClassVector();
    classQueue.enqueue(this);
    while (!classQueue.empty()) {
      JavaClass javaClass1 = classQueue.dequeue();
      JavaClass javaClass2 = javaClass1.getSuperClass();
      JavaClass[] arrayOfJavaClass = javaClass1.getInterfaces();
      if (javaClass1.isInterface()) {
        classVector.addElement(javaClass1);
      } else if (javaClass2 != null) {
        classQueue.enqueue(javaClass2);
      } 
      for (byte b = 0; b < arrayOfJavaClass.length; b++)
        classQueue.enqueue(arrayOfJavaClass[b]); 
    } 
    return classVector.toArray();
  }
  
  static  {
    String str1 = null;
    String str2 = null;
    try {
      str1 = SecuritySupport.getSystemProperty("JavaClass.debug");
      str2 = SecuritySupport.getSystemProperty("file.separator");
    } catch (SecurityException securityException) {}
    if (str1 != null)
      debug = (new Boolean(str1)).booleanValue(); 
    if (str2 != null)
      try {
        sep = str2.charAt(0);
      } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {} 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\JavaClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */