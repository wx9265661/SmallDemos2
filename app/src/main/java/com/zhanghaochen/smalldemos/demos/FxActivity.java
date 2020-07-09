package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.utils.SysUtils;
import com.zhanghaochen.smalldemos.widget.FxView;

public class FxActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mFormula;
    private EditText mMaxX;
    private EditText mMinX;
    private Button mGo;
    private FxView mFxView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fx);

        initViews();
    }

    private void initViews() {
        mFormula = findViewById(R.id.functions_text);
        mMaxX = findViewById(R.id.max_value);
        mMinX = findViewById(R.id.min_value);
        mGo = findViewById(R.id.go_btn);
        mFxView = findViewById(R.id.fx_view);

        mGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_btn:
                String formula = mFormula.getText().toString().trim();
                String maxX = mMaxX.getText().toString().trim();
                String minX = mMinX.getText().toString().trim();
                if (!SysUtils.isEmpty(formula) && !SysUtils.isEmpty(maxX) && !SysUtils.isEmpty(minX)) {
                    mFxView.setFormula(formula, maxX, minX);
                } else {
                    Toast.makeText(getBaseContext(), "把三个都输完哟~", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
