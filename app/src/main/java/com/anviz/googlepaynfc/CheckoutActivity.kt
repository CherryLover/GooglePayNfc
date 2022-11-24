package com.anviz.googlepaynfc

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anviz.googlepaynfc.databinding.ActivityCheckoutBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat

/**
 * Checkout implementation for the app
 */
class CheckoutActivity : AppCompatActivity() {


  private val binding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.btnClearLog.setOnClickListener {
      binding.tvLog.text = ""
    }
    binding.btnSetting.setOnClickListener {
      NfcHelper.openNfcSetting(this)
    }

    binding.cbDefault.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        NfcHelper.setDefaultPayment(this, MyHostApduService::class.java)
      }
    }

    if (!NfcHelper.isSupportNfc(this)) {
      AlertDialog.Builder(this)
        .setTitle("Error")
        .setMessage("This device does not support NFC")
        .setPositiveButton("OK") { dialog, which ->
          finish()
        }
        .show()
      return
    }
    if (!NfcHelper.isNfcEnabled(this)) {
      AlertDialog.Builder(this)
        .setTitle("Notice")
        .setMessage("NFC is not enabled, Now open It?")
        .setPositiveButton("OK") { dialog, which ->
          NfcHelper.openNfcSetting(this)
        }
        .setNegativeButton("Cancel") { dialog, which ->
          finish()
        }
        .show()
    }
    if (!NfcHelper.isDefaultPayment(this, MyHostApduService::class.java)) {
      AlertDialog.Builder(this)
        .setTitle("Notice")
        .setMessage("This app is not default payment, Now set It?")
        .setPositiveButton("OK") { dialog, which ->
          NfcHelper.setDefaultPayment(this, MyHostApduService::class.java)
        }
        .setNegativeButton("Cancel") { dialog, which ->
          finish()
        }
        .show()
    }
  }

  override fun onResume() {
    super.onResume()
    EventBus.getDefault().register(this)

    binding.cbDefault.isChecked = NfcHelper.isDefaultPayment(this, MyHostApduService::class.java)
  }

  override fun onPause() {
    super.onPause()
    EventBus.getDefault().unregister(this)
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun receiveLog(msg: Pair<Int, String>) {
    when (msg.first) {
      0 -> {
        binding.tvLog.append("${time()}: ERROR: ${msg.second} ERROR!!!\n")
      }
      1 -> {
        binding.tvLog.append("${time()}: ${msg.second}\n")
      }
    }
  }

  private fun time(): String {
    val time = System.currentTimeMillis()
    return SimpleDateFormat("HH:mm:ss.SSS").format(time)
  }
}