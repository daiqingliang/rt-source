package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.OutputStream;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class CCacheOutputStream extends KrbDataOutputStream implements FileCCacheConstants {
  public CCacheOutputStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public void writeHeader(PrincipalName paramPrincipalName, int paramInt) throws IOException {
    write((paramInt & 0xFF00) >> 8);
    write(paramInt & 0xFF);
    paramPrincipalName.writePrincipal(this);
  }
  
  public void addCreds(Credentials paramCredentials) throws IOException, Asn1Exception {
    paramCredentials.cname.writePrincipal(this);
    paramCredentials.sname.writePrincipal(this);
    paramCredentials.key.writeKey(this);
    write32((int)(paramCredentials.authtime.getTime() / 1000L));
    if (paramCredentials.starttime != null) {
      write32((int)(paramCredentials.starttime.getTime() / 1000L));
    } else {
      write32(0);
    } 
    write32((int)(paramCredentials.endtime.getTime() / 1000L));
    if (paramCredentials.renewTill != null) {
      write32((int)(paramCredentials.renewTill.getTime() / 1000L));
    } else {
      write32(0);
    } 
    if (paramCredentials.isEncInSKey) {
      write8(1);
    } else {
      write8(0);
    } 
    writeFlags(paramCredentials.flags);
    if (paramCredentials.caddr == null) {
      write32(0);
    } else {
      paramCredentials.caddr.writeAddrs(this);
    } 
    if (paramCredentials.authorizationData == null) {
      write32(0);
    } else {
      paramCredentials.authorizationData.writeAuth(this);
    } 
    writeTicket(paramCredentials.ticket);
    writeTicket(paramCredentials.secondTicket);
  }
  
  void writeTicket(Ticket paramTicket) throws IOException, Asn1Exception {
    if (paramTicket == null) {
      write32(0);
    } else {
      byte[] arrayOfByte = paramTicket.asn1Encode();
      write32(arrayOfByte.length);
      write(arrayOfByte, 0, arrayOfByte.length);
    } 
  }
  
  void writeFlags(TicketFlags paramTicketFlags) throws IOException {
    int i = 0;
    boolean[] arrayOfBoolean = paramTicketFlags.toBooleanArray();
    if (arrayOfBoolean[1] == true)
      i |= 0x40000000; 
    if (arrayOfBoolean[2] == true)
      i |= 0x20000000; 
    if (arrayOfBoolean[3] == true)
      i |= 0x10000000; 
    if (arrayOfBoolean[4] == true)
      i |= 0x8000000; 
    if (arrayOfBoolean[5] == true)
      i |= 0x4000000; 
    if (arrayOfBoolean[6] == true)
      i |= 0x2000000; 
    if (arrayOfBoolean[7] == true)
      i |= 0x1000000; 
    if (arrayOfBoolean[8] == true)
      i |= 0x800000; 
    if (arrayOfBoolean[9] == true)
      i |= 0x400000; 
    if (arrayOfBoolean[10] == true)
      i |= 0x200000; 
    if (arrayOfBoolean[11] == true)
      i |= 0x100000; 
    write32(i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\CCacheOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */