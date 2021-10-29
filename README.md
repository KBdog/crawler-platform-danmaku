## 实时抓取各大直播平台的弹幕的简单springboot项目
上班摸鱼写的，闲着蛋疼

### 使用技术或依赖
selenium、springboot、poi、chromedriver

### 可抓取的直播平台
斗鱼、虎牙、bilibili、抖音

### 配置相关
1. 访问 [https://chromedriver.chromium.org/downloads](https://chromedriver.chromium.org/downloads) 找到对应chrome浏览器版本下载谷歌驱动`chromedriver`
2. 在配置文件中修改相关配置
3. `application.yml`

```yml
server:
  port: 8080
local:
  proxy:
    #是否使用代理
    using: false
    #代理ip端口
    host: 127.0.0.1
    port: 1080
  chrome:
    #谷歌驱动本地路径
    chromeDriverPath: C:\Users\Lenovo\Desktop\chrome_driver\chromedriver.exe
crawler:
  #爬取时间:单位为分钟
  time: 30
  #导出相关
  export:
    #抓取到的弹幕导出excel路径
    filePath: C:\Users\Lenovo\Desktop\export
  #导出报表 / 打印日志显示日期格式
  timeFormat: 'yyyy-MM-dd_HH-mm-ss'

```

### 各个平台抓取的API

#### 斗鱼
GET `/crawler/danmaku/douyu`

| 参数 |       描述        |             例子              |
| :--: | :---------------: | :---------------------------: |
| url  | 斗鱼直播间url地址 | https://www.douyu.com/1126960 |

#### 虎牙
GET `/crawler/danmaku/huya`

| 参数 |       描述        |        例子        |
| :--: | :---------------: | :----------------: |
| url  | 虎牙直播间url地址 | https://huya.com/s |

#### bilibili
GET `/crawler/danmaku/bilibili`

| 参数 |       描述       |                例子                |
| :--: | :--------------: | :--------------------------------: |
| url  | b站直播间url地址 | https://live.bilibili.com/22637261 |

#### 抖音
GET `/crawler/danmaku/douyin`

| 参数 |          描述           |                 例子                 |
| :--: | :---------------------: | :----------------------------------: |
| url  | 抖音网页端直播间url地址 | https://live.douyin.com/392228830217 |

#### 截断或停止当前爬虫任务
GET `/crawler/danmaku/shutdown`