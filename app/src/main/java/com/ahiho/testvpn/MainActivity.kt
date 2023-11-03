package com.ahiho.testvpn

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.api.APIVpnProfile
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.ConnectionStatus
import de.blinkt.openvpn.core.IOpenVPNServiceInternal
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader


class MainActivity : AppCompatActivity() {

    private lateinit var service: IOpenVPNAPIService
    private lateinit var mService: IOpenVPNServiceInternal

    private val openVPNRequestPermissionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            Log.d("TestVPN", "activity for result")
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Ok roi day baby", Toast.LENGTH_SHORT).show()
//                service.registerStatusCallback(opvnStatusCallback)
                startVpn()
            }
    }

    private val opvnStatusCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            Toast.makeText(this@MainActivity, message ?: "Null roi baby", Toast.LENGTH_SHORT).show()
        }
    }

    private val vpnStatusListener = object : VpnStatus.StateListener{
        override fun updateState(
            state: String?,
            logmessage: String?,
            localizedResId: Int,
            level: ConnectionStatus?,
            Intent: Intent?
        ) {
            Toast.makeText(this@MainActivity, "vpnStatusListener $logmessage", Toast.LENGTH_SHORT).show()
        }

        override fun setConnectedVPN(uuid: String?) {

        }
    }

    private val connection by lazy {
//        Log.d("TestVPN", "connection lazy")
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, iBInder: IBinder?) {
//                service = IOpenVPNAPIService.Stub.asInterface(iBInder)
                mService = IOpenVPNServiceInternal.Stub.asInterface(iBInder)
                Log.d("TestVPN", "lateinit service")


//                try {
////                    service.prepare(packageName).let {
////                        openVPNRequestPermissionLauncher.launch(it)
////                    }
//
//                } catch (e: RemoteException) {
//                    Log.d("TestVPN", "openvpn service connection failed: $e")
//                    Toast.makeText(this@MainActivity, "openvpn service connection failed: $e", Toast.LENGTH_SHORT).show()
//                    e.printStackTrace()
//                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Toast.makeText(this@MainActivity, "service disconnect", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        bindService()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VpnStatus.addStateListener(vpnStatusListener)

        findViewById<Button>(R.id.btn).let {
            it.setOnClickListener {
                prepareVpn()
            }
        }

    }

    @Throws(RemoteException::class)
    private fun prepareVpn() {
//        if (!vpnStart) {
//            if (getInternetStatus()) {

                // Checking permission for network monitor
//                val intent: Intent = service.prepareVPNService()
//                startActivityForResult(intent, 1)
        VpnService.prepare(this)?.let{ openVPNRequestPermissionLauncher.launch(it) } ?: kotlin.run { startVpn() }


                // Update confection status
//                status("connecting")
//            } else {
//                println("you have no internet connection !!")
//            }
//        } else if (stopVpn()) {
//            println("Disconnect Successfully")
//        }
    }

    private fun startVpn() {
        try {
            val conf: InputStream = assets.open("us-freeopenvpn.ovpn")
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                $line
                
                """.trimIndent()
            }
            br.readLine()
            val cp = ConfigParser()
            cp.parseConfig(StringReader(config))
            val profile = cp.convertProfile()
            profile.mName = "United States-FreeOpenVPN"
            profile.mUsername = "freeopenvpn"
            profile.mPassword = "108927705"
            VPNLaunchHelper.startOpenVpn(profile, this, "")

//            val profile: APIVpnProfile =
//                service.addNewVPNProfile("United States-FreeOpenVPN", false, config)
//            service.startProfile(profile.mUUID)
//            service.startVPN(config)

            // Update log
        } catch (e: IOException) {
            Toast.makeText(this, "openvpn server connection failed: $e", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: RemoteException) {
            Toast.makeText(this, "openvpn server connection failed: $e", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun bindService() {
//        Log.d("TestVPN", "bind service: ${application.packageName}")
        val icsopenvpnService = Intent(this, OpenVPNService::class.java).apply {
            `package` = application.packageName
            action = OpenVPNService.START_SERVICE
        }
        val check = bindService(icsopenvpnService, connection, BIND_AUTO_CREATE)
        Log.d("TestVPN", "bind service: $check")
        if (!check) unbindService(connection)
    }
}