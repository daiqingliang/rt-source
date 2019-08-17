package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public class PrefixParserAction extends ParserActionBase {
  private Class componentType;
  
  private ORBUtilSystemException wrapper;
  
  public PrefixParserAction(String paramString1, Operation paramOperation, String paramString2, Class paramClass) {
    super(paramString1, true, paramOperation, paramString2);
    this.componentType = paramClass;
    this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
  }
  
  public Object apply(Properties paramProperties) {
    String str = getPropertyName();
    int i = str.length();
    if (str.charAt(i - 1) != '.') {
      str = str + '.';
      i++;
    } 
    LinkedList linkedList = new LinkedList();
    for (String str1 : paramProperties.keySet()) {
      if (str1.startsWith(str)) {
        String str2 = str1.substring(i);
        String str3 = paramProperties.getProperty(str1);
        StringPair stringPair = new StringPair(str2, str3);
        Object object = getOperation().operate(stringPair);
        linkedList.add(object);
      } 
    } 
    int j = linkedList.size();
    if (j > 0) {
      Object object = null;
      try {
        object = Array.newInstance(this.componentType, j);
      } catch (Throwable throwable) {
        throw this.wrapper.couldNotCreateArray(throwable, getPropertyName(), this.componentType, new Integer(j));
      } 
      Iterator iterator = linkedList.iterator();
      for (byte b = 0; iterator.hasNext(); b++) {
        Object object1 = iterator.next();
        try {
          Array.set(object, b, object1);
        } catch (Throwable throwable) {
          throw this.wrapper.couldNotSetArray(throwable, getPropertyName(), new Integer(b), this.componentType, new Integer(j), object1.toString());
        } 
      } 
      return object;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\PrefixParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */