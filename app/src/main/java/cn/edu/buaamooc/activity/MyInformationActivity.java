package cn.edu.buaamooc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.fragment.LoginFragment;

public class MyInformationActivity extends Activity {

    private final int UPLOAD_LOCAL_IMAGE  = 0x0001;
    private final int UPLOAD_CAMERA_IMAGE = 0x0002;
    private final String SD_CARD_ROOT = "sdcard";
    //获取图片按照路径获取，否则按照流文件获取
    private final boolean RESPONSE_PATH = true;
    private ImageView mImageView = null;
    private String mImageTmpPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        //initialize tab controls

        //initialize xml skip

        //back to the xml MoocMainActivity
        ImageView information_back = (ImageView) findViewById(R.id.Information_Left_Return);
        information_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(0);
                MyInformationActivity.this.finish();
            }
        });
        //选择用户头像
        ImageView mImageView = (ImageView)findViewById(R.id.userphoto);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.userphoto:
                        uploadImageMethod();
                        break;
                }
            }

        });

        //skip to the xml MoocMainActivity
        TextView mycourse = (TextView) findViewById(R.id.button_mycourse);
        mycourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(1);
                MyInformationActivity.this.finish();
            }
        });
        //skip to the xml MyDownloads
        TextView mydownloads = (TextView) findViewById(R.id.button_mydownloads);
        mydownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,MyDownloads.class);
                startActivityForResult(intent,1);
            }
        });
        //skip to the xml AccountSetting
        TextView accountsetting = (TextView) findViewById(R.id.button_accountsetting);
        accountsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AccountSetting.class);
                startActivityForResult(intent, 1);
            }
        });
        //skip to the xml AppSetting
        TextView appsetting = (TextView) findViewById(R.id.button_appsetting);
        appsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AppSetting.class);
                startActivityForResult(intent,1);
            }
        });
        //skip to the xml AboutUs
        TextView aboutus = (TextView) findViewById(R.id.button_aboutus);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AboutUs.class);
                startActivityForResult(intent,1);
            }
        });
        //back to the xml LoginFragment
        TextView logout = (TextView) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(2);
                MyInformationActivity.this.finish();
            }
        });

    }


    protected void uploadImageMethod(){
        String[] items = {"本地图片","手机拍照"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择头像");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        uploadLocalImage();
                        break;
                    case 1:
                        uploadFromCamera();
                        break;
                }
            }
        });
        builder.create().show();
    }


    /*
     *  * 获取本地图片
     *  */
    protected void uploadLocalImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_LOCAL_IMAGE);
        onActivityResult(UPLOAD_LOCAL_IMAGE, RESULT_OK, intent);
    }

    /*
     *  * 调用系统照相机
      *  */
    protected void uploadFromCamera(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageTmpPath = getImageFullPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImageTmpPath)));
        startActivityForResult(intent, UPLOAD_CAMERA_IMAGE);
        onActivityResult(UPLOAD_CAMERA_IMAGE, RESULT_OK, intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    // super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case UPLOAD_LOCAL_IMAGE:
                    uploadLocalImageResponse(data);

                    break;
                case UPLOAD_CAMERA_IMAGE:
                    uploadCameraImageResponse(data);
                    break;
            }
        }

    }

    /*
    * * 完成选中本地图片调用
    *
    * */

    protected void uploadLocalImageResponse(Intent data){
        Uri uri = data.getData();
        if(RESPONSE_PATH){
            getLocalImageFromPath(uri);
        }else{
            getLocalImageFromStream(uri);
        }
    }


     /*
     * * 通过流文件加载本地图片
      * */
     protected void getLocalImageFromStream(Uri uri){
         ContentResolver contentResolver = this.getContentResolver();
         Bitmap bmp = null;
         try {
             bmp = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
         } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         //
                e.printStackTrace();
         }
         mImageView.setImageBitmap(bmp);
     }
     /*
     * * 通过图片路径加载本地图片
      * */
     protected void getLocalImageFromPath(Uri uri){
         String[] proj = {MediaStore.Images.Media.DATA};
         CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

         Cursor cursor = cursorLoader.loadInBackground();
         int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         String path = cursor.getString(columnIndex);
         Bitmap bmp = BitmapFactory.decodeFile(path);
         mImageView.setImageBitmap(bmp);
     }
      /*
      * * 系统照相机完成拍照后调用
       * */
      protected void uploadCameraImageResponse(Intent data){
          ContentResolver resolver = getContentResolver();
          File f = new File(mImageTmpPath);
          try {
              Uri capturedImage = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), f.getAbsolutePath(), null, null));
              Bitmap bmp = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(capturedImage.toString())));
              mImageView.setImageBitmap(bmp);
              storeBitmap(bmp,getImageFullPath());
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          }
          //      Bundle bundle = data.getExtras();
          //      Bitmap bmp = (Bitmap)bundle.get("data");
          //      mImageView.setImageBitmap(bmp);
          //      storeBitmap(bmp,"ImageUploadTest");
          //

      }
      /*
       *  * 存储图片到本地
        *  */
      protected void storeBitmap(Bitmap bmp, String storePath){
          if(sdCardAvailable()){
              StringBuilder sb = new StringBuilder();
              sb.delete(0, sb.length());
              sb.append(SD_CARD_ROOT);
              sb.append("/");
              sb.append(storePath);
              FileOutputStream fos = null;

              File file = new File(sb.toString());
              file.mkdir();
              sb.append("/");
              sb.append(getUploadImageName());
              sb.append(".png");
              try {
                  fos = new FileOutputStream(sb.toString());
                  bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
              } catch (FileNotFoundException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              } finally{
                  try {
                      fos.flush();
                      fos.close();
                  } catch (IOException e) {

                      // TODO Auto-generated catch block
                      //
                      e.printStackTrace();
                  }
              }
          }

      }
       /*
       * * SD是否可用
       * */
       protected boolean sdCardAvailable(){
           String status = Environment.getExternalStorageState();
           return (status == Environment.MEDIA_MOUNTED);
       }



    /*
      *  * 获取完整路径
      *  */
     protected String getImageFullPath(){
         StringBuilder sb = new StringBuilder();
         sb.delete(0, sb.length());
         sb.append(SD_CARD_ROOT);
         sb.append("/ImageUploadTest/");
         sb.append(getUploadImageName());
         sb.append(".jpg");
         return sb.toString();
     }

    /*
     *  * 获取系统时间
     *  * 格式：IMG_xxxx年xx月xx日_xx时xx分xx秒
     *
     *  */
    protected String getUploadImageName(){
        Calendar c = Calendar.getInstance();

        String str = String.format("IMG_%04d%02d%02d_%02d%02d%02d",
                c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),
                c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));

        return str;
    }

    /*  * 提示信息
    * */
    protected void showToastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
