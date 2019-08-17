package sun.security.x509;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class GeneralSubtrees implements Cloneable {
  private final List<GeneralSubtree> trees = new ArrayList();
  
  private static final int NAME_DIFF_TYPE = -1;
  
  private static final int NAME_MATCH = 0;
  
  private static final int NAME_NARROWS = 1;
  
  private static final int NAME_WIDENS = 2;
  
  private static final int NAME_SAME_TYPE = 3;
  
  public GeneralSubtrees() {}
  
  private GeneralSubtrees(GeneralSubtrees paramGeneralSubtrees) {}
  
  public GeneralSubtrees(DerValue paramDerValue) throws IOException {
    this();
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding of GeneralSubtrees."); 
    while (paramDerValue.data.available() != 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      GeneralSubtree generalSubtree = new GeneralSubtree(derValue);
      add(generalSubtree);
    } 
  }
  
  public GeneralSubtree get(int paramInt) { return (GeneralSubtree)this.trees.get(paramInt); }
  
  public void remove(int paramInt) { this.trees.remove(paramInt); }
  
  public void add(GeneralSubtree paramGeneralSubtree) {
    if (paramGeneralSubtree == null)
      throw new NullPointerException(); 
    this.trees.add(paramGeneralSubtree);
  }
  
  public boolean contains(GeneralSubtree paramGeneralSubtree) {
    if (paramGeneralSubtree == null)
      throw new NullPointerException(); 
    return this.trees.contains(paramGeneralSubtree);
  }
  
  public int size() { return this.trees.size(); }
  
  public Iterator<GeneralSubtree> iterator() { return this.trees.iterator(); }
  
  public List<GeneralSubtree> trees() { return this.trees; }
  
  public Object clone() { return new GeneralSubtrees(this); }
  
  public String toString() { return "   GeneralSubtrees:\n" + this.trees.toString() + "\n"; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    byte b = 0;
    int i = size();
    while (b < i) {
      get(b).encode(derOutputStream);
      b++;
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof GeneralSubtrees))
      return false; 
    GeneralSubtrees generalSubtrees = (GeneralSubtrees)paramObject;
    return this.trees.equals(generalSubtrees.trees);
  }
  
  public int hashCode() { return this.trees.hashCode(); }
  
  private GeneralNameInterface getGeneralNameInterface(int paramInt) { return getGeneralNameInterface(get(paramInt)); }
  
  private static GeneralNameInterface getGeneralNameInterface(GeneralSubtree paramGeneralSubtree) {
    GeneralName generalName = paramGeneralSubtree.getName();
    return generalName.getName();
  }
  
  private void minimize() {
    for (byte b = 0; b < size() - 1; b++) {
      GeneralNameInterface generalNameInterface = getGeneralNameInterface(b);
      boolean bool = false;
      for (byte b1 = b + 1; b1 < size(); b1++) {
        GeneralNameInterface generalNameInterface1 = getGeneralNameInterface(b1);
        switch (generalNameInterface.constrains(generalNameInterface1)) {
          case -1:
            break;
          case 0:
            bool = true;
            break;
          case 1:
            remove(b1);
            b1--;
            break;
          case 2:
            bool = true;
            break;
          case 3:
            break;
          default:
            break;
        } 
      } 
      if (bool) {
        remove(b);
        b--;
      } 
    } 
  }
  
  private GeneralSubtree createWidestSubtree(GeneralNameInterface paramGeneralNameInterface) {
    try {
      ObjectIdentifier objectIdentifier;
      GeneralName generalName;
      switch (paramGeneralNameInterface.getType()) {
        case 0:
          objectIdentifier = ((OtherName)paramGeneralNameInterface).getOID();
          generalName = new GeneralName(new OtherName(objectIdentifier, null));
          return new GeneralSubtree(generalName, 0, -1);
        case 1:
          generalName = new GeneralName(new RFC822Name(""));
          return new GeneralSubtree(generalName, 0, -1);
        case 2:
          generalName = new GeneralName(new DNSName(""));
          return new GeneralSubtree(generalName, 0, -1);
        case 3:
          generalName = new GeneralName(new X400Address((byte[])null));
          return new GeneralSubtree(generalName, 0, -1);
        case 4:
          generalName = new GeneralName(new X500Name(""));
          return new GeneralSubtree(generalName, 0, -1);
        case 5:
          generalName = new GeneralName(new EDIPartyName(""));
          return new GeneralSubtree(generalName, 0, -1);
        case 6:
          generalName = new GeneralName(new URIName(""));
          return new GeneralSubtree(generalName, 0, -1);
        case 7:
          generalName = new GeneralName(new IPAddressName((byte[])null));
          return new GeneralSubtree(generalName, 0, -1);
        case 8:
          generalName = new GeneralName(new OIDName(new ObjectIdentifier((int[])null)));
          return new GeneralSubtree(generalName, 0, -1);
      } 
      throw new IOException("Unsupported GeneralNameInterface type: " + paramGeneralNameInterface.getType());
    } catch (IOException iOException) {
      throw new RuntimeException("Unexpected error: " + iOException, iOException);
    } 
  }
  
  public GeneralSubtrees intersect(GeneralSubtrees paramGeneralSubtrees) {
    if (paramGeneralSubtrees == null)
      throw new NullPointerException("other GeneralSubtrees must not be null"); 
    GeneralSubtrees generalSubtrees1 = new GeneralSubtrees();
    GeneralSubtrees generalSubtrees2 = null;
    if (size() == 0) {
      union(paramGeneralSubtrees);
      return null;
    } 
    minimize();
    paramGeneralSubtrees.minimize();
    byte b;
    for (b = 0; b < size(); b++) {
      GeneralNameInterface generalNameInterface = getGeneralNameInterface(b);
      boolean bool1 = false;
      boolean bool2 = false;
      byte b1;
      for (b1 = 0; b1 < paramGeneralSubtrees.size(); b1++) {
        GeneralSubtree generalSubtree = paramGeneralSubtrees.get(b1);
        GeneralNameInterface generalNameInterface1 = getGeneralNameInterface(generalSubtree);
        switch (generalNameInterface.constrains(generalNameInterface1)) {
          case 1:
            remove(b);
            b--;
            generalSubtrees1.add(generalSubtree);
            bool2 = false;
            break;
          case 3:
            bool2 = true;
            break;
          case 0:
          case 2:
            bool2 = false;
            break;
        } 
      } 
      if (bool2) {
        b1 = 0;
        for (byte b2 = 0; b2 < size(); b2++) {
          GeneralNameInterface generalNameInterface1 = getGeneralNameInterface(b2);
          if (generalNameInterface1.getType() == generalNameInterface.getType())
            for (byte b3 = 0; b3 < paramGeneralSubtrees.size(); b3++) {
              GeneralNameInterface generalNameInterface2 = paramGeneralSubtrees.getGeneralNameInterface(b3);
              int i = generalNameInterface1.constrains(generalNameInterface2);
              if (i == 0 || i == 2 || i == 1) {
                b1 = 1;
                break;
              } 
            }  
        } 
        if (b1 == 0) {
          if (generalSubtrees2 == null)
            generalSubtrees2 = new GeneralSubtrees(); 
          GeneralSubtree generalSubtree = createWidestSubtree(generalNameInterface);
          if (!generalSubtrees2.contains(generalSubtree))
            generalSubtrees2.add(generalSubtree); 
        } 
        remove(b);
        b--;
      } 
    } 
    if (generalSubtrees1.size() > 0)
      union(generalSubtrees1); 
    for (b = 0; b < paramGeneralSubtrees.size(); b++) {
      GeneralSubtree generalSubtree = paramGeneralSubtrees.get(b);
      GeneralNameInterface generalNameInterface = getGeneralNameInterface(generalSubtree);
      boolean bool = false;
      for (byte b1 = 0; b1 < size(); b1++) {
        GeneralNameInterface generalNameInterface1 = getGeneralNameInterface(b1);
        switch (generalNameInterface1.constrains(generalNameInterface)) {
          case -1:
            bool = true;
            break;
          case 0:
          case 1:
          case 2:
          case 3:
            bool = false;
            break;
        } 
      } 
      if (bool)
        add(generalSubtree); 
    } 
    return generalSubtrees2;
  }
  
  public void union(GeneralSubtrees paramGeneralSubtrees) {
    if (paramGeneralSubtrees != null) {
      byte b = 0;
      int i = paramGeneralSubtrees.size();
      while (b < i) {
        add(paramGeneralSubtrees.get(b));
        b++;
      } 
      minimize();
    } 
  }
  
  public void reduce(GeneralSubtrees paramGeneralSubtrees) {
    if (paramGeneralSubtrees == null)
      return; 
    byte b = 0;
    int i = paramGeneralSubtrees.size();
    while (b < i) {
      GeneralNameInterface generalNameInterface = paramGeneralSubtrees.getGeneralNameInterface(b);
      for (byte b1 = 0; b1 < size(); b1++) {
        GeneralNameInterface generalNameInterface1 = getGeneralNameInterface(b1);
        switch (generalNameInterface.constrains(generalNameInterface1)) {
          case 0:
            remove(b1);
            b1--;
            break;
          case 1:
            remove(b1);
            b1--;
            break;
        } 
      } 
      b++;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\GeneralSubtrees.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */