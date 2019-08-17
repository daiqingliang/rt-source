package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.corba.se.impl.orb.ParserActionFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyParser {
  private List actions = new LinkedList();
  
  public PropertyParser add(String paramString1, Operation paramOperation, String paramString2) {
    this.actions.add(ParserActionFactory.makeNormalAction(paramString1, paramOperation, paramString2));
    return this;
  }
  
  public PropertyParser addPrefix(String paramString1, Operation paramOperation, String paramString2, Class paramClass) {
    this.actions.add(ParserActionFactory.makePrefixAction(paramString1, paramOperation, paramString2, paramClass));
    return this;
  }
  
  public Map parse(Properties paramProperties) {
    HashMap hashMap = new HashMap();
    for (ParserAction parserAction : this.actions) {
      Object object = parserAction.apply(paramProperties);
      if (object != null)
        hashMap.put(parserAction.getFieldName(), object); 
    } 
    return hashMap;
  }
  
  public Iterator iterator() { return this.actions.iterator(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\PropertyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */