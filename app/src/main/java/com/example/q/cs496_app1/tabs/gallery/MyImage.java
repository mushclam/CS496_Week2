package com.example.q.cs496_app1.tabs.gallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.q.cs496_app1.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyImage implements Serializable {
    private Integer image_ids[] = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8,
            R.drawable.img9,
            R.drawable.img10,
            R.drawable.img11,
            R.drawable.img12,
            R.drawable.img13
    };

    public List<Uri> fetchAllImages(Context context) {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor imageCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

        if (imageCursor == null) {
            // Error 발생

        } else if (imageCursor.moveToFirst()) {
            do {
                String filePath = imageCursor.getString(dataColumnIndex);
                Uri imageUri = Uri.parse(filePath);
                result.add(imageUri);
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();

        return result;
    }

//    public String getImageTitle() {
//        return image_title;
//    }
//
//    public void setImageTitle(String title) {
//        this.image_title = title;
//    }

    public Integer getImageID(int i) {
        return this.image_ids[i];
    }

    public int getSize() {
        return this.image_ids.length;
    }

//    public void setImageID(Integer id) {
//        this.image_id = id;
//    }


}
