package com.cityelc.ahu.lhw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import com.cityelc.ahu.lhw.R;


public class  LogActivity extends Activity {

	private EditText use;// �û���
	private EditText password;// ����

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
		use = (EditText) findViewById(R.id.edi1);
		password = (EditText) findViewById(R.id.edi2);
		
    }

 // ��¼��ť����
 	public void onload(View v)
 	{
 		// ����ȡ���ݵ��ַ���
 		String userName = "";
 		String userPw = "";


// 		�ж��û����Ƿ�Ϊ��
 		if (use.getText().toString().equals(""))
 		{
 			DialogDemo.builder(LogActivity.this, "������Ϣ",
 					"�û�������Ϊ�գ�");
 		}
// 		�ж������Ƿ�Ϊ��
 		else if (password.getText().toString().equals(""))
 		{
 	DialogDemo.builder(LogActivity.this, "������Ϣ",
 					"���벻��Ϊ�գ�");
 		} 
// 		�ж��û����������Ƿ���ȷ
 		else if (!(use.getText().toString()!="wqq" && password
 				.getText().toString()!="wqq"))
 		{
 			DialogDemo.builder(LogActivity.this, "������Ϣ",
 					"�û����������������������");
 		}
// 		ȫ����ȷ��ת����������
 		else
 		{
 			Intent intent = new Intent();
 			Bundle bundle = new Bundle();
 		
 			intent.putExtras(bundle);
 			intent.setClass(getApplicationContext(), AttributeEditorActivity.class);
 			startActivity(intent);
 		}
 	}
 	//ע�ᰴť����
 	
 	
 	
 	
 	
 	


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
      
        //System.exit(0); 
          android.os.Process.killProcess(android.os.Process.myPid()); 

    }


 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 
 		}

 	
 	
 	
 	
 	
 	
 	

