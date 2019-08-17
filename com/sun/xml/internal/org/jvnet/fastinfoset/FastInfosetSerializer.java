package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.util.Map;

public interface FastInfosetSerializer {
  public static final String IGNORE_DTD_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/DTD";
  
  public static final String IGNORE_COMMENTS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/comments";
  
  public static final String IGNORE_PROCESSING_INSTRUCTIONS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/processingInstructions";
  
  public static final String IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/whiteSpaceTextContent";
  
  public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
  
  public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
  
  public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
  
  public static final int MIN_CHARACTER_CONTENT_CHUNK_SIZE = 0;
  
  public static final int MAX_CHARACTER_CONTENT_CHUNK_SIZE = 32;
  
  public static final int CHARACTER_CONTENT_CHUNK_MAP_MEMORY_CONSTRAINT = 2147483647;
  
  public static final int MIN_ATTRIBUTE_VALUE_SIZE = 0;
  
  public static final int MAX_ATTRIBUTE_VALUE_SIZE = 32;
  
  public static final int ATTRIBUTE_VALUE_MAP_MEMORY_CONSTRAINT = 2147483647;
  
  public static final String UTF_8 = "UTF-8";
  
  public static final String UTF_16BE = "UTF-16BE";
  
  void setIgnoreDTD(boolean paramBoolean);
  
  boolean getIgnoreDTD();
  
  void setIgnoreComments(boolean paramBoolean);
  
  boolean getIgnoreComments();
  
  void setIgnoreProcesingInstructions(boolean paramBoolean);
  
  boolean getIgnoreProcesingInstructions();
  
  void setIgnoreWhiteSpaceTextContent(boolean paramBoolean);
  
  boolean getIgnoreWhiteSpaceTextContent();
  
  void setCharacterEncodingScheme(String paramString);
  
  String getCharacterEncodingScheme();
  
  void setRegisteredEncodingAlgorithms(Map paramMap);
  
  Map getRegisteredEncodingAlgorithms();
  
  int getMinCharacterContentChunkSize();
  
  void setMinCharacterContentChunkSize(int paramInt);
  
  int getMaxCharacterContentChunkSize();
  
  void setMaxCharacterContentChunkSize(int paramInt);
  
  int getCharacterContentChunkMapMemoryLimit();
  
  void setCharacterContentChunkMapMemoryLimit(int paramInt);
  
  int getMinAttributeValueSize();
  
  void setMinAttributeValueSize(int paramInt);
  
  int getMaxAttributeValueSize();
  
  void setMaxAttributeValueSize(int paramInt);
  
  int getAttributeValueMapMemoryLimit();
  
  void setAttributeValueMapMemoryLimit(int paramInt);
  
  void setExternalVocabulary(ExternalVocabulary paramExternalVocabulary);
  
  void setVocabularyApplicationData(VocabularyApplicationData paramVocabularyApplicationData);
  
  VocabularyApplicationData getVocabularyApplicationData();
  
  void reset();
  
  void setOutputStream(OutputStream paramOutputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */