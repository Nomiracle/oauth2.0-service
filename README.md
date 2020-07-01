# oauth2.0 service
micro-service for oauth 2.0

step 1: open redis server


step 2: run application and go to http://localhost:18106/

# have been implemented provider


|provider|reference document
|-|-|
|aliyun| https://help.aliyun.com/document_detail/93696.html
|alipay| https://opendocs.alipay.com/open/284/web
|dingding| https://ding-doc.dingtalk.com/doc#/serverapi2/etaarr

# 添加第三方认证源步骤：
1.在application中添加client_id 与 client_secret设置

如定义阿里云client_id 与 client_secret：
```
oauth2:
  aliyun:
    client_id: "45456xxx"
    client_secret: "terggsgxxx"
    redirect_uri: http://localhost:18106/auth/callback
```
2.在`ClientConfig` 类中创建设置bean。如阿里云，其中名称规则为`ConfigConstants`类中`AUTH_CLIENT_ALIYUN + AUTH_CONFIG_SUFFIX`
```
    @Bean(ConfigConstants.AUTH_CLIENT_ALIYUN+ ConfigConstants.AUTH_CONFIG_SUFFIX)
    @ConfigurationProperties("oauth2.aliyun")
    public ClientConfig aliyun() {
        return new ClientConfig();
    }
```
3.创建 `service` 实现请求第三方认证源获取用户信息。例如阿里云实现: 实现` CommonClient`接口：
```
@Slf4j
@Service(AUTH_CLIENT_ALIYUN + AUTH__CONFIG_CLIENT_SUFFIX)
@RequiredArgsConstructor
public class AliyunAuth implements CommonClient {
    ......
}
```
其中 `Service` 别名规则为 `ConfigConstants`类中 `AUTH_CLIENT_ALIYUN +AUTH__CONFIG_CLIENT_SUFFIX`

实现接口中方法：
```
public interface CommonClient {

    String getAuthorize(String redirectUrl,String client_id,String state);
    String getUserInfo(AuthCallback authCallback, String redirectUrl, String client_id, String client_secret) throws BusinessException;
    String getResponse(String rawUserInfo);

     ......
}
```
其中 getAuthorize 为构造链接函数，getUserInfo实现获取用户信息，getResponse 封装原始用户信息到统一定义的用户信息类中
