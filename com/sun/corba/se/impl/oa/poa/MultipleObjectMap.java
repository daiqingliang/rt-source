package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;

class MultipleObjectMap extends ActiveObjectMap {
  private Map entryToKeys = new HashMap();
  
  public MultipleObjectMap(POAImpl paramPOAImpl) { super(paramPOAImpl); }
  
  public ActiveObjectMap.Key getKey(AOMEntry paramAOMEntry) throws WrongPolicy { throw new WrongPolicy(); }
  
  protected void putEntry(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry) {
    super.putEntry(paramKey, paramAOMEntry);
    Set set = (Set)this.entryToKeys.get(paramAOMEntry);
    if (set == null) {
      set = new HashSet();
      this.entryToKeys.put(paramAOMEntry, set);
    } 
    set.add(paramKey);
  }
  
  public boolean hasMultipleIDs(AOMEntry paramAOMEntry) {
    Set set = (Set)this.entryToKeys.get(paramAOMEntry);
    return (set == null) ? false : ((set.size() > 1));
  }
  
  protected void removeEntry(AOMEntry paramAOMEntry, ActiveObjectMap.Key paramKey) {
    Set set = (Set)this.entryToKeys.get(paramAOMEntry);
    if (set != null) {
      set.remove(paramKey);
      if (set.isEmpty())
        this.entryToKeys.remove(paramAOMEntry); 
    } 
  }
  
  public void clear() {
    super.clear();
    this.entryToKeys.clear();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\MultipleObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */