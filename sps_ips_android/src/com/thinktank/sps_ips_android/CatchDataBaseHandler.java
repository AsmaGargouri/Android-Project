package com.thinktank.sps_ips_android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thinktank.sps_ips_android.Model.Catch;
import com.thinktank.sps_ips_android.Model.CellCatched;
import com.thinktank.sps_ips_android.Model.GsmCell;
import com.thinktank.sps_ips_android.Model.Wifi;
import com.thinktank.sps_ips_android.Model.WifiCatched;

public class CatchDataBaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 14;

	private static final String DATABASE_NAME = "positionsManager";

	private static final String TABLE_CATCH = "catch";
	private static final String TABLE_WIFI = "wifi2";
	private static final String TABLE_WIFI_CATCHED = "wifi_catched";
	private static final String TABLE_CELL = "cell";
	private static final String TABLE_CELL_CATCHED = "cell_catched";

	private static final String KEY_CATCH_ID = "id_catch";
	private static final String KEY_X = "x";
	private static final String KEY_Y = "y";
	private static final String KEY_TIME = "time";
	private static final String KEY_DATE = "date";

	private static final String KEY_WIFI_ID = "id_BSSID";
	private static final String KEY_WIFI_NAME = "SSID";
	private static final String KEY_WIFI_CAPABILITIES = "capabilities";
	private static final String KEY_WIFI_FREQUENCY = "frequency";

	private static final String KEY_CELL_ID = "id_cell";
	private static final String KEY_CELL_LAC = "lac";
	private static final String KEY_CELL_PSC = "psc";

	private static final String KEY_STRENGTH = "strentgh";

	private static final String KEY_RSSI_GSM = "rssi_Gsm";

	public CatchDataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String CREATE_CATCH_TABLE = "CREATE TABLE "
			+ TABLE_CATCH + "(" + KEY_CATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_X + " NUMBER," + KEY_Y + " NUMBER," + KEY_TIME + " DATETIME,"
			+ KEY_DATE + " DATE" + ")";

	private static final String CREATE_TABLE_CELL = "CREATE TABLE "
			+ TABLE_CELL + "(" + KEY_CELL_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CELL_LAC + " INTEGER," + KEY_CELL_PSC + " INTEGER" + ")";

	private static final String CREATE_TABLE_WIFI_CATCHED = "CREATE TABLE "
			+ TABLE_WIFI_CATCHED + "(" + KEY_CATCH_ID + " INTEGER,"
			+ KEY_WIFI_ID + " TEXT," + KEY_STRENGTH + " INTEGER,"
			+ " PRIMARY KEY(" + KEY_CATCH_ID + "," + KEY_WIFI_ID + ") ON CONFLICT IGNORE " + ")";

	private static final String CREATE_WIFI_TABLE = "CREATE TABLE "
			+ TABLE_WIFI + "(" + KEY_WIFI_ID + " TEXT PRIMARY KEY,"
			+ KEY_WIFI_NAME + " TEXT," + KEY_WIFI_CAPABILITIES + " TEXT,"
			+ KEY_WIFI_FREQUENCY + " NUMBER" + ")";

	private static final String CREATE_TABLE_CELL_CATCHED = "CREATE TABLE "
			+ TABLE_CELL_CATCHED + "(" + KEY_CATCH_ID + " INTEGER,"
			+ KEY_CELL_ID + " INTEGER," + KEY_RSSI_GSM + " INTEGER,"
			+ " PRIMARY KEY(" + KEY_CATCH_ID + "," + KEY_CELL_ID + ") ON CONFLICT IGNORE " + ")";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CATCH_TABLE);
		db.execSQL(CREATE_WIFI_TABLE);
		db.execSQL(CREATE_TABLE_WIFI_CATCHED);
		db.execSQL(CREATE_TABLE_CELL);
		db.execSQL(CREATE_TABLE_CELL_CATCHED);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATCH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_CATCHED);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELL_CATCHED);

		onCreate(db);
	}

	public long createCatch(Catch catched) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_X, catched.getX());
		values.put(KEY_Y, catched.getY());
		values.put(KEY_TIME, catched.getTime());
		values.put(KEY_DATE, catched.getDate());

		long catch_id = db.insert(TABLE_CATCH, null, values);
		db.close();
		return catch_id;

	}

	public Catch getCatch(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CATCH, new String[] { KEY_CATCH_ID,
				KEY_X, KEY_TIME, KEY_DATE }, KEY_CATCH_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Catch catched = new Catch(Integer.parseInt(cursor.getString(0)),
				(double) Integer.parseInt(cursor.getString(1)),
				(double) Integer.parseInt(cursor.getString(2)),
				cursor.getString(3), cursor.getString(4));
		db.close();
		return catched;
	}

	public List<Catch> getAllCatchs() {
		List<Catch> catchList = new ArrayList<Catch>();
		String selectQuery = "SELECT  * FROM " + TABLE_CATCH;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Catch position = new Catch();
				position.setId_catch(Integer.parseInt(cursor.getString(0)));
				position.setX(cursor.getDouble(1));
				position.setY(cursor.getDouble(2));

				position.setTime(cursor.getString(3));
				position.setDate(cursor.getString(4));

				catchList.add(position);
			} while (cursor.moveToNext());
		}
		db.close();
		return catchList;
	}

	public void deleteAllCatchs() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CATCH, null, null);
		db.close();
	}

	public void createWifi(Wifi wifiInf, long[] catch_ids, int strength) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_WIFI_ID, wifiInf.getBSSID());
		values.put(KEY_WIFI_NAME, wifiInf.getSSID());
		values.put(KEY_WIFI_CAPABILITIES, wifiInf.getCapabilities());
		values.put(KEY_WIFI_FREQUENCY, wifiInf.getFrequency());
		long sucessInsertWifis = db.insertWithOnConflict(TABLE_WIFI, null,
				values, SQLiteDatabase.CONFLICT_IGNORE);
		for (long catch_id : catch_ids) {

			long successInsertCatched = createWifiCatched(wifiInf.getBSSID(),
					catch_id, strength);
		}
		db.close();

	}

	public Wifi getWifi(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_WIFI, new String[] { KEY_WIFI_ID,
				KEY_WIFI_NAME, KEY_WIFI_FREQUENCY, KEY_WIFI_CAPABILITIES },
				KEY_WIFI_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Wifi wifi = new Wifi(cursor.getString(0), cursor.getString(1),
				Integer.parseInt(cursor.getString(2)), cursor.getString(3));
		db.close();
		return wifi;
	}

	public long createWifiCatched(String wifi_id, long catched_id, int strength) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_CATCH_ID, catched_id);
		values.put(KEY_WIFI_ID, wifi_id);
		values.put(KEY_STRENGTH, strength);

		long id = db.insertOrThrow(TABLE_WIFI_CATCHED, null, values);
		db.close();
		return id;
	}

	public List<Wifi> getAllWifi() {
		List<Wifi> wifiInfoList = new ArrayList<Wifi>();
		String selectQuery = "SELECT  * FROM " + TABLE_WIFI;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Wifi wi = new Wifi();
				wi.setBSSID(c.getString(c.getColumnIndex(KEY_WIFI_ID)));
				wi.setSSID(c.getString(c.getColumnIndex(KEY_WIFI_NAME)));
				wi.setCapabilities(c.getString(c
						.getColumnIndex(KEY_WIFI_CAPABILITIES)));
				wi.setFrequency(c.getInt((c.getColumnIndex(KEY_WIFI_FREQUENCY))));
				wifiInfoList.add(wi);
			} while (c.moveToNext());
		}
		db.close();
		return wifiInfoList;
	}

	public int getWifiByCatch(int id_catch, String id_wifi) {

		int res = 0;

		String selectQuery = "SELECT  *   FROM " + TABLE_WIFI_CATCHED
				+ " WHERE " + KEY_CATCH_ID + " = " + id_catch + " AND "
				+ KEY_WIFI_ID + "='" + id_wifi + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			res = c.getInt(c.getColumnIndex(KEY_STRENGTH));
			Log.e("res:", c.getColumnIndex(KEY_STRENGTH) + " ");
		}
		db.close();
		return res;

	}

	public void deleteAllWifis() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIFI, null, null);
		db.close();
	}

	public List<Wifi> getAllWifisByCatch(int id_catch) {
		List<Wifi> WifiList = new ArrayList<Wifi>();

		String selectQuery = "SELECT  * FROM " + TABLE_WIFI + " wi,"
				+ TABLE_CATCH + " cat," + TABLE_WIFI_CATCHED + " wc WHERE cat."
				+ KEY_CATCH_ID + " = " + id_catch + " AND cat." + KEY_CATCH_ID
				+ " = " + "wc." + KEY_CATCH_ID + " AND wi." + KEY_WIFI_ID
				+ " = " + "wc." + KEY_WIFI_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Wifi wi = new Wifi();
				wi.setSSID(c.getString(c.getColumnIndex(KEY_WIFI_NAME)));
				wi.setBSSID(c.getString(c.getColumnIndex(KEY_WIFI_ID)));
				wi.setFrequency(c.getInt((c.getColumnIndex(KEY_STRENGTH))));
				WifiList.add(wi);
			} while (c.moveToNext());
		}
		db.close();
		return WifiList;
	}

	
	public List<WifiCatched> getAllFromWifiCatched() {
		
			List<WifiCatched> catchList = new ArrayList<WifiCatched>();
			
			String selectQuery = "SELECT  * FROM " + TABLE_WIFI_CATCHED;
			
			SQLiteDatabase db = this.getWritableDatabase();
			
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			if (cursor.moveToFirst()) {
				do {
					WifiCatched wicatch = new WifiCatched();
					wicatch.setId_catch(Integer.parseInt(cursor.getString(0)));
					wicatch.setBSSID(cursor.getString(1));
					wicatch.setStrength(Integer.parseInt(cursor.getString(2)));

					catchList.add(wicatch);
				} while (cursor.moveToNext());
			}
			db.close();
			return catchList;
		}
	
	public List<Wifi> getAllWifisStrengthsByCatch(int id_catch) {
		List<Wifi> WifiList = new ArrayList<Wifi>();

		String selectQuery = "SELECT  wi." + KEY_WIFI_NAME + ", wp."
				+ KEY_STRENGTH + "  FROM " + TABLE_WIFI + " wi, " + TABLE_CATCH
				+ " cat, " + TABLE_WIFI_CATCHED + " wc WHERE cat."
				+ KEY_CATCH_ID + " = " + id_catch + " AND cat." + KEY_CATCH_ID
				+ " = " + "wc." + KEY_CATCH_ID + " AND wi." + KEY_WIFI_ID
				+ " = " + "wc." + KEY_WIFI_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Wifi wi = new Wifi();
				wi.setSSID(c.getString(c.getColumnIndex(KEY_WIFI_NAME)));
				wi.setCapabilities(c.getString(c
						.getColumnIndex(KEY_WIFI_CAPABILITIES)));
				wi.setFrequency(c.getInt((c.getColumnIndex(KEY_WIFI_FREQUENCY))));
				WifiList.add(wi);
			} while (c.moveToNext());
		}
		db.close();
		return WifiList;
	}

	public void deleteAllWifis_catch() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIFI_CATCHED, null, null);
		db.close();
	}

	public void createGsmCell(GsmCell cellInf, long[] catch_ids, int rssi_gsm) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CELL_ID, cellInf.getCid());
		values.put(KEY_CELL_LAC, cellInf.getLac());
		values.put(KEY_CELL_PSC, cellInf.getPsc());

		long sucessInsertGsmCells = db.insertWithOnConflict(TABLE_CELL, null,
				values, SQLiteDatabase.CONFLICT_IGNORE);

		for (long catch_id : catch_ids) {
			long successInsertCatched = createGsmCellCatched(cellInf.getCid(),
					catch_id, rssi_gsm);
		}
		db.close();

	}

	public long createGsmCellCatched(int cell_id, long catched_id, int rssi_gsm) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_CATCH_ID, catched_id);
		values.put(KEY_CELL_ID, cell_id);
		values.put(KEY_RSSI_GSM, rssi_gsm);

		long id = db.insert(TABLE_CELL_CATCHED, null, values);
		db.close();
		return id;
	}

	public List<GsmCell> getAllCells() {
		List<GsmCell> GsmCellInfoList = new ArrayList<GsmCell>();
		String selectQuery = "SELECT  * FROM " + TABLE_CELL;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				GsmCell ce = new GsmCell();
				ce.setCid(c.getInt(c.getColumnIndex(KEY_CELL_ID)));
				ce.setLac(c.getInt(c.getColumnIndex(KEY_CELL_LAC)));
				ce.setPsc(c.getInt(c.getColumnIndex(KEY_CELL_PSC)));
				GsmCellInfoList.add(ce);
			} while (c.moveToNext());
			
			db.close();
		}

		return GsmCellInfoList;
	}

	public GsmCell getGsm(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CELL, new String[] { KEY_CELL_ID,
				KEY_CELL_LAC, KEY_CELL_PSC }, KEY_CELL_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		GsmCell cell = new GsmCell(Integer.parseInt(cursor.getString(1)),
				Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor
						.getString(3)));
		
		db.close();
		
		return cell;
	}

	public List<GsmCell> getAllGsmCellsByCatch(int id_catch) {
		List<GsmCell> GsmCellList = new ArrayList<GsmCell>();

		String selectQuery = "SELECT  * FROM " + TABLE_CELL + " cell,"
				+ TABLE_CATCH + " cat," + TABLE_CELL_CATCHED + " cc WHERE cat."
				+ KEY_CATCH_ID + " = " + id_catch + " AND cat." + KEY_CATCH_ID
				+ " = " + "cc." + KEY_CATCH_ID + " AND cell." + KEY_CELL_ID
				+ " = " + "cc." + KEY_CELL_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				GsmCell cell = new GsmCell();
				cell.setCid(c.getInt(c.getColumnIndex(KEY_CELL_ID)));
				cell.setLac(c.getInt(c.getColumnIndex(KEY_CELL_LAC)));
				cell.setPsc(c.getInt(c.getColumnIndex(KEY_CELL_PSC)));
				GsmCellList.add(cell);
			} while (c.moveToNext());
		}
		db.close();
		return GsmCellList;
	}

	public List<GsmCell> getAllGsmCellsStrengthsByCatch(int id_catch) {

		List<GsmCell> GsmCellList = new ArrayList<GsmCell>();

		String selectQuery = "SELECT  ce." + KEY_CELL_ID + ", wp."
				+ KEY_RSSI_GSM + "  FROM " + TABLE_CELL + " ce, " + TABLE_CATCH
				+ " cat, " + TABLE_CELL_CATCHED + " cc WHERE cat."
				+ KEY_CATCH_ID + " = " + id_catch + " AND cat." + KEY_CATCH_ID
				+ " = " + "cc." + KEY_CATCH_ID + " AND ce." + KEY_CELL_ID
				+ " = " + "cc." + KEY_CELL_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				GsmCell ce = new GsmCell();
				ce.setCid(c.getInt(c.getColumnIndex(KEY_CELL_ID)));
				GsmCellList.add(ce);
			} while (c.moveToNext());
		}
		
		db.close();
		return GsmCellList;
	}

	public int getCellStrengthByCatch(int id_catch, int id_cell) {

		int res = 0;

		String selectQuery = "SELECT  *   FROM " + TABLE_CELL_CATCHED
				+ " WHERE " + KEY_CATCH_ID + " = " + id_catch + " AND "
				+ KEY_CELL_ID + " = " + id_cell;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			res = c.getInt(c.getColumnIndex(KEY_RSSI_GSM));
		}
		db.close();
		return res;

	}
	

	public void deleteAllGsmCells() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CELL, null, null);
		db.close();
	}

	public void deleteAllGsmCells_catch() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CELL_CATCHED, null, null);
		db.close();
	}

	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
