package com.example.rateandchat.sports

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rateandchat.BasicActivity
import com.example.rateandchat.Position.teamNumberSave
import com.example.rateandchat.R
import com.example.rateandchat.dataclass.Team
import com.example.rateandchat.profile.ProfilePic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SeasonGuessActivity : BasicActivity() {

    val listOfTeams = mutableListOf<Team>()

    private lateinit var db: FirebaseFirestore
    private lateinit var usersRef : CollectionReference

    private var leagueID = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_guess)

        db = Firebase.firestore

        recyclerView = findViewById(R.id.teamRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamRecyclerAdapter(this, listOfTeams)
        recyclerView.adapter = adapter

        getTeamData()
        
        //leagueID is stored in a variable which will be used to sort the teams correctly
        leagueID = intent.getStringExtra("league name")!!
        val leagueLogo = intent.getStringExtra("league image")

        val imageLogoView = findViewById<ImageView>(R.id.leagueLogoImageView)
        if (leagueLogo?.isNotEmpty()!!) {
            Picasso.get().load(leagueLogo).into(imageLogoView)
        }
    }

    fun moveTeams(teamA : Int, teamB : Int) {
        //and then this function where the user moves the teams in what other he thinks the teams will end in the end of the seaso

            fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
                val tmp = this[index1]
                this[index1] = this[index2]
                this[index2] = tmp
            }
        listOfTeams.swap(teamA, teamB)
        adapter.notifyDataSetChanged()
    }

    fun saveToFirebase(view: View){
        //and then the user uploads his results to the firebase

        //här ska användaren kunna spara den uppdaterade listan listOfTeams till firebase
        usersRef = db.collection("Users")

       /* usersRef.add(listOfTeams).addOnSuccessListener { documentReference ->
            Toast.makeText(this@SeasonGuessActivity, "DocumentSnapshot added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(this@SeasonGuessActivity, "Error handling document", Toast.LENGTH_SHORT).show()
            }
*/
        Log.v("!!!", "not saved to database")
    }

    fun moveUpClick(teamA: Int) {
        if (teamA == 0){
            return
        }

        //the number for teamA needs to be collected from the array
        //so that when user presses any up button the number registers and is put as teamA

        val teamB = teamA - 1
        Log.v("!!!", "teamA $teamA")
        Log.v("!!!", "teamB $teamB")

        moveTeams(teamA, teamB)
        Log.v("!!!", "move up")
    }

    fun moveDownClick(teamA : Int){

        val size = listOfTeams.size -1

        Log.v("!!!", "listofTeams ${listOfTeams.size}")

        if (teamA == size){
            return
        } else {
            val teamB = teamA + 1

            Log.v("!!!", "teamA $teamA")
            Log.v("!!!", "teamB $teamB")

            moveTeams(teamA, teamB)
            Log.v("!!!", "move down")
        }
        //the number for teamA needs to be collected from the array
        //so that when user presses any down button the number registers and is put as teamA
    }

    private fun getTeamData(){
        //get teams from firebase and save it in an array which is shown in the recyclerview

        db.collection("Team")
                .addSnapshotListener { snapshot, e ->
                    listOfTeams.clear()
                    if (snapshot != null) {
                        val teamArray = mutableListOf<Team>()
                        for (document in snapshot.documents) {
                            val teamDoc = document.toObject<Team>()
                            if (teamDoc != null) {

                                //if the team has the same leagueID as stored above it is saved in the array
                                if (teamDoc.league.equals(leagueID)){
                                    teamArray.add(teamDoc)

                                }
                            } else {
                                Log.v("!!!", "no info")
                            }
                        }
                        teamArray.sortBy { it.teamNumber }

                        listOfTeams.addAll(teamArray)
                        adapter.notifyDataSetChanged()
                    }
        }
    }
}