package jp.s64.poc.cleanarchitecture.app.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import jp.s64.poc.cleanarchitecture.app.R
import jp.s64.poc.cleanarchitecture.app.databinding.MainActivityBinding
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import jp.s64.poc.cleanarchitecture.app.interactor.UserInteractor
import jp.s64.poc.cleanarchitecture.app.repository.UserRepository
import jp.s64.poc.cleanarchitecture.app.service.ApiService
import jp.s64.poc.cleanarchitecture.app.usecase.GetUserUseCase
import jp.s64.poc.cleanarchitecture.exception.app.AppLayerException
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private val vm: MainViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.lifecycleOwner = this
        binding.vm = vm

        vm.useCase = UserInteractor(
            UserRepository(
                ApiService()
            )
        )

        binding.refresh.setOnClickListener {
            vm.refreshUser(
                binding.userId.text.toString().let {
                    if (it.isEmpty()) null else it.toLong()
                }
            )
        }

        vm.featureMessage.observe(this, Observer {
            if (it != null) {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        vm.onDialogDismiss()
                    }
                    .show()
            } else {
                // no-op
            }
        })
    }

}

class MainViewModel : ViewModel() {

    lateinit var useCase: UserInteractor

    val user = MutableLiveData<User?>()
    val result = MutableLiveData<String>()
    val status = MutableLiveData("Not loaded")

    val featureMessage = MutableLiveData<String?>()

    fun refreshUser(id: UserId?) {
        viewModelScope.launch {
            try {
                status.value = "Loading..."
                when (val response = useCase.getUser(id)) {
                    is Either.Left -> {
                        user.value = null
                        result.value = when (val error = response.a) {
                            is AppLayerException.NetworkState -> "Network error."
                            is AppLayerException.FeatureSpecific -> {
                                displayFeatureMessage(error.parent)
                                ""
                            }
                            else -> error.parent.toString()
                        }
                    }
                    is Either.Right -> {
                        user.value = response.b
                        result.value = "Succeed!"
                    }
                }
            } finally {
                status.value = "Loaded."
            }
        }
    }

    private fun displayFeatureMessage(ex: Throwable?) {
        featureMessage.value = when (ex) {
            is GetUserUseCase.MyUserLimitException -> "feature-specific: MyUserLimitException"
            is GetUserUseCase.MyEmptyUserIdException -> "feature-specific: MyEmptyUserIdException"
            else -> "Unknown (feature-specific) exception!"
        }
    }

    fun onDialogDismiss() {
        featureMessage.value = null
    }

}
