package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.Map;

public interface FastInfosetParser {
  public static final String STRING_INTERNING_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/string-interning";
  
  public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
  
  public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
  
  public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
  
  public static final String FORCE_STREAM_CLOSE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/force-stream-close";
  
  void setStringInterning(boolean paramBoolean);
  
  boolean getStringInterning();
  
  void setBufferSize(int paramInt);
  
  int getBufferSize();
  
  void setRegisteredEncodingAlgorithms(Map paramMap);
  
  Map getRegisteredEncodingAlgorithms();
  
  void setExternalVocabularies(Map paramMap);
  
  Map getExternalVocabularies();
  
  void setParseFragments(boolean paramBoolean);
  
  boolean getParseFragments();
  
  void setForceStreamClose(boolean paramBoolean);
  
  boolean getForceStreamClose();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */