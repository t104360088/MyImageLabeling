package com.example.myimagelabeling

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            data?.data?.let { uri ->
                try {
                    val labeler = ImageLabeling.getClient(
                        ImageLabelerOptions.DEFAULT_OPTIONS
                    )
                    val image = InputImage.fromFilePath(this, uri)

                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            val result = arrayListOf<String>()
                            // Task completed successfully
                            for (label in labels) {
                                val text = label.text
                                val confidence = label.confidence

                                result.add("$text, 可信度：$confidence")
                            }

                            imageView.setImageURI(uri)
                            listView.adapter = ArrayAdapter(
                                this,
                                android.R.layout.simple_list_item_1,
                                result
                            )
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,
                                "發生錯誤",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}