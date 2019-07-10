package jetbooster.sample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val content = MutableLiveData<String>()
}
