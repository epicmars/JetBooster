package jetbooster.sample.ui

import android.os.Bundle
import jetbooster.sample.BaseActivity
import jetbooster.sample.R
import jetbooster.sample.databinding.ActivityMainBinding
import jetbooster.sample.vm.MainViewModel
import jetbooster.sample.vm.SensorViewModel
import layoutbinder.annotations.BindLayout
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModel : MainViewModel

    @Inject
    lateinit var sensorViewModel: SensorViewModel

    @BindLayout(R.layout.activity_main)
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}
