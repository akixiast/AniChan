package com.aki.anichan.ui.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.aki.anichan.R
import com.aki.anichan.databinding.ActivityLaunchBinding
import com.aki.anichan.helper.utils.DeepLink
import com.aki.anichan.ui.base.BaseActivity
import com.aki.anichan.ui.base.DialogManager
import com.aki.anichan.ui.base.NavigationManager
import com.aki.anichan.ui.root.RootActivity
import io.reactivex.rxjava3.core.Observable

class LaunchActivity : BaseActivity<ActivityLaunchBinding>() {

    override lateinit var navigationManager: NavigationManager

    override lateinit var dialogManager: DialogManager

    override val incomingDeepLink: Observable<DeepLink>
        get() = Observable.never()

    override fun generateViewBinding(): ActivityLaunchBinding {
        return ActivityLaunchBinding.inflate(layoutInflater)
    }

    override fun setUpLayout() {
        val targetIntent = Intent(this, RootActivity::class.java)
        targetIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        targetIntent.data = intent.data
        targetIntent.putExtra("RESTART", intent.getBooleanExtra("RESTART", false))
        startActivity(targetIntent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun setUpObserver() {
        // do nothing
    }
}