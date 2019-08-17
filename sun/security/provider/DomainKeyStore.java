package sun.security.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import sun.security.util.PolicyUtil;

abstract class DomainKeyStore extends KeyStoreSpi {
  private static final String ENTRY_NAME_SEPARATOR = "entrynameseparator";
  
  private static final String KEYSTORE_PROVIDER_NAME = "keystoreprovidername";
  
  private static final String KEYSTORE_TYPE = "keystoretype";
  
  private static final String KEYSTORE_URI = "keystoreuri";
  
  private static final String KEYSTORE_PASSWORD_ENV = "keystorepasswordenv";
  
  private static final String REGEX_META = ".$|()[{^?*+\\";
  
  private static final String DEFAULT_STREAM_PREFIX = "iostream";
  
  private int streamCounter = 1;
  
  private String entryNameSeparator = " ";
  
  private String entryNameSeparatorRegEx = " ";
  
  private static final String DEFAULT_KEYSTORE_TYPE = KeyStore.getDefaultType();
  
  private final Map<String, KeyStore> keystores = new HashMap();
  
  abstract String convertAlias(String paramString);
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    Key key = null;
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        key = keyStore.getKey(str, paramArrayOfChar);
        if (key != null)
          break; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return key;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    Certificate[] arrayOfCertificate = null;
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        arrayOfCertificate = keyStore.getCertificateChain(str);
        if (arrayOfCertificate != null)
          break; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return arrayOfCertificate;
  }
  
  public Certificate engineGetCertificate(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    Certificate certificate = null;
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        certificate = keyStore.getCertificate(str);
        if (certificate != null)
          break; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return certificate;
  }
  
  public Date engineGetCreationDate(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    Date date = null;
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        date = keyStore.getCreationDate(str);
        if (date != null)
          break; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return date;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    AbstractMap.SimpleEntry simpleEntry = getKeystoreForWriting(paramString);
    if (simpleEntry == null)
      throw new KeyStoreException("Error setting key entry for '" + paramString + "'"); 
    String str = (String)simpleEntry.getKey();
    Map.Entry entry = (Map.Entry)simpleEntry.getValue();
    ((KeyStore)entry.getValue()).setKeyEntry(str, paramKey, paramArrayOfChar, paramArrayOfCertificate);
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    AbstractMap.SimpleEntry simpleEntry = getKeystoreForWriting(paramString);
    if (simpleEntry == null)
      throw new KeyStoreException("Error setting protected key entry for '" + paramString + "'"); 
    String str = (String)simpleEntry.getKey();
    Map.Entry entry = (Map.Entry)simpleEntry.getValue();
    ((KeyStore)entry.getValue()).setKeyEntry(str, paramArrayOfByte, paramArrayOfCertificate);
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    AbstractMap.SimpleEntry simpleEntry = getKeystoreForWriting(paramString);
    if (simpleEntry == null)
      throw new KeyStoreException("Error setting certificate entry for '" + paramString + "'"); 
    String str = (String)simpleEntry.getKey();
    Map.Entry entry = (Map.Entry)simpleEntry.getValue();
    ((KeyStore)entry.getValue()).setCertificateEntry(str, paramCertificate);
  }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException {
    AbstractMap.SimpleEntry simpleEntry = getKeystoreForWriting(paramString);
    if (simpleEntry == null)
      throw new KeyStoreException("Error deleting entry for '" + paramString + "'"); 
    String str = (String)simpleEntry.getKey();
    Map.Entry entry = (Map.Entry)simpleEntry.getValue();
    ((KeyStore)entry.getValue()).deleteEntry(str);
  }
  
  public Enumeration<String> engineAliases() {
    final Iterator iterator = this.keystores.entrySet().iterator();
    return new Enumeration<String>() {
        private int index = 0;
        
        private Map.Entry<String, KeyStore> keystoresEntry = null;
        
        private String prefix = null;
        
        private Enumeration<String> aliases = null;
        
        public boolean hasMoreElements() {
          try {
            if (this.aliases == null)
              if (iterator.hasNext()) {
                this.keystoresEntry = (Map.Entry)iterator.next();
                this.prefix = (String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
                this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
              } else {
                return false;
              }  
            if (this.aliases.hasMoreElements())
              return true; 
            if (iterator.hasNext()) {
              this.keystoresEntry = (Map.Entry)iterator.next();
              this.prefix = (String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
              this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
            } else {
              return false;
            } 
          } catch (KeyStoreException keyStoreException) {
            return false;
          } 
          return this.aliases.hasMoreElements();
        }
        
        public String nextElement() {
          if (hasMoreElements())
            return this.prefix + (String)this.aliases.nextElement(); 
          throw new NoSuchElementException();
        }
      };
  }
  
  public boolean engineContainsAlias(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        if (keyStore.containsAlias(str))
          return true; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return false;
  }
  
  public int engineSize() {
    int i = 0;
    try {
      for (KeyStore keyStore : this.keystores.values())
        i += keyStore.size(); 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return i;
  }
  
  public boolean engineIsKeyEntry(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        if (keyStore.isKeyEntry(str))
          return true; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return false;
  }
  
  public boolean engineIsCertificateEntry(String paramString) {
    AbstractMap.SimpleEntry simpleEntry = getKeystoresForReading(paramString);
    try {
      String str = (String)simpleEntry.getKey();
      for (KeyStore keyStore : (Collection)simpleEntry.getValue()) {
        if (keyStore.isCertificateEntry(str))
          return true; 
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    return false;
  }
  
  private AbstractMap.SimpleEntry<String, Collection<KeyStore>> getKeystoresForReading(String paramString) {
    String[] arrayOfString = paramString.split(this.entryNameSeparatorRegEx, 2);
    if (arrayOfString.length == 2) {
      KeyStore keyStore = (KeyStore)this.keystores.get(arrayOfString[0]);
      if (keyStore != null)
        return new AbstractMap.SimpleEntry(arrayOfString[1], Collections.singleton(keyStore)); 
    } else if (arrayOfString.length == 1) {
      return new AbstractMap.SimpleEntry(paramString, this.keystores.values());
    } 
    return new AbstractMap.SimpleEntry("", Collections.emptyList());
  }
  
  private AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> getKeystoreForWriting(String paramString) {
    String[] arrayOfString = paramString.split(this.entryNameSeparator, 2);
    if (arrayOfString.length == 2) {
      KeyStore keyStore = (KeyStore)this.keystores.get(arrayOfString[0]);
      if (keyStore != null)
        return new AbstractMap.SimpleEntry(arrayOfString[1], new AbstractMap.SimpleEntry(arrayOfString[0], keyStore)); 
    } 
    return null;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) {
    try {
      KeyStore keyStore;
      String str = null;
      Iterator iterator = this.keystores.values().iterator();
      do {
        keyStore = (KeyStore)iterator.next();
      } while (iterator.hasNext() && (str = keyStore.getCertificateAlias(paramCertificate)) == null);
      return str;
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    try {
      if (this.keystores.size() == 1) {
        ((KeyStore)this.keystores.values().iterator().next()).store(paramOutputStream, paramArrayOfChar);
        return;
      } 
    } catch (KeyStoreException keyStoreException) {
      throw new IllegalStateException(keyStoreException);
    } 
    throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
  }
  
  public void engineStore(KeyStore.LoadStoreParameter paramLoadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (paramLoadStoreParameter instanceof DomainLoadStoreParameter) {
      DomainLoadStoreParameter domainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
      List list = getBuilders(domainLoadStoreParameter.getConfiguration(), domainLoadStoreParameter.getProtectionParams());
      for (KeyStoreBuilderComponents keyStoreBuilderComponents : list) {
        try {
          KeyStore.ProtectionParameter protectionParameter = keyStoreBuilderComponents.protection;
          if (!(protectionParameter instanceof KeyStore.PasswordProtection))
            throw new KeyStoreException(new IllegalArgumentException("ProtectionParameter must be a KeyStore.PasswordProtection")); 
          char[] arrayOfChar = ((KeyStore.PasswordProtection)keyStoreBuilderComponents.protection).getPassword();
          KeyStore keyStore = (KeyStore)this.keystores.get(keyStoreBuilderComponents.name);
          try (FileOutputStream null = new FileOutputStream(keyStoreBuilderComponents.file)) {
            keyStore.store(fileOutputStream, arrayOfChar);
          } 
        } catch (KeyStoreException keyStoreException) {
          throw new IOException(keyStoreException);
        } 
      } 
    } else {
      throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
    } 
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    try {
      KeyStore keyStore = null;
      try {
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(paramInputStream, paramArrayOfChar);
      } catch (Exception exception) {
        if (!"JKS".equalsIgnoreCase(DEFAULT_KEYSTORE_TYPE)) {
          keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
          keyStore.load(paramInputStream, paramArrayOfChar);
        } else {
          throw exception;
        } 
      } 
      String str = "iostream" + this.streamCounter++;
      this.keystores.put(str, keyStore);
    } catch (Exception exception) {
      throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
    } 
  }
  
  public void engineLoad(KeyStore.LoadStoreParameter paramLoadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (paramLoadStoreParameter instanceof DomainLoadStoreParameter) {
      DomainLoadStoreParameter domainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
      List list = getBuilders(domainLoadStoreParameter.getConfiguration(), domainLoadStoreParameter.getProtectionParams());
      for (KeyStoreBuilderComponents keyStoreBuilderComponents : list) {
        try {
          if (keyStoreBuilderComponents.file != null) {
            this.keystores.put(keyStoreBuilderComponents.name, KeyStore.Builder.newInstance(keyStoreBuilderComponents.type, keyStoreBuilderComponents.provider, keyStoreBuilderComponents.file, keyStoreBuilderComponents.protection).getKeyStore());
            continue;
          } 
          this.keystores.put(keyStoreBuilderComponents.name, KeyStore.Builder.newInstance(keyStoreBuilderComponents.type, keyStoreBuilderComponents.provider, keyStoreBuilderComponents.protection).getKeyStore());
        } catch (KeyStoreException keyStoreException) {
          throw new IOException(keyStoreException);
        } 
      } 
    } else {
      throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
    } 
  }
  
  private List<KeyStoreBuilderComponents> getBuilders(URI paramURI, Map<String, KeyStore.ProtectionParameter> paramMap) throws IOException {
    PolicyParser policyParser = new PolicyParser(true);
    Collection collection = null;
    ArrayList arrayList = new ArrayList();
    String str = paramURI.getFragment();
    try (InputStreamReader null = new InputStreamReader(PolicyUtil.getInputStream(paramURI.toURL()), "UTF-8")) {
      policyParser.read(inputStreamReader);
      collection = policyParser.getDomainEntries();
    } catch (MalformedURLException malformedURLException) {
      throw new IOException(malformedURLException);
    } catch (ParsingException parsingException) {
      throw new IOException(parsingException);
    } 
    for (PolicyParser.DomainEntry domainEntry : collection) {
      Map map = domainEntry.getProperties();
      if (str != null && !str.equalsIgnoreCase(domainEntry.getName()))
        continue; 
      if (map.containsKey("entrynameseparator")) {
        this.entryNameSeparator = (String)map.get("entrynameseparator");
        char c = Character.MIN_VALUE;
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b = 0; b < this.entryNameSeparator.length(); b++) {
          c = this.entryNameSeparator.charAt(b);
          if (".$|()[{^?*+\\".indexOf(c) != -1)
            stringBuilder.append('\\'); 
          stringBuilder.append(c);
        } 
        this.entryNameSeparatorRegEx = stringBuilder.toString();
      } 
      Collection collection1 = domainEntry.getEntries();
      for (PolicyParser.KeyStoreEntry keyStoreEntry : collection1) {
        String str1 = keyStoreEntry.getName();
        HashMap hashMap = new HashMap(map);
        hashMap.putAll(keyStoreEntry.getProperties());
        String str2 = DEFAULT_KEYSTORE_TYPE;
        if (hashMap.containsKey("keystoretype"))
          str2 = (String)hashMap.get("keystoretype"); 
        Provider provider = null;
        if (hashMap.containsKey("keystoreprovidername")) {
          String str3 = (String)hashMap.get("keystoreprovidername");
          provider = Security.getProvider(str3);
          if (provider == null)
            throw new IOException("Error locating JCE provider: " + str3); 
        } 
        File file = null;
        if (hashMap.containsKey("keystoreuri")) {
          String str3 = (String)hashMap.get("keystoreuri");
          try {
            if (str3.startsWith("file://")) {
              file = new File(new URI(str3));
            } else {
              file = new File(str3);
            } 
          } catch (URISyntaxException|IllegalArgumentException uRISyntaxException) {
            throw new IOException("Error processing keystore property: keystoreURI=\"" + str3 + "\"", uRISyntaxException);
          } 
        } 
        KeyStore.ProtectionParameter protectionParameter = null;
        if (paramMap.containsKey(str1)) {
          protectionParameter = (KeyStore.ProtectionParameter)paramMap.get(str1);
        } else if (hashMap.containsKey("keystorepasswordenv")) {
          String str3 = (String)hashMap.get("keystorepasswordenv");
          String str4 = System.getenv(str3);
          if (str4 != null) {
            protectionParameter = new KeyStore.PasswordProtection(str4.toCharArray());
          } else {
            throw new IOException("Error processing keystore property: keystorePasswordEnv=\"" + str3 + "\"");
          } 
        } else {
          protectionParameter = new KeyStore.PasswordProtection(null);
        } 
        arrayList.add(new KeyStoreBuilderComponents(str1, str2, provider, file, protectionParameter));
      } 
    } 
    if (arrayList.isEmpty())
      throw new IOException("Error locating domain configuration data for: " + paramURI); 
    return arrayList;
  }
  
  public static final class DKS extends DomainKeyStore {
    String convertAlias(String param1String) { return param1String.toLowerCase(Locale.ENGLISH); }
  }
  
  class KeyStoreBuilderComponents {
    String name;
    
    String type;
    
    Provider provider;
    
    File file;
    
    KeyStore.ProtectionParameter protection;
    
    KeyStoreBuilderComponents(String param1String1, String param1String2, Provider param1Provider, File param1File, KeyStore.ProtectionParameter param1ProtectionParameter) {
      this.name = param1String1;
      this.type = param1String2;
      this.provider = param1Provider;
      this.file = param1File;
      this.protection = param1ProtectionParameter;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DomainKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */