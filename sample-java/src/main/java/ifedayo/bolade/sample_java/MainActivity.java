package ifedayo.bolade.sample_java;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import ifedayo.bolade.primedialog.PrimeDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        String[] strings = new String[]{
                "Simple Dialog",
                "Accent-Colored Dialog",
                "Multi-Colored Dialog",
                "Dimension Dialog",
                "Header Dialog",
                "Animated Dialog",
                "Listener Dialog",
                "Custom View Dialog 1",
                "Custom View Dialog 2",
        };
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));
        SampleAdapter adapter = new SampleAdapter(this, list);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    /** A basic dialog sample.
     * @implNote If your action button is ONLY meant to dismiss the dialog, there is
     * no need to explicitly set an OnDialogButtonClickListener. Clicking the button
     * will trigger an automatic dismiss as shown in the 'DISMISS' button of this sample. */
    void demoSimpleDialog() {
        new PrimeDialog(this)
                .setTitle("HELLO WORLD!")
                .setMessage("I'm PrimeDialog, nice to meet you.")
                .setNegativeButton("DISMISS")
                .setPositiveButton("I SEE YOU", (dialog, buttonId) -> {
                    showMessage("I see you too!");
                    dialog.dismiss();
                }).show();
    }

    /** Define a general color for icon, title and action buttons by calling
     * 'setAccentColor(colorInt)' or 'setAccentColorRes(colorRes)'.
     *
     * You can define which of these elements the color gets applied on by setting
     * 'setAccentColor(colorInt, mode) or 'setAccentColorRes(colorRes, mode)'. */
    void demoAccentColoredDialog() {
        new PrimeDialog(this)
                .setAccentColorRes(R.color.colorGreen)
                .setCancelable(false).setDialogWidth(92)
                .setIcon(R.drawable.ic_info).setIconSize(36)
                .setTitle("HEY THERE!")
                .setMessage("Accent color applies on icon, title, and action buttons by calling 'setAccentColor()'.\n\nIf accent color is not set, PrimeDialog will default to the 'colorAccent' value in your app theme.\n\nIf 'colorAccent' is undefined in app theme, PrimeDialog will use color WHITE or BLACK depending on your UI mode.")
                .setMessageTypefaceRes(R.font.maitree_medium)
                .setPositiveButton("DISMISS")
                .show();
    }

    /** By default, icon, title and action buttons inherit their colors from the dialog
     * accent color.
     *
     * However, you can explicitly specify separate colors for these elements as demonstrated
     * in this example.
     *
     * By default, the dialog automatically adapt itself in night or light UI modes.
     * But you can define your preferred background colors for night and light UI modes as
     * shown also in this example. */
    void demoMultiColoredDialog() {
        new PrimeDialog(this)
                .setAccentColorRes(R.color.colorCyan).setDialogWidth(92)
                .setIcon(R.drawable.ic_info).setIconSize(37).setIconTintRes(R.color.colorGreen)
                .setTitle("HEY THERE!").setTitleColorRes(R.color.colorOrange)
                .setMessage("Icon, Title and Action Button texts inherits any color you specify with 'setAccentColor()'.\n\nHowever, you can explicitly set individual color for each of these elements as seen here\n\nLikewise you can customise your dialog background color between dark and light UI modes.")
                .setMessageTypefaceRes(R.font.maitree_medium)
                .setNeutralButton("GREAT")
                .setNegativeButton("DISMISS")
                .setPositiveButton("GOT IT")
                .setNightBackgroundColor(Color.DKGRAY)
                .setActionTextColor(PrimeDialog.NEUTRAL_BUTTON, Color.RED)
                .setActionTextColor(PrimeDialog.NEGATIVE_BUTTON, Color.MAGENTA)
                .setActionTextColorRes(PrimeDialog.POSITIVE_BUTTON, R.color.colorOrange)
                .show();
    }

    /** You could set a precise width and height of the dialog on a screen size
     * percentage basis by calling
     * 'setDialogWidth(percentageWidth)'
     * 'setDialogHeight(percentageHeight)'
     *
     * You can also set a maximum attainable height by calling
     * 'setMaxHeight(percentageHeight)'. This will cause dialog to dynamically increase
     * its height relative to the displayed content, but halt height increase as soon
     * as the 'max height' value is reached.
     *
     * NOTE: 'setDialogHeight(percentageHeight)' will take priority if called alongside
     * 'setMaxHeight(percentageHeight)'.
     * */
    void demoDimensionDialog() {
        new PrimeDialog(this)
                .setDialogWidth(100)
                .setMaxHeight(20)
                .setDialogHeight(100)

                .removeRoundedCorners()
                .setTitle("HEY THERE!")
                .setMessage("Hello there,\nDo you know you can set dialog exact percentage width and height relative to your device screen?\n\nBy the way, you could also remove the rounded corners if you prefer a rectangular dialog by calling 'removeRoundedCorners()'.")
                .setMessageTypefaceRes(R.font.maitree_medium)
                .setPositiveButton("DISMISS")
                .show();
    }

    /** A header-supported dialog with adjustable header height and overlay. An image
     * resource or a solid color could be used has the header background.
     *
     * PrimeDialog also provides a "Don't show again" mechanism as demonstrated in
     * this sample. This functionality is NOT restricted to header dialogs alone.
     * */
    void demoHeaderDialog() {
        new PrimeDialog(this)
                .setTitle("HEY THERE!").setCancelable(false)
                .setHeaderBackgroundRes(R.drawable.header)
                .setHeaderOverlayTintDepth("65")
                .setHeaderHeight(86).setIcon(R.drawable.ic_info)
                .setMessage("Hi there,\nI'm PrimeDialog with Header support. An image resource, bitmap or a solid color can be used as header background.")
                .setMessageLineSpacing(4F)
                .setPositiveButton("DISMISS")
                .show();
    }

    /** This dialog gets an ENTER and an EXIT animation from a style resources passed
     * to the PrimeDialog constructor.
     *
     * Header background image ken burns animation can be enabled or disabled by passing
     * 'true' or 'false' respectively to the second parameter of 'setHeaderBackgroundRes()'.
     * Default value is 'true' i.e. Animated. */
    void demoAnimatedDialog() {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.maitree_medium);
        new PrimeDialog(this)
                .setWindowAnimation(R.style.MyDialogAnimationStyle)
                .setHeaderBackgroundRes(R.drawable.header, true)
                .setHeaderOverlayTintDepth("45")
                .setIcon(R.mipmap.ic_launcher).setIconTintEnabled(false)
                .setTitle("HEY THERE!")
                .setMessage("Hi there,\nI'm animated dialog, and I got my custom ENTER and EXIT animation from a style resource.")
                .setMessageTypeface(typeface)
                .setNegativeButton("DISMISS")
                .show();
    }

    void demoListenerDialog() {
        new PrimeDialog(this)
                .setDialogWidth(95)
                .setHeaderBackgroundRes(R.drawable.header)
                .setHeaderOverlayTintDepth("40")
                .setIcon(R.drawable.ic_info).setTitle("HEY THERE!")
                .setMessage("Hi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.\n\nHi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.\nHi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.\nHi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.\nHi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.\nHi there,\nJust to let you know I have my ears to the ground for your clicks and interactions.\nBy the way, 'Don't remind me again' could be a handy feature for you.")
                .setMessageTypefaceRes(R.font.maitree_medium)
                .setNegativeButton("CLICK ME", (dialog, buttonId) -> {
                    // dialog.dismiss();
                    showMessage("You clicked me!");
                })
                .setPositiveButton("DISMISS")
                .setOnDialogShowListener(dialog ->
                        showMessage("Hello from onDialogShowListener!"))
                .setDontShowAgain("Don't remind me again", onDontShowAgainListener)
                .show();
    }

    /** Set a custom view from a 'Layout resource' or 'View' to use as
     * dialog content.
     * If needed, you could set percentage width and height based
     * on your need. Both are set to 100 here to achieve fullscreen display. */
    void demoCustomDialog(){
        new PrimeDialog(this)
                .setCustomView(R.layout.custom_dialog_layout)
                .setOnDialogShowListener(dialog -> {
                    showLoginGuide();

                    Button loginButton = dialog.findViewById(R.id.btnLogin);
                    loginButton.setOnClickListener(view -> {
                        loginButton.setText("HEY, I'M A DUMMY BUTTON");
                        showMessage("Hello from dummy login button");
                    });
                })

                .setDialogWidth(100) // Optional
                .setDialogHeight(100, true) // Optional
                .setScreenDimLevel(0.0f) // Optional
                .setWindowAnimationEnabled(false) // Optional
                .removeRoundedCorners() // Optional

                .show();
    }

    /** Set a custom view from a 'Layout resource' or 'View' to use as
     * dialog content.
     * If needed, you could set percentage width and height based
     * on your need. Both are set to 100 here to achieve fullscreen display. */
    void demoCustomDialog2(){
        new PrimeDialog(this)
                .setCustomView(R.layout.custom_dialog_layout)
                .setOnDialogShowListener(dialog -> {
                    showLoginGuide();

                    Button loginButton = dialog.findViewById(R.id.btnLogin);
                    loginButton.setOnClickListener(view -> {
                        loginButton.setText("HEY, I'M A DUMMY BUTTON");
                        showMessage("Click 'CREATE ACCOUNT'");
                    });
                })

                .setAccentColorRes(R.color.colorOrange)  // Optional
                .setDontShowAgain(onDontShowAgainListener) // Optional
                .setBackgroundColor(Color.LTGRAY) // Optional
                .setActionLayoutBackgroundColor(Color.DKGRAY) // Optional
                .setNeutralButton("CREATE ACCOUNT", (dialog, buttonId) -> // Optional
                        showMessage("I'm a PrimeDialog action button!"))
                .setPositiveButton("DISMISS") // Optional

                .show();
    }

    private void showLoginGuide() {
        showMessage("Please click the login button");
    }

    private final PrimeDialog.OnDontShowAgainListener onDontShowAgainListener = new PrimeDialog.OnDontShowAgainListener() {
        @Override
        public void onBoxCheck(boolean isChecked) {
            showMessage(isChecked ? "Checked" : "Not checked");
        }

        @Override
        public void onDismiss() {
            // This method will ONLY get called IF the 'Don't show again'
            // checkbox is checked.

            // Write your "Don't remind me again" logic here. This could be
            // storing a Shared preference value or some other means.
            // You check for this value next time you are to show the dialog
            showMessage("Okay, I won't remind you again");
        }
    };

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            MainActivity.this.finish();
        }
    };
}