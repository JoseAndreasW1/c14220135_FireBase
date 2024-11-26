package com.example.c14220135_firebase

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

val db = Firebase.firestore
var DataProvinsi = ArrayList<dftarProvinsi>()
lateinit var lvAdaper : ArrayAdapter<dftarProvinsi>
lateinit var _etProvinsi : EditText
lateinit var _etIbuKota : EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _etProvinsi = findViewById<EditText>(R.id.etNamaProvinsi)
        _etIbuKota = findViewById<EditText>(R.id.etIbuKotaProvinsi)
        val _btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val _lvData = findViewById<ListView>(R.id.lvData)
        lvAdaper = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            DataProvinsi
        )
        _lvData.adapter = lvAdaper

        _btnSimpan.setOnClickListener {
            val provinsi = _etProvinsi.text.toString()
            val ibuKota = _etIbuKota.text.toString()

            if (provinsi.isNotEmpty() && ibuKota.isNotEmpty()) {
                TambahData(db, provinsi, ibuKota)
                readData(db)

            }
        }
    }

    fun TambahData(db:FirebaseFirestore, Provinsi: String, Ibukota: String){
        val dataBaru = dftarProvinsi(Provinsi, Ibukota)
        db.collection("tbProvinsi")
            .add(dataBaru)
            .addOnSuccessListener {
                _etProvinsi.setText("")
                _etIbuKota.setText("")
                Log.d("Firebase", "Data Berhasil Disimpan")
            }
            .addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }


    }

    fun readData(db: FirebaseFirestore){
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                DataProvinsi.clear()
                for(document in result){
                    val readData = dftarProvinsi(
                        document.data.get("provinsi").toString(),
                        document.data.get("ibukota").toString(),
                    )
                    DataProvinsi.add(readData)
                }
                lvAdaper.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }
}