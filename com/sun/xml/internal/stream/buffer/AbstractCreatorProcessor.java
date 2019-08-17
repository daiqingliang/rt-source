package com.sun.xml.internal.stream.buffer;

public abstract class AbstractCreatorProcessor {
  protected static final int FLAG_DOCUMENT_FRAGMENT = 1;
  
  protected static final int FLAG_PREFIX = 1;
  
  protected static final int FLAG_URI = 2;
  
  protected static final int FLAG_QUALIFIED_NAME = 4;
  
  protected static final int CONTENT_TYPE_CHAR_ARRAY = 0;
  
  protected static final int CONTENT_TYPE_CHAR_ARRAY_COPY = 4;
  
  protected static final int CONTENT_TYPE_STRING = 8;
  
  protected static final int CONTENT_TYPE_OBJECT = 12;
  
  protected static final int CHAR_ARRAY_LENGTH_SMALL = 0;
  
  protected static final int CHAR_ARRAY_LENGTH_MEDIUM = 1;
  
  protected static final int CHAR_ARRAY_LENGTH_SMALL_SIZE = 256;
  
  protected static final int CHAR_ARRAY_LENGTH_MEDIUM_SIZE = 65536;
  
  protected static final int VALUE_TYPE_STRING = 0;
  
  protected static final int VALUE_TYPE_OBJECT = 8;
  
  protected static final int TYPE_MASK = 240;
  
  protected static final int T_DOCUMENT = 16;
  
  protected static final int T_ELEMENT = 32;
  
  protected static final int T_ATTRIBUTE = 48;
  
  protected static final int T_NAMESPACE_ATTRIBUTE = 64;
  
  protected static final int T_TEXT = 80;
  
  protected static final int T_COMMENT = 96;
  
  protected static final int T_PROCESSING_INSTRUCTION = 112;
  
  protected static final int T_UNEXPANDED_ENTITY_REFERENCE = 128;
  
  protected static final int T_END = 144;
  
  protected static final int T_DOCUMENT_FRAGMENT = 17;
  
  protected static final int T_ELEMENT_U_LN_QN = 38;
  
  protected static final int T_ELEMENT_P_U_LN = 35;
  
  protected static final int T_ELEMENT_U_LN = 34;
  
  protected static final int T_ELEMENT_LN = 32;
  
  protected static final int T_NAMESPACE_ATTRIBUTE_P = 65;
  
  protected static final int T_NAMESPACE_ATTRIBUTE_P_U = 67;
  
  protected static final int T_NAMESPACE_ATTRIBUTE_U = 66;
  
  protected static final int T_ATTRIBUTE_U_LN_QN = 54;
  
  protected static final int T_ATTRIBUTE_P_U_LN = 51;
  
  protected static final int T_ATTRIBUTE_U_LN = 50;
  
  protected static final int T_ATTRIBUTE_LN = 48;
  
  protected static final int T_ATTRIBUTE_U_LN_QN_OBJECT = 62;
  
  protected static final int T_ATTRIBUTE_P_U_LN_OBJECT = 59;
  
  protected static final int T_ATTRIBUTE_U_LN_OBJECT = 58;
  
  protected static final int T_ATTRIBUTE_LN_OBJECT = 56;
  
  protected static final int T_TEXT_AS_CHAR_ARRAY = 80;
  
  protected static final int T_TEXT_AS_CHAR_ARRAY_SMALL = 80;
  
  protected static final int T_TEXT_AS_CHAR_ARRAY_MEDIUM = 81;
  
  protected static final int T_TEXT_AS_CHAR_ARRAY_COPY = 84;
  
  protected static final int T_TEXT_AS_STRING = 88;
  
  protected static final int T_TEXT_AS_OBJECT = 92;
  
  protected static final int T_COMMENT_AS_CHAR_ARRAY = 96;
  
  protected static final int T_COMMENT_AS_CHAR_ARRAY_SMALL = 96;
  
  protected static final int T_COMMENT_AS_CHAR_ARRAY_MEDIUM = 97;
  
  protected static final int T_COMMENT_AS_CHAR_ARRAY_COPY = 100;
  
  protected static final int T_COMMENT_AS_STRING = 104;
  
  protected static final int T_END_OF_BUFFER = -1;
  
  protected FragmentedArray<byte[]> _currentStructureFragment;
  
  protected byte[] _structure;
  
  protected int _structurePtr;
  
  protected FragmentedArray<String[]> _currentStructureStringFragment;
  
  protected String[] _structureStrings;
  
  protected int _structureStringsPtr;
  
  protected FragmentedArray<char[]> _currentContentCharactersBufferFragment;
  
  protected char[] _contentCharactersBuffer;
  
  protected int _contentCharactersBufferPtr;
  
  protected FragmentedArray<Object[]> _currentContentObjectFragment;
  
  protected Object[] _contentObjects;
  
  protected int _contentObjectsPtr;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\AbstractCreatorProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */