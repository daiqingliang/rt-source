package com.sun.org.apache.bcel.internal.classfile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ClassParser {
  private DataInputStream file;
  
  private ZipFile zip;
  
  private String file_name;
  
  private int class_name_index;
  
  private int superclass_name_index;
  
  private int major;
  
  private int minor;
  
  private int access_flags;
  
  private int[] interfaces;
  
  private ConstantPool constant_pool;
  
  private Field[] fields;
  
  private Method[] methods;
  
  private Attribute[] attributes;
  
  private boolean is_zip;
  
  private static final int BUFSIZE = 8192;
  
  public ClassParser(InputStream paramInputStream, String paramString) {
    this.file_name = paramString;
    String str = paramInputStream.getClass().getName();
    this.is_zip = (str.startsWith("java.util.zip.") || str.startsWith("java.util.jar."));
    if (paramInputStream instanceof DataInputStream) {
      this.file = (DataInputStream)paramInputStream;
    } else {
      this.file = new DataInputStream(new BufferedInputStream(paramInputStream, 8192));
    } 
  }
  
  public ClassParser(String paramString) throws IOException {
    this.is_zip = false;
    this.file_name = paramString;
    this.file = new DataInputStream(new BufferedInputStream(new FileInputStream(paramString), 8192));
  }
  
  public ClassParser(String paramString1, String paramString2) throws IOException {
    this.is_zip = true;
    this.zip = new ZipFile(paramString1);
    ZipEntry zipEntry = this.zip.getEntry(paramString2);
    this.file_name = paramString2;
    this.file = new DataInputStream(new BufferedInputStream(this.zip.getInputStream(zipEntry), 8192));
  }
  
  public JavaClass parse() throws IOException, ClassFormatException {
    readID();
    readVersion();
    readConstantPool();
    readClassInfo();
    readInterfaces();
    readFields();
    readMethods();
    readAttributes();
    this.file.close();
    if (this.zip != null)
      this.zip.close(); 
    return new JavaClass(this.class_name_index, this.superclass_name_index, this.file_name, this.major, this.minor, this.access_flags, this.constant_pool, this.interfaces, this.fields, this.methods, this.attributes, this.is_zip ? 3 : 2);
  }
  
  private final void readAttributes() throws IOException, ClassFormatException {
    int i = this.file.readUnsignedShort();
    this.attributes = new Attribute[i];
    for (byte b = 0; b < i; b++)
      this.attributes[b] = Attribute.readAttribute(this.file, this.constant_pool); 
  }
  
  private final void readClassInfo() throws IOException, ClassFormatException {
    this.access_flags = this.file.readUnsignedShort();
    if ((this.access_flags & 0x200) != 0)
      this.access_flags |= 0x400; 
    if ((this.access_flags & 0x400) != 0 && (this.access_flags & 0x10) != 0)
      throw new ClassFormatException("Class can't be both final and abstract"); 
    this.class_name_index = this.file.readUnsignedShort();
    this.superclass_name_index = this.file.readUnsignedShort();
  }
  
  private final void readConstantPool() throws IOException, ClassFormatException { this.constant_pool = new ConstantPool(this.file); }
  
  private final void readFields() throws IOException, ClassFormatException {
    int i = this.file.readUnsignedShort();
    this.fields = new Field[i];
    for (byte b = 0; b < i; b++)
      this.fields[b] = new Field(this.file, this.constant_pool); 
  }
  
  private final void readID() throws IOException, ClassFormatException {
    int i = -889275714;
    if (this.file.readInt() != i)
      throw new ClassFormatException(this.file_name + " is not a Java .class file"); 
  }
  
  private final void readInterfaces() throws IOException, ClassFormatException {
    int i = this.file.readUnsignedShort();
    this.interfaces = new int[i];
    for (byte b = 0; b < i; b++)
      this.interfaces[b] = this.file.readUnsignedShort(); 
  }
  
  private final void readMethods() throws IOException, ClassFormatException {
    int i = this.file.readUnsignedShort();
    this.methods = new Method[i];
    for (byte b = 0; b < i; b++)
      this.methods[b] = new Method(this.file, this.constant_pool); 
  }
  
  private final void readVersion() throws IOException, ClassFormatException {
    this.minor = this.file.readUnsignedShort();
    this.major = this.file.readUnsignedShort();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ClassParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */