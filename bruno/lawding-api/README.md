# LawDing Bruno Collection

## 열기

Bruno에서 `Open Collection`을 선택하고 이 `lawding-api` 폴더를 엽니다. 컬렉션이 현재 Git 저장소 안에 있으므로 `.bru` 파일을 수정하면 일반 소스 코드와 동일하게 Git 변경사항으로 표시됩니다.

## 환경 선택

- `local`: `http://localhost:8080/v1`
- `production`: `https://api.lawding.net/v1`

각 환경에는 공통 헤더용 `platform=android`가 들어 있습니다. 필요하면 `web`, `ios`, `android` 중 하나로 변경합니다.

## JWT 설정

1. `.env.example`을 복사해 같은 폴더에 `.env`를 만듭니다.
2. `LAWDING_ACCESS_TOKEN`에 실제 액세스 토큰을 입력합니다.
3. `.env`는 `.gitignore`에 포함되어 Git에 올라가지 않습니다.

```dotenv
LAWDING_ACCESS_TOKEN=실제_JWT
```

환경 파일은 `{{process.env.LAWDING_ACCESS_TOKEN}}`을 통해 토큰을 읽습니다.

## Git 사용

이 컬렉션만 별도 Git 저장소로 초기화하지 마세요. 상위 `AnnualLeaveCalculator-BE` 저장소가 이미 Git과 연결되어 있으므로 Bruno에서 변경한 `.bru` 파일을 백엔드 변경과 함께 커밋하면 됩니다.
