package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;

public class DeltaCRLIndicatorExtension extends CRLNumberExtension {
  public static final String NAME = "DeltaCRLIndicator";
  
  private static final String LABEL = "Base CRL Number";
  
  public DeltaCRLIndicatorExtension(int paramInt) throws IOException { super(PKIXExtensions.DeltaCRLIndicator_Id, true, BigInteger.valueOf(paramInt), "DeltaCRLIndicator", "Base CRL Number"); }
  
  public DeltaCRLIndicatorExtension(BigInteger paramBigInteger) throws IOException { super(PKIXExtensions.DeltaCRLIndicator_Id, true, paramBigInteger, "DeltaCRLIndicator", "Base CRL Number"); }
  
  public DeltaCRLIndicatorExtension(Boolean paramBoolean, Object paramObject) throws IOException { super(PKIXExtensions.DeltaCRLIndicator_Id, Boolean.valueOf(paramBoolean.booleanValue()), paramObject, "DeltaCRLIndicator", "Base CRL Number"); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    encode(paramOutputStream, PKIXExtensions.DeltaCRLIndicator_Id, true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\DeltaCRLIndicatorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */