package com.anviz.googlepaynfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import org.greenrobot.eventbus.EventBus

/**
 * @description
 * @author: Created jiangjiwei in 2022/11/10 11:42
 */
class MyHostApduService : HostApduService() {

  companion object {
    private const val TAG = "MyHostApduService"
    private const val SAMPLE_LOYALTY_CARD_AID = "F123422221"

    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
//    private const val SELECT_APDU_HEADER = "00A40400"
    // "UNKNOWN"
    private val UNKNOWN_CMD_SW: ByteArray = hexStringToByteArray("1111")

    private val FIRST_REQUEST = "00A40400" + String.format("%02X", SAMPLE_LOYALTY_CARD_AID.length / 2) + SAMPLE_LOYALTY_CARD_AID

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
      val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
      val hexChars = CharArray(bytes.size * 2)
      var v: Int
      for (j in bytes.indices) {
        v = bytes[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
      }
      return String(hexChars)
    }

    private fun hexStringToByteArray(s: String): ByteArray {
      val len = s.length
      require(len % 2 != 1) { "Hex string must have even number of characters" }
      val data = ByteArray(len / 2)
      var i = 0
      while (i < len) {
        data[i / 2] = ((Character.digit(s[i], 16) shl 4)
          + Character.digit(s[i + 1], 16)).toByte()
        i += 2
      }
      return data
    }
  }

  override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
    val command = byteArrayToHexString(commandApdu)
    log("接收内容: $command")
    return when (command) {
      FIRST_REQUEST -> {
        val accountNumber = "FABC"
        log("发送 $accountNumber")
        hexStringToByteArray(accountNumber)
      }

      "AAAA" -> {
        log("发送 A1B2")
        hexStringToByteArray("A1B2")
      }

      "BBBB" -> {
        log("发送 B1B2")
        hexStringToByteArray("B1B2")
      }

      else -> {
        log("收到未知指令: $command 返回 1111")
        UNKNOWN_CMD_SW
      }
    }
  }

  override fun onDeactivated(reason: Int) {
    val text = if (reason == DEACTIVATION_LINK_LOSS) {
      "NFC 链路丢失造成的断开连接"
    } else if (reason == DEACTIVATION_DESELECTED) {
      "AID 不同造成的连接断开"
    } else {
      "Deactivated:  $reason"
    }
    loge("模拟卡断开连接：$text")
  }

//  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//    Log.d(TAG, "onStartCommand")
//    return super.onStartCommand(intent, flags, startId)
//  }

  override fun onCreate() {
    super.onCreate()
    log("模拟卡服务启动")
  }

  private fun log(msg: String) {
    Log.d(TAG, msg);
    EventBus.getDefault().post(Pair(1, msg))
  }

  private fun loge(msg: String) {
    Log.e(TAG, msg);
    EventBus.getDefault().post(Pair(0, msg))
  }
}