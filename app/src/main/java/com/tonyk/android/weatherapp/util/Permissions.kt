package com.tonyk.android.weatherapp.util
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object Permissions {
    private fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

    fun requestLocationPermission(activity: FragmentActivity, success: (Boolean) -> Unit)
    {
        val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted && isGPSEnabled(activity)) {
                success.invoke(true)
            } else {
                success.invoke(false)
                showToast(activity, "LOCATION PERMISSION IS NOT GRANTED")
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    fun retrieveGPSLocation(activity: FragmentActivity, callback: (coordinates: String) -> Unit) {
        if (isGPSEnabled(activity)) {
            LocationServices.getFusedLocationProviderClient(activity).getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val coordinates = "${location.latitude},${location.longitude}"
                        callback.invoke(coordinates)
                    }
                }
                .addOnFailureListener {
                    Log.d("debug", "$it")
                    showToast(activity, it.toString())
                }
        } else {
            showToast(activity, "GPS is not turned on")
        }
    }
}

