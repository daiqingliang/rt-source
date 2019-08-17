package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class ActiveObjectMap {
  protected POAImpl poa;
  
  private Map keyToEntry = new HashMap();
  
  private Map entryToServant = new HashMap();
  
  private Map servantToEntry = new HashMap();
  
  protected ActiveObjectMap(POAImpl paramPOAImpl) { this.poa = paramPOAImpl; }
  
  public static ActiveObjectMap create(POAImpl paramPOAImpl, boolean paramBoolean) { return paramBoolean ? new MultipleObjectMap(paramPOAImpl) : new SingleObjectMap(paramPOAImpl); }
  
  public final boolean contains(Servant paramServant) { return this.servantToEntry.containsKey(paramServant); }
  
  public final boolean containsKey(Key paramKey) { return this.keyToEntry.containsKey(paramKey); }
  
  public final AOMEntry get(Key paramKey) {
    AOMEntry aOMEntry = (AOMEntry)this.keyToEntry.get(paramKey);
    if (aOMEntry == null) {
      aOMEntry = new AOMEntry(this.poa);
      putEntry(paramKey, aOMEntry);
    } 
    return aOMEntry;
  }
  
  public final Servant getServant(AOMEntry paramAOMEntry) { return (Servant)this.entryToServant.get(paramAOMEntry); }
  
  public abstract Key getKey(AOMEntry paramAOMEntry) throws WrongPolicy;
  
  public Key getKey(Servant paramServant) throws WrongPolicy {
    AOMEntry aOMEntry = (AOMEntry)this.servantToEntry.get(paramServant);
    return getKey(aOMEntry);
  }
  
  protected void putEntry(Key paramKey, AOMEntry paramAOMEntry) { this.keyToEntry.put(paramKey, paramAOMEntry); }
  
  public final void putServant(Servant paramServant, AOMEntry paramAOMEntry) {
    this.entryToServant.put(paramAOMEntry, paramServant);
    this.servantToEntry.put(paramServant, paramAOMEntry);
  }
  
  protected abstract void removeEntry(AOMEntry paramAOMEntry, Key paramKey);
  
  public final void remove(Key paramKey) {
    AOMEntry aOMEntry = (AOMEntry)this.keyToEntry.remove(paramKey);
    Servant servant = (Servant)this.entryToServant.remove(aOMEntry);
    if (servant != null)
      this.servantToEntry.remove(servant); 
    removeEntry(aOMEntry, paramKey);
  }
  
  public abstract boolean hasMultipleIDs(AOMEntry paramAOMEntry);
  
  protected void clear() { this.keyToEntry.clear(); }
  
  public final Set keySet() { return this.keyToEntry.keySet(); }
  
  public static class Key {
    public byte[] id;
    
    Key(byte[] param1ArrayOfByte) { this.id = param1ArrayOfByte; }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.id.length; b++) {
        stringBuffer.append(Integer.toString(this.id[b], 16));
        if (b != this.id.length - 1)
          stringBuffer.append(":"); 
      } 
      return stringBuffer.toString();
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Key))
        return false; 
      Key key = (Key)param1Object;
      if (key.id.length != this.id.length)
        return false; 
      for (byte b = 0; b < this.id.length; b++) {
        if (this.id[b] != key.id[b])
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      byte b = 0;
      for (byte b1 = 0; b1 < this.id.length; b1++)
        b = 31 * b + this.id[b1]; 
      return b;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\ActiveObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */