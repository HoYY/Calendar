# programmers 과제 테스트 템플릿 - Spring Boot

programmers의 과제 테스트는 코드를 제출하면 자동으로 서버를 구성하여 실행해 볼 수 있는 환경을 제공합니다. 

아래 내용을 확인하여 프로젝트를 구성하고 코드를 제출해 주세요.

programmers의 과제 테스트는 Spring Boot의 프로젝트 관리 도구로 Maven과 Gradle을 지원합니다. 각 관리 도구에 맞는 주의사항을 확인하세요.

> 참고: 본 템플릿은 [Gradle Build Tool](https://gradle.org/) 로 사전 구성이 되어 있으며, 데이터베이스 마이그레이션을 위한 [flyway](https://flywaydb.org/) plugin 이 `build.gradle` 에 포함되어 있습니다.



## 실행 및 제출 시 주의사항

- Port 번호는 **PORT 환경 변수** 또는 **5000** 을 사용해야 합니다.

- **Maven인 경우**

  - application에 종속성이 있는 경우 `pom.xml` 파일에는 `maven-dependency-plugin`이 포함되어야합니다. 

    이 플러그인은 Maven에 application이 의존하는 jar 파일을 대상과 종속성 디렉토리에 복사하도록 합니다.

    pom.xml 파일의 예)

    ```xml 
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.example</groupId>
        <version>1.0-SNAPSHOT</version>
        <artifactId>helloworld</artifactId>
        <dependencies>
            ...
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-dependency-plugin</artifactId>
                    <version>3.0.1</version>
                    <executions>
                        <execution>
                            <id>copy-dependencies</id>
                            <phase>package</phase>
                            <goals><goal>copy-dependencies</goal></goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </project>
    ```

  - `Procfile` 파일이  프로젝트의 root 디렉토리에 있어야 합니다. `Procfile`은 application의 프로세스 유형과 진입점을 명시적으로 선언한 파일이며 실행을 위해 사용됩니다. 

    다음은 `Pipfile` 의 예시 입니다.  `web`에 프로젝트의 진입 명령어를 명시해주어야 합니다.

    ```
    web: java $JAVA_OPTS -cp target/classes:target/dependency/* HelloWorld
    ```

- **Gradle인 경우**

  - `build.gradle` 파일, `settings.gradle` 파일, `gradlew` 파일이 프로젝트 루트에 있어야 합니다.

    `gradlew`를 사용하는 경우 `gradle/wrapper/gradle-wrapper.jar` 및 `gradle/wrapper/gradle-wrapper.properties` 파일도 Git 저장소에 추가해야 하며, `.gitignore` 파일에 명시하면 안됩니다. 이 파일들을 추가하지 않으면 다음과 같은 오류가 발생합니다.

    ```
    -----> executing ./gradlew stage
           Error: Could not find or load main class org.gradle.wrapper.GradleWrapperMain
    ```

    파일이 올바른 경우 다음의 명령어를 통해 application 프로세스가 실행됩니다.

    ```
    > java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
    ```

  - `Procfile` 파일이  프로젝트의 root 디렉토리에 있어야 합니다. `Procfile`은 application의 프로세스 유형과 진입점을 명시적으로 선언한 파일이며 실행을 위해 사용됩니다. 

    다음은 `Pipfile` 의 예시 입니다.  `web`에 프로젝트의 진입 명령어를 명시해주어야 합니다.

    ```
    web: java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
    ```


- programmers에서 과제 실행 및 제출 시에는

  1. **본인의 github 계정**의 **private** 저장소를 생성해서 과제를 **master** 브랜치에 push 해주세요.

     > **주의!**: 저장소가 public으로 되어 있는 경우에는 programmers에서 과제를 실행/제출할 수 없습니다.

  2. github 저장소 페이지의 **Setting > Collaborators** 에서 [programmers-gitbot](https://github.com/programmers-gitbot) 을 Collaborator로 추가 해주세요.

  3. 실행시 github 저장소의 url을 입력하시고 **[빌드(build)]** 버튼을 누르면 자동으로 서버를 구성하여 실행 합니다. 실행 후에는 **[최종 제출]** 버튼이 활성화되어 과제를 제출 할 수 있습니다.

  4. **[최종 제출] 하면 테스트가 종료되며, 이후 다시 제출이 불가능하니 충분히 실행을 해보시고 제출하시기 바랍니다!!**

     > **주의!**: [최종 제출] 되면 제출 당시의 master 브랜치의 commit으로 제출이 고정되므로 제출 이후 저장소에 commit을 push 해도 제출물에는 반영되지 않습니다.

- programmers에서 실행된 서버는 최대 10분 뒤에는 자동으로 종료됩니다.



## 데이터베이스 관련 주의사항

- 데이터베이스는 **MySQL**만 지원하며, 다른 데이터베이스 엔진은 지원하지 않습니다.

- 데이터베이스와 관련되어 정의되어 있는 환경 변수들은 다음과 같습니다.

  - MySQL 접속 host: `MYSQL_ROOT_HOST=mysql-server`
  - MySQL 유저: `MYSQL_USER=root`
  - MySQL 유저의 비밀번호: `MYSQL_ROOT_PASSWORD=password`
  - 사전에 생성된 데이터베이스: `MYSQL_DATABASE=my_database`

  > 참고: 이 템플릿의 `application.properties` 와 `build.gradle` 에 정의되어 있는 값들을 참고해주세요.

- programmers에서 과제를 자동으로 실행할 때에는 마이그레이션을 위해 서버 실행 전 Procfile 파일의 **migrate**에 정의되어 있는 명령어를 실행합니다.

  > **주의!**: 이 템플릿에는 Procfile 파일의 migrate로 아래 내용이 작성되어 있습니다.
  >
  > ```
  > migrate: ./gradlew flywayMigrate
  > ```
  >
  > flyway 외의 다른 모듈을 사용할 경우 Procfile 파일의 migrate을 사용 모듈의 마이그레이션 cli 명령어로 수정해주어야 합니다.

- 데이터베이스를 위한 모듈로 flyway 을 사용할 경우, `src/main/resources/db/migration` 디렉토리에 마이그레이션 파일을 `V(버전명)__(설명).sql` 형식으로 작성해주어야 합니다.

  > 참고: Users 테이블을 만드는 마이그레이션 파일이 이 템플릿의 `V2__create-user-table.sql` 에 포함되어 있습니다. 이를 참고해주세요.

- programmers에서 자동으로 실행된 서버는 종료되면 데이터베이스 내의 데이터는 삭제됩니다.



## 에러 로그 발생 시 참고사항

- git fetch failed! please check your repository.

  > 잘못된 저장소 주소를 입력하거나 force commit으로 해당 commit이 없어진 경우 발생합니다.

- -----> Unable to select a buildpack

  > 저장소 root 디렉토리에 `pom.xml` 혹은 `build.gradle` 파일을 찾을 수 없습니다. 각 패키지 관리 툴에 맞는 파일들을 확인해주세요.

- ERROR: connect ECONNREFUSED 127.0.0.1:3306

  > MySQL host를 찾을 수 없습니다. MySQL host는 *MYSQL_ROOT_HOST* 환경변수 혹은 "mysql-server" 이여야 합니다.

  > **주의!**: programmers에서 과제 실행 시 MySQL host는 127.0.0.1(localhost)가 아니라 *mysql-server*임을 유의해주세요.

- ERROR: Access denied for user 'root'@'172.17.0.3' (using password: YES)

  > MySQL 유저 혹은 비밀번호를 잘못 입력했습니다. 유저는 *MYSQL_USER* 환경 변수 혹은 "root", 비밀번호는 *MYSQL_ROOT_PASSWORD* 환경 변수 혹은 "password" 이여야 합니다.

  > **주의!**: 이 로그에서 나오는 ip 주소를 고정으로 사용하지 마세요. MySQL host의 ip 주소는 동적으로 변할 수 있습니다.

- ERROR: Unknown database

  > database를 찾을 수 없습니다. database는 *MYSQL_DATABASE* 환경 변수 혹은 "my_database" 이여야 합니다.

- Proc entrypoint migrate does not exist. Please check your Procfile

  > 저장소 root 디렉토리에 Procfile 파일을 찾을 수 없거나 Procfile의 migrate가 정의되어 있지 않은 경우 발생합니다. 

- 페이지가 작동하지 않습니다.

  > 서버 실행에 실패했거나 서버가 실행된지 10분이 지나 종료되었습니다. Procfile의 web을 확인하여 서버 실행의 이상이 없는지 확인하세요. 이상이 없으면 [초기화] 후 다시 실행하세요.



## programmers 과제 실행 시스템 및 프레임워크 환경

programmers에서 실행된 과제는 다음 환경에서 동작합니다.

> OS 버전: ubuntu 16.04
>
> 메모리 제한: 1024 MiB
>
> MySQL 버전: 8.0.17
>
> JDK 버전: 1.8.0_201
>
> maven 버전: 3.3.9
>
> gradle 버전: 5.2.1

