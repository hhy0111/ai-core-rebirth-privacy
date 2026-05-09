# Keep billing and ads entry points conservative until the production SDK flows are wired.
-keep class com.android.billingclient.** { *; }
-keep class com.google.android.gms.ads.** { *; }
