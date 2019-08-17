package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class XSGrammarBucket {
  Map<String, SchemaGrammar> fGrammarRegistry = new HashMap();
  
  SchemaGrammar fNoNSGrammar = null;
  
  public SchemaGrammar getGrammar(String paramString) { return (paramString == null) ? this.fNoNSGrammar : (SchemaGrammar)this.fGrammarRegistry.get(paramString); }
  
  public void putGrammar(SchemaGrammar paramSchemaGrammar) {
    if (paramSchemaGrammar.getTargetNamespace() == null) {
      this.fNoNSGrammar = paramSchemaGrammar;
    } else {
      this.fGrammarRegistry.put(paramSchemaGrammar.getTargetNamespace(), paramSchemaGrammar);
    } 
  }
  
  public boolean putGrammar(SchemaGrammar paramSchemaGrammar, boolean paramBoolean) {
    SchemaGrammar schemaGrammar = getGrammar(paramSchemaGrammar.fTargetNamespace);
    if (schemaGrammar != null)
      return (schemaGrammar == paramSchemaGrammar); 
    if (!paramBoolean) {
      putGrammar(paramSchemaGrammar);
      return true;
    } 
    Vector vector1 = paramSchemaGrammar.getImportedGrammars();
    if (vector1 == null) {
      putGrammar(paramSchemaGrammar);
      return true;
    } 
    Vector vector2 = (Vector)vector1.clone();
    int i;
    for (i = 0; i < vector2.size(); i++) {
      SchemaGrammar schemaGrammar1 = (SchemaGrammar)vector2.elementAt(i);
      SchemaGrammar schemaGrammar2 = getGrammar(schemaGrammar1.fTargetNamespace);
      if (schemaGrammar2 == null) {
        Vector vector = schemaGrammar1.getImportedGrammars();
        if (vector != null)
          for (int j = vector.size() - 1; j >= 0; j--) {
            schemaGrammar2 = (SchemaGrammar)vector.elementAt(j);
            if (!vector2.contains(schemaGrammar2))
              vector2.addElement(schemaGrammar2); 
          }  
      } else if (schemaGrammar2 != schemaGrammar1) {
        return false;
      } 
    } 
    putGrammar(paramSchemaGrammar);
    for (i = vector2.size() - 1; i >= 0; i--)
      putGrammar((SchemaGrammar)vector2.elementAt(i)); 
    return true;
  }
  
  public boolean putGrammar(SchemaGrammar paramSchemaGrammar, boolean paramBoolean1, boolean paramBoolean2) {
    if (!paramBoolean2)
      return putGrammar(paramSchemaGrammar, paramBoolean1); 
    SchemaGrammar schemaGrammar = getGrammar(paramSchemaGrammar.fTargetNamespace);
    if (schemaGrammar == null)
      putGrammar(paramSchemaGrammar); 
    if (!paramBoolean1)
      return true; 
    Vector vector1 = paramSchemaGrammar.getImportedGrammars();
    if (vector1 == null)
      return true; 
    Vector vector2 = (Vector)vector1.clone();
    int i;
    for (i = 0; i < vector2.size(); i++) {
      SchemaGrammar schemaGrammar1 = (SchemaGrammar)vector2.elementAt(i);
      SchemaGrammar schemaGrammar2 = getGrammar(schemaGrammar1.fTargetNamespace);
      if (schemaGrammar2 == null) {
        Vector vector = schemaGrammar1.getImportedGrammars();
        if (vector != null)
          for (int j = vector.size() - 1; j >= 0; j--) {
            schemaGrammar2 = (SchemaGrammar)vector.elementAt(j);
            if (!vector2.contains(schemaGrammar2))
              vector2.addElement(schemaGrammar2); 
          }  
      } else {
        vector2.remove(schemaGrammar1);
      } 
    } 
    for (i = vector2.size() - 1; i >= 0; i--)
      putGrammar((SchemaGrammar)vector2.elementAt(i)); 
    return true;
  }
  
  public SchemaGrammar[] getGrammars() {
    int i = this.fGrammarRegistry.size() + ((this.fNoNSGrammar == null) ? 0 : 1);
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    byte b = 0;
    for (Map.Entry entry : this.fGrammarRegistry.entrySet())
      arrayOfSchemaGrammar[b++] = (SchemaGrammar)entry.getValue(); 
    if (this.fNoNSGrammar != null)
      arrayOfSchemaGrammar[i - 1] = this.fNoNSGrammar; 
    return arrayOfSchemaGrammar;
  }
  
  public void reset() {
    this.fNoNSGrammar = null;
    this.fGrammarRegistry.clear();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSGrammarBucket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */