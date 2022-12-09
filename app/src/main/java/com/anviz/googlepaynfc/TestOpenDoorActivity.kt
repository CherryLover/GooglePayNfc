package com.anviz.googlepaynfc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.anviz.googlepaynfc.databinding.ActivityTestOpenDoorBinding
import com.anviz.logger.Logger
import com.google.android.material.snackbar.Snackbar

class TestOpenDoorActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "TestOpenDoorActivity"
  }

  private val binding by lazy { ActivityTestOpenDoorBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.btnCheckConfig.setOnClickListener {
      checkConfig()
    }

    binding.btnApplyCode.setOnClickListener {
      val input = binding.etInput.editText?.text?.toString() ?: ""
      if (input.isEmpty()) {
        binding.etInput.error = "Please input code"
        toast("Please input code")
        return@setOnClickListener
      }
      binding.etInput.error = null
      val code = input.split(",")
      NfcHelper.getInstance(this)
        .saveInfo(code[0], code[1], "")
      toast("修改成功，可以尝试开门了。")
    }

    if (getSpBoolean("first_open", true)) {
      NfcHelper.getInstance(this)
        .saveInfo("FFFF2173", "8003572", "")
      NfcHelper.registerDefault(this, NfcEmulatorService::class.java, listOf(getString(R.string.nfc_emulator_aid)))
      binding.etInput.editText?.setText("FFFF2173,8003572")
      putSpBoolean("first_open", false)
    }
    checkConfig()
  }

  private fun toast(msg: String) {
    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
  }

  override fun onResume() {
    super.onResume()
    NfcHelper.getInstance(this).info?.let {
      binding.etInput.editText?.setText("${it.first},${it.second}")
    }
  }

  private fun checkConfig() {
    if (!NfcHelper.isSupportNfc(this)) {
      AlertDialog.Builder(this)
        .setTitle(getString(R.string.error))
        .setMessage(getString(R.string.no_nfc_support_hint))
        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
          finish()
        }
        .show()
      finish()
    }
    if (!NfcHelper.isNfcEnabled(this)) {
      AlertDialog.Builder(this)
        .setTitle(getString(R.string.notice))
        .setMessage(getString(R.string.ask_nfc_open))
        .setPositiveButton(R.string.ok) { dialog, which ->
          NfcHelper.openNfcSetting(this)
        }
        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
          finish()
        }
        .show()
    }
    val defaultOther = NfcHelper.isDefaultOther(this, NfcEmulatorService::class.java, getString(R.string.nfc_emulator_aid))
    Logger.logD(TAG, "defaultOther: $defaultOther")
    NfcHelper.setDefaultOther(this, NfcEmulatorService::class.java)

    if (!defaultOther) {
      AlertDialog.Builder(this)
        .setTitle(R.string.notice)
        .setMessage(getString(R.string.nfc_default_other_ask))
        .setPositiveButton(R.string.ok) { dialog, which ->
          NfcHelper.setDefaultOther(this, NfcEmulatorService::class.java)
        }
        .setNegativeButton(R.string.cancel) { dialog, which ->
          finish()
        }
        .show()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    Logger.logD(TAG, "onActivityResult: $requestCode, $resultCode")
    Logger.logD(TAG, "onActivityResult: data is null ${data == null}")
    data?.let {
      Logger.logD(TAG, "data: ${it.toString()}")
    }
  }

}