# MiniProject_Basic_AhnChaeYeon
# ♻️멋사마켓♻️
> 멋쟁이사자처럼 백엔드 스쿨 5기 미니프로젝트     
> 🥕당근마켓, 중고나라 등을 착안하여 중고 제품 거래 플랫폼을 만들어보는 미니 프로젝트입니다.

## 💻프로젝트 소개
_________________________
> 사용자가 물품을 등록하고, 댓글을 통해 소통을 하며, 최종적으로 구매 제안에 대해 수락을 해서 거래가 이루어지는 중고거래 플랫폼입니다.

## 🗓️ 프로젝트 기간
_________________________
 **2023. 06.29 ~ 2023. 07. 04**

## ⚙️ 개발환경
___________________________
- Spring Boot 3.1.1
- java 17
- Intellij
- SQLite

## ERD
___________________________
![ERD](/image.png)
### 기능
* 물품 판매글 올리기(제목, 설명, 이미지, 최소판매가격, 상태, 작성자, 비밀번호) - 판매 상태: 판매중, 판매완료
* 댓글 달기(작성자, 비밀번호, 내용, 답글) 
* 댓글에 판매자가 답글 달기(내용)
* 구매 제안하기(제안금액, 제안상태, 작성자, 비밀번호) - 제안 상태: 제안, 수락, 거절, 확정



## API Endpoint
### 1. 물품 판매
______



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
## 에러 값, 성공 값
____________
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


## 추가 개선 방향
_______________
- 이미지 여러개 등록하는 기능 추가
- 데이터를 생성한 시간(creat_at), 수정한 시간(update_at) 필드 추가
- exceptionHandler 자세히