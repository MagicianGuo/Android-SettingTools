# 设置工具

通过Shizuku授权，可以实现静默安装应用、修改系统设置项（WRITE_SECURE_SETTINGS）、修改屏幕分辨率和像素密度。
修改屏幕刷新率会提示需要安装targetSdk<23的插件，高版本无法修改“Settings.System”里面的设置项。（部分设备可能不支持修改屏幕刷新率，或者屏幕刷新率改为自适应模式才能生效）

**注意！！！** 运行代码时请先编译 **SettingToolsPlugin** 模块，使其生成的安装包自动复制到 **SettingTools** 模块的 *assets* 文件夹，然后运行 **SettingTools** 模块。