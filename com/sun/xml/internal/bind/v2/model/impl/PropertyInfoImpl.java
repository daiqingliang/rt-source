package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import javax.activation.MimeType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;

abstract class PropertyInfoImpl<T, C, F, M> extends Object implements PropertyInfo<T, C>, Locatable, Comparable<PropertyInfoImpl> {
  protected final PropertySeed<T, C, F, M> seed;
  
  private final boolean isCollection;
  
  private final ID id;
  
  private final MimeType expectedMimeType;
  
  private final boolean inlineBinary;
  
  private final QName schemaType;
  
  protected final ClassInfoImpl<T, C, F, M> parent;
  
  private final Adapter<T, C> adapter;
  
  protected PropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed) {
    this.seed = paramPropertySeed;
    this.parent = paramClassInfoImpl;
    if (paramClassInfoImpl == null)
      throw new AssertionError(); 
    MimeType mimeType = Util.calcExpectedMediaType(this.seed, paramClassInfoImpl.builder);
    if (mimeType != null && !(kind()).canHaveXmlMimeType) {
      paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_ANNOTATION.format(new Object[] { javax.xml.bind.annotation.XmlMimeType.class.getName() }, ), this.seed.readAnnotation(javax.xml.bind.annotation.XmlMimeType.class)));
      mimeType = null;
    } 
    this.expectedMimeType = mimeType;
    this.inlineBinary = this.seed.hasAnnotation(javax.xml.bind.annotation.XmlInlineBinaryData.class);
    Object object = this.seed.getRawType();
    XmlJavaTypeAdapter xmlJavaTypeAdapter = getApplicableAdapter(object);
    if (xmlJavaTypeAdapter != null) {
      this.isCollection = false;
      this.adapter = new Adapter(xmlJavaTypeAdapter, reader(), nav());
    } else {
      this.isCollection = (nav().isSubClassOf(object, nav().ref(java.util.Collection.class)) || nav().isArrayButNotByteArray(object));
      xmlJavaTypeAdapter = getApplicableAdapter(getIndividualType());
      if (xmlJavaTypeAdapter == null) {
        XmlAttachmentRef xmlAttachmentRef = (XmlAttachmentRef)this.seed.readAnnotation(XmlAttachmentRef.class);
        if (xmlAttachmentRef != null) {
          paramClassInfoImpl.builder.hasSwaRef = true;
          this.adapter = new Adapter(nav().asDecl(com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter.class), nav());
        } else {
          this.adapter = null;
          xmlJavaTypeAdapter = (XmlJavaTypeAdapter)this.seed.readAnnotation(XmlJavaTypeAdapter.class);
          if (xmlJavaTypeAdapter != null) {
            Object object1 = reader().getClassValue(xmlJavaTypeAdapter, "value");
            paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.UNMATCHABLE_ADAPTER.format(new Object[] { nav().getTypeName(object1), nav().getTypeName(object) }, ), xmlJavaTypeAdapter));
          } 
        } 
      } else {
        this.adapter = new Adapter(xmlJavaTypeAdapter, reader(), nav());
      } 
    } 
    this.id = calcId();
    this.schemaType = Util.calcSchemaType(reader(), this.seed, paramClassInfoImpl.clazz, getIndividualType(), this);
  }
  
  public ClassInfoImpl<T, C, F, M> parent() { return this.parent; }
  
  protected final Navigator<T, C, F, M> nav() { return this.parent.nav(); }
  
  protected final AnnotationReader<T, C, F, M> reader() { return this.parent.reader(); }
  
  public T getRawType() { return (T)this.seed.getRawType(); }
  
  public T getIndividualType() {
    if (this.adapter != null)
      return (T)this.adapter.defaultType; 
    Object object1 = getRawType();
    if (!isCollection())
      return (T)object1; 
    if (nav().isArrayButNotByteArray(object1))
      return (T)nav().getComponentType(object1); 
    Object object2 = nav().getBaseClass(object1, nav().asDecl(java.util.Collection.class));
    return nav().isParameterizedType(object2) ? (T)nav().getTypeArgument(object2, 0) : (T)nav().ref(Object.class);
  }
  
  public final String getName() { return this.seed.getName(); }
  
  private boolean isApplicable(XmlJavaTypeAdapter paramXmlJavaTypeAdapter, T paramT) {
    if (paramXmlJavaTypeAdapter == null)
      return false; 
    Object object1 = reader().getClassValue(paramXmlJavaTypeAdapter, "type");
    if (nav().isSameType(paramT, object1))
      return true; 
    Object object2 = reader().getClassValue(paramXmlJavaTypeAdapter, "value");
    Object object3 = nav().getBaseClass(object2, nav().asDecl(javax.xml.bind.annotation.adapters.XmlAdapter.class));
    if (!nav().isParameterizedType(object3))
      return true; 
    Object object4 = nav().getTypeArgument(object3, 1);
    return nav().isSubClassOf(paramT, object4);
  }
  
  private XmlJavaTypeAdapter getApplicableAdapter(T paramT) {
    XmlJavaTypeAdapter xmlJavaTypeAdapter = (XmlJavaTypeAdapter)this.seed.readAnnotation(XmlJavaTypeAdapter.class);
    if (xmlJavaTypeAdapter != null && isApplicable(xmlJavaTypeAdapter, paramT))
      return xmlJavaTypeAdapter; 
    XmlJavaTypeAdapters xmlJavaTypeAdapters = (XmlJavaTypeAdapters)reader().getPackageAnnotation(XmlJavaTypeAdapters.class, this.parent.clazz, this.seed);
    if (xmlJavaTypeAdapters != null)
      for (XmlJavaTypeAdapter xmlJavaTypeAdapter1 : xmlJavaTypeAdapters.value()) {
        if (isApplicable(xmlJavaTypeAdapter1, paramT))
          return xmlJavaTypeAdapter1; 
      }  
    xmlJavaTypeAdapter = (XmlJavaTypeAdapter)reader().getPackageAnnotation(XmlJavaTypeAdapter.class, this.parent.clazz, this.seed);
    if (isApplicable(xmlJavaTypeAdapter, paramT))
      return xmlJavaTypeAdapter; 
    Object object = nav().asDecl(paramT);
    if (object != null) {
      xmlJavaTypeAdapter = (XmlJavaTypeAdapter)reader().getClassAnnotation(XmlJavaTypeAdapter.class, object, this.seed);
      if (xmlJavaTypeAdapter != null && isApplicable(xmlJavaTypeAdapter, paramT))
        return xmlJavaTypeAdapter; 
    } 
    return null;
  }
  
  public Adapter<T, C> getAdapter() { return this.adapter; }
  
  public final String displayName() { return nav().getClassName(this.parent.getClazz()) + '#' + getName(); }
  
  public final ID id() { return this.id; }
  
  private ID calcId() {
    if (this.seed.hasAnnotation(javax.xml.bind.annotation.XmlID.class)) {
      if (!nav().isSameType(getIndividualType(), nav().ref(String.class)))
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.ID_MUST_BE_STRING.format(new Object[] { getName() }, ), this.seed)); 
      return ID.ID;
    } 
    return this.seed.hasAnnotation(javax.xml.bind.annotation.XmlIDREF.class) ? ID.IDREF : ID.NONE;
  }
  
  public final MimeType getExpectedMimeType() { return this.expectedMimeType; }
  
  public final boolean inlineBinaryData() { return this.inlineBinary; }
  
  public final QName getSchemaType() { return this.schemaType; }
  
  public final boolean isCollection() { return this.isCollection; }
  
  protected void link() {
    if (this.id == ID.IDREF)
      for (TypeInfo typeInfo : ref()) {
        if (!typeInfo.canBeReferencedByIDREF())
          this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_IDREF.format(new Object[] { this.parent.builder.nav.getTypeName(typeInfo.getType()) }, ), this)); 
      }  
  }
  
  public Locatable getUpstream() { return this.parent; }
  
  public Location getLocation() { return this.seed.getLocation(); }
  
  protected final QName calcXmlName(XmlElement paramXmlElement) { return (paramXmlElement != null) ? calcXmlName(paramXmlElement.namespace(), paramXmlElement.name()) : calcXmlName("##default", "##default"); }
  
  protected final QName calcXmlName(XmlElementWrapper paramXmlElementWrapper) { return (paramXmlElementWrapper != null) ? calcXmlName(paramXmlElementWrapper.namespace(), paramXmlElementWrapper.name()) : calcXmlName("##default", "##default"); }
  
  private QName calcXmlName(String paramString1, String paramString2) {
    TODO.checkSpec();
    if (paramString2.length() == 0 || paramString2.equals("##default"))
      paramString2 = this.seed.getName(); 
    if (paramString1.equals("##default")) {
      XmlSchema xmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
      if (xmlSchema != null) {
        QName qName;
        switch (xmlSchema.elementFormDefault()) {
          case QUALIFIED:
            qName = this.parent.getTypeName();
            if (qName != null) {
              paramString1 = qName.getNamespaceURI();
            } else {
              paramString1 = xmlSchema.namespace();
            } 
            if (paramString1.length() == 0)
              paramString1 = this.parent.builder.defaultNsUri; 
            break;
          case UNQUALIFIED:
          case UNSET:
            paramString1 = "";
            break;
        } 
      } else {
        paramString1 = "";
      } 
    } 
    return new QName(paramString1.intern(), paramString2.intern());
  }
  
  public int compareTo(PropertyInfoImpl paramPropertyInfoImpl) { return getName().compareTo(paramPropertyInfoImpl.getName()); }
  
  public final <A extends Annotation> A readAnnotation(Class<A> paramClass) { return (A)this.seed.readAnnotation(paramClass); }
  
  public final boolean hasAnnotation(Class<? extends Annotation> paramClass) { return this.seed.hasAnnotation(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\PropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */