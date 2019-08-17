package sun.security.x509;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X500Name implements GeneralNameInterface, Principal {
  private String dn;
  
  private String rfc1779Dn;
  
  private String rfc2253Dn;
  
  private String canonicalDn;
  
  private RDN[] names;
  
  private X500Principal x500Principal;
  
  private byte[] encoded;
  
  private static final Map<ObjectIdentifier, ObjectIdentifier> internedOIDs = new HashMap();
  
  private static final int[] commonName_data = { 2, 5, 4, 3 };
  
  private static final int[] SURNAME_DATA = { 2, 5, 4, 4 };
  
  private static final int[] SERIALNUMBER_DATA = { 2, 5, 4, 5 };
  
  private static final int[] countryName_data = { 2, 5, 4, 6 };
  
  private static final int[] localityName_data = { 2, 5, 4, 7 };
  
  private static final int[] stateName_data = { 2, 5, 4, 8 };
  
  private static final int[] streetAddress_data = { 2, 5, 4, 9 };
  
  private static final int[] orgName_data = { 2, 5, 4, 10 };
  
  private static final int[] orgUnitName_data = { 2, 5, 4, 11 };
  
  private static final int[] title_data = { 2, 5, 4, 12 };
  
  private static final int[] GIVENNAME_DATA = { 2, 5, 4, 42 };
  
  private static final int[] INITIALS_DATA = { 2, 5, 4, 43 };
  
  private static final int[] GENERATIONQUALIFIER_DATA = { 2, 5, 4, 44 };
  
  private static final int[] DNQUALIFIER_DATA = { 2, 5, 4, 46 };
  
  private static final int[] ipAddress_data = { 
      1, 3, 6, 1, 4, 1, 42, 2, 11, 2, 
      1 };
  
  private static final int[] DOMAIN_COMPONENT_DATA = { 0, 9, 2342, 19200300, 100, 1, 25 };
  
  private static final int[] userid_data = { 0, 9, 2342, 19200300, 100, 1, 1 };
  
  public static final ObjectIdentifier commonName_oid;
  
  public static final ObjectIdentifier countryName_oid;
  
  public static final ObjectIdentifier localityName_oid;
  
  public static final ObjectIdentifier orgName_oid;
  
  public static final ObjectIdentifier orgUnitName_oid;
  
  public static final ObjectIdentifier stateName_oid;
  
  public static final ObjectIdentifier streetAddress_oid;
  
  public static final ObjectIdentifier title_oid;
  
  public static final ObjectIdentifier DNQUALIFIER_OID;
  
  public static final ObjectIdentifier SURNAME_OID;
  
  public static final ObjectIdentifier GIVENNAME_OID;
  
  public static final ObjectIdentifier INITIALS_OID;
  
  public static final ObjectIdentifier GENERATIONQUALIFIER_OID;
  
  public static final ObjectIdentifier ipAddress_oid;
  
  public static final ObjectIdentifier DOMAIN_COMPONENT_OID;
  
  public static final ObjectIdentifier userid_oid = intern((DOMAIN_COMPONENT_OID = intern((ipAddress_oid = intern((GENERATIONQUALIFIER_OID = intern((INITIALS_OID = intern((GIVENNAME_OID = intern((SURNAME_OID = intern((DNQUALIFIER_OID = intern((title_oid = intern((streetAddress_oid = intern((stateName_oid = intern((orgUnitName_oid = intern((orgName_oid = intern((localityName_oid = intern((countryName_oid = intern((SERIALNUMBER_OID = intern((commonName_oid = intern(ObjectIdentifier.newInternal(commonName_data))).newInternal(SERIALNUMBER_DATA))).newInternal(countryName_data))).newInternal(localityName_data))).newInternal(orgName_data))).newInternal(orgUnitName_data))).newInternal(stateName_data))).newInternal(streetAddress_data))).newInternal(title_data))).newInternal(DNQUALIFIER_DATA))).newInternal(SURNAME_DATA))).newInternal(GIVENNAME_DATA))).newInternal(INITIALS_DATA))).newInternal(GENERATIONQUALIFIER_DATA))).newInternal(ipAddress_data))).newInternal(DOMAIN_COMPONENT_DATA))).newInternal(userid_data));
  
  public static final ObjectIdentifier SERIALNUMBER_OID;
  
  private static final Constructor<X500Principal> principalConstructor;
  
  private static final Field principalField;
  
  public X500Name(String paramString) throws IOException { this(paramString, Collections.emptyMap()); }
  
  public X500Name(String paramString, Map<String, String> paramMap) throws IOException { parseDN(paramString, paramMap); }
  
  public X500Name(String paramString1, String paramString2) throws IOException {
    if (paramString1 == null)
      throw new NullPointerException("Name must not be null"); 
    if (paramString2.equalsIgnoreCase("RFC2253")) {
      parseRFC2253DN(paramString1);
    } else if (paramString2.equalsIgnoreCase("DEFAULT")) {
      parseDN(paramString1, Collections.emptyMap());
    } else {
      throw new IOException("Unsupported format " + paramString2);
    } 
  }
  
  public X500Name(String paramString1, String paramString2, String paramString3, String paramString4) throws IOException {
    this.names = new RDN[4];
    this.names[3] = new RDN(1);
    (this.names[3]).assertion[0] = new AVA(commonName_oid, new DerValue(paramString1));
    this.names[2] = new RDN(1);
    (this.names[2]).assertion[0] = new AVA(orgUnitName_oid, new DerValue(paramString2));
    this.names[1] = new RDN(1);
    (this.names[1]).assertion[0] = new AVA(orgName_oid, new DerValue(paramString3));
    this.names[0] = new RDN(1);
    (this.names[0]).assertion[0] = new AVA(countryName_oid, new DerValue(paramString4));
  }
  
  public X500Name(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) throws IOException {
    this.names = new RDN[6];
    this.names[5] = new RDN(1);
    (this.names[5]).assertion[0] = new AVA(commonName_oid, new DerValue(paramString1));
    this.names[4] = new RDN(1);
    (this.names[4]).assertion[0] = new AVA(orgUnitName_oid, new DerValue(paramString2));
    this.names[3] = new RDN(1);
    (this.names[3]).assertion[0] = new AVA(orgName_oid, new DerValue(paramString3));
    this.names[2] = new RDN(1);
    (this.names[2]).assertion[0] = new AVA(localityName_oid, new DerValue(paramString4));
    this.names[1] = new RDN(1);
    (this.names[1]).assertion[0] = new AVA(stateName_oid, new DerValue(paramString5));
    this.names[0] = new RDN(1);
    (this.names[0]).assertion[0] = new AVA(countryName_oid, new DerValue(paramString6));
  }
  
  public X500Name(RDN[] paramArrayOfRDN) throws IOException {
    if (paramArrayOfRDN == null) {
      this.names = new RDN[0];
    } else {
      this.names = (RDN[])paramArrayOfRDN.clone();
      for (byte b = 0; b < this.names.length; b++) {
        if (this.names[b] == null)
          throw new IOException("Cannot create an X500Name"); 
      } 
    } 
  }
  
  public X500Name(DerValue paramDerValue) throws IOException { this(paramDerValue.toDerInputStream()); }
  
  public X500Name(DerInputStream paramDerInputStream) throws IOException { parseDER(paramDerInputStream); }
  
  public X500Name(byte[] paramArrayOfByte) throws IOException {
    DerInputStream derInputStream = new DerInputStream(paramArrayOfByte);
    parseDER(derInputStream);
  }
  
  public List<RDN> rdns() {
    List list = this.rdnList;
    if (list == null) {
      list = Collections.unmodifiableList(Arrays.asList(this.names));
      this.rdnList = list;
    } 
    return list;
  }
  
  public int size() { return this.names.length; }
  
  public List<AVA> allAvas() {
    List list = this.allAvaList;
    if (list == null) {
      list = new ArrayList();
      for (byte b = 0; b < this.names.length; b++)
        list.addAll(this.names[b].avas()); 
      list = Collections.unmodifiableList(list);
      this.allAvaList = list;
    } 
    return list;
  }
  
  public int avaSize() { return allAvas().size(); }
  
  public boolean isEmpty() {
    int i = this.names.length;
    for (byte b = 0; b < i; b++) {
      if ((this.names[b]).assertion.length != 0)
        return false; 
    } 
    return true;
  }
  
  public int hashCode() { return getRFC2253CanonicalName().hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X500Name))
      return false; 
    X500Name x500Name = (X500Name)paramObject;
    if (this.canonicalDn != null && x500Name.canonicalDn != null)
      return this.canonicalDn.equals(x500Name.canonicalDn); 
    int i = this.names.length;
    if (i != x500Name.names.length)
      return false; 
    for (byte b = 0; b < i; b++) {
      RDN rDN1 = this.names[b];
      RDN rDN2 = x500Name.names[b];
      if (rDN1.assertion.length != rDN2.assertion.length)
        return false; 
    } 
    String str1 = getRFC2253CanonicalName();
    String str2 = x500Name.getRFC2253CanonicalName();
    return str1.equals(str2);
  }
  
  private String getString(DerValue paramDerValue) throws IOException {
    if (paramDerValue == null)
      return null; 
    String str = paramDerValue.getAsString();
    if (str == null)
      throw new IOException("not a DER string encoding, " + paramDerValue.tag); 
    return str;
  }
  
  public int getType() { return 4; }
  
  public String getCountry() throws IOException {
    DerValue derValue = findAttribute(countryName_oid);
    return getString(derValue);
  }
  
  public String getOrganization() throws IOException {
    DerValue derValue = findAttribute(orgName_oid);
    return getString(derValue);
  }
  
  public String getOrganizationalUnit() throws IOException {
    DerValue derValue = findAttribute(orgUnitName_oid);
    return getString(derValue);
  }
  
  public String getCommonName() throws IOException {
    DerValue derValue = findAttribute(commonName_oid);
    return getString(derValue);
  }
  
  public String getLocality() throws IOException {
    DerValue derValue = findAttribute(localityName_oid);
    return getString(derValue);
  }
  
  public String getState() throws IOException {
    DerValue derValue = findAttribute(stateName_oid);
    return getString(derValue);
  }
  
  public String getDomain() throws IOException {
    DerValue derValue = findAttribute(DOMAIN_COMPONENT_OID);
    return getString(derValue);
  }
  
  public String getDNQualifier() throws IOException {
    DerValue derValue = findAttribute(DNQUALIFIER_OID);
    return getString(derValue);
  }
  
  public String getSurname() throws IOException {
    DerValue derValue = findAttribute(SURNAME_OID);
    return getString(derValue);
  }
  
  public String getGivenName() throws IOException {
    DerValue derValue = findAttribute(GIVENNAME_OID);
    return getString(derValue);
  }
  
  public String getInitials() throws IOException {
    DerValue derValue = findAttribute(INITIALS_OID);
    return getString(derValue);
  }
  
  public String getGeneration() throws IOException {
    DerValue derValue = findAttribute(GENERATIONQUALIFIER_OID);
    return getString(derValue);
  }
  
  public String getIP() throws IOException {
    DerValue derValue = findAttribute(ipAddress_oid);
    return getString(derValue);
  }
  
  public String toString() throws IOException {
    if (this.dn == null)
      generateDN(); 
    return this.dn;
  }
  
  public String getRFC1779Name() throws IOException { return getRFC1779Name(Collections.emptyMap()); }
  
  public String getRFC1779Name(Map<String, String> paramMap) throws IllegalArgumentException {
    if (paramMap.isEmpty()) {
      if (this.rfc1779Dn != null)
        return this.rfc1779Dn; 
      this.rfc1779Dn = generateRFC1779DN(paramMap);
      return this.rfc1779Dn;
    } 
    return generateRFC1779DN(paramMap);
  }
  
  public String getRFC2253Name() throws IOException { return getRFC2253Name(Collections.emptyMap()); }
  
  public String getRFC2253Name(Map<String, String> paramMap) throws IllegalArgumentException {
    if (paramMap.isEmpty()) {
      if (this.rfc2253Dn != null)
        return this.rfc2253Dn; 
      this.rfc2253Dn = generateRFC2253DN(paramMap);
      return this.rfc2253Dn;
    } 
    return generateRFC2253DN(paramMap);
  }
  
  private String generateRFC2253DN(Map<String, String> paramMap) throws IllegalArgumentException {
    if (this.names.length == 0)
      return ""; 
    StringBuilder stringBuilder = new StringBuilder(48);
    for (int i = this.names.length - 1; i >= 0; i--) {
      if (i < this.names.length - 1)
        stringBuilder.append(','); 
      stringBuilder.append(this.names[i].toRFC2253String(paramMap));
    } 
    return stringBuilder.toString();
  }
  
  public String getRFC2253CanonicalName() throws IOException {
    if (this.canonicalDn != null)
      return this.canonicalDn; 
    if (this.names.length == 0) {
      this.canonicalDn = "";
      return this.canonicalDn;
    } 
    StringBuilder stringBuilder = new StringBuilder(48);
    for (int i = this.names.length - 1; i >= 0; i--) {
      if (i < this.names.length - 1)
        stringBuilder.append(','); 
      stringBuilder.append(this.names[i].toRFC2253String(true));
    } 
    this.canonicalDn = stringBuilder.toString();
    return this.canonicalDn;
  }
  
  public String getName() throws IOException { return toString(); }
  
  private DerValue findAttribute(ObjectIdentifier paramObjectIdentifier) {
    if (this.names != null)
      for (byte b = 0; b < this.names.length; b++) {
        DerValue derValue = this.names[b].findAttribute(paramObjectIdentifier);
        if (derValue != null)
          return derValue; 
      }  
    return null;
  }
  
  public DerValue findMostSpecificAttribute(ObjectIdentifier paramObjectIdentifier) {
    if (this.names != null)
      for (int i = this.names.length - 1; i >= 0; i--) {
        DerValue derValue = this.names[i].findAttribute(paramObjectIdentifier);
        if (derValue != null)
          return derValue; 
      }  
    return null;
  }
  
  private void parseDER(DerInputStream paramDerInputStream) throws IOException {
    DerValue[] arrayOfDerValue = null;
    byte[] arrayOfByte = paramDerInputStream.toByteArray();
    try {
      arrayOfDerValue = paramDerInputStream.getSequence(5);
    } catch (IOException iOException) {
      if (arrayOfByte == null) {
        arrayOfDerValue = null;
      } else {
        DerValue derValue = new DerValue((byte)48, arrayOfByte);
        arrayOfByte = derValue.toByteArray();
        arrayOfDerValue = (new DerInputStream(arrayOfByte)).getSequence(5);
      } 
    } 
    if (arrayOfDerValue == null) {
      this.names = new RDN[0];
    } else {
      this.names = new RDN[arrayOfDerValue.length];
      for (byte b = 0; b < arrayOfDerValue.length; b++)
        this.names[b] = new RDN(arrayOfDerValue[b]); 
    } 
  }
  
  @Deprecated
  public void emit(DerOutputStream paramDerOutputStream) throws IOException { encode(paramDerOutputStream); }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    for (byte b = 0; b < this.names.length; b++)
      this.names[b].encode(derOutputStream); 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public byte[] getEncodedInternal() throws IOException {
    if (this.encoded == null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      for (byte b = 0; b < this.names.length; b++)
        this.names[b].encode(derOutputStream2); 
      derOutputStream1.write((byte)48, derOutputStream2);
      this.encoded = derOutputStream1.toByteArray();
    } 
    return this.encoded;
  }
  
  public byte[] getEncoded() throws IOException { return (byte[])getEncodedInternal().clone(); }
  
  private void parseDN(String paramString, Map<String, String> paramMap) throws IOException {
    if (paramString == null || paramString.length() == 0) {
      this.names = new RDN[0];
      return;
    } 
    ArrayList arrayList = new ArrayList();
    int i = 0;
    int j = 0;
    String str2 = paramString;
    int k = 0;
    int m = str2.indexOf(',');
    int n;
    for (n = str2.indexOf(';'); m >= 0 || n >= 0; n = str2.indexOf(';', k)) {
      int i1;
      if (n < 0) {
        i1 = m;
      } else if (m < 0) {
        i1 = n;
      } else {
        i1 = Math.min(m, n);
      } 
      j += countQuotes(str2, k, i1);
      if (i1 >= 0 && j != 1 && !escaped(i1, k, str2)) {
        String str = str2.substring(i, i1);
        RDN rDN1 = new RDN(str, paramMap);
        arrayList.add(rDN1);
        i = i1 + 1;
        j = 0;
      } 
      k = i1 + 1;
      m = str2.indexOf(',', k);
    } 
    String str1 = str2.substring(i);
    RDN rDN = new RDN(str1, paramMap);
    arrayList.add(rDN);
    Collections.reverse(arrayList);
    this.names = (RDN[])arrayList.toArray(new RDN[arrayList.size()]);
  }
  
  private void parseRFC2253DN(String paramString) throws IOException {
    if (paramString.length() == 0) {
      this.names = new RDN[0];
      return;
    } 
    ArrayList arrayList = new ArrayList();
    int i = 0;
    int j = 0;
    int k;
    for (k = paramString.indexOf(','); k >= 0; k = paramString.indexOf(',', j)) {
      if (k > 0 && !escaped(k, j, paramString)) {
        String str1 = paramString.substring(i, k);
        RDN rDN1 = new RDN(str1, "RFC2253");
        arrayList.add(rDN1);
        i = k + 1;
      } 
      j = k + 1;
    } 
    String str = paramString.substring(i);
    RDN rDN = new RDN(str, "RFC2253");
    arrayList.add(rDN);
    Collections.reverse(arrayList);
    this.names = (RDN[])arrayList.toArray(new RDN[arrayList.size()]);
  }
  
  static int countQuotes(String paramString, int paramInt1, int paramInt2) {
    byte b = 0;
    for (int i = paramInt1; i < paramInt2; i++) {
      if ((paramString.charAt(i) == '"' && i == paramInt1) || (paramString.charAt(i) == '"' && paramString.charAt(i - 1) != '\\'))
        b++; 
    } 
    return b;
  }
  
  private static boolean escaped(int paramInt1, int paramInt2, String paramString) {
    if (paramInt1 == 1 && paramString.charAt(paramInt1 - 1) == '\\')
      return true; 
    if (paramInt1 > 1 && paramString.charAt(paramInt1 - 1) == '\\' && paramString.charAt(paramInt1 - 2) != '\\')
      return true; 
    if (paramInt1 > 1 && paramString.charAt(paramInt1 - 1) == '\\' && paramString.charAt(paramInt1 - 2) == '\\') {
      byte b = 0;
      while (--paramInt1 >= paramInt2) {
        if (paramString.charAt(paramInt1) == '\\')
          b++; 
        paramInt1--;
      } 
      return (b % 2 != 0);
    } 
    return false;
  }
  
  private void generateDN() {
    if (this.names.length == 1) {
      this.dn = this.names[0].toString();
      return;
    } 
    StringBuilder stringBuilder = new StringBuilder(48);
    if (this.names != null)
      for (int i = this.names.length - 1; i >= 0; i--) {
        if (i != this.names.length - 1)
          stringBuilder.append(", "); 
        stringBuilder.append(this.names[i].toString());
      }  
    this.dn = stringBuilder.toString();
  }
  
  private String generateRFC1779DN(Map<String, String> paramMap) throws IllegalArgumentException {
    if (this.names.length == 1)
      return this.names[0].toRFC1779String(paramMap); 
    StringBuilder stringBuilder = new StringBuilder(48);
    if (this.names != null)
      for (int i = this.names.length - 1; i >= 0; i--) {
        if (i != this.names.length - 1)
          stringBuilder.append(", "); 
        stringBuilder.append(this.names[i].toRFC1779String(paramMap));
      }  
    return stringBuilder.toString();
  }
  
  static ObjectIdentifier intern(ObjectIdentifier paramObjectIdentifier) {
    ObjectIdentifier objectIdentifier = (ObjectIdentifier)internedOIDs.putIfAbsent(paramObjectIdentifier, paramObjectIdentifier);
    return (objectIdentifier == null) ? paramObjectIdentifier : objectIdentifier;
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 4) {
      b = -1;
    } else {
      X500Name x500Name = (X500Name)paramGeneralNameInterface;
      if (x500Name.equals(this)) {
        b = 0;
      } else if (x500Name.names.length == 0) {
        b = 2;
      } else if (this.names.length == 0) {
        b = 1;
      } else if (x500Name.isWithinSubtree(this)) {
        b = 1;
      } else if (isWithinSubtree(x500Name)) {
        b = 2;
      } else {
        b = 3;
      } 
    } 
    return b;
  }
  
  private boolean isWithinSubtree(X500Name paramX500Name) {
    if (this == paramX500Name)
      return true; 
    if (paramX500Name == null)
      return false; 
    if (paramX500Name.names.length == 0)
      return true; 
    if (this.names.length == 0)
      return false; 
    if (this.names.length < paramX500Name.names.length)
      return false; 
    for (byte b = 0; b < paramX500Name.names.length; b++) {
      if (!this.names[b].equals(paramX500Name.names[b]))
        return false; 
    } 
    return true;
  }
  
  public int subtreeDepth() { return this.names.length; }
  
  public X500Name commonAncestor(X500Name paramX500Name) {
    if (paramX500Name == null)
      return null; 
    int i = paramX500Name.names.length;
    int j = this.names.length;
    if (j == 0 || i == 0)
      return null; 
    int k = (j < i) ? j : i;
    byte b1;
    for (b1 = 0; b1 < k; b1++) {
      if (!this.names[b1].equals(paramX500Name.names[b1])) {
        if (!b1)
          return null; 
        break;
      } 
    } 
    RDN[] arrayOfRDN = new RDN[b1];
    for (byte b2 = 0; b2 < b1; b2++)
      arrayOfRDN[b2] = this.names[b2]; 
    X500Name x500Name = null;
    try {
      x500Name = new X500Name(arrayOfRDN);
    } catch (IOException iOException) {
      return null;
    } 
    return x500Name;
  }
  
  public X500Principal asX500Principal() {
    if (this.x500Principal == null)
      try {
        Object[] arrayOfObject = { this };
        this.x500Principal = (X500Principal)principalConstructor.newInstance(arrayOfObject);
      } catch (Exception exception) {
        throw new RuntimeException("Unexpected exception", exception);
      }  
    return this.x500Principal;
  }
  
  public static X500Name asX500Name(X500Principal paramX500Principal) {
    try {
      X500Name x500Name = (X500Name)principalField.get(paramX500Principal);
      x500Name.x500Principal = paramX500Principal;
      return x500Name;
    } catch (Exception exception) {
      throw new RuntimeException("Unexpected exception", exception);
    } 
  }
  
  static  {
    PrivilegedExceptionAction<Object[]> privilegedExceptionAction = new PrivilegedExceptionAction<Object[]>() {
        public Object[] run() throws Exception {
          Class clazz = X500Principal.class;
          Class[] arrayOfClass = { X500Name.class };
          Constructor constructor = clazz.getDeclaredConstructor(arrayOfClass);
          constructor.setAccessible(true);
          Field field = clazz.getDeclaredField("thisX500Name");
          field.setAccessible(true);
          return new Object[] { constructor, field };
        }
      };
    try {
      Object[] arrayOfObject = (Object[])AccessController.doPrivileged(privilegedExceptionAction);
      Constructor constructor = (Constructor)arrayOfObject[0];
      principalConstructor = constructor;
      principalField = (Field)arrayOfObject[1];
    } catch (Exception exception) {
      throw new InternalError("Could not obtain X500Principal access", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X500Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */