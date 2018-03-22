package net.samystudio.beaver.ui.base.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.evernote.android.state.StateSaver
import dagger.android.AndroidInjection
import net.samystudio.beaver.ui.base.viewmodel.BaseActivityViewModel
import net.samystudio.beaver.ui.main.home.HomeController
import javax.inject.Inject

abstract class BaseActivity<VM : BaseActivityViewModel> : AppCompatActivity()
{
    @get:LayoutRes
    protected abstract val layoutViewRes: Int
    @Inject
    protected lateinit var viewModelProvider: ViewModelProvider
    protected abstract val viewModelClass: Class<VM>
    @Inject
    lateinit var router: Router
    lateinit var viewModel: VM
    /**
     * For dagger only.
     * @hide
     */
    var saveInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(layoutViewRes)
        ButterKnife.bind(this)
        saveInstanceState = savedInstanceState
        AndroidInjection.inject(this)
        saveInstanceState = null

        viewModel = viewModelProvider.get(viewModelClass)
        viewModel.handleCreate()
        viewModel.handleIntent(intent)
        onViewModelCreated()

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(HomeController()))
    }

    @CallSuper
    protected open fun onViewModelCreated()
    {
        viewModel.titleObservable.observe(this, Observer { it -> title = it })
        viewModel.resultEvent.observe(this, Observer {
            it?.let {
                setResult(it.code, it.intent)
                if (it.finish)
                    finish()
            }
        })
    }

    override fun onNewIntent(intent: Intent)
    {
        super.onNewIntent(intent)

        setIntent(intent)
        viewModel.handleIntent(intent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.handleResult(requestCode, resultCode, data)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?)
    {
        super.onRestoreInstanceState(savedInstanceState)

        StateSaver.restoreInstanceState(this, savedInstanceState)

        if (savedInstanceState != null)
            viewModel.handleRestoreInstanceState(savedInstanceState)
    }

    override fun onResume()
    {
        super.onResume()

        viewModel.handleReady()
    }

    override fun onBackPressed()
    {
        if (!router.handleBack())
        {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        StateSaver.saveInstanceState(this, outState)

        viewModel.handleSaveInstanceState(outState)
    }
}