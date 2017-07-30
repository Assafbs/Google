package money.mezu.mezu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by asafb on 6/22/2017.
 */

public class CategoryPredictor {
    private Map<String, Category> dict;
    private Watcher watcher;
    private Spinner catSpinner;
    private Activity activity;
    private boolean enabled;

    public void enable(){
        enabled = true;
    }

    public void disable(){
        enabled = false;
    }

    public Watcher getWatcher(){
        return watcher;
    }

    public CategoryPredictor(Activity context, Spinner spinner, Budget budget) {
        initDictFromJson(context);
        addToDictFromBudget(budget);
        watcher = new Watcher();
        this.activity = context;
        this.catSpinner = spinner;
        enabled = true;
    }

    private Category predict(String str){
        String[] words = str.toLowerCase().split("\\s+");
        for (String word : words){
            if (dict.containsKey(word)){
                return  dict.get(word);
            }
        }
        return null; // no prediction
    }

    private void initDictFromJson(Activity context){
        InputStream stream = null;
        try {
            stream = context.getAssets().open("strings_to_categories.json");
        } catch (IOException e) {
            e.printStackTrace();
            dict = new HashMap<>(); // error occurred; dict will be empty
        }
        String json = convertStreamToString(stream);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Category>>(){}.getType();
        dict = gson.fromJson(json, type);
    }

    private void addToDictFromBudget(Budget budget) {
        List<Expense> expenses = budget.getExpenses();
        for (Expense expense : expenses){
            String title  = expense.getTitle();
            Category category = expense.getCategory();
            String[] words = title.toLowerCase().split("\\s+");
            for (String word : words){
                dict.put(word, category);
            }
        }
    }

    @NonNull
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private class Watcher implements TextWatcher {
        private Timer timer = new Timer();
        // the delay can be throttled; If the dictionary will get significantly larger and it will
        // effect performance we can choose a bigger delay.
        private final long DELAY = 100; // in ms.

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(timer != null)
                timer.cancel();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!enabled){
                return;
            }
            final String str = s.toString();
            if (str.length() >= 3) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final Category prediction = predict(str);
                        if (prediction!=null && prediction.getIsExpense()){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int loc = prediction.getSpinnerLocation(false);
                                    catSpinner.setTag(loc);
                                    catSpinner.setSelection(loc);
                                }
                            });
                        }

                    }
                }, DELAY);
            }
        }

    }
}
