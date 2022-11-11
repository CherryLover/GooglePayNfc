package com.anviz.googlepaynfc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anviz.googlepaynfc.databinding.ActivityCheckoutBinding

/**
 * Checkout implementation for the app
 */
class CheckoutActivity : AppCompatActivity() {


  private val layoutBinding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(layoutBinding.root)
  }
}