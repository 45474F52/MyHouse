package com.aes.myhome.storage.json

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonDataSerializer(private val context: Context, private val storage: StorageType) {

//    private fun isExternalStorageWritable(): Boolean {
//        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
//    }
//
//    private fun isExternalStorageReadable(): Boolean {
//        return Environment.getExternalStorageState() in
//                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
//    }

//    private fun isExternalStorageAvailable() : Boolean {
//        return isExternalStorageWritable() && isExternalStorageReadable()
//    }

    fun writeData(json: String, directory: String, fileName: String) {
        if (storage == StorageType.INTERNAL) {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        }
//        else {
//            if (isExternalStorageAvailable()) {
//                //val dir = File(context.getExternalFilesDir(null), fileName)
//
//                val sdMain = File(Environment
//                        .getExternalStorageDirectory()
//                        .path + "/" + directory)
//
//                var success = true
//
//                if (!sdMain.exists()) {
//                    success = sdMain.mkdir()
//                }
//
//                if (success) {
//                    val sd = File(fileName)
//
//                    if (!sd.exists()) {
//                        success = sd.mkdir()
//                    }
//
//                    if (success) {
//                        val dest = File(sd, fileName)
//
//                        try {
//                            PrintWriter(dest).use {
//                                    out -> out.print(json)
//                            }
//                        }
//                        catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    else {
//                        throw Exception("File not created")
//                    }
//                }
//                else {
//                    throw Exception("Directory not created")
//                }
//            }
//            else {
//                throw Exception("Access denied for external storage")
//            }
//        }
    }

    fun readData(directory: String, fileName: String) : String {
        if (storage == StorageType.INTERNAL) {
            try {
                context.openFileInput(fileName).use {
                    return it.readBytes().decodeToString()
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
        else {
            return ""
        }
    }

    inline fun <reified T> serialize(obj: T, directory: String, fileName: String) {
        val json = Json.encodeToString(obj)
        writeData(json, directory, fileName)
    }

    inline fun <reified T> deserialize(directory: String, fileName: String) : T? {
        val json = readData(directory, fileName)
        return if (json.isEmpty()) {
            null
        } else {
            Json.decodeFromString(json)
        }
    }

    enum class StorageType {
        INTERNAL,
        EXTERNAL
    }
}