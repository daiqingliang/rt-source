package sun.security.jgss.wrapper;

import java.io.UnsupportedEncodingException;
import java.security.Provider;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class NativeGSSFactory implements MechanismFactory {
  GSSLibStub cStub = null;
  
  private final GSSCaller caller;
  
  private GSSCredElement getCredFromSubject(GSSNameElement paramGSSNameElement, boolean paramBoolean) throws GSSException {
    Oid oid = this.cStub.getMech();
    Vector vector = GSSUtil.searchSubject(paramGSSNameElement, oid, paramBoolean, GSSCredElement.class);
    if (vector != null && vector.isEmpty() && GSSUtil.useSubjectCredsOnly(this.caller))
      throw new GSSException(13); 
    GSSCredElement gSSCredElement = (vector == null || vector.isEmpty()) ? null : (GSSCredElement)vector.firstElement();
    if (gSSCredElement != null)
      gSSCredElement.doServicePermCheck(); 
    return gSSCredElement;
  }
  
  public NativeGSSFactory(GSSCaller paramGSSCaller) { this.caller = paramGSSCaller; }
  
  public void setMech(Oid paramOid) throws GSSException { this.cStub = GSSLibStub.getInstance(paramOid); }
  
  public GSSNameSpi getNameElement(String paramString, Oid paramOid) throws GSSException {
    try {
      byte[] arrayOfByte = (paramString == null) ? null : paramString.getBytes("UTF-8");
      return new GSSNameElement(arrayOfByte, paramOid, this.cStub);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new GSSExceptionImpl(11, unsupportedEncodingException);
    } 
  }
  
  public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid) throws GSSException { return new GSSNameElement(paramArrayOfByte, paramOid, this.cStub); }
  
  public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    GSSNameElement gSSNameElement = null;
    if (paramGSSNameSpi != null && !(paramGSSNameSpi instanceof GSSNameElement)) {
      gSSNameElement = (GSSNameElement)getNameElement(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType());
    } else {
      gSSNameElement = (GSSNameElement)paramGSSNameSpi;
    } 
    if (paramInt3 == 0)
      paramInt3 = 1; 
    GSSCredElement gSSCredElement = getCredFromSubject(gSSNameElement, (paramInt3 == 1));
    if (gSSCredElement == null)
      if (paramInt3 == 1) {
        gSSCredElement = new GSSCredElement(gSSNameElement, paramInt1, paramInt3, this.cStub);
      } else if (paramInt3 == 2) {
        if (gSSNameElement == null)
          gSSNameElement = GSSNameElement.DEF_ACCEPTOR; 
        gSSCredElement = new GSSCredElement(gSSNameElement, paramInt2, paramInt3, this.cStub);
      } else {
        throw new GSSException(11, -1, "Unknown usage mode requested");
      }  
    return gSSCredElement;
  }
  
  public GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt) throws GSSException {
    if (paramGSSNameSpi == null)
      throw new GSSException(3); 
    if (!(paramGSSNameSpi instanceof GSSNameElement))
      paramGSSNameSpi = (GSSNameElement)getNameElement(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType()); 
    if (paramGSSCredentialSpi == null) {
      paramGSSCredentialSpi = getCredFromSubject(null, true);
    } else if (!(paramGSSCredentialSpi instanceof GSSCredElement)) {
      throw new GSSException(13);
    } 
    return new NativeGSSContext((GSSNameElement)paramGSSNameSpi, (GSSCredElement)paramGSSCredentialSpi, paramInt, this.cStub);
  }
  
  public GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi) throws GSSException {
    if (paramGSSCredentialSpi == null) {
      paramGSSCredentialSpi = getCredFromSubject(null, false);
    } else if (!(paramGSSCredentialSpi instanceof GSSCredElement)) {
      throw new GSSException(13);
    } 
    return new NativeGSSContext((GSSCredElement)paramGSSCredentialSpi, this.cStub);
  }
  
  public GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException { return this.cStub.importContext(paramArrayOfByte); }
  
  public final Oid getMechanismOid() { return this.cStub.getMech(); }
  
  public Provider getProvider() { return SunNativeProvider.INSTANCE; }
  
  public Oid[] getNameTypes() throws GSSException { return this.cStub.inquireNamesForMech(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\NativeGSSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */