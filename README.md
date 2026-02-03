# santaLog _ SantaOps Blog Platform

![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0001](https://github.com/user-attachments/assets/57bdc3c8-6377-4158-ad8e-570550e01a8d)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0003](https://github.com/user-attachments/assets/3e0bf693-8596-4740-8878-c209dbec7e5a)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0007](https://github.com/user-attachments/assets/1e64f8bb-cb3b-4bf6-b328-d6ef43a62dca)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0023](https://github.com/user-attachments/assets/8d6bee98-e7a8-4697-b2c2-371941e949cc)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0024](https://github.com/user-attachments/assets/486223fa-7a17-4251-a7ce-3e3257e2e939)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0030](https://github.com/user-attachments/assets/453adb14-cf2b-468c-aa5f-55e2e00d90fc)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0033](https://github.com/user-attachments/assets/cf4a602e-9881-489c-8144-509fa7d714af)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0046](https://github.com/user-attachments/assets/678f3f1c-9548-4d19-944d-3987b5d1ea1d)
![santaOps (문상희, 공채연, 김효연, 류한나)_final_v8 0_(0130,수정)2_page-0047](https://github.com/user-attachments/assets/f629380f-ffbd-4920-8bdb-6a4dc18222e4)



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

