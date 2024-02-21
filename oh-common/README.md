# 서버 공통 모듈

## - 개요
- 웹 어플리케이션 개발시 사용하는 서버 공통 모듈

## - 기능
- JPA (Hibernate)
    - JPA CRUD와 페이징/정렬 처리를 위한 기본 Controller/Service/Repository/Model 객체
        - AbstractCrudDbController, AbstractCrudDbService, CrudDbRepository, Model
    - 데이터 출력을 제어하기 위한 인터페이스 : CommonModel
- IMDG (Hazelcast)
    - In-Memory 데이터 처리를 위한 기본 Controller/Service/Repository/Model 객체
        - AbstractCrudDgController, AbstractCrudDgService, SessionDgRepository
    - Memory Data/Session/Cache/file 이중화 구성
- Mapper (Mybatis)
    - Mapper CRUD와 페이징/정렬 처리를 위한 기본 Controller/Service/Mapper/Model 객체
        - AbstractCrudMapperController, AbstractCrudMapperService, CrudMapper
    - /mapper/down API, CLI 도구(gradlew -S mapper) 제공 

- 기본 API
    - Login(Token) API : CommonRestController
    - Data(IMDG) API : DataRestController
- API Token
    - 서버에 데이터 요청시 JWT(RFC 7519)라는 웹 표준 보안을 사용
        - SecurityConfig, PropertyUserDetailsService
- API Exception Handling
    - API Exception에 대한 통합 처리
        - ControllerExceptionHandler, DefaultErrorController, error.html
- API Doc
    - Swagger를 사용하기 위한 객체 : SpringDocConfig

- Sync/Async(Multi Thread) HttpClient
    - HTTP 통신을 사용 하기 위한 Client : HttpClientConfig, HttpClient
- Mail Client
    - SMTP 통신을 사용 하기 위한 Client : MailService, mail.html

- Cache
    - Cache를 사용하기 위한 기본 annotations : Cache*
- Schedule (IMDG/DB)
    - 스케쥴러 이중화 (Active(1):Standby(N) 구성) : AbstractScheduleService
- AOP
    - 메소드에 대한 로그 출력을 위한 annotations : ResultLogging
    - 메소드에 대한 에러 제어를 위한 annotations : ExceptionHandling

- Converter (org.oh.common.converter)
    - DB 데이터 변환을 위한 기본 converter
    - JSON 데이터 변환을 위한 기본 converter
- Exception (org.oh.common.exception)
    - CommonException, HttpException
- Utils (org.oh.common.util)
    - JsonUtil, SpringUtil, ImageUtil, ...

- Sample (org.oh.sample)
    - JPA/IMDG/Mapper CRUD와 페이징/정렬 처리를 위한 sample 코드 
- Test (src/test)
    - Controller/Service classes에 대한 단위 테스트
    - @TestController, @TestService
