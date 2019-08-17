package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class ClassWriter {
  int verbose;
  
  Package pkg;
  
  Package.Class cls;
  
  DataOutputStream out;
  
  ConstantPool.Index cpIndex;
  
  ConstantPool.Index bsmIndex;
  
  ByteArrayOutputStream buf = new ByteArrayOutputStream();
  
  DataOutputStream bufOut = new DataOutputStream(this.buf);
  
  ClassWriter(Package.Class paramClass, OutputStream paramOutputStream) throws IOException {
    this.pkg = paramClass.getPackage();
    this.cls = paramClass;
    this.verbose = this.pkg.verbose;
    this.out = new DataOutputStream(new BufferedOutputStream(paramOutputStream));
    this.cpIndex = ConstantPool.makeIndex(paramClass.toString(), paramClass.getCPMap());
    this.cpIndex.flattenSigs = true;
    if (paramClass.hasBootstrapMethods())
      this.bsmIndex = ConstantPool.makeIndex(this.cpIndex.debugName + ".BootstrapMethods", paramClass.getBootstrapMethodMap()); 
    if (this.verbose > 1)
      Utils.log.fine("local CP=" + ((this.verbose > 2) ? this.cpIndex.dumpString() : this.cpIndex.toString())); 
  }
  
  private void writeShort(int paramInt) throws IOException { this.out.writeShort(paramInt); }
  
  private void writeInt(int paramInt) throws IOException { this.out.writeInt(paramInt); }
  
  private void writeRef(ConstantPool.Entry paramEntry) throws IOException { writeRef(paramEntry, this.cpIndex); }
  
  private void writeRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex) throws IOException {
    byte b = (paramEntry == null) ? 0 : paramIndex.indexOf(paramEntry);
    writeShort(b);
  }
  
  void write() throws IOException {
    bool = false;
    try {
      if (this.verbose > 1)
        Utils.log.fine("...writing " + this.cls); 
      writeMagicNumbers();
      writeConstantPool();
      writeHeader();
      writeMembers(false);
      writeMembers(true);
      writeAttributes(0, this.cls);
      this.out.flush();
      bool = true;
    } finally {
      if (!bool)
        Utils.log.warning("Error on output of " + this.cls); 
    } 
  }
  
  void writeMagicNumbers() throws IOException {
    writeInt(this.cls.magic);
    writeShort(this.cls.version.minor);
    writeShort(this.cls.version.major);
  }
  
  void writeConstantPool() throws IOException {
    ConstantPool.Entry[] arrayOfEntry = this.cls.cpMap;
    writeShort(arrayOfEntry.length);
    for (byte b = 0; b < arrayOfEntry.length; b++) {
      ConstantPool.Entry entry = arrayOfEntry[b];
      assert false;
      if (entry != null) {
        ConstantPool.MethodHandleEntry methodHandleEntry;
        double d;
        float f;
        byte b1 = entry.getTag();
        if (this.verbose > 2)
          Utils.log.fine("   CP[" + b + "] = " + entry); 
        this.out.write(b1);
        switch (b1) {
          case 13:
            throw new AssertionError("CP should have Signatures remapped to Utf8");
          case 1:
            this.out.writeUTF(entry.stringValue());
            break;
          case 3:
            this.out.writeInt(((ConstantPool.NumberEntry)entry).numberValue().intValue());
            break;
          case 4:
            f = ((ConstantPool.NumberEntry)entry).numberValue().floatValue();
            this.out.writeInt(Float.floatToRawIntBits(f));
            break;
          case 5:
            this.out.writeLong(((ConstantPool.NumberEntry)entry).numberValue().longValue());
            break;
          case 6:
            d = ((ConstantPool.NumberEntry)entry).numberValue().doubleValue();
            this.out.writeLong(Double.doubleToRawLongBits(d));
            break;
          case 7:
          case 8:
          case 16:
            writeRef(entry.getRef(0));
            break;
          case 15:
            methodHandleEntry = (ConstantPool.MethodHandleEntry)entry;
            this.out.writeByte(methodHandleEntry.refKind);
            writeRef(methodHandleEntry.getRef(0));
            break;
          case 9:
          case 10:
          case 11:
          case 12:
            writeRef(entry.getRef(0));
            writeRef(entry.getRef(1));
            break;
          case 18:
            writeRef(entry.getRef(0), this.bsmIndex);
            writeRef(entry.getRef(1));
            break;
          case 17:
            throw new AssertionError("CP should have BootstrapMethods moved to side-table");
          default:
            throw new IOException("Bad constant pool tag " + b1);
        } 
      } 
    } 
  }
  
  void writeHeader() throws IOException {
    writeShort(this.cls.flags);
    writeRef(this.cls.thisClass);
    writeRef(this.cls.superClass);
    writeShort(this.cls.interfaces.length);
    for (byte b = 0; b < this.cls.interfaces.length; b++)
      writeRef(this.cls.interfaces[b]); 
  }
  
  void writeMembers(boolean paramBoolean) throws IOException {
    List list;
    if (!paramBoolean) {
      list = this.cls.getFields();
    } else {
      list = this.cls.getMethods();
    } 
    writeShort(list.size());
    for (Package.Class.Member member : list)
      writeMember(member, paramBoolean); 
  }
  
  void writeMember(Package.Class.Member paramMember, boolean paramBoolean) throws IOException {
    if (this.verbose > 2)
      Utils.log.fine("writeMember " + paramMember); 
    writeShort(paramMember.flags);
    writeRef((paramMember.getDescriptor()).nameRef);
    writeRef((paramMember.getDescriptor()).typeRef);
    writeAttributes(!paramBoolean ? 1 : 2, paramMember);
  }
  
  private void reorderBSMandICS(Attribute.Holder paramHolder) {
    Attribute attribute1 = paramHolder.getAttribute(Package.attrBootstrapMethodsEmpty);
    if (attribute1 == null)
      return; 
    Attribute attribute2 = paramHolder.getAttribute(Package.attrInnerClassesEmpty);
    if (attribute2 == null)
      return; 
    int i = paramHolder.attributes.indexOf(attribute1);
    int j = paramHolder.attributes.indexOf(attribute2);
    if (i > j) {
      paramHolder.attributes.remove(attribute1);
      paramHolder.attributes.add(j, attribute1);
    } 
  }
  
  void writeAttributes(int paramInt, Attribute.Holder paramHolder) throws IOException {
    if (paramHolder.attributes == null) {
      writeShort(0);
      return;
    } 
    if (paramHolder instanceof Package.Class)
      reorderBSMandICS(paramHolder); 
    writeShort(paramHolder.attributes.size());
    for (Attribute attribute : paramHolder.attributes) {
      attribute.finishRefs(this.cpIndex);
      writeRef(attribute.getNameRef());
      if (attribute.layout() == Package.attrCodeEmpty || attribute.layout() == Package.attrBootstrapMethodsEmpty || attribute.layout() == Package.attrInnerClassesEmpty) {
        DataOutputStream dataOutputStream = this.out;
        assert this.out != this.bufOut;
        this.buf.reset();
        this.out = this.bufOut;
        if ("Code".equals(attribute.name())) {
          Package.Class.Method method = (Package.Class.Method)paramHolder;
          writeCode(method.code);
        } else if ("BootstrapMethods".equals(attribute.name())) {
          assert paramHolder == this.cls;
          writeBootstrapMethods(this.cls);
        } else if ("InnerClasses".equals(attribute.name())) {
          assert paramHolder == this.cls;
          writeInnerClasses(this.cls);
        } else {
          throw new AssertionError();
        } 
        this.out = dataOutputStream;
        if (this.verbose > 2)
          Utils.log.fine("Attribute " + attribute.name() + " [" + this.buf.size() + "]"); 
        writeInt(this.buf.size());
        this.buf.writeTo(this.out);
        continue;
      } 
      if (this.verbose > 2)
        Utils.log.fine("Attribute " + attribute.name() + " [" + attribute.size() + "]"); 
      writeInt(attribute.size());
      this.out.write(attribute.bytes());
    } 
  }
  
  void writeCode(Code paramCode) throws IOException {
    paramCode.finishRefs(this.cpIndex);
    writeShort(paramCode.max_stack);
    writeShort(paramCode.max_locals);
    writeInt(paramCode.bytes.length);
    this.out.write(paramCode.bytes);
    int i = paramCode.getHandlerCount();
    writeShort(i);
    for (byte b = 0; b < i; b++) {
      writeShort(paramCode.handler_start[b]);
      writeShort(paramCode.handler_end[b]);
      writeShort(paramCode.handler_catch[b]);
      writeRef(paramCode.handler_class[b]);
    } 
    writeAttributes(3, paramCode);
  }
  
  void writeBootstrapMethods(Package.Class paramClass) throws IOException {
    List list = paramClass.getBootstrapMethods();
    writeShort(list.size());
    for (ConstantPool.BootstrapMethodEntry bootstrapMethodEntry : list) {
      writeRef(bootstrapMethodEntry.bsmRef);
      writeShort(bootstrapMethodEntry.argRefs.length);
      for (ConstantPool.Entry entry : bootstrapMethodEntry.argRefs)
        writeRef(entry); 
    } 
  }
  
  void writeInnerClasses(Package.Class paramClass) throws IOException {
    List list = paramClass.getInnerClasses();
    writeShort(list.size());
    for (Package.InnerClass innerClass : list) {
      writeRef(innerClass.thisClass);
      writeRef(innerClass.outerClass);
      writeRef(innerClass.name);
      writeShort(innerClass.flags);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */