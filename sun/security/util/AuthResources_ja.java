package sun.security.util;

import java.util.ListResourceBundle;

public class AuthResources_ja extends ListResourceBundle {
  private static final Object[][] contents = { 
      { "invalid.null.input.value", "無効なnullの入力: {0}" }, { "NTDomainPrincipal.name", "NTDomainPrincipal: {0}" }, { "NTNumericCredential.name", "NTNumericCredential: {0}" }, { "Invalid.NTSid.value", "無効なNTSid値" }, { "NTSid.name", "NTSid: {0}" }, { "NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}" }, { "NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}" }, { "NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}" }, { "NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}" }, { "NTUserPrincipal.name", "NTUserPrincipal: {0}" }, 
      { "UnixNumericGroupPrincipal.Primary.Group.name", "UnixNumericGroupPrincipal [主グループ]: {0}" }, { "UnixNumericGroupPrincipal.Supplementary.Group.name", "UnixNumericGroupPrincipal [補助グループ]: {0}" }, { "UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}" }, { "UnixPrincipal.name", "UnixPrincipal: {0}" }, { "Unable.to.properly.expand.config", "{0}を正しく展開できません" }, { "extra.config.No.such.file.or.directory.", "{0}(指定されたファイルまたはディレクトリは存在しません)" }, { "Configuration.Error.No.such.file.or.directory", "構成エラー:\n\t指定されたファイルまたはディレクトリは存在しません" }, { "Configuration.Error.Invalid.control.flag.flag", "構成エラー:\n\t無効な制御フラグ: {0}" }, { "Configuration.Error.Can.not.specify.multiple.entries.for.appName", "構成エラー:\n\t{0}に複数のエントリを指定できません" }, { "Configuration.Error.expected.expect.read.end.of.file.", "構成エラー:\n\t[{0}]ではなく、[ファイルの終わり]が読み込まれました" }, 
      { "Configuration.Error.Line.line.expected.expect.found.value.", "構成エラー:\n\t行{0}: [{1}]ではなく、[{2}]が検出されました" }, { "Configuration.Error.Line.line.expected.expect.", "構成エラー:\n\t行{0}: [{1}]が要求されました" }, { "Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", "構成エラー:\n\t行{0}: システム・プロパティ[{1}]が空の値に展開されました" }, { "username.", "ユーザー名: " }, { "password.", "パスワード: " }, { "Please.enter.keystore.information", "キーストア情報を入力してください" }, { "Keystore.alias.", "キーストアの別名: " }, { "Keystore.password.", "キーストアのパスワード: " }, { "Private.key.password.optional.", "秘密鍵のパスワード(オプション): " }, { "Kerberos.username.defUsername.", "Kerberosユーザー名[{0}]: " }, 
      { "Kerberos.password.for.username.", "{0}のKerberosパスワード: " }, { ".error.parsing.", ": 解析エラー " }, { "COLON", ": " }, { ".error.adding.Permission.", ": アクセス権の追加エラー " }, { "SPACE", " " }, { ".error.adding.Entry.", ": エントリの追加エラー " }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "attempt.to.add.a.Permission.to.a.readonly.PermissionCollection", "読取り専用のPermissionCollectionにアクセス権の追加が試行されました" }, { "expected.keystore.type", "予想されたキーストア・タイプ" }, 
      { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "ワイルドカード名のないワイルドカード・クラスを使用して、プリンシパルを指定することはできません" }, { "expected.codeBase.or.SignedBy", "予想されたcodeBaseまたはSignedBy" }, { "only.Principal.based.grant.entries.permitted", "プリンシパル・ベースのエントリのみが許可されます。" }, { "expected.permission.entry", "予想されたアクセス権エントリ" }, { "number.", "数 " }, { "expected.expect.read.end.of.file.", "{0}ではなくファイルの終わりが読み込まれました" }, { "expected.read.end.of.file", "予想値は';'ですが、ファイルの終わりが読み込まれました" }, { "line.", "行番号 " }, { ".expected.", ": 予想値'" }, { ".found.", "',検出値'" }, 
      { "QUOTE", "'" }, { "SolarisNumericGroupPrincipal.Primary.Group.", "SolarisNumericGroupPrincipal [主グループ]: " }, { "SolarisNumericGroupPrincipal.Supplementary.Group.", "SolarisNumericGroupPrincipal [補助グループ]: " }, { "SolarisNumericUserPrincipal.", "SolarisNumericUserPrincipal: " }, { "SolarisPrincipal.", "SolarisPrincipal: " }, { "provided.null.name", "nullの名前が指定されました" } };
  
  public Object[][] getContents() { return contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\AuthResources_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */