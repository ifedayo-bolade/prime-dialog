package ifedayo.bolade.primedialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.core.widget.ImageViewCompat
import kotlin.properties.Delegates
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import ifedayo.bolade.primedialog.databinding.PrimeDialogLayoutBinding
import kotlin.apply
import kotlin.let
import kotlin.ranges.until
import kotlin.run
import kotlin.text.substring

/**
 * PrimeDialog v1.0.0
 * Created by Ifedayo Bolade on May 14, 2026.
 */

class PrimeDialog

@JvmOverloads
constructor(
    private var context: Context,
    styleRes: Int = R.style.DefaultDialogTheme
) {
    private var buttonActionId = CLICK_OUTSIDE
    private var dismissActionId: Int? = null
    private var cornerRadius = 16
    private var dialogPadding = 0
    private var dayColor = "#FFFFFF".toColorInt()
    private var nightColor = "#131316".toColorInt()
    private var isCustomView = false
    private var dontShowAgainSet = false
    private var isActionButtonSet = false
    private val dialog: Dialog
    private var headerLayout: RelativeLayout
    private var overlay: RelativeLayout
    private var frameLayout: FrameLayout
    private var customView: ViewGroup? = null
    private var titleIcon: AppCompatImageView? = null
    private var title: AppCompatTextView? = null
    private var message: AppCompatTextView
    private var binding: PrimeDialogLayoutBinding
    private var positiveButtonLayout: LinearLayout? = null
    private var negativeButtonLayout: LinearLayout? = null
    private var neutralButtonLayout: LinearLayout? = null
    private var positiveButtonTextView: AppCompatTextView? = null
    private var negativeButtonTextView: AppCompatTextView? = null
    private var neutralButtonTextView: AppCompatTextView? = null
    private var windowAnimationStyleRes = R.style.WindowAnimationStyle

    private var iconAttributes: IconAttributes = IconAttributes()
    private var titleAttributes: TitleAttributes = TitleAttributes()
    private var messageAttributes: MessageAttributes = MessageAttributes()

    private val TAG = "PrimeDialog"

    private data class IconAttributes(
        var size: Int = 38,
        var tint: Int? = null,
        var iconRes: Int? = null,
        var iconWidth: Int? = null,
        var iconHeight: Int? = null,
        var bitmap: Bitmap? = null,
        var isIconSet: Boolean = false,
        var animation: Animation? = null,
        var overrideHeaderTint: Boolean? = null
    )

    private data class TitleAttributes(
        var text: CharSequence? = null,
        var color: Int? = null,
        var size: Float = 21F,
        var typeface: Typeface = TYPEFACE_SANS_SERIF_MEDIUM_BOLD,
        var typefaceStyle: Int = Typeface.NORMAL,
        var isTitleSet: Boolean = false,
        var letterSpacing: Float? = null,
        var animation: Animation? = null,
        var overrideHeaderColor: Boolean? = null
    )

    private data class MessageAttributes(
        var isMarginSet: Boolean = false
    )

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = PrimeDialogLayoutBinding.inflate(layoutInflater)
        dialog = Dialog(context, styleRes)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        setWindowAnimationEnabled(true)

        // Initialize views
        headerLayout = binding.header
        overlay = binding.overlay
        frameLayout = binding.frameLayout
        message = binding.dialogMessage
    }

    private var isTransparencySet = false

    /** Makes this dialog transparent.  */
    fun setTransparency(): PrimeDialog {
        isTransparencySet = true
        return this
    }

    @JvmOverloads
    /**
     * Sets the view to be displayed as the dialog's content.
     * @param view The view to be displayed. Especially programmatically created views.
     * @param layoutParams The layout parameters to be applied to the view.
     */
    fun setCustomView(view: View, layoutParams: FrameLayout.LayoutParams? = null): PrimeDialog {
        isCustomView = true
        frameLayout.addView(view)
        frameLayout.isVisible = true
        layoutParams?.let { view.layoutParams = it }
        customView = frameLayout
        return this
    }

    /**
     * Sets the layout resource to be display as the dialog's content.
     * @param layoutRes The id of the drawable resource to be used as header icon.
     */
    fun setCustomView(@LayoutRes layoutRes: Int): PrimeDialog {
        isCustomView = true
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customView = layoutInflater.inflate(layoutRes, null) as ViewGroup
        frameLayout.addView(customView)
        frameLayout.isVisible = true
        return this
    }

    /**
     * Sets a custom layout to be display in this dialog.
     * @param layoutRes The id of the drawable resource to be used as header icon.
     * @param titleTextViewIdRes Specify the custom view's title text view id. This allows
     * the internal modification of the Title textView like [setTitleColor],[setTitleTypeface] etc.
     * @param messageTextViewIdRes Specify the custom view's message text view id. This allows
     * the internal modification of the Title textView like [setMessageMargin],[setMessageColor] etc.
     */
    fun setCustomView(
        @LayoutRes layoutRes: Int,
        @IdRes titleTextViewIdRes: Int,
        @IdRes messageTextViewIdRes: Int
    ): PrimeDialog {
        isCustomView = true
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutView = layoutInflater.inflate(layoutRes, null)
        frameLayout.addView(layoutView)
        frameLayout.isVisible = true
        if (titleTextViewIdRes != 0) {
            title = frameLayout.findViewById(titleTextViewIdRes)
        }
        if (messageTextViewIdRes != 0) {
            message = frameLayout.findViewById(messageTextViewIdRes)
        }
        return this
    }

    fun getCustomView(): View? {
        return customView
    }

    /** Sets an ENTER and/or EXIT animation for the dialog window.
     * @param styleRes The style resource id containing the animation attributes.
     * @see [setWindowAnimationEnabled]*/
    fun setWindowAnimation(@StyleRes styleRes: Int): PrimeDialog {
        this.windowAnimationStyleRes = styleRes
        dialog.window?.attributes?.windowAnimations = styleRes
        return this
    }

    /** Sets whether the default dialog window ENTER and/or EXIT animations should
     * be enabled or not.
     * @param isEnabled Animation is enabled when 'true' and disabled with 'false'.
     * @author A call to [setWindowAnimation] will automatically override the
     * dialog's default animation, and set the new animation style in motion.
     *
     * @see [setWindowAnimation]*/
    fun setWindowAnimationEnabled(isEnabled: Boolean): PrimeDialog {
        return setWindowAnimation(if(isEnabled) windowAnimationStyleRes else 0)
    }

    /** Indicates that a header background or color has been set. */
    private var isHeaderShown: Boolean = false

    /** Indicates that a header related functions has been called. */
    private var isHeaderConfiguring = false

    @JvmOverloads
    /** Sets a drawable resources as the dialog header.
     * @param drawableRes The drawable resource id.
     * @param isAnimated Whether to apply a ken burns effect on the header drawable or not. Default value is true
     * */
    fun setHeaderBackgroundRes(
        @DrawableRes drawableRes: Int,
        isAnimated: Boolean = true
    ): PrimeDialog {
        isHeaderShown = true
        headerLayout.isVisible = true
        if(isAnimated){
            binding.kenBurnsView.apply {
                setImageResource(drawableRes)
                isVisible = true
            }
        } else {
            binding.imageView1.apply {
                setImageResource(drawableRes)
                isVisible = true
            }
        }
        return this
    }

    /** Sets the background color for the header. [setHeaderBackgroundRes] is given a higher
     * priority if called alongside.
     * @param colorRes The color resource id.
     * @see [setHeaderBackgroundRes]*/
    fun setHeaderBackgroundColor(@ColorRes colorRes: Int): PrimeDialog {
        isHeaderShown = true
        headerLayout.apply {
            isVisible = true
            setBackgroundColor(getColor(colorRes))
        }
        binding.kenBurnsView.isVisible = false
        binding.imageView1.isVisible = false
        return this
    }

    /**
     * Set the height of the dialog header, default value is 85dp
     *
     * @param heightDp The desired height in dp
     */
    fun setHeaderHeight(heightDp: Int): PrimeDialog {
        isHeaderConfiguring = true
        val layoutParams = headerLayout.layoutParams as RelativeLayout.LayoutParams
        layoutParams.height = toDP(heightDp)
        return this
    }

    private var isDimLevelSet = false
    /** Set background screen dim level relative to the dialog. Default value is 0.6F
     * @param dimLevel The dim level ranging from 0.0 (no dim) to 1.0 (fully dark)
     * */
    fun setScreenDimLevel(dimLevel: Float = 0.6F): PrimeDialog {
        isDimLevelSet = true
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setDimAmount(dimLevel) // 0.0 = no dim, 1.0 = fully dark
        return this
    }

    /**
     * Sets the drawable resource to be used as dialog icon.
     * @param drawableRes The id of the drawable resource to be used as icon.
     */
    fun setIcon(@DrawableRes drawableRes: Int): PrimeDialog {
        iconAttributes.iconRes = drawableRes
        iconAttributes.isIconSet = true
        return this
    }

    fun setIcon(bitmap: Bitmap?): PrimeDialog {
        bitmap?.let {
            iconAttributes.bitmap = it
            iconAttributes.isIconSet = true
        } ?: run {
            val message = "PrimeDialog icon bitmap is null"
            Log.i(TAG, message)
            showDebugToast(message)
        }
        return this
    }

    fun setIconTintRes(@ColorRes colorRes: Int): PrimeDialog {
        return setIconTint(getColor(colorRes))
    }

    fun setIconTint(@ColorInt color: Int): PrimeDialog {
        iconAttributes.tint = getColor(color)
        iconAttributes.overrideHeaderTint = true
        return this
    }

    private fun applyIconTint(@ColorInt color: Int) {
        titleIcon =
            findViewById<AppCompatImageView>(if (isHeaderShown) R.id.imageView else R.id.imageView2)
        ImageViewCompat.setImageTintList(titleIcon!!, ColorStateList.valueOf(color))
    }

    private var isIconTintEnabled = true

    /**
     * Define whether the dialog icon should inherit the dialog accent color. Default value is true.
     * @param enabled Whether the icon should be tinted with the accent color or not.
     */
    fun setIconTintEnabled(enabled: Boolean): PrimeDialog {
        isIconTintEnabled = enabled
        return this
    }

    /**
     * Sets dialog icon size. Default value is 38.
     * @param sizeDp Icon size in dp.
     */
    fun setIconSize(sizeDp: Int): PrimeDialog {
        return setIconSize(sizeDp, sizeDp)
    }

    /**
     * Sets dialog icon size. Default value is 38.
     * @param widthDp Icon width in dp.
     * @param heightDp Icon height in dp.
     */
    fun setIconSize(widthDp: Int, heightDp: Int): PrimeDialog {
        iconAttributes.iconWidth = widthDp
        iconAttributes.iconHeight = heightDp
        return this
    }

    private fun getAnimation(@AnimRes animRes: Int, duration: Long): Animation {
        val anim = AnimationUtils.loadAnimation(context, animRes)
        anim.reset()
        if (duration != DEFAULT_ANIMATION_DURATION) {
            anim.duration = duration
        }
        return anim
    }

    @JvmOverloads
    fun setIconAnimation(
        animRes: Int,
        duration: Long = DEFAULT_ANIMATION_DURATION,
        interpolator: Interpolator? = null
    ): PrimeDialog {
        val anim = getAnimation(animRes, duration)
        interpolator?.let { anim.interpolator = it }
        iconAttributes.animation = anim
        return this
    }

    @JvmOverloads
    fun setTitleAnimation(
        @AnimRes animRes: Int,
        duration: Long = DEFAULT_ANIMATION_DURATION,
        interpolator: Interpolator? = null
    ): PrimeDialog {
        val anim = getAnimation(animRes, duration)
        interpolator?.let { anim.interpolator = it }
        titleAttributes.animation = anim
        return this
    }

    private fun setHeaderOverlayEnabled() {
        isHeaderConfiguring = true
        overlay.isVisible = true
    }

    /** Sets the header overlay transparency.
     * @param alphaValue The opacity value ranging from 00 (Fully transparent) to FF (Fully opaque)
     */
    fun setHeaderOverlayTintDepth(alphaValue: String): PrimeDialog {
        if (alphaValue.length > 2) {
            val message = "'setHeaderOverlayTintDepth' - Alpha value should be two characters long"
            Log.i(TAG, message)
            showDebugToast(message)
            return this
        }
        setHeaderOverlayEnabled()
        val color = "#" + alphaValue + "000000"
        overlay.setBackgroundColor(color.toColorInt())
        return this
    }

    private fun setHeaderOverlayTint(@ColorInt color: Int): PrimeDialog {
        setHeaderOverlayEnabled()
        overlay.setBackgroundColor(color)
        return this
    }

    fun setHeaderOverlayTintRes(@ColorRes colorRes: Int): PrimeDialog {
        return setHeaderOverlayTint(getColor(colorRes))
    }

    fun setHeaderOverlayTint(colorCode: String): PrimeDialog {
        return setHeaderOverlayTint(colorCode.toColorInt())
    }

    private fun initializeTitle(isVisible: Boolean = true): AppCompatTextView {
        if(isHeaderShown){
            title = binding.dialogTitle
        } else {
            title = binding.dialogTitle2.apply {
                this.isVisible = isVisible
                binding.titleLayout.let {
                    it.isVisible = isVisible
                    val layoutParams = it.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.setMargins(toDP(17), toDP(12), toDP(17), toDP(2))
                }
                // Adjust message scrollview depending on whether title is shown or not.
                binding.scrollView.setPadding(
                    0,toDP(if(isVisible) 0 else 8),0, 0
                )
            }
        }
        titleAttributes.typeface = TYPEFACE_SANS_SERIF_MEDIUM_BOLD
        return title as AppCompatTextView
    }

    fun setTitle(@StringRes stringRes: Int): PrimeDialog {
        return setTitle(context.getString(stringRes))
    }

    fun setTitle(charSequence: CharSequence?): PrimeDialog {
        titleAttributes.text = charSequence
        titleAttributes.isTitleSet = true
        return this
    }

    @JvmOverloads
    fun setTitleTypefaceRes(@FontRes fontRes: Int, style: Int = Typeface.NORMAL): PrimeDialog {
        try {
            val typeface = ResourcesCompat.getFont(context, fontRes) ?: Typeface.DEFAULT
            return setTitleTypeface(typeface, style)
        } catch (e: Exception) {
            val message = "Font resource not found: $fontRes"
            Log.i(TAG, message)
            showDebugToast(message)
        }
        return this
    }

    @JvmOverloads
    fun setTitleTypeface(typeface: Typeface, style: Int = Typeface.NORMAL): PrimeDialog {
        titleAttributes.typeface = typeface
        titleAttributes.typefaceStyle = style
        return this
    }

    fun setTitleColor(@ColorInt color: Int): PrimeDialog {
        titleAttributes.color = getColor(color)
        titleAttributes.overrideHeaderColor = true
        return this
    }

    fun setTitleColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setTitleColor(getColor(colorRes))
    }

    fun setTitleColor(colorCode: String): PrimeDialog {
        return setTitleColor(colorCode.toColorInt())
    }

    /** Sets title textview size. Default value is 21sp
     * @param sizeSp The desired size value in sp.*/
    fun setTitleSize(sizeSp: Float): PrimeDialog {
        titleAttributes.size = sizeSp
        return this
    }

    /** Sets the spacing between the title character.
     * @author Value range is 0.0 to 0.1. Any value above this range will default to 0.1
     * */
    fun setTitleLetterSpacing(@FloatRange(from = 0.0, to = 0.1) spacing: Float): PrimeDialog {
        var value = spacing
        if(value > 0.1F) value = 0.1F
        titleAttributes.letterSpacing = value
        return this
    }

    fun setMessage(@StringRes stringRes: Int): PrimeDialog {
        return setMessage(context.getString(stringRes))
    }

    fun setMessage(charSequence: CharSequence): PrimeDialog {
        message.text = charSequence
        frameLayout.isVisible = charSequence.isNotEmpty()
        return this
    }

    /** Sets message textview size. Default value is 18sp
     * @param sizeSp The desired size value in sp.*/
    fun setMessageSize(sizeSp: Float): PrimeDialog {
        message.textSize = sizeSp
        return this
    }

    private var isMessageColorSet = false
    fun setMessageColor(@ColorInt color: Int): PrimeDialog {
        if(!isMessageColorSet){
            isMessageColorSet = true
            message.setTextColor(getColor(color))
        }
        return this
    }

    fun setMessageColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setMessageColor(getColor(colorRes))
    }

    fun setMessageColor(colorRGB: String): PrimeDialog {
        return setMessageColor(colorRGB.toColorInt())
    }

    fun setMessageTypeface(typeface: Typeface?): PrimeDialog {
        message.typeface = typeface
        return this
    }

    fun setMessageTypefaceRes(@FontRes fontRes: Int): PrimeDialog {
        val typeface = ResourcesCompat.getFont(context, fontRes)
        return setMessageTypeface(typeface)
    }

    @JvmOverloads
    /**Sets line spacing with a multiplier value of 1.0F.
     * @param add Addition value
     * @param multiplier Multiplier value. Default is 1.0F
     */
    fun setMessageLineSpacing(add: Float, multiplier: Float = 1.0F): PrimeDialog {
        message.setLineSpacing(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                add,
                context.resources.displayMetrics
            ), multiplier
        )
        return this
    }

    /** The dialog message, or the FrameLayout (for a custom view dialog) on which
     * height or width modification is applied. */
    private val dialogView: View?
        get() {
            // We can't use the rootLayout of the Dialog layout file because
            // it causes the action button layout to be cut off.
            return if(isCustomView){
                findViewById(R.id.frameLayout) // FrameLayout for custom views
            } else {
                dialog.findViewById(R.id.scrollView) // Scrollview holding message text only
            }
        }

    /** The approximate percentage portion occupied by [dialogView] relative to the
     * overall dialog width. */
    private val DEFAULT_WIDTH_FRACTION: Float
        get() {
            val screenOrientation = context.resources.configuration.orientation
            return if(screenOrientation == Configuration.ORIENTATION_PORTRAIT)
                100F  // 100% of dialog overall width
            else 113F
        }

    /** The approximate percentage portion occupied by [dialogView] relative to the
     * overall dialog height. (Excluding height taken by 'Title', 'Don't show again'
     * and 'Action buttons')*/
    private val DEFAULT_HEIGHT_FRACTION: Float
        get() {
            val screenOrientation = context.resources.configuration.orientation
            return if(screenOrientation == Configuration.ORIENTATION_PORTRAIT)
                0.7463F  // Approx. 75% of dialog overall height
            else 0.56F
        }

    private var isDialogWidthSet = false
    /**Sets the percentage width of the dialog. If needed, further adjustments to the dialog
     * width can be made using the 'dialogPaddingDp' parameter in [setRoundedCorners]
     * @param percentageWidth The width (in percentage) relative to the device screen width.
     * Default value is 89.
     * @see setDialogHeight
     * @see setDimension */
    fun setDialogWidth(percentageWidth: Int = 89): PrimeDialog {
        isDialogWidthSet = true
        var finalPercentageWidth = percentageWidth
        if(finalPercentageWidth > 100) finalPercentageWidth = 100
        val factor = (finalPercentageWidth / DEFAULT_WIDTH_FRACTION)
        val targetWidth = (screenWidth * factor).toInt()
        val currentHeight = dialog.window?.attributes?.height
            ?: ViewGroup.LayoutParams.WRAP_CONTENT // Fallback if not previously set
        dialog.window?.setLayout(targetWidth, currentHeight)
        setRoundedCorners(dialogPaddingDp = 0)
        return this
    }

    private var isDialogHeightDefined = false
    /** Sets the maximum height of the dialog. This function will ALWAYS override [setMaxHeight]
     * If no height is set by this function, a default value is set to prevent dialog from
     * exceeding the device screen height. @see [DEFAULT_HEIGHT_FRACTION]
     * @param percentageHeight The maximum height in percentage of the screen.
     * @param disregardFraction Whether to apply controlled height adjustment or not. Default
     * value is 'false' to prevent action buttons conflicting with the device navigation bar.
     * Typically, it should only be set to 'true' when the dialog is a custom
     * view, and it's expected to take the entire screen volume (like a regular activity class).
     * You may need to handle inset if you are using this function.
     *
     * IMPORTANT NOTE: This height explicitly applies to the ScrollView parent of the dialog message.
     * The height of 'Title', 'Don't show again' and 'Action buttons' are not taken into
     * account.
     * @author
     * @see setDialogWidth
     * @see setDimension*/
    @JvmOverloads
    fun setDialogHeight(percentageHeight: Int, disregardFraction: Boolean = false): PrimeDialog {
        dialogView?.apply {
            var finalPercentageHeight = percentageHeight
            if(finalPercentageHeight > 100) finalPercentageHeight = 100
            val v = if(disregardFraction) 1f else DEFAULT_HEIGHT_FRACTION
            val factor = (finalPercentageHeight / 100f) * v
            val targetHeight = (screenHeight * factor).toInt()
            layoutParams.height = targetHeight
            requestLayout()
            isDialogHeightDefined = true
        }
        return this
    }

    private var isMaxHeightSet = false
    private var maxHeightPercent = 100
    /**Sets the maximum height of the dialog. NOTE that this function will ALWAYS be
     * silenced/overridden everytime [setDialogHeight] is called alongside. */
    fun setMaxHeight(percentageHeight: Int): PrimeDialog {
        isMaxHeightSet = true
        maxHeightPercent = percentageHeight
        return this
    }

    /** Sets the dialog's width and height (in percentage) relative to the device
     * screen. This function is equivalent to calling both [setDialogWidth] and [setDialogHeight].*/
    fun setDimension(percentageWidth: Int, percentageHeight: Int): PrimeDialog {
        setDialogWidth(percentageWidth)
        setDialogHeight(percentageHeight)
        return this
    }

    private val screenWidth: Int
        get() {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val windowMetrics = windowManager.currentWindowMetrics
                windowMetrics.bounds.width()  // In pixels
            } else {
                val displayMetrics = Resources.getSystem().displayMetrics
                displayMetrics.widthPixels  // In pixels
            }
        }

    private val screenHeight: Int
        get() {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val windowMetrics = windowManager.currentWindowMetrics
                windowMetrics.bounds.height()  // In pixels
            } else {
                val displayMetrics = Resources.getSystem().displayMetrics
                displayMetrics.heightPixels  // In pixels
            }
        }

    /**
     * Apply specified margins to left, top, right and bottom of message.
     * @param marginDp The margin in dp.
     */
    fun setMessageMargin(marginDp: Int): PrimeDialog {
        return setMessageMargin(marginDp, marginDp, marginDp, marginDp)
    }

    /**
     * Apply specific margins (in dp) to left, top, right and bottom of message.
     * @see [setMessageMargin]
     */
    fun setMessageMargin(leftDp: Int = 16, topDp: Int = 0, rightDp: Int = 16, bottomDp: Int = 0): PrimeDialog {
        if (isCustomView) {
            val linearLayout = frameLayout.getChildAt(0) as? LinearLayout
            val layoutParams = linearLayout?.layoutParams as? RelativeLayout.LayoutParams
            layoutParams?.setMargins(toDP(leftDp), toDP(topDp), toDP(rightDp), toDP(bottomDp))
        } else {
            val layoutParams = message.layoutParams as? RelativeLayout.LayoutParams
            layoutParams?.setMargins(toDP(leftDp), toDP(topDp), toDP(rightDp), toDP(bottomDp))
        }
        messageAttributes.isMarginSet = true
        return this
    }

    /** Set whether dialog can be dismissed by user pressing 'BACK' button or tapping
     * outside the dialog window. If set to 'false', the dialog can only be closed
     * programmatically or by tapping the action buttons.
     * @param isCancelable Allow dialog dismiss if 'true'.*/
    fun setCancelable(isCancelable: Boolean): PrimeDialog {
        dialog.setCancelable(isCancelable)
        return this
    }

    /** Sets whether this dialog is canceled when touched outside the window's bounds.
     * If setting to true, the dialog is set to be cancelable if not already set.
     * @param isCancelable Whether the dialog should be canceled when touched outside the window.
     * */
    fun setCanceledOnTouchOutside(isCancelable: Boolean): PrimeDialog {
        dialog.setCanceledOnTouchOutside(isCancelable)
        return this
    }

    fun setMessageLayoutBackground(@ColorInt colorInt: Int): PrimeDialog {
        binding.messageLayout.setBackgroundColor(getColor(colorInt))
        return this
    }

    fun setMessageLayoutBackgroundRes(@ColorRes colorRes: Int): PrimeDialog {
        return setMessageLayoutBackground(getColor(colorRes))
    }

    /** Set the background color for the action layouts including
     * both the 'Don't show again' layout and action buttons layout.
     * @see setDontShowAgainLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setActionLayoutBackgroundColor(colorCode: String): PrimeDialog {
        return setActionLayoutBackgroundColor(colorCode.toColorInt())
    }

    /** Set the background color for the action layouts including
     * both the 'Don't show again' layout and action buttons layout.
     * @see setDontShowAgainLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setActionLayoutBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setActionLayoutBackgroundColor(getColor(colorRes))
    }

    private var isActionLayoutBackgroundColorSet = false
    /** Set the background color for the action layouts including
     * both the 'Don't show again' layout and action buttons layout.
     * @see setDontShowAgainLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setActionLayoutBackgroundColor(@ColorInt color: Int): PrimeDialog {
        setInternalActionLayoutBackgroundColor(color)
        isActionLayoutBackgroundColorSet = true
        return this
    }

    private fun setInternalActionLayoutBackgroundColor(@ColorInt color: Int): PrimeDialog {
        if (!isActionLayoutBackgroundColorSet) {
            binding.actionLayout.setBackgroundColor(getColor(color))
        }
        return this
    }

    /** Set the background color for the action buttons layout.
     * @see setActionLayoutBackgroundColor
     * @see setDontShowAgainLayoutBackgroundColor */
    fun setActionButtonLayoutBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setActionButtonLayoutBackgroundColor(getColor(colorRes))
    }

    /** Set the background color for the action buttons layout.
     * @see setActionLayoutBackgroundColor
     * @see setDontShowAgainLayoutBackgroundColor */
    fun setActionButtonLayoutBackgroundColor(colorHex: String): PrimeDialog {
        return setActionButtonLayoutBackgroundColor(colorHex.toColorInt())
    }

    /** Set the background color for the action buttons layout.
     * @see setActionLayoutBackgroundColor
     * @see setDontShowAgainLayoutBackgroundColor */
    fun setActionButtonLayoutBackgroundColor(@ColorInt color: Int): PrimeDialog {
        binding.buttonLayoutParent.setBackgroundColor(getColor(color))
        return this
    }

    /** Set the background color for the 'Don't show again' layout.
     * @see setActionLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setDontShowAgainLayoutBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setDontShowAgainLayoutBackgroundColor(getColor(colorRes))
    }

    /** Set the background color for the 'Don't show again' layout.
     * @see setActionLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setDontShowAgainLayoutBackgroundColor(colorHex: String): PrimeDialog {
        return setDontShowAgainLayoutBackgroundColor(colorHex.toColorInt())
    }

    /** Set the background color for the 'Don't show again' layout.
     * @see setActionLayoutBackgroundColor
     * @see setActionButtonLayoutBackgroundColor */
    fun setDontShowAgainLayoutBackgroundColor(@ColorInt color: Int): PrimeDialog {
        binding.dontShowAgainLayout.setBackgroundColor(getColor(color))
        return this
    }

    private val defaultOnClickListener =
        OnDialogButtonClickListener { dialog, _ ->
            dismissActionId = DISMISS_ACTION_INTERNAL
            dialog.dismiss()
        }

    @JvmOverloads
    fun setPositiveButton(
        @StringRes labelRes: Int,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        return setPositiveButton(context.getString(labelRes), onClickListener)
    }

    @JvmOverloads
    fun setPositiveButton(
        label: String,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        setActionButtonLayoutVisibility()
        POSITIVE_BUTTON =
            if (isCustomView) R.id.dialog_btn_positive_layout else R.id.dialog_btn_positive_layout
        positiveButtonLayout = findViewById<LinearLayout>(POSITIVE_BUTTON).apply {
            isVisible = true
            positiveButtonTextView = (getChildAt(0) as? AppCompatTextView)?.apply {
                text = label
                setTypeface(Typeface.DEFAULT_BOLD)
            }
            setOnClickListener {
                onClickListener.onDialogButtonClick(this@PrimeDialog, POSITIVE_BUTTON)
                buttonActionId = POSITIVE_BUTTON
                dismissActionId = DISMISS_ACTION_POSITIVE_BUTTON
            }
        }
        return this
    }

    @JvmOverloads
    fun setNegativeButton(
        @StringRes label: Int,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        return setNegativeButton(context.getString(label), onClickListener)
    }

    @JvmOverloads
    fun setNegativeButton(
        label: String,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        setActionButtonLayoutVisibility()
        NEGATIVE_BUTTON =
            if (isCustomView) R.id.dialog_btn_negative_layout else R.id.dialog_btn_negative_layout
        negativeButtonLayout = findViewById<LinearLayout>(NEGATIVE_BUTTON).apply {
            isVisible = true
            negativeButtonTextView = (getChildAt(0) as? AppCompatTextView)?.apply {
                text = label
                setTypeface(Typeface.DEFAULT_BOLD)
            }
            setOnClickListener {
                onClickListener.onDialogButtonClick(this@PrimeDialog, NEGATIVE_BUTTON)
                buttonActionId = NEGATIVE_BUTTON
                dismissActionId = DISMISS_ACTION_NEGATIVE_BUTTON
            }
        }
        return this
    }

    @JvmOverloads
    fun setNeutralButton(
        @StringRes labelRes: Int,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        return setNeutralButton(context.getString(labelRes), onClickListener)
    }

    @JvmOverloads
    fun setNeutralButton(
        label: String,
        onClickListener: OnDialogButtonClickListener = defaultOnClickListener
    ): PrimeDialog {
        setActionButtonLayoutVisibility()
        NEUTRAL_BUTTON =
            if (isCustomView) R.id.dialog_btn_neutral_layout else R.id.dialog_btn_neutral_layout
        neutralButtonLayout = findViewById<LinearLayout>(NEUTRAL_BUTTON).apply {
            isVisible = true
            neutralButtonTextView = (getChildAt(0) as? AppCompatTextView)?.apply {
                text = label
                setTypeface(Typeface.DEFAULT_BOLD)
            }
            setOnClickListener {
                onClickListener.onDialogButtonClick(this@PrimeDialog, NEUTRAL_BUTTON)
                buttonActionId = NEUTRAL_BUTTON
                dismissActionId = DISMISS_ACTION_NEUTRAL_BUTTON
            }
        }
        return this
    }

    private var actionTextColorMap = hashMapOf<Int, Int>()
    private var isActionTextColorSet = false

    @JvmOverloads
    /**
     * Defines the text color for the dialog action buttons, calling this method will overwrite any existing
     * modifications to the action buttons, including those from [setAccentColorRes].
     *
     * @param buttonId The id of the target button.
     * Should be one of [POSITIVE_BUTTON], [NEGATIVE_BUTTON] or [NEUTRAL_BUTTON].
     * If no id is provided, the specified color will be applied to all the button text.
     * @param colorRes The color to set on the dialog action TextViews.
     * @see PrimeDialog.setActionTextAppearance
     */
    fun setActionTextColorRes(buttonId: Int? = null, @ColorRes colorRes: Int): PrimeDialog {
        return setActionTextColor(buttonId, getColor(colorRes))
    }

    @JvmOverloads
    /**
     * Defines the text color for the dialog action buttons, calling this method will overwrite any existing
     * modifications to the action buttons, including those from [setAccentColorRes].
     *
     * @param buttonId The id of the target button.
     * Should be one of [POSITIVE_BUTTON], [NEGATIVE_BUTTON] or [NEUTRAL_BUTTON].
     * If no id is provided, the specified color will be applied to all the button text.
     * @param colorInt The color to set on the dialog action TextViews.
     * @see PrimeDialog.setActionTextAppearance
     */
    fun setActionTextColor(buttonId: Int? = null, @ColorInt colorInt: Int): PrimeDialog {
        buttonId?.let {
            actionTextColorMap[buttonId] = getColor(colorInt)
        } ?: run {
            actionTextColorMap[POSITIVE_BUTTON] = getColor(colorInt)
            actionTextColorMap[NEGATIVE_BUTTON] = getColor(colorInt)
            actionTextColorMap[NEUTRAL_BUTTON] = getColor(colorInt)
        }
        isActionTextColorSet = true
        return this
    }

    private var textAppearanceStyle = 0
    private var isActionTextAppearanceSet = false

    /**
     * Defines the text color for the dialog action buttons, calling this method will overwrite any existing
     * modifications to the action buttons, including those from [setAccentColorRes].
     *
     * @param styleRes The style resources to set on the dialog action buttons.
     * @see PrimeDialog.setActionTextColor
     */
    fun setActionTextAppearance(styleRes: Int): PrimeDialog {
        textAppearanceStyle = styleRes
        isActionTextAppearanceSet = true
        return this
    }

    private fun applyActionTextAppearance() {
        positiveButtonTextView?.apply {
            setTextAppearance(context, textAppearanceStyle)
            applyActionButtonBackground(this)
        }
        negativeButtonTextView?.apply {
            setTextAppearance(context, textAppearanceStyle)
            applyActionButtonBackground(this)
        }
        neutralButtonTextView?.apply {
            setTextAppearance(context, textAppearanceStyle)
            applyActionButtonBackground(this)
        }
    }

    private fun applyActionTextColor(@ColorInt textColor: Int) {
        applySingleActionTextColor(positiveButtonTextView, textColor)
        applySingleActionTextColor(negativeButtonTextView, textColor)
        applySingleActionTextColor(neutralButtonTextView, textColor)
    }

    private fun applySingleActionTextColor(textView: AppCompatTextView?, @ColorInt textColor: Int) {
        textView?.apply {
            setTextColor(textColor)
            applyActionButtonBackground(this)
        }
    }

    private fun applyActionTextColor(){
        val ids = mutableListOf(POSITIVE_BUTTON, NEGATIVE_BUTTON, NEUTRAL_BUTTON)
        actionTextColorMap.forEach { (buttonId, textColor) ->
            when(buttonId){
               POSITIVE_BUTTON -> {
                   applySingleActionTextColor(positiveButtonTextView, textColor)
                   ids.remove(POSITIVE_BUTTON)
               }
               NEGATIVE_BUTTON -> {
                   applySingleActionTextColor(negativeButtonTextView, textColor)
                   ids.remove(NEGATIVE_BUTTON)
               }
               NEUTRAL_BUTTON -> {
                   applySingleActionTextColor(neutralButtonTextView, textColor)
                   ids.remove(NEUTRAL_BUTTON)
               }
            }
        }

        for(id in ids){
            when(id){
                POSITIVE_BUTTON ->
                    applySingleActionTextColor(positiveButtonTextView, colorAccent)
                NEGATIVE_BUTTON ->
                    applySingleActionTextColor(negativeButtonTextView, colorAccent)
                NEUTRAL_BUTTON ->
                    applySingleActionTextColor(neutralButtonTextView, colorAccent)
            }
        }
    }

    private fun applyActionButtonBackground(textView: AppCompatTextView?) {
        if (textView != null) {
            val color = textView.currentTextColor
            val hexColor = Integer.toHexString(color).substring(2)
            val parent = textView.parent
            if (parent is LinearLayout) {
                parent.background = getActionLayoutDrawable(color)
                parent.backgroundTintList = getActionLayoutColorStateList(hexColor)
            }
        }
    }

    private fun getActionLayoutDrawable(color: Int): Drawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = toDP(5).toFloat()
        return gradientDrawable
    }

    private fun getActionLayoutColorStateList(colorString: String): ColorStateList {
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed))
        val colors = intArrayOf(getHexColor("#25$colorString"))
        return ColorStateList(states, colors)
    }

    fun setOnDialogShowListener(onDialogShowListener: OnDialogShowListener): PrimeDialog {
        dialog.setOnShowListener { onDialogShowListener.onDialogShow(this) }
        return this
    }

    private var isDialogCancelled: Boolean = false

    private var onDialogDismissListenerSet = false
    private var onDialogDismissListener: OnDialogDismissListener? = null
    fun setOnDialogDismissListener(onDialogDismissListener: OnDialogDismissListener): PrimeDialog {
        onDialogDismissListenerSet = true
        this.onDialogDismissListener = onDialogDismissListener
        dialog.setOnKeyListener { _, actionId, _ ->
            if(actionId == KeyEvent.KEYCODE_BACK) {
                dismissActionId = DISMISS_ACTION_BACK_PRESSED
            }
            false
        }
        dialog.setOnDismissListener {
            if(!isDialogCancelled){
                dispatchDismissEvent(dismissActionId, false)
            }
        }
        dialog.setOnCancelListener {
            isDialogCancelled = true
            if(dismissActionId == DISMISS_ACTION_POSITIVE_BUTTON ||
                dismissActionId == DISMISS_ACTION_NEGATIVE_BUTTON ||
                dismissActionId == DISMISS_ACTION_NEUTRAL_BUTTON)
                dismissActionId = DISMISS_ACTION_CLICK_OUTSIDE
            dispatchDismissEvent(dismissActionId, true)
        }
        return this
    }

    private fun dispatchDismissEvent(actionId: Int?, isCancelled: Boolean){
        onDialogDismissListener?.onDialogDismiss(this, actionId ?: DISMISS_ACTION_CLICK_OUTSIDE, isCancelled)
        onDontShowAgainListener?.let {
            if(binding.checkBox.isChecked && dontShowAgainSet) it.onDismiss()
        }
    }

    private var dontShowLabel: String = "Don't show again"

    @JvmOverloads
    /** A checkbox will be shown on this dialog with the provided 'Don't show again' label.
     * @param label The text to display with the CheckBox.
     * @param onDontShowAgainListener The listener to govern your 'Don't show again'
     * logic.
     */
    fun setDontShowAgain(label: String = dontShowLabel, onDontShowAgainListener: OnDontShowAgainListener?): PrimeDialog {
        dontShowLabel = label
        dontShowAgainSet = true
        this.onDontShowAgainListener = onDontShowAgainListener
        return this
    }

    private var colorAccent = getDefaultAccentColor()
    private var isAccentColorSet = false
    private var accentColorMode by Delegates.notNull<Int>()

    /**
     * Defines the color to apply on elements like icon, title and action buttons.
     * This function also lets you decide which of these elements the color gets applied on.
     * Explicitly setting individual icon, title or action button colors will override this method.
     *
     * @param colorInt The color int to apply to dialog elements. If this value is not specified,
     *  'colorAccent' attribute in your app theme will be used.
     * @param mode Specifies the element(s) on which the accent color should be applied. Default
     *  is [ACCENT_MODE_ALL].
     * @see PrimeDialog.setAccentColorRes
     */
    @JvmOverloads
    fun setAccentColor(@ColorInt colorInt: Int? = null, mode: Int = ACCENT_MODE_ALL): PrimeDialog {
        isAccentColorSet = true
        colorInt?.let { colorAccent = getColor(it) }
        accentColorMode = when(mode){
            ACCENT_MODE_NONE -> ACCENT_MODE_NONE
            ACCENT_MODE_TITLE -> ACCENT_MODE_TITLE
            ACCENT_MODE_ACTION -> ACCENT_MODE_ACTION

            else -> { ACCENT_MODE_ALL }
        }
        return this
    }

    /**
     * Defines the color to apply on elements like icon, title and action buttons.
     * This function also lets you decide which of these elements the color gets applied on.
     * Explicitly setting individual icon, title or action button colors will override this method.
     *
     * @param colorRes The color resource to apply to dialog elements. If this resource is not
     *    specified, then 'colorAccent' attribute resource in your app theme will be used.
     * @param mode Specifies the element(s) on which the accent color should be applied. Default
     *   is [ACCENT_MODE_ALL].
     * @see PrimeDialog.setAccentColor
     */
    @JvmOverloads
    fun setAccentColorRes(@ColorRes colorRes: Int? = null, mode: Int = ACCENT_MODE_ALL): PrimeDialog {
        val color = colorRes?.let { getColor(it) }
        return setAccentColor(color, mode)
    }

    private fun applyAccentColor() {
        val isActionApply = accentColorMode == ACCENT_MODE_ALL || accentColorMode == ACCENT_MODE_ACTION
        val isTitleAndIconApply = accentColorMode == ACCENT_MODE_ALL || accentColorMode == ACCENT_MODE_TITLE
        val color = if (isNightModeActive) Color.WHITE else Color.BLACK

        val isIconTintSet = iconAttributes.tint != null
        if (!isIconTintSet && isIconTintEnabled) {
            if(isTitleAndIconApply) {
                val finalColor = if(isHeaderShown) Color.WHITE else colorAccent
                applyIconTint(finalColor)
            } else {
                val finalColor = if(isHeaderShown) Color.WHITE else color
                applyIconTint(finalColor)
            }
        } else if (isIconTintSet) {
            applyIconTint(iconAttributes.tint ?: colorAccent)
        }

        if(titleAttributes.color == null) {
            if (isTitleAndIconApply) {
                val finalColor = if(isHeaderShown) Color.WHITE else colorAccent
                setTitleColor(finalColor)
            } else {
                val finalColor = if(isHeaderShown) Color.WHITE else color
                setTitleColor(finalColor)
            }
        }
        applyTitleAttributes()

        if (!isActionTextAppearanceSet) {
            if(isActionApply){
                if (isActionTextColorSet) {
                    applyActionTextColor()
                } else {
                    applyActionTextColor(colorAccent)
                }
            } else {
                applyActionTextColor(color)
            }
        } else {
            applyActionTextAppearance()
        }
    }

    private var accentMode = AccentMode.THEME
    private fun getDefaultAccentColor(): Int {
        if(isAccentColorSet) return colorAccent

        val dayOrNightColor = if (isNightModeActive) Color.WHITE else Color.BLACK
        if(accentMode == AccentMode.UI) return dayOrNightColor

        val colorAccent = TypedValue()
        // Check if 'colorAccent' attribute is defined in current theme resource
        // or other library used by the app.
        val isColorDefinedInAppTheme = context.theme
            .resolveAttribute(
                androidx.appcompat.R.attr.colorAccent,
                colorAccent,
                true
            )
        return if(isColorDefinedInAppTheme)
            colorAccent.data else dayOrNightColor
    }

    /** Sets the default accent color mode to one of
     * [AccentMode.UI] or [AccentMode.THEME]. The default is [AccentMode.THEME].
     *
     * This function will be overridden if one of [setAccentColor] or [setAccentColorRes]
     * is called at any point.*/
    fun setDefaultAccentColorMode(mode: AccentMode): PrimeDialog {
        accentMode = mode
        colorAccent = getDefaultAccentColor()
        return this
    }

    /** Enum representing the possible values for the default accent color mode.
     * Value can be one of [AccentMode.UI] or [AccentMode.THEME] */
    enum class AccentMode {
        /** Defines the default accent color as WHITE or BLACK based on whether the
         *  device is in day or night mode.
         *  @see [THEME]*/
        UI,
        /** Defines the default accent color as the value of 'colorAccent' attribute
         *  in the app theme. If 'colorAccent' is not defined in the app theme, then [AccentMode.UI] is used.
         *  @see [UI]*/
        THEME;
    }

    /**
     * Sets a background color for the dialog.
     *  See also See [setBackgroundColorRes].
     * @param colorCode The color code to be used on dialog background.
     */
    fun setBackgroundColor(colorCode: String): PrimeDialog {
        return setBackgroundColor(colorCode.toColorInt())
    }

    /**
     * Sets a background color for the dialog.
     *  See also See [setBackgroundColor].
     * @param colorRes The color resource id to be used on dialog background.
     */
    fun setBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        return setBackgroundColor(getColor(colorRes))
    }

    /**
     * Sets a background color for the dialog.
     * @since This function will be ignored if one of [setDayBackgroundColorRes] or
     * [setNightBackgroundColorRes] is set.
     *
     * @param color The id of color to be used on dialog background.
     */
    fun setBackgroundColor(@ColorInt color: Int): PrimeDialog {
        frameLayout.setBackgroundColor(color)
        setInternalActionLayoutBackgroundColor(color)
        dayColor = if (isTransparencySet) Color.TRANSPARENT else color
        nightColor = if (isTransparencySet) Color.TRANSPARENT else color
        return this
    }

    /**
     * Sets the dialog background color to be used in day mode. Calling this method will in turn automatically
     * enable day/night awareness. See [setNightBackgroundColorRes]
     *
     * @param color The color int to use.
     */
    fun setDayBackgroundColor(@ColorInt color: Int): PrimeDialog {
        dayColor = color
        return this
    }

    /**
     * Sets the dialog background color to be used in day mode. Calling this method will in turn automatically
     * enable day/night awareness. See [setNightBackgroundColorRes]
     *
     * @param colorRes The color resource id to use.
     */
    fun setDayBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        dayColor = getColor(colorRes)
        return this
    }

    /**
     * Sets the dialog background color to be used in night mode. Calling this method will in turn automatically
     * enable day/night awareness. See [setDayBackgroundColor]
     * @param color The color int to use.
     */
    fun setNightBackgroundColor(@ColorInt color: Int): PrimeDialog {
        nightColor = color
        return this
    }

    /**
     * Sets the dialog background color to be used in night mode. Calling this method will in turn automatically
     * enable day/night awareness. See [setDayBackgroundColorRes]
     * @param colorRes The color resource id to use.
     */
    fun setNightBackgroundColorRes(@ColorRes colorRes: Int): PrimeDialog {
        nightColor = getColor(colorRes)
        return this
    }

    fun getActionButtonLayout(buttonId: Int): LinearLayout {
        return when(buttonId){
            NEGATIVE_BUTTON -> binding.dialogBtnNegativeLayout
            NEUTRAL_BUTTON -> binding.dialogBtnNeutralLayout
            else -> binding.dialogBtnPositiveLayout
        }
    }

    fun getActionButtonTextView(buttonId: Int): AppCompatTextView {
        return when(buttonId){
            NEGATIVE_BUTTON -> binding.dialogBtnNegativeTextview
            NEUTRAL_BUTTON -> binding.dialogBtnNeutralTextview
            else -> binding.dialogBtnPositiveTextview
        }
    }

    fun <T : View?> findViewById(@IdRes viewID: Int): T {
        val default = dialog.findViewById<T>(viewID)
        if(isCustomView)
            return customView?.findViewById<T>(viewID) ?: default
        return default
    }

    val isShowing: Boolean = dialog.isShowing

    /** Commands a build of the dialog with all specified parameters.
     * @return A dialog instance with all defined parameters applied. */
    fun getDialog(): Dialog {
        applyIconAttributes()
        applyTitleAttributes()

        if (isDefaultCornersEnabled)
            setRoundedCorners()

        val isNightMode = isNightModeActive
        if (dontShowAgainSet) {
            binding.actionLayout.isVisible = true
            binding.dontShowAgainLayout.isVisible = true
            binding.checkBox.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    onDontShowAgainListener?.onBoxCheck(isChecked)
                }
                CompoundButtonCompat.setButtonTintList(
                    this,
                    ColorStateList.valueOf(colorAccent)
                )
                val textColor = if (isNightMode) Color.WHITE else Color.BLACK
                setTextColor(textColor)
                text = dontShowLabel
            }
        }

        prepareAccentColor()
        setMessageColor(if (isNightMode) Color.WHITE else Color.BLACK)
        val bgColor = if (isNightMode) nightColor else dayColor
        setBackgroundColor(bgColor)
        if(!isDimLevelSet)
            setScreenDimLevel()

        if(!isDialogWidthSet)
            setDialogWidth()

        if (isCornerRadiusSet) {
            setRoundedCorners()
        } else {
            setRoundedCorners(cornerRadiusDp = 0, backgroundColor = bgColor)
        }
        if (customView != null) {
            val childrenCount = customView!!.childCount
            for (i in 0 until childrenCount) {
                val child = customView!!.getChildAt(i)
                if (child is TextView) {
                    child.setTextColor(if (isNightMode) Color.WHITE else Color.BLACK)
                }
            }
        }

        applyDynamicMargins()

        if(isHeaderConfiguring && !isHeaderShown){
            Log.i(TAG, "Header configured with no background image or color")
            showDebugToast("Set header background image or color")
        }

        return dialog
    }

    private fun applyDynamicMargins() {
        with(binding){
            if(!isCustomView){
                // Apply a top margin of 4dp to 'actionLayout' if 'Don't show again' is in use.
                val actionLayoutParams = actionLayout.layoutParams as? RelativeLayout.LayoutParams
                actionLayoutParams?.apply {
                    topMargin = toDP(if(dontShowAgainSet) 4 else 0)
                }

                if(message.text.isNotEmpty() && !messageAttributes.isMarginSet) {
                    if(!titleAttributes.isTitleSet){
                        val margin = 9
                        val bottomMargin = if(isActionButtonSet) 0 else margin
                        setMessageMargin(topDp = margin, bottomDp = bottomMargin)
                    } else {
                        setMessageMargin()
                    }
                }
            }

            // Remove the default 4dp top margin on button layout when both the layout
            // and 'Don't show again' layout are both in use, so they don't feel too apart.
            if(dontShowAgainSet && isActionButtonSet){
                val buttonLayoutParams = buttonLayout.layoutParams as? FrameLayout.LayoutParams
                buttonLayoutParams?.topMargin = toDP(0)
            }
        }
    }

    private fun applyIconAttributes() {
        with(iconAttributes){
            if(isIconSet){
                titleIcon = findViewById<AppCompatImageView>(if (isHeaderShown) R.id.imageView else R.id.imageView2)
                titleIcon?.apply {
                    bitmap?.let {
                        setImageBitmap(it)
                    } ?: run {
                        iconRes?.let { setImageResource(it) }
                    }

                    if(iconWidth != null && iconHeight != null){
                        val layoutParams = layoutParams as LinearLayout.LayoutParams
                        layoutParams.width = toDP(iconWidth!!)
                        layoutParams.height = toDP(iconHeight!!)
                    }

                    isVisible = true
                    if (isHeaderShown) {
                        this@with.animation?.let { animation ->
                            clearAnimation()
                            startAnimation(animation)
                        }
                    }
                }
            }
        }
    }

    private fun applyTitleAttributes() {
        if(titleAttributes.isTitleSet){
            initializeTitle().apply {
                text = titleAttributes.text
                textSize = titleAttributes.size
                titleAttributes.letterSpacing?.let { letterSpacing = it }
                setTypeface(titleAttributes.typeface, titleAttributes.typefaceStyle)
                if(isHeaderShown) {
                    titleAttributes.let {
                        it.animation?.let { animation ->
                            clearAnimation()
                            startAnimation(animation)
                        }
                        if(it.overrideHeaderColor == true){
                            setTextColor(titleAttributes.color ?: colorAccent)
                        }
                    }
                } else {
                    setTextColor(titleAttributes.color ?: colorAccent)
                }
            }
        }
    }

    private fun prepareAccentColor(){
        if (isAccentColorSet) {
            applyAccentColor()
        } else {
            val isIconTintSet = iconAttributes.tint != null
            if (!isIconTintSet && isIconTintEnabled) {
                val finalColor = if(isHeaderShown) Color.WHITE else colorAccent
                applyIconTint(finalColor)
            } else if (isIconTintSet) {
                applyIconTint(iconAttributes.tint ?: colorAccent)
            }

            if (!isActionTextAppearanceSet) {
                if(isActionTextColorSet)
                    applyActionTextColor()
                else applyActionTextColor(colorAccent)

            } else {
                applyActionTextAppearance()
            }
        }
    }

    fun show() {
        val dialog = getDialog()
        if (context is Activity) {
            if (!(context as Activity).isFinishing) {
                dialog.show()
            } else {
                /** This prevents a crash due to 'BadTokenException', it occurs
                 * if the activity for whatever reason gets killed before the
                 * Dialog is shown.  */
                val message = "show() - 'BadTokenException'"
                Log.i(TAG, message)
                showDebugToast(message)
            }
        } else {
            dialog.show()
        }
        if (!onDialogDismissListenerSet && dontShowAgainSet) {
            dialog.setOnDismissListener {
                if (binding.checkBox.isChecked && onDontShowAgainListener != null) onDontShowAgainListener!!.onDismiss()
            }
        }

        val dialogView = dialog.window?.decorView
        dialogView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                dialogView.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                val factor = (maxHeightPercent / 100f) * DEFAULT_HEIGHT_FRACTION
                val maxHeight = (screenHeight * factor)
                val targetHeight = (screenHeight * DEFAULT_HEIGHT_FRACTION).toInt()
                val dialogHeight = this@PrimeDialog.dialogView?.height
                dialogHeight?.let {
                    val isMaxHeightMet = isMaxHeightSet && dialogHeight > maxHeight
                    if(!isDialogHeightDefined){
                        /**If the dialog exceeds the device screen height, and [setMaxHeight] is not
                         * called, the dialog height is reset to a maximum
                         * of [maxHeightPercent] of the device screen height.
                         *
                         * If [setMaxHeight] is called, then the specified value of [maxHeightPercent]
                         * is used instead. */
                        if(dialogHeight > targetHeight || isMaxHeightMet)
                            setDialogHeight(maxHeightPercent)
                    }
                }
            }
        })
    }

    fun dismiss() {
        if(dismissActionId == null)
            dismissActionId = DISMISS_ACTION_INTERNAL
        dialog.dismiss()
    }

    /** Execute an indirect action button click by passing the button id
     * to this function. Button id could be one of [POSITIVE_BUTTON],
     * [NEGATIVE_BUTTON] or [NEUTRAL_BUTTON]
     * @param buttonId The id of the target action button. */
    fun performActionClick(buttonId: Int) {
        getActionButtonLayout(buttonId).performClick()
    }

    private fun setActionButtonLayoutVisibility() {
        isActionButtonSet = true
        binding.actionLayout.isVisible = true
        binding.buttonLayoutParent.isVisible = true
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun toDP(dpValue: Int): Int {
        return (dpValue * context.resources.displayMetrics.density + 0.5).toInt()
    }

    private fun getColor(color: Int): Int {
        return try {
            ContextCompat.getColor(context, color)
        } catch (_: Exception){
            color
        }
    }

    private fun getHexColor(colorCode: String): Int {
        return try {
            colorCode.toColorInt()
        } catch (e: Exception){ 0 }
    }

    private var isCornerRadiusSet = false
    private var isDefaultCornersEnabled = true

    @JvmOverloads
    /**
     * Set rounded-corners of the dialog with a specified radius, dialog
     * padding and background color. This method targets API 21+
     *
     * @param cornerRadiusDp The radius (in dp) to be used for the dialog edges. Default value is 16
     * @param dialogPaddingDp The padding (in dp) of the dialog relative to the device screen. Default value is 16
     * @param backgroundColor The color to use as the rounded dialog background. The color is
     * automatically passed to [setBackgroundColor].
     * @see PrimeDialog.setRoundedCorners
     */
    fun setRoundedCorners(
        cornerRadiusDp: Int = cornerRadius,
        dialogPaddingDp: Int = dialogPadding,
        @ColorInt backgroundColor: Int = if(isNightModeActive) nightColor else dayColor
    ): PrimeDialog {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(backgroundColor)
        gradientDrawable.cornerRadius = toDP(cornerRadiusDp).toFloat()
//        gradientDrawable.setStroke(toDP(5),Color.RED)
        val insetDrawable = InsetDrawable(gradientDrawable, toDP(dialogPaddingDp))
        val layerDrawable = LayerDrawable(arrayOf<Drawable>(insetDrawable))
        dialog.window?.setBackgroundDrawable(layerDrawable)
        removeRoundedCorners()
        isCornerRadiusSet = true
        setBackgroundColor(backgroundColor)
        cornerRadius = cornerRadiusDp
        dialogPadding = dialogPaddingDp
        return this
    }

    /**
     * Disables dialog's default rounded corners, this function will be ignored if any of the
     * parameter specific variant of [setRoundedCorners] is called. After calling this function,
     * any subsequent call to [setRoundedCorners] should explicitly specific the new corner
     * radius value.
     */
    fun removeRoundedCorners(): PrimeDialog {
        cornerRadius = 0
        isCornerRadiusSet = false
        isDefaultCornersEnabled = false
        return this
    }

    /** A listener for action button click event. */
    fun interface OnDialogButtonClickListener {
        /** Action button click event.
         * @param dialog An instance of PrimeDialog
         * @param buttonId The id of the clicked action button */
        fun onDialogButtonClick(dialog: PrimeDialog, buttonId: Int)
    }

    fun interface OnDialogShowListener {
        fun onDialogShow(dialog: PrimeDialog)
    }

    /** Dialog dismiss event listener. */
    fun interface OnDialogDismissListener {
        /**@param dialog The dismissed dialog.
         * @param actionId The action that triggered the dismissal.
         * @param isCancelled A dialog is 'canceled' when it is dismissed by an
         * external action such as clicking outside the dialog window or pressing
         * device BACK button. */
        fun onDialogDismiss(dialog: PrimeDialog, actionId: Int, isCancelled: Boolean)
    }

    private var onDontShowAgainListener: OnDontShowAgainListener? = null

    /** This listener checks for 'Don't show again' event. */
    private interface DontShowAgainListener {
        fun onDismiss()
        fun onBoxCheck(isChecked: Boolean)
    }

    /** This listener checks for 'Don't show again' event. */
    open class OnDontShowAgainListener : DontShowAgainListener {
        /** Fires ONLY when the 'Don't show again' checkbox is checked before dialog dismissal.
         * @author Write your logic for not showing the dialog again here. This could be
         * storing a Shared preference value or some other means. */
        override fun onDismiss(){}
        /** Fires everytime the checkbox is toggled. */
        override fun onBoxCheck(isChecked: Boolean){}
    }

    private val isNightModeActive: Boolean
        get() {
            val configuration = context.resources.configuration
            val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }

    private fun showDebugToast(message: String) {
        if (BuildConfig.DEBUG) Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        /** No accent color is applied on icon, title and action buttons. */
        @JvmField
        var ACCENT_MODE_NONE: Int = 0

        /** Apply accent color to icon and title only. */
        @JvmField
        var ACCENT_MODE_TITLE: Int = 1

        /** Apply accent color to action buttons only. */
        @JvmField
        var ACCENT_MODE_ACTION: Int = 2

        /** Apply accent color to icon, title and action buttons. */
        @JvmField
        var ACCENT_MODE_ALL: Int = 3

        @JvmField
        var MATCH_PARENT: Int = ViewGroup.LayoutParams.MATCH_PARENT
        @JvmField
        var WRAP_CONTENT: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        @JvmField
        var CLICK_OUTSIDE: Int = 0
        @JvmField /** Positive action button layout id*/
        var POSITIVE_BUTTON: Int = 0
        @JvmField /** Negative action button layout id*/
        var NEGATIVE_BUTTON: Int = 0
        @JvmField /** Neutral action button layout id*/
        var NEUTRAL_BUTTON: Int = 0
        @JvmField /** Signifies id for internal dismiss action. */
        var UNIDENTIFIED_BUTTON: Int = 0

        @JvmField /** Dialog dismissed by a non-action button call to 'dismiss()'. */
        var DISMISS_ACTION_INTERNAL: Int = 1411
        @JvmField /** Dialog dismissed by user pressing BACK button on their device. */
        var DISMISS_ACTION_BACK_PRESSED: Int = 1412
        @JvmField /** Dialog dismissed by tapping outside dialog window. */
        var DISMISS_ACTION_CLICK_OUTSIDE: Int = 1413
        @JvmField /** Dialog dismissed by positive action button click or a call to 'dismiss()' at any point within the button click function. */
        var DISMISS_ACTION_POSITIVE_BUTTON: Int = 1414
        @JvmField /** Dialog dismissed by negative action button click or a call to 'dismiss()' at any point within the button click function. */
        var DISMISS_ACTION_NEGATIVE_BUTTON: Int = 1415
        @JvmField /** Dialog dismissed by neutral action button click or a call to 'dismiss()' at any point within the button click function. */
        var DISMISS_ACTION_NEUTRAL_BUTTON: Int = 1416

        val TYPEFACE_SERIF_BOLD = Typeface.create("serif", Typeface.BOLD)
        val TYPEFACE_SERIF_NORMAL = Typeface.create("serif", Typeface.NORMAL)
        val TYPEFACE_SERIF_MONOSPACE = Typeface.create("serif-monospace", Typeface.BOLD)
        val TYPEFACE_SANS_SERIF_MEDIUM_BOLD = Typeface.create("sans-serif-medium", Typeface.BOLD)
        val TYPEFACE_SANS_SERIF_MEDIUM_NORMAL =
            Typeface.create("sans-serif-medium", Typeface.NORMAL)
        val TYPEFACE_SANS_SERIF_REGULAR_BOLD = Typeface.create("sans-serif", Typeface.BOLD)
        val TYPEFACE_SANS_SERIF_REGULAR_NORMAL = Typeface.create("sans-serif", Typeface.NORMAL)
        val TYPEFACE_SANS_SERIF_CONDENSED_BOLD =
            Typeface.create("sans-serif-condensed", Typeface.BOLD)
        val TYPEFACE_SANS_SERIF_CONDENSED_NORMAL =
            Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        const val DEFAULT_ANIMATION_DURATION: Long = -11

        /** Converts a regular int dimension value to dp*/
        fun toDP(context: Context, dpValue: Int): Int {
            return (dpValue * context.resources.displayMetrics.density + 0.5).toInt()
        }
    }
}