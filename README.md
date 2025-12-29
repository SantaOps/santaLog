# santaLog _ SantaOps Blog Platform

## 📝 Commit Message Convention

프로젝트의 커밋 메시지는 아래 규칙을 따릅니다.


| Type | Description |
|------|------------|
| **feat** | 새로운 기능 추가, 기존 기능을 요구 사항에 맞추어 수정 |
| **fix** | 기능에 대한 버그 수정 |
| **build** | 빌드 관련 수정 / 모듈 설치 또는 삭제 |
| **chore** | 패키지 매니저 수정, 기타 설정 변경<br/>(예: `.gitignore`) |
| **ci** | CI/CD 관련 설정 수정 |
| **docs** | 문서 및 주석 수정 |
| **style** | 코드 스타일, 포맷팅 수정<br/>(기능 변화 없음) |
| **refactor** | 기능 변경 없는 코드 리팩터링<br/>(예: 변수명 변경, 구조 개선) |
| **test** | 테스트 코드 추가 또는 수정 |
| **release** | 버전 릴리즈 관련 커밋 |

---

### 🧾 Commit Message Format

커밋 메시지는 아래 형식을 따릅니다.  
**type: 이슈사항 #이슈번호**

#### 📌 Rules
- `type`은 사전에 정의된 커밋 타입을 사용합니다.
- 이슈 번호는 GitHub Issue 번호를 기준으로 작성합니다.
- 하나의 커밋은 하나의 이슈 해결을 원칙으로 합니다.


#### ✅ Examples
> feat: 사용자 로그인 기능 추가 #12  
> fix: 회원가입 시 비밀번호 검증 오류 수정 #3  
> docs: README에 프로젝트 실행 방법 추가 #5  
> refactor: 중복 로직 함수로 분리 #18

---


## 🌱 Branch Convention
브랜치 네이밍은 아래 형식을 따릅니다.  
**type/#이슈번호-간단한-설명**

### 🌿 Branch Types
| 타입 | 설명 |
|---|---|
| main | 운영 브랜치 |
| develop | 개발 통합 브랜치 |
| feat | 기능 개발 |
| fix | 버그 수정 |
| docs | 문서 작업 |
| refactor | 리팩터링 |
| hotfix | 운영 긴급 수정 |

### ✅ Examples
> feat/#12-login  
> fix/#3-password-validation  
> docs/#5-readme-update  
> refactor/#18-service-cleanup  

