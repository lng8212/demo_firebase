package com.example.demofirebaserealtimedatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance("https://demofirebaserealtimedata-26fa3-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database.getReference("user")
        done.setOnClickListener{
            addUser()
        }
        btnGet.setOnClickListener{
            val userName = txtName.text.toString()
            if(userName.isNotEmpty()){
                readUser(userName)
            }
            else {
                Toast.makeText(applicationContext,"Name is empty",Toast.LENGTH_LONG).show()
            }
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
            val info = Info(name.text.toString(),age.text.toString().toInt(), address.text.toString())
            reference.child(info.name).setValue(info).addOnSuccessListener {
                name.text.clear()
                age.text.clear()
                address.text.clear()
                Toast.makeText(applicationContext,"Successfully",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext,"Error add user",Toast.LENGTH_LONG).show()
            }

    }
}