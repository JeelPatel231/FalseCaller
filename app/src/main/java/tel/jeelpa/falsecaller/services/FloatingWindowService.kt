package tel.jeelpa.falsecaller.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import tel.jeelpa.falsecaller.R
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowScreen
import tel.jeelpa.falsecaller.ui.theme.FalseCallerTheme

//https://gist.github.com/handstandsam/6ecff2f39da72c0b38c07aa80bbb5a2f
class OverlayService : Service(), KoinComponent {
    private lateinit var composeView: ComposeView
    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    private val phoneNumberUtil: PhoneNumberUtil = get()

    private lateinit var number: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        number = intent!!.getStringExtra("number")!!
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        windowManager.removeView(composeView)
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_FalseCaller)

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        composeView = ComposeView(this)
        composeView.setContent {
            FalseCallerTheme {
                Card(
                    modifier = Modifier.wrapContentSize()
                ) {
                    FloatingWindowScreen(
                        modifier = Modifier.padding(16.dp),
                        // TODO: REGION CODE should be choosable in settings
                        number = phoneNumberUtil.parse(number, "IN")
                    )
                }
            }
        }

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)


        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        windowManager.addView(composeView, params)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}