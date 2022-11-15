package com.anviz.googlepaynfc

import android.os.Bundle
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
  }

  override fun onResume() {
    super.onResume()
    EventBus.getDefault().register(this)
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