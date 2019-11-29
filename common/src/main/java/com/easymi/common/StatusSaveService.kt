package com.easymi.common

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.easymi.common.push.MqttManager
import com.easymi.component.Config
import com.easymi.component.app.XApp
import com.easymi.component.utils.EmUtil
import com.easymi.component.utils.NetUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StatusSaveService : Service() {

    companion object {
        @JvmField
        val START = "com.easymi.component.statusSaveServiceStart"
        @JvmField
        val END = "com.easymi.component.statusSaveServiceEnd"
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return intent?.let {
            when (it.action) {
                START -> startFun()
                END -> stopFun()
                else -> START_NOT_STICKY
            }
        } ?: START_NOT_STICKY
    }


    var disposable: Disposable? = null

    private fun startFun(): Int {
        Observable.interval(0, 5, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        this@StatusSaveService.disposable = d;
                    }

                    override fun onNext(t: Long) {
                        val folderName = "${Environment.getExternalStorageDirectory()}/statusSave/"
                        val fileName = "$folderName${getDate()}-${Config.APP_KEY}-${EmUtil.getEmployId()}.txt"
                        File(folderName).run {
                            if (!exists()) {
                                mkdirs()
                            }
                            if (isDirectory && listFiles().isNotEmpty()) {
                                listFiles().filter {
                                    !fileName.contains(it.name)
                                }.forEach {
                                    it.delete()
                                }
                            }
                        }

                        File(fileName).run {
                            if (!exists()) {
                                createNewFile()
                            }
                            FileWriter(this, true).run {
                                write("\n{timeStamp = ${System.currentTimeMillis()}  netWorkStatus = ${NetUtil.getNetWorkState(XApp.getInstance())}  mqttStatus = ${MqttManager.getInstance().isConnected}}")
                                flush()
                                close()
                            }
                        }

                    }

                    override fun onError(e: Throwable) {
                        Log.e("here", "error ${e.message}")
                    }

                })
        return START_STICKY
    }

    private fun getDate() = SimpleDateFormat("yyyy-MM-dd").format(Date())

    private fun stopFun(): Int {
        disposable?.run {
            dispose()
        }
        stopSelf()
        return START_NOT_STICKY
    }
}