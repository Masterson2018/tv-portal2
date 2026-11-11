package no.hjemme.portal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    // ===== Endre adressene her =====
    private val navidromeUrl = "https://navidrome.mastetson2025.duckdns.org"
    private val embyUrl = "http://bulldog2025.duckdns.org:8096"
    // ================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#101418"))
        }

        val title = TextView(this).apply {
            text = "Hva vil du åpne?"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(40))
        }
        root.addView(title)

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val b1 = makeButton("Navidrome", "Musikk", "#3DDC84", navidromeUrl)
        val b2 = makeButton("Emby", "Film og TV", "#52B54B", embyUrl)
        row.addView(b1)
        row.addView(b2)
        root.addView(row)

        setContentView(root)
        b1.requestFocus()
    }

    private fun makeButton(name: String, sub: String, accent: String, url: String): Button {
        val normal = GradientDrawable().apply {
            cornerRadius = dp(20).toFloat()
            setColor(Color.parseColor("#1E252D"))
            setStroke(dp(3), Color.parseColor("#2C3640"))
        }
        val focused = GradientDrawable().apply {
            cornerRadius = dp(20).toFloat()
            setColor(Color.parseColor("#2A3441"))
            setStroke(dp(5), Color.parseColor(accent))
        }
        val states = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), focused)
            addState(intArrayOf(android.R.attr.state_pressed), focused)
            addState(intArrayOf(), normal)
        }

        return Button(this).apply {
            text = "$name\n$sub"
            isAllCaps = false
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            background = states
            isFocusable = true
            isFocusableInTouchMode = true
            layoutParams = LinearLayout.LayoutParams(dp(300), dp(180)).apply {
                setMargins(dp(24), 0, dp(24), 0)
            }
            setOnClickListener {
                startActivity(Intent(this@MainActivity, WebActivity::class.java).putExtra("url", url))
            }
            setOnFocusChangeListener { v: View, hasFocus: Boolean ->
                v.animate().scaleX(if (hasFocus) 1.08f else 1f)
                    .scaleY(if (hasFocus) 1.08f else 1f).setDuration(120).start()
            }
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
