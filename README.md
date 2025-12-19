# 联想笔记本商城项目 2025软件工程

## 后端

框架：Spring Boot+Maven+MyBatis

安装Maven 3.8.8  jdk1.8 IDE: IntelliJ IDEA

IDEA打开后端，安装相关依赖，修改application-dev.yml中数据库配置。运行。


## 数据库

数据库环境：mysql5.7

进入Mysql环境后运行SOURCE lenovoshop/sql/mysql/sqlnew.sql建立本项目数据库，然后运行sql.py（注意配置数据库部分先修改为自己的数据库密码）插入数据项。


## 前端

框架：Vue-cli+node.JS+Elmentui

安装node.js (v14.21.3)  vue  IDE：vscode

初次运行先在终端输入npm run build，之后直接npm run serve。

前后端均运行后，打开前端显示的网页进入。


## 主要功能要求

1、用户：注册，找回密码；

2、商品：按型号、价格等查询，浏览显示，显示详情，购买；

3、购物车：加减商品，统计金额，取消订单；

4、后台管理：用户管理、商品管理、会员管理、优惠促销管理等；

暂未实现：找回密码（邮件+token?）

增加：公告功能 评价管理 首页商品分类

12.20修改：增加了优惠促销设计，修改了商家端任何人都可以注册的问题，只允许使用内置账号admin密码123456登录。