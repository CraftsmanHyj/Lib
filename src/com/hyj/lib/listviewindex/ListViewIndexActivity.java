package com.hyj.lib.listviewindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.hyj.lib.R;

public class ListViewIndexActivity extends Activity {
	private ArrayList<String> mItems;
	private ListViewIndex mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listviewindex_main);

		mItems = new ArrayList<String>();
		mItems.add("Ateve Jobs");
		mItems.add("Ateve Jobs");
		mItems.add("Bteve Jobs");
		mItems.add("Cteve Jobs");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Eteve Jobs");
		mItems.add("Fteve Jobs");
		mItems.add("Gteve Jobs");
		mItems.add("Steve Jobs");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("11/22/63: A Novel");
		mItems.add("The Hunger Games");
		mItems.add("The LEGO Ideas Book");
		mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Death Comes to Pemberley");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
		mItems.add("Steve Jobs");
		mItems.add("Inheritance (The Inheritance Cycle)");
		mItems.add("11/22/63: A Novel");
		mItems.add("The Hunger Games");
		mItems.add("The LEGO Ideas Book");
		mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Catching Fire (The Second Book of the Hunger Games)");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Flder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ilder Scrolls V: Skyrim: Prima Official Game Guide");

		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Ylder Scrolls V: Skyrim: Prima Official Game Guide");

		mItems.add("Zlder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Zlder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Zlder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Zlder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Zlder Scrolls V: Skyrim: Prima Official Game Guide");
		mItems.add("Death Comes to Pemberley");

		Collections.sort(mItems);

		// a中v e零fw　f阳ewef32323ff
		new SortSpell("中华人民共和国 fewfwf　dfewfw 2 fewf333");

		ContentAdapter adapter = new ContentAdapter(this,
				android.R.layout.simple_list_item_1, mItems);

		mListView = (ListViewIndex) findViewById(R.id.indexLv);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
	}

	private class ContentAdapter extends ArrayAdapter<String> implements
			SectionIndexer {

		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public ContentAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public int getPositionForSection(int section) {
			// 如果当前部分没有item，则之前的部分将被选择
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							// 字符串第一个字符与1~9之间的数字进行匹配
							if (StringMatcher.match(
									String.valueOf(getItem(j).charAt(0)),
									String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.match(
								String.valueOf(getItem(j).charAt(0)),
								String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		/**
		 * 获取右侧索引数组
		 */
		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++) {
				sections[i] = String.valueOf(mSections.charAt(i));
			}
			return sections;
		}
	}
}
