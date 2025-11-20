package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.QuizType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LocalQuestionRepository implements QuestionRepository {
    private final List<Question> allQuestions = new ArrayList<>();
    private final Random random = new Random();

    public LocalQuestionRepository() {
        loadManualQuestions();
    }

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

        // ---- FLAGS - MCQ ----

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Canada", "Austria", "Denmark", "Switzerland"),
                "Canada",
                List.of(),
                "The Canadian flag is red and white with a red maple leaf in the center.",
                "/flags/ca.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Japan", "Bangladesh", "Indonesia", "South Korea"),
                "Japan",
                List.of(),
                "Japan's flag is a white field with a red circle representing the sun.",
                "/flags/jp.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Germany", "Belgium", "Russia", "Romania"),
                "Germany",
                List.of(),
                "Germany's flag has three horizontal stripes: black, red, and gold.",
                "/flags/de.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Brazil", "Argentina", "Mexico", "Portugal"),
                "Brazil",
                List.of(),
                "Brazil's flag is green with a yellow diamond and a blue globe with stars.",
                "/flags/br.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("United Kingdom", "Australia", "New Zealand", "United States"),
                "United Kingdom",
                List.of("uk", "great britain", "britain", "united kingdom"),
                "The Union Jack combines the crosses of England, Scotland, and Ireland.",
                "/flags/gb.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Italy", "Ireland", "Mexico", "France"),
                "Italy",
                List.of(),
                "Italy's flag has three vertical stripes: green, white, and red.",
                "/flags/it.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("China", "Vietnam", "Turkey", "Morocco"),
                "China",
                List.of(),
                "China's flag is red with one large yellow star and four smaller stars.",
                "/flags/cn.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("South Africa", "Kenya", "Nigeria", "Ghana"),
                "South Africa",
                List.of(),
                "South Africa's flag has a unique design with green, yellow, black, white, red, and blue.",
                "/flags/za.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("India", "Ireland", "Niger", "Hungary"),
                "India",
                List.of(),
                "India's flag has saffron, white, and green horizontal stripes with a blue Ashoka Chakra.",
                "/flags/in.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.MCQ,
                "Which country's flag is shown?",
                List.of("Spain", "Portugal", "Colombia", "Venezuela"),
                "Spain",
                List.of(),
                "Spain's flag has red and yellow horizontal stripes with a coat of arms near the hoist.",
                "/flags/es.png"
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

        // ---- FLAGS - TYPE-IN ----

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Canada",
                List.of("ca", "canada"),
                "The Canadian flag features a red maple leaf.",
                "/flags/ca.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Japan",
                List.of("jp", "japan"),
                "Japan's flag is white with a red sun disc.",
                "/flags/jp.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Germany",
                List.of("de", "germany"),
                "Germany's flag has black, red, and gold horizontal stripes.",
                "/flags/de.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Brazil",
                List.of("br", "brazil"),
                "Brazil's flag is green with a yellow diamond and blue globe.",
                "/flags/br.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "United Kingdom",
                List.of("uk", "united kingdom", "england", "great britain", "britain"),
                "The Union Jack represents the UK.",
                "/flags/gb.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Italy",
                List.of("it", "italy"),
                "Italy's flag has green, white, and red vertical stripes.",
                "/flags/it.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "China",
                List.of("cn", "china"),
                "China's flag is red with five yellow stars.",
                "/flags/cn.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "South Africa",
                List.of("south africa", "sa", "za", "southafrica"),
                "South Africa's flag features six colors and a unique Y-shape.",
                "/flags/za.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "India",
                List.of("in", "india"),
                "India's flag is saffron, white, and green with a blue Ashoka Chakra.",
                "/flags/in.png"
        ));

        allQuestions.add(new Question(
                QuizType.FLAGS,
                QuestionType.TYPE_IN,
                "Which country's flag is shown?",
                List.of(),
                "Spain",
                List.of("es", "spain"),
                "Spain's flag is red and yellow with its coat of arms.",
                "/flags/es.png"
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
