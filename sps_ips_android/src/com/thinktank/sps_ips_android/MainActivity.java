package com.thinktank.sps_ips_android;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinktank.sps_ips_android.Model.Catch;
import com.thinktank.sps_ips_android.Model.GsmCell;
import com.thinktank.sps_ips_android.Model.Point;
import com.thinktank.sps_ips_android.Model.Wifi;

public class MainActivity extends ActionBarActivity {

	public static int numberSplit = 5;
	public static int numberSplitLines = 5;
	public static int numberSplitColumns = 5;

	static Point[][] splitLimits = null;

	private ViewPager pager;

	ActionBar mActionbar;

	private String selectedImagePath;

	protected static final int PICK_IMAGE = 1;

	public static int widthImage = 0;
	public static int heightImage = 0;

	public boolean isWifiEnabled = false;

	Uri selectedImageUri;

	public static int originalWImage = 0;
	public static int originaHImage = 0;

	public static int firstTimeTTmap = 0;

	private static String STORETEXT = "storetext.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mActionbar = getSupportActionBar();

		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		pager = (ViewPager) findViewById(R.id.pager);

		FragmentManager fm = getSupportFragmentManager();

		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionbar.setSelectedNavigationItem(position);
			}
		};

		pager.setOnPageChangeListener(pageChangeListener);

		FragmentsPagerAdapter fragmentPagerAdapter = new FragmentsPagerAdapter(
				fm);

		pager.setAdapter(fragmentPagerAdapter);

		mActionbar.setDisplayShowTitleEnabled(true);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
				MapFragment.textviewListForVisualize = null;
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction arg1) {
				pager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			}

		};

		Tab start_tab = mActionbar.newTab().setText("Home")
				.setTabListener(tabListener);
		mActionbar.addTab(start_tab);

		Tab map_tab = mActionbar.newTab().setText("Collect")
				.setTabListener(collectListener);
		mActionbar.addTab(map_tab);

		Tab visualize_data_tab = mActionbar.newTab().setText("Visualize")
				.setTabListener(tabListenerData);
		mActionbar.addTab(visualize_data_tab);

		Tab analysis_tab = mActionbar.newTab().setText("Analysis")
				.setTabListener(tabListenerAnalysis);
		mActionbar.addTab(analysis_tab);

		CatchDataBaseHandler db = new CatchDataBaseHandler(
				getApplicationContext());

		db.deleteAllCatchs();
		db.deleteAllWifis();
		db.deleteAllWifis_catch();
		db.deleteAllGsmCells();
		db.deleteAllGsmCells_catch();
	}

	ActionBar.TabListener tabListenerData = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction arg1) {
			ImageView imageMap = (ImageView) findViewById(R.id.imageMapVizualizedata);
			if (selectedImagePath != null) {
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
				tracer(R.id.imageMapVizualizedata,
						R.id.relativeLayoutVisualizeData, 1);
				count2(R.id.relativeLayoutVisualizeData, 1);
			} else {
				if (firstTimeTTmap == 0) {
					splitLimits = new Point[numberSplit + 1][4];
					System.out.println("initialisaatioon");
					tracer(R.id.imageMapdata, R.id.relativeLayoutVisualizeData,
							0);
					count2(R.id.relativeLayoutVisualizeData, 0);
					firstTimeTTmap++;
				} else {
					tracer(R.id.imageMapVizualizedata,
							R.id.relativeLayoutVisualizeData, 1);
					count2(R.id.relativeLayoutVisualizeData, 0);
				}

			}
			// getData();
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction arg1) {

			pager.setCurrentItem(tab.getPosition());
			if (selectedImagePath != null) {
				ImageView imageMap = (ImageView) findViewById(R.id.imageMapVizualizedata);
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
			}
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// MapFragment.textviewListForVisualize = null;
		}

		public void getData() {

			final CatchDataBaseHandler db = new CatchDataBaseHandler(
					getApplicationContext());

			List<Catch> catchs = db.getAllCatchs();

			for (final Catch ct : catchs) {

				ImageButton CatchButtonData = new ImageButton(
						getApplicationContext());

				RelativeLayout r2 = (RelativeLayout) findViewById(R.id.relativeLayoutVisualizeData);
				RelativeLayout.LayoutParams paramsdata = new RelativeLayout.LayoutParams(
						30, 30);

				final int coordX = (int) ct.getX();
				final int coordY = (int) ct.getY();

				CatchButtonData.setImageResource(R.drawable.bluebutton);
				CatchButtonData.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						List<Wifi> OnePositionWifisList = db
								.getAllWifisByCatch(ct.getId_catch());

						Spanned formattedText;

						CharSequence sequenceMsg;

						String messageToDisplay = "WIFI informations \n";

						formattedText = Html
								.fromHtml("<font color='#7FC6BC'><b>"
										+ messageToDisplay + ": </b></font>");
						sequenceMsg = formattedText;

						for (Wifi wi : OnePositionWifisList) {
							int r = db.getWifiByCatch(ct.getId_catch(),
									wi.getBSSID());

							messageToDisplay = messageToDisplay + wi.getSSID()
									+ "   :   " + r + "\n";

							formattedText = Html
									.fromHtml(" <br/> <font color='#046380'><b> "
											+ wi.getSSID()
											+ ": </b></font>"
											+ r + "\n . ");
							sequenceMsg = TextUtils.concat(sequenceMsg,
									formattedText);
						}

						messageToDisplay = "CELLTOWERS Informations \n";
						formattedText = Html
								.fromHtml("<font color='#7FC6BC'><b>"
										+ messageToDisplay + ": </b></font>");
						sequenceMsg = TextUtils.concat(sequenceMsg,
								formattedText);

						List<GsmCell> OnePositionCellList = db
								.getAllGsmCellsByCatch(ct.getId_catch());

						for (GsmCell gsm : OnePositionCellList) {
							int r = db.getCellStrengthByCatch(ct.getId_catch(),
									gsm.getCid());
							messageToDisplay = messageToDisplay + gsm.getCid()
									+ " :  " + r + "\n";

							formattedText = Html
									.fromHtml(" <br/> <font color='#046380'><b> Cell Id: "
											+ gsm.getCid()
											+ ": </b></font>"
											+ r + " \n");
							sequenceMsg = TextUtils.concat(sequenceMsg,
									formattedText);

						}

						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								MainActivity.this);

						alertDialog.setTitle(Html
								.fromHtml("<font color='#7FC6BC'><b>Data : </b></font>"));
						alertDialog.setNeutralButton("OK", null);

						alertDialog.setMessage(sequenceMsg);
						alertDialog.show();
					}
				});

				ImageView map = (ImageView) findViewById(R.id.imageMapVizualizedata);

				float[] f = new float[9];
				map.getImageMatrix().getValues(f);
				final float scaleX = f[Matrix.MSCALE_X];
				final float scaleY = f[Matrix.MSCALE_Y];
				final Drawable d = map.getDrawable();
				final int originalW = originalWImage;
				final int originalH = originaHImage;
				final int actualW = Math.round(originalW * scaleX);
				final int actualH = Math.round(originalH * scaleY);
				double imageViewRatio = (double) widthImage
						/ (double) heightImage;
				double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));

				double h = 0, w = 0;
				double x = 0, y = 0;

				if (imgRatio > imageViewRatio) {
					h = (imageViewRatio / imgRatio) * heightImage;
					y = (heightImage - h) / 2;
				} else {
					w = (imgRatio / imageViewRatio) * widthImage;
					x = (widthImage - w) / 2;
				}

				int posX = (int) (x + coordX);
				int posY = (int) (y + coordY);

				CatchButtonData.setLayoutParams(paramsdata);
				paramsdata.leftMargin = (posX - 15);
				paramsdata.topMargin = (posY - 15);
				r2.addView(CatchButtonData, paramsdata);
				CatchButtonData.bringToFront();
			}

		}

	};

	ActionBar.TabListener collectListener = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			if (selectedImagePath != null) {
				ImageView imageMap = (ImageView) findViewById(R.id.imageMapdata);
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
				tracer(R.id.imageMapdata, R.id.relativeLayoutMap, 1);
				count(R.id.relativeLayoutMap, 0);
			} else {
				if (firstTimeTTmap == 0) {
					splitLimits = new Point[numberSplit + 1][4];
					tracer(R.id.imageMapdata, R.id.relativeLayoutMap, 0);
					count(R.id.relativeLayoutMap, 0);
					firstTimeTTmap++;
				} else {
					tracer(R.id.imageMapdata, R.id.relativeLayoutMap, 1);
					count(R.id.relativeLayoutMap, 0);
				}

			}
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction arg1) {
			pager.setCurrentItem(tab.getPosition());

			if (selectedImagePath != null) {
				ImageView imageMap = (ImageView) findViewById(R.id.imageMapdata);
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
			}
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// tracer(R.id.imageMapdata, R.id.relativeLayoutMap, 1);

		}

		public void placeAllPins() {

			final CatchDataBaseHandler db = new CatchDataBaseHandler(
					getApplicationContext());

			List<Catch> catchs = db.getAllCatchs();

			for (final Catch ct : catchs) {

				RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
				ImageView PinImgViewConstant = new ImageView(MainActivity.this);
				PinImgViewConstant.setImageResource(R.drawable.mapconstantpin);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						30, 30);

				final int coordX = (int) ct.getX();
				final int coordY = (int) ct.getY();

				ImageView map = (ImageView) findViewById(R.id.imageMapdata);
				float[] f = new float[9];
				map.getImageMatrix().getValues(f);
				final float scaleX = f[Matrix.MSCALE_X];
				final float scaleY = f[Matrix.MSCALE_Y];
				final Drawable d = map.getDrawable();
				final int originalW = originalWImage;
				final int originalH = originaHImage;
				final int actualW = Math.round(originalW * scaleX);
				final int actualH = Math.round(originalH * scaleY);
				double imageViewRatio = (double) widthImage
						/ (double) heightImage;
				double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));

				double h = 0, w = 0;
				double x = 0, y = 0;

				if (imgRatio > imageViewRatio) {
					h = (imageViewRatio / imgRatio) * heightImage;
					y = (heightImage - h) / 2;
				} else {
					w = (imgRatio / imageViewRatio) * widthImage;
					x = (widthImage - w) / 2;
				}

				int posX = (int) (x + coordX);
				int posY = (int) (y + coordY);

				Log.e("x,y", x + "**" + y + "");
				Log.e("cordoonnées x y", coordX + "**" + coordY + "");
				Log.e("positionsx y", posX + "''" + posY);

				PinImgViewConstant.setLayoutParams(params);
				params.leftMargin = (int) (posX - 20);
				params.topMargin = (int) (posY - 20);
				rl.addView(PinImgViewConstant, params);
				PinImgViewConstant.bringToFront();
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void chooseMap(View view) {

		CatchDataBaseHandler db = new CatchDataBaseHandler(
				getApplicationContext());
		db.deleteAllCatchs();
		db.deleteAllWifis();
		db.deleteAllWifis_catch();
		db.deleteAllGsmCells();
		db.deleteAllGsmCells_catch();

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(
				Intent.createChooser(intent, "Select Picture please"),
				PICK_IMAGE);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((requestCode == PICK_IMAGE) && (data != null)
				&& (data.getData() != null)) {

			selectedImageUri = data.getData();
			selectedImagePath = getPath(selectedImageUri);
			if (selectedImagePath != null) {
				for (int iw = 0; iw < numberSplitColumns; iw++) {
					for (int jh = 0; jh < numberSplitLines; jh++) {
						RelativeLayout rlA;
						rlA = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
						int id = iw * 10 + jh;
						try {
							TextView ViewToRemove = (TextView) findViewById(id);
							rlA.removeView(ViewToRemove);
						} catch (Exception e) {
							Log.d("From", "Try to delete: ");
							Log.d("exception", e.getStackTrace() + "");
						}
					}
				}

				numberSplitLines = 5;
				numberSplitColumns = 5;
				
				MapFragment.textviewList = new ArrayList<TextView>();
				firstTimeTTmap = 0;
				MapFragment.firstTime = 0;
				splitLimits = null;

				ImageView imageMap = (ImageView) findViewById(R.id.imageMapdata);
				RelativeLayout rlA;
				rlA = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
				imageMap.setVisibility(View.VISIBLE);
				rlA.removeView(imageMap);
				imageMap.setBackgroundColor(Color.BLACK);
				imageMap.destroyDrawingCache();
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
				rlA.addView(imageMap);
				imageMap.bringToFront();

				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.number_alert_dialog,
						null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
				alertDialogBuilder.setView(promptsView);
				final EditText userInputLines = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInputNumberLines);
				userInputLines.setRawInputType(Configuration.KEYBOARD_12KEY);
				final EditText userInputColumns = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInputNumberColumns);
				userInputColumns.setRawInputType(Configuration.KEYBOARD_12KEY);
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("OK", null)
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										try {
											splitLimits = new Point[numberSplit + 1][4];
											tracer(R.id.imageMapdata,
													R.id.relativeLayoutMap, 0);
											dialog.cancel();
										} catch (Exception e) {
											System.out.println(e.toString());
										}

									}
								});

				final AlertDialog alertDialog;
				alertDialog = alertDialogBuilder.create();

				alertDialog
						.setOnShowListener(new DialogInterface.OnShowListener() {

							@Override
							public void onShow(DialogInterface dialog) {

								Button b = alertDialog
										.getButton(AlertDialog.BUTTON_POSITIVE);
								b.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {

										String sL = userInputLines.getText()
												.toString();

										String sC = userInputColumns.getText()
												.toString();

										try {

											numberSplitLines = Integer
													.parseInt(sL);

											numberSplitColumns = Integer
													.parseInt(sC);

											int t = Math.max(numberSplitLines,
													numberSplitColumns);

											splitLimits = new Point[t + 1][4];
											tracer(R.id.imageMapdata,
													R.id.relativeLayoutMap, 0);
											if ((Integer.parseInt(sL) > 0)
													&& (Integer.parseInt(sC) > 0)) {
												alertDialog.dismiss();
											}

										} catch (Exception e) {
											Log.w("exception", "exception");
										}

									}
								});
							}
						});
				alertDialog.show();

				pager.setCurrentItem(1);

			}
		} else {
			pager.setCurrentItem(0);
		}

		MapFragment.textviewList.clear();
	}

	public void tracer(int id, int idLayout, int utilisat) {
		ImageView map = (ImageView) findViewById(id);
		float[] f = new float[9];
		map.getImageMatrix().getValues(f);
		final float scaleX = f[Matrix.MSCALE_X];
		final float scaleY = f[Matrix.MSCALE_Y];
		final Drawable d = map.getDrawable();
		final int originalW = d.getIntrinsicWidth();
		final int originalH = d.getIntrinsicHeight();
		final int actualW = Math.round(originalW * scaleX);
		final int actualH = Math.round(originalH * scaleY);
		MainActivity.widthImage = map.getWidth();
		MainActivity.heightImage = map.getHeight();
		MainActivity.originaHImage = originalH;
		MainActivity.originalWImage = originalW;

		double imageViewRatio = (double) map.getWidth()
				/ (double) map.getHeight();
		double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));
		double h = 0, w = 0;
		double x = 0, y = 0;

		if (imgRatio > imageViewRatio) {
			h = (imageViewRatio / imgRatio) * map.getHeight();
			y = (map.getHeight() - h) / 2;
		} else {
			w = (imgRatio / imageViewRatio) * map.getWidth();
			x = (map.getWidth() - w) / 2;
		}

		double partitionW = actualW / numberSplitColumns;

		double partitionH = actualH / numberSplitLines;

		if (utilisat == 0) {

			for (int i = 0; i <= numberSplitColumns; i++) {

				splitLimits[i][0] = new Point(x + (i * partitionW), y);

				splitLimits[i][1] = new Point(x + (i * partitionW), y + actualH);

			}

			for (int i = 0; i <= numberSplitLines; i++) {

				splitLimits[i][2] = new Point(x, y + (i * partitionH));

				splitLimits[i][3] = new Point(x + actualW, y + (i * partitionH));

			}

		}

		Point p1, p2, p3, p4;

		for (int i = 0; i <= numberSplitColumns; i++) {
			p1 = splitLimits[i][0];
			p2 = splitLimits[i][1];

			DrawLineView dr = new DrawLineView(getApplicationContext(), p1, p2);
			dr.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout rlA;
			rlA = (RelativeLayout) findViewById(idLayout);
			RelativeLayout.LayoutParams params;
			params = new RelativeLayout.LayoutParams(2000, 2000);
			params.leftMargin = 0;
			params.topMargin = 0;
			dr.setLayoutParams(params);
			rlA.addView(dr, params);
			dr.bringToFront();

		}

		for (int i = 0; i <= numberSplitLines; i++) {
			p3 = splitLimits[i][2];
			p4 = splitLimits[i][3];
			DrawLineView dr2 = new DrawLineView(getApplicationContext(), p3, p4);
			dr2.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout rlA;
			rlA = (RelativeLayout) findViewById(idLayout);
			RelativeLayout.LayoutParams params;
			params = new RelativeLayout.LayoutParams(2000, 2000);
			params.leftMargin = 0;
			params.topMargin = 0;
			dr2.setLayoutParams(params);
			rlA.addView(dr2, params);
			dr2.bringToFront();
		}

	}

	public void count(int LayoutId, int choice) {

		RelativeLayout rl = (RelativeLayout) findViewById(LayoutId);

		if (MapFragment.textviewList.size() > 0) {

			TextView ntv;

			System.out.println(MapFragment.textviewList.size() + " : size ");
			List<TextView> newTextviewList = new ArrayList<TextView>();
			for (TextView itv : MapFragment.textviewList) {

				ntv = new TextView(this);
				ntv.setText(itv.getText());

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						(int) (MainActivity.splitLimits[1][0].getX() - MainActivity.splitLimits[0][0]
								.getX()),
						(int) (MainActivity.splitLimits[1][2].getY() - MainActivity.splitLimits[0][2]
								.getY()));

				params.setMargins(
						(int) (MainActivity.splitLimits[0][0].getX() + ((itv
								.getId() / 10) * (MainActivity.splitLimits[1][0]
								.getX() - MainActivity.splitLimits[0][0].getX()))),
						(int) (MainActivity.splitLimits[0][0].getY() + ((itv
								.getId() % 10) * (MainActivity.splitLimits[1][2]
								.getY() - MainActivity.splitLimits[0][2].getY()))),
						0, 0);

				if (LayoutId == R.id.relativeLayoutMap) {
					Log.w("layout", LayoutId + "");
					ntv.setTextColor(Color.BLUE);
					ntv.setId(itv.getId());
				} else {
					ntv.setTextColor(Color.BLACK);
					ntv.setId(itv.getId() + 100);
				}

				ntv.setLayoutParams(params);
				ntv.setVisibility(View.VISIBLE);
				if (ntv.getText().equals("0")) {
					ntv.setVisibility(View.INVISIBLE);
				}
				itv.destroyDrawingCache();
				newTextviewList.add(ntv);
				// ntv = new TextView(this);
				rl.addView(ntv, params);
				rl.removeView(itv);
			}
			MapFragment.textviewList = newTextviewList;
		}
	}

	public void count2(int LayoutId, int choice) {

		RelativeLayout rl = (RelativeLayout) findViewById(LayoutId);

		if (MapFragment.textviewList.size() > 0) {

			TextView ntv;

			if (MapFragment.textviewListForVisualize == null) {
				MapFragment.textviewListForVisualize = new ArrayList<TextView>();
			}

			int i = 0;
			boolean listForVisulizeUpdated = (MapFragment.textviewListForVisualize
					.size() == MapFragment.textviewList.size());
			for (TextView itv : MapFragment.textviewList) {
				if (listForVisulizeUpdated) {
					ntv = MapFragment.textviewListForVisualize.get(i);
				} else {
					ntv = new TextView(this);
					ntv.setTextColor(Color.BLACK);
					ntv.setId(itv.getId() + 100);
				}

				ntv.setText(itv.getText());

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						(int) (MainActivity.splitLimits[1][0].getX() - MainActivity.splitLimits[0][0]
								.getX()),
						(int) (MainActivity.splitLimits[1][2].getY() - MainActivity.splitLimits[0][2]
								.getY()));

				params.setMargins(
						(int) (MainActivity.splitLimits[0][0].getX() + ((itv
								.getId() / 10) * (MainActivity.splitLimits[1][0]
								.getX() - MainActivity.splitLimits[0][0].getX()))),
						(int) (MainActivity.splitLimits[0][0].getY() + ((itv
								.getId() % 10) * (MainActivity.splitLimits[1][2]
								.getY() - MainActivity.splitLimits[0][2].getY()))),
						0, 0);

				ntv.setLayoutParams(params);
				ntv.setVisibility(View.VISIBLE);
				if (ntv.getText().equals("0")) {
					ntv.setVisibility(View.INVISIBLE);
				}
				if (!listForVisulizeUpdated) {
					MapFragment.textviewListForVisualize.add(ntv);
					rl.addView(ntv, params);
				}
				i++;
			}
		}
	}

	public void itenerary(View view) {
		Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
		startActivity(intent);
		finish();
	}

	public void saveImg(View view) {

		LayoutInflater li = LayoutInflater.from(this);
		View alertView = li.inflate(R.layout.save_file, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(alertView);

		final EditText filenameET = (EditText) alertView
				.findViewById(R.id.filenameEt);

		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", null)
				.setNegativeButton("Cancel", null);

		final AlertDialog alertDialog;
		alertDialog = alertDialogBuilder.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {

						String filename = filenameET.getText().toString()
								+ ".txt";

						STORETEXT = filename;

						System.out.println(STORETEXT);
						alertDialog.dismiss();

						OutputStreamWriter out;
						try {

							out = new OutputStreamWriter(openFileOutput(
									STORETEXT, 0));

							//File path=new File(getFilesDir(),"folder");
							
							
							out.write("numberSplitLines\n" + numberSplitLines
									+ "\n");
							out.write("numberSplitColumns\n"
									+ numberSplitColumns + "\n");
							out.write("numberSplit\n" + numberSplit + "\n");
							out.write("widthImage\n" + widthImage + "\n");
							out.write("heightImage\n" + heightImage + "\n");
							out.write("originalWImage\n" + originalWImage
									+ "\n");
							out.write("originaHImage\n" + originaHImage + "\n");
							out.write("selectedImagePath\n" + selectedImagePath
									+ "\n");
							out.write("selectedImageUri\n" + selectedImageUri
									+ "\n");

							System.out.println("selectedImagePath\n"
									+ selectedImagePath + "\n"
									+ "selectedImageUri\n" + selectedImageUri
									+ "\n");
							out.close();

						}

						catch (Throwable t) {

							Log.e("Exception: ", t.toString());

						}

					}
				});
			}
		});

		alertDialog.show();

	}

	public void readImg(View view) {
		LayoutInflater li = LayoutInflater.from(this);
		View alertView = li.inflate(R.layout.save_file, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(alertView);

		final EditText filenameET = (EditText) alertView
				.findViewById(R.id.filenameEt);
		
	//	File path=new File(getFilesDir(),"myfolder");
//		File fileList = new File("/sdcard");
//		if (fileList != null){
//		    File[] filenames = fileList.listFiles();
//		        for (File tmpf : filenames){
//		            System.out.println(tmpf.getName()+" : ");
//		        }
//		    }

		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", null)
				.setNegativeButton("Cancel", null);

		final AlertDialog alertDialog;
		alertDialog = alertDialogBuilder.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {

						String filename = filenameET.getText().toString()
								+ ".txt";

						STORETEXT = filename;

						System.out.println(STORETEXT);

						alertDialog.dismiss();

						try {

							InputStream in = openFileInput(STORETEXT);
							
							
							if (in != null) {

								
								
								InputStreamReader tmp = new InputStreamReader(
										in);

								BufferedReader reader = new BufferedReader(tmp);

								String str;

								StringBuilder buf = new StringBuilder();

								int i = 0;

								while ((str = reader.readLine()) != null) {

									if (i == 1) {
										numberSplitLines = Integer
												.parseInt(str);
										System.out.println(numberSplitLines);
									}
									if (i == 3) {
										numberSplitColumns = Integer
												.parseInt(str);
										System.out.println(numberSplitColumns);
									}
									if (i == 5) {
										numberSplit = Integer.parseInt(str);
									}
									if (i == 7) {
										widthImage = Integer.parseInt(str);
									}
									if (i == 9) {
										heightImage = Integer.parseInt(str);
									}
									if (i == 11) {
										originalWImage = Integer.parseInt(str);
									}
									if (i == 13) {
										originaHImage = Integer.parseInt(str);
									}
									if (i == 15) {
										selectedImageUri = Uri.parse(new File(
												str).toString());
										System.out.println("URIII :   "
												+ selectedImageUri);
									}
									i++;
									buf.append(str + "\n");
									
									MapFragment.textviewList = new ArrayList<TextView>();
									firstTimeTTmap = 0;
									MapFragment.firstTime = 0;
									splitLimits = null;

									ImageView imageMap = (ImageView) findViewById(R.id.imageMapdata);
									RelativeLayout rlA;
									rlA = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
									imageMap.setVisibility(View.VISIBLE);
									rlA.removeView(imageMap);
									imageMap.setBackgroundColor(Color.BLACK);
									imageMap.destroyDrawingCache();
									imageMap.setImageURI(null);
									imageMap.setImageURI(selectedImageUri);
									rlA.addView(imageMap);
									imageMap.bringToFront();
								}

								in.close();

								splitLimits = new Point[numberSplit + 1][4];
								tracer(R.id.imageMapdata,
										R.id.relativeLayoutMap, 0);
								Log.d("File", "" + buf.toString());
								firstTimeTTmap = 0;
								pager.setCurrentItem(1);
							}

						}

						catch (java.io.FileNotFoundException e) {

						}

						catch (Throwable t) {

							Log.e("Exception: ", t.toString());

						}

					}
				});
			}
		});

		alertDialog.show();

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.getContentResolver().query(uri, projection, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	ActionBar.TabListener tabListenerAnalysis = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction arg1) {
			pager.setCurrentItem(tab.getPosition());
			if (selectedImagePath != null) {
				ImageView imageMap = (ImageView) findViewById(R.id.imageMapAnalysis);
				imageMap.setImageURI(null);
				imageMap.setImageURI(selectedImageUri);
			}
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		}
	};

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

};
