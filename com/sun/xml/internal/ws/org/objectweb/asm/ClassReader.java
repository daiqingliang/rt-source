package com.sun.xml.internal.ws.org.objectweb.asm;

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
  
  public String getSuperName() {
    int i = this.items[readUnsignedShort(this.header + 4)];
    return (i == 0) ? null : readUTF8(i, new char[this.maxStringLength]);
  }
  
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
  
  public ClassReader(InputStream paramInputStream) throws IOException { this(readClass(paramInputStream)); }
  
  public ClassReader(String paramString) throws IOException { this(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class")); }
  
  private static byte[] readClass(InputStream paramInputStream) throws IOException {
    if (paramInputStream == null)
      throw new IOException("Class not found"); 
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
        byte[] arrayOfByte1 = new byte[arrayOfByte.length + 1000];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, i);
        arrayOfByte = arrayOfByte1;
      } 
    } 
  }
  
  public void accept(ClassVisitor paramClassVisitor, int paramInt) { accept(paramClassVisitor, new Attribute[0], paramInt); }
  
  public void accept(ClassVisitor paramClassVisitor, Attribute[] paramArrayOfAttribute, int paramInt) {
    byte[] arrayOfByte = this.b;
    char[] arrayOfChar = new char[this.maxStringLength];
    int i1 = 0;
    int i2 = 0;
    Attribute attribute = null;
    int j = this.header;
    int n = readUnsignedShort(j);
    String str1 = readClass(j + 2, arrayOfChar);
    int k = this.items[readUnsignedShort(j + 4)];
    String str3 = (k == 0) ? null : readUTF8(k, arrayOfChar);
    String[] arrayOfString = new String[readUnsignedShort(j + 6)];
    int m = 0;
    j += 8;
    int i;
    for (i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = readClass(j, arrayOfChar);
      j += 2;
    } 
    boolean bool1 = ((paramInt & true) != 0) ? 1 : 0;
    boolean bool2 = ((paramInt & 0x2) != 0) ? 1 : 0;
    boolean bool3 = ((paramInt & 0x8) != 0) ? 1 : 0;
    k = j;
    i = readUnsignedShort(k);
    k += 2;
    while (i > 0) {
      int i3 = readUnsignedShort(k + 6);
      k += 8;
      while (i3 > 0) {
        k += 6 + readInt(k + 2);
        i3--;
      } 
      i--;
    } 
    i = readUnsignedShort(k);
    k += 2;
    while (i > 0) {
      int i3 = readUnsignedShort(k + 6);
      k += 8;
      while (i3 > 0) {
        k += 6 + readInt(k + 2);
        i3--;
      } 
      i--;
    } 
    String str2 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    i = readUnsignedShort(k);
    k += 2;
    while (i > 0) {
      String str = readUTF8(k, arrayOfChar);
      if ("SourceFile".equals(str)) {
        str4 = readUTF8(k + 6, arrayOfChar);
      } else if ("InnerClasses".equals(str)) {
        m = k + 6;
      } else if ("EnclosingMethod".equals(str)) {
        str6 = readClass(k + 6, arrayOfChar);
        int i3 = readUnsignedShort(k + 8);
        if (i3 != 0) {
          str7 = readUTF8(this.items[i3], arrayOfChar);
          str8 = readUTF8(this.items[i3] + 2, arrayOfChar);
        } 
      } else if ("Signature".equals(str)) {
        str2 = readUTF8(k + 6, arrayOfChar);
      } else if ("RuntimeVisibleAnnotations".equals(str)) {
        i1 = k + 6;
      } else if ("Deprecated".equals(str)) {
        n |= 0x20000;
      } else if ("Synthetic".equals(str)) {
        n |= 0x1000;
      } else if ("SourceDebugExtension".equals(str)) {
        int i3 = readInt(k + 2);
        str5 = readUTF(k + 6, i3, new char[i3]);
      } else if ("RuntimeInvisibleAnnotations".equals(str)) {
        i2 = k + 6;
      } else {
        Attribute attribute1 = readAttribute(paramArrayOfAttribute, str, k + 6, readInt(k + 2), arrayOfChar, -1, null);
        if (attribute1 != null) {
          attribute1.next = attribute;
          attribute = attribute1;
        } 
      } 
      k += 6 + readInt(k + 2);
      i--;
    } 
    paramClassVisitor.visit(readInt(4), n, str1, str2, str3, arrayOfString);
    if (!bool2 && (str4 != null || str5 != null))
      paramClassVisitor.visitSource(str4, str5); 
    if (str6 != null)
      paramClassVisitor.visitOuterClass(str6, str7, str8); 
    for (i = 1; i >= 0; i--) {
      k = (i == 0) ? i2 : i1;
      if (k != 0) {
        int i3 = readUnsignedShort(k);
        k += 2;
        while (i3 > 0) {
          k = readAnnotationValues(k + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(k, arrayOfChar), (i != 0)));
          i3--;
        } 
      } 
    } 
    while (attribute != null) {
      Attribute attribute1 = attribute.next;
      attribute.next = null;
      paramClassVisitor.visitAttribute(attribute);
      attribute = attribute1;
    } 
    if (m != 0) {
      i = readUnsignedShort(m);
      m += 2;
      while (i > 0) {
        paramClassVisitor.visitInnerClass((readUnsignedShort(m) == 0) ? null : readClass(m, arrayOfChar), (readUnsignedShort(m + 2) == 0) ? null : readClass(m + 2, arrayOfChar), (readUnsignedShort(m + 4) == 0) ? null : readUTF8(m + 4, arrayOfChar), readUnsignedShort(m + 6));
        m += 8;
        i--;
      } 
    } 
    i = readUnsignedShort(j);
    j += 2;
    while (i > 0) {
      n = readUnsignedShort(j);
      str1 = readUTF8(j + 2, arrayOfChar);
      String str = readUTF8(j + 4, arrayOfChar);
      int i4 = 0;
      str2 = null;
      i1 = 0;
      i2 = 0;
      attribute = null;
      int i3 = readUnsignedShort(j + 6);
      j += 8;
      while (i3 > 0) {
        String str9 = readUTF8(j, arrayOfChar);
        if ("ConstantValue".equals(str9)) {
          i4 = readUnsignedShort(j + 6);
        } else if ("Signature".equals(str9)) {
          str2 = readUTF8(j + 6, arrayOfChar);
        } else if ("Deprecated".equals(str9)) {
          n |= 0x20000;
        } else if ("Synthetic".equals(str9)) {
          n |= 0x1000;
        } else if ("RuntimeVisibleAnnotations".equals(str9)) {
          i1 = j + 6;
        } else if ("RuntimeInvisibleAnnotations".equals(str9)) {
          i2 = j + 6;
        } else {
          Attribute attribute1 = readAttribute(paramArrayOfAttribute, str9, j + 6, readInt(j + 2), arrayOfChar, -1, null);
          if (attribute1 != null) {
            attribute1.next = attribute;
            attribute = attribute1;
          } 
        } 
        j += 6 + readInt(j + 2);
        i3--;
      } 
      FieldVisitor fieldVisitor = paramClassVisitor.visitField(n, str1, str, str2, (i4 == 0) ? null : readConst(i4, arrayOfChar));
      if (fieldVisitor != null) {
        for (i3 = 1; i3 >= 0; i3--) {
          k = (i3 == 0) ? i2 : i1;
          if (k != 0) {
            int i5 = readUnsignedShort(k);
            k += 2;
            while (i5 > 0) {
              k = readAnnotationValues(k + 2, arrayOfChar, true, fieldVisitor.visitAnnotation(readUTF8(k, arrayOfChar), (i3 != 0)));
              i5--;
            } 
          } 
        } 
        while (attribute != null) {
          Attribute attribute1 = attribute.next;
          attribute.next = null;
          fieldVisitor.visitAttribute(attribute);
          attribute = attribute1;
        } 
        fieldVisitor.visitEnd();
      } 
      i--;
    } 
    i = readUnsignedShort(j);
    j += 2;
    while (i > 0) {
      String[] arrayOfString1;
      int i4 = j + 6;
      n = readUnsignedShort(j);
      str1 = readUTF8(j + 2, arrayOfChar);
      String str = readUTF8(j + 4, arrayOfChar);
      str2 = null;
      i1 = 0;
      i2 = 0;
      int i5 = 0;
      int i6 = 0;
      int i7 = 0;
      attribute = null;
      k = 0;
      m = 0;
      int i3 = readUnsignedShort(j + 6);
      j += 8;
      while (i3 > 0) {
        String str9 = readUTF8(j, arrayOfChar);
        int i8 = readInt(j + 2);
        j += 6;
        if ("Code".equals(str9)) {
          if (!bool1)
            k = j; 
        } else if ("Exceptions".equals(str9)) {
          m = j;
        } else if ("Signature".equals(str9)) {
          str2 = readUTF8(j, arrayOfChar);
        } else if ("Deprecated".equals(str9)) {
          n |= 0x20000;
        } else if ("RuntimeVisibleAnnotations".equals(str9)) {
          i1 = j;
        } else if ("AnnotationDefault".equals(str9)) {
          i5 = j;
        } else if ("Synthetic".equals(str9)) {
          n |= 0x1000;
        } else if ("RuntimeInvisibleAnnotations".equals(str9)) {
          i2 = j;
        } else if ("RuntimeVisibleParameterAnnotations".equals(str9)) {
          i6 = j;
        } else if ("RuntimeInvisibleParameterAnnotations".equals(str9)) {
          i7 = j;
        } else {
          Attribute attribute1 = readAttribute(paramArrayOfAttribute, str9, j, i8, arrayOfChar, -1, null);
          if (attribute1 != null) {
            attribute1.next = attribute;
            attribute = attribute1;
          } 
        } 
        j += i8;
        i3--;
      } 
      if (m == 0) {
        arrayOfString1 = null;
      } else {
        arrayOfString1 = new String[readUnsignedShort(m)];
        m += 2;
        for (i3 = 0; i3 < arrayOfString1.length; i3++) {
          arrayOfString1[i3] = readClass(m, arrayOfChar);
          m += 2;
        } 
      } 
      MethodVisitor methodVisitor = paramClassVisitor.visitMethod(n, str1, str, str2, arrayOfString1);
      if (methodVisitor != null) {
        if (methodVisitor instanceof MethodWriter) {
          MethodWriter methodWriter = (MethodWriter)methodVisitor;
          if (methodWriter.cw.cr == this && str2 == methodWriter.signature) {
            boolean bool = false;
            if (arrayOfString1 == null) {
              bool = (methodWriter.exceptionCount == 0) ? 1 : 0;
            } else if (arrayOfString1.length == methodWriter.exceptionCount) {
              bool = true;
              for (i3 = arrayOfString1.length - 1; i3 >= 0; i3--) {
                m -= 2;
                if (methodWriter.exceptions[i3] != readUnsignedShort(m)) {
                  bool = false;
                  break;
                } 
              } 
            } 
            if (bool) {
              methodWriter.classReaderOffset = i4;
              methodWriter.classReaderLength = j - i4;
              continue;
            } 
          } 
        } 
        if (i5 != 0) {
          AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
          readAnnotationValue(i5, arrayOfChar, null, annotationVisitor);
          if (annotationVisitor != null)
            annotationVisitor.visitEnd(); 
        } 
        for (i3 = 1; i3 >= 0; i3--) {
          m = (i3 == 0) ? i2 : i1;
          if (m != 0) {
            int i8 = readUnsignedShort(m);
            m += 2;
            while (i8 > 0) {
              m = readAnnotationValues(m + 2, arrayOfChar, true, methodVisitor.visitAnnotation(readUTF8(m, arrayOfChar), (i3 != 0)));
              i8--;
            } 
          } 
        } 
        if (i6 != 0)
          readParameterAnnotations(i6, str, arrayOfChar, true, methodVisitor); 
        if (i7 != 0)
          readParameterAnnotations(i7, str, arrayOfChar, false, methodVisitor); 
        while (attribute != null) {
          Attribute attribute1 = attribute.next;
          attribute.next = null;
          methodVisitor.visitAttribute(attribute);
          attribute = attribute1;
        } 
      } 
      if (methodVisitor != null && k != 0) {
        int i8 = readUnsignedShort(k);
        int i9 = readUnsignedShort(k + 2);
        int i10 = readInt(k + 4);
        k += 8;
        int i11 = k;
        int i12 = k + i10;
        methodVisitor.visitCode();
        Label[] arrayOfLabel = new Label[i10 + 2];
        readLabel(i10 + 1, arrayOfLabel);
        while (k < i12) {
          m = k - i11;
          byte b2 = arrayOfByte[k] & 0xFF;
          switch (ClassWriter.TYPE[b2]) {
            case 0:
            case 4:
              k++;
              continue;
            case 8:
              readLabel(m + readShort(k + 1), arrayOfLabel);
              k += 3;
              continue;
            case 9:
              readLabel(m + readInt(k + 1), arrayOfLabel);
              k += 5;
              continue;
            case 16:
              b2 = arrayOfByte[k + 1] & 0xFF;
              if (b2 == 132) {
                k += 6;
                continue;
              } 
              k += 4;
              continue;
            case 13:
              k = k + 4 - (m & 0x3);
              readLabel(m + readInt(k), arrayOfLabel);
              i3 = readInt(k + 8) - readInt(k + 4) + 1;
              k += 12;
              while (i3 > 0) {
                readLabel(m + readInt(k), arrayOfLabel);
                k += 4;
                i3--;
              } 
              continue;
            case 14:
              k = k + 4 - (m & 0x3);
              readLabel(m + readInt(k), arrayOfLabel);
              i3 = readInt(k + 4);
              k += 8;
              while (i3 > 0) {
                readLabel(m + readInt(k + 4), arrayOfLabel);
                k += 8;
                i3--;
              } 
              continue;
            case 1:
            case 3:
            case 10:
              k += 2;
              continue;
            case 2:
            case 5:
            case 6:
            case 11:
            case 12:
              k += 3;
              continue;
            case 7:
              k += 5;
              continue;
          } 
          k += 4;
        } 
        i3 = readUnsignedShort(k);
        k += 2;
        while (i3 > 0) {
          Label label1 = readLabel(readUnsignedShort(k), arrayOfLabel);
          Label label2 = readLabel(readUnsignedShort(k + 2), arrayOfLabel);
          Label label3 = readLabel(readUnsignedShort(k + 4), arrayOfLabel);
          int i21 = readUnsignedShort(k + 6);
          if (i21 == 0) {
            methodVisitor.visitTryCatchBlock(label1, label2, label3, null);
          } else {
            methodVisitor.visitTryCatchBlock(label1, label2, label3, readUTF8(this.items[i21], arrayOfChar));
          } 
          k += 8;
          i3--;
        } 
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        byte b1 = 0;
        int i17 = 0;
        int i18 = 0;
        int i19 = 0;
        int i20 = 0;
        Object[] arrayOfObject1 = null;
        Object[] arrayOfObject2 = null;
        boolean bool = true;
        attribute = null;
        i3 = readUnsignedShort(k);
        k += 2;
        while (i3 > 0) {
          String str9 = readUTF8(k, arrayOfChar);
          if ("LocalVariableTable".equals(str9)) {
            if (!bool2) {
              i13 = k + 6;
              int i21 = readUnsignedShort(k + 6);
              m = k + 8;
              while (i21 > 0) {
                int i22 = readUnsignedShort(m);
                if (arrayOfLabel[i22] == null)
                  (readLabel(i22, arrayOfLabel)).status |= 0x1; 
                i22 += readUnsignedShort(m + 2);
                if (arrayOfLabel[i22] == null)
                  (readLabel(i22, arrayOfLabel)).status |= 0x1; 
                m += 10;
                i21--;
              } 
            } 
          } else if ("LocalVariableTypeTable".equals(str9)) {
            i14 = k + 6;
          } else if ("LineNumberTable".equals(str9)) {
            if (!bool2) {
              int i21 = readUnsignedShort(k + 6);
              m = k + 8;
              while (i21 > 0) {
                int i22 = readUnsignedShort(m);
                if (arrayOfLabel[i22] == null)
                  (readLabel(i22, arrayOfLabel)).status |= 0x1; 
                (arrayOfLabel[i22]).line = readUnsignedShort(m + 2);
                m += 4;
                i21--;
              } 
            } 
          } else if ("StackMapTable".equals(str9)) {
            if ((paramInt & 0x4) == 0) {
              i15 = k + 8;
              i16 = readUnsignedShort(k + 6);
            } 
          } else if ("StackMap".equals(str9)) {
            if ((paramInt & 0x4) == 0) {
              i15 = k + 8;
              i16 = readUnsignedShort(k + 6);
              bool = false;
            } 
          } else {
            for (byte b2 = 0; b2 < paramArrayOfAttribute.length; b2++) {
              if ((paramArrayOfAttribute[b2]).type.equals(str9)) {
                Attribute attribute1 = paramArrayOfAttribute[b2].read(this, k + 6, readInt(k + 2), arrayOfChar, i11 - 8, arrayOfLabel);
                if (attribute1 != null) {
                  attribute1.next = attribute;
                  attribute = attribute1;
                } 
              } 
            } 
          } 
          k += 6 + readInt(k + 2);
          i3--;
        } 
        if (i15 != 0) {
          arrayOfObject1 = new Object[i9];
          arrayOfObject2 = new Object[i8];
          if (bool3) {
            byte b2 = 0;
            if ((n & 0x8) == 0)
              if ("<init>".equals(str1)) {
                arrayOfObject1[b2++] = Opcodes.UNINITIALIZED_THIS;
              } else {
                arrayOfObject1[b2++] = readClass(this.header + 2, arrayOfChar);
              }  
            i3 = 1;
            while (true) {
              int i21 = i3;
              switch (str.charAt(i3++)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                case 'Z':
                  arrayOfObject1[b2++] = Opcodes.INTEGER;
                  continue;
                case 'F':
                  arrayOfObject1[b2++] = Opcodes.FLOAT;
                  continue;
                case 'J':
                  arrayOfObject1[b2++] = Opcodes.LONG;
                  continue;
                case 'D':
                  arrayOfObject1[b2++] = Opcodes.DOUBLE;
                  continue;
                case '[':
                  while (str.charAt(i3) == '[')
                    i3++; 
                  if (str.charAt(i3) == 'L')
                    while (str.charAt(++i3) != ';')
                      i3++;  
                  arrayOfObject1[b2++] = str.substring(i21, ++i3);
                  continue;
                case 'L':
                  while (str.charAt(i3) != ';')
                    i3++; 
                  arrayOfObject1[b2++] = str.substring(i21 + 1, i3++);
                  continue;
              } 
              break;
            } 
            i18 = b2;
          } 
          i17 = -1;
        } 
        for (k = i11; k < i12; k += 4) {
          String str11;
          String str10;
          String str9;
          int i24;
          Label[] arrayOfLabel2;
          int[] arrayOfInt;
          Label[] arrayOfLabel1;
          int i23;
          int i22;
          int i21;
          m = k - i11;
          Label label1 = arrayOfLabel[m];
          if (label1 != null) {
            methodVisitor.visitLabel(label1);
            if (!bool2 && label1.line > 0)
              methodVisitor.visitLineNumber(label1.line, label1); 
          } 
          while (arrayOfObject1 != null && (i17 == m || i17 == -1)) {
            if (!bool || bool3) {
              methodVisitor.visitFrame(-1, i18, arrayOfObject1, i20, arrayOfObject2);
            } else if (i17 != -1) {
              methodVisitor.visitFrame(b1, i19, arrayOfObject1, i20, arrayOfObject2);
            } 
            if (i16 > 0) {
              int i25;
              char c;
              if (bool) {
                c = arrayOfByte[i15++] & 0xFF;
              } else {
                c = 'ÿ';
                i17 = -1;
              } 
              i19 = 0;
              if (c < '@') {
                i25 = c;
                b1 = 3;
                i20 = 0;
              } else if (c < '') {
                i25 = c - '@';
                i15 = readFrameType(arrayOfObject2, 0, i15, arrayOfChar, arrayOfLabel);
                b1 = 4;
                i20 = 1;
              } else {
                i25 = readUnsignedShort(i15);
                i15 += 2;
                if (c == '÷') {
                  i15 = readFrameType(arrayOfObject2, 0, i15, arrayOfChar, arrayOfLabel);
                  b1 = 4;
                  i20 = 1;
                } else if (c >= 'ø' && c < 'û') {
                  b1 = 2;
                  i19 = 'û' - c;
                  i18 -= i19;
                  i20 = 0;
                } else if (c == 'û') {
                  b1 = 3;
                  i20 = 0;
                } else if (c < 'ÿ') {
                  i3 = bool3 ? i18 : 0;
                  for (char c1 = c - 'û'; c1 > '\000'; c1--)
                    i15 = readFrameType(arrayOfObject1, i3++, i15, arrayOfChar, arrayOfLabel); 
                  b1 = 1;
                  i19 = c - 'û';
                  i18 += i19;
                  i20 = 0;
                } else {
                  b1 = 0;
                  int i26 = i19 = i18 = readUnsignedShort(i15);
                  i15 += 2;
                  i3 = 0;
                  while (i26 > 0) {
                    i15 = readFrameType(arrayOfObject1, i3++, i15, arrayOfChar, arrayOfLabel);
                    i26--;
                  } 
                  i26 = i20 = readUnsignedShort(i15);
                  i15 += 2;
                  i3 = 0;
                  while (i26 > 0) {
                    i15 = readFrameType(arrayOfObject2, i3++, i15, arrayOfChar, arrayOfLabel);
                    i26--;
                  } 
                } 
              } 
              i17 += i25 + 1;
              readLabel(i17, arrayOfLabel);
              i16--;
              continue;
            } 
            arrayOfObject1 = null;
          } 
          byte b2 = arrayOfByte[k] & 0xFF;
          switch (ClassWriter.TYPE[b2]) {
            case 0:
              methodVisitor.visitInsn(b2);
              k++;
              continue;
            case 4:
              if (b2 > 54) {
                b2 -= 59;
                methodVisitor.visitVarInsn(54 + (b2 >> 2), b2 & 0x3);
              } else {
                b2 -= 26;
                methodVisitor.visitVarInsn(21 + (b2 >> 2), b2 & 0x3);
              } 
              k++;
              continue;
            case 8:
              methodVisitor.visitJumpInsn(b2, arrayOfLabel[m + readShort(k + 1)]);
              k += 3;
              continue;
            case 9:
              methodVisitor.visitJumpInsn(b2 - 33, arrayOfLabel[m + readInt(k + 1)]);
              k += 5;
              continue;
            case 16:
              b2 = arrayOfByte[k + 1] & 0xFF;
              if (b2 == 132) {
                methodVisitor.visitIincInsn(readUnsignedShort(k + 2), readShort(k + 4));
                k += 6;
                continue;
              } 
              methodVisitor.visitVarInsn(b2, readUnsignedShort(k + 2));
              k += 4;
              continue;
            case 13:
              k = k + 4 - (m & 0x3);
              i21 = m + readInt(k);
              i22 = readInt(k + 4);
              i23 = readInt(k + 8);
              k += 12;
              arrayOfLabel1 = new Label[i23 - i22 + 1];
              for (i3 = 0; i3 < arrayOfLabel1.length; i3++) {
                arrayOfLabel1[i3] = arrayOfLabel[m + readInt(k)];
                k += 4;
              } 
              methodVisitor.visitTableSwitchInsn(i22, i23, arrayOfLabel[i21], arrayOfLabel1);
              continue;
            case 14:
              k = k + 4 - (m & 0x3);
              i21 = m + readInt(k);
              i3 = readInt(k + 4);
              k += 8;
              arrayOfInt = new int[i3];
              arrayOfLabel2 = new Label[i3];
              for (i3 = 0; i3 < arrayOfInt.length; i3++) {
                arrayOfInt[i3] = readInt(k);
                arrayOfLabel2[i3] = arrayOfLabel[m + readInt(k + 4)];
                k += 8;
              } 
              methodVisitor.visitLookupSwitchInsn(arrayOfLabel[i21], arrayOfInt, arrayOfLabel2);
              continue;
            case 3:
              methodVisitor.visitVarInsn(b2, arrayOfByte[k + 1] & 0xFF);
              k += 2;
              continue;
            case 1:
              methodVisitor.visitIntInsn(b2, arrayOfByte[k + 1]);
              k += 2;
              continue;
            case 2:
              methodVisitor.visitIntInsn(b2, readShort(k + 1));
              k += 3;
              continue;
            case 10:
              methodVisitor.visitLdcInsn(readConst(arrayOfByte[k + 1] & 0xFF, arrayOfChar));
              k += 2;
              continue;
            case 11:
              methodVisitor.visitLdcInsn(readConst(readUnsignedShort(k + 1), arrayOfChar));
              k += 3;
              continue;
            case 6:
            case 7:
              i24 = this.items[readUnsignedShort(k + 1)];
              str9 = readClass(i24, arrayOfChar);
              i24 = this.items[readUnsignedShort(i24 + 2)];
              str10 = readUTF8(i24, arrayOfChar);
              str11 = readUTF8(i24 + 2, arrayOfChar);
              if (b2 < 182) {
                methodVisitor.visitFieldInsn(b2, str9, str10, str11);
              } else {
                methodVisitor.visitMethodInsn(b2, str9, str10, str11);
              } 
              if (b2 == 185) {
                k += 5;
                continue;
              } 
              k += 3;
              continue;
            case 5:
              methodVisitor.visitTypeInsn(b2, readClass(k + 1, arrayOfChar));
              k += 3;
              continue;
            case 12:
              methodVisitor.visitIincInsn(arrayOfByte[k + 1] & 0xFF, arrayOfByte[k + 2]);
              k += 3;
              continue;
          } 
          methodVisitor.visitMultiANewArrayInsn(readClass(k + 1, arrayOfChar), arrayOfByte[k + 3] & 0xFF);
        } 
        Label label = arrayOfLabel[i12 - i11];
        if (label != null)
          methodVisitor.visitLabel(label); 
        if (!bool2 && i13 != 0) {
          int[] arrayOfInt = null;
          if (i14 != 0) {
            int i22 = readUnsignedShort(i14) * 3;
            m = i14 + 2;
            arrayOfInt = new int[i22];
            while (i22 > 0) {
              arrayOfInt[--i22] = m + 6;
              arrayOfInt[--i22] = readUnsignedShort(m + 8);
              arrayOfInt[--i22] = readUnsignedShort(m);
              m += 10;
            } 
          } 
          int i21 = readUnsignedShort(i13);
          m = i13 + 2;
          while (i21 > 0) {
            int i22 = readUnsignedShort(m);
            int i23 = readUnsignedShort(m + 2);
            int i24 = readUnsignedShort(m + 8);
            String str9 = null;
            if (arrayOfInt != null)
              for (boolean bool4 = false; bool4 < arrayOfInt.length; bool4 += true) {
                if (arrayOfInt[bool4] == i22 && arrayOfInt[bool4 + true] == i24) {
                  str9 = readUTF8(arrayOfInt[bool4 + 2], arrayOfChar);
                  break;
                } 
              }  
            methodVisitor.visitLocalVariable(readUTF8(m + 4, arrayOfChar), readUTF8(m + 6, arrayOfChar), str9, arrayOfLabel[i22], arrayOfLabel[i22 + i23], i24);
            m += 10;
            i21--;
          } 
        } 
        while (attribute != null) {
          Attribute attribute1 = attribute.next;
          attribute.next = null;
          methodVisitor.visitAttribute(attribute);
          attribute = attribute1;
        } 
        methodVisitor.visitMaxs(i8, i9);
      } 
      if (methodVisitor != null)
        methodVisitor.visitEnd(); 
      continue;
      i--;
    } 
    paramClassVisitor.visitEnd();
  }
  
  private void readParameterAnnotations(int paramInt, String paramString, char[] paramArrayOfChar, boolean paramBoolean, MethodVisitor paramMethodVisitor) {
    byte b2 = this.b[paramInt++] & 0xFF;
    int i = Type.getArgumentTypes(paramString).length - b2;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      AnnotationVisitor annotationVisitor = paramMethodVisitor.visitParameterAnnotation(b1, "Ljava/lang/Synthetic;", false);
      if (annotationVisitor != null)
        annotationVisitor.visitEnd(); 
    } 
    while (b1 < b2 + i) {
      int j = readUnsignedShort(paramInt);
      paramInt += 2;
      while (j > 0) {
        AnnotationVisitor annotationVisitor = paramMethodVisitor.visitParameterAnnotation(b1, readUTF8(paramInt, paramArrayOfChar), paramBoolean);
        paramInt = readAnnotationValues(paramInt + 2, paramArrayOfChar, true, annotationVisitor);
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
        paramAnnotationVisitor.visit(paramString, new Byte((byte)readInt(this.items[readUnsignedShort(paramInt)])));
        paramInt += 2;
        break;
      case 90:
        paramAnnotationVisitor.visit(paramString, (readInt(this.items[readUnsignedShort(paramInt)]) == 0) ? Boolean.FALSE : Boolean.TRUE);
        paramInt += 2;
        break;
      case 83:
        paramAnnotationVisitor.visit(paramString, new Short((short)readInt(this.items[readUnsignedShort(paramInt)])));
        paramInt += 2;
        break;
      case 67:
        paramAnnotationVisitor.visit(paramString, new Character((char)readInt(this.items[readUnsignedShort(paramInt)])));
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
  
  private Attribute readAttribute(Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel) {
    for (byte b1 = 0; b1 < paramArrayOfAttribute.length; b1++) {
      if ((paramArrayOfAttribute[b1]).type.equals(paramString))
        return paramArrayOfAttribute[b1].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel); 
    } 
    return (new Attribute(paramString)).read(this, paramInt1, paramInt2, null, -1, null);
  }
  
  public int getItem(int paramInt) { return this.items[paramInt]; }
  
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
    while (paramInt1 < i) {
      byte b2 = arrayOfByte[paramInt1++] & 0xFF;
      switch (b2 >> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          paramArrayOfChar[b1++] = (char)b2;
          continue;
        case 12:
        case 13:
          b3 = arrayOfByte[paramInt1++];
          paramArrayOfChar[b1++] = (char)((b2 & 0x1F) << 6 | b3 & 0x3F);
          continue;
      } 
      byte b3 = arrayOfByte[paramInt1++];
      byte b4 = arrayOfByte[paramInt1++];
      paramArrayOfChar[b1++] = (char)((b2 & 0xF) << 12 | (b3 & 0x3F) << 6 | b4 & 0x3F);
    } 
    return new String(paramArrayOfChar, 0, b1);
  }
  
  public String readClass(int paramInt, char[] paramArrayOfChar) { return readUTF8(this.items[readUnsignedShort(paramInt)], paramArrayOfChar); }
  
  public Object readConst(int paramInt, char[] paramArrayOfChar) {
    int i = this.items[paramInt];
    switch (this.b[i - 1]) {
      case 3:
        return new Integer(readInt(i));
      case 4:
        return new Float(Float.intBitsToFloat(readInt(i)));
      case 5:
        return new Long(readLong(i));
      case 6:
        return new Double(Double.longBitsToDouble(readLong(i)));
      case 7:
        return Type.getObjectType(readUTF8(i, paramArrayOfChar));
    } 
    return readUTF8(i, paramArrayOfChar);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */