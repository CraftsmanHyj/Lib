package com.hyj.lib.image_mt;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageHelper {
	public static final int PIXELS_NEGATIVE = 0X001;// 处理底片效果
	public static final int PIXELS_OLDPHOTO = 0X002;// 老照片效果
	public static final int PIXELS_RELIEF = 0X003;// 浮雕效果

	/**
	 * 对图片色相、饱和度、亮度进行调整
	 * 
	 * @param bmpOld
	 *            原图片
	 * @param hue
	 *            色相
	 * @param saturation
	 *            饱和度
	 * @param lum
	 *            亮度
	 */
	public static Bitmap handleImageEffect(Bitmap bmpOld, float hue,
			float saturation, float lum) {
		// 用原图创建一个可以读写的图片
		Bitmap bmpNew = Bitmap.createBitmap(bmpOld.getWidth(),
				bmpOld.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpNew);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		// 设置色相
		ColorMatrix hueMatrix = new ColorMatrix();
		hueMatrix.setRotate(0, hue);// R
		hueMatrix.setRotate(1, hue);// G
		hueMatrix.setRotate(2, hue);// B

		// 设置饱和度
		ColorMatrix saturationMatrix = new ColorMatrix();
		saturationMatrix.setSaturation(saturation);

		// 设置亮度
		ColorMatrix lumMatrix = new ColorMatrix();
		lumMatrix.setScale(lum, lum, lum, 1);

		// 将三个属性合成
		ColorMatrix imageMatrix = new ColorMatrix();
		imageMatrix.postConcat(hueMatrix);
		imageMatrix.postConcat(saturationMatrix);
		imageMatrix.postConcat(lumMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
		canvas.drawBitmap(bmpOld, 0, 0, paint);
		return bmpNew;
	}

	/**
	 * 用像素矩阵让图片达到底片之类的效果
	 * 
	 * @param bmpOld
	 * @return
	 */
	public static Bitmap handleImageNegative(Bitmap bmpOld, int effect) {
		int width = bmpOld.getWidth();
		int height = bmpOld.getHeight();

		Bitmap bmpNew = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		// 获取原照片上的像素矩阵
		int[] pixelsOld = new int[width * height];
		bmpOld.getPixels(pixelsOld, 0, width, 0, 0, width, height);

		// 处理过后的新像素矩阵
		int[] pixelsNew = new int[width * height];

		int pixels, r, g, b, a;
		for (int i = 0; i < pixelsOld.length; i++) {
			// 假如是浮雕效果只需要算到pixelsOld.length-1即可
			if (effect == PIXELS_RELIEF && i == pixelsOld.length - 1) {
				break;
			}

			pixels = pixelsOld[i];

			r = Color.red(pixels);
			g = Color.green(pixels);
			b = Color.blue(pixels);
			a = Color.alpha(pixels);

			switch (effect) {
			case PIXELS_NEGATIVE:// 底片效果
				// 根据公式获取处理后的像素点
				r = 255 - r;
				g = 255 - g;
				b = 255 - b;
				break;

			case PIXELS_OLDPHOTO:// 老照片效果
				r = (int) (0.393 * r + 0.769 * g + 0.189 * b);
				g = (int) (0.349 * r + 0.686 * g + 0.168 * b);
				b = (int) (0.272 * r + 0.534 * g + 0.131 * b);
				break;

			case PIXELS_RELIEF:// 浮雕效果
				int r1 = Color.red(pixelsOld[i + 1]);
				int g1 = Color.green(pixelsOld[i + 1]);
				int b1 = Color.blue(pixelsOld[i + 1]);

				r = r - r1 + 127;
				g = g - g1 + 127;
				b = b - b1 + 127;
				break;
			}

			r = r > 255 ? 255 : (r < 0 ? 0 : r);
			g = g > 255 ? 255 : (g < 0 ? 0 : g);
			b = b > 255 ? 255 : (b < 0 ? 0 : b);

			pixelsNew[i] = Color.argb(a, r, g, b);// 将处理过后的值合成新的像素点
		}

		bmpNew.setPixels(pixelsNew, 0, width, 0, 0, width, height);
		return bmpNew;
	}
}
