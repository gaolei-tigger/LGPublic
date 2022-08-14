package com.example.textviewdebug.TextLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.textviewdebug.R;

import java.util.Set;

public class TextViewTestActivity extends AppCompatActivity {
    private TextView tv_textview;
    private TextView tv_textview_status;
    private ListView lv_options;
    private CheckBox cb_uniform;
    private CheckBox cb_none;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_test);
        // textview
        tv_textview = findViewById(R.id.textView_test);
        tv_textview_status = findViewById(R.id.textView_status);
        // listview_options
        lv_options = findViewById(R.id.listview_options);
        String[] data = new String[] {"bold","italic","bold_italic","normal","...","...","...","..."};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(TextViewTestActivity.this,android.R.layout.simple_list_item_1,data);
        lv_options.setAdapter(adapter);
        lv_options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_textview.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                tv_textview_status.setText("Style:" + data[position] + " of all:" + data.length);
                switch (position) {
                    case 0:
                        tv_textview.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        break;
                    case 1:
                        tv_textview.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                        break;
                    case 2:
                        tv_textview.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
                        break;
                    case 3:
                        tv_textview.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        break;
                    default:
                        break;
                }
            }
        });
        // cb uniform
        cb_uniform = findViewById(R.id.checkBox_uniform);
        cb_uniform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_uniform.isChecked()) {
                    tv_textview.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    cb_none.setChecked(false);
                }
            }
        });
        // cb_none
        cb_none = findViewById(R.id.checkBox_none);
        cb_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_none.isChecked()) {
                    tv_textview.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
                    // Bug(无法从uniform恢复到none)
                    cb_uniform.setChecked(false);
                }
            }
        });
    }
}