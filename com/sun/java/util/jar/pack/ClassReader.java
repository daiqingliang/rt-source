package com.sun.java.util.jar.pack;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class ClassReader {
  int verbose;
  
  Package pkg;
  
  Package.Class cls;
  
  long inPos;
  
  long constantPoolLimit = -1L;
  
  DataInputStream in;
  
  Map<Attribute.Layout, Attribute> attrDefs;
  
  Map<Attribute.Layout, String> attrCommands;
  
  String unknownAttrCommand = "error";
  
  boolean haveUnresolvedEntry;
  
  ClassReader(Package.Class paramClass, InputStream paramInputStream) throws IOException {
    this.pkg = paramClass.getPackage();
    this.cls = paramClass;
    this.verbose = this.pkg.verbose;
    this.in = new DataInputStream(new FilterInputStream(this, paramInputStream) {
          public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
            int i = super.read(param1ArrayOfByte, param1Int1, param1Int2);
            if (i >= 0)
              ClassReader.this.inPos += i; 
            return i;
          }
          
          public int read() throws IOException {
            int i = super.read();
            if (i >= 0)
              ClassReader.this.inPos++; 
            return i;
          }
          
          public long skip(long param1Long) throws IOException {
            long l = super.skip(param1Long);
            if (l >= 0L)
              ClassReader.this.inPos += l; 
            return l;
          }
        });
  }
  
  public void setAttrDefs(Map<Attribute.Layout, Attribute> paramMap) { this.attrDefs = paramMap; }
  
  public void setAttrCommands(Map<Attribute.Layout, String> paramMap) { this.attrCommands = paramMap; }
  
  private void skip(int paramInt, String paramString) throws IOException {
    Utils.log.warning("skipping " + paramInt + " bytes of " + paramString);
    long l;
    for (l = 0L; l < paramInt; l += l1) {
      long l1 = this.in.skip(paramInt - l);
      assert l1 > 0L;
    } 
    assert l == paramInt;
  }
  
  private int readUnsignedShort() throws IOException { return this.in.readUnsignedShort(); }
  
  private int readInt() throws IOException { return this.in.readInt(); }
  
  private ConstantPool.Entry readRef() throws IOException {
    int i = this.in.readUnsignedShort();
    return (i == 0) ? null : this.cls.cpMap[i];
  }
  
  private ConstantPool.Entry readRef(byte paramByte) throws IOException {
    ConstantPool.Entry entry = readRef();
    assert !(entry instanceof UnresolvedEntry);
    checkTag(entry, paramByte);
    return entry;
  }
  
  private ConstantPool.Entry checkTag(ConstantPool.Entry paramEntry, byte paramByte) throws ClassFormatException {
    if (paramEntry == null || !paramEntry.tagMatches(paramByte)) {
      String str1 = (this.inPos == this.constantPoolLimit) ? " in constant pool" : (" at pos: " + this.inPos);
      String str2 = (paramEntry == null) ? "null CP index" : ("type=" + ConstantPool.tagName(paramEntry.tag));
      throw new ClassFormatException("Bad constant, expected type=" + ConstantPool.tagName(paramByte) + " got " + str2 + ", in File: " + this.cls.file.nameString + str1);
    } 
    return paramEntry;
  }
  
  private ConstantPool.Entry checkTag(ConstantPool.Entry paramEntry, byte paramByte, boolean paramBoolean) throws ClassFormatException { return (paramBoolean && paramEntry == null) ? null : checkTag(paramEntry, paramByte); }
  
  private ConstantPool.Entry readRefOrNull(byte paramByte) throws IOException {
    ConstantPool.Entry entry = readRef();
    checkTag(entry, paramByte, true);
    return entry;
  }
  
  private ConstantPool.Utf8Entry readUtf8Ref() throws IOException { return (ConstantPool.Utf8Entry)readRef((byte)1); }
  
  private ConstantPool.ClassEntry readClassRef() throws IOException { return (ConstantPool.ClassEntry)readRef((byte)7); }
  
  private ConstantPool.ClassEntry readClassRefOrNull() throws IOException { return (ConstantPool.ClassEntry)readRefOrNull((byte)7); }
  
  private ConstantPool.SignatureEntry readSignatureRef() throws IOException {
    ConstantPool.Entry entry = readRef((byte)13);
    return (entry != null && entry.getTag() == 1) ? ConstantPool.getSignatureEntry(entry.stringValue()) : (ConstantPool.SignatureEntry)entry;
  }
  
  void read() throws IOException {
    bool = false;
    try {
      readMagicNumbers();
      readConstantPool();
      readHeader();
      readMembers(false);
      readMembers(true);
      readAttributes(0, this.cls);
      fixUnresolvedEntries();
      this.cls.finishReading();
      assert 0 >= this.in.read(new byte[1]);
      bool = true;
    } finally {
      if (!bool && this.verbose > 0)
        Utils.log.warning("Erroneous data at input offset " + this.inPos + " of " + this.cls.file); 
    } 
  }
  
  void readMagicNumbers() throws IOException {
    this.cls.magic = this.in.readInt();
    if (this.cls.magic != -889275714)
      throw new Attribute.FormatException("Bad magic number in class file " + Integer.toHexString(this.cls.magic), 0, "magic-number", "pass"); 
    short s1 = (short)readUnsignedShort();
    short s2 = (short)readUnsignedShort();
    this.cls.version = Package.Version.of(s2, s1);
    String str = checkVersion(this.cls.version);
    if (str != null)
      throw new Attribute.FormatException("classfile version too " + str + ": " + this.cls.version + " in " + this.cls.file, 0, "version", "pass"); 
  }
  
  private String checkVersion(Package.Version paramVersion) {
    short s1 = paramVersion.major;
    short s2 = paramVersion.minor;
    return (s1 < this.pkg.minClassVersion.major || (s1 == this.pkg.minClassVersion.major && s2 < this.pkg.minClassVersion.minor)) ? "small" : ((s1 > this.pkg.maxClassVersion.major || (s1 == this.pkg.maxClassVersion.major && s2 > this.pkg.maxClassVersion.minor)) ? "large" : null);
  }
  
  void readConstantPool() throws IOException {
    int i = this.in.readUnsignedShort();
    int[] arrayOfInt = new int[i * 4];
    byte b1 = 0;
    ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[i];
    arrayOfEntry[0] = null;
    byte b2;
    for (b2 = 1; b2 < i; b2++) {
      byte b = this.in.readByte();
      switch (b) {
        case 1:
          arrayOfEntry[b2] = ConstantPool.getUtf8Entry(this.in.readUTF());
          break;
        case 3:
          arrayOfEntry[b2] = ConstantPool.getLiteralEntry(Integer.valueOf(this.in.readInt()));
          break;
        case 4:
          arrayOfEntry[b2] = ConstantPool.getLiteralEntry(Float.valueOf(this.in.readFloat()));
          break;
        case 5:
          arrayOfEntry[b2] = ConstantPool.getLiteralEntry(Long.valueOf(this.in.readLong()));
          arrayOfEntry[++b2] = null;
          break;
        case 6:
          arrayOfEntry[b2] = ConstantPool.getLiteralEntry(Double.valueOf(this.in.readDouble()));
          arrayOfEntry[++b2] = null;
          break;
        case 7:
        case 8:
        case 16:
          arrayOfInt[b1++] = b2;
          arrayOfInt[b1++] = b;
          arrayOfInt[b1++] = this.in.readUnsignedShort();
          arrayOfInt[b1++] = -1;
          break;
        case 9:
        case 10:
        case 11:
        case 12:
          arrayOfInt[b1++] = b2;
          arrayOfInt[b1++] = b;
          arrayOfInt[b1++] = this.in.readUnsignedShort();
          arrayOfInt[b1++] = this.in.readUnsignedShort();
          break;
        case 18:
          arrayOfInt[b1++] = b2;
          arrayOfInt[b1++] = b;
          arrayOfInt[b1++] = 0xFFFFFFFF ^ this.in.readUnsignedShort();
          arrayOfInt[b1++] = this.in.readUnsignedShort();
          break;
        case 15:
          arrayOfInt[b1++] = b2;
          arrayOfInt[b1++] = b;
          arrayOfInt[b1++] = 0xFFFFFFFF ^ this.in.readUnsignedByte();
          arrayOfInt[b1++] = this.in.readUnsignedShort();
          break;
        default:
          throw new ClassFormatException("Bad constant pool tag " + b + " in File: " + this.cls.file.nameString + " at pos: " + this.inPos);
      } 
    } 
    this.constantPoolLimit = this.inPos;
    while (b1 > 0) {
      if (this.verbose > 3)
        Utils.log.fine("CP fixups [" + (b1 / 4) + "]"); 
      b2 = b1;
      b1 = 0;
      byte b = 0;
      while (b < b2) {
        ConstantPool.DescriptorEntry descriptorEntry2;
        ConstantPool.MemberEntry memberEntry;
        byte b3;
        ConstantPool.Utf8Entry utf8Entry2;
        ConstantPool.Utf8Entry utf8Entry1;
        ConstantPool.DescriptorEntry descriptorEntry1;
        ConstantPool.ClassEntry classEntry;
        int j = arrayOfInt[b++];
        int k = arrayOfInt[b++];
        int m = arrayOfInt[b++];
        int n = arrayOfInt[b++];
        if (this.verbose > 3)
          Utils.log.fine("  cp[" + j + "] = " + ConstantPool.tagName(k) + "{" + m + "," + n + "}"); 
        if ((m >= 0 && arrayOfEntry[m] == null) || (n >= 0 && arrayOfEntry[n] == null)) {
          arrayOfInt[b1++] = j;
          arrayOfInt[b1++] = k;
          arrayOfInt[b1++] = m;
          arrayOfInt[b1++] = n;
          continue;
        } 
        switch (k) {
          case 7:
            arrayOfEntry[j] = ConstantPool.getClassEntry(arrayOfEntry[m].stringValue());
            continue;
          case 8:
            arrayOfEntry[j] = ConstantPool.getStringEntry(arrayOfEntry[m].stringValue());
            continue;
          case 9:
          case 10:
          case 11:
            classEntry = (ConstantPool.ClassEntry)checkTag(arrayOfEntry[m], (byte)7);
            descriptorEntry1 = (ConstantPool.DescriptorEntry)checkTag(arrayOfEntry[n], (byte)12);
            arrayOfEntry[j] = ConstantPool.getMemberEntry((byte)k, classEntry, descriptorEntry1);
            continue;
          case 12:
            utf8Entry1 = (ConstantPool.Utf8Entry)checkTag(arrayOfEntry[m], (byte)1);
            utf8Entry2 = (ConstantPool.Utf8Entry)checkTag(arrayOfEntry[n], (byte)13);
            arrayOfEntry[j] = ConstantPool.getDescriptorEntry(utf8Entry1, utf8Entry2);
            continue;
          case 16:
            arrayOfEntry[j] = ConstantPool.getMethodTypeEntry((ConstantPool.Utf8Entry)checkTag(arrayOfEntry[m], (byte)13));
            continue;
          case 15:
            b3 = (byte)(0xFFFFFFFF ^ m);
            memberEntry = (ConstantPool.MemberEntry)checkTag(arrayOfEntry[n], (byte)52);
            arrayOfEntry[j] = ConstantPool.getMethodHandleEntry(b3, memberEntry);
            continue;
          case 18:
            descriptorEntry2 = (ConstantPool.DescriptorEntry)checkTag(arrayOfEntry[n], (byte)12);
            arrayOfEntry[j] = new UnresolvedEntry((byte)k, new Object[] { Integer.valueOf(0xFFFFFFFF ^ m), descriptorEntry2 });
            continue;
        } 
        assert false;
      } 
      assert b1 < b2;
    } 
    this.cls.cpMap = arrayOfEntry;
  }
  
  private void fixUnresolvedEntries() throws IOException {
    if (!this.haveUnresolvedEntry)
      return; 
    ConstantPool.Entry[] arrayOfEntry = this.cls.getCPMap();
    for (byte b = 0; b < arrayOfEntry.length; b++) {
      ConstantPool.Entry entry = arrayOfEntry[b];
      if (entry instanceof UnresolvedEntry) {
        arrayOfEntry[b] = entry = ((UnresolvedEntry)entry).resolve();
        assert !(entry instanceof UnresolvedEntry);
      } 
    } 
    this.haveUnresolvedEntry = false;
  }
  
  void readHeader() throws IOException {
    this.cls.flags = readUnsignedShort();
    this.cls.thisClass = readClassRef();
    this.cls.superClass = readClassRefOrNull();
    int i = readUnsignedShort();
    this.cls.interfaces = new ConstantPool.ClassEntry[i];
    for (byte b = 0; b < i; b++)
      this.cls.interfaces[b] = readClassRef(); 
  }
  
  void readMembers(boolean paramBoolean) throws IOException {
    int i = readUnsignedShort();
    for (byte b = 0; b < i; b++)
      readMember(paramBoolean); 
  }
  
  void readMember(boolean paramBoolean) throws IOException {
    Package.Class.Method method;
    int i = readUnsignedShort();
    ConstantPool.Utf8Entry utf8Entry = readUtf8Ref();
    ConstantPool.SignatureEntry signatureEntry = readSignatureRef();
    ConstantPool.DescriptorEntry descriptorEntry = ConstantPool.getDescriptorEntry(utf8Entry, signatureEntry);
    if (!paramBoolean) {
      this.cls.getClass();
      method = new Package.Class.Field(this.cls, i, descriptorEntry);
    } else {
      this.cls.getClass();
      method = new Package.Class.Method(this.cls, i, descriptorEntry);
    } 
    readAttributes(!paramBoolean ? 1 : 2, method);
  }
  
  void readAttributes(int paramInt, Attribute.Holder paramHolder) throws IOException {
    int i = readUnsignedShort();
    if (i == 0)
      return; 
    if (this.verbose > 3)
      Utils.log.fine("readAttributes " + paramHolder + " [" + i + "]"); 
    byte b = 0;
    while (b < i) {
      String str = readUtf8Ref().stringValue();
      int j = readInt();
      if (this.attrCommands != null) {
        Attribute.Layout layout = Attribute.keyForLookup(paramInt, str);
        String str1 = (String)this.attrCommands.get(layout);
        if (str1 != null) {
          String str3;
          long l;
          String str2;
          boolean bool;
          Attribute attribute;
          switch (str1) {
            case "pass":
              str3 = "passing attribute bitwise in " + paramHolder;
              throw new Attribute.FormatException(str3, paramInt, str, str1);
            case "error":
              str4 = "attribute not allowed in " + paramHolder;
              throw new Attribute.FormatException(str4, paramInt, str, str1);
            case "strip":
              skip(j, str + " attribute in " + paramHolder);
              break;
            default:
              attribute = Attribute.lookup(Package.attrDefs, paramInt, str);
              if (this.verbose > 4 && attribute != null)
                Utils.log.fine("pkg_attribute_lookup " + str + " = " + attribute); 
              if (attribute == null) {
                attribute = Attribute.lookup(this.attrDefs, paramInt, str);
                if (this.verbose > 4 && attribute != null)
                  Utils.log.fine("this " + str + " = " + attribute); 
              } 
              if (attribute == null) {
                attribute = Attribute.lookup(null, paramInt, str);
                if (this.verbose > 4 && attribute != null)
                  Utils.log.fine("null_attribute_lookup " + str + " = " + attribute); 
              } 
              if (attribute == null && j == 0)
                attribute = Attribute.find(paramInt, str, ""); 
              bool = (paramInt == 3 && (str.equals("StackMap") || str.equals("StackMapX"))) ? 1 : 0;
              if (bool) {
                Code code = (Code)paramHolder;
                if (code.max_stack >= 65536 || code.max_locals >= 65536 || code.getLength() >= 65536 || str.endsWith("X"))
                  attribute = null; 
              } 
              if (attribute == null) {
                if (bool) {
                  str2 = "unsupported StackMap variant in " + paramHolder;
                  throw new Attribute.FormatException(str2, paramInt, str, "pass");
                } 
                if ("strip".equals(this.unknownAttrCommand)) {
                  skip(j, "unknown " + str + " attribute in " + paramHolder);
                  break;
                } 
                str2 = " is unknown attribute in class " + paramHolder;
                throw new Attribute.FormatException(str2, paramInt, str, this.unknownAttrCommand);
              } 
              l = this.inPos;
              if (attribute.layout() == Package.attrCodeEmpty) {
                Package.Class.Method method = (Package.Class.Method)paramHolder;
                method.code = new Code(method);
                try {
                  readCode(method.code);
                } catch (FormatException str4) {
                  String str5 = str4.getMessage() + " in " + paramHolder;
                  throw new ClassFormatException(str5, str4);
                } 
                assert j == this.inPos - l;
              } else {
                if (attribute.layout() == Package.attrBootstrapMethodsEmpty) {
                  assert paramHolder == this.cls;
                  readBootstrapMethods(this.cls);
                  assert j == this.inPos - l;
                  break;
                } 
                if (attribute.layout() == Package.attrInnerClassesEmpty) {
                  assert paramHolder == this.cls;
                  readInnerClasses(this.cls);
                  assert j == this.inPos - l;
                } else if (j > 0) {
                  byte[] arrayOfByte = new byte[j];
                  this.in.readFully(arrayOfByte);
                  attribute = attribute.addContent(arrayOfByte);
                } 
              } 
              if (attribute.size() == 0 && !attribute.layout().isEmpty())
                throw new ClassFormatException(str + ": attribute length cannot be zero, in " + paramHolder); 
              paramHolder.addAttribute(attribute);
              if (this.verbose > 2)
                Utils.log.fine("read " + attribute); 
              break;
          } 
          b++;
        } 
      } 
    } 
  }
  
  void readCode(Code paramCode) throws IOException {
    paramCode.max_stack = readUnsignedShort();
    paramCode.max_locals = readUnsignedShort();
    paramCode.bytes = new byte[readInt()];
    this.in.readFully(paramCode.bytes);
    ConstantPool.Entry[] arrayOfEntry = this.cls.getCPMap();
    Instruction.opcodeChecker(paramCode.bytes, arrayOfEntry, this.cls.version);
    int i = readUnsignedShort();
    paramCode.setHandlerCount(i);
    for (byte b = 0; b < i; b++) {
      paramCode.handler_start[b] = readUnsignedShort();
      paramCode.handler_end[b] = readUnsignedShort();
      paramCode.handler_catch[b] = readUnsignedShort();
      paramCode.handler_class[b] = readClassRefOrNull();
    } 
    readAttributes(3, paramCode);
  }
  
  void readBootstrapMethods(Package.Class paramClass) throws IOException {
    ConstantPool.BootstrapMethodEntry[] arrayOfBootstrapMethodEntry = new ConstantPool.BootstrapMethodEntry[readUnsignedShort()];
    for (byte b = 0; b < arrayOfBootstrapMethodEntry.length; b++) {
      ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)readRef((byte)15);
      ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[readUnsignedShort()];
      for (byte b1 = 0; b1 < arrayOfEntry.length; b1++)
        arrayOfEntry[b1] = readRef((byte)51); 
      arrayOfBootstrapMethodEntry[b] = ConstantPool.getBootstrapMethodEntry(methodHandleEntry, arrayOfEntry);
    } 
    paramClass.setBootstrapMethods(Arrays.asList(arrayOfBootstrapMethodEntry));
  }
  
  void readInnerClasses(Package.Class paramClass) throws IOException {
    int i = readUnsignedShort();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Package.InnerClass innerClass = new Package.InnerClass(readClassRef(), readClassRefOrNull(), (ConstantPool.Utf8Entry)readRefOrNull((byte)1), readUnsignedShort());
      arrayList.add(innerClass);
    } 
    paramClass.innerClasses = arrayList;
  }
  
  static class ClassFormatException extends IOException {
    private static final long serialVersionUID = -3564121733989501833L;
    
    public ClassFormatException(String param1String) { super(param1String); }
    
    public ClassFormatException(String param1String, Throwable param1Throwable) { super(param1String, param1Throwable); }
  }
  
  private class UnresolvedEntry extends ConstantPool.Entry {
    final Object[] refsOrIndexes;
    
    UnresolvedEntry(byte param1Byte, Object... param1VarArgs) {
      super(param1Byte);
      this.refsOrIndexes = param1VarArgs;
      ClassReader.this.haveUnresolvedEntry = true;
    }
    
    ConstantPool.Entry resolve() throws IOException {
      ConstantPool.DescriptorEntry descriptorEntry;
      ConstantPool.BootstrapMethodEntry bootstrapMethodEntry;
      Package.Class clazz = ClassReader.this.cls;
      switch (this.tag) {
        case 18:
          bootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)clazz.bootstrapMethods.get(((Integer)this.refsOrIndexes[0]).intValue());
          descriptorEntry = (ConstantPool.DescriptorEntry)this.refsOrIndexes[1];
          return ConstantPool.getInvokeDynamicEntry(bootstrapMethodEntry, descriptorEntry);
      } 
      throw new AssertionError();
    }
    
    private void unresolved() throws IOException { throw new RuntimeException("unresolved entry has no string"); }
    
    public int compareTo(Object param1Object) {
      unresolved();
      return 0;
    }
    
    public boolean equals(Object param1Object) {
      unresolved();
      return false;
    }
    
    protected int computeValueHash() throws IOException {
      unresolved();
      return 0;
    }
    
    public String stringValue() {
      unresolved();
      return toString();
    }
    
    public String toString() { return "(unresolved " + ConstantPool.tagName(this.tag) + ")"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */