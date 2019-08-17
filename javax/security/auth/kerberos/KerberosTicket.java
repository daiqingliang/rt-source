package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;

public class KerberosTicket implements Destroyable, Refreshable, Serializable {
  private static final long serialVersionUID = 7395334370157380539L;
  
  private static final int FORWARDABLE_TICKET_FLAG = 1;
  
  private static final int FORWARDED_TICKET_FLAG = 2;
  
  private static final int PROXIABLE_TICKET_FLAG = 3;
  
  private static final int PROXY_TICKET_FLAG = 4;
  
  private static final int POSTDATED_TICKET_FLAG = 6;
  
  private static final int RENEWABLE_TICKET_FLAG = 8;
  
  private static final int INITIAL_TICKET_FLAG = 9;
  
  private static final int NUM_FLAGS = 32;
  
  private byte[] asn1Encoding;
  
  private KeyImpl sessionKey;
  
  private boolean[] flags;
  
  private Date authTime;
  
  private Date startTime;
  
  private Date endTime;
  
  private Date renewTill;
  
  private KerberosPrincipal client;
  
  private KerberosPrincipal server;
  
  private InetAddress[] clientAddresses;
  
  private boolean destroyed = false;
  
  public KerberosTicket(byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) { init(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress); }
  
  private void init(byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) {
    if (paramArrayOfByte2 == null)
      throw new IllegalArgumentException("Session key for ticket cannot be null"); 
    init(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, new KeyImpl(paramArrayOfByte2, paramInt), paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
  }
  
  private void init(byte[] paramArrayOfByte, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, KeyImpl paramKeyImpl, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress) {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("ASN.1 encoding of ticket cannot be null"); 
    this.asn1Encoding = (byte[])paramArrayOfByte.clone();
    if (paramKerberosPrincipal1 == null)
      throw new IllegalArgumentException("Client name in ticket cannot be null"); 
    this.client = paramKerberosPrincipal1;
    if (paramKerberosPrincipal2 == null)
      throw new IllegalArgumentException("Server name in ticket cannot be null"); 
    this.server = paramKerberosPrincipal2;
    this.sessionKey = paramKeyImpl;
    if (paramArrayOfBoolean != null) {
      if (paramArrayOfBoolean.length >= 32) {
        this.flags = (boolean[])paramArrayOfBoolean.clone();
      } else {
        this.flags = new boolean[32];
        for (byte b = 0; b < paramArrayOfBoolean.length; b++)
          this.flags[b] = paramArrayOfBoolean[b]; 
      } 
    } else {
      this.flags = new boolean[32];
    } 
    if (this.flags[8]) {
      if (paramDate4 == null)
        throw new IllegalArgumentException("The renewable period end time cannot be null for renewable tickets."); 
      this.renewTill = new Date(paramDate4.getTime());
    } 
    if (paramDate1 != null)
      this.authTime = new Date(paramDate1.getTime()); 
    if (paramDate2 != null) {
      this.startTime = new Date(paramDate2.getTime());
    } else {
      this.startTime = this.authTime;
    } 
    if (paramDate3 == null)
      throw new IllegalArgumentException("End time for ticket validity cannot be null"); 
    this.endTime = new Date(paramDate3.getTime());
    if (paramArrayOfInetAddress != null)
      this.clientAddresses = (InetAddress[])paramArrayOfInetAddress.clone(); 
  }
  
  public final KerberosPrincipal getClient() { return this.client; }
  
  public final KerberosPrincipal getServer() { return this.server; }
  
  public final SecretKey getSessionKey() {
    if (this.destroyed)
      throw new IllegalStateException("This ticket is no longer valid"); 
    return this.sessionKey;
  }
  
  public final int getSessionKeyType() {
    if (this.destroyed)
      throw new IllegalStateException("This ticket is no longer valid"); 
    return this.sessionKey.getKeyType();
  }
  
  public final boolean isForwardable() { return (this.flags == null) ? false : this.flags[1]; }
  
  public final boolean isForwarded() { return (this.flags == null) ? false : this.flags[2]; }
  
  public final boolean isProxiable() { return (this.flags == null) ? false : this.flags[3]; }
  
  public final boolean isProxy() { return (this.flags == null) ? false : this.flags[4]; }
  
  public final boolean isPostdated() { return (this.flags == null) ? false : this.flags[6]; }
  
  public final boolean isRenewable() { return (this.flags == null) ? false : this.flags[8]; }
  
  public final boolean isInitial() { return (this.flags == null) ? false : this.flags[9]; }
  
  public final boolean[] getFlags() { return (this.flags == null) ? null : (boolean[])this.flags.clone(); }
  
  public final Date getAuthTime() { return (this.authTime == null) ? null : (Date)this.authTime.clone(); }
  
  public final Date getStartTime() { return (this.startTime == null) ? null : (Date)this.startTime.clone(); }
  
  public final Date getEndTime() { return (this.endTime == null) ? null : (Date)this.endTime.clone(); }
  
  public final Date getRenewTill() { return (this.renewTill == null) ? null : (Date)this.renewTill.clone(); }
  
  public final InetAddress[] getClientAddresses() { return (this.clientAddresses == null) ? null : (InetAddress[])this.clientAddresses.clone(); }
  
  public final byte[] getEncoded() {
    if (this.destroyed)
      throw new IllegalStateException("This ticket is no longer valid"); 
    return (byte[])this.asn1Encoding.clone();
  }
  
  public boolean isCurrent() { return (this.endTime == null) ? false : ((System.currentTimeMillis() <= this.endTime.getTime())); }
  
  public void refresh() throws RefreshFailedException {
    if (this.destroyed)
      throw new RefreshFailedException("A destroyed ticket cannot be renewd."); 
    if (!isRenewable())
      throw new RefreshFailedException("This ticket is not renewable"); 
    if (System.currentTimeMillis() > getRenewTill().getTime())
      throw new RefreshFailedException("This ticket is past its last renewal time."); 
    IOException iOException = null;
    Credentials credentials = null;
    try {
      credentials = new Credentials(this.asn1Encoding, this.client.toString(), this.server.toString(), this.sessionKey.getEncoded(), this.sessionKey.getKeyType(), this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
      credentials = credentials.renew();
    } catch (KrbException krbException) {
      iOException = krbException;
    } catch (IOException iOException1) {
      iOException = iOException1;
    } 
    if (iOException != null) {
      RefreshFailedException refreshFailedException = new RefreshFailedException("Failed to renew Kerberos Ticket for client " + this.client + " and server " + this.server + " - " + iOException.getMessage());
      refreshFailedException.initCause(iOException);
      throw refreshFailedException;
    } 
    synchronized (this) {
      try {
        destroy();
      } catch (DestroyFailedException destroyFailedException) {}
      init(credentials.getEncoded(), new KerberosPrincipal(credentials.getClient().getName()), new KerberosPrincipal(credentials.getServer().getName(), 2), credentials.getSessionKey().getBytes(), credentials.getSessionKey().getEType(), credentials.getFlags(), credentials.getAuthTime(), credentials.getStartTime(), credentials.getEndTime(), credentials.getRenewTill(), credentials.getClientAddresses());
      this.destroyed = false;
    } 
  }
  
  public void destroy() throws RefreshFailedException {
    if (!this.destroyed) {
      Arrays.fill(this.asn1Encoding, (byte)0);
      this.client = null;
      this.server = null;
      this.sessionKey.destroy();
      this.flags = null;
      this.authTime = null;
      this.startTime = null;
      this.endTime = null;
      this.renewTill = null;
      this.clientAddresses = null;
      this.destroyed = true;
    } 
  }
  
  public boolean isDestroyed() { return this.destroyed; }
  
  public String toString() {
    if (this.destroyed)
      return "Destroyed KerberosTicket"; 
    StringBuffer stringBuffer = new StringBuffer();
    if (this.clientAddresses != null)
      for (byte b = 0; b < this.clientAddresses.length; b++)
        stringBuffer.append("clientAddresses[" + b + "] = " + this.clientAddresses[b].toString());  
    return "Ticket (hex) = \n" + (new HexDumpEncoder()).encodeBuffer(this.asn1Encoding) + "\nClient Principal = " + this.client.toString() + "\nServer Principal = " + this.server.toString() + "\nSession Key = " + this.sessionKey.toString() + "\nForwardable Ticket " + this.flags[1] + "\nForwarded Ticket " + this.flags[2] + "\nProxiable Ticket " + this.flags[3] + "\nProxy Ticket " + this.flags[4] + "\nPostdated Ticket " + this.flags[6] + "\nRenewable Ticket " + this.flags[8] + "\nInitial Ticket " + this.flags[8] + "\nAuth Time = " + String.valueOf(this.authTime) + "\nStart Time = " + String.valueOf(this.startTime) + "\nEnd Time = " + this.endTime.toString() + "\nRenew Till = " + String.valueOf(this.renewTill) + "\nClient Addresses " + ((this.clientAddresses == null) ? " Null " : (stringBuffer.toString() + "\n"));
  }
  
  public int hashCode() {
    int i = 17;
    if (isDestroyed())
      return i; 
    i = i * 37 + Arrays.hashCode(getEncoded());
    i = i * 37 + this.endTime.hashCode();
    i = i * 37 + this.client.hashCode();
    i = i * 37 + this.server.hashCode();
    i = i * 37 + this.sessionKey.hashCode();
    if (this.authTime != null)
      i = i * 37 + this.authTime.hashCode(); 
    if (this.startTime != null)
      i = i * 37 + this.startTime.hashCode(); 
    if (this.renewTill != null)
      i = i * 37 + this.renewTill.hashCode(); 
    i = i * 37 + Arrays.hashCode(this.clientAddresses);
    return i * 37 + Arrays.hashCode(this.flags);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof KerberosTicket))
      return false; 
    KerberosTicket kerberosTicket = (KerberosTicket)paramObject;
    if (isDestroyed() || kerberosTicket.isDestroyed())
      return false; 
    if (!Arrays.equals(getEncoded(), kerberosTicket.getEncoded()) || !this.endTime.equals(kerberosTicket.getEndTime()) || !this.server.equals(kerberosTicket.getServer()) || !this.client.equals(kerberosTicket.getClient()) || !this.sessionKey.equals(kerberosTicket.getSessionKey()) || !Arrays.equals(this.clientAddresses, kerberosTicket.getClientAddresses()) || !Arrays.equals(this.flags, kerberosTicket.getFlags()))
      return false; 
    if (this.authTime == null) {
      if (kerberosTicket.getAuthTime() != null)
        return false; 
    } else if (!this.authTime.equals(kerberosTicket.getAuthTime())) {
      return false;
    } 
    if (this.startTime == null) {
      if (kerberosTicket.getStartTime() != null)
        return false; 
    } else if (!this.startTime.equals(kerberosTicket.getStartTime())) {
      return false;
    } 
    if (this.renewTill == null) {
      if (kerberosTicket.getRenewTill() != null)
        return false; 
    } else if (!this.renewTill.equals(kerberosTicket.getRenewTill())) {
      return false;
    } 
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.sessionKey == null)
      throw new InvalidObjectException("Session key cannot be null"); 
    try {
      init(this.asn1Encoding, this.client, this.server, this.sessionKey, this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw (InvalidObjectException)(new InvalidObjectException(illegalArgumentException.getMessage())).initCause(illegalArgumentException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KerberosTicket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */