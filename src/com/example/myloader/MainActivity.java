package com.example.myloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.R.integer;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.ContactsContract.Contacts;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


// 大きいデータ (または構築に時間のかかるデータ)
class BigData{
	static int msDataIndex = 0;
	
	int mDataIndex = 0;
	String mDataName = "";
	ArrayList<String> mList = new ArrayList<String>();
	
	// コンストラクタでは何もしないでおく
	public BigData(){
		String table = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		mDataIndex = msDataIndex++;
		mDataName = String.valueOf(table.charAt(mDataIndex % table.length()));
		Log.d("test", "...BigData" + mDataName);
	}
	
	// この処理には時間がかかるものとする
	public void load(){
		Log.d("test", "...load" + mDataName);
		try{
			Random r = new Random();
			for(int i = 0; i < 3000; i++){
				if(i % 100 == 0){
					Log.d("test", "...loading" + mDataName + ":" + i);
				}
				int n = r.nextInt(10000);
				mList.add("item" + mDataName + "_" + n);
				Thread.sleep(1);
			}
		}
		catch(InterruptedException ex){
			Log.d("test", "======InterruptedException" + mDataName);
		}
	}
	
	// アクセサ
	String at(int index){
		return mList.get(index)  ;
	}
}

// ローダー
class BigDataLoader extends AsyncTaskLoader<BigData>{
	public BigDataLoader(Context context) {
		super(context);
		Log.d("test", "...BigDataLoader.");
	}

	@Override
	public BigData loadInBackground() {
		// BigData構築
		Log.d("test", "...loadInBackground.");
		BigData data = new BigData();
		data.load();
		return data;
	}
}

// Loader を呼び出す Activity または Fragment では
// LoaderCallbacks を implements する必要がある。
public class MainActivity extends Activity implements LoaderCallbacks<BigData> {
	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
	// LoaderCallbacksのメソッド3つ
	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
	@Override
	public Loader<BigData> onCreateLoader(int id, Bundle args) {
		// Loaderを作る
		// ※LoaderはActivityのライフサイクルを超えて存在することがあるため、
		//   Activityより広範のgetApplication()をcontextとして渡す。
		//   …という解釈？
		Log.d("test", "------------onCreateLoader");
		BigDataLoader loader = new BigDataLoader(this.getApplication());
		loader.forceLoad();
		return loader;
	}
	@Override
	public void onLoadFinished(Loader<BigData> loader, BigData data) {
		// ロード完了。データ表示。
		Log.d("test", "------------onLoadFinished");
		TextView textView = (TextView)findViewById(R.id.textView1);
		textView.setText(data.at(0));
		// プログレス非表示
		ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
		progress.setVisibility(View.GONE);
		
	}
	@Override
	public void onLoaderReset(Loader<BigData> loader) {
		// Loaderがリセットされたときに呼ばれるらしい。あまり使わない。
		Log.d("test", "------------onLoaderReset");
	}
		
	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
	// ボタンイベント等
	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
    public void buttonMethodRestart(View button){
    	// プログレス表示
		ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
		progress.setVisibility(View.VISIBLE);
    	// ロード再実行
    	getLoaderManager().restartLoader(0, null, this);
    }

	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
	// Activity
	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- //
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// まずはLoaderManagerを初期化
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
