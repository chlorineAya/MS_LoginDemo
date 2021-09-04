# MS_LoginDemo
此demo使用开源项目[Ratsiiel/minecraft-auth-library](https://github.com/Ratsiiel/minecraft-auth-library)写成程序用于直观展示免网页登录微软账号的过程。  
## 注意
1. 由于我正在开发Android上的minecraft java版启动器，所以此demo是Android项目，但是如果您在开发PC启动器需要使用，登录方法与Android widget无关。  
2. 此LoginActivity项目是Intellij IDEA自动生成的，花里胡哨的代码很多，不需要管，核心就在Activity里Button的点击监听事件。  
## 调用方法
### 微软/Xbox
请先调用异步线程，再在线程中使用  
```java
MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox("EMAIL", "PASSWORD");
MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
```
### 麻将（2022年以后不再支持）
同样需要异步线程  
```java
MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
MinecraftToken minecraftToken = minecraftAuthenticator.login("EMAIL", "PASSWORD");
MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
```
