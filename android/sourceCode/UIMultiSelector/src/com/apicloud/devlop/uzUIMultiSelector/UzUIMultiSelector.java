package com.apicloud.devlop.uzUIMultiSelector;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.apicloud.devlop.uzUIMultiSelector.JsParamsUtil.Item;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class UzUIMultiSelector extends UZModule {

	private UZModuleContext mModuleContext;
	private JsParamsUtil mJsParamsUtil;
	private LinearLayout mSelectorLayout;
	private View mMaskView;
	private JSONObject mStyles;
	private JSONObject mIcon;
	private JSONObject mItem;
	private ListView mListView;
	private ListAdapter mListAdapter;
	private List<Item> mItems;
	private JSONArray mItemsJson;
	private String mIconAlign;
	private Drawable mItemNormal;
	private Drawable mItemHighlight;
	private Drawable mItemSelectedDrawable;
	private Drawable mIconNormal;
	private Drawable mIconHighlight;
	private Drawable mIconSelectedDrawable;
	private int mMaxSelected;
	private ImageView mCurrentIcon;
	private View mCurrentItem;
	private TextView mCurrentTxt;
	private int mCurrentPostion = -1;
	private int mItemTxtNormal;
	private int mItemTxtHighlight;
	private int mItemTxtSelected;
	private boolean mSingleSelection;

	public UzUIMultiSelector(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_open(UZModuleContext moduleContext) {
		if (mSelectorLayout == null) {
			mModuleContext = moduleContext;
			mJsParamsUtil = JsParamsUtil.getInstance();
			firstOpen();
		} else {
			open();
		}
	}

	public void jsmethod_show(UZModuleContext moduleContext) {
		if (mSelectorLayout != null) {
			if (mJsParamsUtil.animation(mModuleContext)) {
				openWithAnim();
			} else {
				mMaskView.setVisibility(View.VISIBLE);
				mSelectorLayout.setVisibility(View.VISIBLE);
			}
			simpleCallBack("show");
		}
	}

	public void jsmethod_hide(UZModuleContext moduleContext) {
		hide();
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		if (mSelectorLayout != null) {
			if (mJsParamsUtil.animation(mModuleContext)) {
				hideWithAnim(true);
			} else {
				clean();
			}

		}
	}

	private void hide() {
		if (mSelectorLayout != null) {
			if (mJsParamsUtil.animation(mModuleContext)) {
				hideWithAnim(false);
			} else {
				mMaskView.setVisibility(View.GONE);
				mSelectorLayout.setVisibility(View.GONE);
			}
		}
	}

	private void clean() {
		removeViewFromCurWindow(mSelectorLayout);
		removeViewFromCurWindow(mMaskView);
		mSelectorLayout = null;
	}

	@Override
	protected void onClean() {
		super.onClean();
		clean();
	}

	private void firstOpen() {
		initStyles();
		initItems();
		initViews();
	}

	private void open() {
		if (mJsParamsUtil.animation(mModuleContext)) {
			openWithAnim();
		} else {
			mMaskView.setVisibility(View.VISIBLE);
			mSelectorLayout.setVisibility(View.VISIBLE);
		}
		simpleCallBack("show");
	}

	private void initStyles() {
		mStyles = mJsParamsUtil.styles(mModuleContext);
		mIcon = mJsParamsUtil.icon(mStyles);
		mIconAlign = mJsParamsUtil.iconAlign(mIcon);
		mItem = mJsParamsUtil.item(mStyles);
		mItemNormal = getItemNormalBg();
		mItemHighlight = getItemHighLightBg();
		mItemSelectedDrawable = getItemActiveBg();
		mIconNormal = getIconNormalBg();
		mIconHighlight = getIconHighLightBg();
		mIconSelectedDrawable = getIconActiveBg();
		mMaxSelected = mJsParamsUtil.max(mModuleContext);
		mSingleSelection = mJsParamsUtil.singleSelection(mModuleContext);
		if (mSingleSelection) {
			mMaxSelected = 1;
		}

		mItemTxtNormal = mJsParamsUtil.itemTxtColor(mItem);
		mItemTxtHighlight = mJsParamsUtil.itemTxtHighlightColor(mItem);
		mItemTxtSelected = mJsParamsUtil.itemTxtActiveColor(mItem);
	}

	private void initItems() {
		mItems = mJsParamsUtil.items(mModuleContext);
		if (mMaxSelected > 0) {
			mItems.remove(0);
		}
		mItemsJson = mJsParamsUtil.itemsJson(mModuleContext);
		mListAdapter = new ListAdapter();
	}

	private void initViews() {
		initMaskView();
		initSelectorLayout();
		initTitle();
		initListView();
		if (mJsParamsUtil.animation(mModuleContext)) {
			openWithAnim();
		}
		simpleCallBack("show");
	}

	private void initMaskView() {
		mMaskView = new View(mContext);
		mMaskView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (mJsParamsUtil.maskClose(mModuleContext)) {
						if (mJsParamsUtil.animation(mModuleContext)) {
							hideWithAnim(true);
						} else {
							clean();
						}
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					callBack("clickMask");
					break;
				}
				return true;
			}
		});
		insertViewToCurWindow(mMaskView, maskLayout());
	}

	@SuppressWarnings("deprecation")
	private LayoutParams maskLayout() {
		LayoutParams maskLayout = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		String mask = mJsParamsUtil.mask(mStyles);
		if (mJsParamsUtil.isBitmap(mask)) {
			mMaskView.setBackgroundDrawable(new BitmapDrawable(mJsParamsUtil
					.maskBitmap(mask, this)));
		} else {
			mMaskView.setBackgroundColor(mJsParamsUtil.maskColor(mask));
		}
		return maskLayout;
	}

	private void initSelectorLayout() {
		int pullLayoutId = UZResourcesIDFinder
				.getResLayoutID("mo_ui_muti_selector");
		Activity act = (Activity) mContext;
		LayoutInflater layoutInflater = act.getLayoutInflater();
		mSelectorLayout = (LinearLayout) layoutInflater.inflate(pullLayoutId,
				null);
		mSelectorLayout.setBackgroundColor(Color.WHITE);
		insertViewToCurWindow(mSelectorLayout, selectorLayout());
	}

	private LayoutParams selectorLayout() {
		int h = mJsParamsUtil.h(mModuleContext);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				h);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		return layoutParams;
	}

	private void initTitle() {
		RelativeLayout titleLayout = (RelativeLayout) mSelectorLayout
				.findViewById(UZResourcesIDFinder.getResIdID("title"));
		JSONObject title = mJsParamsUtil.title(mStyles);
		initTitleParam(titleLayout, title);
		initTitleBg(titleLayout, title);
		initTitleTxt(titleLayout, title);
		initLeftBtn(titleLayout);
		initRightBtn(titleLayout);
	}

	private void initTitleParam(RelativeLayout titleLayout, JSONObject title) {
		LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) titleLayout
				.getLayoutParams();
		titleParams.height = UZUtility.dipToPix(mJsParamsUtil.titleH(title));
		titleLayout.setLayoutParams(titleParams);
	}

	@SuppressWarnings("deprecation")
	private void initTitleBg(RelativeLayout titleLayout, JSONObject title) {
		String titleBg = mJsParamsUtil.titleBg(title);
		if (mJsParamsUtil.isBitmap(titleBg)) {
			titleLayout.setBackgroundDrawable(new BitmapDrawable(mJsParamsUtil
					.titleBitmap(titleBg, this)));
		} else {
			titleLayout.setBackgroundColor(mJsParamsUtil.titleBgColor(titleBg));
		}
	}

	private void initTitleTxt(RelativeLayout titleLayout, JSONObject title) {
		TextView titleTxt = (TextView) titleLayout
				.findViewById(UZResourcesIDFinder.getResIdID("title_txt"));
		titleTxt.setTextColor(mJsParamsUtil.titleColor(title));
		titleTxt.setTextSize(mJsParamsUtil.titleSize(title));
		titleTxt.setText(mJsParamsUtil.title(mModuleContext));
		titleTxt.setPadding(0, 0, 0, 0);
	}

	private void initLeftBtn(RelativeLayout titleLayout) {
		Button leftBtn = (Button) titleLayout.findViewById(UZResourcesIDFinder
				.getResIdID("leftBtn"));
		JSONObject leftButton = mJsParamsUtil.leftButton(mStyles);
		initLeftBtnParam(leftBtn, leftButton);
		initLeftBtnTxtStyle(leftBtn, leftButton);
		initLeftBtnBg(leftBtn, leftButton);
		leftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack("clickLeft");
			}
		});
	}

	private void initLeftBtnParam(Button leftBtn, JSONObject leftButton) {
		LayoutParams leftBtnParams = (LayoutParams) leftBtn.getLayoutParams();
		leftBtnParams.width = UZUtility.dipToPix(mJsParamsUtil
				.leftButtonW(leftButton));
		leftBtnParams.height = UZUtility.dipToPix(mJsParamsUtil
				.leftButtonH(leftButton));
		leftBtnParams.leftMargin = UZUtility.dipToPix(mJsParamsUtil
				.leftButtonMarginL(leftButton));
		leftBtnParams.topMargin = UZUtility.dipToPix(mJsParamsUtil
				.leftButtonMarginT(leftButton));
		leftBtn.setLayoutParams(leftBtnParams);
	}

	private void initLeftBtnTxtStyle(Button leftBtn, JSONObject leftButton) {
		leftBtn.setTextColor(mJsParamsUtil.leftButtonColor(leftButton));
		leftBtn.setTextSize(mJsParamsUtil.leftButtonSize(leftButton));
		leftBtn.setText(mJsParamsUtil.leftBtn(mModuleContext));
		leftBtn.setPadding(0, 0, 0, 0);
	}

	@SuppressWarnings("deprecation")
	private void initLeftBtnBg(Button leftBtn, JSONObject leftButton) {
		String leftBtnBg = mJsParamsUtil.leftButtonBg(leftButton);
		if (mJsParamsUtil.isBitmap(leftBtnBg)) {
			leftBtn.setBackgroundDrawable(new BitmapDrawable(mJsParamsUtil
					.leftButtonBitmap(leftBtnBg, this)));
		} else {
			int color = mJsParamsUtil.leftButtonBgColor(leftBtnBg);
			leftBtn.setBackgroundColor(color);
		}
	}

	@SuppressWarnings("deprecation")
	private void initRightBtn(RelativeLayout titleLayout) {
		Button rightBtn = (Button) titleLayout.findViewById(UZResourcesIDFinder
				.getResIdID("rightBtn"));
		LayoutParams rightBtnParams = (LayoutParams) rightBtn.getLayoutParams();
		JSONObject rightButton = mJsParamsUtil.rightButton(mStyles);
		rightBtnParams.width = UZUtility.dipToPix(mJsParamsUtil
				.rightButtonW(rightButton));
		rightBtnParams.height = UZUtility.dipToPix(mJsParamsUtil
				.rightButtonH(rightButton));
		rightBtnParams.rightMargin = UZUtility.dipToPix(mJsParamsUtil
				.rightButtonMarginR(rightButton));
		rightBtnParams.topMargin = UZUtility.dipToPix(mJsParamsUtil
				.rightButtonMarginT(rightButton));
		rightBtn.setLayoutParams(rightBtnParams);
		rightBtn.setTextColor(mJsParamsUtil.rightButtonColor(rightButton));
		rightBtn.setTextSize(mJsParamsUtil.rightButtonSize(rightButton));
		rightBtn.setText(mJsParamsUtil.rightBtn(mModuleContext));
		rightBtn.setPadding(0, 0, 0, 0);
		String rightBtnBg = mJsParamsUtil.leftButtonBg(rightButton);
		if (mJsParamsUtil.isBitmap(rightBtnBg)) {
			rightBtn.setBackgroundDrawable(new BitmapDrawable(mJsParamsUtil
					.rightButtonBitmap(rightBtnBg, this)));
		} else {
			rightBtn.setBackgroundColor(mJsParamsUtil
					.rightButtonBgColor(rightBtnBg));
		}

		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack("clickRight");
			}
		});
	}

	private void callBack(String eventType) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", eventType);
			ret.put("items", selectItems());
			mModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void simpleCallBack(String eventType) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", eventType);
			mModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONArray selectItems() {
		if (mItems != null && mItems.size() > 0) {
			JSONArray items = new JSONArray();
			JSONObject itemJson;
			Item item;
			int i = 0;
			if (mMaxSelected == 0) {
				i = 1;
			}
			for (; i < mItems.size(); i++) {
				item = mItems.get(i);
				if (item.status.equals("selected")
						|| item.status.equals("forever")) {
					if (mMaxSelected == 0) {
						itemJson = mItemsJson.optJSONObject(i - 1);
					} else {
						itemJson = mItemsJson.optJSONObject(i);
					}
					if (!itemJson.optString("status").equals("forever")) {
						try {
							itemJson.put("status", "selected");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					items.put(itemJson);
				}
			}
			return items;
		}
		return null;
	}

	private int getSelectedPostion() {
		Item item;
		int i = 0;
		for (; i < mItems.size(); i++) {
			item = mItems.get(i);
			if (item.status.equals("selected") || item.status.equals("forever")) {
				return i;
			}
		}
		return 0;
	}

	private void initListView() {
		mListView = (ListView) mSelectorLayout.findViewById(UZResourcesIDFinder
				.getResIdID("list"));
		mListView.setAdapter(mListAdapter);
		mListView.setDivider(new ColorDrawable(mJsParamsUtil
				.itemLineColor(mItem)));
		mListView.setDividerHeight(1);
		mListView.setSelection(getSelectedPostion());
		mListView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mCurrentIcon != null) {
					mCurrentIcon.setBackgroundDrawable(mIconNormal);
					mCurrentIcon = null;
				}
				if (mCurrentItem != null) {
					if (mCurrentPostion != -1) {
						if (mItems.get(mCurrentPostion).status.equals("normal")
								|| mItems.get(mCurrentPostion).status
										.equals("disable"))
							mCurrentItem.setBackgroundDrawable(mItemNormal);
						else {
							mCurrentItem
									.setBackgroundDrawable(mItemSelectedDrawable);
						}
						mCurrentItem = null;
					}
				}
				if (mCurrentTxt != null) {
					if (mCurrentPostion != -1) {
						if (mItems.get(mCurrentPostion).status
								.equals("forever")
								|| mItems.get(mCurrentPostion).status
										.equals("selected")) {
							mCurrentTxt.setTextColor(mItemTxtSelected);
						} else {
							mCurrentTxt.setTextColor(mItemTxtNormal);
						}
					}
					mCurrentTxt = null;
				}
				mCurrentPostion = -1;
				return false;
			}
		});
	}

	private boolean isSelectedMax() {
		if (mMaxSelected == 0) {
			return false;
		}
		if (mItems != null && mItems.size() > 1) {
			int j = 0;
			int i;
			if (mMaxSelected == 0) {
				i = 1;
			} else {
				i = 0;
			}
			for (; i < mItems.size(); i++) {
				if (mItems.get(i).status.equals("selected")
						|| mItems.get(i).status.equals("forever")) {
					j++;
					if (j >= mMaxSelected) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private int selectedNum() {
		if (mItems != null && mItems.size() > 1) {
			int j = 0;
			int i;
			if (mMaxSelected == 0) {
				i = 1;
			} else {
				i = 0;
			}
			for (; i < mItems.size(); i++) {
				if (mItems.get(i).status.equals("selected")
						|| mItems.get(i).status.equals("forever")) {
					j++;
				}
			}
			return j;
		}
		return 0;
	}

	private int disableNum() {
		if (mItems != null && mItems.size() > 1) {
			int j = 0;
			int i;
			if (mMaxSelected == 0) {
				i = 1;
			} else {
				i = 0;
			}
			for (; i < mItems.size(); i++) {
				if (mItems.get(i).status.equals("disable")) {
					j++;
				}
			}
			return j;
		}
		return 0;
	}

	private void selectAll(boolean isSelectAll) {
		int i = selectedNum();
		for (Item item : mItems) {
			if (isSelectAll) {
				if (item.status.equals("normal")) {
					if (i < mMaxSelected || mMaxSelected == 0) {
						item.status = "selected";
						i++;
					} else {
						simpleCallBack("overflow");
						break;
					}
				}
			} else {
				if (item.status.equals("selected")) {
					item.status = "normal";
				}
			}
		}
	}

	private void openWithAnim() {
		mMaskView.setVisibility(View.VISIBLE);
		mSelectorLayout.setVisibility(View.VISIBLE);
		TranslateAnimation translateAnimation = createAnim();
		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(500);
		mSelectorLayout.startAnimation(translateAnimation);
	}

	private TranslateAnimation createAnim() {
		int height = mJsParamsUtil.h(mModuleContext);
		height = UZUtility.dipToPix(height);
		return new TranslateAnimation(0, 0, height, 0);
	}

	public void hideWithAnim(final boolean isClose) {
		TranslateAnimation translateAnimation = createHideAnim();
		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(500);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mSelectorLayout.clearAnimation();
				if (isClose) {
					UzUIMultiSelector.this
							.removeViewFromCurWindow(mSelectorLayout);
					UzUIMultiSelector.this.removeViewFromCurWindow(mMaskView);
					mSelectorLayout = null;
				} else {
					mSelectorLayout.setVisibility(View.GONE);
					mMaskView.setVisibility(View.GONE);
				}
			}
		});
		mSelectorLayout.startAnimation(translateAnimation);
	}

	private TranslateAnimation createHideAnim() {
		int height = mJsParamsUtil.h(mModuleContext);
		height = UZUtility.dipToPix(height);
		TranslateAnimation translateAnimation;
		translateAnimation = new TranslateAnimation(0, 0, 0, height);
		return translateAnimation;
	}

	class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				int layoutID;
				if (mIconAlign.equals("left")) {
					layoutID = UZResourcesIDFinder
							.getResLayoutID("mo_ui_muti_selector_item_left");
				} else {
					layoutID = UZResourcesIDFinder
							.getResLayoutID("mo_ui_muti_selector_item_right");
				}
				convertView = layoutInflater.inflate(layoutID, parent, false);
				holder = new ViewHolder();
				holder.txt = (TextView) convertView
						.findViewById(UZResourcesIDFinder.getResIdID("txt"));
				holder.txt.setTextSize(mJsParamsUtil.itemTxtSize(mItem));
				holder.txt.setTextColor(mJsParamsUtil.itemTxtColor(mItem));
				String gravity = mJsParamsUtil.itemTxtAlign(mItem);
				if (gravity.equals("left")) {
					holder.txt.setGravity(Gravity.LEFT
							| Gravity.CENTER_VERTICAL);
				} else if (gravity.equals("right")) {
					holder.txt.setGravity(Gravity.RIGHT
							| Gravity.CENTER_VERTICAL);
				} else {
					holder.txt.setGravity(Gravity.CENTER);
				}
				holder.txt.setPadding(0, 0, 0, 0);
				holder.icon = (ImageView) convertView
						.findViewById(UZResourcesIDFinder.getResIdID("icon"));

				LinearLayout.LayoutParams iconParams = (LinearLayout.LayoutParams) holder.icon
						.getLayoutParams();
				iconParams.width = UZUtility.dipToPix(mJsParamsUtil
						.iconW(mIcon));
				iconParams.height = UZUtility.dipToPix(mJsParamsUtil
						.iconH(mIcon));
				int iconMarginT = mJsParamsUtil.iconMarginT(mIcon);
				if (iconMarginT == -1) {
					iconMarginT = (mJsParamsUtil.itemH(mItem) - mJsParamsUtil
							.iconH(mIcon)) / 2;
				}
				int iconMarginH = mJsParamsUtil.iconMarginH(mIcon);
				iconParams.setMargins(UZUtility.dipToPix(iconMarginH),
						UZUtility.dipToPix(iconMarginT),
						UZUtility.dipToPix(iconMarginH), 0);
				String iconBg = mJsParamsUtil.iconBg(mIcon);
				if (mJsParamsUtil.isBitmap(iconBg)) {
					convertView.setBackgroundDrawable(new BitmapDrawable(
							mJsParamsUtil.iconBitmap(iconBg,
									UzUIMultiSelector.this)));
				} else {
					convertView.setBackgroundColor(mJsParamsUtil
							.iconBgColor(iconBg));
				}

				holder.icon.setLayoutParams(iconParams);
				String bg = mJsParamsUtil.itemBg(mItem);
				if (mJsParamsUtil.isBitmap(bg)) {
					convertView.setBackgroundDrawable(new BitmapDrawable(
							mJsParamsUtil
									.itemBitmap(bg, UzUIMultiSelector.this)));
				} else {
					convertView.setBackgroundColor(mJsParamsUtil
							.itemBgColor(bg));
				}
				AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) convertView
						.getLayoutParams();
				layoutParams.height = UZUtility.dipToPix(mJsParamsUtil
						.itemH(mItem));
				convertView.setLayoutParams(layoutParams);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.icon.setOnTouchListener(new OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mCurrentIcon = holder.icon;
						holder.icon.setBackgroundDrawable(mIconHighlight);
						break;
					case MotionEvent.ACTION_MOVE:
						if (isInRect(holder.icon, event)) {
							holder.icon.setBackgroundDrawable(mIconHighlight);
						} else {
							holder.icon.setBackgroundDrawable(mIconNormal);
						}
						break;
					case MotionEvent.ACTION_UP:
						holder.icon
								.setBackgroundDrawable(mIconSelectedDrawable);
						holder.txt.setTextColor(mItemTxtSelected);
						onItemClick(position);
						break;
					}
					return true;
				}
			});
			Item item = mItems.get(position);
			final View itemView = convertView;
			itemView.setOnTouchListener(new OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mCurrentItem = itemView;
						mCurrentTxt = holder.txt;
						mCurrentPostion = position;
						itemView.setBackgroundDrawable(mItemHighlight);
						holder.txt.setTextColor(mItemTxtHighlight);
						break;
					case MotionEvent.ACTION_MOVE:
						if (isInRect(itemView, event)) {
							itemView.setBackgroundDrawable(mItemHighlight);
							holder.txt.setTextColor(mItemTxtHighlight);
						} else {
							itemView.setBackgroundDrawable(mItemNormal);
							holder.txt.setTextColor(mItemTxtNormal);
						}
						break;
					case MotionEvent.ACTION_UP:
						itemView.setBackgroundDrawable(mItemSelectedDrawable);
						holder.txt.setTextColor(mItemTxtSelected);
						onItemClick(position);
						break;
					}
					return true;
				}
			});
			if (item.status.equals("normal") || item.status.equals("disable")) {
				convertView.setBackgroundDrawable(mItemNormal);
				holder.icon.setBackgroundDrawable(mIconNormal);
				holder.txt.setTextColor(mItemTxtNormal);
			} else if (item.status.equals("selected")
					|| item.status.equals("forever")) {
				convertView.setBackgroundDrawable(mItemSelectedDrawable);
				holder.icon.setBackgroundDrawable(mIconSelectedDrawable);
				holder.txt.setTextColor(mItemTxtSelected);
			}
			holder.txt.setText(item.text);
			return convertView;
		}
	}

	class ViewHolder {
		TextView txt;
		ImageView icon;
	}

	private void onItemClick(int position) {
		if (mItems != null) {
			Item item = mItems.get(position);
			if (item.status.equals("normal")) {
				if (mSingleSelection) {
					singleSelect(item);
				}
				if (selectedNum() >= mMaxSelected && mMaxSelected != 0
						&& !mSingleSelection) {
					simpleCallBack("overflow");
				}
				if (!isSelectedMax() && !mSingleSelection) {
					item.status = "selected";
				}
				if (selectedNum() + disableNum() == mItems.size() - 1
						&& mMaxSelected == 0) {
					mItems.get(0).status = "selected";
				}
			} else if (item.status.equals("selected")) {
				item.status = "normal";
				if (position != 0 && mMaxSelected == 0) {
					mItems.get(0).status = "normal";
				}
			}
			if (position == 0 && mMaxSelected == 0) {
				selectAll(item.status.equals("normal") ? false : true);
			}
			callBack("clickItem");
			mListAdapter.notifyDataSetChanged();
		}
	}

	private void singleSelect(Item item) {
		if (isForever()) {
			return;
		}
		for (Item i : mItems) {
			if (i.status.equals("selected")) {
				i.status = "normal";
			}
		}
		item.status = "selected";
	}

	private boolean isForever() {
		for (Item i : mItems) {
			if (i.status.equals("forever")) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private Drawable getItemNormalBg() {
		String bg = mJsParamsUtil.itemBg(mItem);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.itemBitmap(bg, this));
		} else {
			return new ColorDrawable(mJsParamsUtil.itemBgColor(bg));
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getItemHighLightBg() {
		String bg = mJsParamsUtil.itemBgHighlight(mItem);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.itemHighlightBitmap(bg,
					this));
		} else {
			return new ColorDrawable(mJsParamsUtil.itemBgHighlightColor(bg));
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getItemActiveBg() {
		String bg = mJsParamsUtil.itemBgActive(mItem);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.itemActiveBitmap(bg, this));
		} else {
			return new ColorDrawable(mJsParamsUtil.itemBgActiveColor(bg));
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getIconNormalBg() {
		String bg = mJsParamsUtil.iconBg(mIcon);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.iconBitmap(bg, this));
		} else {
			return new ColorDrawable(mJsParamsUtil.iconBgColor(bg));
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getIconHighLightBg() {
		String bg = mJsParamsUtil.iconBgHighlight(mIcon);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.iconHighlightBitmap(bg,
					this));
		} else {
			return new ColorDrawable(mJsParamsUtil.iconBgHighlightColor(bg));
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getIconActiveBg() {
		String bg = mJsParamsUtil.iconBgActive(mIcon);
		if (mJsParamsUtil.isBitmap(bg)) {
			return new BitmapDrawable(mJsParamsUtil.iconActiveBitmap(bg, this));
		} else {
			return new ColorDrawable(mJsParamsUtil.iconBgActiveColor(bg));
		}
	}

	public StateListDrawable addStateDrawable(Drawable nomalDrawable,
			Drawable pressDrawable) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed }, pressDrawable);
		sd.addState(new int[] {}, nomalDrawable);
		return sd;
	}

	private boolean isInRect(View view, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float viewWidth = view.getWidth();
		float viewHeight = view.getHeight();
		if (x <= viewWidth && x >= 0 && y <= viewHeight && y >= 0) {
			return true;
		}
		return false;
	}

}
