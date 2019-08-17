package com.sun.java.util.jar.pack;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

abstract class ConstantPool {
  protected static final Entry[] noRefs = new Entry[0];
  
  protected static final ClassEntry[] noClassRefs = new ClassEntry[0];
  
  static final byte[] TAGS_IN_ORDER = { 
      1, 3, 4, 5, 6, 8, 7, 13, 12, 9, 
      10, 11, 15, 16, 17, 18 };
  
  static final byte[] TAG_ORDER = new byte[19];
  
  static final byte[] NUMBER_TAGS;
  
  static final byte[] EXTRA_TAGS;
  
  static final byte[] LOADABLE_VALUE_TAGS;
  
  static final byte[] ANY_MEMBER_TAGS;
  
  static final byte[] FIELD_SPECIFIC_TAGS;
  
  static int verbose() { return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose"); }
  
  public static Utf8Entry getUtf8Entry(String paramString) {
    Map map = Utils.getTLGlobals().getUtf8Entries();
    Utf8Entry utf8Entry = (Utf8Entry)map.get(paramString);
    if (utf8Entry == null) {
      utf8Entry = new Utf8Entry(paramString);
      map.put(utf8Entry.stringValue(), utf8Entry);
    } 
    return utf8Entry;
  }
  
  public static ClassEntry getClassEntry(String paramString) {
    Map map = Utils.getTLGlobals().getClassEntries();
    ClassEntry classEntry = (ClassEntry)map.get(paramString);
    if (classEntry == null) {
      classEntry = new ClassEntry(getUtf8Entry(paramString));
      assert paramString.equals(classEntry.stringValue());
      map.put(classEntry.stringValue(), classEntry);
    } 
    return classEntry;
  }
  
  public static LiteralEntry getLiteralEntry(Comparable<?> paramComparable) {
    Map map = Utils.getTLGlobals().getLiteralEntries();
    LiteralEntry literalEntry = (LiteralEntry)map.get(paramComparable);
    if (literalEntry == null) {
      if (paramComparable instanceof String) {
        literalEntry = new StringEntry(getUtf8Entry((String)paramComparable));
      } else {
        literalEntry = new NumberEntry((Number)paramComparable);
      } 
      map.put(paramComparable, literalEntry);
    } 
    return literalEntry;
  }
  
  public static StringEntry getStringEntry(String paramString) { return (StringEntry)getLiteralEntry(paramString); }
  
  public static SignatureEntry getSignatureEntry(String paramString) {
    Map map = Utils.getTLGlobals().getSignatureEntries();
    SignatureEntry signatureEntry = (SignatureEntry)map.get(paramString);
    if (signatureEntry == null) {
      signatureEntry = new SignatureEntry(paramString);
      assert signatureEntry.stringValue().equals(paramString);
      map.put(paramString, signatureEntry);
    } 
    return signatureEntry;
  }
  
  public static SignatureEntry getSignatureEntry(Utf8Entry paramUtf8Entry, ClassEntry[] paramArrayOfClassEntry) { return getSignatureEntry(SignatureEntry.stringValueOf(paramUtf8Entry, paramArrayOfClassEntry)); }
  
  public static DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry, SignatureEntry paramSignatureEntry) {
    Map map = Utils.getTLGlobals().getDescriptorEntries();
    String str = DescriptorEntry.stringValueOf(paramUtf8Entry, paramSignatureEntry);
    DescriptorEntry descriptorEntry = (DescriptorEntry)map.get(str);
    if (descriptorEntry == null) {
      descriptorEntry = new DescriptorEntry(paramUtf8Entry, paramSignatureEntry);
      assert descriptorEntry.stringValue().equals(str) : descriptorEntry.stringValue() + " != " + str;
      map.put(str, descriptorEntry);
    } 
    return descriptorEntry;
  }
  
  public static DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry1, Utf8Entry paramUtf8Entry2) { return getDescriptorEntry(paramUtf8Entry1, getSignatureEntry(paramUtf8Entry2.stringValue())); }
  
  public static MemberEntry getMemberEntry(byte paramByte, ClassEntry paramClassEntry, DescriptorEntry paramDescriptorEntry) {
    Map map = Utils.getTLGlobals().getMemberEntries();
    String str = MemberEntry.stringValueOf(paramByte, paramClassEntry, paramDescriptorEntry);
    MemberEntry memberEntry = (MemberEntry)map.get(str);
    if (memberEntry == null) {
      memberEntry = new MemberEntry(paramByte, paramClassEntry, paramDescriptorEntry);
      assert memberEntry.stringValue().equals(str) : memberEntry.stringValue() + " != " + str;
      map.put(str, memberEntry);
    } 
    return memberEntry;
  }
  
  public static MethodHandleEntry getMethodHandleEntry(byte paramByte, MemberEntry paramMemberEntry) {
    Map map = Utils.getTLGlobals().getMethodHandleEntries();
    String str = MethodHandleEntry.stringValueOf(paramByte, paramMemberEntry);
    MethodHandleEntry methodHandleEntry = (MethodHandleEntry)map.get(str);
    if (methodHandleEntry == null) {
      methodHandleEntry = new MethodHandleEntry(paramByte, paramMemberEntry);
      assert methodHandleEntry.stringValue().equals(str);
      map.put(str, methodHandleEntry);
    } 
    return methodHandleEntry;
  }
  
  public static MethodTypeEntry getMethodTypeEntry(SignatureEntry paramSignatureEntry) {
    Map map = Utils.getTLGlobals().getMethodTypeEntries();
    String str = paramSignatureEntry.stringValue();
    MethodTypeEntry methodTypeEntry = (MethodTypeEntry)map.get(str);
    if (methodTypeEntry == null) {
      methodTypeEntry = new MethodTypeEntry(paramSignatureEntry);
      assert methodTypeEntry.stringValue().equals(str);
      map.put(str, methodTypeEntry);
    } 
    return methodTypeEntry;
  }
  
  public static MethodTypeEntry getMethodTypeEntry(Utf8Entry paramUtf8Entry) { return getMethodTypeEntry(getSignatureEntry(paramUtf8Entry.stringValue())); }
  
  public static InvokeDynamicEntry getInvokeDynamicEntry(BootstrapMethodEntry paramBootstrapMethodEntry, DescriptorEntry paramDescriptorEntry) {
    Map map = Utils.getTLGlobals().getInvokeDynamicEntries();
    String str = InvokeDynamicEntry.stringValueOf(paramBootstrapMethodEntry, paramDescriptorEntry);
    InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry)map.get(str);
    if (invokeDynamicEntry == null) {
      invokeDynamicEntry = new InvokeDynamicEntry(paramBootstrapMethodEntry, paramDescriptorEntry);
      assert invokeDynamicEntry.stringValue().equals(str);
      map.put(str, invokeDynamicEntry);
    } 
    return invokeDynamicEntry;
  }
  
  public static BootstrapMethodEntry getBootstrapMethodEntry(MethodHandleEntry paramMethodHandleEntry, Entry[] paramArrayOfEntry) {
    Map map = Utils.getTLGlobals().getBootstrapMethodEntries();
    String str = BootstrapMethodEntry.stringValueOf(paramMethodHandleEntry, paramArrayOfEntry);
    BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)map.get(str);
    if (bootstrapMethodEntry == null) {
      bootstrapMethodEntry = new BootstrapMethodEntry(paramMethodHandleEntry, paramArrayOfEntry);
      assert bootstrapMethodEntry.stringValue().equals(str);
      map.put(str, bootstrapMethodEntry);
    } 
    return bootstrapMethodEntry;
  }
  
  static boolean isMemberTag(byte paramByte) {
    switch (paramByte) {
      case 9:
      case 10:
      case 11:
        return true;
    } 
    return false;
  }
  
  static byte numberTagOf(Number paramNumber) {
    if (paramNumber instanceof Integer)
      return 3; 
    if (paramNumber instanceof Float)
      return 4; 
    if (paramNumber instanceof Long)
      return 5; 
    if (paramNumber instanceof Double)
      return 6; 
    throw new RuntimeException("bad literal value " + paramNumber);
  }
  
  static boolean isRefKind(byte paramByte) { return (1 <= paramByte && paramByte <= 9); }
  
  static String qualifiedStringValue(Entry paramEntry1, Entry paramEntry2) { return qualifiedStringValue(paramEntry1.stringValue(), paramEntry2.stringValue()); }
  
  static String qualifiedStringValue(String paramString1, String paramString2) {
    assert paramString1.indexOf(".") < 0;
    return paramString1 + "." + paramString2;
  }
  
  static int compareSignatures(String paramString1, String paramString2) { return compareSignatures(paramString1, paramString2, null, null); }
  
  static int compareSignatures(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) {
    char c1 = paramString1.charAt(0);
    char c2 = paramString2.charAt(0);
    if (c1 != '(' && c2 == '(')
      return -1; 
    if (c2 != '(' && c1 == '(')
      return 1; 
    if (paramArrayOfString1 == null)
      paramArrayOfString1 = structureSignature(paramString1); 
    if (paramArrayOfString2 == null)
      paramArrayOfString2 = structureSignature(paramString2); 
    if (paramArrayOfString1.length != paramArrayOfString2.length)
      return paramArrayOfString1.length - paramArrayOfString2.length; 
    int i = paramArrayOfString1.length;
    int j = i;
    while (--j >= 0) {
      int k = paramArrayOfString1[j].compareTo(paramArrayOfString2[j]);
      if (k != 0)
        return k; 
    } 
    assert paramString1.equals(paramString2);
    return 0;
  }
  
  static int countClassParts(Utf8Entry paramUtf8Entry) {
    byte b1 = 0;
    String str = paramUtf8Entry.stringValue();
    for (byte b2 = 0; b2 < str.length(); b2++) {
      if (str.charAt(b2) == 'L')
        b1++; 
    } 
    return b1;
  }
  
  static String flattenSignature(String[] paramArrayOfString) {
    String str = paramArrayOfString[0];
    if (paramArrayOfString.length == 1)
      return str; 
    int i = str.length();
    for (byte b1 = 1; b1 < paramArrayOfString.length; b1++)
      i += paramArrayOfString[b1].length(); 
    char[] arrayOfChar = new char[i];
    int j = 0;
    byte b2 = 1;
    for (byte b3 = 0; b3 < str.length(); b3++) {
      char c = str.charAt(b3);
      arrayOfChar[j++] = c;
      if (c == 'L') {
        String str1 = paramArrayOfString[b2++];
        str1.getChars(0, str1.length(), arrayOfChar, j);
        j += str1.length();
      } 
    } 
    assert j == i;
    assert b2 == paramArrayOfString.length;
    return new String(arrayOfChar);
  }
  
  private static int skipTo(char paramChar, String paramString, int paramInt) {
    paramInt = paramString.indexOf(paramChar, paramInt);
    return (paramInt >= 0) ? paramInt : paramString.length();
  }
  
  static String[] structureSignature(String paramString) { // Byte code:
    //   0: aload_0
    //   1: bipush #76
    //   3: invokevirtual indexOf : (I)I
    //   6: istore_1
    //   7: iload_1
    //   8: ifge -> 22
    //   11: iconst_1
    //   12: anewarray java/lang/String
    //   15: dup
    //   16: iconst_0
    //   17: aload_0
    //   18: aastore
    //   19: astore_2
    //   20: aload_2
    //   21: areturn
    //   22: aconst_null
    //   23: astore_2
    //   24: aconst_null
    //   25: astore_3
    //   26: iconst_0
    //   27: istore #4
    //   29: iload #4
    //   31: iconst_1
    //   32: if_icmpgt -> 220
    //   35: iconst_0
    //   36: istore #5
    //   38: iconst_1
    //   39: istore #6
    //   41: iconst_0
    //   42: istore #7
    //   44: iconst_0
    //   45: istore #8
    //   47: iconst_0
    //   48: istore #9
    //   50: iload_1
    //   51: iconst_1
    //   52: iadd
    //   53: istore #10
    //   55: iload #10
    //   57: ifle -> 170
    //   60: iload #7
    //   62: iload #10
    //   64: if_icmpge -> 77
    //   67: bipush #59
    //   69: aload_0
    //   70: iload #10
    //   72: invokestatic skipTo : (CLjava/lang/String;I)I
    //   75: istore #7
    //   77: iload #8
    //   79: iload #10
    //   81: if_icmpge -> 94
    //   84: bipush #60
    //   86: aload_0
    //   87: iload #10
    //   89: invokestatic skipTo : (CLjava/lang/String;I)I
    //   92: istore #8
    //   94: iload #7
    //   96: iload #8
    //   98: if_icmpge -> 106
    //   101: iload #7
    //   103: goto -> 108
    //   106: iload #8
    //   108: istore #11
    //   110: iload #4
    //   112: ifeq -> 138
    //   115: aload_0
    //   116: iload #9
    //   118: iload #10
    //   120: aload_2
    //   121: iload #5
    //   123: invokevirtual getChars : (II[CI)V
    //   126: aload_3
    //   127: iload #6
    //   129: aload_0
    //   130: iload #10
    //   132: iload #11
    //   134: invokevirtual substring : (II)Ljava/lang/String;
    //   137: aastore
    //   138: iload #5
    //   140: iload #10
    //   142: iload #9
    //   144: isub
    //   145: iadd
    //   146: istore #5
    //   148: iinc #6, 1
    //   151: iload #11
    //   153: istore #9
    //   155: aload_0
    //   156: bipush #76
    //   158: iload #11
    //   160: invokevirtual indexOf : (II)I
    //   163: iconst_1
    //   164: iadd
    //   165: istore #10
    //   167: goto -> 55
    //   170: iload #4
    //   172: ifeq -> 191
    //   175: aload_0
    //   176: iload #9
    //   178: aload_0
    //   179: invokevirtual length : ()I
    //   182: aload_2
    //   183: iload #5
    //   185: invokevirtual getChars : (II[CI)V
    //   188: goto -> 220
    //   191: iload #5
    //   193: aload_0
    //   194: invokevirtual length : ()I
    //   197: iload #9
    //   199: isub
    //   200: iadd
    //   201: istore #5
    //   203: iload #5
    //   205: newarray char
    //   207: astore_2
    //   208: iload #6
    //   210: anewarray java/lang/String
    //   213: astore_3
    //   214: iinc #4, 1
    //   217: goto -> 29
    //   220: aload_3
    //   221: iconst_0
    //   222: new java/lang/String
    //   225: dup
    //   226: aload_2
    //   227: invokespecial <init> : ([C)V
    //   230: aastore
    //   231: aload_3
    //   232: areturn }
  
  public static Index makeIndex(String paramString, Entry[] paramArrayOfEntry) { return new Index(paramString, paramArrayOfEntry); }
  
  public static Index makeIndex(String paramString, Collection<Entry> paramCollection) { return new Index(paramString, paramCollection); }
  
  public static void sort(Index paramIndex) {
    paramIndex.clearIndex();
    Arrays.sort(paramIndex.cpMap);
    if (verbose() > 2)
      System.out.println("sorted " + paramIndex.dumpString()); 
  }
  
  public static Index[] partition(Index paramIndex, int[] paramArrayOfInt) {
    ArrayList arrayList = new ArrayList();
    Entry[] arrayOfEntry = paramIndex.cpMap;
    assert paramArrayOfInt.length == arrayOfEntry.length;
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++) {
      int i = paramArrayOfInt[b1];
      if (i >= 0) {
        while (i >= arrayList.size())
          arrayList.add(null); 
        List list = (List)arrayList.get(i);
        if (list == null)
          arrayList.set(i, list = new ArrayList()); 
        list.add(arrayOfEntry[b1]);
      } 
    } 
    Index[] arrayOfIndex = new Index[arrayList.size()];
    for (byte b2 = 0; b2 < arrayOfIndex.length; b2++) {
      List list = (List)arrayList.get(b2);
      if (list != null) {
        arrayOfIndex[b2] = new Index(paramIndex.debugName + "/part#" + b2, list);
        assert arrayOfIndex[b2].indexOf((Entry)list.get(0)) == 0;
      } 
    } 
    return arrayOfIndex;
  }
  
  public static Index[] partitionByTag(Index paramIndex) {
    Entry[] arrayOfEntry = paramIndex.cpMap;
    int[] arrayOfInt = new int[arrayOfEntry.length];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
      Entry entry = arrayOfEntry[b1];
      arrayOfInt[b1] = (entry == null) ? -1 : entry.tag;
    } 
    Index[] arrayOfIndex = partition(paramIndex, arrayOfInt);
    for (byte b2 = 0; b2 < arrayOfIndex.length; b2++) {
      if (arrayOfIndex[b2] != null)
        (arrayOfIndex[b2]).debugName = tagName(b2); 
    } 
    if (arrayOfIndex.length < 19) {
      Index[] arrayOfIndex1 = new Index[19];
      System.arraycopy(arrayOfIndex, 0, arrayOfIndex1, 0, arrayOfIndex.length);
      arrayOfIndex = arrayOfIndex1;
    } 
    return arrayOfIndex;
  }
  
  public static void completeReferencesIn(Set<Entry> paramSet, boolean paramBoolean) { completeReferencesIn(paramSet, paramBoolean, null); }
  
  public static void completeReferencesIn(Set<Entry> paramSet, boolean paramBoolean, List<BootstrapMethodEntry> paramList) {
    paramSet.remove(null);
    ListIterator listIterator = (new ArrayList(paramSet)).listIterator(paramSet.size());
    label27: while (listIterator.hasPrevious()) {
      Entry entry = (Entry)listIterator.previous();
      listIterator.remove();
      assert entry != null;
      if (paramBoolean && entry.tag == 13) {
        SignatureEntry signatureEntry = (SignatureEntry)entry;
        Utf8Entry utf8Entry = signatureEntry.asUtf8Entry();
        paramSet.remove(signatureEntry);
        paramSet.add(utf8Entry);
        entry = utf8Entry;
      } 
      if (paramList != null && entry.tag == 17) {
        BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)entry;
        paramSet.remove(bootstrapMethodEntry);
        if (!paramList.contains(bootstrapMethodEntry))
          paramList.add(bootstrapMethodEntry); 
      } 
      for (byte b = 0;; b++) {
        Entry entry1 = entry.getRef(b);
        if (entry1 == null)
          continue label27; 
        if (paramSet.add(entry1))
          listIterator.add(entry1); 
      } 
    } 
  }
  
  static double percent(int paramInt1, int paramInt2) { return (int)(10000.0D * paramInt1 / paramInt2 + 0.5D) / 100.0D; }
  
  public static String tagName(int paramInt) {
    switch (paramInt) {
      case 1:
        return "Utf8";
      case 3:
        return "Integer";
      case 4:
        return "Float";
      case 5:
        return "Long";
      case 6:
        return "Double";
      case 7:
        return "Class";
      case 8:
        return "String";
      case 9:
        return "Fieldref";
      case 10:
        return "Methodref";
      case 11:
        return "InterfaceMethodref";
      case 12:
        return "NameandType";
      case 15:
        return "MethodHandle";
      case 16:
        return "MethodType";
      case 18:
        return "InvokeDynamic";
      case 50:
        return "**All";
      case 0:
        return "**None";
      case 51:
        return "**LoadableValue";
      case 52:
        return "**AnyMember";
      case 53:
        return "*FieldSpecific";
      case 13:
        return "*Signature";
      case 17:
        return "*BootstrapMethod";
    } 
    return "tag#" + paramInt;
  }
  
  public static String refKindName(int paramInt) {
    switch (paramInt) {
      case 1:
        return "getField";
      case 2:
        return "getStatic";
      case 3:
        return "putField";
      case 4:
        return "putStatic";
      case 5:
        return "invokeVirtual";
      case 6:
        return "invokeStatic";
      case 7:
        return "invokeSpecial";
      case 8:
        return "newInvokeSpecial";
      case 9:
        return "invokeInterface";
    } 
    return "refKind#" + paramInt;
  }
  
  private static boolean verifyTagOrder(byte[] paramArrayOfByte) {
    byte b = -1;
    for (byte b1 : paramArrayOfByte) {
      byte b2 = TAG_ORDER[b1];
      assert b2 > 0 : "tag not found: " + b1;
      assert TAGS_IN_ORDER[b2 - 1] == b1 : "tag repeated: " + b1 + " => " + b2 + " => " + TAGS_IN_ORDER[b2 - true];
      assert b < b2 : "tags not in order: " + Arrays.toString(paramArrayOfByte) + " at " + b1;
      b = b2;
    } 
    return true;
  }
  
  static  {
    for (byte b = 0; b < TAGS_IN_ORDER.length; b++)
      TAG_ORDER[TAGS_IN_ORDER[b]] = (byte)(b + true); 
    NUMBER_TAGS = new byte[] { 3, 4, 5, 6 };
    EXTRA_TAGS = new byte[] { 15, 16, 17, 18 };
    LOADABLE_VALUE_TAGS = new byte[] { 3, 4, 5, 6, 8, 7, 15, 16 };
    ANY_MEMBER_TAGS = new byte[] { 9, 10, 11 };
    FIELD_SPECIFIC_TAGS = new byte[] { 3, 4, 5, 6, 8 };
    assert verifyTagOrder(TAGS_IN_ORDER) && verifyTagOrder(NUMBER_TAGS) && verifyTagOrder(EXTRA_TAGS) && verifyTagOrder(LOADABLE_VALUE_TAGS) && verifyTagOrder(ANY_MEMBER_TAGS) && verifyTagOrder(FIELD_SPECIFIC_TAGS);
  }
  
  public static class BootstrapMethodEntry extends Entry {
    final ConstantPool.MethodHandleEntry bsmRef;
    
    final ConstantPool.Entry[] argRefs;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.bsmRef : ((param1Int - 1 < this.argRefs.length) ? this.argRefs[param1Int - 1] : null); }
    
    protected int computeValueHash() {
      int i = this.bsmRef.hashCode();
      return Arrays.hashCode(this.argRefs) + (i << 8) ^ i;
    }
    
    BootstrapMethodEntry(ConstantPool.MethodHandleEntry param1MethodHandleEntry, ConstantPool.Entry[] param1ArrayOfEntry) {
      super((byte)17);
      this.bsmRef = param1MethodHandleEntry;
      this.argRefs = (Entry[])param1ArrayOfEntry.clone();
      hashCode();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != BootstrapMethodEntry.class)
        return false; 
      BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)param1Object;
      return (this.bsmRef.eq(bootstrapMethodEntry.bsmRef) && Arrays.equals(this.argRefs, bootstrapMethodEntry.argRefs));
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)param1Object;
        if (Utils.SORT_BSS_BSM_MAJOR)
          i = this.bsmRef.compareTo(bootstrapMethodEntry.bsmRef); 
        if (i == 0)
          i = compareArgArrays(this.argRefs, bootstrapMethodEntry.argRefs); 
        if (i == 0)
          i = this.bsmRef.compareTo(bootstrapMethodEntry.bsmRef); 
      } 
      return i;
    }
    
    public String stringValue() { return stringValueOf(this.bsmRef, this.argRefs); }
    
    static String stringValueOf(ConstantPool.MethodHandleEntry param1MethodHandleEntry, ConstantPool.Entry[] param1ArrayOfEntry) {
      StringBuffer stringBuffer = new StringBuffer(param1MethodHandleEntry.stringValue());
      char c = '<';
      boolean bool = false;
      for (ConstantPool.Entry entry : param1ArrayOfEntry) {
        stringBuffer.append(c).append(entry.stringValue());
        c = ';';
      } 
      if (c == '<')
        stringBuffer.append(c); 
      stringBuffer.append('>');
      return stringBuffer.toString();
    }
    
    static int compareArgArrays(ConstantPool.Entry[] param1ArrayOfEntry1, ConstantPool.Entry[] param1ArrayOfEntry2) {
      int i = param1ArrayOfEntry1.length - param1ArrayOfEntry2.length;
      if (i != 0)
        return i; 
      for (byte b = 0; b < param1ArrayOfEntry1.length; b++) {
        i = param1ArrayOfEntry1[b].compareTo(param1ArrayOfEntry2[b]);
        if (i != 0)
          break; 
      } 
      return i;
    }
  }
  
  public static class ClassEntry extends Entry {
    final ConstantPool.Utf8Entry ref;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.ref : null; }
    
    protected int computeValueHash() { return this.ref.hashCode() + this.tag; }
    
    ClassEntry(ConstantPool.Entry param1Entry) {
      super((byte)7);
      this.ref = (ConstantPool.Utf8Entry)param1Entry;
      hashCode();
    }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == ClassEntry.class && ((ClassEntry)param1Object).ref.eq(this.ref)); }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0)
        i = this.ref.compareTo(((ClassEntry)param1Object).ref); 
      return i;
    }
    
    public String stringValue() { return this.ref.stringValue(); }
  }
  
  public static class DescriptorEntry extends Entry {
    final ConstantPool.Utf8Entry nameRef;
    
    final ConstantPool.SignatureEntry typeRef;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.nameRef : ((param1Int == 1) ? this.typeRef : null); }
    
    DescriptorEntry(ConstantPool.Entry param1Entry1, ConstantPool.Entry param1Entry2) {
      super((byte)12);
      if (param1Entry2 instanceof ConstantPool.Utf8Entry)
        param1Entry2 = ConstantPool.getSignatureEntry(param1Entry2.stringValue()); 
      this.nameRef = (ConstantPool.Utf8Entry)param1Entry1;
      this.typeRef = (ConstantPool.SignatureEntry)param1Entry2;
      hashCode();
    }
    
    protected int computeValueHash() {
      int i = this.typeRef.hashCode();
      return this.nameRef.hashCode() + (i << 8) ^ i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != DescriptorEntry.class)
        return false; 
      DescriptorEntry descriptorEntry = (DescriptorEntry)param1Object;
      return (this.nameRef.eq(descriptorEntry.nameRef) && this.typeRef.eq(descriptorEntry.typeRef));
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        DescriptorEntry descriptorEntry = (DescriptorEntry)param1Object;
        i = this.typeRef.compareTo(descriptorEntry.typeRef);
        if (i == 0)
          i = this.nameRef.compareTo(descriptorEntry.nameRef); 
      } 
      return i;
    }
    
    public String stringValue() { return stringValueOf(this.nameRef, this.typeRef); }
    
    static String stringValueOf(ConstantPool.Entry param1Entry1, ConstantPool.Entry param1Entry2) { return ConstantPool.qualifiedStringValue(param1Entry2, param1Entry1); }
    
    public String prettyString() { return this.nameRef.stringValue() + this.typeRef.prettyString(); }
    
    public boolean isMethod() { return this.typeRef.isMethod(); }
    
    public byte getLiteralTag() { return this.typeRef.getLiteralTag(); }
  }
  
  public static abstract class Entry extends Object implements Comparable<Object> {
    protected final byte tag;
    
    protected int valueHash;
    
    protected Entry(byte param1Byte) { this.tag = param1Byte; }
    
    public final byte getTag() { return this.tag; }
    
    public final boolean tagEquals(int param1Int) { return (getTag() == param1Int); }
    
    public Entry getRef(int param1Int) { return null; }
    
    public boolean eq(Entry param1Entry) {
      assert param1Entry != null;
      return (this == param1Entry || equals(param1Entry));
    }
    
    public abstract boolean equals(Object param1Object);
    
    public final int hashCode() {
      if (this.valueHash == 0) {
        this.valueHash = computeValueHash();
        if (this.valueHash == 0)
          this.valueHash = 1; 
      } 
      return this.valueHash;
    }
    
    protected abstract int computeValueHash();
    
    public abstract int compareTo(Object param1Object);
    
    protected int superCompareTo(Object param1Object) {
      Entry entry = (Entry)param1Object;
      return (this.tag != entry.tag) ? (ConstantPool.TAG_ORDER[this.tag] - ConstantPool.TAG_ORDER[entry.tag]) : 0;
    }
    
    public final boolean isDoubleWord() { return (this.tag == 6 || this.tag == 5); }
    
    public final boolean tagMatches(int param1Int) {
      byte[] arrayOfByte;
      if (this.tag == param1Int)
        return true; 
      switch (param1Int) {
        case 50:
          return true;
        case 13:
          return (this.tag == 1);
        case 51:
          arrayOfByte = ConstantPool.LOADABLE_VALUE_TAGS;
          break;
        case 52:
          arrayOfByte = ConstantPool.ANY_MEMBER_TAGS;
          break;
        case 53:
          arrayOfByte = ConstantPool.FIELD_SPECIFIC_TAGS;
          break;
        default:
          return false;
      } 
      for (byte b : arrayOfByte) {
        if (b == this.tag)
          return true; 
      } 
      return false;
    }
    
    public String toString() {
      String str = stringValue();
      if (ConstantPool.verbose() > 4) {
        if (this.valueHash != 0)
          str = str + " hash=" + this.valueHash; 
        str = str + " id=" + System.identityHashCode(this);
      } 
      return ConstantPool.tagName(this.tag) + "=" + str;
    }
    
    public abstract String stringValue();
  }
  
  public static final class Index extends AbstractList<Entry> {
    protected String debugName;
    
    protected ConstantPool.Entry[] cpMap;
    
    protected boolean flattenSigs;
    
    protected ConstantPool.Entry[] indexKey;
    
    protected int[] indexValue;
    
    protected ConstantPool.Entry[] getMap() { return this.cpMap; }
    
    protected Index(String param1String) { this.debugName = param1String; }
    
    protected Index(String param1String, ConstantPool.Entry[] param1ArrayOfEntry) {
      this(param1String);
      setMap(param1ArrayOfEntry);
    }
    
    protected void setMap(ConstantPool.Entry[] param1ArrayOfEntry) {
      clearIndex();
      this.cpMap = param1ArrayOfEntry;
    }
    
    protected Index(String param1String, Collection<ConstantPool.Entry> param1Collection) {
      this(param1String);
      setMap(param1Collection);
    }
    
    protected void setMap(Collection<ConstantPool.Entry> param1Collection) {
      this.cpMap = new ConstantPool.Entry[param1Collection.size()];
      param1Collection.toArray(this.cpMap);
      setMap(this.cpMap);
    }
    
    public int size() { return this.cpMap.length; }
    
    public ConstantPool.Entry get(int param1Int) { return this.cpMap[param1Int]; }
    
    public ConstantPool.Entry getEntry(int param1Int) { return this.cpMap[param1Int]; }
    
    private int findIndexOf(ConstantPool.Entry param1Entry) {
      if (this.indexKey == null)
        initializeIndex(); 
      int i = findIndexLocation(param1Entry);
      if (this.indexKey[i] != param1Entry) {
        if (this.flattenSigs && param1Entry.tag == 13) {
          ConstantPool.SignatureEntry signatureEntry = (ConstantPool.SignatureEntry)param1Entry;
          return findIndexOf(signatureEntry.asUtf8Entry());
        } 
        return -1;
      } 
      int j = this.indexValue[i];
      assert param1Entry.equals(this.cpMap[j]);
      return j;
    }
    
    public boolean contains(ConstantPool.Entry param1Entry) { return (findIndexOf(param1Entry) >= 0); }
    
    public int indexOf(ConstantPool.Entry param1Entry) {
      int i = findIndexOf(param1Entry);
      if (i < 0 && ConstantPool.verbose() > 0) {
        System.out.println("not found: " + param1Entry);
        System.out.println("       in: " + dumpString());
        Thread.dumpStack();
      } 
      assert i >= 0;
      return i;
    }
    
    public int lastIndexOf(ConstantPool.Entry param1Entry) { return indexOf(param1Entry); }
    
    public boolean assertIsSorted() {
      for (byte b = 1; b < this.cpMap.length; b++) {
        if (this.cpMap[b - true].compareTo(this.cpMap[b]) > 0) {
          System.out.println("Not sorted at " + (b - true) + "/" + b + ": " + dumpString());
          return false;
        } 
      } 
      return true;
    }
    
    protected void clearIndex() {
      this.indexKey = null;
      this.indexValue = null;
    }
    
    private int findIndexLocation(ConstantPool.Entry param1Entry) {
      int i = this.indexKey.length;
      int j = param1Entry.hashCode();
      int k = j & i - 1;
      int m = (j >>> 8 | true) & i - 1;
      while (true) {
        ConstantPool.Entry entry = this.indexKey[k];
        if (entry == param1Entry || entry == null)
          return k; 
        k += m;
        if (k >= i)
          k -= i; 
      } 
    }
    
    private void initializeIndex() {
      if (ConstantPool.verbose() > 2)
        System.out.println("initialize Index " + this.debugName + " [" + size() + "]"); 
      int i = (int)((this.cpMap.length + 10) * 1.5D);
      boolean bool;
      for (bool = true; bool < i; bool <<= true);
      this.indexKey = new ConstantPool.Entry[bool];
      this.indexValue = new int[bool];
      for (byte b = 0; b < this.cpMap.length; b++) {
        ConstantPool.Entry entry = this.cpMap[b];
        if (entry != null) {
          int j = findIndexLocation(entry);
          assert this.indexKey[j] == null;
          this.indexKey[j] = entry;
          this.indexValue[j] = b;
        } 
      } 
    }
    
    public ConstantPool.Entry[] toArray(ConstantPool.Entry[] param1ArrayOfEntry) {
      int i = size();
      if (param1ArrayOfEntry.length < i)
        return (Entry[])toArray(param1ArrayOfEntry); 
      System.arraycopy(this.cpMap, 0, param1ArrayOfEntry, 0, i);
      if (param1ArrayOfEntry.length > i)
        param1ArrayOfEntry[i] = null; 
      return param1ArrayOfEntry;
    }
    
    public ConstantPool.Entry[] toArray() { return toArray(new ConstantPool.Entry[size()]); }
    
    public Object clone() { return new Index(this.debugName, (Entry[])this.cpMap.clone()); }
    
    public String toString() { return "Index " + this.debugName + " [" + size() + "]"; }
    
    public String dumpString() {
      null = toString();
      null = null + " {\n";
      for (byte b = 0; b < this.cpMap.length; b++)
        null = null + "    " + b + ": " + this.cpMap[b] + "\n"; 
      return null + "}";
    }
  }
  
  public static class IndexGroup {
    private ConstantPool.Index[] indexByTag = new ConstantPool.Index[19];
    
    private ConstantPool.Index[] indexByTagGroup;
    
    private int[] untypedFirstIndexByTag;
    
    private int totalSizeQQ;
    
    private ConstantPool.Index[][] indexByTagAndClass;
    
    private ConstantPool.Index makeTagGroupIndex(byte param1Byte, byte[] param1ArrayOfByte) {
      if (this.indexByTagGroup == null)
        this.indexByTagGroup = new ConstantPool.Index[4]; 
      byte b = param1Byte - 50;
      assert this.indexByTagGroup[b] == null;
      int i = 0;
      ConstantPool.Entry[] arrayOfEntry = null;
      for (byte b1 = 1; b1 <= 2; b1++) {
        untypedIndexOf(null);
        for (byte b2 : param1ArrayOfByte) {
          ConstantPool.Index index = this.indexByTag[b2];
          if (index != null) {
            int j = index.cpMap.length;
            if (j != 0)
              if ($assertionsDisabled || ((param1Byte == 50) ? (i == this.untypedFirstIndexByTag[b2]) : (i < this.untypedFirstIndexByTag[b2]))) {
                if (arrayOfEntry != null) {
                  assert arrayOfEntry[i] == null;
                  assert arrayOfEntry[i + j - true] == null;
                  System.arraycopy(index.cpMap, 0, arrayOfEntry, i, j);
                } 
                i += j;
              } else {
                throw new AssertionError();
              }  
          } 
        } 
        if (arrayOfEntry == null) {
          assert b1 == 1;
          arrayOfEntry = new ConstantPool.Entry[i];
          i = 0;
        } 
      } 
      this.indexByTagGroup[b] = new ConstantPool.Index(ConstantPool.tagName(param1Byte), arrayOfEntry);
      return this.indexByTagGroup[b];
    }
    
    public int untypedIndexOf(ConstantPool.Entry param1Entry) {
      if (this.untypedFirstIndexByTag == null) {
        this.untypedFirstIndexByTag = new int[20];
        int j = 0;
        for (byte b1 = 0; b1 < ConstantPool.TAGS_IN_ORDER.length; b1++) {
          byte b2 = ConstantPool.TAGS_IN_ORDER[b1];
          ConstantPool.Index index1 = this.indexByTag[b2];
          if (index1 != null) {
            int k = index1.cpMap.length;
            this.untypedFirstIndexByTag[b2] = j;
            j += k;
          } 
        } 
        this.untypedFirstIndexByTag[19] = j;
      } 
      if (param1Entry == null)
        return -1; 
      byte b = param1Entry.tag;
      ConstantPool.Index index = this.indexByTag[b];
      if (index == null)
        return -1; 
      int i = index.findIndexOf(param1Entry);
      if (i >= 0)
        i += this.untypedFirstIndexByTag[b]; 
      return i;
    }
    
    public void initIndexByTag(byte param1Byte, ConstantPool.Index param1Index) {
      assert this.indexByTag[param1Byte] == null;
      ConstantPool.Entry[] arrayOfEntry = param1Index.cpMap;
      for (byte b = 0; b < arrayOfEntry.length; b++)
        assert (arrayOfEntry[b]).tag == param1Byte; 
      if (param1Byte == 1 && !$assertionsDisabled && arrayOfEntry.length != 0 && !arrayOfEntry[0].stringValue().equals(""))
        throw new AssertionError(); 
      this.indexByTag[param1Byte] = param1Index;
      this.untypedFirstIndexByTag = null;
      this.indexByTagGroup = null;
      if (this.indexByTagAndClass != null)
        this.indexByTagAndClass[param1Byte] = null; 
    }
    
    public ConstantPool.Index getIndexByTag(byte param1Byte) {
      if (param1Byte >= 50)
        return getIndexByTagGroup(param1Byte); 
      ConstantPool.Index index = this.indexByTag[param1Byte];
      if (index == null) {
        index = new ConstantPool.Index(ConstantPool.tagName(param1Byte), new ConstantPool.Entry[0]);
        this.indexByTag[param1Byte] = index;
      } 
      return index;
    }
    
    private ConstantPool.Index getIndexByTagGroup(byte param1Byte) {
      if (this.indexByTagGroup != null) {
        ConstantPool.Index index = this.indexByTagGroup[param1Byte - 50];
        if (index != null)
          return index; 
      } 
      switch (param1Byte) {
        case 50:
          return makeTagGroupIndex((byte)50, ConstantPool.TAGS_IN_ORDER);
        case 51:
          return makeTagGroupIndex((byte)51, ConstantPool.LOADABLE_VALUE_TAGS);
        case 52:
          return makeTagGroupIndex((byte)52, ConstantPool.ANY_MEMBER_TAGS);
        case 53:
          return null;
      } 
      throw new AssertionError("bad tag group " + param1Byte);
    }
    
    public ConstantPool.Index getMemberIndex(byte param1Byte, ConstantPool.ClassEntry param1ClassEntry) {
      if (param1ClassEntry == null)
        throw new RuntimeException("missing class reference for " + ConstantPool.tagName(param1Byte)); 
      if (this.indexByTagAndClass == null)
        this.indexByTagAndClass = new Index[19][]; 
      ConstantPool.Index index = getIndexByTag((byte)7);
      ConstantPool.Index[] arrayOfIndex = this.indexByTagAndClass[param1Byte];
      if (arrayOfIndex == null) {
        ConstantPool.Index index1 = getIndexByTag(param1Byte);
        int[] arrayOfInt = new int[index1.size()];
        byte b;
        for (b = 0; b < arrayOfInt.length; b++) {
          ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)index1.get(b);
          int j = index.indexOf(memberEntry.classRef);
          arrayOfInt[b] = j;
        } 
        arrayOfIndex = ConstantPool.partition(index1, arrayOfInt);
        for (b = 0; b < arrayOfIndex.length; b++)
          assert arrayOfIndex[b] == null || arrayOfIndex[b].assertIsSorted(); 
        this.indexByTagAndClass[param1Byte] = arrayOfIndex;
      } 
      int i = index.indexOf(param1ClassEntry);
      return arrayOfIndex[i];
    }
    
    public int getOverloadingIndex(ConstantPool.MemberEntry param1MemberEntry) {
      ConstantPool.Index index = getMemberIndex(param1MemberEntry.tag, param1MemberEntry.classRef);
      ConstantPool.Utf8Entry utf8Entry = param1MemberEntry.descRef.nameRef;
      byte b1 = 0;
      for (byte b2 = 0; b2 < index.cpMap.length; b2++) {
        ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)index.cpMap[b2];
        if (memberEntry.equals(param1MemberEntry))
          return b1; 
        if (memberEntry.descRef.nameRef.equals(utf8Entry))
          b1++; 
      } 
      throw new RuntimeException("should not reach here");
    }
    
    public ConstantPool.MemberEntry getOverloadingForIndex(byte param1Byte, ConstantPool.ClassEntry param1ClassEntry, String param1String, int param1Int) {
      assert param1String.equals(param1String.intern());
      ConstantPool.Index index = getMemberIndex(param1Byte, param1ClassEntry);
      byte b1 = 0;
      for (byte b2 = 0; b2 < index.cpMap.length; b2++) {
        ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)index.cpMap[b2];
        if (memberEntry.descRef.nameRef.stringValue().equals(param1String)) {
          if (b1 == param1Int)
            return memberEntry; 
          b1++;
        } 
      } 
      throw new RuntimeException("should not reach here");
    }
    
    public boolean haveNumbers() {
      for (byte b : ConstantPool.NUMBER_TAGS) {
        if (getIndexByTag(b).size() > 0)
          return true; 
      } 
      return false;
    }
    
    public boolean haveExtraTags() {
      for (byte b : ConstantPool.EXTRA_TAGS) {
        if (getIndexByTag(b).size() > 0)
          return true; 
      } 
      return false;
    }
  }
  
  public static class InvokeDynamicEntry extends Entry {
    final ConstantPool.BootstrapMethodEntry bssRef;
    
    final ConstantPool.DescriptorEntry descRef;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.bssRef : ((param1Int == 1) ? this.descRef : null); }
    
    protected int computeValueHash() {
      int i = this.descRef.hashCode();
      return this.bssRef.hashCode() + (i << 8) ^ i;
    }
    
    InvokeDynamicEntry(ConstantPool.BootstrapMethodEntry param1BootstrapMethodEntry, ConstantPool.DescriptorEntry param1DescriptorEntry) {
      super((byte)18);
      this.bssRef = param1BootstrapMethodEntry;
      this.descRef = param1DescriptorEntry;
      hashCode();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != InvokeDynamicEntry.class)
        return false; 
      InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry)param1Object;
      return (this.bssRef.eq(invokeDynamicEntry.bssRef) && this.descRef.eq(invokeDynamicEntry.descRef));
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry)param1Object;
        if (Utils.SORT_INDY_BSS_MAJOR)
          i = this.bssRef.compareTo(invokeDynamicEntry.bssRef); 
        if (i == 0)
          i = this.descRef.compareTo(invokeDynamicEntry.descRef); 
        if (i == 0)
          i = this.bssRef.compareTo(invokeDynamicEntry.bssRef); 
      } 
      return i;
    }
    
    public String stringValue() { return stringValueOf(this.bssRef, this.descRef); }
    
    static String stringValueOf(ConstantPool.BootstrapMethodEntry param1BootstrapMethodEntry, ConstantPool.DescriptorEntry param1DescriptorEntry) { return "Indy:" + param1BootstrapMethodEntry.stringValue() + "." + param1DescriptorEntry.stringValue(); }
  }
  
  public static abstract class LiteralEntry extends Entry {
    protected LiteralEntry(byte param1Byte) { super(param1Byte); }
    
    public abstract Comparable<?> literalValue();
  }
  
  public static class MemberEntry extends Entry {
    final ConstantPool.ClassEntry classRef;
    
    final ConstantPool.DescriptorEntry descRef;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.classRef : ((param1Int == 1) ? this.descRef : null); }
    
    protected int computeValueHash() {
      int i = this.descRef.hashCode();
      return this.classRef.hashCode() + (i << 8) ^ i;
    }
    
    MemberEntry(byte param1Byte, ConstantPool.ClassEntry param1ClassEntry, ConstantPool.DescriptorEntry param1DescriptorEntry) {
      super(param1Byte);
      assert ConstantPool.isMemberTag(param1Byte);
      this.classRef = param1ClassEntry;
      this.descRef = param1DescriptorEntry;
      hashCode();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != MemberEntry.class)
        return false; 
      MemberEntry memberEntry = (MemberEntry)param1Object;
      return (this.classRef.eq(memberEntry.classRef) && this.descRef.eq(memberEntry.descRef));
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        MemberEntry memberEntry = (MemberEntry)param1Object;
        if (Utils.SORT_MEMBERS_DESCR_MAJOR)
          i = this.descRef.compareTo(memberEntry.descRef); 
        if (i == 0)
          i = this.classRef.compareTo(memberEntry.classRef); 
        if (i == 0)
          i = this.descRef.compareTo(memberEntry.descRef); 
      } 
      return i;
    }
    
    public String stringValue() { return stringValueOf(this.tag, this.classRef, this.descRef); }
    
    static String stringValueOf(byte param1Byte, ConstantPool.ClassEntry param1ClassEntry, ConstantPool.DescriptorEntry param1DescriptorEntry) {
      assert ConstantPool.isMemberTag(param1Byte);
      switch (param1Byte) {
        case 9:
          str = "Field:";
          return str + ConstantPool.qualifiedStringValue(param1ClassEntry, param1DescriptorEntry);
        case 10:
          str = "Method:";
          return str + ConstantPool.qualifiedStringValue(param1ClassEntry, param1DescriptorEntry);
        case 11:
          str = "IMethod:";
          return str + ConstantPool.qualifiedStringValue(param1ClassEntry, param1DescriptorEntry);
      } 
      String str = param1Byte + "???";
      return str + ConstantPool.qualifiedStringValue(param1ClassEntry, param1DescriptorEntry);
    }
    
    public boolean isMethod() { return this.descRef.isMethod(); }
  }
  
  public static class MethodHandleEntry extends Entry {
    final int refKind;
    
    final ConstantPool.MemberEntry memRef;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.memRef : null; }
    
    protected int computeValueHash() {
      int i = this.refKind;
      return this.memRef.hashCode() + (i << 8) ^ i;
    }
    
    MethodHandleEntry(byte param1Byte, ConstantPool.MemberEntry param1MemberEntry) {
      super((byte)15);
      assert ConstantPool.isRefKind(param1Byte);
      this.refKind = param1Byte;
      this.memRef = param1MemberEntry;
      hashCode();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != MethodHandleEntry.class)
        return false; 
      MethodHandleEntry methodHandleEntry = (MethodHandleEntry)param1Object;
      return (this.refKind == methodHandleEntry.refKind && this.memRef.eq(methodHandleEntry.memRef));
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        MethodHandleEntry methodHandleEntry = (MethodHandleEntry)param1Object;
        if (Utils.SORT_HANDLES_KIND_MAJOR)
          i = this.refKind - methodHandleEntry.refKind; 
        if (i == 0)
          i = this.memRef.compareTo(methodHandleEntry.memRef); 
        if (i == 0)
          i = this.refKind - methodHandleEntry.refKind; 
      } 
      return i;
    }
    
    public static String stringValueOf(int param1Int, ConstantPool.MemberEntry param1MemberEntry) { return ConstantPool.refKindName(param1Int) + ":" + param1MemberEntry.stringValue(); }
    
    public String stringValue() { return stringValueOf(this.refKind, this.memRef); }
  }
  
  public static class MethodTypeEntry extends Entry {
    final ConstantPool.SignatureEntry typeRef;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.typeRef : null; }
    
    protected int computeValueHash() { return this.typeRef.hashCode() + this.tag; }
    
    MethodTypeEntry(ConstantPool.SignatureEntry param1SignatureEntry) {
      super((byte)16);
      this.typeRef = param1SignatureEntry;
      hashCode();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || param1Object.getClass() != MethodTypeEntry.class)
        return false; 
      MethodTypeEntry methodTypeEntry = (MethodTypeEntry)param1Object;
      return this.typeRef.eq(methodTypeEntry.typeRef);
    }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        MethodTypeEntry methodTypeEntry = (MethodTypeEntry)param1Object;
        i = this.typeRef.compareTo(methodTypeEntry.typeRef);
      } 
      return i;
    }
    
    public String stringValue() { return this.typeRef.stringValue(); }
  }
  
  public static class NumberEntry extends LiteralEntry {
    final Number value;
    
    NumberEntry(Number param1Number) {
      super(ConstantPool.numberTagOf(param1Number));
      this.value = param1Number;
      hashCode();
    }
    
    protected int computeValueHash() { return this.value.hashCode(); }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == NumberEntry.class && ((NumberEntry)param1Object).value.equals(this.value)); }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        Comparable comparable = (Comparable)this.value;
        i = comparable.compareTo(((NumberEntry)param1Object).value);
      } 
      return i;
    }
    
    public Number numberValue() { return this.value; }
    
    public Comparable<?> literalValue() { return (Comparable)this.value; }
    
    public String stringValue() { return this.value.toString(); }
  }
  
  public static class SignatureEntry extends Entry {
    final ConstantPool.Utf8Entry formRef;
    
    final ConstantPool.ClassEntry[] classRefs;
    
    String value;
    
    ConstantPool.Utf8Entry asUtf8Entry;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.formRef : ((param1Int - 1 < this.classRefs.length) ? this.classRefs[param1Int - 1] : null); }
    
    SignatureEntry(String param1String) {
      super((byte)13);
      param1String = param1String.intern();
      this.value = param1String;
      String[] arrayOfString = ConstantPool.structureSignature(param1String);
      this.formRef = ConstantPool.getUtf8Entry(arrayOfString[0]);
      this.classRefs = new ConstantPool.ClassEntry[arrayOfString.length - 1];
      for (byte b = 1; b < arrayOfString.length; b++)
        this.classRefs[b - true] = ConstantPool.getClassEntry(arrayOfString[b]); 
      hashCode();
    }
    
    protected int computeValueHash() {
      stringValue();
      return this.value.hashCode() + this.tag;
    }
    
    public ConstantPool.Utf8Entry asUtf8Entry() {
      if (this.asUtf8Entry == null)
        this.asUtf8Entry = ConstantPool.getUtf8Entry(stringValue()); 
      return this.asUtf8Entry;
    }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == SignatureEntry.class && ((SignatureEntry)param1Object).value.equals(this.value)); }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0) {
        SignatureEntry signatureEntry = (SignatureEntry)param1Object;
        i = ConstantPool.compareSignatures(this.value, signatureEntry.value);
      } 
      return i;
    }
    
    public String stringValue() {
      if (this.value == null)
        this.value = stringValueOf(this.formRef, this.classRefs); 
      return this.value;
    }
    
    static String stringValueOf(ConstantPool.Utf8Entry param1Utf8Entry, ConstantPool.ClassEntry[] param1ArrayOfClassEntry) {
      String[] arrayOfString = new String[1 + param1ArrayOfClassEntry.length];
      arrayOfString[0] = param1Utf8Entry.stringValue();
      for (byte b = 1; b < arrayOfString.length; b++)
        arrayOfString[b] = param1ArrayOfClassEntry[b - true].stringValue(); 
      return ConstantPool.flattenSignature(arrayOfString).intern();
    }
    
    public int computeSize(boolean param1Boolean) {
      String str = this.formRef.stringValue();
      boolean bool = false;
      int i = 1;
      if (isMethod()) {
        bool = true;
        i = str.indexOf(')');
      } 
      byte b1 = 0;
      for (byte b2 = bool; b2 < i; b2++) {
        switch (str.charAt(b2)) {
          case 'D':
          case 'J':
            if (param1Boolean)
              b1++; 
            b1++;
            break;
          case '[':
            while (str.charAt(b2) == '[')
              b2++; 
            b1++;
            break;
          case ';':
            break;
          default:
            assert 0 <= "BSCIJFDZLV([".indexOf(str.charAt(b2));
            b1++;
            break;
        } 
      } 
      return b1;
    }
    
    public boolean isMethod() { return (this.formRef.stringValue().charAt(0) == '('); }
    
    public byte getLiteralTag() {
      switch (this.formRef.stringValue().charAt(0)) {
        case 'I':
          return 3;
        case 'J':
          return 5;
        case 'F':
          return 4;
        case 'D':
          return 6;
        case 'B':
        case 'C':
        case 'S':
        case 'Z':
          return 3;
        case 'L':
          return 8;
      } 
      assert false;
      return 0;
    }
    
    public String prettyString() {
      String str;
      if (isMethod()) {
        str = this.formRef.stringValue();
        str = str.substring(0, 1 + str.indexOf(')'));
      } else {
        str = "/" + this.formRef.stringValue();
      } 
      int i;
      while ((i = str.indexOf(';')) >= 0)
        str = str.substring(0, i) + str.substring(i + 1); 
      return str;
    }
  }
  
  public static class StringEntry extends LiteralEntry {
    final ConstantPool.Utf8Entry ref;
    
    public ConstantPool.Entry getRef(int param1Int) { return (param1Int == 0) ? this.ref : null; }
    
    StringEntry(ConstantPool.Entry param1Entry) {
      super((byte)8);
      this.ref = (ConstantPool.Utf8Entry)param1Entry;
      hashCode();
    }
    
    protected int computeValueHash() { return this.ref.hashCode() + this.tag; }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == StringEntry.class && ((StringEntry)param1Object).ref.eq(this.ref)); }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0)
        i = this.ref.compareTo(((StringEntry)param1Object).ref); 
      return i;
    }
    
    public Comparable<?> literalValue() { return this.ref.stringValue(); }
    
    public String stringValue() { return this.ref.stringValue(); }
  }
  
  public static class Utf8Entry extends Entry {
    final String value;
    
    Utf8Entry(String param1String) {
      super((byte)1);
      this.value = param1String.intern();
      hashCode();
    }
    
    protected int computeValueHash() { return this.value.hashCode(); }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == Utf8Entry.class && ((Utf8Entry)param1Object).value.equals(this.value)); }
    
    public int compareTo(Object param1Object) {
      int i = superCompareTo(param1Object);
      if (i == 0)
        i = this.value.compareTo(((Utf8Entry)param1Object).value); 
      return i;
    }
    
    public String stringValue() { return this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\ConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */