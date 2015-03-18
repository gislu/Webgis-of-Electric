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
 * Activity �ϴ��Ľ���
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

		// �����ҳ
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

				// ������ȷʹ��mailtoǰ׺�������ʼ���ַ,���ʹ��

				// intent.putExtra(Intent.EXTRA_EMAIL, email)�������ƥ�䲻���κ�Ӧ��
				Uri uri = Uri.parse("mailto:3802**[email]92@qq.com[/email]");
				String[] email = { "3802**[email]92@qq.com[/email]" };
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra(Intent.EXTRA_CC, email); // ������
				intent.putExtra(Intent.EXTRA_SUBJECT, "�����ʼ������ⲿ��"); // ����
				intent.putExtra(Intent.EXTRA_TEXT, "�����ʼ������Ĳ���"); // ����
				startActivity(Intent.createChooser(intent, "��ѡ���ʼ���Ӧ��"));

			}
		});

		this.camera1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(); // ���������
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
			 * ����ǵ���android���õ�intent��������ͼƬ�ļ� ��ͬʱҲ���Թ���������
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
			 * ��ѡ���ͼƬ��Ϊ�յĻ����ڻ�ȡ��ͼƬ��;��
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
					 * ���������һ���ж���Ҫ��Ϊ�˵����������ѡ�񣬱��磺ʹ�õ��������ļ��������Ļ�����ѡ����ļ��Ͳ�һ����ͼƬ�ˣ�
					 * �����Ļ��������ж��ļ��ĺ�׺��
					 * 
					 * �����ͼƬ��ʽ�Ļ�����ô�ſ���
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
		Dialog dialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ѡ��Ĳ�����Ч��ͼƬ").setPositiveButton("ȷ��",

				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						picPath = null;
					}
				}).create();

		dialog.show();
	}
}