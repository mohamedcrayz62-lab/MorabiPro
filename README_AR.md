# مشروع Android لتطبيق MorabiPro

هذه حزمة مشروع Android لتشغيل واجهة HTML الحالية داخل تطبيق Android. لم يتم تعديل واجهة HTML أو الألوان أو اللوغو أو وظائف التطبيق؛ اسم التطبيق المعروض هو **MorabiPro** فقط.

## إنشاء ملف APK عبر Android Studio
1. افتح مجلد `MorabiPro_Android` في Android Studio.
2. وافق على تثبيت Android SDK المناسب إذا طُلب منك ذلك.
3. من القائمة اختر **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. بعد اكتمال البناء ستجد APK عادةً في:
   `app/build/outputs/apk/debug/app-debug.apk`

## البناء التلقائي عبر GitHub Actions
أُضيف سير عمل في `.github/workflows/build-apk.yml`:
1. أنشئ مستودعاً جديداً على GitHub وارفع محتويات هذا المجلد إليه.
2. افتح تبويب **Actions** وشغّل سير العمل **Build MorabiPro APK**.
3. بعد نجاحه، افتح نتيجة التشغيل وحمّل الملف من قسم **Artifacts** باسم `MorabiPro-debug-apk`.

## ملاحظات
- لم يتم إرفاق APK مبنيّ في هذه الحزمة لأن بيئة التنفيذ الحالية لا تحتوي على Android SDK أو Gradle جاهزين للبناء.
- نسخة APK الناتجة من مسار debug للاختبار، وليست نسخة موقّعة للنشر في المتجر.
- اختبر التطبيق على هاتفك وتأكد من حفظ البيانات والنسخ الاحتياطي قبل الاعتماد عليه.
