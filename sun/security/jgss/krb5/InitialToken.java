package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSToken;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbCred;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

abstract class InitialToken extends Krb5Token {
  private static final int CHECKSUM_TYPE = 32771;
  
  private static final int CHECKSUM_LENGTH_SIZE = 4;
  
  private static final int CHECKSUM_BINDINGS_SIZE = 16;
  
  private static final int CHECKSUM_FLAGS_SIZE = 4;
  
  private static final int CHECKSUM_DELEG_OPT_SIZE = 2;
  
  private static final int CHECKSUM_DELEG_LGTH_SIZE = 2;
  
  private static final int CHECKSUM_DELEG_FLAG = 1;
  
  private static final int CHECKSUM_MUTUAL_FLAG = 2;
  
  private static final int CHECKSUM_REPLAY_FLAG = 4;
  
  private static final int CHECKSUM_SEQUENCE_FLAG = 8;
  
  private static final int CHECKSUM_CONF_FLAG = 16;
  
  private static final int CHECKSUM_INTEG_FLAG = 32;
  
  private final byte[] CHECKSUM_FIRST_BYTES = { 16, 0, 0, 0 };
  
  private static final int CHANNEL_BINDING_AF_INET = 2;
  
  private static final int CHANNEL_BINDING_AF_INET6 = 24;
  
  private static final int CHANNEL_BINDING_AF_NULL_ADDR = 255;
  
  private static final int Inet4_ADDRSZ = 4;
  
  private static final int Inet6_ADDRSZ = 16;
  
  private int getAddrType(InetAddress paramInetAddress) {
    char c = 'ÿ';
    if (paramInetAddress instanceof java.net.Inet4Address) {
      c = '\002';
    } else if (paramInetAddress instanceof java.net.Inet6Address) {
      c = '\030';
    } 
    return c;
  }
  
  private byte[] getAddrBytes(InetAddress paramInetAddress) throws GSSException {
    int i = getAddrType(paramInetAddress);
    byte[] arrayOfByte = paramInetAddress.getAddress();
    if (arrayOfByte != null) {
      switch (i) {
        case 2:
          if (arrayOfByte.length != 4)
            throw new GSSException(11, -1, "Incorrect AF-INET address length in ChannelBinding."); 
          return arrayOfByte;
        case 24:
          if (arrayOfByte.length != 16)
            throw new GSSException(11, -1, "Incorrect AF-INET6 address length in ChannelBinding."); 
          return arrayOfByte;
      } 
      throw new GSSException(11, -1, "Cannot handle non AF-INET addresses in ChannelBinding.");
    } 
    return null;
  }
  
  private byte[] computeChannelBinding(ChannelBinding paramChannelBinding) throws GSSException {
    InetAddress inetAddress1 = paramChannelBinding.getInitiatorAddress();
    InetAddress inetAddress2 = paramChannelBinding.getAcceptorAddress();
    int i = 20;
    int j = getAddrType(inetAddress1);
    int k = getAddrType(inetAddress2);
    byte[] arrayOfByte1 = null;
    if (inetAddress1 != null) {
      arrayOfByte1 = getAddrBytes(inetAddress1);
      i += arrayOfByte1.length;
    } 
    byte[] arrayOfByte2 = null;
    if (inetAddress2 != null) {
      arrayOfByte2 = getAddrBytes(inetAddress2);
      i += arrayOfByte2.length;
    } 
    byte[] arrayOfByte3 = paramChannelBinding.getApplicationData();
    if (arrayOfByte3 != null)
      i += arrayOfByte3.length; 
    byte[] arrayOfByte4 = new byte[i];
    int m = 0;
    writeLittleEndian(j, arrayOfByte4, m);
    m += 4;
    if (arrayOfByte1 != null) {
      writeLittleEndian(arrayOfByte1.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte1, 0, arrayOfByte4, m, arrayOfByte1.length);
      m += arrayOfByte1.length;
    } else {
      m += 4;
    } 
    writeLittleEndian(k, arrayOfByte4, m);
    m += 4;
    if (arrayOfByte2 != null) {
      writeLittleEndian(arrayOfByte2.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte4, m, arrayOfByte2.length);
      m += arrayOfByte2.length;
    } else {
      m += 4;
    } 
    if (arrayOfByte3 != null) {
      writeLittleEndian(arrayOfByte3.length, arrayOfByte4, m);
      m += 4;
      System.arraycopy(arrayOfByte3, 0, arrayOfByte4, m, arrayOfByte3.length);
      m += arrayOfByte3.length;
    } else {
      m += 4;
    } 
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      return messageDigest.digest(arrayOfByte4);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new GSSException(11, -1, "Could not get MD5 Message Digest - " + noSuchAlgorithmException.getMessage());
    } 
  }
  
  public abstract byte[] encode() throws IOException;
  
  protected class OverloadedChecksum {
    private byte[] checksumBytes = null;
    
    private Credentials delegCreds = null;
    
    private int flags = 0;
    
    public OverloadedChecksum(Krb5Context param1Krb5Context, Credentials param1Credentials1, Credentials param1Credentials2) throws KrbException, IOException, GSSException {
      byte[] arrayOfByte1 = null;
      byte b = 0;
      int i = 24;
      if (!param1Credentials1.isForwardable()) {
        param1Krb5Context.setCredDelegState(false);
        param1Krb5Context.setDelegPolicyState(false);
      } else if (param1Krb5Context.getCredDelegState()) {
        if (param1Krb5Context.getDelegPolicyState() && !param1Credentials2.checkDelegate())
          param1Krb5Context.setDelegPolicyState(false); 
      } else if (param1Krb5Context.getDelegPolicyState()) {
        if (param1Credentials2.checkDelegate()) {
          param1Krb5Context.setCredDelegState(true);
        } else {
          param1Krb5Context.setDelegPolicyState(false);
        } 
      } 
      if (param1Krb5Context.getCredDelegState()) {
        KrbCred krbCred = null;
        CipherHelper cipherHelper = param1Krb5Context.getCipherHelper(param1Credentials2.getSessionKey());
        if (useNullKey(cipherHelper)) {
          krbCred = new KrbCred(param1Credentials1, param1Credentials2, EncryptionKey.NULL_KEY);
        } else {
          krbCred = new KrbCred(param1Credentials1, param1Credentials2, param1Credentials2.getSessionKey());
        } 
        arrayOfByte1 = krbCred.getMessage();
        i += 4 + arrayOfByte1.length;
      } 
      this.checksumBytes = new byte[i];
      this.checksumBytes[b++] = this$0.CHECKSUM_FIRST_BYTES[0];
      this.checksumBytes[b++] = this$0.CHECKSUM_FIRST_BYTES[1];
      this.checksumBytes[b++] = this$0.CHECKSUM_FIRST_BYTES[2];
      this.checksumBytes[b++] = this$0.CHECKSUM_FIRST_BYTES[3];
      ChannelBinding channelBinding = param1Krb5Context.getChannelBinding();
      if (channelBinding != null) {
        byte[] arrayOfByte = this$0.computeChannelBinding(param1Krb5Context.getChannelBinding());
        System.arraycopy(arrayOfByte, 0, this.checksumBytes, b, arrayOfByte.length);
      } 
      b += 16;
      if (param1Krb5Context.getCredDelegState())
        this.flags |= 0x1; 
      if (param1Krb5Context.getMutualAuthState())
        this.flags |= 0x2; 
      if (param1Krb5Context.getReplayDetState())
        this.flags |= 0x4; 
      if (param1Krb5Context.getSequenceDetState())
        this.flags |= 0x8; 
      if (param1Krb5Context.getIntegState())
        this.flags |= 0x20; 
      if (param1Krb5Context.getConfState())
        this.flags |= 0x10; 
      byte[] arrayOfByte2 = new byte[4];
      GSSToken.writeLittleEndian(this.flags, arrayOfByte2);
      this.checksumBytes[b++] = arrayOfByte2[0];
      this.checksumBytes[b++] = arrayOfByte2[1];
      this.checksumBytes[b++] = arrayOfByte2[2];
      this.checksumBytes[b++] = arrayOfByte2[3];
      if (param1Krb5Context.getCredDelegState()) {
        PrincipalName principalName = param1Credentials2.getServer();
        StringBuffer stringBuffer = new StringBuffer("\"");
        stringBuffer.append(principalName.getName()).append('"');
        String str = principalName.getRealmAsString();
        stringBuffer.append(" \"krbtgt/").append(str).append('@');
        stringBuffer.append(str).append('"');
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
          DelegationPermission delegationPermission = new DelegationPermission(stringBuffer.toString());
          securityManager.checkPermission(delegationPermission);
        } 
        this.checksumBytes[b++] = 1;
        this.checksumBytes[b++] = 0;
        if (arrayOfByte1.length > 65535)
          throw new GSSException(11, -1, "Incorrect message length"); 
        GSSToken.writeLittleEndian(arrayOfByte1.length, arrayOfByte2);
        this.checksumBytes[b++] = arrayOfByte2[0];
        this.checksumBytes[b++] = arrayOfByte2[1];
        System.arraycopy(arrayOfByte1, 0, this.checksumBytes, b, arrayOfByte1.length);
      } 
    }
    
    public OverloadedChecksum(Krb5Context param1Krb5Context, Checksum param1Checksum, EncryptionKey param1EncryptionKey1, EncryptionKey param1EncryptionKey2) throws GSSException, KrbException, IOException {
      boolean bool = false;
      if (param1Checksum == null) {
        GSSException gSSException = new GSSException(11, -1, "No cksum in AP_REQ's authenticator");
        gSSException.initCause(new KrbException(50));
        throw gSSException;
      } 
      this.checksumBytes = param1Checksum.getBytes();
      if (this.checksumBytes[0] != this$0.CHECKSUM_FIRST_BYTES[0] || this.checksumBytes[1] != this$0.CHECKSUM_FIRST_BYTES[1] || this.checksumBytes[2] != this$0.CHECKSUM_FIRST_BYTES[2] || this.checksumBytes[3] != this$0.CHECKSUM_FIRST_BYTES[3])
        throw new GSSException(11, -1, "Incorrect checksum"); 
      ChannelBinding channelBinding = param1Krb5Context.getChannelBinding();
      if (channelBinding != null) {
        byte[] arrayOfByte1 = new byte[16];
        System.arraycopy(this.checksumBytes, 4, arrayOfByte1, 0, 16);
        byte[] arrayOfByte2 = new byte[16];
        if (!Arrays.equals(arrayOfByte2, arrayOfByte1)) {
          byte[] arrayOfByte = this$0.computeChannelBinding(channelBinding);
          if (!Arrays.equals(arrayOfByte, arrayOfByte1))
            throw new GSSException(1, -1, "Bytes mismatch!"); 
        } else {
          throw new GSSException(1, -1, "Token missing ChannelBinding!");
        } 
      } 
      this.flags = GSSToken.readLittleEndian(this.checksumBytes, 20, 4);
      if ((this.flags & true) > 0) {
        KrbCred krbCred;
        int i = GSSToken.readLittleEndian(this.checksumBytes, 26, 2);
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(this.checksumBytes, 28, arrayOfByte, 0, i);
        try {
          krbCred = new KrbCred(arrayOfByte, param1EncryptionKey1);
        } catch (KrbException krbException) {
          if (param1EncryptionKey2 != null) {
            krbCred = new KrbCred(arrayOfByte, param1EncryptionKey2);
          } else {
            throw krbException;
          } 
        } 
        this.delegCreds = krbCred.getDelegatedCreds()[0];
      } 
    }
    
    private boolean useNullKey(CipherHelper param1CipherHelper) {
      boolean bool = true;
      if (param1CipherHelper.getProto() == 1 || param1CipherHelper.isArcFour())
        bool = false; 
      return bool;
    }
    
    public Checksum getChecksum() throws KrbException { return new Checksum(this.checksumBytes, 32771); }
    
    public Credentials getDelegatedCreds() { return this.delegCreds; }
    
    public void setContextFlags(Krb5Context param1Krb5Context) {
      if ((this.flags & true) > 0)
        param1Krb5Context.setCredDelegState(true); 
      if ((this.flags & 0x2) == 0)
        param1Krb5Context.setMutualAuthState(false); 
      if ((this.flags & 0x4) == 0)
        param1Krb5Context.setReplayDetState(false); 
      if ((this.flags & 0x8) == 0)
        param1Krb5Context.setSequenceDetState(false); 
      if ((this.flags & 0x10) == 0)
        param1Krb5Context.setConfState(false); 
      if ((this.flags & 0x20) == 0)
        param1Krb5Context.setIntegState(false); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\InitialToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */