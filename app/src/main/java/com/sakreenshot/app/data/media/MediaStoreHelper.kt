package com.sakreenshot.app.data.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

data class MediaStoreItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val relativePath: String?,
    val dateAdded: Long,
    val fileSize: Long,
    val width: Int,
    val height: Int
)

class MediaStoreHelper(private val context: Context) {

    fun queryScreenshots(): List<MediaStoreItem> {
        val screenshots = mutableListOf<MediaStoreItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        // Query all images, we'll filter by path/name to catch different OEM screenshot folders
        val selection = "${MediaStore.Images.Media.SIZE} > 0"
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: ""
                val path = cursor.getString(pathColumn) ?: ""

                if (isScreenshot(name, path)) {
                    val contentUri = ContentUris.withAppendedId(queryUri, id)
                    val dateAdded = cursor.getLong(dateColumn) * 1000L // convert to ms
                    val size = cursor.getLong(sizeColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)

                    screenshots.add(
                        MediaStoreItem(
                            id = id,
                            uri = contentUri,
                            displayName = name,
                            relativePath = path,
                            dateAdded = dateAdded,
                            fileSize = size,
                            width = width,
                            height = height
                        )
                    )
                }
            }
        }
        return screenshots
    }

    private fun isScreenshot(name: String, path: String): Boolean {
        val lowerName = name.lowercase()
        val lowerPath = path.lowercase()

        return lowerPath.contains("screenshot") ||
               lowerName.contains("screenshot") ||
               lowerName.startsWith("screenshot_") ||
               lowerName.startsWith("screenshot-") ||
               lowerPath.contains("screen_recorder") ||
               lowerName.contains("smartcapture") ||
               lowerName.contains("screencapture")
    }
}
