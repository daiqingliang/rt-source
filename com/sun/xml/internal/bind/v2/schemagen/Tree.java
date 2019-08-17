package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class Tree {
  Tree makeOptional(boolean paramBoolean) { return paramBoolean ? new Optional(this, null) : this; }
  
  Tree makeRepeated(boolean paramBoolean) { return paramBoolean ? new Repeated(this, null) : this; }
  
  static Tree makeGroup(GroupKind paramGroupKind, List<Tree> paramList) {
    if (paramList.size() == 1)
      return (Tree)paramList.get(0); 
    ArrayList arrayList = new ArrayList(paramList.size());
    for (Tree tree : paramList) {
      if (tree instanceof Group) {
        Group group;
        if (group.kind == paramGroupKind) {
          arrayList.addAll(Arrays.asList(group.children));
          continue;
        } 
      } 
      arrayList.add(tree);
    } 
    return new Group(paramGroupKind, (Tree[])arrayList.toArray(new Tree[arrayList.size()]), null);
  }
  
  abstract boolean isNullable();
  
  boolean canBeTopLevel() { return false; }
  
  protected abstract void write(ContentModelContainer paramContentModelContainer, boolean paramBoolean1, boolean paramBoolean2);
  
  protected void write(TypeDefParticle paramTypeDefParticle) {
    if (canBeTopLevel()) {
      write((ContentModelContainer)paramTypeDefParticle._cast(ContentModelContainer.class), false, false);
    } else {
      (new Group(GroupKind.SEQUENCE, new Tree[] { this }, null)).write(paramTypeDefParticle);
    } 
  }
  
  protected final void writeOccurs(Occurs paramOccurs, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1)
      paramOccurs.minOccurs(0); 
    if (paramBoolean2)
      paramOccurs.maxOccurs("unbounded"); 
  }
  
  private static final class Group extends Tree {
    private final GroupKind kind;
    
    private final Tree[] children;
    
    private Group(GroupKind param1GroupKind, Tree... param1VarArgs) {
      this.kind = param1GroupKind;
      this.children = param1VarArgs;
    }
    
    boolean canBeTopLevel() { return true; }
    
    boolean isNullable() {
      if (this.kind == GroupKind.CHOICE) {
        for (Tree tree : this.children) {
          if (tree.isNullable())
            return true; 
        } 
        return false;
      } 
      for (Tree tree : this.children) {
        if (!tree.isNullable())
          return false; 
      } 
      return true;
    }
    
    protected void write(ContentModelContainer param1ContentModelContainer, boolean param1Boolean1, boolean param1Boolean2) {
      Particle particle = this.kind.write(param1ContentModelContainer);
      writeOccurs(particle, param1Boolean1, param1Boolean2);
      for (Tree tree : this.children)
        tree.write(particle, false, false); 
    }
  }
  
  private static final class Optional extends Tree {
    private final Tree body;
    
    private Optional(Tree param1Tree) { this.body = param1Tree; }
    
    boolean isNullable() { return true; }
    
    Tree makeOptional(boolean param1Boolean) { return this; }
    
    protected void write(ContentModelContainer param1ContentModelContainer, boolean param1Boolean1, boolean param1Boolean2) { this.body.write(param1ContentModelContainer, true, param1Boolean2); }
  }
  
  private static final class Repeated extends Tree {
    private final Tree body;
    
    private Repeated(Tree param1Tree) { this.body = param1Tree; }
    
    boolean isNullable() { return this.body.isNullable(); }
    
    Tree makeRepeated(boolean param1Boolean) { return this; }
    
    protected void write(ContentModelContainer param1ContentModelContainer, boolean param1Boolean1, boolean param1Boolean2) { this.body.write(param1ContentModelContainer, param1Boolean1, true); }
  }
  
  static abstract class Term extends Tree {
    boolean isNullable() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Tree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */