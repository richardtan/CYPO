package com.tckr.dukcud.data;

import android.content.Context;
import android.database.Cursor;

/**
 * This will be used for both the Insight and InsightArc table
 */
public class InsightTable {

    private Context context;

    private String name;
    private int display; //Only used for the Insight table
    private String date1;
    private String date2;
    private String date3;
    private String date4;
    private String datetime1;
    private String datetime2;
    private String datetime3;
    private String datetime4;
    private double float1;
    private double float2;
    private double float3;
    private double float4;
    private long int1;
    private long int2;
    private long int3;
    private long int4;
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String text5;
    private String text6;
    private long timestamp1;
    private long timestamp2;
    private long timestamp3;
    private long timestamp4;
    private long arctimestamp; //Only used for the InsightArc table

    public InsightTable(Context context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public String getDate4() {
        return date4;
    }

    public void setDate4(String date4) {
        this.date4 = date4;
    }

    public String getDatetime1() {
        return datetime1;
    }

    public void setDatetime1(String datetime1) {
        this.datetime1 = datetime1;
    }

    public String getDatetime2() {
        return datetime2;
    }

    public void setDatetime2(String datetime2) {
        this.datetime2 = datetime2;
    }

    public String getDatetime3() {
        return datetime3;
    }

    public void setDatetime3(String datetime3) {
        this.datetime3 = datetime3;
    }

    public String getDatetime4() {
        return datetime4;
    }

    public void setDatetime4(String datetime4) {
        this.datetime4 = datetime4;
    }

    public double getFloat1() {
        return float1;
    }

    public void setFloat1(double float1) {
        this.float1 = float1;
    }

    public double getFloat2() {
        return float2;
    }

    public void setFloat2(double float2) {
        this.float2 = float2;
    }

    public double getFloat3() {
        return float3;
    }

    public void setFloat3(double float3) {
        this.float3 = float3;
    }

    public double getFloat4() {
        return float4;
    }

    public void setFloat4(double float4) {
        this.float4 = float4;
    }

    public long getInt1() {
        return int1;
    }

    public void setInt1(long int1) {
        this.int1 = int1;
    }

    public long getInt2() {
        return int2;
    }

    public void setInt2(long int2) {
        this.int2 = int2;
    }

    public long getInt3() {
        return int3;
    }

    public void setInt3(long int3) {
        this.int3 = int3;
    }

    public long getInt4() {
        return int4;
    }

    public void setInt4(long int4) {
        this.int4 = int4;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getText5() {
        return text5;
    }

    public void setText5(String text5) {
        this.text5 = text5;
    }

    public String getText6() {
        return text6;
    }

    public void setText6(String text6) {
        this.text6 = text6;
    }

    public long getTimestamp1() {
        return timestamp1;
    }

    public void setTimestamp1(long timestamp1) {
        this.timestamp1 = timestamp1;
    }

    public long getTimestamp2() {
        return timestamp2;
    }

    public void setTimestamp2(long timestamp2) {
        this.timestamp2 = timestamp2;
    }

    public long getTimestamp3() {
        return timestamp3;
    }

    public void setTimestamp3(long timestamp3) {
        this.timestamp3 = timestamp3;
    }

    public long getTimestamp4() {
        return timestamp4;
    }

    public void setTimestamp4(long timestamp4) {
        this.timestamp4 = timestamp4;
    }

    public long getArctimestamp() {
        return arctimestamp;
    }

    public void setArctimestamp(long arctimestamp) {
        this.arctimestamp = arctimestamp;
    }

    /**
     * Used to map the data from the cursor into a InsightTable object.
     * @param cursor cursor
     * @param context context
     * @return InsightTable
     */
    public static InsightTable mapInsightTable(Cursor cursor, Context context) {

        InsightTable it = new InsightTable(context);

        // Set the default
        it.setName("null");
        it.setDisplay(DatabaseDAO.INSIGHT_DISPLAY_OFF);
        it.setDate1("null");
        it.setDate2("null");
        it.setDate3("null");
        it.setDate4("null");
        it.setDatetime1("null");
        it.setDatetime2("null");
        it.setDatetime3("null");
        it.setDatetime4("null");
        it.setFloat1(0);
        it.setFloat2(0);
        it.setFloat3(0);
        it.setFloat4(0);
        it.setInt1(0);
        it.setInt2(0);
        it.setInt3(0);
        it.setInt4(0);
        it.setText1("null");
        it.setText2("null");
        it.setText3("null");
        it.setText4("null");
        it.setText5("null");
        it.setText6("null");
        it.setTimestamp1(0);
        it.setTimestamp2(0);
        it.setTimestamp3(0);
        it.setTimestamp4(0);
        it.setArctimestamp(0);

        // Iterate through the columns in the cursor and update the variables.
        for (String columns : cursor.getColumnNames()) {

            switch (columns) {
                case DatabaseDAO.INSIGHT_NAME:
                    it.setName(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_NAME)));
                    break;
                case DatabaseDAO.INSIGHT_DISPLAY:
                    it.setDisplay(cursor.getInt(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DISPLAY)));
                    break;
                case DatabaseDAO.INSIGHT_DATE1:
                    it.setDate1(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATE1)));
                    break;
                case DatabaseDAO.INSIGHT_DATE2:
                    it.setDate2(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATE2)));
                    break;
                case DatabaseDAO.INSIGHT_DATE3:
                    it.setDate3(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATE3)));
                    break;
                case DatabaseDAO.INSIGHT_DATE4:
                    it.setDate4(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATE4)));
                    break;
                case DatabaseDAO.INSIGHT_DATETIME1:
                    it.setDatetime1(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATETIME1)));
                    break;
                case DatabaseDAO.INSIGHT_DATETIME2:
                    it.setDatetime2(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATETIME2)));
                    break;
                case DatabaseDAO.INSIGHT_DATETIME3:
                    it.setDatetime3(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATETIME3)));
                    break;
                case DatabaseDAO.INSIGHT_DATETIME4:
                    it.setDatetime4(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_DATETIME4)));
                    break;
                case DatabaseDAO.INSIGHT_FLOAT1:
                    it.setFloat1(cursor.getDouble(cursor.getColumnIndex(DatabaseDAO.INSIGHT_FLOAT1)));
                    break;
                case DatabaseDAO.INSIGHT_FLOAT2:
                    it.setFloat2(cursor.getDouble(cursor.getColumnIndex(DatabaseDAO.INSIGHT_FLOAT2)));
                    break;
                case DatabaseDAO.INSIGHT_FLOAT3:
                    it.setFloat3(cursor.getDouble(cursor.getColumnIndex(DatabaseDAO.INSIGHT_FLOAT3)));
                    break;
                case DatabaseDAO.INSIGHT_FLOAT4:
                    it.setFloat4(cursor.getDouble(cursor.getColumnIndex(DatabaseDAO.INSIGHT_FLOAT4)));
                    break;
                case DatabaseDAO.INSIGHT_INT1:
                    it.setInt1(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_INT1)));
                    break;
                case DatabaseDAO.INSIGHT_INT2:
                    it.setInt2(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_INT2)));
                    break;
                case DatabaseDAO.INSIGHT_INT3:
                    it.setInt3(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_INT3)));
                    break;
                case DatabaseDAO.INSIGHT_INT4:
                    it.setInt4(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_INT4)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT1:
                    it.setText1(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT1)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT2:
                    it.setText2(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT2)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT3:
                    it.setText3(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT3)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT4:
                    it.setText4(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT4)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT5:
                    it.setText5(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT5)));
                    break;
                case DatabaseDAO.INSIGHT_TEXT6:
                    it.setText6(cursor.getString(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TEXT6)));
                    break;
                case DatabaseDAO.INSIGHT_TIMESTAMP1:
                    it.setTimestamp1(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TIMESTAMP1)));
                    break;
                case DatabaseDAO.INSIGHT_TIMESTAMP2:
                    it.setTimestamp2(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TIMESTAMP2)));
                    break;
                case DatabaseDAO.INSIGHT_TIMESTAMP3:
                    it.setTimestamp3(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TIMESTAMP3)));
                    break;
                case DatabaseDAO.INSIGHT_TIMESTAMP4:
                    it.setTimestamp4(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_TIMESTAMP4)));
                    break;
                case DatabaseDAO.INSIGHT_ARCTIMESTAMP:
                    it.setArctimestamp(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.INSIGHT_ARCTIMESTAMP)));
                    break;
            }

        }

        return it;
    }

}
