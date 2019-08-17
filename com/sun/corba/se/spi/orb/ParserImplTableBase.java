package com.sun.corba.se.spi.orb;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ParserImplTableBase extends ParserImplBase {
  private final ParserData[] entries;
  
  public ParserImplTableBase(ParserData[] paramArrayOfParserData) {
    this.entries = paramArrayOfParserData;
    setDefaultValues();
  }
  
  protected PropertyParser makeParser() {
    PropertyParser propertyParser = new PropertyParser();
    for (byte b = 0; b < this.entries.length; b++) {
      ParserData parserData = this.entries[b];
      parserData.addToParser(propertyParser);
    } 
    return propertyParser;
  }
  
  protected void setDefaultValues() {
    FieldMap fieldMap = new FieldMap(this.entries, true);
    setFields(fieldMap);
  }
  
  public void setTestValues() {
    FieldMap fieldMap = new FieldMap(this.entries, false);
    setFields(fieldMap);
  }
  
  private static class FieldMap extends AbstractMap {
    private final ParserData[] entries;
    
    private final boolean useDefault;
    
    public FieldMap(ParserData[] param1ArrayOfParserData, boolean param1Boolean) {
      this.entries = param1ArrayOfParserData;
      this.useDefault = param1Boolean;
    }
    
    public Set entrySet() { return new AbstractSet() {
          public Iterator iterator() { return new Iterator() {
                int ctr = 0;
                
                public boolean hasNext() { return (this.ctr < ParserImplTableBase.FieldMap.null.this.this$0.entries.length); }
                
                public Object next() {
                  ParserData parserData = ParserImplTableBase.FieldMap.null.this.this$0.entries[this.ctr++];
                  ParserImplTableBase.MapEntry mapEntry = new ParserImplTableBase.MapEntry(parserData.getFieldName());
                  if (ParserImplTableBase.FieldMap.null.this.this$0.useDefault) {
                    mapEntry.setValue(parserData.getDefaultValue());
                  } else {
                    mapEntry.setValue(parserData.getTestValue());
                  } 
                  return mapEntry;
                }
                
                public void remove() { throw new UnsupportedOperationException(); }
              }; }
          
          public int size() { return ParserImplTableBase.FieldMap.this.entries.length; }
        }; }
  }
  
  private static final class MapEntry implements Map.Entry {
    private Object key;
    
    private Object value;
    
    public MapEntry(Object param1Object) { this.key = param1Object; }
    
    public Object getKey() { return this.key; }
    
    public Object getValue() { return this.value; }
    
    public Object setValue(Object param1Object) {
      Object object = this.value;
      this.value = param1Object;
      return object;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof MapEntry))
        return false; 
      MapEntry mapEntry = (MapEntry)param1Object;
      return (this.key.equals(mapEntry.key) && this.value.equals(mapEntry.value));
    }
    
    public int hashCode() { return this.key.hashCode() ^ this.value.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\ParserImplTableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */