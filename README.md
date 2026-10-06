# الدفتر الذكي — Smart Ledger

> تطبيق Android عربي تجاري لإدارة الحسابات، العمليات، المبيعات، المشتريات، المخزون، النفقات والتقارير — مبني بعقلية **Architecture First / Design System First / Domain First / Test First / Evidence First**.

**Repository:** `784988787aaaa-eng/my-New-App`  
**Platform:** Android Phones  
**Primary language:** Arabic (RTL)  
**Architecture:** Offline-First, layered architecture  
**Current phase:** Phase 0 — Forensics / Foundation planning  
**Current status:** `NEEDS_VERIFICATION`

---

## 1. تعريف المنتج

«الدفتر الذكي» هو برنامج أعمال يومي لصاحب النشاط التجاري والمستخدم العربي غير المتخصص في المحاسبة.

المنتج لا يهدف إلى تحويل المستخدم إلى محاسب، بل إلى الإجابة بوضوح وسرعة عن أسئلة العمل اليومية:

- كم لنا؟
- كم علينا؟
- ماذا دفع فلان؟
- ماذا أخذ؟
- ماذا بقي عليه؟
- ماذا لنا عنده؟
- ماذا بعنا؟
- ماذا اشترينا؟
- ماذا لدينا في المخزن؟
- ماذا حدث اليوم؟
- كم أنفقنا؟

كل قرار في UX وDomain وCopy يجب أن يخدم هذه اللغة.

---

## 2. الفلسفة الهندسية

لا نعتبر أي جزء مكتملًا لأنه يبدو جميلاً.

الاكتمال يحتاج إلى:

**Code + Architecture + Domain + Data + Tests + Build + Runtime + Visual Evidence + QA + Documentation**

وقاعدة التنفيذ لكل تغيير:

```text
READ
  ↓
INSPECT
  ↓
UNDERSTAND
  ↓
PLAN
  ↓
IMPLEMENT
  ↓
UNIT TEST
  ↓
INTEGRATION TEST
  ↓
BUILD
  ↓
RUN
  ↓
SCREENSHOT
  ↓
VISUAL QA
  ↓
ACCESSIBILITY QA
  ↓
PERFORMANCE QA
  ↓
FIX
  ↓
REGRESSION
  ↓
DOCUMENT
  ↓
COMMIT
  ↓
REPORT
```

### قاعدة لا شيء بلا سبب

كل Screen وRoute وButton وIcon وCard وDialog وAnimation وColor وText وField وService وDependency يجب أن يكون له سبب واضح وقابل للتوثيق.

---

## 3. التقنية المستهدفة

- Kotlin
- Jetpack Compose
- Material 3 كأساس للمكونات والوصول، مع Design System خاص بالتطبيق
- Coroutines
- Flow / StateFlow
- ViewModel
- Navigation Compose
- Room
- DataStore
- WorkManager
- Hilt
- Android Keystore
- Kotlin Serialization أو بديل مضبوط عند الحاجة

### مبادئ تقنية أساسية

- لا Business Logic داخل Composable.
- لا SQL داخل ViewModel.
- لا وصول مباشر من UI إلى Room.
- لا God Activity / God ViewModel / God Repository.
- لا Global mutable state.
- لا `runBlocking` في واجهة المستخدم.
- لا IO ثقيل على Main Thread.
- لا Network dependency للعمليات المحلية الأساسية.

---

## 4. المعمارية

### Presentation

Compose UI، Screen State، ViewModel، UI Events، UI Effects، Navigation.

### Domain

Entities، Value Objects، Use Cases، Business Rules، Financial Semantics، Validation، Policies.

### Data

Repositories، Room DAO، Entities، Mappers، Local Data Source، Backup Data Source، External Integration Adapters.

### Core / Shared

Design Tokens، Result/Error model، Money، Date/Number formatting، Localization، Logging contracts، Security abstractions.

### Testing

Unit، Integration، Database، Repository، Use Case، ViewModel، Compose UI، Screenshot/Golden عند توفرها، وEnd-to-End للمسارات الحرجة.

---

## 5. البنية المستهدفة

```text
app/

core/
  common/
  domain/
  database/
  security/
  localization/
  design-system/
  testing/

feature/
  auth/
  dashboard/
  people/
  books/
  operations/
  sales/
  purchases/
  inventory/
  expenses/
  employees/
  statements/
  reports/
  messages/
  users/
  settings/
  recycle-bin/
  audit/

backup/
docs/
```

يمكن تعديل أسماء الوحدات عندما تثبت المعمارية سبباً أفضل، مع الحفاظ على فصل المسؤوليات وحدود الـFeatures.

---

## 6. Design System

Design System مركزي واحد للتطبيق يغطي:

- Color Tokens
- Typography Tokens
- Spacing Tokens
- Shape Tokens
- Elevation Tokens
- Icon Sizes
- Touch Targets
- Component Heights
- Motion
- Borders / Dividers
- Forms
- Lists
- Loading / Empty / Error states
- Accessibility

### اللغة البصرية

**Premium Business Software**

- Deep Navy / Professional Blue
- Light Neutral Background
- White / Neutral Surfaces
- Blue للإجراء الأساسي
- Green = لنا / نجاح
- Red = علينا / خطر
- Orange = تنبيه
- Gray = معلومات ثانوية

لا Neon، ولا Gradients مبالغ فيها، ولا Glassmorphism، ولا Cards لمجرد الزينة.

الألوان Semantic Tokens وليست Hex values متناثرة داخل الشاشات.

---

## 7. العربية وRTL

العربية هي اللغة الأساسية والمصدر الأول للنصوص.

الالتزام بـ:

- True RTL
- Localization مركزية
- عدم استخدام Hardcoded UI strings
- دعم النصوص العربية الطويلة والقصيرة
- الأرقام العربية والإنجليزية
- أرقام الهواتف
- أرقام الفواتير
- أكواد المنتجات
- Mixed Arabic / English
- التواريخ والأوقات
- اتجاه الأيقونات والأسهم والتنقل

لا نعتبر RTL مجرد Mirror للواجهة.

---

## 8. المال والأرقام

يجب أن يكون هناك **Money Value Object**، ولا يجوز استخدام Float/Double كمصدر حقيقة للأموال.

يجب تطبيع الإدخالات مثل:

```text
1000
١٠٠٠
1,000
١٬٠٠٠
```

### المعنى المالي

- **لنا** = مبلغ مستحق لنا.
- **علينا** = مبلغ مستحق علينا.

مصدر حقيقة واحد لكل:

- Balance
- Stock Quantity
- Invoice Total
- User Permission
- Business Identity

لا يجوز وجود حساب مختلف للرصيد في Dashboard وStatement وPerson Details.

---

## 9. المجال الأساسي

النطاق المستهدف يشمل:

- الدفاتر والمجموعات والتصنيفات
- الأشخاص والحسابات
- العمليات
- المبيعات
- المشتريات
- المخزون والوحدات والتحويلات
- النفقات
- العمال والموظفين
- كشف الحساب
- التقارير
- الفواتير والمستندات
- المشاركة وWhatsApp عبر Android Sharesheet
- المستخدمون والأدوار والصلاحيات
- Privacy Mode
- النسخ الاحتياطي والاستعادة
- Recycle Bin
- Audit Log

---

## 10. المسارات الحرجة

### الحساب

```text
Person → Operation → Balance → Statement → Dashboard → Report
```

### البيع المخزني

```text
Sale → Lines → Stock → Person Balance → Invoice → Audit
```

### الشراء

```text
Purchase → Lines → Stock → Supplier Balance → Invoice → Audit
```

### الحذف

```text
Delete → Recycle → Restore → Rebuild/Validate → Audit
```

### النسخ الاحتياطي

```text
Backup → File → Integrity → Restore → Validate → Audit
```

أي Mutation تؤثر في أكثر من Aggregate يجب أن تكون Atomic عند الحاجة.

---

## 11. قاعدة البيانات

Room هو مصدر Persistence المحلي مع:

- Foreign Keys
- Indexes
- Transactions
- Migrations
- TypeConverters عند الحاجة

الكيانات المحتملة:

```text
Users
Roles
Permissions
UserRoles
RolePermissions
Books
Groups
People
Operations
OperationLines
Products
Units
StockMovements
Sales
SaleLines
Purchases
PurchaseLines
Expenses
ExpenseCategories
Employees
MessageTemplates
BusinessIdentity
Settings
RecycleBin
AuditLogs
BackupMetadata
```

لا تتم إضافة Entity دون Domain Purpose واضح.

---

## 12. Offline-First

الوظائف الأساسية يجب أن تعمل دون الإنترنت:

- الحسابات
- العمليات
- المخزون
- المبيعات
- المشتريات
- النفقات
- التقارير المحلية
- البحث المحلي
- كشف الحساب
- الإعدادات
- النسخ الاحتياطي المحلي

لا تتم إضافة Cloud Sync إلا بقرار موثق، وعند الحاجة يجب عزله خلف Sync Engine مع Queue وConflict Resolution وStable IDs وRetry وIdempotency.

---

## 13. الأمان والخصوصية

المتطلبات الأساسية:

- Android Keystore
- Password hashing / KDF مناسب
- عدم تخزين كلمات المرور Plain Text
- عدم وضع Secrets في Source Control
- عدم وضع Private Signing Keys في المستودع
- Secure local secrets
- حماية التصدير والمشاركة
- Permission checks على مستوى التطبيق والمنطق، وليس إخفاء الزر فقط
- Audit للأفعال الحساسة
- Privacy Mode لإخفاء المبالغ الحساسة دون تغيير البيانات
- Session timeout / App Lock / Re-authentication عند اعتمادها

لا تسجل كلمات المرور أو Recovery secrets أو Tokens أو بيانات مالية حساسة دون ضرورة.

---

## 14. النسخ الاحتياطي والاستعادة

اسم النسخة:

```text
SmartLedger_YYYY-MM-DD_HH-mm-ss.zip
```

تحتوي عند الإمكان على:

- Database
- Version Metadata
- Backup Metadata
- Integrity Information

قبل Restore:

1. Backup للحالة الحالية
2. فحص الملف
3. Version check
4. Integrity check
5. Readability check
6. عرض ملخص
7. Confirmation
8. Restore
9. Safe initialization
10. Audit

بعد Restore يجب فتح قاعدة البيانات والتحقق من Schema وSanity checks وإعادة بناء الـProjections عند الحاجة.

---

## 15. الاختبارات

### Unit

- Money
- Balance
- Unit conversion
- Validation
- Permissions
- Dates
- Message templates
- Financial semantics

### Integration

- Room
- Repository
- Transactions
- Backup
- Restore
- Migrations

### UI

- Navigation
- Forms
- Focus
- Keyboard / IME
- RTL
- Accessibility
- Empty / Error / Loading
- Permission states

### E2E

- First Launch
- Person → Debt → Payment → Balance → Statement
- Product → Purchase → Stock → Sale
- Delete → Restore
- Permissions
- Privacy
- Backup → Restore

---

## 16. Keyboard & Forms

الـKeyboard والـFocus جزء من Architecture وليس تحسيناً تجميلياً.

عند الحاجة:

- FocusRequester
- KeyboardOptions
- KeyboardActions
- IME Actions
- IME Insets
- Next / Done
- External keyboard support
- حفظ قيم الحقول عند إعادة الإنشاء
- منع تغطية الإجراء الرئيسي بالـKeyboard

الاختبار الأساسي:

```text
Open Form
→ Keyboard
→ Type
→ Next
→ Focus
→ Save
→ Correct Keyboard State
→ New Operation
```

---

## 17. الأداء

الأهداف:

- Startup سريع
- Navigation سريع
- Search سريع
- Save سريع
- Scroll سلس
- لا DB/IO على Main Thread
- Paging/Lazy loading للبيانات الكبيرة
- Compose stability
- Efficient images
- WorkManager للمهام الطويلة
- لا Polling غير ضروري
- لا Timers مستمرة
- لا Animations مستمرة بلا قيمة

---

## 18. Document Architecture

لا يتم ربط Compose مباشرة بتوليد PDF.

```text
Document DTO
→ Document Mapper
→ Document Model
→ Renderer
→ Output
```

Profiles المستهدفة:

- A4
- A5
- Thermal 58
- Thermal 80
- Statement
- Invoice
- Report

التوليد Async وغير حاجب للواجهة.

---

## 19. التقارير والمشاركة

التقارير الأساسية:

- الحسابات
- كشف الحساب
- المبيعات
- المشتريات
- النفقات
- المخزون
- حركة المخزون
- اليومية
- الأسبوعية
- الشهرية

التدفق:

```text
Filter → Preview → Export → Share → Print
```

المشاركة عبر Android Sharesheet/Intent architecture، وWhatsApp ليس شرطاً لحفظ البيانات.

---

## 20. مراحل التنفيذ

### Phase 0 — Forensics
جرد المشروع، Dependencies، Architecture، Build، Tests، Runtime، Screenshots، Risks.

### Phase 1 — Foundation
Gradle، Modules، Compose، Theme، Localization، DI، Database، Navigation، Errors، Logging، Testing.

### Phase 2 — Identity/Auth
First Launch، Login، Users، Roles، Permissions، Secure State.

### Phase 3 — Core Domain
Books، People، Operations، Balance، Statements.

### Phase 4 — Inventory
Products، Units، Conversion، Stock Movements، Alerts.

### Phase 5 — Commerce
Sales، Purchases، Invoice، Payment، Partial Payment، Returns.

### Phase 6 — Expenses & Employees
Expenses، Categories، Employees، Advances/Payments عند اعتمادها.

### Phase 7 — Reports/Documents
Statements، Reports، PDF، CSV/XLSX إن أمكن، Print/Share.

### Phase 8 — Backup/Security/Audit
Backup، Restore، Recycle، Audit، Privacy.

### Phase 9 — UX Excellence
Keyboard، Focus، Accessibility، Responsive، Empty/Error/Loading.

### Phase 10 — QA & Release
Regression، Critical Paths، Visual، Performance، Security، Release Build، Checklist.

---

## 21. Definition of Done

لا يعتبر أي Phase مكتملًا إلا بوجود:

- Code
- Architecture
- UI
- Domain
- Data
- Validation
- Permissions
- Error states
- Tests
- Successful build
- Successful runtime
- Reviewed screenshots
- Updated documentation
- Recorded known limitations
- Evidence for the claim

لا نستخدم «مكتمل» بدون Evidence.

---

## 22. Status Protocol

الحالات الرسمية:

`IMPLEMENTED` / `PARTIAL` / `MISSING` / `BROKEN` / `NEEDS_VERIFICATION` / `BLOCKED`

صيغة تقرير الحالة:

```text
PHASE:
DATE:
BASE:
HEAD:

INSPECTED:
FOUND:
CHANGED:
WHY:

DOMAIN:
DATA:
SECURITY:
UI:
RTL:
PERFORMANCE:
TESTS:
BUILD:
RUNTIME:
VISUAL QA:

DEFECTS:
FIXES:
RISKS:
KNOWN LIMITATIONS:
NEXT:
```

---

## 23. Gap Matrix

| Requirement | Status | Evidence | Test | Notes |
|---|---|---|---|---|
| RTL | NEEDS_VERIFICATION | — | — | Baseline pending |
| Balance | NEEDS_VERIFICATION | — | — | Domain implementation pending |
| Inventory | NEEDS_VERIFICATION | — | — | Domain implementation pending |
| Sales | NEEDS_VERIFICATION | — | — | Feature pending |
| Purchases | NEEDS_VERIFICATION | — | — | Feature pending |
| Reports | NEEDS_VERIFICATION | — | — | Document architecture pending |
| Backup | NEEDS_VERIFICATION | — | — | Backup architecture pending |
| Restore | NEEDS_VERIFICATION | — | — | Restore flow pending |
| Permissions | NEEDS_VERIFICATION | — | — | Auth foundation pending |
| Audit | NEEDS_VERIFICATION | — | — | Audit architecture pending |
| Privacy | NEEDS_VERIFICATION | — | — | Security foundation pending |
| Keyboard | NEEDS_VERIFICATION | — | — | UX foundation pending |
| Accessibility | NEEDS_VERIFICATION | — | — | QA foundation pending |

---

## 24. وثائق المشروع

المرجع الهندسي التفصيلي محفوظ داخل `docs/`:

```text
docs/
  MASTER_ENGINEERING_COMMAND.md
  ARCHITECTURE.md
  DOMAIN_MODEL.md
  DATA_MODEL.md
  DESIGN_SYSTEM.md
  NAVIGATION_MODEL.md
  SECURITY_MODEL.md
  BACKUP_RECOVERY.md
  TEST_STRATEGY.md
  QA_MATRIX.md
  IMPLEMENTATION_PLAN.md
  DECISIONS.md
  KNOWN_LIMITATIONS.md
  RELEASE_CHECKLIST.md
  STATUS.md
```

الوثائق جزء من المنتج وليست ملاحق اختيارية.

---

## 25. Git & Change Discipline

كل Phase وكل تغيير جوهري يجب أن يمر عبر:

```text
Inspect
→ Plan
→ Implement
→ Build
→ Test
→ Run
→ Evidence
→ Fix
→ Regression
→ Document
→ Commit
→ Report
```

لا تتم إعادة كتابة جزء صحيح لمجرد إعادة البناء.

ولا يتم الاعتماد على عبارة «تم» إذا لم يمكن إثباتها.

---

## 26. الهوية التجارية

بيانات المطور عند اعتمادها:

**م/ منصور قطينه للبرمجيات — 774004399**

هذه البيانات يجب أن تبقى قابلة للتخصيص ولا تُدفن داخل Domain أو UI بشكل Hardcoded.

---

## 27. Known Limitations

هذا المستودع في مرحلة التأسيس. لا يوجد في هذه اللحظة دليل على وجود:

- تطبيق Android مكتمل
- Build ناجح
- Runtime ناجح
- Database production schema
- Financial domain implementation
- Inventory implementation
- Sales/Purchases implementation
- Backup/Restore implementation
- Visual QA evidence

سيتم تحديث هذا القسم والـGap Matrix فقط بناءً على Evidence فعلي.

---

## 28. الهدف النهائي

نريد تطبيقاً:

- احترافياً
- عربياً
- سريعاً
- واضحاً
- هادئاً
- Offline-First
- آمناً
- دقيقاً
- قابلاً للصيانة والتوسع
- قليل التكرار
- ذا مصدر حقيقة واضح

وليس:

- CRUD App
- Dashboard Template
- Excel متطوراً
- مجموعة Screens غير مترابطة
- Prototype بصرياً فقط

**Premium لا يعني كثرة التفاصيل؛ Premium يعني أن كل تفصيلة في مكانها الصحيح.**

---

## 29. Master Acceptance Statement

لا يعتبر Smart Ledger Android جاهزاً تجارياً إلا عند تحقق جميع المتطلبات الحرجة:

- Architecture واضحة
- Design System مركزي
- RTL حقيقي
- Arabic UX احترافي
- Offline-First
- Balance صحيح
- Inventory صحيح
- Sales/Purchases صحيحة
- Reports صحيحة
- Backup/Restore مختبران
- Permissions مطبقة على مستوى التطبيق والمنطق
- Audit موجود
- Privacy موجود
- Keyboard / IME / Focus صحيح
- Accessibility مختبرة
- Performance مقبولة
- Critical flows مختبرة
- لا Hardcoded UI strings
- لا Random dimensions/colors
- لا Dead buttons
- لا Unexplained screens
- لا Duplicate financial logic
- لا Critical crashes
- لا Critical data-loss defects
- Documentation محدثة
- Gap Matrix واضحة
- Release Checklist مكتملة

---

## 30. المرجع التأسيسي

المستند الذي يحدد المتطلبات الهندسية التفصيلية للمشروع هو:

**MASTER ENGINEERING COMMAND — ANDROID EDITION**

ويجب حفظ النسخة الكاملة منه في:

`docs/MASTER_ENGINEERING_COMMAND.md`

ويُعامل كمرجع ملزم ما لم يتم اعتماد قرار هندسي موثق في `docs/DECISIONS.md`.

---

**الحالة الحالية:** `NEEDS_VERIFICATION`  
**القاعدة:** Architecture First · Design System First · Domain First · Test First · Evidence First
