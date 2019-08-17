package com.sun.xml.internal.txw2;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

public interface DatatypeWriter<DT> {
  public static final List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList(new AbstractList() {
        private DatatypeWriter<?>[] BUILTIN_ARRAY = { new DatatypeWriter<String>() {
              public Class<String> getType() { return String.class; }
              
              public void print(String param2String, NamespaceResolver param2NamespaceResolver, StringBuilder param2StringBuilder) { param2StringBuilder.append(param2String); }
            }, new DatatypeWriter<Integer>() {
              public Class<Integer> getType() { return Integer.class; }
              
              public void print(Integer param2Integer, NamespaceResolver param2NamespaceResolver, StringBuilder param2StringBuilder) { param2StringBuilder.append(param2Integer); }
            }, new DatatypeWriter<Float>() {
              public Class<Float> getType() { return Float.class; }
              
              public void print(Float param2Float, NamespaceResolver param2NamespaceResolver, StringBuilder param2StringBuilder) { param2StringBuilder.append(param2Float); }
            }, new DatatypeWriter<Double>() {
              public Class<Double> getType() { return Double.class; }
              
              public void print(Double param2Double, NamespaceResolver param2NamespaceResolver, StringBuilder param2StringBuilder) { param2StringBuilder.append(param2Double); }
            }, new DatatypeWriter<QName>() {
              public Class<QName> getType() { return QName.class; }
              
              public void print(QName param2QName, NamespaceResolver param2NamespaceResolver, StringBuilder param2StringBuilder) {
                String str = param2NamespaceResolver.getPrefix(param2QName.getNamespaceURI());
                if (str.length() != 0)
                  param2StringBuilder.append(str).append(':'); 
                param2StringBuilder.append(param2QName.getLocalPart());
              }
            } };
        
        public DatatypeWriter<?> get(int param1Int) { return this.BUILTIN_ARRAY[param1Int]; }
        
        public int size() { return this.BUILTIN_ARRAY.length; }
      });
  
  Class<DT> getType();
  
  void print(DT paramDT, NamespaceResolver paramNamespaceResolver, StringBuilder paramStringBuilder);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\DatatypeWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */