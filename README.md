# MiniProject_Basic_AhnChaeYeon
# ♻️멋사마켓♻️
> 멋쟁이사자처럼 백엔드 스쿨 5기 미니프로젝트

## 멋사마켓이란?
사용자가 물품을 등록하고, 댓글을 통해 소통을 하며, 최종적으로 구매 제안에 대해 수락을 해서 거래가 이루어지는 중고거래 플랫폼입니다.

### 기능
* 물품 판매글 올리기(제목, 설명, 이미지, 최소판매가격, 상태, 작성자, 비밀번호) - 판매 상태: 판매중, 판매완료
* 댓글 달기(작성자, 비밀번호, 내용, 답글) 
* 댓글에 판매자가 답글 달기(내용)
* 구매 제안하기(제안금액, 제안상태, 작성자, 비밀번호) - 제안 상태: 제안, 수락, 거절, 확정

## 에러 값, 성공 값
에러 시
판매글, 댓글, 구매 제안을 찾을 수 없는 경우 에러코드 404 NOT_FOUND를 반환합니다.  
작성자, 비밀번호를 비교했을 때 다를 경우 BAD_REQUEST를 반환합니다.

성공시 상태 코드 200 상태에 맞는 성공 메세지를 반환하거나 조회한 경우에는 조회한 값을 반환합니다.

## Endpoints url
### 1. 물품 판매
*    판매글 등록 `POST /items`       
물품 판매글을 올리면 "판매중"로 등록됩니다. 제목, 설명, 작성자, 비밀번호는 공백이거나 입력하지 않으면 등록되지 않습니다. 또한  최소가격은 0이상으로 입력해야 등록됩니다.
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
    페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 기본값(page = 0, limit = 20)으로 조회됩니다.
* 단일조회 `GET /items/{itemId}` - 해당 글 하나만 반환됩니다.
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
  이미지를 "media/{id}/image.확장자"로 저장을 하고, image 값은 "/static/{id}/image.확장자"로 저장되며 해당 url로 이미지를 확인할 수 있습니다.
* 삭제 `DELETE /items/{itemId}`   
비밀번호를 확인하고, 삭제를 합니다.

### 2. 댓글
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
    해당 물품 판매글에 대한 댓글을 전체 조회합니다. 페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 기본값(page = 0, limit = 20)으로 조회됩니다.
* 판매글 수정 `PUT /items/{itemId}/comments/{commentId}`  
  비밀번호가 댓글 작성자의 비밀번호와 확인한 뒤 같으면 요청한 값으로 수정합니다. 단, 비밀번호는 수정되지 않습니다.
  수정도 등록과 같은 유효성 검증을 합니다.
* 답글 수정 `PUT /items/{itemId}/comments/{commentId}/reply`  
  댓글이 달린 물품 판매글의 작성자가 댓글에 답글을 쓸 수 있습니다. 물품 판매자의 비밀번호와 첨부한 비밀번호가 확인한 뒤 같으면 답글을 달 수 있습니다. 한 댓글에는 한번만 답글을 달 수 있고, 수정은 여러번할 수 있습니다.
  ```JSON
  {
    "writer": "kim k",
    "password": "123456",
    "reply": "안됩니다"
  }
  ```
* 삭제 `DELETE /items/{itemId}`   
  비밀번호를 확인하고, 삭제를 합니다.

### 3. 구매 제안
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
      물품 판매자와 구매제한 등록자만 조회할 수 있습니다.
  해당 물품 판매글에 대한 댓글을 전체 조회합니다. 페이지 단위로 조회되며,  page랑 limit를 지정하지 않으면 기본값(page = 0, limit = 20)으로 조회됩니다.
* 판매글 수정 `PUT /items/{itemId}/comments/{commentId}`  
  비밀번호가 댓글 작성자의 비밀번호와 확인한 뒤 같으면 요청한 값으로 수정합니다. 단, 비밀번호는 수정되지 않습니다.
  수정도 등록과 같은 유효성 검증을 합니다.
* 답글 수정 `PUT /items/{itemId}/comments/{commentId}/reply`  
  댓글이 달린 물품 판매글의 작성자가 댓글에 답글을 쓸 수 있습니다. 물품 판매자의 비밀번호와 첨부한 비밀번호가 확인한 뒤 같으면 답글을 달 수 있습니다. 한 댓글에는 한번만 답글을 달 수 있고, 수정은 여러번할 수 있습니다.
  ```JSON
  {
    "writer": "kim k",
    "password": "123456",
    "reply": "안됩니다"
  }
  ```
* 삭제 `DELETE /items/{itemId}`   
  비밀번호를 확인하고, 삭제를 합니다.