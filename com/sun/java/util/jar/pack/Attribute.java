package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Attribute extends Object implements Comparable<Attribute> {
  Layout def;
  
  byte[] bytes;
  
  Object fixups;
  
  private static final Map<List<Attribute>, List<Attribute>> canonLists = new HashMap();
  
  private static final Map<Layout, Attribute> attributes = new HashMap();
  
  private static final Map<Layout, Attribute> standardDefs = new HashMap();
  
  static final byte EK_INT = 1;
  
  static final byte EK_BCI = 2;
  
  static final byte EK_BCO = 3;
  
  static final byte EK_FLAG = 4;
  
  static final byte EK_REPL = 5;
  
  static final byte EK_REF = 6;
  
  static final byte EK_UN = 7;
  
  static final byte EK_CASE = 8;
  
  static final byte EK_CALL = 9;
  
  static final byte EK_CBLE = 10;
  
  static final byte EF_SIGN = 1;
  
  static final byte EF_DELTA = 2;
  
  static final byte EF_NULL = 4;
  
  static final byte EF_BACK = 8;
  
  static final int NO_BAND_INDEX = -1;
  
  public String name() { return this.def.name(); }
  
  public Layout layout() { return this.def; }
  
  public byte[] bytes() { return this.bytes; }
  
  public int size() { return this.bytes.length; }
  
  public ConstantPool.Entry getNameRef() { return this.def.getNameRef(); }
  
  private Attribute(Attribute paramAttribute) {
    this.def = paramAttribute.def;
    this.bytes = paramAttribute.bytes;
    this.fixups = paramAttribute.fixups;
  }
  
  public Attribute(Layout paramLayout, byte[] paramArrayOfByte, Object paramObject) {
    this.def = paramLayout;
    this.bytes = paramArrayOfByte;
    this.fixups = paramObject;
    Fixups.setBytes(paramObject, paramArrayOfByte);
  }
  
  public Attribute(Layout paramLayout, byte[] paramArrayOfByte) { this(paramLayout, paramArrayOfByte, null); }
  
  public Attribute addContent(byte[] paramArrayOfByte, Object paramObject) {
    assert isCanonical();
    if (paramArrayOfByte.length == 0 && paramObject == null)
      return this; 
    Attribute attribute = new Attribute(this);
    attribute.bytes = paramArrayOfByte;
    attribute.fixups = paramObject;
    Fixups.setBytes(paramObject, paramArrayOfByte);
    return attribute;
  }
  
  public Attribute addContent(byte[] paramArrayOfByte) { return addContent(paramArrayOfByte, null); }
  
  public void finishRefs(ConstantPool.Index paramIndex) {
    if (this.fixups != null) {
      Fixups.finishRefs(this.fixups, this.bytes, paramIndex);
      this.fixups = null;
    } 
  }
  
  public boolean isCanonical() { return (this == this.def.canon); }
  
  public int compareTo(Attribute paramAttribute) { return this.def.compareTo(paramAttribute.def); }
  
  public static List<Attribute> getCanonList(List<Attribute> paramList) {
    synchronized (canonLists) {
      List list = (List)canonLists.get(paramList);
      if (list == null) {
        list = new ArrayList(paramList.size());
        list.addAll(paramList);
        list = Collections.unmodifiableList(list);
        canonLists.put(paramList, list);
      } 
      return list;
    } 
  }
  
  public static Attribute find(int paramInt, String paramString1, String paramString2) {
    Layout layout = Layout.makeKey(paramInt, paramString1, paramString2);
    synchronized (attributes) {
      Attribute attribute = (Attribute)attributes.get(layout);
      if (attribute == null) {
        attribute = (new Layout(paramInt, paramString1, paramString2)).canonicalInstance();
        attributes.put(layout, attribute);
      } 
      return attribute;
    } 
  }
  
  public static Layout keyForLookup(int paramInt, String paramString) { return Layout.makeKey(paramInt, paramString); }
  
  public static Attribute lookup(Map<Layout, Attribute> paramMap, int paramInt, String paramString) {
    if (paramMap == null)
      paramMap = standardDefs; 
    return (Attribute)paramMap.get(Layout.makeKey(paramInt, paramString));
  }
  
  public static Attribute define(Map<Layout, Attribute> paramMap, int paramInt, String paramString1, String paramString2) {
    Attribute attribute = find(paramInt, paramString1, paramString2);
    paramMap.put(Layout.makeKey(paramInt, paramString1), attribute);
    return attribute;
  }
  
  public static String contextName(int paramInt) {
    switch (paramInt) {
      case 0:
        return "class";
      case 1:
        return "field";
      case 2:
        return "method";
      case 3:
        return "code";
    } 
    return null;
  }
  
  void visitRefs(Holder paramHolder, int paramInt, final Collection<ConstantPool.Entry> refs) {
    if (paramInt == 0)
      paramCollection.add(getNameRef()); 
    if (this.bytes.length == 0)
      return; 
    if (!this.def.hasRefs)
      return; 
    if (this.fixups != null) {
      Fixups.visitRefs(this.fixups, paramCollection);
      return;
    } 
    this.def.parse(paramHolder, this.bytes, 0, this.bytes.length, new ValueStream() {
          public void putInt(int param1Int1, int param1Int2) {}
          
          public void putRef(int param1Int, ConstantPool.Entry param1Entry) { refs.add(param1Entry); }
          
          public int encodeBCI(int param1Int) { return param1Int; }
        });
  }
  
  public void parse(Holder paramHolder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ValueStream paramValueStream) { this.def.parse(paramHolder, paramArrayOfByte, paramInt1, paramInt2, paramValueStream); }
  
  public Object unparse(ValueStream paramValueStream, ByteArrayOutputStream paramByteArrayOutputStream) { return this.def.unparse(paramValueStream, paramByteArrayOutputStream); }
  
  public String toString() { return this.def + "{" + ((this.bytes == null) ? -1 : size()) + "}" + ((this.fixups == null) ? "" : this.fixups.toString()); }
  
  public static String normalizeLayoutString(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j = paramString.length();
    while (i < j) {
      char c = paramString.charAt(i++);
      if (c <= ' ')
        continue; 
      if (c == '#') {
        int k = paramString.indexOf('\n', i);
        int m = paramString.indexOf('\r', i);
        if (k < 0)
          k = j; 
        if (m < 0)
          m = j; 
        i = Math.min(k, m);
        continue;
      } 
      if (c == '\\') {
        stringBuilder.append(paramString.charAt(i++));
        continue;
      } 
      if (c == '0' && paramString.startsWith("0x", i - 1)) {
        int k = i - 1;
        int m = k + 2;
        while (m < j) {
          char c1 = paramString.charAt(m);
          if ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f'))
            m++; 
        } 
        if (m > k) {
          String str = paramString.substring(k, m);
          stringBuilder.append(Integer.decode(str));
          i = m;
          continue;
        } 
        stringBuilder.append(c);
        continue;
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  static Layout.Element[] tokenizeLayout(Layout paramLayout, int paramInt, String paramString) {
    ArrayList arrayList = new ArrayList(paramString.length());
    tokenizeLayout(paramLayout, paramInt, paramString, arrayList);
    Layout.Element[] arrayOfElement = new Layout.Element[arrayList.size()];
    arrayList.toArray(arrayOfElement);
    return arrayOfElement;
  }
  
  static void tokenizeLayout(Layout paramLayout, int paramInt, String paramString, List<Layout.Element> paramList) {
    boolean bool = false;
    int i = paramString.length();
    int j = 0;
    while (j < i) {
      Layout.Element element2;
      int n;
      int m;
      byte b3;
      String str;
      ArrayList arrayList;
      byte b2;
      int k;
      byte b1 = j;
      paramLayout.getClass();
      Layout.Element element1 = new Layout.Element(paramLayout);
      switch (paramString.charAt(j++)) {
        case 'B':
        case 'H':
        case 'I':
        case 'V':
          b2 = 1;
          j = tokenizeUInt(element1, paramString, --j);
          break;
        case 'S':
          b2 = 1;
          j = tokenizeSInt(element1, paramString, --j);
          break;
        case 'P':
          b2 = 2;
          if (paramString.charAt(j++) == 'O') {
            element1.flags = (byte)(element1.flags | 0x2);
            if (!bool) {
              j = -j;
              continue;
            } 
            j++;
          } 
          j = tokenizeUInt(element1, paramString, --j);
          break;
        case 'O':
          b2 = 3;
          element1.flags = (byte)(element1.flags | 0x2);
          if (!bool) {
            j = -j;
            continue;
          } 
          j = tokenizeSInt(element1, paramString, j);
          break;
        case 'F':
          b2 = 4;
          j = tokenizeUInt(element1, paramString, j);
          break;
        case 'N':
          b2 = 5;
          j = tokenizeUInt(element1, paramString, j);
          if (paramString.charAt(j++) != '[') {
            j = -j;
            continue;
          } 
          j = skipBody(paramString, k = j);
          element1.body = tokenizeLayout(paramLayout, paramInt, paramString.substring(k, j++));
          break;
        case 'T':
          b2 = 7;
          j = tokenizeSInt(element1, paramString, j);
          arrayList = new ArrayList();
          label161: while (true) {
            if (paramString.charAt(j++) != '(') {
              j = -j;
              break;
            } 
            int i1 = j;
            j = paramString.indexOf(')', j);
            String str1 = paramString.substring(i1, j++);
            int i2 = str1.length();
            if (paramString.charAt(j++) != '[') {
              j = -j;
              break;
            } 
            if (paramString.charAt(j) == ']') {
              k = j;
            } else {
              j = skipBody(paramString, k = j);
            } 
            Layout.Element[] arrayOfElement = tokenizeLayout(paramLayout, paramInt, paramString.substring(k, j++));
            if (i2 == 0) {
              paramLayout.getClass();
              Layout.Element element = new Layout.Element(paramLayout);
              element.body = arrayOfElement;
              element.kind = 8;
              element.removeBand();
              arrayList.add(element);
              break;
            } 
            boolean bool1 = true;
            int i3;
            for (i3 = 0;; i3 = i4 + 1) {
              int i6;
              int i5;
              int i4 = str1.indexOf(',', i3);
              if (i4 < 0)
                i4 = i2; 
              String str2 = str1.substring(i3, i4);
              if (str2.length() == 0)
                str2 = "empty"; 
              int i7 = findCaseDash(str2, 0);
              if (i7 >= 0) {
                i5 = parseIntBefore(str2, i7);
                i6 = parseIntAfter(str2, i7);
                if (i5 >= i6) {
                  j = -j;
                  continue label161;
                } 
              } else {
                i5 = i6 = Integer.parseInt(str2);
              } 
              while (true) {
                paramLayout.getClass();
                Layout.Element element = new Layout.Element(paramLayout);
                element.body = arrayOfElement;
                element.kind = 8;
                element.removeBand();
                if (!bool1)
                  element.flags = (byte)(element.flags | 0x8); 
                bool1 = false;
                element.value = i5;
                arrayList.add(element);
                if (i5 == i6)
                  break; 
                i5++;
              } 
              if (i4 == i2)
                continue label161; 
            } 
          } 
          element1.body = new Layout.Element[arrayList.size()];
          arrayList.toArray(element1.body);
          element1.kind = b2;
          for (b3 = 0; b3 < element1.body.length - 1; b3++) {
            Layout.Element element = element1.body[b3];
            if (matchCase(element1, element.value) != element) {
              j = -j;
              break;
            } 
          } 
          break;
        case '(':
          b2 = 9;
          element1.removeBand();
          j = paramString.indexOf(')', j);
          str = paramString.substring(b1 + true, j++);
          m = Integer.parseInt(str);
          n = paramInt + m;
          if (!(m + "").equals(str) || paramLayout.elems == null || n < 0 || n >= paramLayout.elems.length) {
            j = -j;
            continue;
          } 
          element2 = paramLayout.elems[n];
          assert element2.kind == 10;
          element1.value = n;
          element1.body = new Layout.Element[] { element2 };
          if (m <= 0) {
            element1.flags = (byte)(element1.flags | 0x8);
            element2.flags = (byte)(element2.flags | 0x8);
          } 
          break;
        case 'K':
          b2 = 6;
          switch (paramString.charAt(j++)) {
            case 'I':
              element1.refKind = 3;
              break;
            case 'J':
              element1.refKind = 5;
              break;
            case 'F':
              element1.refKind = 4;
              break;
            case 'D':
              element1.refKind = 6;
              break;
            case 'S':
              element1.refKind = 8;
              break;
            case 'Q':
              element1.refKind = 53;
              break;
            case 'M':
              element1.refKind = 15;
              break;
            case 'T':
              element1.refKind = 16;
              break;
            case 'L':
              element1.refKind = 51;
              break;
          } 
          j = -j;
          continue;
        case 'R':
          b2 = 6;
          switch (paramString.charAt(j++)) {
            case 'C':
              element1.refKind = 7;
              break;
            case 'S':
              element1.refKind = 13;
              break;
            case 'D':
              element1.refKind = 12;
              break;
            case 'F':
              element1.refKind = 9;
              break;
            case 'M':
              element1.refKind = 10;
              break;
            case 'I':
              element1.refKind = 11;
              break;
            case 'U':
              element1.refKind = 1;
              break;
            case 'Q':
              element1.refKind = 50;
              break;
            case 'Y':
              element1.refKind = 18;
              break;
            case 'B':
              element1.refKind = 17;
              break;
            case 'N':
              element1.refKind = 52;
              break;
          } 
          j = -j;
          continue;
        default:
          j = -j;
          continue;
      } 
      if (b2 == 6) {
        if (paramString.charAt(j++) == 'N') {
          element1.flags = (byte)(element1.flags | 0x4);
          j++;
        } 
        j = tokenizeUInt(element1, paramString, --j);
        paramLayout.hasRefs = true;
      } 
      bool = (b2 == 2) ? 1 : 0;
      element1.kind = b2;
      element1.layout = paramString.substring(b1, j);
      paramList.add(element1);
    } 
  }
  
  static String[] splitBodies(String paramString) {
    ArrayList arrayList = new ArrayList();
    for (int i = 0; i < paramString.length(); i++) {
      if (paramString.charAt(i++) != '[')
        paramString.charAt(-i); 
      byte b;
      i = skipBody(paramString, b = i);
      arrayList.add(paramString.substring(b, i));
    } 
    String[] arrayOfString = new String[arrayList.size()];
    arrayList.toArray(arrayOfString);
    return arrayOfString;
  }
  
  private static int skipBody(String paramString, int paramInt) {
    assert paramString.charAt(paramInt - 1) == '[';
    if (paramString.charAt(paramInt) == ']')
      return -paramInt; 
    byte b = 1;
    while (b) {
      switch (paramString.charAt(paramInt++)) {
        case '[':
          b++;
        case ']':
          b--;
      } 
    } 
    assert paramString.charAt(--paramInt) == ']';
    return paramInt;
  }
  
  private static int tokenizeUInt(Layout.Element paramElement, String paramString, int paramInt) {
    switch (paramString.charAt(paramInt++)) {
      case 'V':
        paramElement.len = 0;
        return paramInt;
      case 'B':
        paramElement.len = 1;
        return paramInt;
      case 'H':
        paramElement.len = 2;
        return paramInt;
      case 'I':
        paramElement.len = 4;
        return paramInt;
    } 
    return -paramInt;
  }
  
  private static int tokenizeSInt(Layout.Element paramElement, String paramString, int paramInt) {
    if (paramString.charAt(paramInt) == 'S') {
      paramElement.flags = (byte)(paramElement.flags | true);
      paramInt++;
    } 
    return tokenizeUInt(paramElement, paramString, paramInt);
  }
  
  private static boolean isDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  static int findCaseDash(String paramString, int paramInt) {
    if (paramInt <= 0)
      paramInt = 1; 
    int i = paramString.length() - 2;
    while (true) {
      int j = paramString.indexOf('-', paramInt);
      if (j < 0 || j > i)
        return -1; 
      if (isDigit(paramString.charAt(j - 1))) {
        char c = paramString.charAt(j + 1);
        if (c == '-' && j + 2 < paramString.length())
          c = paramString.charAt(j + 2); 
        if (isDigit(c))
          return j; 
      } 
      paramInt = j + 1;
    } 
  }
  
  static int parseIntBefore(String paramString, int paramInt) {
    int i = paramInt;
    int j;
    for (j = i; j > 0 && isDigit(paramString.charAt(j - 1)); j--);
    if (j == i)
      return Integer.parseInt("empty"); 
    if (j >= 1 && paramString.charAt(j - 1) == '-')
      j--; 
    assert j == 0 || !isDigit(paramString.charAt(j - 1));
    return Integer.parseInt(paramString.substring(j, i));
  }
  
  static int parseIntAfter(String paramString, int paramInt) {
    int i = paramInt + 1;
    int j = i;
    int k = paramString.length();
    if (j < k && paramString.charAt(j) == '-')
      j++; 
    while (j < k && isDigit(paramString.charAt(j)))
      j++; 
    return (i == j) ? Integer.parseInt("empty") : Integer.parseInt(paramString.substring(i, j));
  }
  
  static String expandCaseDashNotation(String paramString) {
    int i = findCaseDash(paramString, 0);
    if (i < 0)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder(paramString.length() * 3);
    int j = 0;
    do {
      stringBuilder.append(paramString.substring(j, i));
      j = i + 1;
      int k = parseIntBefore(paramString, i);
      int m = parseIntAfter(paramString, i);
      assert k < m;
      stringBuilder.append(",");
      for (int n = k + 1; n < m; n++) {
        stringBuilder.append(n);
        stringBuilder.append(",");
      } 
      i = findCaseDash(paramString, j);
    } while (i >= 0);
    stringBuilder.append(paramString.substring(j));
    return stringBuilder.toString();
  }
  
  static int parseUsing(Layout.Element[] paramArrayOfElement, Holder paramHolder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ValueStream paramValueStream) {
    int i = 0;
    int j = 0;
    int k = paramInt1 + paramInt2;
    int[] arrayOfInt = { 0 };
    for (byte b = 0; b < paramArrayOfElement.length; b++) {
      ConstantPool.Entry entry;
      int i3;
      byte b1;
      Layout.Element element2;
      int i2;
      int i1;
      int n;
      Layout.Element element1 = paramArrayOfElement[b];
      int m = element1.bandIndex;
      switch (element1.kind) {
        case 1:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          n = arrayOfInt[0];
          paramValueStream.putInt(m, n);
          break;
        case 2:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          i1 = arrayOfInt[0];
          i2 = paramValueStream.encodeBCI(i1);
          if (!element1.flagTest((byte)2)) {
            n = i2;
          } else {
            n = i2 - j;
          } 
          i = i1;
          j = i2;
          paramValueStream.putInt(m, n);
          break;
        case 3:
          assert element1.flagTest((byte)2);
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          i1 = i + arrayOfInt[0];
          i2 = paramValueStream.encodeBCI(i1);
          n = i2 - j;
          i = i1;
          j = i2;
          paramValueStream.putInt(m, n);
          break;
        case 4:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          n = arrayOfInt[0];
          paramValueStream.putInt(m, n);
          break;
        case 5:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          n = arrayOfInt[0];
          paramValueStream.putInt(m, n);
          for (b1 = 0; b1 < n; b1++)
            paramInt1 = parseUsing(element1.body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream); 
          break;
        case 7:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          n = arrayOfInt[0];
          paramValueStream.putInt(m, n);
          element2 = matchCase(element1, n);
          paramInt1 = parseUsing(element2.body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream);
          break;
        case 9:
          assert element1.body.length == 1;
          assert (element1.body[0]).kind == 10;
          if (element1.flagTest((byte)8))
            paramValueStream.noteBackCall(element1.value); 
          paramInt1 = parseUsing((element1.body[0]).body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream);
          break;
        case 6:
          paramInt1 = parseInt(element1, paramArrayOfByte, paramInt1, arrayOfInt);
          i3 = arrayOfInt[0];
          if (i3 == 0) {
            entry = null;
          } else {
            ConstantPool.Entry[] arrayOfEntry = paramHolder.getCPMap();
            entry = (i3 >= 0 && i3 < arrayOfEntry.length) ? arrayOfEntry[i3] : null;
            byte b2 = element1.refKind;
            if (entry != null && b2 == 13 && entry.getTag() == 1) {
              String str1 = entry.stringValue();
              entry = ConstantPool.getSignatureEntry(str1);
            } 
            String str = (entry == null) ? "invalid CP index" : ("type=" + ConstantPool.tagName(entry.tag));
            if (entry == null || !entry.tagMatches(b2))
              throw new IllegalArgumentException("Bad constant, expected type=" + ConstantPool.tagName(b2) + " got " + str); 
          } 
          paramValueStream.putRef(m, entry);
          break;
        default:
          assert false;
          break;
      } 
    } 
    return paramInt1;
  }
  
  static Layout.Element matchCase(Layout.Element paramElement, int paramInt) {
    assert paramElement.kind == 7;
    int i = paramElement.body.length - 1;
    for (byte b = 0; b < i; b++) {
      Layout.Element element = paramElement.body[b];
      assert element.kind == 8;
      if (paramInt == element.value)
        return element; 
    } 
    return paramElement.body[i];
  }
  
  private static int parseInt(Layout.Element paramElement, byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt) {
    byte b1 = 0;
    byte b = paramElement.len * 8;
    byte b2 = b;
    b2 -= 8;
    while (b2 >= 0)
      b1 += ((paramArrayOfByte[paramInt++] & 0xFF) << b2); 
    if (b < 32 && paramElement.flagTest((byte)1)) {
      b2 = 32 - b;
      b1 = b1 << b2 >> b2;
    } 
    paramArrayOfInt[0] = b1;
    return paramInt;
  }
  
  static void unparseUsing(Layout.Element[] paramArrayOfElement, Object[] paramArrayOfObject, ValueStream paramValueStream, ByteArrayOutputStream paramByteArrayOutputStream) {
    int i = 0;
    int j = 0;
    for (byte b = 0; b < paramArrayOfElement.length; b++) {
      byte b2;
      ConstantPool.Entry entry;
      Layout.Element element2;
      byte b1;
      int i1;
      int n;
      int m;
      Layout.Element element1 = paramArrayOfElement[b];
      int k = element1.bandIndex;
      switch (element1.kind) {
        case 1:
          m = paramValueStream.getInt(k);
          unparseInt(element1, m, paramByteArrayOutputStream);
          break;
        case 2:
          m = paramValueStream.getInt(k);
          if (!element1.flagTest((byte)2)) {
            i1 = m;
          } else {
            i1 = j + m;
          } 
          assert i == paramValueStream.decodeBCI(j);
          n = paramValueStream.decodeBCI(i1);
          unparseInt(element1, n, paramByteArrayOutputStream);
          i = n;
          j = i1;
          break;
        case 3:
          m = paramValueStream.getInt(k);
          assert element1.flagTest((byte)2);
          assert i == paramValueStream.decodeBCI(j);
          i1 = j + m;
          n = paramValueStream.decodeBCI(i1);
          unparseInt(element1, n - i, paramByteArrayOutputStream);
          i = n;
          j = i1;
          break;
        case 4:
          m = paramValueStream.getInt(k);
          unparseInt(element1, m, paramByteArrayOutputStream);
          break;
        case 5:
          m = paramValueStream.getInt(k);
          unparseInt(element1, m, paramByteArrayOutputStream);
          for (b1 = 0; b1 < m; b1++)
            unparseUsing(element1.body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream); 
          break;
        case 7:
          m = paramValueStream.getInt(k);
          unparseInt(element1, m, paramByteArrayOutputStream);
          element2 = matchCase(element1, m);
          unparseUsing(element2.body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream);
          break;
        case 9:
          assert element1.body.length == 1;
          assert (element1.body[0]).kind == 10;
          unparseUsing((element1.body[0]).body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream);
          break;
        case 6:
          entry = paramValueStream.getRef(k);
          if (entry != null) {
            paramArrayOfObject[0] = Fixups.addRefWithLoc(paramArrayOfObject[0], paramByteArrayOutputStream.size(), entry);
            b2 = 0;
          } else {
            b2 = 0;
          } 
          unparseInt(element1, b2, paramByteArrayOutputStream);
          break;
        default:
          assert false;
          break;
      } 
    } 
  }
  
  private static void unparseInt(Layout.Element paramElement, int paramInt, ByteArrayOutputStream paramByteArrayOutputStream) {
    byte b = paramElement.len * 8;
    if (b == 0)
      return; 
    if (b < 32) {
      int j;
      byte b1 = 32 - b;
      if (paramElement.flagTest((byte)1)) {
        j = paramInt << b1 >> b1;
      } else {
        j = paramInt << b1 >>> b1;
      } 
      if (j != paramInt)
        throw new InternalError("cannot code in " + paramElement.len + " bytes: " + paramInt); 
    } 
    int i = b;
    i -= 8;
    while (i >= 0)
      paramByteArrayOutputStream.write((byte)(paramInt >>> i)); 
  }
  
  static  {
    Map map1 = standardDefs;
    String[] arrayOfString1 = { define(map1, 0, "Signature", "RSH").define(map1, 0, "Synthetic", "").define(map1, 0, "Deprecated", "").define(map1, 0, "SourceFile", "RUH").define(map1, 0, "EnclosingMethod", "RCHRDNH").define(map1, 0, "InnerClasses", "NH[RCHRCNHRUNHFH]").define(map1, 0, "BootstrapMethods", "NH[RMHNH[KLH]]").define(map1, 1, "Signature", "RSH").define(map1, 1, "Synthetic", "").define(map1, 1, "Deprecated", "").define(map1, 1, "ConstantValue", "KQH").define(map1, 2, "Signature", "RSH").define(map1, 2, "Synthetic", "").define(map1, 2, "Deprecated", "").define(map1, 2, "Exceptions", "NH[RCH]").define(map1, 2, "MethodParameters", "NB[RUNHFH]").define(map1, 3, "StackMapTable", "[NH[(1)]][TB(64-127)[(2)](247)[(1)(2)](248-251)[(1)](252)[(1)(2)](253)[(1)(2)(2)](254)[(1)(2)(2)(2)](255)[(1)NH[(2)]NH[(2)]]()[]][H][TB(7)[RCH](8)[PH]()[]]").define(map1, 3, "LineNumberTable", "NH[PHH]").define(map1, 3, "LocalVariableTable", "NH[PHOHRUHRSHH]").define(map1, 3, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]").normalizeLayoutString("\n  # parameter_annotations :=\n  [ NB[(1)] ]     # forward call to annotations"), normalizeLayoutString("\n  # annotations :=\n  [ NH[(1)] ]     # forward call to annotation\n  "), normalizeLayoutString("\n  # annotation :=\n  [RSH\n    NH[RUH (1)]   # forward call to value\n    ]"), normalizeLayoutString("\n  # value :=\n  [TB # Callable 2 encodes one tagged value.\n    (\\B,\\C,\\I,\\S,\\Z)[KIH]\n    (\\D)[KDH]\n    (\\F)[KFH]\n    (\\J)[KJH]\n    (\\c)[RSH]\n    (\\e)[RSH RUH]\n    (\\s)[RUH]\n    (\\[)[NH[(0)]] # backward self-call to value\n    (\\@)[RSH NH[RUH (0)]] # backward self-call to value\n    ()[] ]") };
    String[] arrayOfString2 = { normalizeLayoutString("\n # type-annotations :=\n  [ NH[(1)(2)(3)] ]     # forward call to type-annotations"), normalizeLayoutString("\n  # type-annotation :=\n  [TB\n    (0-1) [B] # {CLASS, METHOD}_TYPE_PARAMETER\n    (16) [FH] # CLASS_EXTENDS\n    (17-18) [BB] # {CLASS, METHOD}_TYPE_PARAMETER_BOUND\n    (19-21) [] # FIELD, METHOD_RETURN, METHOD_RECEIVER\n    (22) [B] # METHOD_FORMAL_PARAMETER\n    (23) [H] # THROWS\n    (64-65) [NH[PHOHH]] # LOCAL_VARIABLE, RESOURCE_VARIABLE\n    (66) [H] # EXCEPTION_PARAMETER\n    (67-70) [PH] # INSTANCEOF, NEW, {CONSTRUCTOR, METHOD}_REFERENCE_RECEIVER\n    (71-75) [PHB] # CAST, {CONSTRUCTOR,METHOD}_INVOCATION_TYPE_ARGUMENT, {CONSTRUCTOR, METHOD}_REFERENCE_TYPE_ARGUMENT\n    ()[] ]"), normalizeLayoutString("\n # type-path\n [ NB[BB] ]") };
    Map map2 = standardDefs;
    String str1 = arrayOfString1[3];
    String str2 = arrayOfString1[1] + arrayOfString1[2] + arrayOfString1[3];
    String str3 = arrayOfString1[0] + str2;
    String str4 = arrayOfString2[0] + arrayOfString2[1] + arrayOfString2[2] + arrayOfString1[2] + arrayOfString1[3];
    for (byte b = 0; b < 4; b++) {
      if (b != 3) {
        define(map2, b, "RuntimeVisibleAnnotations", str2).define(map2, b, "RuntimeInvisibleAnnotations", str2);
        if (b == 2)
          define(map2, b, "RuntimeVisibleParameterAnnotations", str3).define(map2, b, "RuntimeInvisibleParameterAnnotations", str3).define(map2, b, "AnnotationDefault", str1); 
      } 
      define(map2, b, "RuntimeVisibleTypeAnnotations", str4).define(map2, b, "RuntimeInvisibleTypeAnnotations", str4);
    } 
    assert expandCaseDashNotation("1-5").equals("1,2,3,4,5");
    assert expandCaseDashNotation("-2--1").equals("-2,-1");
    assert expandCaseDashNotation("-2-1").equals("-2,-1,0,1");
    assert expandCaseDashNotation("-1-0").equals("-1,0");
  }
  
  public static class FormatException extends IOException {
    private static final long serialVersionUID = -2542243830788066513L;
    
    private int ctype;
    
    private String name;
    
    String layout;
    
    public FormatException(String param1String1, int param1Int, String param1String2, String param1String3) {
      super(Constants.ATTR_CONTEXT_NAME[param1Int] + " attribute \"" + param1String2 + "\"" + ((param1String1 == null) ? "" : (": " + param1String1)));
      this.ctype = param1Int;
      this.name = param1String2;
      this.layout = param1String3;
    }
    
    public FormatException(String param1String1, int param1Int, String param1String2) { this(param1String1, param1Int, param1String2, null); }
  }
  
  public static abstract class Holder {
    protected int flags;
    
    protected List<Attribute> attributes;
    
    static final List<Attribute> noAttributes = Arrays.asList(new Attribute[0]);
    
    protected abstract ConstantPool.Entry[] getCPMap();
    
    public int attributeSize() { return (this.attributes == null) ? 0 : this.attributes.size(); }
    
    public void trimToSize() {
      if (this.attributes == null)
        return; 
      if (this.attributes.isEmpty()) {
        this.attributes = null;
        return;
      } 
      if (this.attributes instanceof ArrayList) {
        ArrayList arrayList = (ArrayList)this.attributes;
        arrayList.trimToSize();
        boolean bool = true;
        for (Attribute attribute : arrayList) {
          if (!attribute.isCanonical())
            bool = false; 
          if (attribute.fixups != null) {
            assert !attribute.isCanonical();
            attribute.fixups = Fixups.trimToSize(attribute.fixups);
          } 
        } 
        if (bool)
          this.attributes = Attribute.getCanonList(arrayList); 
      } 
    }
    
    public void addAttribute(Attribute param1Attribute) {
      if (this.attributes == null) {
        this.attributes = new ArrayList(3);
      } else if (!(this.attributes instanceof ArrayList)) {
        this.attributes = new ArrayList(this.attributes);
      } 
      this.attributes.add(param1Attribute);
    }
    
    public Attribute removeAttribute(Attribute param1Attribute) {
      if (this.attributes == null)
        return null; 
      if (!this.attributes.contains(param1Attribute))
        return null; 
      if (!(this.attributes instanceof ArrayList))
        this.attributes = new ArrayList(this.attributes); 
      this.attributes.remove(param1Attribute);
      return param1Attribute;
    }
    
    public Attribute getAttribute(int param1Int) { return (Attribute)this.attributes.get(param1Int); }
    
    protected void visitRefs(int param1Int, Collection<ConstantPool.Entry> param1Collection) {
      if (this.attributes == null)
        return; 
      for (Attribute attribute : this.attributes)
        attribute.visitRefs(this, param1Int, param1Collection); 
    }
    
    public List<Attribute> getAttributes() { return (this.attributes == null) ? noAttributes : this.attributes; }
    
    public void setAttributes(List<Attribute> param1List) {
      if (param1List.isEmpty()) {
        this.attributes = null;
      } else {
        this.attributes = param1List;
      } 
    }
    
    public Attribute getAttribute(String param1String) {
      if (this.attributes == null)
        return null; 
      for (Attribute attribute : this.attributes) {
        if (attribute.name().equals(param1String))
          return attribute; 
      } 
      return null;
    }
    
    public Attribute getAttribute(Attribute.Layout param1Layout) {
      if (this.attributes == null)
        return null; 
      for (Attribute attribute : this.attributes) {
        if (attribute.layout() == param1Layout)
          return attribute; 
      } 
      return null;
    }
    
    public Attribute removeAttribute(String param1String) { return removeAttribute(getAttribute(param1String)); }
    
    public Attribute removeAttribute(Attribute.Layout param1Layout) { return removeAttribute(getAttribute(param1Layout)); }
    
    public void strip(String param1String) { removeAttribute(getAttribute(param1String)); }
  }
  
  public static class Layout extends Object implements Comparable<Layout> {
    int ctype;
    
    String name;
    
    boolean hasRefs;
    
    String layout;
    
    int bandCount;
    
    Element[] elems;
    
    Attribute canon;
    
    private static final Element[] noElems = new Element[0];
    
    public int ctype() { return this.ctype; }
    
    public String name() { return this.name; }
    
    public String layout() { return this.layout; }
    
    public Attribute canonicalInstance() { return this.canon; }
    
    public ConstantPool.Entry getNameRef() { return ConstantPool.getUtf8Entry(name()); }
    
    public boolean isEmpty() { return this.layout.isEmpty(); }
    
    public Layout(int param1Int, String param1String1, String param1String2) {
      this.ctype = param1Int;
      this.name = param1String1.intern();
      this.layout = param1String2.intern();
      assert param1Int < 4;
      boolean bool = param1String2.startsWith("[");
      try {
        if (!bool) {
          this.elems = Attribute.tokenizeLayout(this, -1, param1String2);
        } else {
          String[] arrayOfString = Attribute.splitBodies(param1String2);
          Element[] arrayOfElement = new Element[arrayOfString.length];
          this.elems = arrayOfElement;
          byte b;
          for (b = 0; b < arrayOfElement.length; b++) {
            Element element = new Element();
            element.kind = 10;
            element.removeBand();
            element.bandIndex = -1;
            element.layout = arrayOfString[b];
            arrayOfElement[b] = element;
          } 
          for (b = 0; b < arrayOfElement.length; b++) {
            Element element = arrayOfElement[b];
            element.body = Attribute.tokenizeLayout(this, b, arrayOfString[b]);
          } 
        } 
      } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
        throw new RuntimeException("Bad attribute layout: " + param1String2, stringIndexOutOfBoundsException);
      } 
      this.canon = new Attribute(this, Constants.noBytes);
    }
    
    private Layout() {}
    
    static Layout makeKey(int param1Int, String param1String1, String param1String2) {
      Layout layout1 = new Layout();
      layout1.ctype = param1Int;
      layout1.name = param1String1.intern();
      layout1.layout = param1String2.intern();
      assert param1Int < 4;
      return layout1;
    }
    
    static Layout makeKey(int param1Int, String param1String) { return makeKey(param1Int, param1String, ""); }
    
    public Attribute addContent(byte[] param1ArrayOfByte, Object param1Object) { return this.canon.addContent(param1ArrayOfByte, param1Object); }
    
    public Attribute addContent(byte[] param1ArrayOfByte) { return this.canon.addContent(param1ArrayOfByte, null); }
    
    public boolean equals(Object param1Object) { return (param1Object != null && param1Object.getClass() == Layout.class && equals((Layout)param1Object)); }
    
    public boolean equals(Layout param1Layout) { return (this.name.equals(param1Layout.name) && this.layout.equals(param1Layout.layout) && this.ctype == param1Layout.ctype); }
    
    public int hashCode() { return ((17 + this.name.hashCode()) * 37 + this.layout.hashCode()) * 37 + this.ctype; }
    
    public int compareTo(Layout param1Layout) {
      int i = this.name.compareTo(param1Layout.name);
      if (i != 0)
        return i; 
      i = this.layout.compareTo(param1Layout.layout);
      return (i != 0) ? i : (this.ctype - param1Layout.ctype);
    }
    
    public String toString() {
      String str = Attribute.contextName(this.ctype) + "." + this.name + "[" + this.layout + "]";
      assert (str = stringForDebug()) != null;
      return str;
    }
    
    private String stringForDebug() { return Attribute.contextName(this.ctype) + "." + this.name + Arrays.asList(this.elems); }
    
    public boolean hasCallables() { return (this.elems.length > 0 && (this.elems[0]).kind == 10); }
    
    public Element[] getCallables() { return hasCallables() ? (Element[])Arrays.copyOf(this.elems, this.elems.length) : noElems; }
    
    public Element[] getEntryPoint() { return hasCallables() ? (this.elems[0]).body : (Element[])Arrays.copyOf(this.elems, this.elems.length); }
    
    public void parse(Attribute.Holder param1Holder, byte[] param1ArrayOfByte, int param1Int1, int param1Int2, Attribute.ValueStream param1ValueStream) {
      int i = Attribute.parseUsing(getEntryPoint(), param1Holder, param1ArrayOfByte, param1Int1, param1Int2, param1ValueStream);
      if (i != param1Int1 + param1Int2)
        throw new InternalError("layout parsed " + (i - param1Int1) + " out of " + param1Int2 + " bytes"); 
    }
    
    public Object unparse(Attribute.ValueStream param1ValueStream, ByteArrayOutputStream param1ByteArrayOutputStream) {
      Object[] arrayOfObject = { null };
      Attribute.unparseUsing(getEntryPoint(), arrayOfObject, param1ValueStream, param1ByteArrayOutputStream);
      return arrayOfObject[0];
    }
    
    public String layoutForClassVersion(Package.Version param1Version) { return param1Version.lessThan(Constants.JAVA6_MAX_CLASS_VERSION) ? Attribute.expandCaseDashNotation(this.layout) : this.layout; }
    
    public class Element {
      String layout;
      
      byte flags;
      
      byte kind;
      
      byte len;
      
      byte refKind;
      
      int bandIndex = Attribute.Layout.this.bandCount++;
      
      int value;
      
      Element[] body;
      
      boolean flagTest(byte param2Byte) { return ((this.flags & param2Byte) != 0); }
      
      void removeBand() {
        Attribute.Layout.this.bandCount--;
        assert this.bandIndex == Attribute.Layout.this.bandCount;
        this.bandIndex = -1;
      }
      
      public boolean hasBand() { return (this.bandIndex >= 0); }
      
      public String toString() {
        String str = this.layout;
        assert (str = stringForDebug()) != null;
        return str;
      }
      
      private String stringForDebug() {
        Element[] arrayOfElement = this.body;
        switch (this.kind) {
          case 9:
            arrayOfElement = null;
            break;
          case 8:
            if (flagTest((byte)8))
              arrayOfElement = null; 
            break;
        } 
        return this.layout + (!hasBand() ? "" : ("#" + this.bandIndex)) + "<" + ((this.flags == 0) ? "" : ("" + this.flags)) + this.kind + this.len + ((this.refKind == 0) ? "" : ("" + this.refKind)) + ">" + ((this.value == 0) ? "" : ("(" + this.value + ")")) + ((arrayOfElement == null) ? "" : ("" + Arrays.asList(arrayOfElement)));
      }
    }
  }
  
  public class Element {
    String layout;
    
    byte flags;
    
    byte kind;
    
    byte len;
    
    byte refKind;
    
    int bandIndex = Attribute.this.bandCount++;
    
    int value;
    
    Element[] body;
    
    boolean flagTest(byte param1Byte) { return ((this.flags & param1Byte) != 0); }
    
    Element() {}
    
    void removeBand() {
      this.this$0.bandCount--;
      assert this.bandIndex == this.this$0.bandCount;
      this.bandIndex = -1;
    }
    
    public boolean hasBand() { return (this.bandIndex >= 0); }
    
    public String toString() {
      String str = this.layout;
      assert (str = stringForDebug()) != null;
      return str;
    }
    
    private String stringForDebug() {
      Element[] arrayOfElement = this.body;
      switch (this.kind) {
        case 9:
          arrayOfElement = null;
          break;
        case 8:
          if (flagTest((byte)8))
            arrayOfElement = null; 
          break;
      } 
      return this.layout + (!hasBand() ? "" : ("#" + this.bandIndex)) + "<" + ((this.flags == 0) ? "" : ("" + this.flags)) + this.kind + this.len + ((this.refKind == 0) ? "" : ("" + this.refKind)) + ">" + ((this.value == 0) ? "" : ("(" + this.value + ")")) + ((arrayOfElement == null) ? "" : ("" + Arrays.asList(arrayOfElement)));
    }
  }
  
  public static abstract class ValueStream {
    public int getInt(int param1Int) { throw undef(); }
    
    public void putInt(int param1Int1, int param1Int2) { throw undef(); }
    
    public ConstantPool.Entry getRef(int param1Int) { throw undef(); }
    
    public void putRef(int param1Int, ConstantPool.Entry param1Entry) { throw undef(); }
    
    public int decodeBCI(int param1Int) { throw undef(); }
    
    public int encodeBCI(int param1Int) { throw undef(); }
    
    public void noteBackCall(int param1Int) {}
    
    private RuntimeException undef() { return new UnsupportedOperationException("ValueStream method"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */