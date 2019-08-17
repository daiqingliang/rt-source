package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.NamespaceContext;

public interface NamespaceContext2 extends NamespaceContext {
  String declareNamespace(String paramString1, String paramString2, boolean paramBoolean);
  
  int force(@NotNull String paramString1, @NotNull String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\NamespaceContext2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */