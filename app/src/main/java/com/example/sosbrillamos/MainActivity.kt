package com.example.sosbrillamos

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.sosbrillamos.Login.Companion.useremail
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

   /* companion object{
        val REQUIRED_PERMISSIONS_GPS =
            arrayOf(
              //  Manifest.permission.ACCESS_COARSE_LOCATION,
              //  Manifest.permission.ACCESS_FINE_LOCATION)

    }*/

    private lateinit var drawer: DrawerLayout
    private lateinit var builder: AlertDialog.Builder

    private var activateGPS: Boolean = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 42



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sos()
        asis()
        dialogo()
        initToolBar()
        initNavigationView()
       // initPermissionGPS()
    }

    override fun onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else{
            builder = AlertDialog.Builder(this)

            builder.setTitle("Alerta")
                .setMessage("Desea salir de la app?")
                .setCancelable(true)
                .setPositiveButton("Si"){dialogInterface, it -> signOut() }
                .setNegativeButton("No"){dialogInterface, it -> dialogInterface.cancel()}
                .show()

        }

    }
    /*

        private fun initPermissionGPS(){
            if (allPermissionsGrantedGPS())
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            else
                requestPermissionsLocation()
        }

       private fun requestPermissionsLocation(){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
        }

        private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    */

    private fun isLocationEnabled(): Boolean{
        var locationManager: LocationManager
        =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }


    private fun activationLocation(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun  dialogo(){
        if(isLocationEnabled() == false){
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertActivationGPSTitle))
                .setMessage(getString(R.string.alertActivationGPSDescription))
                .setPositiveButton(R.string.aceptActivationGPS,
            DialogInterface.OnClickListener{ dialog, which ->  
                    activationLocation()
        })
        .setNegativeButton(R.string.ignoreActivationGPS,
        DialogInterface.OnClickListener{dialog, which ->
            activateGPS = false
        })
                .setCancelable(true)
                .show()

        }
    }




    private fun initToolBar(){
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun initNavigationView(){
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var headerView: View = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        var tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = useremail
    }

    fun callSignOut(view: View){
        signOut()
    }
    private fun signOut(){
        useremail = ""

    FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, Login::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//Generar salida para el menu
        when (item.itemId){
            R.id.nav_alarma -> callAlarmaActivity()
            R.id.nav_Telefono -> callregTel()
            R.id.nav_Mensaje -> callregMens()
            R.id.nav_salir -> signOut()
        }
        return true
    }
    private fun callAlarmaActivity(){
        val intent = Intent(this, Alarmas::class.java)
        startActivity(intent)
    }
    private fun callregTel(){
        val intent = Intent(this, registroTelefono::class.java)
        startActivity(intent)
    }

    private fun callregMens(){
        val intent = Intent(this, Mensaje::class.java)
        startActivity(intent)
    }

    private fun sos() {
        val boton1 = findViewById<ImageButton>(R.id.ibWhats)
        boton1.setOnClickListener {
            val intento1 = Intent(this, sosActivity::class.java)
            startActivity(intento1)
        }


    }

    private fun asis() {
        val boton1 = findViewById<ImageButton>(R.id.ibMessege)
        boton1.setOnClickListener {
            val intento1 = Intent(this, sosMessage::class.java)
            startActivity(intento1)
        }
    }

    private fun camin() {
        val boton1 = findViewById<ImageButton>(R.id.ibAqui)
        boton1.setOnClickListener {
            val intento1 = Intent(this, sosActivity::class.java)
            startActivity(intento1)
        }
    }

}


