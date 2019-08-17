package com.sun.xml.internal.fastinfoset;

import java.io.UnsupportedEncodingException;

public final class EncodingConstants {
  public static final String XML_NAMESPACE_PREFIX = "xml";
  
  public static final int XML_NAMESPACE_PREFIX_LENGTH;
  
  public static final String XML_NAMESPACE_NAME = "http://www.w3.org/XML/1998/namespace";
  
  public static final int XML_NAMESPACE_NAME_LENGTH;
  
  public static final String XMLNS_NAMESPACE_PREFIX = "xmlns";
  
  public static final int XMLNS_NAMESPACE_PREFIX_LENGTH;
  
  public static final String XMLNS_NAMESPACE_NAME = "http://www.w3.org/2000/xmlns/";
  
  public static final int XMLNS_NAMESPACE_NAME_LENGTH;
  
  public static final QualifiedName DEFAULT_NAMESPACE_DECLARATION;
  
  public static final int DOCUMENT_ADDITIONAL_DATA_FLAG = 64;
  
  public static final int DOCUMENT_INITIAL_VOCABULARY_FLAG = 32;
  
  public static final int DOCUMENT_NOTATIONS_FLAG = 16;
  
  public static final int DOCUMENT_UNPARSED_ENTITIES_FLAG = 8;
  
  public static final int DOCUMENT_CHARACTER_ENCODING_SCHEME = 4;
  
  public static final int DOCUMENT_STANDALONE_FLAG = 2;
  
  public static final int DOCUMENT_VERSION_FLAG = 1;
  
  public static final int INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG = 16;
  
  public static final int INITIAL_VOCABULARY_RESTRICTED_ALPHABETS_FLAG = 8;
  
  public static final int INITIAL_VOCABULARY_ENCODING_ALGORITHMS_FLAG = 4;
  
  public static final int INITIAL_VOCABULARY_PREFIXES_FLAG = 2;
  
  public static final int INITIAL_VOCABULARY_NAMESPACE_NAMES_FLAG = 1;
  
  public static final int INITIAL_VOCABULARY_LOCAL_NAMES_FLAG = 128;
  
  public static final int INITIAL_VOCABULARY_OTHER_NCNAMES_FLAG = 64;
  
  public static final int INITIAL_VOCABULARY_OTHER_URIS_FLAG = 32;
  
  public static final int INITIAL_VOCABULARY_ATTRIBUTE_VALUES_FLAG = 16;
  
  public static final int INITIAL_VOCABULARY_CONTENT_CHARACTER_CHUNKS_FLAG = 8;
  
  public static final int INITIAL_VOCABULARY_OTHER_STRINGS_FLAG = 4;
  
  public static final int INITIAL_VOCABULARY_ELEMENT_NAME_SURROGATES_FLAG = 2;
  
  public static final int INITIAL_VOCABULARY_ATTRIBUTE_NAME_SURROGATES_FLAG = 1;
  
  public static final int NAME_SURROGATE_PREFIX_FLAG = 2;
  
  public static final int NAME_SURROGATE_NAME_FLAG = 1;
  
  public static final int NOTATIONS = 192;
  
  public static final int NOTATIONS_MASK = 252;
  
  public static final int NOTATIONS_SYSTEM_IDENTIFIER_FLAG = 2;
  
  public static final int NOTATIONS_PUBLIC_IDENTIFIER_FLAG = 1;
  
  public static final int UNPARSED_ENTITIES = 208;
  
  public static final int UNPARSED_ENTITIES_MASK = 254;
  
  public static final int UNPARSED_ENTITIES_PUBLIC_IDENTIFIER_FLAG = 1;
  
  public static final int PROCESSING_INSTRUCTION = 225;
  
  public static final int PROCESSING_INSTRUCTION_MASK = 255;
  
  public static final int COMMENT = 226;
  
  public static final int COMMENT_MASK = 255;
  
  public static final int DOCUMENT_TYPE_DECLARATION = 196;
  
  public static final int DOCUMENT_TYPE_DECLARATION_MASK = 252;
  
  public static final int DOCUMENT_TYPE_SYSTEM_IDENTIFIER_FLAG = 2;
  
  public static final int DOCUMENT_TYPE_PUBLIC_IDENTIFIER_FLAG = 1;
  
  public static final int ELEMENT = 0;
  
  public static final int ELEMENT_ATTRIBUTE_FLAG = 64;
  
  public static final int ELEMENT_NAMESPACES_FLAG = 56;
  
  public static final int ELEMENT_LITERAL_QNAME_FLAG = 60;
  
  public static final int NAMESPACE_ATTRIBUTE = 204;
  
  public static final int NAMESPACE_ATTRIBUTE_MASK = 252;
  
  public static final int NAMESPACE_ATTRIBUTE_PREFIX_NAME_MASK = 3;
  
  public static final int NAMESPACE_ATTRIBUTE_PREFIX_FLAG = 2;
  
  public static final int NAMESPACE_ATTRIBUTE_NAME_FLAG = 1;
  
  public static final int ATTRIBUTE_LITERAL_QNAME_FLAG = 120;
  
  public static final int LITERAL_QNAME_PREFIX_NAMESPACE_NAME_MASK = 3;
  
  public static final int LITERAL_QNAME_PREFIX_FLAG = 2;
  
  public static final int LITERAL_QNAME_NAMESPACE_NAME_FLAG = 1;
  
  public static final int CHARACTER_CHUNK = 128;
  
  public static final int CHARACTER_CHUNK_ADD_TO_TABLE_FLAG = 16;
  
  public static final int CHARACTER_CHUNK_UTF_8_FLAG = 0;
  
  public static final int CHARACTER_CHUNK_UTF_16_FLAG = 4;
  
  public static final int CHARACTER_CHUNK_RESTRICTED_ALPHABET_FLAG = 8;
  
  public static final int CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG = 12;
  
  public static final int UNEXPANDED_ENTITY_REFERENCE = 200;
  
  public static final int UNEXPANDED_ENTITY_REFERENCE_MASK = 252;
  
  public static final int UNEXPANDED_ENTITY_SYSTEM_IDENTIFIER_FLAG = 2;
  
  public static final int UNEXPANDED_ENTITY_PUBLIC_IDENTIFIER_FLAG = 1;
  
  public static final int NISTRING_ADD_TO_TABLE_FLAG = 64;
  
  public static final int NISTRING_UTF_8_FLAG = 0;
  
  public static final int NISTRING_UTF_16_FLAG = 16;
  
  public static final int NISTRING_RESTRICTED_ALPHABET_FLAG = 32;
  
  public static final int NISTRING_ENCODING_ALGORITHM_FLAG = 48;
  
  public static final int TERMINATOR = 240;
  
  public static final int DOUBLE_TERMINATOR = 255;
  
  public static final int ENCODING_ALGORITHM_BUILTIN_END = 9;
  
  public static final int ENCODING_ALGORITHM_APPLICATION_START = 32;
  
  public static final int ENCODING_ALGORITHM_APPLICATION_MAX = 255;
  
  public static final int RESTRICTED_ALPHABET_BUILTIN_END = 1;
  
  public static final int RESTRICTED_ALPHABET_APPLICATION_START = 32;
  
  public static final int RESTRICTED_ALPHABET_APPLICATION_MAX = 255;
  
  public static final int OCTET_STRING_LENGTH_SMALL_LIMIT = 0;
  
  public static final int OCTET_STRING_LENGTH_MEDIUM_LIMIT = 1;
  
  public static final int OCTET_STRING_LENGTH_MEDIUM_FLAG = 2;
  
  public static final int OCTET_STRING_LENGTH_LARGE_FLAG = 3;
  
  public static final long OCTET_STRING_MAXIMUM_LENGTH = 4294967296L;
  
  public static final int OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT = 65;
  
  public static final int OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT = 321;
  
  public static final int OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_FLAG = 64;
  
  public static final int OCTET_STRING_LENGTH_2ND_BIT_LARGE_FLAG = 96;
  
  public static final int OCTET_STRING_LENGTH_2ND_BIT_SMALL_MASK = 31;
  
  static final int[] OCTET_STRING_LENGTH_2ND_BIT_VALUES;
  
  public static final int OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT = 9;
  
  public static final int OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT = 265;
  
  public static final int OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_FLAG = 8;
  
  public static final int OCTET_STRING_LENGTH_5TH_BIT_LARGE_FLAG = 12;
  
  public static final int OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK = 7;
  
  static final int[] OCTET_STRING_LENGTH_5TH_BIT_VALUES;
  
  public static final int OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT = 3;
  
  public static final int OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT = 259;
  
  public static final int OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_FLAG = 2;
  
  public static final int OCTET_STRING_LENGTH_7TH_BIT_LARGE_FLAG = 3;
  
  public static final int OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK = 1;
  
  static final int[] OCTET_STRING_LENGTH_7TH_BIT_VALUES;
  
  public static final int INTEGER_SMALL_LIMIT = 0;
  
  public static final int INTEGER_MEDIUM_LIMIT = 1;
  
  public static final int INTEGER_LARGE_LIMIT = 2;
  
  public static final int INTEGER_MEDIUM_FLAG = 3;
  
  public static final int INTEGER_LARGE_FLAG = 4;
  
  public static final int INTEGER_LARGE_LARGE_FLAG = 5;
  
  public static final int INTEGER_MAXIMUM_SIZE = 1048576;
  
  public static final int INTEGER_2ND_BIT_SMALL_LIMIT = 64;
  
  public static final int INTEGER_2ND_BIT_MEDIUM_LIMIT = 8256;
  
  public static final int INTEGER_2ND_BIT_LARGE_LIMIT = 1048576;
  
  public static final int INTEGER_2ND_BIT_MEDIUM_FLAG = 64;
  
  public static final int INTEGER_2ND_BIT_LARGE_FLAG = 96;
  
  public static final int INTEGER_2ND_BIT_SMALL_MASK = 63;
  
  public static final int INTEGER_2ND_BIT_MEDIUM_MASK = 31;
  
  public static final int INTEGER_2ND_BIT_LARGE_MASK = 15;
  
  static final int[] INTEGER_2ND_BIT_VALUES;
  
  public static final int INTEGER_3RD_BIT_SMALL_LIMIT = 32;
  
  public static final int INTEGER_3RD_BIT_MEDIUM_LIMIT = 2080;
  
  public static final int INTEGER_3RD_BIT_LARGE_LIMIT = 526368;
  
  public static final int INTEGER_3RD_BIT_MEDIUM_FLAG = 32;
  
  public static final int INTEGER_3RD_BIT_LARGE_FLAG = 40;
  
  public static final int INTEGER_3RD_BIT_LARGE_LARGE_FLAG = 48;
  
  public static final int INTEGER_3RD_BIT_SMALL_MASK = 31;
  
  public static final int INTEGER_3RD_BIT_MEDIUM_MASK = 7;
  
  public static final int INTEGER_3RD_BIT_LARGE_MASK = 7;
  
  public static final int INTEGER_3RD_BIT_LARGE_LARGE_MASK = 15;
  
  static final int[] INTEGER_3RD_BIT_VALUES;
  
  public static final int INTEGER_4TH_BIT_SMALL_LIMIT = 16;
  
  public static final int INTEGER_4TH_BIT_MEDIUM_LIMIT = 1040;
  
  public static final int INTEGER_4TH_BIT_LARGE_LIMIT = 263184;
  
  public static final int INTEGER_4TH_BIT_MEDIUM_FLAG = 16;
  
  public static final int INTEGER_4TH_BIT_LARGE_FLAG = 20;
  
  public static final int INTEGER_4TH_BIT_LARGE_LARGE_FLAG = 24;
  
  public static final int INTEGER_4TH_BIT_SMALL_MASK = 15;
  
  public static final int INTEGER_4TH_BIT_MEDIUM_MASK = 3;
  
  public static final int INTEGER_4TH_BIT_LARGE_MASK = 3;
  
  static final int[] INTEGER_4TH_BIT_VALUES;
  
  static final byte[] BINARY_HEADER;
  
  static byte[][] XML_DECLARATION_VALUES;
  
  private static void initiateXMLDeclarationValues() {
    XML_DECLARATION_VALUES = new byte[9][];
    try {
      XML_DECLARATION_VALUES[0] = "<?xml encoding='finf'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[1] = "<?xml version='1.0' encoding='finf'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[2] = "<?xml version='1.1' encoding='finf'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[3] = "<?xml encoding='finf' standalone='no'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[4] = "<?xml encoding='finf' standalone='yes'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[5] = "<?xml version='1.0' encoding='finf' standalone='no'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[6] = "<?xml version='1.1' encoding='finf' standalone='no'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[7] = "<?xml version='1.0' encoding='finf' standalone='yes'?>".getBytes("UTF-8");
      XML_DECLARATION_VALUES[8] = "<?xml version='1.1' encoding='finf' standalone='yes'?>".getBytes("UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
  }
  
  static  {
    initiateXMLDeclarationValues();
    XML_NAMESPACE_PREFIX_LENGTH = "xml".length();
    XML_NAMESPACE_NAME_LENGTH = "http://www.w3.org/XML/1998/namespace".length();
    XMLNS_NAMESPACE_PREFIX_LENGTH = "xmlns".length();
    XMLNS_NAMESPACE_NAME_LENGTH = "http://www.w3.org/2000/xmlns/".length();
    DEFAULT_NAMESPACE_DECLARATION = new QualifiedName("", "http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
    OCTET_STRING_LENGTH_2ND_BIT_VALUES = new int[] { 65, 321, 64, 96 };
    OCTET_STRING_LENGTH_5TH_BIT_VALUES = new int[] { 9, 265, 8, 12 };
    OCTET_STRING_LENGTH_7TH_BIT_VALUES = new int[] { 3, 259, 2, 3 };
    INTEGER_2ND_BIT_VALUES = new int[] { 64, 8256, 1048576, 64, 96, -1 };
    INTEGER_3RD_BIT_VALUES = new int[] { 32, 2080, 526368, 32, 40, 48 };
    INTEGER_4TH_BIT_VALUES = new int[] { 16, 1040, 263184, 16, 20, 24 };
    BINARY_HEADER = new byte[] { -32, 0, 0, 1 };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\EncodingConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */