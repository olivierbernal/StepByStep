package fr.oworld.stepbystep.datas.model

import android.content.Context
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import java.io.*

/*
Data coming from MaPetitePoubelle FTP
 */

interface APIService {
    @Streaming
    @GET("/StepByStep/bd.csv")
    fun getBD(): Call<String>

    @Streaming
    @GET("/StepByStep/competences.csv")
    fun getSkills(): Call<String>

    @Streaming
    @GET("/StepByStep/notifs.csv")
    fun getNotifs(): Call<String>

    @Streaming
    @GET("/StepByStep/tasks.csv")
    fun getTasks(): Call<String>
}


object ApiUtils {
    val BASE_URL = "https:/www.oworld.fr"

    var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) //url of firebase app
            .addConverterFactory(ScalarsConverterFactory.create()) //use for convert JSON file into object
            .build()
    }

    val apiService: APIService
        get() = retrofit.create(
            APIService::class.java) //use of interface

    private fun writeResponseBodyToDisk(c: Context, fileName: String, body: ResponseBody): Boolean {
        return try {
            val f =
                File(c.getExternalFilesDir(null)?.path + File.separator + fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(f)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream?.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("WriteService", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream?.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

}
