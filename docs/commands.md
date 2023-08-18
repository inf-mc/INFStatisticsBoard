# 命令
这个mod的所有命令均以infboard开头

## switch
```
/infboard switch [计分项]
```
切换当前显示的计分项，如果计分项缺省则在聊天框输出所有计分项，点击对应计分项即可快速切换。

## setDisplayName
```
/infboard setDisplayName <计分项> <显示名称>
```
需要权限等级: 1<br />
设置计分项显示名称

## fakePlayerPrefix
```
/infboard fakePlayerPrefix
```
查看当前假人前缀配置信息

```
/infboard fakePlayerPrefix switch <开关: bool>
```
需要权限等级: 1<br />
是否打开假人前缀功能

```
/infboard fakePlayerPrefix set <开关: bool>
```
需要权限等级: 1<br />
设置假人前缀

## miningAreaBlackList
```
/infboard miningAreaBlackList list
```
列出挖掘榜黑名单区域

```
/infboard miningAreaBlackList add <纬度> <起始坐标> <结束坐标>
```
需要权限等级: 1<br />
添加黑名单区域

```
/infboard miningAreaBlackList remove <序号: int>
```
需要权限等级: 1<br />
移除黑名单区域

## miningAreaWhiteList
挖掘榜白名单，操作同挖掘榜黑名单

## defaultMiningAreaTypeIsBlackList
```
/infboard defaultMiningAreaTypeIsBlackList [开关: bool]
```
若一个方块不在miningAreaBlackList和miningAreaWhiteList中，或同时在两个中，是否不计算该值，缺省为查看当前取值
