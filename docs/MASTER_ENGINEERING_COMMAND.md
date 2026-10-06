# MASTER ENGINEERING COMMAND — ANDROID EDITION

## المرجع التأسيسي الإلزامي

الهدف: بناء **الدفتر الذكي — Smart Ledger** كتطبيق Android تجاري عربي، RTL، Offline-First، سريع، مستقر وقابل للصيانة والتوسع.

### أوامر عليا
1. Architecture First.
2. Design System First.
3. Domain First.
4. Test First.
5. Evidence First.
6. لا Hardcoded UI strings.
7. لا ألوان أو مقاسات تصميمية عشوائية.
8. لا Business Logic داخل Compose.
9. لا SQL داخل ViewModel.
10. لا تكرار للمنطق المالي.
11. لا شاشة أو زر أو Dialog بلا سبب.
12. لا إعلان اكتمال دون Evidence.

### التقنية
Kotlin، Jetpack Compose، Material 3 كأساس، Coroutines، Flow/StateFlow، ViewModel، Navigation Compose، Room، DataStore، WorkManager، Hilt، Android Keystore، Kotlin Serialization عند الحاجة.

### الطبقات
Presentation → Domain → Data، مع Core/Shared للـDesign Tokens والـMoney والـErrors والـLocalization والأمان، وTesting مستقلة.

### المجال
Books، Groups، People، Operations، Balance، Sales، Purchases، Inventory، Units، Expenses، Employees، Statements، Reports، Documents، Users/Roles/Permissions، Backup/Restore، Recycle Bin، Audit، Privacy.

### المسارات الحرجة
Person → Operation → Balance → Statement → Dashboard → Report

Sale → Lines → Stock → Person Balance → Invoice → Audit

Purchase → Lines → Stock → Supplier Balance → Invoice → Audit

Delete → Recycle → Restore → Rebuild/Validate → Audit

Backup → File → Integrity → Restore → Validate → Audit

### المال
Money Value Object، لا Float/Double كمصدر حقيقة. معنى «لنا» مستحق لنا، و«علينا» مستحق علينا. مصدر حقيقة واحد للرصيد والمخزون والإجماليات والصلاحيات وهوية النشاط.

### RTL والعربية
العربية هي المصدر الأساسي. يجب اختبار RTL الحقيقي، الأرقام، الهواتف، أكواد المنتجات، الفواتير، النص المختلط، التاريخ، الاتجاهات وTalkBack.

### UX
Full Screen للمهام الرئيسية، Dialog للقرارات القصيرة، Bottom Sheet للاختيار السياقي، وNavigation موحد. Touch target لا يقل عن 48dp. Keyboard/IME/Focus جزء من Architecture.

### الأمان
Keystore، KDF/Hashing مناسب، لا كلمات مرور Plain Text، لا أسرار في Source Control، صلاحيات في Domain/Application، Audit للأفعال الحساسة.

### Offline
الحسابات والعمليات والمخزون والمبيعات والمشتريات والنفقات والتقارير المحلية والبحث وكشف الحساب والإعدادات والنسخ المحلي يجب أن تعمل بدون الإنترنت.

### Backup
SmartLedger_YYYY-MM-DD_HH-mm-ss.zip، مع Database وVersion Metadata وIntegrity. Restore يبدأ بنسخة احتياطية للحالة الحالية ثم فحص Version/Integrity/Readability ثم Confirmation ثم Restore ثم Validation ثم Audit.

### الاختبارات
Unit، Integration، Room/Transactions، UI/RTL/Keyboard/Accessibility، وE2E للمسارات الحرجة.

### دورة التغيير
READ → INSPECT → UNDERSTAND → PLAN → IMPLEMENT → UNIT TEST → INTEGRATION TEST → BUILD → RUN → SCREENSHOT → VISUAL QA → ACCESSIBILITY QA → PERFORMANCE QA → FIX → REGRESSION → DOCUMENT → COMMIT → REPORT.

### حالات المتطلبات
IMPLEMENTED / PARTIAL / MISSING / BROKEN / NEEDS_VERIFICATION / BLOCKED.

هذه الوثيقة مرجع تأسيسي. أي انحراف غير بديهي يوثق في DECISIONS.md.
