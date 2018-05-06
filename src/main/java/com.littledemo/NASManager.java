package com.littledemo;

import com.littledemo.Util.HttpClientUtil;
import io.nebulas.account.AccountManager;
import io.nebulas.core.Address;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NASManager {
    private static NASManager ourInstance = new NASManager();

    public static NASManager getInstance() {
        return ourInstance;
    }

    private String NAS_URL = "https://testnet.nebulas.io";
    private String NAS_GetAccountState = "/v1/user/accountstate";

    private AccountManager mAccountManager;
    private HttpClientUtil mHttpClientUtil;

    private Address mCurAddress = null;
    private byte[] mPassphrase;

    private NASManager() {
        try {
            mAccountManager = new AccountManager();
            mHttpClientUtil = new HttpClientUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateAccount(String passphrase)
    {
        try {
            mCurAddress = mAccountManager.newAccount(passphrase.getBytes());
            mPassphrase = passphrase.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetAccountAddress()
    {
        if(mCurAddress != null)
            return mCurAddress.string();
        else
            return "";
    }

    public String GetAccountBalance()
    {
        if(mCurAddress != null)
        {
            JSONObject requestJson = new JSONObject();
            requestJson.put("address",mCurAddress.string());

            String responseJson = mHttpClientUtil.executeByPOST(NAS_URL+NAS_GetAccountState,requestJson.toString());

            JSONObject jObject = new JSONObject(responseJson);

            return jObject.getJSONObject("result").getString("balance");
        }
        else
            return "ERROR";
    }

    public byte[] ExportWallet() throws Exception {
        if(mCurAddress != null)
        {
            return mAccountManager.export(mCurAddress,mPassphrase);
        }
        else
        {
            return null;
        }
    }

    public void ImportWallet(byte[] keydata,byte[] passphrase) throws Exception {
        mCurAddress = mAccountManager.load(keydata,passphrase);
        mPassphrase = passphrase;
    }
}
