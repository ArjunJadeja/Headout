package com.arjun.headout.util

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.arjun.headout.R

class ProgressLoadingHelper {

    companion object {

        private var progressDialog: Dialog? = null
        private var loadingAnimator: Animator? = null

        fun showProgressBar(context: Context, message: String) {
            progressDialog?.dismiss()

            val dialogView = LayoutInflater.from(context).inflate(R.layout.progress_indicator, null)

            val loadingIcon = dialogView.findViewById<ImageView>(R.id.loadingIcon)
            val loadingText = dialogView.findViewById<TextView>(R.id.loadingText)
            loadingText.text = message

            // Load the animator from the XML and set it to the ImageView
            loadingAnimator = AnimatorInflater.loadAnimator(context, R.animator.bounce)
            loadingAnimator?.apply {
                setTarget(loadingIcon)
                start()
            }

            progressDialog = Dialog(context).apply {
                setContentView(dialogView)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }
        }


        // Dismiss the currently shown progress bar
        fun dismissProgressBar() {
            progressDialog?.dismiss()
            progressDialog = null
            // Also, stop the animation when the progress bar is dismissed
            if (loadingAnimator?.isRunning == true) {
                loadingAnimator?.end()
            }
        }
    }
}