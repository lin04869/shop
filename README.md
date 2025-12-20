# 联想笔记本商城项目 2025软件工程

## 安装使用说明

### 1. 环境准备
*   JDK 1.8
*   MySQL 5.7
*   Node.js 14.21.3
*   Maven 3.8.8
*   IDE： IntelliJ IDEA（后端） & VS Code（前端）

### 2.数据库配置
1.  登录本地MYSQL。
2.  执行脚本 `SOURCE sql/sqlnew.sql` 建表。
3.  运行 `sql/sql.py` 导入初始数据（注意配置数据库部分先修改为自己的数据库密码）。

### 3.后端启动
1.  使用IDEA打开 `后端/LenovoShop` 目录。
2.  修改 `src/main/resources/application.yml`：
    *   更新 `spring.datasource.password` 为你的数据库密码。
3.  运行 `LenovoShopApplication.java`，服务启动在端口 `9090`。

### 4. 前端启动
1.  使用 VS Code 打开 `前端/vue-lenovo` 目录。
2.  终端运行`npm run build`安装依赖。
3.  运行 `npm run serve` 启动前端，访问 `http://localhost:8080`。



## 主要功能要求

1、用户：注册，找回密码；

2、商品：按型号、价格等查询，浏览显示，显示详情，购买；

3、购物车：加减商品，统计金额，取消订单；

4、后台管理：用户管理、商品管理、会员管理、优惠促销管理等；

增加：公告功能 评价管理 首页商品分类

12.20修改：
1. 增加了优惠促销设计；
2. 修改了商家端注册逻辑，只允许使用内置账号admin（密码123456）登录；
3. 实现了找回密码功能（基于邮箱验证码）。


<!-- ## 后端

框架：Spring Boot+Maven+MyBatis

安装Maven 3.8.8  jdk1.8 IDE: IntelliJ IDEA

IDEA打开后端，安装相关依赖，修改application-dev.yml中数据库配置。运行。


## 数据库

数据库环境：mysql5.7

进入Mysql环境后运行SOURCE lenovoshop/sql/mysql/sqlnew.sql建立本项目数据库，然后运行sql.py（注意配置数据库部分先修改为自己的数据库密码）插入数据项。


## 前端

框架：Vue-cli+Node.js+Element UI

安装node.js (v14.21.3)  vue  IDE：vscode

初次运行先在终端输入npm run build，之后直接npm run serve。

前后端均运行后，打开前端显示的网页进入。 -->