package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.QuizType;
import app.entities.Country;
import app.use_cases.country.CountryDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * A concrete implementation of QuestionRepository that provides all quiz
 * questions available in the application. This repository loads:
 * <ul>
 *   <li>Manually written static questions (capitals, flags, languages, currencies)</li>
 *   <li>API-generated flag MCQ questions (using country flag URLs)</li>
 *   <li>API-generated flag type-in questions</li>
 * </ul>
 */
public final class LocalQuestionRepository implements QuestionRepository {
    private final List<Question> allQuestions = new ArrayList<>();
    private final Random random = new Random();
    private final CountryDataAccessInterface countryDataAccess;

    /**
     * Creates a LocalQuestionRepository, loads all manual questions,
     * and loads flag questions using live API data.
     *
     * @param countryDataAccess a data-access object used for fetching country names and flag URLs
     */
    public LocalQuestionRepository(CountryDataAccessInterface countryDataAccess) {
        this.countryDataAccess = countryDataAccess;
        loadManualQuestions();
        loadFlagQuestionsFromApi();
        loadFlagTypeInQuestionsFromApi();
    }

    /**
     * Loads all manually written questions into memory.
     * These include capitals, reverse-capitals, flags, languages, currencies, etc.
     */
    private void loadManualQuestions() {
        // ---- CAPITALS - MCQ ----
        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of France?",
                List.of("Paris","Rome","Berlin","Madrid"),
                "Paris",
                List.of(),
                "France's capital city is Paris.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Japan?",
                List.of("Tokyo", "Osaka", "Kyoto", "Nagoya"),
                "Tokyo",
                List.of(),
                "Tokyo has been Japan's capital since 1869.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Canada?",
                List.of("Ottawa", "Toronto", "Vancouver", "Montreal"),
                "Ottawa",
                List.of(),
                "Ottawa is the political capital of Canada.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Australia?",
                List.of("Canberra", "Sydney", "Melbourne", "Brisbane"),
                "Canberra",
                List.of(),
                "Canberra was chosen as a compromise between Sydney and Melbourne.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Brazil?",
                List.of("Brasília", "Rio de Janeiro", "São Paulo", "Salvador"),
                "Brasília",
                List.of("brasilia"),
                "Brasília became Brazil's capital in 1960.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of China?",
                List.of("Beijing", "Shanghai", "Guangzhou", "Shenzhen"),
                "Beijing",
                List.of(),
                "Beijing is China's capital and a major cultural center.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Egypt?",
                List.of("Cairo", "Alexandria", "Giza", "Luxor"),
                "Cairo",
                List.of(),
                "Cairo is the largest city in the Arab world.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of Germany?",
                List.of("Berlin", "Munich", "Hamburg", "Frankfurt"),
                "Berlin",
                List.of(),
                "Berlin is Germany's capital and cultural hub.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of India?",
                List.of("New Delhi", "Mumbai", "Bangalore", "Kolkata"),
                "New Delhi",
                List.of("delhi"),
                "New Delhi is the official capital of India.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of South Korea?",
                List.of("Seoul", "Busan", "Incheon", "Daegu"),
                "Seoul",
                List.of(),
                "Seoul is the largest city in South Korea.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "What is the capital of the United Kingdom?",
                List.of("London", "Manchester", "Birmingham", "Edinburgh"),
                "London",
                List.of(),
                "London has been the capital of the UK since 1707.",
                null
        ));

        // ---- CAPITALS (reverse) - MCQ ----
        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Tokyo as its capital?",
                List.of("Japan", "China", "South Korea", "Thailand"),
                "Japan",
                List.of(),
                "Tokyo is the capital city of Japan.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Ottawa as its capital?",
                List.of("Canada", "United States", "Australia", "United Kingdom"),
                "Canada",
                List.of(),
                "Ottawa is the political capital of Canada.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Cairo as its capital?",
                List.of("Egypt", "Morocco", "Saudi Arabia", "Sudan"),
                "Egypt",
                List.of(),
                "Cairo is the capital and largest city of Egypt.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Berlin as its capital?",
                List.of("Germany", "Austria", "Belgium", "Switzerland"),
                "Germany",
                List.of(),
                "Berlin is the capital of Germany.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Beijing as its capital?",
                List.of("China", "Japan", "Vietnam", "Singapore"),
                "China",
                List.of(),
                "Beijing is the capital of China.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Brasília as its capital?",
                List.of("Brazil", "Argentina", "Colombia", "Chile"),
                "Brazil",
                List.of("brasilia"),
                "Brasília became Brazil's capital in 1960.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Canberra as its capital?",
                List.of("Australia", "New Zealand", "United Kingdom", "South Africa"),
                "Australia",
                List.of(),
                "Canberra is the capital of Australia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has New Delhi as its capital?",
                List.of("India", "Pakistan", "Bangladesh", "Sri Lanka"),
                "India",
                List.of("delhi"),
                "New Delhi is the official capital of India.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has Seoul as its capital?",
                List.of("South Korea", "Japan", "North Korea", "China"),
                "South Korea",
                List.of(),
                "Seoul is the capital of South Korea.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.MCQ,
                "Which country has London as its capital?",
                List.of("United Kingdom", "Ireland", "France", "Belgium"),
                "United Kingdom",
                List.of("uk", "britain", "england", "great britain"),
                "London has been the capital of the United Kingdom since 1707.",
                null
        ));

        // ---- LANGUAGES - MCQ ----

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Spain?",
                List.of("Spanish", "Portuguese", "Catalan", "French"),
                "Spanish",
                List.of("spanish"),
                "Spanish (Castilian) is the official national language of Spain.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Egypt?",
                List.of("Arabic", "Hebrew", "Turkish", "Persian"),
                "Arabic",
                List.of("arabic"),
                "Egypt's official language is Arabic.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Russia?",
                List.of("Russian", "Ukrainian", "Polish", "Belarusian"),
                "Russian",
                List.of("russian"),
                "Russian is the official language of the Russian Federation.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Turkey?",
                List.of("Turkish", "Arabic", "Greek", "Kurdish"),
                "Turkish",
                List.of("turkish"),
                "Turkish is the official language of Turkey.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in South Korea?",
                List.of("Korean", "Mandarin", "Japanese", "Cantonese"),
                "Korean",
                List.of("korean"),
                "Korean is the official language of South Korea.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Mexico?",
                List.of("Spanish", "Portuguese", "Mayan", "English"),
                "Spanish",
                List.of("spanish"),
                "Spanish is spoken by the vast majority of Mexico's population.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Iran?",
                List.of("Persian", "Arabic", "Turkish", "Pashto"),
                "Persian",
                List.of("persian", "farsi"),
                "Iran's official language is Persian (Farsi).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Brazil?",
                List.of("Portuguese", "Spanish", "French", "Italian"),
                "Portuguese",
                List.of("portuguese"),
                "Brazil is the largest Portuguese-speaking country in the world.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in the Netherlands?",
                List.of("Dutch", "German", "Danish", "English"),
                "Dutch",
                List.of("dutch"),
                "Dutch is the official language of the Netherlands.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Kenya?",
                List.of("Swahili", "English", "Amharic", "Zulu"),
                "Swahili",
                List.of("swahili"),
                "Kenya's national languages are Swahili and English.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Italy?",
                List.of("Italian", "Spanish", "French", "Portuguese"),
                "Italian",
                List.of("italian"),
                "Italian is the official language of Italy.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in China?",
                List.of("Mandarin Chinese", "Cantonese", "Japanese", "Korean"),
                "Mandarin Chinese",
                List.of("mandarin", "chinese", "mandarin chinese"),
                "Mandarin Chinese is the official language of China.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Saudi Arabia?",
                List.of("Arabic", "Persian", "Turkish", "Urdu"),
                "Arabic",
                List.of("arabic"),
                "Arabic is the official language of Saudi Arabia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Greece?",
                List.of("Greek", "Turkish", "Italian", "Bulgarian"),
                "Greek",
                List.of("greek"),
                "Greek is the official language of Greece.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Thailand?",
                List.of("Thai", "Lao", "Khmer", "Vietnamese"),
                "Thai",
                List.of("thai"),
                "Thai is the official language of Thailand.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Vietnam?",
                List.of("Vietnamese", "Thai", "Khmer", "Mandarin Chinese"),
                "Vietnamese",
                List.of("vietnamese"),
                "Vietnamese is the official language of Vietnam.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Indonesia?",
                List.of("Indonesian", "Malay", "Javanese", "Tagalog"),
                "Indonesian",
                List.of("indonesian", "bahasa indonesia"),
                "Indonesian (Bahasa Indonesia) is the official language of Indonesia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Pakistan?",
                List.of("Urdu", "Hindi", "Punjabi", "Persian"),
                "Urdu",
                List.of("urdu"),
                "Urdu is the national language of Pakistan.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Ethiopia?",
                List.of("Amharic", "Tigrinya", "Somali", "Oromo"),
                "Amharic",
                List.of("amharic"),
                "Amharic is the official working language of Ethiopia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.MCQ,
                "Which language is primarily spoken in Nigeria?",
                List.of("English", "Hausa", "Yoruba", "Igbo"),
                "English",
                List.of("english"),
                "English is the official language of Nigeria, used in government and education.",
                null
        ));


        // ---- CURRENCIES - MCQ ----

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of Japan?",
                List.of("Yen", "Won", "Yuan", "Ringgit"),
                "Yen",
                List.of("yen", "japanese yen", "jpy"),
                "Japan's currency is the Japanese yen (JPY).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of the United Kingdom?",
                List.of("Pound sterling", "Euro", "Dollar", "Krone"),
                "Pound sterling",
                List.of("pound", "pound sterling", "gbp"),
                "The UK's currency is the pound sterling (GBP).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of the United States?",
                List.of("US Dollar", "Canadian Dollar", "Euro", "Peso"),
                "US Dollar",
                List.of("usd", "us dollar", "dollar"),
                "The United States uses the US Dollar (USD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of India?",
                List.of("Rupee", "Yen", "Rupiah", "Lira"),
                "Rupee",
                List.of("indian rupee", "inr", "rupee"),
                "India's currency is the Indian rupee (INR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of China?",
                List.of("Yuan", "Yen", "Won", "Baht"),
                "Yuan",
                List.of("yuan", "renminbi", "cny"),
                "China's currency is the renminbi (yuan), abbreviated as CNY.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of Brazil?",
                List.of("Real", "Peso", "Escudo", "Cruzeiro"),
                "Real",
                List.of("real", "brazilian real", "brl"),
                "Brazil's currency is the Brazilian real (BRL).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of South Korea?",
                List.of("Won", "Yen", "Yuan", "Dollar"),
                "Won",
                List.of("won", "south korean won", "krw"),
                "South Korea uses the South Korean won (KRW).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of Switzerland?",
                List.of("Swiss Franc", "Euro", "Krone", "Pound"),
                "Swiss Franc",
                List.of("swiss franc", "chf"),
                "Switzerland uses the Swiss franc (CHF).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of Australia?",
                List.of("Australian Dollar", "New Zealand Dollar", "Pound", "Rand"),
                "Australian Dollar",
                List.of("aud", "australian dollar"),
                "Australia uses the Australian dollar (AUD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "What is the currency of Mexico?",
                List.of("Mexican Peso", "Brazilian Real", "Lira", "Dollar"),
                "Mexican Peso",
                List.of("peso", "mexican peso", "mxn"),
                "Mexico uses the Mexican peso (MXN).",
                null
        ));

        // ---- CURRENCIES (reverse) - MCQ ----
        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Euro as its currency?",
                List.of("Germany", "Denmark", "Sweden", "Poland"),
                "Germany",
                List.of("germany"),
                "Germany is one of the countries that use the Euro (EUR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Canadian dollar as its currency?",
                List.of("Canada", "United States", "Australia", "New Zealand"),
                "Canada",
                List.of("canada"),
                "Canada uses the Canadian dollar (CAD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the New Zealand dollar as its currency?",
                List.of("New Zealand", "Australia", "South Africa", "United Kingdom"),
                "New Zealand",
                List.of("new zealand"),
                "New Zealand uses the New Zealand dollar (NZD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Russian ruble as its currency?",
                List.of("Russia", "Ukraine", "Belarus", "Kazakhstan"),
                "Russia",
                List.of("russia", "russian federation"),
                "Russia uses the Russian ruble (RUB).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Turkish lira as its currency?",
                List.of("Turkey", "Egypt", "Saudi Arabia", "Iran"),
                "Turkey",
                List.of("turkey", "türkiye"),
                "Turkey uses the Turkish lira (TRY).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Swiss franc as its currency?",
                List.of("Switzerland", "Austria", "Germany", "France"),
                "Switzerland",
                List.of("switzerland"),
                "Switzerland uses the Swiss franc (CHF).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the South African rand as its currency?",
                List.of("South Africa", "Nigeria", "Kenya", "Ghana"),
                "South Africa",
                List.of("south africa"),
                "South Africa uses the South African rand (ZAR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Singapore dollar as its currency?",
                List.of("Singapore", "Malaysia", "Indonesia", "Philippines"),
                "Singapore",
                List.of("singapore"),
                "Singapore uses the Singapore dollar (SGD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Egyptian pound as its currency?",
                List.of("Egypt", "Morocco", "Algeria", "Tunisia"),
                "Egypt",
                List.of("egypt"),
                "Egypt uses the Egyptian pound (EGP).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.MCQ,
                "Which country uses the Norwegian krone as its currency?",
                List.of("Norway", "Sweden", "Denmark", "Finland"),
                "Norway",
                List.of("norway"),
                "Norway uses the Norwegian krone (NOK).",
                null
        ));

        // ---- CAPITALS - TYPE-IN ----

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Italy?",
                List.of(),
                "Rome",
                List.of("roma"),
                "Rome has been Italy's capital since 1871.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of South Africa? (app.Main administrative capital)",
                List.of(),
                "Pretoria",
                List.of("city of tshwane", "tshwane"),
                "Pretoria is the administrative capital; Cape Town and Bloemfontein are also capitals.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Argentina?",
                List.of(),
                "Buenos Aires",
                List.of("buenosaires", "buenos aires"),
                "Buenos Aires is the capital and largest city of Argentina.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Saudi Arabia?",
                List.of(),
                "Riyadh",
                List.of("riyad", "riyadh"),
                "Riyadh is the political and administrative capital of Saudi Arabia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Norway?",
                List.of(),
                "Oslo",
                List.of(),
                "Oslo is Norway's capital and largest city.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Thailand?",
                List.of(),
                "Bangkok",
                List.of("krung thep", "krungthep", "bangkok"),
                "The local name is Krung Thep Maha Nakhon, but Bangkok is internationally used.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Kenya?",
                List.of(),
                "Nairobi",
                List.of(),
                "Nairobi is the capital and largest city of Kenya.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Sweden?",
                List.of(),
                "Stockholm",
                List.of(),
                "Stockholm is Sweden's capital, located across 14 islands.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Chile?",
                List.of(),
                "Santiago",
                List.of(),
                "Santiago is Chile’s capital and largest city.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "What is the capital of Indonesia?",
                List.of(),
                "Jakarta",
                List.of("dki jakarta", "jakarta"),
                "Jakarta is the capital of Indonesia, though a future capital (Nusantara) is planned.",
                null
        ));

        // ---- CAPITALS (reverse) - TYPE-IN ----
        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Rome as its capital?",
                List.of(),
                "Italy",
                List.of("italy"),
                "Rome is the capital of Italy.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Madrid as its capital?",
                List.of(),
                "Spain",
                List.of("spain"),
                "Madrid is the capital of Spain.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Buenos Aires as its capital?",
                List.of(),
                "Argentina",
                List.of("argentina"),
                "Buenos Aires is the capital and largest city of Argentina.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Bangkok as its capital?",
                List.of(),
                "Thailand",
                List.of("thailand"),
                "Bangkok is the capital of Thailand.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Nairobi as its capital?",
                List.of(),
                "Kenya",
                List.of("kenya"),
                "Nairobi is the capital of Kenya.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Stockholm as its capital?",
                List.of(),
                "Sweden",
                List.of("sweden"),
                "Stockholm is the capital of Sweden.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Jakarta as its capital?",
                List.of(),
                "Indonesia",
                List.of("indonesia"),
                "Jakarta is the capital of Indonesia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Ankara as its capital?",
                List.of(),
                "Turkey",
                List.of("turkey", "türkiye"),
                "Ankara is the capital of Turkey.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Bern as its capital?",
                List.of(),
                "Switzerland",
                List.of("switzerland"),
                "Bern is the de facto capital of Switzerland.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                "Which country has Lisbon as its capital?",
                List.of(),
                "Portugal",
                List.of("portugal"),
                "Lisbon is the capital and largest city of Portugal.",
                null
        ));

        // ---- LANGUAGES - TYPE-IN ----

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in France?",
                List.of(),
                "French",
                List.of("french"),
                "French is the official language of France.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Japan?",
                List.of(),
                "Japanese",
                List.of("japanese"),
                "Japanese is the national language of Japan.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Brazil?",
                List.of(),
                "Portuguese",
                List.of("portuguese"),
                "Brazil is the largest Portuguese-speaking country in the world.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Russia?",
                List.of(),
                "Russian",
                List.of("russian"),
                "Russian is the official language of Russia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Egypt?",
                List.of(),
                "Arabic",
                List.of("arabic"),
                "Arabic is the official language of Egypt.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Germany?",
                List.of(),
                "German",
                List.of("german"),
                "German is the official language of Germany.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Mexico?",
                List.of(),
                "Spanish",
                List.of("spanish"),
                "Spanish is spoken by the majority of the Mexican population.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in South Korea?",
                List.of(),
                "Korean",
                List.of("korean"),
                "Korean is the official language of South Korea.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in the Netherlands?",
                List.of(),
                "Dutch",
                List.of("dutch"),
                "Dutch is the official language of the Netherlands.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Iran?",
                List.of(),
                "Persian",
                List.of("persian", "farsi"),
                "Persian (Farsi) is the official language of Iran.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Italy?",
                List.of(),
                "Italian",
                List.of("italian"),
                "Italian is the official language of Italy.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in China?",
                List.of(),
                "Mandarin Chinese",
                List.of("mandarin", "chinese", "mandarin chinese"),
                "Mandarin Chinese is the official language of China.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Saudi Arabia?",
                List.of(),
                "Arabic",
                List.of("arabic"),
                "Arabic is the official language of Saudi Arabia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Greece?",
                List.of(),
                "Greek",
                List.of("greek"),
                "Greek is the official language of Greece.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Thailand?",
                List.of(),
                "Thai",
                List.of("thai"),
                "Thai is the official language of Thailand.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Vietnam?",
                List.of(),
                "Vietnamese",
                List.of("vietnamese"),
                "Vietnamese is the official language of Vietnam.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Indonesia?",
                List.of(),
                "Indonesian",
                List.of("indonesian", "bahasa indonesia"),
                "Indonesian (Bahasa Indonesia) is the official language of Indonesia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Pakistan?",
                List.of(),
                "Urdu",
                List.of("urdu"),
                "Urdu is the national language of Pakistan.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Ethiopia?",
                List.of(),
                "Amharic",
                List.of("amharic"),
                "Amharic is a major official language of Ethiopia.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.LANGUAGES,
                QuestionType.TYPE_IN,
                "What language is primarily spoken in Nigeria?",
                List.of(),
                "English",
                List.of("english"),
                "English is the official language of Nigeria.",
                null
        ));


        // ---- CURRENCIES - TYPE-IN ----

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Japan?",
                List.of(),
                "Yen",
                List.of("yen", "jpy", "japanese yen"),
                "Japan uses the Japanese yen (JPY).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of the United Kingdom?",
                List.of(),
                "Pound sterling",
                List.of("gbp", "pound", "pound sterling"),
                "The UK uses the pound sterling (GBP).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of China?",
                List.of(),
                "Yuan",
                List.of("yuan", "cny", "renminbi", "rmb"),
                "China's currency is the renminbi (yuan), abbreviated as CNY.",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of India?",
                List.of(),
                "Rupee",
                List.of("rupee", "inr", "indian rupee"),
                "India uses the Indian rupee (INR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Brazil?",
                List.of(),
                "Real",
                List.of("real", "brl", "brazilian real"),
                "Brazil uses the Brazilian real (BRL).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of South Korea?",
                List.of(),
                "Won",
                List.of("won", "krw", "south korean won"),
                "South Korea uses the South Korean won (KRW).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Switzerland?",
                List.of(),
                "Swiss franc",
                List.of("chf", "swiss franc"),
                "Switzerland uses the Swiss franc (CHF).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Australia?",
                List.of(),
                "Australian dollar",
                List.of("aud", "australian dollar"),
                "Australia uses the Australian dollar (AUD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Mexico?",
                List.of(),
                "Mexican peso",
                List.of("peso", "mexican peso", "mxn"),
                "Mexico uses the Mexican peso (MXN).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "What is the currency of Turkey?",
                List.of(),
                "Turkish lira",
                List.of("lira", "try", "turkish lira"),
                "Turkey uses the Turkish lira (TRY).",
                null
        ));

        // ---- CURRENCIES (reverse) - TYPE-IN ----
        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Euro as its currency?",
                List.of(),
                "Germany",
                List.of("germany"),
                "Germany is one of the countries that use the Euro (EUR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Canadian dollar as its currency?",
                List.of(),
                "Canada",
                List.of("canada"),
                "Canada uses the Canadian dollar (CAD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the New Zealand dollar as its currency?",
                List.of(),
                "New Zealand",
                List.of("new zealand"),
                "New Zealand uses the New Zealand dollar (NZD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Russian ruble as its currency?",
                List.of(),
                "Russia",
                List.of("russia", "russian federation"),
                "Russia uses the Russian ruble (RUB).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Turkish lira as its currency?",
                List.of(),
                "Turkey",
                List.of("turkey", "türkiye"),
                "Turkey uses the Turkish lira (TRY).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Swiss franc as its currency?",
                List.of(),
                "Switzerland",
                List.of("switzerland"),
                "Switzerland uses the Swiss franc (CHF).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the South African rand as its currency?",
                List.of(),
                "South Africa",
                List.of("south africa"),
                "South Africa uses the South African rand (ZAR).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Singapore dollar as its currency?",
                List.of(),
                "Singapore",
                List.of("singapore"),
                "Singapore uses the Singapore dollar (SGD).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Egyptian pound as its currency?",
                List.of(),
                "Egypt",
                List.of("egypt"),
                "Egypt uses the Egyptian pound (EGP).",
                null
        ));

        allQuestions.add(new Question(
                QuizType.CURRENCIES,
                QuestionType.TYPE_IN,
                "Which country uses the Norwegian krone as its currency?",
                List.of(),
                "Norway",
                List.of("norway"),
                "Norway uses the Norwegian krone (NOK).",
                null
        ));
    }

    private void loadFlagQuestionsFromApi() {
        List<Country> countries = countryDataAccess.getCountries();
        if (countries.size() < 4) {
            return; // not enough for MCQ
        }

        // Let's generate, say, 10 flag questions
        int numQuestions = 20;

        for (int i = 0; i < numQuestions; i++) {
            Country correct = countries.get(random.nextInt(countries.size()));

            // Pick 3 distinct wrong countries
            List<Country> pool = new ArrayList<>(countries);
            pool.remove(correct);
            Collections.shuffle(pool);
            List<Country> wrongChoices = pool.subList(0, 3);

            // Build options list
            List<String> options = new ArrayList<>();
            options.add(correct.getName());
            for (Country c : wrongChoices) {
                options.add(c.getName());
            }
            Collections.shuffle(options);

            // If your Question has a mediaUrl field, pass correct.getFlagUrl()
            allQuestions.add(new Question(
                    QuizType.FLAGS,
                    QuestionType.MCQ,
                    "Which country's flag is shown?",
                    options,
                    correct.getName(),
                    List.of(correct.getName()),
                    "This is the flag of " + correct.getName()
                    , correct.getFlagUrl()
            ));
        }
    }

    private void loadFlagTypeInQuestionsFromApi() {
        List<Country> countries = countryDataAccess.getCountries();
        int numQuestions = 20;
        for (int i = 0; i < numQuestions && i < countries.size(); i++) {
            Country c = countries.get(i);

            allQuestions.add(new Question(
                    QuizType.FLAGS,
                    QuestionType.TYPE_IN,
                    "What country does this flag belong to?",
                    List.of(), // no MCQ options
                    c.getName(),
                    List.of(c.getName()),
                    "This is the flag of " + c.getName(),
                    c.getFlagUrl()
            ));
        }
    }

    @Override
    public List<Question> getQuestionsForQuiz(QuizType quizType,
                                              QuestionType questionType,
                                              int limit) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getQuizType() == quizType && q.getQuestionType() == questionType) {
                filtered.add(q);
            }
        }

        if (filtered.size() <= limit) {
            return filtered;
        }

        List<Question> result = new ArrayList<>(limit);
        List<Question> pool = new ArrayList<>(filtered);

        for (int i = 0; i < limit; i++) {
            int index = random.nextInt(pool.size());
            result.add(pool.remove(index));
        }

        return result;
    }
}
