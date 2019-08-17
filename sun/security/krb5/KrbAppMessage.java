package sun.security.krb5;

import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;

abstract class KrbAppMessage {
  private static boolean DEBUG = Krb5.DEBUG;
  
  void check(KerberosTime paramKerberosTime, Integer paramInteger1, Integer paramInteger2, HostAddress paramHostAddress1, HostAddress paramHostAddress2, SeqNumber paramSeqNumber, HostAddress paramHostAddress3, HostAddress paramHostAddress4, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName) throws KrbApErrException {
    if (paramHostAddress3 != null && (paramHostAddress1 == null || paramHostAddress3 == null || !paramHostAddress1.equals(paramHostAddress3))) {
      if (DEBUG && paramHostAddress1 == null)
        System.out.println("packetSAddress is null"); 
      if (DEBUG && paramHostAddress3 == null)
        System.out.println("sAddress is null"); 
      throw new KrbApErrException(38);
    } 
    if (paramHostAddress4 != null && (paramHostAddress2 == null || paramHostAddress4 == null || !paramHostAddress2.equals(paramHostAddress4)))
      throw new KrbApErrException(38); 
    if (paramKerberosTime != null) {
      if (paramInteger1 != null)
        paramKerberosTime = paramKerberosTime.withMicroSeconds(paramInteger1.intValue()); 
      if (!paramKerberosTime.inClockSkew())
        throw new KrbApErrException(37); 
    } else if (paramBoolean1) {
      throw new KrbApErrException(37);
    } 
    if (paramSeqNumber == null && paramBoolean2 == true)
      throw new KrbApErrException(400); 
    if (paramInteger2 != null && paramSeqNumber != null) {
      if (paramInteger2.intValue() != paramSeqNumber.current())
        throw new KrbApErrException(42); 
      paramSeqNumber.step();
    } else if (paramBoolean2) {
      throw new KrbApErrException(42);
    } 
    if (paramKerberosTime == null && paramInteger2 == null)
      throw new KrbApErrException(41); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbAppMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */