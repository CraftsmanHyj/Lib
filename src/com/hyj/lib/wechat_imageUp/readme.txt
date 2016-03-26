仿微信图片上传所要达到的目标：
1、避免内存溢出
	a、根据图片显示大小去压缩图片
	b、使用缓存对图片进行管理(LruCache对图片缓存)
	
2、用户操作UI控件必须流畅
	a、getView中不做耗时操作(异步加载+回调显示)
	
3、用户预期显示的图片尽可能快(图片加载策略选择)
	LIFO(last in first out)
	
ImageLoader加载、管理图片
getView(){
	url→BitMap
	url→LurCcahe查找
		→找到返回
		→找不到 url→Task→TaskQueue且
		发送一个通知去提醒后台轮询线程
}

Task→run(){
	根据url加载图片：
		1、获得图片大小
		2、用options对图片进行压缩
		3、加载图片且放入LruCache中
}

后台轮询线程
TaskQueue→Task→线程池去执行
Handler+Looper+Message(Android异步消息处理框架)