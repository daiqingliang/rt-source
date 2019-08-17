package sun.util.locale.provider;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.text.spi.CollatorProvider;
import java.util.Locale;
import java.util.Set;

public class CollatorProviderImpl extends CollatorProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public CollatorProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
  
  public Collator getInstance(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    RuleBasedCollator ruleBasedCollator = null;
    String str = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getCollationData();
    try {
      ruleBasedCollator = new RuleBasedCollator("='​'=‌=‍=‎=‏=\000 =\001 =\002 =\003 =\004=\005 =\006 =\007 =\b ='\t'='\013' =\016=\017 ='\020' =\021 =\022 =\023=\024 =\025 =\026 =\027 =\030=\031 =\032 =\033 =\034 =\035=\036 =\037 == = = = = == = = = = == = = = = == = = = = == = = = = == =;' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';'　';'﻿';'\r' ;'\t' ;'\n';'\f';'\013';́;̀;̆;̂;̌;̊;̍;̈;̋;̃;̇;̄;̷;̧;̨;̣;̲;̅;̉;̎;̏;̐;̑;̒;̓;̔;̕;̖;̗;̘;̙;̚;̛;̜;̝;̞;̟;̠;̡;̢;̤;̥;̦;̩;̪;̫;̬;̭;̮;̯;̰;̱;̳;̴;̵;̶;̸;̹;̺;̻;̼;̽;̾;̿;͂;̈́;ͅ;͠;͡;҃;҄;҅;҆;⃐;⃑;⃒;⃓;⃔;⃕;⃖;⃗;⃘;⃙;⃚;⃛;⃜;⃝;⃞;⃟;⃠;⃡,'-';­;‐;‑;‒;–;—;―;−<'_'<¯<','<';'<':'<'!'<¡<'?'<¿<'/'<'.'<´<'`'<'^'<¨<'~'<·<¸<'''<'\"'<«<»<'('<')'<'['<']'<'{'<'}'<§<¶<©<®<'@'<¤<฿<¢<₡<₢<'$'<₫<€<₣<₤<₥<₦<₧<£<₨<₪<₩<¥<'*'<'\\'<'&'<'#'<'%'<'+'<±<÷<×<'<'<'='<'>'<¬<'|'<¦<°<µ<0<1<2<3<4<5<6<7<8<9<¼<½<¾<a,A<b,B<c,C<d,D<ð,Ð<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,ß<t,T& TH, Þ &TH, þ <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,Æ&AE,æ&OE,Œ&OE,œ" + str);
    } catch (ParseException parseException) {
      try {
        ruleBasedCollator = new RuleBasedCollator("='​'=‌=‍=‎=‏=\000 =\001 =\002 =\003 =\004=\005 =\006 =\007 =\b ='\t'='\013' =\016=\017 ='\020' =\021 =\022 =\023=\024 =\025 =\026 =\027 =\030=\031 =\032 =\033 =\034 =\035=\036 =\037 == = = = = == = = = = == = = = = == = = = = == = = = = == =;' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';'　';'﻿';'\r' ;'\t' ;'\n';'\f';'\013';́;̀;̆;̂;̌;̊;̍;̈;̋;̃;̇;̄;̷;̧;̨;̣;̲;̅;̉;̎;̏;̐;̑;̒;̓;̔;̕;̖;̗;̘;̙;̚;̛;̜;̝;̞;̟;̠;̡;̢;̤;̥;̦;̩;̪;̫;̬;̭;̮;̯;̰;̱;̳;̴;̵;̶;̸;̹;̺;̻;̼;̽;̾;̿;͂;̈́;ͅ;͠;͡;҃;҄;҅;҆;⃐;⃑;⃒;⃓;⃔;⃕;⃖;⃗;⃘;⃙;⃚;⃛;⃜;⃝;⃞;⃟;⃠;⃡,'-';­;‐;‑;‒;–;—;―;−<'_'<¯<','<';'<':'<'!'<¡<'?'<¿<'/'<'.'<´<'`'<'^'<¨<'~'<·<¸<'''<'\"'<«<»<'('<')'<'['<']'<'{'<'}'<§<¶<©<®<'@'<¤<฿<¢<₡<₢<'$'<₫<€<₣<₤<₥<₦<₧<£<₨<₪<₩<¥<'*'<'\\'<'&'<'#'<'%'<'+'<±<÷<×<'<'<'='<'>'<¬<'|'<¦<°<µ<0<1<2<3<4<5<6<7<8<9<¼<½<¾<a,A<b,B<c,C<d,D<ð,Ð<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,ß<t,T& TH, Þ &TH, þ <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,Æ&AE,æ&OE,Œ&OE,œ");
      } catch (ParseException parseException1) {
        throw new InternalError(parseException1);
      } 
    } 
    ruleBasedCollator.setDecomposition(0);
    return (Collator)ruleBasedCollator.clone();
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\CollatorProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */