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

	private EditText use;// 用户名
	private EditText password;// 密码

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
		use = (EditText) findViewById(R.id.edi1);
		password = (EditText) findViewById(R.id.edi2);
		
    }

 // 登录按钮监听
 	public void onload(View v)
 	{
 		// 定义取数据的字符串
 		String userName = "";
 		String userPw = "";


// 		判断用户名是否为空
 		if (use.getText().toString().equals(""))
 		{
 			DialogDemo.builder(LogActivity.this, "错误信息",
 					"用户名不能为空！");
 		}
// 		判断密码是否为空
 		else if (password.getText().toString().equals(""))
 		{
 	DialogDemo.builder(LogActivity.this, "错误信息",
 					"密码不能为空！");
 		} 
// 		判断用户名和密码是否正确
 		else if (!(use.getText().toString()!="wqq" && password
 				.getText().toString()!="wqq"))
 		{
 			DialogDemo.builder(LogActivity.this, "错误信息",
 					"用户名或密码错误，请重新输入");
 		}
// 		全部正确跳转到操作界面
 		else
 		{
 			Intent intent = new Intent();
 			Bundle bundle = new Bundle();
 		
 			intent.putExtras(bundle);
 			intent.setClass(getApplicationContext(), AttributeEditorActivity.class);
 			startActivity(intent);
 		}
 	}
 	//注册按钮监听
 	
 	
 	
 	
 	
 	


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
      
        //System.exit(0); 
          android.os.Process.killProcess(android.os.Process.myPid()); 

    }


 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 
 		}

 	
 	
 	
 	
 	
 	
 	

