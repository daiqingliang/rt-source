package com.sun.org.apache.xml.internal.utils;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StylesheetPIHandler extends DefaultHandler {
  String m_baseID;
  
  String m_media;
  
  String m_title;
  
  String m_charset;
  
  Vector m_stylesheets = new Vector();
  
  URIResolver m_uriResolver;
  
  public void setURIResolver(URIResolver paramURIResolver) { this.m_uriResolver = paramURIResolver; }
  
  public URIResolver getURIResolver() { return this.m_uriResolver; }
  
  public StylesheetPIHandler(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.m_baseID = paramString1;
    this.m_media = paramString2;
    this.m_title = paramString3;
    this.m_charset = paramString4;
  }
  
  public Source getAssociatedStylesheet() {
    int i = this.m_stylesheets.size();
    return (i > 0) ? (Source)this.m_stylesheets.elementAt(i - 1) : null;
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (paramString1.equals("xml-stylesheet")) {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      String str5 = null;
      boolean bool = false;
      StringTokenizer stringTokenizer = new StringTokenizer(paramString2, " \t=\n", true);
      boolean bool1 = false;
      Source source = null;
      String str6 = "";
      while (stringTokenizer.hasMoreTokens()) {
        if (!bool1) {
          str6 = stringTokenizer.nextToken();
        } else {
          bool1 = false;
        } 
        if (stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")))
          continue; 
        String str = str6;
        if (str.equals("type")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          str2 = str6.substring(1, str6.length() - 1);
          continue;
        } 
        if (str.equals("href")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          str1 = str6;
          if (stringTokenizer.hasMoreTokens()) {
            str6 = stringTokenizer.nextToken();
            while (str6.equals("=") && stringTokenizer.hasMoreTokens()) {
              str1 = str1 + str6 + stringTokenizer.nextToken();
              if (stringTokenizer.hasMoreTokens()) {
                str6 = stringTokenizer.nextToken();
                bool1 = true;
              } 
            } 
          } 
          str1 = str1.substring(1, str1.length() - 1);
          try {
            if (this.m_uriResolver != null) {
              source = this.m_uriResolver.resolve(str1, this.m_baseID);
              continue;
            } 
            str1 = SystemIDResolver.getAbsoluteURI(str1, this.m_baseID);
            source = new SAXSource(new InputSource(str1));
            continue;
          } catch (TransformerException transformerException) {
            throw new SAXException(transformerException);
          } 
        } 
        if (str.equals("title")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          str3 = str6.substring(1, str6.length() - 1);
          continue;
        } 
        if (str.equals("media")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          str4 = str6.substring(1, str6.length() - 1);
          continue;
        } 
        if (str.equals("charset")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          str5 = str6.substring(1, str6.length() - 1);
          continue;
        } 
        if (str.equals("alternate")) {
          for (str6 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens() && (str6.equals(" ") || str6.equals("\t") || str6.equals("=")); str6 = stringTokenizer.nextToken());
          bool = str6.substring(1, str6.length() - 1).equals("yes");
        } 
      } 
      if (null != str2 && (str2.equals("text/xsl") || str2.equals("text/xml") || str2.equals("application/xml+xslt")) && null != str1) {
        if (null != this.m_media)
          if (null != str4) {
            if (!str4.equals(this.m_media))
              return; 
          } else {
            return;
          }  
        if (null != this.m_charset)
          if (null != str5) {
            if (!str5.equals(this.m_charset))
              return; 
          } else {
            return;
          }  
        if (null != this.m_title)
          if (null != str3) {
            if (!str3.equals(this.m_title))
              return; 
          } else {
            return;
          }  
        this.m_stylesheets.addElement(source);
      } 
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { throw new StopParseException(); }
  
  public void setBaseId(String paramString) { this.m_baseID = paramString; }
  
  public String getBaseId() { return this.m_baseID; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StylesheetPIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */