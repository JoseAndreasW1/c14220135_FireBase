package com.example.c14220135_firebase

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

val db = Firebase.firestore
var DataProvinsi = ArrayList<dftarProvinsi>()
lateinit var lvAdaper : SimpleAdapter
lateinit var _etProvinsi : EditText
lateinit var _etIbuKota : EditText
var data: MutableList<Map<String, String>> = ArrayList()
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
        lvAdaper = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro", "Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2,
            )
        )
        _lvData.adapter = lvAdaper
        readData(db)
        _btnSimpan.setOnClickListener {
            val provinsi = _etProvinsi.text.toString()
            val ibuKota = _etIbuKota.text.toString()

            if (provinsi.isNotEmpty() && ibuKota.isNotEmpty()) {
                TambahData(db, provinsi, ibuKota)
                readData(db)
            }
        }

        _lvData.setOnItemLongClickListener { parent, view, position, id ->
            val docId = data[position]["DocId"]
            if(docId != null){
                db.collection("tbProvinsi")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firebase", "Berhasil dihapus")
                        readData(db)
                    }
                    .addOnFailureListener {
                        e -> Log.w("Firebase", e.message.toString())
                    }
                lvAdaper.notifyDataSetChanged()
            }
            true
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

    fun readData(db: FirebaseFirestore) {
        db.collection("tbProvinsi").get()
            .addOnSuccessListener { result ->
                data.clear()
                for (document in result) {
                    val provinsi = document.data["provinsi"].toString()
                    val ibukota = document.data["ibukota"].toString()
                    val docId = document.id

                    val dt: MutableMap<String, String> = HashMap()
                    dt["Pro"] = provinsi
                    dt["Ibu"] = ibukota
                    dt["DocId"] = docId
                    data.add(dt)
                }


                lvAdaper.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Error reading data: ${e.message}")
            }
    }

}