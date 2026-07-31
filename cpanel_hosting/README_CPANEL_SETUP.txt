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
   - ads.json
   - customers.json
   - packages.json
   - payments.json
   - sync_full.json
৪) ফাইল পারমিশন নিশ্চিত করুন:
   - api.php -> 644
   - version.json, ads.json, customers.json, packages.json, payments.json, sync_full.json -> 666 (Write Permission)
   - ফোল্ডার পারমিশন -> 755
৫) অ্যাপের "সেটিংস (Settings)" পেজে গিয়ে Server URL দিন:
   https://yourdomain.com/api/

২. ইন-অ্যাপ ব্যানার অ্যাড ও GIF সাপোর্ট সেটআপ (Admin Control):
------------------------------------------------------
১) অ্যাপে ব্যানার বিজ্ঞাপন বা স্পনসর পিকচার দেখাতে cPanel এর `ads.json` অথবা `version.json` ফাইলে ব্যানার যুক্ত করুন।
২) উদাহরণ `ads.json` স্ট্রাকচার:
   [
     {
       "id": "ad_1",
       "title": "বিশেষ প্রোমো অফার",
       "imageUrl": "https://i.postimg.cc/mD8N0x5P/banner-promo.gif",
       "targetUrl": "https://your-domain.com/offer",
       "active": true,
       "position": "dashboard"
     },
     {
       "id": "ad_2",
       "title": "স্পনসর ব্যানার",
       "imageUrl": "https://i.postimg.cc/0jXqQ6v3/sponsor-banner.png",
       "targetUrl": "https://your-domain.com/sponsor",
       "active": true,
       "position": "customer_list"
     }
   ]
৩) PNG, JPG, WEBP সহ **Animated GIF** ও সরাসরি সাপোর্ট করে।
৪) `position`: "dashboard" দিলে ড্যাশবোর্ডে দেখাবে, "customer_list" দিলে কাস্টমার তালিকায় দেখাবে, "all" দিলে সব জায়গায় দেখাবে।
৫) `active`: true রাখলে বিজ্ঞাপনি ব্যানারটি প্রদর্শিত হবে, false করলে বন্ধ থাকবে।

৩. OneSignal ইন-অ্যাপ ও পুশ নটিফিকেশন ব্যাকএন্ড সেটআপ:
------------------------------------------------
১) onesignal.com এ ফ্রী একাউন্ট খুলে একটি নতুন Android App তৈরি করুন।
২) OneSignal ড্যাশবোর্ড থেকে App ID কপি করুন।
৩) cPanel File Manager-এ থাকা `version.json` ফাইল ওপেন করুন এবং `"oneSignalAppId": "আপনার_ONESIGNAL_APP_ID"` বসিয়ে সেভ করুন।
৪) অ্যাপটি সার্ভারের সাথে কানেক্ট হওয়ামাত্রই অ্যাপটিতে স্বয়ংক্রিয়ভাবে OneSignal সচল হয়ে যাবে।
৫) OneSignal ড্যাশবোর্ডের "In-App Messages" বা "Push Notifications" মেনু থেকে মেসেজ তৈরি করে পাঠালে সকল ব্যবহারকারীর ফোনে পপআপ ও পুশ নটিফিকেশন চলে যাবে।

৪. Google Sign-In / Sign-Up (Google Auth) সুবিধা:
------------------------------------------------
১) অ্যাপে "Google দিয়ে সাইন ইন / সাইন আপ" এর ব্যবস্থা রাখা হয়েছে।
২) অ্যান্ড্রয়েড বিল্ড ফাইলে `firebase-auth`, `credentials`, এবং `googleid` সরাসরি ইন্টিগ্রেটেড রয়েছে।
৩) অ্যাপের সাইন-ইন স্ক্রীন এবং সেটিংস স্ক্রীন উভয় জায়গা থেকেই কাস্টমার ও অ্যাডমিন তাদের গুগল অ্যাকাউন্ট নির্বাচন করে এক ক্লিকে সাইন-ইন করতে পারবেন।

৫. অ্যান্ড্রয়েড স্টুডিওতে APK বিল্ড করার নিয়মাবলী:
--------------------------------------------
১) সম্পূর্ণ প্রোজেক্টটি ZIP হিসেবে ডাউনলোড করে Extract করুন।
২) Android Studio ওপেন করে Open Project এ গিয়ে প্রোজেক্ট ফোল্ডার নির্বাচন করুন।
৩) Build Menu -> Build Bundle(s) / APK(s) -> Build APK(s) এ ক্লিক করুন।
৪) APK বিল্ড সম্পন্ন হলে `app/build/outputs/apk/debug/app-debug.apk` ফাইলটি ইনস্টল করুন।

====================================================================
