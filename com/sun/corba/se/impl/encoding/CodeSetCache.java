package com.sun.corba.se.impl.encoding;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.WeakHashMap;

class CodeSetCache {
  private ThreadLocal converterCaches = new ThreadLocal() {
      public Object initialValue() { return new Map[] { new WeakHashMap(), new WeakHashMap() }; }
    };
  
  private static final int BTC_CACHE_MAP = 0;
  
  private static final int CTB_CACHE_MAP = 1;
  
  CharsetDecoder getByteToCharConverter(Object paramObject) {
    Map map = (Map[])this.converterCaches.get()[0];
    return (CharsetDecoder)map.get(paramObject);
  }
  
  CharsetEncoder getCharToByteConverter(Object paramObject) {
    Map map = (Map[])this.converterCaches.get()[1];
    return (CharsetEncoder)map.get(paramObject);
  }
  
  CharsetDecoder setConverter(Object paramObject, CharsetDecoder paramCharsetDecoder) {
    Map map = (Map[])this.converterCaches.get()[0];
    map.put(paramObject, paramCharsetDecoder);
    return paramCharsetDecoder;
  }
  
  CharsetEncoder setConverter(Object paramObject, CharsetEncoder paramCharsetEncoder) {
    Map map = (Map[])this.converterCaches.get()[1];
    map.put(paramObject, paramCharsetEncoder);
    return paramCharsetEncoder;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */