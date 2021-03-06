# 貓薄荷
網路上其實到處都有寫好的 blog 系統，如果單純只是想不依賴平台的創造一個屬於自己的空間，實在沒有必要這麼麻煩，所以我寫這個系統是有其他目標的，畢竟按照功能的完整性來講，我不可能做到像網路上的其他解決方案那樣完整，因為我沒有足夠的時間。貓薄荷專案有三個目標，按照時間排列如下:
- 短期目標: 藉此驗證自己在後端領域所學。
- 中期目標: 有一個可用的 blog 系統，讓我不用依賴平台。
- 長期目標: 做成一個教學專案，讓想學程式的人可以當成教材來使用。

之所以會想做成教學的專案，主要原因是我週遭好多人想要轉行當軟體工程師，雖然我不能算一個很厲害的工程師，但我也希望自己有能力可以幫助他們，可以說第三個目標才是激發我寫這個專案的契機，其他目標都是後來才慢慢浮現出來的。

## 版本控制
主要講一下開發管理的策略。本專案主要有兩個 branch ，開發完成但未上線的功能會放在 master branch 中，線上運行的則是 production branch 的版本。

在開發新功能的時候會另外開一條新的 feature branch，開發完畢後合成到 master 中。


如果想看當前 production 分支運行的成果，可以直接參考我的部落格 https://blog.nov29.one 

## 後端 TODO
- [X] 串接 AWS S3 作為圖片服務器。
- [X] github action auto deploy
- [X] testing
- [ ] 重新設計並完成文章分類列表(category、tag)相關的資料 API
- [ ] 重構管理員註冊及使用者管理模塊
- [ ] jwt token auto renew
- [ ] 輸入檢查
- [ ] 完善錯誤處理
- [ ] README 新增「部屬」章節

## 目錄結構
使用 Java Spring Boot 並不像前端使用 Nuxt 一樣有一個規範好的目錄結構，基本上都是自己來，下面主要解釋 src/main 目錄底下的結構。
```
src
├── main
│   ├── java.space.nov29.cataria
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── exception
│   │   ├── filter
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   └── resources
└── test
```
1. config:

    config 資料夾底下主要放的是有關後端伺服器設定的部份，包含安全設定、CORS 設定等。

2. controller:

    這裡主要放 MVC 架構中的 controller 的部份，透過不同的功能分類來拆分 controller。

3. dto

    DTO 的用途是規範在 class 之間傳遞的物件的格式，一個請求進來，或是從 DB 裡面把資料撈出來之後，應該進行適當的封裝，在組件之間傳遞資料的時候才有標準可以參照。

4. exception

    這邊存放的是自訂的 exception，有這些自訂的 exception 才能更精準的進行調適，出錯的時候也才有跡可循。

5. filter
   
   這邊放的是自訂的 filter，作用是在 HTTP 請求到達之可以被一個或是多個的 filter 請求進行預處理，處理內容可能是登入檢查、輸出日誌等功能，這樣可以把程式邏輯拆分的更乾淨，處理請求的 controller 只需要專住在怎麼處理請求的資源即可。

6. model
   
   這邊放的是與資料庫交互的 model。ORM 工具的目的在於自動的把位於資料庫的內容和程式之間的物件進行關係映射，這裡規定了他們之間的映射關係。

7. repository
   
   在資料存取方面，由於我用的是 Spring Data JPA，這是一個比 Hibernate 這種 ORM 工具更抽象的一層工具，進一步對 DB 的操作進行封裝以簡化開發的繁瑣程度，在這裡我們需要定義需要哪些方法來進行和資料庫的互動。

8. service

    這裡進一步對和資料庫之間的互動進行封裝，controller 只會調用這裡暴露出去的方法。對 controller 來說，它只知道系統內部有哪些資料類型，和有哪些 service 提供了的操作。進一步減少程式之間的耦合。

9. resources

    這裡存放的是一些資源類的文件，像是存儲專案設定的 application.properties 文件。Github 上只提供了一個範本作為參考，使用者在運行專案的時候應該根據自己的環境去撰寫相關的設定。

10. test

    所以的測試都寫在這邊，裡面的結構和src/main裡面一樣。
## RESTful API 定義
決定 API 的時候，我自己的作法是先思考我有哪些資源，並且需要支援那些操作，之後再根據這些資願把功能轉換為一系列的 API endpoint。
所以首先來看資源列表以及支援的操作:
```markdown
文章(資源)
  - 列舉所有文章
  - 取得某 category 所有文章
  - 取得某 tag 所有文章
  - 取得指定文章
  - 新增文章
  - 修改文章
  - 刪除文章
tag(資源)
  - 取得所有 tag
  - 取得該 tag 底下的所有文章
category(資源)
  - 取得所有 category
  - 取得該 category 底下的所有文章
圖片(資源)
  - 新增圖片
  - 取得圖片列表
  - 刪除圖片
認證
  - 取得 jwt token
  - 新增使用者
```

在確定能夠被使用者取得的資源之後，我們可以大致列出應該要有哪些 API。
### 普通用戶(未登入)
| method |              path              |      description                | 實現 |
| :----: | :----------------------------: | :-----------------------------: | :--: |
| GET    | /posts                         | 取得所有文章(含分頁功能)            | ✅  |
| GET    | /posts/category/{categoryName} | 取得分類目錄下所有文章(含分頁功能)    | ✅  |
| GET    | /posts/tags/{tagName}          | 取得分類標籤下所有文章(含分頁功能)    | ❌  |
| GET    | /posts/{id or slug}            | 透過文章 id 或 slug 取得指定文章    | ✅  |
| GET    | /pageInfo                      | 取得 category 和 tag 列表         | ❌  |

### 帳號管理
| method |              path              |      description                | 實現 |
| :----: | :----------------------------: | :-----------------------------: | :--: |
| POST   | /auth/login                    | 獲取登入 token                    | ✅  |
| POST   | /auth/signup                   | 註冊管理員帳號(暫時關閉)            | ✅  |

### 管理員功能
| method |              path              |      description                | 實現 |
| :----: | :----------------------------: | :-----------------------------: | :--: |
| GET    | /admin/posts                   | 取得所有文章(包含未發布文章)        | ✅  |
| POST   | /admin/posts                   | 新增文章                         | ✅  |
| PUT    | /admin/posts                   | 修改文章                         | ✅  |
| DELETE | /admin/posts?id={post id}      | 刪除文章                         | ✅  |
| GET    | /admin/assets                  | 取得所有圖片                      | ❌  |
| POST   | /admin/assets                  | 新增圖片                         | ✅  |
| DELETE | /admin/assets                  | 刪除圖片                         | ❌  |
| GET    | /admin/categories              | 取得所有 category 的狀態          | ❌  |
| GET    | /admin/tags                    | 取得所有 tag 的狀態               | ❌  |

## 設計
### 後端結構設計
![](.github/assets/backend-structure.png)
後端分成三層結構，Controller 層、Service 層和 Module 層。

1. Controller 層: 負責對請求進行分類，根據使用者的身份分成訪客和管理員兩類，再加上不屬於兩者的身份驗證類。作為整個應用程式的入口，Controller 層的設計是會資料進行格式轉換，隔絕外部的請求以及內部流通的資料格式，降低耦合，除此之外還會進行請求的錯誤處理。Controller 基本上部處理業務邏輯，上述動作執行完之後就會把業務委託給 Service 層。
   
2. Service 層: 負責最主要的業務邏輯，大部分的功能實現都寫在這邊，在完成 controller 委託的任務之後會返回相應的結果。在需要對資料進行互動的時候，則會進一步的把資料庫操作的部份交給 Module 層。

3. Module 層: 負責對資料庫的操作，並把資料庫內的資料映射到 java 的類上。對 service 層隱藏資料庫內的實現，降低耦合。

### production 環境設計
![](.github/assets/production-environment.png)
貓薄荷這個專案是採用前後端分離的架構，圖片中除了 S3 是外部服務之外，都可以分開部屬到不同的機器上，Nginx 會負責對請求進行代理的工作，根據 uri 去轉發，前端伺服器會負責畫面顯示的工作，並在需要 SSR 的時候對後端伺服器做出請求，後端只負責資料處理，並根據 API 的規定返回相應的資料。由於目前我採用的是單伺服器的 production 環境，所以前端會直接對在同一台機器上的後端發送請求。

Nginx 會代理對於已經上傳到 S3 的資源請求，並不會經過前端或後端服務，可以降低伺服器的負擔。

