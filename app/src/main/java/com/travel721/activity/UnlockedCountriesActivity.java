package com.travel721.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.travel721.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UnlockedCountriesActivity extends AppCompatActivity {
    private Set<String> hashSet;
    private Map<String, String> countryNameToCode = new HashMap<>();

    public static String countryCodeToEmoji(String code) {

        // offset between uppercase ascii and regional indicator symbols
        int OFFSET = 127397;

        // validate code
        if (code == null || code.length() != 2) {
            return "";
        }

        //fix for uk -> gb
        if (code.equalsIgnoreCase("uk")) {
            code = "gb";
        }

        // convert code to uppercase
        code = code.toUpperCase();

        StringBuilder emojiStr = new StringBuilder();

        //loop all characters
        for (int i = 0; i < code.length(); i++) {
            emojiStr.appendCodePoint(code.charAt(i) + OFFSET);
        }

        // return emoji
        return emojiStr.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryNameToCode.put("Andorra, Principality Of", "AD");
        countryNameToCode.put("United Arab Emirates", "AE");
        countryNameToCode.put("Afghanistan, Islamic State Of", "AF");
        countryNameToCode.put("Antigua And Barbuda", "AG");
        countryNameToCode.put("Anguilla", "AI");
        countryNameToCode.put("Albania", "AL");
        countryNameToCode.put("Armenia", "AM");
        countryNameToCode.put("Netherlands Antilles", "AN");
        countryNameToCode.put("Angola", "AO");
        countryNameToCode.put("Antarctica", "AQ");
        countryNameToCode.put("Argentina", "AR");
        countryNameToCode.put("American Samoa", "AS");
        countryNameToCode.put("Austria", "AT");
        countryNameToCode.put("Australia", "AU");
        countryNameToCode.put("Aruba", "AW");
        countryNameToCode.put("Azerbaidjan", "AZ");
        countryNameToCode.put("Bosnia-Herzegovina", "BA");
        countryNameToCode.put("Barbados", "BB");
        countryNameToCode.put("Bangladesh", "BD");
        countryNameToCode.put("Belgium", "BE");
        countryNameToCode.put("Burkina Faso", "BF");
        countryNameToCode.put("Bulgaria", "BG");
        countryNameToCode.put("Bahrain", "BH");
        countryNameToCode.put("Burundi", "BI");
        countryNameToCode.put("Benin", "BJ");
        countryNameToCode.put("Bermuda", "BM");
        countryNameToCode.put("Brunei Darussalam", "BN");
        countryNameToCode.put("Bolivia", "BO");
        countryNameToCode.put("Brazil", "BR");
        countryNameToCode.put("Bahamas", "BS");
        countryNameToCode.put("Bhutan", "BT");
        countryNameToCode.put("Bouvet Island", "BV");
        countryNameToCode.put("Botswana", "BW");
        countryNameToCode.put("Belarus", "BY");
        countryNameToCode.put("Belize", "BZ");
        countryNameToCode.put("Canada", "CA");
        countryNameToCode.put("Cocos (Keeling) Islands", "CC");
        countryNameToCode.put("Central African Republic", "CF");
        countryNameToCode.put("Congo, The Democratic Republic Of The", "CD");
        countryNameToCode.put("Congo", "CG");
        countryNameToCode.put("Switzerland", "CH");
        countryNameToCode.put("Ivory Coast (Cote D'Ivoire)", "CI");
        countryNameToCode.put("Cook Islands", "CK");
        countryNameToCode.put("Chile", "CL");
        countryNameToCode.put("Cameroon", "CM");
        countryNameToCode.put("China", "CN");
        countryNameToCode.put("Colombia", "CO");
        countryNameToCode.put("Costa Rica", "CR");
        countryNameToCode.put("Former Czechoslovakia", "CS");
        countryNameToCode.put("Cuba", "CU");
        countryNameToCode.put("Cape Verde", "CV");
        countryNameToCode.put("Christmas Island", "CX");
        countryNameToCode.put("Cyprus", "CY");
        countryNameToCode.put("Czech Republic", "CZ");
        countryNameToCode.put("Germany", "DE");
        countryNameToCode.put("Djibouti", "DJ");
        countryNameToCode.put("Denmark", "DK");
        countryNameToCode.put("Dominica", "DM");
        countryNameToCode.put("Dominican Republic", "DO");
        countryNameToCode.put("Algeria", "DZ");
        countryNameToCode.put("Ecuador", "EC");
        countryNameToCode.put("Estonia", "EE");
        countryNameToCode.put("Egypt", "EG");
        countryNameToCode.put("Western Sahara", "EH");
        countryNameToCode.put("Eritrea", "ER");
        countryNameToCode.put("Spain", "ES");
        countryNameToCode.put("Ethiopia", "ET");
        countryNameToCode.put("Finland", "FI");
        countryNameToCode.put("Fiji", "FJ");
        countryNameToCode.put("Falkland Islands", "FK");
        countryNameToCode.put("Micronesia", "FM");
        countryNameToCode.put("Faroe Islands", "FO");
        countryNameToCode.put("France", "FR");
        countryNameToCode.put("France (European Territory)", "FX");
        countryNameToCode.put("Gabon", "GA");
        countryNameToCode.put("Great Britain", "UK");
        countryNameToCode.put("Grenada", "GD");
        countryNameToCode.put("Georgia", "GE");
        countryNameToCode.put("French Guyana", "GF");
        countryNameToCode.put("Ghana", "GH");
        countryNameToCode.put("Gibraltar", "GI");
        countryNameToCode.put("Greenland", "GL");
        countryNameToCode.put("Gambia", "GM");
        countryNameToCode.put("Guinea", "GN");
        countryNameToCode.put("Guadeloupe (French)", "GP");
        countryNameToCode.put("Equatorial Guinea", "GQ");
        countryNameToCode.put("Greece", "GR");
        countryNameToCode.put("S. Georgia & S. Sandwich Isls.", "GS");
        countryNameToCode.put("Guatemala", "GT");
        countryNameToCode.put("Guam (USA)", "GU");
        countryNameToCode.put("Guinea Bissau", "GW");
        countryNameToCode.put("Guyana", "GY");
        countryNameToCode.put("Hong Kong", "HK");
        countryNameToCode.put("Heard And McDonald Islands", "HM");
        countryNameToCode.put("Honduras", "HN");
        countryNameToCode.put("Croatia", "HR");
        countryNameToCode.put("Haiti", "HT");
        countryNameToCode.put("Hungary", "HU");
        countryNameToCode.put("Indonesia", "ID");
        countryNameToCode.put("Ireland", "IE");
        countryNameToCode.put("Israel", "IL");
        countryNameToCode.put("India", "IN");
        countryNameToCode.put("British Indian Ocean Territory", "IO");
        countryNameToCode.put("Iraq", "IQ");
        countryNameToCode.put("Iran", "IR");
        countryNameToCode.put("Iceland", "IS");
        countryNameToCode.put("Italy", "IT");
        countryNameToCode.put("Jamaica", "JM");
        countryNameToCode.put("Jordan", "JO");
        countryNameToCode.put("Japan", "JP");
        countryNameToCode.put("Kenya", "KE");
        countryNameToCode.put("Kyrgyz Republic (Kyrgyzstan)", "KG");
        countryNameToCode.put("Cambodia, Kingdom Of", "KH");
        countryNameToCode.put("Kiribati", "KI");
        countryNameToCode.put("Comoros", "KM");
        countryNameToCode.put("Saint Kitts & Nevis Anguilla", "KN");
        countryNameToCode.put("North Korea", "KP");
        countryNameToCode.put("South Korea", "KR");
        countryNameToCode.put("Kuwait", "KW");
        countryNameToCode.put("Cayman Islands", "KY");
        countryNameToCode.put("Kazakhstan", "KZ");
        countryNameToCode.put("Laos", "LA");
        countryNameToCode.put("Lebanon", "LB");
        countryNameToCode.put("Saint Lucia", "LC");
        countryNameToCode.put("Liechtenstein", "LI");
        countryNameToCode.put("Sri Lanka", "LK");
        countryNameToCode.put("Liberia", "LR");
        countryNameToCode.put("Lesotho", "LS");
        countryNameToCode.put("Lithuania", "LT");
        countryNameToCode.put("Luxembourg", "LU");
        countryNameToCode.put("Latvia", "LV");
        countryNameToCode.put("Libya", "LY");
        countryNameToCode.put("Morocco", "MA");
        countryNameToCode.put("Monaco", "MC");
        countryNameToCode.put("Moldavia", "MD");
        countryNameToCode.put("Madagascar", "MG");
        countryNameToCode.put("Marshall Islands", "MH");
        countryNameToCode.put("Macedonia", "MK");
        countryNameToCode.put("Mali", "ML");
        countryNameToCode.put("Myanmar", "MM");
        countryNameToCode.put("Mongolia", "MN");
        countryNameToCode.put("Macau", "MO");
        countryNameToCode.put("Northern Mariana Islands", "MP");
        countryNameToCode.put("Martinique (French)", "MQ");
        countryNameToCode.put("Mauritania", "MR");
        countryNameToCode.put("Montserrat", "MS");
        countryNameToCode.put("Malta", "MT");
        countryNameToCode.put("Mauritius", "MU");
        countryNameToCode.put("Maldives", "MV");
        countryNameToCode.put("Malawi", "MW");
        countryNameToCode.put("Mexico", "MX");
        countryNameToCode.put("Malaysia", "MY");
        countryNameToCode.put("Mozambique", "MZ");
        countryNameToCode.put("Namibia", "NA");
        countryNameToCode.put("New Caledonia (French)", "NC");
        countryNameToCode.put("Niger", "NE");
        countryNameToCode.put("Norfolk Island", "NF");
        countryNameToCode.put("Nigeria", "NG");
        countryNameToCode.put("Nicaragua", "NI");
        countryNameToCode.put("Netherlands", "NL");
        countryNameToCode.put("Norway", "NO");
        countryNameToCode.put("Nepal", "NP");
        countryNameToCode.put("Nauru", "NR");
        countryNameToCode.put("Neutral Zone", "NT");
        countryNameToCode.put("Niue", "NU");
        countryNameToCode.put("New Zealand", "NZ");
        countryNameToCode.put("Oman", "OM");
        countryNameToCode.put("Panama", "PA");
        countryNameToCode.put("Peru", "PE");
        countryNameToCode.put("Polynesia (French)", "PF");
        countryNameToCode.put("Papua New Guinea", "PG");
        countryNameToCode.put("Philippines", "PH");
        countryNameToCode.put("Pakistan", "PK");
        countryNameToCode.put("Poland", "PL");
        countryNameToCode.put("Saint Pierre And Miquelon", "PM");
        countryNameToCode.put("Pitcairn Island", "PN");
        countryNameToCode.put("Puerto Rico", "PR");
        countryNameToCode.put("Portugal", "PT");
        countryNameToCode.put("Palau", "PW");
        countryNameToCode.put("Paraguay", "PY");
        countryNameToCode.put("Qatar", "QA");
        countryNameToCode.put("Reunion (French)", "RE");
        countryNameToCode.put("Romania", "RO");
        countryNameToCode.put("Russian Federation", "RU");
        countryNameToCode.put("Rwanda", "RW");
        countryNameToCode.put("Saudi Arabia", "SA");
        countryNameToCode.put("Solomon Islands", "SB");
        countryNameToCode.put("Seychelles", "SC");
        countryNameToCode.put("Sudan", "SD");
        countryNameToCode.put("Sweden", "SE");
        countryNameToCode.put("Singapore", "SG");
        countryNameToCode.put("Saint Helena", "SH");
        countryNameToCode.put("Slovenia", "SI");
        countryNameToCode.put("Svalbard And Jan Mayen Islands", "SJ");
        countryNameToCode.put("Slovak Republic", "SK");
        countryNameToCode.put("Sierra Leone", "SL");
        countryNameToCode.put("San Marino", "SM");
        countryNameToCode.put("Senegal", "SN");
        countryNameToCode.put("Somalia", "SO");
        countryNameToCode.put("Suriname", "SR");
        countryNameToCode.put("Saint Tome (Sao Tome) And Principe", "ST");
        countryNameToCode.put("Former USSR", "SU");
        countryNameToCode.put("El Salvador", "SV");
        countryNameToCode.put("Syria", "SY");
        countryNameToCode.put("Swaziland", "SZ");
        countryNameToCode.put("Turks And Caicos Islands", "TC");
        countryNameToCode.put("Chad", "TD");
        countryNameToCode.put("French Southern Territories", "TF");
        countryNameToCode.put("Togo", "TG");
        countryNameToCode.put("Thailand", "TH");
        countryNameToCode.put("Tadjikistan", "TJ");
        countryNameToCode.put("Tokelau", "TK");
        countryNameToCode.put("Turkmenistan", "TM");
        countryNameToCode.put("Tunisia", "TN");
        countryNameToCode.put("Tonga", "TO");
        countryNameToCode.put("East Timor", "TP");
        countryNameToCode.put("Turkey", "TR");
        countryNameToCode.put("Trinidad And Tobago", "TT");
        countryNameToCode.put("Tuvalu", "TV");
        countryNameToCode.put("Taiwan", "TW");
        countryNameToCode.put("Tanzania", "TZ");
        countryNameToCode.put("Ukraine", "UA");
        countryNameToCode.put("Uganda", "UG");
        countryNameToCode.put("United Kingdom", "UK");
        countryNameToCode.put("USA Minor Outlying Islands", "UM");
        countryNameToCode.put("United States", "US");
        countryNameToCode.put("Uruguay", "UY");
        countryNameToCode.put("Uzbekistan", "UZ");
        countryNameToCode.put("Holy See (Vatican City State)", "VA");
        countryNameToCode.put("Saint Vincent & Grenadines", "VC");
        countryNameToCode.put("Venezuela", "VE");
        countryNameToCode.put("Virgin Islands (British)", "VG");
        countryNameToCode.put("Virgin Islands (USA)", "VI");
        countryNameToCode.put("Vietnam", "VN");
        countryNameToCode.put("Vanuatu", "VU");
        countryNameToCode.put("Wallis And Futuna Islands", "WF");
        countryNameToCode.put("Samoa", "WS");
        countryNameToCode.put("Yemen", "YE");
        countryNameToCode.put("Mayotte", "YT");
        countryNameToCode.put("Yugoslavia", "YU");
        countryNameToCode.put("South Africa", "ZA");
        countryNameToCode.put("Zambia", "ZM");
        countryNameToCode.put("Zaire", "ZR");
        countryNameToCode.put("Zimbabwe", "ZW");
        setContentView(R.layout.activity_unlocked_countries);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_lock_open_padding_right);
        SharedPreferences ss = getSharedPreferences("unlocked_countries_721", 0);
        hashSet = ss.getStringSet("set", new HashSet<>());
        LinearLayout countriesListView = findViewById(R.id.unlocked_countries_listview);
        for (String s : hashSet) {
            View countryEntry = getLayoutInflater().inflate(R.layout.country_entry, null);
            TextView countryName = countryEntry.findViewById(R.id.countryName);
            countryName.setText(s);
            TextView countryEmoji = countryEntry.findViewById(R.id.countryEmoji);
            countryEmoji.setText(countryCodeToEmoji(countryNameToCode.get(s)));
            countriesListView.addView(countryEntry);
        }
    }

    public void finish(MenuItem item) {
        finish();
    }
}
