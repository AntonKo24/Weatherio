package com.tonyk.android.weatherapp.util
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException
import java.lang.reflect.Field
import java.util.Locale

object LocationService {

    private fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    fun isLocationPermissionGranted(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }
    fun requestLocationPermission(activity: FragmentActivity, success: (success : Boolean) -> Unit)
    {
        val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            success.invoke(isGranted)
        }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    @SuppressLint("MissingPermission")
    fun getLocationData(activity: FragmentActivity, onGPSSuccess: (coordinates: String, address: String) -> Unit) {
        LocationServices.getFusedLocationProviderClient(activity).getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val coordinates = "${location.latitude},${location.longitude}"
                    onGPSSuccess.invoke(coordinates, getLocationName(activity, location.latitude,location.longitude))
                }
            }
            .addOnFailureListener {
                showToast(activity, it.toString())
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
                showToast(context, "Can't load location name")
                ("Unknown location")
            }
        } catch (e: IOException) {
            showToast(context, "Error loading location name")
            ("Unknown location")
        }
    }
}

