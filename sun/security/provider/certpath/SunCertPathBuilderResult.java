package sun.security.provider.certpath;

import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import sun.security.util.Debug;

public class SunCertPathBuilderResult extends PKIXCertPathBuilderResult {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private AdjacencyList adjList;
  
  SunCertPathBuilderResult(CertPath paramCertPath, TrustAnchor paramTrustAnchor, PolicyNode paramPolicyNode, PublicKey paramPublicKey, AdjacencyList paramAdjacencyList) {
    super(paramCertPath, paramTrustAnchor, paramPolicyNode, paramPublicKey);
    this.adjList = paramAdjacencyList;
  }
  
  public AdjacencyList getAdjacencyList() { return this.adjList; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\SunCertPathBuilderResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */