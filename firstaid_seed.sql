PRAGMA foreign_keys = ON;

-- Android expects this table in databases it did not create itself.
CREATE TABLE android_metadata (locale TEXT);
INSERT INTO android_metadata (locale) VALUES ('en_US');

CREATE TABLE emergencies (
    case_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    title        TEXT    NOT NULL,
    danger_level INTEGER NOT NULL CHECK (danger_level BETWEEN 1 AND 3), -- 1 minor, 2 medium, 3 critical
    symptoms     TEXT    NOT NULL,
    search_text  TEXT    NOT NULL                                       -- normalized title + symptoms
);

CREATE TABLE steps (
    step_id          INTEGER PRIMARY KEY AUTOINCREMENT,
    case_id          INTEGER NOT NULL REFERENCES emergencies (case_id) ON DELETE CASCADE,
    step_number      INTEGER NOT NULL CHECK (step_number >= 1),
    instruction_text TEXT    NOT NULL,
    UNIQUE (case_id, step_number)
);

CREATE TABLE custom_contacts (
    contact_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    contact_name TEXT NOT NULL,
    phone_number TEXT NOT NULL
);

CREATE TABLE treated_cases (
    treated_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    description  TEXT NOT NULL,
    treated_date TEXT NOT NULL,   -- ISO format yyyy-MM-dd (sorts correctly as text)
    photo_path   TEXT,            -- path of the image file inside the app storage, optional
    procedures   TEXT NOT NULL
);

CREATE INDEX idx_emergencies_danger ON emergencies (danger_level);
CREATE INDEX idx_treated_date ON treated_cases (treated_date);

BEGIN TRANSACTION;
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (1, 'نزيف حاد', 3, 'نزيف غزير أو متدفق لا يتوقف، شحوب وبرودة في الجلد، دوخة وتسارع في النبض، وقد يصل إلى فقدان الوعي.', 'نزيف حاد نزيف غزير او متدفق لا يتوقف، شحوب وبروده في الجلد، دوخه وتسارع في النبض، وقد يصل الي فقدان الوعي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 1, 'اتصل بالإسعاف فوراً أو اطلب من شخص قريب الاتصال، وارتدِ قفازات إن وُجدت.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 2, 'أجلس المصاب أو أرقده لتقليل خطر الإغماء.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 3, 'اضغط بقوة ومباشرة على الجرح بقطعة قماش نظيفة أو شاش.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 4, 'استمر بالضغط دون رفع يدك لتفقّد الجرح. إذا تشبّعت القماشة بالدم فضع قماشة أخرى فوقها ولا تنزع الأولى.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 5, 'ارفع الطرف المصاب إن أمكن، وثبّت ضماداً ضاغطاً فوق القماش.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 6, 'لا تحاول سحب أي جسم عالق في الجرح. ولا تستخدم الرباط الضاغط (تورنيكيه) إلا إذا فشل الضغط المباشر في إيقاف نزيف مهدد للحياة في أحد الأطراف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 7, 'ابقَ مع المصاب وراقب وعيه وتنفسه حتى وصول الإسعاف.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (2, 'اختناق', 3, 'عدم القدرة على الكلام أو التنفس أو السعال، إمساك المصاب برقبته بيديه، صوت صفير أو شهيق مرتفع، ازرقاق الوجه والشفتين.', 'اختناق عدم القدره علي الكلام او التنفس او السعال، امساك المصاب برقبته بيديه، صوت صفير او شهيق مرتفع، ازرقاق الوجه والشفتين.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 1, 'اسأل المصاب: هل تختنق؟ إذا كان يسعل بقوة فشجّعه على الاستمرار بالسعال ولا تتدخل.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 2, 'إذا لم يستطع التنفس أو الكلام أو السعال، فاتصل بالإسعاف أو اطلب من غيرك ذلك فوراً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 3, 'قف إلى جانب المصاب وأمِل جسمه إلى الأمام، ثم اضرب بين لوحَي الكتف بكعب يدك بقوة 5 ضربات.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 4, 'إذا لم يخرج الجسم: قف خلفه ولفّ ذراعيك حول بطنه، ضع قبضة يدك فوق السرة وأمسكها بيدك الأخرى، واضغط بقوة إلى الداخل وإلى الأعلى 5 مرات.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 5, 'بدّل بين 5 ضربات على الظهر و5 ضغطات على البطن حتى يخرج الجسم أو يفقد المصاب وعيه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 6, 'إذا فقد وعيه، أنزله برفق على الأرض وابدأ الإنعاش القلبي الرئوي مع استمرار طلب الإسعاف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 7, 'للرضيع (أقل من سنة): ضعه على ساعدك ووجهه للأسفل واضرب بين كتفيه 5 مرات، ثم اقلبه واضغط على منتصف صدره بإصبعين 5 مرات. لا تستخدم ضغطات البطن.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (3, 'نوبة قلبية وتوقف القلب', 3, 'ألم أو ضغط أو ثقل في الصدر قد يمتد إلى الذراع أو الفك أو الظهر، ضيق في التنفس، تعرق بارد، غثيان، شحوب، وقد يفقد المصاب وعيه ويتوقف تنفسه.', 'نوبه قلبيه وتوقف القلب الم او ضغط او ثقل في الصدر قد يمتد الي الذراع او الفك او الظهر، ضيق في التنفس، تعرق بارد، غثيان، شحوب، وقد يفقد المصاب وعيه ويتوقف تنفسه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 1, 'تأكد من أن المكان آمن، ثم نادِ المصاب وهزّ كتفيه برفق وتحقق من وعيه وتنفسه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 2, 'اتصل بالإسعاف فوراً أو اطلب من شخص آخر الاتصال وإحضار جهاز الصدمات الكهربائية (AED) إن وُجد.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 3, 'إذا كان المصاب واعياً: أجلسه بوضع مريح مستنداً إلى ظهره، وفكّ الملابس الضيقة، وابقَ معه وطمئنه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 4, 'إذا كان واعياً وليس لديه حساسية من الأسبرين ولم يمنعه طبيبه عنه، يمكنه مضغ قرص أسبرين 300 ملغ ببطء.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 5, 'إذا كان فاقد الوعي ولا يتنفس تنفساً طبيعياً: أرقده على ظهره على سطح صلب وابدأ فوراً بالضغطات الصدرية في منتصف الصدر بعمق 5 إلى 6 سم وبمعدل 100 إلى 120 ضغطة في الدقيقة.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 6, 'إن كنت مدرباً فأعطِ نفَسَي إنقاذ بعد كل 30 ضغطة، وإلا فاستمر بالضغطات وحدها.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 7, 'استمر دون توقف حتى وصول الإسعاف. وإذا توفر جهاز الصدمات فشغّله واتبع تعليماته الصوتية.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (4, 'لدغات الأفاعي والحشرات', 3, 'ألم وتورم واحمرار حول مكان اللدغة، علامتا ثقبين صغيرين في حالة الأفاعي، غثيان وقيء ودوخة، وفي الحالات الشديدة صعوبة في التنفس أو تورم في الوجه والشفتين.', 'لدغات الافاعي والحشرات الم وتورم واحمرار حول مكان اللدغه، علامتا ثقبين صغيرين في حاله الافاعي، غثيان وقيء ودوخه، وفي الحالات الشديده صعوبه في التنفس او تورم في الوجه والشفتين.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 1, 'أبعد المصاب عن مصدر اللدغة وأبقه هادئاً وساكناً قدر الإمكان.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 2, 'اتصل بالإسعاف فوراً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 3, 'ثبّت الطرف المصاب بمستوى القلب أو أسفل منه دون تحريكه، وانزع الخواتم والساعات قبل أن يتورم.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 4, 'اغسل مكان اللدغة برفق بالماء والصابون وغطّه بضماد نظيف وجاف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 5, 'لا تحاول مصّ السم، ولا تقطع الجرح، ولا تضع رباطاً ضاغطاً أو ثلجاً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 6, 'تذكّر شكل الأفعى أو الحشرة دون الاقتراب منها لإخبار الطبيب، وراقب تنفس المصاب ووعيه حتى وصول الإسعاف.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (5, 'غرق', 3, 'سعال أو اختناق، صعوبة في التنفس، ازرقاق الشفتين والأطراف، قيء، دوخة أو فقدان الوعي.', 'غرق سعال او اختناق، صعوبه في التنفس، ازرقاق الشفتين والاطراف، قيء، دوخه او فقدان الوعي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 1, 'اطلب النجدة واتصل بالإسعاف فوراً. لا تنزل إلى الماء إلا إذا كنت مدرباً، وحاول إنقاذه بمدّ عصا أو حبل أو طوق.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 2, 'أخرج المصاب من الماء وأرقده على ظهره على سطح ثابت.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 3, 'تحقق من تنفسه. إذا لم يكن يتنفس طبيعياً فابدأ الإنعاش القلبي الرئوي فوراً: ضغطات صدرية بمعدل 100 إلى 120 في الدقيقة، مع نفَسَي إنقاذ بعد كل 30 ضغطة إن كنت مدرباً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 4, 'إذا كان يتنفس لكنه فاقد الوعي، ضعه على جنبه (وضعية الإفاقة) وراقب تنفسه باستمرار.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 5, 'انزع ملابسه المبللة وغطّه بغطاء جاف ليبقى دافئاً. لا تحاول إخراج الماء من رئتيه بالضغط على بطنه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (5, 6, 'حتى لو بدا بخير بعد الإنقاذ، يجب نقله إلى المستشفى للفحص.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (6, 'ضربة الشمس والإجهاد الحراري', 3, 'حرارة مرتفعة في الجسم (أكثر من 40 درجة مئوية)، جلد ساخن وجاف أو متعرق، صداع شديد ودوخة، غثيان، تسارع النبض، تشوش أو فقدان الوعي.', 'ضربه الشمس والاجهاد الحراري حراره مرتفعه في الجسم (اكثر من 40 درجه مئويه)، جلد ساخن وجاف او متعرق، صداع شديد ودوخه، غثيان، تسارع النبض، تشوش او فقدان الوعي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 1, 'انقل المصاب فوراً إلى مكان ظليل أو بارد، واتصل بالإسعاف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 2, 'أرقده وارفع قدميه قليلاً، وانزع ملابسه الزائدة.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 3, 'برّد جسمه بسرعة: رشّه بماء بارد أو فاتر، وضع كمادات باردة على الرقبة والإبطين والفخذين، وحرّك الهواء حوله بمروحة.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 4, 'إذا كان واعياً تماماً فاسقه ماءً بارداً بجرعات صغيرة.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 5, 'لا تعطه أدوية خافضة للحرارة (باراسيتامول أو أسبرين) ولا مشروبات تحتوي على الكافيين أو الكحول.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (6, 6, 'راقب وعيه وتنفسه حتى وصول الإسعاف.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (7, 'تسمم (ابتلاع مادة سامة)', 3, 'غثيان وقيء، ألم في البطن، حروق أو احمرار حول الفم، نعاس أو تشوش، صعوبة في التنفس، وجود عبوة أو زجاجة فارغة قرب المصاب.', 'تسمم (ابتلاع ماده سامه) غثيان وقيء، الم في البطن، حروق او احمرار حول الفم، نعاس او تشوش، صعوبه في التنفس، وجود عبوه او زجاجه فارغه قرب المصاب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 1, 'اتصل بالإسعاف أو بمركز السموم فوراً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 2, 'حدّد المادة والكمية ووقت تناولها، واحتفظ بالعبوة لإظهارها للطبيب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 3, 'إذا كان واعياً فامسح بقايا المادة من فمه، ولا تعطه أي شيء إلا بتعليمات مركز السموم أو الطبيب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 4, 'لا تحاول إجباره على التقيؤ، ولا تعطه حليباً أو ماءً أو أي مادة مضادة إلا بتعليمات طبية.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 5, 'إذا كان فاقد الوعي ويتنفس فضعه على جنبه (وضعية الإفاقة). وإذا لم يتنفس فابدأ الإنعاش القلبي الرئوي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (7, 6, 'إذا لامست المادة الجلد أو العين، فاغسل المكان بماء جارٍ لمدة 15 إلى 20 دقيقة.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (8, 'حروق', 2, 'احمرار الجلد وألم، فقاعات مملوءة بسائل، جلد متقشر أو أبيض أو متفحم في الحروق العميقة، تورم في مكان الحرق.', 'حروق احمرار الجلد والم، فقاعات مملوءه بسائل، جلد متقشر او ابيض او متفحم في الحروق العميقه، تورم في مكان الحرق.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 1, 'أبعد المصاب عن مصدر الحرارة وأطفئ أي نار.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 2, 'برّد الحرق بماء جارٍ بارد أو فاتر (وليس مثلجاً) لمدة 10 إلى 20 دقيقة.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 3, 'انزع الخواتم والساعات والملابس الضيقة قرب الحرق قبل أن يتورم المكان، إلا ما كان ملتصقاً بالجلد.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 4, 'غطِّ الحرق بشاش نظيف أو غلاف بلاستيكي شفاف دون شدّ.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 5, 'لا تضع ثلجاً أو معجون أسنان أو زبدة أو زيتاً على الحرق، ولا تفقأ الفقاعات.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (8, 6, 'اتصل بالإسعاف إذا كان الحرق كبيراً أو عميقاً، أو في الوجه أو اليدين أو فوق مفصل، أو كان المصاب طفلاً، أو كان السبب مواد كيميائية أو كهرباء.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (9, 'كسور', 2, 'ألم شديد وتورم في مكان الإصابة، تشوه في شكل الطرف، عدم القدرة على تحريك الطرف أو تحميل الوزن عليه، كدمات، وأحياناً ظهور العظم من الجرح.', 'كسور الم شديد وتورم في مكان الاصابه، تشوه في شكل الطرف، عدم القدره علي تحريك الطرف او تحميل الوزن عليه، كدمات، واحيانا ظهور العظم من الجرح.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 1, 'أبقِ المصاب ساكناً ولا تحرّك الطرف المصاب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 2, 'اتصل بالإسعاف إذا كان الكسر مفتوحاً (عظم ظاهر)، أو في الرأس أو الرقبة أو الظهر أو الحوض أو الفخذ، أو كان هناك نزيف شديد.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 3, 'أوقف أي نزيف بالضغط بقطعة نظيفة حول الجرح دون الضغط على العظم البارز.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 4, 'ثبّت الطرف بالوضع الذي وُجد عليه بجبيرة بسيطة (لوح أو مجلة ملفوفة)، مع تثبيت المفصل فوق الكسر وتحته.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 5, 'ضع كمادة باردة ملفوفة بقماش على مكان الإصابة لتخفيف الألم والتورم.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (9, 6, 'لا تحاول إعادة العظم إلى مكانه.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (10, 'إغماء', 2, 'دوخة وشحوب، تعرق بارد، تشوش الرؤية، ثم فقدان الوعي لفترة قصيرة.', 'اغماء دوخه وشحوب، تعرق بارد، تشوش الرؤيه، ثم فقدان الوعي لفتره قصيره.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 1, 'أرقد المصاب على ظهره وارفع ساقيه نحو 30 سم فوق مستوى القلب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 2, 'فكّ أي ملابس ضيقة حول الرقبة والخصر، وتأكد من وجود هواء نقي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 3, 'تحقق من تنفسه. إذا لم يتنفس فاتصل بالإسعاف وابدأ الإنعاش القلبي الرئوي.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 4, 'عندما يستعيد وعيه اتركه يستلقي بضع دقائق، ثم ساعده على الجلوس ببطء.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 5, 'لا تعطه طعاماً أو شراباً وهو فاقد الوعي، ولا ترشّ وجهه بالماء ولا تصفعه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (10, 6, 'اتصل بالإسعاف إذا لم يستعد وعيه خلال دقيقة، أو إذا كان مصاباً بمرض في القلب، أو حاملاً، أو أُصيب أثناء السقوط.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (11, 'نوبة صرع وتشنجات', 2, 'تشنجات وارتجاف عنيف في الجسم، فقدان الوعي، عضّ اللسان، فقدان السيطرة على البول، وارتباك بعد انتهاء النوبة.', 'نوبه صرع وتشنجات تشنجات وارتجاف عنيف في الجسم، فقدان الوعي، عض اللسان، فقدان السيطره علي البول، وارتباك بعد انتهاء النوبه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (11, 1, 'أبعد الأشياء الصلبة أو الحادة عن المصاب وضع شيئاً ناعماً تحت رأسه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (11, 2, 'دوّن وقت بدء النوبة. اتصل بالإسعاف إذا استمرت أكثر من 5 دقائق، أو تكررت، أو كانت أول نوبة له.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (11, 3, 'فكّ الملابس الضيقة حول رقبته.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (11, 4, 'لا تحاول تثبيته أو إيقاف حركته، ولا تضع أي شيء في فمه أبداً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (11, 5, 'بعد انتهاء التشنجات ضعه على جنبه (وضعية الإفاقة) وابقَ معه حتى يستعيد وعيه تماماً.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (12, 'جرح سطحي', 1, 'نزيف بسيط، ألم موضعي، خدش أو شق سطحي في الجلد.', 'جرح سطحي نزيف بسيط، الم موضعي، خدش او شق سطحي في الجلد.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 1, 'اغسل يديك بالماء والصابون قبل لمس الجرح.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 2, 'اضغط على الجرح بقطعة قماش نظيفة حتى يتوقف النزيف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 3, 'اشطف الجرح بماء جارٍ نظيف لإزالة الأوساخ.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 4, 'جفّف حول الجرح برفق وغطّه بضماد أو شاش نظيف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 5, 'لا تصبّ الكحول أو الماء الأكسجيني داخل الجرح.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (12, 6, 'بدّل الضماد يومياً وراقب أي احمرار أو تورم أو صديد، وراجع الطبيب إذا ظهرت أو كان الجرح عميقاً أو متسخاً جداً.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (13, 'رعاف (نزيف الأنف)', 1, 'نزول دم من فتحة أو فتحتي الأنف، وقد يصاحبه شعور بالدم في الحلق.', 'رعاف (نزيف الانف) نزول دم من فتحه او فتحتي الانف، وقد يصاحبه شعور بالدم في الحلق.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (13, 1, 'أجلس المصاب ومِل رأسه إلى الأمام قليلاً (وليس إلى الخلف).');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (13, 2, 'اضغط على الجزء الليّن من الأنف بإصبعين لمدة 10 دقائق دون إفلات.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (13, 3, 'اجعله يتنفس من فمه، وضع كمادة باردة على جسر الأنف.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (13, 4, 'إذا استمر النزيف أكثر من 20 دقيقة، أو كان بعد إصابة في الرأس، فاطلب مساعدة طبية.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (13, 5, 'بعد توقف النزيف لا يفرك أنفه ولا ينفخه لعدة ساعات.');
INSERT INTO emergencies (case_id, title, danger_level, symptoms, search_text) VALUES (14, 'التواء المفاصل', 1, 'ألم وتورم حول المفصل، كدمة، صعوبة في تحريك المفصل أو الاستناد عليه.', 'التواء المفاصل الم وتورم حول المفصل، كدمه، صعوبه في تحريك المفصل او الاستناد عليه.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (14, 1, 'أوقف النشاط وأرح المفصل المصاب.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (14, 2, 'ضع كمادة باردة ملفوفة بقماش لمدة 15 إلى 20 دقيقة كل ساعتين تقريباً.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (14, 3, 'لفّ المفصل برباط ضاغط مرن دون شدّ مفرط.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (14, 4, 'ارفع الطرف المصاب فوق مستوى القلب إن أمكن.');
INSERT INTO steps (case_id, step_number, instruction_text) VALUES (14, 5, 'راجع الطبيب إذا لم يستطع المصاب الاستناد على الطرف، أو استمر الألم والتورم، أو ظهر تشوه في المفصل.');
COMMIT;
PRAGMA user_version = 1;
