package jdk.internal.org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader {
  static final boolean SIGNATURES = true;
  
  static final boolean ANNOTATIONS = true;
  
  static final boolean FRAMES = true;
  
  static final boolean WRITER = true;
  
  static final boolean RESIZE = true;
  
  public static final int SKIP_CODE = 1;
  
  public static final int SKIP_DEBUG = 2;
  
  public static final int SKIP_FRAMES = 4;
  
  public static final int EXPAND_FRAMES = 8;
  
  public final byte[] b;
  
  private final int[] items;
  
  private final String[] strings;
  
  private final int maxStringLength;
  
  public final int header;
  
  public ClassReader(byte[] paramArrayOfByte) { this(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public ClassReader(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.b = paramArrayOfByte;
    if (readShort(paramInt1 + 6) > 52)
      throw new IllegalArgumentException(); 
    this.items = new int[readUnsignedShort(paramInt1 + 8)];
    int i = this.items.length;
    this.strings = new String[i];
    int j = 0;
    int k = paramInt1 + 10;
    for (byte b1 = 1; b1 < i; b1++) {
      int m;
      this.items[b1] = k + 1;
      switch (paramArrayOfByte[k]) {
        case 3:
        case 4:
        case 9:
        case 10:
        case 11:
        case 12:
        case 18:
          m = 5;
          break;
        case 5:
        case 6:
          m = 9;
          b1++;
          break;
        case 1:
          m = 3 + readUnsignedShort(k + 1);
          if (m > j)
            j = m; 
          break;
        case 15:
          m = 4;
          break;
        default:
          m = 3;
          break;
      } 
      k += m;
    } 
    this.maxStringLength = j;
    this.header = k;
  }
  
  public int getAccess() { return readUnsignedShort(this.header); }
  
  public String getClassName() { return readClass(this.header + 2, new char[this.maxStringLength]); }
  
  public String getSuperName() { return readClass(this.header + 4, new char[this.maxStringLength]); }
  
  public String[] getInterfaces() {
    int i = this.header + 6;
    int j = readUnsignedShort(i);
    String[] arrayOfString = new String[j];
    if (j > 0) {
      char[] arrayOfChar = new char[this.maxStringLength];
      for (byte b1 = 0; b1 < j; b1++) {
        i += 2;
        arrayOfString[b1] = readClass(i, arrayOfChar);
      } 
    } 
    return arrayOfString;
  }
  
  void copyPool(ClassWriter paramClassWriter) {
    char[] arrayOfChar = new char[this.maxStringLength];
    int i = this.items.length;
    Item[] arrayOfItem = new Item[i];
    int j;
    for (j = 1; j < i; j++) {
      String str;
      int m;
      int k = this.items[j];
      byte b1 = this.b[k - 1];
      Item item = new Item(j);
      switch (b1) {
        case 9:
        case 10:
        case 11:
          m = this.items[readUnsignedShort(k + 2)];
          item.set(b1, readClass(k, arrayOfChar), readUTF8(m, arrayOfChar), readUTF8(m + 2, arrayOfChar));
          break;
        case 3:
          item.set(readInt(k));
          break;
        case 4:
          item.set(Float.intBitsToFloat(readInt(k)));
          break;
        case 12:
          item.set(b1, readUTF8(k, arrayOfChar), readUTF8(k + 2, arrayOfChar), null);
          break;
        case 5:
          item.set(readLong(k));
          j++;
          break;
        case 6:
          item.set(Double.longBitsToDouble(readLong(k)));
          j++;
          break;
        case 1:
          str = this.strings[j];
          if (str == null) {
            k = this.items[j];
            str = this.strings[j] = readUTF(k + 2, readUnsignedShort(k), arrayOfChar);
          } 
          item.set(b1, str, null, null);
          break;
        case 15:
          n = this.items[readUnsignedShort(k + 1)];
          m = this.items[readUnsignedShort(n + 2)];
          item.set(20 + readByte(k), readClass(n, arrayOfChar), readUTF8(m, arrayOfChar), readUTF8(m + 2, arrayOfChar));
          break;
        case 18:
          if (paramClassWriter.bootstrapMethods == null)
            copyBootstrapMethods(paramClassWriter, arrayOfItem, arrayOfChar); 
          m = this.items[readUnsignedShort(k + 2)];
          item.set(readUTF8(m, arrayOfChar), readUTF8(m + 2, arrayOfChar), readUnsignedShort(k));
          break;
        default:
          item.set(b1, readUTF8(k, arrayOfChar), null, null);
          break;
      } 
      int n = item.hashCode % arrayOfItem.length;
      item.next = arrayOfItem[n];
      arrayOfItem[n] = item;
    } 
    j = this.items[1] - 1;
    paramClassWriter.pool.putByteArray(this.b, j, this.header - j);
    paramClassWriter.items = arrayOfItem;
    paramClassWriter.threshold = (int)(0.75D * i);
    paramClassWriter.index = i;
  }
  
  private void copyBootstrapMethods(ClassWriter paramClassWriter, Item[] paramArrayOfItem, char[] paramArrayOfChar) {
    int i = getAttributes();
    boolean bool = false;
    int j;
    for (j = readUnsignedShort(i); j > 0; j--) {
      String str = readUTF8(i + 2, paramArrayOfChar);
      if ("BootstrapMethods".equals(str)) {
        bool = true;
        break;
      } 
      i += 6 + readInt(i + 4);
    } 
    if (!bool)
      return; 
    j = readUnsignedShort(i + 8);
    int k = 0;
    int m = i + 10;
    while (k < j) {
      int n = m - i - 10;
      int i1 = readConst(readUnsignedShort(m), paramArrayOfChar).hashCode();
      for (int i2 = readUnsignedShort(m + 2); i2 > 0; i2--) {
        i1 ^= readConst(readUnsignedShort(m + 4), paramArrayOfChar).hashCode();
        m += 2;
      } 
      m += 4;
      Item item = new Item(k);
      item.set(n, i1 & 0x7FFFFFFF);
      int i3 = item.hashCode % paramArrayOfItem.length;
      item.next = paramArrayOfItem[i3];
      paramArrayOfItem[i3] = item;
      k++;
    } 
    k = readInt(i + 4);
    ByteVector byteVector = new ByteVector(k + 62);
    byteVector.putByteArray(this.b, i + 10, k - 2);
    paramClassWriter.bootstrapMethodsCount = j;
    paramClassWriter.bootstrapMethods = byteVector;
  }
  
  public ClassReader(InputStream paramInputStream) throws IOException { this(readClass(paramInputStream, false)); }
  
  public ClassReader(String paramString) throws IOException { this(readClass(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class"), true)); }
  
  private static byte[] readClass(InputStream paramInputStream, boolean paramBoolean) throws IOException {
    if (paramInputStream == null)
      throw new IOException("Class not found"); 
    try {
      byte[] arrayOfByte = new byte[paramInputStream.available()];
      int i = 0;
      while (true) {
        int j = paramInputStream.read(arrayOfByte, i, arrayOfByte.length - i);
        if (j == -1) {
          if (i < arrayOfByte.length) {
            byte[] arrayOfByte1 = new byte[i];
            System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, i);
            arrayOfByte = arrayOfByte1;
          } 
          return arrayOfByte;
        } 
        i += j;
        if (i == arrayOfByte.length) {
          int k = paramInputStream.read();
          if (k < 0)
            return arrayOfByte; 
          byte[] arrayOfByte1 = new byte[arrayOfByte.length + 1000];
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, i);
          arrayOfByte1[i++] = (byte)k;
          arrayOfByte = arrayOfByte1;
        } 
      } 
    } finally {
      if (paramBoolean)
        paramInputStream.close(); 
    } 
  }
  
  public void accept(ClassVisitor paramClassVisitor, int paramInt) { accept(paramClassVisitor, new Attribute[0], paramInt); }
  
  public void accept(ClassVisitor paramClassVisitor, Attribute[] paramArrayOfAttribute, int paramInt) {
    int i = this.header;
    char[] arrayOfChar = new char[this.maxStringLength];
    Context context = new Context();
    context.attrs = paramArrayOfAttribute;
    context.flags = paramInt;
    context.buffer = arrayOfChar;
    int j = readUnsignedShort(i);
    String str1 = readClass(i + 2, arrayOfChar);
    String str2 = readClass(i + 4, arrayOfChar);
    String[] arrayOfString = new String[readUnsignedShort(i + 6)];
    i += 8;
    for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
      arrayOfString[b1] = readClass(i, arrayOfChar);
      i += 2;
    } 
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    Attribute attribute = null;
    i = getAttributes();
    int i3;
    for (i3 = readUnsignedShort(i); i3 > 0; i3--) {
      String str = readUTF8(i + 2, arrayOfChar);
      if ("SourceFile".equals(str)) {
        str4 = readUTF8(i + 8, arrayOfChar);
      } else if ("InnerClasses".equals(str)) {
        i2 = i + 8;
      } else if ("EnclosingMethod".equals(str)) {
        str6 = readClass(i + 8, arrayOfChar);
        int i4 = readUnsignedShort(i + 10);
        if (i4 != 0) {
          str7 = readUTF8(this.items[i4], arrayOfChar);
          str8 = readUTF8(this.items[i4] + 2, arrayOfChar);
        } 
      } else if ("Signature".equals(str)) {
        str3 = readUTF8(i + 8, arrayOfChar);
      } else if ("RuntimeVisibleAnnotations".equals(str)) {
        k = i + 8;
      } else if ("RuntimeVisibleTypeAnnotations".equals(str)) {
        n = i + 8;
      } else if ("Deprecated".equals(str)) {
        j |= 0x20000;
      } else if ("Synthetic".equals(str)) {
        j |= 0x41000;
      } else if ("SourceDebugExtension".equals(str)) {
        int i4 = readInt(i + 4);
        str5 = readUTF(i + 8, i4, new char[i4]);
      } else if ("RuntimeInvisibleAnnotations".equals(str)) {
        m = i + 8;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(str)) {
        i1 = i + 8;
      } else if ("BootstrapMethods".equals(str)) {
        int[] arrayOfInt = new int[readUnsignedShort(i + 8)];
        byte b2 = 0;
        int i4 = i + 10;
        while (b2 < arrayOfInt.length) {
          arrayOfInt[b2] = i4;
          i4 += (2 + readUnsignedShort(i4 + 2) << 1);
          b2++;
        } 
        context.bootstrapMethods = arrayOfInt;
      } else {
        Attribute attribute1 = readAttribute(paramArrayOfAttribute, str, i + 8, readInt(i + 4), arrayOfChar, -1, null);
        if (attribute1 != null) {
          attribute1.next = attribute;
          attribute = attribute1;
        } 
      } 
      i += 6 + readInt(i + 4);
    } 
    paramClassVisitor.visit(readInt(this.items[1] - 7), j, str1, str3, str2, arrayOfString);
    if ((paramInt & 0x2) == 0 && (str4 != null || str5 != null))
      paramClassVisitor.visitSource(str4, str5); 
    if (str6 != null)
      paramClassVisitor.visitOuterClass(str6, str7, str8); 
    if (k != 0) {
      i3 = readUnsignedShort(k);
      int i4 = k + 2;
      while (i3 > 0) {
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), true));
        i3--;
      } 
    } 
    if (m != 0) {
      i3 = readUnsignedShort(m);
      int i4 = m + 2;
      while (i3 > 0) {
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), false));
        i3--;
      } 
    } 
    if (n != 0) {
      i3 = readUnsignedShort(n);
      int i4 = n + 2;
      while (i3 > 0) {
        i4 = readAnnotationTarget(context, i4);
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(context.typeRef, context.typePath, readUTF8(i4, arrayOfChar), true));
        i3--;
      } 
    } 
    if (i1 != 0) {
      i3 = readUnsignedShort(i1);
      int i4 = i1 + 2;
      while (i3 > 0) {
        i4 = readAnnotationTarget(context, i4);
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(context.typeRef, context.typePath, readUTF8(i4, arrayOfChar), false));
        i3--;
      } 
    } 
    while (attribute != null) {
      Attribute attribute1 = attribute.next;
      attribute.next = null;
      paramClassVisitor.visitAttribute(attribute);
      attribute = attribute1;
    } 
    if (i2 != 0) {
      i3 = i2 + 2;
      for (int i4 = readUnsignedShort(i2); i4 > 0; i4--) {
        paramClassVisitor.visitInnerClass(readClass(i3, arrayOfChar), readClass(i3 + 2, arrayOfChar), readUTF8(i3 + 4, arrayOfChar), readUnsignedShort(i3 + 6));
        i3 += 8;
      } 
    } 
    i = this.header + 10 + 2 * arrayOfString.length;
    for (i3 = readUnsignedShort(i - 2); i3 > 0; i3--)
      i = readField(paramClassVisitor, context, i); 
    i += 2;
    for (i3 = readUnsignedShort(i - 2); i3 > 0; i3--)
      i = readMethod(paramClassVisitor, context, i); 
    paramClassVisitor.visitEnd();
  }
  
  private int readField(ClassVisitor paramClassVisitor, Context paramContext, int paramInt) {
    char[] arrayOfChar = paramContext.buffer;
    int i = readUnsignedShort(paramInt);
    String str1 = readUTF8(paramInt + 2, arrayOfChar);
    String str2 = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    String str3 = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    Object object = null;
    Attribute attribute = null;
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--) {
      String str = readUTF8(paramInt + 2, arrayOfChar);
      if ("ConstantValue".equals(str)) {
        int i2 = readUnsignedShort(paramInt + 8);
        object = (i2 == 0) ? null : readConst(i2, arrayOfChar);
      } else if ("Signature".equals(str)) {
        str3 = readUTF8(paramInt + 8, arrayOfChar);
      } else if ("Deprecated".equals(str)) {
        i |= 0x20000;
      } else if ("Synthetic".equals(str)) {
        i |= 0x41000;
      } else if ("RuntimeVisibleAnnotations".equals(str)) {
        j = paramInt + 8;
      } else if ("RuntimeVisibleTypeAnnotations".equals(str)) {
        m = paramInt + 8;
      } else if ("RuntimeInvisibleAnnotations".equals(str)) {
        k = paramInt + 8;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(str)) {
        n = paramInt + 8;
      } else {
        Attribute attribute1 = readAttribute(paramContext.attrs, str, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (attribute1 != null) {
          attribute1.next = attribute;
          attribute = attribute1;
        } 
      } 
      paramInt += 6 + readInt(paramInt + 4);
    } 
    paramInt += 2;
    FieldVisitor fieldVisitor = paramClassVisitor.visitField(i, str1, str2, str3, object);
    if (fieldVisitor == null)
      return paramInt; 
    if (j != 0) {
      int i2 = readUnsignedShort(j);
      int i3 = j + 2;
      while (i2 > 0) {
        i3 = readAnnotationValues(i3 + 2, arrayOfChar, true, fieldVisitor.visitAnnotation(readUTF8(i3, arrayOfChar), true));
        i2--;
      } 
    } 
    if (k != 0) {
      int i2 = readUnsignedShort(k);
      int i3 = k + 2;
      while (i2 > 0) {
        i3 = readAnnotationValues(i3 + 2, arrayOfChar, true, fieldVisitor.visitAnnotation(readUTF8(i3, arrayOfChar), false));
        i2--;
      } 
    } 
    if (m != 0) {
      int i2 = readUnsignedShort(m);
      int i3 = m + 2;
      while (i2 > 0) {
        i3 = readAnnotationTarget(paramContext, i3);
        i3 = readAnnotationValues(i3 + 2, arrayOfChar, true, fieldVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i3, arrayOfChar), true));
        i2--;
      } 
    } 
    if (n != 0) {
      int i2 = readUnsignedShort(n);
      int i3 = n + 2;
      while (i2 > 0) {
        i3 = readAnnotationTarget(paramContext, i3);
        i3 = readAnnotationValues(i3 + 2, arrayOfChar, true, fieldVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i3, arrayOfChar), false));
        i2--;
      } 
    } 
    while (attribute != null) {
      Attribute attribute1 = attribute.next;
      attribute.next = null;
      fieldVisitor.visitAttribute(attribute);
      attribute = attribute1;
    } 
    fieldVisitor.visitEnd();
    return paramInt;
  }
  
  private int readMethod(ClassVisitor paramClassVisitor, Context paramContext, int paramInt) {
    char[] arrayOfChar = paramContext.buffer;
    paramContext.access = readUnsignedShort(paramInt);
    paramContext.name = readUTF8(paramInt + 2, arrayOfChar);
    paramContext.desc = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    int i = 0;
    int j = 0;
    String[] arrayOfString = null;
    String str = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = paramInt;
    Attribute attribute = null;
    for (int i7 = readUnsignedShort(paramInt); i7 > 0; i7--) {
      String str1 = readUTF8(paramInt + 2, arrayOfChar);
      if ("Code".equals(str1)) {
        if ((paramContext.flags & true) == 0)
          i = paramInt + 8; 
      } else if ("Exceptions".equals(str1)) {
        arrayOfString = new String[readUnsignedShort(paramInt + 8)];
        j = paramInt + 10;
        for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
          arrayOfString[b1] = readClass(j, arrayOfChar);
          j += 2;
        } 
      } else if ("Signature".equals(str1)) {
        str = readUTF8(paramInt + 8, arrayOfChar);
      } else if ("Deprecated".equals(str1)) {
        paramContext.access |= 0x20000;
      } else if ("RuntimeVisibleAnnotations".equals(str1)) {
        m = paramInt + 8;
      } else if ("RuntimeVisibleTypeAnnotations".equals(str1)) {
        i1 = paramInt + 8;
      } else if ("AnnotationDefault".equals(str1)) {
        i3 = paramInt + 8;
      } else if ("Synthetic".equals(str1)) {
        paramContext.access |= 0x41000;
      } else if ("RuntimeInvisibleAnnotations".equals(str1)) {
        n = paramInt + 8;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(str1)) {
        i2 = paramInt + 8;
      } else if ("RuntimeVisibleParameterAnnotations".equals(str1)) {
        i4 = paramInt + 8;
      } else if ("RuntimeInvisibleParameterAnnotations".equals(str1)) {
        i5 = paramInt + 8;
      } else if ("MethodParameters".equals(str1)) {
        k = paramInt + 8;
      } else {
        Attribute attribute1 = readAttribute(paramContext.attrs, str1, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (attribute1 != null) {
          attribute1.next = attribute;
          attribute = attribute1;
        } 
      } 
      paramInt += 6 + readInt(paramInt + 4);
    } 
    paramInt += 2;
    MethodVisitor methodVisitor = paramClassVisitor.visitMethod(paramContext.access, paramContext.name, paramContext.desc, str, arrayOfString);
    if (methodVisitor == null)
      return paramInt; 
    if (methodVisitor instanceof MethodWriter) {
      MethodWriter methodWriter = (MethodWriter)methodVisitor;
      if (methodWriter.cw.cr == this && str == methodWriter.signature) {
        boolean bool = false;
        if (arrayOfString == null) {
          bool = (methodWriter.exceptionCount == 0) ? 1 : 0;
        } else if (arrayOfString.length == methodWriter.exceptionCount) {
          bool = true;
          for (int i8 = arrayOfString.length - 1; i8 >= 0; i8--) {
            j -= 2;
            if (methodWriter.exceptions[i8] != readUnsignedShort(j)) {
              bool = false;
              break;
            } 
          } 
        } 
        if (bool) {
          methodWriter.classReaderOffset = i6;
          methodWriter.classReaderLength = paramInt - i6;
          return paramInt;
        } 
      } 
    } 
    if (k != 0) {
      byte b1 = this.b[k] & 0xFF;
      int i8;
      for (i8 = k + 1; b1 > 0; i8 += 4) {
        methodVisitor.visitParameter(readUTF8(i8, arrayOfChar), readUnsignedShort(i8 + 2));
        b1--;
      } 
    } 
    if (i3 != 0) {
      AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
      readAnnotationValue(i3, arrayOfChar, null, annotationVisitor);
      if (annotationVisitor != null)
        annotationVisitor.visitEnd(); 
    } 
    if (m != 0) {
      int i8 = readUnsignedShort(m);
      int i9 = m + 2;
      while (i8 > 0) {
        i9 = readAnnotationValues(i9 + 2, arrayOfChar, true, methodVisitor.visitAnnotation(readUTF8(i9, arrayOfChar), true));
        i8--;
      } 
    } 
    if (n != 0) {
      int i8 = readUnsignedShort(n);
      int i9 = n + 2;
      while (i8 > 0) {
        i9 = readAnnotationValues(i9 + 2, arrayOfChar, true, methodVisitor.visitAnnotation(readUTF8(i9, arrayOfChar), false));
        i8--;
      } 
    } 
    if (i1 != 0) {
      int i8 = readUnsignedShort(i1);
      int i9 = i1 + 2;
      while (i8 > 0) {
        i9 = readAnnotationTarget(paramContext, i9);
        i9 = readAnnotationValues(i9 + 2, arrayOfChar, true, methodVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i9, arrayOfChar), true));
        i8--;
      } 
    } 
    if (i2 != 0) {
      int i8 = readUnsignedShort(i2);
      int i9 = i2 + 2;
      while (i8 > 0) {
        i9 = readAnnotationTarget(paramContext, i9);
        i9 = readAnnotationValues(i9 + 2, arrayOfChar, true, methodVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i9, arrayOfChar), false));
        i8--;
      } 
    } 
    if (i4 != 0)
      readParameterAnnotations(methodVisitor, paramContext, i4, true); 
    if (i5 != 0)
      readParameterAnnotations(methodVisitor, paramContext, i5, false); 
    while (attribute != null) {
      Attribute attribute1 = attribute.next;
      attribute.next = null;
      methodVisitor.visitAttribute(attribute);
      attribute = attribute1;
    } 
    if (i != 0) {
      methodVisitor.visitCode();
      readCode(methodVisitor, paramContext, i);
    } 
    methodVisitor.visitEnd();
    return paramInt;
  }
  
  private void readCode(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt) {
    byte[] arrayOfByte = this.b;
    char[] arrayOfChar = paramContext.buffer;
    int i = readUnsignedShort(paramInt);
    int j = readUnsignedShort(paramInt + 2);
    int k = readInt(paramInt + 4);
    paramInt += 8;
    int m = paramInt;
    int n = paramInt + k;
    Label[] arrayOfLabel = paramContext.labels = new Label[k + 2];
    readLabel(k + 1, arrayOfLabel);
    while (paramInt < n) {
      int i9;
      int i8 = paramInt - m;
      byte b5 = arrayOfByte[paramInt] & 0xFF;
      switch (ClassWriter.TYPE[b5]) {
        case 0:
        case 4:
          paramInt++;
          continue;
        case 9:
          readLabel(i8 + readShort(paramInt + 1), arrayOfLabel);
          paramInt += 3;
          continue;
        case 10:
          readLabel(i8 + readInt(paramInt + 1), arrayOfLabel);
          paramInt += 5;
          continue;
        case 17:
          b5 = arrayOfByte[paramInt + 1] & 0xFF;
          if (b5 == 132) {
            paramInt += 6;
            continue;
          } 
          paramInt += 4;
          continue;
        case 14:
          paramInt = paramInt + 4 - (i8 & 0x3);
          readLabel(i8 + readInt(paramInt), arrayOfLabel);
          for (i9 = readInt(paramInt + 8) - readInt(paramInt + 4) + 1; i9 > 0; i9--) {
            readLabel(i8 + readInt(paramInt + 12), arrayOfLabel);
            paramInt += 4;
          } 
          paramInt += 12;
          continue;
        case 15:
          paramInt = paramInt + 4 - (i8 & 0x3);
          readLabel(i8 + readInt(paramInt), arrayOfLabel);
          for (i9 = readInt(paramInt + 4); i9 > 0; i9--) {
            readLabel(i8 + readInt(paramInt + 12), arrayOfLabel);
            paramInt += 8;
          } 
          paramInt += 8;
          continue;
        case 1:
        case 3:
        case 11:
          paramInt += 2;
          continue;
        case 2:
        case 5:
        case 6:
        case 12:
        case 13:
          paramInt += 3;
          continue;
        case 7:
        case 8:
          paramInt += 5;
          continue;
      } 
      paramInt += 4;
    } 
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--) {
      Label label1 = readLabel(readUnsignedShort(paramInt + 2), arrayOfLabel);
      Label label2 = readLabel(readUnsignedShort(paramInt + 4), arrayOfLabel);
      Label label3 = readLabel(readUnsignedShort(paramInt + 6), arrayOfLabel);
      String str = readUTF8(this.items[readUnsignedShort(paramInt + 8)], arrayOfChar);
      paramMethodVisitor.visitTryCatchBlock(label1, label2, label3, str);
      paramInt += 8;
    } 
    paramInt += 2;
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = -1;
    byte b4 = -1;
    int i2 = 0;
    int i3 = 0;
    boolean bool1 = true;
    boolean bool2 = ((paramContext.flags & 0x8) != 0);
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    Context context = null;
    Attribute attribute = null;
    int i7;
    for (i7 = readUnsignedShort(paramInt); i7 > 0; i7--) {
      String str = readUTF8(paramInt + 2, arrayOfChar);
      if ("LocalVariableTable".equals(str)) {
        if ((paramContext.flags & 0x2) == 0) {
          i2 = paramInt + 8;
          int i8 = readUnsignedShort(paramInt + 8);
          int i9 = paramInt;
          while (i8 > 0) {
            int i10 = readUnsignedShort(i9 + 10);
            if (arrayOfLabel[i10] == null)
              (readLabel(i10, arrayOfLabel)).status |= 0x1; 
            i10 += readUnsignedShort(i9 + 12);
            if (arrayOfLabel[i10] == null)
              (readLabel(i10, arrayOfLabel)).status |= 0x1; 
            i9 += 10;
            i8--;
          } 
        } 
      } else if ("LocalVariableTypeTable".equals(str)) {
        i3 = paramInt + 8;
      } else if ("LineNumberTable".equals(str)) {
        if ((paramContext.flags & 0x2) == 0) {
          int i8 = readUnsignedShort(paramInt + 8);
          int i9 = paramInt;
          while (i8 > 0) {
            int i10 = readUnsignedShort(i9 + 10);
            if (arrayOfLabel[i10] == null)
              (readLabel(i10, arrayOfLabel)).status |= 0x1; 
            (arrayOfLabel[i10]).line = readUnsignedShort(i9 + 12);
            i9 += 4;
            i8--;
          } 
        } 
      } else if ("RuntimeVisibleTypeAnnotations".equals(str)) {
        arrayOfInt1 = readTypeAnnotations(paramMethodVisitor, paramContext, paramInt + 8, true);
        b3 = (arrayOfInt1.length == 0 || readByte(arrayOfInt1[0]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[0] + 1);
      } else if ("RuntimeInvisibleTypeAnnotations".equals(str)) {
        arrayOfInt2 = readTypeAnnotations(paramMethodVisitor, paramContext, paramInt + 8, false);
        b4 = (arrayOfInt2.length == 0 || readByte(arrayOfInt2[0]) < 67) ? -1 : readUnsignedShort(arrayOfInt2[0] + 1);
      } else if ("StackMapTable".equals(str)) {
        if ((paramContext.flags & 0x4) == 0) {
          i4 = paramInt + 10;
          i5 = readInt(paramInt + 4);
          i6 = readUnsignedShort(paramInt + 8);
        } 
      } else if ("StackMap".equals(str)) {
        if ((paramContext.flags & 0x4) == 0) {
          bool1 = false;
          i4 = paramInt + 10;
          i5 = readInt(paramInt + 4);
          i6 = readUnsignedShort(paramInt + 8);
        } 
      } else {
        for (byte b5 = 0; b5 < paramContext.attrs.length; b5++) {
          if ((paramContext.attrs[b5]).type.equals(str)) {
            Attribute attribute1 = paramContext.attrs[b5].read(this, paramInt + 8, readInt(paramInt + 4), arrayOfChar, m - 8, arrayOfLabel);
            if (attribute1 != null) {
              attribute1.next = attribute;
              attribute = attribute1;
            } 
          } 
        } 
      } 
      paramInt += 6 + readInt(paramInt + 4);
    } 
    paramInt += 2;
    if (i4 != 0) {
      context = paramContext;
      context.offset = -1;
      context.mode = 0;
      context.localCount = 0;
      context.localDiff = 0;
      context.stackCount = 0;
      context.local = new Object[j];
      context.stack = new Object[i];
      if (bool2)
        getImplicitFrame(paramContext); 
      for (i7 = i4; i7 < i4 + i5 - 2; i7++) {
        if (arrayOfByte[i7] == 8) {
          int i8 = readUnsignedShort(i7 + 1);
          if (i8 >= 0 && i8 < k && (arrayOfByte[m + i8] & 0xFF) == 187)
            readLabel(i8, arrayOfLabel); 
        } 
      } 
    } 
    paramInt = m;
    while (paramInt < n) {
      String str5;
      byte b7;
      String str4;
      byte b6;
      String str3;
      Object[] arrayOfObject;
      int i12;
      Label[] arrayOfLabel1;
      String str2;
      int i11;
      Handle handle;
      String str1;
      int[] arrayOfInt;
      int i10;
      int i9;
      int i8;
      i7 = paramInt - m;
      Label label = arrayOfLabel[i7];
      if (label != null) {
        paramMethodVisitor.visitLabel(label);
        if ((paramContext.flags & 0x2) == 0 && label.line > 0)
          paramMethodVisitor.visitLineNumber(label.line, label); 
      } 
      while (context != null && (context.offset == i7 || context.offset == -1)) {
        if (context.offset != -1)
          if (!bool1 || bool2) {
            paramMethodVisitor.visitFrame(-1, context.localCount, context.local, context.stackCount, context.stack);
          } else {
            paramMethodVisitor.visitFrame(context.mode, context.localDiff, context.local, context.stackCount, context.stack);
          }  
        if (i6 > 0) {
          i4 = readFrame(i4, bool1, bool2, context);
          i6--;
          continue;
        } 
        context = null;
      } 
      byte b5 = arrayOfByte[paramInt] & 0xFF;
      switch (ClassWriter.TYPE[b5]) {
        case 0:
          paramMethodVisitor.visitInsn(b5);
          paramInt++;
          break;
        case 4:
          if (b5 > 54) {
            b5 -= 59;
            paramMethodVisitor.visitVarInsn(54 + (b5 >> 2), b5 & 0x3);
          } else {
            b5 -= 26;
            paramMethodVisitor.visitVarInsn(21 + (b5 >> 2), b5 & 0x3);
          } 
          paramInt++;
          break;
        case 9:
          paramMethodVisitor.visitJumpInsn(b5, arrayOfLabel[i7 + readShort(paramInt + 1)]);
          paramInt += 3;
          break;
        case 10:
          paramMethodVisitor.visitJumpInsn(b5 - 33, arrayOfLabel[i7 + readInt(paramInt + 1)]);
          paramInt += 5;
          break;
        case 17:
          b5 = arrayOfByte[paramInt + 1] & 0xFF;
          if (b5 == 132) {
            paramMethodVisitor.visitIincInsn(readUnsignedShort(paramInt + 2), readShort(paramInt + 4));
            paramInt += 6;
            break;
          } 
          paramMethodVisitor.visitVarInsn(b5, readUnsignedShort(paramInt + 2));
          paramInt += 4;
          break;
        case 14:
          paramInt = paramInt + 4 - (i7 & 0x3);
          i8 = i7 + readInt(paramInt);
          i10 = readInt(paramInt + 4);
          i11 = readInt(paramInt + 8);
          arrayOfLabel1 = new Label[i11 - i10 + 1];
          paramInt += 12;
          for (b6 = 0; b6 < arrayOfLabel1.length; b6++) {
            arrayOfLabel1[b6] = arrayOfLabel[i7 + readInt(paramInt)];
            paramInt += 4;
          } 
          paramMethodVisitor.visitTableSwitchInsn(i10, i11, arrayOfLabel[i8], arrayOfLabel1);
          break;
        case 15:
          paramInt = paramInt + 4 - (i7 & 0x3);
          i8 = i7 + readInt(paramInt);
          i10 = readInt(paramInt + 4);
          arrayOfInt = new int[i10];
          arrayOfLabel1 = new Label[i10];
          paramInt += 8;
          for (b6 = 0; b6 < i10; b6++) {
            arrayOfInt[b6] = readInt(paramInt);
            arrayOfLabel1[b6] = arrayOfLabel[i7 + readInt(paramInt + 4)];
            paramInt += 8;
          } 
          paramMethodVisitor.visitLookupSwitchInsn(arrayOfLabel[i8], arrayOfInt, arrayOfLabel1);
          break;
        case 3:
          paramMethodVisitor.visitVarInsn(b5, arrayOfByte[paramInt + 1] & 0xFF);
          paramInt += 2;
          break;
        case 1:
          paramMethodVisitor.visitIntInsn(b5, arrayOfByte[paramInt + 1]);
          paramInt += 2;
          break;
        case 2:
          paramMethodVisitor.visitIntInsn(b5, readShort(paramInt + 1));
          paramInt += 3;
          break;
        case 11:
          paramMethodVisitor.visitLdcInsn(readConst(arrayOfByte[paramInt + 1] & 0xFF, arrayOfChar));
          paramInt += 2;
          break;
        case 12:
          paramMethodVisitor.visitLdcInsn(readConst(readUnsignedShort(paramInt + 1), arrayOfChar));
          paramInt += 3;
          break;
        case 6:
        case 7:
          i8 = this.items[readUnsignedShort(paramInt + 1)];
          i10 = (arrayOfByte[i8 - 1] == 11) ? 1 : 0;
          str1 = readClass(i8, arrayOfChar);
          i8 = this.items[readUnsignedShort(i8 + 2)];
          str2 = readUTF8(i8, arrayOfChar);
          str3 = readUTF8(i8 + 2, arrayOfChar);
          if (b5 < 182) {
            paramMethodVisitor.visitFieldInsn(b5, str1, str2, str3);
          } else {
            paramMethodVisitor.visitMethodInsn(b5, str1, str2, str3, i10);
          } 
          if (b5 == 185) {
            paramInt += 5;
            break;
          } 
          paramInt += 3;
          break;
        case 8:
          i8 = this.items[readUnsignedShort(paramInt + 1)];
          i9 = paramContext.bootstrapMethods[readUnsignedShort(i8)];
          handle = (Handle)readConst(readUnsignedShort(i9), arrayOfChar);
          i12 = readUnsignedShort(i9 + 2);
          arrayOfObject = new Object[i12];
          i9 += 4;
          for (b7 = 0; b7 < i12; b7++) {
            arrayOfObject[b7] = readConst(readUnsignedShort(i9), arrayOfChar);
            i9 += 2;
          } 
          i8 = this.items[readUnsignedShort(i8 + 2)];
          str4 = readUTF8(i8, arrayOfChar);
          str5 = readUTF8(i8 + 2, arrayOfChar);
          paramMethodVisitor.visitInvokeDynamicInsn(str4, str5, handle, arrayOfObject);
          paramInt += 5;
          break;
        case 5:
          paramMethodVisitor.visitTypeInsn(b5, readClass(paramInt + 1, arrayOfChar));
          paramInt += 3;
          break;
        case 13:
          paramMethodVisitor.visitIincInsn(arrayOfByte[paramInt + 1] & 0xFF, arrayOfByte[paramInt + 2]);
          paramInt += 3;
          break;
        default:
          paramMethodVisitor.visitMultiANewArrayInsn(readClass(paramInt + 1, arrayOfChar), arrayOfByte[paramInt + 3] & 0xFF);
          paramInt += 4;
          break;
      } 
      while (arrayOfInt1 != null && b1 < arrayOfInt1.length && b3 <= i7) {
        if (b3 == i7) {
          i8 = readAnnotationTarget(paramContext, arrayOfInt1[b1]);
          readAnnotationValues(i8 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i8, arrayOfChar), true));
        } 
        b3 = (++b1 >= arrayOfInt1.length || readByte(arrayOfInt1[b1]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[b1] + 1);
      } 
      while (arrayOfInt2 != null && b2 < arrayOfInt2.length && b4 <= i7) {
        if (b4 == i7) {
          i8 = readAnnotationTarget(paramContext, arrayOfInt2[b2]);
          readAnnotationValues(i8 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i8, arrayOfChar), false));
        } 
        b4 = (++b2 >= arrayOfInt2.length || readByte(arrayOfInt2[b2]) < 67) ? -1 : readUnsignedShort(arrayOfInt2[b2] + 1);
      } 
    } 
    if (arrayOfLabel[k] != null)
      paramMethodVisitor.visitLabel(arrayOfLabel[k]); 
    if ((paramContext.flags & 0x2) == 0 && i2 != 0) {
      int[] arrayOfInt = null;
      if (i3 != 0) {
        paramInt = i3 + 2;
        arrayOfInt = new int[readUnsignedShort(i3) * 3];
        int i9 = arrayOfInt.length;
        while (i9 > 0) {
          arrayOfInt[--i9] = paramInt + 6;
          arrayOfInt[--i9] = readUnsignedShort(paramInt + 8);
          arrayOfInt[--i9] = readUnsignedShort(paramInt);
          paramInt += 10;
        } 
      } 
      paramInt = i2 + 2;
      for (int i8 = readUnsignedShort(i2); i8 > 0; i8--) {
        int i9 = readUnsignedShort(paramInt);
        int i10 = readUnsignedShort(paramInt + 2);
        int i11 = readUnsignedShort(paramInt + 8);
        String str = null;
        if (arrayOfInt != null)
          for (boolean bool = false; bool < arrayOfInt.length; bool += true) {
            if (arrayOfInt[bool] == i9 && arrayOfInt[bool + true] == i11) {
              str = readUTF8(arrayOfInt[bool + 2], arrayOfChar);
              break;
            } 
          }  
        paramMethodVisitor.visitLocalVariable(readUTF8(paramInt + 4, arrayOfChar), readUTF8(paramInt + 6, arrayOfChar), str, arrayOfLabel[i9], arrayOfLabel[i9 + i10], i11);
        paramInt += 10;
      } 
    } 
    if (arrayOfInt1 != null)
      for (i7 = 0; i7 < arrayOfInt1.length; i7++) {
        if (readByte(arrayOfInt1[i7]) >> 1 == 32) {
          int i8 = readAnnotationTarget(paramContext, arrayOfInt1[i7]);
          i8 = readAnnotationValues(i8 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.typeRef, paramContext.typePath, paramContext.start, paramContext.end, paramContext.index, readUTF8(i8, arrayOfChar), true));
        } 
      }  
    if (arrayOfInt2 != null)
      for (i7 = 0; i7 < arrayOfInt2.length; i7++) {
        if (readByte(arrayOfInt2[i7]) >> 1 == 32) {
          int i8 = readAnnotationTarget(paramContext, arrayOfInt2[i7]);
          i8 = readAnnotationValues(i8 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.typeRef, paramContext.typePath, paramContext.start, paramContext.end, paramContext.index, readUTF8(i8, arrayOfChar), false));
        } 
      }  
    while (attribute != null) {
      Attribute attribute1 = attribute.next;
      attribute.next = null;
      paramMethodVisitor.visitAttribute(attribute);
      attribute = attribute1;
    } 
    paramMethodVisitor.visitMaxs(i, j);
  }
  
  private int[] readTypeAnnotations(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt, boolean paramBoolean) {
    char[] arrayOfChar = paramContext.buffer;
    int[] arrayOfInt = new int[readUnsignedShort(paramInt)];
    paramInt += 2;
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
      arrayOfInt[b1] = paramInt;
      int i = readInt(paramInt);
      switch (i >>> 24) {
        case 0:
        case 1:
        case 22:
          paramInt += 2;
          break;
        case 19:
        case 20:
        case 21:
          paramInt++;
          break;
        case 64:
        case 65:
          for (j = readUnsignedShort(paramInt + 1); j > 0; j--) {
            int k = readUnsignedShort(paramInt + 3);
            int m = readUnsignedShort(paramInt + 5);
            readLabel(k, paramContext.labels);
            readLabel(k + m, paramContext.labels);
            paramInt += 6;
          } 
          paramInt += 3;
          break;
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
          paramInt += 4;
          break;
        default:
          paramInt += 3;
          break;
      } 
      int j = readByte(paramInt);
      if (i >>> 24 == 66) {
        TypePath typePath = (j == 0) ? null : new TypePath(this.b, paramInt);
        paramInt += 1 + 2 * j;
        paramInt = readAnnotationValues(paramInt + 2, arrayOfChar, true, paramMethodVisitor.visitTryCatchAnnotation(i, typePath, readUTF8(paramInt, arrayOfChar), paramBoolean));
      } else {
        paramInt = readAnnotationValues(paramInt + 3 + 2 * j, arrayOfChar, true, null);
      } 
    } 
    return arrayOfInt;
  }
  
  private int readAnnotationTarget(Context paramContext, int paramInt) {
    byte b1;
    int i = readInt(paramInt);
    switch (i >>> 24) {
      case 0:
      case 1:
      case 22:
        i &= 0xFFFF0000;
        paramInt += 2;
        break;
      case 19:
      case 20:
      case 21:
        i &= 0xFF000000;
        paramInt++;
        break;
      case 64:
      case 65:
        i &= 0xFF000000;
        j = readUnsignedShort(paramInt + 1);
        paramContext.start = new Label[j];
        paramContext.end = new Label[j];
        paramContext.index = new int[j];
        paramInt += 3;
        for (b1 = 0; b1 < j; b1++) {
          int k = readUnsignedShort(paramInt);
          int m = readUnsignedShort(paramInt + 2);
          paramContext.start[b1] = readLabel(k, paramContext.labels);
          paramContext.end[b1] = readLabel(k + m, paramContext.labels);
          paramContext.index[b1] = readUnsignedShort(paramInt + 4);
          paramInt += 6;
        } 
        break;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
        i &= 0xFF0000FF;
        paramInt += 4;
        break;
      default:
        i &= ((i >>> 24 < 67) ? -256 : -16777216);
        paramInt += 3;
        break;
    } 
    int j = readByte(paramInt);
    paramContext.typeRef = i;
    paramContext.typePath = (j == 0) ? null : new TypePath(this.b, paramInt);
    return paramInt + 1 + 2 * j;
  }
  
  private void readParameterAnnotations(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt, boolean paramBoolean) {
    byte b2 = this.b[paramInt++] & 0xFF;
    int i = Type.getArgumentTypes(paramContext.desc).length - b2;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      AnnotationVisitor annotationVisitor = paramMethodVisitor.visitParameterAnnotation(b1, "Ljava/lang/Synthetic;", false);
      if (annotationVisitor != null)
        annotationVisitor.visitEnd(); 
    } 
    char[] arrayOfChar = paramContext.buffer;
    while (b1 < b2 + i) {
      int j = readUnsignedShort(paramInt);
      paramInt += 2;
      while (j > 0) {
        AnnotationVisitor annotationVisitor = paramMethodVisitor.visitParameterAnnotation(b1, readUTF8(paramInt, arrayOfChar), paramBoolean);
        paramInt = readAnnotationValues(paramInt + 2, arrayOfChar, true, annotationVisitor);
        j--;
      } 
      b1++;
    } 
  }
  
  private int readAnnotationValues(int paramInt, char[] paramArrayOfChar, boolean paramBoolean, AnnotationVisitor paramAnnotationVisitor) {
    int i = readUnsignedShort(paramInt);
    paramInt += 2;
    if (paramBoolean) {
      while (i > 0) {
        paramInt = readAnnotationValue(paramInt + 2, paramArrayOfChar, readUTF8(paramInt, paramArrayOfChar), paramAnnotationVisitor);
        i--;
      } 
    } else {
      while (i > 0) {
        paramInt = readAnnotationValue(paramInt, paramArrayOfChar, null, paramAnnotationVisitor);
        i--;
      } 
    } 
    if (paramAnnotationVisitor != null)
      paramAnnotationVisitor.visitEnd(); 
    return paramInt;
  }
  
  private int readAnnotationValue(int paramInt, char[] paramArrayOfChar, String paramString, AnnotationVisitor paramAnnotationVisitor) {
    double[] arrayOfDouble;
    float[] arrayOfFloat;
    long[] arrayOfLong;
    int[] arrayOfInt;
    char[] arrayOfChar;
    short[] arrayOfShort;
    boolean[] arrayOfBoolean;
    byte[] arrayOfByte;
    int i;
    byte b1;
    if (paramAnnotationVisitor == null) {
      switch (this.b[paramInt] & 0xFF) {
        case 101:
          return paramInt + 5;
        case 64:
          return readAnnotationValues(paramInt + 3, paramArrayOfChar, true, null);
        case 91:
          return readAnnotationValues(paramInt + 1, paramArrayOfChar, false, null);
      } 
      return paramInt + 3;
    } 
    switch (this.b[paramInt++] & 0xFF) {
      case 68:
      case 70:
      case 73:
      case 74:
        paramAnnotationVisitor.visit(paramString, readConst(readUnsignedShort(paramInt), paramArrayOfChar));
        paramInt += 2;
        break;
      case 66:
        paramAnnotationVisitor.visit(paramString, Byte.valueOf((byte)readInt(this.items[readUnsignedShort(paramInt)])));
        paramInt += 2;
        break;
      case 90:
        paramAnnotationVisitor.visit(paramString, (readInt(this.items[readUnsignedShort(paramInt)]) == 0) ? Boolean.FALSE : Boolean.TRUE);
        paramInt += 2;
        break;
      case 83:
        paramAnnotationVisitor.visit(paramString, Short.valueOf((short)readInt(this.items[readUnsignedShort(paramInt)])));
        paramInt += 2;
        break;
      case 67:
        paramAnnotationVisitor.visit(paramString, Character.valueOf((char)readInt(this.items[readUnsignedShort(paramInt)])));
        paramInt += 2;
        break;
      case 115:
        paramAnnotationVisitor.visit(paramString, readUTF8(paramInt, paramArrayOfChar));
        paramInt += 2;
        break;
      case 101:
        paramAnnotationVisitor.visitEnum(paramString, readUTF8(paramInt, paramArrayOfChar), readUTF8(paramInt + 2, paramArrayOfChar));
        paramInt += 4;
        break;
      case 99:
        paramAnnotationVisitor.visit(paramString, Type.getType(readUTF8(paramInt, paramArrayOfChar)));
        paramInt += 2;
        break;
      case 64:
        paramInt = readAnnotationValues(paramInt + 2, paramArrayOfChar, true, paramAnnotationVisitor.visitAnnotation(paramString, readUTF8(paramInt, paramArrayOfChar)));
        break;
      case 91:
        i = readUnsignedShort(paramInt);
        paramInt += 2;
        if (i == 0)
          return readAnnotationValues(paramInt - 2, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString)); 
        switch (this.b[paramInt++] & 0xFF) {
          case 66:
            arrayOfByte = new byte[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfByte[b1] = (byte)readInt(this.items[readUnsignedShort(paramInt)]);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfByte);
            paramInt--;
            break;
          case 90:
            arrayOfBoolean = new boolean[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfBoolean[b1] = (readInt(this.items[readUnsignedShort(paramInt)]) != 0);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfBoolean);
            paramInt--;
            break;
          case 83:
            arrayOfShort = new short[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfShort[b1] = (short)readInt(this.items[readUnsignedShort(paramInt)]);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfShort);
            paramInt--;
            break;
          case 67:
            arrayOfChar = new char[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfChar[b1] = (char)readInt(this.items[readUnsignedShort(paramInt)]);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfChar);
            paramInt--;
            break;
          case 73:
            arrayOfInt = new int[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfInt[b1] = readInt(this.items[readUnsignedShort(paramInt)]);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfInt);
            paramInt--;
            break;
          case 74:
            arrayOfLong = new long[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfLong[b1] = readLong(this.items[readUnsignedShort(paramInt)]);
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfLong);
            paramInt--;
            break;
          case 70:
            arrayOfFloat = new float[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfFloat[b1] = Float.intBitsToFloat(readInt(this.items[readUnsignedShort(paramInt)]));
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfFloat);
            paramInt--;
            break;
          case 68:
            arrayOfDouble = new double[i];
            for (b1 = 0; b1 < i; b1++) {
              arrayOfDouble[b1] = Double.longBitsToDouble(readLong(this.items[readUnsignedShort(paramInt)]));
              paramInt += 3;
            } 
            paramAnnotationVisitor.visit(paramString, arrayOfDouble);
            paramInt--;
            break;
        } 
        paramInt = readAnnotationValues(paramInt - 3, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
        break;
    } 
    return paramInt;
  }
  
  private void getImplicitFrame(Context paramContext) {
    String str = paramContext.desc;
    Object[] arrayOfObject = paramContext.local;
    byte b1 = 0;
    if ((paramContext.access & 0x8) == 0)
      if ("<init>".equals(paramContext.name)) {
        arrayOfObject[b1++] = Opcodes.UNINITIALIZED_THIS;
      } else {
        arrayOfObject[b1++] = readClass(this.header + 2, paramContext.buffer);
      }  
    byte b2 = 1;
    while (true) {
      byte b3 = b2;
      switch (str.charAt(b2++)) {
        case 'B':
        case 'C':
        case 'I':
        case 'S':
        case 'Z':
          arrayOfObject[b1++] = Opcodes.INTEGER;
          continue;
        case 'F':
          arrayOfObject[b1++] = Opcodes.FLOAT;
          continue;
        case 'J':
          arrayOfObject[b1++] = Opcodes.LONG;
          continue;
        case 'D':
          arrayOfObject[b1++] = Opcodes.DOUBLE;
          continue;
        case '[':
          while (str.charAt(b2) == '[')
            b2++; 
          if (str.charAt(b2) == 'L')
            while (str.charAt(++b2) != ';')
              b2++;  
          arrayOfObject[b1++] = str.substring(b3, ++b2);
          continue;
        case 'L':
          while (str.charAt(b2) != ';')
            b2++; 
          arrayOfObject[b1++] = str.substring(b3 + 1, b2++);
          continue;
      } 
      break;
    } 
    paramContext.localCount = b1;
  }
  
  private int readFrame(int paramInt, boolean paramBoolean1, boolean paramBoolean2, Context paramContext) {
    int i;
    char c;
    char[] arrayOfChar = paramContext.buffer;
    Label[] arrayOfLabel = paramContext.labels;
    if (paramBoolean1) {
      c = this.b[paramInt++] & 0xFF;
    } else {
      c = 'ÿ';
      paramContext.offset = -1;
    } 
    paramContext.localDiff = 0;
    if (c < '@') {
      i = c;
      paramContext.mode = 3;
      paramContext.stackCount = 0;
    } else if (c < '') {
      i = c - '@';
      paramInt = readFrameType(paramContext.stack, 0, paramInt, arrayOfChar, arrayOfLabel);
      paramContext.mode = 4;
      paramContext.stackCount = 1;
    } else {
      i = readUnsignedShort(paramInt);
      paramInt += 2;
      if (c == '÷') {
        paramInt = readFrameType(paramContext.stack, 0, paramInt, arrayOfChar, arrayOfLabel);
        paramContext.mode = 4;
        paramContext.stackCount = 1;
      } else if (c >= 'ø' && c < 'û') {
        paramContext.mode = 2;
        paramContext.localDiff = 'û' - c;
        paramContext.localCount -= paramContext.localDiff;
        paramContext.stackCount = 0;
      } else if (c == 'û') {
        paramContext.mode = 3;
        paramContext.stackCount = 0;
      } else if (c < 'ÿ') {
        int j = paramBoolean2 ? paramContext.localCount : 0;
        for (char c1 = c - 'û'; c1 > '\000'; c1--)
          paramInt = readFrameType(paramContext.local, j++, paramInt, arrayOfChar, arrayOfLabel); 
        paramContext.mode = 1;
        paramContext.localDiff = c - 'û';
        paramContext.localCount += paramContext.localDiff;
        paramContext.stackCount = 0;
      } else {
        paramContext.mode = 0;
        int j = readUnsignedShort(paramInt);
        paramInt += 2;
        paramContext.localDiff = j;
        paramContext.localCount = j;
        byte b1 = 0;
        while (j > 0) {
          paramInt = readFrameType(paramContext.local, b1++, paramInt, arrayOfChar, arrayOfLabel);
          j--;
        } 
        j = readUnsignedShort(paramInt);
        paramInt += 2;
        paramContext.stackCount = j;
        b1 = 0;
        while (j > 0) {
          paramInt = readFrameType(paramContext.stack, b1++, paramInt, arrayOfChar, arrayOfLabel);
          j--;
        } 
      } 
    } 
    paramContext.offset += i + 1;
    readLabel(paramContext.offset, arrayOfLabel);
    return paramInt;
  }
  
  private int readFrameType(Object[] paramArrayOfObject, int paramInt1, int paramInt2, char[] paramArrayOfChar, Label[] paramArrayOfLabel) {
    byte b1 = this.b[paramInt2++] & 0xFF;
    switch (b1) {
      case 0:
        paramArrayOfObject[paramInt1] = Opcodes.TOP;
        return paramInt2;
      case 1:
        paramArrayOfObject[paramInt1] = Opcodes.INTEGER;
        return paramInt2;
      case 2:
        paramArrayOfObject[paramInt1] = Opcodes.FLOAT;
        return paramInt2;
      case 3:
        paramArrayOfObject[paramInt1] = Opcodes.DOUBLE;
        return paramInt2;
      case 4:
        paramArrayOfObject[paramInt1] = Opcodes.LONG;
        return paramInt2;
      case 5:
        paramArrayOfObject[paramInt1] = Opcodes.NULL;
        return paramInt2;
      case 6:
        paramArrayOfObject[paramInt1] = Opcodes.UNINITIALIZED_THIS;
        return paramInt2;
      case 7:
        paramArrayOfObject[paramInt1] = readClass(paramInt2, paramArrayOfChar);
        return 2;
    } 
    paramArrayOfObject[paramInt1] = readLabel(readUnsignedShort(paramInt2), paramArrayOfLabel);
    return 2;
  }
  
  protected Label readLabel(int paramInt, Label[] paramArrayOfLabel) {
    if (paramArrayOfLabel[paramInt] == null)
      paramArrayOfLabel[paramInt] = new Label(); 
    return paramArrayOfLabel[paramInt];
  }
  
  private int getAttributes() {
    int i = this.header + 8 + readUnsignedShort(this.header + 6) * 2;
    int j;
    for (j = readUnsignedShort(i); j > 0; j--) {
      for (int k = readUnsignedShort(i + 8); k > 0; k--)
        i += 6 + readInt(i + 12); 
      i += 8;
    } 
    i += 2;
    for (j = readUnsignedShort(i); j > 0; j--) {
      for (int k = readUnsignedShort(i + 8); k > 0; k--)
        i += 6 + readInt(i + 12); 
      i += 8;
    } 
    return i + 2;
  }
  
  private Attribute readAttribute(Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel) {
    for (byte b1 = 0; b1 < paramArrayOfAttribute.length; b1++) {
      if ((paramArrayOfAttribute[b1]).type.equals(paramString))
        return paramArrayOfAttribute[b1].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel); 
    } 
    return (new Attribute(paramString)).read(this, paramInt1, paramInt2, null, -1, null);
  }
  
  public int getItemCount() { return this.items.length; }
  
  public int getItem(int paramInt) { return this.items[paramInt]; }
  
  public int getMaxStringLength() { return this.maxStringLength; }
  
  public int readByte(int paramInt) { return this.b[paramInt] & 0xFF; }
  
  public int readUnsignedShort(int paramInt) {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[paramInt + 1] & 0xFF;
  }
  
  public short readShort(int paramInt) {
    byte[] arrayOfByte = this.b;
    return (short)((arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[paramInt + 1] & 0xFF);
  }
  
  public int readInt(int paramInt) {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 24 | (arrayOfByte[paramInt + 1] & 0xFF) << 16 | (arrayOfByte[paramInt + 2] & 0xFF) << 8 | arrayOfByte[paramInt + 3] & 0xFF;
  }
  
  public long readLong(int paramInt) {
    long l1 = readInt(paramInt);
    long l2 = readInt(paramInt + 4) & 0xFFFFFFFFL;
    return l1 << 32 | l2;
  }
  
  public String readUTF8(int paramInt, char[] paramArrayOfChar) {
    int i = readUnsignedShort(paramInt);
    if (paramInt == 0 || i == 0)
      return null; 
    String str = this.strings[i];
    if (str != null)
      return str; 
    paramInt = this.items[i];
    this.strings[i] = readUTF(paramInt + 2, readUnsignedShort(paramInt), paramArrayOfChar);
    return readUTF(paramInt + 2, readUnsignedShort(paramInt), paramArrayOfChar);
  }
  
  private String readUTF(int paramInt1, int paramInt2, char[] paramArrayOfChar) {
    int i = paramInt1 + paramInt2;
    byte[] arrayOfByte = this.b;
    byte b1 = 0;
    byte b2 = 0;
    char c = Character.MIN_VALUE;
    while (paramInt1 < i) {
      byte b3 = arrayOfByte[paramInt1++];
      switch (b2) {
        case false:
          b3 &= 0xFF;
          if (b3 < 128) {
            paramArrayOfChar[b1++] = (char)b3;
            continue;
          } 
          if (b3 < 224 && b3 > 191) {
            c = (char)(b3 & 0x1F);
            b2 = 1;
            continue;
          } 
          c = (char)(b3 & 0xF);
          b2 = 2;
        case true:
          paramArrayOfChar[b1++] = (char)(c << '\006' | b3 & 0x3F);
          b2 = 0;
        case true:
          c = (char)(c << '\006' | b3 & 0x3F);
          b2 = 1;
      } 
    } 
    return new String(paramArrayOfChar, 0, b1);
  }
  
  public String readClass(int paramInt, char[] paramArrayOfChar) { return readUTF8(this.items[readUnsignedShort(paramInt)], paramArrayOfChar); }
  
  public Object readConst(int paramInt, char[] paramArrayOfChar) {
    int i = this.items[paramInt];
    switch (this.b[i - 1]) {
      case 3:
        return Integer.valueOf(readInt(i));
      case 4:
        return Float.valueOf(Float.intBitsToFloat(readInt(i)));
      case 5:
        return Long.valueOf(readLong(i));
      case 6:
        return Double.valueOf(Double.longBitsToDouble(readLong(i)));
      case 7:
        return Type.getObjectType(readUTF8(i, paramArrayOfChar));
      case 8:
        return readUTF8(i, paramArrayOfChar);
      case 16:
        return Type.getMethodType(readUTF8(i, paramArrayOfChar));
    } 
    int j = readByte(i);
    int[] arrayOfInt = this.items;
    int k = arrayOfInt[readUnsignedShort(i + 1)];
    String str1 = readClass(k, paramArrayOfChar);
    k = arrayOfInt[readUnsignedShort(k + 2)];
    String str2 = readUTF8(k, paramArrayOfChar);
    String str3 = readUTF8(k + 2, paramArrayOfChar);
    return new Handle(j, str1, str2, str3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */