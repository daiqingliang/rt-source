package javax.net.ssl;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SSLParameters {
  private String[] cipherSuites;
  
  private String[] protocols;
  
  private boolean wantClientAuth;
  
  private boolean needClientAuth;
  
  private String identificationAlgorithm;
  
  private AlgorithmConstraints algorithmConstraints;
  
  private Map<Integer, SNIServerName> sniNames = null;
  
  private Map<Integer, SNIMatcher> sniMatchers = null;
  
  private boolean preferLocalCipherSuites;
  
  public SSLParameters() {}
  
  public SSLParameters(String[] paramArrayOfString) { setCipherSuites(paramArrayOfString); }
  
  public SSLParameters(String[] paramArrayOfString1, String[] paramArrayOfString2) {
    setCipherSuites(paramArrayOfString1);
    setProtocols(paramArrayOfString2);
  }
  
  private static String[] clone(String[] paramArrayOfString) { return (paramArrayOfString == null) ? null : (String[])paramArrayOfString.clone(); }
  
  public String[] getCipherSuites() { return clone(this.cipherSuites); }
  
  public void setCipherSuites(String[] paramArrayOfString) { this.cipherSuites = clone(paramArrayOfString); }
  
  public String[] getProtocols() { return clone(this.protocols); }
  
  public void setProtocols(String[] paramArrayOfString) { this.protocols = clone(paramArrayOfString); }
  
  public boolean getWantClientAuth() { return this.wantClientAuth; }
  
  public void setWantClientAuth(boolean paramBoolean) {
    this.wantClientAuth = paramBoolean;
    this.needClientAuth = false;
  }
  
  public boolean getNeedClientAuth() { return this.needClientAuth; }
  
  public void setNeedClientAuth(boolean paramBoolean) {
    this.wantClientAuth = false;
    this.needClientAuth = paramBoolean;
  }
  
  public AlgorithmConstraints getAlgorithmConstraints() { return this.algorithmConstraints; }
  
  public void setAlgorithmConstraints(AlgorithmConstraints paramAlgorithmConstraints) { this.algorithmConstraints = paramAlgorithmConstraints; }
  
  public String getEndpointIdentificationAlgorithm() { return this.identificationAlgorithm; }
  
  public void setEndpointIdentificationAlgorithm(String paramString) { this.identificationAlgorithm = paramString; }
  
  public final void setServerNames(List<SNIServerName> paramList) {
    if (paramList != null) {
      if (!paramList.isEmpty()) {
        this.sniNames = new LinkedHashMap(paramList.size());
        for (SNIServerName sNIServerName : paramList) {
          if (this.sniNames.put(Integer.valueOf(sNIServerName.getType()), sNIServerName) != null)
            throw new IllegalArgumentException("Duplicated server name of type " + sNIServerName.getType()); 
        } 
      } else {
        this.sniNames = Collections.emptyMap();
      } 
    } else {
      this.sniNames = null;
    } 
  }
  
  public final List<SNIServerName> getServerNames() { return (this.sniNames != null) ? (!this.sniNames.isEmpty() ? Collections.unmodifiableList(new ArrayList(this.sniNames.values())) : Collections.emptyList()) : null; }
  
  public final void setSNIMatchers(Collection<SNIMatcher> paramCollection) {
    if (paramCollection != null) {
      if (!paramCollection.isEmpty()) {
        this.sniMatchers = new HashMap(paramCollection.size());
        for (SNIMatcher sNIMatcher : paramCollection) {
          if (this.sniMatchers.put(Integer.valueOf(sNIMatcher.getType()), sNIMatcher) != null)
            throw new IllegalArgumentException("Duplicated server name of type " + sNIMatcher.getType()); 
        } 
      } else {
        this.sniMatchers = Collections.emptyMap();
      } 
    } else {
      this.sniMatchers = null;
    } 
  }
  
  public final Collection<SNIMatcher> getSNIMatchers() { return (this.sniMatchers != null) ? (!this.sniMatchers.isEmpty() ? Collections.unmodifiableList(new ArrayList(this.sniMatchers.values())) : Collections.emptyList()) : null; }
  
  public final void setUseCipherSuitesOrder(boolean paramBoolean) { this.preferLocalCipherSuites = paramBoolean; }
  
  public final boolean getUseCipherSuitesOrder() { return this.preferLocalCipherSuites; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */