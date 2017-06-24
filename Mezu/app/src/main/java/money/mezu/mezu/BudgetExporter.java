package money.mezu.mezu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import jxl.Workbook;
import jxl.write.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

public class BudgetExporter {

    public static void exportBudget (Context context, Budget budget) {
        Exporter exporter = new Exporter(context, budget);
        exporter.export();
    }

    private static void export(Context context, Budget budget){
        Exporter exporter = new Exporter(context, budget);
        exporter.export();
    }

    private static class Exporter {
        private Context mContext;
        private Budget mBudget;
        private SessionManager mSessionManager;

        String path;
        File file;
        WritableWorkbook wb;
        WritableSheet expensesSheet;
        WritableSheet budgetSheet;
        WritableCellFormat cFormat;

        private Exporter (Context context, Budget budget) {
            mBudget = budget;
            mContext = context;
            mSessionManager = new SessionManager(context);
        }

        private void export(){
            try {
                createFile();
            } catch (Exception e) {
                Toast.makeText(mContext,e.getMessage() , Toast.LENGTH_SHORT).show();
            } finally {
                if (wb != null)
                    try {
                        wb.write();
                        wb.close();
                        sendEmail();
                        file.delete();
                    } catch (Exception e) {
                        Toast.makeText(mContext, mContext.getString(R.string.failed_to_create_file), Toast.LENGTH_SHORT).show();

                    }
            }
        }

        private void createFile() throws WriteException, IOException{
            path = mBudget.getName() + ".xls";
            cFormat = new WritableCellFormat();
            WritableFont font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD);
            cFormat.setFont(font);
            if (!getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(mContext, mContext.getString(R.string.external_storage_unavailable), Toast.LENGTH_SHORT).show();
                return;
            }
            File external = getExternalStorageDirectory();
            file = new File (external, path);
            wb = Workbook.createWorkbook(file);

            handleExpensesSheet();

            handleBudgetSheet();
        }

        private void handleExpensesSheet() throws WriteException{
            expensesSheet = wb.createSheet("Expenses", 0);
            Label[] labels = new Label[6];

            labels[0] = new Label(0, 0, "Title", cFormat);
            labels[1] = new Label(1, 0, "Description", cFormat);
            labels[2] = new Label(2, 0, "Category", cFormat);
            labels[3] = new Label(3, 0, "Added By", cFormat);
            labels[4] = new Label(4, 0, "Added On", cFormat);
            labels[5] = new Label(5, 0, "Amount", cFormat);
            writeCells(labels, true);

            ArrayList<Expense> Expenses = mBudget.getExpenses();
            int row = 1;

            for (Expense expense : Expenses) {
                labels[0] = new Label(0, row, expense.getTitle());
                labels[1] = new Label(1, row, expense.getDescription());
                labels[2] = new Label(2, row, expense.getCategory().toNiceString());
                labels[3] = new Label(3, row, expense.getUserName());
                labels[4] = new Label(4, row, expense.getTime().toString());
                labels[5] = new Label(5, row, String.valueOf(expense.getAmount()));
                writeCells(labels, true);
                row++;
            }
        }

        private void handleBudgetSheet() throws WriteException{
            budgetSheet = wb.createSheet("Budget", 1);
            Label[] labels = new Label[6];

            labels[0] = new Label(0, 0, "Budget Name", cFormat);
            labels[1] = new Label(0, 1, "Initial Balance", cFormat);
            labels[2] = new Label(0, 2, "Partners", cFormat);
            labels[3] = new Label(0, 3, "Ceilings", cFormat);
            labels[4] = new Label(1, 0, mBudget.getName());
            labels[5] = new Label(1, 1, String.valueOf(mBudget.getInitialBalance()));
            writeCells(labels, false);

            ArrayList<String> partnersEmails = mBudget.getEmails();
            Label[] emails = new Label[partnersEmails.size()];
            int col = 1;
            for (String email: partnersEmails) {
                emails[col - 1] = new Label(col, 2, email);
                col++;
            }
            writeCells(emails, false);

            int row = 3;
            double ceiling;
            String name;
            Label[] ceilingsLabels = new Label[2*Category.values().length];
            for(Category category : Category.values()) {
                ceiling = mBudget.tryGetCategoryCeiling(category);
                if (ceiling != -1) {
                    if (category == Category.CATEGORY)
                        name = "Overall";
                    else
                        name = category.toNiceString();
                    ceilingsLabels[2*(row-3)] = new Label(1, row, name);
                    ceilingsLabels[2*(row-3)+1] = new Label(2, row, String.valueOf(ceiling));
                    row++;
                }
            }
            writeCells(ceilingsLabels, (row-3)*2, false);
        }

        private void writeCells(Label[] labels, boolean toExpenses) throws WriteException{
            writeCells(labels, labels.length,toExpenses);
        }

        private void writeCells(Label[] labels, int len, boolean toExpenses) throws WriteException {
            WritableSheet ws = toExpenses ? expensesSheet : budgetSheet;
            for (int i=0; i<len; i++) {
                ws.addCell(labels[i]);
            }
        }

        private void sendEmail() {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("application/excel");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {mSessionManager.getUserEmail()});

            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your Mezu budget - " + mBudget.getName());
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent By Mezu");

            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            mContext.startActivity(Intent.createChooser(emailIntent, "Send Mail..."));
        }
    }
}
