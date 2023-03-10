---
sidebar_position: 2
---


# 开关管理

FeatureProbe平台提供了强大的功能开关管理模块，功能开关通过选择目标流量，进行功能投放，通过持续观测数据逐步放量直到全量部署。
## 开关仪表盘 

![toggles screenshot](/toggles_zh.png)

1. 默认展示My First Project的online环境的开关列表信息
2. 左侧导航栏提供了快速切换环境的入口（点击环境右侧的下拉icon）
3. 通过筛选条件，我们可以根据"evaluated","enabled/disabled","tags","name/key/description"对开关进行快速的筛选

## 创建开关
开关创建成功后，将同步到项目下的所有环境

![create toggle screenshot](/create_toggle_zh.png)

1. 填写开关名称
2. 填写开关的key（开关的唯一性标识，同项目下唯一，一旦创建不可编辑）
3. 填写描述信息
4. 选择标签（无初始值，可自行创建）
5. 选择sdk类型
6. 选择开关的return type（支持4种：Boolean、String、Number、JSON），一旦创建不可编辑
7. 填写Variations
    - 默认两个variations（最少2个，可自行增减）

8. 填写当开关未生效（开关未生效时的返回值），默认同步variation1的数据，可更改
9. 选择“是否是永久性开关？”（非永久性开关的默认周期是30天，超过30天我们会提醒您清理）
10. 点击创建按钮，完成开关的创建

## 编辑开关
编辑成功后，将在整个项目下的所有环境内生效。

![edit toggle screenshot](/edit_toggle.png)

## 下线及恢复开关

![archived toggle screenshot](/archived_toggle.png)

开关的下线：
1. 点击“下线”可随时下线开关，下线后，不可编辑及发布，且该开关将展示在【下线开关列表】中
2. 点击“查看下线开关”，可看到项目的所有被下线开关，点击“返回”按钮，可返回到到正在线上使用的开关

开关的恢复上线：
1. 点击“恢复”，可以将该开关恢复上线，恢复后，该开关将展示在【开关列表】中


