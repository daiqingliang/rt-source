package sun.security.provider.certpath.ldap;

import com.sun.jndi.ldap.LdapReferralException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.LDAPCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.provider.certpath.X509CertificatePair;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

public final class LDAPCertStore extends CertStoreSpi {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final boolean DEBUG = false;
  
  private static final String USER_CERT = "userCertificate;binary";
  
  private static final String CA_CERT = "cACertificate;binary";
  
  private static final String CROSS_CERT = "crossCertificatePair;binary";
  
  private static final String CRL = "certificateRevocationList;binary";
  
  private static final String ARL = "authorityRevocationList;binary";
  
  private static final String DELTA_CRL = "deltaRevocationList;binary";
  
  private static final String[] STRING0 = new String[0];
  
  private static final byte[][] BB0 = new byte[0][];
  
  private static final Attributes EMPTY_ATTRIBUTES = new BasicAttributes();
  
  private static final int DEFAULT_CACHE_SIZE = 750;
  
  private static final int DEFAULT_CACHE_LIFETIME = 30;
  
  private static final int LIFETIME;
  
  private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
  
  private static final String PROP_DISABLE_APP_RESOURCE_FILES = "sun.security.certpath.ldap.disable.app.resource.files";
  
  private CertificateFactory cf;
  
  private DirContext ctx;
  
  private boolean prefetchCRLs = false;
  
  private final Cache<String, byte[][]> valueCache;
  
  private int cacheHits = 0;
  
  private int cacheMisses = 0;
  
  private int requests = 0;
  
  private static final Cache<LDAPCertStoreParameters, CertStore> certStoreCache;
  
  public LDAPCertStore(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof LDAPCertStoreParameters))
      throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters"); 
    LDAPCertStoreParameters lDAPCertStoreParameters = (LDAPCertStoreParameters)paramCertStoreParameters;
    createInitialDirContext(lDAPCertStoreParameters.getServerName(), lDAPCertStoreParameters.getPort());
    try {
      this.cf = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
    } 
    if (LIFETIME == 0) {
      this.valueCache = Cache.newNullCache();
    } else if (LIFETIME < 0) {
      this.valueCache = Cache.newSoftMemoryCache(750);
    } else {
      this.valueCache = Cache.newSoftMemoryCache(750, LIFETIME);
    } 
  }
  
  static CertStore getInstance(LDAPCertStoreParameters paramLDAPCertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkConnect(paramLDAPCertStoreParameters.getServerName(), paramLDAPCertStoreParameters.getPort()); 
    CertStore certStore = (CertStore)certStoreCache.get(paramLDAPCertStoreParameters);
    if (certStore == null) {
      certStore = CertStore.getInstance("LDAP", paramLDAPCertStoreParameters);
      certStoreCache.put(paramLDAPCertStoreParameters, certStore);
    } else if (debug != null) {
      debug.println("LDAPCertStore.getInstance: cache hit");
    } 
    return certStore;
  }
  
  private void createInitialDirContext(String paramString, int paramInt) throws InvalidAlgorithmParameterException {
    String str = "ldap://" + paramString + ":" + paramInt;
    Hashtable hashtable = new Hashtable();
    hashtable.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    hashtable.put("java.naming.provider.url", str);
    boolean bool = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.certpath.ldap.disable.app.resource.files"))).booleanValue();
    if (bool) {
      if (debug != null)
        debug.println("LDAPCertStore disabling app resource files"); 
      hashtable.put("com.sun.naming.disable.app.resource.files", "true");
    } 
    try {
      this.ctx = new InitialDirContext(hashtable);
      Hashtable hashtable1 = this.ctx.getEnvironment();
      if (hashtable1.get("java.naming.referral") == null)
        this.ctx.addToEnvironment("java.naming.referral", "throw"); 
    } catch (NamingException namingException) {
      if (debug != null) {
        debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
        namingException.printStackTrace();
      } 
      InvalidAlgorithmParameterException invalidAlgorithmParameterException = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
      invalidAlgorithmParameterException.initCause(namingException);
      throw (InvalidAlgorithmParameterException)invalidAlgorithmParameterException;
    } 
  }
  
  private Collection<X509Certificate> getCertificates(LDAPRequest paramLDAPRequest, String paramString, X509CertSelector paramX509CertSelector) throws CertStoreException {
    byte[][] arrayOfByte;
    try {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    } catch (NamingException namingException) {
      throw new CertStoreException(namingException);
    } 
    int i = arrayOfByte.length;
    if (i == 0)
      return Collections.emptySet(); 
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte[b]);
      try {
        Certificate certificate = this.cf.generateCertificate(byteArrayInputStream);
        if (paramX509CertSelector.match(certificate))
          arrayList.add((X509Certificate)certificate); 
      } catch (CertificateException certificateException) {
        if (debug != null) {
          debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + hexDumpEncoder.encodeBuffer(arrayOfByte[b]) + " ]");
        } 
      } 
    } 
    return arrayList;
  }
  
  private Collection<X509CertificatePair> getCertPairs(LDAPRequest paramLDAPRequest, String paramString) throws CertStoreException {
    byte[][] arrayOfByte;
    try {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    } catch (NamingException namingException) {
      throw new CertStoreException(namingException);
    } 
    int i = arrayOfByte.length;
    if (i == 0)
      return Collections.emptySet(); 
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      try {
        X509CertificatePair x509CertificatePair = X509CertificatePair.generateCertificatePair(arrayOfByte[b]);
        arrayList.add(x509CertificatePair);
      } catch (CertificateException certificateException) {
        if (debug != null) {
          debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + hexDumpEncoder.encodeBuffer(arrayOfByte[b]) + " ]");
        } 
      } 
    } 
    return arrayList;
  }
  
  private Collection<X509Certificate> getMatchingCrossCerts(LDAPRequest paramLDAPRequest, X509CertSelector paramX509CertSelector1, X509CertSelector paramX509CertSelector2) throws CertStoreException {
    Collection collection = getCertPairs(paramLDAPRequest, "crossCertificatePair;binary");
    ArrayList arrayList = new ArrayList();
    for (X509CertificatePair x509CertificatePair : collection) {
      if (paramX509CertSelector1 != null) {
        X509Certificate x509Certificate = x509CertificatePair.getForward();
        if (x509Certificate != null && paramX509CertSelector1.match(x509Certificate))
          arrayList.add(x509Certificate); 
      } 
      if (paramX509CertSelector2 != null) {
        X509Certificate x509Certificate = x509CertificatePair.getReverse();
        if (x509Certificate != null && paramX509CertSelector2.match(x509Certificate))
          arrayList.add(x509Certificate); 
      } 
    } 
    return arrayList;
  }
  
  public Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    if (debug != null)
      debug.println("LDAPCertStore.engineGetCertificates() selector: " + String.valueOf(paramCertSelector)); 
    if (paramCertSelector == null)
      paramCertSelector = new X509CertSelector(); 
    if (!(paramCertSelector instanceof X509CertSelector))
      throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs"); 
    X509CertSelector x509CertSelector = (X509CertSelector)paramCertSelector;
    int i = x509CertSelector.getBasicConstraints();
    String str1 = x509CertSelector.getSubjectAsString();
    String str2 = x509CertSelector.getIssuerAsString();
    HashSet hashSet = new HashSet();
    if (debug != null)
      debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + i); 
    if (str1 != null) {
      if (debug != null)
        debug.println("LDAPCertStore.engineGetCertificates() subject is not null"); 
      LDAPRequest lDAPRequest = new LDAPRequest(str1);
      if (i > -2) {
        lDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
        lDAPRequest.addRequestedAttribute("cACertificate;binary");
        lDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
        if (this.prefetchCRLs)
          lDAPRequest.addRequestedAttribute("certificateRevocationList;binary"); 
      } 
      if (i < 0)
        lDAPRequest.addRequestedAttribute("userCertificate;binary"); 
      if (i > -2) {
        hashSet.addAll(getMatchingCrossCerts(lDAPRequest, x509CertSelector, null));
        if (debug != null)
          debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + hashSet.size()); 
        hashSet.addAll(getCertificates(lDAPRequest, "cACertificate;binary", x509CertSelector));
        if (debug != null)
          debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + hashSet.size()); 
      } 
      if (i < 0) {
        hashSet.addAll(getCertificates(lDAPRequest, "userCertificate;binary", x509CertSelector));
        if (debug != null)
          debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + hashSet.size()); 
      } 
    } else {
      if (debug != null)
        debug.println("LDAPCertStore.engineGetCertificates() subject is null"); 
      if (i == -2)
        throw new CertStoreException("need subject to find EE certs"); 
      if (str2 == null)
        throw new CertStoreException("need subject or issuer to find certs"); 
    } 
    if (debug != null)
      debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts..."); 
    if (str2 != null && i > -2) {
      LDAPRequest lDAPRequest = new LDAPRequest(str2);
      lDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
      lDAPRequest.addRequestedAttribute("cACertificate;binary");
      lDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
      if (this.prefetchCRLs)
        lDAPRequest.addRequestedAttribute("certificateRevocationList;binary"); 
      hashSet.addAll(getMatchingCrossCerts(lDAPRequest, null, x509CertSelector));
      if (debug != null)
        debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + hashSet.size()); 
      hashSet.addAll(getCertificates(lDAPRequest, "cACertificate;binary", x509CertSelector));
      if (debug != null)
        debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + hashSet.size()); 
    } 
    if (debug != null)
      debug.println("LDAPCertStore.engineGetCertificates() returning certs"); 
    return hashSet;
  }
  
  private Collection<X509CRL> getCRLs(LDAPRequest paramLDAPRequest, String paramString, X509CRLSelector paramX509CRLSelector) throws CertStoreException {
    byte[][] arrayOfByte;
    try {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    } catch (NamingException namingException) {
      throw new CertStoreException(namingException);
    } 
    int i = arrayOfByte.length;
    if (i == 0)
      return Collections.emptySet(); 
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      try {
        CRL cRL = this.cf.generateCRL(new ByteArrayInputStream(arrayOfByte[b]));
        if (paramX509CRLSelector.match(cRL))
          arrayList.add((X509CRL)cRL); 
      } catch (CRLException cRLException) {
        if (debug != null) {
          debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + hexDumpEncoder.encodeBuffer(arrayOfByte[b]) + " ]");
        } 
      } 
    } 
    return arrayList;
  }
  
  public Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    Collection collection;
    if (debug != null)
      debug.println("LDAPCertStore.engineGetCRLs() selector: " + paramCRLSelector); 
    if (paramCRLSelector == null)
      paramCRLSelector = new X509CRLSelector(); 
    if (!(paramCRLSelector instanceof X509CRLSelector))
      throw new CertStoreException("need X509CRLSelector to find CRLs"); 
    X509CRLSelector x509CRLSelector = (X509CRLSelector)paramCRLSelector;
    HashSet hashSet = new HashSet();
    X509Certificate x509Certificate = x509CRLSelector.getCertificateChecking();
    if (x509Certificate != null) {
      collection = new HashSet();
      X500Principal x500Principal = x509Certificate.getIssuerX500Principal();
      collection.add(x500Principal.getName("RFC2253"));
    } else {
      collection = x509CRLSelector.getIssuerNames();
      if (collection == null)
        throw new CertStoreException("need issuerNames or certChecking to find CRLs"); 
    } 
    for (Object object : collection) {
      String str;
      if (object instanceof byte[]) {
        try {
          X500Principal x500Principal = new X500Principal((byte[])object);
          str = x500Principal.getName("RFC2253");
        } catch (IllegalArgumentException illegalArgumentException) {
          continue;
        } 
      } else {
        str = (String)object;
      } 
      Collection collection1 = Collections.emptySet();
      if (x509Certificate == null || x509Certificate.getBasicConstraints() != -1) {
        LDAPRequest lDAPRequest = new LDAPRequest(str);
        lDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
        lDAPRequest.addRequestedAttribute("cACertificate;binary");
        lDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
        if (this.prefetchCRLs)
          lDAPRequest.addRequestedAttribute("certificateRevocationList;binary"); 
        try {
          collection1 = getCRLs(lDAPRequest, "authorityRevocationList;binary", x509CRLSelector);
          if (collection1.isEmpty()) {
            this.prefetchCRLs = true;
          } else {
            hashSet.addAll(collection1);
          } 
        } catch (CertStoreException certStoreException) {
          if (debug != null) {
            debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + certStoreException);
            certStoreException.printStackTrace();
          } 
        } 
      } 
      if (collection1.isEmpty() || x509Certificate == null) {
        LDAPRequest lDAPRequest = new LDAPRequest(str);
        lDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
        collection1 = getCRLs(lDAPRequest, "certificateRevocationList;binary", x509CRLSelector);
        hashSet.addAll(collection1);
      } 
    } 
    return hashSet;
  }
  
  static LDAPCertStoreParameters getParameters(URI paramURI) {
    String str = paramURI.getHost();
    if (str == null)
      return new SunLDAPCertStoreParameters(); 
    int i = paramURI.getPort();
    return (i == -1) ? new SunLDAPCertStoreParameters(str) : new SunLDAPCertStoreParameters(str, i);
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime"));
    if (str != null) {
      LIFETIME = Integer.parseInt(str);
    } else {
      LIFETIME = 30;
    } 
    certStoreCache = Cache.newSoftMemoryCache(185);
  }
  
  static class LDAPCRLSelector extends X509CRLSelector {
    private X509CRLSelector selector;
    
    private Collection<X500Principal> certIssuers;
    
    private Collection<X500Principal> issuers;
    
    private HashSet<Object> issuerNames;
    
    LDAPCRLSelector(X509CRLSelector param1X509CRLSelector, Collection<X500Principal> param1Collection, String param1String) throws IOException {
      this.selector = (param1X509CRLSelector == null) ? new X509CRLSelector() : param1X509CRLSelector;
      this.certIssuers = param1Collection;
      this.issuerNames = new HashSet();
      this.issuerNames.add(param1String);
      this.issuers = new HashSet();
      this.issuers.add((new X500Name(param1String)).asX500Principal());
    }
    
    public Collection<X500Principal> getIssuers() { return Collections.unmodifiableCollection(this.issuers); }
    
    public Collection<Object> getIssuerNames() { return Collections.unmodifiableCollection(this.issuerNames); }
    
    public BigInteger getMinCRL() { return this.selector.getMinCRL(); }
    
    public BigInteger getMaxCRL() { return this.selector.getMaxCRL(); }
    
    public Date getDateAndTime() { return this.selector.getDateAndTime(); }
    
    public X509Certificate getCertificateChecking() { return this.selector.getCertificateChecking(); }
    
    public boolean match(CRL param1CRL) {
      this.selector.setIssuers(this.certIssuers);
      boolean bool = this.selector.match(param1CRL);
      this.selector.setIssuers(this.issuers);
      return bool;
    }
  }
  
  static class LDAPCertSelector extends X509CertSelector {
    private X500Principal certSubject;
    
    private X509CertSelector selector;
    
    private X500Principal subject;
    
    LDAPCertSelector(X509CertSelector param1X509CertSelector, X500Principal param1X500Principal, String param1String) throws IOException {
      this.selector = (param1X509CertSelector == null) ? new X509CertSelector() : param1X509CertSelector;
      this.certSubject = param1X500Principal;
      this.subject = (new X500Name(param1String)).asX500Principal();
    }
    
    public X509Certificate getCertificate() { return this.selector.getCertificate(); }
    
    public BigInteger getSerialNumber() { return this.selector.getSerialNumber(); }
    
    public X500Principal getIssuer() { return this.selector.getIssuer(); }
    
    public String getIssuerAsString() { return this.selector.getIssuerAsString(); }
    
    public byte[] getIssuerAsBytes() throws IOException { return this.selector.getIssuerAsBytes(); }
    
    public X500Principal getSubject() { return this.subject; }
    
    public String getSubjectAsString() { return this.subject.getName(); }
    
    public byte[] getSubjectAsBytes() throws IOException { return this.subject.getEncoded(); }
    
    public byte[] getSubjectKeyIdentifier() throws IOException { return this.selector.getSubjectKeyIdentifier(); }
    
    public byte[] getAuthorityKeyIdentifier() throws IOException { return this.selector.getAuthorityKeyIdentifier(); }
    
    public Date getCertificateValid() { return this.selector.getCertificateValid(); }
    
    public Date getPrivateKeyValid() { return this.selector.getPrivateKeyValid(); }
    
    public String getSubjectPublicKeyAlgID() { return this.selector.getSubjectPublicKeyAlgID(); }
    
    public PublicKey getSubjectPublicKey() { return this.selector.getSubjectPublicKey(); }
    
    public boolean[] getKeyUsage() { return this.selector.getKeyUsage(); }
    
    public Set<String> getExtendedKeyUsage() { return this.selector.getExtendedKeyUsage(); }
    
    public boolean getMatchAllSubjectAltNames() { return this.selector.getMatchAllSubjectAltNames(); }
    
    public Collection<List<?>> getSubjectAlternativeNames() { return this.selector.getSubjectAlternativeNames(); }
    
    public byte[] getNameConstraints() throws IOException { return this.selector.getNameConstraints(); }
    
    public int getBasicConstraints() { return this.selector.getBasicConstraints(); }
    
    public Set<String> getPolicy() { return this.selector.getPolicy(); }
    
    public Collection<List<?>> getPathToNames() { return this.selector.getPathToNames(); }
    
    public boolean match(Certificate param1Certificate) {
      this.selector.setSubject(this.certSubject);
      boolean bool = this.selector.match(param1Certificate);
      this.selector.setSubject(this.subject);
      return bool;
    }
  }
  
  private class LDAPRequest {
    private final String name;
    
    private Map<String, byte[][]> valueMap;
    
    private final List<String> requestedAttributes;
    
    LDAPRequest(String param1String) throws CertStoreException {
      this.name = checkName(param1String);
      this.requestedAttributes = new ArrayList(5);
    }
    
    private String checkName(String param1String) throws CertStoreException {
      if (param1String == null)
        throw new CertStoreException("Name absent"); 
      try {
        if ((new CompositeName(param1String)).size() > 1)
          throw new CertStoreException("Invalid name: " + param1String); 
      } catch (InvalidNameException invalidNameException) {
        throw new CertStoreException("Invalid name: " + param1String, invalidNameException);
      } 
      return param1String;
    }
    
    String getName() { return this.name; }
    
    void addRequestedAttribute(String param1String) {
      if (this.valueMap != null)
        throw new IllegalStateException("Request already sent"); 
      this.requestedAttributes.add(param1String);
    }
    
    byte[][] getValues(String param1String) throws NamingException {
      String str = this.name + "|" + param1String;
      null = (byte[][])LDAPCertStore.this.valueCache.get(str);
      if (null != null) {
        LDAPCertStore.this.cacheHits++;
        return null;
      } 
      LDAPCertStore.this.cacheMisses++;
      Map map = getValueMap();
      return (byte[][])map.get(param1String);
    }
    
    private Map<String, byte[][]> getValueMap() throws NamingException {
      Attributes attributes;
      if (this.valueMap != null)
        return this.valueMap; 
      this.valueMap = new HashMap(8);
      String[] arrayOfString = (String[])this.requestedAttributes.toArray(STRING0);
      try {
        attributes = LDAPCertStore.this.ctx.getAttributes(this.name, arrayOfString);
      } catch (LdapReferralException ldapReferralException) {
        while (true) {
          try {
            String str1 = (String)ldapReferralException.getReferralInfo();
            URI uRI = new URI(str1);
            if (!uRI.getScheme().equalsIgnoreCase("ldap"))
              throw new IllegalArgumentException("Not LDAP"); 
            String str2 = uRI.getPath();
            if (str2 != null && str2.charAt(0) == '/')
              str2 = str2.substring(1); 
            checkName(str2);
          } catch (Exception exception) {
            throw new NamingException("Cannot follow referral to " + ldapReferralException.getReferralInfo());
          } 
          ldapContext = (LdapContext)ldapReferralException.getReferralContext();
          try {
            attributes = ldapContext.getAttributes(this.name, arrayOfString);
            ldapContext.close();
          } catch (LdapReferralException ldapReferralException1) {
            ldapReferralException = ldapReferralException1;
          } finally {
            ldapContext.close();
          } 
        } 
      } catch (NameNotFoundException nameNotFoundException) {
        attributes = EMPTY_ATTRIBUTES;
      } 
      for (String str : this.requestedAttributes) {
        Attribute attribute = attributes.get(str);
        byte[][] arrayOfByte = getAttributeValues(attribute);
        cacheAttribute(str, arrayOfByte);
        this.valueMap.put(str, arrayOfByte);
      } 
      return this.valueMap;
    }
    
    private void cacheAttribute(String param1String, byte[][] param1ArrayOfByte) {
      String str = this.name + "|" + param1String;
      LDAPCertStore.this.valueCache.put(str, param1ArrayOfByte);
    }
    
    private byte[][] getAttributeValues(Attribute param1Attribute) throws NamingException {
      byte[][] arrayOfByte;
      if (param1Attribute == null) {
        arrayOfByte = BB0;
      } else {
        arrayOfByte = new byte[param1Attribute.size()][];
        byte b = 0;
        NamingEnumeration namingEnumeration = param1Attribute.getAll();
        while (namingEnumeration.hasMore()) {
          Object object = namingEnumeration.next();
          if (debug != null && object instanceof String)
            debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + object); 
          byte[] arrayOfByte1 = (byte[])object;
          arrayOfByte[b++] = arrayOfByte1;
        } 
      } 
      return arrayOfByte;
    }
  }
  
  private static class SunLDAPCertStoreParameters extends LDAPCertStoreParameters {
    SunLDAPCertStoreParameters(String param1String, int param1Int) throws InvalidAlgorithmParameterException { super(param1String, param1Int); }
    
    SunLDAPCertStoreParameters(String param1String) { super(param1String); }
    
    SunLDAPCertStoreParameters() {}
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof LDAPCertStoreParameters))
        return false; 
      LDAPCertStoreParameters lDAPCertStoreParameters = (LDAPCertStoreParameters)param1Object;
      return (getPort() == lDAPCertStoreParameters.getPort() && getServerName().equalsIgnoreCase(lDAPCertStoreParameters.getServerName()));
    }
    
    public int hashCode() {
      if (this.hashCode == 0) {
        int i = 17;
        i = 37 * i + getPort();
        i = 37 * i + getServerName().toLowerCase(Locale.ENGLISH).hashCode();
        this.hashCode = i;
      } 
      return this.hashCode;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ldap\LDAPCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */