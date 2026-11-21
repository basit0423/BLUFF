package com.bluffparty.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(7,17,15);
    private static final int CARD = Color.rgb(17,35,30);
    private static final int CARD2 = Color.rgb(24,45,38);
    private static final int GREEN = Color.rgb(35,220,145);
    private static final int GOLD = Color.rgb(255,198,82);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(160,180,170);
    private static final int RED = Color.rgb(255,95,95);
    private static final int TOTAL_LEVELS = 400;
    private static final String PREFS = "bluff_progress_v2";

    private FrameLayout root;
    private LinearLayout pageBody;
    private SharedPreferences prefs;
    private int unlockedLevel = 1, currentLevel = 1, questionIndex = 0;
    private int score = 0, correct = 0, combo = 0, bestCombo = 0;
    private int quizMode = 0; // 0 Truth Hunt, 1 Bluff Battle
    private CountDownTimer timer;
    private long timeLeft = 15000;
    private TextView timerText, questionCounter;
    private boolean soundOn = true, vibrationOn = true;
    private final Random random = new Random();

    private static class Question {
        String text, answer, explanation, difficulty;
        Question(String q, String a, String e, String d) { text=q; answer=a; explanation=e; difficulty=d; }
    }

    private final Question[] questions = new Question[] {
        new Question("Which planet is known as the Red Planet?", "Mars", "Iron-rich dust gives Mars its reddish appearance.", "Easy"),
        new Question("How many sides does a hexagon have?", "6", "A hexagon has six sides.", "Easy"),
        new Question("What is the largest ocean on Earth?", "Pacific Ocean", "The Pacific is the largest and deepest ocean.", "Easy"),
        new Question("Which gas do plants absorb from the air?", "Carbon dioxide", "Plants use carbon dioxide during photosynthesis.", "Easy"),
        new Question("What is 9 × 7?", "63", "Nine groups of seven make sixty-three.", "Easy"),
        new Question("Which organ pumps blood around the body?", "Heart", "The heart circulates blood through the body.", "Easy"),
        new Question("How many minutes are in two hours?", "120", "Two hours contain 120 minutes.", "Easy"),
        new Question("What is the freezing point of water in Celsius?", "0°C", "Pure water freezes at zero degrees Celsius at standard pressure.", "Easy"),
        new Question("Which continent is the Sahara Desert in?", "Africa", "The Sahara stretches across northern Africa.", "Easy"),
        new Question("What is the chemical symbol for oxygen?", "O", "O is the chemical symbol for oxygen.", "Easy"),
        new Question("What is the square root of 144?", "12", "12 multiplied by 12 equals 144.", "Medium"),
        new Question("Which language is primarily used to build Android apps?", "Java and Kotlin", "Android apps are commonly developed with Kotlin or Java.", "Medium"),
        new Question("What is the process by which liquid becomes gas?", "Evaporation", "Evaporation changes a liquid into vapour.", "Medium"),
        new Question("Which number is a prime number?", "29", "29 has exactly two positive factors: 1 and 29.", "Medium"),
        new Question("What is the capital city of Japan?", "Tokyo", "Tokyo is Japan's capital.", "Medium"),
        new Question("How many degrees are in a right angle?", "90", "A right angle measures ninety degrees.", "Medium"),
        new Question("Which blood cells help fight infection?", "White blood cells", "White blood cells are part of the immune system.", "Medium"),
        new Question("What does CPU stand for?", "Central Processing Unit", "The CPU executes instructions in a computer.", "Medium"),
        new Question("Which force pulls objects toward Earth?", "Gravity", "Gravity attracts objects with mass.", "Medium"),
        new Question("What is the next number: 2, 4, 8, 16, ...?", "32", "Each term is double the previous term.", "Medium"),
        new Question("What is the sum of the interior angles of a triangle?", "180°", "A Euclidean triangle's interior angles total 180 degrees.", "Hard"),
        new Question("Which part of a cell contains most of its genetic material?", "Nucleus", "The nucleus contains most of a eukaryotic cell's DNA.", "Hard"),
        new Question("What is the binary representation of decimal 10?", "1010", "Eight plus two gives ten, represented as 1010 in binary.", "Hard"),
        new Question("Which planet has the most prominent ring system?", "Saturn", "Saturn's extensive rings are especially visible.", "Hard"),
        new Question("What is the value of 2 to the power of 5?", "32", "2 × 2 × 2 × 2 × 2 equals 32.", "Hard"),
        new Question("Which scientist formulated the laws of motion?", "Isaac Newton", "Newton described three foundational laws of motion.", "Hard"),
        new Question("What is the SI unit of electric current?", "Ampere", "Electric current is measured in amperes.", "Hard"),
        new Question("Which layer of the atmosphere contains most weather?", "Troposphere", "Most clouds and weather occur in the troposphere.", "Hard"),
        new Question("What is the derivative of x²?", "2x", "The power rule gives d(x²)/dx = 2x.", "Hard"),
        new Question("Which element has atomic number 6?", "Carbon", "Carbon has six protons.", "Hard"),
        new Question("What is the approximate speed of light in vacuum?", "300,000 km/s", "Light travels at about 299,792 kilometres per second.", "Expert"),
        new Question("Which mathematical constant is the ratio of circumference to diameter?", "Pi", "Pi is approximately 3.14159.", "Expert"),
        new Question("What is the name of the boundary around a black hole beyond which light cannot escape?", "Event horizon", "The event horizon marks the point of no return.", "Expert"),
        new Question("In computing, what does HTTPS add to HTTP?", "Encryption and authentication", "HTTPS uses TLS to protect communication and authenticate the server.", "Expert"),
        new Question("What is the determinant of [[1,2],[3,4]]?", "-2", "The determinant is 1×4 − 2×3 = −2.", "Expert"),
        new Question("Which particle carries the electromagnetic force?", "Photon", "The photon is the gauge boson of electromagnetism.", "Expert"),
        new Question("What is the pH of a neutral solution at 25°C?", "7", "At 25°C, a neutral aqueous solution has pH 7.", "Expert"),
        new Question("Which data structure follows FIFO order?", "Queue", "A queue removes items in first-in, first-out order.", "Expert"),
        new Question("What is the integral of 1 with respect to x?", "x + C", "The antiderivative of one is x plus a constant.", "Expert"),
        new Question("Which phenomenon explains the bending of light between materials?", "Refraction", "Light changes direction when its speed changes between media.", "Expert")
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        unlockedLevel = Math.max(1, Math.min(TOTAL_LEVELS, prefs.getInt("unlocked", 1)));
        soundOn = prefs.getBoolean("sound", true);
        vibrationOn = prefs.getBoolean("vibration", true);
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        setContentView(root);
        showHome();
    }

    private GradientDrawable shape(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius));
        if (strokeWidth > 0) d.setStroke(dp(strokeWidth), strokeColor); return d;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL); t.setFontFeatureSettings("kern");
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w < 0 ? w : dp(w), h < 0 ? h : dp(h)); }
    private void pad(View v, int a, int b, int c, int d) { v.setPadding(dp(a),dp(b),dp(c),dp(d)); }
    private void space(LinearLayout parent, int height) { View v = new View(this); parent.addView(v, lp(1,height)); }
    private void basePage(String title, String subtitle) {
        if (timer != null) { timer.cancel(); timer = null; }
        root.removeAllViews();
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setClipToPadding(false);
        pageBody = column(); pad(pageBody,20,18,20,28); scroll.addView(pageBody);
        root.addView(scroll, new FrameLayout.LayoutParams(-1,-1));
        TextView top = text("BLUFF!  ·  " + title.toUpperCase(Locale.US), 13, GREEN, true);
        pageBody.addView(top, lp(-1,28));
        if (subtitle != null && !subtitle.isEmpty()) { TextView sub=text(subtitle,13,MUTED,false); pageBody.addView(sub,lp(-1,-2)); }
        space(pageBody,18);
    }
    private TextView heading(String value) { TextView t=text(value,25,WHITE,true); pageBody.addView(t,lp(-1,-2)); space(pageBody,12); return t; }
    private LinearLayout card(String title, String description) {
        LinearLayout c=column(); pad(c,17,15,17,15); c.setBackground(shape(CARD,20,Color.rgb(35,65,54),1));
        TextView h=text(title,16,WHITE,true); c.addView(h,lp(-1,-2));
        if(description!=null&&!description.isEmpty()){ space(c,5); TextView d=text(description,13,MUTED,false); c.addView(d,lp(-1,-2)); }
        pageBody.addView(c,lp(-1,-2)); space(pageBody,11); return c;
    }
    private Button button(String label, boolean primary, View.OnClickListener click) {
        Button b=new Button(this); b.setText(label); b.setAllCaps(false); b.setTextSize(15); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setTextColor(primary?BG:WHITE); b.setBackground(shape(primary?GREEN:CARD2,16,primary?GREEN:Color.rgb(55,85,72),1));
        b.setPadding(dp(12),dp(5),dp(12),dp(5)); b.setOnClickListener(click); return b;
    }
    private void addButton(String label, boolean primary, View.OnClickListener click) {
        Button b=button(label,primary,click); pageBody.addView(b,lp(-1,54)); space(pageBody,9);
    }
    private String stageName(int level) {
        if(level<=50)return "ROOKIE"; if(level<=100)return "QUICK THINKER";
        if(level<=150)return "BRAIN BUILDER"; if(level<=200)return "STRATEGIST";
        if(level<=250)return "MASTERMIND"; if(level<=300)return "ELITE";
        if(level<=350)return "GRAND CHAMPION"; return "LEGEND";
    }
    private String stageDifficulty(int level) {
        if(level<=50)return "Easy"; if(level<=100)return "Medium"; if(level<=150)return "Hard";
        if(level<=200)return "Advanced"; if(level<=250)return "Expert"; if(level<=300)return "Expert+";
        if(level<=350)return "Master"; return "Legend";
    }
    private int worldFor(int level) { return (level-1)/50+1; }
    private void showHome() {
        basePage("Think fast. Bluff smart.","A party quiz where quick thinking wins.");
        TextView logo=text("BLUFF!",42,GREEN,true); logo.setGravity(Gravity.CENTER); pageBody.addView(logo,lp(-1,68));
        TextView tagline=text("THINK FAST. BLUFF SMART.",12,GOLD,true); tagline.setGravity(Gravity.CENTER); pageBody.addView(tagline,lp(-1,26));
        space(pageBody,20);
        LinearLayout progress=card("YOUR JOURNEY","World "+worldFor(unlockedLevel)+"  ·  Level "+unlockedLevel+" of 400  ·  "+stageName(unlockedLevel));
        TextView p=text("✦  "+(unlockedLevel-1)+" levels completed",13,GREEN,true); progress.addView(p,lp(-1,28));
        addButton("CONTINUE  →",true,v->{ currentLevel=unlockedLevel; startQuiz(0); });
        addButton("LEVEL MAP",false,v->showLevelMap(1));
        addButton("TRUTH HUNT",false,v->startQuiz(0));
        addButton("BLUFF BATTLE",false,v->startQuiz(1));
        addButton("IMPOSTOR PARTY",false,v->showImpostorSetup());
        addButton("SETTINGS",false,v->showSettings());
        addButton("ACCOUNT & ONLINE",false,v->showAccount());
        TextView foot=text("LOCAL PROGRESS SAVED ON THIS DEVICE",10,MUTED,false); foot.setGravity(Gravity.CENTER); pageBody.addView(foot,lp(-1,30));
    }
    private void showLevelMap(int world) {
        final int safeWorld=Math.max(1,Math.min(8,world));
        basePage("Level map","Choose your path. Complete a level to unlock the next one.");
        TextView worldTitle=text("WORLD "+safeWorld+"  /  8",19,GOLD,true); pageBody.addView(worldTitle,lp(-1,34));
        TextView stage=text(stageName((safeWorld-1)*50+1)+"  ·  "+stageDifficulty((safeWorld-1)*50+1),12,MUTED,true); pageBody.addView(stage,lp(-1,25));
        LinearLayout nav=row();
        Button prev=button("‹  PREVIOUS",false,v->showLevelMap(safeWorld-1)); prev.setEnabled(safeWorld>1); prev.setAlpha(safeWorld>1?1f:.35f);
        Button next=button("NEXT  ›",false,v->showLevelMap(safeWorld+1)); next.setEnabled(safeWorld<8); next.setAlpha(safeWorld<8?1f:.35f);
        nav.addView(prev,new LinearLayout.LayoutParams(0,dp(46),1)); View gap=new View(this); nav.addView(gap,lp(10,1)); nav.addView(next,new LinearLayout.LayoutParams(0,dp(46),1)); pageBody.addView(nav,lp(-1,46)); space(pageBody,15);
        int start=(safeWorld-1)*50+1;
        for(int r=0;r<10;r++) {
            LinearLayout line=row(); line.setGravity(Gravity.CENTER);
            for(int c=0;c<5;c++) {
                final int level=start+r*5+c; boolean open=level<=unlockedLevel; boolean current=level==unlockedLevel;
                TextView tile=text(String.valueOf(level),14,open?BG:MUTED,true); tile.setGravity(Gravity.CENTER);
                tile.setBackground(shape(open?(current?GOLD:GREEN):CARD2,15,current?GOLD:CARD2, current?2:1));
                tile.setOnClickListener(v->{ if(level<=unlockedLevel){currentLevel=level;startQuiz(0);} else Toast.makeText(this,"Complete the previous level first.",Toast.LENGTH_SHORT).show(); });
                LinearLayout.LayoutParams cell=new LinearLayout.LayoutParams(0,dp(48),1); cell.setMargins(dp(3),dp(4),dp(3),dp(4)); line.addView(tile,cell);
            }
            pageBody.addView(line,lp(-1,56));
        }
        space(pageBody,12);
        addButton("BACK TO HOME",false,v->showHome());
    }
    private ArrayList<Question> questionsForLevel(int level) {
        ArrayList<Question> pool=new ArrayList<>(); String target=stageDifficulty(level);
        for(Question q:questions) {
            if(target.equals("Easy") && q.difficulty.equals("Easy")) pool.add(q);
            else if(target.equals("Medium") && (q.difficulty.equals("Easy")||q.difficulty.equals("Medium"))) pool.add(q);
            else if(target.equals("Hard") && !q.difficulty.equals("Expert")) pool.add(q);
            else if(target.equals("Advanced") && !q.difficulty.equals("Easy")) pool.add(q);
            else if(target.equals("Expert") && q.difficulty.equals("Expert")) pool.add(q);
            else if(target.equals("Expert+") && q.difficulty.equals("Expert")) pool.add(q);
            else if(target.equals("Master") && q.difficulty.equals("Expert")) pool.add(q);
            else if(target.equals("Legend") && q.difficulty.equals("Expert")) pool.add(q);
        }
        if(pool.size()<5) Collections.addAll(pool,questions);
        Collections.shuffle(pool,random); return new ArrayList<>(pool.subList(0,Math.min(5,pool.size())));
    }
    private ArrayList<Question> roundQuestions;
    private void startQuiz(int mode) {
        quizMode=mode; questionIndex=0; score=0; correct=0; combo=0; bestCombo=0;
        roundQuestions=questionsForLevel(currentLevel); showQuestion();
    }
    private void showQuestion() {
        if(questionIndex>=roundQuestions.size()){showQuizResult();return;}
        basePage(quizMode==0?"Truth hunt":"Bluff battle","Level "+currentLevel+"  ·  "+stageName(currentLevel)+"  ·  Question "+(questionIndex+1)+"/"+roundQuestions.size());
        Question q=roundQuestions.get(questionIndex);
        LinearLayout info=row();
        TextView mode=text(quizMode==0?"TRUTH HUNT":"BLUFF BATTLE",11,GREEN,true); info.addView(mode,new LinearLayout.LayoutParams(0,dp(28),1));
        timerText=text("15s",16,GOLD,true); timerText.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); info.addView(timerText,lp(55,28)); pageBody.addView(info,lp(-1,30));
        questionCounter=text("QUESTION "+(questionIndex+1)+" OF 5",11,MUTED,true); pageBody.addView(questionCounter,lp(-1,28));
        LinearLayout qcard=card("THINK IT THROUGH",q.text);
        TextView qbig=text(q.text,22,WHITE,true); qbig.setGravity(Gravity.CENTER); pad(qbig,8,20,8,20); qcard.addView(qbig,lp(-1,-2));
        space(pageBody,12);
        String[] options=buildOptions(q);
        for(String option:options) addButton(option,false,v->answer(option,q));
        startTimer();
    }
    private String[] buildOptions(Question q) {
        ArrayList<String> opts=new ArrayList<>(); opts.add(q.answer);
        while(opts.size()<4){String candidate=questions[random.nextInt(questions.length)].answer;if(!opts.contains(candidate))opts.add(candidate);}
        Collections.shuffle(opts,random); return opts.toArray(new String[0]);
    }
    private void startTimer() {
        timeLeft=15000; timer=new CountDownTimer(15000,1000){
            public void onTick(long ms){timeLeft=ms;if(timerText!=null)timerText.setText((ms/1000)+"s");}
            public void onFinish(){timer=null; if(timerText!=null){combo=0;showAnswer(false,"Time is up!");}}
        }.start();
    }
    private void answer(String chosen, Question q) {
        if(timer==null)return; timer.cancel(); timer=null;
        boolean right=chosen.equals(q.answer); if(right){correct++;combo++;bestCombo=Math.max(bestCombo,combo);score+=100+(int)(timeLeft/1000)*5+Math.min(combo,5)*10;}else combo=0;
        showAnswer(right,right?"Correct! + points":"Not quite. Keep going.");
    }
    private void showAnswer(boolean right,String title) {
        if(timer!=null){timer.cancel();timer=null;}
        basePage("Answer reveal",title);
        Question q=roundQuestions.get(questionIndex);
        LinearLayout c=card(right?"NICE WORK":"THE CORRECT ANSWER",q.answer);
        TextView explanation=text(q.explanation,15,MUTED,false); pad(explanation,0,8,0,0); c.addView(explanation,lp(-1,-2));
        addButton(questionIndex+1<roundQuestions.size()?"NEXT QUESTION  →":"SEE RESULTS  →",true,v->{questionIndex++;showQuestion();});
    }
    private void showQuizResult() {
        int stars=correct>=5?3:correct>=4?2:correct>=3?1:0;
        boolean passed=correct>=3;
        if(passed && currentLevel<TOTAL_LEVELS && currentLevel>=unlockedLevel){unlockedLevel=currentLevel+1;prefs.edit().putInt("unlocked",unlockedLevel).apply();}
        basePage("Round complete","Level "+currentLevel+"  ·  "+stageName(currentLevel));
        TextView result=text(passed?"LEVEL CLEARED":"GOOD TRY!",28,passed?GREEN:GOLD,true); result.setGravity(Gravity.CENTER); pageBody.addView(result,lp(-1,60));
        TextView star=text(stars==3?"★  ★  ★":stars==2?"★  ★  ☆":stars==1?"★  ☆  ☆":"☆  ☆  ☆",30,GOLD,true); star.setGravity(Gravity.CENTER); pageBody.addView(star,lp(-1,55));
        card("YOUR SCORE",score+" points  ·  "+correct+"/5 correct  ·  Best combo "+bestCombo);
        if(passed){card("NEXT UNLOCKED",currentLevel==TOTAL_LEVELS?"You completed all 400 levels!":"Level "+(currentLevel+1)+" is ready.");}
        else card("KEEP PRACTISING","Get at least 3 correct answers to unlock the next level.");
        addButton(passed&&currentLevel<TOTAL_LEVELS?"NEXT LEVEL  →":"TRY AGAIN",true,v->{if(passed&&currentLevel<TOTAL_LEVELS){currentLevel++;startQuiz(0);}else startQuiz(quizMode);});
        addButton("LEVEL MAP",false,v->showLevelMap(worldFor(currentLevel)));
        addButton("HOME",false,v->showHome());
    }
    private void showSettings() {
        basePage("Settings","Make BLUFF! feel right for your game night.");
        addButton("SOUND: "+(soundOn?"ON":"OFF"),false,v->{soundOn=!soundOn;prefs.edit().putBoolean("sound",soundOn).apply();showSettings();});
        addButton("VIBRATION: "+(vibrationOn?"ON":"OFF"),false,v->{vibrationOn=!vibrationOn;prefs.edit().putBoolean("vibration",vibrationOn).apply();showSettings();});
        card("PROGRESS","Your unlocked level is saved on this device. Clearing app data may erase local progress.");
        addButton("RESET LOCAL PROGRESS",false,v->new AlertDialog.Builder(this).setTitle("Reset progress?").setMessage("This resets your unlocked level to 1 on this device.").setNegativeButton("CANCEL",(d,w)->{}).setPositiveButton("RESET",(d,w)->{unlockedLevel=1;prefs.edit().putInt("unlocked",1).apply();showSettings();}).show());
        addButton("BACK",true,v->showHome());
    }
    private void showAccount() {
        basePage("Account & online","Online features require a connected authentication service.");
        card("GUEST MODE","You can play all local modes without creating an account.");
        addButton("CONTINUE AS GUEST",true,v->showHome());
        addButton("GOOGLE SIGN-IN",false,v->authNotice("Google"));
        addButton("FACEBOOK SIGN-IN",false,v->authNotice("Facebook"));
        card("ONLINE FEATURES","Google/Facebook sign-in, cloud saves, online profiles and leaderboards are not active yet. They require Firebase Authentication setup, provider credentials, app configuration and a backend. These buttons do not sign you in yet.");
        addButton("BACK",false,v->showHome());
    }
    private void authNotice(String provider) {
        new AlertDialog.Builder(this).setTitle(provider+" sign-in not configured")
            .setMessage("This project still needs its authentication provider setup and app credentials. No sign-in has been performed.")
            .setPositiveButton("OK",(d,w)->{}).show();
    }
    private void showImpostorSetup() {
        basePage("Impostor party","Pass the phone around. Keep your role secret.");
        card("HOW IT WORKS","Everyone receives the same secret word except one impostor. Give clues, discuss, then vote.");
        for(int n=3;n<=6;n++){final int count=n;addButton(count+" PLAYERS",count==4,v->startImpostor(count));}
        addButton("BACK",false,v->showHome());
    }
    private void startImpostor(int players) {
        String[] words={"PIZZA","MOUNTAIN","GUITAR","AIRPLANE","LIBRARY","SUNFLOWER","FOOTBALL","WATERFALL","CHOCOLATE","BICYCLE","PLANET","CAMERA"};
        String word=words[random.nextInt(words.length)]; int impostor=random.nextInt(players);
        ArrayList<String> roles=new ArrayList<>(); for(int i=0;i<players;i++)roles.add(i==impostor?"IMPOSTOR":"WORD: "+word);
        showPassScreen(roles,0,impostor,word);
    }
    private void showPassScreen(ArrayList<String> roles,int index,int impostor,String word) {
        basePage("Private role","Player "+(index+1)+" of "+roles.size());
        TextView prompt=text("PASS THE PHONE",23,GOLD,true);prompt.setGravity(Gravity.CENTER);pageBody.addView(prompt,lp(-1,70));
        card("PLAYER "+(index+1),"Only this player should view the next screen. Make sure everyone else looks away.");
        addButton("REVEAL MY ROLE",true,v->{basePage("Your secret role","Do not show this screen to anyone.");TextView role=text(roles.get(index),26,roles.get(index).equals("IMPOSTOR")?RED:GREEN,true);role.setGravity(Gravity.CENTER);pageBody.addView(role,lp(-1,120));addButton(index+1<roles.size()?"HIDE & PASS PHONE":"START DISCUSSION",true,x->{if(index+1<roles.size())showPassScreen(roles,index+1,impostor,word);else showImpostorDiscussion(roles,impostor,word);});});
    }
    private void showImpostorDiscussion(ArrayList<String> roles,int impostor,String word) {
        basePage("Discussion","Give clues without saying the secret word.");
        card("DISCUSS","Take turns giving one clue. The impostor should try to blend in.");
        addButton("START VOTING",true,v->showVoting(roles,impostor,word));
    }
    private void showVoting(ArrayList<String> roles,int impostor,String word) {
        basePage("Private voting","Players discuss and agree on one person to vote out.");
        card("GROUP VOTE","Choose who the group thinks is the impostor.");
        for(int i=0;i<roles.size();i++){final int vote=i;addButton("VOTE PLAYER "+(i+1),false,v->showImpostorResult(vote,impostor,word));}
    }
    private void showImpostorResult(int vote,int impostor,String word) {
        basePage("Party result","The round is over.");
        boolean found=vote==impostor;
        TextView outcome=text(found?"IMPOSTOR FOUND!":"THE IMPOSTOR ESCAPED!",24,found?GREEN:GOLD,true);outcome.setGravity(Gravity.CENTER);pageBody.addView(outcome,lp(-1,80));
        card("THE ANSWER","Player "+(impostor+1)+" was the impostor. The secret word was "+word+".");
        addButton("PLAY AGAIN",true,v->showImpostorSetup());addButton("HOME",false,v->showHome());
    }
    @Override public void onBackPressed() {
        if(timer!=null){timer.cancel();timer=null;}
        showHome();
    }
}
