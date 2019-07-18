package com.zhanghaochen.smalldemos.demos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.framework.BaseActivity;
import com.zhanghaochen.smalldemos.customer.views.DoodleView;

public class EditPictureActivity extends BaseActivity {

    private DoodleView mDoodleView;

    private RadioGroup mGraphGroup, mModeGroup;

    private Button mRevertBtn;

    @Override
    protected void handleMessage(Message message) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        initViews();
    }

    private void initViews() {
        mDoodleView = findViewById(R.id.doodle_view);
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_example);

        // 动态设置宽高比
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mDoodleView.getLayoutParams();
        params.dimensionRatio = "W," + originBitmap.getWidth() + ":" + originBitmap.getHeight();
        // 加载图片
        if (mDoodleView != null && !originBitmap.isRecycled()) {
            mDoodleView.setOriginBitmap(originBitmap);
        }

        mRevertBtn = findViewById(R.id.edit_revert);
        mRevertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = mDoodleView.revertPath();
//                if (result <= 0) {
//                    Toast.makeText(EditPictureActivity.this, "不能再删除了", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        // 初始化radioButtons
        mModeGroup = findViewById(R.id.edit_funcs);
        mModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.edit_func1) {
                    mDoodleView.setEditable(true);
                    mGraphGroup.setVisibility(View.VISIBLE);
                    mDoodleView.setMode(DoodleView.MODE.GRAPH_MODE);
                } else if (checkedId == R.id.edit_func2) {
                    mDoodleView.setEditable(true);
                    mGraphGroup.setVisibility(View.GONE);
                    mDoodleView.setMode(DoodleView.MODE.DOODLE_MODE);

                } else if (checkedId == R.id.edit_func3) {
                    mDoodleView.setEditable(true);
                    mGraphGroup.setVisibility(View.GONE);
                    mDoodleView.setMode(DoodleView.MODE.MOSAIC_MODE);
                }
            }
        });

        // 图形选择器
        mGraphGroup = findViewById(R.id.graph_picker);
        mGraphGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.graph01) {
                    setDoodleGraph(DoodleView.GRAPH_TYPE.LINE);
                } else if (checkedId == R.id.graph02) {
                    setDoodleGraph(DoodleView.GRAPH_TYPE.OVAL);
                } else if (checkedId == R.id.graph03) {
                    setDoodleGraph(DoodleView.GRAPH_TYPE.RECT);
                } else if (checkedId == R.id.graph04) {
                    setDoodleGraph(DoodleView.GRAPH_TYPE.ARROW);
                } else if (checkedId == R.id.graph05) {
                    setDoodleGraph(DoodleView.GRAPH_TYPE.DIRECT_LINE);
                }
            }
        });

        mDoodleView.setCallBack(new DoodleView.DoodleCallback() {
            @Override
            public void onDrawStart() {
                mGraphGroup.setVisibility(View.GONE);
            }

            @Override
            public void onDrawing() {

            }

            @Override
            public void onDrawComplete() {
                if (mModeGroup.getCheckedRadioButtonId() == R.id.edit_func1) {
                    mGraphGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRevertStateChanged(boolean canRevert) {
                if (mRevertBtn != null) {
                    mRevertBtn.setEnabled(canRevert);
                }
            }
        });
    }

    private void setDoodlePaintColor(int color) {
        if (mDoodleView != null) {
            mDoodleView.setPaintColor(color);
        }
    }

    private void setDoodleGraph(DoodleView.GRAPH_TYPE graphType) {
        if (mDoodleView != null) {
            mDoodleView.setGraphType(graphType);
        }
    }
}
