package com.example.textviewdebug.TextLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.textviewdebug.R;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ScaleXSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TextLayoutActivity extends AppCompatActivity {

    private  String TAG = "TextLayoutActivity";
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
                long t0 = System.currentTimeMillis();
                CharSequence sample_opt1 = doSpaceOpt(sample);
                long t1 = System.currentTimeMillis();
                CharSequence sample_opt = makeSpaceOpt(sample);
                long t2 = System.currentTimeMillis();
                tv_font_sample_opt7.setText(sample_opt);
                Log.d(TAG, "time cost: do:" + (t1-t0) + "make:" + (t2-t1));
                //tv_font_sample7.setText(sample.toString() + sample); // test
                tv_font_sample7.setText(sample_opt);
            }
        });
    }

    /**
     * 1. 存在英文间前两行空格误删除问题
     *
     */
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
            if (sBuilder.charAt(spaceIndex) != ' ') { // double check
                Log.d(TAG, "double check fast fail, FIXME");
                continue;
            }
            sBuilder.replace(spaceIndex,spaceIndex + 1, ""); // TODO(maybe assamble a new string is more effective)
        }
        return sBuilder.toString();
    }
    // 存在单字缺陷无法解决
    private CharSequence doSpaceOpt(CharSequence origin) {
//        if (origin instanceof Spanned) {
//            return origin; // only process clear text
//        }
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

    //////////////////////////////////////////////////////////////////////
    // 常规方法(BL)效率反而更高
    private CharSequence preMakeSpaceOpt(CharSequence origin) {
        ArrayList<Integer> spaceIndices = new ArrayList<Integer>();
        IntStream left_char = origin.codePoints();
        //[\u4e00-\u9fa5]{1}[A-Za-z0-9]{1}|[A-Za-z0-9]{1}[\u4e00-\u9fa5]{1}
        int[] codePoints = left_char.toArray();
        for (int i = 1; i < codePoints.length - 1; i++) {
            if (codePoints[i] != 32 /* ASCII space */) {
                continue;
            }
            if (isInHansRange(codePoints[i-1]) && isInAlphabet(codePoints[i+1])) { // HANS-space-ENGLISH
                spaceIndices.add(i);
                continue;
            }
            if (isInAlphabet(codePoints[i-1]) && isInHansRange(codePoints[i+1])) { // ENGLISH-space-HANS
                spaceIndices.add(i);
                continue;
            }
            if (isInHansRange(codePoints[i-1]) && Character.isDigit(codePoints[i+1])) { // HANS-space-NUM
                spaceIndices.add(i);
                continue;
            }
            if (Character.isDigit(codePoints[i-1]) && isInHansRange(codePoints[i+1])) { // NUM-space-HANS
                spaceIndices.add(i);
                continue;
            }
        }
        StringBuilder sBuilder = new StringBuilder().append(origin);
        for (int i = spaceIndices.size() - 1; i >= 0; i--) {
            int spaceIndex = spaceIndices.get(i);
            if (sBuilder.charAt(spaceIndex) != ' ') { // double check
                Log.d(TAG, "double check slow fail, FIXME");
                continue;
            }
            sBuilder.replace(spaceIndex,spaceIndex + 1, "");
        }
        Log.d(TAG, "rawtext length:" + codePoints.length + "trimed length:" + sBuilder.length());
        return sBuilder.toString();
    }

    // 常规方法
    private CharSequence makeSpaceOpt(CharSequence origin) {
//        if (origin instanceof Spanned) {
//            return origin; // only process clear text
//        }
        //CharSequence oString = preMakeSpaceOpt(origin);
        CharSequence oString = preSpaceOpt(origin);
        ArrayList<Integer> spaceIndices = new ArrayList<Integer>();
        IntStream left_char = oString.codePoints();
        //[\u4e00-\u9fa5]{1}[A-Za-z0-9]{1}|[A-Za-z0-9]{1}[\u4e00-\u9fa5]{1}
        int[] codePoints = left_char.toArray();
        for (int i = 0; i < codePoints.length - 1; i++) {
            if (isInHansRange(codePoints[i]) && isInAlphabet(codePoints[i+1])) { // HANS-ENGLISH
                spaceIndices.add(i); // record
                continue;
            }
            if (isInAlphabet(codePoints[i]) && isInHansRange(codePoints[i+1])) { // ENGLISH-HANS
                spaceIndices.add(i);
                continue;
            }
            if (isInHansRange(codePoints[i]) && Character.isDigit(codePoints[i+1])) { // HANS-NUM
                spaceIndices.add(i);
                continue;
            }
            if (Character.isDigit(codePoints[i]) && isInHansRange(codePoints[i+1])) { // NUM-HANS
                spaceIndices.add(i);
                continue;
            }
        }
        //
        StringBuilder sBuilder = new StringBuilder().append(oString);
        ArrayList<Integer> updatedIndices = new ArrayList<Integer>();
        int spaceOpt = 0;
        for (int i = 0; i < spaceIndices.size(); i++) {
            int insertAt = spaceIndices.get(i)+ 1 + spaceOpt;
            sBuilder.insert(insertAt, " ");
            spaceOpt++;
            updatedIndices.add(insertAt);
        }
        // custom space
        SpannableStringBuilder ssb = new SpannableStringBuilder(sBuilder.toString());
        for (int i =0; i < updatedIndices.size(); i++) {
            int targetIndex = updatedIndices.get(i);
            ssb.setSpan(new ScaleXSpan(0.75f),
                    targetIndex, targetIndex + 1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }

    private boolean isInHansRange(long cCode) {
        return  HANS_MIN <= cCode && cCode <= HANS_MAX;
    }

    private boolean isInAlphabet(int cCode) {
        return EN_A <= cCode && cCode <= EN_Z || EN_a <= cCode && cCode <= EN_z;
    }

    static long HANS_MIN = 0x4e00;
    static long HANS_MAX = 0x9fa5;
    static int EN_A = Integer.valueOf('A');
    static int EN_Z = Integer.valueOf('Z');
    static int EN_a = Integer.valueOf('a');
    static int EN_z = Integer.valueOf('z');
}