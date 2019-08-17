package sun.security.tools.keytool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSigner;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.Timestamp;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs10.PKCS10;
import sun.security.pkcs10.PKCS10Attribute;
import sun.security.provider.certpath.CertStoreHelper;
import sun.security.tools.KeyStoreUtil;
import sun.security.tools.PathList;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Password;
import sun.security.util.Pem;
import sun.security.util.SecurityProviderConstants;
import sun.security.x509.AccessDescription;
import sun.security.x509.AlgorithmId;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.CRLExtensions;
import sun.security.x509.CRLReasonCodeExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.DNSName;
import sun.security.x509.DistributionPoint;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.Extension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNames;
import sun.security.x509.IPAddressName;
import sun.security.x509.IssuerAlternativeNameExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.OIDName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RFC822Name;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.SubjectInfoAccessExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public final class Main {
  private static final byte[] CRLF = { 13, 10 };
  
  private boolean debug = false;
  
  private Command command = null;
  
  private String sigAlgName = null;
  
  private String keyAlgName = null;
  
  private boolean verbose = false;
  
  private int keysize = -1;
  
  private boolean rfc = false;
  
  private long validity = 90L;
  
  private String alias = null;
  
  private String dname = null;
  
  private String dest = null;
  
  private String filename = null;
  
  private String infilename = null;
  
  private String outfilename = null;
  
  private String srcksfname = null;
  
  private Set<Pair<String, String>> providers = null;
  
  private String storetype = null;
  
  private String srcProviderName = null;
  
  private String providerName = null;
  
  private String pathlist = null;
  
  private char[] storePass = null;
  
  private char[] storePassNew = null;
  
  private char[] keyPass = null;
  
  private char[] keyPassNew = null;
  
  private char[] newPass = null;
  
  private char[] destKeyPass = null;
  
  private char[] srckeyPass = null;
  
  private String ksfname = null;
  
  private File ksfile = null;
  
  private InputStream ksStream = null;
  
  private String sslserver = null;
  
  private String jarfile = null;
  
  private KeyStore keyStore = null;
  
  private boolean token = false;
  
  private boolean nullStream = false;
  
  private boolean kssave = false;
  
  private boolean noprompt = false;
  
  private boolean trustcacerts = false;
  
  private boolean nowarn = false;
  
  private boolean protectedPath = false;
  
  private boolean srcprotectedPath = false;
  
  private CertificateFactory cf = null;
  
  private KeyStore caks = null;
  
  private char[] srcstorePass = null;
  
  private String srcstoretype = null;
  
  private Set<char[]> passwords = new HashSet();
  
  private String startDate = null;
  
  private List<String> ids = new ArrayList();
  
  private List<String> v3ext = new ArrayList();
  
  private boolean inplaceImport = false;
  
  private String inplaceBackupName = null;
  
  private List<String> weakWarnings = new ArrayList();
  
  private static final DisabledAlgorithmConstraints DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
  
  private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  
  private static final Class<?>[] PARAM_STRING = { String.class };
  
  private static final String NONE = "NONE";
  
  private static final String P11KEYSTORE = "PKCS11";
  
  private static final String P12KEYSTORE = "PKCS12";
  
  private static final String keyAlias = "mykey";
  
  private static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.keytool.Resources");
  
  private static final Collator collator = Collator.getInstance();
  
  private static final String[] extSupported;
  
  public static void main(String[] paramArrayOfString) throws Exception {
    Main main = new Main();
    main.run(paramArrayOfString, System.out);
  }
  
  private void run(String[] paramArrayOfString, PrintStream paramPrintStream) throws Exception {
    try {
      parseArgs(paramArrayOfString);
      if (this.command != null)
        doCommands(paramPrintStream); 
    } catch (Exception exception) {
      System.out.println(rb.getString("keytool.error.") + exception);
      if (this.verbose)
        exception.printStackTrace(System.out); 
      if (!this.debug) {
        System.exit(1);
      } else {
        throw exception;
      } 
    } finally {
      printWeakWarnings(false);
      for (char[] arrayOfChar : this.passwords) {
        if (arrayOfChar != null) {
          Arrays.fill(arrayOfChar, ' ');
          arrayOfChar = null;
        } 
      } 
      if (this.ksStream != null)
        this.ksStream.close(); 
    } 
  }
  
  void parseArgs(String[] paramArrayOfString) throws Exception {
    byte b = 0;
    boolean bool = (paramArrayOfString.length == 0) ? 1 : 0;
    for (b = 0; b < paramArrayOfString.length && paramArrayOfString[b].startsWith("-"); b++) {
      String str1 = paramArrayOfString[b];
      if (b == paramArrayOfString.length - 1)
        for (Option option : Option.values()) {
          if (collator.compare(str1, option.toString()) == 0) {
            if (option.arg != null)
              errorNeedArgument(str1); 
            break;
          } 
        }  
      String str2 = null;
      int i = str1.indexOf(':');
      if (i > 0) {
        str2 = str1.substring(i + 1);
        str1 = str1.substring(0, i);
      } 
      boolean bool1 = false;
      for (Command command1 : Command.values()) {
        if (collator.compare(str1, command1.toString()) == 0) {
          this.command = command1;
          bool1 = true;
          break;
        } 
      } 
      if (!bool1)
        if (collator.compare(str1, "-export") == 0) {
          this.command = Command.EXPORTCERT;
        } else if (collator.compare(str1, "-genkey") == 0) {
          this.command = Command.GENKEYPAIR;
        } else if (collator.compare(str1, "-import") == 0) {
          this.command = Command.IMPORTCERT;
        } else if (collator.compare(str1, "-importpassword") == 0) {
          this.command = Command.IMPORTPASS;
        } else if (collator.compare(str1, "-help") == 0) {
          bool = true;
        } else if (collator.compare(str1, "-nowarn") == 0) {
          this.nowarn = true;
        } else if (collator.compare(str1, "-keystore") == 0 || collator.compare(str1, "-destkeystore") == 0) {
          this.ksfname = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-storepass") == 0 || collator.compare(str1, "-deststorepass") == 0) {
          this.storePass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.storePass);
        } else if (collator.compare(str1, "-storetype") == 0 || collator.compare(str1, "-deststoretype") == 0) {
          this.storetype = KeyStoreUtil.niceStoreTypeName(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-srcstorepass") == 0) {
          this.srcstorePass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.srcstorePass);
        } else if (collator.compare(str1, "-srcstoretype") == 0) {
          this.srcstoretype = KeyStoreUtil.niceStoreTypeName(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-srckeypass") == 0) {
          this.srckeyPass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.srckeyPass);
        } else if (collator.compare(str1, "-srcprovidername") == 0) {
          this.srcProviderName = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-providername") == 0 || collator.compare(str1, "-destprovidername") == 0) {
          this.providerName = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-providerpath") == 0) {
          this.pathlist = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-keypass") == 0) {
          this.keyPass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.keyPass);
        } else if (collator.compare(str1, "-new") == 0) {
          this.newPass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.newPass);
        } else if (collator.compare(str1, "-destkeypass") == 0) {
          this.destKeyPass = getPass(str2, paramArrayOfString[++b]);
          this.passwords.add(this.destKeyPass);
        } else if (collator.compare(str1, "-alias") == 0 || collator.compare(str1, "-srcalias") == 0) {
          this.alias = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-dest") == 0 || collator.compare(str1, "-destalias") == 0) {
          this.dest = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-dname") == 0) {
          this.dname = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-keysize") == 0) {
          this.keysize = Integer.parseInt(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-keyalg") == 0) {
          this.keyAlgName = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-sigalg") == 0) {
          this.sigAlgName = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-startdate") == 0) {
          this.startDate = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-validity") == 0) {
          this.validity = Long.parseLong(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-ext") == 0) {
          this.v3ext.add(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-id") == 0) {
          this.ids.add(paramArrayOfString[++b]);
        } else if (collator.compare(str1, "-file") == 0) {
          this.filename = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-infile") == 0) {
          this.infilename = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-outfile") == 0) {
          this.outfilename = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-sslserver") == 0) {
          this.sslserver = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-jarfile") == 0) {
          this.jarfile = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-srckeystore") == 0) {
          this.srcksfname = paramArrayOfString[++b];
        } else if (collator.compare(str1, "-provider") == 0 || collator.compare(str1, "-providerclass") == 0) {
          if (this.providers == null)
            this.providers = new HashSet(3); 
          String str3 = paramArrayOfString[++b];
          String str4 = null;
          if (paramArrayOfString.length > b + 1) {
            str1 = paramArrayOfString[b + 1];
            if (collator.compare(str1, "-providerarg") == 0) {
              if (paramArrayOfString.length == b + 2)
                errorNeedArgument(str1); 
              str4 = paramArrayOfString[b + 2];
              b += 2;
            } 
          } 
          this.providers.add(Pair.of(str3, str4));
        } else if (collator.compare(str1, "-v") == 0) {
          this.verbose = true;
        } else if (collator.compare(str1, "-debug") == 0) {
          this.debug = true;
        } else if (collator.compare(str1, "-rfc") == 0) {
          this.rfc = true;
        } else if (collator.compare(str1, "-noprompt") == 0) {
          this.noprompt = true;
        } else if (collator.compare(str1, "-trustcacerts") == 0) {
          this.trustcacerts = true;
        } else if (collator.compare(str1, "-protected") == 0 || collator.compare(str1, "-destprotected") == 0) {
          this.protectedPath = true;
        } else if (collator.compare(str1, "-srcprotected") == 0) {
          this.srcprotectedPath = true;
        } else {
          System.err.println(rb.getString("Illegal.option.") + str1);
          tinyHelp();
        }  
    } 
    if (b < paramArrayOfString.length) {
      System.err.println(rb.getString("Illegal.option.") + paramArrayOfString[b]);
      tinyHelp();
    } 
    if (this.command == null) {
      if (bool) {
        usage();
      } else {
        System.err.println(rb.getString("Usage.error.no.command.provided"));
        tinyHelp();
      } 
    } else if (bool) {
      usage();
      this.command = null;
    } 
  }
  
  boolean isKeyStoreRelated(Command paramCommand) { return (paramCommand != Command.PRINTCERT && paramCommand != Command.PRINTCERTREQ); }
  
  void doCommands(PrintStream paramPrintStream) throws Exception {
    if ("PKCS11".equalsIgnoreCase(this.storetype) || KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
      this.token = true;
      if (this.ksfname == null)
        this.ksfname = "NONE"; 
    } 
    if ("NONE".equals(this.ksfname))
      this.nullStream = true; 
    if (this.token && !this.nullStream) {
      System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.storetype }));
      System.err.println();
      tinyHelp();
    } 
    if (this.token && (this.command == Command.KEYPASSWD || this.command == Command.STOREPASSWD))
      throw new UnsupportedOperationException(MessageFormat.format(rb.getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), new Object[] { this.storetype })); 
    if (this.token && (this.keyPass != null || this.newPass != null || this.destKeyPass != null))
      throw new IllegalArgumentException(MessageFormat.format(rb.getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), new Object[] { this.storetype })); 
    if (this.protectedPath && (this.storePass != null || this.keyPass != null || this.newPass != null || this.destKeyPass != null))
      throw new IllegalArgumentException(rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified")); 
    if (this.srcprotectedPath && (this.srcstorePass != null || this.srckeyPass != null))
      throw new IllegalArgumentException(rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified")); 
    if (KeyStoreUtil.isWindowsKeyStore(this.storetype) && (this.storePass != null || this.keyPass != null || this.newPass != null || this.destKeyPass != null))
      throw new IllegalArgumentException(rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified")); 
    if (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype) && (this.srcstorePass != null || this.srckeyPass != null))
      throw new IllegalArgumentException(rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified")); 
    if (this.validity <= 0L)
      throw new Exception(rb.getString("Validity.must.be.greater.than.zero")); 
    if (this.providers != null) {
      ClassLoader classLoader = null;
      if (this.pathlist != null) {
        String str = null;
        str = PathList.appendPath(str, System.getProperty("java.class.path"));
        str = PathList.appendPath(str, System.getProperty("env.class.path"));
        str = PathList.appendPath(str, this.pathlist);
        URL[] arrayOfURL = PathList.pathToURLs(str);
        classLoader = new URLClassLoader(arrayOfURL);
      } else {
        classLoader = ClassLoader.getSystemClassLoader();
      } 
      for (Pair pair : this.providers) {
        Object object;
        Class clazz;
        String str1 = (String)pair.fst;
        if (classLoader != null) {
          clazz = classLoader.loadClass(str1);
        } else {
          clazz = Class.forName(str1);
        } 
        String str2 = (String)pair.snd;
        if (str2 == null) {
          object = clazz.newInstance();
        } else {
          Constructor constructor = clazz.getConstructor(PARAM_STRING);
          object = constructor.newInstance(new Object[] { str2 });
        } 
        if (!(object instanceof Provider)) {
          MessageFormat messageFormat = new MessageFormat(rb.getString("provName.not.a.provider"));
          Object[] arrayOfObject = { str1 };
          throw new Exception(messageFormat.format(arrayOfObject));
        } 
        Security.addProvider((Provider)object);
      } 
    } 
    if (this.command == Command.LIST && this.verbose && this.rfc) {
      System.err.println(rb.getString("Must.not.specify.both.v.and.rfc.with.list.command"));
      tinyHelp();
    } 
    if (this.command == Command.GENKEYPAIR && this.keyPass != null && this.keyPass.length < 6)
      throw new Exception(rb.getString("Key.password.must.be.at.least.6.characters")); 
    if (this.newPass != null && this.newPass.length < 6)
      throw new Exception(rb.getString("New.password.must.be.at.least.6.characters")); 
    if (this.destKeyPass != null && this.destKeyPass.length < 6)
      throw new Exception(rb.getString("New.password.must.be.at.least.6.characters")); 
    if (this.ksfname == null)
      this.ksfname = System.getProperty("user.home") + File.separator + ".keystore"; 
    KeyStore keyStore1 = null;
    if (this.command == Command.IMPORTKEYSTORE) {
      this.inplaceImport = inplaceImportCheck();
      if (this.inplaceImport) {
        keyStore1 = loadSourceKeyStore();
        if (this.storePass == null)
          this.storePass = this.srcstorePass; 
      } 
    } 
    if (isKeyStoreRelated(this.command) && !this.nullStream && !this.inplaceImport)
      try {
        this.ksfile = new File(this.ksfname);
        if (this.ksfile.exists() && this.ksfile.length() == 0L)
          throw new Exception(rb.getString("Keystore.file.exists.but.is.empty.") + this.ksfname); 
        this.ksStream = new FileInputStream(this.ksfile);
      } catch (FileNotFoundException fileNotFoundException) {
        if (this.command != Command.GENKEYPAIR && this.command != Command.GENSECKEY && this.command != Command.IDENTITYDB && this.command != Command.IMPORTCERT && this.command != Command.IMPORTPASS && this.command != Command.IMPORTKEYSTORE && this.command != Command.PRINTCRL)
          throw new Exception(rb.getString("Keystore.file.does.not.exist.") + this.ksfname); 
      }  
    if ((this.command == Command.KEYCLONE || this.command == Command.CHANGEALIAS) && this.dest == null) {
      this.dest = getAlias("destination");
      if ("".equals(this.dest))
        throw new Exception(rb.getString("Must.specify.destination.alias")); 
    } 
    if (this.command == Command.DELETE && this.alias == null) {
      this.alias = getAlias(null);
      if ("".equals(this.alias))
        throw new Exception(rb.getString("Must.specify.alias")); 
    } 
    if (this.storetype == null)
      this.storetype = KeyStore.getDefaultType(); 
    if (this.providerName == null) {
      this.keyStore = KeyStore.getInstance(this.storetype);
    } else {
      this.keyStore = KeyStore.getInstance(this.storetype, this.providerName);
    } 
    if (!this.nullStream) {
      if (this.inplaceImport) {
        this.keyStore.load(null, this.storePass);
      } else {
        this.keyStore.load(this.ksStream, this.storePass);
      } 
      if (this.ksStream != null)
        this.ksStream.close(); 
    } 
    if ("PKCS12".equalsIgnoreCase(this.storetype) && this.command == Command.KEYPASSWD)
      throw new UnsupportedOperationException(rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12")); 
    if (this.nullStream && this.storePass != null) {
      this.keyStore.load(null, this.storePass);
    } else if (!this.nullStream && this.storePass != null) {
      if (this.ksStream == null && this.storePass.length < 6)
        throw new Exception(rb.getString("Keystore.password.must.be.at.least.6.characters")); 
    } else if (this.storePass == null) {
      if (!this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype) && (this.command == Command.CERTREQ || this.command == Command.DELETE || this.command == Command.GENKEYPAIR || this.command == Command.GENSECKEY || this.command == Command.IMPORTCERT || this.command == Command.IMPORTPASS || this.command == Command.IMPORTKEYSTORE || this.command == Command.KEYCLONE || this.command == Command.CHANGEALIAS || this.command == Command.SELFCERT || this.command == Command.STOREPASSWD || this.command == Command.KEYPASSWD || this.command == Command.IDENTITYDB)) {
        byte b = 0;
        do {
          if (this.command == Command.IMPORTKEYSTORE) {
            System.err.print(rb.getString("Enter.destination.keystore.password."));
          } else {
            System.err.print(rb.getString("Enter.keystore.password."));
          } 
          System.err.flush();
          this.storePass = Password.readPassword(System.in);
          this.passwords.add(this.storePass);
          if (!this.nullStream && (this.storePass == null || this.storePass.length < 6)) {
            System.err.println(rb.getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
            this.storePass = null;
          } 
          if (this.storePass != null && !this.nullStream && this.ksStream == null) {
            System.err.print(rb.getString("Re.enter.new.password."));
            char[] arrayOfChar = Password.readPassword(System.in);
            this.passwords.add(arrayOfChar);
            if (!Arrays.equals(this.storePass, arrayOfChar)) {
              System.err.println(rb.getString("They.don.t.match.Try.again"));
              this.storePass = null;
            } 
          } 
          b++;
        } while (this.storePass == null && b < 3);
        if (this.storePass == null) {
          System.err.println(rb.getString("Too.many.failures.try.later"));
          return;
        } 
      } else if (!this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype) && isKeyStoreRelated(this.command) && this.command != Command.PRINTCRL) {
        System.err.print(rb.getString("Enter.keystore.password."));
        System.err.flush();
        this.storePass = Password.readPassword(System.in);
        this.passwords.add(this.storePass);
      } 
      if (this.nullStream) {
        this.keyStore.load(null, this.storePass);
      } else if (this.ksStream != null) {
        this.ksStream = new FileInputStream(this.ksfile);
        this.keyStore.load(this.ksStream, this.storePass);
        this.ksStream.close();
      } 
    } 
    if (this.storePass != null && "PKCS12".equalsIgnoreCase(this.storetype)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
      if (this.keyPass != null && !Arrays.equals(this.storePass, this.keyPass)) {
        Object[] arrayOfObject = { "-keypass" };
        System.err.println(messageFormat.format(arrayOfObject));
        this.keyPass = this.storePass;
      } 
      if (this.newPass != null && !Arrays.equals(this.storePass, this.newPass)) {
        Object[] arrayOfObject = { "-new" };
        System.err.println(messageFormat.format(arrayOfObject));
        this.newPass = this.storePass;
      } 
      if (this.destKeyPass != null && !Arrays.equals(this.storePass, this.destKeyPass)) {
        Object[] arrayOfObject = { "-destkeypass" };
        System.err.println(messageFormat.format(arrayOfObject));
        this.destKeyPass = this.storePass;
      } 
    } 
    if (this.command == Command.PRINTCERT || this.command == Command.IMPORTCERT || this.command == Command.IDENTITYDB || this.command == Command.PRINTCRL)
      this.cf = CertificateFactory.getInstance("X509"); 
    if (this.command != Command.IMPORTCERT)
      this.trustcacerts = false; 
    if (this.trustcacerts)
      this.caks = KeyStoreUtil.getCacertsKeyStore(); 
    if (this.command == Command.CERTREQ) {
      if (this.filename != null) {
        try (PrintStream null = new PrintStream(new FileOutputStream(this.filename))) {
          doCertReq(this.alias, this.sigAlgName, printStream);
        } 
      } else {
        doCertReq(this.alias, this.sigAlgName, paramPrintStream);
      } 
      if (this.verbose && this.filename != null) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Certification.request.stored.in.file.filename."));
        Object[] arrayOfObject = { this.filename };
        System.err.println(messageFormat.format(arrayOfObject));
        System.err.println(rb.getString("Submit.this.to.your.CA"));
      } 
    } else if (this.command == Command.DELETE) {
      doDeleteEntry(this.alias);
      this.kssave = true;
    } else if (this.command == Command.EXPORTCERT) {
      if (this.filename != null) {
        try (PrintStream null = new PrintStream(new FileOutputStream(this.filename))) {
          doExportCert(this.alias, printStream);
        } 
      } else {
        doExportCert(this.alias, paramPrintStream);
      } 
      if (this.filename != null) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.stored.in.file.filename."));
        Object[] arrayOfObject = { this.filename };
        System.err.println(messageFormat.format(arrayOfObject));
      } 
    } else if (this.command == Command.GENKEYPAIR) {
      if (this.keyAlgName == null)
        this.keyAlgName = "DSA"; 
      doGenKeyPair(this.alias, this.dname, this.keyAlgName, this.keysize, this.sigAlgName);
      this.kssave = true;
    } else if (this.command == Command.GENSECKEY) {
      if (this.keyAlgName == null)
        this.keyAlgName = "DES"; 
      doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
      this.kssave = true;
    } else if (this.command == Command.IMPORTPASS) {
      if (this.keyAlgName == null)
        this.keyAlgName = "PBE"; 
      doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
      this.kssave = true;
    } else if (this.command == Command.IDENTITYDB) {
      if (this.filename != null) {
        try (FileInputStream null = new FileInputStream(this.filename)) {
          doImportIdentityDatabase(fileInputStream);
        } 
      } else {
        doImportIdentityDatabase(System.in);
      } 
    } else if (this.command == Command.IMPORTCERT) {
      inputStream = System.in;
      if (this.filename != null)
        inputStream = new FileInputStream(this.filename); 
      String str = (this.alias != null) ? this.alias : "mykey";
      try {
        if (this.keyStore.entryInstanceOf(str, KeyStore.PrivateKeyEntry.class)) {
          this.kssave = installReply(str, inputStream);
          if (this.kssave) {
            System.err.println(rb.getString("Certificate.reply.was.installed.in.keystore"));
          } else {
            System.err.println(rb.getString("Certificate.reply.was.not.installed.in.keystore"));
          } 
        } else if (!this.keyStore.containsAlias(str) || this.keyStore.entryInstanceOf(str, KeyStore.TrustedCertificateEntry.class)) {
          this.kssave = addTrustedCert(str, inputStream);
          if (this.kssave) {
            System.err.println(rb.getString("Certificate.was.added.to.keystore"));
          } else {
            System.err.println(rb.getString("Certificate.was.not.added.to.keystore"));
          } 
        } 
      } finally {
        if (inputStream != System.in)
          inputStream.close(); 
      } 
    } else if (this.command == Command.IMPORTKEYSTORE) {
      if (keyStore1 == null)
        keyStore1 = loadSourceKeyStore(); 
      doImportKeyStore(keyStore1);
      this.kssave = true;
    } else if (this.command == Command.KEYCLONE) {
      this.keyPassNew = this.newPass;
      if (this.alias == null)
        this.alias = "mykey"; 
      if (!this.keyStore.containsAlias(this.alias)) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
        Object[] arrayOfObject = { this.alias };
        throw new Exception(messageFormat.format(arrayOfObject));
      } 
      if (!this.keyStore.entryInstanceOf(this.alias, KeyStore.PrivateKeyEntry.class)) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key"));
        Object[] arrayOfObject = { this.alias };
        throw new Exception(messageFormat.format(arrayOfObject));
      } 
      doCloneEntry(this.alias, this.dest, true);
      this.kssave = true;
    } else if (this.command == Command.CHANGEALIAS) {
      if (this.alias == null)
        this.alias = "mykey"; 
      doCloneEntry(this.alias, this.dest, false);
      if (this.keyStore.containsAlias(this.alias))
        doDeleteEntry(this.alias); 
      this.kssave = true;
    } else if (this.command == Command.KEYPASSWD) {
      this.keyPassNew = this.newPass;
      doChangeKeyPasswd(this.alias);
      this.kssave = true;
    } else if (this.command == Command.LIST) {
      if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype))
        printNoIntegrityWarning(); 
      if (this.alias != null) {
        doPrintEntry(rb.getString("the.certificate"), this.alias, paramPrintStream);
      } else {
        doPrintEntries(paramPrintStream);
      } 
    } else if (this.command == Command.PRINTCERT) {
      doPrintCert(paramPrintStream);
    } else if (this.command == Command.SELFCERT) {
      doSelfCert(this.alias, this.dname, this.sigAlgName);
      this.kssave = true;
    } else if (this.command == Command.STOREPASSWD) {
      this.storePassNew = this.newPass;
      if (this.storePassNew == null)
        this.storePassNew = getNewPasswd("keystore password", this.storePass); 
      this.kssave = true;
    } else if (this.command == Command.GENCERT) {
      if (this.alias == null)
        this.alias = "mykey"; 
      inputStream = System.in;
      if (this.infilename != null)
        inputStream = new FileInputStream(this.infilename); 
      printStream = null;
      if (this.outfilename != null) {
        printStream = new PrintStream(new FileOutputStream(this.outfilename));
        paramPrintStream = printStream;
      } 
      try {
        doGenCert(this.alias, this.sigAlgName, inputStream, paramPrintStream);
      } finally {
        if (inputStream != System.in)
          inputStream.close(); 
        if (printStream != null)
          printStream.close(); 
      } 
    } else if (this.command == Command.GENCRL) {
      if (this.alias == null)
        this.alias = "mykey"; 
      if (this.filename != null) {
        try (PrintStream null = new PrintStream(new FileOutputStream(this.filename))) {
          doGenCRL(printStream);
        } 
      } else {
        doGenCRL(paramPrintStream);
      } 
    } else if (this.command == Command.PRINTCERTREQ) {
      if (this.filename != null) {
        try (FileInputStream null = new FileInputStream(this.filename)) {
          doPrintCertReq(fileInputStream, paramPrintStream);
        } 
      } else {
        doPrintCertReq(System.in, paramPrintStream);
      } 
    } else if (this.command == Command.PRINTCRL) {
      doPrintCRL(this.filename, paramPrintStream);
    } 
    if (this.kssave) {
      if (this.verbose) {
        MessageFormat messageFormat = new MessageFormat(rb.getString(".Storing.ksfname."));
        Object[] arrayOfObject = { this.nullStream ? "keystore" : this.ksfname };
        System.err.println(messageFormat.format(arrayOfObject));
      } 
      if (this.token) {
        this.keyStore.store(null, null);
      } else {
        char[] arrayOfChar = (this.storePassNew != null) ? this.storePassNew : this.storePass;
        if (this.nullStream) {
          this.keyStore.store(null, arrayOfChar);
        } else {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          this.keyStore.store(byteArrayOutputStream, arrayOfChar);
          try (FileOutputStream null = new FileOutputStream(this.ksfname)) {
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
          } 
        } 
      } 
    } 
    if (isKeyStoreRelated(this.command) && !this.token && !this.nullStream && this.ksfname != null) {
      File file = new File(this.ksfname);
      if (file.exists()) {
        String str = keyStoreType(file);
        if (str.equalsIgnoreCase("JKS") || str.equalsIgnoreCase("JCEKS")) {
          boolean bool = true;
          for (String str1 : Collections.list(this.keyStore.aliases())) {
            if (!this.keyStore.entryInstanceOf(str1, KeyStore.TrustedCertificateEntry.class)) {
              bool = false;
              break;
            } 
          } 
          if (!bool)
            this.weakWarnings.add(String.format(rb.getString("jks.storetype.warning"), new Object[] { str, this.ksfname })); 
        } 
        if (this.inplaceImport) {
          String str1 = keyStoreType(new File(this.inplaceBackupName));
          String str2;
          this.weakWarnings.add((str2 = str.equalsIgnoreCase(str1) ? rb.getString("backup.keystore.warning") : rb.getString("migrate.keystore.warning")).format(str2, new Object[] { this.srcksfname, str1, this.inplaceBackupName, str }));
        } 
      } 
    } 
  }
  
  private String keyStoreType(File paramFile) throws IOException {
    int i = -17957139;
    int j = -825307442;
    try (DataInputStream null = new DataInputStream(new FileInputStream(paramFile))) {
      int k = dataInputStream.readInt();
      if (k == i)
        return "JKS"; 
      if (k == j)
        return "JCEKS"; 
      return "Non JKS/JCEKS";
    } 
  }
  
  private void doGenCert(String paramString1, String paramString2, InputStream paramInputStream, PrintStream paramPrintStream) throws Exception {
    if (!this.keyStore.containsAlias(paramString1)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString1 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    Certificate certificate = this.keyStore.getCertificate(paramString1);
    byte[] arrayOfByte1 = certificate.getEncoded();
    X509CertImpl x509CertImpl1 = new X509CertImpl(arrayOfByte1);
    X509CertInfo x509CertInfo1 = (X509CertInfo)x509CertImpl1.get("x509.info");
    X500Name x500Name = (X500Name)x509CertInfo1.get("subject.dname");
    Date date1 = getStartDate(this.startDate);
    Date date2 = new Date();
    date2.setTime(date1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
    CertificateValidity certificateValidity = new CertificateValidity(date1, date2);
    PrivateKey privateKey = (PrivateKey)(recoverKey(paramString1, this.storePass, this.keyPass)).fst;
    if (paramString2 == null)
      paramString2 = getCompatibleSigAlgName(privateKey.getAlgorithm()); 
    Signature signature = Signature.getInstance(paramString2);
    signature.initSign(privateKey);
    X509CertInfo x509CertInfo2 = new X509CertInfo();
    x509CertInfo2.set("validity", certificateValidity);
    x509CertInfo2.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & 0x7FFFFFFF));
    x509CertInfo2.set("version", new CertificateVersion(2));
    x509CertInfo2.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.get(paramString2)));
    x509CertInfo2.set("issuer", x500Name);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    boolean bool = false;
    StringBuffer stringBuffer = new StringBuffer();
    while (true) {
      String str = bufferedReader.readLine();
      if (str == null)
        break; 
      if (str.startsWith("-----BEGIN") && str.indexOf("REQUEST") >= 0) {
        bool = true;
        continue;
      } 
      if (str.startsWith("-----END") && str.indexOf("REQUEST") >= 0)
        break; 
      if (bool)
        stringBuffer.append(str); 
    } 
    byte[] arrayOfByte2 = Pem.decode(new String(stringBuffer));
    PKCS10 pKCS10 = new PKCS10(arrayOfByte2);
    checkWeak(rb.getString("the.certificate.request"), pKCS10);
    x509CertInfo2.set("key", new CertificateX509Key(pKCS10.getSubjectPublicKeyInfo()));
    x509CertInfo2.set("subject", (this.dname == null) ? pKCS10.getSubjectName() : new X500Name(this.dname));
    CertificateExtensions certificateExtensions1 = null;
    for (PKCS10Attribute pKCS10Attribute : pKCS10.getAttributes().getAttributes()) {
      if (pKCS10Attribute.getAttributeId().equals(PKCS9Attribute.EXTENSION_REQUEST_OID))
        certificateExtensions1 = (CertificateExtensions)pKCS10Attribute.getAttributeValue(); 
    } 
    CertificateExtensions certificateExtensions2 = createV3Extensions(certificateExtensions1, null, this.v3ext, pKCS10.getSubjectPublicKeyInfo(), certificate.getPublicKey());
    x509CertInfo2.set("extensions", certificateExtensions2);
    X509CertImpl x509CertImpl2 = new X509CertImpl(x509CertInfo2);
    x509CertImpl2.sign(privateKey, paramString2);
    dumpCert(x509CertImpl2, paramPrintStream);
    for (Certificate certificate1 : this.keyStore.getCertificateChain(paramString1)) {
      if (certificate1 instanceof X509Certificate) {
        X509Certificate x509Certificate = (X509Certificate)certificate1;
        if (!KeyStoreUtil.isSelfSigned(x509Certificate))
          dumpCert(x509Certificate, paramPrintStream); 
      } 
    } 
    checkWeak(rb.getString("the.issuer"), this.keyStore.getCertificateChain(paramString1));
    checkWeak(rb.getString("the.generated.certificate"), x509CertImpl2);
  }
  
  private void doGenCRL(PrintStream paramPrintStream) throws Exception {
    if (this.ids == null)
      throw new Exception("Must provide -id when -gencrl"); 
    Certificate certificate = this.keyStore.getCertificate(this.alias);
    byte[] arrayOfByte = certificate.getEncoded();
    X509CertImpl x509CertImpl = new X509CertImpl(arrayOfByte);
    X509CertInfo x509CertInfo = (X509CertInfo)x509CertImpl.get("x509.info");
    X500Name x500Name = (X500Name)x509CertInfo.get("subject.dname");
    Date date1 = getStartDate(this.startDate);
    Date date2 = (Date)date1.clone();
    date2.setTime(date2.getTime() + this.validity * 1000L * 24L * 60L * 60L);
    CertificateValidity certificateValidity = new CertificateValidity(date1, date2);
    PrivateKey privateKey = (PrivateKey)(recoverKey(this.alias, this.storePass, this.keyPass)).fst;
    if (this.sigAlgName == null)
      this.sigAlgName = getCompatibleSigAlgName(privateKey.getAlgorithm()); 
    X509CRLEntry[] arrayOfX509CRLEntry = new X509CRLEntry[this.ids.size()];
    for (byte b = 0; b < this.ids.size(); b++) {
      String str = (String)this.ids.get(b);
      int i = str.indexOf(':');
      if (i >= 0) {
        CRLExtensions cRLExtensions = new CRLExtensions();
        cRLExtensions.set("Reason", new CRLReasonCodeExtension(Integer.parseInt(str.substring(i + 1))));
        arrayOfX509CRLEntry[b] = new X509CRLEntryImpl(new BigInteger(str.substring(0, i)), date1, cRLExtensions);
      } else {
        arrayOfX509CRLEntry[b] = new X509CRLEntryImpl(new BigInteger((String)this.ids.get(b)), date1);
      } 
    } 
    X509CRLImpl x509CRLImpl = new X509CRLImpl(x500Name, date1, date2, arrayOfX509CRLEntry);
    x509CRLImpl.sign(privateKey, this.sigAlgName);
    if (this.rfc) {
      paramPrintStream.println("-----BEGIN X509 CRL-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(x509CRLImpl.getEncodedInternal()));
      paramPrintStream.println("-----END X509 CRL-----");
    } else {
      paramPrintStream.write(x509CRLImpl.getEncodedInternal());
    } 
    checkWeak(rb.getString("the.generated.crl"), x509CRLImpl, privateKey);
  }
  
  private void doCertReq(String paramString1, String paramString2, PrintStream paramPrintStream) throws Exception {
    if (paramString1 == null)
      paramString1 = "mykey"; 
    Pair pair = recoverKey(paramString1, this.storePass, this.keyPass);
    PrivateKey privateKey = (PrivateKey)pair.fst;
    if (this.keyPass == null)
      this.keyPass = (char[])pair.snd; 
    Certificate certificate = this.keyStore.getCertificate(paramString1);
    if (certificate == null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
      Object[] arrayOfObject = { paramString1 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    PKCS10 pKCS10 = new PKCS10(certificate.getPublicKey());
    CertificateExtensions certificateExtensions = createV3Extensions(null, null, this.v3ext, certificate.getPublicKey(), null);
    pKCS10.getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, certificateExtensions));
    if (paramString2 == null)
      paramString2 = getCompatibleSigAlgName(privateKey.getAlgorithm()); 
    Signature signature = Signature.getInstance(paramString2);
    signature.initSign(privateKey);
    X500Name x500Name = (this.dname == null) ? new X500Name(((X509Certificate)certificate).getSubjectDN().toString()) : new X500Name(this.dname);
    pKCS10.encodeAndSign(x500Name, signature);
    pKCS10.print(paramPrintStream);
    checkWeak(rb.getString("the.generated.certificate.request"), pKCS10);
  }
  
  private void doDeleteEntry(String paramString) throws Exception {
    if (!this.keyStore.containsAlias(paramString)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    this.keyStore.deleteEntry(paramString);
  }
  
  private void doExportCert(String paramString, PrintStream paramPrintStream) throws Exception {
    if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype))
      printNoIntegrityWarning(); 
    if (paramString == null)
      paramString = "mykey"; 
    if (!this.keyStore.containsAlias(paramString)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    X509Certificate x509Certificate = (X509Certificate)this.keyStore.getCertificate(paramString);
    if (x509Certificate == null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.has.no.certificate"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    dumpCert(x509Certificate, paramPrintStream);
    checkWeak(rb.getString("the.certificate"), x509Certificate);
  }
  
  private char[] promptForKeyPass(String paramString1, String paramString2, char[] paramArrayOfChar) throws Exception {
    if ("PKCS12".equalsIgnoreCase(this.storetype))
      return paramArrayOfChar; 
    if (!this.token && !this.protectedPath) {
      byte b;
      for (b = 0; b < 3; b++) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        Object[] arrayOfObject = { paramString1 };
        System.err.println(messageFormat.format(arrayOfObject));
        if (paramString2 == null) {
          System.err.print(rb.getString(".RETURN.if.same.as.keystore.password."));
        } else {
          messageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
          Object[] arrayOfObject1 = { paramString2 };
          System.err.print(messageFormat.format(arrayOfObject1));
        } 
        System.err.flush();
        char[] arrayOfChar = Password.readPassword(System.in);
        this.passwords.add(arrayOfChar);
        if (arrayOfChar == null)
          return paramArrayOfChar; 
        if (arrayOfChar.length >= 6) {
          System.err.print(rb.getString("Re.enter.new.password."));
          char[] arrayOfChar1 = Password.readPassword(System.in);
          this.passwords.add(arrayOfChar1);
          if (!Arrays.equals(arrayOfChar, arrayOfChar1)) {
            System.err.println(rb.getString("They.don.t.match.Try.again"));
          } else {
            return arrayOfChar;
          } 
        } else {
          System.err.println(rb.getString("Key.password.is.too.short.must.be.at.least.6.characters"));
        } 
      } 
      if (b == 3) {
        if (this.command == Command.KEYCLONE)
          throw new Exception(rb.getString("Too.many.failures.Key.entry.not.cloned")); 
        throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
      } 
    } 
    return null;
  }
  
  private char[] promptForCredential() throws Exception {
    if (System.console() == null) {
      char[] arrayOfChar = Password.readPassword(System.in);
      this.passwords.add(arrayOfChar);
      return arrayOfChar;
    } 
    byte b;
    for (b = 0; b < 3; b++) {
      System.err.print(rb.getString("Enter.the.password.to.be.stored."));
      System.err.flush();
      char[] arrayOfChar1 = Password.readPassword(System.in);
      this.passwords.add(arrayOfChar1);
      System.err.print(rb.getString("Re.enter.password."));
      char[] arrayOfChar2 = Password.readPassword(System.in);
      this.passwords.add(arrayOfChar2);
      if (!Arrays.equals(arrayOfChar1, arrayOfChar2)) {
        System.err.println(rb.getString("They.don.t.match.Try.again"));
      } else {
        return arrayOfChar1;
      } 
    } 
    if (b == 3)
      throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore")); 
    return null;
  }
  
  private void doGenSecretKey(String paramString1, String paramString2, int paramInt) throws Exception {
    if (paramString1 == null)
      paramString1 = "mykey"; 
    if (this.keyStore.containsAlias(paramString1)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Secret.key.not.generated.alias.alias.already.exists"));
      Object[] arrayOfObject = { paramString1 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    boolean bool = true;
    SecretKey secretKey = null;
    if (paramString2.toUpperCase(Locale.ENGLISH).startsWith("PBE")) {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBE");
      secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(promptForCredential()));
      if (!"PBE".equalsIgnoreCase(paramString2))
        bool = false; 
      if (this.verbose) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Generated.keyAlgName.secret.key"));
        Object[] arrayOfObject = { bool ? "PBE" : secretKey.getAlgorithm() };
        System.err.println(messageFormat.format(arrayOfObject));
      } 
    } else {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(paramString2);
      if (paramInt == -1)
        if ("DES".equalsIgnoreCase(paramString2)) {
          paramInt = 56;
        } else if ("DESede".equalsIgnoreCase(paramString2)) {
          paramInt = 168;
        } else {
          throw new Exception(rb.getString("Please.provide.keysize.for.secret.key.generation"));
        }  
      keyGenerator.init(paramInt);
      secretKey = keyGenerator.generateKey();
      if (this.verbose) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Generated.keysize.bit.keyAlgName.secret.key"));
        Object[] arrayOfObject = { new Integer(paramInt), secretKey.getAlgorithm() };
        System.err.println(messageFormat.format(arrayOfObject));
      } 
    } 
    if (this.keyPass == null)
      this.keyPass = promptForKeyPass(paramString1, null, this.storePass); 
    if (bool) {
      this.keyStore.setKeyEntry(paramString1, secretKey, this.keyPass, null);
    } else {
      this.keyStore.setEntry(paramString1, new KeyStore.SecretKeyEntry(secretKey), new KeyStore.PasswordProtection(this.keyPass, paramString2, null));
    } 
  }
  
  private static String getCompatibleSigAlgName(String paramString) throws Exception {
    if ("DSA".equalsIgnoreCase(paramString))
      return "SHA256WithDSA"; 
    if ("RSA".equalsIgnoreCase(paramString))
      return "SHA256WithRSA"; 
    if ("EC".equalsIgnoreCase(paramString))
      return "SHA256withECDSA"; 
    throw new Exception(rb.getString("Cannot.derive.signature.algorithm"));
  }
  
  private void doGenKeyPair(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4) throws Exception {
    X500Name x500Name;
    if (paramInt == -1)
      if ("EC".equalsIgnoreCase(paramString3)) {
        paramInt = SecurityProviderConstants.DEF_EC_KEY_SIZE;
      } else if ("RSA".equalsIgnoreCase(paramString3)) {
        paramInt = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
      } else if ("DSA".equalsIgnoreCase(paramString3)) {
        paramInt = SecurityProviderConstants.DEF_DSA_KEY_SIZE;
      }  
    if (paramString1 == null)
      paramString1 = "mykey"; 
    if (this.keyStore.containsAlias(paramString1)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Key.pair.not.generated.alias.alias.already.exists"));
      x500Name = new Object[] { paramString1 };
      throw new Exception(messageFormat.format(x500Name));
    } 
    if (paramString4 == null)
      paramString4 = getCompatibleSigAlgName(paramString3); 
    CertAndKeyGen certAndKeyGen = new CertAndKeyGen(paramString3, paramString4, this.providerName);
    if (paramString2 == null) {
      x500Name = getX500Name();
    } else {
      x500Name = new X500Name(paramString2);
    } 
    certAndKeyGen.generate(paramInt);
    PrivateKey privateKey = certAndKeyGen.getPrivateKey();
    CertificateExtensions certificateExtensions = createV3Extensions(null, null, this.v3ext, certAndKeyGen.getPublicKeyAnyway(), null);
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[1];
    arrayOfX509Certificate[0] = certAndKeyGen.getSelfCertificate(x500Name, getStartDate(this.startDate), this.validity * 24L * 60L * 60L, certificateExtensions);
    if (this.verbose) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for"));
      Object[] arrayOfObject = { new Integer(paramInt), privateKey.getAlgorithm(), arrayOfX509Certificate[0].getSigAlgName(), new Long(this.validity), x500Name };
      System.err.println(messageFormat.format(arrayOfObject));
    } 
    if (this.keyPass == null)
      this.keyPass = promptForKeyPass(paramString1, null, this.storePass); 
    checkWeak(rb.getString("the.generated.certificate"), arrayOfX509Certificate[0]);
    this.keyStore.setKeyEntry(paramString1, privateKey, this.keyPass, arrayOfX509Certificate);
  }
  
  private void doCloneEntry(String paramString1, String paramString2, boolean paramBoolean) throws Exception {
    if (paramString1 == null)
      paramString1 = "mykey"; 
    if (this.keyStore.containsAlias(paramString2)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Destination.alias.dest.already.exists"));
      Object[] arrayOfObject = { paramString2 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    Pair pair = recoverEntry(this.keyStore, paramString1, this.storePass, this.keyPass);
    KeyStore.Entry entry = (KeyStore.Entry)pair.fst;
    this.keyPass = (char[])pair.snd;
    KeyStore.PasswordProtection passwordProtection = null;
    if (this.keyPass != null) {
      if (!paramBoolean || "PKCS12".equalsIgnoreCase(this.storetype)) {
        this.keyPassNew = this.keyPass;
      } else if (this.keyPassNew == null) {
        this.keyPassNew = promptForKeyPass(paramString2, paramString1, this.keyPass);
      } 
      passwordProtection = new KeyStore.PasswordProtection(this.keyPassNew);
    } 
    this.keyStore.setEntry(paramString2, entry, passwordProtection);
  }
  
  private void doChangeKeyPasswd(String paramString) throws Exception {
    if (paramString == null)
      paramString = "mykey"; 
    Pair pair = recoverKey(paramString, this.storePass, this.keyPass);
    Key key = (Key)pair.fst;
    if (this.keyPass == null)
      this.keyPass = (char[])pair.snd; 
    if (this.keyPassNew == null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("key.password.for.alias."));
      Object[] arrayOfObject = { paramString };
      this.keyPassNew = getNewPasswd(messageFormat.format(arrayOfObject), this.keyPass);
    } 
    this.keyStore.setKeyEntry(paramString, key, this.keyPassNew, this.keyStore.getCertificateChain(paramString));
  }
  
  private void doImportIdentityDatabase(InputStream paramInputStream) throws Exception { System.err.println(rb.getString("No.entries.from.identity.database.added")); }
  
  private void doPrintEntry(String paramString1, String paramString2, PrintStream paramPrintStream) throws Exception {
    if (!this.keyStore.containsAlias(paramString2)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString2 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    if (this.verbose || this.rfc || this.debug) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.name.alias"));
      Object[] arrayOfObject = { paramString2 };
      paramPrintStream.println(messageFormat.format(arrayOfObject));
      if (!this.token) {
        messageFormat = new MessageFormat(rb.getString("Creation.date.keyStore.getCreationDate.alias."));
        Object[] arrayOfObject1 = { this.keyStore.getCreationDate(paramString2) };
        paramPrintStream.println(messageFormat.format(arrayOfObject1));
      } 
    } else if (!this.token) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias.keyStore.getCreationDate.alias."));
      Object[] arrayOfObject = { paramString2, this.keyStore.getCreationDate(paramString2) };
      paramPrintStream.print(messageFormat.format(arrayOfObject));
    } else {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias."));
      Object[] arrayOfObject = { paramString2 };
      paramPrintStream.print(messageFormat.format(arrayOfObject));
    } 
    if (this.keyStore.entryInstanceOf(paramString2, KeyStore.SecretKeyEntry.class)) {
      if (this.verbose || this.rfc || this.debug) {
        Object[] arrayOfObject = { "SecretKeyEntry" };
        paramPrintStream.println((new MessageFormat(rb.getString("Entry.type.type."))).format(arrayOfObject));
      } else {
        paramPrintStream.println("SecretKeyEntry, ");
      } 
    } else if (this.keyStore.entryInstanceOf(paramString2, KeyStore.PrivateKeyEntry.class)) {
      if (this.verbose || this.rfc || this.debug) {
        Object[] arrayOfObject = { "PrivateKeyEntry" };
        paramPrintStream.println((new MessageFormat(rb.getString("Entry.type.type."))).format(arrayOfObject));
      } else {
        paramPrintStream.println("PrivateKeyEntry, ");
      } 
      Certificate[] arrayOfCertificate = this.keyStore.getCertificateChain(paramString2);
      if (arrayOfCertificate != null)
        if (this.verbose || this.rfc || this.debug) {
          paramPrintStream.println(rb.getString("Certificate.chain.length.") + arrayOfCertificate.length);
          for (byte b = 0; b < arrayOfCertificate.length; b++) {
            MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.i.1."));
            Object[] arrayOfObject = { new Integer(b + true) };
            paramPrintStream.println(messageFormat.format(arrayOfObject));
            if (this.verbose && arrayOfCertificate[b] instanceof X509Certificate) {
              printX509Cert((X509Certificate)arrayOfCertificate[b], paramPrintStream);
            } else if (this.debug) {
              paramPrintStream.println(arrayOfCertificate[b].toString());
            } else {
              dumpCert(arrayOfCertificate[b], paramPrintStream);
            } 
            checkWeak(paramString1, arrayOfCertificate[b]);
          } 
        } else {
          paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", arrayOfCertificate[0]));
          checkWeak(paramString1, arrayOfCertificate[0]);
        }  
    } else if (this.keyStore.entryInstanceOf(paramString2, KeyStore.TrustedCertificateEntry.class)) {
      Certificate certificate = this.keyStore.getCertificate(paramString2);
      Object[] arrayOfObject = { "trustedCertEntry" };
      String str = (new MessageFormat(rb.getString("Entry.type.type."))).format(arrayOfObject) + "\n";
      if (this.verbose && certificate instanceof X509Certificate) {
        paramPrintStream.println(str);
        printX509Cert((X509Certificate)certificate, paramPrintStream);
      } else if (this.rfc) {
        paramPrintStream.println(str);
        dumpCert(certificate, paramPrintStream);
      } else if (this.debug) {
        paramPrintStream.println(certificate.toString());
      } else {
        paramPrintStream.println("trustedCertEntry, ");
        paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", certificate));
      } 
      checkWeak(paramString1, certificate);
    } else {
      paramPrintStream.println(rb.getString("Unknown.Entry.Type"));
    } 
  }
  
  boolean inplaceImportCheck() throws Exception {
    if ("PKCS11".equalsIgnoreCase(this.srcstoretype) || KeyStoreUtil.isWindowsKeyStore(this.srcstoretype))
      return false; 
    if (this.srcksfname != null) {
      File file = new File(this.srcksfname);
      if (file.exists() && file.length() == 0L)
        throw new Exception(rb.getString("Source.keystore.file.exists.but.is.empty.") + this.srcksfname); 
      if (file.getCanonicalFile().equals((new File(this.ksfname)).getCanonicalFile()))
        return true; 
      System.err.println(String.format(rb.getString("importing.keystore.status"), new Object[] { this.srcksfname, this.ksfname }));
      return false;
    } 
    throw new Exception(rb.getString("Please.specify.srckeystore"));
  }
  
  KeyStore loadSourceKeyStore() throws Exception {
    KeyStore keyStore1;
    fileInputStream = null;
    File file = null;
    if ("PKCS11".equalsIgnoreCase(this.srcstoretype) || KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
      if (!"NONE".equals(this.srcksfname)) {
        System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.srcstoretype }));
        System.err.println();
        tinyHelp();
      } 
    } else {
      file = new File(this.srcksfname);
      fileInputStream = new FileInputStream(file);
    } 
    try {
      if (this.srcstoretype == null)
        this.srcstoretype = KeyStore.getDefaultType(); 
      if (this.srcProviderName == null) {
        keyStore1 = KeyStore.getInstance(this.srcstoretype);
      } else {
        keyStore1 = KeyStore.getInstance(this.srcstoretype, this.srcProviderName);
      } 
      if (this.srcstorePass == null && !this.srcprotectedPath && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
        System.err.print(rb.getString("Enter.source.keystore.password."));
        System.err.flush();
        this.srcstorePass = Password.readPassword(System.in);
        this.passwords.add(this.srcstorePass);
      } 
      if ("PKCS12".equalsIgnoreCase(this.srcstoretype) && this.srckeyPass != null && this.srcstorePass != null && !Arrays.equals(this.srcstorePass, this.srckeyPass)) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
        Object[] arrayOfObject = { "-srckeypass" };
        System.err.println(messageFormat.format(arrayOfObject));
        this.srckeyPass = this.srcstorePass;
      } 
      keyStore1.load(fileInputStream, this.srcstorePass);
    } finally {
      if (fileInputStream != null)
        fileInputStream.close(); 
    } 
    if (this.srcstorePass == null && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
      System.err.println();
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println();
    } 
    return keyStore1;
  }
  
  private void doImportKeyStore(KeyStore paramKeyStore) throws Exception {
    if (this.alias != null) {
      doImportKeyStoreSingle(paramKeyStore, this.alias);
    } else {
      if (this.dest != null || this.srckeyPass != null)
        throw new Exception(rb.getString("if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified")); 
      doImportKeyStoreAll(paramKeyStore);
    } 
    if (this.inplaceImport)
      for (byte b = 1;; b++) {
        this.inplaceBackupName = this.srcksfname + ".old" + ((b == 1) ? "" : Integer.valueOf(b));
        File file = new File(this.inplaceBackupName);
        if (!file.exists()) {
          Files.copy(Paths.get(this.srcksfname, new String[0]), file.toPath(), new java.nio.file.CopyOption[0]);
          break;
        } 
      }  
  }
  
  private int doImportKeyStoreSingle(KeyStore paramKeyStore, String paramString) throws Exception {
    String str = (this.dest == null) ? paramString : this.dest;
    if (this.keyStore.containsAlias(str)) {
      Object[] arrayOfObject = { paramString };
      if (this.noprompt) {
        System.err.println((new MessageFormat(rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore"))).format(arrayOfObject));
      } else {
        String str1 = getYesNoReply((new MessageFormat(rb.getString("Existing.entry.alias.alias.exists.overwrite.no."))).format(arrayOfObject));
        if ("NO".equals(str1)) {
          str = inputStringFromStdin(rb.getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
          if ("".equals(str)) {
            System.err.println((new MessageFormat(rb.getString("Entry.for.alias.alias.not.imported."))).format(arrayOfObject));
            return 0;
          } 
        } 
      } 
    } 
    Pair pair = recoverEntry(paramKeyStore, paramString, this.srcstorePass, this.srckeyPass);
    KeyStore.Entry entry = (KeyStore.Entry)pair.fst;
    KeyStore.PasswordProtection passwordProtection = null;
    char[] arrayOfChar = null;
    if (this.destKeyPass != null) {
      arrayOfChar = this.destKeyPass;
      passwordProtection = new KeyStore.PasswordProtection(this.destKeyPass);
    } else if (pair.snd != null) {
      arrayOfChar = (char[])pair.snd;
      passwordProtection = new KeyStore.PasswordProtection((char[])pair.snd);
    } 
    try {
      Certificate certificate = paramKeyStore.getCertificate(paramString);
      if (certificate != null)
        checkWeak("<" + str + ">", certificate); 
      this.keyStore.setEntry(str, entry, passwordProtection);
      if ("PKCS12".equalsIgnoreCase(this.storetype) && arrayOfChar != null && !Arrays.equals(arrayOfChar, this.storePass))
        throw new Exception(rb.getString("The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified.")); 
      return 1;
    } catch (KeyStoreException keyStoreException) {
      Object[] arrayOfObject = { paramString, keyStoreException.toString() };
      MessageFormat messageFormat = new MessageFormat(rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported."));
      System.err.println(messageFormat.format(arrayOfObject));
      return 2;
    } 
  }
  
  private void doImportKeyStoreAll(KeyStore paramKeyStore) throws Exception {
    int i = 0;
    int j = paramKeyStore.size();
    Enumeration enumeration = paramKeyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      int k = doImportKeyStoreSingle(paramKeyStore, str);
      if (k == 1) {
        i++;
        Object[] arrayOfObject1 = { str };
        MessageFormat messageFormat1 = new MessageFormat(rb.getString("Entry.for.alias.alias.successfully.imported."));
        System.err.println(messageFormat1.format(arrayOfObject1));
        continue;
      } 
      if (k == 2 && !this.noprompt) {
        String str1 = getYesNoReply("Do you want to quit the import process? [no]:  ");
        if ("YES".equals(str1))
          break; 
      } 
    } 
    Object[] arrayOfObject = { Integer.valueOf(i), Integer.valueOf(j - i) };
    MessageFormat messageFormat = new MessageFormat(rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled"));
    System.err.println(messageFormat.format(arrayOfObject));
  }
  
  private void doPrintEntries(PrintStream paramPrintStream) throws Exception {
    paramPrintStream.println(rb.getString("Keystore.type.") + this.keyStore.getType());
    paramPrintStream.println(rb.getString("Keystore.provider.") + this.keyStore.getProvider().getName());
    paramPrintStream.println();
    MessageFormat messageFormat = (this.keyStore.size() == 1) ? new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entries"));
    Object[] arrayOfObject = { new Integer(this.keyStore.size()) };
    paramPrintStream.println(messageFormat.format(arrayOfObject));
    paramPrintStream.println();
    Enumeration enumeration = this.keyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      doPrintEntry("<" + str + ">", str, paramPrintStream);
      if (this.verbose || this.rfc) {
        paramPrintStream.println(rb.getString("NEWLINE"));
        paramPrintStream.println(rb.getString("STAR"));
        paramPrintStream.println(rb.getString("STARNN"));
      } 
    } 
  }
  
  private static <T> Iterable<T> e2i(final Enumeration<T> e) { return new Iterable<T>() {
        public Iterator<T> iterator() { return new Iterator<T>() {
              public boolean hasNext() throws Exception { return e.hasMoreElements(); }
              
              public T next() { return (T)e.nextElement(); }
              
              public void remove() { throw new UnsupportedOperationException("Not supported yet."); }
            }; }
      }; }
  
  public static Collection<? extends CRL> loadCRLs(String paramString) throws Exception {
    inputStream = null;
    URI uRI = null;
    if (paramString == null) {
      inputStream = System.in;
    } else {
      try {
        uRI = new URI(paramString);
        if (!uRI.getScheme().equals("ldap"))
          inputStream = uRI.toURL().openStream(); 
      } catch (Exception exception) {
        try {
          inputStream = new FileInputStream(paramString);
        } catch (Exception exception1) {
          if (uRI == null || uRI.getScheme() == null)
            throw exception1; 
          throw exception;
        } 
      } 
    } 
    if (inputStream != null)
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte[4096];
        while (true) {
          int i = inputStream.read(arrayOfByte);
          if (i < 0)
            break; 
          byteArrayOutputStream.write(arrayOfByte, 0, i);
        } 
        return CertificateFactory.getInstance("X509").generateCRLs(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
      } finally {
        if (inputStream != System.in)
          inputStream.close(); 
      }  
    CertStoreHelper certStoreHelper = CertStoreHelper.getInstance("LDAP");
    String str = uRI.getPath();
    if (str.charAt(0) == '/')
      str = str.substring(1); 
    CertStore certStore = certStoreHelper.getCertStore(uRI);
    X509CRLSelector x509CRLSelector = certStoreHelper.wrap(new X509CRLSelector(), null, str);
    return certStore.getCRLs(x509CRLSelector);
  }
  
  public static List<CRL> readCRLsFromCert(X509Certificate paramX509Certificate) throws Exception {
    ArrayList arrayList = new ArrayList();
    CRLDistributionPointsExtension cRLDistributionPointsExtension = X509CertImpl.toImpl(paramX509Certificate).getCRLDistributionPointsExtension();
    if (cRLDistributionPointsExtension == null)
      return arrayList; 
    List list = cRLDistributionPointsExtension.get("points");
    for (DistributionPoint distributionPoint : list) {
      GeneralNames generalNames = distributionPoint.getFullName();
      if (generalNames != null)
        label22: for (GeneralName generalName : generalNames.names()) {
          if (generalName.getType() == 6) {
            URIName uRIName = (URIName)generalName.getName();
            Iterator iterator = loadCRLs(uRIName.getName()).iterator();
            break label22;
          } 
        }  
    } 
    return arrayList;
  }
  
  private static String verifyCRL(KeyStore paramKeyStore, CRL paramCRL) throws Exception {
    X509CRLImpl x509CRLImpl = (X509CRLImpl)paramCRL;
    X500Principal x500Principal = x509CRLImpl.getIssuerX500Principal();
    for (String str : e2i(paramKeyStore.aliases())) {
      Certificate certificate = paramKeyStore.getCertificate(str);
      if (certificate instanceof X509Certificate) {
        X509Certificate x509Certificate = (X509Certificate)certificate;
        if (x509Certificate.getSubjectX500Principal().equals(x500Principal))
          try {
            ((X509CRLImpl)paramCRL).verify(certificate.getPublicKey());
            return str;
          } catch (Exception exception) {} 
      } 
    } 
    return null;
  }
  
  private void doPrintCRL(String paramString, PrintStream paramPrintStream) throws Exception {
    for (CRL cRL : loadCRLs(paramString)) {
      printCRL(cRL, paramPrintStream);
      String str = null;
      Certificate certificate = null;
      if (this.caks != null) {
        str = verifyCRL(this.caks, cRL);
        if (str != null) {
          certificate = this.caks.getCertificate(str);
          paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "cacerts", withWeak(certificate.getPublicKey()) });
          paramPrintStream.println();
        } 
      } 
      if (str == null && this.keyStore != null) {
        str = verifyCRL(this.keyStore, cRL);
        if (str != null) {
          certificate = this.keyStore.getCertificate(str);
          paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "keystore", withWeak(certificate.getPublicKey()) });
          paramPrintStream.println();
        } 
      } 
      if (str == null) {
        paramPrintStream.println(rb.getString("STAR"));
        paramPrintStream.println(rb.getString("warning.not.verified.make.sure.keystore.is.correct"));
        paramPrintStream.println(rb.getString("STARNN"));
      } 
      checkWeak(rb.getString("the.crl"), cRL, (certificate == null) ? null : certificate.getPublicKey());
    } 
  }
  
  private void printCRL(CRL paramCRL, PrintStream paramPrintStream) throws Exception {
    X509CRL x509CRL = (X509CRL)paramCRL;
    if (this.rfc) {
      paramPrintStream.println("-----BEGIN X509 CRL-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(x509CRL.getEncoded()));
      paramPrintStream.println("-----END X509 CRL-----");
    } else {
      String str;
      if (paramCRL instanceof X509CRLImpl) {
        X509CRLImpl x509CRLImpl = (X509CRLImpl)paramCRL;
        str = x509CRLImpl.toStringWithAlgName(withWeak("" + x509CRLImpl.getSigAlgId()));
      } else {
        str = paramCRL.toString();
      } 
      paramPrintStream.println(str);
    } 
  }
  
  private void doPrintCertReq(InputStream paramInputStream, PrintStream paramPrintStream) throws Exception {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = false;
    while (true) {
      String str = bufferedReader.readLine();
      if (str == null)
        break; 
      if (!bool) {
        if (str.startsWith("-----"))
          bool = true; 
        continue;
      } 
      if (str.startsWith("-----"))
        break; 
      stringBuffer.append(str);
    } 
    PKCS10 pKCS10 = new PKCS10(Pem.decode(new String(stringBuffer)));
    PublicKey publicKey = pKCS10.getSubjectPublicKeyInfo();
    paramPrintStream.printf(rb.getString("PKCS.10.with.weak"), new Object[] { pKCS10.getSubjectName(), publicKey.getFormat(), withWeak(publicKey), withWeak(pKCS10.getSigAlg()) });
    for (PKCS10Attribute pKCS10Attribute : pKCS10.getAttributes().getAttributes()) {
      ObjectIdentifier objectIdentifier = pKCS10Attribute.getAttributeId();
      if (objectIdentifier.equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
        CertificateExtensions certificateExtensions = (CertificateExtensions)pKCS10Attribute.getAttributeValue();
        if (certificateExtensions != null)
          printExtensions(rb.getString("Extension.Request."), certificateExtensions, paramPrintStream); 
        continue;
      } 
      paramPrintStream.println("Attribute: " + pKCS10Attribute.getAttributeId());
      PKCS9Attribute pKCS9Attribute = new PKCS9Attribute(pKCS10Attribute.getAttributeId(), pKCS10Attribute.getAttributeValue());
      paramPrintStream.print(pKCS9Attribute.getName() + ": ");
      Object object = pKCS10Attribute.getAttributeValue();
      paramPrintStream.println((object instanceof String[]) ? Arrays.toString((String[])object) : object);
    } 
    if (this.debug)
      paramPrintStream.println(pKCS10); 
    checkWeak(rb.getString("the.certificate.request"), pKCS10);
  }
  
  private void printCertFromStream(InputStream paramInputStream, PrintStream paramPrintStream) throws Exception {
    Collection collection = null;
    try {
      collection = this.cf.generateCertificates(paramInputStream);
    } catch (CertificateException certificateException) {
      throw new Exception(rb.getString("Failed.to.parse.input"), certificateException);
    } 
    if (collection.isEmpty())
      throw new Exception(rb.getString("Empty.input")); 
    Certificate[] arrayOfCertificate = (Certificate[])collection.toArray(new Certificate[collection.size()]);
    for (byte b = 0; b < arrayOfCertificate.length; b++) {
      X509Certificate x509Certificate = null;
      try {
        x509Certificate = (X509Certificate)arrayOfCertificate[b];
      } catch (ClassCastException classCastException) {
        throw new Exception(rb.getString("Not.X.509.certificate"));
      } 
      if (arrayOfCertificate.length > 1) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.i.1."));
        Object[] arrayOfObject = { new Integer(b + true) };
        paramPrintStream.println(messageFormat.format(arrayOfObject));
      } 
      if (this.rfc) {
        dumpCert(x509Certificate, paramPrintStream);
      } else {
        printX509Cert(x509Certificate, paramPrintStream);
      } 
      if (b < arrayOfCertificate.length - 1)
        paramPrintStream.println(); 
      checkWeak(oneInMany(rb.getString("the.certificate"), b, arrayOfCertificate.length), x509Certificate);
    } 
  }
  
  private static String oneInMany(String paramString, int paramInt1, int paramInt2) { return (paramInt2 == 1) ? paramString : String.format(rb.getString("one.in.many"), new Object[] { paramString, Integer.valueOf(paramInt1 + 1), Integer.valueOf(paramInt2) }); }
  
  private void doPrintCert(PrintStream paramPrintStream) throws Exception {
    if (this.jarfile != null) {
      JarFile jarFile = new JarFile(this.jarfile, true);
      Enumeration enumeration = jarFile.entries();
      HashSet hashSet = new HashSet();
      byte[] arrayOfByte = new byte[8192];
      byte b = 0;
      while (enumeration.hasMoreElements()) {
        JarEntry jarEntry = (JarEntry)enumeration.nextElement();
        try (InputStream null = jarFile.getInputStream(jarEntry)) {
          while (inputStream.read(arrayOfByte) != -1);
        } 
        CodeSigner[] arrayOfCodeSigner = jarEntry.getCodeSigners();
        if (arrayOfCodeSigner != null)
          for (CodeSigner codeSigner : arrayOfCodeSigner) {
            if (!hashSet.contains(codeSigner)) {
              hashSet.add(codeSigner);
              paramPrintStream.printf(rb.getString("Signer.d."), new Object[] { Integer.valueOf(++b) });
              paramPrintStream.println();
              paramPrintStream.println();
              paramPrintStream.println(rb.getString("Signature."));
              paramPrintStream.println();
              List list = codeSigner.getSignerCertPath().getCertificates();
              byte b1 = 0;
              for (Certificate certificate : list) {
                X509Certificate x509Certificate = (X509Certificate)certificate;
                if (this.rfc) {
                  paramPrintStream.println(rb.getString("Certificate.owner.") + x509Certificate.getSubjectDN() + "\n");
                  dumpCert(x509Certificate, paramPrintStream);
                } else {
                  printX509Cert(x509Certificate, paramPrintStream);
                } 
                paramPrintStream.println();
                checkWeak(oneInMany(rb.getString("the.certificate"), b1++, list.size()), x509Certificate);
              } 
              Timestamp timestamp = codeSigner.getTimestamp();
              if (timestamp != null) {
                paramPrintStream.println(rb.getString("Timestamp."));
                paramPrintStream.println();
                list = timestamp.getSignerCertPath().getCertificates();
                b1 = 0;
                for (Certificate certificate : list) {
                  X509Certificate x509Certificate = (X509Certificate)certificate;
                  if (this.rfc) {
                    paramPrintStream.println(rb.getString("Certificate.owner.") + x509Certificate.getSubjectDN() + "\n");
                    dumpCert(x509Certificate, paramPrintStream);
                  } else {
                    printX509Cert(x509Certificate, paramPrintStream);
                  } 
                  paramPrintStream.println();
                  checkWeak(oneInMany(rb.getString("the.tsa.certificate"), b1++, list.size()), x509Certificate);
                } 
              } 
            } 
          }  
      } 
      jarFile.close();
      if (hashSet.isEmpty())
        paramPrintStream.println(rb.getString("Not.a.signed.jar.file")); 
    } else if (this.sslserver != null) {
      Collection collection;
      CertStoreHelper certStoreHelper = CertStoreHelper.getInstance("SSLServer");
      CertStore certStore = certStoreHelper.getCertStore(new URI("https://" + this.sslserver));
      try {
        collection = certStore.getCertificates(null);
        if (collection.isEmpty())
          throw new Exception(rb.getString("No.certificate.from.the.SSL.server")); 
      } catch (CertStoreException certStoreException) {
        if (certStoreException.getCause() instanceof IOException)
          throw new Exception(rb.getString("No.certificate.from.the.SSL.server"), certStoreException.getCause()); 
        throw certStoreException;
      } 
      byte b = 0;
      for (Certificate certificate : collection) {
        try {
          if (this.rfc) {
            dumpCert(certificate, paramPrintStream);
          } else {
            paramPrintStream.println("Certificate #" + b);
            paramPrintStream.println("====================================");
            printX509Cert((X509Certificate)certificate, paramPrintStream);
            paramPrintStream.println();
          } 
          checkWeak(oneInMany(rb.getString("the.certificate"), b++, collection.size()), certificate);
        } catch (Exception exception) {
          if (this.debug)
            exception.printStackTrace(); 
        } 
      } 
    } else if (this.filename != null) {
      try (FileInputStream null = new FileInputStream(this.filename)) {
        printCertFromStream(fileInputStream, paramPrintStream);
      } 
    } else {
      printCertFromStream(System.in, paramPrintStream);
    } 
  }
  
  private void doSelfCert(String paramString1, String paramString2, String paramString3) throws Exception {
    X500Name x500Name;
    if (paramString1 == null)
      paramString1 = "mykey"; 
    Pair pair = recoverKey(paramString1, this.storePass, this.keyPass);
    PrivateKey privateKey = (PrivateKey)pair.fst;
    if (this.keyPass == null)
      this.keyPass = (char[])pair.snd; 
    if (paramString3 == null)
      paramString3 = getCompatibleSigAlgName(privateKey.getAlgorithm()); 
    Certificate certificate = this.keyStore.getCertificate(paramString1);
    if (certificate == null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias.has.no.public.key"));
      Object[] arrayOfObject = { paramString1 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    if (!(certificate instanceof X509Certificate)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias.has.no.X.509.certificate"));
      Object[] arrayOfObject = { paramString1 };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    byte[] arrayOfByte = certificate.getEncoded();
    X509CertImpl x509CertImpl1 = new X509CertImpl(arrayOfByte);
    X509CertInfo x509CertInfo = (X509CertInfo)x509CertImpl1.get("x509.info");
    Date date1 = getStartDate(this.startDate);
    Date date2 = new Date();
    date2.setTime(date1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
    CertificateValidity certificateValidity = new CertificateValidity(date1, date2);
    x509CertInfo.set("validity", certificateValidity);
    x509CertInfo.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & 0x7FFFFFFF));
    if (paramString2 == null) {
      x500Name = (X500Name)x509CertInfo.get("subject.dname");
    } else {
      x500Name = new X500Name(paramString2);
      x509CertInfo.set("subject.dname", x500Name);
    } 
    x509CertInfo.set("issuer.dname", x500Name);
    X509CertImpl x509CertImpl2 = new X509CertImpl(x509CertInfo);
    x509CertImpl2.sign(privateKey, paramString3);
    AlgorithmId algorithmId = (AlgorithmId)x509CertImpl2.get("x509.algorithm");
    x509CertInfo.set("algorithmID.algorithm", algorithmId);
    x509CertInfo.set("version", new CertificateVersion(2));
    CertificateExtensions certificateExtensions = createV3Extensions(null, (CertificateExtensions)x509CertInfo.get("extensions"), this.v3ext, certificate.getPublicKey(), null);
    x509CertInfo.set("extensions", certificateExtensions);
    x509CertImpl2 = new X509CertImpl(x509CertInfo);
    x509CertImpl2.sign(privateKey, paramString3);
    this.keyStore.setKeyEntry(paramString1, privateKey, (this.keyPass != null) ? this.keyPass : this.storePass, new Certificate[] { x509CertImpl2 });
    if (this.verbose) {
      System.err.println(rb.getString("New.certificate.self.signed."));
      System.err.print(x509CertImpl2.toString());
      System.err.println();
    } 
  }
  
  private boolean installReply(String paramString, InputStream paramInputStream) throws Exception {
    Certificate[] arrayOfCertificate2;
    if (paramString == null)
      paramString = "mykey"; 
    Pair pair = recoverKey(paramString, this.storePass, this.keyPass);
    PrivateKey privateKey = (PrivateKey)pair.fst;
    if (this.keyPass == null)
      this.keyPass = (char[])pair.snd; 
    Certificate certificate = this.keyStore.getCertificate(paramString);
    if (certificate == null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    Collection collection = this.cf.generateCertificates(paramInputStream);
    if (collection.isEmpty())
      throw new Exception(rb.getString("Reply.has.no.certificates")); 
    Certificate[] arrayOfCertificate1 = (Certificate[])collection.toArray(new Certificate[collection.size()]);
    if (arrayOfCertificate1.length == 1) {
      arrayOfCertificate2 = establishCertChain(certificate, arrayOfCertificate1[0]);
    } else {
      arrayOfCertificate2 = validateReply(paramString, certificate, arrayOfCertificate1);
    } 
    if (arrayOfCertificate2 != null) {
      this.keyStore.setKeyEntry(paramString, privateKey, (this.keyPass != null) ? this.keyPass : this.storePass, arrayOfCertificate2);
      return true;
    } 
    return false;
  }
  
  private boolean addTrustedCert(String paramString, InputStream paramInputStream) throws Exception {
    if (paramString == null)
      throw new Exception(rb.getString("Must.specify.alias")); 
    if (this.keyStore.containsAlias(paramString)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.not.imported.alias.alias.already.exists"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    X509Certificate x509Certificate = null;
    try {
      x509Certificate = (X509Certificate)this.cf.generateCertificate(paramInputStream);
    } catch (ClassCastException|CertificateException classCastException) {
      throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
    } 
    if (this.noprompt) {
      checkWeak(rb.getString("the.input"), x509Certificate);
      this.keyStore.setCertificateEntry(paramString, x509Certificate);
      return true;
    } 
    boolean bool = false;
    if (KeyStoreUtil.isSelfSigned(x509Certificate)) {
      x509Certificate.verify(x509Certificate.getPublicKey());
      bool = true;
    } 
    String str1 = null;
    String str2 = this.keyStore.getCertificateAlias(x509Certificate);
    if (str2 != null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias."));
      Object[] arrayOfObject = { str2 };
      System.err.println(messageFormat.format(arrayOfObject));
      checkWeak(rb.getString("the.input"), x509Certificate);
      printWeakWarnings(true);
      str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.no."));
    } else if (bool) {
      if (this.trustcacerts && this.caks != null && (str2 = this.caks.getCertificateAlias(x509Certificate)) != null) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias."));
        Object[] arrayOfObject = { str2 };
        System.err.println(messageFormat.format(arrayOfObject));
        checkWeak(rb.getString("the.input"), x509Certificate);
        printWeakWarnings(true);
        str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
      } 
      if (str2 == null) {
        printX509Cert(x509Certificate, System.out);
        checkWeak(rb.getString("the.input"), x509Certificate);
        printWeakWarnings(true);
        str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
      } 
    } 
    if (str1 != null) {
      if ("YES".equals(str1)) {
        this.keyStore.setCertificateEntry(paramString, x509Certificate);
        return true;
      } 
      return false;
    } 
    try {
      Certificate[] arrayOfCertificate = establishCertChain(null, x509Certificate);
      if (arrayOfCertificate != null) {
        this.keyStore.setCertificateEntry(paramString, x509Certificate);
        return true;
      } 
    } catch (Exception exception) {
      printX509Cert(x509Certificate, System.out);
      checkWeak(rb.getString("the.input"), x509Certificate);
      printWeakWarnings(true);
      str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
      if ("YES".equals(str1)) {
        this.keyStore.setCertificateEntry(paramString, x509Certificate);
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  private char[] getNewPasswd(String paramString, char[] paramArrayOfChar) throws Exception {
    char[] arrayOfChar1 = null;
    char[] arrayOfChar2 = null;
    for (byte b = 0; b < 3; b++) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("New.prompt."));
      Object[] arrayOfObject = { paramString };
      System.err.print(messageFormat.format(arrayOfObject));
      arrayOfChar1 = Password.readPassword(System.in);
      this.passwords.add(arrayOfChar1);
      if (arrayOfChar1 == null || arrayOfChar1.length < 6) {
        System.err.println(rb.getString("Password.is.too.short.must.be.at.least.6.characters"));
      } else if (Arrays.equals(arrayOfChar1, paramArrayOfChar)) {
        System.err.println(rb.getString("Passwords.must.differ"));
      } else {
        messageFormat = new MessageFormat(rb.getString("Re.enter.new.prompt."));
        Object[] arrayOfObject1 = { paramString };
        System.err.print(messageFormat.format(arrayOfObject1));
        arrayOfChar2 = Password.readPassword(System.in);
        this.passwords.add(arrayOfChar2);
        if (!Arrays.equals(arrayOfChar1, arrayOfChar2)) {
          System.err.println(rb.getString("They.don.t.match.Try.again"));
        } else {
          Arrays.fill(arrayOfChar2, ' ');
          return arrayOfChar1;
        } 
      } 
      if (arrayOfChar1 != null) {
        Arrays.fill(arrayOfChar1, ' ');
        arrayOfChar1 = null;
      } 
      if (arrayOfChar2 != null) {
        Arrays.fill(arrayOfChar2, ' ');
        arrayOfChar2 = null;
      } 
    } 
    throw new Exception(rb.getString("Too.many.failures.try.later"));
  }
  
  private String getAlias(String paramString) throws Exception {
    if (paramString != null) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Enter.prompt.alias.name."));
      Object[] arrayOfObject = { paramString };
      System.err.print(messageFormat.format(arrayOfObject));
    } else {
      System.err.print(rb.getString("Enter.alias.name."));
    } 
    return (new BufferedReader(new InputStreamReader(System.in))).readLine();
  }
  
  private String inputStringFromStdin(String paramString) throws Exception {
    System.err.print(paramString);
    return (new BufferedReader(new InputStreamReader(System.in))).readLine();
  }
  
  private char[] getKeyPasswd(String paramString1, String paramString2, char[] paramArrayOfChar) throws Exception {
    byte b = 0;
    char[] arrayOfChar = null;
    do {
      if (paramArrayOfChar != null) {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        Object[] arrayOfObject1 = { paramString1 };
        System.err.println(messageFormat.format(arrayOfObject1));
        messageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
        Object[] arrayOfObject2 = { paramString2 };
        System.err.print(messageFormat.format(arrayOfObject2));
      } else {
        MessageFormat messageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        Object[] arrayOfObject = { paramString1 };
        System.err.print(messageFormat.format(arrayOfObject));
      } 
      System.err.flush();
      arrayOfChar = Password.readPassword(System.in);
      this.passwords.add(arrayOfChar);
      if (arrayOfChar == null)
        arrayOfChar = paramArrayOfChar; 
      b++;
    } while (arrayOfChar == null && b < 3);
    if (arrayOfChar == null)
      throw new Exception(rb.getString("Too.many.failures.try.later")); 
    return arrayOfChar;
  }
  
  private String withWeak(String paramString) throws Exception { return DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString, null) ? paramString : String.format(rb.getString("with.weak"), new Object[] { paramString }); }
  
  private String withWeak(PublicKey paramPublicKey) { return DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramPublicKey) ? String.format(rb.getString("key.bit"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() }) : String.format(rb.getString("key.bit.weak"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() }); }
  
  private void printX509Cert(X509Certificate paramX509Certificate, PrintStream paramPrintStream) throws Exception {
    MessageFormat messageFormat = new MessageFormat(rb.getString(".PATTERN.printX509Cert.with.weak"));
    PublicKey publicKey = paramX509Certificate.getPublicKey();
    String str = paramX509Certificate.getSigAlgName();
    if (!isTrustedCert(paramX509Certificate))
      str = withWeak(str); 
    Object[] arrayOfObject = { 
        paramX509Certificate.getSubjectDN().toString(), paramX509Certificate.getIssuerDN().toString(), paramX509Certificate.getSerialNumber().toString(16), paramX509Certificate.getNotBefore().toString(), paramX509Certificate.getNotAfter().toString(), getCertFingerPrint("MD5", paramX509Certificate), getCertFingerPrint("SHA1", paramX509Certificate), getCertFingerPrint("SHA-256", paramX509Certificate), str, withWeak(publicKey), 
        Integer.valueOf(paramX509Certificate.getVersion()) };
    paramPrintStream.println(messageFormat.format(arrayOfObject));
    if (paramX509Certificate instanceof X509CertImpl) {
      X509CertImpl x509CertImpl = (X509CertImpl)paramX509Certificate;
      X509CertInfo x509CertInfo = (X509CertInfo)x509CertImpl.get("x509.info");
      CertificateExtensions certificateExtensions = (CertificateExtensions)x509CertInfo.get("extensions");
      if (certificateExtensions != null)
        printExtensions(rb.getString("Extensions."), certificateExtensions, paramPrintStream); 
    } 
  }
  
  private static void printExtensions(String paramString, CertificateExtensions paramCertificateExtensions, PrintStream paramPrintStream) throws Exception {
    byte b = 0;
    Iterator iterator1 = paramCertificateExtensions.getAllExtensions().iterator();
    Iterator iterator2 = paramCertificateExtensions.getUnparseableExtensions().values().iterator();
    while (iterator1.hasNext() || iterator2.hasNext()) {
      Extension extension = iterator1.hasNext() ? (Extension)iterator1.next() : (Extension)iterator2.next();
      if (!b) {
        paramPrintStream.println();
        paramPrintStream.println(paramString);
        paramPrintStream.println();
      } 
      paramPrintStream.print("#" + ++b + ": " + extension);
      if (extension.getClass() == Extension.class) {
        byte[] arrayOfByte = extension.getExtensionValue();
        if (arrayOfByte.length == 0) {
          paramPrintStream.println(rb.getString(".Empty.value."));
        } else {
          (new HexDumpEncoder()).encodeBuffer(extension.getExtensionValue(), paramPrintStream);
          paramPrintStream.println();
        } 
      } 
      paramPrintStream.println();
    } 
  }
  
  private static Pair<String, Certificate> getSigner(Certificate paramCertificate, KeyStore paramKeyStore) throws Exception {
    if (paramKeyStore.getCertificateAlias(paramCertificate) != null)
      return new Pair("", paramCertificate); 
    Enumeration enumeration = paramKeyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      Certificate certificate = paramKeyStore.getCertificate(str);
      if (certificate != null)
        try {
          paramCertificate.verify(certificate.getPublicKey());
          return new Pair(str, certificate);
        } catch (Exception exception) {} 
    } 
    return null;
  }
  
  private X500Name getX500Name() throws IOException {
    X500Name x500Name;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String str1 = "Unknown";
    String str2 = "Unknown";
    String str3 = "Unknown";
    String str4 = "Unknown";
    String str5 = "Unknown";
    String str6 = "Unknown";
    String str7 = null;
    byte b = 20;
    do {
      if (b-- < 0)
        throw new RuntimeException(rb.getString("Too.many.retries.program.terminated")); 
      str1 = inputString(bufferedReader, rb.getString("What.is.your.first.and.last.name."), str1);
      str2 = inputString(bufferedReader, rb.getString("What.is.the.name.of.your.organizational.unit."), str2);
      str3 = inputString(bufferedReader, rb.getString("What.is.the.name.of.your.organization."), str3);
      str4 = inputString(bufferedReader, rb.getString("What.is.the.name.of.your.City.or.Locality."), str4);
      str5 = inputString(bufferedReader, rb.getString("What.is.the.name.of.your.State.or.Province."), str5);
      str6 = inputString(bufferedReader, rb.getString("What.is.the.two.letter.country.code.for.this.unit."), str6);
      x500Name = new X500Name(str1, str2, str3, str4, str5, str6);
      MessageFormat messageFormat = new MessageFormat(rb.getString("Is.name.correct."));
      Object[] arrayOfObject = { x500Name };
      str7 = inputString(bufferedReader, messageFormat.format(arrayOfObject), rb.getString("no"));
    } while (collator.compare(str7, rb.getString("yes")) != 0 && collator.compare(str7, rb.getString("y")) != 0);
    System.err.println();
    return x500Name;
  }
  
  private String inputString(BufferedReader paramBufferedReader, String paramString1, String paramString2) throws IOException {
    System.err.println(paramString1);
    MessageFormat messageFormat = new MessageFormat(rb.getString(".defaultValue."));
    Object[] arrayOfObject = { paramString2 };
    System.err.print(messageFormat.format(arrayOfObject));
    System.err.flush();
    String str = paramBufferedReader.readLine();
    if (str == null || collator.compare(str, "") == 0)
      str = paramString2; 
    return str;
  }
  
  private void dumpCert(Certificate paramCertificate, PrintStream paramPrintStream) throws IOException, CertificateException {
    if (this.rfc) {
      paramPrintStream.println("-----BEGIN CERTIFICATE-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(paramCertificate.getEncoded()));
      paramPrintStream.println("-----END CERTIFICATE-----");
    } else {
      paramPrintStream.write(paramCertificate.getEncoded());
    } 
  }
  
  private void byte2hex(byte paramByte, StringBuffer paramStringBuffer) {
    char[] arrayOfChar = { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F' };
    byte b1 = (paramByte & 0xF0) >> 4;
    byte b2 = paramByte & 0xF;
    paramStringBuffer.append(arrayOfChar[b1]);
    paramStringBuffer.append(arrayOfChar[b2]);
  }
  
  private String toHexString(byte[] paramArrayOfByte) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = paramArrayOfByte.length;
    for (byte b = 0; b < i; b++) {
      byte2hex(paramArrayOfByte[b], stringBuffer);
      if (b < i - 1)
        stringBuffer.append(":"); 
    } 
    return stringBuffer.toString();
  }
  
  private Pair<Key, char[]> recoverKey(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2) throws Exception {
    Key key = null;
    if (!this.keyStore.containsAlias(paramString)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    if (!this.keyStore.entryInstanceOf(paramString, KeyStore.PrivateKeyEntry.class) && !this.keyStore.entryInstanceOf(paramString, KeyStore.SecretKeyEntry.class)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.has.no.key"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    if (paramArrayOfChar2 == null) {
      try {
        key = this.keyStore.getKey(paramString, paramArrayOfChar1);
        paramArrayOfChar2 = paramArrayOfChar1;
        this.passwords.add(paramArrayOfChar2);
      } catch (UnrecoverableKeyException unrecoverableKeyException) {
        if (!this.token) {
          paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
          key = this.keyStore.getKey(paramString, paramArrayOfChar2);
        } else {
          throw unrecoverableKeyException;
        } 
      } 
    } else {
      key = this.keyStore.getKey(paramString, paramArrayOfChar2);
    } 
    return Pair.of(key, paramArrayOfChar2);
  }
  
  private Pair<KeyStore.Entry, char[]> recoverEntry(KeyStore paramKeyStore, String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2) throws Exception {
    KeyStore.Entry entry;
    if (!paramKeyStore.containsAlias(paramString)) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      entry = new Object[] { paramString };
      throw new Exception(messageFormat.format(entry));
    } 
    KeyStore.ProtectionParameter protectionParameter = null;
    try {
      entry = paramKeyStore.getEntry(paramString, protectionParameter);
      paramArrayOfChar2 = null;
    } catch (UnrecoverableEntryException unrecoverableEntryException) {
      if ("PKCS11".equalsIgnoreCase(paramKeyStore.getType()) || KeyStoreUtil.isWindowsKeyStore(paramKeyStore.getType()))
        throw unrecoverableEntryException; 
      if (paramArrayOfChar2 != null) {
        protectionParameter = new KeyStore.PasswordProtection(paramArrayOfChar2);
        entry = paramKeyStore.getEntry(paramString, protectionParameter);
      } else {
        try {
          protectionParameter = new KeyStore.PasswordProtection(paramArrayOfChar1);
          entry = paramKeyStore.getEntry(paramString, protectionParameter);
          paramArrayOfChar2 = paramArrayOfChar1;
        } catch (UnrecoverableEntryException unrecoverableEntryException1) {
          if ("PKCS12".equalsIgnoreCase(paramKeyStore.getType()))
            throw unrecoverableEntryException1; 
          paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
          protectionParameter = new KeyStore.PasswordProtection(paramArrayOfChar2);
          entry = paramKeyStore.getEntry(paramString, protectionParameter);
        } 
      } 
    } 
    return Pair.of(entry, paramArrayOfChar2);
  }
  
  private String getCertFingerPrint(String paramString, Certificate paramCertificate) throws Exception {
    byte[] arrayOfByte1 = paramCertificate.getEncoded();
    MessageDigest messageDigest = MessageDigest.getInstance(paramString);
    byte[] arrayOfByte2 = messageDigest.digest(arrayOfByte1);
    return toHexString(arrayOfByte2);
  }
  
  private void printNoIntegrityWarning() {
    System.err.println();
    System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
    System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.your.keystore."));
    System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
    System.err.println();
  }
  
  private Certificate[] validateReply(String paramString, Certificate paramCertificate, Certificate[] paramArrayOfCertificate) throws Exception {
    checkWeak(rb.getString("reply"), paramArrayOfCertificate);
    PublicKey publicKey = paramCertificate.getPublicKey();
    byte b;
    for (b = 0; b < paramArrayOfCertificate.length && !publicKey.equals(paramArrayOfCertificate[b].getPublicKey()); b++);
    if (b == paramArrayOfCertificate.length) {
      MessageFormat messageFormat = new MessageFormat(rb.getString("Certificate.reply.does.not.contain.public.key.for.alias."));
      Object[] arrayOfObject = { paramString };
      throw new Exception(messageFormat.format(arrayOfObject));
    } 
    Certificate certificate1 = paramArrayOfCertificate[0];
    paramArrayOfCertificate[0] = paramArrayOfCertificate[b];
    paramArrayOfCertificate[b] = certificate1;
    X509Certificate x509Certificate = (X509Certificate)paramArrayOfCertificate[0];
    for (b = 1; b < paramArrayOfCertificate.length - 1; b++) {
      byte b1;
      for (b1 = b; b1 < paramArrayOfCertificate.length; b1++) {
        if (KeyStoreUtil.signedBy(x509Certificate, (X509Certificate)paramArrayOfCertificate[b1])) {
          certificate1 = paramArrayOfCertificate[b];
          paramArrayOfCertificate[b] = paramArrayOfCertificate[b1];
          paramArrayOfCertificate[b1] = certificate1;
          x509Certificate = (X509Certificate)paramArrayOfCertificate[b];
          break;
        } 
      } 
      if (b1 == paramArrayOfCertificate.length)
        throw new Exception(rb.getString("Incomplete.certificate.chain.in.reply")); 
    } 
    if (this.noprompt)
      return paramArrayOfCertificate; 
    Certificate certificate2 = paramArrayOfCertificate[paramArrayOfCertificate.length - 1];
    boolean bool = true;
    Pair pair = getSigner(certificate2, this.keyStore);
    if (pair == null && this.trustcacerts && this.caks != null) {
      pair = getSigner(certificate2, this.caks);
      bool = false;
    } 
    if (pair == null) {
      System.err.println();
      System.err.println(rb.getString("Top.level.certificate.in.reply."));
      printX509Cert((X509Certificate)certificate2, System.out);
      System.err.println();
      System.err.print(rb.getString(".is.not.trusted."));
      printWeakWarnings(true);
      String str = getYesNoReply(rb.getString("Install.reply.anyway.no."));
      if ("NO".equals(str))
        return null; 
    } else if (pair.snd != certificate2) {
      Certificate[] arrayOfCertificate = new Certificate[paramArrayOfCertificate.length + 1];
      System.arraycopy(paramArrayOfCertificate, 0, arrayOfCertificate, 0, paramArrayOfCertificate.length);
      arrayOfCertificate[arrayOfCertificate.length - 1] = (Certificate)pair.snd;
      paramArrayOfCertificate = arrayOfCertificate;
      checkWeak(String.format(rb.getString(bool ? "alias.in.keystore" : "alias.in.cacerts"), new Object[] { pair.fst }), (Certificate)pair.snd);
    } 
    return paramArrayOfCertificate;
  }
  
  private Certificate[] establishCertChain(Certificate paramCertificate1, Certificate paramCertificate2) throws Exception {
    if (paramCertificate1 != null) {
      PublicKey publicKey1 = paramCertificate1.getPublicKey();
      PublicKey publicKey2 = paramCertificate2.getPublicKey();
      if (!publicKey1.equals(publicKey2))
        throw new Exception(rb.getString("Public.keys.in.reply.and.keystore.don.t.match")); 
      if (paramCertificate2.equals(paramCertificate1))
        throw new Exception(rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical")); 
    } 
    Hashtable hashtable = null;
    if (this.keyStore.size() > 0) {
      hashtable = new Hashtable(11);
      keystorecerts2Hashtable(this.keyStore, hashtable);
    } 
    if (this.trustcacerts && this.caks != null && this.caks.size() > 0) {
      if (hashtable == null)
        hashtable = new Hashtable(11); 
      keystorecerts2Hashtable(this.caks, hashtable);
    } 
    Vector vector = new Vector(2);
    if (buildChain(new Pair(rb.getString("the.input"), (X509Certificate)paramCertificate2), vector, hashtable)) {
      for (Pair pair : vector)
        checkWeak((String)pair.fst, (Certificate)pair.snd); 
      Certificate[] arrayOfCertificate = new Certificate[vector.size()];
      byte b = 0;
      for (int i = vector.size() - 1; i >= 0; i--) {
        arrayOfCertificate[b] = (Certificate)((Pair)vector.elementAt(i)).snd;
        b++;
      } 
      return arrayOfCertificate;
    } 
    throw new Exception(rb.getString("Failed.to.establish.chain.from.reply"));
  }
  
  private boolean buildChain(Pair<String, X509Certificate> paramPair, Vector<Pair<String, X509Certificate>> paramVector, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable) {
    if (KeyStoreUtil.isSelfSigned((X509Certificate)paramPair.snd)) {
      paramVector.addElement(paramPair);
      return true;
    } 
    Principal principal = ((X509Certificate)paramPair.snd).getIssuerDN();
    Vector vector = (Vector)paramHashtable.get(principal);
    if (vector == null)
      return false; 
    Enumeration enumeration = vector.elements();
    while (enumeration.hasMoreElements()) {
      Pair pair = (Pair)enumeration.nextElement();
      PublicKey publicKey = ((X509Certificate)pair.snd).getPublicKey();
      try {
        ((X509Certificate)paramPair.snd).verify(publicKey);
      } catch (Exception exception) {
        continue;
      } 
      if (buildChain(pair, paramVector, paramHashtable)) {
        paramVector.addElement(paramPair);
        return true;
      } 
    } 
    return false;
  }
  
  private String getYesNoReply(String paramString) throws Exception {
    String str = null;
    byte b = 20;
    do {
      if (b-- < 0)
        throw new RuntimeException(rb.getString("Too.many.retries.program.terminated")); 
      System.err.print(paramString);
      System.err.flush();
      str = (new BufferedReader(new InputStreamReader(System.in))).readLine();
      if (collator.compare(str, "") == 0 || collator.compare(str, rb.getString("n")) == 0 || collator.compare(str, rb.getString("no")) == 0) {
        str = "NO";
      } else if (collator.compare(str, rb.getString("y")) == 0 || collator.compare(str, rb.getString("yes")) == 0) {
        str = "YES";
      } else {
        System.err.println(rb.getString("Wrong.answer.try.again"));
        str = null;
      } 
    } while (str == null);
    return str;
  }
  
  private void keystorecerts2Hashtable(KeyStore paramKeyStore, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable) throws Exception {
    Enumeration enumeration = paramKeyStore.aliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      Certificate certificate = paramKeyStore.getCertificate(str);
      if (certificate != null) {
        Principal principal = ((X509Certificate)certificate).getSubjectDN();
        Pair pair = new Pair(String.format(rb.getString((paramKeyStore == this.caks) ? "alias.in.cacerts" : "alias.in.keystore"), new Object[] { str }), (X509Certificate)certificate);
        Vector vector = (Vector)paramHashtable.get(principal);
        if (vector == null) {
          vector = new Vector();
          vector.addElement(pair);
        } else if (!vector.contains(pair)) {
          vector.addElement(pair);
        } 
        paramHashtable.put(principal, vector);
      } 
    } 
  }
  
  private static Date getStartDate(String paramString) throws IOException {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    if (paramString != null) {
      IOException iOException = new IOException(rb.getString("Illegal.startdate.value"));
      int i = paramString.length();
      if (i == 0)
        throw iOException; 
      if (paramString.charAt(0) == '-' || paramString.charAt(0) == '+') {
        byte b;
        for (b = 0; b < i; b = b1 + 1) {
          int j = 0;
          switch (paramString.charAt(b)) {
            case '+':
              j = 1;
              break;
            case '-':
              j = -1;
              break;
            default:
              throw iOException;
          } 
          byte b1;
          for (b1 = b + 1; b1 < i; b1++) {
            char c = paramString.charAt(b1);
            if (c < '0' || c > '9')
              break; 
          } 
          if (b1 == b + 1)
            throw iOException; 
          int k = Integer.parseInt(paramString.substring(b + 1, b1));
          if (b1 >= i)
            throw iOException; 
          byte b2 = 0;
          switch (paramString.charAt(b1)) {
            case 'y':
              b2 = 1;
              break;
            case 'm':
              b2 = 2;
              break;
            case 'd':
              b2 = 5;
              break;
            case 'H':
              b2 = 10;
              break;
            case 'M':
              b2 = 12;
              break;
            case 'S':
              b2 = 13;
              break;
            default:
              throw iOException;
          } 
          gregorianCalendar.add(b2, j * k);
        } 
      } else {
        String str1 = null;
        String str2 = null;
        if (i == 19) {
          str1 = paramString.substring(0, 10);
          str2 = paramString.substring(11);
          if (paramString.charAt(10) != ' ')
            throw iOException; 
        } else if (i == 10) {
          str1 = paramString;
        } else if (i == 8) {
          str2 = paramString;
        } else {
          throw iOException;
        } 
        if (str1 != null)
          if (str1.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
            gregorianCalendar.set(Integer.valueOf(str1.substring(0, 4)).intValue(), Integer.valueOf(str1.substring(5, 7)).intValue() - 1, Integer.valueOf(str1.substring(8, 10)).intValue());
          } else {
            throw iOException;
          }  
        if (str2 != null)
          if (str2.matches("\\d\\d:\\d\\d:\\d\\d")) {
            gregorianCalendar.set(11, Integer.valueOf(str2.substring(0, 2)).intValue());
            gregorianCalendar.set(12, Integer.valueOf(str2.substring(0, 2)).intValue());
            gregorianCalendar.set(13, Integer.valueOf(str2.substring(0, 2)).intValue());
            gregorianCalendar.set(14, 0);
          } else {
            throw iOException;
          }  
      } 
    } 
    return gregorianCalendar.getTime();
  }
  
  private static int oneOf(String paramString, String... paramVarArgs) throws Exception {
    int[] arrayOfInt = new int[paramVarArgs.length];
    byte b1 = 0;
    int i = Integer.MAX_VALUE;
    for (int j = 0; j < paramVarArgs.length; j++) {
      String str = paramVarArgs[j];
      if (str == null) {
        i = j;
      } else if (str.toLowerCase(Locale.ENGLISH).startsWith(paramString.toLowerCase(Locale.ENGLISH))) {
        arrayOfInt[b1++] = j;
      } else {
        StringBuffer stringBuffer1 = new StringBuffer();
        boolean bool = true;
        for (char c : str.toCharArray()) {
          if (bool) {
            stringBuffer1.append(c);
            bool = false;
          } else if (!Character.isLowerCase(c)) {
            stringBuffer1.append(c);
          } 
        } 
        if (stringBuffer1.toString().equalsIgnoreCase(paramString))
          arrayOfInt[b1++] = j; 
      } 
    } 
    if (b1 == 0)
      return -1; 
    if (b1 == 1)
      return arrayOfInt[0]; 
    if (arrayOfInt[1] > i)
      return arrayOfInt[0]; 
    StringBuffer stringBuffer = new StringBuffer();
    MessageFormat messageFormat = new MessageFormat(rb.getString("command.{0}.is.ambiguous."));
    Object[] arrayOfObject = { paramString };
    stringBuffer.append(messageFormat.format(arrayOfObject));
    stringBuffer.append("\n    ");
    for (byte b2 = 0; b2 < b1 && arrayOfInt[b2] < i; b2++) {
      stringBuffer.append(' ');
      stringBuffer.append(paramVarArgs[arrayOfInt[b2]]);
    } 
    throw new Exception(stringBuffer.toString());
  }
  
  private GeneralName createGeneralName(String paramString1, String paramString2) throws Exception {
    DNSName dNSName;
    URIName uRIName;
    IPAddressName iPAddressName;
    RFC822Name rFC822Name;
    int i = oneOf(paramString1, new String[] { "EMAIL", "URI", "DNS", "IP", "OID" });
    if (i < 0)
      throw new Exception(rb.getString("Unrecognized.GeneralName.type.") + paramString1); 
    switch (i) {
      case 0:
        rFC822Name = new RFC822Name(paramString2);
        return new GeneralName(rFC822Name);
      case 1:
        uRIName = new URIName(paramString2);
        return new GeneralName(uRIName);
      case 2:
        dNSName = new DNSName(paramString2);
        return new GeneralName(dNSName);
      case 3:
        iPAddressName = new IPAddressName(paramString2);
        return new GeneralName(iPAddressName);
    } 
    OIDName oIDName = new OIDName(paramString2);
    return new GeneralName(oIDName);
  }
  
  private ObjectIdentifier findOidForExtName(String paramString) throws Exception {
    switch (oneOf(paramString, extSupported)) {
      case 0:
        return PKIXExtensions.BasicConstraints_Id;
      case 1:
        return PKIXExtensions.KeyUsage_Id;
      case 2:
        return PKIXExtensions.ExtendedKeyUsage_Id;
      case 3:
        return PKIXExtensions.SubjectAlternativeName_Id;
      case 4:
        return PKIXExtensions.IssuerAlternativeName_Id;
      case 5:
        return PKIXExtensions.SubjectInfoAccess_Id;
      case 6:
        return PKIXExtensions.AuthInfoAccess_Id;
      case 8:
        return PKIXExtensions.CRLDistributionPoints_Id;
    } 
    return new ObjectIdentifier(paramString);
  }
  
  private CertificateExtensions createV3Extensions(CertificateExtensions paramCertificateExtensions1, CertificateExtensions paramCertificateExtensions2, List<String> paramList, PublicKey paramPublicKey1, PublicKey paramPublicKey2) throws Exception {
    if (paramCertificateExtensions2 != null && paramCertificateExtensions1 != null)
      throw new Exception("One of request and original should be null."); 
    if (paramCertificateExtensions2 == null)
      paramCertificateExtensions2 = new CertificateExtensions(); 
    try {
      if (paramCertificateExtensions1 != null)
        for (String str : paramList) {
          if (str.toLowerCase(Locale.ENGLISH).startsWith("honored=")) {
            List list = Arrays.asList(str.toLowerCase(Locale.ENGLISH).substring(8).split(","));
            if (list.contains("all"))
              paramCertificateExtensions2 = paramCertificateExtensions1; 
            for (String str1 : list) {
              if (str1.equals("all"))
                continue; 
              boolean bool = true;
              int i = -1;
              String str2 = null;
              if (str1.startsWith("-")) {
                bool = false;
                str2 = str1.substring(1);
              } else {
                int j = str1.indexOf(':');
                if (j >= 0) {
                  str2 = str1.substring(0, j);
                  i = oneOf(str1.substring(j + 1), new String[] { "critical", "non-critical" });
                  if (i == -1)
                    throw new Exception(rb.getString("Illegal.value.") + str1); 
                } 
              } 
              String str3 = paramCertificateExtensions1.getNameByOid(findOidForExtName(str2));
              if (bool) {
                Extension extension = paramCertificateExtensions1.get(str3);
                if ((!extension.isCritical() && i == 0) || (extension.isCritical() && i == 1)) {
                  extension = Extension.newExtension(extension.getExtensionId(), !extension.isCritical(), extension.getExtensionValue());
                  paramCertificateExtensions2.set(str3, extension);
                } 
                continue;
              } 
              paramCertificateExtensions2.delete(str3);
            } 
            break;
          } 
        }  
      for (String str1 : paramList) {
        byte[] arrayOfByte;
        ObjectIdentifier objectIdentifier;
        boolean bool2;
        int m;
        String str3;
        String str2;
        boolean bool1 = false;
        int i = str1.indexOf('=');
        if (i >= 0) {
          str2 = str1.substring(0, i);
          str3 = str1.substring(i + 1);
        } else {
          str2 = str1;
          str3 = null;
        } 
        int j = str2.indexOf(':');
        if (j >= 0) {
          if (oneOf(str2.substring(j + 1), new String[] { "critical" }) == 0)
            bool1 = true; 
          str2 = str2.substring(0, j);
        } 
        if (str2.equalsIgnoreCase("honored"))
          continue; 
        int k = oneOf(str2, extSupported);
        switch (k) {
          case 0:
            m = -1;
            bool2 = false;
            if (str3 == null) {
              bool2 = true;
            } else {
              try {
                m = Integer.parseInt(str3);
                bool2 = true;
              } catch (NumberFormatException numberFormatException) {
                for (String str : str3.split(",")) {
                  String[] arrayOfString = str.split(":");
                  if (arrayOfString.length != 2)
                    throw new Exception(rb.getString("Illegal.value.") + str1); 
                  if (arrayOfString[0].equalsIgnoreCase("ca")) {
                    bool2 = Boolean.parseBoolean(arrayOfString[1]);
                  } else if (arrayOfString[0].equalsIgnoreCase("pathlen")) {
                    m = Integer.parseInt(arrayOfString[1]);
                  } else {
                    throw new Exception(rb.getString("Illegal.value.") + str1);
                  } 
                } 
              } 
            } 
            paramCertificateExtensions2.set("BasicConstraints", new BasicConstraintsExtension(Boolean.valueOf(bool1), bool2, m));
            continue;
          case 1:
            if (str3 != null) {
              boolean[] arrayOfBoolean = new boolean[9];
              for (String str : str3.split(",")) {
                int n = oneOf(str, new String[] { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment" });
                if (n < 0)
                  throw new Exception(rb.getString("Unknown.keyUsage.type.") + str); 
                if (n == 9)
                  n = 1; 
                arrayOfBoolean[n] = true;
              } 
              KeyUsageExtension keyUsageExtension = new KeyUsageExtension(arrayOfBoolean);
              paramCertificateExtensions2.set("KeyUsage", Extension.newExtension(keyUsageExtension.getExtensionId(), bool1, keyUsageExtension.getExtensionValue()));
              continue;
            } 
            throw new Exception(rb.getString("Illegal.value.") + str1);
          case 2:
            if (str3 != null) {
              Vector vector = new Vector();
              for (String str : str3.split(",")) {
                int n = oneOf(str, new String[] { "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning" });
                if (n < 0) {
                  try {
                    vector.add(new ObjectIdentifier(str));
                  } catch (Exception exception) {
                    throw new Exception(rb.getString("Unknown.extendedkeyUsage.type.") + str);
                  } 
                } else if (n == 0) {
                  vector.add(new ObjectIdentifier("2.5.29.37.0"));
                } else {
                  vector.add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + n));
                } 
              } 
              paramCertificateExtensions2.set("ExtendedKeyUsage", new ExtendedKeyUsageExtension(Boolean.valueOf(bool1), vector));
              continue;
            } 
            throw new Exception(rb.getString("Illegal.value.") + str1);
          case 3:
          case 4:
            if (str3 != null) {
              String[] arrayOfString = str3.split(",");
              GeneralNames generalNames = new GeneralNames();
              for (String str4 : arrayOfString) {
                j = str4.indexOf(':');
                if (j < 0)
                  throw new Exception("Illegal item " + str4 + " in " + str1); 
                String str5 = str4.substring(0, j);
                String str6 = str4.substring(j + 1);
                generalNames.add(createGeneralName(str5, str6));
              } 
              if (k == 3) {
                paramCertificateExtensions2.set("SubjectAlternativeName", new SubjectAlternativeNameExtension(Boolean.valueOf(bool1), generalNames));
                continue;
              } 
              paramCertificateExtensions2.set("IssuerAlternativeName", new IssuerAlternativeNameExtension(Boolean.valueOf(bool1), generalNames));
              continue;
            } 
            throw new Exception(rb.getString("Illegal.value.") + str1);
          case 5:
          case 6:
            if (bool1)
              throw new Exception(rb.getString("This.extension.cannot.be.marked.as.critical.") + str1); 
            if (str3 != null) {
              ArrayList arrayList = new ArrayList();
              String[] arrayOfString = str3.split(",");
              for (String str4 : arrayOfString) {
                ObjectIdentifier objectIdentifier1;
                j = str4.indexOf(':');
                int n = str4.indexOf(':', j + 1);
                if (j < 0 || n < 0)
                  throw new Exception(rb.getString("Illegal.value.") + str1); 
                String str5 = str4.substring(0, j);
                String str6 = str4.substring(j + 1, n);
                String str7 = str4.substring(n + 1);
                int i1 = oneOf(str5, new String[] { "", "ocsp", "caIssuers", "timeStamping", "", "caRepository" });
                if (i1 < 0) {
                  try {
                    objectIdentifier1 = new ObjectIdentifier(str5);
                  } catch (Exception exception) {
                    throw new Exception(rb.getString("Unknown.AccessDescription.type.") + str5);
                  } 
                } else {
                  objectIdentifier1 = new ObjectIdentifier("1.3.6.1.5.5.7.48." + i1);
                } 
                arrayList.add(new AccessDescription(objectIdentifier1, createGeneralName(str6, str7)));
              } 
              if (k == 5) {
                paramCertificateExtensions2.set("SubjectInfoAccess", new SubjectInfoAccessExtension(arrayList));
                continue;
              } 
              paramCertificateExtensions2.set("AuthorityInfoAccess", new AuthorityInfoAccessExtension(arrayList));
              continue;
            } 
            throw new Exception(rb.getString("Illegal.value.") + str1);
          case 8:
            if (str3 != null) {
              String[] arrayOfString = str3.split(",");
              GeneralNames generalNames = new GeneralNames();
              for (String str4 : arrayOfString) {
                j = str4.indexOf(':');
                if (j < 0)
                  throw new Exception("Illegal item " + str4 + " in " + str1); 
                String str5 = str4.substring(0, j);
                String str6 = str4.substring(j + 1);
                generalNames.add(createGeneralName(str5, str6));
              } 
              paramCertificateExtensions2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(bool1, Collections.singletonList(new DistributionPoint(generalNames, null, null))));
              continue;
            } 
            throw new Exception(rb.getString("Illegal.value.") + str1);
          case -1:
            objectIdentifier = new ObjectIdentifier(str2);
            arrayOfByte = null;
            if (str3 != null) {
              arrayOfByte = new byte[str3.length() / 2 + 1];
              byte b = 0;
              for (char c1 : str3.toCharArray()) {
                char c2;
                if (c1 >= '0' && c1 <= '9') {
                  c2 = c1 - '0';
                } else if (c1 >= 'A' && c1 <= 'F') {
                  c2 = c1 - 'A' + '\n';
                } else if (c1 >= 'a' && c1 <= 'f') {
                  c2 = c1 - 'a' + '\n';
                } else {
                  continue;
                } 
                if (b % 2 == 0) {
                  arrayOfByte[b / 2] = (byte)(c2 << '\004');
                } else {
                  arrayOfByte[b / 2] = (byte)(arrayOfByte[b / 2] + c2);
                } 
                b++;
                continue;
              } 
              if (b % 2 != 0)
                throw new Exception(rb.getString("Odd.number.of.hex.digits.found.") + str1); 
              arrayOfByte = Arrays.copyOf(arrayOfByte, b / 2);
            } else {
              arrayOfByte = new byte[0];
            } 
            paramCertificateExtensions2.set(objectIdentifier.toString(), new Extension(objectIdentifier, bool1, (new DerValue((byte)4, arrayOfByte)).toByteArray()));
            continue;
        } 
        throw new Exception(rb.getString("Unknown.extension.type.") + str1);
      } 
      paramCertificateExtensions2.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension((new KeyIdentifier(paramPublicKey1)).getIdentifier()));
      if (paramPublicKey2 != null && !paramPublicKey1.equals(paramPublicKey2))
        paramCertificateExtensions2.set("AuthorityKeyIdentifier", new AuthorityKeyIdentifierExtension(new KeyIdentifier(paramPublicKey2), null, null)); 
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    return paramCertificateExtensions2;
  }
  
  private boolean isTrustedCert(Certificate paramCertificate) throws KeyStoreException {
    if (this.caks != null && this.caks.getCertificateAlias(paramCertificate) != null)
      return true; 
    String str = this.keyStore.getCertificateAlias(paramCertificate);
    return (str != null && this.keyStore.isCertificateEntry(str));
  }
  
  private void checkWeak(String paramString1, String paramString2, Key paramKey) {
    if (paramString2 != null && !DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString2, null))
      this.weakWarnings.add(String.format(rb.getString("whose.sigalg.risk"), new Object[] { paramString1, paramString2 })); 
    if (paramKey != null && !DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramKey))
      this.weakWarnings.add(String.format(rb.getString("whose.key.risk"), new Object[] { paramString1, String.format(rb.getString("key.bit"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramKey)), paramKey.getAlgorithm() }) })); 
  }
  
  private void checkWeak(String paramString, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    for (byte b = 0; b < paramArrayOfCertificate.length; b++) {
      Certificate certificate = paramArrayOfCertificate[b];
      if (certificate instanceof X509Certificate) {
        X509Certificate x509Certificate = (X509Certificate)certificate;
        String str = paramString;
        if (paramArrayOfCertificate.length > 1)
          str = oneInMany(paramString, b, paramArrayOfCertificate.length); 
        checkWeak(str, x509Certificate);
      } 
    } 
  }
  
  private void checkWeak(String paramString, Certificate paramCertificate) throws KeyStoreException {
    if (paramCertificate instanceof X509Certificate) {
      X509Certificate x509Certificate = (X509Certificate)paramCertificate;
      String str = isTrustedCert(paramCertificate) ? null : x509Certificate.getSigAlgName();
      checkWeak(paramString, str, x509Certificate.getPublicKey());
    } 
  }
  
  private void checkWeak(String paramString, PKCS10 paramPKCS10) { checkWeak(paramString, paramPKCS10.getSigAlg(), paramPKCS10.getSubjectPublicKeyInfo()); }
  
  private void checkWeak(String paramString, CRL paramCRL, Key paramKey) {
    if (paramCRL instanceof X509CRLImpl) {
      X509CRLImpl x509CRLImpl = (X509CRLImpl)paramCRL;
      checkWeak(paramString, x509CRLImpl.getSigAlgName(), paramKey);
    } 
  }
  
  private void printWeakWarnings(boolean paramBoolean) {
    if (!this.weakWarnings.isEmpty() && !this.nowarn) {
      System.err.println("\nWarning:");
      for (String str : this.weakWarnings)
        System.err.println(str); 
      if (paramBoolean)
        System.err.println(); 
    } 
    this.weakWarnings.clear();
  }
  
  private void usage() {
    if (this.command != null) {
      System.err.println("keytool " + this.command + rb.getString(".OPTION."));
      System.err.println();
      System.err.println(rb.getString(this.command.description));
      System.err.println();
      System.err.println(rb.getString("Options."));
      System.err.println();
      String[] arrayOfString1 = new String[this.command.options.length];
      String[] arrayOfString2 = new String[this.command.options.length];
      boolean bool = false;
      int i = 0;
      byte b;
      for (b = 0; b < arrayOfString1.length; b++) {
        Option option = this.command.options[b];
        arrayOfString1[b] = option.toString();
        if (option.arg != null)
          arrayOfString1[b] = arrayOfString1[b] + " " + option.arg; 
        if (arrayOfString1[b].length() > i)
          i = arrayOfString1[b].length(); 
        arrayOfString2[b] = rb.getString(option.description);
      } 
      for (b = 0; b < arrayOfString1.length; b++) {
        System.err.printf(" %-" + i + "s  %s\n", new Object[] { arrayOfString1[b], arrayOfString2[b] });
      } 
      System.err.println();
      System.err.println(rb.getString("Use.keytool.help.for.all.available.commands"));
    } else {
      System.err.println(rb.getString("Key.and.Certificate.Management.Tool"));
      System.err.println();
      System.err.println(rb.getString("Commands."));
      System.err.println();
      for (Command command1 : Command.values()) {
        if (command1 == Command.KEYCLONE)
          break; 
        System.err.printf(" %-20s%s\n", new Object[] { command1, rb.getString(command1.description) });
      } 
      System.err.println();
      System.err.println(rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
    } 
  }
  
  private void tinyHelp() {
    usage();
    if (this.debug)
      throw new RuntimeException("NO BIG ERROR, SORRY"); 
    System.exit(1);
  }
  
  private void errorNeedArgument(String paramString) throws Exception {
    Object[] arrayOfObject = { paramString };
    System.err.println((new MessageFormat(rb.getString("Command.option.flag.needs.an.argument."))).format(arrayOfObject));
    tinyHelp();
  }
  
  private char[] getPass(String paramString1, String paramString2) {
    char[] arrayOfChar = KeyStoreUtil.getPassWithModifier(paramString1, paramString2, rb);
    if (arrayOfChar != null)
      return arrayOfChar; 
    tinyHelp();
    return null;
  }
  
  static  {
    collator.setStrength(0);
    extSupported = new String[] { "BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints" };
  }
  
  enum Command {
    CERTREQ("Generates.a.certificate.request", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.FILEOUT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.DNAME, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, 
        Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    CHANGEALIAS("Changes.an.entry.s.alias", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, 
        Main.Option.V, Main.Option.PROTECTED }),
    DELETE("Deletes.an.entry", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    EXPORTCERT("Exports.certificate", new Main.Option[] { 
        Main.Option.RFC, Main.Option.ALIAS, Main.Option.FILEOUT, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, 
        Main.Option.V, Main.Option.PROTECTED }),
    GENKEYPAIR("Generates.a.key.pair", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.SIGALG, Main.Option.DESTALIAS, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, 
        Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    GENSECKEY("Generates.a.secret.key", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, 
        Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    GENCERT("Generates.certificate.from.a.certificate.request", new Main.Option[] { 
        Main.Option.RFC, Main.Option.INFILE, Main.Option.OUTFILE, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, 
        Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new Main.Option[] { 
        Main.Option.NOPROMPT, Main.Option.TRUSTCACERTS, Main.Option.PROTECTED, Main.Option.ALIAS, Main.Option.FILEIN, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, 
        Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),
    IMPORTPASS("Imports.a.password", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, 
        Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new Main.Option[] { 
        Main.Option.SRCKEYSTORE, Main.Option.DESTKEYSTORE, Main.Option.SRCSTORETYPE, Main.Option.DESTSTORETYPE, Main.Option.SRCSTOREPASS, Main.Option.DESTSTOREPASS, Main.Option.SRCPROTECTED, Main.Option.SRCPROVIDERNAME, Main.Option.DESTPROVIDERNAME, Main.Option.SRCALIAS, 
        Main.Option.DESTALIAS, Main.Option.SRCKEYPASS, Main.Option.DESTKEYPASS, Main.Option.NOPROMPT, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),
    KEYPASSWD("Changes.the.key.password.of.an.entry", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, 
        Main.Option.V }),
    LIST("Lists.entries.in.a.keystore", new Main.Option[] { 
        Main.Option.RFC, Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, 
        Main.Option.PROTECTED }),
    PRINTCERT("Prints.the.content.of.a.certificate", new Main.Option[] { Main.Option.RFC, Main.Option.FILEIN, Main.Option.SSLSERVER, Main.Option.JARFILE, Main.Option.V }),
    PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new Main.Option[] { Main.Option.FILEIN, Main.Option.V }),
    PRINTCRL("Prints.the.content.of.a.CRL.file", new Main.Option[] { Main.Option.FILEIN, Main.Option.V }),
    STOREPASSWD("Changes.the.store.password.of.a.keystore", new Main.Option[] { Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),
    KEYCLONE("Clones.a.key.entry", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, 
        Main.Option.PROVIDERPATH, Main.Option.V }),
    SELFCERT("Generates.a.self.signed.certificate", new Main.Option[] { 
        Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, 
        Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),
    GENCRL("Generates.CRL", new Main.Option[] { 
        Main.Option.RFC, Main.Option.FILEOUT, Main.Option.ID, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.EXT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, 
        Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),
    IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new Main.Option[] { Main.Option.FILEIN, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V });
    
    final String description;
    
    final Main.Option[] options;
    
    Command(Main.Option[] param1ArrayOfOption1, Main.Option... param1VarArgs1) {
      this.description = param1ArrayOfOption1;
      this.options = param1VarArgs1;
    }
    
    public String toString() { return "-" + name().toLowerCase(Locale.ENGLISH); }
  }
  
  enum Option {
    ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"),
    DESTALIAS("destalias", "<destalias>", "destination.alias"),
    DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"),
    DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"),
    DESTPROTECTED("destprotected", null, "destination.keystore.password.protected"),
    DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"),
    DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"),
    DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"),
    DNAME("dname", "<dname>", "distinguished.name"),
    EXT("ext", "<value>", "X.509.extension"),
    FILEOUT("file", "<filename>", "output.file.name"),
    FILEIN("file", "<filename>", "input.file.name"),
    ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"),
    INFILE("infile", "<filename>", "input.file.name"),
    KEYALG("keyalg", "<keyalg>", "key.algorithm.name"),
    KEYPASS("keypass", "<arg>", "key.password"),
    KEYSIZE("keysize", "<keysize>", "key.bit.size"),
    KEYSTORE("keystore", "<keystore>", "keystore.name"),
    NEW("new", "<arg>", "new.password"),
    NOPROMPT("noprompt", null, "do.not.prompt"),
    OUTFILE("outfile", "<filename>", "output.file.name"),
    PROTECTED("protected", null, "password.through.protected.mechanism"),
    PROVIDERARG("providerarg", "<arg>", "provider.argument"),
    PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"),
    PROVIDERNAME("providername", "<providername>", "provider.name"),
    PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"),
    RFC("rfc", null, "output.in.RFC.style"),
    SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"),
    SRCALIAS("srcalias", "<srcalias>", "source.alias"),
    SRCKEYPASS("srckeypass", "<arg>", "source.key.password"),
    SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"),
    SRCPROTECTED("srcprotected", null, "source.keystore.password.protected"),
    SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"),
    SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"),
    SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"),
    SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"),
    JARFILE("jarfile", "<filename>", "signed.jar.file"),
    STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"),
    STOREPASS("storepass", "<arg>", "keystore.password"),
    STORETYPE("storetype", "<storetype>", "keystore.type"),
    TRUSTCACERTS("trustcacerts", null, "trust.certificates.from.cacerts"),
    V("v", null, "verbose.output"),
    VALIDITY("validity", "<valDays>", "validity.number.of.days");
    
    final String name;
    
    final String arg;
    
    final String description;
    
    Option(String param1String1, String param1String2, String param1String3) throws Exception {
      this.name = param1String1;
      this.arg = param1String2;
      this.description = param1String3;
    }
    
    public String toString() { return "-" + this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\keytool\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */