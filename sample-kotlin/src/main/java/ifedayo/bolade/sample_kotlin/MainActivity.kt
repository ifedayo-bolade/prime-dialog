package ifedayo.bolade.sample_kotlin

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ifedayo.bolade.primedialog.PrimeDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        demoSimpleDialog()

//        lifecycleScope.launch {
//            delay(1000)
//            demoAnimatedDialog()
//        }

//        demoTitledDialog()
//        demoAccentColoredDialog()
//        demoMultiColoredDialog()
    }

    fun demoSimpleDialog() {
        PrimeDialog(this)
            .setMessage("Hello world!")
            .setNegativeButton("DISMISS")
            .setPositiveButton("I SEE YOU"){ dialog ->
                dialog.dismiss()
                showMessage("I see you!")
            }
            .show()
    }

    fun demoAnimatedDialog() {
        PrimeDialog(this, R.style.MyDialogTheme)
            .setIcon(R.drawable.ic_info).setTitle("HEY THERE!")
            .setMessage("I'm your animated dialog styled with an ENTER and EXIT animation defined within the app theme.")
            .setMessageLineSpacing(6F)
            .setNegativeButton("DISMISS")
            .setPositiveButton("I SEE YOU"){ dialog ->
                dialog.dismiss()
                showMessage("I see you!")
            }
            .show()
    }

    fun demoTitledDialog() {
        PrimeDialog(this)
            .setIcon(R.drawable.ic_info).setIconSize(30)
            .setTitle("HEY THERE!")
            .setMessage("This dialog is styled with an IN/OUT animation")
            .setMessageLineSpacing(6F)
            .setPositiveButton("DISMISS")
            .show()
    }

    fun demoCustomColoredDialog() {
        PrimeDialog(this)
            .setHeaderBackgroundRes(R.drawable.header)
            .setAccentColor(
                Color.GREEN,
                PrimeDialog.ACCENT_MODE_ALL
            )
            .setIcon(R.drawable.ic_info).setIconSize(30)
            .setTitle("HEY THERE!")
            .setMessage("Defined your dialog accent color by calling 'setAccentColor()'")
            .setMessageLineSpacing(6F)
            .setPositiveButton("DISMISS")
            .show()
    }

    fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}