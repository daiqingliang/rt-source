package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PrintTable {
  public static void printVocabulary(ParserVocabulary paramParserVocabulary) {
    printArray("Attribute Name Table", paramParserVocabulary.attributeName);
    printArray("Attribute Value Table", paramParserVocabulary.attributeValue);
    printArray("Character Content Chunk Table", paramParserVocabulary.characterContentChunk);
    printArray("Element Name Table", paramParserVocabulary.elementName);
    printArray("Local Name Table", paramParserVocabulary.localName);
    printArray("Namespace Name Table", paramParserVocabulary.namespaceName);
    printArray("Other NCName Table", paramParserVocabulary.otherNCName);
    printArray("Other String Table", paramParserVocabulary.otherString);
    printArray("Other URI Table", paramParserVocabulary.otherURI);
    printArray("Prefix Table", paramParserVocabulary.prefix);
  }
  
  public static void printArray(String paramString, StringArray paramStringArray) {
    System.out.println(paramString);
    for (byte b = 0; b < paramStringArray.getSize(); b++)
      System.out.println("" + (b + true) + ": " + paramStringArray.getArray()[b]); 
  }
  
  public static void printArray(String paramString, PrefixArray paramPrefixArray) {
    System.out.println(paramString);
    for (byte b = 0; b < paramPrefixArray.getSize(); b++)
      System.out.println("" + (b + true) + ": " + paramPrefixArray.getArray()[b]); 
  }
  
  public static void printArray(String paramString, CharArrayArray paramCharArrayArray) {
    System.out.println(paramString);
    for (byte b = 0; b < paramCharArrayArray.getSize(); b++)
      System.out.println("" + (b + true) + ": " + paramCharArrayArray.getArray()[b]); 
  }
  
  public static void printArray(String paramString, ContiguousCharArrayArray paramContiguousCharArrayArray) {
    System.out.println(paramString);
    for (byte b = 0; b < paramContiguousCharArrayArray.getSize(); b++)
      System.out.println("" + (b + true) + ": " + paramContiguousCharArrayArray.getString(b)); 
  }
  
  public static void printArray(String paramString, QualifiedNameArray paramQualifiedNameArray) {
    System.out.println(paramString);
    for (byte b = 0; b < paramQualifiedNameArray.getSize(); b++) {
      QualifiedName qualifiedName = paramQualifiedNameArray.getArray()[b];
      System.out.println("" + (qualifiedName.index + 1) + ": {" + qualifiedName.namespaceName + "}" + qualifiedName.prefix + ":" + qualifiedName.localName);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      sAXParserFactory.setNamespaceAware(true);
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      ParserVocabulary parserVocabulary = new ParserVocabulary();
      VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(parserVocabulary);
      File file = new File(paramArrayOfString[0]);
      sAXParser.parse(file, vocabularyGenerator);
      printVocabulary(parserVocabulary);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\PrintTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */