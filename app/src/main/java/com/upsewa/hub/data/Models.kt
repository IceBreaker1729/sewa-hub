package com.upsewa.hub.data

/**
 * One government service the user can open.
 * All strings are stored bilingually so we can render either language
 * without re-fetching anything.
 */
data class Service(
    val id: String,
    val categoryId: String,
    val popular: Boolean,
    val icon: String,            // single emoji
    val nameEn: String,
    val nameHi: String,
    val descEn: String,
    val descHi: String,
    val url: String,             // official URL — opened in in-app WebView
    val usesEn: List<String>,
    val usesHi: List<String>,
    val howEn: List<String>,
    val howHi: List<String>,
    val docs: List<String> = emptyList()
) {
    fun name(lang: String) = if (lang == "hi") nameHi else nameEn
    fun desc(lang: String) = if (lang == "hi") descHi else descEn
    fun uses(lang: String) = if (lang == "hi") usesHi else usesEn
    fun how(lang: String)  = if (lang == "hi") howHi  else howEn
}

data class Category(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val descEn: String,
    val descHi: String,
    val icon: String
) {
    fun name(lang: String) = if (lang == "hi") nameHi else nameEn
    fun desc(lang: String) = if (lang == "hi") descHi else descEn
}

object Catalog {

    val categories: List<Category> = listOf(
        Category("land",      "Land & Property",   "भूमि एवं संपत्ति",   "Khatauni, naksha, registration",   "खतौनी, नक्शा, रजिस्ट्री",     "🏞️"),
        Category("cert",      "Certificates",      "प्रमाण पत्र",        "Income, caste, domicile, birth",   "आय, जाति, निवास, जन्म",        "📜"),
        Category("grievance", "Grievances",        "शिकायत",             "Jansunwai, CM helpline",           "जनसुनवाई, सीएम हेल्पलाइन",     "📣"),
        Category("transport", "Transport",         "परिवहन",             "DL, RC, vehicle services",         "डीएल, आरसी, वाहन सेवाएँ",     "🚗"),
        Category("law",       "Legal & Courts",    "कानून एवं न्यायालय", "Case status, caveat, judgments",   "केस स्थिति, कैविएट, निर्णय",   "⚖️"),
        Category("ration",    "Ration & Food",     "राशन एवं खाद्य",     "Ration card, PDS",                 "राशन कार्ड, पीडीएस",          "🍚"),
        Category("police",    "Police",            "पुलिस",              "e-FIR, character verification",    "ई-एफआईआर, चरित्र सत्यापन",    "🛡️"),
        Category("edu",       "Education",         "शिक्षा",             "Scholarships, results",            "छात्रवृत्ति, परिणाम",          "🎓"),
        Category("power",     "Electricity",       "बिजली",              "UPPCL bills, complaints",          "बिजली बिल, शिकायत",           "💡"),
        Category("jobs",      "Employment",        "रोजगार",             "UPSSSC, govt jobs",                "यूपीएसएसएससी, सरकारी नौकरी",  "💼"),
        Category("health",    "Health",            "स्वास्थ्य",          "Ayushman, hospital info",          "आयुष्मान, अस्पताल",           "🏥"),
        Category("misc",      "Other Services",    "अन्य सेवाएँ",        "Misc UP & central services",       "विविध सेवाएँ",                "🧰"),
    )

    val services: List<Service> = listOf(
        // ---------- LAND & PROPERTY ----------
        Service("bhulekh", "land", true, "📋",
            "UP Bhulekh — Land Records", "यूपी भूलेख — भूमि अभिलेख",
            "Check khatauni, gata number, ownership of any land in UP.",
            "उत्तर प्रदेश में किसी भी भूमि की खतौनी, गाटा संख्या व मालिकाना देखें।",
            "https://upbhulekh.gov.in/",
            listOf("View khasra/khatauni online","Verify land ownership","Get extract of records","Check land area & type"),
            listOf("खसरा/खतौनी देखें","भूमि का मालिकाना सत्यापन","अभिलेख का विवरण","क्षेत्रफल व प्रकार"),
            listOf("Open the portal","Select your district → tehsil → village","Enter khasra/gata or owner name","View & download the record"),
            listOf("पोर्टल खोलें","जनपद → तहसील → गाँव चुनें","खसरा/गाटा या मालिक का नाम भरें","अभिलेख देखें व डाउनलोड करें"),
            listOf("Khasra No.","Village name")),

        Service("bhunaksha", "land", false, "🗺️",
            "Bhu Naksha UP — Land Maps", "भू-नक्शा यूपी — भूमि का नक्शा",
            "View rural cadastral maps and plot boundaries.",
            "ग्रामीण क्षेत्रों के कैडस्ट्रल नक्शे व प्लॉट सीमाएँ देखें।",
            "https://upbhunaksha.gov.in/",
            listOf("See plot location on map","Check shape & neighbours","Download map for legal use"),
            listOf("प्लॉट को मानचित्र पर देखें","आकार व सीमाएँ","कानूनी उपयोग हेतु डाउनलोड"),
            listOf("Choose district/tehsil/village","Click on the plot or enter gata number","View map & download PDF"),
            listOf("जनपद/तहसील/गाँव चुनें","प्लॉट पर क्लिक करें या गाटा भरें","PDF डाउनलोड करें"),
            listOf("Gata No.")),

        Service("igrsup", "land", true, "🏛️",
            "IGRSUP — Property Registration", "आईजीआरएसयूपी — संपत्ति पंजीकरण",
            "Property registration, stamp duty calculator, encumbrance certificate, marriage registration.",
            "संपत्ति पंजीकरण, स्टांप शुल्क कैलकुलेटर, भार-मुक्त प्रमाण पत्र, विवाह पंजीकरण।",
            "https://igrsup.gov.in/",
            listOf("Calculate stamp duty","Book appointment for sale-deed","Search registered property","Encumbrance certificate","Marriage registration"),
            listOf("स्टांप शुल्क गणना","विक्रय पत्र अपॉइंटमेंट","पंजीकृत संपत्ति खोज","भार-मुक्त प्रमाण","विवाह पंजीकरण"),
            listOf("Choose service from menu","Fill property/personal details","Pay fees online","Visit sub-registrar on slot"),
            listOf("मेनू से सेवा चुनें","विवरण भरें","ऑनलाइन शुल्क जमा","निर्धारित दिन उप-निबंधक कार्यालय जाएँ"),
            listOf("Aadhaar","Property papers","PAN")),

        Service("uprera", "land", false, "🏢",
            "UP RERA — Real Estate Regulator", "यूपी रेरा — रियल एस्टेट प्राधिकरण",
            "Check project & agent registration; file complaint against builder.",
            "परियोजना व एजेंट पंजीकरण देखें; बिल्डर के विरुद्ध शिकायत।",
            "https://www.up-rera.in/",
            listOf("Verify a real estate project","Check builder/agent RERA ID","File complaint against developer"),
            listOf("परियोजना सत्यापन","बिल्डर/एजेंट RERA आईडी","डेवलपर पर शिकायत"),
            listOf("Search project by name/RERA no.","Open registration card","Use 'Complaint' menu if needed"),
            listOf("परियोजना खोजें","पंजीकरण विवरण देखें","आवश्यक हो तो शिकायत दर्ज करें"),
            listOf("Builder name","RERA No.")),

        // ---------- CERTIFICATES ----------
        Service("edistrict", "cert", true, "🪪",
            "e-District UP — Certificates", "ई-डिस्ट्रिक्ट — प्रमाण पत्र",
            "Apply for income, caste, domicile, character, birth & death certificates.",
            "आय, जाति, निवास, चरित्र, जन्म व मृत्यु प्रमाण पत्र के लिए आवेदन।",
            "https://edistrict.up.gov.in/",
            listOf("Income certificate","Caste certificate (SC/ST/OBC)","Domicile / Residence certificate","Character certificate","Birth / Death certificate"),
            listOf("आय प्रमाण पत्र","जाति प्रमाण पत्र","निवास प्रमाण पत्र","चरित्र प्रमाण पत्र","जन्म / मृत्यु प्रमाण पत्र"),
            listOf("Register as citizen","Choose certificate type","Upload Aadhaar/proof","Pay nominal fee","Download after 3–7 working days"),
            listOf("नागरिक रजिस्टर करें","प्रमाण पत्र चुनें","आधार/प्रमाण अपलोड करें","शुल्क जमा करें","3–7 कार्यदिवस में डाउनलोड"),
            listOf("Aadhaar","Address proof","Ration card","Self-declaration")),

        Service("birthdeath", "cert", false, "👶",
            "Birth & Death Certificate (CRS)", "जन्म व मृत्यु प्रमाण पत्र (CRS)",
            "Central Civil Registration System for birth/death registration in UP.",
            "जन्म/मृत्यु पंजीकरण हेतु केंद्रीय नागरिक पंजीकरण प्रणाली।",
            "https://crsorgi.gov.in/",
            listOf("Register a birth within 21 days","Register a death","Download digital certificate"),
            listOf("21 दिन में जन्म पंजीकरण","मृत्यु पंजीकरण","डिजिटल प्रमाण पत्र डाउनलोड"),
            listOf("Create user account","Fill event details","Upload hospital slip / proof","Submit & track"),
            listOf("खाता बनाएँ","विवरण भरें","अस्पताल पर्ची अपलोड","सबमिट करें"),
            listOf("Hospital slip","Parent Aadhaar")),

        // ---------- GRIEVANCE ----------
        Service("jansunwai", "grievance", true, "📞",
            "Jansunwai — CM Samadhan", "जनसुनवाई — सीएम समाधान",
            "Lodge complaint with any UP govt department; track resolution.",
            "किसी भी विभाग के विरुद्ध शिकायत दर्ज करें व स्थिति देखें।",
            "https://jansunwai.up.nic.in/",
            listOf("File complaint vs govt official","Track existing complaint","Send reminder","Submit suggestion"),
            listOf("शिकायत दर्ज करें","स्थिति देखें","अनुस्मारक भेजें","सुझाव दें"),
            listOf("Click 'Complaint Registration'","OTP-verify mobile","Choose department & describe issue","Note your complaint ID"),
            listOf("शिकायत पंजीकरण पर क्लिक","OTP सत्यापन","विभाग चुनें व समस्या लिखें","शिकायत ID सुरक्षित रखें"),
            listOf("Mobile number","Aadhaar (optional)")),

        Service("cpgrams", "grievance", false, "🇮🇳",
            "CPGRAMS — Central Grievance", "CPGRAMS — केंद्रीय शिकायत",
            "Complaints against central govt ministries & PSUs.",
            "केंद्र सरकार के मंत्रालयों/PSU के विरुद्ध शिकायत।",
            "https://pgportal.gov.in/",
            listOf("Complaint against central dept.","PSU complaints","Track grievance"),
            listOf("केंद्रीय विभाग पर शिकायत","PSU पर शिकायत","स्थिति देखें"),
            listOf("Register on portal","Pick ministry","Submit complaint","Get registration no."),
            listOf("पोर्टल पर पंजीकरण","मंत्रालय चुनें","शिकायत दर्ज करें","रजिस्ट्रेशन नंबर लें"),
            listOf("Email","Mobile")),

        // ---------- TRANSPORT ----------
        Service("parivahan", "transport", true, "🚙",
            "Parivahan Sewa — DL & RC", "परिवहन सेवा — डीएल व आरसी",
            "Driving licence, vehicle registration, ownership transfer, tax payment.",
            "ड्राइविंग लाइसेंस, वाहन पंजीकरण, स्वामित्व स्थानांतरण, कर भुगतान।",
            "https://parivahan.gov.in/parivahan/",
            listOf("Apply / renew DL","Vehicle RC services","Ownership transfer","Pay road tax","Duplicate documents"),
            listOf("DL आवेदन / नवीनीकरण","आरसी सेवाएँ","स्वामित्व स्थानांतरण","रोड टैक्स","डुप्लीकेट दस्तावेज़"),
            listOf("Select state: Uttar Pradesh","Choose Sarathi (DL) or Vahan (vehicle)","Aadhaar e-KYC","Pay fees & book slot if needed"),
            listOf("राज्य: उत्तर प्रदेश चुनें","Sarathi (DL) या Vahan (वाहन)","आधार ई-केवाईसी","शुल्क जमा करें"),
            listOf("Aadhaar","Address proof","RC / DL")),

        Service("mparivahan", "transport", false, "📱",
            "mParivahan — DL/RC verify", "एमपरिवहन — डीएल/आरसी सत्यापन",
            "Verify any vehicle's RC details from registration number.",
            "पंजीकरण संख्या से किसी भी वाहन का RC विवरण सत्यापित करें।",
            "https://parivahan.gov.in/parivahan/en/content/m-parivahan",
            listOf("Check vehicle owner","Insurance & PUC status","Vehicle age & class"),
            listOf("वाहन मालिक","बीमा व PUC","वाहन की आयु व श्रेणी"),
            listOf("Enter vehicle number","View RC summary"),
            listOf("वाहन संख्या भरें","सारांश देखें"),
            listOf("Vehicle number")),

        // ---------- LEGAL & COURTS ----------
        Service("ecourts", "law", true, "⚖️",
            "eCourts — Case Status", "ईकोर्ट्स — केस स्थिति",
            "Search case status, orders, cause-list across all UP district courts.",
            "यूपी के सभी जिला न्यायालयों में केस स्थिति, आदेश, कॉज़-लिस्ट खोजें।",
            "https://services.ecourts.gov.in/ecourtindia_v6/",
            listOf("Search by CNR / case no.","Search by party name","Search by FIR no.","Download orders","Daily cause list"),
            listOf("CNR / केस नंबर से खोज","पक्षकार नाम से","FIR नंबर से","आदेश डाउनलोड","दैनिक कॉज़ लिस्ट"),
            listOf("Choose state: Uttar Pradesh","Pick district court","Use Case Status menu","Enter detail & captcha"),
            listOf("राज्य: उत्तर प्रदेश","जनपद न्यायालय चुनें","केस स्टेटस मेनू","विवरण भरें"),
            listOf("CNR or Case No.")),

        Service("caveat", "law", false, "📑",
            "Caveat Search — eCourts", "कैविएट खोज — ईकोर्ट्स",
            "Search caveats filed in any court across India.",
            "भारत के किसी भी न्यायालय में दर्ज कैविएट खोजें।",
            "https://services.ecourts.gov.in/ecourtindia_v6/?p=caveat_search/index",
            listOf("Verify caveat filed against you","Check pending caveats"),
            listOf("आप पर कैविएट सत्यापन","लंबित कैविएट देखें"),
            listOf("Pick state & court","Enter party / advocate name","Submit"),
            listOf("राज्य व न्यायालय चुनें","पक्षकार/अधिवक्ता नाम","सबमिट"),
            listOf("Party name")),

        Service("hcallahabad", "law", true, "🏛️",
            "Allahabad High Court", "इलाहाबाद उच्च न्यायालय",
            "UP High Court case status, daily orders, judgments & e-filing.",
            "यूपी उच्च न्यायालय की केस स्थिति, आदेश, निर्णय व ई-फाइलिंग।",
            "https://www.allahabadhighcourt.in/",
            listOf("HC case status","Search judgments","e-Filing","Cause list"),
            listOf("हाईकोर्ट केस स्थिति","निर्णय खोज","ई-फाइलिंग","कॉज़ लिस्ट"),
            listOf("Use Case Status link","Enter case type / number / year","View status & orders"),
            listOf("केस स्थिति लिंक","केस प्रकार/नंबर/वर्ष","स्थिति व आदेश देखें"),
            listOf("Case No.","Year")),

        Service("districtcourtup", "law", false, "🏤",
            "UP District Courts", "यूपी जिला न्यायालय",
            "Directory & services of all UP district courts.",
            "यूपी के सभी जिला न्यायालयों की निर्देशिका व सेवाएँ।",
            "https://districts.ecourts.gov.in/up",
            listOf("Find your district court","Contact details","Local case status"),
            listOf("जिला न्यायालय खोज","संपर्क विवरण","केस स्थिति"),
            listOf("Pick district","Open court website","Use case status / cause list"),
            listOf("जनपद चुनें","साइट खोलें","केस स्थिति देखें"),
            emptyList()),

        // ---------- RATION ----------
        Service("fcsup", "ration", true, "🍱",
            "FCS UP — Ration Card", "FCS यूपी — राशन कार्ड",
            "Apply for ration card; check eligibility & beneficiary list.",
            "राशन कार्ड हेतु आवेदन; पात्रता व लाभार्थी सूची।",
            "https://fcs.up.gov.in/",
            listOf("New ration card application","Check beneficiary list","Locate fair-price shop","File complaint"),
            listOf("नया राशन कार्ड","लाभार्थी सूची","कोटेदार खोज","शिकायत"),
            listOf("Use 'Ration card related → Beneficiary list'","Pick district → block → village","Search by name / card no."),
            listOf("'राशन कार्ड' मेनू","जनपद → ब्लॉक → गाँव","नाम/कार्ड नंबर से खोज"),
            listOf("Aadhaar","Family photo","Address proof")),

        // ---------- POLICE ----------
        Service("uppolice", "police", true, "👮",
            "UP Police Citizen Services", "यूपी पुलिस नागरिक सेवाएँ",
            "Lost-item report, character verification, employee verification, FIR view.",
            "गुमशुदा प्रतिवेदन, चरित्र सत्यापन, कर्मचारी सत्यापन, FIR देखें।",
            "https://uppolice.gov.in/",
            listOf("File e-FIR / lost item","Character verification","Tenant / employee verification","View FIR copy"),
            listOf("ई-एफआईआर / गुमशुदा","चरित्र सत्यापन","किरायेदार/कर्मचारी सत्यापन","FIR की प्रति"),
            listOf("Open 'Citizen Services'","Register & verify mobile","Pick the service","Submit details"),
            listOf("'नागरिक सेवाएँ' खोलें","मोबाइल सत्यापन","सेवा चुनें","विवरण भरें"),
            listOf("Aadhaar","Photo","Address proof")),

        // ---------- EDUCATION ----------
        Service("upscholarship", "edu", true, "🎓",
            "UP Scholarship & Fee Reimbursement", "यूपी छात्रवृत्ति व शुल्क प्रतिपूर्ति",
            "Pre/post-matric scholarship for SC/ST/OBC/General students of UP.",
            "उत्तर प्रदेश के SC/ST/OBC/सामान्य छात्रों हेतु प्री/पोस्ट-मैट्रिक छात्रवृत्ति।",
            "https://scholarship.up.gov.in/",
            listOf("Apply for scholarship","Renew application","Check payment status","Download passbook"),
            listOf("छात्रवृत्ति आवेदन","नवीनीकरण","भुगतान स्थिति","पासबुक डाउनलोड"),
            listOf("Register as fresh / renewal","Fill institute & bank details","Upload certificates","Submit to institute"),
            listOf("नया/नवीनीकरण रजिस्ट्रेशन","संस्था व बैंक विवरण","प्रमाण पत्र अपलोड","संस्था को सबमिट"),
            listOf("Income cert.","Caste cert.","Bank passbook","Aadhaar","Last marksheet")),

        Service("upmsp", "edu", false, "📚",
            "UP Board (UPMSP) — Results", "यूपी बोर्ड — परिणाम",
            "UP Madhyamik Shiksha Parishad — 10th/12th results, dates, schemes.",
            "यूपी माध्यमिक शिक्षा परिषद् — 10वीं/12वीं परिणाम, तिथियाँ।",
            "https://upmsp.edu.in/",
            listOf("Check 10th/12th result","Download marksheet","See exam schedule"),
            listOf("10/12 परिणाम","मार्कशीट डाउनलोड","परीक्षा कार्यक्रम"),
            listOf("Click result link","Enter roll number","View / print result"),
            listOf("रिजल्ट लिंक","रोल नंबर भरें","देखें / प्रिंट"),
            listOf("Roll number")),

        // ---------- ELECTRICITY ----------
        Service("uppcl", "power", true, "⚡",
            "UPPCL — Electricity Bill", "यूपीपीसीएल — बिजली बिल",
            "Pay/view electricity bill, new connection, fault complaint.",
            "बिजली बिल भुगतान, नया कनेक्शन, फॉल्ट शिकायत।",
            "https://www.uppcl.org/",
            listOf("Pay bill online","Download bill","New connection","Lodge fault complaint","Name / address change"),
            listOf("बिल भुगतान","बिल डाउनलोड","नया कनेक्शन","फॉल्ट शिकायत","नाम/पता परिवर्तन"),
            listOf("Choose Urban/Rural discom","Enter account number","Pay via UPI / card"),
            listOf("शहरी/ग्रामीण विद्युत खंड","खाता संख्या","UPI/कार्ड से भुगतान"),
            listOf("Account / connection no.")),

        // ---------- EMPLOYMENT ----------
        Service("upsssc", "jobs", true, "📝",
            "UPSSSC — Govt Jobs", "यूपीएसएसएससी — सरकारी नौकरी",
            "Recruitment notifications & one-time registration for UP group-C jobs.",
            "उत्तर प्रदेश की ग्रुप-C नौकरियों हेतु सूचना व वन-टाइम रजिस्ट्रेशन।",
            "https://upsssc.gov.in/",
            listOf("See active vacancies","One-time registration (OTR)","Download admit card","Result & cut-off"),
            listOf("रिक्तियाँ देखें","वन-टाइम रजिस्ट्रेशन","प्रवेश पत्र","परिणाम व कट-ऑफ"),
            listOf("Complete OTR (mandatory)","Apply for current advt.","Pay fee","Download admit card before exam"),
            listOf("OTR भरें","विज्ञापन के लिए आवेदन","शुल्क जमा","प्रवेश पत्र डाउनलोड"),
            listOf("OTR No.","Photo","Signature","Caste/EWS cert.")),

        Service("sewayojan", "jobs", false, "💼",
            "Sewayojan — UP Rojgar Mela", "सेवायोजन — रोजगार मेला",
            "Register for govt-organised job fairs & private vacancies in UP.",
            "यूपी के रोजगार मेलों व निजी रिक्तियों हेतु पंजीकरण।",
            "https://sewayojan.up.nic.in/",
            listOf("Job-seeker registration","See fairs near you","Apply to private postings"),
            listOf("जॉब सीकर रजिस्ट्रेशन","नज़दीकी मेले","निजी रिक्तियों पर आवेदन"),
            listOf("Register with mobile","Upload qualifications","Apply to fairs"),
            listOf("मोबाइल से रजिस्टर","योग्यता अपलोड","मेले में आवेदन"),
            listOf("Aadhaar","Qualification certs.")),

        Service("uppsc", "jobs", false, "🏅",
            "UPPSC — State PCS Exams", "यूपीपीएससी — राज्य पीसीएस",
            "UP Public Service Commission notifications & applications.",
            "यूपी लोक सेवा आयोग की अधिसूचना व आवेदन।",
            "https://uppsc.up.nic.in/",
            listOf("UP-PCS apply","Result & key","Interview schedule"),
            listOf("यूपी-पीसीएस आवेदन","परिणाम व उत्तर-कुंजी","साक्षात्कार"),
            listOf("OTR registration","Choose exam","Pay fee & submit"),
            listOf("OTR रजिस्ट्रेशन","परीक्षा चुनें","शुल्क व सबमिट"),
            listOf("Photo","Signature","Degree certificate")),

        // ---------- HEALTH ----------
        Service("pmjay", "health", false, "🩺",
            "PM-JAY / Ayushman Bharat", "पीएम-जय / आयुष्मान भारत",
            "Check Ayushman card eligibility & download card.",
            "आयुष्मान कार्ड पात्रता जाँचें व डाउनलोड करें।",
            "https://beneficiary.nha.gov.in/",
            listOf("Check eligibility by Aadhaar","Download Ayushman card","Find empanelled hospital"),
            listOf("आधार से पात्रता","आयुष्मान कार्ड डाउनलोड","सूचीबद्ध अस्पताल खोज"),
            listOf("Login with Aadhaar OTP","Verify family","Download e-card"),
            listOf("आधार OTP लॉगिन","परिवार सत्यापन","ई-कार्ड डाउनलोड"),
            listOf("Aadhaar","Ration card")),

        // ---------- MISC ----------
        Service("digilocker", "misc", true, "🗄️",
            "DigiLocker — Digital Documents", "डिजीलॉकर — डिजिटल दस्तावेज़",
            "Govt-issued digital wallet for Aadhaar, DL, PAN, marksheets etc.",
            "आधार, डीएल, पैन, मार्कशीट आदि के लिए सरकारी डिजिटल वॉलेट।",
            "https://www.digilocker.gov.in/",
            listOf("Store govt documents","Share verified copies","Fetch RC / DL / marksheets"),
            listOf("दस्तावेज़ संग्रह","सत्यापित प्रति साझा","RC/DL/मार्कशीट लोड"),
            listOf("Sign up with Aadhaar","Add documents from issuer","Use the e-share link as needed"),
            listOf("आधार से रजिस्टर","जारीकर्ता से दस्तावेज़ जोड़ें","साझा करें"),
            listOf("Aadhaar","Mobile linked to Aadhaar")),

        Service("umang", "misc", false, "📲",
            "UMANG — 1700+ Govt Services", "उमंग — 1700+ सरकारी सेवाएँ",
            "Central super-app for all govt schemes & services in one ID.",
            "सभी सरकारी योजनाओं/सेवाओं के लिए केंद्रीय सुपर-ऐप।",
            "https://web.umang.gov.in/",
            listOf("EPF, PAN, gas booking","Aadhaar services","State + central services"),
            listOf("EPF, PAN, गैस","आधार सेवाएँ","राज्य व केंद्र सेवाएँ"),
            listOf("Register with mobile + MPIN","Use the search bar","Open any service"),
            listOf("मोबाइल + MPIN से रजिस्टर","खोज बार","कोई भी सेवा खोलें"),
            listOf("Mobile","Aadhaar")),

        Service("aadhaar", "misc", false, "🆔",
            "UIDAI — Aadhaar Services", "UIDAI — आधार सेवाएँ",
            "Update address, mobile, download Aadhaar, lock biometrics.",
            "आधार में पता/मोबाइल अपडेट, डाउनलोड, बायोमेट्रिक लॉक।",
            "https://uidai.gov.in/",
            listOf("Download e-Aadhaar","Update demographic data","Locate Aadhaar centre","Lock / unlock biometrics"),
            listOf("ई-आधार डाउनलोड","विवरण अपडेट","केंद्र खोज","बायोमेट्रिक लॉक/अनलॉक"),
            listOf("My Aadhaar menu → Service","Login with OTP","Follow on-screen steps"),
            listOf("My Aadhaar → सेवा","OTP लॉगिन","निर्देशों का पालन"),
            listOf("Aadhaar","Mobile linked")),
    )

    fun byCategory(catId: String) = services.filter { it.categoryId == catId }
    fun byId(id: String) = services.firstOrNull { it.id == id }
    fun categoryOf(svc: Service) = categories.firstOrNull { it.id == svc.categoryId }

    fun search(query: String, lang: String): List<Service> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) return emptyList()
        return services.filter {
            it.nameEn.lowercase().contains(needle) ||
            it.nameHi.contains(needle) ||
            it.descEn.lowercase().contains(needle) ||
            it.descHi.contains(needle) ||
            it.uses(lang).any { u -> u.lowercase().contains(needle) } ||
            it.id.contains(needle)
        }
    }
}
