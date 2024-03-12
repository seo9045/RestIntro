package ddwu.com.mobile.restintro.data

data class ResultSearchKeyword(
    var meta: PlaceMeta,
    var documents: List<Place>
)

data class PlaceMeta(
    var total_count: Int,
    var pageable_count: Int,
    var is_end: Boolean,
    var same_name: RegionInfo
)

data class RegionInfo(
    var region: List<String>,
    var keyword: String,
    var selected_region: String
)

data class Place(
    var id: String, // 장소 ID
    var place_name: String, // 장소명, 업체명
    var category_name: String, // 카테고리 이름
    var phone: String, // 전화번호
    var address_name: String, // 전체 지번 주소
    var road_address_name: String, // 전체 도로명 주소
    var x: String, // X 좌표값 혹은 longitude
    var y: String, // Y 좌표값 혹은 latitude
    var distanc: String // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter
)
