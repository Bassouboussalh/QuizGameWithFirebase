package com.boussalh.quizapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.boussalh.quizapp.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {
    private lateinit var quizBinding: ActivityQuizBinding
    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumer = 1

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    private lateinit var timer : CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)
        gameLogic()
        quizBinding.buttonNext.setOnClickListener {
            resetTime()
            gameLogic()
        }
        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }
        quizBinding.textViewOptionOne.setOnClickListener {
            pauseTimer()
            userAnswer = "a"
            if(correctAnswer == userAnswer){
                quizBinding.textViewOptionOne.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
               quizBinding.textViewOptionOne.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOption()
        }
        quizBinding.textViewOptionTwo.setOnClickListener {
            pauseTimer()
            userAnswer = "b"
            if (correctAnswer == userAnswer){
                quizBinding.textViewOptionTwo.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionTwo.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOption()
        }
        quizBinding.textViewOptionThree.setOnClickListener {
            pauseTimer()
            userAnswer = "c"
            if (correctAnswer == userAnswer){
                quizBinding.textViewOptionThree.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionThree.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOption()
        }
        quizBinding.textViewOptionFour.setOnClickListener {
            pauseTimer()
            userAnswer = "d"
            if(correctAnswer == userAnswer){
                quizBinding.textViewOptionFour.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionFour.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOption()
        }
    }

    private fun gameLogic() {
        resetOptions()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount = snapshot.childrenCount.toInt()
                if (questionNumer <= questionCount) {
                    question = snapshot.child(questionNumer.toString()).child("q").value.toString()
                    answerA = snapshot.child(questionNumer.toString()).child("a").value.toString()
                    answerB = snapshot.child(questionNumer.toString()).child("b").value.toString()
                    answerC = snapshot.child(questionNumer.toString()).child("c").value.toString()
                    answerD = snapshot.child(questionNumer.toString()).child("d").value.toString()
                    correctAnswer =
                        snapshot.child(questionNumer.toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewOptionOne.text = answerA
                    quizBinding.textViewOptionTwo.text = answerB
                    quizBinding.textViewOptionThree.text = answerC
                    quizBinding.textViewOptionFour.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    startTimer()
                }else {
                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("Congratulation!!!\nYou have answered all the questions. Do you want to see the result")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result") { dialogWindow, position ->
                        sendScore()
                    }
                    dialogMessage.setNegativeButton("Play Again"){dialogWindow,position ->
                        val intent = Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()

                }

                questionNumer++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun findAnswer(){
        when(correctAnswer){
            "a" -> quizBinding.textViewOptionOne.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewOptionTwo.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewOptionThree.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewOptionFour.setBackgroundColor(Color.GREEN)
        }
    }

    fun disableClickableOfOption(){
        quizBinding.textViewOptionOne.isClickable = false
        quizBinding.textViewOptionTwo.isClickable = false
        quizBinding.textViewOptionThree.isClickable = false
        quizBinding.textViewOptionFour.isClickable = false
    }

    fun resetOptions(){
        quizBinding.textViewOptionOne.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionTwo.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionThree.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionFour.setBackgroundColor(Color.WHITE)

        quizBinding.textViewOptionOne.isClickable = true
        quizBinding.textViewOptionTwo.isClickable = true
        quizBinding.textViewOptionThree.isClickable = true
        quizBinding.textViewOptionFour.isClickable = true
    }

    private fun startTimer(){
        timer = object : CountDownTimer(leftTime,1000){
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOfOption()

                resetOptions()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is up! Continue with next question"
                timerContinue = false
            }

        }.start()
        timerContinue = true
    }

    private fun updateCountDownText(){
        val remainingTime : Int = (leftTime / 1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseTimer(){
        timer.cancel()
        timerContinue = false
    }

    private fun resetTime(){
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    fun sendScore(){
        user?.let {
            val userUID = it.uid
            scoreRef.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUID).child("wrong").setValue(userWrong)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext,"Scores sent to database successfully",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

}