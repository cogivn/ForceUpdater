package com.legatotechnologies.v2.updater.ui

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.DimenRes
import androidx.fragment.app.DialogFragment
import com.legatotechnologies.updater.R
import com.legatotechnologies.updater.databinding.DialogForceUpdateViewBinding
import com.legatotechnologies.v2.updater.ForceUpdate
import com.legatotechnologies.v2.updater.ForceUpdateFlow
import com.legatotechnologies.v2.updater.OnOptionalDialogDismissListener
import com.legatotechnologies.v2.updater.datas.Version
import com.legatotechnologies.v2.updater.datas.enums.UpdateType
import kotlinx.parcelize.Parcelize


open class ForceUpdateDialogFragment(
    private var layoutId: Int = 0
) : DialogFragment() {
    private val mArguments: Arguments? by lazy { arguments?.getParcelable(CONTENT) }
    private var mDismissListener: OnOptionalDialogDismissListener? = null
    private var mRadius: Int = R.dimen.zero

    override fun onStart() {
        super.onStart()
        isCancelable = mArguments?.version?.type == UpdateType.OPTIONAL
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            attributes = attributes.apply {
                width = resources.getDimensionPixelSize(R.dimen._310sdp)
                height = WRAP_CONTENT
            }
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        if (layoutId != 0) layoutId else R.layout.dialog_force_update_view,
        container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mRootView = DialogForceUpdateViewBinding.bind(view)
        with(mRootView) {
            root.radius = resources.getDimensionPixelOffset(mRadius).toFloat()
            tvContentDialog.text = mArguments?.version?.content
            btnOkDialog.setOnClickListener { _ ->
                val context = root.context
                mArguments?.version?.url?.let {
                    ForceUpdateFlow.goToUpdate(context, it)
                } ?: ForceUpdateFlow.showErrorMessage(context)
            }

            btnUpdateLaterDialog.setOnClickListener { dismissAllowingStateLoss() }
            btnSkipDialog.setOnClickListener {
                //set latest version to preferences
                ForceUpdateFlow.setPreferenceLastVersion(
                    it.context,
                    mArguments?.version?.latestVersion
                )
                ForceUpdateFlow.setPreferenceForLaterUpdate(
                    it.context,
                    true,
                    mArguments?.notificationSkipTime
                        ?: ForceUpdate.DEFAULT_NOTIFICATION_TIME
                )
                dismissAllowingStateLoss()
            }
            mArguments?.version?.apply {
                val visibility = if (type == UpdateType.OPTIONAL)
                    View.VISIBLE else View.INVISIBLE
                btnUpdateLaterDialog.visibility = visibility
                btnSkipDialog.visibility = visibility
            }
        }
    }

    fun setOnDismissListener(listener: OnOptionalDialogDismissListener) {
        this.mDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mDismissListener?.onOptionalDialogDismiss()
    }

    fun setContentView(layoutId: Int) {
        this.layoutId = layoutId
    }

    fun setRadius(@DimenRes radius: Int) {
        mRadius = radius
    }

    companion object {
        const val CONTENT = "force_update#content"
        fun getInstance(args: Bundle) =
            ForceUpdateDialogFragment().apply { arguments = args }
    }

    @Parcelize
    data class Arguments(
        val notificationSkipTime: Long = ForceUpdate.DEFAULT_NOTIFICATION_TIME,
        val version: Version = Version()
    ) : Parcelable
}