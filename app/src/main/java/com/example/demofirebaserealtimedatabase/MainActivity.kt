package com.example.demofirebaserealtimedatabase


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var imageUri: Uri? = null
    private val PICK_IMAGE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance("https://demofirebaserealtimedata-26fa3-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database.getReference("user")
        btnDone.setOnClickListener{
            addUser()
        }
        btnGet.setOnClickListener{
            val userName = edtName.text.toString()
            if(userName.isNotEmpty()){
                readUser(userName)
            }
            else {
                Toast.makeText(applicationContext,"Name is empty",Toast.LENGTH_LONG).show()
            }
        }
        btnChoose.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        btnRecognize.setOnClickListener {
            detector.processImage(FirebaseVisionImage.fromFilePath(applicationContext,imageUri!!))
                .addOnSuccessListener { firebaseVisionText ->
                    txtRecognize.text = firebaseVisionText.text
                }
                .addOnFailureListener { e ->
                }
        }
        if(intent.extras != null){
            for (i in intent.extras!!.keySet()){
                if(i.equals("title")){
                    Log.e("MainActivity", "onCreate: ${intent.extras!!.getString(i)}", )
                }
                if(i.equals("body")){
                    Log.e("MainActivity", "onCreate: ${intent.extras!!.getString(i)}", )
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            ivChoose.setImageURI(imageUri)
        }
    }


    private fun readUser(userName: String) {
        Log.e("readUser", "readUser: ${userName}")
        reference.child(userName).get().addOnSuccessListener {
            if(it.exists()){
                "${it.child("name").value} \n ${it.child("age").value} \n ${it.child("address").value}".also { txtInfo.text = it }
            }else{
                    Toast.makeText(applicationContext,"User does not exist!",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"Error get user",Toast.LENGTH_LONG).show()
        }
    }

    private fun addUser(){
            val info = Info(edtName.text.toString(),edtAge.text.toString().toInt(), edtAddress.text.toString())
            reference.child(info.name).setValue(info).addOnSuccessListener {
                edtName.text.clear()
                edtAge.text.clear()
                edtAddress.text.clear()
                Toast.makeText(applicationContext,"Successfully",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext,"Error add user",Toast.LENGTH_LONG).show()
            }

    }
}