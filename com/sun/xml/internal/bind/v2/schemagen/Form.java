package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;

static final abstract enum Form {
  QUALIFIED, UNQUALIFIED, UNSET;
  
  private final XmlNsForm xnf;
  
  public final boolean isEffectivelyQualified;
  
  Form(boolean paramBoolean1, boolean paramBoolean2) {
    this.xnf = paramBoolean1;
    this.isEffectivelyQualified = paramBoolean2;
  }
  
  abstract void declare(String paramString, Schema paramSchema);
  
  public void writeForm(LocalElement paramLocalElement, QName paramQName) { _writeForm(paramLocalElement, paramQName); }
  
  public void writeForm(LocalAttribute paramLocalAttribute, QName paramQName) { _writeForm(paramLocalAttribute, paramQName); }
  
  private void _writeForm(TypedXmlWriter paramTypedXmlWriter, QName paramQName) {
    boolean bool = (paramQName.getNamespaceURI().length() > 0) ? 1 : 0;
    if (bool && this != QUALIFIED) {
      paramTypedXmlWriter._attribute("form", "qualified");
    } else if (!bool && this == QUALIFIED) {
      paramTypedXmlWriter._attribute("form", "unqualified");
    } 
  }
  
  public static Form get(XmlNsForm paramXmlNsForm) {
    for (Form form : values()) {
      if (form.xnf == paramXmlNsForm)
        return form; 
    } 
    throw new IllegalArgumentException();
  }
  
  static  {
    // Byte code:
    //   0: new com/sun/xml/internal/bind/v2/schemagen/Form$1
    //   3: dup
    //   4: ldc 'QUALIFIED'
    //   6: iconst_0
    //   7: getstatic javax/xml/bind/annotation/XmlNsForm.QUALIFIED : Ljavax/xml/bind/annotation/XmlNsForm;
    //   10: iconst_1
    //   11: invokespecial <init> : (Ljava/lang/String;ILjavax/xml/bind/annotation/XmlNsForm;Z)V
    //   14: putstatic com/sun/xml/internal/bind/v2/schemagen/Form.QUALIFIED : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   17: new com/sun/xml/internal/bind/v2/schemagen/Form$2
    //   20: dup
    //   21: ldc 'UNQUALIFIED'
    //   23: iconst_1
    //   24: getstatic javax/xml/bind/annotation/XmlNsForm.UNQUALIFIED : Ljavax/xml/bind/annotation/XmlNsForm;
    //   27: iconst_0
    //   28: invokespecial <init> : (Ljava/lang/String;ILjavax/xml/bind/annotation/XmlNsForm;Z)V
    //   31: putstatic com/sun/xml/internal/bind/v2/schemagen/Form.UNQUALIFIED : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   34: new com/sun/xml/internal/bind/v2/schemagen/Form$3
    //   37: dup
    //   38: ldc 'UNSET'
    //   40: iconst_2
    //   41: getstatic javax/xml/bind/annotation/XmlNsForm.UNSET : Ljavax/xml/bind/annotation/XmlNsForm;
    //   44: iconst_0
    //   45: invokespecial <init> : (Ljava/lang/String;ILjavax/xml/bind/annotation/XmlNsForm;Z)V
    //   48: putstatic com/sun/xml/internal/bind/v2/schemagen/Form.UNSET : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   51: iconst_3
    //   52: anewarray com/sun/xml/internal/bind/v2/schemagen/Form
    //   55: dup
    //   56: iconst_0
    //   57: getstatic com/sun/xml/internal/bind/v2/schemagen/Form.QUALIFIED : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   60: aastore
    //   61: dup
    //   62: iconst_1
    //   63: getstatic com/sun/xml/internal/bind/v2/schemagen/Form.UNQUALIFIED : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   66: aastore
    //   67: dup
    //   68: iconst_2
    //   69: getstatic com/sun/xml/internal/bind/v2/schemagen/Form.UNSET : Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   72: aastore
    //   73: putstatic com/sun/xml/internal/bind/v2/schemagen/Form.$VALUES : [Lcom/sun/xml/internal/bind/v2/schemagen/Form;
    //   76: return
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Form.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */