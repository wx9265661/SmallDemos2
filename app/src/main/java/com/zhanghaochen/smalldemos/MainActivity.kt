package com.zhanghaochen.smalldemos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zhanghaochen.smalldemos.demos.*
import com.zhanghaochen.smalldemos.demos.recyclerstudy.RecyclerViewDemoActivity
import com.zhanghaochen.smalldemos.widget.floatview.FloatViewUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        // test01 函数图像
        findViewById<View>(R.id.demo01).setOnClickListener(this)
        findViewById<View>(R.id.demo02).setOnClickListener(this)
        findViewById<View>(R.id.demo03).setOnClickListener(this)
        findViewById<View>(R.id.demo04).setOnClickListener(this)
        findViewById<View>(R.id.demo05).setOnClickListener(this)
        findViewById<View>(R.id.demo06).setOnClickListener(this)
        findViewById<View>(R.id.demo07).setOnClickListener(this)
        findViewById<View>(R.id.demo08).setOnClickListener(this)

        demo09.setOnClickListener {
            startActivity<RecyclerViewDemoActivity>()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.demo01 -> startActivity(Intent(this@MainActivity, FxActivity::class.java))
            R.id.demo02 -> startActivity(Intent(this@MainActivity, ConstraintLayoutDemoActivity::class.java))
            R.id.demo03 -> startActivity(Intent(this@MainActivity, EditPictureActivity::class.java))
            R.id.demo04 -> startActivity(Intent(this@MainActivity, CircleBoardActivity::class.java))
            R.id.demo05 -> startActivity(Intent(this@MainActivity, MyRecyclerViewActivity::class.java))
            R.id.demo06 -> FloatViewUtil.getTestFloatIns().show()
            R.id.demo07 -> startActivity(Intent(this@MainActivity, SomeChartShowActivity::class.java))
            R.id.demo08 -> startActivity(Intent(this@MainActivity, MyViewsDisplayActivity::class.java))
            else -> {
            }
        }
    }
}
