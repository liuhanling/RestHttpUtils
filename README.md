[![](https://jitpack.io/v/liuhanling/RestHttpUtils.svg)](https://jitpack.io/#liuhanling/RestHttpUtils)

# RestHttpUtils
## 基于RxJava2和Retrofit2封装，REST风格，支持动态切换baseUrl

### 添加Gradle依赖

1.项目 build.gradle 的 repositories 添加:
```
     allprojects {
         repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```
 2.模块 build.gradle 的 dependencies 添加:
 
 ```
        dependencies {
            ...
            compile 'com.github.liuhanling:RestHttpUtils:1.1.0'
        }
```

# 使用说明
### 1、RestHttpUtils初始化配置

> ##### 在项目的Application的onCreate方法中进行初始化配置
```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient client = new OkHttpConfig.Builder(this)
        //全局的请求头信息
        .setHeaders(headerMap)
        //开启缓存策略(默认false)
        .setCache(true)
        .setHasNetCacheTime(10)//默认有网络时候缓存60秒
        .setNoNetCacheTime(3600)//默认有网络时候缓存3600秒
        //不设置的话，默认不对cookie做处理
        //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
        //.setAddInterceptor(UrlInterceptor.create())
        //全局ssl证书认证
        //1、信任所有证书, 不安全有风险（默认信任所有证书）
        //.setSslSocketFactory()
        //2、使用预埋证书, 校验服务端证书（自签名证书）
        //.setSslSocketFactory(cerInputStream)
        //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
        //全局超时配置
        .setReadTimeout(15)
        //全局超时配置
        .setWriteTimeout(15)
        //全局超时配置
        .setConnectTimeout(15)
        //全局打开日志
        .setDebug(true)
        .build();
        
        RestHttpUtils
                .getInstance()
                .init(this)
                .config()
                //配置adapter
                //.setCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //配置converter
                //.setConverterFactory(ScalarsConverterFactory.create(), GsonConverterFactory.create())
                //配置全局baseUrl
                .setBaseUrl("https://github.com/")
                //开启全局配置
                .setOkClient(okHttpClient);
      
    }
}
```

# 2、baseUrl配置及动态修改

### 2.1、全局单个baseUrl动态切换
```
设置URL拦截
setAddInterceptor(UrlInterceptor.create())

修改URL格式
RestUrlManager.getInstance().setUrl("newUrl");
```

### 2.2、多baseUrl对应单个ApiService
```
public interface ApiUrl {

    String DOUBAN_KEY = "douban";
    String GITHUB_KET = "github";
    String DOUBAN_URL = UrlInterceptor.BASE_URL_KEY + ":" + DOUBAN_KEY;
    String GITHUB_URL = UrlInterceptor.BASE_URL_KEY + ":" + GITHUB_KET;
}

public interface ApiService {

    @Headers({DOUBAN_URL})
    @GET("api/task/list")
    Observable<BaseData<List<Task>>> getTask();

    @Headers({GITHUB_URL})
    @GET("api/type/list")
    Observable<BaseData<List<Type>> getTypes();
}

设置URL拦截
setAddInterceptor(UrlInterceptor.create())

添加URL格式
RestUrlManager.getInstance()
        .addUrl(ApiUrl.DOUBAN_KEY, ApiUrl.DEFAULT_DOUBAN_URL)
        .addUrl(ApiUrl.GITHUB_KET, ApiUrl.DEFAULT_GITHUB_URL);

请求API格式
RestHttpUtils.createApi(ApiService.class)

修改URL格式
RestUrlManager.getInstance()
        .setUrl(ApiUrl.DOUBAN_KEY, "newDoubanUrl")
        .setUrl(ApiUrl.GITHUB_KET, "newGitHubUrl");
```
### 2.3、多baseUrl对应多个ApiService
```
请求API格式改变，其他与3.2一致：
RestHttpUtils.createApi(ApiUrl.DOUBAN_KEY, DouBanApi.class)
RestHttpUtils.createApi(ApiUrl.GITHUB_KET, GitHubApi.class)

若嫌在API的各个请求添加Headers麻烦，可采用以下方式：
① 不设置UrlInterceptor拦截器
② 修改Url前调用clear方法
  RestUrlManager.getInstance()
          .clear(ApiUrl.DOUBAN_KEY)
          .setUrl(ApiUrl.DOUBAN_KEY, "newDoubanUrl");
  
  说明：clear方法可清除缓存的对应API实例，下次请求时会重新创建API。
        
```          
### 2.4、温馨提示

> #### 存在多个API时，可创建一个类来管理，以简化操作

```
public class RestUtils {

    public static void init(Application app) {
        //RestHttpUtils初始化配置
    }

    public static DouBanApi getDouBanApi() {
        return RestHttpUtils.createApi(ApiUrl.DOUBAN_KEY, DouBanApi.class);
    }

    public static GitHubApi getGitHubApi() {
        return RestHttpUtils.createApi(ApiUrl.GITHUB_KET, GitHubApi.class);
    }
    
    ...
}
```

### 3、默认已实现三种数据格式

* 1、BaseObserver     (基础Observer, 接收类型实体类T)
* 2、StringObserver   (接收String数据)
* 3、DataObserver     (传入BaseData<T>类型; 返回data对应类型T)

# 代码实例

> ## 使用Application里边的全局配置的参数

### 3.1、使用BaseObserver请求示例
```
User数据结构
{
    "id": 1001
    "username":"hanley",
    "password":"xxxxxx",
    "loginNum":"900019"
    ...
}

@GET("api/getUser")
Observable<User> getUser(int id);

RestHttpUtils
         .createApi(ApiService.class)
         .getUser(1001)
         .compose(RxSchedulers.apply())
         .subscribe(new BaseObserver<User>() {
             @Override
             public void onSuccess(User user) {
                  //业务处理
             }

             @Override
             public void onFailure(String msg) {
                  //错误处理
             }
          });
```
### 3.2、使用StringObserver请求示例
```
@GET("api/info")
Observable<Info> getInfo();

RestHttpUtils
         .createApi(ApiService.class)
         .getInfo()
         .compose(RxSchedulers.apply())
         .subscribe(new StringObserver<String>() {
             @Override
             public void onSuccess(String str) {
                  //业务处理
             }

             @Override
             public void onFailure(String msg) {
                  //错误处理
             }
          });
```
### 3.3、使用DataObserver请求示例
```
BaseData<User>数据结构
{
    "code":0,
    "msg":"success",
    "data":{
        "id": 1001
        "username":"hanley",
        "password":"xxxxxx",
        "loginNum":"900019"
        ...
    }
}
        
@GET("api/info")
Observable<BaseData<User>> getUser(int id);

RestHttpUtils
         .createApi(ApiService.class)
         .getInfo()
         .compose(RxSchedulers.apply())
         .subscribe(new DataObserver<User>() {
             @Override
             public void onSuccess(User user) {
                  //业务处理
             }

             @Override
             public void onFailure(String msg) {
                  //错误处理
             }
          });
```
### 4、文件下载
```
RestHttpUtils
        .download(fileUrl)
        .subscribe(new DownloadObserver(fileName, fileDir) {
            @Override
            public String setTag() {
                return "download";
            }
        
            @Override
            public void onProgress(long bytes, long total, int progress) {
                //view.setText("下载中：" + progress + "%");
            }
            
            @Override
            public void onSuccess(File file) {
                //view.setText("下载完成");
            }
            
            @Override
            public void onFailure(String msg) {
                //
            }
        });
```
### 5、上传图片
上传单张图片的接口
```
RestHttpUtils
        .uploadFile(url, filePath)
        .compose(RxSchedulers.apply())
        .subscribe(new BaseObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                //业务处理
            }

            @Override
            public void onFailure(String error) {
                //错误处理
            }
        });
```
上传多张图片的接口
```
RestHttpUtils
        .uploadFiles(url, filePathList)
        .compose(RxSchedulers.apply())
        .subscribe(new BaseObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                //业务处理
            }

            @Override
            public void onFailure(String error) {
                //错误处理
            }
        });
```
自定义特定返回类型
```
@POST("api/photos/upload")
@Multipart
Observable<BaseData<List<Photo>>> uploadFiles(@Part List<MultipartBody.Part> files);

RestHttpUtils.getApi()
        .uploadFiles(UploadHelper.getParts(params, filePaths))
        .compose(RxSchedulers.apply())
        .subscribe(new BeanObserver<List<Photo>>() {
            @Override
            public void onResult(List<Photo> photos) {
                //业务处理
            }

            @Override
            public void onFailure(String msg) {
                //错误处理
            }
        });
```

### 6、取消请求相关配置
```
//Observer重写setTag
new XXXObserver<T>() {
    @Override
    protected String setTag() {
        return "xxxTag";
    }
    ...
}

//取消某个或某组请求
RestHttpUtils.cancel("xxxTag");

//取消多个或多组请求
RestHttpUtils.cancel("xxxTag1", "xxxTag2", "xxxTag3");
```

