package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;

public interface MessageHeaders {
  void understood(Header paramHeader);
  
  void understood(QName paramQName);
  
  void understood(String paramString1, String paramString2);
  
  Header get(String paramString1, String paramString2, boolean paramBoolean);
  
  Header get(QName paramQName, boolean paramBoolean);
  
  Iterator<Header> getHeaders(String paramString1, String paramString2, boolean paramBoolean);
  
  Iterator<Header> getHeaders(String paramString, boolean paramBoolean);
  
  Iterator<Header> getHeaders(QName paramQName, boolean paramBoolean);
  
  Iterator<Header> getHeaders();
  
  boolean hasHeaders();
  
  boolean add(Header paramHeader);
  
  Header remove(QName paramQName);
  
  Header remove(String paramString1, String paramString2);
  
  void replace(Header paramHeader1, Header paramHeader2);
  
  boolean addOrReplace(Header paramHeader);
  
  Set<QName> getUnderstoodHeaders();
  
  Set<QName> getNotUnderstoodHeaders(Set<String> paramSet1, Set<QName> paramSet2, WSBinding paramWSBinding);
  
  boolean isUnderstood(Header paramHeader);
  
  boolean isUnderstood(QName paramQName);
  
  boolean isUnderstood(String paramString1, String paramString2);
  
  List<Header> asList();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\MessageHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */