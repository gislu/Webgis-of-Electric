
import java.util.Iterator;

import com.cityelc.ahu.lhw.R;
import com.cityelc.ahu.lhw.FeatureLayerUtils.FieldType;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureEditResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.Query.SpatialRelationship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity class for the Attribute Editor Sample
 */
public class AttributeEditorActivity extends Activity {

	MapView mapView;

	ArcGISFeatureLayer featureLayer;

	Point pointClicked;

	LayoutInflater inflator;

	AttributeListAdapter listAdapter;

	ListView listView;
	
	
	private TextView editText;
	private LocationManager lm;
	private static final String TAG2 = "GpsActivity";

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lm.removeUpdates(locationListener);
	}
	

	View listLayout;
	
	private Button buttonZoomOut = null;
	private Button GPSbutton = null;
	private Button Upload =null;
	
	public static final String TAG = "AttributeEditorSample";

	static final int ATTRIBUTE_EDITOR_DIALOG_ID = 1;
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Setup map objects

		mapView = (MapView) findViewById(R.id.map);
		featureLayer = (ArcGISFeatureLayer) mapView
				.findViewById(R.id.ksfieldsFLayer);
		SimpleFillSymbol sfs = new SimpleFillSymbol(Color.TRANSPARENT);
		sfs.setOutline(new SimpleLineSymbol(Color.YELLOW, 3));
		featureLayer.setSelectionSymbol(sfs);

		// set up local variables
		inflator = LayoutInflater.from(getApplicationContext());
		listLayout = inflator.inflate(R.layout.list_layout, null);
		listView = (ListView) listLayout.findViewById(R.id.list_view);
		
		
		
		this.buttonZoomOut = (Button) findViewById(R.id.buttonZoomOut);
		this.buttonZoomOut.setOnClickListener(new OnClickListener() { 
			public void  onClick(View v) { 
				AttributeEditorActivity.this.mapView.zoomout(); 
			} 
			});

       this.Upload = (Button) findViewById(R.id.Upload);
    	this.Upload.setOnClickListener(new OnClickListener() { 
		public void  onClick(View v) { 		
			Intent intent = new Intent();
 			Bundle bundle = new Bundle();
 			intent.putExtras(bundle);
 			intent.setClass(getApplicationContext(), UpdataActivity.class);
 			startActivity(intent);
				
		} 
		});

		
		
		//GPS
		
		this.GPSbutton = (Button) findViewById(R.id.GPSbutton);
		this.GPSbutton.setOnClickListener(new OnClickListener() { 
			public void  onClick(View v) { 


				editText = (TextView) findViewById(R.id.label);
				lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

				// 判断GPS是否正常启动
				if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Toast.makeText(AttributeEditorActivity.this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
					// 返回开启GPS导航设置界面
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
					return;
				}

				// 为获取地理位置信息时设置查询条件
				String bestProvider = lm.getBestProvider(getCriteria(), true);
				// 获取位置信息
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
				Location location = lm.getLastKnownLocation(bestProvider);
				updateView(location);
				// 监听状态
				lm.addGpsStatusListener(listener);
				// 绑定监听，有4个参数
				// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
				// 参数2，位置信息更新周期，单位毫秒
				// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
				// 参数4，监听
				// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

				// 1秒更新一次，或最小位移变化超过1米更新一次；
				// 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
						locationListener);
				// 给按钮绑定点击监听器
			
			} 
			});

		
		
		
		
		
		
	
	
		
		
		
		
		
		
		
		
		// Create a new AttributeListAdapter when the feature layer is
		// initialized
		if (featureLayer.isInitialized()) {

			listAdapter = new AttributeListAdapter(this,
					featureLayer.getFields(), featureLayer.getTypes(),
					featureLayer.getTypeIdField());

		}
		else {

			featureLayer
					.setOnStatusChangedListener(new OnStatusChangedListener() {

						private static final long serialVersionUID = 1L;

						public void onStatusChanged(View view, STATUS status) {

							if (status == STATUS.INITIALIZED) {
								listAdapter = new AttributeListAdapter(
										AttributeEditorActivity.this,
										featureLayer.getFields(), featureLayer
												.getTypes(), featureLayer
												.getTypeIdField());
							}
						}
					});
		}

		// Set tap listener for MapView
		mapView.setOnSingleTapListener(new OnSingleTapListener() {

			private static final long serialVersionUID = 1L;

			
			public void onSingleTap(float x, float y) {

				// convert event into screen click
				pointClicked = mapView.toMapPoint(x, y);

				// build a query to select the clicked feature
				Query query = new Query();
				query.setOutFields(new String[] { "*" });
				query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
				query.setGeometry(pointClicked);
				query.setInSpatialReference(mapView.getSpatialReference());

				// call the select features method and implement the
				// callbacklistener
				featureLayer.selectFeatures(query,
						ArcGISFeatureLayer.SELECTION_METHOD.NEW,
						new CallbackListener<FeatureSet>() {

							// handle any errors
							public void onError(Throwable e) {

								Log.d(TAG,
										"Select Features Error"
												+ e.getLocalizedMessage());

							}

							public void onCallback(FeatureSet queryResults) {

								if (queryResults.getGraphics().length > 0) {

									Log.d(TAG,
											"Feature found id="
													+ queryResults
															.getGraphics()[0]
															.getAttributeValue(featureLayer
																	.getObjectIdField()));

									// set new data and notify adapter that data
									// has changed
									listAdapter.setFeatureSet(queryResults);
									listAdapter.notifyDataSetChanged();

									// This callback is not run in the main UI
									// thread. All GUI
									// related events must run in the UI thread,
									// therefore use the
									// Activity.runOnUiThread() method. See
									// http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
									// for more information.
									AttributeEditorActivity.this
											.runOnUiThread(new Runnable() {

												public void run() {

													// show the editor dialog.
													showDialog(ATTRIBUTE_EDITOR_DIALOG_ID);

												}
											});
								}
							}
						});
			}
		});

		// TODO handle rotation
	}

	/**
	 * Overidden method from Activity class - this is the recommended way of
	 * creating dialogs
	 */
	
	
	
	//GPS
	// 位置监听
	private LocationListener locationListener = new LocationListener() {

		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
			updateView(location);
			Log.i(TAG, "时间：" + location.getTime());
			Log.i(TAG, "经度：" + location.getLongitude());
			Log.i(TAG, "纬度：" + location.getLatitude());
			Log.i(TAG, "海拔：" + location.getAltitude());
		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				Log.i(TAG2, "当前GPS状态为可见状态");
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG2, "当前GPS状态为服务区外状态");
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG2, "当前GPS状态为暂停服务状态");
				break;
			}
		}

		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			Location location = lm.getLastKnownLocation(provider);
			updateView(location);
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "第一次定位");
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "卫星状态改变");
				// 获取当前状态
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("搜索到：" + count + "颗卫星");
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "定位结束");
				break;
			}
		};
	};

	/**
	 * 实时更新文本内容
	 * 
	 * @param location
	 */
	private void updateView(Location location) {
		if (location != null) {
			editText.setText("设备位置信息\n\n经度：");
			editText.append(String.valueOf(location.getLongitude()));
			editText.append("\n纬度：");
			editText.append(String.valueOf(location.getLatitude()));
		} else {
			// 清空EditText对象
			editText.getEditableText().clear();
		}
	}

	/**
	 * 返回查询条件
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置是否要求速度
		criteria.setSpeedRequired(false);
		// 设置是否允许运营商收费
		criteria.setCostAllowed(false);
		// 设置是否需要方位信息
		criteria.setBearingRequired(false);
		// 设置是否需要海拔信息
		criteria.setAltitudeRequired(false);
		// 设置对电源的需求
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	
	}


	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case ATTRIBUTE_EDITOR_DIALOG_ID:

			// create the attributes dialog
			Dialog dialog = new Dialog(this);
			listView.setAdapter(listAdapter);
			dialog.setContentView(listLayout);
			dialog.setTitle("Edit Attributes");

			// set button on click listeners, setting as xml attributes doesnt
			// work
			// due to a scope/thread issue
			Button btnEditCancel = (Button) listLayout
					.findViewById(R.id.btn_edit_discard);
			btnEditCancel
					.setOnClickListener(returnOnClickDiscardChangesListener());

			Button btnEditApply = (Button) listLayout
					.findViewById(R.id.btn_edit_apply);
			btnEditApply
					.setOnClickListener(returnOnClickApplyChangesListener());

			return dialog;
		}
		return null;
	}

	/**
	 * Helper method to return an OnClickListener for the Apply button
	 */
	public OnClickListener returnOnClickApplyChangesListener() {

		return new OnClickListener() {

			public void onClick(View v) {

				boolean isTypeField = false;
				boolean hasEdits = false;
				boolean updateMapLayer = false;
				Graphic newGraphic = new Graphic();

				// loop through each attribute and set the new values if they
				// have
				// changed
				for (int i = 0; i < listAdapter.getCount(); i++) {

					AttributeItem item = (AttributeItem) listAdapter.getItem(i);
					String value = "";

					// check to see if the View has been set
					if (item.getView() != null) {

						// TODO implement applying domain fields values if
						// required
						// determine field type and therefore View type
						if (item.getField().getName()
								.equals(featureLayer.getTypeIdField())) {
							// drop down spinner

							Spinner spinner = (Spinner) item.getView();
							// get value for the type
							String typeName = spinner.getSelectedItem()
									.toString();
							value = FeatureLayerUtils.returnTypeIdFromTypeName(
									featureLayer.getTypes(), typeName);

							// update map layer as for this featurelayer the
							// type change will
							// change the features symbol.
							isTypeField = true;

						} else if (FieldType
								.determineFieldType(item.getField()) == FieldType.DATE) {
							// date

							Button dateButton = (Button) item.getView();
							value = dateButton.getText().toString();

						} else {
							// edit text

							EditText editText = (EditText) item.getView();
							value = editText.getText().toString();

						}

						// try to set the attribute value on the graphic and see
						// if it has
						// been changed
						boolean hasChanged = FeatureLayerUtils.setAttribute(
								newGraphic,
								listAdapter.featureSet.getGraphics()[0],
								item.getField(), value, listAdapter.formatter);

						// if a value has for this field, log this and set the
						// hasEdits
						// boolean to true
						if (hasChanged) {

							Log.d(TAG, "Change found for field="
									+ item.getField().getName() + " value = "
									+ value + " applyEdits() will be called");
							hasEdits = true;

							// If the change was from a Type field then set the
							// dynamic map
							// service to update when the edits have been
							// applied, as the
							// renderer of the feature will likely change
							if (isTypeField) {

								updateMapLayer = true;

							}
						}

						// check if this was a type field, if so set boolean
						// back to false
						// for next field
						if (isTypeField) {

							isTypeField = false;
						}
					}
				}

				// check there have been some edits before applying the changes
				if (hasEdits) {

					// set objectID field value from graphic held in the
					// featureset
					newGraphic.setAttributeValue(featureLayer
							.getObjectIdField(), listAdapter.featureSet
							.getGraphics()[0].getAttributeValue(featureLayer
							.getObjectIdField()));
					featureLayer.applyEdits(null, null,
							new Graphic[] { newGraphic },
							createEditCallbackListener(updateMapLayer));
				}

				// close the dialog
				dismissDialog(ATTRIBUTE_EDITOR_DIALOG_ID);

			}
		};

	}

	/**
	 * OnClick method for the Discard button
	 */
	public OnClickListener returnOnClickDiscardChangesListener() {

		return new OnClickListener() {

			public void onClick(View v) {

				// close the dialog
				dismissDialog(ATTRIBUTE_EDITOR_DIALOG_ID);

			}
		};

	}

	/**
	 * Helper method to create a CallbackListener<FeatureEditResult[][]>
	 * 
	 * @return CallbackListener<FeatureEditResult[][]>
	 */
	CallbackListener<FeatureEditResult[][]> createEditCallbackListener(
			final boolean updateLayer) {

		return new CallbackListener<FeatureEditResult[][]>() {

			public void onCallback(FeatureEditResult[][] result) {

				// check the response for success or failure
				if (result[2] != null && result[2][0] != null
						&& result[2][0].isSuccess()) {

					Log.d(AttributeEditorActivity.TAG,
							"Success updating feature with id="
									+ result[2][0].getObjectId());

					// see if we want to update the dynamic layer to get new
					// symbols for
					// updated features
					if (updateLayer) {

						ArcGISDynamicMapServiceLayer dynamicMapLayer = (ArcGISDynamicMapServiceLayer) AttributeEditorActivity.this
								.findViewById(R.id.ksfieldsDynLayer);
						dynamicMapLayer.update();

					}
				}
			}

			public void onError(Throwable e) {

				Log.d(AttributeEditorActivity.TAG, "error updating feature: "
						+ e.getLocalizedMessage());

			}
		};
	}









@Override 
public boolean onKeyDown(int keyCode, KeyEvent event) {         

//按下键盘上返回按钮 

        if(keyCode == KeyEvent.KEYCODE_BACK){ 

            new AlertDialog.Builder(this) 
                .setIcon(R.drawable.icon) 
                .setTitle(R.string.Note)                
                .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                    } 
                }) 
                .setPositiveButton(R.string.confirm ,new DialogInterface.OnClickListener() 

{ 
                    public void onClick(DialogInterface dialog, int whichButton) { 
                        finish(); 
                    } 
                }).show(); 
          
            return true; 
        }else{        
            return super.onKeyDown(keyCode, event); 
        } 
    } 


    protected void onDestroy1() { 
        super.onDestroy(); 
      
        System.exit(0); 
        //或者下面这种方式 

        //android.os.Process.killProcess(android.os.Process.myPid()); 

    }




    
    
    
    
    
    
    
    
    


}




