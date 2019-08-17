package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.StringPair;
import java.util.Properties;

public class PrefixParserData extends ParserDataBase {
  private StringPair[] testData;
  
  private Class componentType;
  
  public PrefixParserData(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2, StringPair[] paramArrayOfStringPair, Class paramClass) {
    super(paramString1, paramOperation, paramString2, paramObject1, paramObject2);
    this.testData = paramArrayOfStringPair;
    this.componentType = paramClass;
  }
  
  public void addToParser(PropertyParser paramPropertyParser) { paramPropertyParser.addPrefix(getPropertyName(), getOperation(), getFieldName(), this.componentType); }
  
  public void addToProperties(Properties paramProperties) {
    for (byte b = 0; b < this.testData.length; b++) {
      StringPair stringPair = this.testData[b];
      String str = getPropertyName();
      if (str.charAt(str.length() - 1) != '.')
        str = str + "."; 
      paramProperties.setProperty(str + stringPair.getFirst(), stringPair.getSecond());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\PrefixParserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */