package ddwu.com.mobile.restintro.data

import java.io.Serializable

data class RestDto(
    val id: Int,
    var photoName: String,
    var name: String,
    var location: String,
    var review: String,
    var rating: String
) : Serializable {
    override fun toString(): String {
        return "$name\n주소:$location\n평가:$review\n평점: $rating"
    }
}
