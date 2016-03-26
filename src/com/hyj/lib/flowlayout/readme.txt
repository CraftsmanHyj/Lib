1、流式布局特点、应用场景
2、自定义ViewGroup需要重写：
	onMeasure：测量子View的宽、高，设置自己的宽、高；
	onLayout：设置子View的位置；

onMeasure：根据子View的布局文件，为子View设置测量模式和测量值；

测量=测量模式+测量值；来决定view的宽或高的值；
测量模式有三种：
	1、EXACTLY：传递过来的是精确值如100dp、match_parent；
	2、AT_MOST：当子View里面设置为wrap_content时为此种测量模式；由子View内部的形态来决定当前View的宽、高；
	3、UNSPCIFIED：(不常用)子View想要多大就给多大,一般出现在ScrollView上；
	
ViewGroup对应一个LayoutParams；
FlowLlayout使用MarginLayoutParams类型；
子View.getLayoutParams()得到的LayoutParams类型由父控件的LayoutParams类型决定；