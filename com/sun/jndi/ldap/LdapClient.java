package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.sasl.LdapSasl;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.ldap.Control;

public final class LdapClient implements PooledConnection {
  private static final int debug = 0;
  
  static final boolean caseIgnore = true;
  
  private static final Hashtable<String, Boolean> defaultBinaryAttrs = new Hashtable(23, 0.75F);
  
  private static final String DISCONNECT_OID = "1.3.6.1.4.1.1466.20036";
  
  boolean isLdapv3;
  
  int referenceCount = 1;
  
  Connection conn;
  
  private final PoolCallback pcb;
  
  private final boolean pooled;
  
  private boolean authenticateCalled = false;
  
  static final int SCOPE_BASE_OBJECT = 0;
  
  static final int SCOPE_ONE_LEVEL = 1;
  
  static final int SCOPE_SUBTREE = 2;
  
  static final int ADD = 0;
  
  static final int DELETE = 1;
  
  static final int REPLACE = 2;
  
  static final int LDAP_VERSION3_VERSION2 = 32;
  
  static final int LDAP_VERSION2 = 2;
  
  static final int LDAP_VERSION3 = 3;
  
  static final int LDAP_VERSION = 3;
  
  static final int LDAP_REF_FOLLOW = 1;
  
  static final int LDAP_REF_THROW = 2;
  
  static final int LDAP_REF_IGNORE = 3;
  
  static final int LDAP_REF_FOLLOW_SCHEME = 4;
  
  static final String LDAP_URL = "ldap://";
  
  static final String LDAPS_URL = "ldaps://";
  
  static final int LBER_BOOLEAN = 1;
  
  static final int LBER_INTEGER = 2;
  
  static final int LBER_BITSTRING = 3;
  
  static final int LBER_OCTETSTRING = 4;
  
  static final int LBER_NULL = 5;
  
  static final int LBER_ENUMERATED = 10;
  
  static final int LBER_SEQUENCE = 48;
  
  static final int LBER_SET = 49;
  
  static final int LDAP_SUPERIOR_DN = 128;
  
  static final int LDAP_REQ_BIND = 96;
  
  static final int LDAP_REQ_UNBIND = 66;
  
  static final int LDAP_REQ_SEARCH = 99;
  
  static final int LDAP_REQ_MODIFY = 102;
  
  static final int LDAP_REQ_ADD = 104;
  
  static final int LDAP_REQ_DELETE = 74;
  
  static final int LDAP_REQ_MODRDN = 108;
  
  static final int LDAP_REQ_COMPARE = 110;
  
  static final int LDAP_REQ_ABANDON = 80;
  
  static final int LDAP_REQ_EXTENSION = 119;
  
  static final int LDAP_REP_BIND = 97;
  
  static final int LDAP_REP_SEARCH = 100;
  
  static final int LDAP_REP_SEARCH_REF = 115;
  
  static final int LDAP_REP_RESULT = 101;
  
  static final int LDAP_REP_MODIFY = 103;
  
  static final int LDAP_REP_ADD = 105;
  
  static final int LDAP_REP_DELETE = 107;
  
  static final int LDAP_REP_MODRDN = 109;
  
  static final int LDAP_REP_COMPARE = 111;
  
  static final int LDAP_REP_EXTENSION = 120;
  
  static final int LDAP_REP_REFERRAL = 163;
  
  static final int LDAP_REP_EXT_OID = 138;
  
  static final int LDAP_REP_EXT_VAL = 139;
  
  static final int LDAP_CONTROLS = 160;
  
  static final String LDAP_CONTROL_MANAGE_DSA_IT = "2.16.840.1.113730.3.4.2";
  
  static final String LDAP_CONTROL_PREFERRED_LANG = "1.3.6.1.4.1.1466.20035";
  
  static final String LDAP_CONTROL_PAGED_RESULTS = "1.2.840.113556.1.4.319";
  
  static final String LDAP_CONTROL_SERVER_SORT_REQ = "1.2.840.113556.1.4.473";
  
  static final String LDAP_CONTROL_SERVER_SORT_RES = "1.2.840.113556.1.4.474";
  
  static final int LDAP_SUCCESS = 0;
  
  static final int LDAP_OPERATIONS_ERROR = 1;
  
  static final int LDAP_PROTOCOL_ERROR = 2;
  
  static final int LDAP_TIME_LIMIT_EXCEEDED = 3;
  
  static final int LDAP_SIZE_LIMIT_EXCEEDED = 4;
  
  static final int LDAP_COMPARE_FALSE = 5;
  
  static final int LDAP_COMPARE_TRUE = 6;
  
  static final int LDAP_AUTH_METHOD_NOT_SUPPORTED = 7;
  
  static final int LDAP_STRONG_AUTH_REQUIRED = 8;
  
  static final int LDAP_PARTIAL_RESULTS = 9;
  
  static final int LDAP_REFERRAL = 10;
  
  static final int LDAP_ADMIN_LIMIT_EXCEEDED = 11;
  
  static final int LDAP_UNAVAILABLE_CRITICAL_EXTENSION = 12;
  
  static final int LDAP_CONFIDENTIALITY_REQUIRED = 13;
  
  static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
  
  static final int LDAP_NO_SUCH_ATTRIBUTE = 16;
  
  static final int LDAP_UNDEFINED_ATTRIBUTE_TYPE = 17;
  
  static final int LDAP_INAPPROPRIATE_MATCHING = 18;
  
  static final int LDAP_CONSTRAINT_VIOLATION = 19;
  
  static final int LDAP_ATTRIBUTE_OR_VALUE_EXISTS = 20;
  
  static final int LDAP_INVALID_ATTRIBUTE_SYNTAX = 21;
  
  static final int LDAP_NO_SUCH_OBJECT = 32;
  
  static final int LDAP_ALIAS_PROBLEM = 33;
  
  static final int LDAP_INVALID_DN_SYNTAX = 34;
  
  static final int LDAP_IS_LEAF = 35;
  
  static final int LDAP_ALIAS_DEREFERENCING_PROBLEM = 36;
  
  static final int LDAP_INAPPROPRIATE_AUTHENTICATION = 48;
  
  static final int LDAP_INVALID_CREDENTIALS = 49;
  
  static final int LDAP_INSUFFICIENT_ACCESS_RIGHTS = 50;
  
  static final int LDAP_BUSY = 51;
  
  static final int LDAP_UNAVAILABLE = 52;
  
  static final int LDAP_UNWILLING_TO_PERFORM = 53;
  
  static final int LDAP_LOOP_DETECT = 54;
  
  static final int LDAP_NAMING_VIOLATION = 64;
  
  static final int LDAP_OBJECT_CLASS_VIOLATION = 65;
  
  static final int LDAP_NOT_ALLOWED_ON_NON_LEAF = 66;
  
  static final int LDAP_NOT_ALLOWED_ON_RDN = 67;
  
  static final int LDAP_ENTRY_ALREADY_EXISTS = 68;
  
  static final int LDAP_OBJECT_CLASS_MODS_PROHIBITED = 69;
  
  static final int LDAP_AFFECTS_MULTIPLE_DSAS = 71;
  
  static final int LDAP_OTHER = 80;
  
  static final String[] ldap_error_message;
  
  private Vector<LdapCtx> unsolicited = new Vector(3);
  
  LdapClient(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, PoolCallback paramPoolCallback) throws NamingException {
    this.conn = new Connection(this, paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream);
    this.pcb = paramPoolCallback;
    this.pooled = (paramPoolCallback != null);
  }
  
  boolean authenticateCalled() { return this.authenticateCalled; }
  
  LdapResult authenticate(boolean paramBoolean, String paramString1, Object paramObject, int paramInt, String paramString2, Control[] paramArrayOfControl, Hashtable<?, ?> paramHashtable) throws NamingException {
    i = this.conn.readTimeout;
    this.conn.readTimeout = this.conn.connectTimeout;
    LdapResult ldapResult = null;
    try {
      this.authenticateCalled = true;
      try {
        ensureOpen();
      } catch (IOException iOException) {
        CommunicationException communicationException = new CommunicationException();
        communicationException.setRootCause(iOException);
        throw communicationException;
      } 
      switch (paramInt) {
        case 3:
        case 32:
          this.isLdapv3 = true;
          break;
        case 2:
          this.isLdapv3 = false;
          break;
        default:
          throw new CommunicationException("Protocol version " + paramInt + " not supported");
      } 
      if (paramString2.equalsIgnoreCase("none") || paramString2.equalsIgnoreCase("anonymous")) {
        if (!paramBoolean || paramInt == 2 || paramInt == 32 || (paramArrayOfControl != null && paramArrayOfControl.length > 0)) {
          try {
            ldapResult = ldapBind(paramString1 = null, (byte[])(paramObject = null), paramArrayOfControl, null, false);
            if (ldapResult.status == 0)
              this.conn.setBound(); 
          } catch (IOException iOException) {
            CommunicationException communicationException = new CommunicationException("anonymous bind failed: " + this.conn.host + ":" + this.conn.port);
            communicationException.setRootCause(iOException);
            throw communicationException;
          } 
        } else {
          ldapResult = new LdapResult();
          ldapResult.status = 0;
        } 
      } else if (paramString2.equalsIgnoreCase("simple")) {
        arrayOfByte = null;
        try {
          arrayOfByte = encodePassword(paramObject, this.isLdapv3);
          ldapResult = ldapBind(paramString1, arrayOfByte, paramArrayOfControl, null, false);
          if (ldapResult.status == 0)
            this.conn.setBound(); 
        } catch (IOException iOException) {
          CommunicationException communicationException = new CommunicationException("simple bind failed: " + this.conn.host + ":" + this.conn.port);
          communicationException.setRootCause(iOException);
          throw communicationException;
        } finally {
          if (arrayOfByte != paramObject && arrayOfByte != null)
            for (byte b = 0; b < arrayOfByte.length; b++)
              arrayOfByte[b] = 0;  
        } 
      } else if (this.isLdapv3) {
        try {
          ldapResult = LdapSasl.saslBind(this, this.conn, this.conn.host, paramString1, paramObject, paramString2, paramHashtable, paramArrayOfControl);
          if (ldapResult.status == 0)
            this.conn.setBound(); 
        } catch (IOException iOException) {
          CommunicationException communicationException = new CommunicationException("SASL bind failed: " + this.conn.host + ":" + this.conn.port);
          communicationException.setRootCause(iOException);
          throw communicationException;
        } 
      } else {
        throw new AuthenticationNotSupportedException(paramString2);
      } 
      if (paramBoolean && ldapResult.status == 2 && paramInt == 32 && (paramString2.equalsIgnoreCase("none") || paramString2.equalsIgnoreCase("anonymous") || paramString2.equalsIgnoreCase("simple"))) {
        arrayOfByte = null;
        try {
          this.isLdapv3 = false;
          arrayOfByte = encodePassword(paramObject, false);
          ldapResult = ldapBind(paramString1, arrayOfByte, paramArrayOfControl, null, false);
          if (ldapResult.status == 0)
            this.conn.setBound(); 
        } catch (IOException iOException) {
          CommunicationException communicationException = new CommunicationException(paramString2 + ":" + this.conn.host + ":" + this.conn.port);
          communicationException.setRootCause(iOException);
          throw communicationException;
        } finally {
          if (arrayOfByte != paramObject && arrayOfByte != null)
            for (byte b = 0; b < arrayOfByte.length; b++)
              arrayOfByte[b] = 0;  
        } 
      } 
      if (ldapResult.status == 32)
        throw new AuthenticationException(getErrorMessage(ldapResult.status, ldapResult.errorMessage)); 
      this.conn.setV3(this.isLdapv3);
      return ldapResult;
    } finally {
      this.conn.readTimeout = i;
    } 
  }
  
  public LdapResult ldapBind(String paramString1, byte[] paramArrayOfByte, Control[] paramArrayOfControl, String paramString2, boolean paramBoolean) throws IOException, NamingException {
    ensureOpen();
    this.conn.abandonOutstandingReqs(null);
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(96);
    berEncoder.encodeInt(this.isLdapv3 ? 3 : 2);
    berEncoder.encodeString(paramString1, this.isLdapv3);
    if (paramString2 != null) {
      berEncoder.beginSeq(163);
      berEncoder.encodeString(paramString2, this.isLdapv3);
      if (paramArrayOfByte != null)
        berEncoder.encodeOctetString(paramArrayOfByte, 4); 
      berEncoder.endSeq();
    } else if (paramArrayOfByte != null) {
      berEncoder.encodeOctetString(paramArrayOfByte, 128);
    } else {
      berEncoder.encodeOctetString(null, 128, 0, 0);
    } 
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i, paramBoolean);
    if (paramArrayOfByte != null)
      berEncoder.reset(); 
    BerDecoder berDecoder = this.conn.readReply(ldapRequest);
    berDecoder.parseSeq(null);
    berDecoder.parseInt();
    if (berDecoder.parseByte() != 97)
      return ldapResult; 
    berDecoder.parseLength();
    parseResult(berDecoder, ldapResult, this.isLdapv3);
    if (this.isLdapv3 && berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 135)
      ldapResult.serverCreds = berDecoder.parseOctetString(135, null); 
    ldapResult.resControls = this.isLdapv3 ? parseControls(berDecoder) : null;
    this.conn.removeRequest(ldapRequest);
    return ldapResult;
  }
  
  boolean usingSaslStreams() { return this.conn.inStream instanceof com.sun.jndi.ldap.sasl.SaslInputStream; }
  
  void incRefCount() { this.referenceCount++; }
  
  private static byte[] encodePassword(Object paramObject, boolean paramBoolean) throws IOException {
    if (paramObject instanceof char[])
      paramObject = new String((char[])paramObject); 
    return (paramObject instanceof String) ? (paramBoolean ? ((String)paramObject).getBytes("UTF8") : ((String)paramObject).getBytes("8859_1")) : (byte[])paramObject;
  }
  
  void close(Control[] paramArrayOfControl, boolean paramBoolean) {
    this.referenceCount--;
    if (this.referenceCount <= 0 && this.conn != null)
      if (!this.pooled) {
        this.conn.cleanup(paramArrayOfControl, false);
        this.conn = null;
      } else if (paramBoolean) {
        this.conn.cleanup(paramArrayOfControl, false);
        this.conn = null;
        this.pcb.removePooledConnection(this);
      } else {
        this.pcb.releasePooledConnection(this);
      }  
  }
  
  private void forceClose(boolean paramBoolean) {
    this.referenceCount = 0;
    if (this.conn != null) {
      this.conn.cleanup(null, false);
      this.conn = null;
      if (paramBoolean)
        this.pcb.removePooledConnection(this); 
    } 
  }
  
  protected void finalize() { forceClose(this.pooled); }
  
  public void closeConnection() { forceClose(false); }
  
  void processConnectionClosure() {
    if (this.unsolicited.size() > 0) {
      String str;
      if (this.conn != null) {
        str = this.conn.host + ":" + this.conn.port + " connection closed";
      } else {
        str = "Connection closed";
      } 
      notifyUnsolicited(new CommunicationException(str));
    } 
    if (this.pooled)
      this.pcb.removePooledConnection(this); 
  }
  
  LdapResult search(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, String[] paramArrayOfString, String paramString2, int paramInt5, Control[] paramArrayOfControl, Hashtable<String, Boolean> paramHashtable, boolean paramBoolean2, int paramInt6) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(99);
    berEncoder.encodeString((paramString1 == null) ? "" : paramString1, this.isLdapv3);
    berEncoder.encodeInt(paramInt1, 10);
    berEncoder.encodeInt(paramInt2, 10);
    berEncoder.encodeInt(paramInt3);
    berEncoder.encodeInt(paramInt4);
    berEncoder.encodeBoolean(paramBoolean1);
    Filter.encodeFilterString(berEncoder, paramString2, this.isLdapv3);
    berEncoder.beginSeq(48);
    berEncoder.encodeStringArray(paramArrayOfString, this.isLdapv3);
    berEncoder.endSeq();
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i, false, paramInt6);
    ldapResult.msgId = i;
    ldapResult.status = 0;
    if (paramBoolean2)
      ldapResult = getSearchReply(ldapRequest, paramInt5, ldapResult, paramHashtable); 
    return ldapResult;
  }
  
  void clearSearchReply(LdapResult paramLdapResult, Control[] paramArrayOfControl) {
    if (paramLdapResult != null && this.conn != null) {
      LdapRequest ldapRequest = this.conn.findRequest(paramLdapResult.msgId);
      if (ldapRequest == null)
        return; 
      if (ldapRequest.hasSearchCompleted()) {
        this.conn.removeRequest(ldapRequest);
      } else {
        this.conn.abandonRequest(ldapRequest, paramArrayOfControl);
      } 
    } 
  }
  
  LdapResult getSearchReply(int paramInt, LdapResult paramLdapResult, Hashtable<String, Boolean> paramHashtable) throws IOException, NamingException {
    ensureOpen();
    LdapRequest ldapRequest;
    return ((ldapRequest = this.conn.findRequest(paramLdapResult.msgId)) == null) ? null : getSearchReply(ldapRequest, paramInt, paramLdapResult, paramHashtable);
  }
  
  private LdapResult getSearchReply(LdapRequest paramLdapRequest, int paramInt, LdapResult paramLdapResult, Hashtable<String, Boolean> paramHashtable) throws IOException, NamingException {
    if (paramInt == 0)
      paramInt = Integer.MAX_VALUE; 
    if (paramLdapResult.entries != null) {
      paramLdapResult.entries.setSize(0);
    } else {
      paramLdapResult.entries = new Vector((paramInt == Integer.MAX_VALUE) ? 32 : paramInt);
    } 
    if (paramLdapResult.referrals != null)
      paramLdapResult.referrals.setSize(0); 
    byte b = 0;
    while (b < paramInt) {
      BerDecoder berDecoder = this.conn.readReply(paramLdapRequest);
      berDecoder.parseSeq(null);
      berDecoder.parseInt();
      int i = berDecoder.parseSeq(null);
      if (i == 100) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        String str = berDecoder.parseString(this.isLdapv3);
        LdapEntry ldapEntry = new LdapEntry(str, basicAttributes);
        int[] arrayOfInt = new int[1];
        berDecoder.parseSeq(arrayOfInt);
        int j = berDecoder.getParsePosition() + arrayOfInt[0];
        while (berDecoder.getParsePosition() < j && berDecoder.bytesLeft() > 0) {
          Attribute attribute = parseAttribute(berDecoder, paramHashtable);
          basicAttributes.put(attribute);
        } 
        ldapEntry.respCtls = this.isLdapv3 ? parseControls(berDecoder) : null;
        paramLdapResult.entries.addElement(ldapEntry);
        b++;
        continue;
      } 
      if (i == 115 && this.isLdapv3) {
        Vector vector = new Vector(4);
        if (berDecoder.peekByte() == 48)
          berDecoder.parseSeq(null); 
        while (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 4)
          vector.addElement(berDecoder.parseString(this.isLdapv3)); 
        if (paramLdapResult.referrals == null)
          paramLdapResult.referrals = new Vector(4); 
        paramLdapResult.referrals.addElement(vector);
        paramLdapResult.resControls = this.isLdapv3 ? parseControls(berDecoder) : null;
        continue;
      } 
      if (i == 120) {
        parseExtResponse(berDecoder, paramLdapResult);
        continue;
      } 
      if (i == 101) {
        parseResult(berDecoder, paramLdapResult, this.isLdapv3);
        paramLdapResult.resControls = this.isLdapv3 ? parseControls(berDecoder) : null;
        this.conn.removeRequest(paramLdapRequest);
        return paramLdapResult;
      } 
    } 
    return paramLdapResult;
  }
  
  private Attribute parseAttribute(BerDecoder paramBerDecoder, Hashtable<String, Boolean> paramHashtable) throws IOException {
    int[] arrayOfInt = new int[1];
    int i = paramBerDecoder.parseSeq(null);
    String str = paramBerDecoder.parseString(this.isLdapv3);
    boolean bool = isBinaryValued(str, paramHashtable);
    LdapAttribute ldapAttribute = new LdapAttribute(str);
    if ((i = paramBerDecoder.parseSeq(arrayOfInt)) == 49) {
      int j = arrayOfInt[0];
      while (paramBerDecoder.bytesLeft() > 0 && j > 0) {
        try {
          j -= parseAttributeValue(paramBerDecoder, ldapAttribute, bool);
        } catch (IOException iOException) {
          paramBerDecoder.seek(j);
          break;
        } 
      } 
    } else {
      paramBerDecoder.seek(arrayOfInt[0]);
    } 
    return ldapAttribute;
  }
  
  private int parseAttributeValue(BerDecoder paramBerDecoder, Attribute paramAttribute, boolean paramBoolean) throws IOException {
    int[] arrayOfInt = new int[1];
    if (paramBoolean) {
      paramAttribute.add(paramBerDecoder.parseOctetString(paramBerDecoder.peekByte(), arrayOfInt));
    } else {
      paramAttribute.add(paramBerDecoder.parseStringWithTag(4, this.isLdapv3, arrayOfInt));
    } 
    return arrayOfInt[0];
  }
  
  private boolean isBinaryValued(String paramString, Hashtable<String, Boolean> paramHashtable) {
    String str = paramString.toLowerCase(Locale.ENGLISH);
    return (str.indexOf(";binary") != -1 || defaultBinaryAttrs.containsKey(str) || (paramHashtable != null && paramHashtable.containsKey(str)));
  }
  
  static void parseResult(BerDecoder paramBerDecoder, LdapResult paramLdapResult, boolean paramBoolean) throws IOException {
    paramLdapResult.status = paramBerDecoder.parseEnumeration();
    paramLdapResult.matchedDN = paramBerDecoder.parseString(paramBoolean);
    paramLdapResult.errorMessage = paramBerDecoder.parseString(paramBoolean);
    if (paramBoolean && paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 163) {
      Vector vector = new Vector(4);
      int[] arrayOfInt = new int[1];
      paramBerDecoder.parseSeq(arrayOfInt);
      int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
      while (paramBerDecoder.getParsePosition() < i && paramBerDecoder.bytesLeft() > 0)
        vector.addElement(paramBerDecoder.parseString(paramBoolean)); 
      if (paramLdapResult.referrals == null)
        paramLdapResult.referrals = new Vector(4); 
      paramLdapResult.referrals.addElement(vector);
    } 
  }
  
  static Vector<Control> parseControls(BerDecoder paramBerDecoder) throws IOException {
    if (paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 160) {
      Vector vector = new Vector(4);
      boolean bool = false;
      byte[] arrayOfByte = null;
      int[] arrayOfInt = new int[1];
      paramBerDecoder.parseSeq(arrayOfInt);
      int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
      while (paramBerDecoder.getParsePosition() < i && paramBerDecoder.bytesLeft() > 0) {
        paramBerDecoder.parseSeq(null);
        String str = paramBerDecoder.parseString(true);
        if (paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 1)
          bool = paramBerDecoder.parseBoolean(); 
        if (paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 4)
          arrayOfByte = paramBerDecoder.parseOctetString(4, null); 
        if (str != null)
          vector.addElement(new BasicControl(str, bool, arrayOfByte)); 
      } 
      return vector;
    } 
    return null;
  }
  
  private void parseExtResponse(BerDecoder paramBerDecoder, LdapResult paramLdapResult) throws IOException {
    parseResult(paramBerDecoder, paramLdapResult, this.isLdapv3);
    if (paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 138)
      paramLdapResult.extensionId = paramBerDecoder.parseStringWithTag(138, this.isLdapv3, null); 
    if (paramBerDecoder.bytesLeft() > 0 && paramBerDecoder.peekByte() == 139)
      paramLdapResult.extensionValue = paramBerDecoder.parseOctetString(139, null); 
    paramLdapResult.resControls = parseControls(paramBerDecoder);
  }
  
  static void encodeControls(BerEncoder paramBerEncoder, Control[] paramArrayOfControl) throws IOException {
    if (paramArrayOfControl == null || paramArrayOfControl.length == 0)
      return; 
    paramBerEncoder.beginSeq(160);
    for (byte b = 0; b < paramArrayOfControl.length; b++) {
      paramBerEncoder.beginSeq(48);
      paramBerEncoder.encodeString(paramArrayOfControl[b].getID(), true);
      if (paramArrayOfControl[b].isCritical())
        paramBerEncoder.encodeBoolean(true); 
      byte[] arrayOfByte;
      if ((arrayOfByte = paramArrayOfControl[b].getEncodedValue()) != null)
        paramBerEncoder.encodeOctetString(arrayOfByte, 4); 
      paramBerEncoder.endSeq();
    } 
    paramBerEncoder.endSeq();
  }
  
  private LdapResult processReply(LdapRequest paramLdapRequest, LdapResult paramLdapResult, int paramInt) throws IOException, NamingException {
    BerDecoder berDecoder = this.conn.readReply(paramLdapRequest);
    berDecoder.parseSeq(null);
    berDecoder.parseInt();
    if (berDecoder.parseByte() != paramInt)
      return paramLdapResult; 
    berDecoder.parseLength();
    parseResult(berDecoder, paramLdapResult, this.isLdapv3);
    paramLdapResult.resControls = this.isLdapv3 ? parseControls(berDecoder) : null;
    this.conn.removeRequest(paramLdapRequest);
    return paramLdapResult;
  }
  
  LdapResult modify(String paramString, int[] paramArrayOfInt, Attribute[] paramArrayOfAttribute, Control[] paramArrayOfControl) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramString == null || paramArrayOfInt.length != paramArrayOfAttribute.length)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(102);
    berEncoder.encodeString(paramString, this.isLdapv3);
    berEncoder.beginSeq(48);
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      berEncoder.beginSeq(48);
      berEncoder.encodeInt(paramArrayOfInt[b], 10);
      if (paramArrayOfInt[b] == 0 && hasNoValue(paramArrayOfAttribute[b]))
        throw new InvalidAttributeValueException("'" + paramArrayOfAttribute[b].getID() + "' has no values."); 
      encodeAttribute(berEncoder, paramArrayOfAttribute[b]);
      berEncoder.endSeq();
    } 
    berEncoder.endSeq();
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i);
    return processReply(ldapRequest, ldapResult, 103);
  }
  
  private void encodeAttribute(BerEncoder paramBerEncoder, Attribute paramAttribute) throws IOException, NamingException {
    paramBerEncoder.beginSeq(48);
    paramBerEncoder.encodeString(paramAttribute.getID(), this.isLdapv3);
    paramBerEncoder.beginSeq(49);
    NamingEnumeration namingEnumeration = paramAttribute.getAll();
    while (namingEnumeration.hasMore()) {
      Object object = namingEnumeration.next();
      if (object instanceof String) {
        paramBerEncoder.encodeString((String)object, this.isLdapv3);
        continue;
      } 
      if (object instanceof byte[]) {
        paramBerEncoder.encodeOctetString((byte[])object, 4);
        continue;
      } 
      if (object == null)
        continue; 
      throw new InvalidAttributeValueException("Malformed '" + paramAttribute.getID() + "' attribute value");
    } 
    paramBerEncoder.endSeq();
    paramBerEncoder.endSeq();
  }
  
  private static boolean hasNoValue(Attribute paramAttribute) throws NamingException { return (paramAttribute.size() == 0 || (paramAttribute.size() == 1 && paramAttribute.get() == null)); }
  
  LdapResult add(LdapEntry paramLdapEntry, Control[] paramArrayOfControl) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramLdapEntry == null || paramLdapEntry.DN == null)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(104);
    berEncoder.encodeString(paramLdapEntry.DN, this.isLdapv3);
    berEncoder.beginSeq(48);
    NamingEnumeration namingEnumeration = paramLdapEntry.attributes.getAll();
    while (namingEnumeration.hasMore()) {
      Attribute attribute = (Attribute)namingEnumeration.next();
      if (hasNoValue(attribute))
        throw new InvalidAttributeValueException("'" + attribute.getID() + "' has no values."); 
      encodeAttribute(berEncoder, attribute);
    } 
    berEncoder.endSeq();
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i);
    return processReply(ldapRequest, ldapResult, 105);
  }
  
  LdapResult delete(String paramString, Control[] paramArrayOfControl) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramString == null)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.encodeString(paramString, 74, this.isLdapv3);
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i);
    return processReply(ldapRequest, ldapResult, 107);
  }
  
  LdapResult moddn(String paramString1, String paramString2, boolean paramBoolean, String paramString3, Control[] paramArrayOfControl) throws IOException, NamingException {
    ensureOpen();
    boolean bool = (paramString3 != null && paramString3.length() > 0) ? 1 : 0;
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramString1 == null || paramString2 == null)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(108);
    berEncoder.encodeString(paramString1, this.isLdapv3);
    berEncoder.encodeString(paramString2, this.isLdapv3);
    berEncoder.encodeBoolean(paramBoolean);
    if (this.isLdapv3 && bool)
      berEncoder.encodeString(paramString3, 128, this.isLdapv3); 
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i);
    return processReply(ldapRequest, ldapResult, 109);
  }
  
  LdapResult compare(String paramString1, String paramString2, String paramString3, Control[] paramArrayOfControl) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramString1 == null || paramString2 == null || paramString3 == null)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(110);
    berEncoder.encodeString(paramString1, this.isLdapv3);
    berEncoder.beginSeq(48);
    berEncoder.encodeString(paramString2, this.isLdapv3);
    byte[] arrayOfByte = this.isLdapv3 ? paramString3.getBytes("UTF8") : paramString3.getBytes("8859_1");
    berEncoder.encodeOctetString(Filter.unescapeFilterValue(arrayOfByte, 0, arrayOfByte.length), 4);
    berEncoder.endSeq();
    berEncoder.endSeq();
    if (this.isLdapv3)
      encodeControls(berEncoder, paramArrayOfControl); 
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i);
    return processReply(ldapRequest, ldapResult, 111);
  }
  
  LdapResult extendedOp(String paramString, byte[] paramArrayOfByte, Control[] paramArrayOfControl, boolean paramBoolean) throws IOException, NamingException {
    ensureOpen();
    LdapResult ldapResult = new LdapResult();
    ldapResult.status = 1;
    if (paramString == null)
      return ldapResult; 
    BerEncoder berEncoder = new BerEncoder();
    int i = this.conn.getMsgId();
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(i);
    berEncoder.beginSeq(119);
    berEncoder.encodeString(paramString, 128, this.isLdapv3);
    if (paramArrayOfByte != null)
      berEncoder.encodeOctetString(paramArrayOfByte, 129); 
    berEncoder.endSeq();
    encodeControls(berEncoder, paramArrayOfControl);
    berEncoder.endSeq();
    LdapRequest ldapRequest = this.conn.writeRequest(berEncoder, i, paramBoolean);
    BerDecoder berDecoder = this.conn.readReply(ldapRequest);
    berDecoder.parseSeq(null);
    berDecoder.parseInt();
    if (berDecoder.parseByte() != 120)
      return ldapResult; 
    berDecoder.parseLength();
    parseExtResponse(berDecoder, ldapResult);
    this.conn.removeRequest(ldapRequest);
    return ldapResult;
  }
  
  static String getErrorMessage(int paramInt, String paramString) {
    String str = "[LDAP: error code " + paramInt;
    if (paramString != null && paramString.length() != 0) {
      str = str + " - " + paramString + "]";
    } else {
      try {
        if (ldap_error_message[paramInt] != null)
          str = str + " - " + ldap_error_message[paramInt] + "]"; 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        str = str + "]";
      } 
    } 
    return str;
  }
  
  void addUnsolicited(LdapCtx paramLdapCtx) { this.unsolicited.addElement(paramLdapCtx); }
  
  void removeUnsolicited(LdapCtx paramLdapCtx) { this.unsolicited.removeElement(paramLdapCtx); }
  
  void processUnsolicited(BerDecoder paramBerDecoder) {
    try {
      LdapResult ldapResult = new LdapResult();
      paramBerDecoder.parseSeq(null);
      paramBerDecoder.parseInt();
      if (paramBerDecoder.parseByte() != 120)
        throw new IOException("Unsolicited Notification must be an Extended Response"); 
      paramBerDecoder.parseLength();
      parseExtResponse(paramBerDecoder, ldapResult);
      if ("1.3.6.1.4.1.1466.20036".equals(ldapResult.extensionId))
        forceClose(this.pooled); 
      LdapCtx ldapCtx = null;
      UnsolicitedResponseImpl unsolicitedResponseImpl = null;
      synchronized (this.unsolicited) {
        if (this.unsolicited.size() > 0) {
          ldapCtx = (LdapCtx)this.unsolicited.elementAt(0);
          unsolicitedResponseImpl = new UnsolicitedResponseImpl(ldapResult.extensionId, ldapResult.extensionValue, ldapResult.referrals, ldapResult.status, ldapResult.errorMessage, ldapResult.matchedDN, (ldapResult.resControls != null) ? ldapCtx.convertControls(ldapResult.resControls) : null);
        } 
      } 
      if (unsolicitedResponseImpl != null) {
        notifyUnsolicited(unsolicitedResponseImpl);
        if ("1.3.6.1.4.1.1466.20036".equals(ldapResult.extensionId))
          notifyUnsolicited(new CommunicationException("Connection closed")); 
      } 
    } catch (IOException iOException) {
      CommunicationException communicationException = new CommunicationException("Problem parsing unsolicited notification");
      communicationException.setRootCause(iOException);
      notifyUnsolicited(communicationException);
    } catch (NamingException namingException) {
      notifyUnsolicited(namingException);
    } 
  }
  
  private void notifyUnsolicited(Object paramObject) {
    Vector vector;
    synchronized (this.unsolicited) {
      vector = new Vector(this.unsolicited);
      if (paramObject instanceof NamingException)
        this.unsolicited.setSize(0); 
    } 
    for (byte b = 0; b < vector.size(); b++)
      ((LdapCtx)vector.elementAt(b)).fireUnsolicited(paramObject); 
  }
  
  private void ensureOpen() {
    if (this.conn == null || !this.conn.useable) {
      if (this.conn != null && this.conn.closureReason != null)
        throw this.conn.closureReason; 
      throw new IOException("connection closed");
    } 
  }
  
  static LdapClient getInstance(boolean paramBoolean, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, int paramInt4, String paramString3, Control[] paramArrayOfControl, String paramString4, String paramString5, Object paramObject, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramBoolean && LdapPoolManager.isPoolingAllowed(paramString2, paramOutputStream, paramString3, paramString4, paramHashtable)) {
      LdapClient ldapClient = LdapPoolManager.getLdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, paramInt4, paramString3, paramArrayOfControl, paramString4, paramString5, paramObject, paramHashtable);
      ldapClient.referenceCount = 1;
      return ldapClient;
    } 
    return new LdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, null);
  }
  
  static  {
    defaultBinaryAttrs.put("userpassword", Boolean.TRUE);
    defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
    defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
    defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
    defaultBinaryAttrs.put("audio", Boolean.TRUE);
    defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
    defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
    defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);
    defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);
    defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
    defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE);
    defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);
    defaultBinaryAttrs.put("photo", Boolean.TRUE);
    defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
    defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE);
    ldap_error_message = new String[] { 
        "Success", "Operations Error", "Protocol Error", "Timelimit Exceeded", "Sizelimit Exceeded", "Compare False", "Compare True", "Authentication Method Not Supported", "Strong Authentication Required", null, 
        "Referral", "Administrative Limit Exceeded", "Unavailable Critical Extension", "Confidentiality Required", "SASL Bind In Progress", null, "No Such Attribute", "Undefined Attribute Type", "Inappropriate Matching", "Constraint Violation", 
        "Attribute Or Value Exists", "Invalid Attribute Syntax", null, null, null, null, null, null, null, null, 
        null, null, "No Such Object", "Alias Problem", "Invalid DN Syntax", null, "Alias Dereferencing Problem", null, null, null, 
        null, null, null, null, null, null, null, null, "Inappropriate Authentication", "Invalid Credentials", 
        "Insufficient Access Rights", "Busy", "Unavailable", "Unwilling To Perform", "Loop Detect", null, null, null, null, null, 
        null, null, null, null, "Naming Violation", "Object Class Violation", "Not Allowed On Non-leaf", "Not Allowed On RDN", "Entry Already Exists", "Object Class Modifications Prohibited", 
        null, "Affects Multiple DSAs", null, null, null, null, null, null, null, null, 
        "Other", null, null, null, null, null, null, null, null, null, 
        null };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */