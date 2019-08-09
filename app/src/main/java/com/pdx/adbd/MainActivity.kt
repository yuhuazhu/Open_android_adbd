package com.pdx.adbd

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val builder = StringBuilder()
        builder.append("手机ip地址：")
        builder.append(getIpAddress())
        builder.append(':')
        builder.append(getAdbdPort())
        tv_ip.text = builder.toString()
    }

    private fun getIpAddress(): String {
        val service = applicationContext.getSystemService(Context.WIFI_SERVICE)
        if (service != null) {
            val wifiManager = (service as WifiManager).connectionInfo
            val address = wifiManager.ipAddress
            val builder = StringBuffer("")
            builder.append(address and 255)
            builder.append(".")
            builder.append(('\uffff'.toInt() and address).ushr(8))
            builder.append(".")
            builder.append((16777215 and address).ushr(16))
            builder.append(".")
            builder.append(address.ushr(24))
            return builder.toString()
        } else {
            throw TypeCastException("null cannot be cast to non-null type android.net.wifi.WifiManager")
        }
    }

    private fun getAdbdPort(): String {
        val var1 = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(null as Any?, "service.adb.tcp.port")
        val builder = StringBuilder()
        builder.append("state = ")
        builder.append(var1.toString())
        Log.e("getAdbdPort", builder.toString())
        return var1.toString()
    }

    fun openAdbWifi(v: View) {
        val builder = StringBuilder()
        builder.append("setprop service.adb.tcp.port 3333")
        builder.append("\n")
        builder.append("stop adbd")
        builder.append("\n")
        builder.append("start adbd")
        if (rootCommand(builder.toString())) {
            builder.clear()
            builder.append("手机ip地址：")
            builder.append(this.getIpAddress())
            builder.append(':')
            builder.append(this.getAdbdPort())
            tv_ip.text = builder.toString()
        }
    }

    fun setAdbdPort(v: View) {
        if (rootCommand("setprop service.adb.tcp.port ${etPort.text}")) {
            val builder = StringBuilder()
            builder.append("手机ip地址：")
            builder.append(this.getIpAddress())
            builder.append(':')
            builder.append(this.getAdbdPort())
            tv_ip.text = builder.toString()
        }
    }

    fun startAdbd(var1: View) {
        if (rootCommand("start adbd")) {
            val builder = StringBuilder()
            builder.append("手机ip地址：")
            builder.append(this.getIpAddress())
            builder.append(':')
            builder.append(this.getAdbdPort())
            tv_ip.text = builder.toString()
        }
    }

    fun stopAdbd(var1: View) {
        if (rootCommand("stop adbd")) {
            val builder = StringBuilder()
            builder.append("手机ip地址：")
            builder.append(this.getIpAddress())
            builder.append(':')
            builder.append(this.getAdbdPort())
            tv_ip.text = builder.toString()
        }
    }

    private fun rootCommand(cmd: String): Boolean {
        return try {
            val p = Runtime.getRuntime().exec("su")
            val dos = DataOutputStream(p.outputStream)
            dos.writeBytes(cmd + "\n")
            dos.flush()
            true
        } catch (e: Exception) {
            false
        }
    }
}
