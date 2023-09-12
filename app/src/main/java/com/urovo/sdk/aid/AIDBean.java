package com.urovo.sdk.aid;

import android.content.ContentValues;
import android.database.Cursor;

public class AIDBean {
    public String IndexID;
    public String AID = "";
    public String AIDLable = "";
    public String terminalAIDVersionNumber = "";
    public String exactOnlySelection = "";
    public String skipEMVProgressing = "";
    public String DefaultTDOL = "";
    public String DefaultDDOL = "";
    public String EMVAdditionalTags = "";
    public String denialActionCode = "";
    public String onlineActionCode = "";
    public String defaultActionCode = "";
    public String thresholdValue = "";
    public String targetPercebtage = "";
    public String maxiumTargetPercent = "";

    public String getIndexID() {
        return IndexID;
    }

    public void setIndexID(String indexID) {
        IndexID = indexID;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getAIDLable() {
        return AIDLable;
    }

    public void setAIDLable(String AIDLable) {
        this.AIDLable = AIDLable;
    }

    public String getTerminalAIDVersionNumber() {
        return terminalAIDVersionNumber;
    }

    public void setTerminalAIDVersionNumber(String terminalAIDVersionNumber) {
        this.terminalAIDVersionNumber = terminalAIDVersionNumber;
    }

    public String getExactOnlySelection() {
        return exactOnlySelection;
    }

    public void setExactOnlySelection(String exactOnlySelection) {
        this.exactOnlySelection = exactOnlySelection;
    }

    public String getSkipEMVProgressing() {
        return skipEMVProgressing;
    }

    public void setSkipEMVProgressing(String skipEMVProgressing) {
        this.skipEMVProgressing = skipEMVProgressing;
    }

    public String getDefaultTDOL() {
        return DefaultTDOL;
    }

    public void setDefaultTDOL(String defaultTDOL) {
        DefaultTDOL = defaultTDOL;
    }

    public String getDefaultDDOL() {
        return DefaultDDOL;
    }

    public void setDefaultDDOL(String defaultDDOL) {
        DefaultDDOL = defaultDDOL;
    }

    public String getEMVAdditionalTags() {
        return EMVAdditionalTags;
    }

    public void setEMVAdditionalTags(String EMVAdditionalTags) {
        this.EMVAdditionalTags = EMVAdditionalTags;
    }

    public String getDenialActionCode() {
        return denialActionCode;
    }

    public void setDenialActionCode(String denialActionCode) {
        this.denialActionCode = denialActionCode;
    }

    public String getOnlineActionCode() {
        return onlineActionCode;
    }

    public void setOnlineActionCode(String onlineActionCode) {
        this.onlineActionCode = onlineActionCode;
    }

    public String getDefaultActionCode() {
        return defaultActionCode;
    }

    public void setDefaultActionCode(String defaultActionCode) {
        this.defaultActionCode = defaultActionCode;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(String thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getTargetPercebtage() {
        return targetPercebtage;
    }

    public void setTargetPercebtage(String targetPercebtage) {
        this.targetPercebtage = targetPercebtage;
    }

    public String getMaxiumTargetPercent() {
        return maxiumTargetPercent;
    }

    public void setMaxiumTargetPercent(String maxiumTargetPercent) {
        this.maxiumTargetPercent = maxiumTargetPercent;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("IndexID", getIndexID());
        values.put("AID", getAID());
        values.put("AIDLable", getAIDLable());
        values.put("terminalAIDVersionNumber", getTerminalAIDVersionNumber());
        values.put("exactOnlySelection", getExactOnlySelection());
        values.put("skipEMVProgressing", getSkipEMVProgressing());
        values.put("DefaultTDOL", getDefaultTDOL());
        values.put("DefaultDDOL", getDefaultDDOL());
        values.put("EMVAdditionalTags", getEMVAdditionalTags());
        values.put("denialActionCode", getDenialActionCode());
        values.put("onlineActionCode", getOnlineActionCode());
        values.put("defaultActionCode", getDenialActionCode());
        values.put("thresholdValue", getThresholdValue());
        values.put("targetPercebtage", getTargetPercebtage());
        values.put("maxiumTargetPercent", getMaxiumTargetPercent());

        return values;
    }

    public void fillByCursor(Cursor cur) {
        setIndexID(cur.getString(cur.getColumnIndexOrThrow("IndexID")));
        setAID(cur.getString(cur.getColumnIndexOrThrow("AID")));
        setAIDLable(cur.getString(cur.getColumnIndexOrThrow("AIDLable")));
        setTerminalAIDVersionNumber(cur.getString(cur.getColumnIndexOrThrow("terminalAIDVersionNumber")));
        setExactOnlySelection(cur.getString(cur.getColumnIndexOrThrow("exactOnlySelection")));
        setSkipEMVProgressing(cur.getString(cur.getColumnIndexOrThrow("skipEMVProgressing")));
        setDefaultTDOL(cur.getString(cur.getColumnIndexOrThrow("DefaultTDOL")));
        setDefaultDDOL(cur.getString(cur.getColumnIndexOrThrow("DefaultDDOL")));
        setEMVAdditionalTags(cur.getString(cur.getColumnIndexOrThrow("EMVAdditionalTags")));
        setDenialActionCode(cur.getString(cur.getColumnIndexOrThrow("denialActionCode")));
        setOnlineActionCode(cur.getString(cur.getColumnIndexOrThrow("onlineActionCode")));
        setDefaultActionCode(cur.getString(cur.getColumnIndexOrThrow("defaultActionCode")));
        setThresholdValue(cur.getString(cur.getColumnIndexOrThrow("thresholdValue")));
        setTargetPercebtage(cur.getString(cur.getColumnIndexOrThrow("targetPercebtage")));
        setMaxiumTargetPercent(cur.getString(cur.getColumnIndexOrThrow("maxiumTargetPercent")));
    }

}
