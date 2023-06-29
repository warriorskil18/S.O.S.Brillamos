package com.example.sosbrillamos

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.MenuItem
import android.view.Surface
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.util.MapUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView

@Suppress("DEPRECATION")
class sosActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var cameraButton: ImageButton
    private val cameraRequestCode = 123
    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private lateinit var imageButton: ImageButton
    private val phoneNumber1 = "5574279252" // Número de teléfono para realizar la llamada


    private lateinit var map: GoogleMap
    private var mapCentered = true
    private var locations = mutableListOf<LatLng>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.run {
                this.forEach {
                    locations.add(LatLng(it.latitude, it.longitude))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,it.longitude),20f))
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        // Obtener la referencia al ImageButton desde el layout
        cameraButton = findViewById(R.id.ib_camara)

        // Agregar el listener de clic al ImageButton
        cameraButton.setOnClickListener {
            // Verificar si se tiene el permiso necesario para acceder a la cámara
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no se tiene el permiso, solicitarlo al usuario
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    sosMessage.CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                // Si se tiene el permiso, abrir la cámara para tomar una foto
                openCamera()
            }
        }

        // Obtener la referencia al ImageButton desde el layout
        imageButton = findViewById(R.id.ib_llamar)

        // Agregar el listener de clic al ImageButton
        imageButton.setOnClickListener {
            // Verificar si se tiene el permiso necesario para realizar llamadas telefónicas
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no se tiene el permiso, solicitarlo al usuario
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    sosMessage.CALL_PERMISSION_REQUEST_CODE
                )
            } else {
                // Si se tiene el permiso, realizar la llamada
                makePhoneCall()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createMapFragment()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create()
                .setInterval(5_000)
                .setFastestInterval(2_000),
            locationCallback, Looper.getMainLooper()
        )
            Msn()
    }


    private fun Msn(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, "Necesito asistencia.")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this@sosActivity,
                "La aplicación Whastapp no se encuentra instalada en el dispositivo.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openCamera() {
        val camera = getCameraInstance()
        val cameraId = findCameraId(currentCameraId)
        configureCameraOrientation(cameraId, camera)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri())
        startActivityForResult(cameraIntent, cameraRequestCode)
    }

    private fun getCameraInstance(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return camera
    }

    private fun findCameraId(cameraFacing: Int): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraInfo = Camera.CameraInfo()
        for (i in 0 until numberOfCameras) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == cameraFacing) {
                return i
            }
        }
        return Camera.CameraInfo.CAMERA_FACING_BACK
    }

    private fun configureCameraOrientation(cameraId: Int, camera: Camera?) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
    }

    private fun getOutputMediaFileUri(): Uri? {
        // Aquí puedes implementar la lógica para crear el archivo donde se guardará la foto
        // y devolver la Uri correspondiente
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }
    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }



    companion object {
         const val CALL_PERMISSION_REQUEST_CODE = 123
         const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {9
        map = googleMap

        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        map.isMyLocationEnabled = true

        map.uiSettings.apply {
            map.isMyLocationEnabled = true
        }


    }

    private fun makePhoneCall() {
        val phoneNumberUri = Uri.parse("tel:$phoneNumber1")
        val callIntent = Intent(Intent.ACTION_CALL, phoneNumberUri)
        startActivity(callIntent)
    }

}

