package ddwu.com.mobile.restintro.ui

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.restintro.KakaoAPI
import ddwu.com.mobile.restintro.data.ListLayout
import ddwu.com.mobile.restintro.data.ResultSearchKeyword
import ddwu.com.mobile.restintro.databinding.ActivityFindRestBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class FindRestActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 15f7173969d1cac3a0306c481b486c04" // REST API 키
    }

    private lateinit var binding : ActivityFindRestBinding
    private val listItems = arrayListOf<ListLayout>()
    private val listAdapter = ListAdapter(listItems)
    private var pageNumber = 1
    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindRestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

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

        // 항목 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val selectedPlaceName = listItems[position].name
                val selectedLatitude = listItems[position].y
                val selectedLongitude = listItems[position].x

                val geocoder = Geocoder(this@FindRestActivity) // Geocoder 객체 생성
                val selectedAddress = getLocationAddress(selectedLatitude, selectedLongitude, geocoder) // getLocationAddress 함수를 통해 주소 변환

                val intent = Intent(this@FindRestActivity, AddRestActivity::class.java)
                intent.putExtra("PLACE_NAME_KEY", selectedPlaceName)
                intent.putExtra("ADDRESS_KEY", selectedAddress)
                startActivity(intent)
                Toast.makeText(this@FindRestActivity, "정보가 입력됐습니다!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword, page)

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
                    document.category_name,
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

    fun getLocationAddress(latitude: Double, longitude: Double, geocoder: Geocoder): String {
        var addressText = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                addressText = sb.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressText
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, RestListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}