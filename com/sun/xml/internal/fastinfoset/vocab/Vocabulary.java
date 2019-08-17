package com.sun.xml.internal.fastinfoset.vocab;

public abstract class Vocabulary {
  public static final int RESTRICTED_ALPHABET = 0;
  
  public static final int ENCODING_ALGORITHM = 1;
  
  public static final int PREFIX = 2;
  
  public static final int NAMESPACE_NAME = 3;
  
  public static final int LOCAL_NAME = 4;
  
  public static final int OTHER_NCNAME = 5;
  
  public static final int OTHER_URI = 6;
  
  public static final int ATTRIBUTE_VALUE = 7;
  
  public static final int OTHER_STRING = 8;
  
  public static final int CHARACTER_CONTENT_CHUNK = 9;
  
  public static final int ELEMENT_NAME = 10;
  
  public static final int ATTRIBUTE_NAME = 11;
  
  protected boolean _hasInitialReadOnlyVocabulary;
  
  protected String _referencedVocabularyURI;
  
  public boolean hasInitialVocabulary() { return this._hasInitialReadOnlyVocabulary; }
  
  protected void setInitialReadOnlyVocabulary(boolean paramBoolean) { this._hasInitialReadOnlyVocabulary = paramBoolean; }
  
  public boolean hasExternalVocabulary() { return (this._referencedVocabularyURI != null); }
  
  public String getExternalVocabularyURI() { return this._referencedVocabularyURI; }
  
  protected void setExternalVocabularyURI(String paramString) { this._referencedVocabularyURI = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\vocab\Vocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */