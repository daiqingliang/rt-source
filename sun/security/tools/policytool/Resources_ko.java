package sun.security.tools.policytool;

import java.util.ListResourceBundle;

public class Resources_ko extends ListResourceBundle {
  private static final Object[][] contents = { 
      { "NEWLINE", "\n" }, { "Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.", "경고: {0} 별칭에 대한 공용 키가 존재하지 않습니다. 키 저장소가 제대로 구성되어 있는지 확인하십시오." }, { "Warning.Class.not.found.class", "경고: 클래스를 찾을 수 없음: {0}" }, { "Warning.Invalid.argument.s.for.constructor.arg", "경고: 생성자에 대해 부적합한 인수: {0}" }, { "Illegal.Principal.Type.type", "잘못된 주체 유형: {0}" }, { "Illegal.option.option", "잘못된 옵션: {0}" }, { "Usage.policytool.options.", "사용법: policytool [options]" }, { ".file.file.policy.file.location", "  [-file <file>]    정책 파일 위치" }, { "New", "새로 만들기(&N)" }, { "Open", "열기(&O)..." }, 
      { "Save", "저장(&S)" }, { "Save.As", "다른 이름으로 저장(&A)..." }, { "View.Warning.Log", "경고 로그 보기(&W)" }, { "Exit", "종료(&X)" }, { "Add.Policy.Entry", "정책 항목 추가(&A)" }, { "Edit.Policy.Entry", "정책 항목 편집(&E)" }, { "Remove.Policy.Entry", "정책 항목 제거(&R)" }, { "Edit", "편집(&E)" }, { "Retain", "유지" }, { "Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes", "경고: 파일 이름에 이스케이프된 백슬래시 문자가 포함되었을 수 있습니다. 백슬래시 문자는 이스케이프할 필요가 없습니다. 영구 저장소에 정책 콘텐츠를 쓸 때 필요에 따라 자동으로 문자가 이스케이프됩니다.\n\n입력된 이름을 그대로 유지하려면 [유지]를 누르고, 이름을 편집하려면 [편집]을 누르십시오." }, 
      { "Add.Public.Key.Alias", "공용 키 별칭 추가" }, { "Remove.Public.Key.Alias", "공용 키 별칭 제거" }, { "File", "파일(&F)" }, { "KeyStore", "키 저장소(&K)" }, { "Policy.File.", "정책 파일:" }, { "Could.not.open.policy.file.policyFile.e.toString.", "정책 파일을 열 수 없음: {0}: {1}" }, { "Policy.Tool", "정책 툴" }, { "Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.", "정책 구성을 여는 중 오류가 발생했습니다. 자세한 내용은 경고 로그를 확인하십시오." }, { "Error", "오류" }, { "OK", "확인" }, 
      { "Status", "상태" }, { "Warning", "경고" }, { "Permission.", "권한:                                                       " }, { "Principal.Type.", "주체 유형:" }, { "Principal.Name.", "주체 이름:" }, { "Target.Name.", "대상 이름:                                                    " }, { "Actions.", "작업:                                                             " }, { "OK.to.overwrite.existing.file.filename.", "기존 파일 {0}을(를) 겹쳐 쓰겠습니까?" }, { "Cancel", "취소" }, { "CodeBase.", "CodeBase(&C)" }, 
      { "SignedBy.", "SignedBy(&S):" }, { "Add.Principal", "주체 추가(&A)" }, { "Edit.Principal", "주체 편집(&E)" }, { "Remove.Principal", "주체 제거(&R)" }, { "Principals.", "주체(&P):" }, { ".Add.Permission", "  권한 추가(&D)" }, { ".Edit.Permission", "  권한 편집(&I)" }, { "Remove.Permission", "권한 제거(&M)" }, { "Done", "완료" }, { "KeyStore.URL.", "키 저장소 URL(&U):" }, 
      { "KeyStore.Type.", "키 저장소 유형(&T):" }, { "KeyStore.Provider.", "키 저장소 제공자(&P):" }, { "KeyStore.Password.URL.", "키 저장소 비밀번호 URL(&W):" }, { "Principals", "주체" }, { ".Edit.Principal.", "  주체 편집:" }, { ".Add.New.Principal.", "  새 주체 추가:" }, { "Permissions", "권한" }, { ".Edit.Permission.", "  권한 편집:" }, { ".Add.New.Permission.", "  새 권한 추가:" }, { "Signed.By.", "서명자:" }, 
      { "Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name", "와일드 카드 문자 이름 없이 와일드 카드 문자 클래스를 사용하는 주체를 지정할 수 없습니다." }, { "Cannot.Specify.Principal.without.a.Name", "이름 없이 주체를 지정할 수 없습니다." }, { "Permission.and.Target.Name.must.have.a.value", "권한과 대상 이름의 값이 있어야 합니다." }, { "Remove.this.Policy.Entry.", "이 정책 항목을 제거하겠습니까?" }, { "Overwrite.File", "파일 겹쳐쓰기" }, { "Policy.successfully.written.to.filename", "{0}에 성공적으로 정책을 썼습니다." }, { "null.filename", "널 파일 이름" }, { "Save.changes.", "변경사항을 저장하겠습니까?" }, { "Yes", "예(&Y)" }, { "No", "아니오(&N)" }, 
      { "Policy.Entry", "정책 항목" }, { "Save.Changes", "변경사항 저장" }, { "No.Policy.Entry.selected", "선택된 정책 항목이 없습니다." }, { "Unable.to.open.KeyStore.ex.toString.", "키 저장소를 열 수 없음: {0}" }, { "No.principal.selected", "선택된 주체가 없습니다." }, { "No.permission.selected", "선택된 권한이 없습니다." }, { "name", "이름" }, { "configuration.type", "구성 유형" }, { "environment.variable.name", "환경 변수 이름" }, { "library.name", "라이브러리 이름" }, 
      { "package.name", "패키지 이름" }, { "policy.type", "정책 유형" }, { "property.name", "속성 이름" }, { "provider.name", "제공자 이름" }, { "url", "URL" }, { "method.list", "메소드 목록" }, { "request.headers.list", "요청 헤더 목록" }, { "Principal.List", "주체 목록" }, { "Permission.List", "권한 목록" }, { "Code.Base", "코드 베이스" }, 
      { "KeyStore.U.R.L.", "키 저장소 URL:" }, { "KeyStore.Password.U.R.L.", "키 저장소 비밀번호 URL:" } };
  
  public Object[][] getContents() { return contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\Resources_ko.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */