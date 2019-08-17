package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.Extension;
import java.util.Collections;
import java.util.List;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.PKIXExtensions;

class OCSPRequest {
  private static final Debug debug;
  
  private static final boolean dump = (debug != null && (debug = Debug.getInstance("certpath")).isOn("ocsp"));
  
  private final List<CertId> certIds;
  
  private final List<Extension> extensions;
  
  private byte[] nonce;
  
  OCSPRequest(CertId paramCertId) { this(Collections.singletonList(paramCertId)); }
  
  OCSPRequest(List<CertId> paramList) {
    this.certIds = paramList;
    this.extensions = Collections.emptyList();
  }
  
  OCSPRequest(List<CertId> paramList1, List<Extension> paramList2) {
    this.certIds = paramList1;
    this.extensions = paramList2;
  }
  
  byte[] encodeBytes() throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    for (CertId certId : this.certIds) {
      DerOutputStream derOutputStream = new DerOutputStream();
      certId.encode(derOutputStream);
      derOutputStream2.write((byte)48, derOutputStream);
    } 
    derOutputStream1.write((byte)48, derOutputStream2);
    if (!this.extensions.isEmpty()) {
      DerOutputStream derOutputStream5 = new DerOutputStream();
      for (Extension extension : this.extensions) {
        extension.encode(derOutputStream5);
        if (extension.getId().equals(PKIXExtensions.OCSPNonce_Id.toString()))
          this.nonce = extension.getValue(); 
      } 
      DerOutputStream derOutputStream6 = new DerOutputStream();
      derOutputStream6.write((byte)48, derOutputStream5);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream6);
    } 
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write((byte)48, derOutputStream1);
    DerOutputStream derOutputStream4 = new DerOutputStream();
    derOutputStream4.write((byte)48, derOutputStream3);
    byte[] arrayOfByte = derOutputStream4.toByteArray();
    if (dump) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      debug.println("OCSPRequest bytes...\n\n" + hexDumpEncoder.encode(arrayOfByte) + "\n");
    } 
    return arrayOfByte;
  }
  
  List<CertId> getCertIds() { return this.certIds; }
  
  byte[] getNonce() throws IOException { return this.nonce; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\OCSPRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */