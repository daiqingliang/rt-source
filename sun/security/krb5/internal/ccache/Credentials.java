package sun.security.krb5.internal.ccache;

import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;

public class Credentials {
  PrincipalName cname;
  
  PrincipalName sname;
  
  EncryptionKey key;
  
  KerberosTime authtime;
  
  KerberosTime starttime;
  
  KerberosTime endtime;
  
  KerberosTime renewTill;
  
  HostAddresses caddr;
  
  AuthorizationData authorizationData;
  
  public boolean isEncInSKey;
  
  TicketFlags flags;
  
  Ticket ticket;
  
  Ticket secondTicket;
  
  private boolean DEBUG = Krb5.DEBUG;
  
  public Credentials(PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, boolean paramBoolean, TicketFlags paramTicketFlags, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket paramTicket1, Ticket paramTicket2) {
    this.cname = (PrincipalName)paramPrincipalName1.clone();
    this.sname = (PrincipalName)paramPrincipalName2.clone();
    this.key = (EncryptionKey)paramEncryptionKey.clone();
    this.authtime = paramKerberosTime1;
    this.starttime = paramKerberosTime2;
    this.endtime = paramKerberosTime3;
    this.renewTill = paramKerberosTime4;
    if (paramHostAddresses != null)
      this.caddr = (HostAddresses)paramHostAddresses.clone(); 
    if (paramAuthorizationData != null)
      this.authorizationData = (AuthorizationData)paramAuthorizationData.clone(); 
    this.isEncInSKey = paramBoolean;
    this.flags = (TicketFlags)paramTicketFlags.clone();
    this.ticket = (Ticket)paramTicket1.clone();
    if (paramTicket2 != null)
      this.secondTicket = (Ticket)paramTicket2.clone(); 
  }
  
  public Credentials(KDCRep paramKDCRep, Ticket paramTicket, AuthorizationData paramAuthorizationData, boolean paramBoolean) {
    if (paramKDCRep.encKDCRepPart == null)
      return; 
    this.cname = (PrincipalName)paramKDCRep.cname.clone();
    this.ticket = (Ticket)paramKDCRep.ticket.clone();
    this.key = (EncryptionKey)paramKDCRep.encKDCRepPart.key.clone();
    this.flags = (TicketFlags)paramKDCRep.encKDCRepPart.flags.clone();
    this.authtime = paramKDCRep.encKDCRepPart.authtime;
    this.starttime = paramKDCRep.encKDCRepPart.starttime;
    this.endtime = paramKDCRep.encKDCRepPart.endtime;
    this.renewTill = paramKDCRep.encKDCRepPart.renewTill;
    this.sname = (PrincipalName)paramKDCRep.encKDCRepPart.sname.clone();
    this.caddr = (HostAddresses)paramKDCRep.encKDCRepPart.caddr.clone();
    this.secondTicket = (Ticket)paramTicket.clone();
    this.authorizationData = (AuthorizationData)paramAuthorizationData.clone();
    this.isEncInSKey = paramBoolean;
  }
  
  public Credentials(KDCRep paramKDCRep) { this(paramKDCRep, null); }
  
  public Credentials(KDCRep paramKDCRep, Ticket paramTicket) {
    this.sname = (PrincipalName)paramKDCRep.encKDCRepPart.sname.clone();
    this.cname = (PrincipalName)paramKDCRep.cname.clone();
    this.key = (EncryptionKey)paramKDCRep.encKDCRepPart.key.clone();
    this.authtime = paramKDCRep.encKDCRepPart.authtime;
    this.starttime = paramKDCRep.encKDCRepPart.starttime;
    this.endtime = paramKDCRep.encKDCRepPart.endtime;
    this.renewTill = paramKDCRep.encKDCRepPart.renewTill;
    this.flags = paramKDCRep.encKDCRepPart.flags;
    if (paramKDCRep.encKDCRepPart.caddr != null) {
      this.caddr = (HostAddresses)paramKDCRep.encKDCRepPart.caddr.clone();
    } else {
      this.caddr = null;
    } 
    this.ticket = (Ticket)paramKDCRep.ticket.clone();
    if (paramTicket != null) {
      this.secondTicket = (Ticket)paramTicket.clone();
      this.isEncInSKey = true;
    } else {
      this.secondTicket = null;
      this.isEncInSKey = false;
    } 
  }
  
  public boolean isValid() {
    boolean bool = true;
    if (this.endtime.getTime() < System.currentTimeMillis()) {
      bool = false;
    } else if (this.starttime != null) {
      if (this.starttime.getTime() > System.currentTimeMillis())
        bool = false; 
    } else if (this.authtime.getTime() > System.currentTimeMillis()) {
      bool = false;
    } 
    return bool;
  }
  
  public PrincipalName getServicePrincipal() throws RealmException { return this.sname; }
  
  public Credentials setKrbCreds() { return new Credentials(this.ticket, this.cname, this.sname, this.key, this.flags, this.authtime, this.starttime, this.endtime, this.renewTill, this.caddr); }
  
  public KerberosTime getStartTime() { return this.starttime; }
  
  public KerberosTime getAuthTime() { return this.authtime; }
  
  public KerberosTime getEndTime() { return this.endtime; }
  
  public KerberosTime getRenewTill() { return this.renewTill; }
  
  public TicketFlags getTicketFlags() { return this.flags; }
  
  public int getEType() { return this.key.getEType(); }
  
  public int getTktEType() { return this.ticket.encPart.getEType(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\Credentials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */