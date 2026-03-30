package com.example.git_repo_4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.git_repo_4.utils.PreferencesManager
import com.example.git_repo_4.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var preferencesManager: PreferencesManager
    private var pendingCameraUri: Uri? = null

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            saveAndApplyProfileImage(uri)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraCapture()
        } else {
            view?.let { Snackbar.make(it, "Camera permission denied", Snackbar.LENGTH_SHORT).show() }
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { saveAndApplyProfileImage(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())

        bindProfile(view)
        setupProfileImageActions(view)
        setupPushNotifications(view)
        setupNavigation(view)
        setupGithub(view)
        setupSignOut(view)
        setupBack(view)
    }

    private fun bindProfile(view: View) {
        val name = preferencesManager.getUserName()
        view.findViewById<TextView>(R.id.tvUserName)?.text = name
        view.findViewById<TextView>(R.id.tvUserEmail)?.text = preferencesManager.getUserEmail()

        val githubUsername = preferencesManager.getGithubUsername()
        view.findViewById<TextView>(R.id.tvGithubSubtitle)?.text = "Connected: @$githubUsername"

        applyAvatar(view, name)
    }

    private fun setupProfileImageActions(view: View) {
        view.findViewById<ImageView>(R.id.ivEditProfile)?.setOnClickListener {
            showImageSourceChooser()
        }
    }

    private fun showImageSourceChooser() {
        val options = arrayOf("Choose from Gallery", "Take Photo", "Remove Photo")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Profile Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickFromGallery()
                    1 -> requestCameraAndCapture()
                    2 -> removeProfilePhoto()
                }
            }
            .show()
    }

    private fun removeProfilePhoto() {
        preferencesManager.setProfileImageUri(null)
        view?.let { applyAvatar(it, preferencesManager.getUserName()) }
    }

    private fun pickFromGallery() {
        pickMediaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun requestCameraAndCapture() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCameraCapture()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraCapture() {
        val photoFile = File(requireContext().filesDir, "profile_photo.jpg")
        val authority = "${requireContext().packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(requireContext(), authority, photoFile)
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun saveAndApplyProfileImage(sourceUri: Uri) {
        val internalUri = copyToInternalStorage(sourceUri)
        if (internalUri != null) {
            preferencesManager.setProfileImageUri(internalUri.toString())
            view?.let { applyAvatar(it, preferencesManager.getUserName()) }
        } else {
            view?.let { Snackbar.make(it, "Unable to save image", Snackbar.LENGTH_SHORT).show() }
        }
    }

    private fun copyToInternalStorage(sourceUri: Uri): Uri? {
        return try {
            val targetFile = File(requireContext().filesDir, "profile_photo_persisted.jpg")
            requireContext().contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            Uri.fromFile(targetFile)
        } catch (_: Exception) {
            null
        }
    }

    private fun applyAvatar(view: View, name: String) {
        val imageView = view.findViewById<ImageView>(R.id.ivProfilePhoto)
        val letterView = view.findViewById<TextView>(R.id.tvProfileLetter)
        val savedUri = preferencesManager.getProfileImageUri()

        if (!savedUri.isNullOrBlank()) {
            val parsed = Uri.parse(savedUri)
            imageView?.setImageURI(parsed)
            imageView?.visibility = View.VISIBLE
            letterView?.visibility = View.GONE
        } else {
            val letter = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            letterView?.text = letter.uppercase(Locale.getDefault())
            letterView?.visibility = View.VISIBLE
            imageView?.setImageDrawable(null)
            imageView?.visibility = View.GONE
        }
    }

    private fun setupPushNotifications(view: View) {
        val notifSwitch = view.findViewById<SwitchMaterial>(R.id.switchPushNotifications)
        notifSwitch?.isChecked = preferencesManager.isPushNotificationsEnabled()
        notifSwitch?.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setPushNotificationsEnabled(isChecked)
        }
    }

    private fun setupNavigation(view: View) {
        view.findViewById<MaterialCardView>(R.id.cardProfileInfo)?.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        view.findViewById<MaterialCardView>(R.id.cardSecurity)?.setOnClickListener {
            startActivity(Intent(requireContext(), SecurityActivity::class.java))
        }
    }

    private fun setupGithub(view: View) {
        val githubUrl = "https://github.com/${preferencesManager.getGithubUsername()}"

        val openGithub = View.OnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)))
        }

        view.findViewById<ImageView>(R.id.ivGithubLink)?.setOnClickListener(openGithub)
        view.findViewById<MaterialCardView>(R.id.cardGithub)?.setOnClickListener(openGithub)
    }

    private fun setupSignOut(view: View) {
        view.findViewById<View>(R.id.btnSignOut)?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            GoogleSignIn.getClient(requireContext(), gso).signOut()

            FirebaseAuth.getInstance().signOut()
            SessionManager(requireContext()).logout()
            preferencesManager.clearAllData()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            Snackbar.make(view, "Logged out", Snackbar.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun setupBack(view: View) {
        view.findViewById<ImageView>(R.id.ivSettingsBack)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { bindProfile(it) }
    }
}
