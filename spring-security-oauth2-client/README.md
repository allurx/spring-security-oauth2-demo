# 集成GitHub和QQ社交登录
## 准备
1. [本机安装nginx](http://nginx.org/en/download.html)
2. [创建一个GitHub OAuth App](https://github.com/settings/developers)
3. [创建一个QQ网站应用](https://connect.qq.com)
## 开始
1. 将spring-security-oauth2-client工程`application.yml`文件中`registration.github`和`registration.qq`属性下的clientId、clientSecret替换为你自己的值
2. 将web工程下的`nginx.conf`文件添加到你的nginx配置下
3. 启动nginx和spring-security-oauth2-client工程，分别点击GitHub和QQ登录即可跳转三方授权登录页，最终在首页会显示认证的用户和客户端信息。
## 参考
[Spring-Security-OAuth2-Client原理](https://www.zyc.red/Spring/Security/OAuth2/OAuth2-Client/)
