package sun.security.x509;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralNames {
  private final List<GeneralName> names = new ArrayList();
  
  public GeneralNames(DerValue paramDerValue) throws IOException {
    this();
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding for GeneralNames."); 
    if (paramDerValue.data.available() == 0)
      throw new IOException("No data available in passed DER encoded value."); 
    while (paramDerValue.data.available() != 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      GeneralName generalName = new GeneralName(derValue);
      add(generalName);
    } 
  }
  
  public GeneralNames() {}
  
  public GeneralNames add(GeneralName paramGeneralName) {
    if (paramGeneralName == null)
      throw new NullPointerException(); 
    this.names.add(paramGeneralName);
    return this;
  }
  
  public GeneralName get(int paramInt) { return (GeneralName)this.names.get(paramInt); }
  
  public boolean isEmpty() { return this.names.isEmpty(); }
  
  public int size() { return this.names.size(); }
  
  public Iterator<GeneralName> iterator() { return this.names.iterator(); }
  
  public List<GeneralName> names() { return this.names; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    if (isEmpty())
      return; 
    DerOutputStream derOutputStream = new DerOutputStream();
    for (GeneralName generalName : this.names)
      generalName.encode(derOutputStream); 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof GeneralNames))
      return false; 
    GeneralNames generalNames = (GeneralNames)paramObject;
    return this.names.equals(generalNames.names);
  }
  
  public int hashCode() { return this.names.hashCode(); }
  
  public String toString() { return this.names.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\GeneralNames.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */