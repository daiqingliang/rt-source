package sun.security.provider.certpath;

import java.security.cert.X509Certificate;

public class BuildStep {
  private Vertex vertex;
  
  private X509Certificate cert;
  
  private Throwable throwable;
  
  private int result;
  
  public static final int POSSIBLE = 1;
  
  public static final int BACK = 2;
  
  public static final int FOLLOW = 3;
  
  public static final int FAIL = 4;
  
  public static final int SUCCEED = 5;
  
  public BuildStep(Vertex paramVertex, int paramInt) {
    this.vertex = paramVertex;
    if (this.vertex != null) {
      this.cert = this.vertex.getCertificate();
      this.throwable = this.vertex.getThrowable();
    } 
    this.result = paramInt;
  }
  
  public Vertex getVertex() { return this.vertex; }
  
  public X509Certificate getCertificate() { return this.cert; }
  
  public String getIssuerName() { return getIssuerName(null); }
  
  public String getIssuerName(String paramString) { return (this.cert == null) ? paramString : this.cert.getIssuerX500Principal().toString(); }
  
  public String getSubjectName() { return getSubjectName(null); }
  
  public String getSubjectName(String paramString) { return (this.cert == null) ? paramString : this.cert.getSubjectX500Principal().toString(); }
  
  public Throwable getThrowable() { return this.throwable; }
  
  public int getResult() { return this.result; }
  
  public String resultToString(int paramInt) {
    null = "";
    switch (paramInt) {
      case 1:
        return "Certificate to be tried.\n";
      case 2:
        return "Certificate backed out since path does not satisfy build requirements.\n";
      case 3:
        return "Certificate satisfies conditions.\n";
      case 4:
        return "Certificate backed out since path does not satisfy conditions.\n";
      case 5:
        return "Certificate satisfies conditions.\n";
    } 
    return "Internal error: Invalid step result value.\n";
  }
  
  public String toString() {
    null = "Internal Error\n";
    switch (this.result) {
      case 2:
      case 4:
        null = resultToString(this.result);
        return null + this.vertex.throwableToString();
      case 1:
      case 3:
      case 5:
        return resultToString(this.result);
    } 
    return "Internal Error: Invalid step result\n";
  }
  
  public String verboseToString() {
    null = resultToString(getResult());
    switch (this.result) {
      case 2:
      case 4:
        null = null + this.vertex.throwableToString();
        break;
      case 3:
      case 5:
        null = null + this.vertex.moreToString();
        break;
    } 
    return null + "Certificate contains:\n" + this.vertex.certToString();
  }
  
  public String fullToString() { return resultToString(getResult()) + this.vertex.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\BuildStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */