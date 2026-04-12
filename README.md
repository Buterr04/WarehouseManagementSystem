<div align="center">
  <h1>WarehouseManagementSystem</h1>
  <p>物流信息系统课程设计项目</p>
  <p>
    <a href="https://github.com/Buterr04/WarehouseManagementSystem/releases">
      <img src="https://img.shields.io/github/release/Buterr04/WarehouseManagementSystem.svg" alt="Release" />
    </a>
    <a href="https://github.com/Buterr04/WarehouseManagementSystem/blob/main/LICENSE">
      <img src="https://img.shields.io/github/license/Buterr04/WarehouseManagementSystem.svg" alt="License" />
    </a>
    <a href="https://github.com/Buterr04/WarehouseManagementSystem/issues">
      <img src="https://img.shields.io/github/issues/Buterr04/WarehouseManagementSystem?color=F48D73" alt="Issues" />
    </a>
    <img 
    src="https://img.shields.io/badge/IDE-IntelliJ%20IDEA-blue?logo=intellijidea&logoColor=ffffff" alt="IDE" />
    <img src="https://img.shields.io/badge/Language-Java%20%26%20JavaScript-orange?logo=java&logoColor=ffffff" alt="Language" />
    <img src="https://img.shields.io/badge/Framework-Spring%20Boot-green?logo=spring&logoColor=ffffff" alt="Framework" />
    <img src="https://img.shields.io/badge/Framework-Vue%203-green?logo=vuedotjs&logoColor=ffffff" alt="Framework" />
    <img src="https://img.shields.io/badge/Framework-Element%20Plus-green?logo=element&logoColor=ffffff" alt="Framework" />
    <img src="https://img.shields.io/badge/Database-MySQL-blue?logo=mysql&logoColor=ffffff" alt="Database" />
    <img src="https://img.shields.io/badge/Build-Maven-red?logo=apachemaven&logoColor=ffffff" alt="Build" />
    
  </p>
  <img src="https://socialify.git.ci/Buterr04/WarehouseManagementSystem/image?issues=1&language=1&name=1&owner=1&stargazers=1&theme=Light" alt="WarehouseManagementSystem" />
</div>

## 项目背景
此项目为物流信息系统课程设计项目，项目假设服务于一数码产品线下实体经销商，为该商户提供进销存管理业务

包括流程：销售、库存、采购

包括单据：发货（实际为线下销售发票）、销售、出库、采购申请、采购、入库

符合课程要求：三流程六单据

## 使用工具
BACKEND: Springboot + Mybatis_Plus + Maven  
FRONTEND: VUE 3 + Element_Plus 3 + Router

## 系统需求
JDK 17  
VUE 3  
MySQL Database

## 参考  
[课程](https://www.bilibili.com/video/BV1Qe411V7TZ?p=1&vd_source=8d5101c64c259c4a82f174c9da33f943)  
Works with Google Gemini and OpenAI ChatGPT

## 📜 License

本项目遵循 [MIT](https://opensource.org/licenses/MIT) 开源协议。

## 🚀 快速上手

1.  **克隆仓库:**

    ```bash
    git clone <仓库地址>
    cd <项目名称>
    ```
    
    
2.  **安装依赖:**

    ```bash
    cd wms-front
    npm install
    # 或者
    yarn add
    ```
3.  **运行后端项目**

    在IDEA或者任何你熟悉的IDE中打开```wms```文件夹作为maven项目文件夹

    使用```WMS.db```在本地创建数据库

    修改```application.yml```符合本地数据库设置
    
    选择```WmsApplication.java```运行

    不要忘记刷新```Maven```依赖

    
5.  **启动开发服务器:**

    ```bash
    npm run serve
    # 或者
    yarn serve
    ```

    然后，在你的浏览器中打开 `http://localhost:8080`

    默认用户信息存储在```user.db```中，你也可以自己在数据库中创建用户信息
