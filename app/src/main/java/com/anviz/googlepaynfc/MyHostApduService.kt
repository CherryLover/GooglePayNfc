package com.anviz.googlepaynfc

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @description
 * @author: Created jiangjiwei in 2022/11/10 11:42
 */
class MyHostApduService : HostApduService() {

  companion object {
    private const val TAG = "MyHostApduService"
    private const val SAMPLE_LOYALTY_CARD_AID = "F123422222"

    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
//    private const val SELECT_APDU_HEADER = "00A40400"

    // "OK"
    private val SELECT_OK_SW: ByteArray = hexStringToByteArray("9000")
    // "UNKNOWN"
    private val UNKNOWN_CMD_SW: ByteArray = hexStringToByteArray("0000")

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

    private fun concatArrays(first: ByteArray, vararg rest: ByteArray): ByteArray {
      var totalLength = first.size
      for (array in rest) {
        totalLength += array.size
      }
      val result = Arrays.copyOf(first, totalLength)
      var offset = first.size
      for (array in rest) {
        System.arraycopy(array, 0, result, offset, array.size)
        offset += array.size
      }
      return result
    }
  }

  override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
    val command = byteArrayToHexString(commandApdu)
    log("接收内容: $command")
    return when (command) {
      FIRST_REQUEST -> {
        val accountNumber = "1234567890"
        log("发送 number: $accountNumber")
        val concatArrays = concatArrays(hexStringToByteArray(accountNumber), SELECT_OK_SW)
        log("实际发送内容: ${byteArrayToHexString(concatArrays)}")
        concatArrays
      }
      "0011AB" -> {
        log("发送 0011AB")
        val concatArrays = concatArrays(hexStringToByteArray("0011AB"), SELECT_OK_SW)
        log("实际发送内容: ${byteArrayToHexString(concatArrays)}")
        concatArrays
      }
      else -> {
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