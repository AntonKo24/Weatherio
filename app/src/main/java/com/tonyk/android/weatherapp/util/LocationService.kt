package com.tonyk.android.weatherapp.util
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

object LocationService {

    fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
    fun isLocationPermissionGranted(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }
    fun requestLocationPermission(activity: FragmentActivity, success: (success : Boolean) -> Unit) {
        val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean -> success.invoke(isGranted)  }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    @SuppressLint("MissingPermission")
    fun getLocationData(activity: FragmentActivity, onGPSSuccess: (coordinates: String, address: String) -> Unit) {
            LocationServices.getFusedLocationProviderClient(activity)
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { it: Location? ->
                    if (it != null) {
                        val coordinates = "${it.latitude},${it.longitude}"
                        val address = getLocationName(activity, it.latitude, it.longitude)
                        onGPSSuccess.invoke(coordinates, address)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Permission is not granted", Toast.LENGTH_LONG).show()
                }
    }
    private fun getLocationName(context: Context, latitude: Double, longitude: Double) : String {
        return try {
            val addresses: List<Address>? = Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val cityName = addresses[0].locality ?: ""
                val countryName = addresses[0].countryName ?: ""
                (if (cityName.isNotEmpty()) "$cityName, $countryName" else countryName)
            } else {
                Toast.makeText(context, "Error loading location name", Toast.LENGTH_LONG).show()
                ("Unknown location")
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Error loading location name", Toast.LENGTH_LONG).show()
            ("Unknown location")
        }
    }

    fun showGPSAlertDialog(activity: FragmentActivity) {
        AlertDialog.Builder(activity)
            .setTitle("GPS Required")
            .setMessage("Please enable GPS and REFRESH page to get your location.")
            .setPositiveButton("Enable GPS") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}

