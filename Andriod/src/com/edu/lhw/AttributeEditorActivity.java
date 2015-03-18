
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

				// �ж�GPS�Ƿ���������
				if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Toast.makeText(AttributeEditorActivity.this, "�뿪��GPS����...", Toast.LENGTH_SHORT).show();
					// ���ؿ���GPS�������ý���
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
					return;
				}

				// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����
				String bestProvider = lm.getBestProvider(getCriteria(), true);
				// ��ȡλ����Ϣ
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER
				Location location = lm.getLastKnownLocation(bestProvider);
				updateView(location);
				// ����״̬
				lm.addGpsStatusListener(listener);
				// �󶨼�������4������
				// ����1���豸����GPS_PROVIDER��NETWORK_PROVIDER����
				// ����2��λ����Ϣ�������ڣ���λ����
				// ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ
				// ����4������
				// ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��

				// 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�
				// ע�⣺�˴�����׼ȷ�ȷǳ��ͣ��Ƽ���service��������һ��Thread����run��sleep(10000);Ȼ��ִ��handler.sendMessage(),����λ��
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
						locationListener);
				// ����ť�󶨵��������
			
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
	// λ�ü���
	private LocationListener locationListener = new LocationListener() {

		/**
		 * λ����Ϣ�仯ʱ����
		 */
		public void onLocationChanged(Location location) {
			updateView(location);
			Log.i(TAG, "ʱ�䣺" + location.getTime());
			Log.i(TAG, "���ȣ�" + location.getLongitude());
			Log.i(TAG, "γ�ȣ�" + location.getLatitude());
			Log.i(TAG, "���Σ�" + location.getAltitude());
		}

		/**
		 * GPS״̬�仯ʱ����
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ
			case LocationProvider.AVAILABLE:
				Log.i(TAG2, "��ǰGPS״̬Ϊ�ɼ�״̬");
				break;
			// GPS״̬Ϊ��������ʱ
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG2, "��ǰGPS״̬Ϊ��������״̬");
				break;
			// GPS״̬Ϊ��ͣ����ʱ
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG2, "��ǰGPS״̬Ϊ��ͣ����״̬");
				break;
			}
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderEnabled(String provider) {
			Location location = lm.getLastKnownLocation(provider);
			updateView(location);
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};

	// ״̬����
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// ��һ�ζ�λ
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "��һ�ζ�λ");
				break;
			// ����״̬�ı�
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "����״̬�ı�");
				// ��ȡ��ǰ״̬
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				// ��ȡ���ǿ�����Ĭ�����ֵ
				int maxSatellites = gpsStatus.getMaxSatellites();
				// ����һ��������������������
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("��������" + count + "������");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "��λ����");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "��λ����");
				break;
			}
		};
	};

	/**
	 * ʵʱ�����ı�����
	 * 
	 * @param location
	 */
	private void updateView(Location location) {
		if (location != null) {
			editText.setText("�豸λ����Ϣ\n\n���ȣ�");
			editText.append(String.valueOf(location.getLongitude()));
			editText.append("\nγ�ȣ�");
			editText.append(String.valueOf(location.getLatitude()));
		} else {
			// ���EditText����
			editText.getEditableText().clear();
		}
	}

	/**
	 * ���ز�ѯ����
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// ���ö�λ��ȷ�� Criteria.ACCURACY_COARSE�Ƚϴ��ԣ�Criteria.ACCURACY_FINE��ȽϾ�ϸ
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// �����Ƿ�Ҫ���ٶ�
		criteria.setSpeedRequired(false);
		// �����Ƿ�������Ӫ���շ�
		criteria.setCostAllowed(false);
		// �����Ƿ���Ҫ��λ��Ϣ
		criteria.setBearingRequired(false);
		// �����Ƿ���Ҫ������Ϣ
		criteria.setAltitudeRequired(false);
		// ���öԵ�Դ������
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

//���¼����Ϸ��ذ�ť 

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
        //�����������ַ�ʽ 

        //android.os.Process.killProcess(android.os.Process.myPid()); 

    }




    
    
    
    
    
    
    
    
    


}




