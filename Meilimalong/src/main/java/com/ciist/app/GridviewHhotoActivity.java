package com.ciist.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


import com.ciist.customview.xlistview.IGridView;
import com.ciist.toolkits.PhotoGridViewAdapter;
import com.ciist.util.ImageTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridviewHhotoActivity extends Activity {
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;

    private Button choseBtn;
    private ImageView imageView;
    private IGridView gridview;
    private PhotoGridViewAdapter adapter;

    private static final int SCALE = 2;//照片缩小比例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gridview_hhoto);
        initView();
    }

    private void initView() {

        choseBtn = (Button) findViewById(R.id.chose_btn);
        choseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicturePicker(GridviewHhotoActivity.this);
            }
        });

        imageView = (ImageView) findViewById(R.id.show_img);

        gridview = (IGridView) findViewById(R.id.photo_gridview);
        adapter = new PhotoGridViewAdapter(this);
        gridview.setAdapter(adapter);

    }


    /**
     * 弹出dialog选择拍照或者是相册去照片
     * @param context
     */
    public void showPicturePicker(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //    String photoName = getPhotoFileName();   //用当前时间来命名照片
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        Log.v("test","-----photoUrl-------" + imageUri);
                        //  openCameraIntent.putExtra("name",photoName);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }


    @Override
    /**
     *选择照片或者拍照后返回的结果并处理 主要包括保存处理或压缩图片处理。
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  mShowLayout.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    //   String photoName = data.getStringExtra("name");
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                 //   imageView.setImageBitmap(newBitmap);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    //   iv_image.setImageBitmap(newBitmap);
                    String photoName = getPhotoFileName();  //photo name
                    String photoPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    ImageTools.savePhotoToSDCard(newBitmap, photoPath, photoName);   //save photo
                 //   putPath(photoPath+"/"+photoName);
                    Log.e("test","--TAKE_PICTURE--path--"+photoPath+"/"+photoName);
                //    Glide.with(this).load(photoPath+"/"+photoName).into(imageView);
                    imageView.setImageBitmap(ImageTools.getPhotoFromSDCard(photoPath,photoName));
                    putPath(photoPath+"/"+photoName);
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();

                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    Log.e("test", "++onActivityResult+getDataString+" + data.getDataString() + "  getData " + data.getData());
                //    putPath(originalUri);  //
                    Log.v("test","-----CHOOSE_PICTURE--originalUri-----"+originalUri);
                    Log.e("test","++++++++++++++");
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            //    iv_image.setImageBitmap(smallBitmap);
                            String photoName1 = getPhotoFileName();  //photo name
                            String photoPath1 = Environment.getExternalStorageDirectory().getAbsolutePath();

                            ImageTools.savePhotoToSDCard(smallBitmap, photoPath1, photoName1);   //save photo并压缩
                            putPath(photoPath1+"/"+photoName1);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    ArrayList<String> pathLists = null;
    private void putPath(String path){

        if ( null == pathLists){
            pathLists = new ArrayList<>();
        }
        pathLists.add(path);
        adapter.setData(pathLists);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) ;
    }
}
