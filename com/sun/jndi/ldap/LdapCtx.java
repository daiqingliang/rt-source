package com.sun.jndi.ldap;

import com.sun.jndi.ldap.ext.StartTlsResponseImpl;
import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import com.sun.jndi.toolkit.dir.SearchFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.PartialResultException;
import javax.naming.ServiceUnavailableException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.naming.spi.DirectoryManager;

public final class LdapCtx extends ComponentDirContext implements EventDirContext, LdapContext {
  private static final boolean debug = false;
  
  private static final boolean HARD_CLOSE = true;
  
  private static final boolean SOFT_CLOSE = false;
  
  public static final int DEFAULT_PORT = 389;
  
  public static final int DEFAULT_SSL_PORT = 636;
  
  public static final String DEFAULT_HOST = "localhost";
  
  private static final boolean DEFAULT_DELETE_RDN = true;
  
  private static final boolean DEFAULT_TYPES_ONLY = false;
  
  private static final int DEFAULT_DEREF_ALIASES = 3;
  
  private static final int DEFAULT_LDAP_VERSION = 32;
  
  private static final int DEFAULT_BATCH_SIZE = 1;
  
  private static final int DEFAULT_REFERRAL_MODE = 3;
  
  private static final char DEFAULT_REF_SEPARATOR = '#';
  
  static final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  
  private static final int DEFAULT_REFERRAL_LIMIT = 10;
  
  private static final String STARTTLS_REQ_OID = "1.3.6.1.4.1.1466.20037";
  
  private static final String[] SCHEMA_ATTRIBUTES = { "objectClasses", "attributeTypes", "matchingRules", "ldapSyntaxes" };
  
  private static final String VERSION = "java.naming.ldap.version";
  
  private static final String BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
  
  private static final String DELETE_RDN = "java.naming.ldap.deleteRDN";
  
  private static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
  
  private static final String TYPES_ONLY = "java.naming.ldap.typesOnly";
  
  private static final String REF_SEPARATOR = "java.naming.ldap.ref.separator";
  
  private static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";
  
  static final String BIND_CONTROLS = "java.naming.ldap.control.connect";
  
  private static final String REFERRAL_LIMIT = "java.naming.ldap.referral.limit";
  
  private static final String TRACE_BER = "com.sun.jndi.ldap.trace.ber";
  
  private static final String NETSCAPE_SCHEMA_BUG = "com.sun.jndi.ldap.netscape.schemaBugs";
  
  private static final String OLD_NETSCAPE_SCHEMA_BUG = "com.sun.naming.netscape.schemaBugs";
  
  private static final String CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout";
  
  private static final String READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
  
  private static final String ENABLE_POOL = "com.sun.jndi.ldap.connect.pool";
  
  private static final String DOMAIN_NAME = "com.sun.jndi.ldap.domainname";
  
  private static final String WAIT_FOR_REPLY = "com.sun.jndi.ldap.search.waitForReply";
  
  private static final String REPLY_QUEUE_SIZE = "com.sun.jndi.ldap.search.replyQueueSize";
  
  private static final NameParser parser = new LdapNameParser();
  
  private static final ControlFactory myResponseControlFactory = new DefaultResponseControlFactory();
  
  private static final Control manageReferralControl = new ManageReferralControl(false);
  
  private static final HierMemDirCtx EMPTY_SCHEMA = new HierMemDirCtx();
  
  int port_number;
  
  String hostname = null;
  
  LdapClient clnt = null;
  
  Hashtable<String, Object> envprops = null;
  
  int handleReferrals = 3;
  
  boolean hasLdapsScheme = false;
  
  String currentDN;
  
  Name currentParsedDN;
  
  Vector<Control> respCtls = null;
  
  Control[] reqCtls = null;
  
  private OutputStream trace = null;
  
  private boolean netscapeSchemaBug = false;
  
  private Control[] bindCtls = null;
  
  private int referralHopLimit = 10;
  
  private Hashtable<String, DirContext> schemaTrees = null;
  
  private int batchSize = 1;
  
  private boolean deleteRDN = true;
  
  private boolean typesOnly = false;
  
  private int derefAliases = 3;
  
  private char addrEncodingSeparator = '#';
  
  private Hashtable<String, Boolean> binaryAttrs = null;
  
  private int connectTimeout = -1;
  
  private int readTimeout = -1;
  
  private boolean waitForReply = true;
  
  private int replyQueueSize = -1;
  
  private boolean useSsl = false;
  
  private boolean useDefaultPortNumber = false;
  
  private boolean parentIsLdapCtx = false;
  
  private int hopCount = 1;
  
  private String url = null;
  
  private EventSupport eventSupport;
  
  private boolean unsolicited = false;
  
  private boolean sharable = true;
  
  private int enumCount = 0;
  
  private boolean closeRequested = false;
  
  public LdapCtx(String paramString1, String paramString2, int paramInt, Hashtable<?, ?> paramHashtable, boolean paramBoolean) throws NamingException {
    this.useSsl = this.hasLdapsScheme = paramBoolean;
    if (paramHashtable != null) {
      this.envprops = (Hashtable)paramHashtable.clone();
      if ("ssl".equals(this.envprops.get("java.naming.security.protocol")))
        this.useSsl = true; 
      this.trace = (OutputStream)this.envprops.get("com.sun.jndi.ldap.trace.ber");
      if (paramHashtable.get("com.sun.jndi.ldap.netscape.schemaBugs") != null || paramHashtable.get("com.sun.naming.netscape.schemaBugs") != null)
        this.netscapeSchemaBug = true; 
    } 
    this.currentDN = (paramString1 != null) ? paramString1 : "";
    this.currentParsedDN = parser.parse(this.currentDN);
    this.hostname = (paramString2 != null && paramString2.length() > 0) ? paramString2 : "localhost";
    if (this.hostname.charAt(0) == '[')
      this.hostname = this.hostname.substring(1, this.hostname.length() - 1); 
    if (paramInt > 0) {
      this.port_number = paramInt;
    } else {
      this.port_number = this.useSsl ? 636 : 389;
      this.useDefaultPortNumber = true;
    } 
    this.schemaTrees = new Hashtable(11, 0.75F);
    initEnv();
    try {
      connect(false);
    } catch (NamingException namingException) {
      try {
        close();
      } catch (Exception exception) {}
      throw namingException;
    } 
  }
  
  LdapCtx(LdapCtx paramLdapCtx, String paramString) throws NamingException {
    this.useSsl = paramLdapCtx.useSsl;
    this.hasLdapsScheme = paramLdapCtx.hasLdapsScheme;
    this.useDefaultPortNumber = paramLdapCtx.useDefaultPortNumber;
    this.hostname = paramLdapCtx.hostname;
    this.port_number = paramLdapCtx.port_number;
    this.currentDN = paramString;
    if (paramLdapCtx.currentDN == this.currentDN) {
      this.currentParsedDN = paramLdapCtx.currentParsedDN;
    } else {
      this.currentParsedDN = parser.parse(this.currentDN);
    } 
    this.envprops = paramLdapCtx.envprops;
    this.schemaTrees = paramLdapCtx.schemaTrees;
    this.clnt = paramLdapCtx.clnt;
    this.clnt.incRefCount();
    this.parentIsLdapCtx = (paramString == null || paramString.equals(paramLdapCtx.currentDN)) ? paramLdapCtx.parentIsLdapCtx : 1;
    this.trace = paramLdapCtx.trace;
    this.netscapeSchemaBug = paramLdapCtx.netscapeSchemaBug;
    initEnv();
  }
  
  public LdapContext newInstance(Control[] paramArrayOfControl) throws NamingException {
    LdapCtx ldapCtx = new LdapCtx(this, this.currentDN);
    ldapCtx.setRequestControls(paramArrayOfControl);
    return ldapCtx;
  }
  
  protected void c_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException { c_bind(paramName, paramObject, null, paramContinuation); }
  
  protected void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    Attributes attributes = paramAttributes;
    try {
      ensureOpen();
      if (paramObject == null) {
        if (paramAttributes == null)
          throw new IllegalArgumentException("cannot bind null object with no attributes"); 
      } else {
        paramAttributes = Obj.determineBindAttrs(this.addrEncodingSeparator, paramObject, paramAttributes, false, paramName, this, this.envprops);
      } 
      String str = fullyQualifiedName(paramName);
      paramAttributes = addRdnAttributes(str, paramAttributes, (attributes != paramAttributes));
      LdapEntry ldapEntry = new LdapEntry(str, paramAttributes);
      LdapResult ldapResult = this.clnt.add(ldapEntry, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.bind(paramName, paramObject, attributes);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException { c_rebind(paramName, paramObject, null, paramContinuation); }
  
  protected void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    Attributes attributes = paramAttributes;
    try {
      Attributes attributes1 = null;
      try {
        attributes1 = c_getAttributes(paramName, null, paramContinuation);
      } catch (NameNotFoundException nameNotFoundException) {}
      if (attributes1 == null) {
        c_bind(paramName, paramObject, paramAttributes, paramContinuation);
        return;
      } 
      if (paramAttributes == null && paramObject instanceof DirContext)
        paramAttributes = ((DirContext)paramObject).getAttributes(""); 
      Attributes attributes2 = (Attributes)attributes1.clone();
      if (paramAttributes == null) {
        Attribute attribute = attributes1.get(Obj.JAVA_ATTRIBUTES[0]);
        if (attribute != null) {
          attribute = (Attribute)attribute.clone();
          for (byte b1 = 0; b1 < Obj.JAVA_OBJECT_CLASSES.length; b1++) {
            attribute.remove(Obj.JAVA_OBJECT_CLASSES_LOWER[b1]);
            attribute.remove(Obj.JAVA_OBJECT_CLASSES[b1]);
          } 
          attributes1.put(attribute);
        } 
        for (byte b = 1; b < Obj.JAVA_ATTRIBUTES.length; b++)
          attributes1.remove(Obj.JAVA_ATTRIBUTES[b]); 
        paramAttributes = attributes1;
      } 
      if (paramObject != null)
        paramAttributes = Obj.determineBindAttrs(this.addrEncodingSeparator, paramObject, paramAttributes, (attributes != paramAttributes), paramName, this, this.envprops); 
      String str = fullyQualifiedName(paramName);
      LdapResult ldapResult = this.clnt.delete(str, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0) {
        processReturnCode(ldapResult, paramName);
        return;
      } 
      NamingException namingException = null;
      try {
        paramAttributes = addRdnAttributes(str, paramAttributes, (attributes != paramAttributes));
        LdapEntry ldapEntry = new LdapEntry(str, paramAttributes);
        ldapResult = this.clnt.add(ldapEntry, this.reqCtls);
        if (ldapResult.resControls != null)
          this.respCtls = appendVector(this.respCtls, ldapResult.resControls); 
      } catch (NamingException|IOException namingException1) {
        namingException = namingException1;
      } 
      if ((namingException != null && !(namingException instanceof LdapReferralException)) || ldapResult.status != 0) {
        LdapResult ldapResult1 = this.clnt.add(new LdapEntry(str, attributes2), this.reqCtls);
        if (ldapResult1.resControls != null)
          this.respCtls = appendVector(this.respCtls, ldapResult1.resControls); 
        if (namingException == null)
          processReturnCode(ldapResult, paramName); 
      } 
      if (namingException instanceof NamingException)
        throw (NamingException)namingException; 
      if (namingException instanceof IOException)
        throw (IOException)namingException; 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.rebind(paramName, paramObject, attributes);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_unbind(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      ensureOpen();
      String str = fullyQualifiedName(paramName);
      LdapResult ldapResult = this.clnt.delete(str, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      adjustDeleteStatus(str, ldapResult);
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.unbind(paramName);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException {
    String str1 = null;
    String str2 = null;
    paramContinuation.setError(this, paramName1);
    try {
      Name name2;
      Name name1;
      ensureOpen();
      if (paramName1.isEmpty()) {
        name2 = parser.parse("");
      } else {
        Name name = parser.parse(paramName1.get(0));
        name2 = name.getPrefix(name.size() - 1);
      } 
      if (paramName2 instanceof CompositeName) {
        name1 = parser.parse(paramName2.get(0));
      } else {
        name1 = paramName2;
      } 
      Name name3 = name1.getPrefix(name1.size() - 1);
      if (!name2.equals(name3)) {
        if (!this.clnt.isLdapv3)
          throw new InvalidNameException("LDAPv2 doesn't support changing the parent as a result of a rename"); 
        str2 = fullyQualifiedName(name3.toString());
      } 
      str1 = name1.get(name1.size() - 1);
      LdapResult ldapResult = this.clnt.moddn(fullyQualifiedName(paramName1), str1, this.deleteRDN, str2, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName1); 
    } catch (LdapReferralException ldapReferralException) {
      ldapReferralException.setNewRdn(str1);
      if (str2 != null) {
        PartialResultException partialResultException = new PartialResultException("Cannot continue referral processing when newSuperior is nonempty: " + str2);
        partialResultException.setRootCause(paramContinuation.fillInException(ldapReferralException));
        throw paramContinuation.fillInException(partialResultException);
      } 
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.rename(paramName1, paramName2);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected Context c_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException { return c_createSubcontext(paramName, null, paramContinuation); }
  
  protected DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    Attributes attributes = paramAttributes;
    try {
      ensureOpen();
      if (paramAttributes == null) {
        BasicAttribute basicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_OBJECT_CLASSES[0]);
        basicAttribute.add("top");
        paramAttributes = new BasicAttributes(true);
        paramAttributes.put(basicAttribute);
      } 
      String str = fullyQualifiedName(paramName);
      paramAttributes = addRdnAttributes(str, paramAttributes, (attributes != paramAttributes));
      LdapEntry ldapEntry = new LdapEntry(str, paramAttributes);
      LdapResult ldapResult = this.clnt.add(ldapEntry, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0) {
        processReturnCode(ldapResult, paramName);
        return null;
      } 
      return new LdapCtx(this, str);
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.createSubcontext(paramName, attributes);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      ensureOpen();
      String str = fullyQualifiedName(paramName);
      LdapResult ldapResult = this.clnt.delete(str, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      adjustDeleteStatus(str, ldapResult);
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.destroySubcontext(paramName);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  private static Attributes addRdnAttributes(String paramString, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    if (paramString.equals(""))
      return paramAttributes; 
    List list = (new LdapName(paramString)).getRdns();
    Rdn rdn = (Rdn)list.get(list.size() - 1);
    Attributes attributes = rdn.toAttributes();
    NamingEnumeration namingEnumeration = attributes.getAll();
    while (namingEnumeration.hasMore()) {
      Attribute attribute = (Attribute)namingEnumeration.next();
      if (paramAttributes.get(attribute.getID()) != null || (!paramAttributes.isCaseIgnored() && containsIgnoreCase(paramAttributes.getIDs(), attribute.getID())))
        continue; 
      if (!paramBoolean) {
        paramAttributes = (Attributes)paramAttributes.clone();
        paramBoolean = true;
      } 
      paramAttributes.put(attribute);
    } 
    return paramAttributes;
  }
  
  private static boolean containsIgnoreCase(NamingEnumeration<String> paramNamingEnumeration, String paramString) throws NamingException {
    while (paramNamingEnumeration.hasMore()) {
      String str = (String)paramNamingEnumeration.next();
      if (str.equalsIgnoreCase(paramString))
        return true; 
    } 
    return false;
  }
  
  private void adjustDeleteStatus(String paramString, LdapResult paramLdapResult) {
    if (paramLdapResult.status == 32 && paramLdapResult.matchedDN != null)
      try {
        Name name1 = parser.parse(paramString);
        Name name2 = parser.parse(paramLdapResult.matchedDN);
        if (name1.size() - name2.size() == 1)
          paramLdapResult.status = 0; 
      } catch (NamingException namingException) {} 
  }
  
  private static <T> Vector<T> appendVector(Vector<T> paramVector1, Vector<T> paramVector2) {
    if (paramVector1 == null) {
      paramVector1 = paramVector2;
    } else {
      for (byte b = 0; b < paramVector2.size(); b++)
        paramVector1.addElement(paramVector2.elementAt(b)); 
    } 
    return paramVector1;
  }
  
  protected Object c_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException { return c_lookup(paramName, paramContinuation); }
  
  protected Object c_lookup(Name paramName, Continuation paramContinuation) throws NamingException {
    Attributes attributes;
    paramContinuation.setError(this, paramName);
    Object object = null;
    try {
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(0);
      searchControls.setReturningAttributes(null);
      searchControls.setReturningObjFlag(true);
      LdapResult ldapResult = doSearchOnce(paramName, "(objectClass=*)", searchControls, true);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
      if (ldapResult.entries == null || ldapResult.entries.size() != 1) {
        attributes = new BasicAttributes(true);
      } else {
        LdapEntry ldapEntry = (LdapEntry)ldapResult.entries.elementAt(0);
        attributes = ldapEntry.attributes;
        Vector vector = ldapEntry.respCtls;
        if (vector != null)
          appendVector(this.respCtls, vector); 
      } 
      if (attributes.get(Obj.JAVA_ATTRIBUTES[2]) != null)
        object = Obj.decodeObject(attributes); 
      if (object == null)
        object = new LdapCtx(this, fullyQualifiedName(paramName)); 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.lookup(paramName);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
    try {
      return DirectoryManager.getObjectInstance(object, paramName, this, this.envprops, attributes);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } catch (Exception exception) {
      NamingException namingException = new NamingException("problem generating object using object factory");
      namingException.setRootCause(exception);
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation) throws NamingException {
    SearchControls searchControls = new SearchControls();
    String[] arrayOfString = new String[2];
    arrayOfString[0] = Obj.JAVA_ATTRIBUTES[0];
    arrayOfString[1] = Obj.JAVA_ATTRIBUTES[2];
    searchControls.setReturningAttributes(arrayOfString);
    searchControls.setReturningObjFlag(true);
    paramContinuation.setError(this, paramName);
    LdapResult ldapResult = null;
    try {
      ldapResult = doSearch(paramName, "(objectClass=*)", searchControls, true, true);
      if (ldapResult.status != 0 || ldapResult.referrals != null)
        processReturnCode(ldapResult, paramName); 
      return new LdapNamingEnumeration(this, ldapResult, paramName, paramContinuation);
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.list(paramName);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (LimitExceededException limitExceededException) {
      LdapNamingEnumeration ldapNamingEnumeration = new LdapNamingEnumeration(this, ldapResult, paramName, paramContinuation);
      ldapNamingEnumeration.setNamingException((LimitExceededException)paramContinuation.fillInException(limitExceededException));
      return ldapNamingEnumeration;
    } catch (PartialResultException partialResultException) {
      LdapNamingEnumeration ldapNamingEnumeration = new LdapNamingEnumeration(this, ldapResult, paramName, paramContinuation);
      ldapNamingEnumeration.setNamingException((PartialResultException)paramContinuation.fillInException(partialResultException));
      return ldapNamingEnumeration;
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation) throws NamingException {
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(null);
    searchControls.setReturningObjFlag(true);
    paramContinuation.setError(this, paramName);
    LdapResult ldapResult = null;
    try {
      ldapResult = doSearch(paramName, "(objectClass=*)", searchControls, true, true);
      if (ldapResult.status != 0 || ldapResult.referrals != null)
        processReturnCode(ldapResult, paramName); 
      return new LdapBindingEnumeration(this, ldapResult, paramName, paramContinuation);
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.listBindings(paramName);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (LimitExceededException limitExceededException) {
      LdapBindingEnumeration ldapBindingEnumeration = new LdapBindingEnumeration(this, ldapResult, paramName, paramContinuation);
      ldapBindingEnumeration.setNamingException(paramContinuation.fillInException(limitExceededException));
      return ldapBindingEnumeration;
    } catch (PartialResultException partialResultException) {
      LdapBindingEnumeration ldapBindingEnumeration = new LdapBindingEnumeration(this, ldapResult, paramName, paramContinuation);
      ldapBindingEnumeration.setNamingException(paramContinuation.fillInException(partialResultException));
      return ldapBindingEnumeration;
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected NameParser c_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    return parser;
  }
  
  public String getNameInNamespace() { return this.currentDN; }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    if (paramName1 instanceof LdapName && paramName2 instanceof LdapName) {
      Name name1 = (Name)paramName2.clone();
      name1.addAll(paramName1);
      return (new CompositeName()).add(name1.toString());
    } 
    if (!(paramName1 instanceof CompositeName))
      paramName1 = (new CompositeName()).add(paramName1.toString()); 
    if (!(paramName2 instanceof CompositeName))
      paramName2 = (new CompositeName()).add(paramName2.toString()); 
    int i = paramName2.size() - 1;
    if (paramName1.isEmpty() || paramName2.isEmpty() || paramName1.get(0).equals("") || paramName2.get(i).equals(""))
      return super.composeName(paramName1, paramName2); 
    Name name = (Name)paramName2.clone();
    name.addAll(paramName1);
    if (this.parentIsLdapCtx) {
      String str = concatNames(name.get(i + 1), name.get(i));
      name.remove(i + 1);
      name.remove(i);
      name.add(i, str);
    } 
    return name;
  }
  
  private String fullyQualifiedName(Name paramName) { return paramName.isEmpty() ? this.currentDN : fullyQualifiedName(paramName.get(0)); }
  
  private String fullyQualifiedName(String paramString) { return concatNames(paramString, this.currentDN); }
  
  private static String concatNames(String paramString1, String paramString2) { return (paramString1 == null || paramString1.equals("")) ? paramString2 : ((paramString2 == null || paramString2.equals("")) ? paramString1 : (paramString1 + "," + paramString2)); }
  
  protected Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(0);
    searchControls.setReturningAttributes(paramArrayOfString);
    try {
      LdapResult ldapResult = doSearchOnce(paramName, "(objectClass=*)", searchControls, true);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
      if (ldapResult.entries == null || ldapResult.entries.size() != 1)
        return new BasicAttributes(true); 
      LdapEntry ldapEntry = (LdapEntry)ldapResult.entries.elementAt(0);
      Vector vector = ldapEntry.respCtls;
      if (vector != null)
        appendVector(this.respCtls, vector); 
      setParents(ldapEntry.attributes, (Name)paramName.clone());
      return ldapEntry.attributes;
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.getAttributes(paramName, paramArrayOfString);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      ensureOpen();
      if (paramAttributes == null || paramAttributes.size() == 0)
        return; 
      String str = fullyQualifiedName(paramName);
      int i = convertToLdapModCode(paramInt);
      int[] arrayOfInt = new int[paramAttributes.size()];
      Attribute[] arrayOfAttribute = new Attribute[paramAttributes.size()];
      NamingEnumeration namingEnumeration = paramAttributes.getAll();
      for (byte b = 0; b < arrayOfInt.length && namingEnumeration.hasMore(); b++) {
        arrayOfInt[b] = i;
        arrayOfAttribute[b] = (Attribute)namingEnumeration.next();
      } 
      LdapResult ldapResult = this.clnt.modify(str, arrayOfInt, arrayOfAttribute, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0) {
        processReturnCode(ldapResult, paramName);
        return;
      } 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.modifyAttributes(paramName, paramInt, paramAttributes);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      ensureOpen();
      if (paramArrayOfModificationItem == null || paramArrayOfModificationItem.length == 0)
        return; 
      String str = fullyQualifiedName(paramName);
      int[] arrayOfInt = new int[paramArrayOfModificationItem.length];
      Attribute[] arrayOfAttribute = new Attribute[paramArrayOfModificationItem.length];
      for (byte b = 0; b < arrayOfInt.length; b++) {
        ModificationItem modificationItem = paramArrayOfModificationItem[b];
        arrayOfInt[b] = convertToLdapModCode(modificationItem.getModificationOp());
        arrayOfAttribute[b] = modificationItem.getAttribute();
      } 
      LdapResult ldapResult = this.clnt.modify(str, arrayOfInt, arrayOfAttribute, this.reqCtls);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, paramName); 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          ldapReferralContext.modifyAttributes(paramName, paramArrayOfModificationItem);
          return;
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  private static int convertToLdapModCode(int paramInt) {
    switch (paramInt) {
      case 1:
        return 0;
      case 2:
        return 2;
      case 3:
        return 1;
    } 
    throw new IllegalArgumentException("Invalid modification code");
  }
  
  protected DirContext c_getSchema(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      return getSchemaTree(paramName);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  protected DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    try {
      Attribute attribute = c_getAttributes(paramName, new String[] { "objectclass" }, paramContinuation).get("objectclass");
      if (attribute == null || attribute.size() == 0)
        return EMPTY_SCHEMA; 
      Context context = (Context)c_getSchema(paramName, paramContinuation).lookup("ClassDefinition");
      HierMemDirCtx hierMemDirCtx = new HierMemDirCtx();
      NamingEnumeration namingEnumeration = attribute.getAll();
      while (namingEnumeration.hasMoreElements()) {
        String str = (String)namingEnumeration.nextElement();
        DirContext dirContext = (DirContext)context.lookup(str);
        hierMemDirCtx.bind(str, dirContext);
      } 
      hierMemDirCtx.setReadOnly(new SchemaViolationException("Cannot update schema object"));
      return hierMemDirCtx;
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  private DirContext getSchemaTree(Name paramName) throws NamingException {
    String str = getSchemaEntry(paramName, true);
    DirContext dirContext = (DirContext)this.schemaTrees.get(str);
    if (dirContext == null) {
      dirContext = buildSchemaTree(str);
      this.schemaTrees.put(str, dirContext);
    } 
    return dirContext;
  }
  
  private DirContext buildSchemaTree(String paramString) throws NamingException {
    SearchControls searchControls = new SearchControls(0, 0L, 0, SCHEMA_ATTRIBUTES, true, false);
    Name name = (new CompositeName()).add(paramString);
    NamingEnumeration namingEnumeration = searchAux(name, "(objectClass=subschema)", searchControls, false, true, new Continuation());
    if (!namingEnumeration.hasMore())
      throw new OperationNotSupportedException("Cannot get read subschemasubentry: " + paramString); 
    SearchResult searchResult = (SearchResult)namingEnumeration.next();
    namingEnumeration.close();
    Object object = searchResult.getObject();
    if (!(object instanceof LdapCtx))
      throw new NamingException("Cannot get schema object as DirContext: " + paramString); 
    return LdapSchemaCtx.createSchemaTree(this.envprops, paramString, (LdapCtx)object, searchResult.getAttributes(), this.netscapeSchemaBug);
  }
  
  private String getSchemaEntry(Name paramName, boolean paramBoolean) throws NamingException {
    NamingEnumeration namingEnumeration;
    SearchControls searchControls = new SearchControls(0, 0L, 0, new String[] { "subschemasubentry" }, false, false);
    try {
      namingEnumeration = searchAux(paramName, "objectclass=*", searchControls, paramBoolean, true, new Continuation());
    } catch (NamingException namingException) {
      if (!this.clnt.isLdapv3 && this.currentDN.length() == 0 && paramName.isEmpty())
        throw new OperationNotSupportedException("Cannot get schema information from server"); 
      throw namingException;
    } 
    if (!namingEnumeration.hasMoreElements())
      throw new ConfigurationException("Requesting schema of nonexistent entry: " + paramName); 
    SearchResult searchResult = (SearchResult)namingEnumeration.next();
    namingEnumeration.close();
    Attribute attribute = searchResult.getAttributes().get("subschemasubentry");
    if (attribute == null || attribute.size() < 0) {
      if (this.currentDN.length() == 0 && paramName.isEmpty())
        throw new OperationNotSupportedException("Cannot read subschemasubentry of root DSE"); 
      return getSchemaEntry(new CompositeName(), false);
    } 
    return (String)attribute.get();
  }
  
  void setParents(Attributes paramAttributes, Name paramName) throws NamingException {
    NamingEnumeration namingEnumeration = paramAttributes.getAll();
    while (namingEnumeration.hasMore())
      ((LdapAttribute)namingEnumeration.next()).setParent(this, paramName); 
  }
  
  String getURL() {
    if (this.url == null)
      this.url = LdapURL.toUrlString(this.hostname, this.port_number, this.currentDN, this.hasLdapsScheme); 
    return this.url;
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { return c_search(paramName, paramAttributes, null, paramContinuation); }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    String str;
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(paramArrayOfString);
    try {
      str = SearchFilter.format(paramAttributes);
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } 
    return c_search(paramName, str, searchControls, paramContinuation);
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { return searchAux(paramName, paramString, cloneSearchControls(paramSearchControls), true, this.waitForReply, paramContinuation); }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    String str;
    try {
      str = SearchFilter.format(paramString, paramArrayOfObject);
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } 
    return c_search(paramName, str, paramSearchControls, paramContinuation);
  }
  
  NamingEnumeration<SearchResult> searchAux(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean1, boolean paramBoolean2, Continuation paramContinuation) throws NamingException {
    LdapResult ldapResult = null;
    String[] arrayOfString1 = new String[2];
    if (paramSearchControls == null)
      paramSearchControls = new SearchControls(); 
    String[] arrayOfString2 = paramSearchControls.getReturningAttributes();
    if (paramSearchControls.getReturningObjFlag() && arrayOfString2 != null) {
      boolean bool = false;
      for (int i = arrayOfString2.length - 1; i >= 0; i--) {
        if (arrayOfString2[i].equals("*")) {
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        String[] arrayOfString = new String[arrayOfString2.length + Obj.JAVA_ATTRIBUTES.length];
        System.arraycopy(arrayOfString2, 0, arrayOfString, 0, arrayOfString2.length);
        System.arraycopy(Obj.JAVA_ATTRIBUTES, 0, arrayOfString, arrayOfString2.length, Obj.JAVA_ATTRIBUTES.length);
        paramSearchControls.setReturningAttributes(arrayOfString);
      } 
    } 
    SearchArgs searchArgs = new SearchArgs(paramName, paramString, paramSearchControls, arrayOfString2);
    paramContinuation.setError(this, paramName);
    try {
      if (searchToCompare(paramString, paramSearchControls, arrayOfString1)) {
        ldapResult = compare(paramName, arrayOfString1[0], arrayOfString1[1]);
        if (!ldapResult.compareToSearchResult(fullyQualifiedName(paramName)))
          processReturnCode(ldapResult, paramName); 
      } else {
        ldapResult = doSearch(paramName, paramString, paramSearchControls, paramBoolean1, paramBoolean2);
        processReturnCode(ldapResult, paramName);
      } 
      return new LdapSearchEnumeration(this, ldapResult, fullyQualifiedName(paramName), searchArgs, paramContinuation);
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw paramContinuation.fillInException(ldapReferralException); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.search(paramName, paramString, paramSearchControls);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (LimitExceededException limitExceededException) {
      LdapSearchEnumeration ldapSearchEnumeration = new LdapSearchEnumeration(this, ldapResult, fullyQualifiedName(paramName), searchArgs, paramContinuation);
      ldapSearchEnumeration.setNamingException(limitExceededException);
      return ldapSearchEnumeration;
    } catch (PartialResultException partialResultException) {
      LdapSearchEnumeration ldapSearchEnumeration = new LdapSearchEnumeration(this, ldapResult, fullyQualifiedName(paramName), searchArgs, paramContinuation);
      ldapSearchEnumeration.setNamingException(partialResultException);
      return ldapSearchEnumeration;
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw paramContinuation.fillInException(communicationException);
    } catch (NamingException namingException) {
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  LdapResult getSearchReply(LdapClient paramLdapClient, LdapResult paramLdapResult) throws NamingException {
    if (this.clnt != paramLdapClient)
      throw new CommunicationException("Context's connection changed; unable to continue enumeration"); 
    try {
      return paramLdapClient.getSearchReply(this.batchSize, paramLdapResult, this.binaryAttrs);
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw communicationException;
    } 
  }
  
  private LdapResult doSearchOnce(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean) throws NamingException {
    int i = this.batchSize;
    this.batchSize = 2;
    LdapResult ldapResult = doSearch(paramName, paramString, paramSearchControls, paramBoolean, true);
    this.batchSize = i;
    return ldapResult;
  }
  
  private LdapResult doSearch(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean1, boolean paramBoolean2) throws NamingException {
    ensureOpen();
    try {
      byte b;
      switch (paramSearchControls.getSearchScope()) {
        case 0:
          b = 0;
          break;
        default:
          b = 1;
          break;
        case 2:
          b = 2;
          break;
      } 
      String[] arrayOfString = paramSearchControls.getReturningAttributes();
      if (arrayOfString != null && arrayOfString.length == 0) {
        arrayOfString = new String[1];
        arrayOfString[0] = "1.1";
      } 
      String str = paramBoolean1 ? fullyQualifiedName(paramName) : (paramName.isEmpty() ? "" : paramName.get(0));
      int i = paramSearchControls.getTimeLimit();
      int j = 0;
      if (i > 0)
        j = i / 1000 + 1; 
      LdapResult ldapResult = this.clnt.search(str, b, this.derefAliases, (int)paramSearchControls.getCountLimit(), j, paramSearchControls.getReturningObjFlag() ? false : this.typesOnly, arrayOfString, paramString, this.batchSize, this.reqCtls, this.binaryAttrs, paramBoolean2, this.replyQueueSize);
      this.respCtls = ldapResult.resControls;
      return ldapResult;
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw communicationException;
    } 
  }
  
  private static boolean searchToCompare(String paramString, SearchControls paramSearchControls, String[] paramArrayOfString) {
    if (paramSearchControls.getSearchScope() != 0)
      return false; 
    String[] arrayOfString = paramSearchControls.getReturningAttributes();
    return (arrayOfString == null || arrayOfString.length != 0) ? false : (!!filterToAssertion(paramString, paramArrayOfString));
  }
  
  private static boolean filterToAssertion(String paramString, String[] paramArrayOfString) {
    StringTokenizer stringTokenizer1 = new StringTokenizer(paramString, "=");
    if (stringTokenizer1.countTokens() != 2)
      return false; 
    paramArrayOfString[0] = stringTokenizer1.nextToken();
    paramArrayOfString[1] = stringTokenizer1.nextToken();
    if (paramArrayOfString[1].indexOf('*') != -1)
      return false; 
    boolean bool = false;
    int i = paramArrayOfString[1].length();
    if (paramArrayOfString[0].charAt(0) == '(' && paramArrayOfString[1].charAt(i - 1) == ')') {
      bool = true;
    } else if (paramArrayOfString[0].charAt(0) == '(' || paramArrayOfString[1].charAt(i - 1) == ')') {
      return false;
    } 
    StringTokenizer stringTokenizer2 = new StringTokenizer(paramArrayOfString[0], "()&|!=~><*", true);
    if (stringTokenizer2.countTokens() != (bool ? 2 : 1))
      return false; 
    stringTokenizer2 = new StringTokenizer(paramArrayOfString[1], "()&|!=~><*", true);
    if (stringTokenizer2.countTokens() != (bool ? 2 : 1))
      return false; 
    if (bool) {
      paramArrayOfString[0] = paramArrayOfString[0].substring(1);
      paramArrayOfString[1] = paramArrayOfString[1].substring(0, i - 1);
    } 
    return true;
  }
  
  private LdapResult compare(Name paramName, String paramString1, String paramString2) throws IOException, NamingException {
    ensureOpen();
    String str = fullyQualifiedName(paramName);
    LdapResult ldapResult = this.clnt.compare(str, paramString1, paramString2, this.reqCtls);
    this.respCtls = ldapResult.resControls;
    return ldapResult;
  }
  
  private static SearchControls cloneSearchControls(SearchControls paramSearchControls) {
    if (paramSearchControls == null)
      return null; 
    String[] arrayOfString = paramSearchControls.getReturningAttributes();
    if (arrayOfString != null) {
      String[] arrayOfString1 = new String[arrayOfString.length];
      System.arraycopy(arrayOfString, 0, arrayOfString1, 0, arrayOfString.length);
      arrayOfString = arrayOfString1;
    } 
    return new SearchControls(paramSearchControls.getSearchScope(), paramSearchControls.getCountLimit(), paramSearchControls.getTimeLimit(), arrayOfString, paramSearchControls.getReturningObjFlag(), paramSearchControls.getDerefLinkFlag());
  }
  
  protected Hashtable<String, Object> p_getEnvironment() { return this.envprops; }
  
  public Hashtable<String, Object> getEnvironment() { return (this.envprops == null) ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone(); }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    if (this.envprops == null || this.envprops.get(paramString) == null)
      return null; 
    switch (paramString) {
      case "java.naming.ldap.ref.separator":
        this.addrEncodingSeparator = '#';
        break;
      case "java.naming.ldap.typesOnly":
        this.typesOnly = false;
        break;
      case "java.naming.ldap.deleteRDN":
        this.deleteRDN = true;
        break;
      case "java.naming.ldap.derefAliases":
        this.derefAliases = 3;
        break;
      case "java.naming.batchsize":
        this.batchSize = 1;
        break;
      case "java.naming.ldap.referral.limit":
        this.referralHopLimit = 10;
        break;
      case "java.naming.referral":
        setReferralMode(null, true);
        break;
      case "java.naming.ldap.attributes.binary":
        setBinaryAttributes(null);
        break;
      case "com.sun.jndi.ldap.connect.timeout":
        this.connectTimeout = -1;
        break;
      case "com.sun.jndi.ldap.read.timeout":
        this.readTimeout = -1;
        break;
      case "com.sun.jndi.ldap.search.waitForReply":
        this.waitForReply = true;
        break;
      case "com.sun.jndi.ldap.search.replyQueueSize":
        this.replyQueueSize = -1;
        break;
      case "java.naming.security.protocol":
        closeConnection(false);
        if (this.useSsl && !this.hasLdapsScheme) {
          this.useSsl = false;
          this.url = null;
          if (this.useDefaultPortNumber)
            this.port_number = 389; 
        } 
        break;
      case "java.naming.ldap.version":
      case "java.naming.ldap.factory.socket":
        closeConnection(false);
        break;
      case "java.naming.security.authentication":
      case "java.naming.security.principal":
      case "java.naming.security.credentials":
        this.sharable = false;
        break;
    } 
    this.envprops = (Hashtable)this.envprops.clone();
    return this.envprops.remove(paramString);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (paramObject == null)
      return removeFromEnvironment(paramString); 
    switch (paramString) {
      case "java.naming.ldap.ref.separator":
        setRefSeparator((String)paramObject);
        break;
      case "java.naming.ldap.typesOnly":
        setTypesOnly((String)paramObject);
        break;
      case "java.naming.ldap.deleteRDN":
        setDeleteRDN((String)paramObject);
        break;
      case "java.naming.ldap.derefAliases":
        setDerefAliases((String)paramObject);
        break;
      case "java.naming.batchsize":
        setBatchSize((String)paramObject);
        break;
      case "java.naming.ldap.referral.limit":
        setReferralLimit((String)paramObject);
        break;
      case "java.naming.referral":
        setReferralMode((String)paramObject, true);
        break;
      case "java.naming.ldap.attributes.binary":
        setBinaryAttributes((String)paramObject);
        break;
      case "com.sun.jndi.ldap.connect.timeout":
        setConnectTimeout((String)paramObject);
        break;
      case "com.sun.jndi.ldap.read.timeout":
        setReadTimeout((String)paramObject);
        break;
      case "com.sun.jndi.ldap.search.waitForReply":
        setWaitForReply((String)paramObject);
        break;
      case "com.sun.jndi.ldap.search.replyQueueSize":
        setReplyQueueSize((String)paramObject);
        break;
      case "java.naming.security.protocol":
        closeConnection(false);
        if ("ssl".equals(paramObject)) {
          this.useSsl = true;
          this.url = null;
          if (this.useDefaultPortNumber)
            this.port_number = 636; 
        } 
        break;
      case "java.naming.ldap.version":
      case "java.naming.ldap.factory.socket":
        closeConnection(false);
        break;
      case "java.naming.security.authentication":
      case "java.naming.security.principal":
      case "java.naming.security.credentials":
        this.sharable = false;
        break;
    } 
    this.envprops = (this.envprops == null) ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone();
    return this.envprops.put(paramString, paramObject);
  }
  
  void setProviderUrl(String paramString) {
    if (this.envprops != null)
      this.envprops.put("java.naming.provider.url", paramString); 
  }
  
  void setDomainName(String paramString) {
    if (this.envprops != null)
      this.envprops.put("com.sun.jndi.ldap.domainname", paramString); 
  }
  
  private void initEnv() throws NamingException {
    if (this.envprops == null) {
      setReferralMode(null, false);
      return;
    } 
    setBatchSize((String)this.envprops.get("java.naming.batchsize"));
    setRefSeparator((String)this.envprops.get("java.naming.ldap.ref.separator"));
    setDeleteRDN((String)this.envprops.get("java.naming.ldap.deleteRDN"));
    setTypesOnly((String)this.envprops.get("java.naming.ldap.typesOnly"));
    setDerefAliases((String)this.envprops.get("java.naming.ldap.derefAliases"));
    setReferralLimit((String)this.envprops.get("java.naming.ldap.referral.limit"));
    setBinaryAttributes((String)this.envprops.get("java.naming.ldap.attributes.binary"));
    this.bindCtls = cloneControls((Control[])this.envprops.get("java.naming.ldap.control.connect"));
    setReferralMode((String)this.envprops.get("java.naming.referral"), false);
    setConnectTimeout((String)this.envprops.get("com.sun.jndi.ldap.connect.timeout"));
    setReadTimeout((String)this.envprops.get("com.sun.jndi.ldap.read.timeout"));
    setWaitForReply((String)this.envprops.get("com.sun.jndi.ldap.search.waitForReply"));
    setReplyQueueSize((String)this.envprops.get("com.sun.jndi.ldap.search.replyQueueSize"));
  }
  
  private void setDeleteRDN(String paramString) {
    if (paramString != null && paramString.equalsIgnoreCase("false")) {
      this.deleteRDN = false;
    } else {
      this.deleteRDN = true;
    } 
  }
  
  private void setTypesOnly(String paramString) {
    if (paramString != null && paramString.equalsIgnoreCase("true")) {
      this.typesOnly = true;
    } else {
      this.typesOnly = false;
    } 
  }
  
  private void setBatchSize(String paramString) {
    if (paramString != null) {
      this.batchSize = Integer.parseInt(paramString);
    } else {
      this.batchSize = 1;
    } 
  }
  
  private void setReferralMode(String paramString, boolean paramBoolean) {
    if (paramString != null) {
      switch (paramString) {
        case "follow-scheme":
          this.handleReferrals = 4;
          break;
        case "follow":
          this.handleReferrals = 1;
          break;
        case "throw":
          this.handleReferrals = 2;
          break;
        case "ignore":
          this.handleReferrals = 3;
          break;
        default:
          throw new IllegalArgumentException("Illegal value for java.naming.referral property.");
      } 
    } else {
      this.handleReferrals = 3;
    } 
    if (this.handleReferrals == 3) {
      this.reqCtls = addControl(this.reqCtls, manageReferralControl);
    } else if (paramBoolean) {
      this.reqCtls = removeControl(this.reqCtls, manageReferralControl);
    } 
  }
  
  private void setDerefAliases(String paramString) {
    if (paramString != null) {
      switch (paramString) {
        case "never":
          this.derefAliases = 0;
          return;
        case "searching":
          this.derefAliases = 1;
          return;
        case "finding":
          this.derefAliases = 2;
          return;
        case "always":
          this.derefAliases = 3;
          return;
      } 
      throw new IllegalArgumentException("Illegal value for java.naming.ldap.derefAliases property.");
    } 
    this.derefAliases = 3;
  }
  
  private void setRefSeparator(String paramString) {
    if (paramString != null && paramString.length() > 0) {
      this.addrEncodingSeparator = paramString.charAt(0);
    } else {
      this.addrEncodingSeparator = '#';
    } 
  }
  
  private void setReferralLimit(String paramString) {
    if (paramString != null) {
      this.referralHopLimit = Integer.parseInt(paramString);
      if (this.referralHopLimit == 0)
        this.referralHopLimit = Integer.MAX_VALUE; 
    } else {
      this.referralHopLimit = 10;
    } 
  }
  
  void setHopCount(int paramInt) { this.hopCount = paramInt; }
  
  private void setConnectTimeout(String paramString) {
    if (paramString != null) {
      this.connectTimeout = Integer.parseInt(paramString);
    } else {
      this.connectTimeout = -1;
    } 
  }
  
  private void setReplyQueueSize(String paramString) {
    if (paramString != null) {
      this.replyQueueSize = Integer.parseInt(paramString);
      if (this.replyQueueSize <= 0)
        this.replyQueueSize = -1; 
    } else {
      this.replyQueueSize = -1;
    } 
  }
  
  private void setWaitForReply(String paramString) {
    if (paramString != null && paramString.equalsIgnoreCase("false")) {
      this.waitForReply = false;
    } else {
      this.waitForReply = true;
    } 
  }
  
  private void setReadTimeout(String paramString) {
    if (paramString != null) {
      this.readTimeout = Integer.parseInt(paramString);
    } else {
      this.readTimeout = -1;
    } 
  }
  
  private static Vector<Vector<String>> extractURLs(String paramString) {
    int i = 0;
    byte b;
    for (b = 0; (i = paramString.indexOf('\n', i)) >= 0; b++)
      i++; 
    Vector vector1 = new Vector(b);
    boolean bool = false;
    i = paramString.indexOf('\n');
    int j;
    for (j = i + 1; (i = paramString.indexOf('\n', j)) >= 0; j = i + 1) {
      Vector vector = new Vector(1);
      vector.addElement(paramString.substring(j, i));
      vector1.addElement(vector);
    } 
    Vector vector2 = new Vector(1);
    vector2.addElement(paramString.substring(j));
    vector1.addElement(vector2);
    return vector1;
  }
  
  private void setBinaryAttributes(String paramString) {
    if (paramString == null) {
      this.binaryAttrs = null;
    } else {
      this.binaryAttrs = new Hashtable(11, 0.75F);
      StringTokenizer stringTokenizer = new StringTokenizer(paramString.toLowerCase(Locale.ENGLISH), " ");
      while (stringTokenizer.hasMoreTokens())
        this.binaryAttrs.put(stringTokenizer.nextToken(), Boolean.TRUE); 
    } 
  }
  
  protected void finalize() throws NamingException {
    try {
      close();
    } catch (NamingException namingException) {}
  }
  
  public void close() throws NamingException {
    if (this.eventSupport != null) {
      this.eventSupport.cleanup();
      removeUnsolicited();
    } 
    if (this.enumCount > 0) {
      this.closeRequested = true;
      return;
    } 
    closeConnection(false);
  }
  
  public void reconnect(Control[] paramArrayOfControl) throws NamingException {
    this.envprops = (this.envprops == null) ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone();
    if (paramArrayOfControl == null) {
      this.envprops.remove("java.naming.ldap.control.connect");
      this.bindCtls = null;
    } else {
      this.envprops.put("java.naming.ldap.control.connect", this.bindCtls = cloneControls(paramArrayOfControl));
    } 
    this.sharable = false;
    ensureOpen();
  }
  
  private void ensureOpen() throws NamingException { ensureOpen(false); }
  
  private void ensureOpen(boolean paramBoolean) throws NamingException {
    try {
      if (this.clnt == null) {
        this.schemaTrees = new Hashtable(11, 0.75F);
        connect(paramBoolean);
      } else if (!this.sharable || paramBoolean) {
        synchronized (this.clnt) {
          if (!this.clnt.isLdapv3 || this.clnt.referenceCount > 1 || this.clnt.usingSaslStreams())
            closeConnection(false); 
        } 
        this.schemaTrees = new Hashtable(11, 0.75F);
        connect(paramBoolean);
      } 
    } finally {
      this.sharable = true;
    } 
  }
  
  private void connect(boolean paramBoolean) throws NamingException {
    String str1 = null;
    Object object = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    boolean bool = false;
    if (this.envprops != null) {
      str1 = (String)this.envprops.get("java.naming.security.principal");
      object = this.envprops.get("java.naming.security.credentials");
      str5 = (String)this.envprops.get("java.naming.ldap.version");
      str2 = this.useSsl ? "ssl" : (String)this.envprops.get("java.naming.security.protocol");
      str3 = (String)this.envprops.get("java.naming.ldap.factory.socket");
      str4 = (String)this.envprops.get("java.naming.security.authentication");
      bool = "true".equalsIgnoreCase((String)this.envprops.get("com.sun.jndi.ldap.connect.pool"));
    } 
    if (str3 == null)
      str3 = "ssl".equals(str2) ? "javax.net.ssl.SSLSocketFactory" : null; 
    if (str4 == null)
      str4 = (str1 == null) ? "none" : "simple"; 
    try {
      byte b;
      boolean bool1 = (this.clnt == null);
      if (bool1) {
        b = (str5 != null) ? Integer.parseInt(str5) : 32;
        this.clnt = LdapClient.getInstance(bool, this.hostname, this.port_number, str3, this.connectTimeout, this.readTimeout, this.trace, b, str4, this.bindCtls, str2, str1, object, this.envprops);
        if (this.clnt.authenticateCalled())
          return; 
      } else {
        if (this.sharable && paramBoolean)
          return; 
        b = 3;
      } 
      LdapResult ldapResult = this.clnt.authenticate(bool1, str1, object, b, str4, this.bindCtls, this.envprops);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0) {
        if (bool1)
          closeConnection(true); 
        processReturnCode(ldapResult);
      } 
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw ldapReferralException; 
      NamingException namingException = null;
      while (true) {
        String str;
        if ((str = ldapReferralException.getNextReferral()) == null) {
          if (namingException != null)
            throw (NamingException)namingException.fillInStackTrace(); 
          throw new NamingException("Internal error processing referral during connection");
        } 
        LdapURL ldapURL = new LdapURL(str);
        this.hostname = ldapURL.getHost();
        if (this.hostname != null && this.hostname.charAt(0) == '[')
          this.hostname = this.hostname.substring(1, this.hostname.length() - 1); 
        this.port_number = ldapURL.getPort();
        try {
          connect(paramBoolean);
          break;
        } catch (NamingException namingException1) {
          namingException = namingException1;
        } 
      } 
    } 
  }
  
  private void closeConnection(boolean paramBoolean) throws NamingException {
    removeUnsolicited();
    if (this.clnt != null) {
      this.clnt.close(this.reqCtls, paramBoolean);
      this.clnt = null;
    } 
  }
  
  void incEnumCount() throws NamingException { this.enumCount++; }
  
  void decEnumCount() throws NamingException {
    this.enumCount--;
    if (this.enumCount == 0 && this.closeRequested)
      try {
        close();
      } catch (NamingException namingException) {} 
  }
  
  protected void processReturnCode(LdapResult paramLdapResult) throws NamingException { processReturnCode(paramLdapResult, null, this, null, this.envprops, null); }
  
  void processReturnCode(LdapResult paramLdapResult, Name paramName) throws NamingException { processReturnCode(paramLdapResult, (new CompositeName()).add(this.currentDN), this, paramName, this.envprops, fullyQualifiedName(paramName)); }
  
  protected void processReturnCode(LdapResult paramLdapResult, Name paramName1, Object paramObject, Name paramName2, Hashtable<?, ?> paramHashtable, String paramString) throws NamingException {
    InvalidNameException invalidNameException;
    String str = LdapClient.getErrorMessage(paramLdapResult.status, paramLdapResult.errorMessage);
    LdapReferralException ldapReferralException = null;
    switch (paramLdapResult.status) {
      case 0:
        if (paramLdapResult.referrals != null) {
          str = "Unprocessed Continuation Reference(s)";
          if (this.handleReferrals == 3) {
            invalidNameException = new PartialResultException(str);
          } else {
            int i = paramLdapResult.referrals.size();
            LdapReferralException ldapReferralException1 = null;
            LdapReferralException ldapReferralException2 = null;
            str = "Continuation Reference";
            for (byte b = 0; b < i; b++) {
              ldapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, this.handleReferrals, this.reqCtls);
              ldapReferralException.setReferralInfo((Vector)paramLdapResult.referrals.elementAt(b), true);
              if (this.hopCount > 1)
                ldapReferralException.setHopCount(this.hopCount); 
              if (ldapReferralException1 == null) {
                ldapReferralException1 = ldapReferralException2 = ldapReferralException;
              } else {
                ldapReferralException2.nextReferralEx = ldapReferralException;
                ldapReferralException2 = ldapReferralException;
              } 
            } 
            paramLdapResult.referrals = null;
            if (paramLdapResult.refEx == null) {
              paramLdapResult.refEx = ldapReferralException1;
            } else {
              for (ldapReferralException2 = paramLdapResult.refEx; ldapReferralException2.nextReferralEx != null; ldapReferralException2 = ldapReferralException2.nextReferralEx);
              ldapReferralException2.nextReferralEx = ldapReferralException1;
            } 
            if (this.hopCount > this.referralHopLimit) {
              LimitExceededException limitExceededException = new LimitExceededException("Referral limit exceeded");
              limitExceededException.setRootCause(ldapReferralException);
              throw limitExceededException;
            } 
            return;
          } 
        } else {
          return;
        } 
        invalidNameException.setResolvedName(paramName1);
        invalidNameException.setResolvedObj(paramObject);
        invalidNameException.setRemainingName(paramName2);
        throw invalidNameException;
      case 10:
        if (this.handleReferrals == 3) {
          invalidNameException = new PartialResultException(str);
        } else {
          Vector vector;
          ldapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, this.handleReferrals, this.reqCtls);
          if (paramLdapResult.referrals == null) {
            vector = null;
          } else if (this.handleReferrals == 4) {
            vector = new Vector();
            for (String str1 : (Vector)paramLdapResult.referrals.elementAt(0)) {
              if (str1.startsWith("ldap:"))
                vector.add(str1); 
            } 
            if (vector.isEmpty())
              vector = null; 
          } else {
            vector = (Vector)paramLdapResult.referrals.elementAt(0);
          } 
          ldapReferralException.setReferralInfo(vector, false);
          if (this.hopCount > 1)
            ldapReferralException.setHopCount(this.hopCount); 
          if (this.hopCount > this.referralHopLimit) {
            LimitExceededException limitExceededException2 = new LimitExceededException("Referral limit exceeded");
            limitExceededException2.setRootCause(ldapReferralException);
            LimitExceededException limitExceededException1 = limitExceededException2;
          } else {
            invalidNameException = ldapReferralException;
          } 
        } 
        invalidNameException.setResolvedName(paramName1);
        invalidNameException.setResolvedObj(paramObject);
        invalidNameException.setRemainingName(paramName2);
        throw invalidNameException;
      case 9:
        if (this.handleReferrals == 3) {
          PartialResultException partialResultException = new PartialResultException(str);
        } else {
          if (paramLdapResult.errorMessage != null && !paramLdapResult.errorMessage.equals("")) {
            paramLdapResult.referrals = extractURLs(paramLdapResult.errorMessage);
          } else {
            PartialResultException partialResultException = new PartialResultException(str);
            partialResultException.setResolvedName(paramName1);
            partialResultException.setResolvedObj(paramObject);
            partialResultException.setRemainingName(paramName2);
            throw partialResultException;
          } 
          ldapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, this.handleReferrals, this.reqCtls);
          if (this.hopCount > 1)
            ldapReferralException.setHopCount(this.hopCount); 
          if ((paramLdapResult.entries == null || paramLdapResult.entries.isEmpty()) && paramLdapResult.referrals != null && paramLdapResult.referrals.size() == 1) {
            ldapReferralException.setReferralInfo(paramLdapResult.referrals, false);
            if (this.hopCount > this.referralHopLimit) {
              LimitExceededException limitExceededException2 = new LimitExceededException("Referral limit exceeded");
              limitExceededException2.setRootCause(ldapReferralException);
              LimitExceededException limitExceededException1 = limitExceededException2;
            } else {
              invalidNameException = ldapReferralException;
            } 
          } else {
            ldapReferralException.setReferralInfo(paramLdapResult.referrals, true);
            paramLdapResult.refEx = ldapReferralException;
            return;
          } 
        } 
        invalidNameException.setResolvedName(paramName1);
        invalidNameException.setResolvedObj(paramObject);
        invalidNameException.setRemainingName(paramName2);
        throw invalidNameException;
      case 34:
      case 64:
        if (paramName2 != null) {
          InvalidNameException invalidNameException1 = new InvalidNameException(paramName2.toString() + ": " + str);
        } else {
          invalidNameException = new InvalidNameException(str);
        } 
        invalidNameException.setResolvedName(paramName1);
        invalidNameException.setResolvedObj(paramObject);
        invalidNameException.setRemainingName(paramName2);
        throw invalidNameException;
    } 
    NamingException namingException = mapErrorCode(paramLdapResult.status, paramLdapResult.errorMessage);
    namingException.setResolvedName(paramName1);
    namingException.setResolvedObj(paramObject);
    namingException.setRemainingName(paramName2);
    throw namingException;
  }
  
  public static NamingException mapErrorCode(int paramInt, String paramString) {
    if (paramInt == 0)
      return null; 
    null = null;
    String str = LdapClient.getErrorMessage(paramInt, paramString);
    switch (paramInt) {
      case 36:
        return new NamingException(str);
      case 33:
        return new NamingException(str);
      case 20:
        return new AttributeInUseException(str);
      case 7:
      case 8:
      case 13:
      case 48:
        return new AuthenticationNotSupportedException(str);
      case 68:
        return new NameAlreadyBoundException(str);
      case 14:
      case 49:
        return new AuthenticationException(str);
      case 18:
        return new InvalidSearchFilterException(str);
      case 50:
        return new NoPermissionException(str);
      case 19:
      case 21:
        return new InvalidAttributeValueException(str);
      case 54:
        return new NamingException(str);
      case 16:
        return new NoSuchAttributeException(str);
      case 32:
        return new NameNotFoundException(str);
      case 65:
      case 67:
      case 69:
        return new SchemaViolationException(str);
      case 66:
        return new ContextNotEmptyException(str);
      case 1:
        return new NamingException(str);
      case 80:
        return new NamingException(str);
      case 2:
        return new CommunicationException(str);
      case 4:
        return new SizeLimitExceededException(str);
      case 3:
        return new TimeLimitExceededException(str);
      case 12:
        return new OperationNotSupportedException(str);
      case 51:
      case 52:
        return new ServiceUnavailableException(str);
      case 17:
        return new InvalidAttributeIdentifierException(str);
      case 53:
        return new OperationNotSupportedException(str);
      case 5:
      case 6:
      case 35:
        return new NamingException(str);
      case 11:
        return new LimitExceededException(str);
      case 10:
        return new NamingException(str);
      case 9:
        return new NamingException(str);
      case 34:
      case 64:
        return new InvalidNameException(str);
    } 
    return new NamingException(str);
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest) throws NamingException {
    boolean bool = paramExtendedRequest.getID().equals("1.3.6.1.4.1.1466.20037");
    ensureOpen(bool);
    try {
      LdapResult ldapResult = this.clnt.extendedOp(paramExtendedRequest.getID(), paramExtendedRequest.getEncodedValue(), this.reqCtls, bool);
      this.respCtls = ldapResult.resControls;
      if (ldapResult.status != 0)
        processReturnCode(ldapResult, new CompositeName()); 
      byte b = (ldapResult.extensionValue == null) ? 0 : ldapResult.extensionValue.length;
      ExtendedResponse extendedResponse = paramExtendedRequest.createExtendedResponse(ldapResult.extensionId, ldapResult.extensionValue, 0, b);
      if (extendedResponse instanceof StartTlsResponseImpl) {
        String str = (String)((this.envprops != null) ? this.envprops.get("com.sun.jndi.ldap.domainname") : null);
        ((StartTlsResponseImpl)extendedResponse).setConnection(this.clnt.conn, str);
      } 
      return extendedResponse;
    } catch (LdapReferralException ldapReferralException) {
      if (this.handleReferrals == 2)
        throw ldapReferralException; 
      while (true) {
        ldapReferralContext = (LdapReferralContext)ldapReferralException.getReferralContext(this.envprops, this.bindCtls);
        try {
          return ldapReferralContext.extendedOperation(paramExtendedRequest);
        } catch (LdapReferralException ldapReferralException1) {
          ldapReferralException = ldapReferralException1;
        } finally {
          ldapReferralContext.close();
        } 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException(iOException.getMessage());
      communicationException.setRootCause(iOException);
      throw communicationException;
    } 
  }
  
  public void setRequestControls(Control[] paramArrayOfControl) throws NamingException {
    if (this.handleReferrals == 3) {
      this.reqCtls = addControl(paramArrayOfControl, manageReferralControl);
    } else {
      this.reqCtls = cloneControls(paramArrayOfControl);
    } 
  }
  
  public Control[] getRequestControls() throws NamingException { return cloneControls(this.reqCtls); }
  
  public Control[] getConnectControls() throws NamingException { return cloneControls(this.bindCtls); }
  
  public Control[] getResponseControls() throws NamingException { return (this.respCtls != null) ? convertControls(this.respCtls) : null; }
  
  Control[] convertControls(Vector<Control> paramVector) throws NamingException {
    int i = paramVector.size();
    if (i == 0)
      return null; 
    Control[] arrayOfControl = new Control[i];
    for (byte b = 0; b < i; b++) {
      arrayOfControl[b] = myResponseControlFactory.getControlInstance((Control)paramVector.elementAt(b));
      if (arrayOfControl[b] == null)
        arrayOfControl[b] = ControlFactory.getControlInstance((Control)paramVector.elementAt(b), this, this.envprops); 
    } 
    return arrayOfControl;
  }
  
  private static Control[] addControl(Control[] paramArrayOfControl, Control paramControl) {
    if (paramArrayOfControl == null)
      return new Control[] { paramControl }; 
    int i = findControl(paramArrayOfControl, paramControl);
    if (i != -1)
      return paramArrayOfControl; 
    Control[] arrayOfControl = new Control[paramArrayOfControl.length + 1];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
    arrayOfControl[paramArrayOfControl.length] = paramControl;
    return arrayOfControl;
  }
  
  private static int findControl(Control[] paramArrayOfControl, Control paramControl) {
    for (byte b = 0; b < paramArrayOfControl.length; b++) {
      if (paramArrayOfControl[b] == paramControl)
        return b; 
    } 
    return -1;
  }
  
  private static Control[] removeControl(Control[] paramArrayOfControl, Control paramControl) {
    if (paramArrayOfControl == null)
      return null; 
    int i = findControl(paramArrayOfControl, paramControl);
    if (i == -1)
      return paramArrayOfControl; 
    Control[] arrayOfControl = new Control[paramArrayOfControl.length - 1];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, i);
    System.arraycopy(paramArrayOfControl, i + 1, arrayOfControl, i, paramArrayOfControl.length - i - 1);
    return arrayOfControl;
  }
  
  private static Control[] cloneControls(Control[] paramArrayOfControl) {
    if (paramArrayOfControl == null)
      return null; 
    Control[] arrayOfControl = new Control[paramArrayOfControl.length];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
    return arrayOfControl;
  }
  
  public void addNamingListener(Name paramName, int paramInt, NamingListener paramNamingListener) throws NamingException { addNamingListener(getTargetName(paramName), paramInt, paramNamingListener); }
  
  public void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener) throws NamingException {
    if (this.eventSupport == null)
      this.eventSupport = new EventSupport(this); 
    this.eventSupport.addNamingListener(getTargetName(new CompositeName(paramString)), paramInt, paramNamingListener);
    if (paramNamingListener instanceof javax.naming.ldap.UnsolicitedNotificationListener && !this.unsolicited)
      addUnsolicited(); 
  }
  
  public void removeNamingListener(NamingListener paramNamingListener) throws NamingException {
    if (this.eventSupport == null)
      return; 
    this.eventSupport.removeNamingListener(paramNamingListener);
    if (paramNamingListener instanceof javax.naming.ldap.UnsolicitedNotificationListener && !this.eventSupport.hasUnsolicited())
      removeUnsolicited(); 
  }
  
  public void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException {
    if (this.eventSupport == null)
      this.eventSupport = new EventSupport(this); 
    this.eventSupport.addNamingListener(getTargetName(new CompositeName(paramString1)), paramString2, cloneSearchControls(paramSearchControls), paramNamingListener);
    if (paramNamingListener instanceof javax.naming.ldap.UnsolicitedNotificationListener && !this.unsolicited)
      addUnsolicited(); 
  }
  
  public void addNamingListener(Name paramName, String paramString, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException { addNamingListener(getTargetName(paramName), paramString, paramSearchControls, paramNamingListener); }
  
  public void addNamingListener(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException { addNamingListener(getTargetName(paramName), paramString, paramArrayOfObject, paramSearchControls, paramNamingListener); }
  
  public void addNamingListener(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException {
    String str = SearchFilter.format(paramString2, paramArrayOfObject);
    addNamingListener(getTargetName(new CompositeName(paramString1)), str, paramSearchControls, paramNamingListener);
  }
  
  public boolean targetMustExist() { return true; }
  
  private static String getTargetName(Name paramName) {
    if (paramName instanceof CompositeName) {
      if (paramName.size() > 1)
        throw new InvalidNameException("Target cannot span multiple namespaces: " + paramName); 
      return paramName.isEmpty() ? "" : paramName.get(0);
    } 
    return paramName.toString();
  }
  
  private void addUnsolicited() throws NamingException {
    ensureOpen();
    synchronized (this.eventSupport) {
      this.clnt.addUnsolicited(this);
      this.unsolicited = true;
    } 
  }
  
  private void removeUnsolicited() throws NamingException {
    if (this.eventSupport == null)
      return; 
    synchronized (this.eventSupport) {
      if (this.unsolicited && this.clnt != null)
        this.clnt.removeUnsolicited(this); 
      this.unsolicited = false;
    } 
  }
  
  void fireUnsolicited(Object paramObject) {
    synchronized (this.eventSupport) {
      if (this.unsolicited) {
        this.eventSupport.fireUnsolicited(paramObject);
        if (paramObject instanceof NamingException)
          this.unsolicited = false; 
      } 
    } 
  }
  
  static  {
    EMPTY_SCHEMA.setReadOnly(new SchemaViolationException("Cannot update schema object"));
  }
  
  static final class SearchArgs {
    Name name;
    
    String filter;
    
    SearchControls cons;
    
    String[] reqAttrs;
    
    SearchArgs(Name param1Name, String param1String, SearchControls param1SearchControls, String[] param1ArrayOfString) {
      this.name = param1Name;
      this.filter = param1String;
      this.cons = param1SearchControls;
      this.reqAttrs = param1ArrayOfString;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */