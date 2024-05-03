package com.example.weatherapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.ui.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding

    private val viewModel: MainViewModel by viewModels()

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,30).apply {
        setMinUpdateDistanceMeters(100f)
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setWaitForAccurateLocation(true)
    }.build()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        val view = binding?.root
        return view
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())


        binding?.etSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding?.etSearch?.text.toString()
                viewModel.getWeatherResponse(query)
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener true
        }


        viewModel.weather.observe(viewLifecycleOwner) {
            binding?.apply {
                Temperature.text = it.main.temp.toString()
                cityName.text = it.name
                country.text = it.sys.country
                date.text = "$currentDate"
                humidity.text = "Humidity: ${it.main.humidity}%"
                pressure.text = "Pressure: ${it.main.pressure} hPa"
                tempMax.text = "Max Temperature: ${it.main.temp_max}°C"
                tempMin.text = "Min Temperature: ${it.main.temp_min}°C"
                Sealevel.text = "Sea level: ${it.main.sea_level}m"
                Grndlevel.text = "Grnd level: ${it.main.grnd_level}m"
                Feelslike.text = "Feels like: ${it.main.feels_like}"
                windSpeed.text = "Wind Speed: ${it.wind.speed} m/s"
                val condition = it.weather[0].main
                when(condition) {
                    "Rain" -> {
                        weatherConditionImage.setImageResource(R.drawable.ic_rainy)
                        weatherImage.setImageResource(R.drawable.rain)
                    }
                    "Clouds" -> {
                        weatherConditionImage.setImageResource(R.drawable.ic_cloudy)
                        weatherImage.setImageResource(R.drawable.cloud)
                    }
                    "Clear" -> {
                        weatherConditionImage.setImageResource(R.drawable.ic_sunny)
                        weatherImage.setImageResource(R.drawable.sun)
                    }
                    "Snow" -> {
                        weatherConditionImage.setImageResource(R.drawable.ic_snowy)
                        weatherImage.setImageResource(R.drawable.snow)

                    }
                }
            }
        }


        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            ) {

            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Location Permission Needed.")
                    .setMessage("App needs the Location Permission, please accept it. ")
                    .setPositiveButton(
                        "Ok"
                    ) {_,_ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),99)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProvider?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                fusedLocationProvider?.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            viewModel.getUserWeather(location.latitude.toString(),location.longitude.toString())
                        } else {
                            Toast.makeText(requireContext(),"Can't Access Location",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } else {

            Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()

            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireContext().packageName, null),
                    ),
                )
            }
        }
        return
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}