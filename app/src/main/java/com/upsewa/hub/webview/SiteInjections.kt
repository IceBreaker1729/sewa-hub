package com.upsewa.hub.webview

import android.net.Uri

/**
 * Per-domain CSS / JS injection that hides the cluttered chrome of UP govt sites
 * (banners, ads, side menus, footers, language switchers) and re-styles the actual
 * useful content (forms, tables, results) into a clean mobile-first look that matches
 * UP Sewa Hub's brand.
 *
 * The govt page is still being rendered by the WebView — we're just overlaying a
 * stylesheet at runtime. Nothing about the underlying form submission, captcha, or
 * data flow changes; the user sees the same fields and gets the same official result,
 * just packaged in a much nicer wrapper.
 *
 * Add a new site by:
 *   1. Adding its host to `SITE_RULES` with a CSS string.
 *   2. Optionally returning extra JS from `extraJsFor()` for DOM tweaks.
 */
object SiteInjections {

    /** Base reset applied to every page on top of site-specific rules. */
    private val BASE_CSS = """
        :root {
          --upsh-bg: #f6f5f1;
          --upsh-ink: #1a2421;
          --upsh-soft: #5a6360;
          --upsh-brand: #0b3d2e;
          --upsh-gold: #c79a3a;
          --upsh-line: #e3e0d6;
          --upsh-radius: 12px;
        }
        @media (prefers-color-scheme: dark) {
          :root {
            --upsh-bg: #0e1411; --upsh-ink: #ecede9; --upsh-soft: #9aa39d;
            --upsh-brand: #3a8a6d; --upsh-line: #243029;
          }
        }
        html, body {
          background: var(--upsh-bg) !important;
          color: var(--upsh-ink) !important;
          font-family: 'Roboto', 'Noto Sans Devanagari', -apple-system, system-ui, sans-serif !important;
          font-size: 15px !important;
          line-height: 1.5 !important;
          margin: 0 !important; padding: 12px !important;
          max-width: 100% !important; overflow-x: hidden !important;
        }
        /* Tables — responsive-ish with card-like presentation */
        table {
          width: 100% !important; border-collapse: collapse !important;
          background: #fff !important; border-radius: var(--upsh-radius) !important;
          overflow: hidden !important; box-shadow: 0 1px 3px rgba(0,0,0,.06) !important;
          margin: 10px 0 !important; font-size: 14px !important;
        }
        @media (prefers-color-scheme: dark) {
          table { background: #161d19 !important; }
        }
        th { background: var(--upsh-brand) !important; color: #fff !important;
             padding: 10px 8px !important; text-align: left !important; font-weight: 600 !important; }
        td { padding: 10px 8px !important; border-bottom: 1px solid var(--upsh-line) !important;
             vertical-align: top !important; word-break: break-word !important; }
        tr:last-child td { border-bottom: 0 !important; }
        /* Forms */
        input[type=text], input[type=number], input[type=email], input[type=tel],
        input[type=password], select, textarea {
          width: 100% !important; padding: 11px 12px !important; margin: 4px 0 10px !important;
          border: 1px solid var(--upsh-line) !important; border-radius: 10px !important;
          background: #fff !important; color: var(--upsh-ink) !important;
          font-size: 15px !important; box-sizing: border-box !important;
        }
        @media (prefers-color-scheme: dark) {
          input, select, textarea { background: #1c2520 !important; }
        }
        input:focus, select:focus, textarea:focus {
          outline: none !important; border-color: var(--upsh-brand) !important;
          box-shadow: 0 0 0 3px rgba(11,61,46,.15) !important;
        }
        button, input[type=button], input[type=submit], .btn, .button {
          background: var(--upsh-brand) !important; color: #fff !important;
          border: 0 !important; border-radius: 10px !important;
          padding: 12px 20px !important; font-weight: 600 !important; font-size: 15px !important;
          cursor: pointer !important; margin: 6px 6px 6px 0 !important;
          box-shadow: 0 1px 3px rgba(11,61,46,.2) !important;
        }
        /* Cards / panels */
        .card, .panel, fieldset, .well, .box, .container > div, .content-wrap {
          background: #fff !important; border: 1px solid var(--upsh-line) !important;
          border-radius: var(--upsh-radius) !important; padding: 14px !important;
          margin: 10px 0 !important; box-shadow: 0 1px 3px rgba(0,0,0,.04) !important;
        }
        @media (prefers-color-scheme: dark) {
          .card, .panel, fieldset, .well { background: #161d19 !important; }
        }
        legend { padding: 0 8px !important; font-weight: 700 !important; color: var(--upsh-brand) !important; }
        h1, h2, h3, h4, .title, .heading {
          color: var(--upsh-brand) !important; font-weight: 700 !important;
          margin: 14px 0 8px !important;
        }
        a, a:visited { color: var(--upsh-brand) !important; text-decoration: underline !important; }
        img { max-width: 100% !important; height: auto !important; }
        /* Hide common bloat across most govt sites */
        marquee, .marquee, .blink, .ticker-tape, .scrolling-news { display: none !important; }
        .ad, .ads, .advertisement, [id*="ads"], [class*="ads"] { display: none !important; }
    """.trimIndent()

    /** Site-specific rules — keyed by hostname suffix (matched with .contains). */
    private val SITE_RULES: Map<String, String> = mapOf(

        "upbhulekh.gov.in" to """
            /* Hide top banner, govt header rows, the language flags, and footer */
            header, .top-bar, .header-top, .header-bottom, .navbar-top,
            #top, #header, .top_strip, .top-strip,
            footer, #footer, .footer, .copyright, .visitor-count,
            .left-bar, .left-menu, .left-side, #left,
            img[src*="banner"], img[src*="cm_"], img[src*="dy_"],
            iframe[src*="banner"] { display: none !important; }
            /* the main lookup form sits inside a centered table — make it card-y */
            #ContentPlaceHolder1_panAdd, .panel-search, form[id*="form"] {
              background: #fff !important; padding: 16px !important; border-radius: 12px !important;
              box-shadow: 0 1px 4px rgba(0,0,0,.06) !important; margin: 12px 0 !important;
            }
        """.trimIndent(),

        "upbhunaksha.gov.in" to """
            header, footer, .navbar, .header, .footer, .top-bar { display: none !important; }
            #map { height: 75vh !important; border-radius: 12px !important; }
        """.trimIndent(),

        "igrsup.gov.in" to """
            /* Strip the heavy 5-row header and footer */
            #header, .header, .top-header, .nav-top, .navbar-default,
            #footer, .footer, .footer-area, .footer-section,
            .right-sidebar, .left-sidebar, .breadcrumb, .marquee-section,
            img[src*="cm_"], img[src*="dy_"], img[src*="header"] { display: none !important; }
            .login-form, .form-container, .main-content {
              max-width: 100% !important; padding: 0 !important;
            }
        """.trimIndent(),

        "edistrict.up.gov.in" to """
            #header, header, .header-section, .top-header, .navbar-top,
            #footer, footer, .footer-section, .copyright,
            .left-panel, #left-panel, .right-panel,
            img[src*="banner"] { display: none !important; }
            .login-box, .login-container, .panel-login {
              max-width: 420px !important; margin: 20px auto !important;
              background: #fff !important; border-radius: 12px !important; padding: 20px !important;
            }
        """.trimIndent(),

        "jansunwai.up.nic.in" to """
            header, footer, #header, #footer, .header, .footer,
            .top-bar, .navbar, .left-panel, .right-panel,
            .marquee, .scroll-news, img[src*="banner"] { display: none !important; }
            .body-content, .container-fluid, .row { padding: 0 !important; margin: 0 !important; }
            /* Reformat the complaint-status form */
            .complaint-form, form { padding: 14px !important; }
        """.trimIndent(),

        "ecourts.gov.in" to """
            header, footer, .header, .footer, .top-bar, .navbar-default,
            .left-menu, .right-menu, #leftmenu, #rightmenu,
            #header, #footer, .copyright, .visitor-count, marquee { display: none !important; }
            /* Centered case-status form */
            .case-status, .form-horizontal, form {
              background: #fff !important; padding: 16px !important; border-radius: 12px !important;
              margin: 12px 0 !important; box-shadow: 0 1px 4px rgba(0,0,0,.06) !important;
            }
            /* Result tables look much cleaner */
            #showList table, .case-history-table { font-size: 13px !important; }
        """.trimIndent(),

        "allahabadhighcourt.in" to """
            header, footer, .header, .footer, .top-bar, .navbar, .top-strip,
            .left-sidebar, .right-sidebar, #sidebar, .breadcrumb,
            marquee, .marquee, .news-ticker, img[src*="banner"] { display: none !important; }
            .case-status-form, form, .main-content {
              background: #fff !important; padding: 16px !important; border-radius: 12px !important;
              max-width: 100% !important;
            }
        """.trimIndent(),

        "fcs.up.gov.in" to """
            header, footer, .header, .footer, .top-bar, .navbar-top, .navbar,
            .left-bar, .right-bar, #left, #right, .breadcrumb,
            marquee, .marquee, img[src*="banner"], img[src*="cm_"] { display: none !important; }
        """.trimIndent(),

        "uppolice.gov.in" to """
            header, footer, .header, .footer, .top-header, .navbar-top,
            .sidebar, .left-sidebar, .right-sidebar, .breadcrumb,
            marquee, .marquee { display: none !important; }
        """.trimIndent(),

        "uppcl.org" to """
            header, footer, .header, .footer, .top-bar, .navbar,
            .left-menu, .right-menu, .breadcrumb, marquee, .marquee,
            img[src*="banner"], img[src*="cm_"] { display: none !important; }
        """.trimIndent(),

        "parivahan.gov.in" to """
            header, footer, .header, .footer, .top-header, .navbar-default,
            #header, #footer, .left-content, .right-content,
            marquee, .marquee, img[src*="banner"] { display: none !important; }
        """.trimIndent(),
    )

    /**
     * Build the JS snippet that, when run inside the WebView, injects the stylesheet
     * and any per-site DOM tweaks. Idempotent (uses an id so re-running doesn't dupe).
     */
    fun buildInjection(url: String): String {
        val host = runCatching { Uri.parse(url).host ?: "" }.getOrDefault("")
        val siteCss = SITE_RULES.entries
            .firstOrNull { host.contains(it.key, ignoreCase = true) }
            ?.value ?: ""
        val combined = BASE_CSS + "\n" + siteCss
        val extra = extraJsFor(host)
        // JS string-escape: backslash, backtick, dollar
        val escaped = combined
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("$", "\\$")
        return """
            (function() {
              try {
                var old = document.getElementById('upsh-injected-style');
                if (old) old.remove();
                var s = document.createElement('style');
                s.id = 'upsh-injected-style';
                s.textContent = `$escaped`;
                document.head.appendChild(s);
                $extra
              } catch (e) { /* ignore */ }
            })();
        """.trimIndent()
    }

    private fun extraJsFor(host: String): String {
        return when {
            // upbhulekh: auto-scroll past the marketing banner on result pages
            host.contains("upbhulekh", true) -> """
                window.scrollTo({ top: 0 });
            """.trimIndent()
            // ecourts: bring case-status form into view on load
            host.contains("ecourts", true) -> """
                var f = document.querySelector('.form-horizontal, .case-status, form');
                if (f) f.scrollIntoView({behavior:'instant', block:'start'});
            """.trimIndent()
            else -> ""
        }
    }
}
