package jdk.internal.org.objectweb.asm;

public class ClassWriter extends ClassVisitor {
  public static final int COMPUTE_MAXS = 1;
  
  public static final int COMPUTE_FRAMES = 2;
  
  static final int ACC_SYNTHETIC_ATTRIBUTE = 262144;
  
  static final int TO_ACC_SYNTHETIC = 64;
  
  static final int NOARG_INSN = 0;
  
  static final int SBYTE_INSN = 1;
  
  static final int SHORT_INSN = 2;
  
  static final int VAR_INSN = 3;
  
  static final int IMPLVAR_INSN = 4;
  
  static final int TYPE_INSN = 5;
  
  static final int FIELDORMETH_INSN = 6;
  
  static final int ITFMETH_INSN = 7;
  
  static final int INDYMETH_INSN = 8;
  
  static final int LABEL_INSN = 9;
  
  static final int LABELW_INSN = 10;
  
  static final int LDC_INSN = 11;
  
  static final int LDCW_INSN = 12;
  
  static final int IINC_INSN = 13;
  
  static final int TABL_INSN = 14;
  
  static final int LOOK_INSN = 15;
  
  static final int MANA_INSN = 16;
  
  static final int WIDE_INSN = 17;
  
  static final byte[] TYPE;
  
  static final int CLASS = 7;
  
  static final int FIELD = 9;
  
  static final int METH = 10;
  
  static final int IMETH = 11;
  
  static final int STR = 8;
  
  static final int INT = 3;
  
  static final int FLOAT = 4;
  
  static final int LONG = 5;
  
  static final int DOUBLE = 6;
  
  static final int NAME_TYPE = 12;
  
  static final int UTF8 = 1;
  
  static final int MTYPE = 16;
  
  static final int HANDLE = 15;
  
  static final int INDY = 18;
  
  static final int HANDLE_BASE = 20;
  
  static final int TYPE_NORMAL = 30;
  
  static final int TYPE_UNINIT = 31;
  
  static final int TYPE_MERGED = 32;
  
  static final int BSM = 33;
  
  ClassReader cr;
  
  int version;
  
  int index = 1;
  
  final ByteVector pool = new ByteVector();
  
  Item[] items = new Item[256];
  
  int threshold = (int)(0.75D * this.items.length);
  
  final Item key = new Item();
  
  final Item key2 = new Item();
  
  final Item key3 = new Item();
  
  final Item key4 = new Item();
  
  Item[] typeTable;
  
  private short typeCount;
  
  private int access;
  
  private int name;
  
  String thisName;
  
  private int signature;
  
  private int superName;
  
  private int interfaceCount;
  
  private int[] interfaces;
  
  private int sourceFile;
  
  private ByteVector sourceDebug;
  
  private int enclosingMethodOwner;
  
  private int enclosingMethod;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private AnnotationWriter tanns;
  
  private AnnotationWriter itanns;
  
  private Attribute attrs;
  
  private int innerClassesCount;
  
  private ByteVector innerClasses;
  
  int bootstrapMethodsCount;
  
  ByteVector bootstrapMethods;
  
  FieldWriter firstField;
  
  FieldWriter lastField;
  
  MethodWriter firstMethod;
  
  MethodWriter lastMethod;
  
  private boolean computeMaxs;
  
  private boolean computeFrames;
  
  boolean invalidFrames;
  
  public ClassWriter(int paramInt) {
    super(327680);
    this.computeMaxs = ((paramInt & true) != 0);
    this.computeFrames = ((paramInt & 0x2) != 0);
  }
  
  public ClassWriter(ClassReader paramClassReader, int paramInt) {
    this(paramInt);
    paramClassReader.copyPool(this);
    this.cr = paramClassReader;
  }
  
  public final void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.version = paramInt1;
    this.access = paramInt2;
    this.name = newClass(paramString1);
    this.thisName = paramString1;
    if (paramString2 != null)
      this.signature = newUTF8(paramString2); 
    this.superName = (paramString3 == null) ? 0 : newClass(paramString3);
    if (paramArrayOfString != null && paramArrayOfString.length > 0) {
      this.interfaceCount = paramArrayOfString.length;
      this.interfaces = new int[this.interfaceCount];
      for (byte b = 0; b < this.interfaceCount; b++)
        this.interfaces[b] = newClass(paramArrayOfString[b]); 
    } 
  }
  
  public final void visitSource(String paramString1, String paramString2) {
    if (paramString1 != null)
      this.sourceFile = newUTF8(paramString1); 
    if (paramString2 != null)
      this.sourceDebug = (new ByteVector()).encodeUTF8(paramString2, 0, 2147483647); 
  }
  
  public final void visitOuterClass(String paramString1, String paramString2, String paramString3) {
    this.enclosingMethodOwner = newClass(paramString1);
    if (paramString2 != null && paramString3 != null)
      this.enclosingMethod = newNameType(paramString2, paramString3); 
  }
  
  public final AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    byteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      annotationWriter.next = this.anns;
      this.anns = annotationWriter;
    } else {
      annotationWriter.next = this.ianns;
      this.ianns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public final AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, byteVector);
    byteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.tanns;
      this.tanns = annotationWriter;
    } else {
      annotationWriter.next = this.itanns;
      this.itanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public final void visitAttribute(Attribute paramAttribute) {
    paramAttribute.next = this.attrs;
    this.attrs = paramAttribute;
  }
  
  public final void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    if (this.innerClasses == null)
      this.innerClasses = new ByteVector(); 
    Item item = newClassItem(paramString1);
    if (item.intVal == 0) {
      this.innerClassesCount++;
      this.innerClasses.putShort(item.index);
      this.innerClasses.putShort((paramString2 == null) ? 0 : newClass(paramString2));
      this.innerClasses.putShort((paramString3 == null) ? 0 : newUTF8(paramString3));
      this.innerClasses.putShort(paramInt);
      item.intVal = this.innerClassesCount;
    } 
  }
  
  public final FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) { return new FieldWriter(this, paramInt, paramString1, paramString2, paramString3, paramObject); }
  
  public final MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) { return new MethodWriter(this, paramInt, paramString1, paramString2, paramString3, paramArrayOfString, this.computeMaxs, this.computeFrames); }
  
  public final void visitEnd() {}
  
  public byte[] toByteArray() {
    if (this.index > 65535)
      throw new RuntimeException("Class file too large!"); 
    int i = 24 + 2 * this.interfaceCount;
    byte b1 = 0;
    FieldWriter fieldWriter;
    for (fieldWriter = this.firstField; fieldWriter != null; fieldWriter = (FieldWriter)fieldWriter.fv) {
      b1++;
      i += fieldWriter.getSize();
    } 
    byte b2 = 0;
    MethodWriter methodWriter;
    for (methodWriter = this.firstMethod; methodWriter != null; methodWriter = (MethodWriter)methodWriter.mv) {
      b2++;
      i += methodWriter.getSize();
    } 
    int j = 0;
    if (this.bootstrapMethods != null) {
      j++;
      i += 8 + this.bootstrapMethods.length;
      newUTF8("BootstrapMethods");
    } 
    if (this.signature != 0) {
      j++;
      i += 8;
      newUTF8("Signature");
    } 
    if (this.sourceFile != 0) {
      j++;
      i += 8;
      newUTF8("SourceFile");
    } 
    if (this.sourceDebug != null) {
      j++;
      i += this.sourceDebug.length + 6;
      newUTF8("SourceDebugExtension");
    } 
    if (this.enclosingMethodOwner != 0) {
      j++;
      i += 10;
      newUTF8("EnclosingMethod");
    } 
    if ((this.access & 0x20000) != 0) {
      j++;
      i += 6;
      newUTF8("Deprecated");
    } 
    if ((this.access & 0x1000) != 0 && ((this.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
      j++;
      i += 6;
      newUTF8("Synthetic");
    } 
    if (this.innerClasses != null) {
      j++;
      i += 8 + this.innerClasses.length;
      newUTF8("InnerClasses");
    } 
    if (this.anns != null) {
      j++;
      i += 8 + this.anns.getSize();
      newUTF8("RuntimeVisibleAnnotations");
    } 
    if (this.ianns != null) {
      j++;
      i += 8 + this.ianns.getSize();
      newUTF8("RuntimeInvisibleAnnotations");
    } 
    if (this.tanns != null) {
      j++;
      i += 8 + this.tanns.getSize();
      newUTF8("RuntimeVisibleTypeAnnotations");
    } 
    if (this.itanns != null) {
      j++;
      i += 8 + this.itanns.getSize();
      newUTF8("RuntimeInvisibleTypeAnnotations");
    } 
    if (this.attrs != null) {
      j += this.attrs.getCount();
      i += this.attrs.getSize(this, null, 0, -1, -1);
    } 
    i += this.pool.length;
    ByteVector byteVector = new ByteVector(i);
    byteVector.putInt(-889275714).putInt(this.version);
    byteVector.putShort(this.index).putByteArray(this.pool.data, 0, this.pool.length);
    int k = 0x60000 | (this.access & 0x40000) / 64;
    byteVector.putShort(this.access & (k ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.superName);
    byteVector.putShort(this.interfaceCount);
    int m;
    for (m = 0; m < this.interfaceCount; m++)
      byteVector.putShort(this.interfaces[m]); 
    byteVector.putShort(b1);
    for (fieldWriter = this.firstField; fieldWriter != null; fieldWriter = (FieldWriter)fieldWriter.fv)
      fieldWriter.put(byteVector); 
    byteVector.putShort(b2);
    for (methodWriter = this.firstMethod; methodWriter != null; methodWriter = (MethodWriter)methodWriter.mv)
      methodWriter.put(byteVector); 
    byteVector.putShort(j);
    if (this.bootstrapMethods != null) {
      byteVector.putShort(newUTF8("BootstrapMethods"));
      byteVector.putInt(this.bootstrapMethods.length + 2).putShort(this.bootstrapMethodsCount);
      byteVector.putByteArray(this.bootstrapMethods.data, 0, this.bootstrapMethods.length);
    } 
    if (this.signature != 0)
      byteVector.putShort(newUTF8("Signature")).putInt(2).putShort(this.signature); 
    if (this.sourceFile != 0)
      byteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(this.sourceFile); 
    if (this.sourceDebug != null) {
      m = this.sourceDebug.length;
      byteVector.putShort(newUTF8("SourceDebugExtension")).putInt(m);
      byteVector.putByteArray(this.sourceDebug.data, 0, m);
    } 
    if (this.enclosingMethodOwner != 0) {
      byteVector.putShort(newUTF8("EnclosingMethod")).putInt(4);
      byteVector.putShort(this.enclosingMethodOwner).putShort(this.enclosingMethod);
    } 
    if ((this.access & 0x20000) != 0)
      byteVector.putShort(newUTF8("Deprecated")).putInt(0); 
    if ((this.access & 0x1000) != 0 && ((this.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0))
      byteVector.putShort(newUTF8("Synthetic")).putInt(0); 
    if (this.innerClasses != null) {
      byteVector.putShort(newUTF8("InnerClasses"));
      byteVector.putInt(this.innerClasses.length + 2).putShort(this.innerClassesCount);
      byteVector.putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
    } 
    if (this.anns != null) {
      byteVector.putShort(newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(byteVector);
    } 
    if (this.ianns != null) {
      byteVector.putShort(newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(byteVector);
    } 
    if (this.tanns != null) {
      byteVector.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
      this.tanns.put(byteVector);
    } 
    if (this.itanns != null) {
      byteVector.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.itanns.put(byteVector);
    } 
    if (this.attrs != null)
      this.attrs.put(this, null, 0, -1, -1, byteVector); 
    if (this.invalidFrames) {
      this.anns = null;
      this.ianns = null;
      this.attrs = null;
      this.innerClassesCount = 0;
      this.innerClasses = null;
      this.bootstrapMethodsCount = 0;
      this.bootstrapMethods = null;
      this.firstField = null;
      this.lastField = null;
      this.firstMethod = null;
      this.lastMethod = null;
      this.computeMaxs = false;
      this.computeFrames = true;
      this.invalidFrames = false;
      (new ClassReader(byteVector.data)).accept(this, 4);
      return toByteArray();
    } 
    return byteVector.data;
  }
  
  Item newConstItem(Object paramObject) {
    if (paramObject instanceof Integer) {
      int i = ((Integer)paramObject).intValue();
      return newInteger(i);
    } 
    if (paramObject instanceof Byte) {
      int i = ((Byte)paramObject).intValue();
      return newInteger(i);
    } 
    if (paramObject instanceof Character) {
      char c = ((Character)paramObject).charValue();
      return newInteger(c);
    } 
    if (paramObject instanceof Short) {
      int i = ((Short)paramObject).intValue();
      return newInteger(i);
    } 
    if (paramObject instanceof Boolean) {
      byte b = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      return newInteger(b);
    } 
    if (paramObject instanceof Float) {
      float f = ((Float)paramObject).floatValue();
      return newFloat(f);
    } 
    if (paramObject instanceof Long) {
      long l = ((Long)paramObject).longValue();
      return newLong(l);
    } 
    if (paramObject instanceof Double) {
      double d = ((Double)paramObject).doubleValue();
      return newDouble(d);
    } 
    if (paramObject instanceof String)
      return newString((String)paramObject); 
    if (paramObject instanceof Type) {
      Type type = (Type)paramObject;
      int i = type.getSort();
      return (i == 10) ? newClassItem(type.getInternalName()) : ((i == 11) ? newMethodTypeItem(type.getDescriptor()) : newClassItem(type.getDescriptor()));
    } 
    if (paramObject instanceof Handle) {
      Handle handle = (Handle)paramObject;
      return newHandleItem(handle.tag, handle.owner, handle.name, handle.desc);
    } 
    throw new IllegalArgumentException("value " + paramObject);
  }
  
  public int newConst(Object paramObject) { return (newConstItem(paramObject)).index; }
  
  public int newUTF8(String paramString) {
    this.key.set(1, paramString, null, null);
    Item item = get(this.key);
    if (item == null) {
      this.pool.putByte(1).putUTF8(paramString);
      item = new Item(this.index++, this.key);
      put(item);
    } 
    return item.index;
  }
  
  Item newClassItem(String paramString) {
    this.key2.set(7, paramString, null, null);
    Item item = get(this.key2);
    if (item == null) {
      this.pool.put12(7, newUTF8(paramString));
      item = new Item(this.index++, this.key2);
      put(item);
    } 
    return item;
  }
  
  public int newClass(String paramString) { return (newClassItem(paramString)).index; }
  
  Item newMethodTypeItem(String paramString) {
    this.key2.set(16, paramString, null, null);
    Item item = get(this.key2);
    if (item == null) {
      this.pool.put12(16, newUTF8(paramString));
      item = new Item(this.index++, this.key2);
      put(item);
    } 
    return item;
  }
  
  public int newMethodType(String paramString) { return (newMethodTypeItem(paramString)).index; }
  
  Item newHandleItem(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.key4.set(20 + paramInt, paramString1, paramString2, paramString3);
    Item item = get(this.key4);
    if (item == null) {
      if (paramInt <= 4) {
        put112(15, paramInt, newField(paramString1, paramString2, paramString3));
      } else {
        put112(15, paramInt, newMethod(paramString1, paramString2, paramString3, (paramInt == 9)));
      } 
      item = new Item(this.index++, this.key4);
      put(item);
    } 
    return item;
  }
  
  public int newHandle(int paramInt, String paramString1, String paramString2, String paramString3) { return (newHandleItem(paramInt, paramString1, paramString2, paramString3)).index; }
  
  Item newInvokeDynamicItem(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    int n;
    ByteVector byteVector = this.bootstrapMethods;
    if (byteVector == null)
      byteVector = this.bootstrapMethods = new ByteVector(); 
    int i = byteVector.length;
    int j = paramHandle.hashCode();
    byteVector.putShort(newHandle(paramHandle.tag, paramHandle.owner, paramHandle.name, paramHandle.desc));
    int k = paramVarArgs.length;
    byteVector.putShort(k);
    for (byte b = 0; b < k; b++) {
      Object object = paramVarArgs[b];
      j ^= object.hashCode();
      byteVector.putShort(newConst(object));
    } 
    byte[] arrayOfByte = byteVector.data;
    int m = 2 + k << 1;
    j &= Integer.MAX_VALUE;
    Item item = this.items[j % this.items.length];
    label33: while (item != null) {
      if (item.type != 33 || item.hashCode != j) {
        item = item.next;
        continue;
      } 
      n = item.intVal;
      for (int i1 = 0; i1 < m; i1++) {
        if (arrayOfByte[i + i1] != arrayOfByte[n + i1]) {
          item = item.next;
          continue label33;
        } 
      } 
    } 
    if (item != null) {
      n = item.index;
      byteVector.length = i;
    } else {
      n = this.bootstrapMethodsCount++;
      item = new Item(n);
      item.set(i, j);
      put(item);
    } 
    this.key3.set(paramString1, paramString2, n);
    item = get(this.key3);
    if (item == null) {
      put122(18, n, newNameType(paramString1, paramString2));
      item = new Item(this.index++, this.key3);
      put(item);
    } 
    return item;
  }
  
  public int newInvokeDynamic(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) { return (newInvokeDynamicItem(paramString1, paramString2, paramHandle, paramVarArgs)).index; }
  
  Item newFieldItem(String paramString1, String paramString2, String paramString3) {
    this.key3.set(9, paramString1, paramString2, paramString3);
    Item item = get(this.key3);
    if (item == null) {
      put122(9, newClass(paramString1), newNameType(paramString2, paramString3));
      item = new Item(this.index++, this.key3);
      put(item);
    } 
    return item;
  }
  
  public int newField(String paramString1, String paramString2, String paramString3) { return (newFieldItem(paramString1, paramString2, paramString3)).index; }
  
  Item newMethodItem(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    byte b = paramBoolean ? 11 : 10;
    this.key3.set(b, paramString1, paramString2, paramString3);
    Item item = get(this.key3);
    if (item == null) {
      put122(b, newClass(paramString1), newNameType(paramString2, paramString3));
      item = new Item(this.index++, this.key3);
      put(item);
    } 
    return item;
  }
  
  public int newMethod(String paramString1, String paramString2, String paramString3, boolean paramBoolean) { return (newMethodItem(paramString1, paramString2, paramString3, paramBoolean)).index; }
  
  Item newInteger(int paramInt) {
    this.key.set(paramInt);
    Item item = get(this.key);
    if (item == null) {
      this.pool.putByte(3).putInt(paramInt);
      item = new Item(this.index++, this.key);
      put(item);
    } 
    return item;
  }
  
  Item newFloat(float paramFloat) {
    this.key.set(paramFloat);
    Item item = get(this.key);
    if (item == null) {
      this.pool.putByte(4).putInt(this.key.intVal);
      item = new Item(this.index++, this.key);
      put(item);
    } 
    return item;
  }
  
  Item newLong(long paramLong) {
    this.key.set(paramLong);
    Item item = get(this.key);
    if (item == null) {
      this.pool.putByte(5).putLong(paramLong);
      item = new Item(this.index, this.key);
      this.index += 2;
      put(item);
    } 
    return item;
  }
  
  Item newDouble(double paramDouble) {
    this.key.set(paramDouble);
    Item item = get(this.key);
    if (item == null) {
      this.pool.putByte(6).putLong(this.key.longVal);
      item = new Item(this.index, this.key);
      this.index += 2;
      put(item);
    } 
    return item;
  }
  
  private Item newString(String paramString) {
    this.key2.set(8, paramString, null, null);
    Item item = get(this.key2);
    if (item == null) {
      this.pool.put12(8, newUTF8(paramString));
      item = new Item(this.index++, this.key2);
      put(item);
    } 
    return item;
  }
  
  public int newNameType(String paramString1, String paramString2) { return (newNameTypeItem(paramString1, paramString2)).index; }
  
  Item newNameTypeItem(String paramString1, String paramString2) {
    this.key2.set(12, paramString1, paramString2, null);
    Item item = get(this.key2);
    if (item == null) {
      put122(12, newUTF8(paramString1), newUTF8(paramString2));
      item = new Item(this.index++, this.key2);
      put(item);
    } 
    return item;
  }
  
  int addType(String paramString) {
    this.key.set(30, paramString, null, null);
    Item item = get(this.key);
    if (item == null)
      item = addType(this.key); 
    return item.index;
  }
  
  int addUninitializedType(String paramString, int paramInt) {
    this.key.type = 31;
    this.key.intVal = paramInt;
    this.key.strVal1 = paramString;
    this.key.hashCode = 0x7FFFFFFF & 31 + paramString.hashCode() + paramInt;
    Item item = get(this.key);
    if (item == null)
      item = addType(this.key); 
    return item.index;
  }
  
  private Item addType(Item paramItem) {
    this.typeCount = (short)(this.typeCount + 1);
    Item item = new Item(this.typeCount, this.key);
    put(item);
    if (this.typeTable == null)
      this.typeTable = new Item[16]; 
    if (this.typeCount == this.typeTable.length) {
      Item[] arrayOfItem = new Item[2 * this.typeTable.length];
      System.arraycopy(this.typeTable, 0, arrayOfItem, 0, this.typeTable.length);
      this.typeTable = arrayOfItem;
    } 
    this.typeTable[this.typeCount] = item;
    return item;
  }
  
  int getMergedType(int paramInt1, int paramInt2) {
    this.key2.type = 32;
    this.key2.longVal = paramInt1 | paramInt2 << 32;
    this.key2.hashCode = 0x7FFFFFFF & 32 + paramInt1 + paramInt2;
    Item item = get(this.key2);
    if (item == null) {
      String str1 = (this.typeTable[paramInt1]).strVal1;
      String str2 = (this.typeTable[paramInt2]).strVal1;
      this.key2.intVal = addType(getCommonSuperClass(str1, str2));
      item = new Item(0, this.key2);
      put(item);
    } 
    return item.intVal;
  }
  
  protected String getCommonSuperClass(String paramString1, String paramString2) {
    Class clazz2;
    Class clazz1;
    ClassLoader classLoader = getClass().getClassLoader();
    try {
      clazz2 = (clazz1 = Class.forName(paramString1.replace('/', '.'), false, classLoader)).forName(paramString2.replace('/', '.'), false, classLoader);
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
    if (clazz1.isAssignableFrom(clazz2))
      return paramString1; 
    if (clazz2.isAssignableFrom(clazz1))
      return paramString2; 
    if (clazz1.isInterface() || clazz2.isInterface())
      return "java/lang/Object"; 
    do {
      clazz1 = clazz1.getSuperclass();
    } while (!clazz1.isAssignableFrom(clazz2));
    return clazz1.getName().replace('.', '/');
  }
  
  private Item get(Item paramItem) {
    Item item;
    for (item = this.items[paramItem.hashCode % this.items.length]; item != null && (item.type != paramItem.type || !paramItem.isEqualTo(item)); item = item.next);
    return item;
  }
  
  private void put(Item paramItem) {
    if (this.index + this.typeCount > this.threshold) {
      int j = this.items.length;
      int k = j * 2 + 1;
      Item[] arrayOfItem = new Item[k];
      for (int m = j - 1; m >= 0; m--) {
        for (Item item = this.items[m]; item != null; item = item1) {
          int n = item.hashCode % arrayOfItem.length;
          Item item1 = item.next;
          item.next = arrayOfItem[n];
          arrayOfItem[n] = item;
        } 
      } 
      this.items = arrayOfItem;
      this.threshold = (int)(k * 0.75D);
    } 
    int i = paramItem.hashCode % this.items.length;
    paramItem.next = this.items[i];
    this.items[i] = paramItem;
  }
  
  private void put122(int paramInt1, int paramInt2, int paramInt3) { this.pool.put12(paramInt1, paramInt2).putShort(paramInt3); }
  
  private void put112(int paramInt1, int paramInt2, int paramInt3) { this.pool.put11(paramInt1, paramInt2).putShort(paramInt3); }
  
  static  {
    byte[] arrayOfByte = new byte[220];
    String str = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = (byte)(str.charAt(b) - 'A'); 
    TYPE = arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */