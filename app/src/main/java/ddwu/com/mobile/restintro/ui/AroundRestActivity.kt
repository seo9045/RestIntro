package ddwu.com.mobile.restintro.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.restintro.KakaoAPI
import ddwu.com.mobile.restintro.data.ListLayout
import ddwu.com.mobile.restintro.data.ResultSearchKeyword
import ddwu.com.mobile.restintro.databinding.ActivityAroundRestBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AroundRestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAroundRestBinding
    private val listItems = arrayListOf<ListLayout>()
    private val listAdapter = ListAdapter(listItems)
    private var pageNumber = 1
    private var keyword = ""

    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundRestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        // 항목 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showUserLocation()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showUserLocation()
                } else {
                    Toast.makeText(this, "위치 권한을 허용해야 내 위치를 확인할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun showUserLocation() {
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val uLatitude = userNowLocation?.latitude
            val uLongitude = userNowLocation?.longitude
            val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)

            val myLocationMarker = MapPOIItem()
            myLocationMarker.itemName = "내 위치"
            myLocationMarker.mapPoint = uNowPosition
            myLocationMarker.markerType = MapPOIItem.MarkerType.BluePin // 마커 유형 설정
            binding.mapView.addPOIItem(myLocationMarker) // 지도에 마커 추가
            binding.mapView.setMapCenterPoint(uNowPosition, true) // 지도 중심점 이동

        } catch (e: NullPointerException) {
            Log.e("LOCATION_ERROR", e.toString())
        }
    }

    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder() // Retrofit 구성
            .baseUrl(FindRestActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(FindRestActivity.API_KEY, keyword, page) // 검색 조건 입력

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            listItems.clear()
            binding.mapView.removeAllPOIItems()
            for (document in searchResult!!.documents) {
                val item = ListLayout(document.place_name,
                    document.phone,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end
            binding.btnPrevPage.isEnabled = pageNumber != 1

        } else {
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
