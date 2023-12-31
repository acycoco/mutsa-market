# MiniProject_Basic_AhnChaeYeon
# ♻️멋사마켓♻️
> 멋쟁이사자처럼 백엔드 스쿨 5기 미니프로젝트     
> 🥕당근마켓, 중고나라 등을 착안하여 중고 제품 거래 플랫폼을 만들어보는 미니 프로젝트입니다.

## 💻프로젝트 소개
> 사용자가 물품을 등록하고, 댓글을 통해 소통을 하며, 최종적으로 구매 제안에 대해 수락을 해서 거래가 이루어지는 중고거래 플랫폼입니다.

## 🗓️ 프로젝트 기간

 **2023. 06.29 ~ 2023. 07. 04**<br>
 **2023. 07.26 ~ 2023. 08. 02**<br>
 

## ⚙️ 개발환경
- Spring Boot 3.1.1
- java 17
- Intellij
- SQLite
- gradle 8.1.1
- H2

## ERD
![ERD](asset/ERD.png)
### 기능
* 물품 판매글 올리기(제목, 설명, 이미지, 최소판매가격, 상태, 작성자, 비밀번호) - 판매 상태: 판매중, 판매완료
* 댓글 달기(작성자, 비밀번호, 내용, 답글) 
* 댓글에 판매자가 답글 달기(내용)
* 구매 제안하기(제안금액, 제안상태, 작성자, 비밀번호) - 제안 상태: 제안, 수락, 거절, 확정
* 회원 정보 관리

User가 keyword라서 users 테이블 명을 사용하였습니다.

## 사용자 인증
### CustomUserDetails
UserDetails 인터페이스를 구현하는 CustomUserDetails를 생성하여, 사용자 정보를 관리하는 데 활용했습니다.
```java
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;
    private String username;
    private String password;
    @Getter
    private String phone;
    @Getter
    private String email;
    @Getter
    private String address;
    
    }
    ...생략...

```

### JpaUserDetailsManager
UserDetailsManager 인터페이스를 구현하여, 데이터베이스와 연동하여 사용자 정보를 관리합니다. 

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        UserEntity user = optionalUser.get();

        return CustomUserDetails.fromEntity(user);
    }

    @Override
    public void createUser(UserDetails user) {
        log.info("try create user : {}", user.getUsername());
        //username 중복시 에러
        if (userExists(user.getUsername())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try {
            //userEntity로 바꾸고 저장
            this.userRepository.save(((CustomUserDetails)user).newEntity());
        } catch(ClassCastException e){
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ... (생략) ...

    @Override
    public boolean userExists(String username) {
        // ... (생략) ...
    }

    @Override
    public void updateUser(UserDetails user) {
        // ... (생략) ...
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        // ... (생략) ...
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // ... (생략) ...
    }
}

```

### UserController
JpaUserDetailsManager를 의존성 주입을 받아 회원가입, 회원정보업데이트, 탈퇴, 비밀번호 변경을 하는 엔드포인트를 구현했습니다. 회원가입을 제외하고 비밀번호를 requestBody로 전달받아서 데이터베이스의 비밀번호와 한번 더 확인을 하고, 기능을 수행합니다.
```java

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;
    
    public UserController(UserDetailsManager manager, PasswordEncoder passwordEncoder, UserUtils userUtils) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
    }

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(
            @RequestBody UserRegisterDto dto
            ){
        //비밀번호, 비밀번호 확인 비교
        if (dto.getPassword().equals(dto.getPasswordCheck())){
            log.info("password match");
            //회원가입
            manager.createUser(CustomUserDetails.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .build());

            ResponseDto response = new ResponseDto();
            response.setMessage("회원가입이 완료되었습니다. ");
            return ResponseEntity.ok(response);
        }

        log.warn("password does not match..");
        ResponseDto response = new ResponseDto();
        response.setMessage("비밀번호와 비밀번호 확인이 틀립니다.");
        return ResponseEntity.badRequest().body(response);

    } ...생략...

```


### JWT 발급, 인증
#### JwtTokenUtils
- generateToken : JWT를 생성합니다. (토큰에는 username, 발급시각, 유효시간이 담겨있습니다.)
- validate : jwt를 해석해서 유효한지 판단합니다.
- parseClaims : jwt를 해석해서 Claims부분만 반환합니다.
```java

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key signingKey;
    private final JwtParser jwtParser;
    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); //암호화
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build();
    }

    //JWT 생성
    public String generateToken(UserDetails userDetails){
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername()) //username
                .setIssuedAt(Date.from(Instant.now())) //발급시간
                .setExpiration(Date.from(Instant.now().plusSeconds(36000))); //1시간 동안 유효

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

    //jwt를 해석해서 유효한 jwt인지 판단
    public boolean validate(String token){
        try{
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.warn("malformed jwt");
        } catch (ExpiredJwtException e) {
            log.warn("expired jwt");
        } catch (UnsupportedJwtException e) {
            log.warn("unsupported jwt");
        } catch (IllegalArgumentException e) {
            log.warn("illegal argument");
        }
        return false;
    }

    //JWT를 해석해서 Claims부분만 반환
    public Claims parseClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
```
#### JwtTokenFilter
header에 들어있는 jwt를 확인해서 사용자 인증정보를 설정합니다. 해당 내용을 필터로 등록해주었습니다.
```java
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenUtils jWtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jWtTokenUtils) {
        this.jWtTokenUtils = jWtTokenUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
         String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
         if(authHeader != null && authHeader.startsWith("Bearer ")){

             if (authHeader.length() >= 8){ //"Bearer "뒤에 글자가 더 있는지 판단
                 String jwt = authHeader.split(" ")[1];
                 if (jWtTokenUtils.validate(jwt)){

                     SecurityContext context = SecurityContextHolder.createEmptyContext();
                     String username = jWtTokenUtils.parseClaims(jwt).getSubject();
                     
                     //사용자 인증정보 설정
                     AbstractAuthenticationToken authenticationToken
                             = new UsernamePasswordAuthenticationToken(
                                     CustomUserDetails.builder().username(username).build(),
                                    jwt, new ArrayList<>()
                            );
                     context.setAuthentication(authenticationToken);
                     SecurityContextHolder.setContext(context);
                     log.info("set security context with jwt");

                 } else {
                     log.warn("jwt validation failed");
                 }
             }
             else {
                 log.warn("jwt validation failed");
             }

         }

         filterChain.doFilter(request, response);
    }
}

```

#### 인증 정보 가져오기
context에 등록되어 있는 사용자 인증 정보를 가져오기 위한 클래스
   비밀번호 확인할 때, 현재 사용자 정보를 가져올 때 쓰입니다. UserDetails의 getPassword 메소드를 할 시 null을 반환해서 아래와 같이 username을 통해 데이터베이스에 저장되어 있는 비밀번호를 불러와 비밀번호를 확인했습니다.
```java
@Slf4j
@Service
public class UserUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUtils(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //username으로 Entity에 저장되어있는 비밀번호랑 비교
    public String getPassword(){
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity user = optionalUser.get();
        return user.getPassword();
    }
    public boolean checkPassword(String password){
        //기존의 저장되어 있던 userEntity 불러오기
        Optional<UserEntity> optionalUser = this.userRepository.findByUsername(getCurrentUser().getUsername());

        //userEntity가 존재하지 않으면 에러
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity user = optionalUser.get();

        return passwordEncoder.matches(password, user.getPassword());

    }

    //인증정보로 UserDetails를 반환하는 메소드
    public UserDetails getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //인증이 되지 않은 사용자의 경우 에러
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        return (UserDetails) authentication.getPrincipal();
    }

    //인증 정보 가져와서 userRepostory에 저장되어있는
    // userEntity를 반환하는 메소드
    public Optional<UserEntity> getUserEntity(UserRepository userRepository){
        String username =  getCurrentUser().getUsername();
        return userRepository.findByUsername(username);
    }
}

```

### security 설정

```java

@Configuration
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authHttp ->
                authHttp
                        .requestMatchers("/token/issue")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,"/items/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,"/item/{itemId}/proposals")
                        .authenticated()
                        .requestMatchers("/users/register")
                        .anonymous()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
```


## API Endpoint


Postman Collection은 asset파일 밑에 위치해 있습니다.

<details><summary> <span style="color: #007bff; text-decoration: underline;"><b>물품 판매</b></span> </summary>


*    물품 등록 `POST /items`    
물품 판매글을 등록합니다. 물품 판매글을 올리면 "판매중"로 등록됩니다. 제목, 설명, 작성자, 비밀번호는 공백이거나 입력하지 않으면 등록되지 않습니다. 또한  최소가격은 0이상으로 입력해야 등록됩니다.
     (유효성 검증)
   ```JSON
   {
        "title": "강아지 노즈워크 장난감 팝니다.",
        "description": "거의 새거입니다. 싸게 가져가세요.",
        "minPriceWanted": 10000,
        "writer": "kim k",
        "password": "123456"
   }
   ```

*   전체 조회  `GET /items?page={page}&limit={limit}`  
    페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 기본값(page = 0, limit = 20)으로 조회됩니다. 조회의 경우 작성자와 비밀번호를 제외하고 보여줍니다.   


  * 단일조회 `GET /items/{itemId}` - 해당 글 하나만 반환됩니다. 조회의 경우 작성자와 비밀번호를 제외하고 보여줍니다.
      ```JSON
    {
        "id": 42,
        "title": "강아지 노즈워크 장난감 팝니다.",
        "description": "거의 새거입니다. 싸게 가져가세요.",
        "imageUrl": null,
        "minPriceWanted": 10000,
        "status": "판매중"
      }
      ```   


* 판매글 수정 `PUT /items/{itemId}`  
    비밀번호가 물품 판매글의 비밀번호와 확인한 뒤 같으면 요청한 값으로 수정합니다. 단, 비밀번호는 수정되지 않습니다.
수정도 등록과 같은 유효성 검증을 합니다.   


* 이미지 수정 `PUT /items/{itemId}/image`  
  이미지는 판매글이 등록할 때는 등록하지 않고, 나중에 따로 이미지를 등록해줍니다. form-data로 아래와 같이 이미지에는 파일을 첨부하여 RequestBody를 보냅니다.
    ```
  image:    image.png(file)
  writer:   kim k
  password: 123456
  ```
  이미지를 "media/{id}/image.확장자"로 저장을 하고, image 값은 "/static/{id}/image.확장자"로 
저장되며 해당 url로 이미지를 확인할 수 있습니다. 이미지는 한개만 저장가능하며, 다시 수정할 경우 덮어씌어집니다.


* 삭제 `DELETE /items/{itemId}`   
비밀번호를 같은지 확인하고, 삭제를 합니다. 작성자, 비밀번호는 공백이거나 입력하지 않으면 삭제할 수 없습니다. (유효성 검증)

</details>
<details><summary> <span style="color: #007bff; text-decoration: underline;"><b>댓글</b></span> </summary>


*    댓글 등록 `POST /items/{itemId}/comments`       
    내용, 작성자, 비밀번호는 공백이거나 입력하지 않으면 등록되지 않습니다. (유효성 검증)
     ```JSON
     {
        "writer": "jeeho.edu",
        "password": "qwerty1234",
        "content": "할인 가능하신가요?"
     }
        ```

*   전체 조회  `GET /items/{itemId}/comments `  
    해당 물품 판매글에 대한 댓글을 전체 조회합니다. 페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 
기본값(page = 0, limit = 20)으로 조회됩니다. 작성자, 비밀번호, 해당 물품 판매글의 번호는 보여지지 않습니다.


* 댓글 수정 `PUT /items/{itemId}/comments/{commentId}`  
  비밀번호가 댓글 작성자의 비밀번호와 확인한 뒤 같으면 요청한 값으로 수정합니다. 단, 비밀번호는 수정되지 않습니다.
  수정도 등록과 같은 유효성 검증을 합니다.


* 답글 수정 `PUT /items/{itemId}/comments/{commentId}/reply`  
  댓글이 달린 물품 판매글의 작성자가 댓글에 답글을 쓸 수 있습니다. 물품 판매자의 비밀번호와 첨부한 비밀번호가 확인한 뒤 같으면 답글을 달 수 있습니다. 한 댓글에는 한번만 답글을 달 수 있고, 수정은 여러번할 수 있습니다. 답글 수정도 작성자, 비밀번호, 답글이 없으면 수정할 수 없습니다.(유효성 검증)
  ```JSON
  {
    "writer": "kim k",
    "password": "123456",
    "reply": "안됩니다"
  }
  ```
* 댓글 삭제 `DELETE /items/{itemId}`   
  비밀번호를 확인하고, 삭제를 합니다. 작성자, 비밀번호는 공백이거나 입력하지 않으면 삭제할 수 없습니다. (유효성 검증)
</details>

<details><summary><span style="color: #007bff; text-decoration: underline;"> <b>구매 제안</b></span> </summary>

*    구매 제안 등록 `POST /items/{itemId}/proposals`       
     작성자, 비밀번호는 공백이거나 입력하지 않으면 등록되지 않습니다. 또한 제안가격은 0이상으로 입력해야 등록됩니다.(유효성 검증)
     ```JSON
     {
         "writer": "jeeho.edu",
        "password": "qwerty1234",
        "suggestedPrice": 1000000
     }
        ```

  *   구매 제안 조회  `GET /items/{itemId}/proposals?writer=jeeho.edu&password=qwerty1234&page=1  `  
      물품 판매자와 구매제안 등록자만 조회할 수 있습니다. 나머지는 내용이 빈 페이지로 조회됩니다. 페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 기본값(page = 0, limit = 20)으로 조회됩니다.
  작성자, 비밀번호, itemId는 표시되지 않습니다.  

      물품 판매자는 해당 물품 판매글에 대한 댓글을 전체 조회합니다. 판매자의 작성자와 비밀번호가 같아야 조회가능합니다.
          구매제안 등록자는 자신이 해당 물품에 대해 등록한 제안만 확인 가능하고, 등록자의 작성자와 비밀번호가 같아야 합니다.
  ```JSON
      {
      "content": [
      {
      "id": 1,
      "suggestedPrice": 1000000,
      "status": "제안"
      }
      ],
       ... 생략 ...
      "size": 20,
      "number": 0,
      "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
      },
      "numberOfElements": 1,
      "first": true,
      "empty": false
      }
   ```

* 구매 제안 수정 `PUT /items/{itemId}/proposals/{proposalId}`  
    구매 제안 등록자의 작성자와 비밀번호를 확인하여 같으면 수정합니다. 등록과 같은 유효성 검증을 합니다.


* 삭제 `DELETE /items/{itemId}/proposals/{proposalId}`   
  구매 제안 등록자의 작성자와 비밀번호를 확인하여 같으면 수정합니다. 작성자, 비밀번호가 공백이거나 입력하지 않으면 삭제할 수 없습니다.(유효성 검증)


* 구매 제안 수락/거절 `PUT /items/{itemId}/proposals/{proposalId}/status`  
    물품 판매자의 작성자와 비밀번호가 상태를 "수락"이나 "거절"로 변경합니다. "수락" / "거절"이 아닌 값을 입력하면
    에러메세지가 납니다.
    ```JSON
    {
        "writer": "kim k",
        "password": "123456",
        "status": "수락"
    }
    ```
* 구매 제안 확정 `PUT /items/{itemId}/proposals/{proposalId}/status`
      
  구매 제안 등록자의 작성자와 비밀번호가 같고, 상태가 "수락"이면 "확정"으로 변경하고, 해당 물품에 대한 다른 구매 제안은 "거절"로 변경합니다. 
구매 제안이 "확정"이 된 해당 물품의 상태는 "판매 완료"가 됩니다.
    ```JSON
    {
      "writer": "jeeho.edu",
      "password": "qwerty1234",
      "status": "확정"
    }
    ```
  </details>

<details><summary> <span style="color: #007bff; text-decoration: underline;"><b>회원 정보 관리</b></span> </summary>


*    회원가입 `POST /users/register`    
    회원 정보는 아이디(username), 비밀번호, 전화번호, 이메일, 주소가 있습니다. 작성자
    아이디, 비밀번호, 비밀번호확인은 공백이거나 입력하지 않으면 등록되지 않습니다. (유효성 검증) 비밀번호와 비밀번호확인이 다르거나 중복된 username이면 badrequest를 반환합니다.
     
   ```JSON
   {
      "username":"kim k",
      "password":"123456",
      "passwordCheck":"123456"
}
   ```

*    회원가입 `POST /token/issue`    
    토큰을 발급하는 엔드포인트입니다. 아이디랑 비밀번호를 입력하면 token을 반환합니다. 입력하지 않으면 오류메세지를 반환합니다. TokenUtils의 generateToken메소드를 사용해서 TokenService가 수행합니다. 해당 엔드포인트 동작은 TokenController에 구현되어 있습니다.

   ```JSON
   {
      "username":"kim k",
      "password":"123456"
  
}
   ```
```java
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJraW0gayIsImlhdCI6MTY5MDk2NzQwNCwiZXhwIjoxNjkxMDAzNDA0fQ.PcfQoEHf4V6EbenzOKrCqjlmRFaWrL-7H-hsLyX3urrbV7Z_ANktbWXzDr-dWBEdSfv_5noMPYSmMjf9RL35fw"
}
```


* 회원 정보 변경 `PUT /users/update`  
  회원정보를 변경하기 위해서 비밀번호를 확인합니다. 비밀번호는 필수로 입력해야하며, 변경가능한 정보는 전화번호, 이메일, 주소입니다. 아이디는 바꿀 수 없으며, 비밀번호는 다른 엔드포인트에서 바꿀 수 있습니다.
```java
{
    "password":"123456",
    "phone":"010-2222-5555",
    "email":"pgjwg@gmail.com",
    "address":"서울시 용산구"
}
```

* 비밀번호 변경 `PUT /users/changePassword`  
    현재비밀번호, 새 비밀번호를 필수로 입력해야 합니다.
 
  ```java
    {
        "oldPassword":"123456",
        "newPassword":"987654"
    }
    ```


* 삭제 `DELETE /users/delete`   
  비밀번호를 같은지 확인하고, 삭제를 합니다. 비밀번호를 필수로 입력해야 합니다.
```java
{
    "password":"123456"
}
```

#### 바뀐 기능
회원 정보 관리 엔드포인트를 제외하고, 다른 엔드포인트의 경우 아이디와 비밀번호와 같은 사용자 정보를 별로도 requestbody를 첨부하지 않고, token을 header에 첨부하면서 사용자정보를 활용하고 있습니다.
또한 물품과 댓글, 구매제안의 조회(GET)의 경우 사용자정보(username)도 함께 제공하고 있습니다.
물품, 댓글의 조회의 경우 모든 사용자들이 접근할 수 있으며, 구매제안의 조회의 경우 인증된 사용자만 사용할 수 있습니다.

## 에러 값, 성공 값

**에러 시**
  
> 판매글, 댓글, 구매 제안을 찾을 수 없는 경우 에러코드 **404 NOT_FOUND**를 반환합니다.  
작성자, 비밀번호를 비교했을 때 다를 경우와 같은 잘못된 요청은 **400 BAD_REQUEST**를 반환합니다.

**성공시**
  
>상태 코드 **200 ok**, 상태에 맞는 성공 메세지를 반환하거나 조회한 경우에는 **조회한 값**을 반환합니다.

**유효성 검증**
> 유효성 검증 시 잘못된 값은 해당 필드와 함께 오류 메세지를 반환합니다.

## DTO

- ItemDto : 모든 필드가 다 들어있는 Item의 dto
- ItemGetDto : 조회할 때 쓰는 dto (작성자, 비밀번호 제외)
- CommentDto : 모든 필드가 다 들어있는 Comment의 dto
- CommentGetDto : 조회할 때 쓰는 dto (작성자, 비밀번호, itemId 제외)
- CommentReplyDto : 댓글에 대한 답글을 수정할 때 쓰는 dto
- NegotiationDto : 모든 필드가 다 들어 있는 Negotiation의 dto
- NegotiationGetDto : 조회할 때 쓰는 dto (작성자, 비밀번호, itemId 제외)
- NegotiationStatusDto : 구매 제안의 상태를 수정할 때 쓰는 dto
- DeleteDto : 물품 판매, 댓글, 구매 제안을 삭제할 때 쓰는 dto (id, 작성자, 비밀번호)
- UserChangePasswordDto : 비밀번호 변경시 쓰는 dto
- UserDeleteDto : 회원 탈퇴 시 쓰는 dto
- UserRegisterDto : 회원 가입 시 쓰는 dto
- UserRequestDto : 회원 정보 수정 시 쓰는 dto
- JwtRequestDto : jwt 발급 발을 때 request하는 dto
- JwtTokenDto : token을 발급 해주는 dto

## TDD
모든 코드에 대해서 테스트 코드를 작성하지는 못하였지만 User와 Jwt에 대한 테스트코드는 일부 작성하였습니다.
또한 테스트를 하기 위해서 profiles를 나눠 테스트할 때는 application-test.yaml로 설정하여 h2데이터베이스를 이용하였습니다.
실제 프로젝트에서는 sqlite를 사용했습니다.

## 추가 개선 방향

- 권한 설정 예: 관리자
- 4, 5, 6 일차 미션 수행
- tdd 코드 
