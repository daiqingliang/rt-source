package sun.net.www;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

public class MessageHeader {
  private String[] keys;
  
  private String[] values;
  
  private int nkeys;
  
  public MessageHeader() { grow(); }
  
  public MessageHeader(InputStream paramInputStream) throws IOException { parseHeader(paramInputStream); }
  
  public String getHeaderNamesInList() {
    StringJoiner stringJoiner = new StringJoiner(",");
    for (byte b = 0; b < this.nkeys; b++)
      stringJoiner.add(this.keys[b]); 
    return stringJoiner.toString();
  }
  
  public void reset() {
    this.keys = null;
    this.values = null;
    this.nkeys = 0;
    grow();
  }
  
  public String findValue(String paramString) {
    if (paramString == null) {
      int i = this.nkeys;
      while (--i >= 0) {
        if (this.keys[i] == null)
          return this.values[i]; 
      } 
    } else {
      int i = this.nkeys;
      while (--i >= 0) {
        if (paramString.equalsIgnoreCase(this.keys[i]))
          return this.values[i]; 
      } 
    } 
    return null;
  }
  
  public int getKey(String paramString) {
    int i = this.nkeys;
    while (--i >= 0) {
      if (this.keys[i] == paramString || (paramString != null && paramString.equalsIgnoreCase(this.keys[i])))
        return i; 
    } 
    return -1;
  }
  
  public String getKey(int paramInt) { return (paramInt < 0 || paramInt >= this.nkeys) ? null : this.keys[paramInt]; }
  
  public String getValue(int paramInt) { return (paramInt < 0 || paramInt >= this.nkeys) ? null : this.values[paramInt]; }
  
  public String findNextValue(String paramString1, String paramString2) {
    boolean bool = false;
    if (paramString1 == null) {
      int i = this.nkeys;
      while (--i >= 0) {
        if (this.keys[i] == null) {
          if (bool)
            return this.values[i]; 
          if (this.values[i] == paramString2)
            bool = true; 
        } 
      } 
    } else {
      int i = this.nkeys;
      while (--i >= 0) {
        if (paramString1.equalsIgnoreCase(this.keys[i])) {
          if (bool)
            return this.values[i]; 
          if (this.values[i] == paramString2)
            bool = true; 
        } 
      } 
    } 
    return null;
  }
  
  public boolean filterNTLMResponses(String paramString) {
    boolean bool = false;
    byte b;
    for (b = 0; b < this.nkeys; b++) {
      if (paramString.equalsIgnoreCase(this.keys[b]) && this.values[b] != null && this.values[b].length() > 5 && this.values[b].substring(0, 5).equalsIgnoreCase("NTLM ")) {
        bool = true;
        break;
      } 
    } 
    if (bool) {
      b = 0;
      for (byte b1 = 0; b1 < this.nkeys; b1++) {
        if (!paramString.equalsIgnoreCase(this.keys[b1]) || (!"Negotiate".equalsIgnoreCase(this.values[b1]) && !"Kerberos".equalsIgnoreCase(this.values[b1]))) {
          if (b1 != b) {
            this.keys[b] = this.keys[b1];
            this.values[b] = this.values[b1];
          } 
          b++;
        } 
      } 
      if (b != this.nkeys) {
        this.nkeys = b;
        return true;
      } 
    } 
    return false;
  }
  
  public Iterator<String> multiValueIterator(String paramString) { return new HeaderIterator(paramString, this); }
  
  public Map<String, List<String>> getHeaders() { return getHeaders(null); }
  
  public Map<String, List<String>> getHeaders(String[] paramArrayOfString) { return filterAndAddHeaders(paramArrayOfString, null); }
  
  public Map<String, List<String>> filterAndAddHeaders(String[] paramArrayOfString, Map<String, List<String>> paramMap) {
    boolean bool = false;
    HashMap hashMap = new HashMap();
    int i = this.nkeys;
    while (--i >= 0) {
      if (paramArrayOfString != null)
        for (byte b = 0; b < paramArrayOfString.length; b++) {
          if (paramArrayOfString[b] != null && paramArrayOfString[b].equalsIgnoreCase(this.keys[i])) {
            bool = true;
            break;
          } 
        }  
      if (!bool) {
        List list = (List)hashMap.get(this.keys[i]);
        if (list == null) {
          list = new ArrayList();
          hashMap.put(this.keys[i], list);
        } 
        list.add(this.values[i]);
        continue;
      } 
      bool = false;
    } 
    if (paramMap != null)
      for (Map.Entry entry : paramMap.entrySet()) {
        List list = (List)hashMap.get(entry.getKey());
        if (list == null) {
          list = new ArrayList();
          hashMap.put(entry.getKey(), list);
        } 
        list.addAll((Collection)entry.getValue());
      }  
    for (String str : hashMap.keySet())
      hashMap.put(str, Collections.unmodifiableList((List)hashMap.get(str))); 
    return Collections.unmodifiableMap(hashMap);
  }
  
  public void print(PrintStream paramPrintStream) {
    for (byte b = 0; b < this.nkeys; b++) {
      if (this.keys[b] != null)
        paramPrintStream.print(this.keys[b] + ((this.values[b] != null) ? (": " + this.values[b]) : "") + "\r\n"); 
    } 
    paramPrintStream.print("\r\n");
    paramPrintStream.flush();
  }
  
  public void add(String paramString1, String paramString2) {
    grow();
    this.keys[this.nkeys] = paramString1;
    this.values[this.nkeys] = paramString2;
    this.nkeys++;
  }
  
  public void prepend(String paramString1, String paramString2) {
    grow();
    for (int i = this.nkeys; i > 0; i--) {
      this.keys[i] = this.keys[i - 1];
      this.values[i] = this.values[i - 1];
    } 
    this.keys[0] = paramString1;
    this.values[0] = paramString2;
    this.nkeys++;
  }
  
  public void set(int paramInt, String paramString1, String paramString2) {
    grow();
    if (paramInt < 0)
      return; 
    if (paramInt >= this.nkeys) {
      add(paramString1, paramString2);
    } else {
      this.keys[paramInt] = paramString1;
      this.values[paramInt] = paramString2;
    } 
  }
  
  private void grow() {
    if (this.keys == null || this.nkeys >= this.keys.length) {
      String[] arrayOfString1 = new String[this.nkeys + 4];
      String[] arrayOfString2 = new String[this.nkeys + 4];
      if (this.keys != null)
        System.arraycopy(this.keys, 0, arrayOfString1, 0, this.nkeys); 
      if (this.values != null)
        System.arraycopy(this.values, 0, arrayOfString2, 0, this.nkeys); 
      this.keys = arrayOfString1;
      this.values = arrayOfString2;
    } 
  }
  
  public void remove(String paramString) {
    if (paramString == null) {
      for (byte b = 0; b < this.nkeys; b++) {
        while (this.keys[b] == null && b < this.nkeys) {
          for (byte b1 = b; b1 < this.nkeys - 1; b1++) {
            this.keys[b1] = this.keys[b1 + true];
            this.values[b1] = this.values[b1 + true];
          } 
          this.nkeys--;
        } 
      } 
    } else {
      for (byte b = 0; b < this.nkeys; b++) {
        while (paramString.equalsIgnoreCase(this.keys[b]) && b < this.nkeys) {
          for (byte b1 = b; b1 < this.nkeys - 1; b1++) {
            this.keys[b1] = this.keys[b1 + true];
            this.values[b1] = this.values[b1 + true];
          } 
          this.nkeys--;
        } 
      } 
    } 
  }
  
  public void set(String paramString1, String paramString2) {
    int i = this.nkeys;
    while (--i >= 0) {
      if (paramString1.equalsIgnoreCase(this.keys[i])) {
        this.values[i] = paramString2;
        return;
      } 
    } 
    add(paramString1, paramString2);
  }
  
  public void setIfNotSet(String paramString1, String paramString2) {
    if (findValue(paramString1) == null)
      add(paramString1, paramString2); 
  }
  
  public static String canonicalID(String paramString) {
    if (paramString == null)
      return ""; 
    byte b = 0;
    int i = paramString.length();
    boolean bool;
    char c;
    for (bool = false; b < i && ((c = paramString.charAt(b)) == '<' || c <= ' '); bool = true)
      b++; 
    while (b < i && ((c = paramString.charAt(i - 1)) == '>' || c <= ' ')) {
      i--;
      bool = true;
    } 
    return bool ? paramString.substring(b, i) : paramString;
  }
  
  public void parseHeader(InputStream paramInputStream) throws IOException {
    synchronized (this) {
      this.nkeys = 0;
    } 
    mergeHeader(paramInputStream);
  }
  
  public void mergeHeader(InputStream paramInputStream) throws IOException { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 5
    //   4: return
    //   5: bipush #10
    //   7: newarray char
    //   9: astore_2
    //   10: aload_1
    //   11: invokevirtual read : ()I
    //   14: istore_3
    //   15: iload_3
    //   16: bipush #10
    //   18: if_icmpeq -> 381
    //   21: iload_3
    //   22: bipush #13
    //   24: if_icmpeq -> 381
    //   27: iload_3
    //   28: iflt -> 381
    //   31: iconst_0
    //   32: istore #4
    //   34: iconst_m1
    //   35: istore #5
    //   37: iload_3
    //   38: bipush #32
    //   40: if_icmple -> 47
    //   43: iconst_1
    //   44: goto -> 48
    //   47: iconst_0
    //   48: istore #7
    //   50: aload_2
    //   51: iload #4
    //   53: iinc #4, 1
    //   56: iload_3
    //   57: i2c
    //   58: castore
    //   59: aload_1
    //   60: invokevirtual read : ()I
    //   63: dup
    //   64: istore #6
    //   66: iflt -> 250
    //   69: iload #6
    //   71: lookupswitch default -> 209, 9 -> 140, 10 -> 150, 13 -> 150, 32 -> 144, 58 -> 120
    //   120: iload #7
    //   122: ifeq -> 134
    //   125: iload #4
    //   127: ifle -> 134
    //   130: iload #4
    //   132: istore #5
    //   134: iconst_0
    //   135: istore #7
    //   137: goto -> 209
    //   140: bipush #32
    //   142: istore #6
    //   144: iconst_0
    //   145: istore #7
    //   147: goto -> 209
    //   150: aload_1
    //   151: invokevirtual read : ()I
    //   154: istore_3
    //   155: iload #6
    //   157: bipush #13
    //   159: if_icmpne -> 184
    //   162: iload_3
    //   163: bipush #10
    //   165: if_icmpne -> 184
    //   168: aload_1
    //   169: invokevirtual read : ()I
    //   172: istore_3
    //   173: iload_3
    //   174: bipush #13
    //   176: if_icmpne -> 184
    //   179: aload_1
    //   180: invokevirtual read : ()I
    //   183: istore_3
    //   184: iload_3
    //   185: bipush #10
    //   187: if_icmpeq -> 252
    //   190: iload_3
    //   191: bipush #13
    //   193: if_icmpeq -> 252
    //   196: iload_3
    //   197: bipush #32
    //   199: if_icmple -> 205
    //   202: goto -> 252
    //   205: bipush #32
    //   207: istore #6
    //   209: iload #4
    //   211: aload_2
    //   212: arraylength
    //   213: if_icmplt -> 237
    //   216: aload_2
    //   217: arraylength
    //   218: iconst_2
    //   219: imul
    //   220: newarray char
    //   222: astore #8
    //   224: aload_2
    //   225: iconst_0
    //   226: aload #8
    //   228: iconst_0
    //   229: iload #4
    //   231: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   234: aload #8
    //   236: astore_2
    //   237: aload_2
    //   238: iload #4
    //   240: iinc #4, 1
    //   243: iload #6
    //   245: i2c
    //   246: castore
    //   247: goto -> 59
    //   250: iconst_m1
    //   251: istore_3
    //   252: iload #4
    //   254: ifle -> 274
    //   257: aload_2
    //   258: iload #4
    //   260: iconst_1
    //   261: isub
    //   262: caload
    //   263: bipush #32
    //   265: if_icmpgt -> 274
    //   268: iinc #4, -1
    //   271: goto -> 252
    //   274: iload #5
    //   276: ifgt -> 288
    //   279: aconst_null
    //   280: astore #8
    //   282: iconst_0
    //   283: istore #5
    //   285: goto -> 338
    //   288: aload_2
    //   289: iconst_0
    //   290: iload #5
    //   292: invokestatic copyValueOf : ([CII)Ljava/lang/String;
    //   295: astore #8
    //   297: iload #5
    //   299: iload #4
    //   301: if_icmpge -> 316
    //   304: aload_2
    //   305: iload #5
    //   307: caload
    //   308: bipush #58
    //   310: if_icmpne -> 316
    //   313: iinc #5, 1
    //   316: iload #5
    //   318: iload #4
    //   320: if_icmpge -> 338
    //   323: aload_2
    //   324: iload #5
    //   326: caload
    //   327: bipush #32
    //   329: if_icmpgt -> 338
    //   332: iinc #5, 1
    //   335: goto -> 316
    //   338: iload #5
    //   340: iload #4
    //   342: if_icmplt -> 357
    //   345: new java/lang/String
    //   348: dup
    //   349: invokespecial <init> : ()V
    //   352: astore #9
    //   354: goto -> 370
    //   357: aload_2
    //   358: iload #5
    //   360: iload #4
    //   362: iload #5
    //   364: isub
    //   365: invokestatic copyValueOf : ([CII)Ljava/lang/String;
    //   368: astore #9
    //   370: aload_0
    //   371: aload #8
    //   373: aload #9
    //   375: invokevirtual add : (Ljava/lang/String;Ljava/lang/String;)V
    //   378: goto -> 15
    //   381: return }
  
  public String toString() {
    String str = super.toString() + this.nkeys + " pairs: ";
    for (byte b = 0; b < this.keys.length && b < this.nkeys; b++)
      str = str + "{" + this.keys[b] + ": " + this.values[b] + "}"; 
    return str;
  }
  
  class HeaderIterator extends Object implements Iterator<String> {
    int index = 0;
    
    int next = -1;
    
    String key;
    
    boolean haveNext = false;
    
    Object lock;
    
    public HeaderIterator(String param1String, Object param1Object) {
      this.key = param1String;
      this.lock = param1Object;
    }
    
    public boolean hasNext() {
      synchronized (this.lock) {
        if (this.haveNext)
          return true; 
        while (this.index < MessageHeader.this.nkeys) {
          if (this.key.equalsIgnoreCase(MessageHeader.this.keys[this.index])) {
            this.haveNext = true;
            this.next = this.index++;
            return true;
          } 
          this.index++;
        } 
        return false;
      } 
    }
    
    public String next() {
      synchronized (this.lock) {
        if (this.haveNext) {
          this.haveNext = false;
          return MessageHeader.this.values[this.next];
        } 
        if (hasNext())
          return next(); 
        throw new NoSuchElementException("No more elements");
      } 
    }
    
    public void remove() { throw new UnsupportedOperationException("remove not allowed"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\MessageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */