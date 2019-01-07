# FloatWindow 安卓任意界面悬浮窗(原项目地址如下：https://github.com/yhaolpz/FloatWindow)

[![](https://jitpack.io/v/yhaolpz/FloatWindow.svg)](https://jitpack.io/#yhaolpz/FloatWindow)

![悬浮按钮图](https://raw.githubusercontent.com/yhaolpz/FixedFloatWindow/master/slide.gif)

因为我刚好需要用到，但作者已不更新修复bug，只能自己干了

修复bug如下：
(每一点最后面的#xx是原项目的issues对应的楼层)

1.修复支持输入事件，在build之前加入.setChildViewTouchable(true)(#81等)

2.修复hide()之后重进又显示的问题(#64等)

3.修复拖动之后点击事件偶尔无法触发(#84)

4,修复横竖屏切换之后位置错误的问题(#56)

其他优化：继承AppCompatActivity(#85),降低minSdkVersion为17(#68,#62)

使用建议：将项目download下来导入as，方便修改如minSdkVersion等问题，也可以删除自己不需要的代码，减少冗余(很多第三方库都是这样，功能一堆，但是你就用那一两个)

不想修复的问题：权限等适配问题。。。最烦的东西，个人觉得适配的唯一难点就是找到对应的定制rom的方法，另外也没有那么多手机去测，一句话：有反馈，再适配

没有修复的简单问题：悬浮窗滑出屏幕问题。其实说到底也是个适配问题，有的手机下面有虚拟按键栏，解决方法也简单：IFloatWindowImpl类的触摸事件的ACTION_UP里，获取一下view的坐标，如果为负，证明超出屏幕了，有底部按键栏的就做一下加减法呗。再updateX/Y/XY()更新view到屏幕里。

最后感谢原作者及帮助原作者修复bug的所有老铁