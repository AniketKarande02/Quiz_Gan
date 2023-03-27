package com.example.quizgame

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random


class QuizActivity : AppCompatActivity() {
    lateinit var quizBinding  : ActivityQuizBinding
    var database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("Question")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""

    var correctAnswer =""
    var questionCount =0
    var questionNumber =0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer: CountDownTimer
    private  val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    val questions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do{
           val number = Random.nextInt(1,7)
           Log.d("number",number.toString())
            questions.add(number)
        }
        while (questions.size < 5)
        Log.d("numberOfQuetions",questions.toString())
        gameLogic()


        quizBinding.buttonNexts.setOnClickListener {
            resetTimer()
            gameLogic()
        }
        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }
        quizBinding.textViewA.setOnClickListener {
            pauseTimer()
            userAnswer = "a"
            if(correctAnswer == userAnswer){
                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewzerocorrectAnswers.text = userCorrect.toString()
            }else{
                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrongAnwers.text = userWrong.toString()
                findCorrectAnswer()
            }
        }
        quizBinding.textViewB.setOnClickListener {
            pauseTimer()
            userAnswer = "b"
            if(correctAnswer == userAnswer){
                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewzerocorrectAnswers.text = userCorrect.toString()
            }else{
                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrongAnwers.text = userWrong.toString()
                findCorrectAnswer()
            }
        }
        quizBinding.textViewC.setOnClickListener {
            pauseTimer()

            userAnswer = "c"
            if(correctAnswer == userAnswer){
                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewzerocorrectAnswers.text = userCorrect.toString()
            }else{
                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrongAnwers.text = userWrong.toString()
                findCorrectAnswer()
            }
        }
        quizBinding.textViewD.setOnClickListener {
            pauseTimer()
            userAnswer = "d"
            if(correctAnswer == userAnswer){
                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewzerocorrectAnswers.text = userCorrect.toString()
            }else{
                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrongAnwers.text = userWrong.toString()
                findCorrectAnswer()
            }

            disableClickableOfOption()
        }

    }
    private fun gameLogic(){

        restoreOption()

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount = snapshot.childrenCount.toInt()
                if(questionNumber <  questions.size){

                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB= snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBar3.visibility = View.INVISIBLE
//                    quizBinding.linearLayoutinfo.visibility = View.VISIBLE
//                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE

                    startTimer()

                }else{
                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz GAme")
                    dialogMessage.setMessage("Comgratulation!!! \n You have answer" +
                            " all the questions. Do you want see the result?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result"){dialogWindow,position->
                        sendScore()
                    }
                    dialogMessage.setNegativeButton("Play Again"){dialodWindow,position->
                        val intent = Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    dialogMessage.create().show()
                }
                questionNumber++


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()

            }
        } )
    }
    fun findCorrectAnswer(){
        when(correctAnswer){
            "a"->quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b"->quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c"->quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d"->quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }
    fun disableClickableOfOption(){
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }
    fun restoreOption(){
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true

    }

   private fun startTimer(){
        timer = object : CountDownTimer(leftTime,1000){
            override fun onTick(millisUntilFinish: Long) {
               leftTime = millisUntilFinish
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOfOption()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry,Times is up! Continue with next question."
                timerContinue = false

            }

        }.start()
    }
    fun  updateCountDownText(){
        val remainingTime : Int =(leftTime/1000).toInt()
        quizBinding.textViewTimesstart.text.toString()
    }
    fun pauseTimer(){
        timer.cancel()
        timerContinue = false
    }
    fun resetTimer(){
        pauseTimer()
    }

    fun sendScore(){
        user?.let {
            val userUID = user.uid
            scoreRef.child("Score").child("Correct").setValue(userCorrect)
            scoreRef.child("Score").child("Correct").setValue(userWrong).addOnSuccessListener {
                Toast.makeText(applicationContext,"Score Sent to database Successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

    }
}