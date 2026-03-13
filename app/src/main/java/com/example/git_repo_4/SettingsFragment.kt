package com.example.git_repo_4

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifSwitch: SwitchMaterial? = view.findViewById(R.id.switchPushNotifications)
        notifSwitch?.isChecked = true

        val cardLight: MaterialCardView? = view.findViewById(R.id.cardLight)
        val cardGreen: MaterialCardView? = view.findViewById(R.id.cardGreen)

        fun selectGreenMode(selectedGreen: Boolean) {
            if (selectedGreen) {
                cardGreen?.isChecked = true
                cardGreen?.setCardBackgroundColor(requireContext().getColor(R.color.rp_neon_green))
                cardLight?.isChecked = false
                cardLight?.setCardBackgroundColor(requireContext().getColor(R.color.rp_surface))
            } else {
                cardLight?.isChecked = true
                cardLight?.setCardBackgroundColor(requireContext().getColor(R.color.rp_neon_green))
                cardGreen?.isChecked = false
                cardGreen?.setCardBackgroundColor(requireContext().getColor(R.color.rp_surface))
            }
        }

        cardLight?.setOnClickListener { selectGreenMode(false) }
        cardGreen?.setOnClickListener { selectGreenMode(true) }

        // Default to Green Juice mode
        selectGreenMode(true)
    }
}
