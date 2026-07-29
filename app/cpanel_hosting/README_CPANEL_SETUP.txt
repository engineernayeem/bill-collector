====================================================================
ISP Bill Collector - cPanel Hosting & Android Studio APK Build Guide
====================================================================

১. cPanel-এ ফাইল আপলোড করার নিয়মাবলী:
------------------------------------
১) cPanel এর File Manager-এ যান।
২) public_html ফোল্ডারের ভেতরে একটি নতুন ফোল্ডার খুলুন, যেমন: `api` বা `isp`
   (উদাহরণ URL: https://yourdomain.com/api/)
৩) cPanel_hosting ফোল্ডারের নিচের সমস্ত ফাইল উক্ত ফোল্ডারে আপলোড করুন:
   - api.php
   - version.json
   - customers.json
   - packages.json
   - payments.json
   - sync_full.json
৪) ফাইল পারমিশন নিশ্চিত করুন:
   - api.php -> 644
   - version.json, customers.json, packages.json, payments.json, sync_full.json -> 666 (Write Permission)
   - ফোল্ডার পারমিশন -> 755
৫) অ্যাপের "সেটিংস (Settings)" পেজে গিয়ে Server URL দিন:
   https://yourdomain.com/api/

২. OneSignal ইন-অ্যাপ ও পুশ নটিফিকেশন ব্যাকএন্ড সেটআপ:
------------------------------------------------
১) onesignal.com এ ফ্রী একাউন্ট খুলে একটি নতুন Android App তৈরি করুন।
২) OneSignal ড্যাশবোর্ড থেকে App ID কপি করুন।
৩) cPanel File Manager-এ থাকা `version.json` ফাইল ওপেন করুন এবং `"oneSignalAppId": "আপনার_ONESIGNAL_APP_ID"` বসিয়ে সেভ করুন।
৪) অ্যাপটি সার্ভারের সাথে কানেক্ট হওয়ামাত্রই অ্যাপটিতে স্বয়ংক্রিয়ভাবে OneSignal সচল হয়ে যাবে (অ্যাপের সেটিংস পেজে ম্যানুয়ালি দেওয়ারও প্রয়োজন নেই)।
৫) OneSignal ড্যাশবোর্ডের "In-App Messages" বা "Push Notifications" মেনু থেকে মেসেজ তৈরি করে পাঠালে সকল ব্যবহারকারীর ফোনে পপআপ ও পুশ নটিফিকেশন চলে যাবে।

৩. অ্যান্ড্রয়েড স্টুডিওতে APK বিল্ড করার নিয়মাবলী:
--------------------------------------------
১) সম্পূর্ণ প্রোজেক্টটি ZIP হিসেবে ডাউনলোড করে Extract করুন।
২) Android Studio ওপেন করে Open Project এ গিয়ে প্রোজেক্ট ফোল্ডার নির্বাচন করুন।
৩) Build Menu -> Build Bundle(s) / APK(s) -> Build APK(s) এ ক্লিক করুন।
৪) APK বিল্ড সম্পন্ন হলে `app/build/outputs/apk/debug/app-debug.apk` ফাইলটি ইনস্টল করুন।

====================================================================
