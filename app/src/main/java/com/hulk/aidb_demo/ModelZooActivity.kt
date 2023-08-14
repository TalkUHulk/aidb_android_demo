package com.hulk.aidb_demo

//import com.hulk.aidb_demo.modelzoo.DiscreteScrollViewOptions

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dd.CircularProgressButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.hanks.htextview.scale.ScaleTextView
import com.hulk.aidb_demo.modelzoo.Model
import com.hulk.aidb_demo.modelzoo.ModelZoo
import com.hulk.aidb_demo.modelzoo.ModelZooAdapter
import com.hulk.aidb_demo.utils.copyAssetAndWrite
import com.hulk.aidb_demo.utils.getFileMD5
import com.hulk.aidb_demo.utils.unzip
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.DiscreteScrollView.OnItemChangedListener
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import net.frakbot.jumpingbeans.JumpingBeans
import java.io.File
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
import com.hulk.aidb_demo.R as AR


class ModelZooActivity : AppCompatActivity(), OnItemChangedListener<ModelZooAdapter.ViewHolder?>,
    View.OnClickListener {
    lateinit var data: List<Model>
    private lateinit var currentModel: TextView
    private lateinit var currentBackend: TextView
//    private lateinit var rateItemButton: ImageView
    lateinit var itemPicker: DiscreteScrollView
    lateinit var circularProgressButton: CircularProgressButton
    private var infiniteAdapter: InfiniteScrollAdapter<*>? = null

    private var mBackend: Int = 2
    private var mMode: Int = 0
    private var modelsMd5Passed = false

    lateinit var drawerLayout: DrawerLayout

    val advertisement = listOf("This is Android Demo of AIDB", "If you find it is helpful to you", "Don't forget give me a star")
    var mAdvID = 0
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if(msg.what < -1 || msg.what > 100){
                if(msg.what == 12580){
                    val scaleTextView: ScaleTextView = findViewById(AR.id.stv)
                    if(mAdvID >= advertisement.size) mAdvID = 0
                    scaleTextView.animateText(advertisement[mAdvID])
                    mAdvID ++

                } else{
                    circularProgressButton.isIndeterminateProgressMode = true;

                }
            } else {
                circularProgressButton.progress = msg.what
            }


        }
    }

//    override fun onRestart() {
//        super.onRestart()
//        Log.d("========@@@", "onRestart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//       Log.d("========@@@", "onResume")
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(AR.layout.activity_modelzoo)

        // hide ActionBar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // 同时隐藏状态栏和导航栏
//        val controller = getInsetsController(window, window.decorView)
//        controller.hide(WindowInsetsCompat.Type.systemBars())

//        controller.hide(WindowInsetsCompat.Type.statusBars());
//        controller.hide(WindowInsetsCompat.Type.navigationBars());


        circularProgressButton = findViewById(AR.id.item_btn_run)
        currentModel = findViewById(AR.id.item_model)
        currentBackend = findViewById(AR.id.item_backend)
//        rateItemButton = findViewById(AR.id.item_btn_rate)
        data = ModelZoo.getData()
        itemPicker = findViewById(AR.id.item_picker)
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL)
        itemPicker.addOnItemChangedListener(this)
        infiniteAdapter = InfiniteScrollAdapter.wrap(ModelZooAdapter(data))
        itemPicker.adapter = infiniteAdapter
        itemPicker.setItemTransitionTimeMillis(100)
        itemPicker.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )
        onItemChanged(data[0])

        findViewById<View>(AR.id.home).setOnClickListener(this)
        findViewById<View>(AR.id.item_btn_run).setOnClickListener(this)
        findViewById<View>(AR.id.btn_mode).setOnClickListener(this)
        findViewById<View>(AR.id.btn_backend).setOnClickListener(this)

        drawerLayout = findViewById(AR.id.drawerLayout)

        val navigationView: NavigationView = findViewById(AR.id.navView)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                AR.id.navGitHub -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://github.com/TalkUHulk/ai.deploy.box")
                    startActivity(intent)
                    Log.d("===>>", "navGitHub")
                    true}
                AR.id.navMail -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://github.com/TalkUHulk")
                    startActivity(intent)
                    Log.d("===>>", "navMail")
                    true
                }
                AR.id.navWasm -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.hulk.show/aidb-webassembly-demo/")
                    startActivity(intent)

                    Log.d("===>>", "navWasm")
                    true
                }

                else -> {
                    true
                }
            }
        }
//        val headerView: View = navigationView.getHeaderView(0)
        val textViewJump: TextView = navigationView.getHeaderView(0).findViewById(AR.id.userText)
        val jumpingBeans = JumpingBeans.with(textViewJump)
            .makeTextJump(0, textViewJump.getText().toString().indexOf(' '))
            .setIsWave(false)
            .setLoopDuration(1000)  // ms
            .build();

        thread {
            var ready = true
            val step = 100 / ModelZoo.models_md5.size
            var walk = 0
            for ((model, md5) in ModelZoo.models_md5){
                val modelPath = File(File(obbDir, "models"), model)
                if(md5 != getFileMD5(modelPath)){
                    ready = false
                    break
                }
                val msg = Message()
                walk += step
                msg.what = walk
                handler.sendMessage(msg)
            }
            if(!ready){
                val msg = Message()
                msg.what = 101
                handler.sendMessage(msg)
                // init vision-cpp
                copyAssetAndWrite("config.zip", cacheDir, assets)
                val configZipPath = File(cacheDir, "config.zip")
                unzip(configZipPath.absolutePath, obbDir.absolutePath)

                copyAssetAndWrite("models.zip", cacheDir, assets)
                val modelsZipPath = File(cacheDir, "models.zip")
                unzip(modelsZipPath.absolutePath, obbDir.absolutePath)

                ready = true

                for ((model, md5) in ModelZoo.models_md5){
                    val modelPath = File(File(obbDir, "models"), model)
                    if(md5 != getFileMD5(modelPath)){
                        Log.d("MD5 Error", "${modelPath.absolutePath} ${getFileMD5(modelPath)} ${md5}")
                        ready = false
                        break
                    }
                }

            }


            val msg = Message()
            msg.what = if(ready) 100 else -1
            handler.sendMessage(msg)
            modelsMd5Passed = ready
        }

        fixedRateTimer("timer", false, 0L, 3000) {

            val msg = Message()
            msg.what = 12580
            handler.sendMessage(msg)
        }


    }


    override fun onClick(v: View) {
        when (v.id) {
            AR.id.item_btn_run -> {
                val adapterPosition = itemPicker.currentItem.toString()
                val positionInModelZoo = infiniteAdapter!!.getRealPosition(adapterPosition.toInt())
                Log.d("====>>", itemPicker.currentItem.toString() + " : " + positionInModelZoo.toString())
                val intent = Intent("com.hulk.aidb_demo.PROCESS_START")
//                ONNX = 1,
//                MNN = 2,
//                NCNN = 3,
//                TNN = 4,
//                OPENVINO = 5,
//                PADDLE_LITE = 6,

                intent.putExtra("model", positionInModelZoo)
                intent.putExtra("backend", mBackend)
                intent.putExtra("mode", mMode)  // 0:catpure, 1:analyse
                startActivity(intent)
            }

            AR.id.home -> {
//                finish()
                drawerLayout.openDrawer(GravityCompat.START)
            }
            AR.id.btn_backend -> {
                Log.d("===>>", "btn_backend")
                backendPopUp(v)
            }
            AR.id.btn_mode -> {
                Log.d("===>>", "btn_mode")
                modePopUp(v)
            }
            else -> showUnsupportedSnackBar()
        }

    }

    private fun onItemChanged(model: Model) {
        currentModel.text = model.name

        if(ModelZoo.getMap()[model.id]?.contains(mBackend) == false){
            mBackend = 2
        }

        currentBackend.text = ModelZoo.backend[mBackend]
//        changeRateButtonState(model)
    }

//    private fun changeRateButtonState(model: Model) {
//        if (ModelZoo.isRated(model.id)) {
//            rateItemButton.setImageResource(AR.drawable.ic_star_black_24dp)
//            rateItemButton.setColorFilter(ContextCompat.getColor(this, AR.color.shopRatedStar))
//        } else {
//            rateItemButton.setImageResource(AR.drawable.ic_star_border_black_24dp)
//            rateItemButton.setColorFilter(ContextCompat.getColor(this, AR.color.shopSecondary))
//        }
//    }

    override fun onCurrentItemChanged(viewHolder: ModelZooAdapter.ViewHolder?, adapterPosition: Int) {
        val positionInDataSet = infiniteAdapter!!.getRealPosition(adapterPosition)
        onItemChanged(data[positionInDataSet])
    }

    private fun showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, AR.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show()
    }

    private fun backendPopUp(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu

        val adapterPosition = itemPicker.currentItem.toString()
        val positionInModelZoo: Int = infiniteAdapter!!.getRealPosition(adapterPosition.toInt())

        val backend_list: List<Int>? = ModelZoo.getMap()[positionInModelZoo]

        if (backend_list != null) {
            for(_backend in backend_list){
                menu.add(if(mBackend == _backend) ModelZoo.backend[_backend] + "✓" else ModelZoo.backend[_backend])
            }
        }

        popupMenu.setOnMenuItemClickListener { item ->
//            mBackend = ModelZoo.backend.entries.find { it.value == item.title.toString() }?.key!!
            mBackend = ModelZoo.backendID(item.title.toString())
            currentBackend.text = ModelZoo.backend[mBackend]
            true
        }
        popupMenu.show()


    }


    private fun modePopUp(view: View) {
        val popupMenu = PopupMenu(this, view)

        val menu = popupMenu.menu
        menu.add(if(mMode == 0) "Capture✓" else "Capture")
        menu.add(if(mMode == 1) "Analyse✓" else "Analyse")


        fun Boolean.toInt() = if (this) 1 else 0

        popupMenu.setOnMenuItemClickListener { item ->
            mMode = (item.title == "Analyse").toInt()
            true
        }
        popupMenu.show()


    }
}