# 使用jwt令牌访问受保护的资源
## 准备
1. 使用[JwtUtil](https://github.com/Allurx/spring-security-oauth2-demo/blob/master/spring-security-oauth2-resourceserver/src/main/java/red/zyc/spring/security/oauth2/resourceserver/JwtUtil.java)
的`keys`方法生成RSA公钥和私钥，然后分别保存为key.private和key.public文件存放到工程的resource文件夹下
2. 使用[JwtUtil](https://github.com/Allurx/spring-security-oauth2-demo/blob/master/spring-security-oauth2-resourceserver/src/main/java/red/zyc/spring/security/oauth2/resourceserver/JwtUtil.java)
的`jwt`方法生成一个jwt令牌

这里的1、2两个步骤在实际使用时应该由授权服务器来执行，即授权服务器生成jwt令牌返回给客户端。这里我们为了方便演示，所以手动模拟了授权服务器生成令牌的过程。
## 开始
启动工程，在请求头中带上刚刚生成的jwt令牌访问`localhost:8080/`
```
Authorization: Bearer jwt令牌
```
即可在控制台看到此jwt令牌所携带的用户信息
## 参考
[Spring-Security-OAuth2-Resource-Server原理](https://www.zyc.red/Spring/Security/OAuth2/OAuth2-Resource-Server/)
