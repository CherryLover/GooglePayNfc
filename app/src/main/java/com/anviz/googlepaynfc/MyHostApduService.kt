package com.anviz.googlepaynfc

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
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
    Log.d(TAG, "Received APDU: $command")
    return when (command) {
      FIRST_REQUEST -> {
        val accountNumber = "1234567890"
        log("send account number: $accountNumber")
        concatArrays(hexStringToByteArray(accountNumber), SELECT_OK_SW)
      }
      "0011AB" -> {
        log("send 0011AB")
        concatArrays(hexStringToByteArray("0011AB"), SELECT_OK_SW)
      }
      else -> {
        UNKNOWN_CMD_SW
      }
    }
  }

  override fun onDeactivated(reason: Int) {
    Log.d(TAG, "onDeactivated $reason")
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "onStartCommand")
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate")
  }

  private fun log(msg: String) {
    Log.e(TAG, msg)
  }
}