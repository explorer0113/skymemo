package com.ldg.skymemo.memo

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldg.skymemo.data.FileSaver
import com.ldg.skymemo.data.Memo
import com.ldg.skymemo.util.ListLiveData
import com.ldg.skymemo.util.loadBitmapFromView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class HandleViewModel @Inject constructor(private val memoFileSaverImpl: FileSaver<Memo>) :ViewModel(){

    private val _saveButtonClick= MutableLiveData<Boolean>().apply { value=false }
    val saveButtonClick:LiveData<Boolean>
        get() = _saveButtonClick

    val pictures= ListLiveData<Bitmap>()


    private val pictureLimit=10

    fun onSaveButtonClick(){
        _saveButtonClick.value=true
    }
    fun onSaveButtonClickDone(){
        _saveButtonClick.value=false
    }


    fun saveMemo(text:String, view: DrawingView, activity: Activity){
        val isEmpty=(pictures.size() == 0  && text == "" && view.isDrawNothing())
        if(isEmpty) {
            Log.d("HandleViewModel","DrawNothing")
            return
        }
            val draw= loadBitmapFromView(view)
            val memo=Memo(System.currentTimeMillis(),content=text,photos=pictures.value,drawing=draw)
            viewModelScope.launch {
                memoFileSaverImpl.saveOnInternalStorage(memo,activity)?: return@launch
            }
    }

    fun addPicture(bitmap: Bitmap?) {
        bitmap?:return
        if(checkLimit().not())
            return
        pictures.add(bitmap)
    }

    fun removePicture(index :Int){
        if(checkLimit().not())
            return
        pictures.remove(index)
    }

    private fun checkLimit():Boolean{
        return pictures.size() <= pictureLimit || pictures.size()>=0
    }

}