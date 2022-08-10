package com.example.textviewdebug.TextLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.textviewdebug.R;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ScaleXSpan;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextLayoutActivity extends AppCompatActivity {

    private TextView tv_font_sample7;
    private Button btn_font_process7;
    private TextView tv_font_sample_opt7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_layout);
        //
        tv_font_sample7 = findViewById(R.id.textView_font7);
        tv_font_sample_opt7 = findViewById(R.id.textView_font7_opt);
        btn_font_process7 = findViewById(R.id.button_font7);
        btn_font_process7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence sample = tv_font_sample7.getText();
                // doprocess
                SpannableStringBuilder sample_opt = doSpaceOpt(sample);
                tv_font_sample_opt7.setText(sample_opt);
            }
        });
    }

    private String preSpaceOpt(CharSequence rawText) {
        String rawString = rawText.toString();
        String[] results = rawString.split("[\\u4e00-\\u9fa5]{1}[\\s]{1}[A-Za-z0-9]{1}|[A-Za-z0-9]{1}[\\s]{1}[\\u4e00-\\u9fa5]{1}");
        List<Pair<Integer,Integer>> indices = new ArrayList<>();
        List<Integer> spaceIndices = new ArrayList<>();
        StringBuilder sBuilder = new StringBuilder().append(rawString);
        // Collect indices
        for (int i=0; i < results.length; i+=2) { // process double end from LTR, skip second hit, care for end
            if (results[i].isEmpty()) {
                continue;
            }
            int index_left = rawString.indexOf(results[i]);
            int index_right = index_left + results[i].length() -1;
            indices.add(new Pair(index_left, index_right));
            int space_left = index_left - 2;
            int space_right = index_right + 2;
            if (space_left > 0) {
                // avoid head
                spaceIndices.add(space_left);
            }
            if (space_right < rawString.length() - 1) {
                // avoid tail
                spaceIndices.add(space_right);
            }
        }
        for (int i = spaceIndices.size() - 1; i >= 0; i--) { // remove the matched spaces by reverse order
            int spaceIndex = spaceIndices.get(i);
            sBuilder.replace(spaceIndex,spaceIndex + 1, ""); // TODO(maybe assamble a new string is more effective)
        }
        return sBuilder.toString();
    }

    private SpannableStringBuilder doSpaceOpt(CharSequence origin) {
        String oString = origin.toString();
        oString = preSpaceOpt(oString);
        // oString.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
        String[] results = oString.split("[\\u4e00-\\u9fa5]{1}[A-Za-z0-9]{1}|[A-Za-z0-9]{1}[\\u4e00-\\u9fa5]{1}");
        List<Pair<Integer,Integer>> indices = new ArrayList<>();
        StringBuilder sBuilder = new StringBuilder().append(oString);
        int posAcc = 0; // HINT(do not use String.indexOf, it will fail with repeat slice)
        for (int i=0; i < results.length; i+=2) { // process double end from LTR, skip second hit, care for end
            if (results[i].isEmpty()) {
                continue;
            }
            int index_left = 0;
            int index_right = 0;
            if (i == 0) { // first slice
                index_left = 0;
                posAcc += results[i].length() + 1;
                index_right = posAcc - 1;
                // handle skipped slice
                if (i + 1 < results.length - 1) { // avoid array overflow
                    posAcc += results[i+1].length() + 2;
                }
                if (i + 1 == results.length - 1) { // avoid array overflow
                    posAcc += results[i+1].length() + 1; // acc the last skipped slice
                }
            } else {
                index_left = posAcc;
                if (i == results.length - 1) { // last slice
                    posAcc += results[i].length() + 1;
                } else {
                    posAcc += results[i].length() + 2;
                }
                index_right = posAcc - 1;
                // handle skipped slice
                if (i + 1 < results.length - 1) { // avoid array overflow
                    posAcc += results[i+1].length() + 2; // acc the skipped slice
                }
                if (i + 1 == results.length - 1) { // avoid array overflow
                    posAcc += results[i+1].length() + 1; // acc the last skipped slice
                }
            }
            indices.add(new Pair(index_left, index_right));
        }
        int spaceOpted = 0;
        List<Integer> finishIndices = new ArrayList<>();
        for (int i=0; i < indices.size(); i++) {
            // handle segment left
            int index_left = indices.get(i).first;
            int index_right = indices.get(i).second;
            if (index_left <= 0) {
                // skip head
            } else {
                sBuilder.insert(index_left + spaceOpted , " ");
                finishIndices.add(index_left + spaceOpted);
                spaceOpted++;
            }
            // handle segment right
            if (index_right >= oString.length()) {
                // skip tail
            } else {
                sBuilder.insert(index_right + 1 + spaceOpted, " ");
                finishIndices.add(index_right + 1 + spaceOpted);
                spaceOpted++;
            }
        }
        // custom space
        SpannableStringBuilder ssb = new SpannableStringBuilder(sBuilder.toString());
        for (int i =0; i < finishIndices.size(); i++) {
            int targetIndex = finishIndices.get(i);
            ssb.setSpan(new ScaleXSpan(0.75f),
                    targetIndex, targetIndex + 1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }

}