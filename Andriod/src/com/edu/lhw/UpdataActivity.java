package com.cityelc.ahu.lhw;

import java.io.File;

import com.cityelc.ahu.lhw.R;
import com.cityelc.ahu.lhw.UploadUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Activity 上传的界面
 */

public class UpdataActivity extends Activity implements OnClickListener {

	private static final String TAG = "uploadImage";
	private static String requestURL = "http://192.168.1.14:8080/SetBlobData/img!up";
	private Button selectImage, uploadImage;
	private ImageView imageView;
	private ImageButton mImageButton1;
	private ImageButton email1;
	private ImageButton camera1;
	private EditText mEditText1;
	private WebView mWebView1;
	private String picPath = null;

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.upload);
		selectImage = (Button) this.findViewById(R.id.selectImage);
		uploadImage = (Button) this.findViewById(R.id.uploadImage);
		selectImage.setOnClickListener(this);
		uploadImage.setOnClickListener(this);
		imageView = (ImageView) this.findViewById(R.id.imageView);
		this.mImageButton1 = (ImageButton) this.findViewById(R.id.imageButton1);
		this.email1 = (ImageButton) this.findViewById(R.id.email);
		this.camera1 = (ImageButton) this.findViewById(R.id.camera1);
		this.mEditText1 = (EditText) this.findViewById(R.id.EditText1);
		this.mWebView1 = (WebView) this.findViewById(R.id.WebView1);

		// 浏览网页
		this.mImageButton1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String strURI = (mEditText1.getText().toString());

				mWebView1.loadUrl(getString(R.string.htttp) + strURI);

				Toast.makeText(UpdataActivity.this,
						getString(R.string.htttp) + strURI, Toast.LENGTH_LONG)
						.show();
			}
		});

		this.email1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// 必须明确使用mailto前缀来修饰邮件地址,如果使用

				// intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
				Uri uri = Uri.parse("mailto:3802**[email]92@qq.com[/email]");
				String[] email = { "3802**[email]92@qq.com[/email]" };
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
				intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
				intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
				startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

			}
		});

		this.camera1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(); // 调用照相机
				intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
				startActivity(intent);

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectImage:

			/***
			 * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
			 */
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
			break;
		case R.id.uploadImage:

			File file = new File(picPath);

			if (file != null) {
				String request = UploadUtil.uploadFile(file, requestURL);
				uploadImage.setText(request);
			}

			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {

			/**
			 * 当选择的图片不为空的话，在获取到图片的途径
			 */

			Uri uri = data.getData();
			Log.e(TAG, "uri = " + uri);
			try {
				String[] pojo = { MediaStore.Images.Media.DATA };

				Cursor cursor = managedQuery(uri, pojo, null, null, null);
				if (cursor != null) {
					ContentResolver cr = this.getContentResolver();
					int colunm_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(colunm_index);

					/***
					 * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
					 * 这样的话，我们判断文件的后缀名
					 * 
					 * 如果是图片格式的话，那么才可以
					 */

					if (path.endsWith("jpg") || path.endsWith("png")) {
						picPath = path;
						Bitmap bitmap = BitmapFactory.decodeStream(cr
								.openInputStream(uri));
						imageView.setImageBitmap(bitmap);
					} else {
						alert();
					}
				} else {
					alert();
				}
			} catch (Exception e) {
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void alert()

	{
		Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您选择的不是有效的图片").setPositiveButton("确定",

				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						picPath = null;
					}
				}).create();

		dialog.show();
	}
}