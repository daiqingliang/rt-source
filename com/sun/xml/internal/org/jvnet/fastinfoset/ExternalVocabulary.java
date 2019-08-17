package com.sun.xml.internal.org.jvnet.fastinfoset;

public class ExternalVocabulary {
  public final String URI;
  
  public final Vocabulary vocabulary;
  
  public ExternalVocabulary(String paramString, Vocabulary paramVocabulary) {
    if (paramString == null || paramVocabulary == null)
      throw new IllegalArgumentException(); 
    this.URI = paramString;
    this.vocabulary = paramVocabulary;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\ExternalVocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */