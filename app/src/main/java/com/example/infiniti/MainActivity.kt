package com.example.infiniti

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    val url = "https://infiniti-festival-default-rtdb.europe-west1.firebasedatabase.app/"
    var fabExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Log.d("checkfb", "LOL")
        checkIfFirstStart()
        loadUsersCount()
        loadJoke()
        val fab = findViewById<FloatingActionButton>(R.id.fabMain)
        val mView = findViewById<View>(R.id.viewJokes)
        val mConstraint = findViewById<ConstraintLayout>(R.id.mainConstraint)


        val fb = FirebaseDatabase.getInstance(url)
        val ref = fb.getReference("message/joke")
        ref.setValue("Hello, Jenny!")

        fab.setOnClickListener {
            val transform = MaterialContainerTransform().apply {
                // Manually tell the container transform which Views to transform between.
                startView = fab
                endView = mView

                addTarget(endView!!)
//            pathMotion = MaterialArcMotion()
                scrimColor = Color.TRANSPARENT
            }
            fabExpanded = true
            TransitionManager.beginDelayedTransition(mConstraint, transform)
            fab.visibility = View.GONE
            mView.visibility = View.VISIBLE

            val ref = fb.getReference("jokes")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        Log.d("checkfb", it.value.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }



    }

    private fun loadJoke() {
        val button = findViewById<Button>(R.id.buttonJoke)
        button.setOnClickListener {
            val db = FirebaseDatabase.getInstance(url)
            val ref = db.getReference("jokes")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val joketext = findViewById<TextView>(R.id.textJoke)
                    Log.d("checkdbcount", (1..10).random().toString())
                    Log.d("checkdbcount", p0.childrenCount.toString())
                    val num = (1..p0.childrenCount).random()
                    joketext.text = p0.child(num.toString()).value.toString()
                }
            })
        }

    }

    private fun loadUsersCount() {
        val db = FirebaseDatabase.getInstance(url)
        val ref = db.getReference("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val textv = findViewById<TextView>(R.id.textViewUsers)
                val count = p0.getValue().toString().toInt()
                textv.text = "Пользователей скачало приложение: $count"
            }
        })
    }

    private fun checkIfFirstStart() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val first = pref.getBoolean("firstLaunch", true)
        if (first){
            pref.edit()
                .putBoolean("firstLaunch", false)
                .apply()
            increaseUserCount()
        }

    }

    private fun increaseUserCount() {
        val db = FirebaseDatabase.getInstance(url)
        val ref = db.getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val count = p0.value.toString().toInt()
                ref.setValue(count+1)

            }
        })
    }

    override fun onBackPressed() {
        val fab = findViewById<FloatingActionButton>(R.id.fabMain)
        val mView = findViewById<View>(R.id.viewJokes)
        val mConstraint = findViewById<ConstraintLayout>(R.id.mainConstraint)
        if(fabExpanded){
            val transform = MaterialContainerTransform().apply {
                // Manually tell the container transform which Views to transform between.
                startView = mView
                endView = fab

                addTarget(endView!!)

//            pathMotion = MaterialArcMotion()

                scrimColor = Color.TRANSPARENT
            }
            fabExpanded = false
            TransitionManager.beginDelayedTransition(mConstraint, transform)
            mView.visibility = View.GONE
            fab.visibility = View.VISIBLE
        }
        else{
            finish()
        }
    }
}