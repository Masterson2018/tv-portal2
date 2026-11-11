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
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private data class Entry(
        val name: String,
        val sub: String,
        val url: String,
        val accent: String
    )

    // ===== Legg til eller endre adresser her =====
    // Husk komma etter hver linje unntatt den siste.
    private val entries = listOf(
        Entry("Navidrome", "Musikk", "https://navidrome.masterson2025.duckdns.org", "#3DDC84"),
        Entry("Emby", "Film og TV", "http://bulldog2025.duckdns.org:8096", "#52B54B"),
        Entry("Audiobookshelf", "Lydbøker", "https://audiobookshelf.masterson2025.duckdns.org", "#FF9800")
    )
    // ============================================

    // Hvor mørk skal bakgrunnsbildet dekkes? Første to tegn etter # er styrken
    // (00 = ingen, FF = helt svart). 99 er omtrent 60 %.
    private val dimColor = "#99101418"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            clipChildren = false
        }

        root.addView(TextView(this).apply {
            text = "Hva vil du åpne?"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(24), 0, dp(24))
        })

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            clipChildren = false
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val buttons = entries.map { makeButton(it) }
        buttons.chunked(3).forEach { chunk ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                clipChildren = false
            }
            chunk.forEach { row.addView(it) }
            content.addView(
                row,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dp(12), 0, dp(12)) }
            )
        }

        val scroll = ScrollView(this).apply {
            clipChildren = false
            clipToPadding = false
            addView(content)
        }
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        // Bakgrunnsbilde med mørkt lag over, så knappene er lette å lese
        val bg = ImageView(this).apply {
            setImageResource(R.drawable.background)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val dim = View(this).apply {
            setBackgroundColor(Color.parseColor(dimColor))
        }
        val frame = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#101418"))
        }
        val full = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        frame.addView(bg, full)
        frame.addView(dim, FrameLayout.LayoutParams(full))
        frame.addView(root, FrameLayout.LayoutParams(full))

        setContentView(frame)
        buttons.firstOrNull()?.requestFocus()
    }

    private fun makeButton(e: Entry): Button {
        val normal = GradientDrawable().apply {
            cornerRadius = dp(20).toFloat()
            setColor(Color.parseColor("#E61E252D"))
            setStroke(dp(3), Color.parseColor("#2C3640"))
        }
        val focused = GradientDrawable().apply {
            cornerRadius = dp(20).toFloat()
            setColor(Color.parseColor("#F22A3441"))
            setStroke(dp(5), Color.parseColor(e.accent))
        }
        val states = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), focused)
            addState(intArrayOf(android.R.attr.state_pressed), focused)
            addState(intArrayOf(), normal)
        }

        return Button(this).apply {
            text = "${e.name}\n${e.sub}"
            isAllCaps = false
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            background = states
            isFocusable = true
            isFocusableInTouchMode = true
            layoutParams = LinearLayout.LayoutParams(dp(260), dp(160)).apply {
                setMargins(dp(16), 0, dp(16), 0)
            }
            setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, WebActivity::class.java)
                        .putExtra("url", e.url)
                )
            }
            setOnFocusChangeListener { v: View, hasFocus: Boolean ->
                v.animate()
                    .scaleX(if (hasFocus) 1.08f else 1f)
                    .scaleY(if (hasFocus) 1.08f else 1f)
                    .setDuration(120).start()
            }
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
