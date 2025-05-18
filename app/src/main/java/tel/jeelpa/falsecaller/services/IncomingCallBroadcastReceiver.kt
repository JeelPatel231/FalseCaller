package tel.jeelpa.falsecaller.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager


// Pre Android 12
class IncomingCallBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        require(intent != null)
        require(context != null)

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            ?: return println("Number was NULL. Aborting.")

        val serviceIntent = Intent(context, OverlayService::class.java)
            .apply { putExtra("number", number) }

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING, TelephonyManager.EXTRA_STATE_OFFHOOK -> context.startService(serviceIntent)
            else -> context.stopService(serviceIntent)
        }
    }
}

