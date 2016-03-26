所要达到的目的：
	1、自由的放大、缩小
	2、双击放大、缩小
	3、放大以后可以尽心自由的移动
	4、处理与ViewPager之间的事件冲突
	
所用到的知识点：
	1、Matrix
	2、ScaleGestureDetector 多指触控缩放手势
	3、GestureDetector
	4、事件分发机制
	
代码实现：
	1、复写ImageView实现效果
	
-------------------------------------	
小结
ZommImageView extnds ImageView 
OnGlobalLayoutListener 在此接口中拿控件宽、高在oncreate里面是不可能获取到宽、高的，而在这个接口的文件里面可以获取到
OnAttachedToWindow 注册接口
onDetachedFromWindow 移除接口

ScaleGestureDetector	多点触控接口，
onScale detector
-------------------------------------

自由移动

-------------------------------------
双击放大/缩小
GestureDectector
postDelay + Runnable
-------------------------------------
与ViewPager的结合
放大以后与ViewPager的左右滑动冲突
判断冲突发生地方及原因：ViewPager屏蔽了子View的左右移动事件
处理：在DOWN、MOVE中，如果宽/高大于屏幕宽/高，则请求不被屏蔽getParent().requestDisallowInterceptTouchEvent(true);
-------------------------------------