package java.security.cert;

import java.util.Collection;
import java.util.Collections;

public class CollectionCertStoreParameters implements CertStoreParameters {
  private Collection<?> coll;
  
  public CollectionCertStoreParameters(Collection<?> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    this.coll = paramCollection;
  }
  
  public CollectionCertStoreParameters() { this.coll = Collections.EMPTY_SET; }
  
  public Collection<?> getCollection() { return this.coll; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("CollectionCertStoreParameters: [\n");
    stringBuffer.append("  collection: " + this.coll + "\n");
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CollectionCertStoreParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */