package fm.smart.r1;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.content.Intent;

public class Utils {
	public static final Hashtable<String, String> POS_MAP = new Hashtable<String, String>();
	static {
		POS_MAP.put("Adjective", "A"); // delicious, 美味しい
		POS_MAP.put("Adverb", "D"); // relatively, あっさり
		POS_MAP.put("Auxillary verb", "VA"); // shall
		POS_MAP.put("Conjunction", "J"); // because, さて
		POS_MAP.put("Interrogative", "INT"); // why
		POS_MAP.put("Noun", "N"); // boat, 船
		POS_MAP.put("Phrasal Verb", "PH"); // "keep up with"
		POS_MAP.put("Phrase", "E"); // "give someone a ride"
		POS_MAP.put("Preposition", "PR"); // around
		POS_MAP.put("Pronoun", "NR"); // you
		POS_MAP.put("Verb", "V"); // eat, 食べる
		POS_MAP.put("Interjection", "I"); // ah
		POS_MAP.put("Noun Abbreviation", "NA"); // NASA
		POS_MAP.put("Proper Noun", "NP"); // Portugal
		POS_MAP.put("Particle", "PL"); // the
		POS_MAP.put("Verbal Noun", "VN"); // 勉強 (Japanese suru-verbs)
		POS_MAP.put("Adjectival Noun", "AN"); // にぎやか (Japanese na-adjectives)
		POS_MAP.put("Kana", "KANA"); // Special class used for items
		// representing the hiragana and
		// katakana characters in Japanese
		POS_MAP.put("None", "NONE"); // This part of speech is used when any of
		// the other parts of speech does not
		// apply to the item or if the item
		// creator is unsure what part of speech
		// to use
		POS_MAP.put("Prefix", "PX"); // un-
		POS_MAP.put("Suffix", "SX"); // -ing
		POS_MAP.put("Determiner", "DT"); // the, a, an
	}

	public static final Hashtable<String, String> INV_LANGUAGE_MAP = new Hashtable<String, String>();
	static {
		INV_LANGUAGE_MAP.put("ab", "Abkhazian");
		INV_LANGUAGE_MAP.put("aa", "Afar");
		INV_LANGUAGE_MAP.put("af", "Afrikaans");
		INV_LANGUAGE_MAP.put("ain", "Ainu");
		INV_LANGUAGE_MAP.put("ak", "Akan");
		INV_LANGUAGE_MAP.put("sq", "Albanian");
		INV_LANGUAGE_MAP.put("am", "Amharic");
		INV_LANGUAGE_MAP.put("grc", "Ancient Greek");
		INV_LANGUAGE_MAP.put("ar", "Arabic");
		INV_LANGUAGE_MAP.put("an", "Aragonese");
		INV_LANGUAGE_MAP.put("arc", "Aramaic");
		INV_LANGUAGE_MAP.put("hy", "Armenian");
		INV_LANGUAGE_MAP.put("as", "Assamese");
		INV_LANGUAGE_MAP.put("av", "Avaric");
		INV_LANGUAGE_MAP.put("ae", "Avestan");
		INV_LANGUAGE_MAP.put("ay", "Aymara");
		INV_LANGUAGE_MAP.put("az", "Azerbaijani");
		INV_LANGUAGE_MAP.put("bm", "Bambara");
		INV_LANGUAGE_MAP.put("ba", "Bashkir");
		INV_LANGUAGE_MAP.put("eu", "Basque");
		INV_LANGUAGE_MAP.put("be", "Belarusian");
		INV_LANGUAGE_MAP.put("bn", "Bengali");
		INV_LANGUAGE_MAP.put("bi", "Bislama");
		INV_LANGUAGE_MAP.put("bs", "Bosnian");
		INV_LANGUAGE_MAP.put("br", "Breton");
		INV_LANGUAGE_MAP.put("bg", "Bulgarian");
		INV_LANGUAGE_MAP.put("my", "Burmese");
		INV_LANGUAGE_MAP.put("zh-HK", "Cantonese");
		INV_LANGUAGE_MAP.put("ca", "Catalan");
		INV_LANGUAGE_MAP.put("ch", "Chamorro");
		INV_LANGUAGE_MAP.put("ce", "Chechen");
		INV_LANGUAGE_MAP.put("ny", "Chichewa; Nyanja");
		INV_LANGUAGE_MAP.put("cv", "Chuvash");
		INV_LANGUAGE_MAP.put("kw", "Cornish");
		INV_LANGUAGE_MAP.put("co", "Corsican");
		INV_LANGUAGE_MAP.put("cr", "Cree");
		INV_LANGUAGE_MAP.put("hr", "Croatian");
		INV_LANGUAGE_MAP.put("cs", "Czech");
		INV_LANGUAGE_MAP.put("da", "Danish");
		INV_LANGUAGE_MAP.put("dv", "Divehi");
		INV_LANGUAGE_MAP.put("nl", "Dutch");
		INV_LANGUAGE_MAP.put("dz", "Dzongkha");
		INV_LANGUAGE_MAP.put("en", "English");
		INV_LANGUAGE_MAP.put("eo", "Esperanto");
		INV_LANGUAGE_MAP.put("et", "Estonian");
		INV_LANGUAGE_MAP.put("ee", "Ewe");
		INV_LANGUAGE_MAP.put("fo", "Faroese");
		INV_LANGUAGE_MAP.put("fj", "Fijian");
		INV_LANGUAGE_MAP.put("fi", "Finnish");
		INV_LANGUAGE_MAP.put("fr", "French");
		INV_LANGUAGE_MAP.put("fy", "Frisian");
		INV_LANGUAGE_MAP.put("ff", "Fulah");
		INV_LANGUAGE_MAP.put("gd", "Gaelic");
		INV_LANGUAGE_MAP.put("gl", "Gallegan");
		INV_LANGUAGE_MAP.put("lg", "Ganda");
		INV_LANGUAGE_MAP.put("ka", "Georgian");
		INV_LANGUAGE_MAP.put("de", "German");
		INV_LANGUAGE_MAP.put("el", "Greek");
		INV_LANGUAGE_MAP.put("gn", "Guarani");
		INV_LANGUAGE_MAP.put("gu", "Gujarati");
		INV_LANGUAGE_MAP.put("ht", "Haitian; Haitian Creole");
		INV_LANGUAGE_MAP.put("ha", "Hausa");
		INV_LANGUAGE_MAP.put("he", "Hebrew");
		INV_LANGUAGE_MAP.put("hz", "Herero");
		INV_LANGUAGE_MAP.put("hi", "Hindi");
		INV_LANGUAGE_MAP.put("ho", "Hiri Motu");
		INV_LANGUAGE_MAP.put("hu", "Hungarian");
		INV_LANGUAGE_MAP.put("is", "Icelandic");
		INV_LANGUAGE_MAP.put("io", "Ido");
		INV_LANGUAGE_MAP.put("ig", "Igbo");
		INV_LANGUAGE_MAP.put("id", "Indonesian");
		INV_LANGUAGE_MAP.put("ia", "Interlingua");
		INV_LANGUAGE_MAP.put("iu", "Inuktitut");
		INV_LANGUAGE_MAP.put("ik", "Inupiaq");
		INV_LANGUAGE_MAP.put("ga", "Irish");
		INV_LANGUAGE_MAP.put("it", "Italian");
		INV_LANGUAGE_MAP.put("ja", "Japanese");
		INV_LANGUAGE_MAP.put("jv", "Javanese");
		INV_LANGUAGE_MAP.put("kl", "Kalaallisut");
		INV_LANGUAGE_MAP.put("kn", "Kannada");
		INV_LANGUAGE_MAP.put("kr", "Kanuri");
		INV_LANGUAGE_MAP.put("ks", "Kashmiri");
		INV_LANGUAGE_MAP.put("kk", "Kazakh");
		INV_LANGUAGE_MAP.put("km", "Khmer");
		INV_LANGUAGE_MAP.put("ki", "Kikuyu");
		INV_LANGUAGE_MAP.put("rw", "Kinyarwanda");
		INV_LANGUAGE_MAP.put("ky", "Kirghiz");
		INV_LANGUAGE_MAP.put("tlh", "Klingon");
		INV_LANGUAGE_MAP.put("kv", "Komi");
		INV_LANGUAGE_MAP.put("kg", "Kongo");
		INV_LANGUAGE_MAP.put("ko", "Korean");
		INV_LANGUAGE_MAP.put("kj", "Kuanyama");
		INV_LANGUAGE_MAP.put("ku", "Kurdish");
		INV_LANGUAGE_MAP.put("lo", "Lao");
		INV_LANGUAGE_MAP.put("ojp", "Old Japanese");
		INV_LANGUAGE_MAP.put("la", "Latin");
		INV_LANGUAGE_MAP.put("lv", "Latvian");
		INV_LANGUAGE_MAP.put("lb", "Letzeburgesch");
		INV_LANGUAGE_MAP.put("li", "Limburgish");
		INV_LANGUAGE_MAP.put("ln", "Lingala");
		INV_LANGUAGE_MAP.put("lt", "Lithuanian");
		INV_LANGUAGE_MAP.put("lu", "Luba-Katanga");
		INV_LANGUAGE_MAP.put("mk", "Macedonian");
		INV_LANGUAGE_MAP.put("mg", "Malagasy");
		INV_LANGUAGE_MAP.put("ms", "Malay");
		INV_LANGUAGE_MAP.put("ml", "Malayalam");
		INV_LANGUAGE_MAP.put("mt", "Maltese");
		INV_LANGUAGE_MAP.put("zh-Hans", "Mandarin Sim");
		INV_LANGUAGE_MAP.put("zh-Hant", "Mandarin Trd");
		INV_LANGUAGE_MAP.put("gv", "Manx");
		INV_LANGUAGE_MAP.put("mi", "Maori");
		INV_LANGUAGE_MAP.put("mr", "Marathi");
		INV_LANGUAGE_MAP.put("mh", "Marshall");
		INV_LANGUAGE_MAP.put("mo", "Moldavian");
		INV_LANGUAGE_MAP.put("mn", "Mongolian");
		INV_LANGUAGE_MAP.put("na", "Nauru");
		INV_LANGUAGE_MAP.put("nv", "Navajo");
		INV_LANGUAGE_MAP.put("nd", "Ndebele, North");
		INV_LANGUAGE_MAP.put("nr", "Ndebele, South");
		INV_LANGUAGE_MAP.put("ng", "Ndonga");
		INV_LANGUAGE_MAP.put("ne", "Nepali");
		INV_LANGUAGE_MAP.put("se", "Northern Sami");
		INV_LANGUAGE_MAP.put("no", "Norwegian");
		INV_LANGUAGE_MAP.put("oc", "Occitan");
		INV_LANGUAGE_MAP.put("oj", "Ojibwa");
		INV_LANGUAGE_MAP.put("och", "Old Chinese");
		INV_LANGUAGE_MAP.put("ang", "Old English");
		INV_LANGUAGE_MAP.put("or", "Oriya");
		INV_LANGUAGE_MAP.put("om", "Oromo");
		INV_LANGUAGE_MAP.put("os", "Ossetian; Ossetic");
		INV_LANGUAGE_MAP.put("pa", "Panjabi");
		INV_LANGUAGE_MAP.put("fa", "Persian");
		INV_LANGUAGE_MAP.put("pl", "Polish");
		INV_LANGUAGE_MAP.put("pt", "Portuguese");
		INV_LANGUAGE_MAP.put("ps", "Pushto");
		INV_LANGUAGE_MAP.put("qu", "Quechua");
		INV_LANGUAGE_MAP.put("qya", "Quenya");
		INV_LANGUAGE_MAP.put("rm", "Raeto-Romance");
		INV_LANGUAGE_MAP.put("ro", "Romanian");
		INV_LANGUAGE_MAP.put("rn", "Rundi");
		INV_LANGUAGE_MAP.put("ru", "Russian");
		INV_LANGUAGE_MAP.put("sm", "Samoan");
		INV_LANGUAGE_MAP.put("sg", "Sango");
		INV_LANGUAGE_MAP.put("sa", "Sanskrit");
		INV_LANGUAGE_MAP.put("sc", "Sardinian");
		INV_LANGUAGE_MAP.put("sr", "Serbian");
		INV_LANGUAGE_MAP.put("sn", "Shona");
		INV_LANGUAGE_MAP.put("sjn", "Sindarin");
		INV_LANGUAGE_MAP.put("sd", "Sindhi");
		INV_LANGUAGE_MAP.put("si", "Sinhalese");
		INV_LANGUAGE_MAP.put("cu", "Slavic");
		INV_LANGUAGE_MAP.put("sk", "Slovak");
		INV_LANGUAGE_MAP.put("sl", "Slovenian");
		INV_LANGUAGE_MAP.put("so", "Somali");
		INV_LANGUAGE_MAP.put("st", "Sotho");
		INV_LANGUAGE_MAP.put("es", "Spanish");
		INV_LANGUAGE_MAP.put("su", "Sundanese");
		INV_LANGUAGE_MAP.put("sw", "Swahili");
		INV_LANGUAGE_MAP.put("ss", "Swati");
		INV_LANGUAGE_MAP.put("sv", "Swedish");
		INV_LANGUAGE_MAP.put("tl", "Tagalog");
		INV_LANGUAGE_MAP.put("ty", "Tahitian");
		INV_LANGUAGE_MAP.put("zh-TW", "Taiwanese");
		INV_LANGUAGE_MAP.put("tg", "Tajik");
		INV_LANGUAGE_MAP.put("ta", "Tamil");
		INV_LANGUAGE_MAP.put("tt", "Tatar");
		INV_LANGUAGE_MAP.put("te", "Telugu");
		INV_LANGUAGE_MAP.put("th", "Thai");
		INV_LANGUAGE_MAP.put("bo", "Tibetan");
		INV_LANGUAGE_MAP.put("ti", "Tigrinya");
		INV_LANGUAGE_MAP.put("to", "Tonga");
		INV_LANGUAGE_MAP.put("ts", "Tsonga");
		INV_LANGUAGE_MAP.put("tn", "Tswana");
		INV_LANGUAGE_MAP.put("tr", "Turkish");
		INV_LANGUAGE_MAP.put("tk", "Turkmen");
		INV_LANGUAGE_MAP.put("tw", "Twi");
		INV_LANGUAGE_MAP.put("ug", "Uighur");
		INV_LANGUAGE_MAP.put("uk", "Ukrainian");
		INV_LANGUAGE_MAP.put("ur", "Urdu");
		INV_LANGUAGE_MAP.put("uz", "Uzbek");
		INV_LANGUAGE_MAP.put("ve", "Venda");
		INV_LANGUAGE_MAP.put("vi", "Vietnamese");
		INV_LANGUAGE_MAP.put("vo", "Volapük");
		INV_LANGUAGE_MAP.put("wa", "Walloon");
		INV_LANGUAGE_MAP.put("cy", "Welsh");
		INV_LANGUAGE_MAP.put("wo", "Wolof");
		INV_LANGUAGE_MAP.put("xh", "Xhosa");
		INV_LANGUAGE_MAP.put("ii", "Yi");
		INV_LANGUAGE_MAP.put("yi", "Yiddish");
		INV_LANGUAGE_MAP.put("za", "Zhuang");
		INV_LANGUAGE_MAP.put("zu", "Zulu");
	};
	public static final List<String> POPULAR_LANGUAGES = new Vector<String>();
	static {
		POPULAR_LANGUAGES.add("English");// 511626
		POPULAR_LANGUAGES.add("Japanese");// 399475
		POPULAR_LANGUAGES.add("French");// 35248
		POPULAR_LANGUAGES.add("Mandarin Trd");// 28063
		POPULAR_LANGUAGES.add("Spanish");// 24330
		POPULAR_LANGUAGES.add("German");// 23147
		POPULAR_LANGUAGES.add("Italian");// 15382
		POPULAR_LANGUAGES.add("Korean");// 13787
		POPULAR_LANGUAGES.add("Swedish");// 6967
		POPULAR_LANGUAGES.add("Czech");// 6807
		POPULAR_LANGUAGES.add("Finnish");// 4599
		POPULAR_LANGUAGES.add("Esperanto");// 3975
		POPULAR_LANGUAGES.add("Old Japanese");// 3621
		POPULAR_LANGUAGES.add("Polish");// 3146
		POPULAR_LANGUAGES.add("Portuguese");// 3088
		POPULAR_LANGUAGES.add("Thai");// 2979
		POPULAR_LANGUAGES.add("Irish");// 2798
		POPULAR_LANGUAGES.add("Russian");// 2651
		POPULAR_LANGUAGES.add("Latin");// 2561
		POPULAR_LANGUAGES.add("Romanian");// 2353
		POPULAR_LANGUAGES.add("Vietnamese");// 2310
		POPULAR_LANGUAGES.add("Norwegian");// 1654
		POPULAR_LANGUAGES.add("Dutch");// 1643
		POPULAR_LANGUAGES.add("Mandarin Sim");// 1609
		POPULAR_LANGUAGES.add("Indonesian");// 1453
		POPULAR_LANGUAGES.add("Arabic");// 1220
		POPULAR_LANGUAGES.add("Hungarian");// 1188
		POPULAR_LANGUAGES.add("Cantonese");// 1130
	}
	public static final Hashtable<String, String> LANGUAGE_MAP = new Hashtable<String, String>();
	static {
		LANGUAGE_MAP.put("Abkhazian", "ab");
		LANGUAGE_MAP.put("Afar", "aa");
		LANGUAGE_MAP.put("Afrikaans", "af");
		LANGUAGE_MAP.put("Ainu", "ain");
		LANGUAGE_MAP.put("Akan", "ak");
		LANGUAGE_MAP.put("Albanian", "sq");
		LANGUAGE_MAP.put("Amharic", "am");
		LANGUAGE_MAP.put("Ancient Greek", "grc");
		LANGUAGE_MAP.put("Arabic", "ar");
		LANGUAGE_MAP.put("Aragonese", "an");
		LANGUAGE_MAP.put("Aramaic", "arc");
		LANGUAGE_MAP.put("Armenian", "hy");
		LANGUAGE_MAP.put("Assamese", "as");
		LANGUAGE_MAP.put("Avaric", "av");
		LANGUAGE_MAP.put("Avestan", "ae");
		LANGUAGE_MAP.put("Aymara", "ay");
		LANGUAGE_MAP.put("Azerbaijani", "az");
		LANGUAGE_MAP.put("Bambara", "bm");
		LANGUAGE_MAP.put("Bashkir", "ba");
		LANGUAGE_MAP.put("Basque", "eu");
		LANGUAGE_MAP.put("Belarusian", "be");
		LANGUAGE_MAP.put("Bengali", "bn");
		LANGUAGE_MAP.put("Bislama", "bi");
		LANGUAGE_MAP.put("Bosnian", "bs");
		LANGUAGE_MAP.put("Breton", "br");
		LANGUAGE_MAP.put("Bulgarian", "bg");
		LANGUAGE_MAP.put("Burmese", "my");
		LANGUAGE_MAP.put("Cantonese", "zh-HK");
		LANGUAGE_MAP.put("Catalan", "ca");
		LANGUAGE_MAP.put("Chamorro", "ch");
		LANGUAGE_MAP.put("Chechen", "ce");
		LANGUAGE_MAP.put("Chichewa; Nyanja", "ny");
		LANGUAGE_MAP.put("Chuvash", "cv");
		LANGUAGE_MAP.put("Cornish", "kw");
		LANGUAGE_MAP.put("Corsican", "co");
		LANGUAGE_MAP.put("Cree", "cr");
		LANGUAGE_MAP.put("Croatian", "hr");
		LANGUAGE_MAP.put("Czech", "cs");
		LANGUAGE_MAP.put("Danish", "da");
		LANGUAGE_MAP.put("Divehi", "dv");
		LANGUAGE_MAP.put("Dutch", "nl");
		LANGUAGE_MAP.put("Dzongkha", "dz");
		LANGUAGE_MAP.put("English", "en");
		LANGUAGE_MAP.put("Esperanto", "eo");
		LANGUAGE_MAP.put("Estonian", "et");
		LANGUAGE_MAP.put("Ewe", "ee");
		LANGUAGE_MAP.put("Faroese", "fo");
		LANGUAGE_MAP.put("Fijian", "fj");
		LANGUAGE_MAP.put("Finnish", "fi");
		LANGUAGE_MAP.put("French", "fr");
		LANGUAGE_MAP.put("Frisian", "fy");
		LANGUAGE_MAP.put("Fulah", "ff");
		LANGUAGE_MAP.put("Gaelic", "gd");
		LANGUAGE_MAP.put("Gallegan", "gl");
		LANGUAGE_MAP.put("Ganda", "lg");
		LANGUAGE_MAP.put("Georgian", "ka");
		LANGUAGE_MAP.put("German", "de");
		LANGUAGE_MAP.put("Greek", "el");
		LANGUAGE_MAP.put("Guarani", "gn");
		LANGUAGE_MAP.put("Gujarati", "gu");
		LANGUAGE_MAP.put("Haitian; Haitian Creole", "ht");
		LANGUAGE_MAP.put("Hausa", "ha");
		LANGUAGE_MAP.put("Hebrew", "he");
		LANGUAGE_MAP.put("Herero", "hz");
		LANGUAGE_MAP.put("Hindi", "hi");
		LANGUAGE_MAP.put("Hiri Motu", "ho");
		LANGUAGE_MAP.put("Hungarian", "hu");
		LANGUAGE_MAP.put("Icelandic", "is");
		LANGUAGE_MAP.put("Ido", "io");
		LANGUAGE_MAP.put("Igbo", "ig");
		LANGUAGE_MAP.put("Indonesian", "id");
		LANGUAGE_MAP.put("Interlingua", "ia");
		LANGUAGE_MAP.put("Inuktitut", "iu");
		LANGUAGE_MAP.put("Inupiaq", "ik");
		LANGUAGE_MAP.put("Irish", "ga");
		LANGUAGE_MAP.put("Italian", "it");
		LANGUAGE_MAP.put("Japanese", "ja");
		LANGUAGE_MAP.put("Javanese", "jv");
		LANGUAGE_MAP.put("Kalaallisut", "kl");
		LANGUAGE_MAP.put("Kannada", "kn");
		LANGUAGE_MAP.put("Kanuri", "kr");
		LANGUAGE_MAP.put("Kashmiri", "ks");
		LANGUAGE_MAP.put("Kazakh", "kk");
		LANGUAGE_MAP.put("Khmer", "km");
		LANGUAGE_MAP.put("Kikuyu", "ki");
		LANGUAGE_MAP.put("Kinyarwanda", "rw");
		LANGUAGE_MAP.put("Kirghiz", "ky");
		LANGUAGE_MAP.put("Klingon", "tlh");
		LANGUAGE_MAP.put("Komi", "kv");
		LANGUAGE_MAP.put("Kongo", "kg");
		LANGUAGE_MAP.put("Korean", "ko");
		LANGUAGE_MAP.put("Kuanyama", "kj");
		LANGUAGE_MAP.put("Kurdish", "ku");
		LANGUAGE_MAP.put("Lao", "lo");
		LANGUAGE_MAP.put("Old Japanese", "ojp");
		LANGUAGE_MAP.put("Latin", "la");
		LANGUAGE_MAP.put("Latvian", "lv");
		LANGUAGE_MAP.put("Letzeburgesch", "lb");
		LANGUAGE_MAP.put("Limburgish", "li");
		LANGUAGE_MAP.put("Lingala", "ln");
		LANGUAGE_MAP.put("Lithuanian", "lt");
		LANGUAGE_MAP.put("Luba-Katanga", "lu");
		LANGUAGE_MAP.put("Macedonian", "mk");
		LANGUAGE_MAP.put("Malagasy", "mg");
		LANGUAGE_MAP.put("Malay", "ms");
		LANGUAGE_MAP.put("Malayalam", "ml");
		LANGUAGE_MAP.put("Maltese", "mt");
		LANGUAGE_MAP.put("Mandarin Sim", "zh-Hans");
		LANGUAGE_MAP.put("Mandarin Trd", "zh-Hant");
		LANGUAGE_MAP.put("Manx", "gv");
		LANGUAGE_MAP.put("Maori", "mi");
		LANGUAGE_MAP.put("Marathi", "mr");
		LANGUAGE_MAP.put("Marshall", "mh");
		LANGUAGE_MAP.put("Moldavian", "mo");
		LANGUAGE_MAP.put("Mongolian", "mn");
		LANGUAGE_MAP.put("Nauru", "na");
		LANGUAGE_MAP.put("Navajo", "nv");
		LANGUAGE_MAP.put("Ndebele, North", "nd");
		LANGUAGE_MAP.put("Ndebele, South", "nr");
		LANGUAGE_MAP.put("Ndonga", "ng");
		LANGUAGE_MAP.put("Nepali", "ne");
		LANGUAGE_MAP.put("Northern Sami", "se");
		LANGUAGE_MAP.put("Norwegian", "no");
		LANGUAGE_MAP.put("Occitan", "oc");
		LANGUAGE_MAP.put("Ojibwa", "oj");
		LANGUAGE_MAP.put("Old Chinese", "och");
		LANGUAGE_MAP.put("Old English", "ang");
		LANGUAGE_MAP.put("Oriya", "or");
		LANGUAGE_MAP.put("Oromo", "om");
		LANGUAGE_MAP.put("Ossetian; Ossetic", "os");
		LANGUAGE_MAP.put("Panjabi", "pa");
		LANGUAGE_MAP.put("Persian", "fa");
		LANGUAGE_MAP.put("Polish", "pl");
		LANGUAGE_MAP.put("Portuguese", "pt");
		LANGUAGE_MAP.put("Pushto", "ps");
		LANGUAGE_MAP.put("Quechua", "qu");
		LANGUAGE_MAP.put("Quenya", "qya");
		LANGUAGE_MAP.put("Raeto-Romance", "rm");
		LANGUAGE_MAP.put("Romanian", "ro");
		LANGUAGE_MAP.put("Rundi", "rn");
		LANGUAGE_MAP.put("Russian", "ru");
		LANGUAGE_MAP.put("Samoan", "sm");
		LANGUAGE_MAP.put("Sango", "sg");
		LANGUAGE_MAP.put("Sanskrit", "sa");
		LANGUAGE_MAP.put("Sardinian", "sc");
		LANGUAGE_MAP.put("Serbian", "sr");
		LANGUAGE_MAP.put("Shona", "sn");
		LANGUAGE_MAP.put("Sindarin", "sjn");
		LANGUAGE_MAP.put("Sindhi", "sd");
		LANGUAGE_MAP.put("Sinhalese", "si");
		LANGUAGE_MAP.put("Slavic", "cu");
		LANGUAGE_MAP.put("Slovak", "sk");
		LANGUAGE_MAP.put("Slovenian", "sl");
		LANGUAGE_MAP.put("Somali", "so");
		LANGUAGE_MAP.put("Sotho", "st");
		LANGUAGE_MAP.put("Spanish", "es");
		LANGUAGE_MAP.put("Sundanese", "su");
		LANGUAGE_MAP.put("Swahili", "sw");
		LANGUAGE_MAP.put("Swati", "ss");
		LANGUAGE_MAP.put("Swedish", "sv");
		LANGUAGE_MAP.put("Tagalog", "tl");
		LANGUAGE_MAP.put("Tahitian", "ty");
		LANGUAGE_MAP.put("Taiwanese", "zh-TW");
		LANGUAGE_MAP.put("Tajik", "tg");
		LANGUAGE_MAP.put("Tamil", "ta");
		LANGUAGE_MAP.put("Tatar", "tt");
		LANGUAGE_MAP.put("Telugu", "te");
		LANGUAGE_MAP.put("Thai", "th");
		LANGUAGE_MAP.put("Tibetan", "bo");
		LANGUAGE_MAP.put("Tigrinya", "ti");
		LANGUAGE_MAP.put("Tonga", "to");
		LANGUAGE_MAP.put("Tsonga", "ts");
		LANGUAGE_MAP.put("Tswana", "tn");
		LANGUAGE_MAP.put("Turkish", "tr");
		LANGUAGE_MAP.put("Turkmen", "tk");
		LANGUAGE_MAP.put("Twi", "tw");
		LANGUAGE_MAP.put("Uighur", "ug");
		LANGUAGE_MAP.put("Ukrainian", "uk");
		LANGUAGE_MAP.put("Urdu", "ur");
		LANGUAGE_MAP.put("Uzbek", "uz");
		LANGUAGE_MAP.put("Venda", "ve");
		LANGUAGE_MAP.put("Vietnamese", "vi");
		LANGUAGE_MAP.put("Volapük", "vo");
		LANGUAGE_MAP.put("Walloon", "wa");
		LANGUAGE_MAP.put("Welsh", "cy");
		LANGUAGE_MAP.put("Wolof", "wo");
		LANGUAGE_MAP.put("Xhosa", "xh");
		LANGUAGE_MAP.put("Yi", "ii");
		LANGUAGE_MAP.put("Yiddish", "yi");
		LANGUAGE_MAP.put("Zhuang", "za");
		LANGUAGE_MAP.put("Zulu", "zu");
	};

	public static boolean isIdeographicLanguage(String code) {
		if (code != null
				&& (code.equals("ja") || code.equals("cn")
						|| code.equals("ojp") || code.equals("och") || code
						.startsWith("zh"))) {
			return true;
		}
		return false;
	}

	public static void putExtra(Intent intent, String key, String value) {
		if (value != null && value.length() > 0) {
			intent.putExtra(key, value);
		}
	}

}
