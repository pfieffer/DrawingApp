package com.kidsdrawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? =
        null // A variable for current color is picked from color pallet.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawing_view.setSizeForBrush(20.toFloat()) // Setting the default brush size to drawing view.

        /**
         * This is to select the default Image button which is
         * active and color is already defined in the drawing view class.
         * As the default color is black so in our color pallet it is on 2 position.
         * But the array list start position is 0 so the black color is at position 1.
         */
        mImageButtonCurrentPaint = ll_paint_colors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_pressed
            )
        )

        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        ib_gallery.setOnClickListener {
            //Very firstly we will check the app required a storage permission.
            // So we will add a permission in the Android.xml for storage.

            //First checking if the app is already having the permission
            if (isReadStorageAllowed()) {

                // This is for selecting the image from local store or let say from Gallery/Photos.
                val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, GALLERY)
            } else {

                //If the app don't have storage access permission we will ask for it.
                requestStoragePermission()
            }
        }

        // TODO(Step 4 - Adding an click event for undo option.)
        // START
        ib_undo.setOnClickListener {
            // This is for undo recent stroke.
            drawing_view.onClickUndo()
        }
        // END
    }

    /**
     * This is override method and the method will be called when the user will tap on allow or deny
     *
     * Determines whether the delegate should handle
     * {@link ActivityCompat#requestPermissions(Activity, String[], int)}, and request
     * permissions if applicable. If this method returns true, it means that permission
     * request is successfully handled by the delegate, and platform should not perform any
     * further requests for permission.
     *
     * @param activity The target activity.
     * @param permissions The requested permissions. Must me non-null and not empty.
     * @param requestCode Application specific request code to match with a result reported to
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission granted now you can read the storage files.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this@MainActivity,
                    "Oops you just denied the permission.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * This is override method here we get the selected image
     * based on the code what we have passed for selecting the image.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                try {
                    if (data!!.data != null) {

                        // Here if the user selects the image from local storage make the image view visible.
                        // By Default we will make it VISIBILITY as GONE.
                        iv_background.visibility = View.VISIBLE

                        // Set the selected image to the backgroung view.
                        iv_background.setImageURI(data.data)
                    } else {
                        // If the selected image is not valid. Or not selected.
                        Toast.makeText(
                                this@MainActivity,
                                "Error in parsing the image or its corrupted.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Method is used to launch the dialog to select different brush sizes.
     */
    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size :")
        val smallBtn = brushDialog.ib_small_brush
        smallBtn.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        })
        val mediumBtn = brushDialog.ib_medium_brush
        mediumBtn.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        })

        val largeBtn = brushDialog.ib_large_brush
        largeBtn.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        })
        brushDialog.show()
    }

    /**
     * Method is called when color is clicked from pallet_normal.
     *
     * @param view ImageButton on which click took place.
     */
    fun paintClicked(view: View) {
        if (view !== mImageButtonCurrentPaint) {
            // Update the color
            val imageButton = view as ImageButton
            // Here the tag is used for swaping the current color with previous color.
            // The tag stores the selected view
            val colorTag = imageButton.tag.toString()
            // The color is set as per the selected tag here.
            drawing_view.setColor(colorTag)
            // Swap the backgrounds for last active and currently active image button.
            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
            mImageButtonCurrentPaint!!.setImageDrawable(
                    ContextCompat.getDrawable(
                            this,
                            R.drawable.pallet_normal
                    )
            )

            //Current view is updated with selected view in the form of ImageButton.
            mImageButtonCurrentPaint = view
        }
    }

    /**
     * Requesting permission
     */
    private fun requestStoragePermission() {

        /**
         * Gets whether you should show UI with rationale for requesting a permission.
         * You should do this only if you do not have the permission and the context in
         * which the permission is requested does not clearly communicate to the user
         * what would be the benefit from granting this permission.
         * <p>
         * For example, if you write a camera app, requesting the camera permission
         * would be expected by the user and no rationale for why it is requested is
         * needed. If however, the app needs location for tagging photos then a non-tech
         * savvy user may wonder how location is related to taking photos. In this case
         * you may choose to show UI with rationale of requesting this permission.
         * </p>
         *
         * @param activity The target activity.
         * @param permission A permission your app wants to request.
         * @return Whether you can show permission rationale UI.
         *
         */
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        /**
         * Requests permissions to be granted to this application. These permissions
         * must be requested in your manifest, otherwise they will not be granted to your app.
         */

        //And finally ask for the permission
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    /**
     * We are calling this method to check the permission status
     */
    private fun isReadStorageAllowed(): Boolean {
        //Getting the permission status
        // Here the checkSelfPermission is
        /**
         * Determine whether <em>you</em> have been granted a particular permission.
         *
         * @param permission The name of the permission being checked.
         *
         */
        val result = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        /**
         *
         * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
         * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
         *
         */
        //If permission is granted returning true and If permission is not granted returning false
        return result == PackageManager.PERMISSION_GRANTED
    }

    companion object{


        /**
         * Permission code that will be checked in the method onRequestPermissionsResult
         *
         * For more Detail visit : https://developer.android.com/training/permissions/requesting#kotlin
         */
        private const val STORAGE_PERMISSION_CODE = 1

        // This is to identify the selection of image from Gallery.
        private const val GALLERY = 2
    }
}