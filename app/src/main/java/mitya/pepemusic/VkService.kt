package mitya.pepemusic

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

private const val VK_URL = "https://vk.com/"
private const val VK_LOGIN_URL = "https://login.vk.com/"
private const val VK_MOBILE_URL = "https://m.vk.com/"

object VkService : RestService<VkService.Api>(Api::class.java, VK_URL) {
    interface Api {
        @GET(".")
        fun getLoginHash(): Single<ResponseBody>

        @FormUrlEncoded
        @POST("$VK_LOGIN_URL?act=login&role=al_frame&_origin=https://vk.com&utf8=1")
        fun login(@Field("email") email: String,
                  @Field("pass") password: String,
                  @Field("lg_h") loginHash: String): Single<ResponseBody>

        @GET(VK_MOBILE_URL + "audio?act=search")
        fun searchMusic(@Query("q") q: String, @Query("offset") offset: Int): Single<ResponseBody>

        @GET("")
        fun downloadTrack(@Url url: String): Single<ResponseBody>
    }
}
