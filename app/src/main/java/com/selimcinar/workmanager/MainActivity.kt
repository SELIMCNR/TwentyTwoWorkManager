package com.selimcinar.workmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

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


        val data = Data.Builder().putInt("intKey",1).build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            //.setInitialDelay(5,TimeUnit.HOURS)
            //.addTag("myTag")
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

        //en az 15 dakika da bir işi yap.
        val myWorkRequest1 :WorkRequest = PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest1)

        //gözlemleme işin durumunu
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest1.id).observe(this,
            Observer {
                if (it.state==WorkInfo.State.RUNNING){
                    println("running")
                }
                else if (it.state==WorkInfo.State.FAILED){
                    println("failed")
                }
                else if (it.state == WorkInfo.State.SUCCEEDED){
                    println("succeded")
                }
            })

        //Chaning tek işlem yada tek işlemler listesi sonra başka işlem
        val oneTimeWorkRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest)
            .then(oneTimeWorkRequest)
            .enqueue()




    }
}