package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import sun.security.util.Debug;

class PKIXMasterCertPathValidator {
  private static final Debug debug = Debug.getInstance("certpath");
  
  static void validate(CertPath paramCertPath, List<X509Certificate> paramList1, List<PKIXCertPathChecker> paramList2) throws CertPathValidatorException {
    int i = paramList1.size();
    if (debug != null) {
      debug.println("--------------------------------------------------------------");
      debug.println("Executing PKIX certification path validation algorithm.");
    } 
    for (byte b = 0; b < i; b++) {
      X509Certificate x509Certificate = (X509Certificate)paramList1.get(b);
      if (debug != null)
        debug.println("Checking cert" + (b + 1) + " - Subject: " + x509Certificate.getSubjectX500Principal()); 
      Set set = x509Certificate.getCriticalExtensionOIDs();
      if (set == null)
        set = Collections.emptySet(); 
      if (debug != null && !set.isEmpty()) {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        for (String str : set)
          stringJoiner.add(str); 
        debug.println("Set of critical extensions: " + stringJoiner.toString());
      } 
      for (byte b1 = 0; b1 < paramList2.size(); b1++) {
        PKIXCertPathChecker pKIXCertPathChecker = (PKIXCertPathChecker)paramList2.get(b1);
        if (debug != null)
          debug.println("-Using checker" + (b1 + 1) + " ... [" + pKIXCertPathChecker.getClass().getName() + "]"); 
        if (b == 0)
          pKIXCertPathChecker.init(false); 
        try {
          pKIXCertPathChecker.check(x509Certificate, set);
          if (debug != null)
            debug.println("-checker" + (b1 + 1) + " validation succeeded"); 
        } catch (CertPathValidatorException certPathValidatorException) {
          throw new CertPathValidatorException(certPathValidatorException.getMessage(), (certPathValidatorException.getCause() != null) ? certPathValidatorException.getCause() : certPathValidatorException, paramCertPath, i - b + 1, certPathValidatorException.getReason());
        } 
      } 
      if (!set.isEmpty())
        throw new CertPathValidatorException("unrecognized critical extension(s)", null, paramCertPath, i - b + 1, PKIXReason.UNRECOGNIZED_CRIT_EXT); 
      if (debug != null)
        debug.println("\ncert" + (b + 1) + " validation succeeded.\n"); 
    } 
    if (debug != null) {
      debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
      debug.println("--------------------------------------------------------------");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PKIXMasterCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */