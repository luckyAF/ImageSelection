package com.luckyaf.imageselction_master.imageselection;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckyaf.imageselction_master.R;
import com.luckyaf.imageselection.ImageData;
import com.luckyaf.imageselection.ImageSelection;
import com.luckyaf.imageselection.SelectionCreator;

public class TestImageSelectionActivity extends AppCompatActivity implements View.OnClickListener{
    private Context mContext;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_test_image_selection);
        Button button = (Button)findViewById(R.id.btn_pick);
        mTextView = (TextView)findViewById(R.id.tv_selected_image) ;


        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_pick:
                ImageSelection.getInstance()
                        .from(this)
                        .capture(true)
                        .needGif(true)
                        .savePublic(true)
                        .translucent(true)
                        .maxSelectable(15)
                        .themeColor(Color.parseColor("#1E8AE8"))
                        .selectWord("发送")
                        .getImage(new SelectionCreator.ImageGetter() {
                            @Override
                            public void getImageSuccess(ImageData imageData) {
                                mTextView.setText(imageData.toString());
                                Toast.makeText(mContext,"size" + imageData.size(),Toast.LENGTH_SHORT).show();
                                ((ImageView)findViewById(R.id.img_select)).setImageURI(imageData.getImage());
                            }
                        })

                        .start();
                break;
            default:
                break;
        }
    }
}
