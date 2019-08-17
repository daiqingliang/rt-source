package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;

static enum GroupKind {
  ALL("all"),
  SEQUENCE("sequence"),
  CHOICE("choice");
  
  private final String name;
  
  GroupKind(String paramString1) { this.name = paramString1; }
  
  Particle write(ContentModelContainer paramContentModelContainer) { return (Particle)paramContentModelContainer._element(this.name, Particle.class); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\GroupKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */