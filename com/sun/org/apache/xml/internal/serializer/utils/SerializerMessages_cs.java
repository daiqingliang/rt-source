package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_cs extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "ER_SERIALIZER_NOT_CONTENTHANDLER", "Třída serializace ''{0}'' neimplementuje org.xml.sax.ContentHandler." }, { "ER_RESOURCE_COULD_NOT_FIND", "Nelze najít zdroj [ {0} ].\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "Nelze zavést zdroj [ {0} ]: {1} \n {2} \n {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "Velikost vyrovnávací paměti <=0" }, { "ER_INVALID_UTF16_SURROGATE", "Byla zjištěna neplatná náhrada UTF-16: {0} ?" }, { "ER_OIERROR", "Chyba vstupu/výstupu" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "Nelze přidat atribut {0} po uzlech potomků ani před tím, než je vytvořen prvek. Atribut bude ignorován." }, { "ER_NAMESPACE_PREFIX", "Obor názvů pro předponu ''{0}'' nebyl deklarován." }, { "ER_STRAY_NAMESPACE", "Deklarace oboru názvů ''{0}''=''{1}'' je vně prvku." }, { "ER_COULD_NOT_LOAD_RESOURCE", "Nelze zavést ''{0}'' (zkontrolujte proměnnou CLASSPATH), proto se používají pouze výchozí hodnoty" }, 
        { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "Nelze načíst soubor vlastností ''{0}'' pro výstupní metodu ''{1}'' (zkontrolujte proměnnou CLASSPATH)." }, { "ER_INVALID_PORT", "Neplatné číslo portu." }, { "ER_PORT_WHEN_HOST_NULL", "Má-li hostitel hodnotu null, nelze nastavit port." }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "Adresa hostitele má nesprávný formát." }, { "ER_SCHEME_NOT_CONFORMANT", "Schéma nevyhovuje." }, { "ER_SCHEME_FROM_NULL_STRING", "Nelze nastavit schéma řetězce s hodnotou null." }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "Cesta obsahuje neplatnou escape sekvenci" }, { "ER_PATH_INVALID_CHAR", "Cesta obsahuje neplatný znak: {0}" }, { "ER_FRAG_INVALID_CHAR", "Fragment obsahuje neplatný znak." }, { "ER_FRAG_WHEN_PATH_NULL", "Má-li cesta hodnotu null, nelze nastavit fragment." }, 
        { "ER_FRAG_FOR_GENERIC_URI", "Fragment lze nastavit jen u generického URI." }, { "ER_NO_SCHEME_IN_URI", "V URI nebylo nalezeno žádné schéma: {0}" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "URI nelze inicializovat s prázdnými parametry." }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "Fragment nelze určit zároveň v cestě i ve fragmentu." }, { "ER_NO_QUERY_STRING_IN_PATH", "V řetězci cesty a dotazu nelze zadat řetězec dotazu." }, { "ER_NO_PORT_IF_NO_HOST", "Není-li určen hostitel, nelze zadat port." }, { "ER_NO_USERINFO_IF_NO_HOST", "Není-li určen hostitel, nelze zadat údaje o uživateli." }, { "ER_SCHEME_REQUIRED", "Je vyžadováno schéma!" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\\utils\SerializerMessages_cs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */