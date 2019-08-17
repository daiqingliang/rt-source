package sun.security.x509;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class RDN {
  final AVA[] assertion;
  
  public RDN(String paramString) throws IOException { this(paramString, Collections.emptyMap()); }
  
  public RDN(String paramString, Map<String, String> paramMap) throws IOException {
    int i = 0;
    int j = 0;
    int k = 0;
    ArrayList arrayList = new ArrayList(3);
    int m;
    for (m = paramString.indexOf('+'); m >= 0; m = paramString.indexOf('+', j)) {
      i += X500Name.countQuotes(paramString, j, m);
      if (m > 0 && paramString.charAt(m - 1) != '\\' && i != 1) {
        String str1 = paramString.substring(k, m);
        if (str1.length() == 0)
          throw new IOException("empty AVA in RDN \"" + paramString + "\""); 
        AVA aVA1 = new AVA(new StringReader(str1), paramMap);
        arrayList.add(aVA1);
        k = m + 1;
        i = 0;
      } 
      j = m + 1;
    } 
    String str = paramString.substring(k);
    if (str.length() == 0)
      throw new IOException("empty AVA in RDN \"" + paramString + "\""); 
    AVA aVA = new AVA(new StringReader(str), paramMap);
    arrayList.add(aVA);
    this.assertion = (AVA[])arrayList.toArray(new AVA[arrayList.size()]);
  }
  
  RDN(String paramString1, String paramString2) throws IOException { this(paramString1, paramString2, Collections.emptyMap()); }
  
  RDN(String paramString1, String paramString2, Map<String, String> paramMap) throws IOException {
    if (!paramString2.equalsIgnoreCase("RFC2253"))
      throw new IOException("Unsupported format " + paramString2); 
    int i = 0;
    int j = 0;
    ArrayList arrayList = new ArrayList(3);
    int k;
    for (k = paramString1.indexOf('+'); k >= 0; k = paramString1.indexOf('+', i)) {
      if (k > 0 && paramString1.charAt(k - 1) != '\\') {
        String str1 = paramString1.substring(j, k);
        if (str1.length() == 0)
          throw new IOException("empty AVA in RDN \"" + paramString1 + "\""); 
        AVA aVA1 = new AVA(new StringReader(str1), 3, paramMap);
        arrayList.add(aVA1);
        j = k + 1;
      } 
      i = k + 1;
    } 
    String str = paramString1.substring(j);
    if (str.length() == 0)
      throw new IOException("empty AVA in RDN \"" + paramString1 + "\""); 
    AVA aVA = new AVA(new StringReader(str), 3, paramMap);
    arrayList.add(aVA);
    this.assertion = (AVA[])arrayList.toArray(new AVA[arrayList.size()]);
  }
  
  RDN(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 49)
      throw new IOException("X500 RDN"); 
    DerInputStream derInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = derInputStream.getSet(5);
    this.assertion = new AVA[arrayOfDerValue.length];
    for (byte b = 0; b < arrayOfDerValue.length; b++)
      this.assertion[b] = new AVA(arrayOfDerValue[b]); 
  }
  
  RDN(int paramInt) { this.assertion = new AVA[paramInt]; }
  
  public RDN(AVA paramAVA) {
    if (paramAVA == null)
      throw new NullPointerException(); 
    this.assertion = new AVA[] { paramAVA };
  }
  
  public RDN(AVA[] paramArrayOfAVA) {
    this.assertion = (AVA[])paramArrayOfAVA.clone();
    for (byte b = 0; b < this.assertion.length; b++) {
      if (this.assertion[b] == null)
        throw new NullPointerException(); 
    } 
  }
  
  public List<AVA> avas() {
    List list = this.avaList;
    if (list == null) {
      list = Collections.unmodifiableList(Arrays.asList(this.assertion));
      this.avaList = list;
    } 
    return list;
  }
  
  public int size() { return this.assertion.length; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof RDN))
      return false; 
    RDN rDN = (RDN)paramObject;
    if (this.assertion.length != rDN.assertion.length)
      return false; 
    String str1 = toRFC2253String(true);
    String str2 = rDN.toRFC2253String(true);
    return str1.equals(str2);
  }
  
  public int hashCode() { return toRFC2253String(true).hashCode(); }
  
  DerValue findAttribute(ObjectIdentifier paramObjectIdentifier) {
    for (byte b = 0; b < this.assertion.length; b++) {
      if ((this.assertion[b]).oid.equals(paramObjectIdentifier))
        return (this.assertion[b]).value; 
    } 
    return null;
  }
  
  void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putOrderedSetOf((byte)49, this.assertion); }
  
  public String toString() {
    if (this.assertion.length == 1)
      return this.assertion[0].toString(); 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < this.assertion.length; b++) {
      if (b)
        stringBuilder.append(" + "); 
      stringBuilder.append(this.assertion[b].toString());
    } 
    return stringBuilder.toString();
  }
  
  public String toRFC1779String() { return toRFC1779String(Collections.emptyMap()); }
  
  public String toRFC1779String(Map<String, String> paramMap) {
    if (this.assertion.length == 1)
      return this.assertion[0].toRFC1779String(paramMap); 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < this.assertion.length; b++) {
      if (b)
        stringBuilder.append(" + "); 
      stringBuilder.append(this.assertion[b].toRFC1779String(paramMap));
    } 
    return stringBuilder.toString();
  }
  
  public String toRFC2253String() { return toRFC2253StringInternal(false, Collections.emptyMap()); }
  
  public String toRFC2253String(Map<String, String> paramMap) { return toRFC2253StringInternal(false, paramMap); }
  
  public String toRFC2253String(boolean paramBoolean) {
    if (!paramBoolean)
      return toRFC2253StringInternal(false, Collections.emptyMap()); 
    String str = this.canonicalString;
    if (str == null) {
      str = toRFC2253StringInternal(true, Collections.emptyMap());
      this.canonicalString = str;
    } 
    return str;
  }
  
  private String toRFC2253StringInternal(boolean paramBoolean, Map<String, String> paramMap) {
    if (this.assertion.length == 1)
      return paramBoolean ? this.assertion[0].toRFC2253CanonicalString() : this.assertion[0].toRFC2253String(paramMap); 
    AVA[] arrayOfAVA = this.assertion;
    if (paramBoolean) {
      arrayOfAVA = (AVA[])this.assertion.clone();
      Arrays.sort(arrayOfAVA, AVAComparator.getInstance());
    } 
    StringJoiner stringJoiner = new StringJoiner("+");
    for (AVA aVA : arrayOfAVA)
      stringJoiner.add(paramBoolean ? aVA.toRFC2253CanonicalString() : aVA.toRFC2253String(paramMap)); 
    return stringJoiner.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\RDN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */