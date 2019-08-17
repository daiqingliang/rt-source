package sun.net.www.protocol.http.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.net.www.protocol.http.Negotiator;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.HttpCaller;

public class NegotiatorImpl extends Negotiator {
  private static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  
  private GSSContext context;
  
  private byte[] oneToken;
  
  private void init(HttpCallerInfo paramHttpCallerInfo) throws GSSException {
    Oid oid;
    if (paramHttpCallerInfo.scheme.equalsIgnoreCase("Kerberos")) {
      oid = GSSUtil.GSS_KRB5_MECH_OID;
    } else {
      String str1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty("http.auth.preference", "spnego"); }
          });
      if (str1.equalsIgnoreCase("kerberos")) {
        oid = GSSUtil.GSS_KRB5_MECH_OID;
      } else {
        oid = GSSUtil.GSS_SPNEGO_MECH_OID;
      } 
    } 
    GSSManagerImpl gSSManagerImpl = new GSSManagerImpl(new HttpCaller(paramHttpCallerInfo));
    String str = "HTTP@" + paramHttpCallerInfo.host.toLowerCase();
    GSSName gSSName = gSSManagerImpl.createName(str, GSSName.NT_HOSTBASED_SERVICE);
    this.context = gSSManagerImpl.createContext(gSSName, oid, null, 0);
    if (this.context instanceof ExtendedGSSContext)
      ((ExtendedGSSContext)this.context).requestDelegPolicy(true); 
    this.oneToken = this.context.initSecContext(new byte[0], 0, 0);
  }
  
  public NegotiatorImpl(HttpCallerInfo paramHttpCallerInfo) throws GSSException {
    try {
      init(paramHttpCallerInfo);
    } catch (GSSException gSSException) {
      if (DEBUG) {
        System.out.println("Negotiate support not initiated, will fallback to other scheme if allowed. Reason:");
        gSSException.printStackTrace();
      } 
      IOException iOException = new IOException("Negotiate support not initiated");
      iOException.initCause(gSSException);
      throw iOException;
    } 
  }
  
  public byte[] firstToken() { return this.oneToken; }
  
  public byte[] nextToken(byte[] paramArrayOfByte) throws IOException {
    try {
      return this.context.initSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
    } catch (GSSException gSSException) {
      if (DEBUG) {
        System.out.println("Negotiate support cannot continue. Reason:");
        gSSException.printStackTrace();
      } 
      IOException iOException = new IOException("Negotiate support cannot continue");
      iOException.initCause(gSSException);
      throw iOException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\spnego\NegotiatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */