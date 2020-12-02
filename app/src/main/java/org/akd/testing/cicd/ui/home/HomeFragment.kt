package org.akd.testing.cicd.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.legatotechnologies.updater.ForceUpdate
import com.legatotechnologies.updater.Language
import org.akd.testing.cicd.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = ForceUpdate.initUpdateJSon(
            "https://developer.android.com",
            "1.1.0",
            "test",
            0
        )

        ForceUpdate(context, this)
            .setJSON(json)
            .setTheme(R.style.AlertDialogCustom)
            .setCustomView(R.layout.dialog_new_version)
            .setLang(Language.Eng)
            .setNotificationTime(30, ForceUpdate.Milli)
            .start()

    }
}