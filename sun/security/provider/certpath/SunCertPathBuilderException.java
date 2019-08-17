package sun.security.provider.certpath;

import java.security.cert.CertPathBuilderException;

public class SunCertPathBuilderException extends CertPathBuilderException {
  private static final long serialVersionUID = -7814288414129264709L;
  
  private AdjacencyList adjList;
  
  public SunCertPathBuilderException() {}
  
  public SunCertPathBuilderException(String paramString) { super(paramString); }
  
  public SunCertPathBuilderException(Throwable paramThrowable) { super(paramThrowable); }
  
  public SunCertPathBuilderException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  SunCertPathBuilderException(String paramString, AdjacencyList paramAdjacencyList) {
    this(paramString);
    this.adjList = paramAdjacencyList;
  }
  
  SunCertPathBuilderException(String paramString, Throwable paramThrowable, AdjacencyList paramAdjacencyList) {
    this(paramString, paramThrowable);
    this.adjList = paramAdjacencyList;
  }
  
  public AdjacencyList getAdjacencyList() { return this.adjList; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\SunCertPathBuilderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */